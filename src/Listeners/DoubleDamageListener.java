package Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import Handlers.DoubleDamage;
import Main.Main;

public class DoubleDamageListener implements Listener
{
	private Main main;
	public DoubleDamageListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void DoubleDamage(DoubleDamage e)
	{
		Player p = e.getPlayer();
		Player target = e.getTarget();
		Double damage = e.getDamage();
		if (e.getMultiplier() == false)
		{
			target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have been " + ChatColor.BOLD + "Double" + " hit!");
		} else
		{
			target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have been " + ChatColor.BOLD + "Triple" + " hit!");

		}
	}
}
