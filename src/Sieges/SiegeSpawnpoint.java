package Sieges;

public class SiegeSpawnpoint extends SiegeObject
{
	protected int teamNumber;
	protected int spawnCountID;
	
	public SiegeSpawnpoint(int scenarioID, int spawnpointID, int teamNumber)
	{
		super(scenarioID, spawnpointID);
		
		this.teamNumber = teamNumber;
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
