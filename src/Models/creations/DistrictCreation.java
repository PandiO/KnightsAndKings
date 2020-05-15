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
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

import DataManager.Districts;
import DataManager.Towns;
import DataManager.Worldguard;
import Handlers.ColorOptions;
import Users.User;

public class DistrictCreation extends Creation
{
	protected String name;
	protected String townName;
	protected int townID;
	
	//This Region is created after the first required variables are set.
	//Then this Region is created to allow the user to add sub-regions to the structure.
	//When the creation is completed or canceled these regions are given the proper name or will be deleted.
	protected ProtectedRegion tempRegion;
	protected List<ProtectedRegion> tempSubRegions = new ArrayList<ProtectedRegion>();
	
	public DistrictCreation(User user) {
		super(user);
		
		this.setStartMessage("District");
		
		this.setStageMessages(new HashMap<Integer, List<String>>(){{
			put(1, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the town of the district",
					ColorOptions.messageformat + "Please type the name of the town and press enter.",
					""
					));
			put(2, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the name of the district",
					ColorOptions.messageformat + "Please type the name of the district and press enter.",
					""
					));
			put(3, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Creating a Worldguard Region",
					ColorOptions.messageformat + "Please make a worldedit selection of the desired district.",
					ColorOptions.messageformat + "If you are satisfied with the selection type 'next'",
					""
					));
			put(4, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Extending the Worldguard Region",
					ColorOptions.messageformat + "If you want to add small parts to the district",
					ColorOptions.messageformat + "you can make a worldedit selection and type 'save'.",
					ColorOptions.messageformat + "If you saved a wrong selection type 'undo'.",
					"",
					ColorOptions.messageformat + "If you are done or if you don't want to add parts",
					ColorOptions.messageformat + "please type 'next'"
					));
		}});
		
		this.sendMessage(startMessages);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getTownName() {
		return this.townName;
	}
	
	public int getTownID() {
		return this.townID;
	}
	
	public ProtectedRegion getTempRegion() {
		return this.tempRegion;
	}
	
	public List<ProtectedRegion> getTempSubRegions() {
		return this.tempSubRegions;
	}
	
	@Override
	public List<String> getSpecificConfirmMessage()
	{
		return Arrays.asList(
				ColorOptions.stats + "Name: " + ColorOptions.statsresults + this.name,
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + this.townName,
				""
				);
	}
	
	@Override
	public void create()
	{
		Districts.CreateDistrict(this);
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
				addStage = this.firstStage;
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
				if (this.getStage() == 3)
				{
					RegionManager manager = Worldguard.getRegionManager(player.getLocation().getWorld());
					Selection selection = worldedit.getWorldEdit().getSelection(player);
					if (selection != null)
					{
						Location maxLoc = new Location(player.getLocation().getWorld(), 
								selection.getMaximumPoint().getX(),
								selection.getMaximumPoint().getY(),
								selection.getMaximumPoint().getZ()
								);
						Location minLoc = new Location(player.getLocation().getWorld(), 
								selection.getNativeMinimumPoint().getX(),
								selection.getNativeMinimumPoint().getY(),
								selection.getNativeMinimumPoint().getZ()
								);
						Main.Main.logMessage("MaxLoc: " + maxLoc.toString());
						Main.Main.logMessage("MinLoc: " + minLoc.toString());
						Integer structureIDMax = Worldguard.getStructureIDbyRegion("town", maxLoc, manager);
						Integer structureIDMin = Worldguard.getStructureIDbyRegion("town", minLoc, manager);
						Main.Main.logMessage("StructureIDMax: " + structureIDMax + ", Min: " + structureIDMin + ", townID: " + townID);
						if ((structureIDMax != null 
								&& structureIDMin != null)
									&& (structureIDMax == townID)
										&& structureIDMin == townID)
						{
							this.tempRegion = new ProtectedCuboidRegion(
									"tempregion_" + this.getCreationID(),
									new BlockVector(selection.getNativeMinimumPoint()),
									new BlockVector(selection.getNativeMaximumPoint())
									);
							manager.addRegion(tempRegion);
							addStage = this.stage+1;
							this.sendMessage(Arrays.asList(
									"",
									ColorOptions.messageachievement + "Succesfully saved the Region",
									""));
						} else
						{
							this.falseCommand(Arrays.asList(
									"",
									ColorOptions.error + "The Region must be inside of the Townregions",
									""
									));
						}
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
					addStage = this.stage+1;
				}
				cancel = true;
			} else
			if (message.equalsIgnoreCase("save"))
			{
				if (this.getStage() == 4)
				{
					RegionManager manager = Worldguard.getRegionManager(player.getLocation().getWorld());
					Selection selection = worldedit.getWorldEdit().getSelection(player);
					if (selection != null)
					{
						Location maxLoc = new Location(player.getLocation().getWorld(), 
								selection.getMaximumPoint().getX(),
								selection.getMaximumPoint().getY(),
								selection.getMaximumPoint().getZ()
								);
						Location minLoc = new Location(player.getLocation().getWorld(), 
								selection.getNativeMinimumPoint().getX(),
								selection.getNativeMinimumPoint().getY(),
								selection.getNativeMinimumPoint().getZ()
								);
						Integer structureIDMax = Worldguard.getStructureIDbyRegion("town", maxLoc, manager);
						Integer structureIDMin = Worldguard.getStructureIDbyRegion("town", minLoc, manager);
						if ((structureIDMax != null 
								&& structureIDMin != null)
									&& (structureIDMax == townID)
										&& structureIDMin == townID)
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
							manager.addRegion(region);
							this.tempSubRegions.add(region);
							this.sendMessage(Arrays.asList(
									"",
									ColorOptions.messageachievement + "Saved the Region as sub Region",
									ColorOptions.messageachievement + "type 'next' when you are done",
									""
									));
						} else
						{
							this.falseCommand(Arrays.asList(
									"",
									ColorOptions.error + "The Region must be inside of the Townregions",
									""
									));
						}
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "You must make a worldedit selection to save a sub Region",
								""
							));
					}
				}
				cancel = true;
			} else
			if (message.equalsIgnoreCase("undo"))
			{
				if (this.getStage() == 4)
				{
					if (!this.tempSubRegions.isEmpty())
					{
						ProtectedRegion region = this.tempSubRegions.get(this.tempSubRegions.size()-1);
						Worldguard.getRegionManager(player.getLocation().getWorld()).removeRegion(region.getId());
						
						this.tempSubRegions.remove(this.tempSubRegions.size()-1);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Removed the last sub Region",
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
					if (Towns.ExistTown(message))
					{
						this.townName = message;
						this.townID = Towns.FetchTownID(townName);
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the Town of the District to " + message,
								""
								));
						addStage = this.stage+1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "No Town could be found named " + message,
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
							ColorOptions.messageachievement + "Saved the name of the District to " + message,
							""
							));
					addStage = this.stage+1;
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
