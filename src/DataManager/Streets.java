/**
 * 
 */
package DataManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import Main.Main;
import Models.Street;

/**
 * @author pandi
 *
 */
public interface Streets {
	public static List<Street> Streets = new ArrayList<Street>();
	
	//Save a street to the database
	public static void CreateStreet(CommandSender sender, String name, int townID)
	{
		//Check if a street with this name already exists
		if (!ExistStreet(name, townID))
		{
			try 
			{
				PreparedStatement stmt = Main.getConnection().prepareStatement("INSERT INTO Street(Name, TownID) VALUES(?, ?);");
				stmt.setString(1, name.toLowerCase());
				stmt.setInt(2, townID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New street " + name + " has succesfully been created in the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void SaveStreet(Street street)
	{
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE Street SET Name = ?, TownID = ? WHERE ID = ?;");
			stmt.setString(1, street.getName());
			stmt.setInt(2, street.getTownID());
			stmt.setInt(3, street.getID());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Street " + street.getID() + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static boolean RemoveStreet(CommandSender sender, int streetID)
	{
		boolean removed = false;
		try
		{
			//Prepare the search query
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM Street WHERE ID=?");
			stmt.setInt(1, streetID);
			
			stmt.executeUpdate();
			Main.logMessage(ChatColor.GRAY + "Deleted street with ID " + streetID + " from the Database at " + Main.getTime());
		} catch (Exception ex)
		{
			Main.logError("Error while removing street " + streetID + " from the database. Please notify a developer");
			ex.printStackTrace();
			removed = false;
		}
		
		if (removed)
		{
			Street street = FindStreet(streetID);
			if (street != null)
			{
				Streets.remove(street);
				DestroyStreet(street);
			}
			removed = true;
		}
		
		return removed;
	}
	
	//
	public static boolean ExistStreet(String name, int townID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Street WHERE Name = ? AND TownID = ?");
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
	
	public static boolean ExistStreet(int streetID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Street WHERE ID = ?");
			stmt.setInt(1, streetID);
			
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
	
	public static Street InstantiateStreet(int streetID, boolean newStreet)
	{
		Street street = FindStreet(streetID);
		
		if (street != null)
		{
			return street;
		}
		
		if (!ExistStreet(streetID))
		{
			return null;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Street WHERE ID = ?");
			stmt.setInt(1, streetID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				int ID = results.getInt("ID");
				street = new Street(ID, 
						results.getString("Name"), 
						results.getInt("TownID"));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return street;
	}
	
	public static Integer FetchStreetID(String name, int townID)
	{
		Integer streetID = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Street WHERE Name = ? AND TownID = ?");
			stmt.setString(1, name);
			stmt.setInt(2, townID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				streetID = results.getInt("ID");
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return streetID;
	}
	
	public static Street FindStreet(String name, int townID)
	{
		Street street = null;
		
		for (Street s : Streets)
		{
			if (s.getName().equalsIgnoreCase(name) && s.getTownID() == townID)
			{
				street = s;
			}
		}
		
		return street;
	}
	
	public static Street FindStreet(int ID)
	{
		Street street = null;
		
		for (Street s : Streets)
		{
			if (s.getID() == ID)
			{
				street = s;
			}
		}
		
		return street;
	}
	
	public static void DestroyStreet(Street street)
	{
		street = null;
		System.gc();
	}
	
	public static ResultSet GetStreetList(int townID)
	{
		ResultSet results = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Street;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			if (townID != -1)
			{
				stmt = Main.getConnection().prepareStatement("SELECT * FROM Street WHERE TownID = ?");
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
		for (Street street : Streets)
		{
			SaveStreet(street);
		}
	}
	
	public static void InstantiateAll(int townID)
	{
		ResultSet set = GetStreetList(townID);
		
		try 
		{
			while (set.next())
			{
				InstantiateStreet(set.getInt("ID"), false);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
