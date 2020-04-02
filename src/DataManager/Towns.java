package DataManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import Handlers.ColorOptions;
import Main.Main;
import Models.Town;
import Models.creations.TownCreation;
import SpawnPoints.SpawnPoint;
import Titles.Title;

public interface Towns 
{
	Title title = new Title();
	
	public static List<Town> Towns = new ArrayList<Town>();
	
	/**
	 * Creates a town in the Database
	 * @param sender: For sending succes/fail messages
	 * @param name: The name of the town
	 * @param requiredTitleID: The minimal required title ID for entering the town
	 * @param description: The description of the town
	 */
	public static void CreateTown(CommandSender sender, String name, Integer requiredTitleID, String description)
	{
		Integer titleID = null;
		if (title.getIDList().contains(requiredTitleID))
		{
			titleID = requiredTitleID;
		}
		//Check if a city with this name already exists
		if (!ExistTown(name))
		{
			try 
			{
				PreparedStatement stmt = Main.getConnection().prepareStatement("INSERT INTO Town(Name, RequiredTitleID, Description) VALUES(?, ?, ?);");
				stmt.setString(1, name);
				stmt.setInt(2, titleID);
				stmt.setString(3, description);
				
				stmt.executeUpdate();
				
				String message = ChatColor.GREEN + "New town " + name + " has succesfully been saved to the database!";
				sender.sendMessage(message);
				Bukkit.getConsoleSender().sendMessage(message);
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates a town. Registers a town into the Database and creates the gateRegion for it
	 * @param creation: Contains all the required information
	 */
	public static void CreateTown(TownCreation creation)
	{
		boolean completeSteps = true;
		Player player = creation.getUser().getPlayer();
		Integer townID = -1;
		Integer spawnpointID = -1;

		try
		{
			CreateTown(creation.getUser().getPlayer(), creation.getName(), creation.getRequiredTitleID(), creation.getDescription());
		} catch (Exception ex)
		{
			completeSteps = false;
			String message = ColorOptions.error + "Something went wrong while creating the Town in the Database. Please try again and notify a developer";
			player.sendMessage(message);
			Main.logError(message);
			ex.printStackTrace();
			return;
		}
		
		try
		{
			townID = FetchTownID(creation.getName());
			
			String succesMessage = ChatColor.GREEN + "The new Town's ID has been fetched from the Database";
			Main.logMessage(succesMessage);
			creation.sendMessage(Arrays.asList(succesMessage));
		} catch (Exception ex)
		{
			completeSteps = false;
			String message = ColorOptions.error + "Something went wrong while fetching the Town form the Database. Please try again and notify a developer";
			player.sendMessage(message);
			Main.logError(message);
			ex.printStackTrace();
			return;
		}
		ProtectedRegion parent = null;
		        
        /**
         * Creating the main gateRegion
         */
        try
        {
            Selection selection = Worldguard.getSelectionFromRegion(player, creation.getRegion());

        	parent = new ProtectedCuboidRegion(
    				"town_" + townID,
    				new BlockVector(selection.getNativeMinimumPoint()),
    				new BlockVector(selection.getNativeMaximumPoint())
    				);
        	Worldguard.getRegionManager(player.getLocation().getWorld()).removeRegion(creation.getRegion().getId());
    		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(parent);
    		
    		parent.setFlag(DefaultFlag.ENTRY, State.ALLOW);
    		parent.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY); 
    		parent.setFlag(DefaultFlag.PVP, State.DENY);
    		parent.setFlag(DefaultFlag.DAMAGE_ANIMALS, State.ALLOW);
    		parent.setFlag(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, State.DENY);
    		parent.setFlag(DefaultFlag.DENY_MESSAGE, "");
    		parent.setPriority(Integer.valueOf(8));
    		RegionGroupFlag entryFlag = DefaultFlag.ENTRY.getRegionGroupFlag();
    		try 
    		{
    			entryFlag.parseInput(Worldguard.getWorldGuard(), null, "non_members");
    		} catch (InvalidFlagFormat e) 
    		{
    			// Auto-generated catch block
    			e.printStackTrace();
    		}
    		
			String succesMessage = ChatColor.GREEN + "The new Town's Worldguard gateRegion has been created";
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
        
        for (ProtectedRegion r : creation.getSubRegions())
    	{
            try
            {
        		Selection selection = Worldguard.getSelectionFromRegion(player, r);

            	ProtectedRegion region = new ProtectedCuboidRegion(
        				"town_" + townID + "," + creation.getSubRegions().indexOf(r),
        				new BlockVector(selection.getNativeMinimumPoint()),
        				new BlockVector(selection.getNativeMaximumPoint())
        				);
            	region.setParent(parent);
            	Worldguard.getRegionManager(player.getLocation().getWorld()).removeRegion(r.getId());
        		Worldguard.getRegionManager(player.getLocation().getWorld()).addRegion(region);
        		
    			String succesMessage = ChatColor.GREEN + "The new Town's ID child gateRegion has been created " + (creation.getSubRegions().indexOf(r)+1) + "/" + creation.getSubRegions().size();
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
        
        try
        {
        	SpawnPoint spawnpoint = new SpawnPoint();
        	Location s = creation.getSpawnpoint();
        	spawnpoint.saveSpawnPoint(creation.getName(), creation.getRequiredTitleID().toString(), 10, "0", s.getWorld(), s.getX(), s.getY(), s.getZ(), s.getYaw(), s.getPitch());
        	String message = ChatColor.GREEN + "The Spawnpoint for the new Town has been created";
        	Main.logMessage(message);
        	creation.sendMessage(Arrays.asList(message));
        } catch (Exception ex)
        {
        	completeSteps = false;
			String message = ColorOptions.error + "Something went wrong while creating the Spawnpoint in the Database. Please try again and notify a developer";
			player.sendMessage(message);
			Main.logError(message);
			ex.printStackTrace();
        }
        
        try
        {
        	SpawnPoint spawnpoint = new SpawnPoint();
        	spawnpointID = spawnpoint.getSpawnPointID(creation.getName());
        	String message = ChatColor.GREEN + "The new Spawnpoints ID has been fetched from the Database";
        	Main.logMessage(message);
        	creation.sendMessage(Arrays.asList(message));
        } catch (Exception ex)
        {
        	completeSteps = false;
			String message = ColorOptions.error + "Something went wrong while fetching the Spawnpoint in the Database. Please try again and notify a developer";
			player.sendMessage(message);
			Main.logError(message);
			ex.printStackTrace();
        }
        
        try
        {
        	SpawnPoint spawnpoint = new SpawnPoint();
        	spawnpoint.saveTownSpawnPoint(townID, spawnpointID);
        	String message = ChatColor.GREEN + "The new Town Spawnpoint ID has been linked in the Database";
        	Main.logMessage(message);
        	creation.sendMessage(Arrays.asList(message));
        } catch (Exception ex)
        {
        	completeSteps = false;
			String message = ColorOptions.error + "Something went wrong while linking the Spawnpoint to the Town in the Database. Please try again and notify a developer";
			player.sendMessage(message);
			Main.logError(message);
			ex.printStackTrace();
        }
        
        if (completeSteps)
        {
        	String message = ChatColor.GREEN + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for Town " + townID + " has succesfully been completed!";
        	Main.logMessage(message);
        	player.sendMessage(message);
        } else
        {
        	RemoveTown(null, townID);
        	String message = ColorOptions.error + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for Town " + townID + " has completed with Errors!";
        	Main.logError(message);
        	player.sendMessage(message);
        }
        Creations.DestroyCreation(creation);
	}
	
	public static void SaveTown(Town town)
	{
		ClearTownDiscovered(town.getID());
		
		try
		{
			Statement stmt = Main.getConnection().createStatement();
			for (Integer userID : town.getDiscoveredUserIDs())
			{
				stmt.addBatch("INSERT INTO DiscoveredTowns(TownID, UserID) VALUES(" + town.getID() + ", " + userID + ")");
			}
			stmt.executeBatch();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE Towns SET Name = ?, Description = ?, RequiredTitleID = ? WHERE ID = ?;");
			stmt.setString(1, town.getName());
			stmt.setString(2, town.getDescription());
			stmt.setInt(3, town.getRequiredTitleID());
			stmt.setDouble(4, town.getID());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Town " + town.getID() + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static boolean RemoveTown(CommandSender sender, int townID)
	{
		String message = null;
		boolean removed = false;

		try
		{
			//Prepare the search query
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM Town WHERE ID=?");
			stmt.setInt(1, townID);
			
			stmt.executeUpdate();
			message = ChatColor.GRAY + "Deleted town with ID " + townID + " from the Database at " + Main.getTime();
			Main.logMessage(message);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing town " + townID + " from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		try
		{
			RegionManager manager = Worldguard.getRegionManager(Bukkit.getWorld("world"));
			manager.removeRegion("town_" + townID);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing town " + townID + " from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		try
		{
			SpawnPoint spawnpoint = new SpawnPoint();
			spawnpoint.removeTownSpawnPoint1(townID);
			message = ChatColor.GRAY + "Deleted town spawnpoint from the Database at " + Main.getTime();
			Main.logMessage(message);
		} catch (Exception ex)
		{
			message = ColorOptions.error + "Error while removing town spawnpoint from the database. Please notify a developer";
			Main.logError(message);
			ex.printStackTrace();
			removed = false;
		}
		
		if (removed)
		{
			Town town = FindTown(townID);
			if (town != null)
			{
				Towns.remove(town);
				DestroyTown(town);
			}
			removed = true;
		}
		sender.sendMessage(message);
		
		return removed;
	}
	
	//
	public static boolean ExistTown(String name)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Town WHERE Name = ?");
			stmt.setString(1, name);
			
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
	
	public static boolean ExistTown(int townID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Town WHERE ID = ?");
			stmt.setInt(1, townID);
			
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
	
	public static Town InstantiateTown(String name, boolean newTown)
	{
		Town town = FindTown(name);
		
		if (town != null)
		{
			return town;
		}
		
		if (!ExistTown(name))
		{
			return null;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Town WHERE Name = ?");
			stmt.setString(1, name);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				int ID = results.getInt("ID");
				town = new Town(ID, 
						results.getString("Name"), 
						results.getString("Description"), 
						results.getInt("RequiredTitleID"), 
						FetchTownDiscovered(ID));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return town;
	}
	
	public static Town InstantiateTown(int townID, boolean newTown)
	{
		Town town = FindTown(townID);
		
		if (town != null)
		{
			return town;
		}
		
		if (!ExistTown(townID))
		{
			return null;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Town WHERE ID = ?");
			stmt.setInt(1, townID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				int ID = results.getInt("ID");
				town = new Town(ID, 
						results.getString("Name"), 
						results.getString("Description"), 
						results.getInt("RequiredTitleID"), 
						FetchTownDiscovered(ID));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return town;
	}
	
	public static List<Integer> FetchTownDiscovered(int townID)
	{
		List<Integer> list = new ArrayList<Integer>();
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM DiscoveredTowns WHERE TownID = ?");
			stmt.setInt(1, townID);
			
			ResultSet results = stmt.executeQuery();
			
			while (results.next())
			{
				list.add(results.getInt("UserID"));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return list;
	}
	
	public static void ClearTownDiscovered(int townID)
	{
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM DiscoveredTowns WHERE TownID = ?");
			stmt.setInt(1, townID);
			
			stmt.executeQuery();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static Town FindTown(String name)
	{
		Town town = null;
		
		for (Town t : Towns)
		{
			if (t.getName().equalsIgnoreCase(name))
			{
				town = t;
			}
		}
		
		return town;
	}
	
	public static Town FindTown(int ID)
	{
		Town town = null;
		
		for (Town t : Towns)
		{
			if (t.getID() == ID)
			{
				town = t;
			}
		}
		
		return town;
	}
	
	public static void PurgeTown(Integer townID)
	{
		RegionManager manager = Worldguard.getRegionManager(Bukkit.getWorld("world"));
		ProtectedRegion region = manager.getRegion("town_" + townID);

		region.setFlags(null);
		
		region.setFlag(DefaultFlag.ENTRY, State.ALLOW);
		region.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY); 
		region.setFlag(DefaultFlag.DAMAGE_ANIMALS, State.ALLOW);
		region.setFlag(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, State.DENY);
		region.setFlag(DefaultFlag.DENY_MESSAGE, "");
		region.setPriority(Integer.valueOf(8));
		RegionGroupFlag entryFlag = DefaultFlag.ENTRY.getRegionGroupFlag();
		try 
		{
			entryFlag.parseInput(Worldguard.getWorldGuard(), null, "non_members");
		} catch (InvalidFlagFormat e) 
		{
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void DestroyTown(Town town)
	{
		town = null;
		System.gc();
	}
	
	public static Integer FetchTownID(String townName)
	{
		Integer townID = null;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Town WHERE Name = ?");
			stmt.setString(1, townName);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				townID = results.getInt("ID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return townID;
	}
	
	public static ResultSet GetTownList()
	{
		ResultSet results = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Town;");
			
			ResultSet rawResults = stmt.executeQuery();
			
			results = rawResults;

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		return results;
	}
	
	public static List<Integer> GetIDList()
	{
		List<Integer> IDList = new ArrayList<Integer>();
		
		ResultSet results = GetTownList();
		
		try {
			while(results.next())
			{
				IDList.add(results.getInt("ID"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return IDList;
	}
	
	public static void SaveAll()
	{
		for (Town town : Towns)
		{
			SaveTown(town);
		}
	}
	
	public static void InstantiateAll()
	{
		ResultSet set = GetTownList();
		
		try 
		{
			while (set.next())
			{
				InstantiateTown(set.getInt("ID"), false);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
