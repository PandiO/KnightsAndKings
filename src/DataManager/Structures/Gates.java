/**
 * 
 */
package DataManager.Structures;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import DataManager.Creations;
import DataManager.Worldguard;
import DataManager.spawnpoints.SpawnpointStructures;
import DataManager.spawnpoints.Spawnpoints;
import Gates.GateAnimation;
import Gates.GateToggle;
import Handlers.ColorOptions;
import Main.Main;
import Models.Structures.Gate;
import Models.creations.StructureCreation;
import Users.User;

/**
 * @author pandi
 *
 */
public interface Gates 
{
	public static List<Gate> Gates = new ArrayList<Gate>();
	public static CopyOnWriteArrayList<GateToggle> GateToggles = new CopyOnWriteArrayList<GateToggle>();
	public static int activeRange = 20;
	
//	public static void createGate(CommandSender sender, String name, int streetID, int townID, int health, String faceDirection)
//	{
//		if (health == -1)
//		{
//			health = 1000;
//		}
//		try 
//		{
//			PreparedStatement stmt = Main.getConnection().prepareStatement("INSERT INTO Gates(Name, StreetID, TownID, MaterialID, OriginalHealth, Health, FaceDirection) VALUES(?, ?, ?, ?, ?, ?, ?);");
//			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
//			stmt.setString(1, name);
//			stmt.setInt(2, streetID);
//			stmt.setInt(3, townID);
//			stmt.setInt(4, 83);
//			stmt.setDouble(5, (double)health);
//			stmt.setDouble(6, (double)health); 
//			stmt.setString(7, faceDirection);
//			
//			stmt.executeUpdate();
//			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New gate has succesfully been saved to the database!");
//			sender.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Saved new Gate to the Database!");
//		} catch (SQLException e) 
//		{
//			e.printStackTrace();
//			sender.sendMessage(ColorOptions.error + "Error while saving new Gate to the Database. Please try again or notify a developer");
//		}
//	}
	
	public static void CreateGate(StructureCreation creation)
	{
		boolean completeSteps = true;
		Player player = creation.getUser().getPlayer();
		Integer structureID = null;
		
		try {
			java.sql.CallableStatement stmt = Main.getConnection().prepareCall("CALL addGate(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			stmt.setString(1, creation.name);
			stmt.setInt(2, creation.streetID);
			stmt.setInt(3, creation.townID);
			stmt.setInt(4, creation.districtID);
			stmt.setInt(5, 83);
			stmt.setString(6, creation.faceDirection);
			stmt.setDouble(7, creation.health);
			stmt.setDouble(8, creation.health);
			stmt.setBoolean(9, false);
			stmt.setString(10, "@structureID");
			
			Main.logMessage(stmt.toString());
		
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				structureID = results.getInt("structureID");
				
				String message = ColorOptions.messageachievement + "Succesfully created a " + creation.structureType + " in the database";
				player.sendMessage(message);
				Main.logMessage(message);
			} else
			{
				throw new SQLException("Test");
			}			
		} catch (Exception e) {
			String message = ColorOptions.error + "Something went wrong while creating the " + creation.structureType + " in the Database. Please try again and notify a developer";
			Main.logError(message);
			player.sendMessage(message);
			e.printStackTrace();
			completeSteps = false;
			return;
		}
		
		try 
		{
			Location spawnpoint = creation.spawnpoint;
			
			CallableStatement stmt = Main.getConnection().prepareCall("CALL addSpawnpointStructure(?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.setInt(1, structureID);
			stmt.setString(2, spawnpoint.getWorld().getName());
			stmt.setDouble(3, spawnpoint.getX());
			stmt.setDouble(4, spawnpoint.getY());
			stmt.setDouble(5, spawnpoint.getZ());
			stmt.setFloat(6, spawnpoint.getYaw());
			stmt.setFloat(7, spawnpoint.getPitch());
			stmt.setString(8, "@spawnpointID");
			
			Main.logMessage(stmt.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				String message = ColorOptions.messageachievement + "Succesfully saved the " + creation.structureType + "'s spawnpoint to the database! (ID " + results.getInt("spawnpointID") + ")";
				Main.logMessage(message);
				player.sendMessage(message);
			} else
			{
				throw new Exception();
			}
		} catch (Exception ex)
		{
			String message = ColorOptions.error + "Something went wrong while saving the spawnpoint for the " + creation.structureType;
			Main.logError(message);
			player.sendMessage(message);
			ex.printStackTrace();
			completeSteps = false;
		}
		ProtectedRegion parent = null;
		        
        /**
         * Creating the main gateRegion
         */
        try
        {
            Selection selection = Worldguard.getSelectionFromRegion(player, creation.tempRegion);

        	parent = new ProtectedCuboidRegion(
    				"gate_" + structureID,
    				new BlockVector(selection.getNativeMinimumPoint()),
    				new BlockVector(selection.getNativeMaximumPoint())
    				);
        	Worldguard.getRegionManager(player.getLocation().getWorld()).removeRegion(creation.tempRegion.getId());
    		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(parent);
    		
			String succesMessage = ChatColor.GREEN + "The new " + creation.structureType + "'s Worldguard Region has been created";
			Main.logMessage(succesMessage);
			creation.sendMessage(Arrays.asList(succesMessage));
        } catch (Exception ex)
        {
        	completeSteps = false;
        	String message = ColorOptions.error + "Something went wrong while creating the Worldguard Region! Please try again and notify a developer";
        	player.sendMessage(message);
        	Main.logError(message);
        	ex.printStackTrace();
        	return;
        }
        
        for (ProtectedRegion r : creation.tempSubRegions)
    	{
            try
            {
        		Selection selection = Worldguard.getSelectionFromRegion(player, r);

            	ProtectedRegion region = new ProtectedCuboidRegion(
        				"gate_" + structureID + "," + creation.tempSubRegions.indexOf(r),
        				new BlockVector(selection.getNativeMinimumPoint()),
        				new BlockVector(selection.getNativeMaximumPoint())
        				);
            	region.setParent(parent);
            	Worldguard.getRegionManager(player.getLocation().getWorld()).removeRegion(r.getId());
        		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(region);
        		
    			String succesMessage = ChatColor.GREEN + "The new " + creation.structureType + "'s ID child Region has been created " + (creation.tempSubRegions.indexOf(r)+1) + "/" + creation.tempSubRegions.size();
    			Main.logMessage(succesMessage);
    			creation.sendMessage(Arrays.asList(succesMessage));
            } catch (Exception ex)
            {
            	completeSteps = false;
            	String message = ColorOptions.error + "Something went wrong while creation the Worldguard child Region! Please try again and notify a developer";
            	player.sendMessage(message);
            	Main.logError(message);
            	ex.printStackTrace();
            	return;
            }
    	}
        
        try
        {
            Selection selection = Worldguard.getSelectionFromRegion(player, creation.tempGateRegion);

        	ProtectedRegion gateRegion = new ProtectedCuboidRegion(
    				"gate_" + structureID + "_gate",
    				new BlockVector(selection.getNativeMinimumPoint()),
    				new BlockVector(selection.getNativeMaximumPoint())
    				);
        	gateRegion.setParent(parent);
        	Worldguard.getRegionManager(player.getLocation().getWorld()).removeRegion(creation.tempGateRegion.getId());
    		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(gateRegion);
    		
			String succesMessage = ChatColor.GREEN + "The new " + creation.structureType + "'s Worldguard gateRegion has been created";
			Main.logMessage(succesMessage);
			creation.sendMessage(Arrays.asList(succesMessage));
        } catch (Exception ex)
        {
        	completeSteps = false;
        	String message = ColorOptions.error + "Something went wrong while creating the Worldguard gateRegion! Please try again and notify a developer";
        	player.sendMessage(message);
        	Main.logError(message);
        	ex.printStackTrace();
        	return;
        }
        
        for (Location loc : creation.guardSpawnpoints)
        {
        	try
        	{
        		CallableStatement stmt = Main.getConnection().prepareCall("CALL addSpawnpointGateGuard(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        		stmt.setInt(1, structureID);
        		stmt.setString(2, loc.getWorld().getName());
        		stmt.setDouble(3, loc.getX());
        		stmt.setDouble(4, loc.getY());
        		stmt.setDouble(5, loc.getZ());
        		stmt.setFloat(6, loc.getYaw());
        		stmt.setFloat(7, loc.getPitch());
        		stmt.setString(8, "@spawnpointID");
        		
    			Main.logMessage(stmt.toString());
        		
        		ResultSet results = stmt.executeQuery();
        		
        		if (!results.next())
        		{
        			throw new Exception();
        		} else
        		{
        			String succesMessage = ChatColor.GREEN + "The new " + creation.structureType + "'s Guard spawnpoint has been created " + (creation.guardSpawnpoints.indexOf(loc)+1) + "/" + creation.guardSpawnpoints.size();
        			Main.logMessage(succesMessage);
        			creation.sendMessage(Arrays.asList(succesMessage));
        		}
        	} catch (Exception ex)
        	{
        		completeSteps = false;
            	String message = ColorOptions.error + "Something went wrong while saving a guard spawnpoint! Please try again and notify a developer";
            	player.sendMessage(message);
            	Main.logError(message);
            	ex.printStackTrace();
        	}
        }
        
        if (completeSteps)
        {
        	String message = ChatColor.GREEN + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for " + creation.structureType + " " + structureID + " has succesfully been completed!";
        	Main.logMessage(message);
        	player.sendMessage(message);
        } else
        {
        	RemoveGate(player, structureID);
        	String message = ColorOptions.error + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for Town " + creation.structureType + " " + structureID + " has completed with Errors!";
        	Main.logError(message);
        	player.sendMessage(message);
        }
        Creations.DestroyCreation(creation);
	}
	
	public static boolean RemoveGate(CommandSender sender, int gateID)
	{
		String message = null;
		boolean removed = false;

		try
		{
			//Prepare the search query
			Structures.RemoveStructure(sender, gateID);
			message = ChatColor.GRAY + "Deleted gate with ID " + gateID + " from the Database at " + Main.getTime();
			Main.logMessage(message);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing gate " + gateID + " from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		try
		{
			RegionManager manager = Worldguard.getRegionManager(Bukkit.getWorld("world"));
			manager.removeRegion("gate_" + gateID);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing town " + gateID + " from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		Spawnpoints.RemoveSpawnpoint(SpawnpointStructures.FetchSpawnpointID(gateID));
		
		if (removed)
		{
			Gate gate = findGate(gateID);
			if (gate != null)
			{
				Gates.remove(gate);
				destroyGate(gate);
			}
			removed = true;
		}
		sender.sendMessage(message);
		
		return removed;
	}
	
	public static void saveGate(Gate gate)
	{
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE Gate SET OriginalHealth = ?, CurrentHealth = ?, FaceDirection = ?, MaterialID = ?, Closed = ? WHERE StructureID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setDouble(1, gate.getOriginalHealth());
			stmt.setDouble(2, gate.getHealth());
			stmt.setString(3, gate.getFaceDirection());
			stmt.setInt(4, gate.getMaterialID());
			stmt.setBoolean(5, gate.getClosed());
			stmt.setInt(6, gate.getId());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Gate " + gate.getId() + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static boolean existGate(String name, int streetID, int townID)
	{
		boolean exist = false;
		
		if (fetchGateID(name, streetID, townID) != null)
		{
			exist = true;
		}
		
		return exist;
	}
	
	public static boolean existGate(int gateID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Gate WHERE StructureID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, gateID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				exist = true;
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return exist;
	}
	
	public static Gate instantiateGate(String name, int streetID, int townID, boolean newGate)
	{
		Gate gate = null;
		
		Integer gateID = fetchGateID(name, streetID, townID);
		
		gate = instantiateGate(gateID, newGate);
		
		return gate;
	}
	
	public static Gate instantiateGate(int gateID, boolean newGate)
	{
		Gate gate = null;
		
		if (findGate(gateID) != null)
		{
			gate = findGate(gateID);
			return gate;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT "
					+ "Structure.ID, "
					+ "Structure.Name, "
					+ "Structure.StreetID, "
					+ "Structure.TownID, "
					+ "Structure.DistrictID, "
					+ "MaterialID, "
					+ "OriginalHealth, "
					+ "CurrentHealth, "
					+ "FaceDirection, "
					+ "Closed "
					+ "FROM Gate "
					+ "INNER JOIN Structure ON Gate.StructureID = Structure.ID "
					+ "WHERE StructureID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, gateID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				gate = new Gate(results.getInt("ID"), 
						results.getString("Name"), 
						results.getInt("StreetID"), 
						results.getInt("TownID"), 
						results.getInt("DistrictID"),
						results.getInt("MaterialID"), 
						results.getDouble("OriginalHealth"), 
						results.getDouble("CurrentHealth"), 
						results.getString("FaceDirection"), 
						results.getBoolean("Closed"), 
						newGate);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return gate;
	}
	
	public static Gate findGate(String name, int streetID, int townID)
	{
		Gate gate = null;
		
		for (Gate Gates : Gates)
		{
			if (Gates.getName().equalsIgnoreCase(name) && Gates.getStreetID() == streetID && Gates.getTownID() == townID)
			{
				gate = Gates;
			}
		}
		
		return gate;
	}
	
	public static Gate findGate(int ID)
	{
		Gate gate = null;
		
		for (Gate Gates : Gates)
		{
			if (Gates.getId() == ID)
			{
				gate = Gates;
			}
		}
		
		return gate;
	}
	
	public static Integer fetchGateID(String name, int streetID, int townID)
	{
		Integer gateID = null;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT Structure.ID, Structure.Name, Structure.StreetID, Structure.TownID "
					+ "FROM Gate "
					+ "INNER JOIN Structure ON Gate.StructureID = Structure.ID "
					+ "WHERE Structure.Name = ? AND Structure.StreetID = ? AND Structure.TownID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, name);
			stmt.setInt(2, streetID);
			stmt.setInt(3, townID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				gateID = results.getInt("ID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return gateID;
	}
	
	public static GateToggle findGateToggle(User user)
	{
		GateToggle toggle = null;
		
		for (GateToggle toggles : GateToggles)
		{
			if (toggles.getUser() == user)
			{
				toggle = toggles;
				break;
			}
		}
		
		return toggle;
	}
		
	public static void destroyGate(Gate gate)
	{
		gate = null;
		System.gc();
	}
	
	public static void destroyGateAnimationTask(GateAnimation gateAnimation)
	{
		gateAnimation = null;
		System.gc();
	}
	
	public static void destroyGateToggle(GateToggle toggle)
	{
		toggle = null;
		System.gc();
	}
	
	public static List<Integer> getGateList(int townID)
	{
		List<Integer> list = new ArrayList<Integer>();
		
		try
		{
			StringBuilder statement = new StringBuilder("SELECT Structure.ID, Structure.TownID "
					+ "FROM Gate "
					+ "INNER JOIN Structure ON Gate.StructureID = Structure.ID ");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			if (townID != -1)
			{
				statement.append(" WHERE Structure.TownID = '" + townID + "';");
			}
			PreparedStatement stmt = Main.getConnection().prepareStatement(statement.toString());
			
			ResultSet results = stmt.executeQuery();
			
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		return list;
	}
	
	public static void saveAll()
	{
		for (Gate gate : Gates)
		{
			saveGate(gate);
		}
	}
	
	public static void instantiateAll(int townID)
	{
		List<Integer> list = getGateList(townID);
		
		if (list == null)
		{
			return;
		}
		
		for (Integer ID : list)
		{
			instantiateGate(ID, false);
		}
	}
}
