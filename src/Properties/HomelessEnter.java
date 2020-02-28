package Properties;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.event.Listener;

import API_methods.WorldGuard;
import Main.Main;
import Menu.Menu;
import Regions.Region;

public class HomelessEnter implements Listener
{
	Menu menu = new Menu();
	Property property = new Property();
	Region region = new Region();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public HomelessEnter(Main main) 
	{
		this.main = main;
	}
	
	public static ArrayList<UUID> homelessList = new ArrayList<UUID>();
	
//	@EventHandler
//	public void onEnter(PlayerMoveEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		RegionManager regionmanager = worldguard.getWorldGuard().getRegionManager(player.getWorld());
//		Integer propertyID = worldguard.getStructureIDbyRegion("property", player.getLocation(), regionmanager);
//		if (propertyID != null)
//		{
//			if (user.getHouseAmount(uuid) == 0 && user.getRoomAmount(uuid) == 0)
//			{
//				if (!main.ownermodus.containsKey(uuid))
//				{
//					if (player.getGameMode() != GameMode.CREATIVE)
//					{
//						if (!homelessList.contains(uuid))
//						{
//							homelessList.add(uuid);
//							player.sendMessage(ColorOptions.messageformat + "Shopowner: " + ColorOptions.error + "Get out you filthy maggot! I don't serve homeless people!");
//							player.damage(2);
//						}
//					}
//				}
//			}
//		} else
//		{
//			if (homelessList.contains(uuid))
//			{
//				if (player.getGameMode() != GameMode.CREATIVE)
//				{
//					if (main.ownermodus.containsKey(uuid))
//					{
//						if (main.ownermodus.get(uuid) == true)
//						{
//							homelessList.remove(uuid);
//							return;
//						}
//					}
//					player.sendMessage(ColorOptions.messageformat + ">Owning a house or renting a room has multiple benefits");
//					player.sendMessage(ColorOptions.messageformat + ">Buy a house or rent a room by going clicking on the 'Houses' tab in your personal menu");
//					new BukkitRunnable()
//					{
//						public void run()
//						{
//							Menu menu = new Menu();
//							menu.openownedHouses(uuid, player);
//							Inventory inv = user.getOpenMenu(player);
//							menu.setMenuItemBlink((CommandSender) Bukkit.getConsoleSender(), player, inv, 1, 15);
//							menu.setMenuItemBlink((CommandSender) Bukkit.getConsoleSender(), player, inv, 2, 15);
//						}
//					}.runTaskLater(main, 1*20);
//				}
//				homelessList.remove(uuid);
//			}
//		}
//	}
//	
//	@EventHandler
//	public void onDeath(PlayerDeathEvent e)
//	{
//		if (e.getEntity() instanceof Player)
//		{
//			Player player = (Player) e.getEntity();
//			UUID uuid = player.getUniqueId();
//			if (homelessList.contains(uuid))
//			{
//				homelessList.remove(uuid);
//			}
//		}
//	}
}
