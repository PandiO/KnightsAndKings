package Sieges;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import DataManager.Creations;
import DataManager.spawnpoints.SpawnpointSiegeScenarios;
import DataManager.spawnpoints.Spawnpoints;
import Handlers.ColorOptions;
import Main.Main;
import SpawnPoints.SpawnPoint;

public class Scenarios 
{
	static Main main = Main.getPlugin(Main.class);
	static SpawnPoint spawnpoint = new SpawnPoint();
	
	public static void createScenario(ScenarioCreation sc)
	{
		int siegeID = -1;
		List<String> spawnpointNames = new ArrayList<String>();
		
		//Creating the actual Scenario in the Database
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Sieges(Name, TownID, PlayersMin, PlayersMax, Team1, Team2, ExpRewardWin, CoinRewardWin, ExpRewardSideObjective, CoinRewardSideObjective, ExpRewardCapture, CoinRewardCapture) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, sc.name);
			stmt.setInt(2, sc.townID);
			stmt.setInt(3, sc.playersMin);
			stmt.setInt(4, sc.playersMax);
			stmt.setInt(5, sc.getSpawnpointTeam1().size());
			stmt.setInt(6, sc.getSpawnpointTeam2().size());
			stmt.setInt(7, sc.expRewardWin);
			stmt.setInt(8, sc.coinRewardWin);
			stmt.setInt(9, sc.expRewardSideObjective);
			stmt.setInt(10, sc.coinRewardSideObjective);
			stmt.setInt(11, sc.expRewardCapture);
			stmt.setInt(12, sc.coinRewardCapture);
			
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
			List<Location> spawnpointTeam1 = sc.getSpawnpointTeam1();
			List<Location> spawnpointTeam2 = sc.getSpawnpointTeam2();
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
					spawnpoint.saveSpawnPoint("siege." + siegeID + ".1," + team1Count, "0", 0, "0", locs.getWorld(), locs.getX(), locs.getY(), locs.getZ(), locs.getYaw(), locs.getPitch());
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
				spawnpoint.saveSpawnPoint("siege." + siegeID + ".2," + team2Count, "0", 0, "0", locs.getWorld(), locs.getX(), locs.getY(), locs.getZ(), locs.getYaw(), locs.getPitch());
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
			spawnpoint.saveSpawnPoint("siege." + siegeID + ".MO", "0", 0, "0", mainObjective.getWorld(), mainObjective.getX(), mainObjective.getY(), mainObjective.getZ(), mainObjective.getYaw(), mainObjective.getPitch());
			
			for (TempSideObjective so : sc.getSideObjectives())
			{
				Location loc = so.location;
				Bukkit.getConsoleSender().sendMessage("Adding side objective to name list and saving spawnpoint to DB");
				spawnpointNames.add("siege." + siegeID + ".SO." + soCount);
				spawnpoint.saveSpawnPoint("siege." + siegeID + ".SO." + soCount, "0", 0, "0", loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
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
								sc.sendMessage(Arrays.asList(ColorOptions.error + "Error while adding Side objective with spawnpoint " + ID + " for Siege " + siegeID));
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
							sc.sendMessage(Arrays.asList(ColorOptions.error + "Error while adding " + columnName + " with spawnpoint " + ID + " for Siege " + siegeID));
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
	
	public static void CreateScenario(ScenarioCreation creation)
	{
		boolean completeSteps = true;
		Player player = creation.getUser().getPlayer();
		Integer mainObjectiveID = null;
		Integer scenarioID = null;
		
		try
		{	
			Location mo = creation.mainObjective;
			CallableStatement stmt = Main.getConnection().prepareCall("{CALL addSpawnpoint(?, ?, ?, ?, ?, ?, ?)}");
			stmt.setString(1, mo.getWorld().getName());
			stmt.setDouble(2, mo.getX());
			stmt.setDouble(3, mo.getY());
			stmt.setDouble(4, mo.getZ());
			stmt.setFloat(5, mo.getYaw());
			stmt.setFloat(6, mo.getPitch());
			stmt.registerOutParameter(7, Types.INTEGER);
			
			Main.logMessage(stmt.toString());
			
			ResultSet results = stmt.executeQuery();
						
			if (results.next())
			{
				mainObjectiveID = results.getInt("spawnpointID");
				String message = ColorOptions.messageachievement + "Succesfully saved the spawnpoint for the Main Objective to the Database";
				Main.logMessage(message);
				player.sendMessage(message);
			} else
			{
				throw new Exception("Error while saving Spawnpoint for Main Objective to the Database");
			}
		} catch (Exception ex)
		{
			String message = ColorOptions.error + "Error while saving the spawnpoint for the Main Objective to the Database";
			Main.logError(message);
			player.sendMessage(message);
			ex.printStackTrace();
			completeSteps = false;
			return;
		}
		
		try
		{
			CallableStatement stmt = Main.getConnection().prepareCall("CALL addSiegeScenario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			stmt.setString(1, creation.name);
			stmt.setInt(2, creation.playersMin);
			stmt.setInt(3, creation.playersMax);
			stmt.setInt(4, creation.expRewardWin);
			stmt.setInt(5, creation.coinRewardWin);
			stmt.setInt(6, creation.expRewardSideObjective);
			stmt.setInt(7, creation.coinRewardSideObjective);
			stmt.setInt(8, creation.expRewardCapture);
			stmt.setInt(9, creation.coinRewardCapture);
			stmt.setInt(10, mainObjectiveID);
			stmt.setInt(11, 1);
			
			Main.logMessage(stmt.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				scenarioID = results.getInt("scenarioID");
				String message = ColorOptions.messageachievement + "Succesfully saved the Scenario to the Database";
				Main.logMessage(message);
				player.sendMessage(message);
			} else
			{
				throw new Exception("Error while saving the Scenario to the Database");
			}
		} catch (Exception ex)
		{
			String message = ColorOptions.error + "Error while saving the Scenario to the Database";
			Main.logError(message);
			player.sendMessage(message);
			ex.printStackTrace();
			completeSteps = false;
			return;
		}
		
		for (Location loc : creation.getSpawnpointTeam1())
		{
			try
			{
				CallableStatement stmt = Main.getConnection().prepareCall("CALL addSpawnpointSiegeScenario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				stmt.setInt(1, scenarioID);
				stmt.setString(2, loc.getWorld().getName());
				stmt.setDouble(3, loc.getX());
				stmt.setDouble(4, loc.getY());
				stmt.setDouble(5, loc.getZ());
				stmt.setFloat(6, loc.getYaw());
				stmt.setFloat(7, loc.getPitch());
				stmt.setBoolean(8, false);
				stmt.setInt(9, 1);
				stmt.setInt(10, 1);
				
				ResultSet results = stmt.executeQuery();
				
				if (results.next())
				{
					String message = ColorOptions.messageachievement + "Succesfully saved a Spawnpoint for Team 1 to the Database (ID " + results.getInt("spawnpointID") + "). " + creation.getSpawnpointTeam1().indexOf(loc) + "/" + creation.getSpawnpointTeam1().size();
					Main.logMessage(message);
					player.sendMessage(message);
				} else
				{
					throw new Exception("Error while saving TeamSpawnpoint to the Database");
				}
			} catch (Exception ex)
			{
				String message = ColorOptions.error + "Error while saving a Spawnpoint for Team 1 to the Database";
				Main.logError(message);
				player.sendMessage(message);
				ex.printStackTrace();
				completeSteps = false;
			}
		}
		
		for (Location loc : creation.getSpawnpointTeam2())
		{
			try
			{
				CallableStatement stmt = Main.getConnection().prepareCall("CALL addSpawnpointSiegeScenario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				stmt.setInt(1, scenarioID);
				stmt.setString(2, loc.getWorld().getName());
				stmt.setDouble(3, loc.getX());
				stmt.setDouble(4, loc.getY());
				stmt.setDouble(5, loc.getZ());
				stmt.setFloat(6, loc.getYaw());
				stmt.setFloat(7, loc.getPitch());
				stmt.setBoolean(8, false);
				stmt.setInt(9, 2);
				stmt.setInt(10, 1);
				
				ResultSet results = stmt.executeQuery();
				
				if (results.next())
				{
					String message = ColorOptions.messageachievement + "Succesfully saved a Spawnpoint for Team 2 to the Database (ID " + results.getInt("spawnpointID") + "). " + creation.getSpawnpointTeam1().indexOf(loc) + "/" + creation.getSpawnpointTeam1().size();
					Main.logMessage(message);
					player.sendMessage(message);
				} else
				{
					throw new Exception("Error while saving TeamSpawnpoint to the Database");
				}
			} catch (Exception ex)
			{
				String message = ColorOptions.error + "Error while saving a Spawnpoint for Team 2 to the Database";
				Main.logError(message);
				player.sendMessage(message);
				ex.printStackTrace();
				completeSteps = false;
			}
		}
		
		for (TempSideObjective so : creation.getSideObjectives())
		{
			Location loc = so.location;
			try
			{
				CallableStatement stmt = Main.getConnection().prepareCall("CALL addSpawnpointSiegeScenario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				stmt.setInt(1, scenarioID);
				stmt.setString(2, loc.getWorld().getName());
				stmt.setDouble(3, loc.getX());
				stmt.setDouble(4, loc.getY());
				stmt.setDouble(5, loc.getZ());
				stmt.setFloat(6, loc.getYaw());
				stmt.setFloat(7, loc.getPitch());
				stmt.setBoolean(8, true);
				stmt.setInt(9, -1);
				stmt.registerOutParameter(10, Types.INTEGER);
				
				ResultSet results = stmt.executeQuery();
				
				if (results.next())
				{
					String message = ColorOptions.messageachievement + "Succesfully saved a Side Objective to the Database (ID " + results.getInt("spawnpointID") + "). " + creation.getSideObjectives().indexOf(so) + "/" + creation.getSideObjectives().size();
					Main.logMessage(message);
					player.sendMessage(message);
				} else
				{
					throw new Exception("Error while saving Side Objective to the Database");
				}
			} catch (Exception ex)
			{
				String message = ColorOptions.error + "Error while saving a Side Objective to the Database";
				Main.logError(message);
				player.sendMessage(message);
				ex.printStackTrace();
				completeSteps = false;
			}
		}
		
		if (completeSteps)
        {
        	String message = ChatColor.GREEN + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for Siege Scenario " + scenarioID + " has succesfully been completed!";
        	Main.logMessage(message);
        	player.sendMessage(message);
        } else
        {
        	RemoveScenario(null, scenarioID);
        	String message = ColorOptions.error + ColorOptions.messageArrow + ChatColor.BOLD + "Creation progress for Siege Scenario " + scenarioID + " has completed with Errors!";
        	Main.logError(message);
        	player.sendMessage(message);
        }
        Creations.DestroyCreation(creation);
	}
	
	public static void RemoveScenario(CommandSender sender, Integer scenarioID)
	{
		boolean completeSteps = true;
		
		try
		{
			Spawnpoints.RemoveSpawnpoint(FetchMainObjectiveID(scenarioID));
			String message = ColorOptions.messageachievement + "Succesfully removed the Main Objective Spawnpoint from the Database";
			Main.logMessage(message);
			sender.sendMessage(message);
		} catch (Exception ex)
		{
			String message = ColorOptions.error + "Error while removing the Main Objective Spawnpoint from the Database";
			Main.logMessage(message);
			sender.sendMessage(message);
			ex.printStackTrace();
			completeSteps = false;
		}
		
		List<Integer> list = SpawnpointSiegeScenarios.FetchSpawnpointIDs(scenarioID);
		for (Integer spawnpointID : list)
		{
			try
			{
				Spawnpoints.RemoveSpawnpoint(spawnpointID);
				String message = ColorOptions.messageachievement + "Succesfully removed a Side Objective or Team Spawnpoint from the Database " + list.indexOf(spawnpointID) + "/" + list.size();
				Main.logMessage(message);
				sender.sendMessage(message);
			} catch (Exception ex)
			{
				String message = ColorOptions.error + "Error while removing a Side Objective or Team Spawnpoint from the Database";
				Main.logError(message);
				sender.sendMessage(message);
				ex.printStackTrace();
				completeSteps = false;
			}
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM SiegeScenario WHERE ID = ?");
			stmt.setInt(1, scenarioID);
			
			stmt.executeUpdate();
			String message = ColorOptions.messageachievement + "Succesfully removed the Scenario from the Database";
			Main.logMessage(message);
			sender.sendMessage(message);
		} catch (Exception ex)
		{
			String message = ColorOptions.error + "Error while removing the Scenario from the Database";
			Main.logMessage(message);
			sender.sendMessage(message);
			ex.printStackTrace();
			completeSteps = false;
		}
		
		if (completeSteps)
        {
        	String message = ChatColor.GREEN + ColorOptions.messageArrow + ChatColor.BOLD + "Removal process of Siege Scenario " + scenarioID + " has succesfully been completed!";
        	Main.logMessage(message);
        	sender.sendMessage(message);
        } else
        {
        	String message = ColorOptions.error + ColorOptions.messageArrow + ChatColor.BOLD + "Removal process of Siege Scenario " + scenarioID + " has completed with Errors!";
        	Main.logError(message);
        	sender.sendMessage(message);
        }
	}
	
	public static void saveAll()
	{
		for (Scenario scenario : Sieges.Scenarios)
		{
			scenario.remove();
		}
	}
	
	public static void saveScenario(Scenario scenario)
	{
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE SiegeScenario SET Name = ?, "
					+ "PlayersMin = ?, "
					+ "PlayersMax = ?, "
					+ "ExpRewardWin = ?, "
					+ "CoinRewardWin = ?, "
					+ "ExpRewardSideObjective = ?, "
					+ "CoinRewardSideObjective = ?, "
					+ "ExpRewardCapture = ?, "
					+ "CoinRewardCapture = ?, "
					+ "MainObjectiveID = ? "
					+ "WHERE ID = ?");
			stmt.setString(1, scenario.getName());
			stmt.setInt(2, scenario.playersMin);
			stmt.setInt(3, scenario.playersMax);
			stmt.setInt(4, scenario.expRewardWin);
			stmt.setInt(5, scenario.coinRewardWin);
			stmt.setInt(6, scenario.expRewardSideObjective);
			stmt.setInt(7, scenario.coinRewardSideObjective);
			stmt.setInt(8, scenario.expRewardCapture);
			stmt.setInt(9, scenario.coinRewardCapture);
			stmt.setInt(10, scenario.mainObjectiveID);
			stmt.setInt(11, scenario.getID());
			
			stmt.executeUpdate();
			String message = ColorOptions.messageachievement + "Scenario " + scenario.getID() + " has succesfully been saved to the database!";
			Main.logMessage(message);
		} catch (Exception ex)
		{
			String message = ColorOptions.error + "Error while saving Scenario " + scenario.getID() + " to the Database";
			Main.logError(message);
			ex.printStackTrace();
		}
		
		try
		{
			Spawnpoints.SaveSpawnpoint(scenario.getMainObjective().spawnpoint);
			String message = ColorOptions.messageachievement + "Succesfully saved the Main Objective";
			Main.logMessage(message);
		} catch (Exception ex)
		{
			String mesasge = ColorOptions.error + "Error while saving the Main Objective to the Database";
			Main.logError(mesasge);
			ex.printStackTrace();
		}
		
		for (SideObjective so : scenario.getSideObjectives())
		{
			Spawnpoints.SaveSpawnpoint(so.spawnpoint);
		}
		
		for (SiegeSpawnpoint sp : scenario.getTeam1Spawnpoints())
		{
			Spawnpoints.SaveSpawnpoint(sp.spawnpoint);
		}
		
		for (SiegeSpawnpoint sp : scenario.getTeam2Spawnpoints())
		{
			Spawnpoints.SaveSpawnpoint(sp.spawnpoint);
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Scenario " + scenario.getEntryTitle() + " has succesfully been saved to the database!");
	}
	
	public static void retrieveTownScenarios(int townID)
	{
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("Select * From SiegeScenario Where TownID=?;");	
			stmt.setInt(1, townID);

			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				boolean createInstance = true;
				for (Scenario scenario : Sieges.Scenarios)
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
	
	public static Integer FetchMainObjectiveID(Integer scenarioID)
	{
		Integer mainObjectiveID = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM SiegeScenario WHERE ID = ?");
			stmt.setInt(1, scenarioID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				mainObjectiveID = results.getInt("MainObjectiveID");
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return mainObjectiveID;
	}
	
	public static Scenario instantiateScenario(int scenarioID, boolean newScenario)
	{
		Scenario scenario = null;
		
		if (Scenarios.findScenario(scenarioID) != null)
		{
			scenario = Scenarios.findScenario(scenarioID);
			return scenario;
		}
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM SiegeScenario WHERE ID = ?");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, scenarioID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				scenario = new Scenario(results.getInt("ID"), 
						results.getString("Name"), 
						results.getInt("PlayersMin"), 
						results.getInt("PlayersMax"), 
						results.getInt("ExpRewardWin"), 
						results.getInt("CoinRewardWin"), 
						results.getInt("ExpRewardSideObjective"), 
						results.getInt("CoinRewardSideObjective"), 
						results.getInt("ExpRewardCapture"), 
						results.getInt("CoinRewardCapture"), 
						results.getInt("MainObjectiveID"), 
						newScenario);
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
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM SiegeScenario WHERE Name = ?;");
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
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM SiegeScenario WHERE Name = ?;");
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
	
	public static Scenario findScenario(String name, int townID)
	{
		Scenario scenario = null;
		
		for (Scenario scenarios : Sieges.Scenarios)
		{
			if (scenarios.getName().equalsIgnoreCase(name) && scenarios.getTownID() == townID)
			{
				scenario = scenarios;
			}
		}
		
		return scenario;
	}
	
	public static Scenario findScenario(int ID)
	{
		Scenario scenario = null;
		
		for (Scenario scenarios : Sieges.Scenarios)
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
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM SiegeScenario");
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
	
	public static void destroyScenario(Scenario scenario)
	{
		scenario = null;
		System.gc();
	}
}
