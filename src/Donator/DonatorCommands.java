package Donator;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Handlers.ColorOptions;
import Main.Main;
import Users.User;
import Users.Users;

public class DonatorCommands implements CommandExecutor
{
	Donator donator = new Donator();
	public Main main;
	public DonatorCommands(Main main) 
	{
		this.main = main;
	}
	
	//Largest donator id currently in the game
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of donator-commands",
			ColorOptions.stats + "-/donator set <donatorName> <username>",
			ColorOptions.stats + "-/donator tempset <donatorName> <username> <time> <timeValue(seconds/hours/days)>",
			ColorOptions.stats + "-/donator upgrade <username>",
			ColorOptions.stats + "-/donator downgrade <username>",
			ColorOptions.stats + "-/donator remove <username>",
			ColorOptions.stats + "-/donator list",
			ColorOptions.statsformat + "================================================="
	});
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of donator-commands",
			ColorOptions.stats + "-/donator list",
			ColorOptions.statsformat + "================================================="
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("donator"))
		{
			if (sender.hasPermission("k&k.donator"))
			{
				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("set"))
					{
						if (args.length == 3)
						{
							String donator = args[1];
							if (args[1].equalsIgnoreCase("dragonblood") || args[1].equalsIgnoreCase("db"))
							{
								donator = "dragon blood";
							}
							if (this.donator.getDonatorNames().contains(donator.toLowerCase()))
							{
								if (Bukkit.getPlayer(args[2]) != null)
								{
									
								} else if (Users.existUser(args[2]))
								{
									UUID uuid = Users.fetchUUIDbyUsername(args[2]);
									Users.setDonator(sender, uuid, this.donator.getDonatorID(donator.toLowerCase()));
								} else
								{
									sender.sendMessage(ColorOptions.error + "Can't find user with username " + args[2]);
								}
							} else
							{
								sender.sendMessage(ColorOptions.falsecommand + "This donator rank does not exist: " + args[1]);
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /donator set <donator> <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("tempset"))
					{
						if (args.length == 5)
						{
							String donator = args[1];
							String username = args[2];
							String timevalue = args[4];
							if (donator.equalsIgnoreCase("dragonblood") || donator.equalsIgnoreCase("db"))
							{
								donator = "dragon blood";
							}
							Integer donatorID = this.donator.getDonatorID(donator);
							if (donatorID != null)
							{
								if (Users.existUser(username))
								{
									if (main.isInt(args[3]))
									{
										Integer time = Integer.valueOf(args[3]);
										if (timevalue.equalsIgnoreCase("days") || timevalue.equalsIgnoreCase("day") || timevalue.equalsIgnoreCase("hour") || timevalue.equalsIgnoreCase("hours") || timevalue.equalsIgnoreCase("minutes") || timevalue.equalsIgnoreCase("minute") || timevalue.equalsIgnoreCase("seconds") | timevalue.equalsIgnoreCase("second"))
										{
											UUID uuid = Users.fetchUUIDbyUsername(username);
											User target = null;
											if (Users.getUser(uuid) != null)
											{
												target = Users.getUser(uuid);
											} else
											{
												target = new User(uuid);
											}
											target.setTempDonator(donatorID, main.calculateTimeValue(time, timevalue));
											target.destroy();
										} else
										{
											sender.sendMessage(ColorOptions.error + "Please specify a correct time-value like seconds, minutes, hours or days");
										}
									} else
									{
										sender.sendMessage(ColorOptions.falsecommand + "The expire-time has to be a number: " + args[3]);
									}
								} else
								{
									sender.sendMessage(ColorOptions.error + "Can't find user with username " + args[2]);
								}
							} else
							{
								sender.sendMessage(ColorOptions.falsecommand + "This donator rank does not exist: " + args[1]);
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /donator tempset <donator> <username> <expireTime> <timeValue(minuts/hours)>");
						}
					} else
					if (args[0].equalsIgnoreCase("upgrade"))
					{
						if (args.length == 2)
						{
							if (Users.existUser(args[1]))
							{
								UUID uuid = Users.fetchUUIDbyUsername(args[1]);
								User target = null;
								if (Users.getUser(uuid) != null)
								{
									target = Users.getUser(uuid);
								} else
								{
									target = new User(uuid);
								}
								target.upgradeDonator(sender);
								target.destroy();
							} else
							{
								sender.sendMessage(ColorOptions.error + "Can't find user with username " + args[2]);
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /donator upgrade <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("downgrade"))
					{
						if (args.length == 2)
						{
							if (Users.existUser(args[1]))
							{
								UUID uuid = Users.fetchUUIDbyUsername(args[1]);
								User target = null;
								if (Users.getUser(uuid) != null)
								{
									target = Users.getUser(uuid);
								} else
								{
									target = new User(uuid);
								}
								target.downgradeDonator(sender);
								target.destroy();
							} else
							{
								sender.sendMessage(ColorOptions.error + "Can't find user with username " + args[2]);
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /donator downgrade <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("remove"))
					{
						if (args.length == 2)
						{
							if (Users.existUser(args[1]))
							{
								Users.setDonator(sender, Users.fetchUUIDbyUsername(args[1]), this.donator.getDonatorID("Default"));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Can't find user with username " + args[2]);
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /donator remove <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("list"))
					{
						sender.sendMessage(ColorOptions.statsformat + "Currently available donator ranks:");
						for (String rank : this.donator.getDonatorNames())
						{
							Integer ID = donator.getDonatorID(rank);
							sender.sendMessage(ColorOptions.stats + "- " + donator.getDonatorColorSecondary(ID) + "-{" + donator.getDonatorColorPrimary(ID) + donator.getDonatorName(ID) + " " + sender.getName() + donator.getDonatorColorSecondary(ID) + "}-");
						}
					} else
					{
						for (String message : this.staffcommandhelp)
						{
							sender.sendMessage(message);
						}
					}
				} else
				{
					for (String message : this.staffcommandhelp)
					{
						sender.sendMessage(message);
					}
				}
			} else
			{
				if (args.length == 1)
				{
					if (args[0].equalsIgnoreCase("list"))
					{
						sender.sendMessage(ColorOptions.statsformat + "Currently available donator ranks:");
						for (String rank : this.donator.getDonatorNames())
						{
							Integer ID = donator.getDonatorID(rank);
							sender.sendMessage(ColorOptions.stats + "- " + donator.getDonatorColorSecondary(ID) + "-{" + donator.getDonatorColorPrimary(ID) + "Noble " + sender.getName() + donator.getDonatorColorSecondary(ID) + "}-");
						}
					}
				} else
				{
					for (String message : this.commandhelp)
					{
						sender.sendMessage(message);
					}
				}
			}
		}
		return false;
	}
}
