package Resources;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Main.Main;
import Products.Product;

public class ResourceCategory 
{
	Main main = Main.getPlugin(Main.class);
	Product product = new Product();
	
	//Save a new category to the database
	public void saveCategory(String name, String description)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO ResourceCategories(Name, Description) VALUES(?, ?);");
			stmt.setString(1, name.substring(0, 1).toUpperCase() + name.substring(1, name.length()));
			stmt.setString(2, description);
			
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
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ResourceCategories WHERE Name=?;");
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
	public String getCategoryName(Integer categoryID)
	{
		String name = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ResourceCategories WHERE ID=?;");
			stmt.setInt(1, categoryID);
			
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
	public String getCategoryDescription(Integer categoryID)
	{
		String desc = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ResourceCategories WHERE ID=?;");
			stmt.setInt(1, categoryID);
			
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
	public void removeCategory(Integer categoryID)
	{
		String name = getCategoryName(categoryID);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM ResourceCategories WHERE ID=?;");
			stmt.setInt(1, categoryID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "ResourceCategories " + name + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Save a new name of a category from the database
	public void saveName(Integer categoryID, String newName)
	{
		if (this.getCategoryIDList().contains(categoryID))
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE ResourceCategories SET Name=? Where ID=?;");
				stmt.setString(1, newName.substring(0, 1).toUpperCase() + newName.substring(1, newName.length()));
				stmt.setInt(2, categoryID);
				
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Save a new description of a category from the database
	public void saveDescription(Integer categoryID, String newDescription)
	{
		if (this.getCategoryIDList().contains(categoryID))
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE ResourceCategories SET Description=? Where ID=?;");
				stmt.setString(1, newDescription);
				stmt.setInt(2, categoryID);
				
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Get a list of productcategory-id's from the database
	public ArrayList<Integer> getCategoryIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ResourceCategories;");
			
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
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ResourceCategories;");
			
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
	
	public Integer getCooldown(Integer categoryID)
	{
		Integer cooldown = 900;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ResourceCategories WHERE ID=?;");
			stmt.setInt(1, categoryID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				cooldown = results.getInt("BlockCooldown");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return cooldown;
	}
	
	public ArrayList<Integer> getStandardResourceList(Integer categoryID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ResourceCategories WHERE ID=?;");
			stmt.setInt(1, categoryID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				String resources = results.getString("StandardItemTypeIDList");
				String[] split = resources.split(", ");
				for (String resourceString : split)
				{
					Integer resourceID = Integer.valueOf(resourceString);
					if (product.getIDList(true, null, false).contains(resourceID))
					{
						list.add(resourceID);
					}
				}
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
}
