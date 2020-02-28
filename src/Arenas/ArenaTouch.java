package Arenas;

import org.bukkit.event.Listener;

import API_methods.WorldGuard;
import Main.Main;
import Menu.Menu;
import Products.Product;
import Streets.Street;
import Titles.Title;
import Towns.Town;

public class ArenaTouch implements Listener
{
	Town town = new Town();
	Street street = new Street();
	Title title = new Title();
	WorldGuard worldguard = new WorldGuard();
	Arena arena = new Arena();
	Product product = new Product();
	Menu menu = new Menu();
	private Main main;
	public ArenaTouch(Main main) 
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void PTouch(PlayerInteractEvent e)
//	{
//		Player p = e.getPlayer();
//		UUID uuid = p.getUniqueId();
//		Block block = e.getClickedBlock();
//		RegionManager regionmanager = worldguard.getWorldGuard().getRegionManager(p.getWorld());
//		if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
//		{
//			Location blockLoc = e.getClickedBlock().getLocation();
//			if (worldguard.getStructureIDbyRegion("arena", blockLoc, regionmanager) != null)
//			{
//				Integer arenaID = worldguard.getStructureIDbyRegion("arena", blockLoc, regionmanager);
////				openArenaMenu(p, arenaID);
//			}
//		}
//	}
}
