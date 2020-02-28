package Properties;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Handlers.StorageEvent;
import Main.Main;
import Products.Product;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Towns.Town;
import Traits.Shopkeeper;
import Users.User;
import Users.offlineUser;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class Property 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	Street street = new Street();
	Town town = new Town();
	offlineUser user = new offlineUser();
	PropertyCategory cats = new PropertyCategory();
	WorldGuard worldguard = new WorldGuard();
	
	//Save a property to the database
	public void saveProperty(String name, Integer streetID, Integer streetnumber, Integer income, Integer price, Integer categoryID, Integer contribution)
	{
		String cityName = null;
		
		//Check if the street exists
		if (street.checkStreetbyID(streetID) == true)
		{
			cityName = town.getTownName(street.getTownID(streetID));
		} else
		{
			return;
		}
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Property(Name, StreetID, PropertyNumber, Income, Price, CategoryID, Contribution) VALUES(?, ?, ?, ?, ?, ?, ?);");
			stmt.setString(1, name);
			stmt.setInt(2, streetID);
			stmt.setInt(3, streetnumber);
			stmt.setInt(4, income);
			stmt.setInt(5, price);
			stmt.setInt(6, categoryID);
			stmt.setInt(7, contribution);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New property " + name + " in city " + cityName + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the id of a property from the database
	public Integer getPropertyID(String propertyName, Integer streetID, Integer streetNumber)
	{
		Integer ID = null;
		
		if (street.checkStreetbyID(streetID) == true)
		{
			try
			{
				//prepare the query to retrieve the id of a house
				PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE Name=? AND StreetID=? AND PropertyNumber=?;");	
				stmt.setString(1, propertyName);
				stmt.setInt(2, streetID);
				stmt.setInt(3, streetNumber);
				//Execute the query
				ResultSet results = stmt.executeQuery();
				if (results.next())
				{
					ID = results.getInt("ID");
				}
			} catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return ID;
	}
	
	//Get the name of a property from the database
	public String getPropertyName(Integer propertyID)
	{
		String name = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
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
	
	//Check if the given streetnumber already exists. (Checks properties as well as houses)
	public boolean checkStreetNumber(Integer streetNumber, Integer streetID)
	{
		boolean unique = true;
			
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt1 = main.getConnection().prepareStatement("Select * From House WHERE HouseNumber=? AND StreetID=?;");	
			PreparedStatement stmt2 = main.getConnection().prepareStatement("Select * From Property WHERE PropertyNumber=? AND StreetID=?;");	

			stmt1.setInt(1, streetNumber);
			stmt1.setInt(2, streetID);
			stmt2.setInt(1, streetNumber);
			stmt2.setInt(2, streetID);
				
			//Execute the query
			ResultSet results1 = stmt1.executeQuery();
			ResultSet results2 = stmt2.executeQuery();
			if (results1.next() || results2.next())
			{
				unique = false;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
			
		return unique;
	}
	
	//Get the house id by location from the database
	public Integer getPropertyIDbyLocation(Integer streetID, Integer streetNumber)
	{
		Integer id = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE StreetID=? AND PropertyNumber=?;");	
			stmt.setInt(1, streetID);
			stmt.setInt(2, streetNumber);
			
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("ID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Save the name of a property to the database
	public void savePropertyName(Integer ID, String name)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET Name=? Where ID=?;");
			stmt.setString(1, name);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get all information of a property by ID
	public ResultSet getPropertyData(Integer propertyID)
	{
		ResultSet results = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			results = stmt.executeQuery();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return results;
	}
	
	//Get the street ID of a property from the database
	public Integer getStreetID(Integer propertyID)
	{
		Integer id = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("StreetID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Save the street ID of a property from the database
	public void saveStreetID(Integer ID, Integer streetID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET StreetID=? Where ID=?;");
			stmt.setInt(1, streetID);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the streetnumber of a property from the database
	public Integer getStreetNumber(Integer propertyID)
	{
		Integer number = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				number = results.getInt("PropertyNumber");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return number;
	}
	
	//Save the streetnumber of a property from the database
	public void saveStreetNumber(Integer ID, Integer streetNumber)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET PropertyNumber=? Where ID=?;");
			stmt.setInt(1, streetNumber);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the level of a property from the Database
	public Integer getLevel(Integer propertyID)
	{
		Integer level = null;
		
		try
		{
			//prepare the query to retrieve the level of a property
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				level = results.getInt("Level");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return level;
	}
	
	//Update the level of a property with a certain amount of coins
	public void saveLevel(Integer ID, Integer levelAmount)
	{
		Integer oldLevel = getLevel(ID);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET Level=? Where ID=?;");
			stmt.setInt(1, levelAmount);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated the property level of " + getPropertyName(ID) + " from " + oldLevel + " to " + levelAmount);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add a level to the level of a property
	public void addLevel(Integer ID, Integer levelAmount)
	{
		//Get the level before the addition of the amount specified in the method
		Integer OldLevel = getLevel(ID);
		//Add the old level with the amount
		Integer NewLevel = (OldLevel + levelAmount);
		
		//Save the new level to the database
		saveLevel(ID, NewLevel);
	}
	
	//Remove a level from a property
	public void removeLevel(Integer ID, Integer levelAmount)
	{
		//Get the level before substraction the amount specified in the method
		Integer OldLevel = getLevel(ID);
		//Substract the amount of the old level
		Integer NewLevel = (OldLevel - levelAmount);
		
		if (NewLevel < 0)
		{
			NewLevel = 0;
		}
		//Save the new level to the database
		saveLevel(ID, NewLevel);
	}
	
	//Get the income of a property from the Database
	public Integer getIncome(Integer propertyID)
	{
		Integer level = null;
		
		try
		{
			//prepare the query to retrieve the income of a property
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				level = results.getInt("Income");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return level;
	}
	
	//Update the income of a property with a certain amount of coins
	public void saveIncome(Integer ID, Integer income)
	{
		Integer oldIncome = getIncome(ID);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET Income=? Where ID=?;");
			stmt.setInt(1, income);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated the property income of " + getPropertyName(ID) + " from " + oldIncome + " to " + income);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the owner of a property
	public Integer getPropertyOwnerID(Integer propertyID)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve the id of a property
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				ID = results.getInt("OwnerID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ID;
	}
	
	//Set the owner of a property
	public void savePropertyOwner(Integer propertyID, User user)
	{
		Integer userID = user.getID();
		String userName = user.getUsername();
		String propertyName = this.getPropertyName(propertyID);
		Integer income = this.getIncome(propertyID);
		
		//Add 1 to the users property-amount
		user.addPropertyAmount(false, 1);
		user.addIncome(income);
		
		if (this.getPropertyOwnerID(propertyID) == 0)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET OwnerID=? WHERE ID=?;");
				stmt.setInt(1, userID);
				stmt.setInt(2, propertyID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New owner for property " + propertyName + " is " + userName + " and has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Remove the owner of a property
	public void RemovePropertyOwner(Integer propertyID)
	{
		Integer userID = this.getPropertyOwnerID(propertyID);
		Integer income = this.getIncome(propertyID);
		UUID uuid = this.user.getUUIDbyID(userID);
		this.user.removePropertyAmount(uuid, 1);
		this.user.removeIncome(uuid, income);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET OwnerID=null WHERE ID=?;");
			stmt.setInt(1, propertyID);
			
			stmt.executeUpdate();
			//Send the console a message of the update
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Removed the owner of property " + this.getPropertyName(propertyID) + " and is succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Remove a property from the database
	public void removePropertybyID(Integer propertyID)
	{
		String propertyName = this.getPropertyName(propertyID);
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Property WHERE ID=?;");
			stmt.setInt(1, propertyID);
			
			stmt.executeUpdate();
			//Send the console a message if the spawnpoint belongs to a city
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Property " + propertyName + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the category ID of a property from the Database
	public Integer getCategoryID(Integer propertyID)
	{
		Integer id = null;
		
		try
		{
			//prepare the query to retrieve the income of a property
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
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
	
	//Update the income of a property with a certain amount of coins
	public void saveCategoryID(Integer propertyID, Integer categoryID)
	{
		String oldCategory = cats.getCategoryName(this.getCategoryID(propertyID));
		String newCategory = cats.getCategoryName(categoryID);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET CategoryID=? Where ID=?;");
			stmt.setInt(1, categoryID);
			stmt.setInt(2, propertyID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated the property category of " + getPropertyName(propertyID) + " from " + oldCategory + " to " + newCategory);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<Integer> getIDList(Integer amount, Integer townID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id's of all properties
			PreparedStatement stmt = null;
			if (amount == null)
			{
				stmt = main.getConnection().prepareStatement("Select * From Property;");	
			} else
			{
				stmt = main.getConnection().prepareStatement("Select * From Property LIMIT ?;");	
				stmt.setInt(1, amount);
			}
			//Execute the query
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				Integer propertyID = results.getInt("ID");
				if (townID != null)
				{
					if (street.getTownID(this.getStreetID(propertyID)) == townID)
					{
						list.add(results.getInt("ID"));
					}
				} else
				{
					list.add(results.getInt("ID"));
				}
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//Save a sub-region of a property
	public void savePropertyPart(Integer propertyID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO PropertyRegion(PropertyID) VALUES(?);");
			stmt.setInt(1, propertyID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New part of property " + getPropertyName(propertyID) + " has succesfully been saved to the Database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get a list of sub-regions of a property
	public ArrayList<Integer> getPropertyPartList(Integer propertyID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From PropertyRegion WHERE PropertyID=?;");	
			stmt.setInt(1, propertyID);
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
	
	//Chect if a part-id already exists in the database
	public boolean checkPartID(Integer propertyID, Integer PartID)
	{
		boolean exist = false;
		
		if (getPropertyPartList(propertyID).contains(PartID))
		{
			exist = true;
		}
		
		return exist;
	}
	
	//Remove a property part from the database
	public void removePropertyPart(Integer propertyID, Integer PartID)
	{
		try
		{
			//prepare the query to delete a part-region
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM PropertyRegion WHERE SubID=? AND PropertyID=?;");	
			stmt.setInt(1, PartID);
			stmt.setInt(2, propertyID);
			//Execute the query
			stmt.executeUpdate();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Get the price of a property
	public Integer getPropertyPrice(Integer propertyID)
	{
		Integer price = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
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
	
	//Save the price of a property
	public void savePrice(Integer propertyID, Integer Price)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET Price=? WHERE ID=?;");
			stmt.setInt(1, Price);
			stmt.setInt(2, propertyID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Edited the price of property " + this.getPropertyData(propertyID).getString("Name") + " to " + Price + " and is succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the contribution of a property from the database
	public Integer getContribution(Integer propertyID)
	{
		Integer contribution = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				contribution = results.getInt("Contribution");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return contribution;
	}
	
	//Set the contribution of a property 
	public void saveContribution(Integer propertyID, Integer contribution)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET Contribution=? WHERE ID=?;");
			stmt.setInt(1, contribution);
			stmt.setInt(2, propertyID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Edited the contribution of property " + this.getPropertyData(propertyID).getString("Name") + " to " + contribution + " and is succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void sellProperty(User user, Integer propertyID)
	{
		UUID uuid = user.getUUID();
		Integer price = this.getPropertyPrice(propertyID);
		this.RemovePropertyOwner(propertyID);
		user.addCoins((price/2));
		RegionManager manager = worldguard.getRegionManager(user.getPlayer().getWorld());
		this.removeRegionOwner(user.getPlayer(), propertyID, manager);
		
		String propertyName = this.getPropertyName(propertyID);
		user.getPlayer().sendMessage(ColorOptions.messageachievement + "-You succesfully sold " + propertyName + " on the " + street.getStreetName(this.getStreetID(propertyID)) + " with streetnumber " + this.getStreetNumber(propertyID) + " in town " + town.getTownName(street.getTownID(this.getStreetID(propertyID))));
		user.getPlayer().sendMessage(ColorOptions.messageachievement + "-You received " + (price/2) + ColorOptions.coinStats + " coins");
	}
	
	public ArrayList<Integer> getIDListbyCategory(String propertyCategory, Integer townID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		PropertyCategory category = new PropertyCategory();
		
		for (Integer propertyID : this.getIDList(null, townID))
		{
			String catName = category.getCategoryName(this.getCategoryID(propertyID));
			if (catName.equalsIgnoreCase(propertyCategory))
			{
				list.add(propertyID);
			}
		}
		
		return list;
	}
	
	public String getProductCategorybyProperty(Integer categoryID)
	{
		PropertyCategory propertyCategory = new PropertyCategory();
		String category = null;
		
		String propertyCat = propertyCategory.getCategoryName(categoryID);
		
		switch(propertyCat.toLowerCase())
		{
		case "weaponry": category = "swords";
		break;
		case "armory": category = "armor";
		break;
		case "archery": category = "bows";
		break;
		case "jewelery": category = "jewelery";
		break;
		case "fishery": category = "fish";
		break;
		case "butchery": category = "meat";
		break;
		case "bakery": category = "baked-goods";
		break;
		case "grocery": category = "vegetables";
		break;
		case "furnitury": category = "furniture";
		break;
		case "witchery": category = "magic";
		break;
		}
		
		return category;
	}
	
	public void removeRegionOwner(Player player, Integer propertyID, RegionManager manager)
	{
		for (ProtectedRegion region : manager.getRegions().values())
		{
			if (region.getId().contains("property"))
			{
				String[] split = region.getId().split("_");
				Integer ID = null;
				if (split[1].contains(","))
				{
					String[] splitsub = split[1].split(",");
					ID = Integer.valueOf(splitsub[0]);
				} else
				{
					ID = Integer.valueOf(split[1]);
				}
				if (propertyID == ID)
				{
					region.getOwners().removePlayer(player.getName());
					region.getMembers().clear();
					break;
				}
			}
		}
	}
	
	public void addRegionOwner(Player player, Integer propertyID, RegionManager manager)
	{
		for (ProtectedRegion region : manager.getRegions().values())
		{
			if (region.getId().contains("property"))
			{
				String[] split = region.getId().split("_");
				Integer ID = null;
				if (split[1].contains(","))
				{
					String[] splitsub = split[1].split(",");
					ID = Integer.valueOf(splitsub[0]);
				} else
				{
					ID = Integer.valueOf(split[1]);
				}
				if (propertyID == ID)
				{
					region.getOwners().addPlayer(player.getName());
					break;
				}
			}
		}
	}
	
	public Integer getNPCID(Integer propertyID)
	{
		Integer npcID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				npcID = results.getInt("npcID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return npcID;
	}
	
	public Integer getPropertyIDbyNPCID(Integer npcID)
	{
		Integer propertyID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE npcID=?;");	
			stmt.setInt(1, npcID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				propertyID = results.getInt("ID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}

		return propertyID;
	}
	
	public void saveNPCID(Integer propertyID, Integer npcID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET npcID=? WHERE ID=?;");
			stmt.setInt(1, npcID);
			stmt.setInt(2, propertyID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Changed the NPC id of property with ID " + propertyID + " to " + npcID + " in the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Save the spawnpoint id of the property to the database
	public void savePropertySpawnPoint(Integer propertyID, Integer spawnpointID)
	{
		Integer propertyspawnID = this.getPropertySpawnPoint(propertyID);
		
		if (propertyspawnID == 0)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Property SET SpawnID=? WHERE ID=?;");
				stmt.setInt(1, spawnpointID);
				stmt.setInt(2, propertyID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New property-spawnoint for property with ID " + propertyID + " has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	//Remove the spawnpoint of a property from the database
	public void removePropertySpawnPoint(Integer propertyID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE property SET SpawnID=null WHERE ID=?;");
			stmt.setInt(1, propertyID);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Integer getPropertySpawnPoint(Integer propertyID)
	{
		Integer spawnpoint = null;
		
		try
		{
			//prepare the query to retrieve the id of a property spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Property WHERE ID=?;");	
			stmt.setInt(1, propertyID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				spawnpoint = results.getInt("SpawnID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return spawnpoint;
	}
	
	public void purgePropertyOwner(Integer propertyID)
	{
		RegionManager manager = this.worldguard.getRegionManager(Bukkit.getWorld("world"));
		ProtectedRegion region = manager.getRegion("property_" + propertyID);
		if (region != null)
		{
			region.getOwners().clear();
			region.getMembers().clear();
			
//			List<Integer> partList = this.getPropertyPartList(propertyID);
//			for (Integer partID : partList)
//			{
//				try
//				{
//					manager.removeRegion("property_" + propertyID + "," + partID);
//					this.removePropertyPart(propertyID, partID);
//				} catch (Exception ex)
//				{
//					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Error when removing child-region of property " + propertyID);
//					ex.printStackTrace();
//				}
//			}
			Integer ownerID = this.getPropertyOwnerID(propertyID);
			if (ownerID != null && ownerID != 0)
			{
				String username = this.user.getUserName(this.user.getUUIDbyID(ownerID));
				region.getMembers().addPlayer(username);
				Bukkit.getConsoleSender().sendMessage("Succesfully purged the members and owners of the regions of a property with ID " + propertyID + ", current owner added back");
			} else
			{
				Bukkit.getConsoleSender().sendMessage("Succesfully purged the members and owners of the regions of a property with ID " + propertyID + ", no current owner found");
			}
		} else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Couldn't purge the regions of a property with ID " + propertyID + ", no regions found!");
		}
		
		
		this.tryCreateShopkeeper(propertyID);
	}
	
	public void tryCreateShopkeeper(Integer propertyID)
	{
		SpawnPoint spawnpoint = new SpawnPoint();
		Integer spawnpointID = this.getPropertySpawnPoint(propertyID);
		Integer shopkeeperID = this.getNPCID(propertyID);
		Location spawnpointLocation = null;
		
		if (spawnpointID == null)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "No spawnpoint could be found for property " + propertyID + ", no shopkeeper could be spawned!");
			return;
		}
		spawnpointLocation = spawnpoint.getSpawnPointLocation(spawnpointID);
		if (spawnpointLocation == null)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "No spawnpoint-location could be found for spawnpoint " + spawnpointID + ", no shopkeeper could be spawned!");
			return;
		}
		if (shopkeeperID != null)
		{
			NPC shopkeeper = CitizensAPI.getNPCRegistry().getById(shopkeeperID);
			
			if (shopkeeper != null)
			{
				if (!shopkeeper.isSpawned())
				{
					shopkeeper.spawn(spawnpointLocation);
				}
				Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Property " + propertyID + " already has a registered shopkeeper. spawned the shopkeeper if it wasn't already");
				return;
			}
		}
		
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "Shopkeeper");
		npc.setProtected(false);
		npc.addTrait(Shopkeeper.class);
		try
		{
			this.saveNPCID(propertyID, npc.getId());
		} catch (Exception ex)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Something went wrong when saving the id of a new shopkeeper for property " + propertyID + ". Destroying shopkeeper!");
			ex.printStackTrace();
			npc.destroy();
			return;
		}
		
		npc.spawn(spawnpointLocation);
		Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully created and spawned a shopkeeper (" + npc.getId() + ") for property " + propertyID);
	}
	
	public void purgePropertyNPC(CommandSender sender)
	{
		SpawnPoint spawnpoint = new SpawnPoint();
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		List<NPC> propertyList = new ArrayList<NPC>();
		for (Integer propertyID : this.getIDList(null, null))
		{
			Integer npcID = this.getNPCID(propertyID);
			
			if (npcID != null)
			{
				NPC npc = registry.getById(npcID);
				if (npc != null)
				{
					propertyList.add(npc);
				}
			}
		}
		
		Iterable<NPC> list = registry.sorted();
		List<Entity> removable = new ArrayList<Entity>();
		for (Entity entity : Bukkit.getWorld("world").getEntities())
		{
			if (registry.isNPC(entity))
			{
				NPC npc = registry.getNPC(entity);
				if (!propertyList.contains(npc) && npc.getName().equalsIgnoreCase("shopkeeper"))
				{
					String name = npc.getName();
					npc.destroy();
					Bukkit.getConsoleSender().sendMessage("Purged a NPC with name " + name + " as it is not a property-npc");
				} else if (propertyList.contains(npc) && npc.getName().equalsIgnoreCase("shopkeeper"))
				{
					//Updating the shopkeeper by despawning it, adding latest trait class and spawning it afterwards
					Integer propertyID = null;
					Integer spawnpointID = null;
					Location loc = null;
					try
					{
						propertyID = this.getPropertyIDbyNPCID(npc.getId());
					} catch (Exception ex)
					{
						Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "No propertyID could be found for shopkeeper " + npc.getId());
						ex.printStackTrace();
					}
					try
					{
						spawnpointID = this.getPropertySpawnPoint(propertyID);
						try
						{
							loc = spawnpoint.getSpawnPointLocation(spawnpointID);
						} catch (Exception ex)
						{
							Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "No location could be found for shopkeeper " + npc.getId() + " from property " + propertyID + " with spawnpointID " + spawnpointID);
							ex.printStackTrace();
						}
					} catch (Exception ex)
					{
						Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "No spawnpointID could be found for shopkeeper " + npc.getId() + " from property " + propertyID);
						ex.printStackTrace();
					}
					if (loc != null)
					{
						npc.despawn();
					}
					npc.setProtected(false);
					npc.addTrait(Shopkeeper.class);
					if (loc != null)
					{
						npc.spawn(loc);
					}
				}
//				else if (npc.hasTrait(Shopkeeper.class))
//				{
//					npc.removeTrait(Shopkeeper.class);
//				}
			} else if (entity.getName().equalsIgnoreCase("shopkeeper"))
			{
				removable.add(entity);
				Bukkit.getConsoleSender().sendMessage("Purged an Entity with name shopkeeper as it is not a property-npc");
			}
		}
		for (Entity rem : removable)
		{
			rem.remove();
		}
		
		if (sender != null)
		{
			sender.sendMessage(ColorOptions.message + "Purged all shopkeepers for all properties!");
		}
	}
	
	public List<Chest> getChestFromRegion(String structureType, Integer structureID)
	{
		List<Chest> chest = new ArrayList<Chest>();
		World world = Bukkit.getWorld("world");
        RegionManager manager = worldguard.getWorldGuard().getGlobalRegionManager().get(world);
        ProtectedRegion region = manager.getRegion("property" + "_" + structureID);
        List<ProtectedRegion> regions = worldguard.getTotalRegions(region, manager);
        if (region != null && !regions.isEmpty())
        {
        	for (ProtectedRegion regionit : regions)
        	{
        		CuboidRegion curegion = new CuboidRegion(BukkitUtil.getLocalWorld(world), regionit.getMinimumPoint(), regionit.getMaximumPoint());
        		for (BlockVector blockv : curegion) 
        		{
        		    Block block = BukkitUtil.toBlock(new BlockWorldVector(BukkitUtil.getLocalWorld(world), blockv));
        		    if (block.getType() == Material.CHEST)
        		    {
        		    	chest.add((Chest) block.getState());
        		    }
        		}
        	}
        } else
        if (region != null)
        {
    		CuboidRegion curegion = new CuboidRegion(BukkitUtil.getLocalWorld(world), region.getMinimumPoint(), region.getMaximumPoint());
    		for (BlockVector blockv : curegion) 
    		{
    		    Block block = BukkitUtil.toBlock(new BlockWorldVector(BukkitUtil.getLocalWorld(world), blockv));
    		    if (block.getType() == Material.CHEST)
    		    {
    		    	chest.add((Chest) block.getState());
    		    }
    		}
        }
		
		return chest;
	}
	
	public void fillWarehouse(Integer propertyID, List<ItemStack> items)
	{
		Product product = new Product();
		List<Chest> chests = getChestFromRegion("property", propertyID);
		HashMap<Chest, Integer> mappedChests = new HashMap<Chest, Integer>();
		
		for (Chest chest : chests)
		{
			Inventory chestInv = chest.getBlockInventory();
			ItemStack item = chestInv.getItem(0);
			
			if (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			{
				String displayName = item.getItemMeta().getDisplayName();
				Integer productID = product.getProductIDbyDisplayName(displayName, false);
				if (productID != null)
				{
					mappedChests.put(chest, product.getCategoryID(productID, false));
				}
			} else
			{
				mappedChests.put(chest, 99);
			}
		}
		
		
		for (ItemStack item : items)
		{
			Integer productID = null;
			Integer categoryID = null;
			Chest chest = null;
			
			if (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			{
				String displayName = item.getItemMeta().getDisplayName();
				productID = product.getProductIDbyDisplayName(displayName, false);
				categoryID = product.getCategoryID(productID, false);
				
				chest = getLoopChest(propertyID, mappedChests, categoryID, false);
				
				if (chest == null)
				{
					chest = getLoopChest(propertyID, mappedChests, categoryID, true);
					Inventory chestInv = chest.getBlockInventory();
					chestInv.addItem(item);
				} else
				{
					Inventory chestInv = chest.getBlockInventory();
					chestInv.addItem(item);
				}
			}
		}
		if (getFullChest(propertyID) != null)
		{
			StorageEvent storage = new StorageEvent(propertyID, getFullChest(propertyID));
			Bukkit.getPluginManager().callEvent(storage);
		}
	}
	
	public Chest getFullChest(Integer propertyID)
	{
		Chest full = null;
		
		List<Chest> chests = getChestFromRegion("property", propertyID);
		for (Chest chest : chests)
		{
			if (chest.getInventory().firstEmpty() == -1)
			{
				full = chest;
				break;
			}
		}
		
		return full;
	}
	
	public Chest getLoopChest(Integer propertyID, HashMap<Chest, Integer> mappedChests, Integer categoryID, boolean dontFilter)
	{
		Chest chest = null;
		
		for (Chest loopChest : mappedChests.keySet())
		{
			Inventory chestInv = loopChest.getBlockInventory();
			if (chestInv.firstEmpty() != -1)
			{
				if (dontFilter)
				{
					chest = loopChest;
					break;
				}
				if (mappedChests.get(loopChest) != 99 && categoryID != null)
				{
					if (mappedChests.get(loopChest) == categoryID)
					{
						chest = loopChest;
						break;
					} else
					{
						continue;
					}
				} else
				{
					if (categoryID != null)
					{
						continue;
					} else
					{
						chest = loopChest;
					}
				}
			} else
			{
				if (!this.cats.getCategoryName(this.getCategoryID(propertyID)).equalsIgnoreCase("warehouse"))
				{
					StorageEvent storage = new StorageEvent(propertyID, loopChest);
					Bukkit.getPluginManager().callEvent(storage);
					continue;
				}
			}
		}
		
		return chest;
	}
}
