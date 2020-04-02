package Arenas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import Handlers.ColorOptions;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Towns.Town;
import Traits.ArenaMaster;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class Arena 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	Street street = new Street();
	Town town = new Town();
	
	public HashMap<Duel, Integer> duelWaitList = new HashMap<Duel, Integer>();
	
	//Save an arena to the database
	public void saveArena(String name, Integer streetID, Integer streetNumber)
	{
		String townName = town.getTownName(street.getTownID(streetID));
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Arena(Name, StreetID, StreetNumber) VALUES(?, ?, ?);");
			stmt.setString(1, name);
			stmt.setInt(2, streetID);
			stmt.setInt(3, streetNumber);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New arena " + name + " in city " + townName + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Integer getArenaID(String name)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Arena WHERE Name=?;");	
			stmt.setString(1, name);
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
	
	public ResultSet getArena(Integer arenaID)
	{
		ResultSet results = null;
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Arena WHERE ID=?;");	
			stmt.setInt(1, arenaID);
			//Execute the query
			results = stmt.executeQuery();
			if (!results.next())
			{
				return null;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return results;
	}
	
	public void saveArenaLocation(Integer arenaID, String category, Integer spawnpointID)
	{
		if (category.equalsIgnoreCase("catacombs") || category.equalsIgnoreCase("spectate") || category.equalsIgnoreCase("player1") || category.equalsIgnoreCase("player2") || category.equalsIgnoreCase("fighter3") || category.equalsIgnoreCase("fighter4") || category.equalsIgnoreCase("exit") || category.equalsIgnoreCase("npc"))
		{
			String statementCategory = this.getValidLoc(category);
			
			String stmtString = "UPDATE Arena SET Spawnpoint" + statementCategory + "=? Where ID=?;";
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement(stmtString);
				stmt.setInt(1, spawnpointID);
				stmt.setInt(2, arenaID);
				
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public Integer getSpawnpointID(Integer arenaID, String category)
	{
		Integer spawnpointID = null;
		if (this.validLoc(category))
		{
			String statementCategory = this.getValidLoc(category);
			
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Arena WHERE ID=?");
				stmt.setInt(1, arenaID);
				
				ResultSet results = stmt.executeQuery();
				if (results.next())
				{
					spawnpointID = results.getInt("Spawnpoint" + statementCategory);
				}
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		
		return spawnpointID;
	}
	
	//Remove the spawnpoint of an arena from the database
	public void removeSpawnPoint(Integer arenaID, String category)
	{
		if (this.validLoc(category))
		{
			String stmtCategory = this.getValidLoc(category);
			try 
			{
				String stringStmt = "UPDATE Arena SET Spawnoint" + stmtCategory + "=null WHERE ID=?";
				PreparedStatement stmt = main.getConnection().prepareStatement(stringStmt);
				stmt.setInt(1, arenaID);
				
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<Integer> getArenaIDList(Integer townID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Arena;");	


			//Execute the query
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				if (townID != null)
				{
					Integer arenaID = results.getInt("ID");
					Integer streetID = this.getArena(arenaID).getInt("StreetID");
					if (street.getTownID(streetID) == townID)
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
	
	//Check if the given streetnumber already exists. (Checks properties as well as houses)
	public boolean checkStreetNumber(Integer streetNumber, Integer streetID)
	{
		boolean unique = true;
			
		try
		{
			//prepare the query to retrieve the id of a house
			PreparedStatement stmt1 = main.getConnection().prepareStatement("Select * From House WHERE HouseNumber=? AND StreetID=?;");	
			PreparedStatement stmt2 = main.getConnection().prepareStatement("Select * From Property WHERE PropertyNumber=? AND StreetID=?;");	
			PreparedStatement stmt3 = main.getConnection().prepareStatement("Select * From Arena WHERE StreetNumber=? AND StreetID=?;");	
			
			stmt1.setInt(1, streetNumber);
			stmt1.setInt(2, streetID);
			stmt2.setInt(1, streetNumber);
			stmt2.setInt(2, streetID);
			stmt3.setInt(1, streetNumber);
			stmt3.setInt(2, streetID);
			
			//Execute the query
			ResultSet results1 = stmt1.executeQuery();
			ResultSet results2 = stmt2.executeQuery();
			ResultSet results3 = stmt3.executeQuery();
			if (results1.next() || results2.next() || results3.next())
			{
				unique = false;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
			
		return unique;
	}
	
	//Save a sub-gateRegion of a property
	public void saveArenaPart(Integer arenaID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO ArenaRegion(ArenaID) VALUES(?);");
			stmt.setInt(1, arenaID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New part of arena " + this.getArena(arenaID).getString("Name") + " has succesfully been saved to the Database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get a list of sub-regions of a property
	public ArrayList<Integer> getArenaPartList(Integer arenaID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From ArenaRegion WHERE ArenaID=?;");	
			stmt.setInt(1, arenaID);
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
	
	//Chect if a part-id already exists in the database
	public boolean checkPartID(Integer arenaID, Integer PartID)
	{
		boolean exist = false;
		
		if (getArenaPartList(arenaID).contains(PartID))
		{
			exist = true;
		}
		
		return exist;
	}
	
	//Remove a property part from the database
	public void removeArenaPart(Integer arenaID, Integer PartID)
	{
		try
		{
			//prepare the query to delete a part-gateRegion
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM ArenaRegion WHERE SubID=? AND ArenaID=?;");	
			stmt.setInt(1, PartID);
			stmt.setInt(2, arenaID);
			//Execute the query
			stmt.executeUpdate();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Remove a property from the database
	public void removeArenabyID(Integer arenaID) throws SQLException
	{
		String arenaName = this.getArena(arenaID).getString("Name");
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Arena WHERE ID=?;");
			stmt.setInt(1, arenaID);
			
			stmt.executeUpdate();
			//Send the console a message if the spawnpoint belongs to a city
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Arena " + arenaName + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public boolean validLoc(String locationName)
	{
		boolean valid = false;
		if (locationName.equalsIgnoreCase("catacombs") || locationName.equalsIgnoreCase("spectate") || locationName.equalsIgnoreCase("player1") || locationName.equalsIgnoreCase("player2") || locationName.equalsIgnoreCase("player3") || locationName.equalsIgnoreCase("player4") || locationName.equalsIgnoreCase("exit") || locationName.equalsIgnoreCase("npc"))
		{
			valid = true;
		}
		
		return valid;
	}
	
	public String getValidLoc(String category)
	{
		String statementCategory = null;
		switch(category)
		{
		case "catacombs":
			statementCategory = "Lobby";
			break;
		case "spectate":
			statementCategory = "Spectate";
			break;
		case "player1":
			statementCategory = "Fighter1";
			break;
		case "player2":
			statementCategory = "Fighter2";
			break;
		case "player3":
			statementCategory = "Fighter3";
			break;
		case "player4":
			statementCategory = "Fighter4";
			break;
		case "exit":
			statementCategory = "Exit";
			break;
		case "npc":
			statementCategory = "NPC";
			break;
		default: statementCategory = "Lobby";
		break;
		}
		
		return statementCategory;
	}
	
	public boolean isDuelling(UUID uuid)
	{
		boolean duelling = false;
		
		if (!main.duelList.isEmpty())
		{
			if (main.duelList.get(0).user1.getUUID() == uuid || main.duelList.get(0).user2.getUUID() == uuid)
			{
				duelling = true;
			}
			if (main.duelList.size() >= 2)
			{
				if (main.duelList.get(1).user1.getUUID() == uuid || main.duelList.get(1).user2.getUUID() == uuid)
				{
					duelling = true;
				}
			}
		}
		
		return duelling;
	}
	
	public Integer getNPCID(Integer arenaID)
	{
		Integer npcID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Arena WHERE ID=?;");	
			stmt.setInt(1, arenaID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				npcID = results.getInt("npcID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return npcID;
	}
	
	public Integer getArenaIDbyNPCID(Integer npcID)
	{
		Integer propertyID = null;
		
		try
		{
			//prepare the query to retrieve the id of a house spawnpoint
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Arena WHERE npcID=?;");	
			stmt.setInt(1, npcID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				npcID = results.getInt("ID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return propertyID;
	}
	
	public void setNPCID(Integer arenaID, Integer npcID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Arena SET npcID=? WHERE ID=?;");
			stmt.setInt(1, npcID);
			stmt.setInt(2, arenaID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Changed the NPC id of arena with ID " + arenaID + " to " + npcID + " in the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void refreshNPC(CommandSender sender, Integer arenaID)
	{
		SpawnPoint spawnpoint = new SpawnPoint();
		Integer spawnpointID = this.getSpawnpointID(arenaID, "npc");
		if (spawnpointID != null)
		{
			NPCRegistry registry = CitizensAPI.getNPCRegistry();
			Integer npcID = this.getNPCID(arenaID);
			if (npcID != null)
			{
				for (NPC npc : registry.sorted())
				{
					if (npc.getId() == npcID)
					{
						npc.destroy();
						break;
					}
				}
				Bukkit.getConsoleSender().sendMessage("Spawnpoint not null, commencing spawning");
			    NPC npc = registry.createNPC(EntityType.PLAYER, "Paladinen");
			    npc.setName("Arena Owner");
			    npc.addTrait(ArenaMaster.class);
			    npc.setProtected(true);
			    npc.spawn(spawnpoint.getSpawnPointLocation(spawnpointID));
			    this.setNPCID(arenaID, npc.getId());
			} else
			{
				sender.sendMessage(ColorOptions.error + "Arena with ID " + arenaID + " doesn't have a registered npc ID");
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "Can't refresh npc, arena has no spawnpoint named 'npc'!");
		}
	}
}
