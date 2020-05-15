package Afk;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import DataManager.Users2;
import Handlers.ColorOptions;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;

public class Afk 
{
	SpawnPoint spawnpoint = new SpawnPoint();
	Main main = Main.getPlugin(Main.class);

	private User user;
	private Player Player;
	private Team team;
	private Location location;
	private Location lastLocation;
	public boolean teleporting = false;
	private BukkitTask task;
	
	public Afk(User user)
	{
		this.user = user;
		this.Player = user.getPlayer();
		this.lastLocation = this.Player.getLocation();
		Integer spawnpointID = spawnpoint.getSpawnPointID("afk");
		addAfk();
		if (spawnpointID != null)
		{
//			teleporting = true;
//			spawnpoint.teleport(player, spawnpoint.getSpawnPointLocation(spawnpoint));
		}
		this.Player.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "You are now AFK");
		this.team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(this.Player);
		this.location = this.Player.getLocation();
		this.task = playEffectTask();
		Users2.UpdateScoreBoard(null);
		AfkEvents.possibleAfk.remove(this.user);
		this.user.checkMiniGames();
	}
	
	public void playParticles()
	{
//		Location loc = this.location.clone().add(  Math.random()-0.5D,  -0.8D-(Math.random()/3-0.5D),  Math.random()-0.5D);
		Location loc = this.location.clone().add(  Math.random()-0.5D,  1,  Math.random()-0.5D);
		World w = this.location.getWorld();
		final ArmorStand arm = (ArmorStand) w.spawnEntity(loc, EntityType.ARMOR_STAND);
		if(Math.random() > 0.5){
			arm.setCustomName("Z");
		}else{
			arm.setCustomName("Zz");
		}
		arm.setCustomNameVisible(true);
		arm.setGravity(false);
		arm.setVisible(false);
		arm.setSmall(true);
		arm.setMarker(true);
		// Remove that armour stand after 1 second
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
			public void run(){
				arm.remove();
			}
		},20L);
		// Move it upwards each tick
		final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){
			public void run(){
				arm.teleport(arm.getLocation().clone().add(0,0.2,0));
			}
		}, 0L, 2L);
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
			public void run(){
				Bukkit.getScheduler().cancelTask(taskId);
			}
		}, 19L);
	}
	
	public void clearLeftovers()
	{
		for(World w: Bukkit.getWorlds())
		{
			for(Entity e: w.getEntities())
			{
				if(e.getName().equals("Z") || e.getName().equals("Zz"))
				{
					e.remove();
				}
			}
		}
	}
	
	public void addAfk()
	{
		AfkEvents.afk.add(this);
	}
	
	public void removeAfk()
	{
		AfkEvents.afk.remove(this);
	}
	
	public void stopAfk()
	{
		stopTask();
//		player.teleport(lastLocation);
		this.Player.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "You are no longer AFK");
		removeAfk();
		Users2.UpdateScoreBoard(null);
		clearLeftovers();
	}
	
	public void stopTask()
	{
		this.task.cancel();
	}
	
	public BukkitTask playEffectTask()
	{
		return new BukkitRunnable()
				{
					public void run()
					{
						playParticles();
					}
				}.runTaskTimer(main, 0, 15);
	}
	
	public User getUser()
	{
		return this.user;
	}
	
	public Player getPlayer()
	{
		return this.Player;
	}
	
	public UUID getUUID()
	{
		return this.user.getUUID();
	}
	
	public Team getTeam()
	{
		return this.team;
	}
}
