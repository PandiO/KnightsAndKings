package Friends;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import Houses.House;
import Main.Main;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Users.User;
import Users.Users;

public class FriendInteract implements Listener
{
	House house = new House();
	Town town = new Town();
	Street street = new Street();
	Title title = new Title();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public FriendInteract(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		try
		{
			if (Users.existUser(player.getName()) == true)
			{
				User user = Users.getUser(uuid);
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					Block block = e.getClickedBlock();
					Location blockLocation = block.getLocation();
					RegionManager regionmanager = worldguard.getWorldGuard().getRegionManager(player.getWorld());
					Integer houseID = worldguard.getStructureIDbyRegion("house", blockLocation, regionmanager);
					if (houseID != null && house.getHouseIDList(null).contains(houseID))
					{
						Integer ownerID = house.getHouseOwnerID(houseID);
						if (ownerID != null && ownerID != 0 && ownerID != user.getID() && !user.inOwnerModus())
						{
							e.setCancelled(true);
						}
					}
				}
			}
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
}
