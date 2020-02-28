package Streets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import API_methods.WorldEdit;
import API_methods.WorldGuard;
import Main.Main;
import Towns.Town;

public class Street 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	Town town = new Town();
	WorldGuard worldguard = new WorldGuard();
	WorldEdit worldedit = new WorldEdit();
	
	//Save a new street to the database
	public void saveStreet(String name, Integer townID)
	{	
		//Check if no street with this name exists
		if (checkStreet(name, townID) == false)
		{
			//Check if the location of the new street is in a city
			if (townID != 1)
			{	
				try 
				{
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Street(Name, TownID) VALUES(?, ?);");
					stmt.setString(1, name);
					stmt.setInt(2, townID);
					
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New street " + name + " in town " + town.getTownName(townID) + " has succesfully been saved to the database!");
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
			} else
			{
				try 
				{
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Street(Name) VALUES(?);");
					stmt.setString(1, name.toLowerCase());
					
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New street " + name + " in the wilderness has succesfully been saved to the database!");
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
		} else
		{
			return;
		}
	}
	
	//Get the id of a street from the database
	public Integer getStreetID(String streetName, Integer townID)
	{
		Integer streetID = null;
		
		try
		{
			//prepare the query to retrieve the id
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Street WHERE Name=? AND TownID=?;");
			stmt.setString(1, streetName.toLowerCase());
			stmt.setInt(2, townID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				streetID = results.getInt("ID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return streetID;
	}
	
	//Get the name of a street from the database
	public String getStreetName(Integer streetID)
	{
		String name = null;
		
		try
		{
			//prepare the query to retrieve the id
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Street WHERE ID=?;");
			stmt.setInt(1, streetID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
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
	
	//Check if a street exists with a given street name
	public boolean checkStreet(String streetName, Integer townID)
	{
		boolean exist = false;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Street WHERE Name=? AND TownID=?;");
			stmt.setString(1, streetName.toLowerCase());
			stmt.setInt(2, townID);
			
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
	
	//Check if a street exists with a given city ID
	public boolean checkStreetbyID(Integer streetID)
	{
		boolean exist = false;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Street WHERE ID=?;");
			stmt.setInt(1, streetID);
			
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
	
	//Get a list of streetnames from the database
	public ArrayList<Integer> getStreetIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve uuid of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Street ORDER BY TownID;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	//Check if there is a city-region on the specific location
	public boolean checkCity(Location location)
	{
		boolean city = false;
		
		//Check if a region has been found in the category 'city'
		if (worldguard.getRegion(worldguard.getAvailableRegions(location), "town") != null)
		{
			city = true;
		}
		
		return city;
	}
	
	//Get the city name of a city-region when there is one at a specific location
	public String getTownName(Location location)
	{
		String name = null;
		Integer townID = this.worldguard.getStructureIDbyRegion("town", location, this.worldguard.getRegionManager(location.getWorld()));

		if (townID != null)
		{
			name = town.getTownName(townID);
		}
		
		return name;
	}
	
	//Get the city name of a city-region when there is one at a specific location
	public Integer getTownIDbyLocation(Location location)
	{
		Integer townID = null;
		
		townID = this.worldguard.getStructureIDbyRegion("town", location, this.worldguard.getRegionManager(location.getWorld()));

		
		return townID;
	}
	
	//Remove a street from the database
	public void removeStreet(Integer streetID, Integer townID)
	{	
		String streetName = this.getStreetName(streetID);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Street WHERE ID=? AND TownID=?;");
			stmt.setInt(1, streetID);
			stmt.setInt(2, townID);
			
			stmt.executeUpdate();
			//Send the console a message if the spawnpoint belongs to a city
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Street " + streetName + " in town " + town.getTownName(townID) + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the city name a street is in
	public Integer getTownID(Integer streetID)
	{
		Integer id = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Street WHERE ID=?;");	
			stmt.setInt(1, streetID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("TownID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
}
