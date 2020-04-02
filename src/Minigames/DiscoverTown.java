package Minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.Listener;

import API_methods.WorldGuard;
import Main.Main;
import Products.Product;
import Titles.Title;
import Towns.Town;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

public class DiscoverTown implements Listener
{
	Town town = new Town();
	Title title = new Title();
	WorldGuard worldguard = new WorldGuard();
	Product product = new Product();
    NPCRegistry registry = CitizensAPI.getNPCRegistry();
	private Main main;
	public DiscoverTown(Main main)
	{
		this.main = main;
	}
	
	Map<UUID, Location> locationList = new HashMap<UUID, Location>();
	//To store whether a player is in a town or not
	List<UUID> inTown = new ArrayList<UUID>();
	
//	@EventHandler
//	public void onEnter(PlayerMoveEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		
//		try
//		{
//			user = Users.getUser(uuid);
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ())
//		{
//			for (Assignment assignment : user.getAssignmentList())
//			{
//				if (assignment instanceof AssignmentTravelDistance)
//				{
//					AssignmentTravelDistance Assignment = (AssignmentTravelDistance) assignment;
//					Assignment.addDistance(1);
//					break;
//				}
//			}
//		}
//	}
}
