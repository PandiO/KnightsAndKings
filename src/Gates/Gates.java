package Gates;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import Handlers.ColorOptions;
import Main.Main;
import Users.User;

public class Gates 
{
	static Main main = Main.getPlugin(Main.class);
	public static List<Gate> gates = new ArrayList<Gate>();
	public static CopyOnWriteArrayList<GateToggle> toggles = new CopyOnWriteArrayList<GateToggle>();
	public static int activeRange = 20;
	
	public static void createGate(CommandSender sender, String name, int streetID, int townID, int health, String faceDirection)
	{
		if (health == -1)
		{
			health = 1000;
		}
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Gates(Name, StreetID, TownID, MaterialID, OriginalHealth, Health, FaceDirection) VALUES(?, ?, ?, ?, ?, ?, ?);");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, name);
			stmt.setInt(2, streetID);
			stmt.setInt(3, townID);
			stmt.setInt(4, 83);
			stmt.setDouble(5, (double)health);
			stmt.setDouble(6, (double)health); 
			stmt.setString(7, faceDirection);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New gate has succesfully been saved to the database!");
			sender.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Saved new Gate to the Database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Error while saving new Gate to the Database. Please try again or notify a developer");
		}
	}
	
	public static void saveGate(Gate gate)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Gates SET Name = ?, StreetID = ?, TownID = ?, OriginalHealth = ?, Health = ?, FaceDirection = ?, MaterialID = ?, IsClosed = ? WHERE ID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, gate.getName());
			stmt.setInt(2, gate.getStreetID());
			stmt.setInt(3, gate.getTownID());
			stmt.setDouble(4, gate.getOriginalHealth());
			stmt.setDouble(5, gate.getHealth());
			stmt.setString(6, gate.getFaceDirection());
			stmt.setInt(7, gate.getMaterialID());
			stmt.setBoolean(8, gate.getClosed());
			stmt.setInt(9, gate.getID());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Gate " + gate.getID() + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void retrieveTownGates(int townID)
	{
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Gates Where TownID=?;");	
			stmt.setInt(1, townID);

			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				boolean createInstance = true;
				for (Gate gate : gates)
				{
					if (gate.getID() == results.getInt("ID"))
					{
						createInstance = false;
						break;
					}
				}
				if (createInstance)
				{
					new Gate(results.getInt("ID"), results.getString("Name"), results.getInt("StreetID"), results.getInt("TownID"), results.getInt("MaterialID"), results.getDouble("OriginalHealth"), results.getDouble("Health"), results.getString("FaceDirection"), results.getBoolean("IsClosed"), false);
				}
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean existGate(String name, int streetID, int townID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Gates WHERE Name = ? AND StreetID = ? AND TownID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, name);
			stmt.setInt(2, streetID);
			stmt.setInt(3, townID);
			
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
	
	public static boolean existGate(int gateID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Gates WHERE ID = ?");
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
		
		if (findGate(name, streetID, townID) != null)
		{
			gate = findGate(name, streetID, townID);
			return gate;
		}
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Gates WHERE Name = ? AND StreetID = ? AND TownID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, name);
			stmt.setInt(2, streetID);
			stmt.setInt(3, townID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				gate = new Gate(results.getInt("ID"), results.getString("Name"), results.getInt("StreetID"), results.getInt("TownID"), results.getInt("MaterialID"), results.getDouble("OriginalHealth"), results.getDouble("Health"), results.getString("FaceDirection"), results.getBoolean("IsClosed"), newGate);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
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
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Gates WHERE ID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, gateID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				gate = new Gate(results.getInt("ID"), results.getString("Name"), results.getInt("StreetID"), results.getInt("TownID"), results.getInt("MaterialID"), results.getDouble("OriginalHealth"), results.getDouble("Health"), results.getString("FaceDirection"), results.getBoolean("IsClosed"), newGate);
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
		
		for (Gate gates : gates)
		{
			if (gates.getName().equalsIgnoreCase(name) && gates.getStreetID() == streetID && gates.getTownID() == townID)
			{
				gate = gates;
			}
		}
		
		return gate;
	}
	
	public static Gate findGate(int ID)
	{
		Gate gate = null;
		
		for (Gate gates : gates)
		{
			if (gates.getID() == ID)
			{
				gate = gates;
			}
		}
		
		return gate;
	}
	
	public static Integer fetchGateID(String name, int streetID, int townID)
	{
		Integer gateID = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Gates WHERE Name = ? AND StreetID = ? AND TownID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, name);
			stmt.setInt(2, streetID);
			stmt.setInt(3, townID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				gateID = results.getInt("ID");
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return gateID;
	}
	
	public static GateToggle findGateToggle(User user)
	{
		GateToggle toggle = null;
		
		for (GateToggle toggles : toggles)
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
	
	public static ResultSet getGateList(int townID)
	{
		ResultSet results = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Gates;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			if (townID != -1)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Gates WHERE TownID = ?");
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
	
	public static void saveAll()
	{
		for (Gate gate : gates)
		{
			saveGate(gate);
		}
	}
	
	public static void instantiateAll(int townID)
	{
		ResultSet set = getGateList(townID);
		
		try 
		{
			while (set.next())
			{
				instantiateGate(set.getInt("ID"), false);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
