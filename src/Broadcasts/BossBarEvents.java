package Broadcasts;

import org.bukkit.event.Listener;

import Main.Main;

public class BossBarEvents implements Listener
{
	BossBar bar = new BossBar();
	private Main main;
	public BossBarEvents(Main main)
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void onJoin(PlayerJoinEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//    	if (!bar.barList.containsKey(uuid))
//    	{
//    		bar.newBar(player);
//    	}
//	}
//	
//	@EventHandler
//	public void onLeave(PlayerQuitEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//    	if (bar.barList.containsKey(uuid))
//    	{
//    		bar.barList.remove(uuid);
//    	}
//	}
//	
//	@EventHandler
//	public void onMove(PlayerMoveEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		Location from = e.getFrom();
//		Location to = e.getTo();
//		if (bar.barList.containsKey(uuid))
//		{
//			Bukkit.getConsoleSender().sendMessage("Containing!");
//			if (from.distance(to) > 0)
//			{
//				Bukkit.getConsoleSender().sendMessage("Location change!");
//				bar.updateBarLocation(player);
//			}
//		} else
//		{
//			Bukkit.getConsoleSender().sendMessage("New bar!");
//			bar.newBar(player);
//		}
//	}
}
