package Properties;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

import DataManager.Worldguard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Rooms.Room;
import Users.User;
import Users.Users;
import Users.offlineUser;

public class PropertyTouch implements Listener
{
	offlineUser user = new offlineUser();
	Room room = new Room();
	Property property = new Property();
	PropertyCategory category = new PropertyCategory();
	private Main main;
	public PropertyTouch(Main main) 
	{
		this.main = main;
	}

	public static HashMap<UUID, Integer> click = new HashMap<UUID, Integer>();

	@EventHandler
	public void PropertyTouch(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		UUID pu = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(pu);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}
		Block block = e.getClickedBlock();
		RegionManager regionmanager = getWorldGuard().getRegionManager(player.getWorld());
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Integer propertyID = Worldguard.getStructureIDbyRegion("property", block.getLocation(), regionmanager);
			if (propertyID != null)
			{
				if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("Property found!");
				}
				Material material = e.getClickedBlock().getType();
				if (material != Material.ENDER_PORTAL_FRAME && material != Material.WOODEN_DOOR && material != Material.ENCHANTMENT_TABLE && material != Material.ANVIL)
				{
					if (material == Material.CHEST && !user.inOwnerModus())
					{
						if (main.debug)
						{
							Bukkit.getConsoleSender().sendMessage("Chest found!");
						}
						if (!category.getCategoryName(property.getCategoryID(propertyID)).equalsIgnoreCase("tavern"))
						{
							e.setCancelled(true);
						} else
						{
							if (main.debug)
							{
								Bukkit.getConsoleSender().sendMessage("Tavern found!");
							}
							Integer roomID = Worldguard.getStructureIDbyRegion("room", block.getLocation(), regionmanager);
							if (roomID != null)
							{
								if (main.debug)
								{
									Bukkit.getConsoleSender().sendMessage("Room found!");
								}
								Integer ownerID = room.getOwnerID(roomID);
								if (ownerID != null && ownerID != user.getID())
								{
									if (main.debug)
									{
										Bukkit.getConsoleSender().sendMessage("No owner found!");
									}
									e.setCancelled(true);
								}
							}
						}
					}
					if (!category.getCategoryName(property.getCategoryID(propertyID)).equalsIgnoreCase("tavern"))
					{

						//TEST
						Integer ownerID = property.getPropertyOwnerID(propertyID);
						if (ownerID != null && ownerID != 0 && ownerID == user.getID()) 
						{
							if (!click.containsKey(pu))
							{
								click.put(pu, Integer.valueOf(0));
								click.put(pu, click.get(pu) +1);
								player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
								player.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
								player.sendMessage(ColorOptions.stats + "This property is owned by you!");
							} else
							{
								click.put(pu, click.get(pu) +1);
								if (click.get(pu) == 7)
								{
									player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
									player.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
									player.sendMessage(ColorOptions.stats + "This property is owned by you!");
									click.put(pu, Integer.valueOf(0));
								}
							}
						} else
						{
							player.sendMessage(ColorOptions.statsbrackets);
							player.sendMessage(ChatColor.BOLD + "" + ColorOptions.statsresults + "Information");
							player.sendMessage(ColorOptions.stats + "-Propertyname: " + ColorOptions.statsresults + property.getPropertyName(propertyID));
							if (main.ownermodus.containsKey(pu) && main.ownermodus.get(pu) == true)
							{
								player.sendMessage(ColorOptions.stats + "-PropertyID: " + ColorOptions.statsresults + propertyID);
							}
							player.sendMessage(ColorOptions.stats + "-Price: " + ColorOptions.statsresults + property.getPropertyPrice(propertyID));
							player.sendMessage(ColorOptions.stats + "-Income: " + ColorOptions.statsresults + property.getIncome(propertyID));
							player.sendMessage(ColorOptions.stats + "-Category: " + ColorOptions.statsresults + category.getCategoryName(property.getCategoryID(propertyID)));
							if (ownerID == null || ownerID == 0)
							{
								player.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + "-");
							} else
							{
								player.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + this.user.getUserName(this.user.getUUIDbyID(property.getPropertyOwnerID(propertyID))));
								player.sendMessage(ColorOptions.stats + "-Titlename: " + ColorOptions.statsresults + this.user.getTitleName(this.user.getUUIDbyID(property.getPropertyOwnerID(propertyID))));
								player.sendMessage(ColorOptions.stats + "-Experience: " + ColorOptions.statsresults + this.user.getExperience(this.user.getUUIDbyID(property.getPropertyOwnerID(propertyID))));
							}
							player.sendMessage(ColorOptions.statsformat + "==================================");
						}
					}
				}
			}
		}
	}
	
    private WorldGuardPlugin getWorldGuard() 
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) 
        {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) plugin;
    }
}
