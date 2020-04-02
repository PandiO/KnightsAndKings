package Arenas;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import API_methods.WorldGuard;
import DataManager.Worldguard;
import Main.Main;
import Menu.Menu;
import Users.User;

public class ArenaMenuClick
{
	WorldGuard worldguard = new WorldGuard();
	Menu menu = new Menu();
	private Main main;
	public ArenaMenuClick(Main main) 
	{
		this.main = main;
	}
	
	public void onArenaClick(InventoryClickEvent e, User user)
	{
		Player player = (Player) e.getWhoClicked();
		UUID uuid = player.getUniqueId();
		
		e.setCancelled(true);
		if (e.getCurrentItem().hasItemMeta())
		{
			String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
			if (name.equalsIgnoreCase("back"))
			{
				player.closeInventory();
			}
			if (name.equalsIgnoreCase("fight other players"))
			{
				this.menu.openDuelInviteMenu(user);
			}
		}
	}
	
	public void onDuelClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		if (e.getCurrentItem().hasItemMeta())
		{
			String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
			if (name.equalsIgnoreCase("back"))
			{
				this.menu.openArenaMenu(player, Worldguard.getStructureIDbyRegion(Worldguard.getRegion(player.getLocation(), "arena", Worldguard.getRegionManager(player.getLocation().getWorld()))));
			}
			if (name.contains("Name: "))
			{
				player.closeInventory();
				player.performCommand("duel " + name.split(": ")[1]);
			}
		}
	}
}
