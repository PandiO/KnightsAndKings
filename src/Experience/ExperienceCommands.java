package Experience;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;
import Users.User;
import Users.Users;

public class ExperienceCommands implements CommandExecutor
{
	public Main main;
	public ExperienceCommands(Main main) 
	{
		this.main = main;
	}
	
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("experience") || label.equalsIgnoreCase("exp"))
		{
			if (sender.hasPermission("k&k.experience"))
			{
				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("set"))
					{
						if (args.length == 3)
						{
							if (main.isInt(args[1]))
							{
								setExperience(sender, args[2], Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else if (args.length == 2 && sender instanceof Player)
						{
							Player p = (Player) sender;
							if (main.isInt(args[1]))
							{
								setExperience(sender, p.getName(), Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /experience set <amount> <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("add"))
					{
						if (args.length == 3)
						{
							if (main.isInt(args[1]))
							{
								addExperience(sender, args[2], Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else if (args.length == 2 && sender instanceof Player)
						{
							Player p = (Player) sender;
							if (main.isInt(args[1]))
							{
								addExperience(sender, p.getName(), Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /experience add <amount> <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del"))
					{
						if (args.length == 3)
						{
							if (args[1].equalsIgnoreCase("all"))
							{
								removeExperience(sender, args[2], 0, true);
							} else
							{
								if (main.isInt(args[1]))
								{
									removeExperience(sender, args[2], Integer.valueOf(args[1]), false);
								} else
								{
									sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
								}
							}
						} else if (args.length == 2 && sender instanceof Player)
						{
							Player p = (Player) sender;
							if (args[1].equalsIgnoreCase("all"))
							{
								removeExperience(sender, p.getName(), 0, true);
							} else
							{
								if (main.isInt(args[1]))
								{
									removeExperience(sender, p.getName(), Integer.valueOf(args[1]), false);
								} else
								{
									sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /experience remove <amount/all> <username>");
						}
					} else
					if (Users.existUser(args[0]))
					{
						UUID targetuuid = Users.fetchUUIDbyUsername(args[0]);
						User target = null;
						if (Users.getUser(targetuuid) != null)
						{
							target = Users.getUser(targetuuid);
						} else
						{
							target = new User(targetuuid);
						}
						sender.sendMessage(ColorOptions.names + args[0] + ColorOptions.currencycolor + " has " + ColorOptions.coinStats + target.getExperience() + " Experience");
						target.destroy();
					}
				} else
				{
					if (sender instanceof Player)
					{
						UUID uuid = Users.fetchUUIDbyUsername(sender.getName());
						User user = Users.getUser(uuid);
						sender.sendMessage(ColorOptions.currencycolor + "You have " + ColorOptions.coinStats + user.getExperience() + " Experience");
					}
				}
			}
		}
		
		return false;
	}
	
	private void setExperience(CommandSender sender, String targetUsername, Integer amount)
	{
		try
		{
			if (Users.existUser(targetUsername) == true)
			{
				User target = null;
				UUID uuid = Users.fetchUUIDbyUsername(targetUsername);
				if (Users.getUser(uuid) != null)
				{
					target = Users.getUser(uuid);
				} else
				{
					target = new User(uuid);
				}
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.messageformat + "The experience of " + ColorOptions.messagesubjects + targetUsername + ColorOptions.messageformat + " has been set to " + ColorOptions.messagesubjects + amount);
				} else
				{
					sender.sendMessage(ColorOptions.messageformat + "The experience of " + ColorOptions.messagesubjects + targetUsername + ColorOptions.messageformat + " has been set to " + ColorOptions.messagesubjects + amount);
				}
				target.setExperience(amount, true);
				target.destroy();
			} else
			{
				sender.sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
	private void addExperience(CommandSender sender, String targetUsername, Integer amount)
	{
		try
		{
			if (Users.existUser(targetUsername) == true)
			{
				User target = null;
				UUID uuid = Users.fetchUUIDbyUsername(targetUsername);
				if (Users.getUser(uuid) != null)
				{
					target = Users.getUser(uuid);
				} else
				{
					target = new User(uuid);
				}
				target.addExperience(amount, true);
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.messageformat + "Added " + ColorOptions.messagesubjects + amount + ColorOptions.messageformat + " experience to your balance");
				} else
				{
					sender.sendMessage(ColorOptions.messageformat + "Added " + ColorOptions.messagesubjects + amount + ColorOptions.messageformat + " experience to " + targetUsername);
				}
				target.destroy();
			} else
			{
				sender.sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
	
	private void removeExperience(CommandSender sender, String targetUsername, Integer amount, boolean all)
	{
		try
		{
			if (Users.existUser(targetUsername) == true)
			{
				User target = null;
				UUID uuid = Users.fetchUUIDbyUsername(targetUsername);
				if (Users.getUser(uuid) != null)
				{
					target = Users.getUser(uuid);
				} else
				{
					target = new User(uuid);
				}
				if (all == true)
				{
					target.setExperience(0, true);
					if (sender.getName().equalsIgnoreCase(targetUsername))
					{
						sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + "all experience " + ColorOptions.currencycolor + "from your balance.");
					} else
					{
						sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + "all experience " + ColorOptions.currencycolor + "from " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "balance.");
					}
					return;
				} else
				{
					target.removeExperience(amount, true);
				}
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + amount + " experience " + ColorOptions.currencycolor + "from your balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + amount + " experience " + ColorOptions.currencycolor + "from " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "balance.");
				}
				target.destroy();
			} else
			{
				sender.sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
}
