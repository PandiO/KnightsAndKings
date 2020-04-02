package Towns;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import DataManager.Worldguard;
import Main.Main;
import Titles.Title;

public class Town 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	Title title = new Title();
	
	//Save a city to the database
	public void saveTown(String name, Integer requiredTitleID, String description)
	{
		Integer titleID = null;
		if (title.getIDList().contains(requiredTitleID))
		{
			titleID = requiredTitleID;
		}
		//Check if a city with this name already exists
		if (this.getTownID(name) == null)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Town(Name, RequiredTitleID, Description) VALUES(?, ?, ?);");
				//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
				stmt.setString(1, name.toLowerCase());
				stmt.setInt(2, titleID);
				stmt.setString(3, description);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New town " + name + " has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void removeTownbyID(Integer townID)
	{
		String townName = null;
		
		townName = getTownName(townID);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Town WHERE ID=?;");
			stmt.setInt(1, townID);
			
			stmt.executeUpdate();
			//Send the console a message if the spawnpoint belongs to a city
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Town " + townName + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Check if a location is inside a city gateRegion
	public boolean checkTownbyLocation(Location location)
	{
		boolean exist = false;
		
        RegionManager manager = Worldguard.getWorldGuard().getGlobalRegionManager().get(location.getWorld());
        ApplicableRegionSet regions = manager.getApplicableRegions(location);
        
        for (ProtectedRegion region : regions)
        {
        	if (region.getId().contains("town"))
        	{
        		exist = true;
        		break;
        	}
        }
		
		return exist;
	}
	
	
	//Get the city id by location
	public Integer getTownIDbyLocation(Location location)
	{
		Integer id = null;
		
		RegionManager manager = Worldguard.getWorldGuard().getGlobalRegionManager().get(location.getWorld());
        ApplicableRegionSet regions = manager.getApplicableRegions(location);
        
        for (ProtectedRegion region : regions)
        {
        	if (region.getId().contains("town"))
        	{
        		String[] split = region.getId().split("_");
        		
        		if (this.checkTown(Integer.valueOf(split[1])))
        		{
            		id = Integer.valueOf(split[1]);
        		}
        		break;
        	}
        }
		
		return id;
	}
	
	//Get a list of citynames from the database
	public ArrayList<String> getTownNameList()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		try
		{
			//prepare the query to retrieve uuid of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Town;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			while (results.next())
			{
				list.add(results.getString("Name"));
			}
			return list;
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//Get a list of city id's from the database
	public ArrayList<Integer> getTownIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve uuid of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Town;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
			return list;
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//Get the id of a city by name
	public Integer getTownID(String name)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Town WHERE Name=?;");	
			stmt.setString(1, name.toLowerCase());
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				ID = results.getInt("ID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ID;
	}
	
	//Get the name of a city by id from the database
	public String getTownName(Integer townID)
	{
		String name = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Town WHERE ID=?;");	
			stmt.setInt(1, townID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				name = results.getString("Name");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return name;
	}
	
	//Get the required title id for a city by id
	public Integer getRequiredTitleID(Integer townID)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve the required id of a city
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Town WHERE ID=?;");	
			stmt.setInt(1, townID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the id of the city
			if (results.next())
			{
				ID = results.getInt("RequiredTitleID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ID;
	}
	
	//Get the description of a specific city
	public String getTownDescription(Integer townID)
	{
		String description = null;
		
		try
		{
			//prepare the query to retrieve the required id of a city
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Town WHERE ID=?;");	
			stmt.setInt(1, townID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the id of the city
			if (results.next())
			{
				description = results.getString("Description");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return description;
	}
	
	//Check if a city exists with a given city id
	public boolean checkTown(Integer townID)
	{
		boolean exist = false;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Town WHERE ID=?;");
			stmt.setInt(1, townID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				exist = true;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return exist;
	}
	
	//Chect if a part-id already exists in the database
	public boolean checkPartID(Integer townID, Integer partID)
	{
		boolean exist = false;
		
		if (getTownPartList(townID).contains(partID))
		{
			exist = true;
		}
		
		return exist;
	}
	
	//Remove a city part from the database
	public void removeTownPart(Integer townID, Integer partID)
	{
		try
		{
			//prepare the query to delete a part-gateRegion
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM TownRegion WHERE SubID=? AND TownID=?;");	
			stmt.setInt(1, partID);
			stmt.setInt(2, townID);
			//Execute the query
			stmt.executeUpdate();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Save a sub-gateRegion of a city
	public void saveTownPart(Integer townID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO TownRegion(TownID) VALUES(?);");
			stmt.setInt(1, townID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New part of town " + getTownName(townID) + " has succesfully been saved to the Database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get a list of sub-regions of a city
	public ArrayList<Integer> getTownPartList(Integer townID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a city
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From TownRegion WHERE TownID=?;");	
			stmt.setInt(1, townID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("SubID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public ArrayList<Integer> getUserIDListbyTown(Integer townID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a city
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From DiscoveredTowns WHERE TownID=?;");	
			stmt.setInt(1, townID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("UserID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public void saveDiscoveredTown(Integer townID, Integer userID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO DiscoveredTowns(TownID, UserID) VALUES(?, ?);");
			stmt.setInt(1, townID);
			stmt.setInt(2, userID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "User with ID " + userID + " has succesfully discovered a town with ID " + townID + " and it has been saved to the Database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void removeDiscoveredTown(Integer townID, Integer userID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM DiscoveredTowns WHERE TownID=? AND UserID=?;");
			stmt.setInt(1, townID);
			stmt.setInt(2, userID);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}
