package Users;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Donator.Donator;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;

public class UserCommands implements CommandExecutor
{
	offlineUser user = new offlineUser();
	Donator donator = new Donator();
	public Main main;
	public UserCommands(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of user-commands",
			ColorOptions.stats + "-/user remove <player/all>",
			ColorOptions.stats + "-/user default <player/all>",
			ColorOptions.stats + "-/user staff <player>",
			ColorOptions.stats + "-/user builder <player>",
			ColorOptions.stats + "-/user co-owner <player>",
			ColorOptions.stats + "-/user owner <player>",
			ColorOptions.stats + "-/user save <player/all>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("user"))
		{
			if (sender.hasPermission("k&k.ranks"))
			{
				if (args.length == 2)
				{
					String targetUsername = null;
					boolean all = false;
					if (Users.existUser(args[1]))
					{
						targetUsername = args[1];
					} else if (args[1].equalsIgnoreCase("all"))
					{
						all = true;
					} else
					{
						sender.sendMessage(ColorOptions.error + "No user could be found with this name " + args[1]);
						return false;
					}
					
					if (args[0].equalsIgnoreCase("remove"))
					{
						if (all)
						{
							for (Player target : Bukkit.getOnlinePlayers())
							{
								UUID targetUUID = target.getUniqueId();
								this.user.deleteUser(targetUUID);
								sender.sendMessage(ColorOptions.messageachievement + "Succesfully removed user " + ColorOptions.messagesubjects + args[1] + ColorOptions.messageachievement + " from the database!");
							}
						} else
						{
							UUID targetUUID = user.getUUID(targetUsername);
							this.user.deleteUser(targetUUID);
							sender.sendMessage(ColorOptions.messageachievement + "Succesfully removed user " + ColorOptions.messagesubjects + targetUsername + ColorOptions.messageachievement + " from the database!");
						}
					} else if (args[0].equalsIgnoreCase("default"))
					{
						if (all)
						{
							for (Player target : Bukkit.getOnlinePlayers())
							{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove co-owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove builder");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove staff");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group add default");
								target.setOp(false);
								sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of all players to " + ColorOptions.defaultsubjects + "Default");
							}
						} else
						{
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove owner");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove co-owner");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove builder");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove staff");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group add default");
							Bukkit.getOfflinePlayer(targetUsername).setOp(false);
							sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of player " + ColorOptions.defaultsubjects + targetUsername + ColorOptions.messageachievement + " to " + ColorOptions.defaultsubjects + "Default");
						}
						user.updateScoreBoard();
					} else if (args[0].equalsIgnoreCase("staff"))
					{
						if (all)
						{
							for (Player target : Bukkit.getOnlinePlayers())
							{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove co-owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove builder");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove staff");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group add staff");
								target.setOp(false);
								sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of all players to " + ColorOptions.staffsubjects + "Staff");
							}
						} else
						{
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove owner");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove co-owner");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove builder");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove staff");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group add staff");
							Bukkit.getOfflinePlayer(targetUsername).setOp(false);
							sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of player " + ColorOptions.staffsubjects + targetUsername + ColorOptions.messageachievement + " to " + ColorOptions.staffsubjects + "Staff");
						}
						user.updateScoreBoard();
					} else if (args[0].equalsIgnoreCase("builder"))
					{
						if (all)
						{
							for (Player target : Bukkit.getOnlinePlayers())
							{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove co-owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove builder");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove staff");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group add builder");
								target.setOp(false);
								sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of all players to " + ColorOptions.defaultsubjects + "Builder");
							}
						} else
						{
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove owner");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove co-owner");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove builder");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove staff");
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group add builder");
							Bukkit.getOfflinePlayer(targetUsername).setOp(false);
							sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of player " + ColorOptions.defaultsubjects + targetUsername + ColorOptions.messageachievement + " to " + ColorOptions.defaultsubjects + "Builder");
						}
						user.updateScoreBoard();
					} else if (args[0].equalsIgnoreCase("co-owner"))
					{
						if (sender.hasPermission("k&k.ranks.co-owner"))
						{
							if (all)
							{
								for (Player target : Bukkit.getOnlinePlayers())
								{
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove owner");
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove builder");
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove staff");
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove builder");
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group add co-owner");
									target.setOp(false);
									sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of all players to " + ColorOptions.ownersubjects + "Co-Owner");
								}
							} else
							{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove builder");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove staff");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove builder");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group add co-owner");
								Bukkit.getOfflinePlayer(targetUsername).setOp(false);
								sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of player " + ColorOptions.defaultsubjects + targetUsername + ColorOptions.messageachievement + " to " + ColorOptions.ownersubjects + "Co-Owner");
							}
							user.updateScoreBoard();
						} else
						{
							sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
						}
					} else if (args[0].equalsIgnoreCase("owner"))
					{
						if (sender.hasPermission("k&k.ranks.owner"))
						{
							if (all)
							{
								for (Player target : Bukkit.getOnlinePlayers())
								{
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove owner");
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove co-owner");
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove builder");
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group remove staff");
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " group add owner");
									target.setOp(false);
								}
								sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of all players to " + ColorOptions.ownersubjects + "Owner");
							} else
							{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove co-owner");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove builder");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group remove staff");
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + targetUsername + " group add owner");
								Bukkit.getOfflinePlayer(targetUsername).setOp(false);
								sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the rank of player " + ColorOptions.ownersubjects + targetUsername + ColorOptions.messageachievement + " to " + ColorOptions.ownersubjects + "Owner");
							}
							user.updateScoreBoard();
						} else
						{
							sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
						}
					} else if (args[0].equalsIgnoreCase("save"))
					{
						if (!sender.hasPermission("k&k.ranks.staff"))
						{
							sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
						}
						if (all)
						{
							User user = null;
							
							try
							{
								user = Users.getUser(((Player)sender).getUniqueId());
							} catch (UserNotFoundException ex)
							{
								ErrorHandlers.userNotFoundAction(null, ((Player) sender), true);
								return false;
							} catch (Exception ex)
							{
								ex.printStackTrace();
								ErrorHandlers.userNotFoundAction(null, ((Player)sender), true);
								return false;
							}
							main.saveUsers(user, false);
						} else
						{
							User user = null;
							
							try
							{
								user = Users.getUser(Bukkit.getPlayer(targetUsername).getUniqueId());
							} catch (UserNotFoundException ex)
							{
								ErrorHandlers.userNotFoundAction(((Player) sender), Bukkit.getPlayer(targetUsername), false);
								return false;
							} catch (Exception ex)
							{
								ex.printStackTrace();
								ErrorHandlers.userNotFoundAction(((Player) sender), Bukkit.getPlayer(targetUsername), false);
								return false;
							}
							try
							{
								user.saveUser();
								sender.sendMessage(ColorOptions.messageachievement + "Succesfully saved the playerdata of " + targetUsername);
							} catch (Exception e)
							{
								e.printStackTrace();
								sender.sendMessage(ColorOptions.error + "Failed to save the playerdata of " + targetUsername + "! Check the console for more information");
							}
						}
					} else
					{
						sender.sendMessage(staffcommandhelp.get(2));
					}
				} else
				{
					for (String msg : staffcommandhelp)
					{
						sender.sendMessage(msg);
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		return false;
	}
	
}
