package UsefulCommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Handlers.ColorOptions;
import Main.Main;

public class PageCommand implements CommandExecutor
{
	@SuppressWarnings("unused")
	private Main main;	
	public PageCommand(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> pagemessage = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsresults + "" + ChatColor.BOLD + "► " + ColorOptions.messageachievement + "Information and updates are placed here!",
			ColorOptions.statsresults + "" + ChatColor.BOLD + "► " + ColorOptions.messageachievement + "https://www.planetminecraft.com/server/knights-and-kings-4045298/",
			ColorOptions.statsformat + "================================================="
	});
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (label.equalsIgnoreCase("page"))
		{
			for (String s : pagemessage)
			{
				sender.sendMessage(s);
			}
		}
		return false;
	}
	
}
