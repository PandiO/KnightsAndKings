package Teleport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;

public class TeleportDelay implements Listener
{
	SpawnPoint spawnpoint = new SpawnPoint();
	public Main main;
	
	public static HashMap<UUID, Integer> Delay = new HashMap<UUID, Integer>();
	public static List<UUID> Immune = new ArrayList<UUID>();
	public static HashMap<UUID, Location> TargetLoc = new HashMap<UUID, Location>();
	public static HashMap<UUID, String> TargetName = new HashMap<UUID, String>();
	public static HashMap<UUID, Integer> TargetPrice = new HashMap<UUID, Integer>();
	
	
	public static boolean hasDelay(UUID uuid)
	{
		boolean contains = false;
		
		if (Delay.containsKey(uuid))
		{
			contains = true;
		}
		
		return contains;
	}
	
	public static boolean hasImmune(UUID uuid)
	{
		boolean contains = false;
		
		if (Immune.contains(uuid))
		{
			contains = true;
		}
		
		return contains;
	}
	
	public static boolean hasLocation(UUID uuid)
	{
		boolean contains = false;
		
		if (TargetLoc.containsKey(uuid))
		{
			contains = true;
		}
		
		return contains;
	}
	
	public static boolean hasText(UUID uuid)
	{
		boolean contains = false;
		
		if (TargetName.containsKey(uuid))
		{
			contains = true;
		}
		
		return contains;
	}
	
	public static boolean hasPrice(UUID uuid)
	{
		boolean contains = false;
		
		if (TargetPrice.containsKey(uuid))
		{
			contains = true;
		}
		
		return contains;
	}
	
	public static void setDelay(UUID uuid, Integer delay, Location location, Integer price, String text, boolean immune)
	{
		if (hasDelay(uuid) == false)
		{
			String immuneText = "Invulnerable";
			
			if (immune == false)
			{
				immuneText = "Vulnerable";
			} else
			{
				setImmune(uuid);
			}
			Delay.put(uuid, delay);
			TargetLoc.put(uuid, location);
			TargetName.put(uuid, text);
			TargetPrice.put(uuid, price);
			
			Bukkit.getConsoleSender().sendMessage("Player with uuid " + uuid + " is waiting for teleportation for " + delay + " seconds and is " + immuneText);

		}
	}
	
	public static void setImmune(UUID uuid)
	{
		Immune.add(uuid);
	}
	
	public static void removeDelay(UUID uuid)
	{
		if (hasDelay(uuid))
		{
			Delay.remove(uuid);
		}
	}
	
	public static void removeImmune(UUID uuid)
	{
		if (hasImmune(uuid))
		{
			Immune.remove(uuid);
		}
	}
	
	public static void removeLocation(UUID uuid)
	{
		if (hasLocation(uuid))
		{
			TargetLoc.remove(uuid);
		}
	}
	
	public static void removeText(UUID uuid)
	{
		if (hasText(uuid))
		{
			TargetName.remove(uuid);
		}
	}
	
	public static void removePrice(UUID uuid)
	{
		if (hasPrice(uuid))
		{
			TargetPrice.remove(uuid);
		}
	}
	
	public void updateDelay()
	{
		for (UUID uuid : Delay.keySet())
		{
			if (Bukkit.getPlayer(uuid) != null)
			{
				User user = null;
				
				try
				{
					user = Users.getUser(uuid);
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(null, Bukkit.getPlayer(uuid), true);
					return;
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(null, Bukkit.getPlayer(uuid), true);
					return;
				}
				Delay.put(uuid, Delay.get(uuid)-1);
				if (Delay.get(uuid) == 0)
				{
					removeDelay(uuid);
					if (hasImmune(uuid))
					{
						removeImmune(uuid);
					}
					executeTeleport(user);
				}
			} else
			{
				removeDelay(uuid);
				removeLocation(uuid);
				if (hasImmune(uuid))
				{
					removeImmune(uuid);
				}
			}
		}
	}
	
	public static void cancelTeleport(UUID uuid, boolean movement)
	{
		if (TeleportDelay.hasDelay(uuid))
		{
			TeleportDelay.removeDelay(uuid);
			if (TeleportDelay.hasLocation(uuid))
			{
				TeleportDelay.removeLocation(uuid);

			}
			if (TeleportDelay.hasText(uuid))
			{
				TeleportDelay.removeText(uuid);
			}
			if (TeleportDelay.hasImmune(uuid))
			{
				TeleportDelay.removeImmune(uuid);
			}
			TeleportDelay.removePrice(uuid);
			if (movement)
			{
				if (Bukkit.getPlayer(uuid) != null)
				{
					Player player = Bukkit.getPlayer(uuid);
					player.sendMessage(ColorOptions.error + "Teleportation canceled due to movement!");
				}
			} else
			{
				if (Bukkit.getPlayer(uuid) != null)
				{
					Player player = Bukkit.getPlayer(uuid);
					player.sendMessage(ColorOptions.error + "Teleportation canceled!");
				}
			}
		}
	}
	
	public void executeTeleport(User user)
	{
		UUID uuid = user.getUUID();
		if (TargetLoc.containsKey(uuid))
		{
			Location location = TargetLoc.get(uuid);
			Integer price = TargetPrice.get(uuid);
			Player player = (Player) Bukkit.getPlayer(uuid);
			if (player != null)
			{
				if (user.getGems() >= price)
				{
					if (price > 0)
					{
						user.removeGems(price);
		            	player.sendMessage(ColorOptions.currencycolor + "You paid " + ColorOptions.gemStats + price + " gems" + ColorOptions.currencycolor + " and your new balance is " + ColorOptions.gemStats + user.getGems());
					}
					
					String teleportName = TargetName.get(uuid);
					if (teleportName.contains("house") || teleportName.contains("room"))
					{
						teleportName = "Home";
					}
					if (location != null)
					{
						try
						{
							spawnpoint.teleport(user, location);

						} catch(Exception e)
						{
							e.printStackTrace();
						}
						Bukkit.getConsoleSender().sendMessage("Player " + player.getName() + " succesfully teleported to his location");
					} else
					{
						player.sendMessage(ColorOptions.error + "The location you wanted to teleport to doesn't exist!");
					}
		        	player.sendMessage(ColorOptions.messageachievement + "You teleported to " + ColorOptions.messagesubjects + teleportName);
					removeText(uuid);
					removeLocation(uuid);
				} else
				{
					player.sendMessage(ColorOptions.error + "You don't have enough gems to teleport to this location!");
					if (TeleportDelay.hasDelay(uuid))
					{
						TeleportDelay.removeDelay(uuid);
					}
					if (TeleportDelay.hasLocation(uuid))
					{
						TeleportDelay.removeLocation(uuid);

					}
					if (TeleportDelay.hasText(uuid))
					{
						TeleportDelay.removeText(uuid);
					}
					if (TeleportDelay.hasImmune(uuid))
					{
						TeleportDelay.removeImmune(uuid);
					}
					TeleportDelay.removePrice(uuid);
				}
			} else
			{
				removeLocation(uuid);
			}
		} else
		{
			Bukkit.getConsoleSender().sendMessage("No location stored..");
		}
	}
}
