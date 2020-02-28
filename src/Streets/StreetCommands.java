package Streets;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;
import Towns.Town;

public class StreetCommands implements CommandExecutor
{
	Town town = new Town();
	Street street = new Street();
	public Main main;
	public StreetCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of street-commands",
			ColorOptions.stats + "-/street list",
			ColorOptions.statsformat + "================================================="
	});
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of street-commands",
			ColorOptions.stats + "-/street set <name> <town-name/town-ID>",
			ColorOptions.stats + "-/street remove <name/id> <town-name/town-ID>",
			ColorOptions.stats + "-/street list",
			ColorOptions.statsformat + "================================================="
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("street"))
		{
			if (sender instanceof Player)
			{
				if (sender.hasPermission("k&k.street"))
				{
					Player player = (Player) sender;
					if (args.length > 0)
					{
						if (args[0].equalsIgnoreCase("set"))
						{
							if (args.length == 3)
							{
								String streetName = args[1];
								if (main.isInt(args[2]))
								{
									if (town.checkTown(Integer.valueOf(args[2])))
									{
										Integer townID = Integer.valueOf(args[2]);
										if (street.checkStreet(streetName, townID) == false)
										{
											setStreet(player, streetName, townID);
										} else
										{
											player.sendMessage(ColorOptions.error + "A different street in town " + town.getTownName(townID) + " already exists with this name");
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No town could be found with ID " + args[2]);
									}
								} else
								{
									if (town.getTownID(args[2]) != null)
									{
										Integer townID = town.getTownID(args[2]);
										if (street.checkStreet(streetName, townID) == false)
										{
											setStreet(player, streetName, townID);
										} else
										{
											player.sendMessage(ColorOptions.error + "A different street in town " + town.getTownName(townID) + " already exists with this name");
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No town could be found named " + args[2]);
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /street set <name> <town-name/town-ID>");
							}
						} else
						if (args[0].equalsIgnoreCase("remove"))
						{
							if (args.length == 3)
							{
								if (main.isInt(args[1]))
								{
									if (street.checkStreetbyID(Integer.valueOf(args[1])))
									{
										Integer streetID = Integer.valueOf(args[1]);
										if (main.isInt(args[2]))
										{
											if (town.checkTown(Integer.valueOf(args[2])))
											{
												Integer townID = Integer.valueOf(args[2]);
												this.removeStreet(player, streetID, townID);
											} else
											{
												player.sendMessage(ColorOptions.error + "No town could be found with ID " + args[2]);
											}
										} else
										{
											if (town.getTownID(args[2]) != null)
											{
												Integer townID = town.getTownID(args[2]);
												this.removeStreet(player, streetID, townID);
											} else
											{
												player.sendMessage(ColorOptions.error + "No town could be found named " + args[2]);
											}
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No street could be found with ID " + args[1]);
									}
								} else
								{
									String streetName = args[1];
									if (main.isInt(args[2]))
									{
										if (town.checkTown(Integer.valueOf(args[2])))
										{
											Integer townID = Integer.valueOf(args[2]);
											if (street.checkStreet(streetName, townID))
											{
												Integer streetID = street.getStreetID(streetName, townID);
												this.removeStreet(player, streetID, townID);
											} else
											{
												player.sendMessage(ColorOptions.error + "No street could be found with name " + streetName + " in town with ID " + args[2]);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No town could be found with ID " + args[2]);
										}
									} else
									{
										if (town.getTownID(args[2]) != null)
										{
											Integer townID = town.getTownID(args[2]);
											if (street.checkStreet(streetName, townID))
											{
												Integer streetID = street.getStreetID(streetName, townID);
												this.removeStreet(player, streetID, townID);
											} else
											{
												player.sendMessage(ColorOptions.error + "No street could be found with name " + streetName + " in the town of " + args[2]);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No town could be found named " + args[2]);
										}
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /street remove <name/ID> <town-name/town-ID>");
							}
						} else
						if (args[0].equalsIgnoreCase("list"))
						{
							if (args.length == 1)
							{
								this.listStreet(player);
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /street list");
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
							this.listStreet((Player) sender);
						} else
						{
							for (String message : commandhelp)
							{
								sender.sendMessage(message);
							}
						}
					} else
					{
						for (String message : commandhelp)
						{
							sender.sendMessage(message);
						}
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
	
	private void setStreet(Player sender, String streetName, Integer townID)
	{
		try
		{
			street.saveStreet(streetName, townID);
			sender.sendMessage(ColorOptions.messageachievement + "Succesfully saved a new street with name " + streetName + " in town " + town.getTownName(townID));
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
	
	private void removeStreet(Player sender, Integer streetID, Integer townID)
	{
		String streetName = street.getStreetName(streetID);
		String townName = town.getTownName(townID);
		if (street.getTownID(streetID) == townID)
		{
			try
			{
				street.removeStreet(streetID, townID);
				sender.sendMessage(ColorOptions.messageachievement + "Succesfully removed street " + streetName + " in town " + townName);
			} catch (Exception e)
			{
				e.printStackTrace();
				sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
			}
		}
	}
	
	public void listStreet(Player sender)
	{
		UUID uuid = sender.getUniqueId();
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of streets:");
		for (Integer streetID : street.getStreetIDList())
		{
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			String streetName = street.getStreetName(streetID);
			String townName = town.getTownName(street.getTownID(streetID));
			sender.sendMessage(ColorOptions.stats + "-Name: " + streetName);
			sender.sendMessage(ColorOptions.stats + "-Town: " + townName);
			if (sender.isOp() || sender.hasPermission("k&k.town") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-street-ID: " + streetID);
			}
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
}
