package Sieges;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Minigames.Participant;
import Scoreboards.ActionBar;
import SpawnPoints.SpawnPoint;
import Users.User;

public class SiegeMember extends Participant
{
	protected SpawnPoint spawnpoint = new SpawnPoint();
	protected SiegeSpawnpoint currentSpawnpoint;
	protected int teamNumber;
	
	public SiegeMember(User user) 
	{
		super(user);
	}
	
	public int getTeamNumber()
	{
		return this.teamNumber;
	}
	
	public SiegeSpawnpoint getCurrentSpawnpoint()
	{
		return this.currentSpawnpoint;
	}
	
	public void setTeamNumber(Integer teamNumber)
	{
		this.teamNumber = teamNumber;
	}
	
	public void setCurrentSpawnpoint(SiegeSpawnpoint spawnpoint)
	{
		if (this.currentSpawnpoint == spawnpoint)
		{
			return;
		}
		main.logMessage("Spawnpoint has been set for participant: " + this.getUser().getUsername());
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
}
