package UsefulCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Handlers.WeatherChange;
import Main.Main;

public class WeatherChangeCommand implements CommandExecutor
{
	private Main main;
	public WeatherChangeCommand(Main main) 
	{
		this.main = main;
	}
	
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("weather"))
		{
			if (sender.hasPermission("k&k.weather"))
			{
				Player player = (Player) sender;
				if (args.length == 1)
				{
					if (args[0].equalsIgnoreCase("rain") || args[0].equalsIgnoreCase("storm") || args[0].equalsIgnoreCase("r"))
					{
						WeatherChange.Weatherchangeallow = true;
						Bukkit.getServer().getWorld(player.getWorld().getName()).setStorm(true);
						player.sendMessage(ColorOptions.messageachievement + "Changed weather type to rain!");
						WeatherChange.Weatherchangeallow = false;
					} else if (args[0].equalsIgnoreCase("sun") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase(""))
					{
						WeatherChange.Weatherchangeallow = true;
						Bukkit.getServer().getWorld(player.getWorld().getName()).setStorm(false);
						player.sendMessage(ColorOptions.messageachievement + "Changed weather type to sun!");
						WeatherChange.Weatherchangeallow = false;
					} else if (args[0].equalsIgnoreCase("allowchange"))
					{
						if (WeatherChange.Weatherchangeallow == true)
						{
							WeatherChange.Weatherchangeallow = false;
							player.sendMessage(ColorOptions.messageachievement + "Toggled weatherchange to " + ColorOptions.falsecommand + "false!");
						} else
						{
							WeatherChange.Weatherchangeallow = true;
							player.sendMessage(ColorOptions.messageachievement + "Toggled weatherchange to true!");
						}
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "Usage: /weather <rain/sun>");
					}
				} else
				{
					player.sendMessage(ColorOptions.falsecommand + "Usage: /weather <rain/sun>");
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
			}
		}
		return false;
		
	}
}
