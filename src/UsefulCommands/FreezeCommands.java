package UsefulCommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Handlers.ColorOptions;
import Main.Main;

public class FreezeCommands implements CommandExecutor
{
	private Main main;
	public FreezeCommands(Main main)
	{
		this.main = main;
	}
	
	//Shouldn't be able to walk, get damage, or run any commands. They should not be able to get teleported by fe a duel that ends.
	//They shouldn't be able to talk only to the member of staff that freezed the player, they also shouldn't receive any chats
	//When quitting in freeze-mode the player will be banned for 7 days
	
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of freeze-commands",
			ColorOptions.stats + "-/freeze <player> <reason>",
			ColorOptions.stats + "-/unfreeze <player>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		return false;
	}
}
