/**
 * 
 */
package DataManager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import Models.creations.Creation;
import Models.creations.DistrictCreation;
import Models.creations.TownCreation;
import Users.User;

/**
 * @author pandi
 *
 */
public interface Creations 
{
	public static List<Creation> Creations = new ArrayList<Creation>();
	public static List<Creation> StashedCreations = new ArrayList<Creation>();
	
	public static Creation FindCreation(User user)
	{
		Creation creation = null;
		
		for (Creation c : Creations)
		{
			if (c.getUser() == user)
			{
				creation = c;
				break;
			}
		}
		
		return creation;
	}
	
	public static Creation FindStashedCreation(Integer ID)
	{
		Creation creation = null;
		
		for (Creation c : StashedCreations)
		{
			if (c.getCreationID() == ID)
			{
				creation = c;
				break;
			}
		}
		
		return creation;
	}
	
	public static void DestroyCreation(Creation creation)
	{
		Player player = creation.getUser().getPlayer();
		if (creation instanceof TownCreation)
		{
			RegionManager manager = Worldguard.getRegionManager(player.getWorld());
			
			String regionID = ((TownCreation)creation).getRegion().getId();
			manager.removeRegion(regionID);
			
			for (ProtectedRegion r : ((TownCreation)creation).getSubRegions())
			{
				manager.removeRegion(r.getId());
			}
		} else
		if (creation instanceof DistrictCreation)
		{
			RegionManager manager = Worldguard.getRegionManager(player.getWorld());
			
			manager.removeRegion(((DistrictCreation)creation).getTempRegion().getId());
			
			for (ProtectedRegion r : ((DistrictCreation)creation).getTempSubRegions())
			{
				manager.removeRegion(r.getId());
			}
		}
	}
	
	public static void ClearCreations()
	{
		RegionManager manager = Worldguard.getRegionManager(Bukkit.getWorld("world"));
		
		for (ProtectedRegion r : manager.getRegions().values())
		{
			if (r.getId().contains("tempregion"))
			{
				manager.removeRegion(r.getId());
			}
		}
	}
}
