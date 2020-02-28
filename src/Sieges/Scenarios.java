package Sieges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import Handlers.ColorOptions;
import Main.Main;
import SpawnPoints.SpawnPoint;

public class Scenarios 
{
	static Main main = Main.getPlugin(Main.class);
	static SpawnPoint spawnpoint = new SpawnPoint();
	
	public static void createScenario(SiegeCreation sc)
	{
		int siegeID = -1;
		List<String> spawnpointNames = new ArrayList<String>();
		
		//Creating the actual Scenario in the Database
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Sieges(Name, TownID, PlayersMin, PlayersMax, Team1, Team2) VALUES(?, ?, ?, ?, ?, ?);");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, sc.name);
			stmt.setInt(2, sc.townID);
			stmt.setInt(3, sc.playersMin);
			stmt.setInt(4, sc.playersMax);
			stmt.setInt(5, sc.spawnpointTeam1.size());
			stmt.setInt(6, sc.spawnpointTeam2.size());
			
			stmt.executeUpdate();
			String succesMessage = ChatColor.GREEN + "New Siege scenario has succesfully been saved to the database!";
			Bukkit.getConsoleSender().sendMessage(succesMessage);
			sc.sendMessage(Arrays.asList(succesMessage));
		} catch (SQLException e) 
		{
			sc.sendMessage(Arrays.asList(ColorOptions.error + "Error while saving the Siege scenario to the Database! Please try again or notify a developer"));
			e.printStackTrace();
			return;
		}
		
		//Retrieving the ScenarioID from the Database which has just been created
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Sieges WHERE Name = ? AND TownID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, sc.name);
			stmt.setInt(2, sc.townID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				siegeID = results.getInt("ID");
			}
			String succesMessage = ChatColor.GREEN + "New Siege scenario's ID has been retrieved from the Database: " + siegeID;
			Bukkit.getConsoleSender().sendMessage(succesMessage);
			sc.sendMessage(Arrays.asList(succesMessage));
		} catch (SQLException e) 
		{
			sc.sendMessage(Arrays.asList(ColorOptions.error + "Error while retrieving Siege ID from the Database! Please try again or notify a developer"));
			e.printStackTrace();
			return;
		}
		
		if (siegeID != -1)
		{
			List<Location> spawnpointTeam1 = sc.spawnpointTeam1;
			List<Location> spawnpointTeam2 = sc.spawnpointTeam2;
			Location mainObjective = sc.mainObjective;
			
			spawnpointNames.addAll(Arrays.asList(
					"siege." + siegeID + ".MO"
					));
			Integer soCount = 0;
			
			try
			{
				Integer team1Count = 0;
				for (Location locs : spawnpointTeam1)
				{
					spawnpoint.saveSpawnPoint("siege." + siegeID + ".1," + team1Count, "0", 0, "0", null, locs.getWorld(), locs.getX(), locs.getY(), locs.getZ(), locs.getYaw(), locs.getPitch());
					Integer spawnpointID = spawnpoint.getSpawnPointID("siege." + siegeID + ".1," + team1Count);
					
					try 
					{
						PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO ScenarioSpawnpoints(ScenarioID, SpawnPointID, TeamNumber) VALUES(?, ?, ?);");
						//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
						stmt.setInt(1, siegeID);
						stmt.setInt(2, spawnpointID);
						stmt.setInt(3, 1);
						
						stmt.executeUpdate();
						String succesMessage = ChatColor.GREEN + "Spawnpoint for siegescenario " + siegeID + " with ID " + spawnpointID + " for team 1 has succesfully been saved to the database!";
						Bukkit.getConsoleSender().sendMessage(succesMessage);
						sc.sendMessage(Arrays.asList(succesMessage));
					} catch (SQLException e) 
					{
						sc.sendMessage(Arrays.asList(ColorOptions.error + "Error while saving the Siege scenario spawnpoint with ID " + spawnpointID + " to the Database! Please try again or notify a developer"));
						e.printStackTrace();
						break;
					}
					team1Count++;
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
				return;
			}
			Integer team2Count = 0;
			for (Location locs :spawnpointTeam2)
			{
				spawnpoint.saveSpawnPoint("siege." + siegeID + ".2," + team2Count, "0", 0, "0", null, locs.getWorld(), locs.getX(), locs.getY(), locs.getZ(), locs.getYaw(), locs.getPitch());
				Integer spawnpointID = spawnpoint.getSpawnPointID("siege." + siegeID + ".2," + team2Count);
				
				try 
				{
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO ScenarioSpawnpoints(ScenarioID, SpawnPointID, TeamNumber) VALUES(?, ?, ?);");
					//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
					stmt.setInt(1, siegeID);
					stmt.setInt(2, spawnpointID);
					stmt.setInt(3, 2);
					
					stmt.executeUpdate();
					String succesMessage = ChatColor.GREEN + "Spawnpoint for siegescenario " + siegeID + " with ID " + spawnpointID + " for team 2 has succesfully been saved to the database!";
					Bukkit.getConsoleSender().sendMessage(succesMessage);
					sc.sendMessage(Arrays.asList(succesMessage));
				} catch (SQLException e) 
				{
					sc.sendMessage(Arrays.asList(ColorOptions.error + "Error while saving the Siege scenario spawnpoint with ID " + spawnpointID + " to the Database! Please try again or notify a developer"));
					e.printStackTrace();
					return;
				}
				
				team2Count++;
			}
			spawnpoint.saveSpawnPoint("siege." + siegeID + ".MO", "0", 0, "0", null, mainObjective.getWorld(), mainObjective.getX(), mainObjective.getY(), mainObjective.getZ(), mainObjective.getYaw(), mainObjective.getPitch());
			
			for (Location loc : sc.sideObjectives)
			{
				Bukkit.getConsoleSender().sendMessage("Adding side objective to name list and saving spawnpoint to DB");
				spawnpointNames.add("siege." + siegeID + ".SO." + soCount);
				spawnpoint.saveSpawnPoint("siege." + siegeID + ".SO." + soCount, "0", 0, "0", null, loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				soCount++;
			}
			
			String succesMessage = ChatColor.GREEN + "The spawnpoints of the new Siege Scenario with ID " + siegeID + " are saved to the Database!";
			Bukkit.getConsoleSender().sendMessage(succesMessage);
			sc.sendMessage(Arrays.asList(succesMessage));
		}
		
		new BukkitRunnable()
		{
			public void run()
			{
				boolean noErrors = true;
				Integer siegeID = null;
				
				for (String spawnpointName : spawnpointNames)
				{
					Integer ID = spawnpoint.getSpawnPointID(spawnpointName);
					siegeID = Integer.valueOf(spawnpointName.split("\\.")[1]);
					
					if (ID != null)
					{
						String columnName = "Team1";
						
						if (spawnpointName.contains(".2"))
						{
							columnName = "Team2";
						} else if (spawnpointName.contains(".MO"))
						{
							columnName = "MainObjective";
						} else if (spawnpointName.contains(".SO"))
						{
							Bukkit.getConsoleSender().sendMessage("SideObjective name found and trying to save to siegeobjectives!");
							try
							{
								PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO SiegeObjectives(SiegeID, SpawnpointID) VALUES(?, ?);");
								//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
								stmt.setInt(1, siegeID);
								stmt.setInt(2, ID);
								
								stmt.executeUpdate();
							} catch (Exception ex)
							{
								sc.sendMessage(Arrays.asList(ColorOptions.error + "Error while adding Side objective with spawnpointID " + ID + " for Siege " + siegeID));
								ex.printStackTrace();
								noErrors = false;
							}
							continue;
						}
						try
						{
							PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Sieges SET " + columnName + " = ? WHERE ID = ?");
							//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
							stmt.setInt(1, ID);
							stmt.setInt(2, siegeID);
							
							stmt.executeUpdate();
						} catch (Exception ex)
						{
							sc.sendMessage(Arrays.asList(ColorOptions.error + "Error while adding " + columnName + " with spawnpointID " + ID + " for Siege " + siegeID));
							ex.printStackTrace();
							noErrors = false;
						}
					}
				}
				
				if (noErrors)
				{
					String succesMessage = ColorOptions.messageachievement + "Succesfully added all spawnpoints to new Siege " + siegeID;
					Bukkit.getConsoleSender().sendMessage(succesMessage);
					sc.sendMessage(Arrays.asList(succesMessage));
				}
			}
		}.runTaskLaterAsynchronously(main, 2*20);
	}
	
	public static void saveAll()
	{
		for (SiegeScenario scenario : Sieges.Scenarios)
		{
			scenario.remove();
		}
	}
	
	public static void saveScenario(SiegeScenario scenario)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Sieges SET Name = ?, TownID = ?, PlayersMin = ?, PlayersMax = ? WHERE ID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, scenario.getName());
			stmt.setInt(2, scenario.getTownID());
			stmt.setInt(3, scenario.getPlayersMin());
			stmt.setInt(4, scenario.getPlayersMax());
			stmt.setInt(5, scenario.getID());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Scenario " + scenario.getEntryTitle() + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void retrieveTownScenarios(int townID)
	{
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Sieges Where TownID=?;");	
			stmt.setInt(1, townID);

			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				boolean createInstance = true;
				for (SiegeScenario scenario : Sieges.Scenarios)
				{
					if (scenario.getID() == results.getInt("ID"))
					{
						createInstance = false;
						break;
					}
				}
				if (createInstance)
				{
					instantiateScenario(results.getInt("ID"), false);
				}
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static SiegeScenario instantiateScenario(int scenarioID, boolean newScenario)
	{
		SiegeScenario scenario = null;
		
		if (Scenarios.findScenario(scenarioID) != null)
		{
			scenario = Scenarios.findScenario(scenarioID);
			return scenario;
		}
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Sieges WHERE ID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, scenarioID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				scenario = new SiegeScenario(results.getInt("ID"), results.getString("Name"), results.getInt("TownID"), results.getInt("PlayersMin"), results.getInt("PlayersMax"), results.getInt("MainObjective"), newScenario);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return scenario;
	}
	
	public static boolean existScenario(String name, Integer townID)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Sieges WHERE Name = ? AND TownID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, name);
			stmt.setInt(2, townID);
			
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
	
	public static int getScenarioID(String name, Integer townID)
	{
		int scenarioID = -1;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Sieges WHERE Name = ? AND TownID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, name);
			stmt.setInt(2, townID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				scenarioID = results.getInt("ID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return scenarioID;
	}
	
	public static SiegeScenario findScenario(String name, int townID)
	{
		SiegeScenario scenario = null;
		
		for (SiegeScenario scenarios : Sieges.Scenarios)
		{
			if (scenarios.getName().equalsIgnoreCase(name) && scenarios.getTownID() == townID)
			{
				scenario = scenarios;
			}
		}
		
		return scenario;
	}
	
	public static SiegeScenario findScenario(int ID)
	{
		SiegeScenario scenario = null;
		
		for (SiegeScenario scenarios : Sieges.Scenarios)
		{
			if (scenarios.getID() == ID)
			{
				scenario = scenarios;
			}
		}
		
		return scenario;
	}
	
	public static List<Integer> getIDList()
	{
		List<Integer> IDList = new ArrayList<Integer>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Sieges");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				IDList.add(results.getInt("ID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return IDList;
	}
	
	public static void instantiateAll()
	{
		List<Integer> IDList = getIDList();
		
		for (Integer ID : IDList)
		{
			if (findScenario(ID) == null)
			{
				instantiateScenario(ID, false);
			}
		}
	}
	
	public static void destroyScenario(SiegeScenario scenario)
	{
		scenario = null;
		System.gc();
	}
}
