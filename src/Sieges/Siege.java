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
import Minigames.MGTeam;
import Minigames.MiniGame;
import Minigames.Participant;
import Scoreboards.ActionBar;
import SpawnPoints.SpawnPoint;
import Users.User;

public class Siege extends MiniGame
{
	private Scoreboards.Scoreboard scoreboard = new Scoreboards.Scoreboard();
	private SpawnPoint spawnpoint = new SpawnPoint();
	
	protected static String name = "Siege";	//Stores the name of the mininame
	
	protected Scenario scenario;
	protected List<Scenario> suggestedScenarioList = new ArrayList<Scenario>();
	
	protected MGTeam Team1;
	protected MGTeam Team2;
	protected MGTeam WinningTeam;
	
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
		this.Team1 = new MGTeam(1, "Defenders", ColorOptions.KAKColor, (short)5, this.scoreboard.getScoreBoard(), null);
		this.Team2 = new MGTeam(2, "Attackers", ColorOptions.error, (short)3, this.scoreboard.getScoreBoard(), null);
		new BukkitRunnable()
		{
			public void run()
			{
				startMatchmaking(true);
			}
		}.runTaskLaterAsynchronously(main, 2*20);
	}
	
	public Scenario getScenario()
	{
		return this.scenario;
	}
	
	public MGTeam GetTeam1()
	{
		return this.Team1;
	}
	
	public MGTeam GetTeam2()
	{
		return this.Team2;
	}
	
//	public List<SiegeMember> getTeam1Participants()
//	{
//		return this.team1;
//	}
//	
//	public List<SiegeMember> getTeam2Participants()
//	{
//		return this.team2;
//	}
	
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
	
	public List<Scenario> getSuggestedScenarioList()
	{
		return this.suggestedScenarioList;
	}
	
	public int getTeam(Participant participant)
	{
		int teamNumber = 1;
		
		if (this.Team1.GetMembers().contains(participant))
		{
			teamNumber = 1;
		} else if (this.Team2.GetMembers().contains(participant))
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
			for (Scenario scenarios : this.suggestedScenarioList)
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
	
	public void setScenarioVote(Participant participant, Scenario scenario)
	{
		if (!scenario.getVotes().contains(participant))
		{
			scenario.setVotes(participant);
			List<Scenario> otherScenarios = new ArrayList<Scenario>(this.suggestedScenarioList);
			otherScenarios.remove(scenario);
			
			otherScenarios.forEach(s -> s.removeVotes(participant));
			this.removeRandomVotes(participant);
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
				Scenario scenario = Scenarios.instantiateScenario(scenarioID, false);
				
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
			Scenario scenario = Scenarios.instantiateScenario(IDList.get(0), false);
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
	
	public void setSuggestedScenario(Scenario scenario)
	{
		if (!this.suggestedScenarioList.contains(scenario))
		{
			this.suggestedScenarioList.add(scenario);
		}
	}
	
	public void removeSuggestedScenario(Scenario scenario)
	{
		if (this.suggestedScenarioList.contains(scenario))
		{
			this.suggestedScenarioList.remove(scenario);
		}
	}
	
	public void setScenario(Scenario scenario)
	{
		if (this.scenario == scenario)
		{
			return;
		}
		this.scenario = scenario;
		double factor = (this.getTitleAverage()/10);
		scenario.expRewardWin *= factor;
		scenario.coinRewardWin *= factor;
		scenario.expRewardSideObjective *= factor;
		scenario.coinRewardSideObjective *= factor;
		scenario.expRewardCapture *= factor;
		scenario.coinRewardCapture *= factor;
		this.entryTitle = scenario.getEntryTitle();
	}
	
	public void drawScenario()
	{
		Scenario scenario = null;
		
		if (this.suggestedScenarioList.isEmpty())
		{
			scenario = this.getRandomScenario();
		}
		
		for (Scenario scenarios : this.suggestedScenarioList)
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
	
	public Scenario getRandomScenario()
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
			Participant participant = this.getParticipant(user);
			participant.returnBeforeJoinLocation();
			participant.GetTeam().RemoveMember(participant);
			this.removeParticipant(participant);
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
			ChatColor teamColor = null;
			String teamName = null;
			if (team1)
			{
				((SiegeMember)part).setTeamNumber(1);
				this.Team1.AddMember((SiegeMember)part);
				teamColor = this.Team1.GetColor();
				teamName = this.Team1.GetName();
				team1 = false;
			} else
			{
				((SiegeMember)part).setTeamNumber(2);
				this.Team2.AddMember((SiegeMember)part);
				teamColor = this.Team2.GetColor();
				teamName = this.Team2.GetName();
				team1 = true;
			}
			part.getUser().getPlayer().sendMessage(ColorOptions.message + "Your team is the " + teamColor + teamName);

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
		
//		for (Participant participant : this.getParticipants())
//		{
//			if (participant.getBeforeJoinLocation() == null)
//			{
//				participant.setBeforeJoinLocation(participant.getUser().getPlayer().getLocation());
//			}
//		}
		
		for (SiegeMember siegeMember : this.Team1.GetMembersAsSiegeMembers())
		{
			siegeMember.spawnMember(siegeMember.currentSpawnpoint);
		}
		for (SiegeMember siegeMember : this.Team2.GetMembersAsSiegeMembers())
		{
			siegeMember.spawnMember(siegeMember.currentSpawnpoint);
		}
		
		scenario.setActive(true, this);
		
		BukkitTask task = new BukkitRunnable()
		{
			public void run()
			{
				announceAll(startMSG);
				
				progressSeconds--;
				
				updateMenus(false);
				
				Users.Users.updateScoreBoard(getUserParticipants());
				
				if (progressSeconds == (progressExpire-2))
				{
					Team1.AnnounceMembersActionBar(startMSGDefense);
					Team2.AnnounceMembersActionBar(startMSGAttack);
				}
				
				if (progressSeconds == 0)
				{
					stopSiege();
				}
			}
		}.runTaskTimer(main, 0, 20);
		
		this.setProgressTask(task, true);
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
		Main.logMessage("Setting siege complete..");
		WinningTeam = this.Team1;
		Scenario scenario = this.getScenario();
		scenario.setActive(false, this);
		Integer sideObjectivesCaptured = scenario.getCapturedSideObjectives();
		Integer sideObjectivesDefended = (scenario.getCapturedSideObjectives() - sideObjectivesCaptured);
		
		if (this.getScenario().mainObjective.getCaptured())
		{
			WinningTeam = this.Team2;
		}
		
		List<String> winningTeamMessage = new ArrayList<String>(Arrays.asList(
				"",
				WinningTeam.GetColor() + "The " + WinningTeam.GetName() + " have won!",
				ColorOptions.message + "They defended their Main Objective untill the end of the Game!",
				ColorOptions.message + "Side Objectives succesfully defended: " + ColorOptions.messagesubjects + sideObjectivesDefended,
				ColorOptions.message + "Side Objectives captured by attackers: " + ColorOptions.error + sideObjectivesCaptured,
				""
				));
		
		for (SideObjective objective : scenario.getSideObjectives())
		{
			Participant capturer = objective.getCapturer();
			if (capturer != null)
			{
				winningTeamMessage.add(ColorOptions.message + ColorOptions.messageArrow + "Side Objective " + objective.getSubID() + " captured by: " + ColorOptions.messagesubjects + capturer.getUser().getUsername());
			}
		}
		winningTeamMessage.add("");
		
		this.announceParticipants(this.getParticipants(), winningTeamMessage);
		
		Main.logMessage("Sending reward receive messages");
		new BukkitRunnable()
		{
			public void run()
			{
				for (Participant participant : getParticipants())
				{
					Main.logMessage("Sending reward message to " + participant.getUser().getUsername());
					int totalExpReward = 0;
					int totalCoinReward = 0;
					
					List<String> rewardMessage = new ArrayList<String>(Arrays.asList(
							"",
							ColorOptions.messageachievement + "You received the following rewards:",
							""
							));
					
					MGTeam team = participant.GetTeam();
					
					if (WinningTeam == team)
					{
						totalExpReward += scenario.expRewardWin;
						totalExpReward += scenario.coinRewardWin;
						rewardMessage.add(ColorOptions.message + ColorOptions.messageArrow + ColorOptions.messagesubjects + scenario.expRewardWin + " experience " + ColorOptions.message + "and "
								+ ColorOptions.messagesubjects + scenario.coinRewardWin + " coins "
								+ ColorOptions.message + "for winning");
					}
					
					if (team.GetNumber() == 1)
					{
						int defendedObjectives = 0;
						
						for (SideObjective objective : scenario.getSideObjectives())
						{
							if (!objective.getCaptured())
							{
								defendedObjectives++;
							}
						}
						
						int subTotalExpReward = defendedObjectives * scenario.expRewardSideObjective;
						int subTotalCoinReward = defendedObjectives * scenario.coinRewardSideObjective;
						
						rewardMessage.add(ColorOptions.message + ColorOptions.messageArrow + ColorOptions.messagesubjects + subTotalExpReward + " experience " + ColorOptions.message + "and "
								+ ColorOptions.messagesubjects + subTotalCoinReward + " coins "
								+ ColorOptions.message + "for holding " + ColorOptions.messagesubjects + defendedObjectives + " Side Objectives");
					} else if (team.GetNumber() == 2)
					{
						int capturedObjectives = 0;
						
						for (SideObjective objective : scenario.getSideObjectives())
						{
							if (objective.getCaptured())
							{
								capturedObjectives++;
							}
						}
						
						int subTotalExpReward = capturedObjectives * scenario.expRewardSideObjective;
						int subTotalCoinReward = capturedObjectives * scenario.coinRewardSideObjective;
						
						rewardMessage.add(ColorOptions.message + ColorOptions.messageArrow + ColorOptions.messagesubjects + subTotalExpReward + " experience " + ColorOptions.message + "and "
								+ ColorOptions.messagesubjects + subTotalCoinReward + " coins "
								+ ColorOptions.message + "for capturing " + ColorOptions.messagesubjects + capturedObjectives + " Side Objectives");
						
						totalExpReward += subTotalExpReward;
						totalCoinReward += subTotalCoinReward;
					}
					
					for (SideObjective objective : scenario.getSideObjectives())
					{
						if (objective.getCaptured() && objective.getCapturer() == participant)
						{
							rewardMessage.add(ColorOptions.message + ColorOptions.messageArrow + ColorOptions.messagesubjects + scenario.expRewardCapture + " experience " + ColorOptions.message + "and " 
									+ ColorOptions.messagesubjects + scenario.coinRewardCapture + " coins " 
									+ ColorOptions.message + "for capturing Side Objective " + ColorOptions.messagesubjects + objective.getSubID());
						}
						totalExpReward += scenario.expRewardCapture;
						totalCoinReward += scenario.coinRewardCapture;
					}
					
					rewardMessage.addAll(Arrays.asList(
							"",
							ColorOptions.message + ColorOptions.messageArrow + "Total: " + ColorOptions.messagesubjects + totalExpReward + " experience " + ColorOptions.message + "and " + ColorOptions.messagesubjects + totalCoinReward + " coins",
							""
							));
					
					participant.getUser().sendMessage(rewardMessage);
					participant.getUser().addCoins(totalCoinReward);
					participant.getUser().addExperience(totalExpReward, true);
				}
				
				stopSiege();
			}
		}.runTaskLaterAsynchronously(main, 2*20);
		
	}
	
	public void stopSiege()
	{
		this.startProgress(false);
		main.logMessage("Stopping siege");
		this.scenario.setActive(false, this);
		for (Participant participant : this.getParticipants())
		{
			participant.returnBeforeJoinLocation();
			participant.GetTeam().RemoveMember(participant);
		}
		Users.Users.updateScoreBoard(this.getUserParticipants());
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
		
		this.Team1.Reset();
		this.Team2.Reset();
		
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
	
	public void skipStage(User user)
	{
		if (this.cooldown)
		{
			this.startCooldown(false);
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
	
	public org.bukkit.scoreboard.Objective createScoreboard(MGTeam team)
	{
		Scoreboard board = team.GetScoreboard();
		Integer teamNumber = team.GetNumber();
		String teamName = team.GetName();
		Scenario scenario = this.getScenario();
		Integer boardLength = 5;	
		boardLength += scenario.getSideObjectives().size();
		
		org.bukkit.scoreboard.Objective sideBoard = null;
		sideBoard = board.getObjective("siege_" + Sieges.Sieges.indexOf(this) + "_" + teamNumber);
		
		if (sideBoard == null)
		{
			sideBoard = board.registerNewObjective("siege_" + Sieges.Sieges.indexOf(this) + "_" + teamNumber, "dummy");
			sideBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
		} else
		{
			sideBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
			HashMap<String, Integer> calcTimeOld = Main.getCalculatedTime(this.progressSeconds+1); 
			board.resetScores("Time remaining: " + ColorOptions.message + "" + calcTimeOld.get("minute") + ":" + calcTimeOld.get("second"));
//			Score timeScoreOld = sideBoard.getScore("Time remaining: " + ColorOptions.message + "" + calcTimeOld.get("minute") + ":" + calcTimeOld.get("second"));
//			timeScoreOld.setScore(0);
		}
		
		sideBoard.setDisplayName(ColorOptions.KAKColor + "Siege");
		
		Score teamScore = sideBoard.getScore(ColorOptions.message + "Team: " + team.GetColor() + teamName);
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
		
		HashMap<String, Integer> calcTime = Main.getCalculatedTime(this.progressSeconds); 
		Score timeScore = sideBoard.getScore("Time remaining: " + ColorOptions.message + "" + calcTime.get("minute") + ":" + calcTime.get("second"));
		timeScore.setScore(boardLength);
				
		return sideBoard;
	}
	
	public void UpdateScoreboard(Objective objective, int oldPercentage)
	{
		List<MGTeam> teams = new ArrayList<MGTeam>(Arrays.asList(this.Team1, this.Team2));
		for (MGTeam team : teams)
		{
			Scoreboard board = team.GetScoreboard();
			org.bukkit.scoreboard.Objective sideBoard = board.getObjective("siege_" + Sieges.Sieges.indexOf(this) + "_" + team.GetNumber());
			int boardSlot = 0;
			
			String objectiveKind = null;
			
			if (objective instanceof MainObjective)
			{
				objectiveKind = ColorOptions.message + "Main Objective: ";
			} else if (objective instanceof SideObjective)
			{
				objectiveKind = ColorOptions.message + "Side Objective " + objective.getSubID() + ": ";
			}
			
			if (objectiveKind != null)
			{
				Score oldScore = sideBoard.getScore(objectiveKind + oldPercentage + "% Captured");
				boardSlot = oldScore.getScore();
				board.resetScores(objectiveKind + oldPercentage + "% Captured");
			}
			
			Score newScore = sideBoard.getScore(objectiveKind + objective.getCapturePercentage() + "% Captured");
			if (boardSlot > 0)
			{
				newScore.setScore(boardSlot);
			}
		}
	}
}
