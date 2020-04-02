package Resources;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;

import DataManager.Worldguard;
import Handlers.ColorOptions;
import Main.Main;
import Products.Product;
import Properties.Property;
import Properties.PropertyCategory;
import Streets.Street;
import Towns.Town;

public class ResourceProperty extends Property
{
	Main main = Main.getPlugin(Main.class);
	Product product = new Product();
	ResourceCategory resourceCategory = new ResourceCategory();
	Street street = new Street();
	YmlFile file = new YmlFile();
	Town town = new Town();
	PropertyCategory cats = new PropertyCategory();
	
	public void saveResourceProperty(Integer propertyID, Integer resourceCategoryID)
	{
		if (this.getIDList(null, null).contains(propertyID))
		{
			if (resourceCategory.getCategoryIDList().contains(resourceCategoryID))
			{
				Integer cooldown = resourceCategory.getCooldown(resourceCategoryID);
				try 
				{
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO ResourceProperties(ID, ResourceCategoryID, BlockCooldown) VALUES(?, ?, ?);");
					stmt.setInt(1, propertyID);
					stmt.setInt(2, resourceCategoryID);
					stmt.setInt(3, cooldown);
					
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New resource-property with propertyID " + propertyID + " has succesfully been saved to the database!");
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void removeResourceProperty(Integer propertyID)
	{
		if (this.getResourcePropertyIDList(false, null).contains(propertyID))
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM ResourceProperties WHERE ID=?;");
				stmt.setInt(1, propertyID);
				
				stmt.executeUpdate();
				//Send the console a message if the spawnpoint belongs to a city
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Resource-Property with propertyID " + propertyID + " has succesfully been removed from the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public Integer getResourceCategory(Integer propertyID)
	{
		Integer categoryID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From ResourceProperties WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				categoryID = results.getInt("ResourceCategoryID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return categoryID;
	}
	
	public ArrayList<Integer> getResourcePropertyIDList(boolean sortByCategory, Integer categoryID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = null;
			
			if (sortByCategory && this.resourceCategory.getCategoryIDList().contains(categoryID))
			{
				stmt = main.getConnection().prepareStatement("Select * From ResourceProperties WHERE ResourceCategoryID=?;");
				stmt.setInt(1, categoryID);
			} else
			{
				stmt = main.getConnection().prepareStatement("Select * From ResourceProperties;");
			}
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
	
//	public ArrayList<Integer> getResourceList(Integer categoryID)
//	{
//		ArrayList<Integer> list = new ArrayList<Integer>();
//		
//		try 
//		{
//			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ResourceProperties WHERE ID=?;");
//			stmt.setInt(1, categoryID);
//			
//			ResultSet results = stmt.executeQuery();
//			if (results.next())
//			{
//				String resources = results.getString("StandardResourceList");
//				String[] split = resources.split(", ");
//				for (String resourceString : split)
//				{
//					Integer resourceID = Integer.valueOf(resourceString);
//					if (product.getIDList().contains(resourceID))
//					{
//						list.add(resourceID);
//					}
//				}
//			}
//		} catch (SQLException e) 
//		{
//			e.printStackTrace();
//		}
//		
//		return list;
//	}
	
	public Integer getCooldown(Integer propertyID)
	{
		Integer cooldown = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From ResourceProperties WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				cooldown = results.getInt("BlockCooldown");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return cooldown;
	}
	
	public ArrayList<String> getProductCategorybyResourceProperty(Integer resourceCategoryID)
	{
		ArrayList<String> categories = new ArrayList<String>();
		
		String propertyCat = resourceCategory.getCategoryName(resourceCategoryID);
		
		switch(propertyCat.toLowerCase())
		{
		case "farm": categories.addAll(Arrays.asList("meat", "farmables"));
		break;
		case "lumber": categories.addAll(Arrays.asList("wood", "farmables"));
		break;
		case "mine": categories.addAll(Arrays.asList("resources"));
		break;
		}
		
		return categories;
	}
	
	public void checkPlayersbeforeChange(String fileName, Integer blockID, Integer seconds)
	{
		Location loc = (Location) file.getBlockValues(fileName, blockID).get("Location");
		RegionManager manager = Worldguard.getRegionManager(loc.getWorld());
		Integer propertyID = Worldguard.getStructureIDbyRegion("property", loc, manager);
		if (this.getResourcePropertyIDList(false, null).contains(propertyID))
		{
			for (Player player : Bukkit.getOnlinePlayers())
			{
				Location location = player.getLocation();
				if (Worldguard.getStructureIDbyRegion("property", location, manager) == propertyID)
				{
					player.sendMessage(ColorOptions.error + "Warning: Blocks from this resource-property will reset in " + seconds + " seconds!");
				}
			}
		}
	}
}
