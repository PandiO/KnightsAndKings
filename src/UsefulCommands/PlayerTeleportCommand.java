package UsefulCommands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Houses.House;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Towns.Town;
import Users.User;
import Users.Users;

public class PlayerTeleportCommand implements CommandExecutor, Listener
{
	SpawnPoint spawnpoint = new SpawnPoint();
	House house = new House();
	Street street = new Street();
	Town town = new Town();
	private Main main;
	public PlayerTeleportCommand(Main main)
	{
		this.main = main;
	}
	
	public static Map<UUID, Boolean> teleportdelay = new HashMap<UUID, Boolean>();
	public static Map<UUID, Integer> teleportconfirm = new HashMap<UUID, Integer>();

	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of staff tpa-commands",
			ColorOptions.stats + "-/tpa <player1> <player2> to teleport player1 to player2",
			ColorOptions.stats + "-/tpa <player1> <player2> <silent> to teleport players without receiving a message",
			ColorOptions.stats + "-/tpa <player> <spawnpoint> to teleport to a players spawnpoint",
			ColorOptions.stats + "-/tpa <player> to teleport to a player",
			ColorOptions.statsformat + "================================================="
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
	
		if (label.equalsIgnoreCase("tpa"))
		{
			if (args.length >= 1)
			{
				if (sender instanceof Player)
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
					if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
					{
						if (args.length >= 2 && args.length <= 3)
						{
							if (Bukkit.getOnlinePlayers().contains(args[0]))
							{
								Player target1 = Bukkit.getPlayer(args[0]);
								if (Bukkit.getOnlinePlayers().contains(args[1]))
								{
									Player target2 = Bukkit.getPlayer(args[1]);
									if (args[2].equalsIgnoreCase("silent") || args[2].equalsIgnoreCase("s"))
									{
										target1.teleport(target2.getLocation());
										player.sendMessage(ColorOptions.messageformat + "Teleported " + ColorOptions.messagesubjects + target1.getName() + ColorOptions.messageformat + " to " + ColorOptions.messagesubjects + target2.getName() );
									} else
									{
										target1.teleport(target2.getLocation());
										player.sendMessage(ColorOptions.messageformat + "Teleported " + ColorOptions.messagesubjects + target1.getName() + ColorOptions.messageformat + " to " + ColorOptions.messagesubjects + target2.getName() );
										target1.sendMessage(ColorOptions.messageformat + "You have been teleported to " + ColorOptions.messagesubjects + target2.getName());
										target2.sendMessage(ColorOptions.messagesubjects + target1.getName() + ColorOptions.messageformat + " has been teleported to you");
									}
								} else
								{
									if (Users.existUser(args[1]))
									{
										player.sendMessage(ColorOptions.falsecommand + args[1] + " is not online!");
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Can't find player " + args[1]);
									}
								}
							} else
							if (Users.existUser(args[0]))
							{
								UUID targetuuid = Users.fetchUUIDbyUsername(args[0]);
								User userTarget = null;
								
								try
								{
									userTarget = Users.getUser(targetuuid);
								} catch (UserNotFoundException ex)
								{
									ErrorHandlers.userNotFoundAction(((Player) sender), Bukkit.getPlayer(targetuuid), false);
									return false;
								} catch (Exception ex)
								{
									ex.printStackTrace();
									ErrorHandlers.userNotFoundAction(((Player) sender), Bukkit.getPlayer(targetuuid), false);
									return false;
								}
								if (args[1].equalsIgnoreCase("spawnpoint") || args[1].equalsIgnoreCase("sp"))
								{
									if (spawnpoint.getSpawnPointName(userTarget.getSpawnpointID()) != "spawn")
									{
										Integer spawnpointID = userTarget.getSpawnpointID();
										for (int i : house.getHouseIDList(null))
										{
											if (house.getHouseSpawnPoint(i) == spawnpointID)
											{
												player.teleport(spawnpoint.getSpawnPointLocation(spawnpointID));
												String housename = house.getHouseName(i);
												Integer streetnumber = house.getHouseNumber(i);
												Integer streetID = house.getStreetID(i);
												String streetName = street.getStreetName(streetID);
												String townName = town.getTownName(street.getTownID(streetID));
												player.sendMessage(ColorOptions.messageachievement + "You teleported to the respawnlocation of house " + ColorOptions.messagesubjects + housename + ColorOptions.messageachievement + " on the " + ColorOptions.messagesubjects + streetName + ColorOptions.messageachievement + " with number " + ColorOptions.messagesubjects + streetnumber + ColorOptions.messageachievement + " in the town of " + ColorOptions.messagesubjects + townName);
											} else
											{
												player.sendMessage(ColorOptions.falsecommand + "An error occured when looking for the house, please contact Pandi");
											}
										} 
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "This player has configured spawn as his respawnlocation");
									}
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "Usage: /tpa <player> <spawnpoint>");
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Can't find player " + args[0]);
							}
						} else if (args.length == 1)
						{
							if (Bukkit.getOnlinePlayers().contains(args[0]))
							{
								player.teleport(Bukkit.getPlayer(args[0]));
								player.sendMessage(ColorOptions.messageachievement + "You teleported to " + ColorOptions.messagesubjects + args[0]);
							} else
							{
								if (Users.existUser(args[0]))
								{
									player.sendMessage(ColorOptions.falsecommand + "This players is not online!");
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "Can't find a player with the name " + args[0]);
								}
							}
						} else
						{
							
						}
					} else if (sender.hasPermission("k&k.teleport.normal"))
					{
						if (args.length == 1)
						{
							if (user.getCoins() >= 10000)
							{
								if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("a"))
								{
								} else if (args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("d"))
								{
									
								} else if (Bukkit.getOnlinePlayers().contains(args[0]))
								{
									teleportconfirm.put(uuid, Integer.valueOf(30));
									player.sendMessage(ColorOptions.falsecommand + "" + ChatColor.BOLD + "You are about to pay 10000 coins for teleporting, type cancel to cancel the tpa");
									player.sendMessage(ColorOptions.messageformat + "Awaiting confirmation from " + ColorOptions.messagesubjects + args[0] + ColorOptions.messageformat + ", he/she has 30 seconds to confirm");
									Bukkit.getPlayer(args[0]).sendMessage(ColorOptions.messageformat + "You received a teleport request from " + ColorOptions.messagesubjects + player.getName() + ColorOptions.messageformat + ", type /tpa <accept/deny> to accept or deny the request");
								}							
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "You need " + ColorOptions.coinStats + "10000 " + ColorOptions.falsecommand + "coins to teleport to another player!");
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Usage: /tpa <player> to teleport to a player");
							player.sendMessage(ColorOptions.falsecommand + "WARNING: teleporting costs 10000 coins");
						}
					}
				}
			} else
			{
				if (sender.hasPermission("k&k.teleport.normal"))
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /tpa <player> to request to teleport to this player, you need " + ColorOptions.coinStats + "10000 " + ColorOptions.falsecommand + "coins to teleport" );
				}
				if (sender.hasPermission("k&k.teleport.staff"))
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /tpa <player> to teleport to a player");
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /tpa <player1> <player2> to teleport player1 to player2");
				}
				if (sender.hasPermission("k&k.teleport.owner") || sender.isOp())
				{
					for (String s : staffcommandhelp)
					{
						sender.sendMessage(s);
					}
				}
			}
		}
		return false;
		
	}
	
	@EventHandler
	public void OnChat(PlayerChatEvent e)
	{
		if (e.getMessage().equalsIgnoreCase("cancel"))
		{
			if (teleportconfirm.containsKey(e.getPlayer().getUniqueId()))
			{
				teleportconfirm.remove(e.getPlayer().getUniqueId());
			}
		}
	}
}
