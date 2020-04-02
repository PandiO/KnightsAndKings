package Friends;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldGuard;
import DataManager.Worldguard;
import Handlers.AddFriendEvent;
import Handlers.RemoveFriendEvent;
import Houses.House;
import Main.Main;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Users.User;
import Users.Users;

public class UpdateFriendRegions implements Listener
{
	House house = new House();
	Town town = new Town();
	Street street = new Street();
	Title title = new Title();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public UpdateFriendRegions(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onAdd(AddFriendEvent e)
	{
        RegionManager manager = Worldguard.getWorldGuard().getGlobalRegionManager().get(e.getWorld());
		UUID targetUUID = e.getFriendUUID();
		User user = e.getUser();
		UUID uuid = user.getUUID();
		Integer userID = null;
		Integer targetID = null;
		
		userID = user.getID();
		targetID = Users.fetchIDbyUUID(targetUUID);
		
		for (Integer houseID : house.getHouseIDList(null))
		{
			ProtectedRegion region = manager.getRegion("house_" + houseID);
			if (region != null)
			{
				Integer ownerID = house.getHouseOwnerID(houseID);
				if (ownerID != null && ownerID != 0)
				{
					if (ownerID == userID)
					{
						region.getMembers().addPlayer(targetUUID);
					}
					if (ownerID == targetID)
					{
						region.getMembers().addPlayer(uuid);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onRemove(RemoveFriendEvent e)
	{
		RegionManager manager = Worldguard.getWorldGuard().getGlobalRegionManager().get(e.getWorld());
		UUID targetUUID = e.getFriendUUID();
		User user = e.getUser();
		UUID uuid = user.getUUID();
		Integer userID = null;
		Integer targetID = null;
		
		for (Integer houseID : house.getHouseIDList(null))
		{
			ProtectedRegion region = manager.getRegion("house_" + houseID);
			if (region != null)
			{
				Integer ownerID = house.getHouseOwnerID(houseID);
				if (ownerID != null && ownerID != 0)
				{
					if (ownerID == userID)
					{
						region.getMembers().removePlayer(targetUUID);
					}
					if (ownerID == targetID)
					{
						region.getMembers().removePlayer(uuid);
					}
				}
			}
		}
	}
}
