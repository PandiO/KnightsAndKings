package UsefulCommands;

import org.bukkit.event.Listener;

import Main.Main;

public class StaffChatEvents implements Listener
{
	private Main main;
	public StaffChatEvents(Main main)
	{
		this.main = main;
	}
	
////	@EventHandler(priority = EventPriority.HIGHEST)
//	public void onCommand(PlayerCommandPreprocessEvent e)
//	{
//		if (main.debug)
//		{
//			Bukkit.getConsoleSender().sendMessage("Command: " + e.getMessage());
//		}
//		if (e.getMessage().equalsIgnoreCase("/sc"))
//		{
//			e.setMessage("/staffchat");
//		}
//	}
}
