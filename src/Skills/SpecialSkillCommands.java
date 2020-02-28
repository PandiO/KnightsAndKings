package Skills;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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

public class SpecialSkillCommands implements CommandExecutor
{	
	private Main main;
	SpecialSkill specialskill = new SpecialSkill();
	public SpecialSkillCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of special-skillpoint-commands",
			ColorOptions.stats + "-/specialskill points <set> <amount> <username>",
			ColorOptions.stats + "-/specialskill points <add> <amount> <username>",
			ColorOptions.stats + "-/specialskill points <remove> <amount> <username>",
			ColorOptions.stats + "-/specialskill set <name/id> <username>",
			ColorOptions.stats + "-/specialskill remove <username>",
			ColorOptions.stats + "-/specialskill list",
			ColorOptions.stats + "-/specialskill info <name/id>",
			ColorOptions.statsbrackets
	});
	
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("specialskill"))
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
				if (player.hasPermission("k&k.specialskill"))
				{
					if (args.length > 0)
					{
						if (args[0].equalsIgnoreCase("points") || args[0].equalsIgnoreCase("point"))
						{
							if (args.length > 1)
							{
								if (args[1].equalsIgnoreCase("set"))
								{
									if (args.length == 4)
									{
										if (main.isInt(args[2]))
										{
											Integer amount = Integer.valueOf(args[2]);
											this.setSpecialSkillPoints(user, amount, args[3]);
										} else
										{
											player.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage: /specialskill points set <amount> <username>");
									}
								} else
								if (args[1].equalsIgnoreCase("add"))
								{
									if (args.length == 4)
									{
										if (main.isInt(args[2]))
										{
											Integer amount = Integer.valueOf(args[2]);
											this.addSpecialSkillPoints(user, amount, args[3]);
										} else
										{
											player.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage: /specialskill points add <amount> <username>");
									}
								} else
								if (args[1].equalsIgnoreCase("remove"))
								{
									if (args.length == 4)
									{
										if (main.isInt(args[2]))
										{
											Integer amount = Integer.valueOf(args[2]);
											this.removeSpecialSkillPoints(user, amount, args[3], false);
										} else if (args[2].equalsIgnoreCase("all"))
										{
											this.removeSpecialSkillPoints(user, Integer.valueOf(args[2]), args[3], true);
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage: /specialskill points remove <amount/all> <username>");
									}
								} else
								{
									for (String message: staffcommandhelp)
									{
										player.sendMessage(message);
									}
								}
							} else
							{
								for (String message: staffcommandhelp)
								{
									player.sendMessage(message);
								}
							}
						} else
						if (args[0].equalsIgnoreCase("set"))
						{
							if (args.length == 3)
							{
								String specialskill = args[1];
								if (this.specialskill.getSpecialSkillID(specialskill) != null)
								{
									Integer specialskillID = this.specialskill.getSpecialSkillID(specialskill);
									String username = args[2];
									User userTarget = null;
									
									try
									{
										userTarget = Users.getUser(Users.fetchUUIDbyUsername(username));
									} catch (UserNotFoundException ex)
									{
										ErrorHandlers.userNotFoundAction(player, Bukkit.getPlayer(username), false);
										return false;
									} catch (Exception ex)
									{
										ex.printStackTrace();
										ErrorHandlers.userNotFoundAction(player, Bukkit.getPlayer(username), false);
										return false;
									}
									userTarget.setSpecialSkill(specialskillID);
									player.sendMessage(ColorOptions.messageachievement + "Succesfully set the special skill of " + ColorOptions.messagesubjects + username + ColorOptions.messageachievement + " to " + ColorOptions.messagesubjects + specialskill);
								} else
								{
									player.sendMessage(ColorOptions.error + "No special-skill could be found named " + specialskill);
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /specialskill set <specialskill> <username>");
							}
						} else
						if (args[0].equalsIgnoreCase("remove"))
						{
							if (args.length == 2)
							{
								String username = args[1];
								User userTarget = null;
								
								try
								{
									userTarget = Users.getUser(Users.fetchUUIDbyUsername(username));
								} catch (UserNotFoundException ex)
								{
									ErrorHandlers.userNotFoundAction(player, Bukkit.getPlayer(username), false);
									return false;
								} catch (Exception ex)
								{
									ex.printStackTrace();
									ErrorHandlers.userNotFoundAction(player, Bukkit.getPlayer(username), false);
									return false;
								}
								userTarget.setSpecialSkill(0);
								player.sendMessage(ColorOptions.messageachievement + "Succesfully removed the special skill of " + ColorOptions.messagesubjects + username);
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /specialskill remove <username>");
							}
						} else
						if (args[0].equalsIgnoreCase("list"))
						{
							this.listSpecialSkill(player);
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
					player.sendMessage(ColorOptions.error + "You don't have permission for this command");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command");
			}
		}
		return false;
	}
	
	public void setSpecialSkillPoints(User user, Integer amount, String targetUsername)
	{
		Player sender = user.getPlayer();
		User userTarget = null;
		
		try
		{
			userTarget = Users.getUser(Users.fetchUUIDbyUsername(targetUsername));
		} catch (Exception ex)

		{
			sender.sendMessage(ColorOptions.message + "User not online, searching for offline player..");
			try
			{
				userTarget = new User(Users.fetchUUIDbyUsername(targetUsername));
				userTarget.setOfflineUser(true);
			} catch (Exception e)
			{
				ErrorHandlers.userNotFoundAction(sender, null, false);
				return;
			}
		}
		try
		{
			userTarget.setSkillPoints(true, amount);
			if (sender.getName().equalsIgnoreCase(targetUsername))
			{
				sender.sendMessage(ColorOptions.currencycolor + "Set " + ColorOptions.coinStats + amount + " special-skillpoints " + ColorOptions.currencycolor + "as your balance.");
			} else
			{
				sender.sendMessage(ColorOptions.currencycolor + "Set " + ColorOptions.coinStats + amount + " special-skillpoints " + ColorOptions.currencycolor + "as " + ColorOptions.messagesubjects + userTarget.getUsername() + "'s " + ColorOptions.currencycolor + "balance.");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
		if (userTarget.isOfflineUser())
		{
			userTarget.destroy();
		}
	}
	
	public void addSpecialSkillPoints(User user, Integer amount, String targetUsername)
	{
		Player sender = user.getPlayer();
		User userTarget = null;
		
		try
		{
			userTarget = Users.getUser(Users.fetchUUIDbyUsername(targetUsername));
		} catch (Exception ex)

		{
			sender.sendMessage(ColorOptions.message + "User not online, searching for offline player..");
			try
			{
				userTarget = new User(Users.fetchUUIDbyUsername(targetUsername));
				userTarget.setOfflineUser(true);
			} catch (Exception e)
			{
				ErrorHandlers.userNotFoundAction(sender, null, false);
				return;
			}
		}
		try
		{
			userTarget.addSkillPoints(true, amount);
			if (sender.getName().equalsIgnoreCase(targetUsername))
			{
				sender.sendMessage(ColorOptions.currencycolor + "Added " + ColorOptions.coinStats + amount + " special-skillpoints " + ColorOptions.currencycolor + "to your balance.");
			} else
			{
				sender.sendMessage(ColorOptions.currencycolor + "Added " + ColorOptions.coinStats + amount + " special-skillpoints " + ColorOptions.currencycolor + "to " + ColorOptions.messagesubjects + userTarget.getUsername() + "'s " + ColorOptions.currencycolor + "balance.");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
		if (userTarget.isOfflineUser())
		{
			userTarget.destroy();
		}
	}
	
	public void removeSpecialSkillPoints(User user, Integer amount, String targetUsername, boolean all)
	{
		Player sender = user.getPlayer();
		User userTarget = null;
		
		try
		{
			userTarget = Users.getUser(Users.fetchUUIDbyUsername(targetUsername));
		} catch (Exception ex)

		{
			sender.sendMessage(ColorOptions.message + "User not online, searching for offline player..");
			try
			{
				userTarget = new User(Users.fetchUUIDbyUsername(targetUsername));
				userTarget.setOfflineUser(true);
			} catch (Exception e)
			{
				ErrorHandlers.userNotFoundAction(sender, null, false);
				return;
			}
		}
		try
		{
			if (all == true)
			{
				user.setSkillPoints(true, 0);
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + "all" + " special-skillpoints " + ColorOptions.currencycolor + "from your balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + "all" + " special-skillpoints " + ColorOptions.currencycolor + "from " + ColorOptions.messagesubjects + userTarget.getUsername() + "'s " + ColorOptions.currencycolor + "balance.");
				}
			} else
			{
				user.removeSkillPoints(true, amount);
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + amount + " special-skillpoints " + ColorOptions.currencycolor + "from your balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + amount + " special-skillpoints " + ColorOptions.currencycolor + "from " + ColorOptions.messagesubjects + userTarget.getUsername() + "'s " + ColorOptions.currencycolor + "balance.");
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
		if (userTarget.isOfflineUser())
		{
			userTarget.destroy();
		}
	}
	
	public void listSpecialSkill(Player sender)
	{
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of special-skills:");
		for (Integer specialskillID : specialskill.getSpecialSkillList())
		{
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			String skillName = specialskill.getName(specialskillID);
			String description = specialskill.getDescription(specialskillID);
			sender.sendMessage(ColorOptions.stats + "-Name: " + skillName);
			if (sender.isOp() || sender.hasPermission("k&k.town") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-ID: " + specialskillID);
			}
			sender.sendMessage(ColorOptions.stats + "-Description: " + description);
			
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
}
