package Rooms;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Main.Main;
import Properties.Property;
import Streets.Street;
import Towns.Town;
import Users.User;
import Users.offlineUser;

public class Room 
{
	WorldGuard worldguard = new WorldGuard();
	Town town = new Town();
	Street street = new Street();
	Main main = Main.getPlugin(Main.class);
	offlineUser user = new offlineUser();
	Property property = new Property();
	
	public void saveRoom(Integer propertyID, Integer roomNumber, Integer price)
	{
		if (checkRoom(propertyID, roomNumber) == false)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Room(PropertyID, roomNumber, Price) VALUES(?, ?, ?);");
				stmt.setInt(1, propertyID);
				stmt.setInt(2, roomNumber);
				stmt.setInt(3, price);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New room in tavern with ID " + propertyID + " with roomNumber " + roomNumber + " has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean checkRoom(Integer propertyID, Integer roomNumber)
	{
		boolean exist = false;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room WHERE PropertyID=? AND roomNumber=?;");	
			stmt.setInt(1, propertyID);
			stmt.setInt(2, roomNumber);
			
			//Execute the query
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
	
	public Integer getRoomID(Integer propertyID, Integer roomNumber)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room WHERE propertyID=? AND roomNumber=?;");	
			stmt.setInt(1, propertyID);
			stmt.setInt(2, roomNumber);
			
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
	
	public Integer getRoomIDbyOwner(UUID uuid)
	{
		Integer ID = null;
		Integer userID = this.user.getUserID(uuid);
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room WHERE OwnerID=?;");	
			stmt.setInt(1, userID);
			
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
	
	public void removeRoom(Integer roomID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Room WHERE ID=?;");
			stmt.setInt(1, roomID);
			
			stmt.executeUpdate();
			//Send the console a message if the spawnpoint belongs to a city
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Room with ID " + roomID + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Integer getPropertyID(Integer roomID)
	{
		Integer propertyID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room WHERE ID=?;");	
			stmt.setInt(1, roomID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				propertyID = results.getInt("PropertyID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return propertyID;
	}
	
	public Integer getOwnerID(Integer roomID)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room WHERE ID=?;");	
			stmt.setInt(1, roomID);
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
	
	public void saveOwnerID(Integer roomID, UUID uuid)
	{
		Integer userID = this.user.getUserID(uuid);
		if (getOwnerID(roomID) == 0)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Room SET OwnerID=? WHERE ID=?;");
				stmt.setInt(1, userID);
				stmt.setInt(2, roomID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New owner for room with ID " + roomID + " is user with ID " + userID + " and has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void removeOwnerID(Integer roomID)
	{
		Integer userID = getOwnerID(roomID);
		if (userID != 0)
		{
			RegionManager manager = worldguard.getRegionManager(Bukkit.getWorld("world"));
			
			this.removeRegionOwner(this.user.getUUIDbyID(userID), roomID, manager);
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Room SET OwnerID=null WHERE ID=?;");
				stmt.setInt(1, roomID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Removed the owner of room with ID " + roomID + " and it is succesfully saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public Integer getPrice(Integer roomID)
	{
		Integer price = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room WHERE ID=?;");	
			stmt.setInt(1, roomID);
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
	
	//Save the price of a room
	public void savePrice(Integer roomID, Integer Price)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Room SET Price=? WHERE ID=?;");
			stmt.setInt(1, Price);
			stmt.setInt(2, roomID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Edited the price of room with ID " + roomID + " to " + Price + " and is succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Integer getRoomNumber(Integer roomID)
	{
		Integer roomNumber = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room WHERE ID=?;");	
			stmt.setInt(1, roomID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				roomNumber = results.getInt("RoomNumber");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return roomNumber;
	}
	
	public Integer getSpawnPointID(Integer roomID)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room WHERE ID=?;");	
			stmt.setInt(1, roomID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				ID = results.getInt("SpawnID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ID;
	}
	
	public void saveSpawnPointID(Integer roomID, Integer spawnpointID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Room SET SpawnID=? WHERE ID=?;");
			stmt.setInt(1, spawnpointID);
			stmt.setInt(2, roomID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Edited the spawnID of room with ID " + roomID + " to " + spawnpointID + " and is succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void removeRoomSpawnPoint(Integer roomID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Room SET SpawnID=null WHERE ID=?;");
			stmt.setInt(1, roomID);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void saveRoomPart(Integer roomID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO RoomRegion(roomID) VALUES(?);");
			stmt.setInt(1, roomID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New part of room with ID " + roomID + " has succesfully been saved to the Database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<Integer> getRoomPartList(Integer roomID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From RoomRegion WHERE RoomID=?;");	
			stmt.setInt(1, roomID);
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
	
	public boolean checkPartID(Integer roomID, Integer PartID)
	{
		boolean exist = false;
		
		if (getRoomPartList(roomID).contains(PartID))
		{
			exist = true;
		}
		
		return exist;
	}
	
	public void removeRoomPart(Integer roomID, Integer PartID)
	{
		try
		{
			//prepare the query to delete a part-region
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM RoomRegion WHERE SubID=? AND roomID=?;");	
			stmt.setInt(1, PartID);
			stmt.setInt(2, roomID);
			//Execute the query
			stmt.executeUpdate();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<Integer> getRoomIDList(Integer townID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Room;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				Integer roomID = results.getInt("ID");
				if (townID != null)
				{
					if (street.getTownID(property.getStreetID(this.getPropertyID(roomID))) == townID)
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
	
	public void removeRegionOwner(UUID uuid, Integer roomID, RegionManager manager)
	{
		for (ProtectedRegion region : manager.getRegions().values())
		{
			if (region.getId().contains("room"))
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
				if (roomID == ID)
				{
					region.getOwners().removePlayer(this.user.getUserName(uuid));
					region.getMembers().clear();
					break;
				}
			}
		}
	}
	
	public void addRegionOwner(Player player, Integer roomID, RegionManager manager)
	{
		for (ProtectedRegion region : manager.getRegions().values())
		{
			if (region.getId().contains("room"))
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
				if (roomID == ID)
				{
					region.getOwners().addPlayer(player.getName());
					break;
				}
			}
		}
	}
	
	public void sellRoom(User user, Integer roomID)
	{
		UUID uuid = user.getUUID();
		
		this.removeOwnerID(roomID);
		this.removeRegionOwner(user.getPlayer(), roomID, worldguard.getRegionManager(Bukkit.getWorld("world")));
		this.purgeRoomOwner(roomID);
		user.removeRoomAmount(false, 1);
		user.removeRentTime();
		user.getPlayer().sendMessage(ColorOptions.messageachievement + "-You stopped renting a room in the " + property.getPropertyName(this.getPropertyID(roomID)) + " on the " + street.getStreetName(property.getStreetID(this.getPropertyID(roomID))) + " in town " + town.getTownName(street.getTownID(property.getStreetID(this.getPropertyID(roomID)))));
		
		if (this.getSpawnPointID(roomID) != 0 && user.getSpawnpointID() == this.getSpawnPointID(roomID))
		{
			user.removeSpawnpoint();
			user.getPlayer().sendMessage(ColorOptions.error + "Your personal spawnpoint has been set to default");
		}
	}
	
	public void removeRegionOwner(Player player, Integer houseID, RegionManager manager)
	{
		for (ProtectedRegion region : manager.getRegions().values())
		{
			if (region.getId().contains("room"))
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
	
	public void purgeRoomOwner(Integer roomID)
	{
		ProtectedRegion region = worldguard.getRegionManager(Bukkit.getWorld("world")).getRegion("room_" + roomID);
		if (region != null)
		{
			region.setFlag(DefaultFlag.CHEST_ACCESS, State.ALLOW);
			region.getOwners().clear();
			region.getMembers().clear();
			Integer ownerID = this.getOwnerID(roomID);
			if (ownerID != null && ownerID != 0)
			{
				String username = this.user.getUserName(this.user.getUUIDbyID(ownerID));
				region.getMembers().addPlayer(username);
				Bukkit.getConsoleSender().sendMessage("Succesfully purged the members and owners of the regions of a room with ID " + roomID + ", current owner added back");
			} else
			{
				Bukkit.getConsoleSender().sendMessage("Succesfully purged the members and owners of the regions of a room with ID " + roomID + ", no current owner found");
			}
		} else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Couldn't purge the regions of a room with ID " + roomID + ", no regions found!");
		}
	}
	
}
