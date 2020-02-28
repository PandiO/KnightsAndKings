package SpawnPoints;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Arenas.Arena;
import Donator.Donator;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Houses.House;
import Main.Main;
import Titles.Title;
import Users.User;
import Users.Users;

public class SpawnPointCommands implements CommandExecutor
{
	Donator donator = new Donator();
	SpawnPoint spawnpoint = new SpawnPoint();
	Title title = new Title();
	House house = new House();
	public Main main;
	public SpawnPointCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> consolecommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of spawnpoint-commands",
			ColorOptions.stats + "-/spawnpoint list",
			ColorOptions.statsformat + "================================================="
	});
	
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of point-commands",
			ColorOptions.stats + "-/point list",
			ColorOptions.stats + "-/point <name>",
			ColorOptions.statsformat + "================================================="
	});
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of spawnpoint-commands",
			ColorOptions.stats + "-/spawnpoint set <name> <requiredtitle> <requireddonator> <price>",
			ColorOptions.stats + "NOTE: set requiredtitle and donator 0 if none is required",
			ColorOptions.stats + "-/spawnpoint remove <name/id>",
			ColorOptions.stats + "-/spawnpoint rename <oldname> <newname>",
			ColorOptions.stats + "-/spawnpoint relocate <name/id>",
			ColorOptions.stats + "-/spawnpoint list",
			ColorOptions.statsformat + "================================================="
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("spawnpoint") || label.equalsIgnoreCase("sp"))
		{
			if (sender.hasPermission("k&k.spawnpoint"))
			{
				if (sender instanceof Player)
				{
					Player player = (Player) sender;
					if (args.length > 0)
					{
						if (args[0].equalsIgnoreCase("set"))
						{
							if (args.length == 5)
							{
								String name = args[1];
								String title = args[2];
								String donator = args[3];
								String pricestring = args[4];
								if (main.isInt(pricestring))
								{
									Integer price = Integer.valueOf(pricestring);
									if (this.title.getTitleID(title) != null || this.title.getIDList().contains(Integer.valueOf(title)))
									{
										Integer titleID = this.title.getTitleID(title);
										if (this.donator.getDonatorIDbyString(donator) != null || this.donator.getDonatorIDList().contains(Integer.valueOf(donator)))
										{
											Integer donatorID = this.donator.getDonatorIDbyString(donator);
											if (spawnpoint.getSpawnPointID(name) == null)
											{
												Location location = player.getLocation();
												try
												{
													spawnpoint.saveSpawnPoint(name, titleID.toString(), price, donatorID.toString(), "", location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
													player.sendMessage(ColorOptions.messageachievement + "Succesfully saved a new spawnpoint with name " + name);
												} catch (Exception e)
												{
													e.printStackTrace();
													player.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
													if (player.isOp())
													{
														player.sendMessage(ColorOptions.error + "Error:" + e);
													}
												}												
											} else
											{
												player.sendMessage(ColorOptions.error + "A different spawnpoint already exists with this name");
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "There is no donator rank named " + donator);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "There is no title named " + title);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "The price of a spawnpoint needs to be a number " + pricestring);
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /spawnpoint set <name> <requiredtitle(put 0 if none is required)> <requireddonator(put 0 if none required)> <price>");
							}
						} else
						if (args[0].equalsIgnoreCase("remove"))
						{
							if (args.length == 2)
							{
								if (main.isInt(args[1]))
								{
									Integer ID = Integer.valueOf(args[1]);
									if (spawnpoint.checkSpawnPoint(ID))
									{
										String name = spawnpoint.getSpawnPointName(ID);
										if (spawnpoint.checkTownSpawnPointbySpawnPoint(ID))
										{
											try
											{
												spawnpoint.removeTownSpawnPoint(ID);
											} catch (Exception e)
											{
												e.printStackTrace();
												player.sendMessage(ColorOptions.error + "Something went wrong while removing the city-spawnpoint, please notify a staff-member");
											}
										}
										try
										{
											spawnpoint.removeSpawnPoint(ID);
											player.sendMessage(ColorOptions.messageachievement + "Succesfully removed a spawnpoint named " + name);
										} catch (Exception e)
										{
											e.printStackTrace();
											player.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No spawnpoint exists with id " + ID);
									}
								} else
								{
									String name = args[1];
									if (spawnpoint.getSpawnPointID(name) != null)
									{
										Integer ID = spawnpoint.getSpawnPointID(name);
										if (spawnpoint.checkTownSpawnPointbySpawnPoint(ID))
										{
											try
											{
												spawnpoint.removeTownSpawnPoint(ID);
											} catch (Exception e)
											{
												e.printStackTrace();
												player.sendMessage(ColorOptions.error + "Something went wrong while removing the city-spawnpoint, please notify a staff-member");
											}
										}
										try
										{
											spawnpoint.removeSpawnPoint(ID);
											player.sendMessage(ColorOptions.messageachievement + "Succesfully removed a spawnpoint named " + name);
										} catch (Exception e)
										{
											e.printStackTrace();
											player.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No spawnpoint exists with name " + name);
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /spawnpoint remove <name/ID>");
							}
						} else
						if (args[0].equalsIgnoreCase("list"))
						{
							player.sendMessage(ColorOptions.statsformat + "=================================================");
							player.sendMessage(ColorOptions.statsformat + "List of available spawnpoints:");
							for (Integer ID : spawnpoint.getSpawnPointList(true, true, true, true, true, true))
							{
								String name = spawnpoint.getSpawnPointName(ID);
								player.sendMessage(ColorOptions.stats + "-Name: " + name + ", ID: " + ID + ", price: " + spawnpoint.getSpawnPointPrice(ID));

							}
							player.sendMessage(ColorOptions.statsformat + "=================================================");
						} else
						if (args[0].equalsIgnoreCase("rename"))
						{
							if (args.length == 3)
							{
								if (main.isInt(args[1]))
								{
									if (spawnpoint.getSpawnPointName(Integer.valueOf(args[1])) != null)
									{
										Integer spawnpointID = Integer.valueOf(args[1]);
										if (spawnpoint.getSpawnPointID(args[2]) == null)
										{
											String oldName = spawnpoint.getSpawnPointName(spawnpointID);
											spawnpoint.saveName(spawnpointID, args[2]);
											player.sendMessage(ColorOptions.messageachievement + "Succesfully renamed spawnpoint " + oldName + " to " + args[2]);
										} else
										{
											player.sendMessage(ColorOptions.error + "This name is already used by another spawnpoint: " + args[2]);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No spawnpoint has been found with ID " + args[1]);
									}
								} else
								{
									if (spawnpoint.getSpawnPointID(args[1]) != null)
									{
										Integer spawnpointID = spawnpoint.getSpawnPointID(args[1]);
										if (spawnpoint.getSpawnPointID(args[2]) == null)
										{
											String oldName = spawnpoint.getSpawnPointName(spawnpointID);
											spawnpoint.saveName(spawnpointID, args[2]);
											player.sendMessage(ColorOptions.messageachievement + "Succesfully renamed spawnpoint " + oldName + " to " + args[2]);
										} else
										{
											player.sendMessage(ColorOptions.error + "This name is already used by another spawnpoint: " + args[2]);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No spawnpoint has bee found named " + args[1]);
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "Usage: /spawnpoint rename <oldname> <newname>");
							}
						} else
						if (args[0].equalsIgnoreCase("relocate"))
						{
							if (args.length == 2)
							{
								if (main.isInt(args[1]))
								{
									if (spawnpoint.getSpawnPointName(Integer.valueOf(args[1])) != null)
									{
										Integer spawnpointID = Integer.valueOf(args[1]);
										String spawnpointName = spawnpoint.getSpawnPointName(spawnpointID);
										Location location = player.getLocation();
										spawnpoint.saveLocation(spawnpointID, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
										player.sendMessage(ColorOptions.messageachievement + "Succesfully saved the new location of " + spawnpointName + " to your current location");
									} else
									{
										player.sendMessage(ColorOptions.error + "No spawnpoint has been found with ID " + args[1]);
									}
								} else
								{
									if (spawnpoint.getSpawnPointID(args[1]) != null)
									{
										Integer spawnpointID = spawnpoint.getSpawnPointID(args[1]);
										Location location = player.getLocation();
										spawnpoint.saveLocation(spawnpointID, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
										player.sendMessage(ColorOptions.messageachievement + "Succesfully saved the new location of " + args[1] + " to your current location");
									} else
									{
										player.sendMessage(ColorOptions.error + "No spawnpoint has been found with ID " + args[1]);
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "Usage: /spawnpoint relocate <name/id>");
							}
						} else
						{
							for (String message : staffcommandhelp)
							{
								player.sendMessage(message);
							}
						}
					} else
					{
						for (String message : staffcommandhelp)
						{
							player.sendMessage(message);
						}
					}
				} else
				{
					if (args.length == 1)
					{
						if (args[0].equalsIgnoreCase("list"))
						{
							sender.sendMessage(ColorOptions.statsformat + "=================================================");
							sender.sendMessage(ColorOptions.statsformat + "List of available spawnpoints:");
							for (Integer ID : spawnpoint.getSpawnPointList(false, false, false, false, false, false))
							{
								String name = spawnpoint.getSpawnPointName(ID);
								if (!spawnpoint.getHouseSpawnPointList().contains(ID))
								{
									sender.sendMessage(ColorOptions.stats + "-Name: " + name + ", ID: " + ID + ", price: " + spawnpoint.getSpawnPointPrice(ID));
								}
							}
							sender.sendMessage(ColorOptions.statsformat + "=================================================");
						} else
						{
							for (String message : consolecommandhelp)
							{
								sender.sendMessage(message);
							}
						}
					} else
					{
						for (String message : consolecommandhelp)
						{
							sender.sendMessage(message);
						}
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		
		if (label.equalsIgnoreCase("point") || label.equalsIgnoreCase("warp"))
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
				if (args.length == 1)
				{
					if (args[0].equalsIgnoreCase("list"))
					{
						player.sendMessage(ColorOptions.statsformat + "List of available spawnpoints(price is in gems and is paid when teleporting):");
						for (Integer ID : spawnpoint.getSpawnPointList(false, false, false, false, false, false))
						{
							String name = spawnpoint.getSpawnPointName(ID);
							if (!name.contains("house") && !name.contains("ocelot"))
							{
								player.sendMessage(ColorOptions.stats + "-Name: " + name + ", price: " + spawnpoint.getSpawnPointPrice(ID));
							}
						}
					} else
					{
						Integer spawnpointID = spawnpoint.getSpawnPointID(args[0]);
						if (spawnpointID != null)
						{
							if (spawnpoint.getSpawnPointList(false, false, false, false, false, false).contains(spawnpointID))
							{
								Arena arena = new Arena();
								if (arena.isDuelling(uuid) == true)
								{
									player.sendMessage(ColorOptions.error + "You can't do this when in a duel!");
									return false;
								}
								spawnpoint.tryRegularTeleport(user, args[0]);
							} else
							{
								player.sendMessage(ColorOptions.error + "You are not allowed to teleport to this location!");
								player.sendMessage(ColorOptions.error + "If you think this is an error, please notify a staff-member");
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "No spawnpoint could be found named " + args[0]);
						}
					}
				} else
				{
					for (String message : commandhelp)
					{
						player.sendMessage(message);
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
	
}
