package Minigames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Handlers.Menus;
import HideAndSeek.HideAndSeek;
import Main.Main;
import Menu.Menu;
import Products.Product;
import Sieges.Siege;
import SpawnPoints.SpawnPoint;
import Users.User;

public class MiniGame 
{
	protected Main main = Main.getPlugin(Main.class);
	protected Menu menu = new Menu();
	protected Product product = new Product();
	protected SpawnPoint spawnpoint = new SpawnPoint();

//	protected HashMap<Integer, String> phaseList = new HashMap<Integer, String>() {{
//		put(0, "Cooldown phase");
//		put(1, "Matchmaking phase");
//		put(2, "Voting phase");
//		put(3, "Hub joining phase");
//		put(4, "In progress phase");
//		put(5, "Hide phase");
//		put(6, "Finished phase");
//	}};
	
	protected String name;	//Stores the name of the mininame
	protected MiniGame instance;
	protected List<Participant> Participants = new ArrayList<Participant>();	//Stores the participants of the minigame
	protected int progressSeconds = 900;
	protected int progressExpire = 900;
	protected int matchmakingSeconds = 300;
	protected int matchmakingExpire = 300;
	protected int cooldownSeconds = 900;
	protected int cooldownExpire = 900;
	protected String hubLocationName = "HideAnkSeek";
	protected Integer matchmakingLocationID;
	protected Location hubLocation;
	protected Integer townID;
	protected String townName;
	protected Integer entryTitle = 0;
	protected boolean canJoin = false;
	protected boolean autoStart = true;
//	protected int currentStage;
	protected boolean matchmaking = false;
	protected boolean progress = false;
	protected boolean inHub = false;
	protected boolean cooldown = false;
	protected boolean finished = false;
	protected BukkitTask matchmakingTask;
	protected BukkitTask progressTask;
	protected BukkitTask cooldownTask;
	
	protected HashMap<Integer, List<String>> matchmakingNotifications = new HashMap<Integer, List<String>>();
	protected List<String> matchmakingMessage = new ArrayList<String>();
	protected List<String> allowedCommands = new ArrayList<String>(Arrays.asList(
			"/leave",
			"/msg",
			"/r",
			"/staffchat"
			));
	
	public MiniGame(String name, String joinCommand)
	{
		this.name = name;
		this.setMatchmakingNotifications(joinCommand);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public MiniGame getInstance()
	{
		return this.instance;
	}
	
	public List<Participant> getParticipants()
	{
		return this.Participants;
	}
	
	public Integer getTitleAverage()
	{
		Integer average = 1;
		
		Integer size = this.Participants.size();
		Integer total = 0;
		
		for (Participant participant : this.Participants)
		{
			User user = participant.getUser();
			total += user.getTitleID();
		}
		
		if (size == 0)
		{
			return average;
		}
		
		average = (int)total/size;
		
		return average;
	}
	
	public int getProgressSeconds()
	{
		return this.progressSeconds;
	}
	
	public int getProgressExpire()
	{
		return this.progressExpire;
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
	
	public String getHubLocationName()
	{
		return this.hubLocationName;
	}
	
	public Integer getHubLocationID()
	{
		return this.matchmakingLocationID;
	}
	
	public Location getHubLocation()
	{
		return this.hubLocation;
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
	
	public boolean getJoinable()
	{
		return this.canJoin;
	}
	
	public boolean getAutoStart()
	{
		return this.autoStart;
	}
	
	public boolean getMatchmaking()
	{
		return this.matchmaking;
	}
	
	public boolean getProgress()
	{
		return this.progress;
	}
	
	public boolean getInHub()
	{
		return this.inHub;
	}
	
	public boolean getCooldown()
	{
		return this.cooldown;
	}
	
	public boolean getFinished()
	{
		return this.finished;
	}
	
	public BukkitTask getMatchmakingTask()
	{
		return this.matchmakingTask;
	}
	
	public BukkitTask getProgressTask()
	{
		return this.progressTask;
	}
	
	public BukkitTask getCooldownTask()
	{
		return this.cooldownTask;
	}
	
	public List<String> getAllowedCommands()
	{
		return this.allowedCommands;
	}
	
	public HashMap<Integer, List<String>> getMatchmakingNotifications()
	{
		return this.matchmakingNotifications;
	}
	
	public boolean getParticipating(User user)
	{
		boolean contains = false;
		
		for (Participant part : this.getParticipants())
		{
			if (part.getUser() == user)
			{
				contains = true;
				break;
			}
		}
		
		return contains;
	}
	
	public Participant getParticipant(User user)
	{
		Participant part = null;
		
		for (Participant parts : this.getParticipants())
		{
			if (parts.getUser() == user)
			{
				part = parts;
				break;
			}
		}
		
		return part;
	}
	
	public List<User> getUserParticipants(List<Participant> participants)
	{
		List<User> Users = new ArrayList<User>();
		
		for (Participant Participant : participants)
		{
			Users.add(Participant.getUser());
		}
		
		return Users;
	}
	
	public void setMatchmakingTask(BukkitTask task, boolean override)
	{
		Main.logMessage("Setting matchmaking task for Minigame. Name: " + this.name + ", override: " + override + "...");
		
		if (override)
		{
			this.matchmakingTask = task;
			return;
		} else
		{
			if (this.matchmakingTask != null)
			{
				Main.logError("Error while setting matchmaking task: Task should be null but it is not. Name: " + this.name + ", override: " + override);
				return;
			} else
			{
				this.matchmakingTask = task;
			}
		}
		
		Main.logMessage("Matchmaking task set for Minigame. Name: " + this.name);
	}
	
	public void setProgressTask(BukkitTask task, boolean override)
	{
		Main.logMessage("Setting progress task for Minigame. Name: " + this.name + ", override: " + override + "...");
	
		if (override)
		{
			this.progressTask = task;
		} else
		{
			if (this.progressTask != null)
			{
				Main.logError("Errpr while setting progress task: Task should be null but it is not. Name: " + this.name + ", override: " + override);
				return;
			} else
			{
				this.matchmakingTask = task;
			}
		}
		
		Main.logMessage("Progress task set for Minigame. Name: " + this.name);
	}
	
	public void setCooldownTask(BukkitTask task, boolean override)
	{
		Main.logMessage("Setting cooldown task for Minigame. Name: " + this.name + ", override: " + override + "...");
		
		if (override)
		{
			this.cooldownTask = task;
		} else
		{
			if (this.cooldownTask != null)
			{
				Main.logError("Errpr while setting cooldown task: Task should be null but it is not. Name: " + this.name + ", override: " + override);
				return;
			} else
			{
				this.cooldownTask = task;
			}
		}
		
		Main.logMessage("Cooldown task set for Minigame. Name: " + this.name);
	}
	
	public void removeMatchmakingTask()
	{
		Main.logMessage("Removing matchmaking task for Minigame. Name: " + this.name + "...");
		
		if (this.matchmakingTask != null)
		{
			this.matchmakingTask.cancel();
			this.matchmakingTask = null;
			Main.logMessage("Removed matchmaking task for Minigame. Name: " + this.name);
			return;
		}
	}
	
	public void removeProgressTask()
	{
		Main.logMessage("Removing progress task for Minigame. Name: " + this.name + "...");
		
		if (this.progressTask != null)
		{
			this.progressTask.cancel();
			this.progressTask = null;
			Main.logMessage("Removed progress task for Minigame. Name: " + this.name);
			return;
		}
	}
	
	public void removeCooldownTask()
	{
		Main.logMessage("Removing cooldown task for Minigame. Name: " + this.name + "...");
		
		if (this.cooldownTask != null)
		{
			this.cooldownTask.cancel();
			this.cooldownTask = null;
			Main.logMessage("Removed cooldown task for Minigame. Name: " + this.name);
			return;
		}
	}
	
	public void addParticipant(Participant participant)
	{
		if (!this.Participants.contains(participant))
		{
			this.Participants.add(participant);
		}
	}
	
	public void removeParticipant(Participant participant)
	{
		if (this.getParticipants().contains(participant))
		{
			this.Participants.remove(participant);
			Users.Users.updateScoreBoard(Arrays.asList(participant.getUser()));
		}
	}
	
	public void announceOnline(List<String> message)
	{
		for(User user : Main.users)
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
	
	public void announceParticipants(List<Participant> participants, List<String> message)
	{
		for (Participant participant : participants)
		{
			
			for (String msg : message)
			{
				participant.getUser().getPlayer().getPlayer().sendMessage(msg);
			}
		}
	}
	
	public void orderLowToHigh()
	{
		Collections.sort(this.Participants, new Comparator<Participant>() {
		    @Override
		    public int compare(Participant o1, Participant o2) {
		        return Integer.valueOf(o1.getUser().getTitleID()).compareTo(o2.getUser().getTitleID());
		    }
		});
	}
	
	public void sendToHub(Participant participant)
	{
		User user = participant.getUser();
		Player player = user.getPlayer();
		player.sendMessage(ColorOptions.message + "Teleporting to " + this.name + " hub...");
		
		if (this.matchmaking)
		{
			participant.setBeforeJoinLocation(player.getLocation());
			participant.setStoredInventory();
			
			if (this.instance instanceof HideAndSeek)
			{
				participant.clearContents();
			}
		}
		if (this.matchmakingLocationID != null)
		{
			this.spawnpoint.TeleportNearby(3
					, user
					, this.spawnpoint.getSpawnPointLocation(this.matchmakingLocationID)
					, this.getUserParticipants(this.getParticipants()));
		}
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
		user.setOwnerMode(false);
		user.setStaffMode(false);
	}
	
	public void updateMenus(boolean reOpen)
	{
		for (User user : main.users)
		{
			Inventory menu = user.getOpenMenu();
			if (menu == null)
			{
				continue;
			}
			
			if (user.inOwnerModus() || user.inStaffModus())
			{
				if (instance instanceof HideAndSeek)
				{
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
								calcTime = main.getCalculatedTime(this.cooldownSeconds);
							}
							if (this.matchmaking)
							{
								calcTime = main.getCalculatedTime(this.matchmakingSeconds);
								status = "Matchmaking";
							}
							if (this.progress)
							{
								calcTime = main.getCalculatedTime(this.progressSeconds);
								status = "In progress";
							}
							display = ColorOptions.message + "Current stage: " + ColorOptions.messagesubjects + status;
							lore.add(ColorOptions.message + "" + calcTime.get("minute") + " minutes and " + calcTime.get("second") + " seconds");
							
							
							menu.setItem(13, product.setItemDescription(menu.getItem(13), 1, display, lore));
						}
					}
				}
			}
			if (instance instanceof Siege)
			{
				Siege siege = (Siege) this;
				if (menu.getName() == Menus.SiegeOverviewMenu)
				{
					if (reOpen)
					{
						this.menu.openSiegeOverview(user);
						continue;
					} else
					{
						Integer slot = 9;
						
						for (int i = 9; i < menu.getSize(); i++)
						{
							ItemStack item = menu.getItem(i);
							String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());

							Integer index = Integer.valueOf(display.split(" ")[1])-1;
							if (Sieges.Sieges.Sieges.get(index) == siege)
							{
								slot = i;
								break;
							}
						}
						ItemStack item = menu.getItem(slot);
						String display = item.getItemMeta().getDisplayName();

						String stage = "Cooldown";
						List<String> lore = item.getItemMeta().getLore();
						if (lore == null || lore.isEmpty())
						{
							this.menu.openSiegeOverview(user);
							continue;
						}
						//lore.remove(2);
						HashMap<String, Integer> timeFormat = null; 
						
						if (this.cooldown)
						{
							timeFormat = main.getCalculatedTime(this.cooldownSeconds);
						}
						if (this.matchmaking)
						{
							timeFormat = main.getCalculatedTime(this.matchmakingSeconds);
							stage = "Matchmaking";
						}
						if (this.progress)
						{
							timeFormat = main.getCalculatedTime(this.progressSeconds);
							stage = "In progress";
						}
						lore.set(3, ColorOptions.message + "Current stage: " + ColorOptions.messagesubjects + stage);
						lore.set(6, ColorOptions.message + "" + timeFormat.get("minute") + " minutes and " + timeFormat.get("second") + " seconds");
						
						menu.setItem(slot, product.setItemDescription(item, 1, display, lore));
					}
				}
				if (menu.getName() == Menus.SiegeInformationMenu)
				{
					if (reOpen)
					{
						this.menu.openSiegeInformation(user, siege, -1);
						continue;
					} else
					{
						List<Integer> IndexList = new ArrayList<Integer>(Arrays.asList(
								4
								));
						
						for (Integer index : IndexList)
						{
							ItemStack item = menu.getItem(index);
							String display = item.getItemMeta().getDisplayName();
							List<String> lore = item.getItemMeta().getLore();
							if (lore == null || lore.isEmpty())
							{
								this.menu.openSiegeInformation(user, siege, -1);
								continue;
							}
							//lore.remove(2);
							HashMap<String, Integer> calcTime = null; 
							
							if (this.cooldown)
							{
								calcTime = main.getCalculatedTime(this.cooldownSeconds);
							}
							if (this.matchmaking)
							{
								calcTime = main.getCalculatedTime(this.matchmakingSeconds);
							}
							if (this.progress)
							{
								calcTime = main.getCalculatedTime(this.progressSeconds);
							}
							lore.set(5, ColorOptions.message + "" + calcTime.get("minute") + " minutes and " + calcTime.get("second") + " seconds");
								
							menu.setItem(index, product.setItemDescription(menu.getItem(index), 1, display, lore));
						}
					}
				}
			}
		}
	}
	
	public void setMatchmakingNotifications(String joinCommand)
	{
		this.matchmakingNotifications.put(290, Arrays.asList(
				"",
				ColorOptions.KAKFormat + ColorOptions.message + ChatColor.BOLD + name + " begins in " + ColorOptions.messagesubjects + "2 minutes!",
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
				""
				));
	}
	
	public void resetValues()
	{
		this.Participants.clear();
		
		this.progressSeconds = this.progressExpire;
		this.matchmakingSeconds = this.matchmakingExpire;
		this.cooldownSeconds = this.cooldownExpire;
		
		this.matchmaking = false;
		this.progress = false;
		this.cooldown = false;
		this.inHub = false;
		this.finished = false;
		
		this.townID = null;
		this.townName = null;
		this.entryTitle = null;
	}
}
