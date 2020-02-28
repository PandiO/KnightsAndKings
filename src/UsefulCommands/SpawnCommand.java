package UsefulCommands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Arenas.Arena;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;

public class SpawnCommand implements CommandExecutor
{
	SpawnPoint spawnpoint = new SpawnPoint();
	private Main main;
	public SpawnCommand(Main main)
	{
		this.main = main;
	}
	
	public static Map<UUID, Boolean> teleportdelay = new HashMap<UUID, Boolean>();
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		
		if (label.equalsIgnoreCase("spawn"))
		{
			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, player, true);
				return false;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, player, true);
				return false;
			}
			if (player.hasPermission("k&k.spawn"))
			{
				if (spawnpoint.getSpawnPointID("spawn") != null)
				{
					Location loc = spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("spawn"));
					if (args.length == 1)
					{
						for (Player target : Bukkit.getServer().getOnlinePlayers())
						{
							if (args[0].equalsIgnoreCase(target.getName()))
							{
								if (player.hasPermission("k&k.spawn.others"))
								{
									target.teleport(loc);
					            	player.sendMessage(ColorOptions.messageachievement + "You teleported " + ColorOptions.messagesubjects + target.getName() + ColorOptions.messageachievement + " to " + ColorOptions.messagesubjects + "spawn");
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
								}
							}
						}
					} else
					{
						if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
						{
							if (spawnpoint.getSpawnPointID("spawn") != null)
							{
								
							    player.teleport(loc);
				            	player.sendMessage(ColorOptions.messageachievement + "You teleported to " + ColorOptions.messagesubjects + "spawn");
							} else if (player.isOp())
							{
								player.sendMessage(ColorOptions.falsecommand + "No spawn-location has been set! This might cause glitches or errors");
							}
						} else
						{
							if (spawnpoint.getSpawnPointID("spawn") != null)
							{
//								player.sendMessage(ColorOptions.messageformat + "You need to wait 3 seconds before teleporting...");
//								teleportdelay.put(uuid, true);
//								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
//						    	{
//						            public void run()
//						            {
//									    player.teleport(loc);
//						            	player.sendMessage(ColorOptions.messageachievement + "You teleported to " + ColorOptions.messagesubjects + "spawn");
//						            }
//						        }, 3*20);
								Arena arena = new Arena();
								if (arena.isDuelling(uuid) == true)
								{
									player.sendMessage(ColorOptions.error + "You can't do this when in a duel!");
									return false;
								}							
								spawnpoint.tryRegularTeleport(user, "spawn");
							} else if (player.isOp())
							{
								player.sendMessage(ColorOptions.falsecommand + "No spawn-location has been set! This might cause glitches or errors");
							}
						}
					}
				}
			} else
			{
				player.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
			}
		}
		return false;
	}
}
