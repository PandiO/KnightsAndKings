/**
 * 
 */
package DataManager.Structures;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import DataManager.spawnpoints.SpawnpointStructures;
import DataManager.spawnpoints.Spawnpoints;
import Handlers.ColorOptions;
import Main.Main;
import Models.Structures.Structure;

/**
 * @author pandi
 *
 */
public interface Structures 
{
	public static List<Structure> Structures = new ArrayList<Structure>();
	
	public static void RemoveStructure(CommandSender sender, Integer structureID)
	{
		Integer spawnpointID = SpawnpointStructures.FetchSpawnpointID(structureID);
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM Structure WHERE ID = ?");
			stmt.setInt(1, structureID);
			
			stmt.executeUpdate();
			String message = ChatColor.GRAY + "Deleted structure with ID " + structureID + " from the Database at " + Main.getTime();
			Main.logMessage(message);
			sender.sendMessage(message);
		} catch (Exception ex)
		{
			String message = ColorOptions.error + "Error while removing structure with ID " + structureID + " from the Database. Please notify a developer and try again";
			Main.logError(message);
			sender.sendMessage(message);
			ex.printStackTrace();
		}
		
		Spawnpoints.RemoveSpawnpoint(spawnpointID);
	}
	
	public static String FetchStructureName(Integer structureID)
	{
		String name = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Structure WHERE ID = ?");
			stmt.setInt(1, structureID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				name = results.getString("Name");
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return name;
	}
}
