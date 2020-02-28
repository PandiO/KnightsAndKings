package Sieges;

import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Minigames.Participant;
import Scoreboards.ActionBar;
import Users.User;

public class SiegeMember extends Participant
{
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
		player.teleport(spawnpoint.getLocation());
		ActionBar spawnMSG = new ActionBar(ColorOptions.message + "You have spawned");
		spawnMSG.sendToPlayer(player);
	}
}
