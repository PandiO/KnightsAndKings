/**
 * 
 */
package DataManager.spawnpoints;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import Main.Main;
import Models.spawnpoint.Spawnpoint;
import Models.spawnpoint.SpawnpointGateGuard;
import Models.spawnpoint.SpawnpointStructure;

/**
 * @author pandi
 *
 */
public interface Spawnpoints 
{
	public static List<Spawnpoint> Spawnpoints = new ArrayList<Spawnpoint>();
	
	public static List<SpawnpointStructure> getSpawnpointStructures()
	{
		List<SpawnpointStructure> list = new ArrayList<SpawnpointStructure>();
		
		for (Spawnpoint s : Spawnpoints)
		{
			if (s instanceof SpawnpointStructure)
			{
				list.add((SpawnpointStructure)s);
			}
		}
		
		return list;
	}
	
	public static void SaveSpawnpoint(Spawnpoint spawnpoint)
	{
		try
		{
			Location loc = spawnpoint.getLocation();
			PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE Spawnpoint SET World = ?, X = ?, Y = ?, Z = ?, Yaw = ?, Pitch = ? WHERE ID = ?");
			stmt.setString(1, loc.getWorld().getName());
			stmt.setDouble(2, loc.getX());
			stmt.setDouble(3, loc.getY());
			stmt.setDouble(4, loc.getZ());
			stmt.setFloat(5, loc.getYaw());
			stmt.setFloat(6, loc.getPitch());
			stmt.setInt(7, spawnpoint.getID());
			
			stmt.executeUpdate();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static List<SpawnpointGateGuard> getSpawnpointGateGuards()
	{
		List<SpawnpointGateGuard> list = new ArrayList<SpawnpointGateGuard>();
		
		for (Spawnpoint s : Spawnpoints)
		{
			if (s instanceof SpawnpointGateGuard)
			{
				list.add((SpawnpointGateGuard)s);
			}
		}
		
		return list;
	}
	
	public static void RemoveSpawnpoint(Integer ID)
	{
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM Spawnpoint WHERE ID = ?");
			stmt.setInt(1, ID);
			
			stmt.executeUpdate();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static Spawnpoint FindSpawnpoint(Integer spawnpointID)
	{
		Spawnpoint spawnpoint = null;
		
		for (Spawnpoint s : Spawnpoints)
		{
			if (s.getID() == spawnpointID)
			{
				spawnpoint = s;
				break;
			}
		}
		
		return spawnpoint;
	}
	
	public static Spawnpoint InstantiateSpawnpoint(int spawnpointID)
	{
		Spawnpoint spawnpoint = FindSpawnpoint(spawnpointID);
		
		if (spawnpoint != null)
		{
			return spawnpoint;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection()
					.prepareStatement("Select ID, World, X, Y, Z, Yaw, Pitch From Spawnpoint WHERE ID = ?");
			
			stmt.setInt(1, spawnpointID);
						
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				spawnpoint = new Spawnpoint(results.getInt("ID"),
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
}
