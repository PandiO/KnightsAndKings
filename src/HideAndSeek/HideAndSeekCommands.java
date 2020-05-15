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

import Donator.Donator;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Titles.Title;
import Users.User;
import Users.Users;

public class HideAndSeekCommands implements CommandExecutor
{
	Donator donator = new Donator();
	Title title = new Title();
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
					
					if (Main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					Main.HideAndSeek.enter(user);
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
					
					if (Main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					if (!Main.HideAndSeek.getParticipating(user))
					{
						sender.sendMessage(ColorOptions.error + "You are not participating in any Hide and Seek");
					}
					
					Main.HideAndSeek.leave(Main.HideAndSeek.getParticipant(user));
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
				}
			} else if (args[0].equalsIgnoreCase("skip"))
			{
				boolean allowedToSkip = false;
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
					
					if (Main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					if (user.getDonatorID() >= 1)
					{
						Main.HideAndSeek.skipStage(user);
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "Only players with donator title " + this.donator.getDonatorName(1) + " or higher can skip stages");
						player.sendMessage(ColorOptions.message + "Check 'personal menu > Current rank' or type /donator");
						return false;
					}
				} else
				{
					if (Main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					Main.HideAndSeek.skipStage(null);
					sender.sendMessage(ColorOptions.messageachievement + "Skipped the cooldown of Hide and Seek");
				}
			} else if (args[0].equalsIgnoreCase("info"))
			{
				if (Main.HideAndSeek == null)
				{
					sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
					return false;
				}
				
				List<String> information = new ArrayList<String>();
				
				information.addAll(Arrays.asList(
						"",
						ColorOptions.statsformat + "Information about the current game of Hide And Seek"
						));
				if (Main.HideAndSeek.getCooldown())
				{
					HashMap<String, Integer> time = Main.getCalculatedTime(Main.HideAndSeek.getCooldownSeconds());
					information.addAll(Arrays.asList(
							ColorOptions.stats + "Status: " + ColorOptions.statsresults + "Cooldown",
							ColorOptions.message + "Time untill next match: " + (time.get("minute") > 0 ? time.get("minute") + " minute(s) and " : " ") + time.get("second") + " second(s)",
							ColorOptions.message + "Join with /hs join"
							));
				}
				if (Main.HideAndSeek.getMatchmaking())
				{
					HashMap<String, Integer> time = Main.getCalculatedTime(Main.HideAndSeek.getMatchmakingSeconds());
					information.addAll(Arrays.asList(
							ColorOptions.stats + "Status: " + ColorOptions.statsresults + "Matchmaking open",
							ColorOptions.stats + "Participants: " + ColorOptions.statsresults + Main.HideAndSeek.getParticipants().size(),
							ColorOptions.stats + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Main.HideAndSeek.getReward()),
							ColorOptions.stats + "Location: " + ColorOptions.statsresults + Main.HideAndSeek.getTownName(),
							ColorOptions.stats + "Required title: " + ColorOptions.statsresults + this.title.getTitleName(Main.HideAndSeek.getEntryTitle(), 0) + "/" + this.title.getTitleName(Main.HideAndSeek.getEntryTitle(), 1),
							ColorOptions.stats + "Time to start: " + ColorOptions.statsresults + (time.get("minute") > 0 ? time.get("minute") + " minute(s) and " : " ") + time.get("second") + " second(s)",
							ColorOptions.message + "Join with /hs join"
							));
				}
				if (Main.HideAndSeek.getProgress())
				{
					HashMap<String, Integer> time = Main.getCalculatedTime(Main.HideAndSeek.getProgressSeconds());
					information.addAll(Arrays.asList(
							ColorOptions.stats + "Status: " + ColorOptions.statsresults + "In progress",
							ColorOptions.stats + "Participants: " + ColorOptions.statsresults + Main.HideAndSeek.getParticipants().size(),
							ColorOptions.stats + "Seekers: " + ColorOptions.statsresults + Main.HideAndSeek.getSeekers().GetMembers().size(),
							ColorOptions.stats + "Hiders: " + ColorOptions.statsresults + Main.HideAndSeek.getHiders().GetMembers().size(),
							ColorOptions.stats + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Main.HideAndSeek.getReward()),
							ColorOptions.stats + "Location: " + ColorOptions.statsresults + Main.HideAndSeek.getTownName(),
							ColorOptions.stats + "Required title: " + ColorOptions.statsresults + this.title.getTitleName(Main.HideAndSeek.getEntryTitle(), 0) + "/" + this.title.getTitleName(Main.HideAndSeek.getEntryTitle(), 1),
							ColorOptions.stats + "Time to end: " + ColorOptions.statsresults + (time.get("minute") > 0 ? time.get("minute") + " minute(s) and " : " ") + time.get("second") + " second(s)"
							));
				}
				information.add("");
				
				for (String msg : information)
				{
					sender.sendMessage(msg);
				}
			} else if (args[0].equalsIgnoreCase("stop"))
			{
				if (sender.hasPermission("k&k.hideandseek"))
				{
					if (Main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
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
					Main.HideAndSeek.forceStop(user);
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			} else if (args[0].equalsIgnoreCase("autostart"))
			{
				if (sender.hasPermission("k&k.hideandseek"))
				{
					if (Main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					if (Main.HideAndSeek.getAutoStart())
					{
						Main.HideAndSeek.setAutostart(false);
					} else
					{
						Main.HideAndSeek.setAutostart(true);
						if (Main.HideAndSeek.getCooldown() == false && Main.HideAndSeek.getMatchmaking() == false && Main.HideAndSeek.getProgress() == false)
						{
							Main.HideAndSeek.startMatchmaking();
						}
					}
					
					sender.sendMessage(ColorOptions.messageachievement + "Autostart of Hide and Seek toggled! Current status:" + (Main.HideAndSeek.getAutoStart() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			}
		}
		return false;
	}
}
