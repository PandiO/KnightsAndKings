package HideAndSeek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import DataManager.HideandSeeks;
import Donator.Donator;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Menu.Menu;
import Titles.Title;
import Users.User;
import Users.Users;

public class HideAndSeekCommands implements CommandExecutor
{
	Donator donator = new Donator();
	Title title = new Title();
	Menu menu = new Menu();
	private Main main;
	public HideAndSeekCommands(Main main)
	{
		this.main = main;
	}
	
	public static List<String> commandhelp = new ArrayList<String>(Arrays.asList(
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of HideAndSeek-commands",
			ColorOptions.stats + "-/hs join (join a game of Hide And Seek)",
			ColorOptions.stats + "-/hs leave (leave a game of Hide And Seek)",
			ColorOptions.stats + "-/hs skip (skip the cooldown)",
			ColorOptions.stats + "-/hs info (information about the current game)"
			));
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("hideandseek") || label.equalsIgnoreCase("hs"))
		{
			if (args.length <= 0)
			{
				List<String> message = commandhelp;
				if (sender.hasPermission("k&k.hideandseek"))
				{
					message.addAll(Arrays.asList(
							ColorOptions.stats + "-/hs stop (stops the current game)",
							ColorOptions.stats + "-/hs autostart (GateToggles autostart on/off)"
							));
				}
				message.add(ColorOptions.statsbrackets);
				for (String msg : message)
				{
					sender.sendMessage(msg);
				}
				return false;
			}
			if (args[0].equalsIgnoreCase("join"))
			{
				if (sender instanceof Player)
				{
					Player player = (Player)sender;
					UUID uuid = player.getUniqueId();
					User user = null;
					
					try
					{
						user = Users.getUser(uuid);
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					}
					
					if (HideandSeeks.HideAndSeeks.isEmpty())
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					for (HideAndSeek hs : HideandSeeks.HideAndSeeks)
					{
						if (hs.getMatchmaking())
						{
							hs.enter(user);
						}
					}
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
				}
			} else if (args[0].equalsIgnoreCase("leave"))
			{
				if (sender instanceof Player)
				{
					Player player = (Player)sender;
					UUID uuid = player.getUniqueId();
					User user = null;
					
					try
					{
						user = Users.getUser(uuid);
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					}
					
					if (HideandSeeks.HideAndSeeks.isEmpty())
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					HideAndSeek hs = HideandSeeks.findHideAndSeek(user);
					
					if (hs == null)
					{
						sender.sendMessage(ColorOptions.error + "You are not participating in any Hide and Seek");
					}
					
					hs.leave(hs.getParticipant(user));
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
				}
			} else if (args[0].equalsIgnoreCase("skip"))
			{
				if (sender instanceof Player)
				{
					Player player = (Player)sender;
					UUID uuid = player.getUniqueId();
					User user = null;
					HideAndSeek hs = null;
					
					try
					{
						user = Users.getUser(uuid);
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					}
					
					if (HideandSeeks.HideAndSeeks.isEmpty())
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					if (user.getDonatorID() < 1)
					{
						player.sendMessage(ColorOptions.falsecommand + "Only players with donator title " + this.donator.getDonatorName(1) + " or higher can skip stages");
						player.sendMessage(ColorOptions.message + "Check 'personal menu > Current rank' or type /donator");
						return false;
					}
					
					hs = HideandSeeks.findHideAndSeek(user);
					if (hs != null)
					{
						hs.skipStage(user);
					} else if (user.inStaffModus() || user.inOwnerModus())
					{
						Integer ID = null;
						if (args.length != 2)
						{
							sender.sendMessage(ColorOptions.falsecommand + "Usage: /hs skip <id>");
							return false;
						}
						
						String stringID = args[1];
						if (!Main.isInt(stringID))
						{
							sender.sendMessage(ColorOptions.error + "Hide and Seek-ID must be a number: " + stringID);
							return false;
						}
						
						ID = Integer.valueOf(stringID);
						hs = HideandSeeks.findHideAndSeek(ID);
						
						if (hs == null)
						{
							sender.sendMessage(ColorOptions.error + "Couldn't find a Hide and Seek with ID " + ID);
							return false;
						}
						
						hs.skipStage(user);
					} else
					{
						sender.sendMessage(ColorOptions.error + "You must have entered a Hide and Seek before you can skip a stage!");
						return false;
					}
				} else
				{
					HideAndSeek hs = null;
					Integer ID = null;
					if (args.length != 2)
					{
						sender.sendMessage(ColorOptions.falsecommand + "Usage: /hs skip <id>");
						return false;
					}
					
					String stringID = args[1];
					if (!Main.isInt(stringID))
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek-ID must be a number: " + stringID);
						return false;
					}
					
					if (HideandSeeks.HideAndSeeks.isEmpty())
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later.");
						return false;
					}
					
					ID = Integer.valueOf(stringID);
					hs = HideandSeeks.findHideAndSeek(ID);
					
					if (hs == null)
					{
						sender.sendMessage(ColorOptions.error + "Couldn't find a Hide and Seek with ID " + ID);
						return false;
					}
					
					hs.skipStage(null);
					sender.sendMessage(ColorOptions.messageachievement + "Skipped the cooldown of Hide and Seek");
				}
			} else if (args[0].equalsIgnoreCase("info"))
			{
				if (HideandSeeks.HideAndSeeks.isEmpty())
				{
					sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
					return false;
				}
				
				if (!(sender instanceof Player))
				{
					sender.sendMessage(ColorOptions.error + "You can only run this command as a player!");
					return false;
				} else
				{
					Player player = (Player)sender;
					UUID uuid = player.getUniqueId();
					User user = null;
					HideAndSeek hs = null;
					
					try
					{
						user = Users.getUser(uuid);
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					}
					
					sender.sendMessage(ColorOptions.messageachievement + "Showing information of all Hide and Seeks..");
					this.menu.openHideAndSeekOverview(user);
				}
			} else if (args[0].equalsIgnoreCase("stop"))
			{
				if (sender.hasPermission("k&k.hideandseek"))
				{
					if (HideandSeeks.HideAndSeeks.isEmpty())
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					HideAndSeek hs = null;
					Integer ID = null;
					if (args.length != 2)
					{
						sender.sendMessage(ColorOptions.falsecommand + "Usage: /hs stop <id>");
						return false;
					}
					
					String stringID = args[1];
					if (!Main.isInt(stringID))
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek-ID must be a number: " + stringID);
						return false;
					}
					
					ID = Integer.valueOf(stringID);
					hs = HideandSeeks.findHideAndSeek(ID);
					
					if (hs == null)
					{
						sender.sendMessage(ColorOptions.error + "Couldn't find a Hide and Seek with ID " + ID);
						return false;
					}
					
					User user = null;
					if (sender instanceof Player)
					{
						Player player = (Player)sender;
						UUID uuid = player.getUniqueId();
						
						try
						{
							user = Users.getUser(uuid);
						} catch (Exception ex)
						{
							ex.printStackTrace();
							ErrorHandlers.userNotFoundAction(null, player, true);
							return false;
						}
					}
					hs.forceStop(user);
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			} else if (args[0].equalsIgnoreCase("autostart"))
			{
				if (sender.hasPermission("k&k.hideandseek"))
				{
					if (HideandSeeks.HideAndSeeks.isEmpty())
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					HideAndSeek hs = null;
					Integer ID = null;
					if (args.length != 2)
					{
						sender.sendMessage(ColorOptions.falsecommand + "Usage: /hs skip <id>");
						return false;
					}
					
					String stringID = args[1];
					if (!Main.isInt(stringID))
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek-ID must be a number: " + stringID);
						return false;
					}
					
					ID = Integer.valueOf(stringID);
					hs = HideandSeeks.findHideAndSeek(ID);
					
					if (hs == null)
					{
						sender.sendMessage(ColorOptions.error + "Couldn't find a Hide and Seek with ID " + ID);
						return false;
					}
					
					if (hs.getAutoStart())
					{
						hs.setAutostart(false);
					} else
					{
						hs.setAutostart(true);
						if (hs.getCooldown() == false && hs.getMatchmaking() == false && hs.getProgress() == false)
						{
							hs.startMatchmaking();
						}
					}
					
					sender.sendMessage(ColorOptions.messageachievement + "Autostart of Hide and Seek toggled! Current status:" + (hs.getAutoStart() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			}
		}
		return false;
	}
}
