package Sieges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import Handlers.ColorOptions;
import Main.Main;
import Minigames.MiniGame;
import Minigames.Participant;
import Scoreboards.ActionBar;
import SpawnPoints.SpawnPoint;
import Users.User;

public class Siege extends MiniGame
{
	private SpawnPoint spawnpoint = new SpawnPoint();
	
	protected static String name = "Siege";	//Stores the name of the mininame
	
	protected SiegeScenario scenario;
	protected List<SiegeScenario> suggestedScenarioList = new ArrayList<SiegeScenario>();
	
	protected String team1Name = "Defenders";
	protected String team2Name = "Attackers";
	protected ChatColor team1Color = ColorOptions.KAKColor;
	protected ChatColor team2Color = ColorOptions.error;
	protected List<SiegeMember> team1 = new ArrayList<SiegeMember>();
	protected List<SiegeMember> team2 = new ArrayList<SiegeMember>();
	
	/**
	 * This region indicates if the match is skilled and for what title's it is joinable
	 */
	protected int titleIDMin = 0;
	protected int titleIDMax = 18;
	protected boolean skilledMatch = false;
	
	
	/**
	 * This region states the technical variables of the actual minigame
	 */
	protected boolean isActive = false;
	protected List<Participant> randomVotes = new ArrayList<Participant>();
	
	protected List<String> matchmakingMessage = new ArrayList<String>(Arrays.asList(
			"",
			ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "A new game of Siege now open for participation!",
			ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + "/siege" + ColorOptions.message + " to join!",
			""
			));
	protected List<String> allowedCommands = new ArrayList<String>(Arrays.asList(
			"/leave",
			"/msg",
			"/r",
			"/staffchat"
			));
	
	ActionBar startMSG = new ActionBar(ColorOptions.message + "Siege is about to begin!");
	ActionBar startMSGDefense = new ActionBar(ColorOptions.message + "Defend all flags to win!");
	ActionBar startMSGAttack = new ActionBar(ColorOptions.message + "Capture all flags to win!");
	
	
	public Siege()
	{
		super(name, "/siege join");
		
		this.instance = this;
		new BukkitRunnable()
		{
			public void run()
			{
				startMatchmaking(true);
			}
		}.runTaskLaterAsynchronously(main, 2*20);
	}
	
	public SiegeScenario getScenario()
	{
		return this.scenario;
	}
	
	public String getTeamName(Integer teamNumber)
	{
		if (teamNumber == 1)
		{
			return this.team1Color + this.team1Name;
		} else
		{
			return this.team2Color + this.team2Name;
		}
	}
	
	public List<SiegeMember> getTeam1Participants()
	{
		return this.team1;
	}
	
	public List<SiegeMember> getTeam2Participants()
	{
		return this.team2;
	}
	
	public boolean getSkilledMatch()
	{
		return this.skilledMatch;
	}
	
	public int getSkilledMin()
	{
		return this.titleIDMin;
	}
	
	public int getSkilledMax()
	{
		return this.titleIDMax;
	}
	
	public List<Participant> getRandomVotes()
	{
		return this.randomVotes;
	}
	
	public List<SiegeScenario> getSuggestedScenarioList()
	{
		return this.suggestedScenarioList;
	}
	
	public int getTeamNumber(Participant participant)
	{
		int teamNumber = 1;
		
		if (this.getTeam1Participants().contains(participant))
		{
			teamNumber = 1;
		} else if (this.getTeam2Participants().contains(participant))
		{
			teamNumber = 2;
		}
		
		return teamNumber;
	}
	
	public void setRandomVotes(Participant participant)
	{
		if (!this.randomVotes.contains(participant))
		{
			this.randomVotes.add(participant);
			for (SiegeScenario scenarios : this.suggestedScenarioList)
			{
				if (scenarios.getVotes().contains(participant))
				{
					scenarios.removeVotes(participant);
				}
			}
		}
	}
	
	public void removeRandomVotes(Participant participant)
	{
		if (this.randomVotes.contains(participant))
		{
			this.randomVotes.remove(participant);
		}
	}
	
	public void setSkilledMatch(boolean skilled)
	{
		this.skilledMatch = skilled;
	}
	
	public void setRandomSuggestedScenarios()
	{
		if (!this.suggestedScenarioList.isEmpty() && this.suggestedScenarioList.size() == 2)
		{
			return;
		}
		List<Integer> IDList = Scenarios.getIDList();
		
		if (IDList.size() >= 2)
		{
			for (int i = 0; i < 2; i++)
			{
				if (this.suggestedScenarioList.size() == 2)
				{
					return;
				}
				
				Integer scenarioID = IDList.get(main.getRandom(0, IDList.size()-1));
				SiegeScenario scenario = Scenarios.instantiateScenario(scenarioID, false);
				
				if (!this.suggestedScenarioList.contains(scenario))
				{
					this.setSuggestedScenario(scenario);
				} else
				{
					i--;
				}
				IDList.remove(scenarioID);
			}
		} else if (!IDList.isEmpty())
		{
			SiegeScenario scenario = Scenarios.instantiateScenario(IDList.get(0), false);
			if (!this.suggestedScenarioList.contains(scenario))
			{
				this.setSuggestedScenario(scenario);
			}
		} else
		{
			main.logError("Siege could not start because there are no scenario's to play");
			this.autoStart = false;
			this.stopSiege();
		}
	}
	
	public void setSuggestedScenario(SiegeScenario scenario)
	{
		if (!this.suggestedScenarioList.contains(scenario))
		{
			this.suggestedScenarioList.add(scenario);
		}
	}
	
	public void removeSuggestedScenario(SiegeScenario scenario)
	{
		if (this.suggestedScenarioList.contains(scenario))
		{
			this.suggestedScenarioList.remove(scenario);
		}
	}
	
	public void setScenario(SiegeScenario scenario)
	{
		if (this.scenario == scenario)
		{
			return;
		}
		this.scenario = scenario;
		this.entryTitle = scenario.getEntryTitle();
	}
	
	public void drawScenario()
	{
		SiegeScenario scenario = null;
		
		if (this.suggestedScenarioList.isEmpty())
		{
			scenario = this.getRandomScenario();
		}
		
		for (SiegeScenario scenarios : this.suggestedScenarioList)
		{
			if (scenario == null)
			{
				scenario = scenarios;
			} else
			{
				if (scenario.getVotes() == scenarios.getVotes())
				{
					if (main.getRandom(0, 100) <= 50)
					{
						scenario = scenarios;
					}
				} else if (scenario.getVotes().size() < scenarios.getVotes().size())
				{
					scenario = scenarios;
				}
			}
		}
		
		if (scenario.getVotes().size() < this.getRandomVotes().size())
		{
			scenario = this.getRandomScenario();
		}
		
		this.setScenario(scenario);
		this.announceParticipants(this.getParticipants(), Arrays.asList(
				"",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Scenario for upcoming Siege is drawn! Scenario: " + scenario.getName(),
				""
				));
	}
	
	public SiegeScenario getRandomScenario()
	{
		List<Integer> IDList = Scenarios.getIDList();
		Integer scenarioID = IDList.get(main.getRandom(0, IDList.size()-1));
		return Scenarios.instantiateScenario(scenarioID, false);
	}
	
	public void joinPlayer(User user)
	{
		boolean canJoin = true;
		
		if (this.scenario != null)
		{
			if (this.getParticipants().size() >= this.scenario.getPlayersMax())
			{
				canJoin = false;
				user.getPlayer().sendMessage(ColorOptions.error + "Can't join siege. Maximum amount of players for the scenario has been reached");
			}
		}
		if (this.skilledMatch)
		{
			Integer titleID = user.getTitleID();
			if (titleID < this.titleIDMin || titleID > this.titleIDMax)
			{
				canJoin = false;
				user.getPlayer().sendMessage(ColorOptions.error + "Can't join siege. Your title is too low or too high to enter");
			}
		}
		
		if (canJoin)
		{
			this.addParticipant(new SiegeMember(user));
			this.updateMenus(true);
		}
		
	}
	
	public void leavePlayer(User user)
	{
		if (this.getParticipant(user) != null)
		{
			this.removeParticipant(this.getParticipant(user));
			this.updateMenus(true);
		}
		
		if (this.progress)
		{
			if (this.getParticipants().size() < this.scenario.getPlayersMin())
			{
				this.setComplete();
			}
		}
	}
	
	public void setTeams()
	{
		//this.orderLowToHigh();
		List<Participant> participants = this.getParticipants();
		Collections.shuffle(participants);
		
		boolean team1 = true;
		for (Participant part : participants)
		{
			if (!(part instanceof SiegeMember))
			{
				part = (SiegeMember) part;
			}
			if (team1)
			{
				((SiegeMember)part).setTeamNumber(1);
				this.team1.add((SiegeMember)part);
				part.getUser().getPlayer().sendMessage(ColorOptions.KAKColor + "Your team is the " + this.team1Name);
				team1 = false;
			} else
			{
				((SiegeMember)part).setTeamNumber(2);
				this.team2.add((SiegeMember)part);
				part.getUser().getPlayer().sendMessage(ColorOptions.error + "Your team is the " + this.team2Name);
				team1 = true;
			}
		}
	}
	
	public void setOriginalSpawnpoints()
	{
		for (Participant participant : this.getParticipants())
		{
			SiegeMember member = (SiegeMember) participant;
			SiegeSpawnpoint spawnpoint = null;
			
			if (member.getTeamNumber() == 1)
			{
				spawnpoint = this.scenario.getTeam1Spawnpoints().get(0);
			} else if (member.getTeamNumber() == 2)
			{
				spawnpoint = this.scenario.getTeam2Spawnpoints().get(0);
			}
			
			member.setCurrentSpawnpoint(spawnpoint);
		}
	}
	
	public void startMatchmaking(boolean start)
	{
		this.matchmakingSeconds = this.matchmakingExpire;
		if (!start)
		{
			this.matchmaking = false;
			this.removeMatchmakingTask();
			return;
		}
		
		this.setRandomSuggestedScenarios();
		
		BukkitTask task = new BukkitRunnable()
		{
			public void run()
			{
				matchmakingSeconds--;
				
				updateMenus(false);
				
				for (Integer timeStamp : matchmakingNotifications.keySet())
				{
					if (timeStamp == matchmakingSeconds)
					{
						List<String> message = new ArrayList<String>();
						message.addAll(matchmakingNotifications.get(timeStamp));
						if (matchmakingSeconds > 10)
						{
							message.add(2, ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Voting for Siege Scenario now opened!");
						}
						announceOnline(message);
					}
				}
				
				if (matchmakingSeconds == 30)
				{
					announceParticipants(getParticipants(), Arrays.asList(
							ColorOptions.KAKFormat + ColorOptions.error + ChatColor.BOLD + "You will be teleported to the Hide and Seek hub in " + 15 + " seconds!"
							));
				}
				if (matchmakingSeconds == 15)
				{
					for (Participant participant : getParticipants())
					{
						sendToHub(participant);
					}
					inHub = true;
				}
				if (matchmakingSeconds == 10)
				{
					announceOnline(Arrays.asList(
							ColorOptions.KAKFormat + ColorOptions.error + ChatColor.BOLD + "Voting for Siege Scenario's now closed!"
							));
					setTeams();
					drawScenario();
					setOriginalSpawnpoints();
				}
				if (matchmakingSeconds == 0)
				{
					startMatchmaking(false);
					startProgress(true);
				}
			}
		}.runTaskTimerAsynchronously(main, 0, 20);
		this.announceOnline(this.matchmakingMessage);
		this.setMatchmakingTask(task, true);
		this.matchmaking = true;
		this.updateMenus(true);
	}
	
	public void startProgress(boolean start)
	{
		this.progressSeconds = this.progressExpire;
		if (!start)
		{
			this.progress = false;
			this.removeProgressTask();
			return;
		}
		
		if (this.Participants.size() < 2 || this.Participants.size() < this.scenario.getPlayersMin())
		{
			this.stopSiege();
			return;
		}
		
		for (SiegeMember siegeMember : getTeam1Participants())
		{
			siegeMember.spawnMember(siegeMember.currentSpawnpoint);
		}
		for (SiegeMember siegeMember : getTeam2Participants())
		{
			siegeMember.spawnMember(siegeMember.currentSpawnpoint);
		}
		
//		Main.logMessage("Setting board while starting siege..");
//		Scoreboards.Scoreboard board = new Scoreboards.Scoreboard();
//		board.setBoard(this.getUserParticipants());
//		Main.logMessage("Board set while starting siege..");
		
		
		BukkitTask task = new BukkitRunnable()
		{
			public void run()
			{
				announceAll(startMSG);
				
				progressSeconds--;
				
				updateMenus(false);
				
				Users.Users.updateScoreBoard(getUserParticipants());
				
//				getScenario().getMainObjective().setActive(true);
//				getScenario().getSideObjectives().forEach(q -> q.setActive(true));
				
				
				if (progressSeconds == (progressExpire-2))
				{
					announceTeam1(startMSGDefense);
					announceTeam2(startMSGAttack);
				}
				
				if (progressSeconds == 0)
				{
					stopSiege();
				}
			}
		}.runTaskTimer(main, 0, 20);
		
		this.progress = true;
		this.updateMenus(true);
	}
	
	public void startCooldown(boolean start)
	{
		if (!start)
		{
			this.cooldown = false;
			this.removeCooldownTask();
			return;
		}
		main.logMessage("Starting cooldown");
		BukkitTask task = this.cooldownTask = new BukkitRunnable()
		{
			public void run()
			{
				cooldownSeconds--;
				
				updateMenus(false);
				
				if (cooldownSeconds == 0)
				{
					startCooldown(false);
					startMatchmaking(true);
				}
			}
		}.runTaskTimerAsynchronously(main, 0, 20);
		
		this.setCooldownTask(task, true);
		this.cooldown = true;
		this.updateMenus(true);
	}
	
	public void setComplete()
	{
		this.stopSiege();
	}
	
	public void stopSiege()
	{
		this.startProgress(false);
		main.logMessage("Stopping siege");
		getScenario().getMainObjective().setActive(false);
		getScenario().getSideObjectives().forEach(q -> q.setActive(false));
		for (Participant participant : this.getParticipants())
		{
			participant.getUser().getPlayer().teleport(participant.getBeforeJoinLocation());
		}
		this.resetSiege();
		
		new BukkitRunnable()
		{
			public void run()
			{
				startCooldown(true);
			}
		}.runTaskLaterAsynchronously(main, 2*20);
	}
	
	public void resetSiege()
	{
		this.Participants.clear();
		this.scenario = null;
		this.suggestedScenarioList.clear();
		
		this.team1.clear();
		this.team2.clear();
		
		this.skilledMatch = false;
		this.titleIDMin = 0;
		this.titleIDMax = 18;
		this.randomVotes.clear();
		
		this.startMatchmaking(false);
		this.startProgress(false);
		this.startCooldown(false);
		
		this.resetValues();
	}
	
	public void announceAll(ActionBar barMessage)
	{
		for (Participant participant : this.getParticipants())
		{
			Player player = participant.getUser().getPlayer();
			barMessage.sendToPlayer(player);
		}
	}
	
	public void announceTeam1(ActionBar barMessage)
	{
		for (SiegeMember member : this.getTeam1Participants())
		{
			Player player = member.getUser().getPlayer();
			barMessage.sendToPlayer(player);
		}
	}
	
	public void announceTeam2(ActionBar barMessage)
	{
		for (SiegeMember member : this.getTeam2Participants())
		{
			Player player = member.getUser().getPlayer();
			barMessage.sendToPlayer(player);
		}
	}
	
	public void skipStage(User user)
	{
		if (this.cooldown)
		{
			this.startMatchmaking(true);
			if (user != null)
			{
				user.getPlayer().sendMessage(ColorOptions.messageachievement + "Skipped the cooldown of Siege " + (Sieges.Sieges.indexOf(this)+1) + "!");
				if (Bukkit.getOnlinePlayers().size() < 2)
				{
					user.getPlayer().sendMessage(ColorOptions.message + "Siege might not proceed because of a lack of players!");
				}
			}
		} else if (this.matchmaking)
		{
			this.matchmakingSeconds = 31;
			if (user != null)
			{
				user.getPlayer().sendMessage(ColorOptions.messageachievement + "Skipped the matchmaking of Siege " + (Sieges.Sieges.indexOf(this)+1) + "!");
				if (Bukkit.getOnlinePlayers().size() < 2)
				{
					user.getPlayer().sendMessage(ColorOptions.message + "Siege might not proceed because of a lack of players!");
				}
			}
		} else
		{
			user.getPlayer().sendMessage(ColorOptions.error + "Siege is already in matchmaking or progress!");
		}
	}
	
	public org.bukkit.scoreboard.Objective createScoreboard(Scoreboard board, User user)
	{
		SiegeMember member = (SiegeMember) this.getParticipant(user);
		Integer teamNumber = member.getTeamNumber();
		String teamName = this.getTeamName(teamNumber);
		SiegeScenario scenario = this.getScenario();
		Integer boardLength = 5;	
		boardLength += scenario.getSideObjectives().size();
		
		Main.logMessage("Setting siege scoreboard for user " + user.getUsername() + ", teamnumber " + teamNumber + ", name " + teamName);

		org.bukkit.scoreboard.Objective sideBoard = null;
		sideBoard = board.getObjective("siege_" + Sieges.Sieges.indexOf(this) + "_" + teamNumber);
		
		if (sideBoard == null)
		{
			sideBoard = board.registerNewObjective("siege_" + Sieges.Sieges.indexOf(this) + "_" + teamNumber, "dummy");
			sideBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		sideBoard.setDisplayName(ColorOptions.KAKColor + "Siege");
		
		Score teamScore = sideBoard.getScore(ColorOptions.message + "Team: " + teamName);
		teamScore.setScore(boardLength);
		
		boardLength--;
		
		Score spaceScore = sideBoard.getScore(" ");
		spaceScore.setScore(boardLength);
		
		boardLength--;
		
		MainObjective MO = scenario.getMainObjective();
		Score MOScore = sideBoard.getScore(ColorOptions.message + "Main Objective: " + MO.getCapturePercentage() + "% Captured");
		MOScore.setScore(boardLength);
		
		boardLength--;
		
		for (SideObjective SO : scenario.getSideObjectives())
		{
			Score SOScore = sideBoard.getScore(ColorOptions.message + "Side Objective " + SO.getSubID() + ": " + SO.getCapturePercentage() + "% Captured");
			SOScore.setScore(boardLength);
			boardLength--;
		}
		
		Score spaceScore2 = sideBoard.getScore("  ");
		spaceScore2.setScore(boardLength);
		
		boardLength--;
		
		HashMap<String, Integer> calcTime = main.getCalculatedTime(this.progressSeconds); 
		Score timeScore = sideBoard.getScore("Time remaining: " + ColorOptions.message + "" + calcTime.get("minute") + ":" + calcTime.get("second"));
		timeScore.setScore(boardLength);
		
		Main.logMessage("Set scoreboard for user " + user.getUsername() + ", teamnumber " + teamNumber + ", criteria " + sideBoard.getCriteria() + ", name " + sideBoard.getName());
		
		org.bukkit.scoreboard.Objective obj = user.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		Main.logMessage("Sidebar board name " + obj.getName());
		return sideBoard;
	}
}
