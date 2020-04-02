/**
 * 
 */
package DataManager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegionSelector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldEdit;

/**
 * @author pandi
 *
 */
public interface Worldguard 
{
	public Integer townPriority = 8;
	public Integer districtPriority = 7;
	public Integer housePriority = 6;
	
	/**
	 * Retrieves the Worldguard plugin, required for further methods
	 * @return Instance of the Worldguard plugin
	 */
	public static WorldGuardPlugin getWorldGuard() 
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) 
        {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) plugin;
    }
	
	/**
	 * Retrieves the Worldguard RegionManager of a specific world
	 * @param world: The world you want the RegionManager of
	 * @return Worldguard RegionManager 
	 */
	public static RegionManager getRegionManager(World world)
	{
		RegionManager manager = null;
		
		manager = getWorldGuard().getRegionManager(world);
		
		return manager;
	}
	
    /**
     * Gets the gateRegion on the given location which matches the criteria given.
     * @param location: The location which needs to be checked on the gateRegion
     * @param criteria: A name or id that is present in the regions name/id
     * @param manager: The WorldGuard API requirement
     * @return Returns gateRegion if it matches the criteria, returns null if not
     */
    public static ProtectedRegion getRegion(Location location, String criteria, RegionManager manager)
    {
    	ProtectedRegion region = null;
    	ApplicableRegionSet regions = manager.getApplicableRegions(location);
    	
    	if (regions != null)
    	{
        	for (ProtectedRegion possibleregion : regions)
        	{
        		if (possibleregion.getId().contains(criteria.toLowerCase()))
        		{
        			region = possibleregion;
        			break;
        		}
        	}
    	}
    	
    	return region;
    }
	
	/**
	 * Retrieves all Worldguard regions of a specific location
	 * @param location: The location you want the regions of
	 * @return A set of Protected Regions
	 */
	public static ApplicableRegionSet getAvailableRegions(Location location)
	{
		ApplicableRegionSet regions = null;
		World world = location.getWorld();
		
		regions = getRegionManager(world).getApplicableRegions(location);
		
		
		return regions;
	}
	
	/**
	 * Returns all regions which contains the Worldguard ID of the gateRegion param
	 * @param gateRegion: The gateRegion you want to compare the other regions with (uses its Id)
	 * @param manager: The RegionManager which contains the regions
	 * @return A list of regions matching the Id of the param gateRegion
	 */
	public static List<ProtectedRegion> getTotalRegions(ProtectedRegion region, RegionManager manager)
	{
		List<ProtectedRegion> regionlist = new ArrayList<ProtectedRegion>();
		
		for (String regions : manager.getRegions().keySet())
		{
			if (regions.contains(region.getId()))
			{
				regionlist.add(manager.getRegions().get(regions));
			}
		}
		
		return regionlist;
	}
	
	//Checks if there are any regions of the same kind in the new gateRegion. 
    //For example if the criteria is property, then this will return true when there are no intersecting property-regions found in the new gateRegion
    /**
     * Checks if the gateRegion
     * @param regionManager
     * @param gateRegion
     * @param criteria
     * @param ID
     * @return
     */
	public static boolean checkSameRegionID(RegionManager regionManager, ProtectedRegion region, String criteria, Integer ID)
    {
    	boolean unique = true;
    	
    	for (ProtectedRegion r : region.getIntersectingRegions(regionManager.getRegions().values()))
    	{
    		if (r.getId().contains(criteria))
    		{
    			String[] split = r.getId().split("_");
    			if (!split[1].contains(","))
    			{
        			Integer regionID = Integer.valueOf(split[1]);
        			if (regionID != ID)
        			{
        				unique = false;
        				break;
        			}
    			} else
    			{
    				String[] splitPart = split[1].split(",");
        			Integer regionID = Integer.valueOf(splitPart[0]);
        			if (regionID != ID)
        			{
        				unique = false;
        				break;
        			}
    			}
    		}
    	}
    	
    	return unique;
    }

    /**
     * Checks if a gateRegion has no intersecting regions with the given criteria
     * @param regionManager: The Worldguard RegionManager
     * @param gateRegion: The gateRegion you want to check on intersecting regions
     * @param criteria: The criteria you want to check on
     * @return true if no intersecting regions contain the criteria, false if at least one gateRegion does
     */
	public static boolean checkUniqueRegion(RegionManager regionManager, ProtectedRegion region, String criteria)
    {
    	boolean unique = true;
    	
    	for (ProtectedRegion allRegions : region.getIntersectingRegions(regionManager.getRegions().values()))
    	{
    		if (allRegions.getId().contains(criteria))
    		{
    			String[] split = allRegions.getId().split("_");
    			Integer regionID = Integer.valueOf(split[1]);
    			unique = false;
    		}
    	}
    	
    	return unique;
    }
	
	/**
	 * Checks if the given location is the battleground of an Arena
	 * @param location: The location to check
	 * @param manager: The Worldguard RegionManager
	 * @return true if the location is battleground, false if not
	 */
	public static boolean isArenaBattleground(Location location, RegionManager manager)
	{
		boolean isBattleground = false;
		
		ApplicableRegionSet regionset = manager.getApplicableRegions(location);
		for (ProtectedRegion region : regionset)
		{
			String regionName = region.getId();
			if (regionName.contains("arena") && regionName.contains("battleground"))
			{
				isBattleground = true;
				break;
			}
		}
		
		return isBattleground;
	}
	
	/**
	 * Gets the Structure ID of the regions Id
	 * @param gateRegion: The gateRegion you want the ID of
	 * @return Structure ID
	 */
	public static Integer getStructureIDbyRegion(ProtectedRegion region)
	{
		if (region.getId().contains("global"))
		{
			return -1;
		}
		Integer structureID = null;
		
		String regionName = region.getId();
		String[] regionSplit = regionName.split("_");
		if (!regionSplit[1].contains(","))
		{
			structureID = Integer.valueOf(regionSplit[1]);
		} else
		{
			String[] subSplit = regionSplit[1].split(",");
			structureID = Integer.valueOf(subSplit[0]);
		}
		
		return structureID;
	}
	
	/**
	 * Gets the Structure ID of the gateRegion which matches the params
	 * @param criteria: The name of the id which is present in the regions Id
	 * @param location: The location you want to search for the Structure ID
	 * @param manager: The Worldguard RegionManager
	 * @return The Structure ID if present, null if not
	 */
	public static Integer getStructureIDbyRegion(String criteria, Location location, RegionManager manager)
	{
		Integer structureID = null;
		
		ProtectedRegion region = getRegion(location, criteria, manager);

		if (region == null)
		{
			return structureID;
		}
		
		String regionName = region.getId();
		if (regionName.contains(criteria.toLowerCase()))
		{
			String[] regionsplit = regionName.split("_");
			if (!regionsplit[1].contains(","))
			{
				structureID = Integer.valueOf(regionsplit[1]);
			} else
			{
				String[] split = regionsplit[1].split(",");
				structureID = Integer.valueOf(split[0]);
			}
		}

		return structureID;
	}
	
	/**
	 * Checks if the gateRegion is a Town gateRegion
	 * @param gateRegion: The gateRegion you want to check
	 * @return true if the gateRegion is a town, false if not
	 */
	public static boolean isTownRegion(ProtectedRegion region)
	{
		boolean isTown = false;
		
		String regionName = region.getId();
		String[] regionSplit = regionName.split("_");
		if (regionSplit[0].equalsIgnoreCase("town"))
		{
			isTown = true;
		}
		
		return isTown;
	}
	
	/**
	 * Checks if the gateRegion is a Property gateRegion
	 * @param gateRegion: The gateRegion you want to check
	 * @return true if the gateRegion is a property, false if not
	 */
	public static boolean isPropertyRegion(ProtectedRegion region)
	{
		boolean isProperty = false;
		
		String regionName = region.getId();
		String[] regionSplit = regionName.split("_");
		if (regionSplit[0].equalsIgnoreCase("property"))
		{
			isProperty = true;
		}
		
		return isProperty;
	}
	
	/**
	 * Checks if a gateRegion is a child Worldguard
	 * @param gateRegion: The gateRegion to check
	 * @return true if the gateRegion has a parent, false if not
	 */
	public static boolean isChildRegion(ProtectedRegion region)
	{
		boolean isChild = false;
		
		if (region.getParent() != null)
		{
			isChild = true;
		}
		
		return isChild;
	}
	
	public static Selection getSelectionFromRegion(Player player, ProtectedRegion region)
	{
		LocalSession session = WorldEdit.getWorldEdit().getSession(player);
		
		ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) region;
        BlockVector pt1 = cuboid.getMinimumPoint();
        BlockVector pt2 = cuboid.getMaximumPoint();

        CuboidRegionSelector selector = new CuboidRegionSelector();
        selector.selectPrimary(pt1);
        selector.selectSecondary(pt2);
        
        session.setRegionSelector(session.getSelectionWorld(), selector);
        selector.explainRegionAdjust(WorldEdit.getWorldEdit().wrapCommandSender(player), session);
        
        return WorldEdit.getWorldEdit().getSelection(player);
	}
}
