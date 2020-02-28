package Sieges;

import org.bukkit.ChatColor;

public class SiegeSpawnpoint extends SiegeObject
{
	protected int ScenarioSpawnpointID;
	protected int teamNumber;
	protected int spawnCountID;
	
	public SiegeSpawnpoint(int ScenarioSpawnpointID, int siegeID, int spawnpointID, int teamNumber)
	{
		super(siegeID, spawnpointID);
		
		this.ScenarioSpawnpointID = ScenarioSpawnpointID;
		this.teamNumber = teamNumber;
		this.fetchLocation();
	}
	
	public int getScenarioSpawnpointID()
	{
		return this.ScenarioSpawnpointID;
	}
	
	public int getTeamNumber()
	{
		return this.teamNumber;
	}
	
	public int getSpawnCountID()
	{
		return this.spawnCountID;
	}
}
