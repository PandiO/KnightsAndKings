package KillsDeaths;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class KillCommands implements CommandExecutor
{
	private Main main;
	public KillCommands(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("kills"))
		{
			Player p = (Player) sender;
			UUID uuid = p.getUniqueId();
			if (p.hasPermission("k&k.kills"))
			{
				if (args.length == 3)
				{
					if (args[0].equalsIgnoreCase("add"))
					{
						if (main.isInt(args[2]))
						{
							Integer amount = Integer.valueOf(args[2]);
							User user = null;
							
							try
							{
								user = Users.getUser(Users.fetchUUIDbyUsername(args[1]));
							} catch (UserNotFoundException ex)
							{
								ErrorHandlers.userNotFoundAction(p, null, false);
								return false;
							} catch (Exception ex)
							{
								ex.printStackTrace();
								ErrorHandlers.userNotFoundAction(p, null, false);
								return false;
							}
							UUID tu = user.getUUID();
							user.addKills(false, amount, amount);
							p.sendMessage(ChatColor.GREEN + "Added " + ChatColor.YELLOW + args[2] + ChatColor.GREEN + " Kills to " + ChatColor.YELLOW + args[1] + "'s " + ChatColor.GREEN + "Statistics");
							Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + "Added " + ChatColor.YELLOW + args[2] + ChatColor.GREEN + " Kills to your Statistics");
						} else
						{
							p.sendMessage(ColorOptions.error + "Kills needs to be a number");
						}
					} else
					if (args[0].equalsIgnoreCase("remove"))
					{
						if (main.isInt(args[2]))
						{
							Integer amount = Integer.valueOf(args[2]);
							User user = null;
							
							try
							{
								user = Users.getUser(Users.fetchUUIDbyUsername(args[1]));
							} catch (UserNotFoundException ex)
							{
								ErrorHandlers.userNotFoundAction(p, null, false);
								return false;
							} catch (Exception ex)
							{
								ex.printStackTrace();
								ErrorHandlers.userNotFoundAction(p, null, false);
								return false;
							}
							UUID tu = user.getUUID();
							user.removeKills(false, amount, amount);
							p.sendMessage(ChatColor.RED + "Removed " + ChatColor.YELLOW + args[2] + ChatColor.GREEN + " Kills to " + ChatColor.YELLOW + args[1] + "'s " + ChatColor.GREEN + "Statistics");
						} else
						{
							p.sendMessage(ColorOptions.error + "Kills needs to be a number");
						}
					} else
					{
						p.sendMessage(ChatColor.BLUE + "Usage: /kills <add/remove> <name> <amount>");
					}
				} else if (args.length == 2)
				{
					User user = null;
					
					try
					{
						user = Users.getUser(uuid);
					} catch (UserNotFoundException ex)
					{
						ErrorHandlers.userNotFoundAction(null, p, true);
						return false;
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, p, true);
						return false;
					}
					Integer amount = Integer.valueOf(args[1]);
					if (args[0].equalsIgnoreCase("add"))
					{
						user.addKills(false, amount, amount);
						p.sendMessage(ChatColor.GREEN + "Added " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + " Kills to your statistics");
					} else
					if (args[0].equalsIgnoreCase("remove"))
					{
						user.removeKills(false, amount, amount);
						p.sendMessage(ChatColor.RED + "Removed " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + " Kills to your statistics");
					}
					
				} else
				{
					p.sendMessage(ChatColor.BLUE + "Usage: /kills <add/remove> <name> <amount>");
				}
			} else
			{
				p.sendMessage(ChatColor.RED + "You don't have permission for this command!");
			}
		}
		return false;
		
	}
}