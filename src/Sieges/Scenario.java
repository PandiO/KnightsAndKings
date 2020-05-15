package Sieges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.managers.RegionManager;

import DataManager.Districts;
import DataManager.Worldguard;
import Handlers.ColorOptions;
import Main.Main;
import Minigames.Participant;
import Minigames.SiegeTeam;
import Models.Town;
import Models.Structures.Gate;
import Models.district.District;
import SpawnPoints.SpawnPoint;
import Users.User;

public class Scenario 
{
	SpawnPoint spawnpoint = new SpawnPoint();
	Main main = Main.getPlugin(Main.class);
	/**
	 * This gateRegion states which location the siege game will be played in
	 */
	protected int ID;
	protected String name;
	protected Models.Town town;
	protected String townName;
	protected int entryTitle;
	protected int playersMin;
	protected int playersMax;
	protected int expRewardWin;
	protected int coinRewardWin;
	protected int expRewardSideObjective;
	protected int coinRewardSideObjective;
	protected int expRewardCapture;
	protected int coinRewardCapture;
	protected Integer mainObjectiveID;
	protected MainObjective mainObjective;
	protected CopyOnWriteArrayList<SideObjective> sideObjectives = new CopyOnWriteArrayList<SideObjective>();
	protected ChatColor team1Color = ColorOptions.KAKColor;
	protected ChatColor team2Color = ColorOptions.error;
	protected int Spawn1Amount;
	protected int Spawn2Amount;
	protected List<SiegeSpawnpoint> team1Spawnpoints = new ArrayList<SiegeSpawnpoint>();
	protected List<SiegeSpawnpoint> team2Spawnpoints = new ArrayList<SiegeSpawnpoint>();
	protected List<District> districts = new ArrayList<District>();
	protected HashMap<User, SiegeObject> editMode = new HashMap<User, SiegeObject>();
	protected List<User> testingList = new ArrayList<User>();
	//Gates inside the area of the scenario but not registered as objective
	protected HashMap<Gate, Boolean> nonPlayingGates = new HashMap<Gate, Boolean>();
	
	/**
	 * Siege minigame related variables
	 */
	protected boolean active;
	protected Siege siege;
	protected List<Participant> votes = new ArrayList<Participant>();
	
	public Scenario(int ID, 
			String name, 
			int playersMin, 
			int playersMax, 
			int expRewardWin, 
			int coinRewardWin, 
			int expRewardSideObjective, 
			int coinRewardSideObjective, 
			int expRewardCapture, 
			int coinRewardCapture, 
			int mainObjectiveID, 
			boolean newScenario)
	{
		this.ID = ID;
		this.name = name;
		this.playersMin = playersMin;
		this.playersMax = playersMax;
		this.mainObjectiveID = mainObjectiveID;
		this.expRewardWin = expRewardWin;
		this.coinRewardWin = coinRewardWin;
		this.expRewardSideObjective = expRewardSideObjective;
		this.coinRewardSideObjective = coinRewardSideObjective;
		this.expRewardCapture = expRewardCapture;
		this.coinRewardCapture = coinRewardCapture;
		
		Main.logMessage("Rewards: " + expRewardWin);
		
		try
		{
			this.mainObjective = new MainObjective(this.ID, this.mainObjectiveID);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		try
		{
			this.sideObjectives = this.fetchSideObjectives();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		try
		{
			this.team1Spawnpoints = this.fetchTeam1Spawnpoints();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		try
		{
			this.team2Spawnpoints = this.fetchTeam2Spawnpoints();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		this.active = false;
		
		this.districts = this.fetchDistricts();
		
		Location moLoc = this.mainObjective.getLocation();
		this.town = DataManager.Towns.FindTown(Worldguard.getStructureIDbyRegion("town", moLoc, Worldguard.getRegionManager(moLoc.getWorld())));
		this.entryTitle = this.fetchEntryTitle();
		
		Sieges.Scenarios.add(this);
		
		Main.logMessage("Rewards: " + expRewardWin);

	}
	
	public int getID()
	{
		return this.ID;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Town getTown()
	{
		return this.town;
	}
	
	public String getTownName()
	{
		return this.townName;
	}
	
	public int getEntryTitle()
	{
		return this.entryTitle;
	}
	
	public int getPlayersMin()
	{
		return this.playersMin;
	}
	
	public int getPlayersMax()
	{
		return this.playersMax;
	}
	
	public Integer getMainObjectiveID()
	{
		return this.mainObjectiveID;
	}
	
	public List<SiegeSpawnpoint> getTeam1Spawnpoints()
	{
		return this.team1Spawnpoints;
	}
	
	public List<SiegeSpawnpoint> getTeam2Spawnpoints()
	{
		return this.team2Spawnpoints;
	}
	
	public List<SiegeSpawnpoint> getTeamSpawnpoints(Integer teamNumber)
	{
		List<SiegeSpawnpoint> spawnpoints = null;
		
		if (teamNumber == 1)
		{
			spawnpoints = this.getTeam1Spawnpoints();
		} else if (teamNumber == 2)
		{
			spawnpoints = this.getTeam2Spawnpoints();
		}
		
		return spawnpoints;
	}
	
	public List<District> getDistricts()
	{
		return this.districts;
	}
	
	public List<String> getDistrictString()
	{
		List<String> list = new ArrayList<String>();
		
		
		
		return list;
	}
	
	public ChatColor getTeam1Color()
	{
		return this.team1Color;
	}
	
	public ChatColor getTeam2Color()
	{
		return this.team2Color;
	}
	
	public int getSpawn1Amount()
	{
		return this.Spawn1Amount;
	}
	
	public int getSpawn2Amount()
	{
		return this.Spawn2Amount;
	}
	
	public boolean getActive()
	{
		return this.active;
	}
	
	public List<User> getTestingList()
	{
		return this.testingList;
	}
	
	public List<SideObjective> getSideObjectives()
	{
		return this.sideObjectives;
	}
	
	public SideObjective getSideObjective(String name)
	{
		SideObjective objective = null;
		
		for (SideObjective o : this.getSideObjectives())
		{
			if (o.getName().equalsIgnoreCase(name))
			{
				objective = o;
				break;
			}
		}
		
		return objective;
	}
	
	public MainObjective getMainObjective()
	{
		return this.mainObjective;
	}
	
	public int getCapturedSideObjectives()
	{
		int amount = 0;
		
		for (SideObjective objective : this.getSideObjectives())
		{
			if (objective.getCaptured())
			{
				amount += 1;
			}
		}
		
		return amount;
	}
	
	public Siege getSiege()
	{
		return this.siege;
	}
	
	protected CopyOnWriteArrayList<SideObjective> fetchSideObjectives()
	{
		CopyOnWriteArrayList<SideObjective> sideObjectives = new CopyOnWriteArrayList<SideObjective>();
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT Spawnpoint.ID, "
					+ "ScenarioID, "
					+ "Name, "
					+ "Spawnpoint.World, "
					+ "Spawnpoint.X, "
					+ "Spawnpoint.Y, "
					+ "Spawnpoint.Z, "
					+ "Spawnpoint.Yaw, "
					+ "Spawnpoint.Pitch "
					+ "FROM SpawnpointSiegeScenario "
					+ "INNER JOIN Spawnpoint On SpawnpointSiegeScenario.SpawnpointID = Spawnpoint.ID "
					+ "WHERE ScenarioID = ? AND Objective = true");
			stmt.setInt(1, this.ID);
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				sideObjectives.add(new SideObjective(results.getString("Name"), results.getInt("Spawnpoint.ID"), results.getInt("ScenarioID"), 
						new Location(Bukkit.getWorld(results.getString("World")), results.getDouble("X"), results.getDouble("Y"), results.getDouble("Z"), results.getFloat("Yaw"), results.getFloat("Pitch"))));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return sideObjectives;
	}
	
	protected List<SiegeSpawnpoint> fetchTeam1Spawnpoints()
	{
		List<SiegeSpawnpoint> spawnpoints = new ArrayList<SiegeSpawnpoint>();
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM SpawnpointSiegeScenario WHERE Objective = false AND ScenarioID = ? AND TeamNumber = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, this.ID);
			stmt.setInt(2, 1);
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				SiegeSpawnpoint spawnpoint = new SiegeSpawnpoint(results.getString("Name"), this.ID, results.getInt("SpawnpointID"), 1);
				spawnpoints.add(spawnpoint);
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return spawnpoints;
	}
	
	protected List<SiegeSpawnpoint> fetchTeam2Spawnpoints()
	{
		int TeamNumber = 2;
		List<SiegeSpawnpoint> spawnpoints = new ArrayList<SiegeSpawnpoint>();
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM SpawnpointSiegeScenario WHERE Objective = false AND ScenarioID = ? AND TeamNumber = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, this.ID);
			stmt.setInt(2, TeamNumber);
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				SiegeSpawnpoint spawnpoint = new SiegeSpawnpoint(results.getString("Name"), this.ID, results.getInt("SpawnpointID"), TeamNumber);
				spawnpoints.add(spawnpoint);
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return spawnpoints;
	}
	
	protected List<District> fetchDistricts()
	{
		List<District> districts = new ArrayList<District>();
		
		RegionManager manager = Worldguard.getRegionManager(this.mainObjective.getLocation().getWorld());
		
		Integer districtID = Worldguard.getStructureIDbyRegion("district", this.mainObjective.getLocation(), manager);
		District district = null;
		
		if (districtID != null)
		{
			districts.add(Districts.InstantiateDistrict(districtID, false));
		}
		
		for (SideObjective so : this.sideObjectives)
		{
			manager = Worldguard.getRegionManager(so.getLocation().getWorld());

			districtID = Worldguard.getStructureIDbyRegion("district", so.getLocation(), manager);
			
			if (districtID != null)
			{
				district = Districts.InstantiateDistrict(districtID, false);

				if (!districts.contains(district))
				{
					districts.add(district);
				}
			}
		}
		
		for (SiegeSpawnpoint sp : this.getTeam1Spawnpoints())
		{
			manager = Worldguard.getRegionManager(sp.getLocation().getWorld());

			districtID = Worldguard.getStructureIDbyRegion("district", sp.getLocation(), manager);
			
			if (districtID != null)
			{
				district = Districts.InstantiateDistrict(districtID, false);

				if (!districts.contains(district))
				{
					districts.add(district);
				}
			}
		}
		
		for (SiegeSpawnpoint sp : this.getTeam2Spawnpoints())
		{
			manager = Worldguard.getRegionManager(sp.getLocation().getWorld());

			districtID = Worldguard.getStructureIDbyRegion("district", sp.getLocation(), manager);
			
			if (districtID != null)
			{
				district = Districts.InstantiateDistrict(districtID, false);

				if (!districts.contains(district))
				{
					districts.add(district);
				}
			}
		}
		
		return districts;
	}
	
	protected Integer fetchEntryTitle()
	{
		Integer title = null;
		
		for (District district : this.districts)
		{
			if (title == null)
			{
				title = district.getTown().getRequiredTitleID();
			} else
			{
				Integer possible = district.getTown().getRequiredTitleID();
				if (possible < title)
				{
					title = possible;
				}
			}
		}
		
		return title;
	}
	
	public SiegeSpawnpoint getSiegeSpawnpoint(Integer team, Integer spawnCountID)
	{
		SiegeSpawnpoint sp = null;
		
		List<SiegeSpawnpoint> spawnpoints = this.team1Spawnpoints;
		
		if (team == 2)
		{
			spawnpoints = this.team2Spawnpoints;
		}
		
		for (SiegeSpawnpoint spawnpoint : spawnpoints)
		{
			if (spawnpoint.getSpawnCountID() == spawnCountID)
			{
				sp = spawnpoint;
				break;
			}
		}
		
		return sp;
	}
	
	public SideObjective getSideObjective(Integer subID)
	{
		SideObjective objective = null;
		
		for (SideObjective objectives : this.getSideObjectives())
		{
			if (objectives.getSubID() == subID)
			{
				objective = objectives;
				break;
			}
		}
		
		return objective;
	}
	
	public List<Participant> getVotes()
	{
		return this.votes;
	}
	
	public void setTeams(SiegeTeam team1, SiegeTeam team2)
	{
		team1.setSpawnpoints(this.team1Spawnpoints);
		team2.setSpawnpoints(this.team2Spawnpoints);
		
		List<Objective> objectives = new ArrayList<Objective>();
		for (SideObjective obj : this.getSideObjectives())
		{
			objectives.add((Objective) obj);
		}
		team1.setHeldObjectives(objectives);
		team1.addHeldObjectives((Objective) this.getMainObjective());
	}
	
	public void setVotes(Participant participant)
	{
		if (!this.votes.contains(participant))
		{
			this.votes.add(participant);
			
			if (siege != null)
			{
				if (this.siege.getRandomVotes().contains(participant))
				{
					this.siege.removeRandomVotes(participant);
				}
				for (Scenario scenarios : this.siege.getSuggestedScenarioList())
				{
					if (scenarios != this && scenarios.getVotes().contains(participant))
					{
						scenarios.removeVotes(participant);
					}
				}
			}
		}
	}
	
	public void removeVotes(Participant participant)
	{
		if (this.votes.contains(participant))
		{
			this.votes.remove(participant);
		}
	}
	
	public void setActive(boolean active, Siege siege)
	{
		this.siege = siege;
		this.active = active;
		Main.logMessage("Setting MO active..");
		mainObjective.setActive(active);
		for (SideObjective objective : this.getSideObjectives())
		{
			Main.logMessage("Setting SO " + this.getSideObjectives().indexOf(objective) + " active..");
			objective.setActive(active);
		}
		this.team1Spawnpoints.forEach(q -> q.setActive(active));
		this.team2Spawnpoints.forEach(q -> q.setActive(active));
		
		if (active)
		{
			for (District district : this.getDistricts())
			{
				for (Gate gate : district.getGateList())
				{
					if (!this.isObjectiveGate(gate))
					{
						this.nonPlayingGates.put(gate, gate.getClosed());
						gate.setClosed(false);
					}
				}
			}	
		} else
		{
			for (Gate gate : this.nonPlayingGates.keySet())
			{
				gate.setClosed(this.nonPlayingGates.get(gate));
			}
			
			this.nonPlayingGates.clear();
		}
	}
	
	public boolean isObjectiveGate(Gate gate)
	{
		boolean isObjective = false;
		
		for (SideObjective so : this.getSideObjectives())
		{
			if (so.getGate() != null)
			{
				if (so.getGate().getId() == gate.getId())
				{
					isObjective = true;
					break;
				}
			}
		}
		
		return isObjective;
	}
	
	public void addTestingList(User user)
	{
		if (!this.testingList.contains(user))
		{
			this.testingList.add(user);
			
			for (SideObjective so : this.sideObjectives)
			{
				so.addTestingList(user);
			}
			this.mainObjective.addTestingList(user);
		}
	}
	
	public void removeTestingList(User user)
	{
		if (this.testingList.contains(user))
		{
			this.testingList.remove(user);
			
			for (SideObjective so : this.sideObjectives)
			{
				so.removeTestingList(user);
			}
			this.mainObjective.removeTestingList(user);
		}
	}
	
	public void setObjectiveCaptured(Objective objective)
	{
		if (this.mainObjective == objective)
		{
			this.setComplete();
		} else if (this.sideObjectives.contains(objective))
		{
			Integer part = (int) (this.mainObjective.getOriginalCapturePoints()/5)*2;
			
			Integer sideObjectivePart = (int)part/this.sideObjectives.size();
			SideObjective so = (SideObjective) objective;
			
			if (so.getGate() != null)
			{
				so.getGate().destroyGate(false);
			}
			SiegeTeam team = this.siege.getHeldTeam(objective);
			team.removeHeldObjectives(objective);
			this.siege.GetOppositeTeam(team).addHeldObjectives(objective);
			
			this.mainObjective.setCurrentCapturePoints(this.mainObjective.getCurrentCapturePoints()-sideObjectivePart);
		}
	}
	
	public void setComplete()
	{
		if (this.active)
		{
			if (this.siege != null)
			{
				this.siege.setComplete();
			}
		}
		if (!this.testingList.isEmpty())
		{
			for (User user :this.testingList)
			{
				user.getPlayer().sendMessage(ColorOptions.message + "Siege has been won by the attackers!");
			}
		}
	}
	
	public void toggleActive(Siege siege)
	{
		if (this.active)
		{
			this.setActive(false, siege);
		} else
		{
			this.setActive(true, siege);
		}
	}
	
	public void setEditMode(User user, SiegeObject object)
	{
		if (object == null)
		{
			return;
		}
		if (!this.editMode.containsKey(user))
		{
			this.editMode.put(user, object);
		}
	}
	
	public void removeEditMode(User user)
	{
		if (this.editMode.containsKey(user))
		{
			this.editMode.remove(user);
			user.getPlayer().sendMessage(ColorOptions.messageachievement + "Stopped Scenario editing mode");
		}
	}
	
	public void removeSideObjective(CommandSender sender, SideObjective sideObjective)
	{
		boolean noWarnings = true;
		
		Integer spawnpointID = sideObjective.getSpawnpointID();
		String SideObjectiveError = ColorOptions.error + "Error while removing SideObjective " + sideObjective.getSubID() + "'s spawnpoint with ID " + spawnpointID + " from siege with ID " + this.ID;
		String removeSideObjective = ColorOptions.message + "Removed Side objective " + sideObjective.getSubID() + " from siege " + this.ID;

		try
		{
			this.spawnpoint.removeSpawnPoint(spawnpointID);
			Main.logMessage(removeSideObjective);
			sender.sendMessage(removeSideObjective);
		} catch (Exception ex)
		{
			Main.logMessage(SideObjectiveError);
			sender.sendMessage(SideObjectiveError);
			ex.printStackTrace();
			noWarnings = false;
		}
		sideObjective.remove();
		
		if (noWarnings)
		{
			sender.sendMessage(ColorOptions.messageachievement + "Completed the removal of Side Objective with no warnings");
		} else
		{
			sender.sendMessage(ColorOptions.error + "Completed the removal of Side Objective of Scenario " + this.ID + " with warnings! Please notify a developer");
		}
	}
	
	public void removeTeamSpawnpoints(CommandSender sender, int teamNumber)
	{
		boolean noWarningsAll = true;
		List<SiegeSpawnpoint> Spawnpoints = null;
		String RemoveError = ColorOptions.error + "Error while removing Team spawnpoints for all teams of siege with ID " + this.ID;
		String SuccesRemove = ColorOptions.message + "Removed Team spawnpoints for all teams of siege " + this.ID;
		
		if (teamNumber == -1)
		{
			Spawnpoints = this.team1Spawnpoints;
			Spawnpoints.addAll(this.team2Spawnpoints);
		} else if (teamNumber == 1)
		{
			Spawnpoints = this.team1Spawnpoints;
			RemoveError = ColorOptions.error + "Error while removing Team spawnpoints for team 1 of siege with ID " + this.ID;
			SuccesRemove = ColorOptions.message + "Removed Team spawnpoints for team 1 of siege " + this.ID;
		} else if (teamNumber == 2)
		{
			Spawnpoints = this.team2Spawnpoints;
			RemoveError = ColorOptions.error + "Error while removing Team spawnpoints for team 2 of siege with ID " + this.ID;
			SuccesRemove = ColorOptions.message + "Removed Team spawnpoints for team 2 of siege " + this.ID;
		}
		
		for(SiegeSpawnpoint spawnpoint : Spawnpoints)
		{
			boolean noWarnings = true;
			
			Integer spawnpointID = spawnpoint.getSpawnpointID();
			String SpawnpointError = ColorOptions.error + "Error while removing Team spawnpoint " + spawnpoint.getSpawnpointID() + " with spawnpoint " + spawnpointID + " from siege with ID " + this.ID;
			String removeSpawnpoint = ColorOptions.message + "Removed Team spawnpoint " + spawnpoint.getSpawnpointID() + " from siege " + this.ID + " with spawnpoint " + spawnpointID;

			try
			{
				this.spawnpoint.removeSpawnPoint(spawnpointID);
				Main.logMessage(removeSpawnpoint);
				sender.sendMessage(removeSpawnpoint);
			} catch (Exception ex)
			{
				Main.logMessage(SpawnpointError);
				sender.sendMessage(SpawnpointError);
				ex.printStackTrace();
				noWarnings = false;
			}
			spawnpoint.remove();
			
			if (noWarnings)
			{
				sender.sendMessage(ColorOptions.messageachievement + "Completed the removal of a Team spawnpoint " + spawnpoint.getSpawnpointID() + " with no warnings");
			} else
			{
				noWarningsAll = false;
				sender.sendMessage(ColorOptions.error + "Completed the removal of a Team spawnpoint " + spawnpoint.getSpawnpointID() + " of Scenario " + this.ID + " with warnings! Please notify a developer");
			}
		}
		
		if (noWarningsAll)
		{
			sender.sendMessage(SuccesRemove);
			Main.logMessage(SuccesRemove);
		} else
		{
			sender.sendMessage(RemoveError);
			Main.logError(RemoveError);
		}
	}
	
	public void removeSpawnpoint(CommandSender sender, SiegeSpawnpoint spawnpoint)
	{
		boolean noWarnings = true;
		
		Integer spawnpointID = spawnpoint.getSpawnpointID();
		String SideObjectiveError = ColorOptions.error + "Error while removing SiegeSpawnpoint " + spawnpoint.getSpawnCountID() + "'s spawnpoint with ID " + spawnpointID + " from siege with ID " + this.ID;
		String removeSideObjective = ColorOptions.message + "Removed Spawnpoint " + spawnpoint.getSpawnCountID() + " of team " + spawnpoint.getTeamNumber() + " from siege " + this.ID;

		try
		{
			this.spawnpoint.removeSpawnPoint(spawnpointID);
			Main.logMessage(removeSideObjective);
			sender.sendMessage(removeSideObjective);
		} catch (Exception ex)
		{
			Main.logMessage(SideObjectiveError);
			sender.sendMessage(SideObjectiveError);
			ex.printStackTrace();
			noWarnings = false;
		}
		spawnpoint.remove();
		
		if (noWarnings)
		{
			sender.sendMessage(ColorOptions.messageachievement + "Completed the removal of Spawnpoint with no warnings");
		} else
		{
			sender.sendMessage(ColorOptions.error + "Completed the removal of Spawnpoint of Scenario " + this.ID + " with warnings! Please notify a developer");
		}
	}
	
	public void teleportSpawnpoint(User user, SiegeSpawnpoint spawnpoint)
	{
		user.getPlayer().teleport(spawnpoint.getLocation());
		user.getPlayer().sendMessage(ColorOptions.message + "Teleported to the spawnpoint of team " + spawnpoint.getTeamNumber());
	}
	
	public void teleportObjective(User user, Objective objective)
	{
		String objectiveType = objective instanceof MainObjective ? "Main Objective" : "Side Objective";
		Location location = objective.getLocation();
		List<Location> locs = new ArrayList<Location>(Arrays.asList(
				location,
				location.clone().add(1, 0, 0),
				location.clone().subtract(1, 0, 0),
				location.clone().add(0, 0, 1),
				location.clone().subtract(0, 0, 1)
				));
		
		for (Location loc : locs)
		{
			if (this.spawnpoint.canTeleport(loc))
			{
				user.getPlayer().teleport(loc);
				user.getPlayer().sendMessage(ColorOptions.message + "Teleported to the " + objectiveType);
				return;
			}
		}
		
		user.getPlayer().sendMessage(ColorOptions.error + "Couldn't find space to teleport to near the " + objectiveType);
		user.getPlayer().sendMessage(ColorOptions.error + "The coordinates are: " + location.toString());
	}
	
	public void removePermanently(CommandSender sender)
	{
		boolean noWarnings = true;
		
		String removeTeamSpawnpointsError = ColorOptions.error + "Error while removing the spawnpoints for all teams from siege with ID " + this.ID;
		String mainObjectiveError = ColorOptions.error + "Error while removing the Main Objective from siege with ID " + this.ID;
		String removeTeamSpawnpoints = ColorOptions.message + "Removed the spawnpoints of all teams from siege " + this.ID;
		String removeMainObjective = ColorOptions.message + "Removed the Main Objective from siege " + this.ID;
		String removeSiege = ColorOptions.messageachievement + "Removed siege with ID " + this.ID + " from the Database!";
		String spawnpointCleanup = ColorOptions.messageachievement + "Cleared all spawnpoints related to this siege";
		String spawnpointCleanupError = ColorOptions.error + "Error while cleaning up spawnpoints related to this siege!";
		
		for (SideObjective sideObjective : this.sideObjectives)
		{
			this.removeSideObjective(sender, sideObjective);
		}
		
		try
		{
			this.removeTeamSpawnpoints(sender, -1);
			Main.logMessage(removeTeamSpawnpoints);
			sender.sendMessage(removeTeamSpawnpoints);
		} catch (Exception ex)
		{
			Main.logError(removeTeamSpawnpointsError);
			sender.sendMessage(removeTeamSpawnpointsError);
			ex.printStackTrace();
			noWarnings = false;
		}
		try
		{
			this.spawnpoint.removeSpawnPoint(this.mainObjective.getSpawnpointID());
			Main.logMessage(removeMainObjective);
			sender.sendMessage(removeMainObjective);
		} catch (Exception ex)
		{
			Main.logMessage(mainObjectiveError);
			sender.sendMessage(mainObjectiveError);
			ex.printStackTrace();
			noWarnings = false;
		}
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Sieges WHERE ID=?;");
			stmt.setInt(1, this.ID);
			
			stmt.executeUpdate();
			Main.logMessage(removeSiege);
			sender.sendMessage(removeSiege);
		} catch (Exception ex)
		{
			Main.logMessage(mainObjectiveError);
			sender.sendMessage(mainObjectiveError);
			ex.printStackTrace();
			noWarnings = false;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("DELETE FROM SpawnPoint WHERE Name LIKE ?;");
			stmt.setString(1, "%siege." + this.ID + "%");
			
			stmt.executeUpdate();
			Main.logMessage(spawnpointCleanup);
			sender.sendMessage(spawnpointCleanup);
		} catch (Exception ex)
		{
			Main.logError(spawnpointCleanupError);
			sender.sendMessage(spawnpointCleanupError);
			ex.printStackTrace();
		}
		
		if (noWarnings)
		{
			sender.sendMessage(ColorOptions.messageachievement + "Completed the removal of siege " + this.ID + " with no warnings");
		} else
		{
			sender.sendMessage(ColorOptions.error + "Completed the removal of siege " + this.ID + " with warnings! Please notify a developer");
		}
		this.destroy();
	}
	
	public void save()
	{
		for (SideObjective objective : this.getSideObjectives())
		{
			objective.saveSideObjective();
		}
		Scenarios.saveScenario(this);
	}
	
	protected void destroy()
	{
		Main.logMessage("Rewards: " + expRewardWin);

		for (SideObjective objective : this.getSideObjectives())
		{
			objective.getBannerBlock().setType(Material.AIR);
//			objective.saveSideObjective();
		}
		this.mainObjective.getBannerBlock().setType(Material.AIR);
		Scenarios.saveScenario(this);
		Main.logMessage("Rewards: " + expRewardWin);

		Sieges.Scenarios.remove(this);
		Scenarios.destroyScenario(this);
	}
}
