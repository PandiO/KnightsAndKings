/**
 * 
 */
package DataManager.Structures;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import DataManager.Worldguard;
import Handlers.ColorOptions;
import Main.Main;
import Models.creations.StructureCreation;
import SpawnPoints.SpawnPoint;

/**
 * @author pandi
 *
 */
public interface Houses 
{
	public static void CreateHouse(StructureCreation creation)
	{
		boolean completeSteps = true;
		Player player = creation.getUser().getPlayer();
		Integer structureID = null;
		
		try {
			java.sql.CallableStatement stmt = Main.getConnection().prepareCall("CALL addHouse(?, ?, ?, ?, ?, ?, ?)");
			stmt.setString(1, creation.name);
			stmt.setInt(2, creation.streetID);
			stmt.setInt(3, creation.streetNumber);
			stmt.setInt(4, creation.townID);
			stmt.setInt(5, creation.districtID);
			stmt.setInt(6, creation.price);
			stmt.setInt(7, -1);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				structureID = results.getInt("structureID");
				
				String message = ColorOptions.messageachievement + "Succesfully created a house in the database";
				player.sendMessage(message);
				Main.logMessage(message);
			} else
			{
				throw new SQLException();
			}			
		} catch (Exception e) {
			String message = ColorOptions.error + "Something went wrong while creating the House in the Database. Please try again and notify a developer";
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
			stmt.setInt(8, -1);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				String message = ColorOptions.messageachievement + "Succesfully saved the House's spawnpoint to the database! (ID " + results.getInt("spawnpoint") + ")";
				Main.logMessage(message);
				player.sendMessage(message);
			} else
			{
				throw new Exception();
			}
		} catch (Exception ex)
		{
			String message = ColorOptions.error + "Something went wrong while saving the spawnpoint for the house";
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
    				"house_" + structureID,
    				new BlockVector(selection.getNativeMinimumPoint()),
    				new BlockVector(selection.getNativeMaximumPoint())
    				);
        	Worldguard.getRegionManager(player.getLocation().getWorld()).removeRegion(creation.tempRegion.getId());
    		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(parent);
    		
    		parent.setFlag(DefaultFlag.ENTRY, State.DENY);
    		parent.setFlag(DefaultFlag.FEED_AMOUNT, Integer.valueOf(20));
    		parent.setFlag(DefaultFlag.FEED_DELAY, Integer.valueOf(1));
    		parent.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, "");
    		parent.setFlag(DefaultFlag.DENY_MESSAGE, "");
    		parent.setPriority(Worldguard.housePriority);
    		//Set the flag that manages the access of players other than the owner of the house
    		RegionGroupFlag entryFlag = DefaultFlag.ENTRY.getRegionGroupFlag();
    		try 
    		{
    			entryFlag.parseInput(Worldguard.getWorldGuard(), null, "non_members");
    		} catch (InvalidFlagFormat e) 
    		{
    			// Auto-generated catch block
    			e.printStackTrace();
    		}
    		//Manage some additional flags
    		player.performCommand("rg flag " + ("house_" + structureID) + " deny-blocks any");
    		
			String succesMessage = ChatColor.GREEN + "The new House's Worldguard gateRegion has been created";
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
        
        for (ProtectedRegion r : creation.tempSubRegions)
    	{
            try
            {
        		Selection selection = Worldguard.getSelectionFromRegion(player, r);

            	ProtectedRegion region = new ProtectedCuboidRegion(
        				"house_" + structureID + "," + creation.tempSubRegions.indexOf(r),
        				new BlockVector(selection.getNativeMinimumPoint()),
        				new BlockVector(selection.getNativeMaximumPoint())
        				);
            	region.setParent(parent);
            	Worldguard.getRegionManager(player.getLocation().getWorld()).removeRegion(r.getId());
        		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(region);
        		
    			String succesMessage = ChatColor.GREEN + "The new House's ID child gateRegion has been created " + (creation.tempSubRegions.indexOf(r)+1) + "/" + creation.tempSubRegions.size();
    			Main.logMessage(succesMessage);
    			creation.sendMessage(Arrays.asList(succesMessage));
            } catch (Exception ex)
            {
            	completeSteps = false;
            	String message = ColorOptions.error + "Something went wrong while creation the Worldguard child gateRegion! Please try again and notify a developer";
            	player.sendMessage(message);
            	Main.logError(message);
            	ex.printStackTrace();
            	return;
            }
    	}
		
		if (completeSteps)
        {
        	String message = ChatColor.GREEN + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for House " + structureID + " has succesfully been completed!";
        	Main.logMessage(message);
        	player.sendMessage(message);
        } else
        {
        	String message = ColorOptions.error + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for House " + structureID + " has completed with Errors!";
        	Main.logError(message);
        	player.sendMessage(message);
        }
	}
	
	public static boolean RemoveHouse(CommandSender sender, int structureID)
	{
		String message = null;
		boolean removed = false;

		try
		{
			//Prepare the search query
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM Structure WHERE ID=?");
			stmt.setInt(1, structureID);
			
			stmt.executeUpdate();
			message = ChatColor.GRAY + "Deleted house with ID " + structureID + " from the Database at " + Main.getTime();
			Main.logMessage(message);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing house " + structureID + " from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		try
		{
			RegionManager manager = Worldguard.getRegionManager(Bukkit.getWorld("world"));
			manager.removeRegion("house_" + structureID);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing house " + structureID + " from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		try
		{
			SpawnPoint spawnpoint = new SpawnPoint();
			spawnpoint.removeTownSpawnPoint1(structureID);
			message = ChatColor.GRAY + "Deleted house's spawnpoint from the Database at " + Main.getTime();
			Main.logMessage(message);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing house spawnpoint from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		if (removed)
		{
//			Town town = FindTown(structureID);
//			if (town != null)
//			{
//				Towns.remove(town);
//				DestroyTown(town);
//			}
//			removed = true;
		}
		sender.sendMessage(message);
		
		return removed;
	}
}
