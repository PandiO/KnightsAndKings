package HideAndSeek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import DataManager.Users2;
import Handlers.ColorOptions;
import Handlers.Menus;
import Main.Main;
import Menu.Menu;
import Minigames.MiniGame;
import Minigames.Participant;
import Products.Product;
import Scoreboards.ActionBar;
import SpawnPoints.SpawnPoint;
import Towns.Town;
import Users.User;

public class HideAndSeek extends MiniGame
{
	Menu menu = new Menu();
	Town town = new Town();
	Product product = new Product();
	SpawnPoint spawnpoint = new SpawnPoint();
	Scoreboards.Scoreboard scoreboard = new Scoreboards.Scoreboard();
	Main main = Main.getPlugin(Main.class);
	
	protected HSTeam Seekers;
	protected HSTeam Hiders;
	protected List<Participant> lastWinners = new ArrayList<Participant>();
	protected int OriginalReward = 5000;
	protected int Reward = 5000;
	protected int participantRewardStep = 5000;
	protected int hideSeconds;
	protected int hideExpire = 20;
	protected String matchmakingLocationName = "HideAnkSeek";
	protected boolean hideTime = false;
	protected BukkitTask hideTask;
	
	public HideAndSeek()
	{
		super("HideAndSeek", "/hs join");
		
		this.Seekers = new HSTeam(2, "Seekers", ChatColor.RED, (short)0, this.scoreboard.getScoreBoard(), null);
		this.Hiders = new HSTeam(2, "Hiders", ChatColor.BLUE, (short)0, this.scoreboard.getScoreBoard(), null);
		
		this.cooldownExpire = 900;
		this.cooldownSeconds = 900;
		this.matchmakingSeconds = 300;
		this.matchmakingExpire = 300;
		this.progressExpire = 900;
		this.progressSeconds = 900;
		
		this.matchmakingNotifications = new HashMap<Integer, List<String>>() {{
			put(120, Arrays.asList(
					"",
					ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Hide and Seek begins in " + ColorOptions.messagesubjects + "2 minutes!",
					ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + "/hs join" + ColorOptions.message + " to join!",
					""
					));
			put(60, Arrays.asList(
					"",
					ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Hide and Seek begins in " + ColorOptions.error + "1 minute!",
					ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + "/hs join" + ColorOptions.message + " to join!",
					""
					));
			put(30, Arrays.asList(
					"",
					ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Hide and Seek begins in " + ColorOptions.error + "30 seconds!",
					ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + "/hs join" + ColorOptions.message + " to join!",
					""
					));
			put(15, Arrays.asList(
					"",
					ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Hide and Seek begins in " + ColorOptions.error + "15 seconds!",
					ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + "/hs join" + ColorOptions.message + " to join!",
					""
					));
		}};
		this.matchmakingMessage = new ArrayList<String>(Arrays.asList(
				"",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "A new game of Hide and Seek now open for participation!",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + "Type " + ColorOptions.messagesubjects + "/hs join" + ColorOptions.message + " to join!",
				""
				));
		
		this.allowedCommands = new ArrayList<String>(Arrays.asList(
				"/leave",
				"/msg",
				"/r",
				"/staffchat",
				"/hs leave",
				"/hideandseek leave"
				));
		
		this.instance = this;
		this.chooseLocation();
		new BukkitRunnable()
		{
			public void run()
			{
				startMatchmaking();
			}
		}.runTaskLaterAsynchronously(main, 2*20);
	}
	
	public List<Participant> getParticipants()
	{
		return this.Participants;
	}
	
	public HSTeam getSeekers()
	{
		return this.Seekers;
	}
	
	public HSTeam getHiders()
	{
		return this.Hiders;
	}
	
	public HSTeam getTeam(Participant participant)
	{
		HSTeam team = null;
		
		if (this.Hiders.GetMembers().contains(participant))
		{
			team = this.Hiders;
		} else
		{
			team = this.Seekers;
		}
		
		return team;
	}
	
	public int getReward()
	{
		return this.Reward;
	}
	
	public boolean getHideTime()
	{
		return this.hideTime;
	}
	
	public int getParticipantRewardStep()
	{
		return this.participantRewardStep;
	}
	
	public int getMatchmakingSeconds()
	{
		return this.matchmakingSeconds;
	}
	
	public int getMatchmakingExpire()
	{
		return this.matchmakingExpire;
	}
	
	public int getCooldownSeconds()
	{
		return this.cooldownSeconds;
	}
	
	public int getCooldownExpire()
	{
		return this.cooldownExpire;
	}
	
	public Integer getTownID()
	{
		return this.townID;
	}
	
	public String getTownName()
	{
		return this.townName;
	}
	
	public Integer getEntryTitle()
	{
		return this.entryTitle;
	}
	
	public List<Participant> getLastWinners()
	{
		return this.lastWinners;
	}
	
	public boolean getAutostart()
	{
		return this.autoStart;
	}
	
	public void setSeeker(Participant participant)
	{
		if (!this.Seekers.GetMembers().contains(participant))
		{
			if (this.getHiders().GetMembers().contains(participant))
			{
				this.getHiders().RemoveMember(participant);
			}
			this.Seekers.AddMember(participant);
			this.setSeekerOutfit(participant);
			if (this.Hiders.GetMembers().isEmpty())
			{
				finishParticipants();
				new BukkitRunnable()
				{
					public void run()
					{
						stopHideAndSeek();
					}
				}.runTaskLaterAsynchronously(main, 3*20);
			}
		}
	}
	
	public void removeSeeker(Participant participant)
	{
		if (this.Seekers.GetMembers().contains(participant))
		{
			this.Seekers.RemoveMember(participant);
			participant.getUser().getPlayer().getInventory().clear();
			
			if (this.Seekers.GetMembers().isEmpty())
			{
				finishParticipants();
				new BukkitRunnable()
				{
					public void run()
					{
						stopHideAndSeek();
					}
				}.runTaskLaterAsynchronously(main, 3*20);
			}
		}
	}
	
	public void setAutostart(boolean autoStart)
	{
		this.autoStart = autoStart;
		
		if (this.autoStart)
		{
			if (this.cooldown == false && this.matchmaking == false && this.progress == false)
			{
				Main.HideAndSeek.startMatchmaking();
			}
		}
	}
	
	public void setMatchmakingSeconds(Integer seconds)
	{
		this.matchmakingSeconds = seconds;
	}
	
	public void setProgressSeconds(Integer seconds)
	{
		this.progressSeconds = seconds;
	}
	
	public void setCooldownSeconds(Integer seconds)
	{
		this.cooldownSeconds = seconds;
	}
	
	public void removeParticipant(User user)
	{
		if (!this.getParticipating(user))
		{
			return;
		}
		
		this.Participants.remove(this.getParticipant(user));
		if (this.Participants.size() == 1)
		{
			this.progressSeconds = 1;
		} else if (this.Participants.size() == 0)
		{
			this.forceStop(null);
		}
	}
	
	public void chooseLocation()
	{
		List<Integer> townIDList = town.getTownIDList();
		townIDList.remove(this.town.getTownID("wilderness"));
		Collections.shuffle(townIDList);
		
		this.setTown(townIDList.get(0));
	}
	
	public void setTown(Integer townID)
	{
		this.townID = townID;
		this.townName = town.getTownName(townID);
		this.entryTitle = town.getRequiredTitleID(townID);
		
		if (this.spawnpoint.getSpawnPointID(this.matchmakingLocationName) != null)
		{
			this.matchmakingLocationID = this.spawnpoint.getSpawnPointID(this.matchmakingLocationName);
		} else
		{
			this.matchmakingLocationID = this.spawnpoint.getSpawnPointIDbyTown(townID);
		}
	}
	
	public void setReward(Integer reward)
	{
		if (reward > 0)
		{
			this.Reward = reward;
		}
	}
	
	public void startMatchmaking()
	{
		this.cooldown = false;
		this.hideSeconds = this.hideExpire;

		if (this.cooldownTask != null)
		{
			this.cooldownTask.cancel();
		}
		
		if (this.townID == null)
		{
			this.chooseLocation();
		}
		
		this.matchmakingTask = new BukkitRunnable()
		{
			public void run()
			{
				matchmakingSeconds--;
				
				updateMenus(false);
				
				for (Integer timeStamp : matchmakingNotifications.keySet())
				{
					if (timeStamp == matchmakingSeconds)
					{
						announceOnline(matchmakingNotifications.get(timeStamp));
					}
				}
				if (matchmakingSeconds == 30)
				{
					announceParticipants(instance.getParticipants(), Arrays.asList(
							ColorOptions.KAKFormat + ColorOptions.error + ChatColor.BOLD + "You will be teleported to the Hide and Seek hub in " + 15 + " seconds!"
							));
				}
				if (matchmakingSeconds == 15)
				{
					for (Participant user : Participants)
					{
						sendToHub(user);
					}
					inHub = true;
				}
				if (matchmakingSeconds == 10)
				{
					if (Participants.size() >= 2)
					{
						chooseSeeker();
					}
				}
				if (matchmakingSeconds <= 10)
				{
					ActionBar greetMessage = new ActionBar(ColorOptions.error + "" + ChatColor.BOLD + "Hide and Seek starting in " + matchmakingSeconds + " seconds!");
					for (Participant participant : Participants)
					{
						greetMessage.sendToPlayer(participant.getUser().getPlayer());
					}
				}
				if (matchmakingSeconds == 0)
				{
					startHideAndSeek();
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(main, 0, 20);
		
		this.matchmaking = true;
		this.announceOnline(this.matchmakingMessage);
		this.updateMenus(true);
	}
	
	public void startHideAndSeek()
	{
		this.matchmaking = false;
		this.inHub = false;
		this.hideSeconds = this.hideExpire;

		if (this.matchmakingTask != null)
		{
			this.matchmakingTask.cancel();
		}
		if (this.Participants.size() < 2)
		{
			this.forceStop(null);
			this.announceParticipants(this.getParticipants(), Arrays.asList(ColorOptions.error + "Couldn't start Hide and Seek! We need more than 1 player"));
			return;
		}
		if (this.Seekers.GetMembers().isEmpty())
		{
			this.chooseSeeker();
		}
		this.progressTask = new BukkitRunnable()
		{
			public void run()
			{
				progressSeconds--;
				
				updateMenus(false);
				Users.Users.updateScoreBoard(getUserParticipants(instance.getParticipants()));
				
				if (progressSeconds == 0)
				{
					finishParticipants();
				}
				if (progressSeconds == -2)
				{
					stopHideAndSeek();
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(main, 0, 20);
		
		this.progress = true;
		this.hideTime = true;
		
		for (Participant participant : this.Participants)
		{
			this.sendToHub(participant);
		}
		this.announceParticipants(this.getHiders().GetMembers(), Arrays.asList(ColorOptions.KAKFormat + ColorOptions.message + "You have 20 seconds to hide!"));
		this.announceParticipants(this.getSeekers().GetMembers(), Arrays.asList(ColorOptions.message + "The hiders have 20 seconds to hide!"));
		for (Participant seeker : this.Seekers.GetMembers())
		{
			for (Participant hider : this.getHiders().GetMembers())
			{
				seeker.getUser().getPlayer().hidePlayer(hider.getUser().getPlayer());
			}
		}
		Users.Users.updateScoreBoard(this.getUserParticipants(this.getParticipants()));
		
		
		
		this.hideTask = new BukkitRunnable()
		{
			public void run()
			{
				ActionBar countDownMSG = new ActionBar(ColorOptions.error + "Visible in " + hideSeconds);
				ActionBar hiderMSG = new ActionBar(ColorOptions.error + "Seekers are now able to catch you!");
				ActionBar seekerMSG = new ActionBar(ColorOptions.error + "Hiders are now visible!");
				for (Participant hider : getHiders().GetMembers())
				{
					countDownMSG.sendToPlayer(hider.getUser().getPlayer());
				}
				hideSeconds--;
				if (hideSeconds <= 0)
				{
					hideTime = false;
					for (Participant seeker : Seekers.GetMembers())
					{
						for (Participant hider : getHiders().GetMembers())
						{
							seeker.getUser().getPlayer().showPlayer(hider.getUser().getPlayer());
						}
						seekerMSG.sendToPlayer(seeker.getUser().getPlayer());
					}
					for (Participant hider : getHiders().GetMembers())
					{
						hiderMSG.sendToPlayer(hider.getUser().getPlayer());
					}
//					announceSeekers(Arrays.asList(ColorOptions.message + "Hiders are now visible and you can catch them!"));
//					announceParticipants(true, Arrays.asList(ColorOptions.KAKFormat + ColorOptions.error + "Seekers are now able to see and catch you!"));
				}
			}
		}.runTaskTimer(main, 0, 20);
		this.updateMenus(true);
	}
	
	public void forceStop(User user)
	{
		if (user == null)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.message + "Stopping current game of Hide and Seek");
		} else
		{
			user.getPlayer().sendMessage(ColorOptions.message + "Stopping current game of Hide and Seek");
		}
		for (Participant participant : this.Participants)
		{
			participant.getUser().getPlayer().teleport(participant.getBeforeJoinLocation());
		}
		new BukkitRunnable()
		{
			public void run()
			{
				stopHideAndSeek();
			}
		}.runTaskLaterAsynchronously(main, 2*20);
		
		if (user == null)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.message + "Stopped Hide and Seek");
			Bukkit.getConsoleSender().sendMessage(ColorOptions.message + "If autoStart is set to true the cooldown will commence as normal");
		} else
		{
			user.getPlayer().sendMessage(ColorOptions.message + "Stopped Hide and Seek");
			user.getPlayer().sendMessage(ColorOptions.message + "If autoStart is set to true the cooldown will commence as normal");
		}
	}
	
	//Handle all the tasks for the participants when a game ends
	public void finishParticipants()
	{
		this.lastWinners.clear();
		List<String> announcement = new ArrayList<String>();
		
		if (this.getHiders().GetMembers().size() == 1)
		{
			Participant winner = this.getHiders().GetMembers().get(0);
			winner.getUser().addCoins(this.Reward);
			this.announceParticipants(this.getParticipants(), Arrays.asList(
					ColorOptions.KAKFormat + ColorOptions.messageachievement + "Game is over! One or more hiders made it to the end",
					ColorOptions.KAKFormat + ColorOptions.messageachievement + winner.getUser().getUsername() + " won and received " + ColorOptions.formatCurrency(this.Reward) + " Coins!"
					));
			for (Participant participant : this.getHiders().GetMembers())
			{
				participant.getUser().getPlayer().playSound(participant.getUser().getPlayer().getLocation(), Sound.FIREWORK_BLAST2, 0.2F, 1.0F);
			}
		} else if (this.getHiders().GetMembers().size() > 1)
		{
			List<Participant> winners = new ArrayList<Participant>();
			
			announcement.add(ColorOptions.KAKFormat + ColorOptions.messageachievement + "Game is over! " + winners.size() + " players made it to the end");

			for (Participant participant : this.getHiders().GetMembers())
			{
				winners.add(participant);
				participant.getUser().getPlayer().playSound(participant.getUser().getPlayer().getLocation(), Sound.FIREWORK_BLAST2, 0.2F, 1.0F);
			}
			
			Integer partReward = this.Reward/winners.size();
			List<String> winnerNames = new ArrayList<String>();
			
			for (Participant winner : winners)
			{
				winner.getUser().addCoins(partReward);
				winnerNames.add(winner.getUser().getUsername());
			}
			
			announcement.addAll(Arrays.asList(
					ColorOptions.KAKFormat + ColorOptions.messageachievement + "The winners received " + ColorOptions.formatCurrency(partReward) + " coins",
					ColorOptions.KAKFormat + ColorOptions.messageachievement + "Winners: " + winnerNames.toString()
					));
			this.announceParticipants(this.getParticipants(), announcement);
			this.lastWinners.addAll(winners);
		} else
		{
			Integer partReward = this.Reward/this.getParticipants().size();
			announcement.add(ColorOptions.KAKFormat + ColorOptions.messageachievement + "Game is over! No players made it to the end");
			announcement.add(ColorOptions.KAKFormat + ColorOptions.messageachievement + "All participants received " + ColorOptions.formatCurrency(partReward) + " coins");
			
			for (Participant p : this.getParticipants())
			{
				p.getUser().addCoins(partReward);
			}
		}
		this.finished = true;
		List<Participant> Participants = this.Participants;
		for (Participant participant : Participants)
		{
			participant.returnStoredInventory();
			participant.returnBeforeJoinLocation();
		}
		this.Participants.clear();
		Users.Users.updateScoreBoard(this.getUserParticipants(Participants));
	}
	
	//Handle all the technical details of ending the game
	public void stopHideAndSeek()
	{
		this.resetValues();
		if (this.autoStart)
		{
			this.cooldownTask = new BukkitRunnable()
			{
				public void run()
				{
					cooldown = true;
					cooldownSeconds--;
					
					updateMenus(false);
					
					if (cooldownSeconds == 0)
					{
						startMatchmaking();
						this.cancel();
					}
				}
			}.runTaskTimerAsynchronously(main, 0, 20);
		}
		this.matchmaking = false;
		this.finished = false;
		this.progress = false;
		this.updateMenus(true);
	}
	
	public void chooseSeeker()
	{
		Participant seeker = this.Participants.get(Main.getRandom(0, this.Participants.size()-1));
		this.Seekers.AddMember(seeker);
		seeker.getUser().getPlayer().sendMessage(ColorOptions.message + "You will begin as Seeker");
		this.announceParticipants(this.getHiders().GetMembers(), Arrays.asList(
				ColorOptions.KAKFormat + ColorOptions.message + "Player " + seeker.getUser().getUsername() + " will begin as seeker",
				ColorOptions.KAKFormat + ColorOptions.message + "When the game starts, you have 20 seconds of invisibility to hide from the seeker!"
				));
		this.setSeekerOutfit(seeker);
		
		for (Participant p : this.getParticipants())
		{
			if (p != seeker)
			{
				this.Hiders.AddMember(p);
			}
		}
	}
	
	public void skipStage(User user)
	{
		if (this.cooldown)
		{
			this.startMatchmaking();
			if (user != null)
			{
				user.getPlayer().sendMessage(ColorOptions.messageachievement + "Skipped the cooldown of Hide and Seek!");
				if (Bukkit.getOnlinePlayers().size() < 2)
				{
					user.getPlayer().sendMessage(ColorOptions.message + "Hide and Seek might not proceed because of a lack of players!");
				}
			}
		} else if (this.matchmaking)
		{
			this.matchmakingSeconds = 31;
			if (user != null)
			{
				user.getPlayer().sendMessage(ColorOptions.messageachievement + "Skipped the matchmaking of Hide and Seek!");
				if (Bukkit.getOnlinePlayers().size() < 2)
				{
					user.getPlayer().sendMessage(ColorOptions.message + "Hide and Seek might not proceed because of a lack of players!");
				}
			}
		} else
		{
			user.getPlayer().sendMessage(ColorOptions.error + "Hide and Seek is already in matchmaking or progress!");
		}
	}
	
	public void updateMenus(boolean reOpen)
	{
		for (User user : Users2.users)
		{
			if (user.inOwnerModus() || user.inStaffModus())
			{
				Inventory menu = user.getOpenMenu();
				if (menu.getName() == Menus.HideAndSeekManagerMenu)
				{
					if (reOpen)
					{
						this.menu.openHideAndSeekManager(user);
						continue;
					} else
					{
						ItemStack item = menu.getItem(13);
						String display = item.getItemMeta().getDisplayName();
						String status = "Cooldown";
						List<String> lore = item.getItemMeta().getLore();
						if (lore == null || lore.isEmpty())
						{
							this.menu.openHideAndSeekManager(user);
							continue;
						}
						lore.remove(2);
						HashMap<String, Integer> calcTime = null; 
						
						if (this.cooldown)
						{
							calcTime = Main.getCalculatedTime(this.cooldownSeconds);
						}
						if (this.matchmaking)
						{
							calcTime = Main.getCalculatedTime(this.matchmakingSeconds);
							status = "Matchmaking";
						}
						if (this.progress)
						{
							calcTime = Main.getCalculatedTime(this.progressSeconds);
							status = "In progress";
						}
						display = ColorOptions.message + "Current stage: " + ColorOptions.messagesubjects + status;
						lore.add(ColorOptions.message + "" + calcTime.get("minute") + " minutes and " + calcTime.get("second") + " seconds");
						
						
						menu.setItem(13, product.setItemDescription(menu.getItem(13), 1, display, lore));
					}
				}
			}
		}
	}
	
	public void resetValues()
	{
		this.Participants.clear();
		this.Hiders.Reset();
		this.Seekers.Reset();
		
		this.progressSeconds = this.progressExpire;
		this.progressTask = null;
		
		this.hideSeconds = this.hideExpire;
		this.hideTask = null;
	
		this.matchmakingSeconds = this.matchmakingExpire;
		this.matchmakingTask = null;
		this.matchmakingLocationID = null;
		this.matchmakingLocationName = null;
		
		this.cooldownSeconds = this.cooldownExpire;
		this.cooldownTask = null;
		
		this.townID = null;
		this.townName = null;
		this.entryTitle = null;
		
		this.Reward = this.OriginalReward;
		
		this.cooldown = false;
		this.matchmaking = false;
		this.inHub = false;
		this.progress = false;
		this.hideTime = false;
		this.finished = false;
	}
	
	public void announceOnline(List<String> message)
	{
		for(User user : Users2.users)
		{
			if (DataManager.Creations.FindCreation(user) == null)
			{
				for (String msg : message)
				{
					user.getPlayer().sendMessage(msg);
				}
			}
		}
	}
	
	public void enter(User user)
	{
		Player player = user.getPlayer();
		if (!this.matchmaking)
		{
			if (this.progress)
			{
				player.sendMessage(ColorOptions.error + "Hide and Seek is already in progress!");
			}
			player.sendMessage(ColorOptions.error + "Please wait untill the matchmaking of Hide and Seek starts");
			if (this.cooldown)
			{
				HashMap<String, Integer> time = Main.getCalculatedTime(this.cooldownSeconds);
				player.sendMessage(ColorOptions.error + "Hide and Seek matchmaking start in " + (time.get("minute") > 0 ? time.get("minute") + " minute(s) and " : "") + time.get("second") + " second(s)");
			}
			return;
		}
		if (this.getParticipating(user))
		{
			HashMap<String, Integer> time = Main.getCalculatedTime(this.matchmakingSeconds);
			player.sendMessage(ColorOptions.error + "You are already participating in the upcoming Hide and Seek!");
			player.sendMessage(ColorOptions.error + "Hide and Seek starts in " + (time.get("minute") > 0 ? time.get("minute") + " minute(s) and " : "") + time.get("second") + " second(s)");
			return;
		}
		HashMap<String, Integer> time = Main.getCalculatedTime(this.matchmakingSeconds);
		
		Participant participant = new Participant(user);
		this.addParticipant(participant);
		this.Reward += this.participantRewardStep;
		player.sendMessage(ColorOptions.messageachievement + "You are now participating in the next game of Hide and Seek");
		if (this.matchmakingSeconds <= 15)
		{
			this.sendToHub(participant);
		} else
		{
			player.sendMessage(ColorOptions.messageachievement + "You will be teleported to the Hide and Seek hub in " + (time.get("minute") > 0 ? time.get("minute") + " minute(s) and " : " ") + time.get("second") + " second(s)");
		}
	}
	
	public void leave(Participant participant)
	{
		if (this.cooldown)
		{
			return;
		}
		if (!this.Participants.contains(participant))
		{
			return;
		}
		
		if (this.Seekers.GetMembers().contains(participant))
		{
			this.removeSeeker(participant);
		} else
		{
			this.removeParticipant(participant);
			if (this.matchmaking)
			{
				this.Reward -= this.participantRewardStep;
			}
		}
		participant.getUser().getPlayer().sendMessage(ColorOptions.message + "You left Hide and Seek");
		participant.getUser().getPlayer().teleport(participant.getBeforeJoinLocation());
		participant.returnStoredInventory();
	}
	
//	public void storeInventory(User user)
//	{
//		Player player = user.getPlayer();
//		HashMap<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
//		
//		for (int i = 0; i < 36; i++)
//		{
//			contents.put(i, player.getInventory().getItem(i));
//		}
//		this.storedInventory.put(user, contents);
//		this.clearContents(user);
//	}
	
	public void setSeekerOutfit(Participant participant)
	{
		User user = participant.getUser();
		Player player = user.getPlayer();
		ItemStack bow = null;
		ItemStack arrows = null;
		boolean noHsBow = false;
		try
		{
			bow = this.product.createPropertyItem(this.product.getProductID("hsbow", false), 1, false, false);
		} catch (Exception ex)
		{
			noHsBow = true;
			bow = this.product.createPropertyItem(this.product.getProductID("practicebow", false), 1, false, false);
			ex.printStackTrace();
		}
		if (noHsBow)
		{
			player.getInventory().addItem(this.product.createPropertyItem(this.product.getProductID("arrow", false), 64, false, false));
			player.getInventory().addItem(this.product.createPropertyItem(this.product.getProductID("arrow", false), 64, false, false));
		} else
		{
			player.getInventory().addItem(this.product.createPropertyItem(this.product.getProductID("arrow", false), 16, false, false));
		}
		player.getInventory().setItemInHand(bow);
		player.updateInventory();
	}
	
//	public void restoreInventory(User user)
//	{
//		Player player = user.getPlayer();
//		this.clearContents(user);
//		HashMap<Integer, ItemStack> inventory = this.storedInventory.get(user);
//		for (Integer slot : inventory.keySet())
//		{
//			player.getInventory().setItem(slot, inventory.get(slot));
//			player.updateInventory();
//		}
//	}
	
	public void clearContents(User user)
	{
		for (int i = 0; i < 36; i++)
		{
			user.getPlayer().getInventory().setItem(i, null);
			user.getPlayer().updateInventory();
		}
	}
	
//	public void sendToHub(User user)
//	{
//		Player player = user.getPlayer();
//		Location location = player.getLocation();
//		player.sendMessage(ColorOptions.message + "Teleporting to Hide and Seek hub...");
//
//		this.beforeTeleportLocation.put(user, location);
//		this.storeInventory(user);
//		player.teleport(this.spawnpoint.getSpawnPointLocation(this.matchmakingLocationID));
//		player.setGameMode(GameMode.SURVIVAL);
//		user.setOwnerMode(false);
//		user.setStaffMode(false);
//	}
	
//	public void sendToBeforeLocation(Participant participant)
//	{
//		User user = participant.getUser();
//		Player player = participant.getUser().getPlayer();
//		
//		this.restoreInventory(user);
//		if (!this.beforeTeleportLocation.containsKey(user))
//		{
//			player.sendMessage(ColorOptions.message + "Teleporting back to spawn...");
//			player.teleport(this.spawnpoint.getSpawnPointLocation(this.spawnpoint.getSpawnPointID("spawn")));
//			return;
//		}
//		player.sendMessage(ColorOptions.message + "Teleporting to last known location before joining Hide and Seek...");
//		Location location = this.beforeTeleportLocation.get(user);
//		new BukkitRunnable()
//		{
//			public void run()
//			{
//				try
//				{
//					player.teleport(location);
//				} catch (Exception ex)
//				{
//					ex.printStackTrace();
//				}
//			}
//		}.runTaskLaterAsynchronously(main, 4*20);
//	}
	
	public void catchParticipant(User seeker, User userParticipant)
	{
		Participant participant = this.getParticipant(userParticipant);
		this.setSeeker(participant);
		userParticipant.getPlayer().sendMessage(ColorOptions.KAKFormat + ColorOptions.error + "You got caught by " + seeker.getUsername());
		userParticipant.getPlayer().sendMessage(ColorOptions.KAKFormat + ColorOptions.message + "You are a seeker now.");
		seeker.getPlayer().sendMessage(ColorOptions.KAKFormat + ColorOptions.messageachievement + "You caught " + userParticipant.getUsername() + ". Good job!");
		this.announceParticipants(this.Participants, Arrays.asList(ColorOptions.KAKFormat + ColorOptions.message + userParticipant.getUsername() + " was caught. Remaining participants: " + this.getHiders().GetMembers().size()));
	}
}
