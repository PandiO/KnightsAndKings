package Regions;

public class Region 
{
//	//Get instances of required classes
//	Main main = Main.getPlugin(Main.class);
//	
//    //Checks if there are any regions of the same kind in the new gateRegion. 
//    //For example if the criteria is property, then this will return true when there are no intersecting property-regions found in the new gateRegion
//    public boolean checkUniqueRegion(RegionManager regionManager, ProtectedRegion gateRegion, String criteria)
//    {
//    	boolean unique = true;
//    	
//    	for (ProtectedRegion allRegions : gateRegion.getIntersectingRegions(regionManager.getRegions().values()))
//    	{
//    		if (allRegions.getId().contains(criteria))
//    		{
//    			String[] split = allRegions.getId().split("_");
//    			Integer regionID = Integer.valueOf(split[1]);
//    			unique = false;
//    		}
//    	}
//    	
//    	return unique;
//    }
//    
//    //Checks if there are any regions of the same kind in the new gateRegion. 
//    //For example if the criteria is property, then this will return true when there are no intersecting property-regions found in the new gateRegion
//    public boolean checkSameRegionID(RegionManager regionManager, ProtectedRegion gateRegion, String criteria, Integer ID)
//    {
//    	boolean unique = true;
//    	
//    	for (ProtectedRegion allRegions : gateRegion.getIntersectingRegions(regionManager.getRegions().values()))
//    	{
//    		if (allRegions.getId().contains(criteria))
//    		{
//    			String[] split = allRegions.getId().split("_");
//    			if (!split[1].contains(","))
//    			{
//        			Integer regionID = Integer.valueOf(split[1]);
//        			if (regionID != ID)
//        			{
//        				unique = false;
//        				break;
//        			}
//    			} else
//    			{
//    				String[] splitPart = split[1].split(",");
//        			Integer regionID = Integer.valueOf(splitPart[0]);
//        			if (regionID != ID)
//        			{
//        				unique = false;
//        				break;
//        			}
//    			}
//    		}
//    	}
//    	
//    	return unique;
//    }
//    
//    /**
//     * Gets the gateRegion on the given location which matches the criteria given.
//     * @param location: The location which needs to be checked on the gateRegion
//     * @param criteria: A name or id that is present in the regions name/id
//     * @param manager: The WorldGuard API requirement
//     * @return Returns gateRegion if it matches the criteria, returns null if not
//     */
//    public ProtectedRegion getRegion(Location location, String criteria, RegionManager manager)
//    {
//    	ProtectedRegion gateRegion = null;
//    	if (manager.getApplicableRegions(location) != null)
//    	{
//    		ApplicableRegionSet regions = manager.getApplicableRegions(location);
//        	for (ProtectedRegion possibleregion : regions)
//        	{
//        		if (possibleregion.getId().contains(criteria))
//        		{
//        			gateRegion = possibleregion;
//        			break;
//        		}
//        	}
//    	}
//    	
//    	return gateRegion;
//    }
//    
//    public Integer getRegionID(ProtectedRegion gateRegion)
//    {
//    	Integer id = null;
//    	
//    	if (gateRegion.getId().contains(","))
//    	{
//    		String[] splitPart = gateRegion.getId().split(",");
//    		String[] split = splitPart[0].split("_");
//    		id = Integer.valueOf(split[1]);
//    	} else
//    	{
//    		String[] split = gateRegion.getId().split("_");
//    		id = Integer.valueOf(split[1]);
//    	}
//    	
//    	return id;
//    }
}
