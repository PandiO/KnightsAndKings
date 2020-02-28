package UsefulCommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Handlers.ColorOptions;
import Main.Main;

public class DiscordCommand implements CommandExecutor
{
	private Main main;	
	public DiscordCommand(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> discordmessage = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsresults + "" + ChatColor.BOLD + "► " + ColorOptions.messageachievement + "Feel free to join our discord..",
			ColorOptions.statsresults + "" + ChatColor.BOLD + "► " + ColorOptions.messageachievement + "You can talk to other players or ask questions",
			ColorOptions.statsresults + "" + ChatColor.BOLD + "► " + ColorOptions.messageachievement + "Click here: https://discord.gg/dbAjFad",
			ColorOptions.statsformat + "================================================="
	});
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (label.equalsIgnoreCase("discord"))
		{
			for (String s : discordmessage)
			{
				sender.sendMessage(s);
			}
		}
		return false;
	}
}
