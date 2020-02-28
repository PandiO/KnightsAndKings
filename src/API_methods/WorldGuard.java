package API_methods;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import Main.Main;

public class WorldGuard 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	public WorldGuardPlugin getWorldGuard() 
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) 
        {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) plugin;
    }
	
	//Get the regionmanager from a specific world
	public RegionManager getRegionManager(World world)
	{
		RegionManager manager = null;
		
		manager = getWorldGuard().getRegionManager(world);
		
		return manager;
	}
	
	//Get all regions available at a specific location
	public ApplicableRegionSet getAvailableRegions(Location location)
	{
		ApplicableRegionSet regions = null;
		World world = location.getWorld();
		
		regions = getRegionManager(world).getApplicableRegions(location);
		
		
		return regions;
	}
	
	//Check the available regions of a location for a matching id-category(fe. house, city, property, keep).
	public ProtectedRegion getRegion(ApplicableRegionSet regions, String regioncategory)
	{
		ProtectedRegion region = null;
		
		if (regions.size() >= 1)
		{
			for (ProtectedRegion reg : regions.getRegions())
			{
				// && Pattern.matches("[^0-9]", reg.getId())
				if (reg.getId().contains(regioncategory.toLowerCase()))
				{
					if (reg.getId().contains(","))
					{
						region = reg.getParent();
					} else
					{
						region = reg;
					}
				} else
				{
					//Bukkit.getConsoleSender().sendMessage("Region doesn't match to " + regioncategory.toLowerCase() + ", regionName: " + reg.getId());
				}
			}
		}
		
		return region;
	}
	
	public List<ProtectedRegion> getTotalRegions(ProtectedRegion region, RegionManager manager)
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
	
	public Integer getStructureIDbyRegion(String structureCategory, Location location, RegionManager manager)
	{
		Integer structureID = null;

		ApplicableRegionSet regionset = manager.getApplicableRegions(location);
		for (ProtectedRegion region : regionset)
		{
			String regionName = region.getId();
			if (regionName.contains(structureCategory))
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
		}

		return structureID;
	}
	
	public boolean isArenaBattleground(Location location, RegionManager manager)
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
	
	public Integer getStructureIDbyRegion(ProtectedRegion region)
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
	
	public boolean isTownRegion(ProtectedRegion region)
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
	
	public boolean isChildRegion(ProtectedRegion region)
	{
		boolean isChild = false;
		
		if (region.getId().contains(","))
		{
			isChild = true;
		}
		
		return isChild;
	}
	
	public boolean isPropertyRegion(ProtectedRegion region)
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
}
