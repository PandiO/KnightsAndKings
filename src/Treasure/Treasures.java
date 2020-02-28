package Treasure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import Gates.Gate;
import Handlers.ColorOptions;
import Main.Main;
import SpawnPoints.SpawnPoint;

public class Treasures 
{
	static SpawnPoint spawnpoint = new SpawnPoint();
	static Main main = Main.getPlugin(Main.class);
	
	public static CopyOnWriteArrayList<Treasure> treasures = new CopyOnWriteArrayList<Treasure>();
	public static HashMap<UUID, Integer> createTreasure = new HashMap<UUID, Integer>();

	public static void createTreasure(Location location, Integer grade)
	{
		Integer spawnpointID = null;
		Integer treasureID = null;
//		String state = null;
//		Integer townID = worldguard.getStructureIDbyRegion("town", location, worldguard.getRegionManager(location.getWorld()));
//		if (townID != null)
//		{
//			state = "INSERT INTO Treasure(Grade, SpawnpointID, TownID) VALUES(?, ?, " + townID + ");";
//			
//		} else
//		{
//			state = "INSERT INTO Treasure(Grade, SpawnpointID) VALUES(?, ?);";
//		}
		try
		{
			try 
			{
				spawnpoint.saveSpawnPoint("treasure_", "0", 0, "0", null, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
				spawnpointID = spawnpoint.getSpawnPointIDbyLocation(location);
				PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Treasure(Grade, SpawnpointID) VALUES(?, ?);");
				stmt.setInt(1, grade);
				stmt.setInt(2, spawnpointID);
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "A new treasure has succesfully been saved to the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
				return;
			}
			treasureID = getTreasureID(spawnpointID);
			spawnpoint.saveName(spawnpointID, "treasure_" + treasureID);
		} catch(Exception e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Error: Couldn't create a treasure!");
			spawnpoint.removeSpawnPoint(spawnpoint.getSpawnPointID("treasure_"));
		}
	}
	
	public static void saveTreasure(Treasure treasure)
	{
		try
		{
			Location location = treasure.GetLocation();
			spawnpoint.saveLocation(treasure.GetSpawnpointID(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		treasure.SaveDiscoveredList();
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Treasure SET Grade = ?, SpawnpointID = ? WHERE ID = ?;");
			stmt.setInt(1, treasure.GetGrade());
			stmt.setInt(2, treasure.GetSpawnpointID());
			stmt.setInt(3, treasure.GetID());
			
			stmt.executeUpdate();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	
	public void removeTreasure(Treasure Treasure)
	{
		try
		{
			spawnpoint.removeSpawnPoint(Treasure.GetSpawnpointID());
			
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM TREASURE WHERE ID=?;");
				stmt.setInt(3, Treasure.GetID());
				
				stmt.executeUpdate();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "A treasure has succesfully been removed from the database!");
			} catch (SQLException e) 
			{
				e.printStackTrace();
				return;
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Error: Couldn't remove treasure!");
		}
	}
	
	public static ResultSet getTreasureList(int townID)
	{
		ResultSet results = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Treasure;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			if (townID != -1)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Treasure WHERE TownID = ?");
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
	
	public static Treasure instantiateTreasure(int treasureID, boolean newTreasure)
	{
		Treasure treasure = null;
		
		if (findTreasure(treasureID) != null)
		{
			treasure = findTreasure(treasureID);
			return treasure;
		}
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Treasure WHERE ID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, treasureID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				treasure = new Treasure(results.getInt("ID"), results.getInt("Grade"), results.getInt("SpawnpointID"), results.getInt("TownID"), newTreasure);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		if (treasure != null)
		{
			treasures.add(treasure);
		}
		
		return treasure;
	}
	
	public static void instantiateAll(int townID)
	{
		ResultSet set = getTreasureList(townID);

		try 
		{
			while (set.next())
			{
				instantiateTreasure(set.getInt("ID"), false);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveAll()
	{
		for (Treasure treasure : treasures)
		{
			saveTreasure(treasure);
		}
	}
	
	public static void stopAll()
	{
		saveAll();
		
		for (Treasure treasure : treasures)
		{
			treasure.Destroy();
		}
	}
	
	public static Treasure findTreasure(int ID)
	{
		Treasure treasure = null;
		
		for (Treasure treasures : treasures)
		{
			if (treasures.GetID() == ID)
			{
				treasure = treasures;
			}
		}
		
		return treasure;
	}
	
	public static Treasure findTreasure(Location location)
	{
		Bukkit.getConsoleSender().sendMessage("looking for treasure");
		main.logMessage("looking for treasure on location...");
		Treasure treasure = null;
		
		if (location.getBlock() != null && location.getBlock().getType() == Material.CHEST)
		{
			main.logMessage("Length of treasure list: " + treasures.size());
			for (Treasure treasures : treasures)
			{
				Location loc = treasures.GetLocation();
				if (loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ())
				{
					treasure = treasures;
					break;
				} else
				{
					main.logMessage(loc.toString() + ", comparing to loc: " + location.toString());
				}
			}
		} else
		{
			main.logMessage("Block null or block is not a chest");
		}
		
		return treasure;
	}
	
	public static Integer getTreasureID(Integer spawnpointID)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Treasure WHERE SpawnPointID=?;");
			stmt.setInt(1, spawnpointID);
			
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
	
	public static Integer getRewardAmount(Treasure Treasure)
	{
		Integer amount = 1;
		
		switch(Treasure.GetGrade())
		{
		case 1: amount = amount+main.getRandom(1, 2);
		break;
		case 2: amount = amount+main.getRandom(1, 3);
		break;
		case 3: amount = amount+main.getRandom(2, 5);
		break;
		case 4: amount = amount+main.getRandom(4, 7);
		break;
		case 5: amount = amount+main.getRandom(6, 12);
		break;
		}
		
		return amount;
	}
	
	public static String getTreasureName(Treasure Treasure)
	{
		String name = null;
		
		switch(Treasure.GetGrade())
		{
		case 1: name = ChatColor.BLUE + "Default Treasure ";
		break;
		case 2: name = ChatColor.BLUE + "Default Treasure ";
		break;
		case 3: name = ChatColor.AQUA + "Rare Treasure ";
		break;
		case 4: name = ChatColor.AQUA + "Rare Treasure ";
		break;
		case 5: name = ChatColor.LIGHT_PURPLE + "Legendary Treasure ";
		}
		name = name + ColorOptions.stars(Treasure.GetGrade());
		
		return name;
	}
}
