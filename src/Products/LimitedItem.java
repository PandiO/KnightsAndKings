package Products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Main.Main;

public class LimitedItem 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	
	//Save a limited mode which is used for temporarily items
	public void saveLimitedItem(String name, Long expireTime)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO LimitedItems(Name, ExpireTime) VALUES(?, ?);");
			stmt.setString(1, name);
			stmt.setInt(2, expireTime.intValue());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "LimitedItem " + name + " has succesfully been added to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the name of a limited item from the database
	public String getName(Integer ID)
	{
		String name = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM LimitedItems WHERE ID=?;");
			stmt.setInt(1, ID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				name = results.getString("Name");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return name;
	}
	
	//Remove a limited mode from the database
	public void removeLimitedItem(Integer ID)
	{
		String name = this.getName(ID);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM LimitedItems WHERE ID=?;");
			stmt.setInt(1, ID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "LimitedItem " + name + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the expire time of a limited item
	public Float getExpireTime(Integer ID)
	{
		Float time = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM LimitedItems WHERE ID=?;");
			stmt.setInt(1, ID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				time = results.getFloat("ExpireTime");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return time;
	}
	
	//Get a list of limited item id's from the database
	public ArrayList<Integer> getIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM LimitedItems;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//Get the id of a limited item from the database
	public Integer getID(String name)
	{
		Integer id = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM LimitedItems WHERE Name=?;");
			stmt.setString(1, name);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("ID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
}
