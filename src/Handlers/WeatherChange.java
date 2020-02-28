package Handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import Main.Main;

public class WeatherChange implements Listener
{
	private Main main;
	public WeatherChange(Main main) 
	{
		this.main = main;
	}
	
	public static boolean Weatherchangeallow = false;
	
	@EventHandler
	public void weatherChange(WeatherChangeEvent e)
	{
		if (Weatherchangeallow == false)
		{
			e.setCancelled(true);
			Bukkit.getServer().broadcastMessage(ColorOptions.messageformat + "===========================");
			Bukkit.getServer().broadcastMessage(ColorOptions.messageformat + "-Prevented weather change");
			Bukkit.getServer().broadcastMessage(ColorOptions.messageformat + "===========================");
			for (Player p: Bukkit.getOnlinePlayers())
			{
				p.playSound(p.getLocation(), SoundHandler.LIGHTNING_THUNDER, 1.0F, 1.0F);
			}
		}
	}
}
