package Regions;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import Main.Main;

public class Region 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
    //Checks if there are any regions of the same kind in the new region. 
    //For example if the criteria is property, then this will return true when there are no intersecting property-regions found in the new region
    public boolean checkUniqueRegion(RegionManager regionManager, ProtectedRegion region, String criteria)
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
    
    //Checks if there are any regions of the same kind in the new region. 
    //For example if the criteria is property, then this will return true when there are no intersecting property-regions found in the new region
    public boolean checkSameRegionID(RegionManager regionManager, ProtectedRegion region, String criteria, Integer ID)
    {
    	boolean unique = true;
    	
    	for (ProtectedRegion allRegions : region.getIntersectingRegions(regionManager.getRegions().values()))
    	{
    		if (allRegions.getId().contains(criteria))
    		{
    			String[] split = allRegions.getId().split("_");
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
    
    public ProtectedRegion getRegion(Location location, String criteria, RegionManager manager)
    {
    	ProtectedRegion region = null;
    	if (manager.getApplicableRegions(location) != null)
    	{
    		ApplicableRegionSet regions = manager.getApplicableRegions(location);
        	for (ProtectedRegion possibleregion : regions)
        	{
        		if (possibleregion.getId().contains(criteria))
        		{
        			region = possibleregion;
        			break;
        		}
        	}
    	}
    	
    	return region;
    }
    
    public Integer getRegionID(ProtectedRegion region)
    {
    	Integer id = null;
    	
    	if (region.getId().contains(","))
    	{
    		String[] splitPart = region.getId().split(",");
    		String[] split = splitPart[0].split("_");
    		id = Integer.valueOf(split[1]);
    	} else
    	{
    		String[] split = region.getId().split("_");
    		id = Integer.valueOf(split[1]);
    	}
    	
    	return id;
    }
}
