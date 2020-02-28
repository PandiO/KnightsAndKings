package SpawnPoints;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import Afk.AfkEvents;
import Arenas.Arena;
import Donator.Donator;
import Handlers.ColorOptions;
import Houses.House;
import Main.Main;
import Rooms.Room;
import Teleport.TeleportDelay;
import Titles.Title;
import Towns.Town;
import Users.User;

public class SpawnPoint 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	Title title = new Title();
	Donator donator = new Donator();
	House house = new House();
	Town town = new Town();
	Room room = new Room();
	Arena arena = new Arena();
	
	//Save a spawnpoint with values (required title and donator can be both the name or the id)
	public void saveSpawnPoint(String name, String requiredtitle, Integer price, String requireddonator, String arenaCategory, World world, Double x, Double y, Double z, Float yaw, Float pitch)
	{
		Integer maxTitles = title.getTitleAmount()-1;
		Integer totaldonators = donator.getDonatorRankAmount();
		Integer titleID = null;
		Integer donatorID = null;
		
		//Check if the given value for the requiredtitle is already the ID or the name of the title
		if (requiredtitle.length() <= 2)
		{
			if (Integer.valueOf(requiredtitle) <= maxTitles && Integer.valueOf(requiredtitle) >= 0)
			{
				titleID = Integer.valueOf(requiredtitle);
			}
		} else
		{
			titleID = title.getTitleID(requiredtitle);
		}
		
		//Check if the given value for the requireddonator is already the ID or the name of the donator
		if (requireddonator.length() <= 1)
		{
			if (Integer.valueOf(requireddonator) <= totaldonators && Integer.valueOf(requiredtitle) >= 0)
			{
				donatorID = Integer.valueOf(requireddonator);
			}
		} else
		{
			donatorID = title.getTitleID(requireddonator);
		}
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO SpawnPoint(Name, TitleIDRequired, Price, DonatorIDRequired, World, X, Y, Z, Yaw, Pitch) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, name);
			stmt.setInt(2, titleID);
			stmt.setInt(3, price);
			stmt.setInt(4, donatorID);
			stmt.setString(5, world.getName());
			stmt.setDouble(6, x);
			stmt.setDouble(7, y);
			stmt.setDouble(8, z);
			stmt.setFloat(9, yaw);
			stmt.setFloat(10, pitch);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New location " + name + " has succesfully been saved to the database!");
			if (name.contains("_"))
			{
				String[] split = name.split("_");
				if (split[0].equalsIgnoreCase("house"))
				{
					if (house.getHouseIDList(null).contains(Integer.valueOf(split[1])))
					{
						Integer houseID = Integer.valueOf(split[1]);
						Integer spawnpointID = this.getSpawnPointID(name);
						house.saveHouseSpawnPoint(houseID, spawnpointID);
					}
				} else
				if (split[0].equalsIgnoreCase("arena"))
				{
					if (arena.getArenaIDList(null).contains(Integer.valueOf(split[1])))
					{
						Integer arenaID = Integer.valueOf(split[1]);
						Integer spawnpointID = this.getSpawnPointID(name);
						String category = split[2];
						arena.saveArenaLocation(arenaID, category, spawnpointID);
					}
				}
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<Integer> getSpawnPointList(boolean allowHouses, boolean allowArenas, boolean allowOcelots, boolean allowRooms, boolean allowProperties, boolean allowTreasures)
	{
		ArrayList<Integer> spawnpoints = new ArrayList<Integer>();
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				Integer id = results.getInt("ID");
				String name = results.getString("Name");
				if (name.contains("house"))
				{
					if (allowHouses == true)
					{
						spawnpoints.add(id);
					}
				} else
				if (name.contains("arena"))
				{
					if (allowArenas == true)
					{
						spawnpoints.add(id);
					}
				} else
				if (name.contains("ocelot"))
				{
					if (allowOcelots == true)
					{
						spawnpoints.add(id);
					}
				} else
				if (name.contains("room"))
				{
					if (allowRooms == true)
					{
						spawnpoints.add(id);
					}	
				} else
				if (name.contains("property"))
				{
					if (allowProperties == true)
					{
						spawnpoints.add(id);
					}
				} else
				if (name.contains("treasure"))
				{
					if (allowTreasures == true)
					{
						spawnpoints.add(id);
					}
				} else if (name.contains("new") || name.contains("tutorial") || name.contains("Tutorial") || name.contains("afk"))
				{
					
				} else
				{
					spawnpoints.add(id);
				}
				
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return spawnpoints;
	}
	
	public ArrayList<Integer> getRawIDList()
	{
		ArrayList<Integer> spawnpoints = new ArrayList<Integer>();
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				spawnpoints.add(results.getInt("ID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return spawnpoints;
	}
	
	
	//Get the price of a spawnpoint from the database
	public Integer getSpawnPointPrice(Integer spawnpointID)
	{
		Integer price = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint WHERE ID=?;");
			stmt.setInt(1, spawnpointID);
			
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
	
	//Get the location of a spawn point by name
	public Location getSpawnPointLocation(Integer spawnpointID)
	{
		Location location = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint WHERE ID=?;");
			stmt.setInt(1, spawnpointID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				location = new Location(Bukkit.getWorld(
						results.getString("World")), 
						results.getDouble("X"),
						results.getDouble("Y"),
						results.getDouble("Z"),
						results.getFloat("Yaw"),
						results.getFloat("Pitch")
						);
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return location;
	}
	
	//Get the Name of a spawnpoint by ID
	public String getSpawnPointName(Integer spawnpointID)
	{
		String name = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint WHERE ID=?;");
			stmt.setInt(1, spawnpointID);
			
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
	
	//Save the name of a spawnpoint to the database
	public void saveName(Integer spawnpointID, String newName)
	{
		if (this.checkSpawnPoint(spawnpointID))
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE SpawnPoint SET Name=? Where ID=?;");
				stmt.setString(1, newName);
				stmt.setInt(2, spawnpointID);
				
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Save the location of an existing spawnpoint to the database
	public void saveLocation(Integer spawnpointID, Double X, Double Y, Double Z, Float Yaw, Float Pitch)
	{
		if (this.checkSpawnPoint(spawnpointID))
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE SpawnPoint SET X=?, Y=?, Z=?, Yaw=?, Pitch=? Where ID=?;");
				stmt.setDouble(1, X);
				stmt.setDouble(2, Y);
				stmt.setDouble(3, Z);
				stmt.setFloat(4, Yaw);
				stmt.setFloat(5, Pitch);
				stmt.setInt(6, spawnpointID);
				
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Get the ID of a spawnpoint by name
	public Integer getSpawnPointID(String name)
	{
		Integer ID = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint WHERE Name=?;");
			stmt.setString(1, name);
			
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
	
	//Remove a spawnpoint from the database
	public void removeSpawnPoint(Integer spawnpointID)
	{
		if (checkSpawnPoint(spawnpointID) == true)
		{
			String spawnpointName = getSpawnPointName(spawnpointID);
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM SpawnPoint WHERE ID=?;");
				stmt.setInt(1, spawnpointID);
				
				//Check if the spawnpoint belongs to a city, then remove this connection aswell
				if (checkTownSpawnPointbySpawnPoint(spawnpointID))
				{
					removeTownSpawnPoint(spawnpointID);
				} else
				{
					//If the spawnpoint doesn't belong to a city, send the console a message of the removed spawnpoint
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Spawnpoint " + spawnpointName + " has succesfully been saved to the database!");
				}
				//Remove the spawnpoint from the table after a possible connection between a city has been removed
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Check if a specific spawnpoint exists with the given ID
	public boolean checkSpawnPoint(Integer spawnpointID)
	{
		boolean exist = false;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint WHERE ID=?;");
			stmt.setInt(1, spawnpointID);
			
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
	
	//Remove a connection between a spawnpoint and a city
	public void removeTownSpawnPoint(Integer spawnpointID)
	{
		if (checkTownSpawnPointbySpawnPoint(spawnpointID))
		{
			String townName = town.getTownName(getTownIDbySpawnPoint(spawnpointID));
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM TownSpawnPoint WHERE SpawnPointID=?;");
				stmt.setInt(1, spawnpointID);
				
				stmt.executeUpdate();
				//Send the console a message if the spawnpoint belongs to a city
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Spawnpoint for town " + townName + " has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Save a cityspawnpoint to the database
	public void saveTownSpawnPoint(Integer townID, Integer spawnpointID)
	{
		try
		{
			if (town.checkTown(townID) == true && checkSpawnPoint(spawnpointID) == true)
			{
				String townName = town.getTownName(townID);
				try 
				{
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO TownSpawnPoint(TownID, SpawnPointID) VALUES(?, ?);");
					stmt.setInt(1, townID);
					stmt.setInt(2, spawnpointID);
					
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New town-spawnpoint for town " + townName + " has succesfully been saved to the database!");
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
			} else
			{
				throw new Exception("Error occured when saving a city-spawnpoint: cityID or spawnpointID does not exist!");
			}
		} catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage(e.toString());
		}
	}
	
	//Check if a spawnpoint has a city connected to it
	public boolean checkTownSpawnPointbySpawnPoint(Integer spawnpointID)
	{
		boolean exist = false;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM TownSpawnPoint WHERE SpawnPointID=?;");
			stmt.setInt(1, spawnpointID);
			
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
	
	//Get the id of a city belonging to a spawnpoint id
	public Integer getTownIDbySpawnPoint(Integer spawnpointID)
	{
		Integer townID = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM TownSpawnPoint WHERE SpawnPointID=?;");
			stmt.setInt(1, spawnpointID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				townID = results.getInt("TownID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return townID;
	}
	
	//Get the spawnpoint ID belonging to a city id
	public Integer getSpawnPointIDbyTown(Integer townID)
	{
		Integer spawnpointID = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM TownSpawnPoint WHERE TownID=?;");
			stmt.setInt(1, townID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				spawnpointID = results.getInt("SpawnPointID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return spawnpointID;
	}
	
	//Get the required donatorID for a spawnpoint
	public Integer getRequiredDonatorID(Integer spawnpointID)
	{
		Integer donatorID = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint WHERE ID=?;");
			stmt.setInt(1, spawnpointID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				donatorID = results.getInt("DonatorIDRequired");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return donatorID;
	}
	
	//Get the required titleID for a spawnpoint
	public Integer getRequiredTitleID(Integer spawnpointID)
	{
		Integer titleID = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint WHERE ID=?;");
			stmt.setInt(1, spawnpointID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				titleID = results.getInt("TitleIDRequired");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return titleID;
	}
	
	public ArrayList<Integer> getHouseSpawnPointList()
	{
		ArrayList<Integer> houseSpawnpointID = new ArrayList<Integer>();
		for (Integer houseID : house.getHouseIDList(null))
		{
			if (house.getHouseSpawnPoint(houseID) != 0)
			{
				houseSpawnpointID.add(house.getHouseSpawnPoint(houseID));
			}
		}
		
		return houseSpawnpointID;
	}
	
	public ArrayList<Integer> getRoomSpawnPointList()
	{
		ArrayList<Integer> roomSpawnpointID = new ArrayList<Integer>();
		for (Integer roomID : room.getRoomIDList(null))
		{
			if (room.getSpawnPointID(roomID) != 0)
			{
				roomSpawnpointID.add(room.getSpawnPointID(roomID));
			}
		}
		
		return roomSpawnpointID;
	}
	
	public ArrayList<Integer> getOcelotSpawnPointList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer ID : this.getRawIDList())
		{
			String name = this.getSpawnPointName(ID);
			if (name.contains("ocelot") || name.contains("Ocelot"))
			{
				list.add(ID);
			}
		}
		
		return list;
	}
	
	public ArrayList<Integer> getArenaSpawnPointList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer ID : this.getRawIDList())
		{
			String name = this.getSpawnPointName(ID);
			if (name.contains("arena") || name.contains("Arena"))
			{
				list.add(ID);
			}
		}
		
		return list;
	}
	
	public ArrayList<Integer> getPropertySpawnPointList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer ID : this.getRawIDList())
		{
			String name = this.getSpawnPointName(ID);
			if (name.contains("property") || name.contains("Property"))
			{
				list.add(ID);
			}
		}
		
		return list;
	}
	
	public ArrayList<Integer> getTreasureSpawnPointList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer ID : this.getRawIDList())
		{
			String name = this.getSpawnPointName(ID);
			if (name.contains("treasure") || name.contains("Treasure"))
			{
				list.add(ID);
			}
		}
		
		return list;
	}
	
	public void tryRegularTeleport(User user, String targetLocation)
	{
		Player player = user.getPlayer();
		UUID uuid = user.getUUID();
		Integer titleID = user.getTitleID();
		Integer donatorID = user.getDonatorID();
		Integer gems = user.getGems();
		Bukkit.getConsoleSender().sendMessage("Location-name: " + targetLocation);
		if (this.getSpawnPointID(targetLocation) != null)
		{
			Integer spawnpointID = this.getSpawnPointID(targetLocation);
			Bukkit.getConsoleSender().sendMessage("Lcoation-ID: " + spawnpointID);
			Integer requiredDonatorID = this.getRequiredDonatorID(spawnpointID);
			Integer requiredTitleID = this.getRequiredTitleID(spawnpointID);
			Location location = this.getSpawnPointLocation(spawnpointID);
//			if (!location.getChunk().isLoaded())
//			{
//				location.getWorld().refreshChunk(location.getChunk().getX(), location.getChunk().getZ());
//			}
			if (user.inOwnerModus())
			{
				this.teleport(user, location);
	        	player.sendMessage(ColorOptions.messageachievement + "You teleported to " + ColorOptions.messagesubjects + targetLocation);
			} else
			{
				if (this.getSpawnPointList(true, false, false, true, false, false).contains(spawnpointID))
				{
					if (donatorID >= requiredDonatorID)
					{
						if (titleID >= requiredTitleID)
						{
							Integer price = this.getSpawnPointPrice(spawnpointID);
							if (gems >= price)
							{
								if (donatorID > 0)
								{
									player.sendMessage(ColorOptions.messageformat + "You need to wait 3 seconds before teleporting...");
					            	TeleportDelay.setDelay(uuid, 3, location, price, targetLocation, false);
								} else
								{
									player.sendMessage(ColorOptions.messageformat + "You need to wait 5 seconds before teleporting...");
					            	TeleportDelay.setDelay(uuid, 5, location, price, targetLocation, false);
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "You don't have enough gems to teleport to this location!");
							}
						} else
						{
							player.sendMessage(ColorOptions.error + "You need to have the title " + title.getTitleName(requiredTitleID, user.getGenderID()) + " or higher to teleport to this location!");
						}
					} else
					{
						player.sendMessage(ColorOptions.error + "You need to have Donator-rank " + donator.getDonatorName(requiredDonatorID) + " or higher to teleport to this location!");
					}
				} else
				{
					player.sendMessage(ColorOptions.error + "You can't teleport to this location with this command!");
				}
			}
		} else
		{
			player.sendMessage(ColorOptions.error + "There is no spawnpoint called " + targetLocation);
		}
	}
	
	public Integer getSpawnPointIDbyLocation(Location location)
	{
		Integer id = null;
		
		Double x = location.getX();
		Double y = location.getY();
		Double z = location.getZ();
		String world = location.getWorld().getName();
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpawnPoint WHERE World=? AND X=? AND Y=? AND Z=?;");
			stmt.setString(1, world);
			stmt.setDouble(2, x);
			stmt.setDouble(3, y);
			stmt.setDouble(4, z);
			
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
	
	public void teleport(User user, Location location)
	{
		Player player = user.getPlayer();
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Teleporting..");
		}
		boolean loaded = location.getWorld().isChunkLoaded((int) location.getX(), (int) location.getZ());
		if (!loaded)
		{
			location.getWorld().loadChunk((int) location.getX(), (int) location.getZ());
		} else
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Chunk is loaded!");
			}
		}
		while(!loaded)
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Chunk not loaded, still loading");
			}
			loaded = location.getWorld().isChunkLoaded((int) location.getX(), (int) location.getZ());
		}
		player.teleport(location);
		if (AfkEvents.getPlayerAfk(player) != null)
		{
			Bukkit.getConsoleSender().sendMessage("Teleporting to false");
			AfkEvents.getPlayerAfk(player).teleporting = false;
		}
	}
	
	public boolean canTeleport(Location location)
	{
		boolean safe = true;
		
		List<Location> checkLocs = new ArrayList<Location>(Arrays.asList(
				location,
				location.clone().add(0, 1, 0)
				));
		
		for (Location loc : checkLocs)
		{
			if (!loc.getBlock().isEmpty())
			{
				Block block = loc.getBlock();
				if (block.getType() != Material.AIR)
				{
					Bukkit.getConsoleSender().sendMessage("Location obstructed by " + block.getType().toString() + " on location " + loc.toString());
					safe = false;
					break;
				}
			}
		}
		
		return safe;
	}
}
