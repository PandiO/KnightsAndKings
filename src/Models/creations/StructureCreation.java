/**
 * 
 */
package Models.creations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

import DataManager.Worldguard;
import Handlers.ColorOptions;
import Models.Structures.Buyable;
import Properties.PropertyCategory;
import Titles.Title;
import Users.User;

/**
 * @author pandi
 *
 */
public class StructureCreation extends Creation
{
	PropertyCategory propertyCategory = new PropertyCategory();
	Title title = new Title();
	/**
	 * Structure specific variables
	 */
	public String name;
	public String townName;
	public int townID;
	public String streetName;
	public int streetID;
	public String districtName;
	public int districtID;
	public Location spawnpoint;
	public String structureType;
	
	//This gateRegion is created after the first required variables are set.
	//Then this gateRegion is created to allow the user to add sub-regions to the structure.
	//When the creation is completed or canceled these regions are given the proper name or will be deleted.
	public ProtectedRegion tempRegion;
	public List<ProtectedRegion> tempSubRegions = new ArrayList<ProtectedRegion>();
	
	private HashMap<Integer, List<String>> structureMessages = new HashMap<Integer, List<String>>(){{
		put(1, Arrays.asList(
				"",
				ColorOptions.messagesubjects + "Setting the town of the Structure",
				ColorOptions.messageformat + "Please type the name of the town and press enter.",
				""
				));
		put(2, Arrays.asList(
				"",
				ColorOptions.messagesubjects + "Setting the name of the Structure",
				ColorOptions.messageformat + "Please type the desired name of the Structure and press enter.",
				""
				));
		put(3, Arrays.asList(
				"",
				ColorOptions.messagesubjects + "Setting the district of the Structure",
				ColorOptions.messageformat + "Please type the name of the district and press enter.",
				""
				));
		put(4, Arrays.asList(
				"",
				ColorOptions.messagesubjects + "Setting the street of the Structure",
				ColorOptions.messageformat + "Please type the name of the street and press enter.",
				""
				));
	}};
	
	/**
	 * Buyable specific variables
	 */
	public int targetTitleID;
	public int price;
	public int streetNumber;
	
	/**
	 * House specific variables
	 */
	public int grade;
	
	/**
	 * Property specific variables
	 */
	public int income;
	public int level;
	public int contribution;
	public String categoryName;
	public int categoryID;
	public Location npcSpawnpoint;
	
	/**
	 * Gate specific variables
	 */
	public ProtectedRegion tempGateRegion;
	public String faceDirection;
	public int health;
	public List<Location> guardSpawnpoints = new ArrayList<Location>();
	
	public StructureCreation(User user, String structureType)
	{
		super (user);
		
		structureType = structureType.toLowerCase();
		if (structureType.equalsIgnoreCase("house"))
		{
			this.structureType = "House";
		} else
		if (structureType.equalsIgnoreCase("property"))
		{
			this.structureType = "Property";
		} else
		if (structureType.equalsIgnoreCase("gate"))
		{
			this.structureType = "Gate";
		} else
		{
			user.sendMessage(ColorOptions.error + "The given structureType is not a valid type: " + structureType);
			this.confirmStop = true;
			this.stop();
			return;
		}
		
		this.setStartMessage(this.structureType);
		
		if (this.structureType.equalsIgnoreCase("House")
				|| this.structureType.equalsIgnoreCase("Property"))
		{
			this.addStructureMessage(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the streetnumber of the " + this.structureType,
					ColorOptions.messageformat + "Please type the streetnumber and press enter",
					""
					));
		}
		
		this.structureMessages.put(6, Arrays.asList(
				"",
				ColorOptions.messagesubjects + "Creating a Worldguard gateRegion",
				ColorOptions.messageformat + "Please make a worldedit selection of the desired structure.",
				ColorOptions.messageformat + "If you are satisfied with the selection type 'next'",
				"",
				ColorOptions.message + "You can add smaller parts to the gateRegion later on",
				"",
				ColorOptions.messageformat + "NOTE: If you are creating a Gate, than this gateRegion",
				ColorOptions.messageformat + "should contain the entire gate, NOT only the actual 'door' part of the gate",
				""
				));

		this.addStructureMessage(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Extending the Worldguard gateRegion",
					ColorOptions.messageformat + "If you want to add small parts to the district",
					ColorOptions.messageformat + "you can make a worldedit selection and type 'save'.",
					ColorOptions.messageformat + "If you saved a wrong selection type 'undo'.",
					"",
					ColorOptions.messageformat + "If you are done or if you don't want to add parts",
					ColorOptions.messageformat + "please type 'next'",
					""
					));
		
		this.addStructureMessage(Arrays.asList(
				"",
				ColorOptions.messagesubjects + "Setting a spawnpoint for the Structure",
				ColorOptions.messageformat + "Stand on the location of the desired spawnpoint and type 'save'",
				"",
				ColorOptions.messageformat + "NOTE: If you are creating a property, please make sure",
				ColorOptions.messageformat + "that the spawnpoint is in front of the entrance of the property,",
				ColorOptions.messageformat + "so that the spawnpoint can be used by donators as quick-travel method",
				"",
				ColorOptions.messageformat + "NOTE: If you are creating a Gate, please make sure",
				ColorOptions.messageformat + "that the spawnpoint is at least 2 blocks away from the outer wall/gate",
				ColorOptions.messageformat + "in order to prevent the capture circle of Siege from reaching outside the gate",
				""
				));
		
		if (this.structureType.equalsIgnoreCase("House")
				|| this.structureType.equalsIgnoreCase("Property"))
		{
			if (this.structureType.equalsIgnoreCase("House"))
			{
				this.addStructureMessage(Arrays.asList(
						"",
						ColorOptions.messagesubjects + "Setting the Grade of the " + this.structureType,
						ColorOptions.messageformat + "The grade play a role in the value of the House, determined by",
						ColorOptions.messageformat + "for example the view, the interior and the space.",
						ColorOptions.messageformat + "The grade you fill in is added up to the standard grade of the District",
						"",
						ColorOptions.messageformat + "Please type the Grade and press enter",
						""
						));
			}
			if (this.structureType.equalsIgnoreCase("Property"))
			{
				this.addStructureMessage(Arrays.asList(
						"",
						ColorOptions.messagesubjects + "Setting the Contribution of the " + this.structureType,
						ColorOptions.messageformat + "The higher the Contribution the more expensive and exclusive the Property is",
						ColorOptions.messageformat + "Please type 5, 10, 15, 20 or 30 and press enter",
						""
						));
			}
			
			this.structureMessages.put(10, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the TitleID as buyergroup",
					ColorOptions.messageformat + "By setting the target Title ID, the system will automaticly",
					ColorOptions.messageformat + "calculate a matching price (and income) for the " + this.structureType + ".",
					"",
					ColorOptions.messageformat + "Please type the desired Title ID and press enter",
					ColorOptions.error + "If you want to do this manually, please type '-1'",
					""
					));

			
			if (this.structureType.equalsIgnoreCase("Property"))
			{
				this.addStructureMessage(Arrays.asList(
						"",
						ColorOptions.messagesubjects + "Setting the Level of the " + this.structureType,
						ColorOptions.messageformat + "Please type a level from 1 to 3 and press enter",
						""
						));
				
				this.addStructureMessage(Arrays.asList(
						"",
						ColorOptions.messagesubjects + "Setting the Category of the " + this.structureType,
						ColorOptions.messageformat + "Choose and type any of the following categories and press enter.",
						ColorOptions.messageformat + "Categories: " + ColorOptions.messagesubjects + this.propertyCategory.getCategoryNameList(),
						""
						));
				this.addStructureMessage(Arrays.asList(
						"",
						ColorOptions.messagesubjects + "Setting the location for the Shopkeeper",
						ColorOptions.messageformat + "Please stand on the desired location you want the shopkeeper",
						ColorOptions.messageformat + "to stand and type 'save'",
						""
						));
			}
		} else if (this.structureType.equalsIgnoreCase("Gate"))
		{
			this.structureMessages.put(16, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the Gate gateRegion of the " + this.structureType,
					ColorOptions.messageformat + "This gateRegion contains the actual gate. The gateRegion will be used",
					ColorOptions.messageformat + "to open and close the gate on command",
					ColorOptions.error + "CAUTION: Only select the space where the actual 'door' of the gate is",
					"",
					ColorOptions.messageformat + "Please make a worldedit selection and type 'next'",
					""
					));
			this.addStructureMessage(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the face direction of the " + this.structureType,
					ColorOptions.messageformat + "The face direction means the direction you face when staring from",
					ColorOptions.messageformat + "the inside of the gate to the outside.",
					ColorOptions.messageformat + "TIP: You can find the direction you face when you press F3",
					"",
					ColorOptions.messageformat + "Type the face direction ('south', 'north' etc.) and press enter",
					""
					));
			
			this.addStructureMessage(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the health of the " + this.structureType,
					ColorOptions.messageformat + "The health of Gates ",
					ColorOptions.messageformat + "Please type an amount and press enter",
					""
					));
			this.addStructureMessage(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting spawnpoints for guards of this " + this.structureType,
					ColorOptions.messageformat + "You and save spawnpoints where guards will spawn which will",
					ColorOptions.messageformat + "defend the gate from enemies and criminals.",
					ColorOptions.messageformat + "You can add multiple locations",
					ColorOptions.messageformat + "Stand on the desired location and type 'save'",
					ColorOptions.messageformat + "When you are done with the locations type 'next'",
					""
					));
		}
		Main.Main.logMessage("Structuremessages size: " + this.structureMessages.size());
		this.setStageMessages(structureMessages);
		
		this.sendMessage(startMessages);
	}
	
	@Override
	protected void create()
	{
		if (this.structureType.equalsIgnoreCase("House"))
		{
			DataManager.Structures.Houses.CreateHouse(this);
		} else if (this.structureType.equalsIgnoreCase("Gate"))
		{
			DataManager.Structures.Gates.CreateGate(this);
		}
	}
	
	@Override
	protected List<String> getSpecificConfirmMessage()
	{
		List<String> message = new ArrayList<String>();
		
		message.addAll(Arrays.asList(
				ColorOptions.stats + "Name: " + ColorOptions.statsresults + this.name,
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + this.townName + ColorOptions.message + "(" + this.townID + ")",
				ColorOptions.stats + "District: " + ColorOptions.statsresults + this.districtName + ColorOptions.message + "(" + this.districtID + ")",
				ColorOptions.stats + "Street: " + ColorOptions.statsresults + this.streetName + ColorOptions.message + "(" + this.streetID + ")"
				));
		
		if (this.structureType.equalsIgnoreCase("House")
				|| this.structureType.equalsIgnoreCase("Property"))
		{
			message.addAll(Arrays.asList(
					ColorOptions.stats + "Number: " + ColorOptions.statsresults + this.streetNumber,
					""
					));
			if (this.targetTitleID != -1)
			{
				message.add(ColorOptions.stats + "Target title: " + ColorOptions.statsresults + this.title.getTitleName(this.targetTitleID, 1) + "/" + this.title.getTitleName(this.targetTitleID, 2) + ColorOptions.message + "(" + this.targetTitleID + ")");
			}
			
			message.add(ColorOptions.stats + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(this.price));
			
			if (this.structureType.equalsIgnoreCase("Property"))
			{
				message.addAll(Arrays.asList(
						ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(this.income),
						ColorOptions.stats + "Level: " + ColorOptions.statsresults + this.level,
						ColorOptions.stats + "Contribution: " + ColorOptions.statsresults + this.contribution,
						ColorOptions.stats + "Category: " + ColorOptions.statsresults + this.categoryName + ColorOptions.message + "(" + this.categoryID + ")"
						));
			}
			
			message.add("");
		} else if (this.structureType.equalsIgnoreCase("Gate"))
		{
			message.addAll(Arrays.asList(
					"",
					ColorOptions.stats + "Face direction: " + ColorOptions.statsresults + this.faceDirection,
					ColorOptions.stats + "Health: " + ColorOptions.statsresults + ColorOptions.formatCurrency(this.health),
					ColorOptions.stats + "No. guard locations: " + ColorOptions.statsresults + this.guardSpawnpoints.size(),
					""
					));
		}
		
		return message;
	}
	
	protected void generateTargetedPricing(Integer titleID)
	{
		Integer factor = 1;
		
		if (this.structureType.equalsIgnoreCase("House"))
		{
			factor = this.grade;
		} else
		if (this.structureType.equalsIgnoreCase("Property"))
		{
			factor = this.contribution;
		}
		
		this.price = Buyable.getTargetedPrice(titleID, factor);
		this.income = Buyable.getTargetedIncome(titleID, price, factor);
		this.targetTitleID = titleID;
	}
	
	protected void addStructureMessage(List<String> message)
	{
		Integer lastStage = this.calculateLastStage(this.structureMessages);
		Main.Main.logMessage("Last key " + lastStage + ", message: " + message.toString());
		this.structureMessages.put((lastStage+1), message);
	}
	
	@Override
	public void processEvent(Event event)
	{
		Player player = user.getPlayer();
		boolean cancel = false;
		Integer addStage = 0;
		
		if (event instanceof PlayerChatEvent
				|| event instanceof PlayerCommandPreprocessEvent)
		{
			String message = null;
			if (event instanceof PlayerChatEvent)
			{
				message = ((PlayerChatEvent) event).getMessage();
			} else if (event instanceof PlayerCommandPreprocessEvent)
			{
				message = ((PlayerCommandPreprocessEvent) event).getMessage();
				message.replace("/", "");
			}
			
			if (message.equalsIgnoreCase("stop"))
			{
				this.stop();
				cancel = true;
			} else
			if (message.equalsIgnoreCase("start"))
			{
				this.start();
				cancel = true;
			} else
			if (message.equalsIgnoreCase("confirm"))
			{
				if (this.getStage() == this.getLastStage()+1)
				{
					this.complete();
				} else
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.error + "You must complete all previous steps first"
							));
				}
				cancel = true;
			} else 
			if (message.equalsIgnoreCase("next"))
			{
				if (this.getStage() == 6)
				{
					Selection selection = worldedit.getWorldEdit().getSelection(player);
					if (selection != null)
					{
						this.tempRegion = new ProtectedCuboidRegion(
								"tempregion_" + this.getCreationID(),
								new BlockVector(selection.getNativeMinimumPoint()),
								new BlockVector(selection.getNativeMaximumPoint())
								);
						Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(tempRegion);
						addStage = this.stage+1;
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Succesfully saved the gateRegion",
								""));
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "You must make a worldedit selection to complete this step",
								""
							));
					}
				} else
				if (this.getStage() == 16)
				{
					Selection selection = worldedit.getWorldEdit().getSelection(player);
					if (selection != null)
					{
						this.tempGateRegion = new ProtectedCuboidRegion(
								"tempregion_" + this.getCreationID() + "_gate",
								new BlockVector(selection.getNativeMinimumPoint()),
								new BlockVector(selection.getNativeMaximumPoint())
								);
						Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(tempGateRegion);
						addStage = this.stage+1;
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Succesfully saved the gate gateRegion",
								""));
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "You must make a worldedit selection to complete this step",
								""
							));
					}
				} else
				{
					addStage = this.getStage() +1;
				}
				cancel = true;
			} else
			if (message.equalsIgnoreCase("save"))
			{
				if (this.getStage() == 7)
				{
					Selection selection = worldedit.getWorldEdit().getSelection(player);
					if (selection != null)
					{
						long subID = System.currentTimeMillis();
						ProtectedRegion region = new ProtectedCuboidRegion(
								"tempregion_" + this.getCreationID() + "_sub_" + subID,
								new BlockVector(selection.getNativeMinimumPoint()),
								new BlockVector(selection.getNativeMaximumPoint())
								);
						try {
							region.setParent(this.tempRegion);
						} catch (CircularInheritanceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						this.tempSubRegions.add(region);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the gateRegion as sub gateRegion",
								ColorOptions.messageachievement + "type 'next' when you are done",
								""
								));
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "You must make a worldedit selection to save a sub gateRegion",
								""
							));
					}
				} else
				if (this.getStage() == 8)
				{
					Location location = player.getLocation();
					Integer id = Worldguard.getStructureIDbyRegion(this.tempRegion.getId(), 
							location, 
							Worldguard.getRegionManager(location.getWorld()));
					Integer tempID = Integer.valueOf(this.tempRegion.getId().split("_")[1]);
					if (id != null && id.equals(tempID))
					{
						this.spawnpoint = location;
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the spawnpoint to your location",
								""
								));
						
						if (this.structureType.equalsIgnoreCase("Gate"))
						{
							addStage = this.getStage() + 8;
						} else
						{
							addStage = this.getStage() + 1;

						}
					} else
					{
						Main.Main.logMessage("Found: " + id.toString());
						Main.Main.logMessage("Region: " + tempID);
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The spawnpoint must be a location inside the regions you just made",
								""
							));
					}
				} else
				if (this.getStage() == 13)
				{
					Location location = player.getLocation();
					Integer id = Worldguard.getStructureIDbyRegion(this.tempRegion.getId(), 
							location, 
							Worldguard.getRegionManager(location.getWorld()));
					Integer tempID = Integer.valueOf(this.tempRegion.getId().split("_")[1]);
					if (id != null && id.equals(tempID))
					{
						this.spawnpoint = location;
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the NPC Spawnpoint of the " + this.structureType,
								""
								));
						addStage = this.getStage() + 1;
					} else
					{
						Main.Main.logMessage("Found: " + id.toString());
						Main.Main.logMessage("Region: " + tempID);
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The spawnpoint must be a location inside the regions you just made",
								""
							));
					}
				} else 
				if (this.getStage() == 19)
				{
					Location location = player.getLocation();
					Integer id = Worldguard.getStructureIDbyRegion(this.tempRegion.getId(), 
							location, 
							Worldguard.getRegionManager(location.getWorld()));
					Integer tempID = Integer.valueOf(this.tempRegion.getId().split("_")[1]);
					if (id != null && id.equals(tempID))
					{
						boolean contains = false;
						for (Location l : this.guardSpawnpoints)
						{
							if (l.getBlockX() == location.getBlockX()
									&& l.getBlockY() == location.getBlockY()
									&& l.getBlockZ() == location.getBlockZ())
							{
								contains = true;
								break;
							}
						}
						
						if (!contains)
						{
							this.guardSpawnpoints.add(location);
							this.sendMessage(Arrays.asList(
									"",
									ColorOptions.messageachievement + "Saved a Guard spawnpoint of the " + this.structureType,
									ColorOptions.message + "Type 'next' when you are done",
									""
									));
						} else
						{
							this.falseCommand(Arrays.asList(
									"",
									ColorOptions.error + "The spawnpoint is already listed as a Guard spawnpoint",
									""
								));
						}
					} else
					{
						Main.Main.logMessage("Found: " + id.toString());
						Main.Main.logMessage("Region: " + tempID);
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The spawnpoint must be a location inside the regions you just made",
								""
							));
					}
				}
				cancel = true;
			} else
			if (message.equalsIgnoreCase("undo"))
			{				
				if (this.getStage() == 7)
				{
					if (!this.tempSubRegions.isEmpty())
					{
						this.tempSubRegions.remove(this.tempSubRegions.size()-1);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Removed the last sub gateRegion",
								ColorOptions.messageachievement + "type 'next' when you are done",
								""
								));
						addStage = this.getStage();
					} else
					{
						addStage = this.getStage()-1;
					}
				} else
				if (this.getStage() == 19)
				{
					if (!this.guardSpawnpoints.isEmpty())
					{
						this.guardSpawnpoints.remove(this.guardSpawnpoints.size()-1);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Removed the last Guard spawnpoint",
								ColorOptions.messageachievement + "type 'next' when you are done",
								""
								));
						addStage = this.getStage();
					} else
					{
						addStage = this.getStage() -1;
					}
				} else
				{
					addStage = this.getStage() -1;
				}
			} else
			{
				if (this.getStage() == 1)
				{
					if (DataManager.Towns.ExistTown(message))
					{
						this.townName = message;
						this.townID = DataManager.Towns.FetchTownID(message);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the town of the " + this.structureType + " to " + message,
								""
								));
						addStage = this.getStage() +1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "No town could be found named " + message,
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 2)
				{
					this.name = message;
					this.sendMessage(Arrays.asList(
							"",
							ColorOptions.messageachievement + "Saved the Name of the " + this.structureType + " to " + message,
							""
							));
					addStage = this.getStage() +1;
					cancel = true;
				} else 
				if (this.getStage() == 3)
				{
					if (DataManager.Districts.ExistDistrict(message, this.townID))
					{
						this.districtName = message;
						this.districtID = DataManager.Districts.FetchDistrictID(message, this.townID);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the district of the " + this.structureType + " to " + message,
								""
								));
						addStage = this.getStage() +1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "No district could be found named " + message + " in the town of " + this.townName,
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 4)
				{
					if (DataManager.Streets.ExistStreet(message, townID))
					{
						boolean cont = true;
						if (this.structureType.equalsIgnoreCase("Gate"))
						{
							if (DataManager.Structures.Gates.existGate(this.name, DataManager.Streets.FetchStreetID(message, townID), townID))
							{
								Integer gateID = DataManager.Structures.Gates.fetchGateID(message, DataManager.Streets.FetchStreetID(message, townID), townID);
								this.falseCommand(Arrays.asList(
									"",
									ColorOptions.error + "You are trying to create a gate with the same street, name and town as gate with ID " + gateID,
									ColorOptions.error + "Please change the name by typing 'undo' 2 times",
									""
								));
								cont = false;
							}
						}
						
						if (cont)
						{
							this.streetName = message;
							this.streetID = DataManager.Streets.FetchStreetID(message, townID);
							this.sendMessage(Arrays.asList(
									"",
									ColorOptions.messageachievement + "Saved the street of the " + this.structureType + " to " + message,
									""
									));
							if (this.structureType.equalsIgnoreCase("Gate"))
							{
								addStage = this.getStage() +2;
							} else
							{
								addStage = this.getStage() +1;
							}
						}
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "No street could be found named " + message + " in the town of " + this.townName,
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 5)
				{
					if (this.structureType.equalsIgnoreCase("House")
							|| this.structureType.equalsIgnoreCase("Property"))
					{
						if (Main.Main.isInt(message))
						{
							Integer streetNumber = Integer.valueOf(message);
							
							this.streetNumber = streetNumber;
							this.sendMessage(Arrays.asList(
									"",
									ColorOptions.messageachievement + "Saved the streetnumber of the " + this.structureType + " to " + message,
									""
									));
							addStage = this.getStage() +1;
						} else
						{
							this.sendMessage(Arrays.asList(
									"",
									ColorOptions.error + "The streetnumber must be a numeric type",
									""
									));
						}
						cancel = true;
					} else
					{
						addStage = this.getStage() +1;
					}
				} else
				if (this.getStage() == 9)
				{
					if (this.structureType.equalsIgnoreCase("House")
							|| this.structureType.equalsIgnoreCase("Property"))
					{
						if (Main.Main.isInt(message))
						{
							Integer argument = Integer.valueOf(message);
							
							if (this.structureType.equalsIgnoreCase("House"))
							{
								this.grade = argument;
								this.sendMessage(Arrays.asList(
										"",
										ColorOptions.messageachievement + "Saved the Grade of the " + this.structureType + " to " + this.grade,
										""
										));
								
								addStage = this.getStage() + 1;
							} else
							if (this.structureType.equalsIgnoreCase("Property"))
							{
								if (Main.Main.contribution.contains(argument))
								{
									this.contribution = argument;
									this.sendMessage(Arrays.asList(
											"",
											ColorOptions.messageachievement + "Saved the Contribution of the " + this.structureType + " to " + this.contribution,
											""
											));
									
									addStage = this.getStage() + 1;
								} else
								{
									this.falseCommand(Arrays.asList(
											"",
											ColorOptions.error + "The contribution must be a number from the following list:",
											ColorOptions.error + Main.Main.contribution.toString(),
											""
											));
								}
							}
						} else
						{
							this.falseCommand(Arrays.asList(
									"",
									ColorOptions.falsecommand + "The Grade/Contribution must be a numeric type",
									""
									));
						}
					} else
					{
						addStage = this.stage + 8;
					}
					cancel = true;
				} else
				if (this.getStage() == 10)
				{
					if (Main.Main.isInt(message))
					{
						Integer titleID = Integer.valueOf(message);
						List<Integer> IDList = title.getIDList();
						
						if (IDList.contains(titleID))
						{
							this.generateTargetedPricing(titleID);
							this.sendMessage(Arrays.asList(
									"",
									ColorOptions.messageachievement + "Saved the target Title ID of the " + this.structureType + " to " + this.targetTitleID,
									""
									));
							if (this.targetTitleID == -1)
							{
								this.addStageMessage(Arrays.asList(
										"",
										ColorOptions.messagesubjects + "Setting the price of the " + this.structureType,
										ColorOptions.messageformat + "Please type a price and press enter",
										""
										));
								
								if (this.structureType.equalsIgnoreCase("Property"))
								{
									this.addStageMessage(Arrays.asList(
											"",
											ColorOptions.messagesubjects + "Setting the income of the " + this.structureType,
											ColorOptions.messageformat + "The owner of this " + this.structureType + " receives the income once per 12 hours",
											ColorOptions.messageformat + "Please type an income and press enter",
											""
											));
								}
							}
							
							addStage = this.getStage() +1;
						} else
						{
							this.falseCommand(Arrays.asList(
									"",
									ColorOptions.error + "No Title could be found with ID " + titleID,
									ColorOptions.message + "Titles range from " + IDList.get(0) + "-" + IDList.get(IDList.get(IDList.size()-1)),
									""
									));
						}
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The target Title ID must be a numeric type",
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 11)
				{
					if (Main.Main.isInt(message))
					{
						this.level = Integer.valueOf(message);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the level of the " + this.structureType + " to " + this.level,
								""
								));
						
						addStage = this.getStage() +1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The level must be a numeric type",
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 12)
				{
					Integer categoryID = this.propertyCategory.getCategoryID(message);
					if (categoryID != null)
					{
						this.categoryName = message;
						this.categoryID = categoryID;
						
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the category of the " + this.structureType + " to " + this.categoryName,
								""
								));
						
						addStage = this.getStage() +1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "No category could be found named " + message,
								ColorOptions.message + "Available categories: " + this.propertyCategory.getCategoryNameList(),
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 14)
				{
					if (Main.Main.isInt(message))
					{
						this.price = Integer.valueOf(message);
						
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the price of the " + this.structureType + " to " + this.price,
								""
								));
						
						addStage = this.getStage() +1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The price must be a numeric type",
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 15)
				{
					if (Main.Main.isInt(message))
					{
						this.income = Integer.valueOf(message);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the income of the " + this.structureType + " to " + this.price,
								""
								));
						
						addStage = this.getStage() +1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The income must be a numeric type",
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 17)
				{
					if (message.equalsIgnoreCase("north")
							|| message.equalsIgnoreCase("east")
							|| message.equalsIgnoreCase("south")
							|| message.equalsIgnoreCase("west"))
					{
						this.faceDirection = message;
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the face direction of the " + this.structureType + " to " + this.faceDirection,
								""
								));
						
						addStage = this.getStage() +1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The face direction must be one of the following values",
								ColorOptions.message + "North, East, South, West",
								""
								));
					}
					cancel = true;
				} else
				if (this.getStage() == 18)
				{
					if (Main.Main.isInt(message))
					{
						this.health = Integer.valueOf(message);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the health of the " + this.structureType + " to " + this.health,
								""
								));
						addStage = this.getStage() +1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The health must be a numeric type",
								""
								));
					}
					cancel = true;
				}
			}
		}
		
		if (cancel)
		{
			if (event instanceof PlayerChatEvent)
			{
				((PlayerChatEvent) event).setCancelled(true);
			} else if (event instanceof PlayerCommandPreprocessEvent)
			{
				((PlayerCommandPreprocessEvent) event).setCancelled(true);
			}
		}
		if (addStage != 0)
		{
			this.nextStage(addStage);
		}
	}
}
