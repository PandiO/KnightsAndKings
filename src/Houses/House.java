package Houses;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldGuard;
import DataManager.Worldguard;
import Handlers.ColorOptions;
import Main.Main;
import Streets.Street;
import Towns.Town;
import Users.User;
import Users.Users;

public class House 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	Street street = new Street();
	Town town = new Town();
	WorldGuard worldguard = new WorldGuard();
	
	//Save a new house to the database
	public void saveHouse(String name, String streetName, Integer houseNumber, Integer price, Integer CityID)
	{
		Integer streetID = null;
		String cityName = null;
		
		if (street.checkStreet(streetName, CityID) == true)
		{
			streetID = street.getStreetID(streetName, CityID);
			cityName = town.getTownName(street.getTownID(streetID));
		}
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO House(Name, StreetID, HouseNumber, Price) VALUES(?, ?, ?, ?);");
			stmt.setString(1, name);
			stmt.setInt(2, streetID);
			stmt.setInt(3, houseNumber);
			stmt.setInt(4, price);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New house " + name + " in city " + cityName + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the id of a house from the database
	public Integer getHouseID(String houseName, Integer streetID, Integer houseNumber)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE Name=? AND StreetID=? AND HouseNumber=?;");	
			stmt.setString(1, houseName);
			stmt.setInt(2, streetID);
			stmt.setInt(3, houseNumber);
			
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
		
		return ID;
	}
	
	//Get the house id by location from the database
	public Integer getHouseIDbyLocation(Integer streetID, Integer houseNumber)
	{
		Integer id = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE StreetID=? AND HouseNumber=?;");	
			stmt.setInt(1, streetID);
			stmt.setInt(2, houseNumber);
			
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
	
	//Get all information of a house by ID
	public ResultSet getHouseData(Integer houseID)
	{
		ResultSet results = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE ID=?;");	
			stmt.setInt(1, houseID);
			//Execute the query
			results = stmt.executeQuery();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return results;
	}
	
	//Get the name of a house from the database
	public String getHouseName(Integer houseID)
	{
		String name = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE ID=?;");	
			stmt.setInt(1, houseID);
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
	
	//Get a list of all housenames from the database
	public ArrayList<String> getHouseNames()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getString("Name"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
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
	
	public boolean checkHouse(String name)
	{
		boolean exist = false;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE Name=?;");	
			stmt.setString(1, name);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (!results.next())
			{
				exist = true;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return exist;
	}
	
	//Remove a house from the database
	public void removeHousebyName(String houseName, Integer streetID, Integer houseNumber)
	{
		Integer houseID = this.getHouseID(houseName, streetID, houseNumber);
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM House WHERE ID=?;");
			stmt.setInt(1, houseID);
			
			stmt.executeUpdate();
			//Send the console a message if the spawnpoint belongs to a city
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "House " + houseName + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Remove a house from the database
	public void removeHousebyID(Integer houseID)
	{
		String houseName = this.getHouseName(houseID);
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM House WHERE ID=?;");
			stmt.setInt(1, houseID);
			
			stmt.executeUpdate();
			//Send the console a message if the spawnpoint belongs to a city
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "House " + houseName + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the owner of a house
	public Integer getHouseOwnerID(Integer houseID)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE ID=?;");	
			stmt.setInt(1, houseID);
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
	
	//Set the owner of a house
	public void saveHouseOwner(Integer houseID, User user)
	{
		Integer userID = user.getID();
		String userName = user.getUsername();
		String houseName = this.getHouseName(houseID);
		if (this.getHouseOwnerID(houseID) == 0)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE House SET OwnerID=? WHERE ID=?;");
				stmt.setInt(1, userID);
				stmt.setInt(2, houseID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New owner for house " + houseName + " is " + userName + " and has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Remove the owner of a house
	public void RemoveHouseOwner(Integer houseID)
	{
		Integer ownerID = this.getHouseOwnerID(houseID);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE House SET OwnerID=null WHERE ID=?;");
			stmt.setInt(1, houseID);
			
			stmt.executeUpdate();
			//Send the console a message of the update
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Removed the owner of house " + this.getHouseName(houseID) + " and is succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		User user = null;
		UUID uuid = Users.fetchUUIDbyID(ownerID);
		if (Users.getUser(uuid) != null)
		{
			user = Users.getUser(uuid);
		} else
		{
			user = new User(uuid);
		}
		
		user.removeHouseAmount(false, 1);
		
		user.destroy();
	}
	
	//Save the spawnpoint id of the house to the database
	public void saveHouseSpawnPoint(Integer houseID, Integer spawnpointID)
	{
		Integer housespawnID = this.getHouseSpawnPoint(houseID);
		
		if (housespawnID == 0)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE House SET SpawnID=? WHERE ID=?;");
				stmt.setInt(1, spawnpointID);
				stmt.setInt(2, houseID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New house-spawnoint for house with ID " + houseID + " has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	//Remove the spawnpoint of a house from the database
	public void removeHouseSpawnPoint(Integer houseID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE House SET SpawnID=null WHERE ID=?;");
			stmt.setInt(1, houseID);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Integer getHouseSpawnPoint(Integer houseID)
	{
		Integer spawnpoint = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE ID=?;");	
			stmt.setInt(1, houseID);
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
	
	
	//Get the price of a house
	public Integer getHousePrice(Integer houseID)
	{
		Integer price = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE ID=?;");	
			stmt.setInt(1, houseID);
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
	
	//Save the price of a house
	public void savePrice(Integer houseID, Integer Price)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE House SET Price=? WHERE ID=?;");
			stmt.setInt(1, Price);
			stmt.setInt(2, houseID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Edited the price of house " + this.getHouseData(houseID).getString("Name") + " to " + Price + " and is succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Save a sub-gateRegion of a house
	public void saveHousePart(Integer houseID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO HouseRegion(HouseID) VALUES(?);");
			stmt.setInt(1, houseID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New part of house " + getHouseName(houseID) + " has succesfully been saved to the Database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get a list of sub-regions of a house
	public ArrayList<Integer> getHousePartList(Integer houseID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From HouseRegion WHERE HouseID=?;");	
			stmt.setInt(1, houseID);
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
	
	//Return the streetID of the house
	public Integer getStreetID(Integer houseID)
	{
		Integer id = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE ID=?;");	
			stmt.setInt(1, houseID);
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
	
	public Integer getHouseNumber(Integer houseID)
	{
		Integer number = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House WHERE ID=?;");	
			stmt.setInt(1, houseID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				number = results.getInt("HouseNumber");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return number;
	}
	
	//Chect if a part-id already exists in the database
	public boolean checkPartID(Integer houseID, Integer PartID)
	{
		boolean exist = false;
		
		if (getHousePartList(houseID).contains(PartID))
		{
			exist = true;
		}
		
		return exist;
	}
	
	//Remove a house part from the database
	public void removeHousePart(Integer houseID, Integer PartID)
	{
		try
		{
			//prepare the query to delete a part-gateRegion
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM HouseRegion WHERE SubID=? AND HouseID=?;");	
			stmt.setInt(1, PartID);
			stmt.setInt(2, houseID);
			//Execute the query
			stmt.executeUpdate();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<Integer> getHouseIDList(Integer townID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From House;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				Integer houseID = results.getInt("ID");
				if (townID != null)
				{
					if (street.getTownID(this.getStreetID(houseID)) == townID)
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
	
	public void sellHouse(User user, Integer houseID)
	{
		UUID uuid = user.getUUID();
		Integer price = this.getHousePrice(houseID);
		Integer streetID = this.getStreetID(houseID);
		Integer streetNumber = this.getHouseNumber(houseID);
		Integer townID = street.getTownID(streetID);
		this.RemoveHouseOwner(houseID);
		user.addCoins((price/2));
		RegionManager manager = Worldguard.getRegionManager(user.getPlayer().getWorld());
		this.removeRegionOwner(user.getPlayer(), houseID, manager);
		String houseName = this.getHouseName(houseID);
		user.getPlayer().sendMessage(ColorOptions.messageachievement + "-You succesfully sold " + houseName + " on the " + street.getStreetName(streetID) + " with streetnumber " + streetNumber + " in town " + town.getTownName(townID));
		user.getPlayer().sendMessage(ColorOptions.messageachievement + "-You received " + (price/2) + ColorOptions.coinStats + " coins");
		
		if (this.getHouseSpawnPoint(houseID) != 0 && user.getSpawnpointID() == this.getHouseSpawnPoint(houseID))
		{
			user.removeSpawnpoint();
			user.getPlayer().sendMessage(ColorOptions.error + "Your personal spawnpoint has been set to default");
		}
	}
	
	public void removeRegionOwner(Player player, Integer houseID, RegionManager manager)
	{
		for (ProtectedRegion region : manager.getRegions().values())
		{
			if (region.getId().contains("house"))
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
				if (houseID == ID)
				{
					region.getOwners().removePlayer(player.getName());
					region.getMembers().clear();
					break;
				}
			}
		}
	}
	
	public void addRegionOwner(Player player, Integer houseID, RegionManager manager)
	{
		UUID uuid = player.getUniqueId();
		User target = null;
		if (Users.getUser(uuid) != null)
		{
			target = Users.getUser(uuid);
		} else
		{
			target = new User(uuid);
		}
		
		for (ProtectedRegion region : manager.getRegions().values())
		{
			if (region.getId().contains("house"))
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
				if (houseID == ID)
				{
					region.getOwners().addPlayer(player.getName());
					for (UUID frienduuid : target.getFriendList())
					{
						region.getMembers().addPlayer(frienduuid);
					}
					break;
				}
			}
		}
		
		target.destroy();
	}
	
	public void purgeHouseOwner(Integer houseID)
	{
		RegionManager manager = Worldguard.getRegionManager(Bukkit.getWorld("world"));
		ProtectedRegion region = manager.getRegion("house_" + houseID);
		if (region != null)
		{
			region.getOwners().clear();
			region.getMembers().clear();
//			List<Integer> partList = this.getHousePartList(houseID);
//			for (Integer partID : partList)
//			{
//				try
//				{
//					manager.removeRegion("house_" + houseID + "," + partID);
//					this.removeHousePart(houseID, partID);
//				} catch (Exception ex)
//				{
//					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Error when removing child-gateRegion of house " + houseID);
//					ex.printStackTrace();
//				}
//			}
			
			Integer ownerID = this.getHouseOwnerID(houseID);
			if (ownerID != null && ownerID != 0)
			{
				String username = Users.fetchUsernamebyUUID(Users.fetchUUIDbyID(ownerID));
				region.getMembers().addPlayer(username);
				Bukkit.getConsoleSender().sendMessage("Succesfully purged the members and owners of the regions of a house with ID " + houseID + ", current owner added back");
			} else
			{
				Bukkit.getConsoleSender().sendMessage("Succesfully purged the members and owners of the regions of a house with ID " + houseID + ", no current owner found");
			}
		} else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Couldn't purge the regions of a house with ID " + houseID + ", no regions found!");
		}
	}
}
