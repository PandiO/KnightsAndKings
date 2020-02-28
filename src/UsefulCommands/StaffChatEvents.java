package UsefulCommands;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import Main.Main;

public class StaffChatEvents implements Listener
{
	private Main main;
	public StaffChatEvents(Main main)
	{
		this.main = main;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent e)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Command: " + e.getMessage());
		}
		if (e.getMessage().equalsIgnoreCase("/sc"))
		{
			e.setMessage("/staffchat");
		}
	}
}
