package UsefulCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import Main.Main;

public class RepairCommand implements CommandExecutor
{
	private Main main;
	public RepairCommand(Main main)
	{
		this.main = main;
	}
	
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("repair"))
		{
			if (sender.hasPermission("k&k.repair"))
			{
				
			}
		}
		return false;
	}
}
