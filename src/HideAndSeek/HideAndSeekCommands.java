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
					
					if (main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					main.HideAndSeek.enter(user);
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
					
					if (main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					main.HideAndSeek.leave(user);
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
					
					if (main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					
					if (user.getDonatorID() >= 1)
					{
						main.HideAndSeek.skipStage(user);
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "Only players with donator title " + this.donator.getDonatorName(1) + " or higher can skip stages");
						player.sendMessage(ColorOptions.message + "Check 'personal menu > Current rank' or type /donator");
						return false;
					}
				} else
				{
					if (main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					main.HideAndSeek.skipStage(null);
					sender.sendMessage(ColorOptions.messageachievement + "Skipped the cooldown of Hide and Seek");
				}
			} else if (args[0].equalsIgnoreCase("info"))
			{
				if (main.HideAndSeek == null)
				{
					sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
					return false;
				}
				
				List<String> information = new ArrayList<String>();
				
				information.addAll(Arrays.asList(
						"",
						ColorOptions.statsformat + "Information about the current game of Hide And Seek"
						));
				if (main.HideAndSeek.getCooldown())
				{
					HashMap<String, Integer> time = main.getCalculatedTime(main.HideAndSeek.getCooldownSeconds());
					information.addAll(Arrays.asList(
							ColorOptions.stats + "Status: " + ColorOptions.statsresults + "Cooldown",
							ColorOptions.message + "Time untill next match: " + (time.get("minute") > 0 ? time.get("minute") + " minute(s) and " : " ") + time.get("second") + " second(s)",
							ColorOptions.message + "Join with /hs join"
							));
				}
				if (main.HideAndSeek.getMatchmaking())
				{
					HashMap<String, Integer> time = main.getCalculatedTime(main.HideAndSeek.getMatchmakingSeconds());
					information.addAll(Arrays.asList(
							ColorOptions.stats + "Status: " + ColorOptions.statsresults + "Matchmaking open",
							ColorOptions.stats + "Participants: " + ColorOptions.statsresults + main.HideAndSeek.getParticipants().size(),
							ColorOptions.stats + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(main.HideAndSeek.getReward()),
							ColorOptions.stats + "Location: " + ColorOptions.statsresults + main.HideAndSeek.getTownName(),
							ColorOptions.stats + "Required title: " + ColorOptions.statsresults + this.title.getTitleName(main.HideAndSeek.getEntryTitle(), 0) + "/" + this.title.getTitleName(main.HideAndSeek.getEntryTitle(), 1),
							ColorOptions.stats + "Time to start: " + ColorOptions.statsresults + (time.get("minute") > 0 ? time.get("minute") + " minute(s) and " : " ") + time.get("second") + " second(s)",
							ColorOptions.message + "Join with /hs join"
							));
				}
				if (main.HideAndSeek.getProgress())
				{
					HashMap<String, Integer> time = main.getCalculatedTime(main.HideAndSeek.getProgressSeconds());
					information.addAll(Arrays.asList(
							ColorOptions.stats + "Status: " + ColorOptions.statsresults + "In progress",
							ColorOptions.stats + "Participants: " + ColorOptions.statsresults + main.HideAndSeek.getParticipants().size(),
							ColorOptions.stats + "Seekers: " + ColorOptions.statsresults + main.HideAndSeek.getSeekers().size(),
							ColorOptions.stats + "Hiders: " + ColorOptions.statsresults + main.HideAndSeek.getHiderAmount(),
							ColorOptions.stats + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(main.HideAndSeek.getReward()),
							ColorOptions.stats + "Location: " + ColorOptions.statsresults + main.HideAndSeek.getTownName(),
							ColorOptions.stats + "Required title: " + ColorOptions.statsresults + this.title.getTitleName(main.HideAndSeek.getEntryTitle(), 0) + "/" + this.title.getTitleName(main.HideAndSeek.getEntryTitle(), 1),
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
					if (main.HideAndSeek == null)
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
					main.HideAndSeek.forceStop(user);
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			} else if (args[0].equalsIgnoreCase("autostart"))
			{
				if (sender.hasPermission("k&k.hideandseek"))
				{
					if (main.HideAndSeek == null)
					{
						sender.sendMessage(ColorOptions.error + "Hide and Seek is not available right now. Please try again later");
						return false;
					}
					if (main.HideAndSeek.getAutoStart())
					{
						main.HideAndSeek.setAutostart(false);
					} else
					{
						main.HideAndSeek.setAutostart(true);
						if (main.HideAndSeek.getCooldown() == false && main.HideAndSeek.getMatchmaking() == false && main.HideAndSeek.getProgress() == false)
						{
							main.HideAndSeek.startMatchmaking();
						}
					}
					
					sender.sendMessage(ColorOptions.messageachievement + "Autostart of Hide and Seek toggled! Current status:" + (main.HideAndSeek.getAutoStart() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			}
		}
		return false;
	}
}
