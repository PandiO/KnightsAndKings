package Currency;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;
import Users.User;
import Users.Users;

public class GemCommands implements CommandExecutor
{
	private Main main;
	public GemCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of gem-commands",
			ColorOptions.stats + "-/gems set <amount> <username>",
			ColorOptions.stats + "-/gems add <amount> <username>",
			ColorOptions.stats + "-/gems remove <amount/all> <username>",
			ColorOptions.statsformat + "================================================="
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("gems"))
		{
			if (sender.hasPermission("k&k.gems"))
			{
				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("set"))
					{
						if (args.length == 3)
						{
							if (main.isInt(args[1]))
							{
								setGems(sender, args[2], Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else if (args.length == 2 && sender instanceof Player)
						{
							Player p = (Player) sender;
							if (main.isInt(args[1]))
							{
								setGems(sender, p.getName(), Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /gems set <amount> <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("add"))
					{
						if (args.length == 3)
						{
							if (main.isInt(args[1]))
							{
								addGems(sender, args[2], Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else if (args.length == 2 && sender instanceof Player)
						{
							Player p = (Player) sender;
							if (main.isInt(args[1]))
							{
								addGems(sender, p.getName(), Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /gems add <amount> <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del"))
					{
						if (args.length == 3)
						{
							if (args[1].equalsIgnoreCase("all"))
							{
								removeGems(sender, args[2], 0, true);
							} else
							{
								if (main.isInt(args[1]))
								{
									removeGems(sender, args[2], Integer.valueOf(args[1]), false);
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
								removeGems(sender, p.getName(), 0, true);
							} else
							{
								if (main.isInt(args[1]))
								{
									removeGems(sender, p.getName(), Integer.valueOf(args[1]), false);
								} else
								{
									sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /gems remove <amount/all> <username>");
						}
					} else
					{
						for (String message : staffcommandhelp)
						{
							sender.sendMessage(message);
						}
					}
				} else
				{
					for (String message : staffcommandhelp)
					{
						sender.sendMessage(message);
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		return false;
	}
	
	private void setGems(CommandSender sender, String targetUsername, Integer amount)
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
				
				target.setGems(amount);
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Set " + ColorOptions.gemStats + amount + " gems " + ColorOptions.currencycolor + "as your new balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Set " + ColorOptions.gemStats + amount + " gems " + ColorOptions.currencycolor + "as " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "new balance.");
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
	
	private void addGems(CommandSender sender, String targetUsername, Integer amount)
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
				target.addGems(amount);
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Added " + ColorOptions.gemStats + amount + " gems " + ColorOptions.currencycolor + "to your balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Added " + ColorOptions.gemStats + amount + " gems " + ColorOptions.currencycolor + "to " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "balance.");
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
	
	private void removeGems(CommandSender sender, String targetUsername, Integer amount, boolean all)
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
					target.setGems(0);
					if (sender.getName().equalsIgnoreCase(targetUsername))
					{
						sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.gemStats + "all gems " + ColorOptions.currencycolor + "from your balance.");
					} else
					{
						sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.gemStats + "all gems " + ColorOptions.currencycolor + "from " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "balance.");
					}
					return;
				} else
				{
					target.removeGems(amount);
				}
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.gemStats + amount + " gems " + ColorOptions.currencycolor + "from your balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.gemStats + amount + " gems " + ColorOptions.currencycolor + "from " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "balance.");
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
