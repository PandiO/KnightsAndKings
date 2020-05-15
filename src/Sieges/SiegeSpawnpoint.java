package Sieges;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Minigames.Participant;
import Users.User;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class SiegeSpawnpoint extends SiegeObject
{
	protected String name;
	protected int teamNumber;
	protected int spawnCountID;
	protected boolean active;
	protected BukkitTask circleTask;
	protected int safeZoneRadius = 4;
	private SiegeSpawnpoint instance;
	
	public SiegeSpawnpoint(String name, int scenarioID, int spawnpointID, int teamNumber)
	{
		super(scenarioID, spawnpointID);
		
		this.name = name;
		this.teamNumber = teamNumber;
		this.active = false;
		this.instance = this;
	}
	
	public int getTeamNumber()
	{
		return this.teamNumber;
	}
	
	public int getSpawnCountID()
	{
		return this.spawnCountID;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public boolean getActive()
	{
		return this.active;
	}
	
	public void setActive(boolean active)
	{
		boolean changedValue = false;
		
		if (this.active != active)
		{
			changedValue = true;
		}
		this.active = active;
		
		if (this.active && changedValue)
		{
			this.startCircleTask();
		} else if (!this.active && changedValue)
		{			
			this.stopCircleTask();
		}
	}
	
	public void stopCircleTask()
	{
		if (this.circleTask != null)
		{
			this.circleTask.cancel();
		}
	}
	
	public void startCircleTask()
	{
		List<User> receivers = new ArrayList<User>();
		
		receivers.addAll(this.getPlayers());
				
		double originalX = this.getLocation().getX();
		double originalZ = this.getLocation().getZ();
		
		this.circleTask = new BukkitRunnable()
		{
			public void run()
			{
				for(double i = 0; i < 360.0; i+=4) {
					double angle = i * Math.PI / 180;
	                double x = safeZoneRadius * Math.cos(angle);
	                double z = safeZoneRadius * Math.sin(angle);
	                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.VILLAGER_HAPPY,true, (float) (originalX+x), (float) (getLocation().getY()+0.1), (float) (originalZ+z), 0, 0, 0, 0, 1);
		            for(User participant : receivers) {
		                ((CraftPlayer)participant.getPlayer()).getHandle().playerConnection.sendPacket(packet);
		            }
	            }
			}
		}.runTaskTimerAsynchronously(main, 0, 2*20);
	}
	
	public List<User> getPlayers()
	{
		List<User> receivers = new ArrayList<User>();
		
		Scenario scenario = Scenarios.findScenario(this.scenarioID);
		
		for (Siege sieges : Sieges.Sieges)
		{
			if (sieges.getScenario() == scenario)
			{
				for (Participant part : sieges.getParticipants())
				{
					receivers.add(part.getUser());
				}
				break;
			}
		}

		return receivers;
	}
	
	
	
//	public void setName(String name)
//	{
//		this.name = name;
//	}
}
