package Products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Main.Main;

public class Enchantment 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	
	public List<Integer> getIDList()
	{
		List<Integer> list = new ArrayList<Integer>();
				
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Enchantments;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
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
	
	//Get the uuid of a user by ID from the database
	public String getEnchantmentName(Integer enchantmentID)
	{
		String name = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Enchantments Where ID=?;");	
			stmt.setInt(1, enchantmentID);
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
	
	//Get the uuid of a user by ID from the database
	public Integer getEnchantmentID(String enchantmentName)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Enchantments Where Name=?;");	
			stmt.setString(1, enchantmentName);
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
	
	//Get the uuid of a user by ID from the database
	public Integer getEnchantmentPrice(Integer enchantmentID)
	{
		Integer price = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Enchantments Where ID=?;");	
			stmt.setInt(1, enchantmentID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				price = results.getInt("Price");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return price;
	}
	
	//Get the uuid of a user by ID from the database
	public Integer getEnchantmentMaxLevel(Integer enchantmentID)
	{
		Integer price = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Enchantments Where ID=?;");	
			stmt.setInt(1, enchantmentID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				price = results.getInt("MaxLevel");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return price;
	}
	
	//Get the uuid of a user by ID from the database
	public Integer getEnchantmentGrade(Integer enchantmentID)
	{
		Integer price = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Enchantments Where ID=?;");	
			stmt.setInt(1, enchantmentID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				price = results.getInt("Grade");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return price;
	}
	
	public boolean customEnhantment(Integer enchantmentID)
	{
		boolean custom = false;
		
		String name = this.getEnchantmentName(enchantmentID);
		switch (name)
		{
		case "Poison": custom = true;
		break;
		case "Wither": custom = true;
		break;
		case "Freeze": custom = true;
		break;
		case "Blindness": custom = true;
		break;
		case "Confusion": custom = true;
		break;
		case "Strength": custom = true;
		break;
		case "Health Boost": custom = true;
		break;
		case "Resistance": custom = true;
		break;
		case "Invisibility": custom = true;
		break;
		case "Chaos": custom = true;
		break;
		case "Flash Chaos": custom = true;
		break;
		case "Armor Repair": custom = true;
		break;
		default: custom = false;
		break;
		}
		
		return custom;
	}
	
	public Integer getPreferCategory(Integer enchantmentID)
	{
		Integer id = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Enchantments Where ID=?;");	
			stmt.setInt(1, enchantmentID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				id = results.getInt("CategoryID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
}
