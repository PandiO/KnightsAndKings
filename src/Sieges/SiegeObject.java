package Sieges;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import Handlers.ColorOptions;
import Main.Main;
import SpawnPoints.SpawnPoint;

public class SiegeObject 
{
	Main main = Main.getPlugin(Main.class);
	SpawnPoint spawnpoint = new SpawnPoint();
	
	protected int siegeID;
	protected int spawnpointID;
	protected int subID;
	protected Location location;
	
	public SiegeObject(int siegeID, int spawnpointID)
	{
		this.siegeID = siegeID;
		this.spawnpointID = spawnpointID;
	}
	
	public int getSiegeID()
	{
		return this.siegeID;
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
		return this.location.clone().add(0.5, 0, 0.5);
	}
	
	public void fetchLocation()
	{
		try
		{
			this.location = this.spawnpoint.getSpawnPointLocation(this.spawnpointID);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void changeLocation(CommandSender sender, Location location)
	{		
		try
		{
			this.spawnpoint.saveLocation(spawnpointID, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			sender.sendMessage(ColorOptions.messageachievement + "Succesfully changed the location of the spawnpoint of this SiegeObject");
			this.location = location;
		} catch (Exception ex)
		{
			sender.sendMessage(ColorOptions.error + "Error while changing location of this SiegeObject!");
			ex.printStackTrace();
		}
	}
	
	public void remove()
	{
		SiegeScenario scenario = Scenarios.findScenario(this.siegeID);
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
