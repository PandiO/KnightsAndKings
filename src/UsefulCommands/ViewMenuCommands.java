package UsefulCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Main.Main;

public class ViewMenuCommands implements CommandExecutor
{
	private Main main;
	public ViewMenuCommands(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		
		return false;
	}
}
