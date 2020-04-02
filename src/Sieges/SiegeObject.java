package Sieges;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import DataManager.spawnpoints.Spawnpoints;
import Handlers.ColorOptions;
import Main.Main;
import Models.spawnpoint.Spawnpoint;
import SpawnPoints.SpawnPoint;

public class SiegeObject 
{
	Main main = Main.getPlugin(Main.class);
	
	protected int scenarioID;
	protected int spawnpointID;
	protected int subID;
	protected Spawnpoint spawnpoint;
	
	public SiegeObject(int siegeID, int spawnpointID)
	{
		this.scenarioID = siegeID;
		this.spawnpointID = spawnpointID;
		this.spawnpoint = Spawnpoints.InstantiateSpawnpoint(spawnpointID);
	}
	
	public int getScenarioID()
	{
		return this.scenarioID;
	}
	
	public int getSpawnpointID()
	{
		return this.spawnpointID;
	}
	
	public int getSubID()
	{
		return this.subID;
	}
	
	public Location getLocation()
	{
		return this.spawnpoint.getLocation().clone().add(0.5, 0, 0.5);
	}
	
	public void changeLocation(CommandSender sender, Location location)
	{		
		try
		{
			this.spawnpoint.setLocation(location);
			sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the location of the spawnpoint of this SiegeObject");
		} catch (Exception ex)
		{
			sender.sendMessage(ColorOptions.error + "Error while changing location of this SiegeObject!");
			ex.printStackTrace();
		}
	}
	
	public void remove()
	{
		Scenario scenario = Scenarios.findScenario(this.scenarioID);
		if (this instanceof SiegeSpawnpoint)
		{
			SiegeSpawnpoint sp = (SiegeSpawnpoint) this;
			if (sp.teamNumber == 1)
			{
				scenario.team1Spawnpoints.remove(sp);
			} else if (sp.teamNumber == 2)
			{
				scenario.team2Spawnpoints.remove(sp);
			}
		} else if (this instanceof SideObjective)
		{
			SideObjective so = (SideObjective) this;
			scenario.sideObjectives.remove(so);
		}
	}
}
