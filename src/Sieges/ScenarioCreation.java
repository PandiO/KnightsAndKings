package Sieges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Main.Main;
import Towns.Town;
import Users.User;

public class ScenarioCreation 
{
	Main main = Main.getPlugin(Main.class);
	WorldGuard worldguard = new WorldGuard();
	Town town = new Town();
	protected int Stage = -1;
	protected ScenarioCreation instance;
	
	protected User user;
	protected String name;
	protected int townID;
	protected int playersMin;
	protected int playersMax;
	protected List<Location> spawnpointTeam1 = new ArrayList<Location>();
	protected List<Location> spawnpointTeam2 = new ArrayList<Location>();
	protected Location mainObjective;
	protected List<Location> sideObjectives = new ArrayList<Location>();
	
	protected boolean confirmStop = false;
	protected BukkitTask confirmTask;
	protected boolean stashed = false;
	protected BukkitTask stashTask;
	
	protected List<String> startMessage = new ArrayList<String>(Arrays.asList(
			"",
			"",
			ColorOptions.message + "Welcome to the Siege creation menu",
			ColorOptions.message + "You can create a siege scenario by following a few steps",
			ColorOptions.message + "You can always configure the details later on",
			ColorOptions.message + "To stop the creation, type " + ColorOptions.error + "stop",
			ColorOptions.message + "To undo a step type " + ColorOptions.messageachievement + "undo",
			"",
			ColorOptions.messagesubjects + "Are you ready to start? Type " + ColorOptions.messageachievement + "start/next",
			"",
			""
			));
	
	public List<ArrayList<String>> messages = new ArrayList<ArrayList<String>>(Arrays.asList(
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messageformat + "Please type the name of the new Siege scenario and press enter",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messageformat + "Please type the minimal amount of players needed to start this siege",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messageformat + "Please type the maximal amount of players needed to start this siege",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the spawn locations of " + ColorOptions.KAKColor + "Team 1 (Defenders)",
					ColorOptions.messageformat + "Teams can have multiple spawn locations. Players can choose where to spawn",
					ColorOptions.messageformat + "Stand on the location of the spawnpoint and type 'save'",
					ColorOptions.error + "Start with the most important spawnpoint and end with the least important one",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the spawn locations of " + ColorOptions.error + "Team 2 (Attackers)",
					ColorOptions.messageformat + "Teams can have multiple spawn locations. Players can choose where to spawn",
					ColorOptions.messageformat + "Stand on the location of the spawnpoint and type 'save'",
					ColorOptions.error + "Start with the most important spawnpoint and end with the least important one",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the main objective",
					ColorOptions.messageformat + "The main objective is the block or flag the attacking team must destroy to win",
					ColorOptions.messageformat + "Right-click a block/banner to set the main objective",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting side objectives",
					ColorOptions.messageformat + "Side objectives will grant bonusses to the attacking team when destroyed, or to the defending team if not",
					ColorOptions.messageformat + "Right-click a block/banner to set a side objective",
					ColorOptions.messageformat + "You can set as many as you want",
					"",
					ColorOptions.error + "To undo the last side objective type 'undo'",
					ColorOptions.messagesubjects + "If you have all side objectives set, type 'next'", 
					""
					))
			));
	
	public ScenarioCreation(User user)
	{
		this.user = user;
		this.instance = this;
		
		Location loc = user.getPlayer().getLocation();
		this.townID = this.worldguard.getStructureIDbyRegion("town", loc, this.worldguard.getRegionManager(loc.getWorld()));
		Sieges.ScenarioCreations.add(this);
		
		this.sendMessage(startMessage);
	}
	
	public void start()
	{
		this.nextStage(0);
	}
	
	protected void nextStage(int stage)
	{
//		if (stage <= -1)
//		{
//			stage = 0;
//			this.Stage = stage;
//		}
		if (stage == 0)
		{
			if (this.Stage == -1)
			{
				this.Stage = 0;
			}
			this.sendMessage(this.messages.get(0));
		} else if (stage == 1)
		{
			new BukkitRunnable()
			{
				public void run()
				{
					sendMessage(messages.get(1));
				}
			}.runTaskLaterAsynchronously(main, 20);
		} else if (stage == 2)
		{
			new BukkitRunnable()
			{
				public void run()
				{
					sendMessage(messages.get(2));
				}
			}.runTaskLaterAsynchronously(main, 20);
		} else if (stage == 3)
		{
			new BukkitRunnable()
			{
				public void run()
				{
					sendMessage(messages.get(3));
				}
			}.runTaskLaterAsynchronously(main, 20);
		} else if (stage == 4)
		{
			new BukkitRunnable()
			{
				public void run()
				{
					sendMessage(messages.get(4));
				}
			}.runTaskLaterAsynchronously(main, 20);
		} else if (stage == 5)
		{
			new BukkitRunnable()
			{
				public void run()
				{
					sendMessage(messages.get(5));
				}
			}.runTaskLaterAsynchronously(main, 20);
		} else if (stage == 6)
		{
			new BukkitRunnable()
			{
				public void run()
				{
					sendMessage(messages.get(6));
				}
			}.runTaskLaterAsynchronously(main, 20);
		} else if (stage == 7)
		{
			this.sendMessage(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "You have completed all steps in the siege creation",
					ColorOptions.message + "Confirm the information below",
					ColorOptions.message + "Type " + ColorOptions.messageachievement + "confirm" + ColorOptions.message + " to confirm or " + ColorOptions.error + "undo" + ColorOptions.message + " to undo",
					"",
					ColorOptions.stats + "Name: " + ColorOptions.statsresults + this.name,
					ColorOptions.stats + "Town: " + ColorOptions.statsresults + this.town.getTownName(this.townID),
					ColorOptions.stats + "Min. players: " + ColorOptions.statsresults + this.playersMin,
					ColorOptions.stats + "Max. players: " + ColorOptions.statsresults + this.playersMax,
					ColorOptions.stats + "Amount of side objectives: " + ColorOptions.statsresults + this.sideObjectives.size(),
					""
					));
		}
		this.Stage = stage;
	}
	
	protected void complete()
	{
		Scenarios.createScenario(this);
		this.user.getPlayer().sendMessage(ColorOptions.messageachievement + "Succesfully completed the Siege scenario creation!");
		this.confirmStop = true;
		this.stop();
		this.stash();
	}
	
	protected void stash()
	{
		this.stashed = true;
		Sieges.stashedSiegeCreations.add(this);
		
		this.stashTask = new BukkitRunnable()
		{
			public void run()
			{
				Sieges.stashedSiegeCreations.remove(instance);
				user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "The stashed Siege scenario is no longer stashed");
			}
		}.runTaskLaterAsynchronously(main, 300*20);
	}
	
	protected void stop()
	{
		
		if (!this.confirmStop)
		{
			this.sendMessage(Arrays.asList(
					"",
					ColorOptions.error + "Are you sure you want to stop the siege creation?",
					ColorOptions.error + "Type stop within 3 seconds to confirm",
					""
					));
			this.confirmTask = new BukkitRunnable()
			{
				public void run()
				{
					confirmStop = false;
				}
			}.runTaskLaterAsynchronously(this.main, 3*20);
			
			confirmStop = true;
		} else
		{
			Sieges.ScenarioCreations.remove(this);
			user.getPlayer().sendMessage(ColorOptions.error + "Stopped siege creation mode");
			if (this.stashed)
			{
				user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "The Siege scenario has been stashed for " + ColorOptions.messagesubjects + "5 minutes" + ColorOptions.message + " in case the save process fails");
			}
		}
	}
	
	protected void sendMessage(List<String> message)
	{
		for (String msg : message)
		{
			this.user.getPlayer().sendMessage(msg);
		}
	}
	
	protected void falseCommand(List<String> error)
	{
		List<String> errormsg = new ArrayList<String>(Arrays.asList(
				"",
				ColorOptions.error + "You are in siege scenario creation mode",
				ColorOptions.message + "Type 'stop' to stop",
				""
				));
		if (error != null)
		{
			errormsg.addAll(error);
		}
		
		this.sendMessage(errormsg);
	}
}
