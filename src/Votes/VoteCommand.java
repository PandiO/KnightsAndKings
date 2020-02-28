package Votes;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Handlers.ColorOptions;
import Main.Main;

public class VoteCommand implements CommandExecutor
{
	private Main main;
	public VoteCommand(Main main)
	{
		this.main = main;
	}
	
	public static List<String> votemessage = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=====================================================",
			ColorOptions.statsformat + "Vote for us every day to get awesome bonuses!",
			ColorOptions.statsformat + "Type " + ColorOptions.statsresults + "/vote <rewards> <" + ColorOptions.defaultsubjects +"default" + ChatColor.GRAY + "/" + ColorOptions.noblesubjects + "noble" + ChatColor.GRAY + "/" + ColorOptions.royalsubjects + "royal" + ChatColor.GRAY + "/" + ColorOptions.dbsubjects + "dragonblood" + ColorOptions.statsresults + ">" + ColorOptions.statsformat + "to view all rewards!",
			ColorOptions.stats + "- Vote 1: " + ColorOptions.statsresults + "https://www.planetminecraft.com/server/knights-and-kings-4045298/vote/",
			ColorOptions.stats + "- Vote 2: " + ColorOptions.statsresults + "http://topg.org/Minecraft/in-479141",
			ColorOptions.statsformat + "====================================================="
	});
	public static List<String> defaultrewards = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=====================================================",
			ColorOptions.statsformat + "List of voting rewards for " + ColorOptions.defaultsubjects + "default" + ColorOptions.statsformat + " rank",
			ColorOptions.stats + "- Per vote: " + ColorOptions.statsresults + "",
			ColorOptions.stats + "- Coins: " + ColorOptions.statsresults + "Fixed amount of coins according to your title, minimum between 5000 and 20000",
			ColorOptions.stats + "- Gems: " + ColorOptions.statsresults + "Chance to get between 0 and 5 gems",
			ColorOptions.stats + "- Experience: " + ColorOptions.statsresults + "10% or 15% of the exp needed for the next rank",
			ColorOptions.statsformat + "Extra's:",
			ColorOptions.stats + "- 50% chance to receive between 0 and 16 gold nuggets and cooked mutton",
			ColorOptions.stats + "- 1% chance to receive an extra skillpoint!",
			ColorOptions.statsformat + "====================================================="
	});
	public static List<String> noblerewards = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=====================================================",
			ColorOptions.statsformat + "List of voting rewards for " + ColorOptions.noblesubjects + "noble" + ColorOptions.statsformat + " rank",
			ColorOptions.stats + "- Per vote: " + ColorOptions.statsresults + "",
			ColorOptions.stats + "- Coins: " + ColorOptions.statsresults + "Fixed amount of coins according to your title, minimum between 10000 and 20000",
			ColorOptions.stats + "- Gems: " + ColorOptions.statsresults + "Chance to get between 1 and 8 gems",
			ColorOptions.stats + "- Experience: " + ColorOptions.statsresults + "10% or 15% of the exp needed for the next rank",
			ColorOptions.statsformat + "Extra's:",
			ColorOptions.stats + "- 50% chance to receive between 0 and 16 gold nuggets and cooked mutton",
			ColorOptions.stats + "- 1% chance to receive an extra skillpoint!",
			ColorOptions.statsformat + "====================================================="
	});
	public static List<String> royalrewards = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=====================================================",
			ColorOptions.statsformat + "List of voting rewards for " + ColorOptions.royalsubjects + "royal" + ColorOptions.statsformat + " rank",
			ColorOptions.stats + "- Per vote: " + ColorOptions.statsresults + "",
			ColorOptions.stats + "- Coins: " + ColorOptions.statsresults + "Fixed amount of coins according to your title, minimum between 10000 and 30000",
			ColorOptions.stats + "- Gems: " + ColorOptions.statsresults + "Chance to get between 2 and 12 gems",
			ColorOptions.stats + "- Experience: " + ColorOptions.statsresults + "10% or 15% of the exp needed for the next rank",
			ColorOptions.statsformat + "Extra's:",
			ColorOptions.stats + "- 50% chance to receive between 0 and 16 gold nuggets and cooked mutton",
			ColorOptions.stats + "- 1% chance to receive an extra skillpoint!",
			ColorOptions.statsformat + "====================================================="
	});
	public static List<String> dragonbloodrewards = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=====================================================",
			ColorOptions.statsformat + "List of voting rewards for " + ColorOptions.dbsubjects + "dragon blood" + ColorOptions.statsformat + " rank",
			ColorOptions.stats + "- Per vote: " + ColorOptions.statsresults + "",
			ColorOptions.stats + "- Coins: " + ColorOptions.statsresults + "Fixed amount of coins according to your title, minimum between 20000 and 45000",
			ColorOptions.stats + "- Gems: " + ColorOptions.statsresults + "Chance to get between 5 and 20 gems",
			ColorOptions.stats + "- Experience: " + ColorOptions.statsresults + "10% or 15% of the exp needed for the next rank",
			ColorOptions.statsformat + "Extra's:",
			ColorOptions.stats + "- 50% chance to receive between 0 and 16 gold nuggets and cooked mutton",
			ColorOptions.stats + "- 1% chance to receive an extra skillpoint!",
			ColorOptions.statsformat + "====================================================="
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("vote"))
		{
			if (args.length >= 1)
			{
				if (args[0].equalsIgnoreCase("rewards"))
				{
					if (args.length != 1)
					{
						if (args[1].equalsIgnoreCase("default"))
						{
							for (String s : defaultrewards)
							{
								sender.sendMessage(s);
							}
						} else
						if (args[1].equalsIgnoreCase("noble"))
						{
							for (String s : noblerewards)
							{
								sender.sendMessage(s);
							}
						} else 
						if (args[1].equalsIgnoreCase("royal"))
						{
							for (String s : royalrewards)
							{
								sender.sendMessage(s);
							}
						} else
						if (args[1].equalsIgnoreCase("dragonblood") || args[1].equalsIgnoreCase("db"))
						{
							for (String s : dragonbloodrewards)
							{
								sender.sendMessage(s);
							}
						} else
						{
							for (String s : defaultrewards)
							{
								sender.sendMessage(s);
							}
						}
					} else
					{
						for (String s : defaultrewards)
						{
							sender.sendMessage(s);
						}
					}
				}
			} else
			{
				for (String s : votemessage)
				{
					sender.sendMessage(s);
				}
			}
		}
		return false;
	}
}
