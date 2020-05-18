package Sieges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Main.Main;
import Minigames.MGTeam;
import Minigames.MiniGame;
import Minigames.Participant;
import Minigames.SiegeTeam;
import Scoreboards.ActionBar;
import Users.User;

public class Siege extends MiniGame
{
	private Scoreboards.Scoreboard scoreboard = new Scoreboards.Scoreboard();
	protected static String name = "Siege";	//Stores the name of the mininame
	
	protected Scenario scenario;
	protected List<Scenario> suggestedScenarioList = new ArrayList<Scenario>();
	
	protected SiegeTeam Team1;
	protected SiegeTeam Team2;
	protected SiegeTeam WinningTeam;
	
	/**
	 * This gateRegion indicates if the match is skilled and for what title's it is joinable
	 */
	protected int titleIDMin = 0;
	protected int titleIDMax = 18;
	protected boolean skilledMatch = false;
	
	/**
	 * This gateRegion states the technical variables of the actual minigame
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
		
		this.setMatchmakingNotifications("/siege join");
		
		this.instance = this;
		this.Team1 = new SiegeTeam(this, 1, "Defenders", ColorOptions.KAKColor, (short)5, this.scoreboard.getScoreBoard(), null);
		this.Team2 = new SiegeTeam(this, 2, "Attackers", ColorOptions.error, (short)3, this.scoreboard.getScoreBoard(), null);
		new BukkitRunnable()
		{
			public void run()
			{
				startMatchmaking(true);
			}
		}.runTaskLater(main, 2*20);
	}
	
	public Scenario getScenario()
	{
		return this.scenario;
	}
	
	public SiegeTeam GetTeam1()
	{
		return this.Team1;
	}
	
	public SiegeTeam GetTeam2()
	{
		return this.Team2;
	}
	
	public SiegeTeam GetOppositeTeam(SiegeTeam team)
	{
		SiegeTeam opposite = null;
		
		if (team != this.Team1)
		{
			opposite = this.Team2;
		} else
		{
			opposite = this.Team1;
		}
		
		return opposite;
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
	
	public SiegeTeam getTeam(Participant participant)
	{
		SiegeTeam team = null;
		
		if (this.Team1.GetMembers().contains(participant))
		{
			team = this.Team1;
		} else if (this.Team2.GetMembers().contains(participant))
		{
			team = this.Team2;
		}
		
		return team;
	}
	
	public SiegeMember getSiegeMember(User user)
	{
		SiegeMember member = null;
		
		List<SiegeMember> members = this.Team1.GetMembersAsSiegeMembers();
		members.addAll(this.Team2.GetMembersAsSiegeMembers());
		
		for (SiegeMember m : members)
		{
			if (m.getUser() == user)
			{
				member = m;
				break;
			}
		}
		
		return member;
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
		if (!scenario.getVotes().contains(participant) && this.matchmakingSeconds > 30)
		{
			scenario.setVotes(participant);
			List<Scenario> otherScenarios = new ArrayList<Scenario>(this.suggestedScenarioList);
			otherScenarios.remove(scenario);
			
			otherScenarios.forEach(s -> s.removeVotes(participant));
			this.removeRandomVotes(participant);
			
			participant.getUser().sendMessage(ColorOptions.message + "Voted for a scenario " + ColorOptions.messagesubjects + scenario.getName());
			participant.getUser().playSound("succesclick");
		} else
		{
			participant.getUser().sendMessage(ColorOptions.error + "Voting for Siege Scenario is closed!");
			participant.getUser().playSound("failclick");
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
				
				Integer scenarioID = IDList.size() > 2 ? IDList.get(Main.getRandom(0, IDList.size()-1)) : IDList.get(0);
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
		
		this.matchmakingLocationID = this.scenario.getMainObjective().getSpawnpointID();
		
		Main.logMessage("Hub location: " + this.matchmakingLocationID);
		double factor = (this.getTitleAverage()/10);
		scenario.expRewardWin *= factor;
		scenario.coinRewardWin *= factor;
		scenario.expRewardSideObjective *= factor;
		scenario.coinRewardSideObjective *= factor;
		scenario.expRewardCapture *= factor;
		scenario.coinRewardCapture *= factor;
		this.entryTitle = scenario.getEntryTitle();
		this.scenario.setTeams(this.Team1, this.Team2);
	}
	
	public void drawScenario()
	{
		Scenario scenario = null;
		
		if (this.suggestedScenarioList.isEmpty())
		{
			scenario = this.getRandomScenario();
			return;
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
					if (Main.getRandom(0, 100) <= 50)
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
	
	public SiegeTeam getHeldTeam(Objective objective)
	{
		SiegeTeam team = null;
		
		if (this.Team1.getHeldObjectives().contains(objective))
		{
			team = this.Team1;
		} else
		{
			team = this.Team2;
		}
		
		return team;
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
			MGTeam team = participant.GetTeam();
			
			if (this.randomVotes.contains(participant))
			{
				this.randomVotes.remove(participant);
			}
			
			for (Scenario s : this.suggestedScenarioList)
			{
				if (s.getVotes().contains(participant))
				{
					s.removeVotes(participant);
				}
			}
			
			if (team != null)
			{
				participant.GetTeam().RemoveMember(participant);
			}
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
							ColorOptions.KAKFormat + ColorOptions.error + ChatColor.BOLD + "Voting for Siege Scenario now closed!",
							ColorOptions.KAKFormat + ColorOptions.error + ChatColor.BOLD + "You will be teleported to the Hide and Seek hub in " + 15 + " seconds!"
							));
				}
				
				if (matchmakingSeconds == 25)
				{
					drawScenario();
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
					setTeams();
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
		
		new BukkitRunnable()
		{
			public void run()
			{
				scenario.setActive(true, (Siege)instance);
			}
		}.runTask(Main.getPlugin(Main.class));
		
		BukkitTask task = new BukkitRunnable()
		{
			public void run()
			{
				announceAll(startMSG);
				
				progressSeconds--;
				
				updateMenus(false);
				
				Users.Users.updateScoreBoard(getUserParticipants(instance.getParticipants()));
				
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
		Main.logMessage("Starting cooldown");
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
		Integer sideObjectivesDefended = (scenario.getSideObjectives().size() - sideObjectivesCaptured);
		
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
				winningTeamMessage.add(ColorOptions.message + ColorOptions.messageArrow + "Side Objective " + objective.getName() + " captured by: " + ColorOptions.messagesubjects + capturer.getUser().getUsername());
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
					Main.logMessage("Rewards: " + scenario.expRewardWin + ", " + scenario.expRewardSideObjective + ", " + scenario.expRewardCapture);
					int totalExpReward = 0;
					int totalCoinReward = 0;
					
					List<String> rewardMessage = new ArrayList<String>(Arrays.asList(
							"",
							ColorOptions.messageachievement + "You received the following rewards:",
							""
							));
					
					SiegeTeam team = (SiegeTeam) participant.GetTeam();
					
					if (WinningTeam == team)
					{
						totalExpReward += scenario.expRewardWin;
						totalCoinReward += scenario.coinRewardWin;
						rewardMessage.add(ColorOptions.message + ColorOptions.messageArrow + ColorOptions.messagesubjects + scenario.expRewardWin + " experience " + ColorOptions.message + "and "
								+ ColorOptions.messagesubjects + scenario.coinRewardWin + " coins "
								+ ColorOptions.message + "for winning");
						Main.logMessage("Total exp: " + totalExpReward + ", total coins: " + totalCoinReward);
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
						
						totalExpReward += subTotalExpReward;
						totalCoinReward += subTotalCoinReward;
						Main.logMessage("Total exp: " + totalExpReward + ", total coins: " + totalCoinReward);
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
								+ ColorOptions.message + "for capturing and holding " + ColorOptions.messagesubjects + capturedObjectives + " Side Objectives");
						
						totalExpReward += subTotalExpReward;
						totalCoinReward += subTotalCoinReward;
						Main.logMessage("Total exp: " + totalExpReward + ", total coins: " + totalCoinReward);
					}
					
					SiegeMember member = getSiegeMember(participant.getUser());
					for (Objective objective : member.getCapturedObjectives())
					{
						if (objective instanceof SideObjective)
						{
							SideObjective so = (SideObjective)objective;
							rewardMessage.add(ColorOptions.message + ColorOptions.messageArrow + ColorOptions.messagesubjects + scenario.expRewardCapture + " experience " + ColorOptions.message + "and " 
									+ ColorOptions.messagesubjects + scenario.coinRewardCapture + " coins " 
									+ ColorOptions.message + "for capturing Side Objective " + ColorOptions.messagesubjects + so.getName());
						}
						totalExpReward += scenario.expRewardCapture;
						totalCoinReward += scenario.coinRewardCapture;
					}
					Main.logMessage("Total exp: " + totalExpReward + ", total coins: " + totalCoinReward);
					
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

		if (this.scenario != null)
		{
			this.scenario.setActive(false, this);
		}
		for (Participant participant : this.getParticipants())
		{
			participant.returnBeforeJoinLocation();
			participant.GetTeam().RemoveMember(participant);
		}
		Users.Users.updateScoreBoard(this.getUserParticipants(this.getParticipants()));
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
		this.scenario.destroy();
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
	
	public boolean CanJoin(User user)
	{
		boolean join = false;
		
		if (this.getMatchmaking())
		{
			if (this.skilledMatch)
			{
				Integer titleID = user.getTitleID();
				if (titleID >= this.getSkilledMin() && titleID <= this.getSkilledMax())
				{
					join = true;
				}
			} else
			{
				join = true;
			}
		}
		
		return join;
	}
	
	public void announceAll(ActionBar barMessage)
	{
		for (Participant participant : this.getParticipants())
		{
			Player player = participant.getUser().getPlayer();
			barMessage.sendToPlayer(player);
		}
	}
	
	public void skipStage(CommandSender sender)
	{
		if (this.cooldown)
		{
			this.startCooldown(false);
			this.startMatchmaking(true);
			if (sender != null)
			{
				sender.sendMessage(ColorOptions.messageachievement + "Skipped the cooldown of Siege " + (Sieges.Sieges.indexOf(this)+1) + "!");
				if (Bukkit.getOnlinePlayers().size() < 2)
				{
					sender.sendMessage(ColorOptions.message + "Siege might not proceed because of a lack of players!");
				}
			}
		} else if (this.matchmaking)
		{
			this.matchmakingSeconds = 31;
			if (sender != null)
			{
				sender.sendMessage(ColorOptions.messageachievement + "Skipped the matchmaking of Siege " + (Sieges.Sieges.indexOf(this)+1) + "!");
				if (Bukkit.getOnlinePlayers().size() < 2)
				{
					sender.sendMessage(ColorOptions.message + "Siege might not proceed because of a lack of players!");
				}
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "Siege is already in matchmaking or progress!");
		}
	}
	
	public void UpdateScoreboard(Objective objective, int oldPercentage)
	{
		List<SiegeTeam> teams = new ArrayList<SiegeTeam>(Arrays.asList(this.Team1, this.Team2));
		for (SiegeTeam team : teams)
		{
			team.UpdateSideBarBoard(this, objective, oldPercentage);
		}
	}
	
	@Override
	public void setMatchmakingNotifications(String joinCommand)
	{
		this.matchmakingNotifications.clear();
		this.matchmakingNotifications.put(290, Arrays.asList(
				"",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + name + " begins in " + ColorOptions.messagesubjects + "5 minutes!",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + joinCommand + ColorOptions.message + " to join!",
				""
				));
		this.matchmakingNotifications.put(60, Arrays.asList(
				"",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + name + " begins in " + ColorOptions.error + "1 minute!",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + joinCommand + ColorOptions.message + " to join!",
				""
				));
		this.matchmakingNotifications.put(30, Arrays.asList(
				"",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + name + " begins in " + ColorOptions.error + "30 seconds!",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + joinCommand + ColorOptions.message + " to join!",
				""
				));
		this.matchmakingNotifications.put(15, Arrays.asList(
				"",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + name + " begins in " + ColorOptions.error + "15 seconds!",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + joinCommand + ColorOptions.message + " to join!",
				"",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Voting for Siege Scenario closes in " + ColorOptions.error + "30 seconds!",
				""
				));
	}
}
