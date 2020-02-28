package Houses;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Users.User;
import Users.Users;

public class HouseTouch implements Listener
{
	House house = new House();
	Town town = new Town();
	Street street = new Street();
	Title title = new Title();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public HouseTouch(Main main) 
	{
		this.main = main;
	}
	public static HashMap<UUID, Integer> click = new HashMap<UUID, Integer>();
	@EventHandler
	public void PTouch(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
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
			ApplicableRegionSet regionset = regionmanager.getApplicableRegions(block.getLocation());
			if (regionset.size() > 0)
			{
				if (worldguard.getStructureIDbyRegion("house", block.getLocation(), regionmanager) != null)
				{
					
						Integer houseID = worldguard.getStructureIDbyRegion("house", block.getLocation(), regionmanager);
						if (houseID != null)
						{
							Integer streetID = house.getStreetID(houseID);
							Integer townID = street.getTownID(streetID);
							Integer ownerID = house.getHouseOwnerID(houseID);
							if (house.getHouseOwnerID(houseID) == user.getID())
							{
								if (!click.containsKey(uuid))
								{
									click.put(uuid, Integer.valueOf(0));
									click.put(uuid, click.get(uuid) +1);
									player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
									player.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
									player.sendMessage(ColorOptions.stats + "This house is owned by you!");
								} else
								{
									click.put(uuid, click.get(uuid) +1);
									if (click.get(uuid) == 7)
									{
										player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
										player.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
										player.sendMessage(ColorOptions.stats + "This house is owned by you!");
										click.put(uuid, Integer.valueOf(0));
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.statsbrackets);
								player.sendMessage(ColorOptions.stats + "-Housename: " + ColorOptions.statsresults + house.getHouseName(houseID));
								if (user.inOwnerModus())
								{
									player.sendMessage(ColorOptions.stats + "-ID: " + ColorOptions.statsresults + houseID);
								}
								player.sendMessage(ColorOptions.stats + "-Price: " + ColorOptions.statsresults + house.getHousePrice(houseID));
								if (ownerID == null || ownerID == 0)
								{
									player.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + "-");
								} else
								{
									UUID tu = user.getUUID();
									player.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + user.getUsername());
									player.sendMessage(ColorOptions.stats + "-Titlename: " + ColorOptions.statsresults + user.getTitleName());
									player.sendMessage(ColorOptions.stats + "-Experience: " + ColorOptions.statsresults + user.getExperience());
								}
								player.sendMessage(ColorOptions.statsbrackets);
							}
//							if (main.ownermodus.containsKey(uuid) && house.getHouseOwnerID(houseID) != null && house.getHouseOwnerID(houseID) != 0)
//							{
//								Integer userID = house.getHouseOwnerID(houseID);
//								UUID tu = user.getUUIDbyID(userID);
//								player.sendMessage(ColorOptions.statsbrackets);
//								player.sendMessage(ColorOptions.statsformat + "Information about " + house.getHouseName(houseID));	
//								player.sendMessage(ColorOptions.stats + "-Housename: " + ColorOptions.statsresults + house.getHouseName(houseID));
//								player.sendMessage(ColorOptions.stats + "-ID: " + ColorOptions.statsresults + houseID);
//								player.sendMessage(ColorOptions.stats + "-Location: Street: " + ColorOptions.statsresults + street.getStreetName(streetID) + ColorOptions.stats + ", streetnumber: " + ColorOptions.statsresults + house.getHouseNumber(houseID) + ColorOptions.stats + ", town: " + ColorOptions.statsresults + town.getTownName(townID));
//								player.sendMessage(ColorOptions.stats + "-Price: " + ColorOptions.statsresults + house.getHousePrice(houseID));
//								player.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + user.getUserName(tu));
//								player.sendMessage(ColorOptions.stats + "-Titlename: " + ColorOptions.statsresults + title.getTitleName(user.getTitleID(tu), user.getGenderID(tu)));
//								player.sendMessage(ColorOptions.stats + "-Experience: " + ColorOptions.statsresults + user.getExperience(tu));
//								player.sendMessage(ColorOptions.statsbrackets);
//							} else
//							if (house.getHouseOwnerID(houseID) == null || house.getHouseOwnerID(houseID) == 0)
//							{
//								player.sendMessage(ColorOptions.statsbrackets);
//								player.sendMessage(ColorOptions.statsformat + "Information about " + house.getHouseName(houseID));
//								player.sendMessage(ColorOptions.stats + "-Location: Street: " + ColorOptions.statsresults + street.getStreetName(streetID) + ColorOptions.stats + ", streetnumber: " + ColorOptions.statsresults + house.getHouseNumber(houseID) + ColorOptions.stats + ", town: " + ColorOptions.statsresults + town.getTownName(townID));
//								player.sendMessage(ColorOptions.stats + "-Price: " + ColorOptions.statsresults + house.getHousePrice(houseID));
//								player.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + "-");
//								player.sendMessage(ColorOptions.statsbrackets);
//							} else if (house.getHouseOwnerID(houseID) == user.getUserID(uuid))
//							{
//								if (!click.containsKey(uuid))
//								{
//									click.put(uuid, Integer.valueOf(0));
//									click.put(uuid, click.get(uuid) +1);
//									player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
//									player.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
//									player.sendMessage(ColorOptions.stats + "This house is owned by you!");
//								} else
//								{
//									click.put(uuid, click.get(uuid) +1);
//									if (click.get(uuid) == 7)
//									{
//										player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
//										player.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
//										player.sendMessage(ColorOptions.stats + "This house is owned by you!");
//										click.put(uuid, Integer.valueOf(0));
//									}
//								}
//							} else
//							{
//								Integer userID = house.getHouseOwnerID(houseID);
//								UUID tu = user.getUUIDbyID(userID);
//								player.sendMessage(ColorOptions.statsbrackets);
//								player.sendMessage(ColorOptions.statsformat + "Information about " + house.getHouseName(houseID));	
//								player.sendMessage(ColorOptions.stats + "-Housename: " + ColorOptions.statsresults + house.getHouseName(houseID));
//								player.sendMessage(ColorOptions.stats + "-Location: Street: " + ColorOptions.statsresults + street.getStreetName(streetID) + ColorOptions.stats + ", streetnumber: " + ColorOptions.statsresults + house.getHouseNumber(houseID) + ColorOptions.stats + ", town: " + ColorOptions.statsresults + town.getTownName(townID));
//								player.sendMessage(ColorOptions.stats + "-Price: " + ColorOptions.statsresults + house.getHousePrice(houseID));
//								player.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + user.getUserName(tu));
//								player.sendMessage(ColorOptions.stats + "-Titlename: " + ColorOptions.statsresults + title.getTitleName(user.getTitleID(tu), user.getGenderID(tu)));
//								player.sendMessage(ColorOptions.stats + "-Experience: " + ColorOptions.statsresults + user.getExperience(tu));
//								player.sendMessage(ColorOptions.statsbrackets);
//							}
						}
			    }
			}
		}
		e.setCancelled(false);
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
