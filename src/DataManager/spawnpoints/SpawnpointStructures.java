package DataManager.spawnpoints;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import Main.Main;
import Models.spawnpoint.SpawnpointStructure;

public interface SpawnpointStructures 
{
	public static SpawnpointStructure InstantiateSpawnpointStructure(int structureID)
	{
		SpawnpointStructure spawnpoint = FindSpawnpointStructure(structureID);
		
		if (spawnpoint != null)
		{
			return spawnpoint;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection()
					.prepareStatement("Select Spawnpoint.ID, "
							+ "Spawnpoint.World, "
							+ "Spawnpoint.X, "
							+ "Spawnpoint.Y, "
							+ "Spawnpoint.Z, "
							+ "Spawnpoint.Yaw, "
							+ "Spawnpoint.Pitch "
							+ "FROM SpawnpointStructure "
							+ "INNER JOIN Spawnpoint ON SpawnpointStructure.SpawnpointID = Spawnpoint.ID WHERE StructureID = ?");
			
			stmt.setInt(1, structureID);
			
			Main.logMessage(stmt.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				spawnpoint = new SpawnpointStructure(results.getInt("ID"),
						structureID,
						new Location(Bukkit.getWorld(results.getString("World")), 
								results.getDouble("X"), 
								results.getDouble("Y"), 
								results.getDouble("Z"), 
								results.getFloat("Yaw"), 
								results.getFloat("Pitch")));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return spawnpoint;
	}
	
	public static Integer FetchSpawnpointID(Integer structureID)
	{
		Integer spawnpointID = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM SpawnpointStructure WHERE StructureID = ?");
			stmt.setInt(1, structureID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				spawnpointID = results.getInt("SpawnpointID");
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return spawnpointID;
	}
	
	public static SpawnpointStructure FindSpawnpointStructure(int spawnpointID)
	{
		SpawnpointStructure spawnpoint = null;
		
		for (SpawnpointStructure s : Spawnpoints.getSpawnpointStructures())
		{
			if (s.getID() == spawnpointID)
			{
				spawnpoint = s;
			}
		}
		
		return spawnpoint;
	}
	
	public static SpawnpointStructure FindSpawnpointStructure(Integer structureID)
	{
		SpawnpointStructure spawnpoint = null;
		
		for (SpawnpointStructure s : Spawnpoints.getSpawnpointStructures())
		{
			if (s.getStructureID() == structureID)
			{
				spawnpoint = s;
			}
		}
		
		return spawnpoint;
	}
	
	public static void DestroySpawnpointStructure(SpawnpointStructure spawnpoint)
	{
		spawnpoint = null;
		System.gc();
	}
}
