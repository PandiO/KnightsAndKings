package UsefulCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;

public class HealCommands implements CommandExecutor
{
	private Main main;
	public HealCommands(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("heal"))
		{
			if (sender.hasPermission("k&k.heal"))
			{
				if (sender instanceof Player)
				{
					Player player = (Player) sender;
					player.setHealth(player.getMaxHealth());
					player.setFoodLevel(20);
					player.sendMessage(ColorOptions.messageachievement + "Healed yourself!");
				} else
				{
					sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		return false;
	}
}
