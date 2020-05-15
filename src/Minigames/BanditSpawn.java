package Minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Main.Main;
import Products.Product;
import Towns.Town;
import Traits.Bandit_v2;
import Users.User;
import Users.offlineUser;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class BanditSpawn implements Listener
{
	Town town = new Town();
	offlineUser user = new offlineUser();
	WorldGuard worldguard = new WorldGuard();
	Product product = new Product();
    NPCRegistry registry = CitizensAPI.getNPCRegistry();
	private Main main;
	public BanditSpawn(Main main)
	{
		this.main = main;
	}
	
	boolean debug = false;
//	public static HashMap<UUID, Integer> bandits = new HashMap<UUID, Integer>();
	public static HashMap<UUID, List<Integer>> banditID = new HashMap<UUID, List<Integer>>();
	Map<User, Location> locationList = new HashMap<User, Location>();

	
//	@EventHandler
//	public void onWalk(PlayerMoveEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(uuid);
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (UserIsNpcException ex)
//		{
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		if (!user.inStaffModus() &&
//				!user.inOwnerModus() &&
//				!user.inSafeZone() &&
//				player.getGameMode() == GameMode.SURVIVAL &&
//				!player.getAllowFlight() &&
//				!BanditAmbushes.hasAmbush(user))
//		{
//			if (!locationList.containsKey(user))
//			{
//				locationList.put(user, player.getLocation());
//				return;
//			}
//			
//			if (locationList.get(user).distance(player.getLocation()) >= 5)
//			{
//				BanditAmbushes.tryAmbush(user);
//				locationList.put(user, player.getLocation());
//			}
//		}
//	}
//	@EventHandler
//	public void onWalk(PlayerMoveEvent event)
//	{
//		Player player = event.getPlayer();
//		UUID uuid = player.getUniqueId();
//		Location location = player.getLocation();
//		RegionManager regionmanager = worldguard.getWorldGuard().getRegionManager(player.getWorld());
//		ApplicableRegionSet regionset = regionmanager.getApplicableRegions(player.getLocation());
//		
//		if (user.inStaffModus(uuid) || user.inOwnerModus(uuid) || player.getGameMode() != GameMode.SURVIVAL || player.getAllowFlight() == true || regionset.getRegions().size() > 0)
//		{
//			if (banditID.containsKey(uuid))
//			{
//				for (Integer i : banditID.get(uuid))
//				{
//					NPC bandit = registry.getById(i);
//					try
//					{
//						registry.deregister(bandit);
//					} catch (Exception e)
//					{
//						
//					}
//				}
//				banditID.remove(uuid);
//				player.sendMessage(ColorOptions.message + "You succesfully fled from the bandits!");
//			}
//			return;
//		} else if (banditID.containsKey(uuid))
//		{
//			if (!banditID.get(uuid).isEmpty())
//			{
//				NPC npc = registry.getById(banditID.get(uuid).get(0));
//				if (npc == null)
//				{
//					banditID.get(uuid).remove(0);
//					return;
//				}
//				if (npc.isSpawned())
//				{
//					Entity entity = npc.getEntity();
//					if (entity.getLocation().distance(location) >= 30)
//					{
//						for (Integer i : banditID.get(uuid))
//						{
//							NPC bandit = registry.getById(i);
//							registry.deregister(bandit);
//						}
//						banditID.remove(uuid);
//						player.sendMessage(ColorOptions.message + "You succesfully fled from the bandits!");
//						if (main.debug)
//						{
//							Bukkit.getConsoleSender().sendMessage("Distance from npc's larger or equal to 30");
//						}
//					}
//					if (worldguard.getStructureIDbyRegion("town", location, worldguard.getWorldGuard().getRegionManager(location.getWorld())) != null)
//					{
//						for (Integer i : banditID.get(uuid))
//						{
//							NPC bandit = registry.getById(i);
//							registry.deregister(bandit);
//						}
//						banditID.remove(uuid);
//						player.sendMessage(ColorOptions.message + "You succesfully fled from the bandits!");
//						if (main.debug)
//						{
//							Bukkit.getConsoleSender().sendMessage("Player entered town");
//						}
//					}
//				} else
//				{
//					banditID.get(uuid).remove(0);
//				}
//			} else
//			{
//				banditID.remove(uuid);
//				player.sendMessage(ColorOptions.message + "You succesfully fled from the bandits!");
//				if (main.debug)
//				{
//					Bukkit.getConsoleSender().sendMessage("No bandits found in bandit list of this player!");
//				}
//			}
//			return;
//		} else
//		if (locationList.containsKey(uuid))
//		{
//			Location listLocation = locationList.get(uuid);
//			if (location.distance(listLocation) >= 5)
//			{
//				if (isNight(player.getWorld()))
//				{
//					spawnBandits(player, location, 10);
//				} else
//				{
//					spawnBandits(player, location, 1);
//				}
//				if (main.debug)
//				{
//					Bukkit.getConsoleSender().sendMessage("Not in overriding state, no bandits spawned, spawning bandits!");
//				}
//				locationList.put(uuid, location);
//			}
//		} else
//		{
//			if (main.debug)
//			{
//				Bukkit.getConsoleSender().sendMessage("Not in overriding state, no bandits spawned and not in locationlist");
//			}
//			locationList.put(uuid, location);
//		}
//		
////		if (!main.ownermodus.containsKey(uuid) || main.ownermodus.get(uuid) == false)
////		{
////			if (!banditID.containsKey(uuid) && player.getGameMode() == GameMode.SURVIVAL && player.getAllowFlight() != true)
////			{
////				if (regionset.getRegions().size() == 0)
////				{
////					if (locationList.containsKey(uuid))
////					{
////						Location listLocation = locationList.get(uuid);
////						if (location.distance(listLocation) >= 5)
////						{
////							if (isNight(player.getWorld()))
////							{
////								spawnBandits(player, location, 10);
////							} else
////							{
////								spawnBandits(player, location, 1);
////							}
////							locationList.put(uuid, location);
////						}
////					} else
////					{
////						locationList.put(uuid, location);
////					}
////				}
////			} else
////			{
////				if (banditID.containsKey(uuid))
////				{
////					NPC npc = registry.getById(banditID.get(uuid).get(0));
////					if (npc.getStoredLocation().distance(location) >= 30)
////					{
////						for (Integer i : banditID.get(uuid))
////						{
////							NPC bandit = registry.getById(i);
////							registry.deregister(bandit);
////						}
////						banditID.remove(uuid);
////						player.sendMessage(ColorOptions.message + "You succesfully fled from the bandits!");
////					}
////					if (worldguard.getStructureIDbyRegion("town", location, worldguard.getWorldGuard().getRegionManager(location.getWorld())) != null)
////					{
////						for (Integer i : banditID.get(uuid))
////						{
////							NPC bandit = registry.getById(i);
////							registry.deregister(bandit);
////						}
////						banditID.remove(uuid);
////						player.sendMessage(ColorOptions.message + "You succesfully fled from the bandits!");
////					}
////				}
////			}
////		}
//	}
	
	
	public boolean isNight(World world) 
	{
	    long time = world.getTime();

	    if(time > 0 && time < 12300) 
	    {
	        return false;
	    } else 
	    {
	        return true;
	    }
	}
	
	public void spawnBandits(Player player, Location playerLocation, Integer chance)
	{
		if (debug)
		{
			Bukkit.getConsoleSender().sendMessage("Spawning bandits for " + player.getName());
		}
		Random random = new Random();
		Integer rand = random.nextInt(100);
	    if (rand <= chance)
		{
			Integer titleID = user.getTitleID(player.getUniqueId());
			if (titleID == null)
			{
				Bukkit.getConsoleSender().sendMessage("TitleID of " + player.getName() + " is null!");
			}
			if (titleID < 5)
			{
				createBandits(player, 1);
			} else if (titleID > 5 && titleID <= 10)
			{
				createBandits(player, main.getRandom(1, 2));
			} else if (titleID > 10 && titleID <= 15)
			{
				createBandits(player, main.getRandom(2, 4));
			} else if (titleID > 15)
			{
				createBandits(player, main.getRandom(3, 6));
			}
		} else
		{
			if (debug)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Random: " + rand + ", Chance: " + chance + ", random has to be smaller or equal to chance!");
			}
		}
	}
	
	public void createBandits(Player player, Integer amount)
	{
	    List<Integer> list = new ArrayList<Integer>();
	    for (int i = 0; i < amount; i++)
	    {
	    	if (debug)
	    	{
	    		Bukkit.getConsoleSender().sendMessage("Spawning loop " + i + " for player " + player.getName());
	    	}
		    NPC bandit = registry.createNPC(EntityType.PLAYER, "Bandit");
		    bandit.setProtected(false);
		    list.add(bandit.getId());
		    if (debug)
		    {
				Bukkit.getConsoleSender().sendMessage("Adding banditID to bandit-list of " + player.getName());
		    }
			bandit.addTrait(Bandit_v2.class);
			bandit.faceLocation(player.getLocation());
	    }
	    this.banditID.put(player.getUniqueId(), list);
	    for (Integer npcID : banditID.get(player.getUniqueId()))
	    {
	    	NPC bandit = registry.getById(npcID);
	    	if (bandit != null)
	    	{
	    	    bandit.spawn(getRandomBanditLocation(player));
	    	}
	    }
	    if (debug)
	    {
			Bukkit.getConsoleSender().sendMessage("Setting banditID hashmap with list of " + player.getName() + ", content: " + list);
	    }
		player.sendMessage(ColorOptions.falsecommand + "Watch out! Bandits ambushed you!");
		player.sendMessage(ColorOptions.falsecommand + "Kill them to receive a reward!");
	}
	public Location getRandomBanditLocation(Player player)
	{
		Location finalLoc = player.getLocation();
		
		Location eyeLocation = player.getEyeLocation();
		Location playerLocation = player.getLocation();
		
		Vector direction = playerLocation.getDirection();
		direction.setY(0);
		direction.normalize();
		direction.multiply(8);
		Bukkit.getConsoleSender().sendMessage("Vector: " + direction.toString());
		Location front = eyeLocation.add(direction);
		
		Double x = front.getX();
		Double z = front.getZ();
		Double randomX = Double.valueOf(main.getRandom(x.intValue(), x.intValue()+3));
		Double randomZ = Double.valueOf(main.getRandom(z.intValue(), z.intValue()+3));
		front.setX(randomX);
		front.setZ(randomZ);
		finalLoc = front;
//		front.getWorld().dropItemNaturally(front, new ItemStack(Material.GOLD_BLOCK, 64));
		if (debug)
		{
			Bukkit.getConsoleSender().sendMessage("Distance from spawning: " + player.getLocation().distance(finalLoc));
			if (front != null)
			{
				Bukkit.getConsoleSender().sendMessage("Returning front-location of player " + player.getName());
			} else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Returning front-location = null of player " + player.getName());
			}
			Bukkit.getConsoleSender().sendMessage("Distance from spawning: " + player.getLocation().distance(finalLoc));
		}
		return finalLoc;
	}
	
	public static List<Integer> getActiveBandits()
	{
		List<Integer> list = new ArrayList<Integer>();
		
		for (UUID uuid : banditID.keySet())
		{
			list.addAll(banditID.get(uuid));
		}
		
		return list;
	}
 
}
