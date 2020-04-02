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

import DataManager.Towns;
import DataManager.Worldguard;
import Handlers.ColorOptions;
import Titles.Title;
import Users.User;

/**
 * @author pandi
 *
 */
public class TownCreation extends Creation {

	Title title = new Title();
	
	protected String name;
	protected String description;
	protected Integer requiredTitleID;
	protected ProtectedRegion tempRegion;
	protected List<ProtectedRegion> tempSubRegions = new ArrayList<ProtectedRegion>();
	protected Location spawnpoint;
	/**
	 * @param user
	 */
	public TownCreation(User user) {
		super(user);
		
		this.setStartMessage("Town");
		
		List<Integer> IDList = title.getIDList();
		
		this.setStageMessages(new HashMap<Integer, List<String>>(){{
			put(1, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the name of the Town",
					ColorOptions.messageformat + "Please type the a name and press enter",
					""
					));
			put(2, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the description of the Town",
					ColorOptions.messageformat + "Please type a description and press enter",
					""
					));
			put(3, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the Required TitleID of the Town",
					ColorOptions.messagesubjects + "By setting this, players with a lower Title ID can't enter the Town",
					ColorOptions.messageformat + "Please type the Title ID and press enter",
					ColorOptions.messageformat + "The titles range from " + IDList.get(0) + "-" + IDList.get(IDList.size()-1),
					""
					));
			put(4, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Creating a Worldguard gateRegion",
					ColorOptions.messageformat + "Please make a worldedit selection of the desired town.",
					ColorOptions.messageformat + "If you are satisfied with the selection type 'next'",
					""
					));
			put(5, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Extending the Worldguard gateRegion",
					ColorOptions.messageformat + "If you want to add small parts to the town",
					ColorOptions.messageformat + "you can make a worldedit selection and type 'save'.",
					ColorOptions.messageformat + "If you saved a wrong selection type 'undo'.",
					"",
					ColorOptions.messageformat + "If you are done or if you don't want to add parts",
					ColorOptions.messageformat + "please type 'next'"
					));
			put(6, Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting a Spawnpoint",
					ColorOptions.messageformat + "Stand on the location of the desired spawnpoint and type 'save'",
					""
					));
		}});
		
		this.sendMessage(this.startMessages);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public Integer getRequiredTitleID()
	{
		return this.requiredTitleID;
	}
	
	public ProtectedRegion getRegion()
	{
		return this.tempRegion;
	}
	
	public List<ProtectedRegion> getSubRegions()
	{
		return this.tempSubRegions;
	}
	
	public Location getSpawnpoint()
	{
		return this.spawnpoint;
	}
	
	@Override
	public List<String> getSpecificConfirmMessage()
	{
		return Arrays.asList(
				ColorOptions.stats + "Name: " + ColorOptions.statsresults + this.name,
				ColorOptions.stats + "Description: " + ColorOptions.statsresults + this.description,
				""
				);
	}
	
	@Override
	public void create()
	{
		Towns.CreateTown(this);
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
				if (this.getStage() == 4)
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
				{
					addStage = this.stage+1;
				}
				cancel = true;
			} else
			if (message.equalsIgnoreCase("save"))
			{
				if (this.getStage() == 5)
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
						Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(region);
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
				if (this.getStage() == 6)
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
				}
				cancel = true;
			} else
			if (message.equalsIgnoreCase("undo"))
			{
				
			} else
			{
				if (this.getStage() == 1)
				{
					if (!Towns.ExistTown(message))
					{
						this.name = message;
						this.sendMessage(Arrays.asList(
								"",
								ColorOptions.messageachievement + "Saved the name of the Town to " + message,
								""
								));
						addStage = this.stage+1;
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "This name is already registered as a townname",
								""
							));
					}
					cancel = true;
				} else
				if (this.getStage() == 2)
				{
					this.description = message;
					this.sendMessage(Arrays.asList(
							"",
							ColorOptions.messageachievement + "Saved the description of the Town to " + message,
							""
							));
					addStage = this.stage+1;
					cancel = true;
				} else
				if (this.getStage() == 3)
				{
					if (Main.Main.isInt(message))
					{
						Integer titleID = Integer.valueOf(message);
						if (title.getIDList().contains(titleID))
						{
							this.requiredTitleID = titleID;
							addStage = this.stage+1;
							
							this.sendMessage(Arrays.asList(
									"",
									ColorOptions.messageachievement + "Saved the Required Title ID of the Town to " + message,
									""
									));
						} else
						{
							this.falseCommand(Arrays.asList(
									"",
									ColorOptions.error + "The Title ID you entered is not registered as a Title",
									""
								));
						}
					} else
					{
						this.falseCommand(Arrays.asList(
								"",
								ColorOptions.error + "The Title ID must be a numeric type",
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
