/**
 * 
 */
package DataManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import Handlers.ColorOptions;
import Main.Main;
import Models.creations.DistrictCreation;
import Models.district.District;

/**
 * @author pandi
 *
 */
public interface Districts 
{
	public static List<District> Districts = new ArrayList<District>();
	
	//Save a district to the database
	public static void CreateDistrict(CommandSender sender, String name, int townID)
	{
		//Check if a district with this name already exists
		if (!ExistDistrict(name, townID))
		{
			try 
			{
				PreparedStatement stmt = Main.getConnection().prepareStatement("INSERT INTO District(Name, TownID) VALUES(?, ?);");
				stmt.setString(1, name.toLowerCase());
				stmt.setInt(2, townID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New district " + name + " has succesfully been created in the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates a district. Registers a district into the Database and creates the Region for it
	 * @param creation: Contains all the required information
	 */
	public static void CreateDistrict(DistrictCreation creation)
	{
		boolean completeSteps = true;
		Player player = creation.getUser().getPlayer();
		Integer districtID = -1;

		try
		{
			CreateDistrict(creation.getUser().getPlayer(), creation.getName(), creation.getTownID());
		} catch (Exception ex)
		{
			completeSteps = false;
			String message = ColorOptions.error + "Something went wrong while creating the District in the Database. Please try again and notify a developer";
			player.sendMessage(message);
			Main.logError(message);
			ex.printStackTrace();
			return;
		}
		
		try
		{
			districtID = FetchDistrictID(creation.getName(), creation.getTownID());
			
			String succesMessage = ChatColor.GREEN + "The new District's ID has been fetched from the Database";
			Main.logMessage(succesMessage);
			creation.sendMessage(Arrays.asList(succesMessage));
		} catch (Exception ex)
		{
			completeSteps = false;
			String message = ColorOptions.error + "Something went wrong while fetching the District form the Database. Please try again and notify a developer";
			player.sendMessage(message);
			Main.logError(message);
			ex.printStackTrace();
			return;
		}
		ProtectedRegion parent = null;
		        
        /**
         * Creating the main Region
         */
        try
        {
            Selection selection = Worldguard.getSelectionFromRegion(player, creation.getTempRegion());
            
        	parent = new ProtectedCuboidRegion(
    				"district_" + districtID,
    				new BlockVector(selection.getNativeMinimumPoint()),
    				new BlockVector(selection.getNativeMaximumPoint())
    				);
    		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(parent);
    		
    		parent.setPriority(Integer.valueOf(Worldguard.districtPriority));
    		RegionGroupFlag entryFlag = DefaultFlag.ENTRY.getRegionGroupFlag();
    		try 
    		{
    			entryFlag.parseInput(Worldguard.getWorldGuard(), null, "non_members");
    		} catch (InvalidFlagFormat e) 
    		{
    			// Auto-generated catch block
    			e.printStackTrace();
    		}
    		
			String succesMessage = ChatColor.GREEN + "The new District's Worldguard Region has been created";
			Main.logMessage(succesMessage);
			creation.sendMessage(Arrays.asList(succesMessage));
        } catch (Exception ex)
        {
        	completeSteps = false;
        	String message = ColorOptions.error + "Something went wrong while creating the Worldguard Region! Please try again and notify a developer";
        	player.sendMessage(message);
        	Main.logError(message);
        	ex.printStackTrace();
        	return;
        }
        
        for (ProtectedRegion r : creation.getTempSubRegions())
    	{
            try
            {
        		Selection selection = Worldguard.getSelectionFromRegion(player, creation.getTempRegion());

            	ProtectedRegion region = new ProtectedCuboidRegion(
        				"district_" + districtID,
        				new BlockVector(selection.getNativeMinimumPoint()),
        				new BlockVector(selection.getNativeMaximumPoint())
        				);
            	region.setParent(parent);
        		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(region);
        		
    			String succesMessage = ChatColor.GREEN + "The new District's ID child Region has been created " + creation.getTempSubRegions().indexOf(r)+1 + "/" + creation.getTempSubRegions().size();
    			Main.logMessage(succesMessage);
    			creation.sendMessage(Arrays.asList(succesMessage));
            } catch (Exception ex)
            {
            	completeSteps = false;
            	String message = ColorOptions.error + "Something went wrong while creation the Worldguard child Region! Please try again and notify a developer";
            	player.sendMessage(message);
            	Main.logError(message);
            	ex.printStackTrace();
            	return;
            }
    	}
        
        if (completeSteps)
        {
        	String message = ChatColor.GREEN + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for District " + districtID + " has succesfully been completed!";
        	Main.logMessage(message);
        	player.sendMessage(message);
        } else
        {
        	String message = ColorOptions.error + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for District " + districtID + " has completed with Errors!";
        	Main.logError(message);
        	player.sendMessage(message);
        }
        Creations.DestroyCreation(creation);
	}
	
	public static void SaveDistrict(District district)
	{
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE District SET Name = ?, TownID = ? WHERE ID = ?;");
			stmt.setString(1, district.getName());
			stmt.setInt(2, district.getTownID());
			stmt.setInt(3, district.getID());
			
			stmt.executeUpdate();
			String message = ChatColor.GREEN + "District " + district.getID() + " has succesfully been saved to the database!";
			Main.logMessage(message);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static boolean RemoveDistrict(CommandSender sender, int districtID)
	{
		String message = null;
		boolean removed = false;
		try
		{
			//Prepare the search query
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM District WHERE ID=?");
			stmt.setInt(1, districtID);
			
			stmt.executeUpdate();
			message = ChatColor.GRAY + "Deleted district with ID " + districtID + " from the Database at " + Main.getTime();
			Main.logMessage(message);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing district " + districtID + " from the database. Please notify a developer";	
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		try
		{
			RegionManager manager = Worldguard.getRegionManager(Bukkit.getWorld("world"));
			manager.removeRegion("district_" + districtID);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing town's Region from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		if (removed)
		{
			District district = FindDistrict(districtID);
			if (district != null)
			{
				Districts.remove(district);
				DestroyDistrict(district);
			}
			removed = true;
		}
		sender.sendMessage(message);
		
		return removed;
	}
	
	//
	public static boolean ExistDistrict(String name, int townID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM District WHERE Name = ? AND TownID = ?");
			stmt.setString(1, name);
			stmt.setInt(2, townID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				exist = true;
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return exist;
	}
	
	public static boolean ExistDistrict(int districtID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM District WHERE ID = ?");
			stmt.setInt(1, districtID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				exist = true;
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return exist;
	}
	
	public static District InstantiateDistrict(int districtID, boolean newDistrict)
	{
		District district = FindDistrict(districtID);
		
		if (district != null)
		{
			return district;
		}
		
		if (!ExistDistrict(districtID))
		{
			return null;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM District WHERE ID = ?");
			stmt.setInt(1, districtID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				int ID = results.getInt("ID");
				district = new District(ID, 
						results.getString("Name"), 
						results.getInt("TownID"));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return district;
	}
	
	public static Integer FetchDistrictID(String name, int townID)
	{
		Integer districtID = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM District WHERE Name = ? AND TownID = ?");
			stmt.setString(1, name);
			stmt.setInt(2, townID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				districtID = results.getInt("ID");
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return districtID;
	}
	
	public static District FindDistrict(String name, int townID)
	{
		District district = null;
		
		for (District d : Districts)
		{
			if (d.getName().equalsIgnoreCase(name) && d.getTownID() == townID)
			{
				district = d;
			}
		}
		
		return district;
	}
	
	public static District FindDistrict(int ID)
	{
		District district = null;
		
		for (District d : Districts)
		{
			if (d.getID() == ID)
			{
				district = d;
			}
		}
		
		return district;
	}
	
	public static void DestroyDistrict(District district)
	{
		district = null;
		System.gc();
	}
	
	public static ResultSet GetDistrictList(int townID)
	{
		ResultSet results = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM District;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			if (townID != -1)
			{
				stmt = Main.getConnection().prepareStatement("SELECT * FROM District WHERE TownID = ?");
				stmt.setInt(1, townID);
			}
			
			ResultSet rawResults = stmt.executeQuery();
			
			results = rawResults;

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		return results;
	}
	
	public static void SaveAll()
	{
		for (District district : Districts)
		{
			SaveDistrict(district);
		}
	}
	
	public static void InstantiateAll(int townID)
	{
		ResultSet set = GetDistrictList(townID);
		
		try 
		{
			while (set.next())
			{
				InstantiateDistrict(set.getInt("ID"), false);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
