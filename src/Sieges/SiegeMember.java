package Sieges;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;
import Minigames.Participant;
import Minigames.SiegeTeam;
import Scoreboards.ActionBar;
import SpawnPoints.SpawnPoint;
import Users.User;

public class SiegeMember extends Participant
{
	protected SpawnPoint spawnpoint = new SpawnPoint();
	protected SiegeSpawnpoint currentSpawnpoint;
	protected int teamNumber;
	protected List<Objective> capturedObjectives = new ArrayList<Objective>();
	
	public SiegeMember(User user) 
	{
		super(user);
	}
	
	public int getTeamNumber()
	{
		return this.teamNumber;
	}
	
	public List<Objective> getCapturedObjectives()
	{
		return this.capturedObjectives;
	}
	
	public SiegeSpawnpoint getCurrentSpawnpoint()
	{
		return this.currentSpawnpoint;
	}
	
	public void setTeamNumber(Integer teamNumber)
	{
		this.teamNumber = teamNumber;
	}
	
	public SiegeTeam GetTeam()
	{
		return (SiegeTeam) this.Team;
	}
	
	public void setCurrentSpawnpoint(SiegeSpawnpoint spawnpoint)
	{
		if (this.currentSpawnpoint == spawnpoint)
		{
			return;
		}
		Main.logMessage("Spawnpoint has been set for participant: " + this.getUser().getUsername());
		this.currentSpawnpoint = spawnpoint;
	}
	
	public void spawnMember(SiegeSpawnpoint spawnpoint)
	{
		Player player = this.getUser().getPlayer();
		Location location = spawnpoint.getLocation();
		
		this.spawnpoint.TeleportNearby(3, this.getUser(), location, this.GetTeam().GetMemberUsers());
		ActionBar spawnMSG = new ActionBar(ColorOptions.message + "You have spawned");
		spawnMSG.sendToPlayer(player);
	}
	
	public boolean isSafe()
	{
		boolean safe = false;
		
		for (SiegeSpawnpoint s : this.GetTeam().getSpawnpoints())
		{
			if (s.getLocation().distance(this.getUser().getPlayer().getLocation()) <= 4)
			{
				safe = true;
				break;
			}
		}
		
		return safe;
	}
	
	public void addCapturedObjective(Objective objective)
	{
		if (!this.capturedObjectives.contains(objective))
		{
			this.capturedObjectives.add(objective);
		}
	}
	
	public void removeCapturedObjective(Objective objective)
	{
		if (this.capturedObjectives.contains(objective))
		{
			this.capturedObjectives.remove(objective);
		}
	}
}
