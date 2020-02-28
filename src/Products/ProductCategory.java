package Products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Main.Main;

public class ProductCategory 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	//Save a new category to the database
	public void saveCategory(Integer ID, String name, String description)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO ProductCategories(ID, Name, Description) VALUES(?, ?, ?);");
			stmt.setInt(1, ID);
			stmt.setString(2, name.substring(0, 1).toUpperCase() + name.substring(1, name.length()));
			stmt.setString(3, description);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the id of a category from the database
	public Integer getCategoryID(String name)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ProductCategories WHERE Name=?;");
			stmt.setString(1, name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase());
			
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
	
	
	//Get the name of a category from the database
	public String getCategoryName(Integer id)
	{
		String name = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ProductCategories WHERE ID=?;");
			stmt.setInt(1, id);
			
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
	
	//Get the description of a category
	public String getCategoryDescription(Integer id)
	{
		String desc = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ProductCategories WHERE ID=?;");
			stmt.setInt(1, id);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				desc = results.getString("Description");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return desc;
	}
	
	//Remove a category from the database
	public void removeCategory(Integer id)
	{
		String name = getCategoryName(id);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM ProductCategories WHERE ID=?;");
			stmt.setInt(1, id);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "ProductCategory " + name + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get a list of productcategory-id's from the database
	public ArrayList<Integer> getCategoryIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ProductCategories;");
			
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
	
	//Get a list of productcategory-names from the database
	public ArrayList<String> getCategoryNameList()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ProductCategories;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getString("Name"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
}
