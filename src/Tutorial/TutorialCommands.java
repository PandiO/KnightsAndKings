package Tutorial;

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

public class TutorialCommands implements CommandExecutor
{
	private Main main;
	public TutorialCommands(Main main)
	{
		this.main = main;
	}
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of tutorial-commands",
			ColorOptions.stats + "-/tutorial start <tutorial> <player>",
			ColorOptions.stats + "-/tutorial info <tutorial> <player>",
			ColorOptions.stats + "-/tutorial list <player>",
			ColorOptions.statsbrackets
	});
	
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of tutorial-commands",
			ColorOptions.stats + "-/tutorial start <tutorial>",
			ColorOptions.stats + "-/tutorial info <tutorial>",
			ColorOptions.stats + "-/tutorial list",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("tutorial"))
		{
			if (args.length >= 1)
			{
				String subCommand = args[0];
				if (subCommand.equalsIgnoreCase("start"))
				{
					if (args.length == 3 && sender.hasPermission("k&k.tutorial.others"))
					{
						String tutorialName = args[1];
						Player target = Bukkit.getPlayer(args[2]);
						UUID uuid = target.getUniqueId();
						User userTarget = null;
						
						try
						{
							userTarget = Users.getUser(uuid);
						} catch (UserNotFoundException ex)
						{
							ErrorHandlers.userNotFoundAction(((Player)sender), target, false);
							return false;
						} catch (Exception ex)
						{
							ex.printStackTrace();
							ErrorHandlers.userNotFoundAction(((Player)sender), target, false);
							return false;
						}
						if (target != null)
						{
							Tutorial tutorial = new Tutorial();
							if (tutorial.isValid(tutorialName))
							{
								tutorial.createTutorial(userTarget, tutorialName);
								sender.sendMessage(ColorOptions.messageachievement + "Started the " + ColorOptions.messagesubjects + tutorialName + " Tutorial " + ColorOptions.messageachievement + " for player " + ColorOptions.messagesubjects + target.getName());
							} else
							{
								sender.sendMessage(ColorOptions.error + "No tutorial could be found named " + tutorialName);
							}
						} else if (Users.existUser(args[2]))
						{
							sender.sendMessage(ColorOptions.error + "Player " + args[2] + " is not online");
						} else
						{
							sender.sendMessage(ColorOptions.error + "No player could be found named " + args[2]);
						}
					} else if (args.length == 2)
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
							String tutorialName = args[1];
							Tutorial tutorial = new Tutorial();
							if (tutorial.isValid(tutorialName))
							{
								tutorial.createTutorial(user, tutorialName);
							} else
							{
								sender.sendMessage(ColorOptions.error + "No tutorial could be found named " + tutorialName);
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "You have to be a player to perform this command!");
						}
					} else
					{
						if (sender.hasPermission("k&k.tutorial.others"))
						{
							sender.sendMessage(staffcommandhelp.get(2));
						} else
						{
							sender.sendMessage(commandhelp.get(2));
						}
					}
				} else if (subCommand.equalsIgnoreCase("info"))
				{
					if (args.length == 3 && sender.hasPermission("k&k.tutorial.others"))
					{
						String tutorialName = args[1];
						Player target = Bukkit.getPlayer(args[2]);
						if (target != null)
						{
							Tutorial tutorial = new Tutorial();
							if (tutorial.isValid(tutorialName))
							{
								target.sendMessage(ColorOptions.stats + "Information about the " + ColorOptions.statsresults + tutorialName + " Tutorial");
								target.sendMessage(ColorOptions.stats + "-" + ColorOptions.statsresults + tutorial.file.getDescription(tutorialName));
								sender.sendMessage(ColorOptions.messageachievement + "Showing information of the " + ColorOptions.messagesubjects + tutorialName + " Tutorial " + ColorOptions.messageachievement + " to player " + ColorOptions.messagesubjects + target.getName());
							} else
							{
								sender.sendMessage(ColorOptions.error + "No tutorial could be found named " + tutorialName);
							}
						} else if (Users.existUser(args[2]))
						{
							sender.sendMessage(ColorOptions.error + "Player " + args[2] + " is not online");
						} else
						{
							sender.sendMessage(ColorOptions.error + "No player could be found named " + args[2]);
						}
					} else if (args.length == 2)
					{
						if (sender instanceof Player)
						{
							String tutorialName = args[1];
							Tutorial tutorial = new Tutorial();
							if (tutorial.isValid(tutorialName))
							{
								sender.sendMessage(ColorOptions.stats + "Information about the " + ColorOptions.statsresults + tutorialName + " Tutorial");
								sender.sendMessage(ColorOptions.stats + "-" + ColorOptions.statsresults + tutorial.file.getDescription(tutorialName));
							} else
							{
								sender.sendMessage(ColorOptions.error + "No tutorial could be found named " + tutorialName);
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "You have to be a player to perform this command!");
						}
					} else
					{
						if (sender.hasPermission("k&k.tutorial.others"))
						{
							sender.sendMessage(staffcommandhelp.get(3));
						} else
						{
							sender.sendMessage(commandhelp.get(3));
						}
					}
				} else if (subCommand.equalsIgnoreCase("list"))
				{
					if (args.length == 2 && sender.hasPermission("k&k.tutorial.others"))
					{
						String tutorialName = args[1];
						Player target = Bukkit.getPlayer(args[2]);
						if (target != null)
						{
							showTutorialList(target);
							sender.sendMessage(ColorOptions.messageachievement + "Showing the list of " + ColorOptions.messagesubjects + "Tutorials " + ColorOptions.messageachievement + " to player " + ColorOptions.messagesubjects + target.getName());
						} else if (Users.existUser(args[2]))
						{
							sender.sendMessage(ColorOptions.error + "Player " + args[2] + " is not online");
						} else
						{
							sender.sendMessage(ColorOptions.error + "No player could be found named " + args[2]);
						}
					} else if (args.length == 1)
					{
						if (sender instanceof Player)
						{
							showTutorialList((Player) sender);
						} else
						{
							sender.sendMessage(ColorOptions.error + "You have to be a player to perform this command!");
						}
					} else
					{
						if (sender.hasPermission("k&k.tutorial.others"))
						{
							sender.sendMessage(staffcommandhelp.get(4));
						} else
						{
							sender.sendMessage(commandhelp.get(4));
						}
					}
				} else
				{
					if (sender.hasPermission("k&k.tutorial.others"))
					{
						for (String msg : staffcommandhelp)
						{
							sender.sendMessage(msg);
						}
					} else
					{
						for (String msg : commandhelp)
						{
							sender.sendMessage(msg);
						}
					}
				}
			} else
			{
				if (sender.hasPermission("k&k.tutorial.others"))
				{
					for (String msg : staffcommandhelp)
					{
						sender.sendMessage(msg);
					}
				} else
				{
					for (String msg : commandhelp)
					{
						sender.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}
	
	public void showTutorialList(Player sender)
	{
		Tutorial tutorial = new Tutorial();
		sender.sendMessage(ColorOptions.statsformat + ColorOptions.halfstatsbrackets);
		for (String tutorialName : tutorial.file.getTutorialNames())
		{
			String description = tutorial.file.getDescription(tutorialName);
			sender.sendMessage(ColorOptions.stats + "-Name: " + ColorOptions.statsresults + tutorialName + " Tutorial");
			if (description != null)
			{
				sender.sendMessage(ColorOptions.stats + "-Description: " + ColorOptions.statsresults + description);
			}
			sender.sendMessage("");
			if (tutorial.file.containsUUID(sender.getUniqueId(), tutorialName))
			{
				sender.sendMessage(ColorOptions.messageachievement + "Completed on " + tutorial.file.getCompleteDate(sender, tutorialName));
			} else
			{
				sender.sendMessage(ColorOptions.message + "Complete this tutorial to receive " + ColorOptions.gemStats + tutorial.file.getReward(tutorialName) + " Gems" + ColorOptions.message + " and " + ColorOptions.statsresults + tutorial.file.getExperienceReward(tutorialName) + " Experience");
			}
			sender.sendMessage(ColorOptions.statsformat + ColorOptions.halfstatsbrackets);
		}
	}
}
