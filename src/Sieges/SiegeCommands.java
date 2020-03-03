package Sieges;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import API_methods.WorldGuard;
import Donator.Donator;
import Exceptions.CommandExceptions;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class SiegeCommands implements CommandExecutor
{
	private Main main;
	private WorldGuard worldguard = new WorldGuard();
	Donator donator = new Donator();
	public SiegeCommands(Main main)
	{
		this.main = main;
	}
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			"",
			ColorOptions.statsformat + "List of Siege-commands",
			ColorOptions.stats + "-/Siege join",
			ColorOptions.stats + "-/Siege leave",
			ColorOptions.stats + "-/Siege vote",
			ColorOptions.stats + "-/Siege skip",
			ColorOptions.stats + "-/Siege info",
			ColorOptions.stats + "-/Siege list",
			""
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("siege"))
		{
			if (args.length <= 0)
			{
				for (String msg : staffcommandhelp)
				{
					sender.sendMessage(msg);
				}
				return false;
			}
			
			if (args[0].equalsIgnoreCase("join"))
			{
				sender.sendMessage(ColorOptions.error + "Command not configured yet.");
			} else if (args[0].equalsIgnoreCase("leave"))
			{
				Player player = null;
				
				if (!(sender instanceof Player))
				{
					sender.sendMessage(CommandExceptions.SenderNotPlayer);
					return false;
				}
				player = (Player) sender;
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
				
				Siege siege = Sieges.findSiege(user);
				if (siege == null)
				{
					user.sendMessage(Arrays.asList(ColorOptions.error + "You are not participating in any sieges"));
					return false;
				}
				
				siege.leavePlayer(user);
				user.sendMessage(ColorOptions.messageachievement + "You left a siege");
			} else if (args[0].equalsIgnoreCase("skip"))
			{
				boolean allowedToSkip = false;
				
				if (args.length < 2)
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /siege skip <siegeNumber>");
					return false;
				}
				User user = null;
				Player player = null;
				String siegeArg = args[1];
				
				if (sender instanceof Player)
				{
					player = (Player)sender;
					UUID uuid = player.getUniqueId();
					user = null;
					
					try
					{
						user = Users.getUser(uuid);
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					}
					
					if (user.getDonatorID() >= 1)
					{
						allowedToSkip = true;
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "Only players with donator title " + this.donator.getDonatorName(1) + " or higher can skip stages");
						player.sendMessage(ColorOptions.message + "Check 'personal menu > Current rank' or type /donator");
						return false;
					}
				} else
				{
					allowedToSkip = true;
				}
				
				if (allowedToSkip)
				{
					if (main.isInt(siegeArg))
					{
						Integer siegeIndex = Integer.valueOf(siegeArg)-1;
						
						Siege siege = Sieges.Sieges.get(siegeIndex);
						
						if (siege == null)
						{
							sender.sendMessage(ColorOptions.error + "No siege found with number " + siegeIndex);
							return false;
						}
						
						sender.sendMessage(ColorOptions.messageachievement + "Skipped the current stage of siege " + siegeArg);
						siege.skipStage(null);
					} else
					{
						sender.sendMessage(ColorOptions.error + "Siegenumber must be a number: /siege skip <siegeNumber>");
					}
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "Only players with donator title " + this.donator.getDonatorName(1) + " or higher can skip stages");
					sender.sendMessage(ColorOptions.message + "Check 'personal menu > Current rank' or type /donator");
					return false;
				}
				
//				if (sender instanceof Player)
//				{
//					Player player = (Player)sender;
//					UUID uuid = player.getUniqueId();
//					User user = null;
//					
//					try
//					{
//						user = Users.getUser(uuid);
//					} catch (Exception ex)
//					{
//						ex.printStackTrace();
//						ErrorHandlers.userNotFoundAction(null, player, true);
//						return false;
//					}
//					
//					
//					
//					if (main.HideAndSeek == null)
//					{
//						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
//						return false;
//					}
//					
//					if (user.getDonatorID() >= 1)
//					{
//						main.HideAndSeek.skipStage(user);
//					} else
//					{
//						player.sendMessage(ColorOptions.falsecommand + "Only players with donator title " + this.donator.getDonatorName(1) + " or higher can skip the cooldown");
//						player.sendMessage(ColorOptions.message + "Check 'personal menu > Current rank' or type /donator");
//						return false;
//					}
//				} else
//				{
//					if (main.HideAndSeek == null)
//					{
//						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
//						return false;
//					}
//					main.HideAndSeek.skipStage(null);
//					sender.sendMessage(ColorOptions.messageachievement + "Skipped the cooldown of Hide and Seek");
//				}
			}
		}
		return false;
	}
}
