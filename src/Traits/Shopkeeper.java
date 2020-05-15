package Traits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import DataManager.Worldguard;
import Exceptions.UserNotFoundException;
import Handlers.ClickShopItemEvent;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.QuestStateChangeEvent;
import Handlers.SoundHandler;
import Main.Main;
import Menu.Menu;
import Products.PropertyProduct;
import Properties.Properties;
import Properties.Property;
import Properties.PropertyCategory;
import Quests.Quest;
import Quests.QuestIntimidateRival;
import Scoreboards.ActionBar;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.event.NavigationBeginEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class Shopkeeper extends Trait
{
	SpawnPoint spawnpoint = new SpawnPoint();
	Menu menu = new Menu();
	PropertyCategory category = new PropertyCategory();
	Property property = new Property();
	PropertyProduct propertyproduct = new PropertyProduct();
	Main main = Main.getPlugin(Main.class);
	
	public Shopkeeper()
	{
		super("Shopkeeper");
	}
	
	public static String prefix = ColorOptions.message + "[" + ColorOptions.messageformat + "Shopkeeper" + ColorOptions.message + "] ";
	private String welcomeMSG = null;
	private String possibleSellMSG = ColorOptions.messageformat + "That's one heck of an offer you have there!";
	
	public Player target = null;
	public ArrayList<UUID> shoutList = new ArrayList<UUID>();
	public ArrayList<User> EnteredUsers = new ArrayList<User>();
	public ArrayList<User> QuestMessageRecieved = new ArrayList<User>();
	private Integer stock = null;
	private Integer lastSellMSG = null;
	private Integer talkDelay = 20;
	private Long nextTalk;
	private Integer openItemDelay = 60;
	private boolean fullmsg = false;
	private int fullmsgcount = 0;
	private ProtectedRegion PropertyRegion = null;
	private int PropertyID = -1;
	private Location Spawnpoint = null;
	private RegionManager regionManager = null;
	private ArmorStand QuestTag = null;
	private double QuestTagX = 0;
	private double QuestTagY = 2.1;
	private double QuestTagZ = 0;
	private boolean isMoving = false;
	private long respawnLong;
	private Integer respawnTime = 120;
	private boolean died = false;
	private boolean SpawnpointError = false;
	private int SpawnpointErrorCount = 0;
	private int SpawnpointErrorSetback = 60;
	
	@Override
	public void onSpawn()
	{
		try
		{
			this.setLocationInfo();
		} catch (Exception ex)
		{	}
		if (this.Spawnpoint != null)
		{
			this.npc.teleport(Spawnpoint, TeleportCause.PLUGIN);
		}
		this.nextTalk = System.currentTimeMillis() + (this.talkDelay*1000);
	}

	
	public void run()
	{
		if (npc.isSpawned())
		{
			if (!this.SpawnpointError)
			{
				this.setLocationInfo();
			} else
			{
				if ((this.SpawnpointErrorCount) >= (this.SpawnpointErrorSetback*1000))
				{
					this.SpawnpointError = false;
					this.SpawnpointErrorCount = 0;
				} else
				{
					this.SpawnpointErrorCount++;
				}
			}
			
			Entity Enpc = npc.getEntity();
			Long talkDelayMillis = Long.valueOf(talkDelay *1000);
			
			//List<Entity> entities = this.npc.getEntity().getNearbyEntities(5, 2, 5);

			for (User user : this.EnteredUsers)
			{
				Player target = user.getPlayer();
				Location location = target.getLocation();
				npc.faceLocation(location);
			}
			if (System.currentTimeMillis() >= this.nextTalk)
			{
				this.afkSuggest();
				this.talk(null);
				
				this.nextTalk = System.currentTimeMillis() + (this.talkDelay*1000);
			}
			if (this.Spawnpoint != null)
			{
				if (npc.getEntity().getLocation().distance(this.Spawnpoint) > 2)
				{
					if (!this.npc.getNavigator().isNavigating())
					{
						this.npc.getNavigator().setTarget(this.Spawnpoint);
					}
				}
			}
		} else
		{
//			if (died)
//			{
//				Bukkit.getConsoleSender().sendMessage("Died!");
//				long current = System.currentTimeMillis();
//				if (current > respawnLong)
//				{
//					this.respawn();
//				}
//			}
		}
	}
	
	public void respawn()
	{
		try
		{
			this.npc.spawn(this.Spawnpoint);
		} catch (Exception ex)
		{
			this.npc.spawn(this.spawnpoint.getSpawnPointLocation(this.property.getPropertySpawnPoint(PropertyID)));
		}
		if (npc.isSpawned())
		{
			this.died = false;
		}
	}
	
	public void setLocationInfo()
	{
		if (this.regionManager == null)
		{
			try
			{
				this.regionManager = Worldguard.getRegionManager(npc.getEntity().getWorld());
			} catch (Exception ex)
			{
				ex.printStackTrace();
				this.SpawnpointError = true;
				main.logError("Error while retrieving spawnpoint for Shopkeeper " + this.npc.getId());
			}
		}
		if (this.PropertyRegion == null)
		{
			try
			{
				this.PropertyRegion = Worldguard.getRegion(npc.getEntity().getLocation(), "Property", this.regionManager);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				this.SpawnpointError = true;
				main.logError("Error while retrieving spawnpoint for Shopkeeper " + this.npc.getId());
			}
		}
		if (PropertyID == -1)
		{
			try
			{
				this.PropertyID = Worldguard.getStructureIDbyRegion(this.PropertyRegion);
				this.welcomeMSG = this.prefix + ColorOptions.message + "Welcome to my " + category.getCategoryName(property.getCategoryID(this.PropertyID)) + ", the " + property.getPropertyName(this.PropertyID); 
			} catch (Exception ex)
			{
				Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Something went wrong when setting propertyID for npc " + npc.getId() + ", named " + npc.getName());
				ex.printStackTrace();
				this.SpawnpointError = true;
				main.logError("Error while retrieving spawnpoint for Shopkeeper " + this.npc.getId());
			}
		}
		if (this.Spawnpoint == null)
		{
			if (this.PropertyID == -1)
			{
				return;
			}
			try
			{
				this.Spawnpoint = this.spawnpoint.getSpawnPointLocation(this.property.getPropertySpawnPoint(this.PropertyID));
			} catch (Exception ex)
			{
				ex.printStackTrace();
				this.SpawnpointError = true;
				main.logError("Error while retrieving spawnpoint for Shopkeeper " + this.npc.getId());
			}
		}
	}
	
	@EventHandler
	public void onNavigationBegin(NavigationBeginEvent e)
	{
		if (e.getNPC().getId() != npc.getId())
		{
			return;
		}
		Bukkit.getConsoleSender().sendMessage("Begin target!");
	}
	
	@EventHandler
	public void onNavigateComplete(NavigationCompleteEvent e)
	{
		if (e.getNPC().getId() != npc.getId())
		{
			return;
		}
		Bukkit.getConsoleSender().sendMessage("Reached target!");
	}
	
	public void tryGreet(User user)
	{
		if (this.isOwner(user))
		{
			user.getPlayer().sendMessage(this.prefix + "Hello boss, how are you doing?");
			if (fullmsg == false)
			{
				user.getPlayer().sendMessage(this.prefix + ColorOptions.error + "A storage is full, transport it to a warehouse otherwise we can't buy new items!");
				fullmsg = true;
			} else if (fullmsgcount == 60*20)
			{
				fullmsgcount = 0;
				fullmsg = false;
			} else
			{
				fullmsgcount++;
			}
		} else
		{
			try
			{
				ActionBar bar = new ActionBar(this.welcomeMSG);
				bar.sendToPlayer(user.getPlayer());
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if (!this.EnteredUsers.contains(user))
		{
			this.EnteredUsers.add(user);
		}
	}
	
	public void tryFarewell(User user)
	{
		if (this.isOwner(user))
		{
			String timeOfDay = "day";
			if (!main.IsDay(user.getPlayer().getWorld()))
			{
				timeOfDay = "evening";
			}
			user.getPlayer().sendMessage(this.prefix + "Have a good " + timeOfDay + ", boss!");
		}
		if (Properties.hasActiveQuest(this.PropertyID) && !Properties.ActiveQuests.get(this.PropertyID).isAssigned())
		{
			user.getPlayer().sendMessage(this.prefix + "Are you sure you don't want to help me, " + user.getUsername() + "?");
		}
		if (this.EnteredUsers.contains(user))
		{
			this.EnteredUsers.remove(user);
		}
		this.removeQuestMessageReceived(user);
	}
	
	public boolean isOwner(User user)
	{
		boolean isOwner = false;
		
		try
		{
			if (this.property.getPropertyOwnerID(this.PropertyID) == user.getID())
			{
				isOwner = true;
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return isOwner;
	}
	
	public void talk(List<String> Messages)
	{
		for (User user : this.EnteredUsers)
		{
			if (user.isAfk())
			{
				continue;
			}
			
			if (Messages != null)
			{
				for(String msg : Messages)
				{
					user.getPlayer().sendMessage(msg);
				}
			} else
			{
				if (Properties.hasActiveQuest(this.PropertyID) && !Properties.ActiveQuests.get(this.PropertyID).isAssigned())
				{
					if (main.getRandom(1, 5) == 1 && !this.QuestMessageRecieved.contains(user))
					{
						List<String> Message = Properties.ActiveQuests.get(this.PropertyID).getShopkeeperMessage();
						for (String msg : Message)
						{
							user.getPlayer().sendMessage(msg);
						}
						this.addQuestMessageReceived(user);
					}
				}
			}
		}
	}
	
	public void afkSuggest()
	{
		if (this.PropertyID != -1)
		{
			for (User user : this.EnteredUsers)
			{
				if (user.isAfk())
				{
					List<Integer> ProductList = this.propertyproduct.getProductListbyProperty(this.PropertyID);
					if (ProductList.isEmpty())
					{
						return;
					}
					Integer relationID = this.propertyproduct.getRelationID(ProductList.get(main.getRandom(0, ProductList.size()-1)), this.PropertyID);
					new BukkitRunnable()
					{
						public void run()
						{
							propertyproduct.openItemInfo(user.getPlayer(), relationID);
						}
					}.runTaskLater(main, 2*20);
					if (user.getOpenMenu() == null)
					{
						user.getPlayer().sendMessage(this.prefix + "This might be a good offer for you");
					}
				} else
				{
					break;
				}
			}
		}
	}
	
	public void addQuestMessageReceived(User user)
	{
		if (!this.QuestMessageRecieved.contains(user))
		{
			this.QuestMessageRecieved.add(user);
		}
	}
	
	public void removeQuestMessageReceived(User user)
	{
		if (this.QuestMessageRecieved.contains(user))
		{
			this.QuestMessageRecieved.remove(user);
		}
	}
	
//	public List<User> getNearbyUsers()
//	{
//		List<User> NearbyUsers = new ArrayList<User>();
//		
//		for (Entity entity : this.npc.getEntity().getNearbyEntities(5, 2, 5))
//		{
//			if (entity instanceof Player && !entity.hasMetadata("NPC"))
//			{
//				Player target = (Player) entity;
//				UUID uuid = target.getUniqueId();
//				User userTarget = null;
//				
//				try
//				{
//					userTarget = users.getUser(uuid);
//				} catch (Exception ex)
//				{
//					ErrorHandlers.userNotFoundAction(null, target, true);
//					break;
//				}
//				NearbyUsers.add(userTarget);
//			}
//		}
//		
//		return NearbyUsers;
//	}
	
	@EventHandler
	public void onPush(NPCPushEvent e)
	{
		if (!this.npc.isSpawned())
		{
			return;
		}
		if (e.getNPC().getId() != this.npc.getId())
		{
			return;
		}
		if (e.getNPC().getNavigator().isNavigating())
		{
			return;
		}
		if (this.QuestTag == null)
		{
			return;
		}
		this.QuestTag.teleport(this.npc.getEntity().getLocation().add(QuestTagX, QuestTagY, QuestTagZ));
	}
	
	@EventHandler
	public void onQuestStateChange(QuestStateChangeEvent e)
	{
		if (e.getPropertyID() == this.PropertyID)
		{
			if (!e.getQuest().isAssigned())
			{
				this.QuestTag = (ArmorStand) this.npc.getEntity().getWorld().spawnEntity(this.npc.getEntity().getLocation().add(this.QuestTagX,	this.QuestTagY, this.QuestTagZ), EntityType.ARMOR_STAND);
				this.QuestTag.setCustomName(ChatColor.YELLOW + "Quest available!");
				
				this.QuestTag.setCustomNameVisible(true);
				this.QuestTag.setGravity(false);
				this.QuestTag.setVisible(false);
				this.QuestTag.setSmall(true);
				this.QuestTag.setMarker(true);

				this.talk(Arrays.asList(
						this.prefix + "Hello! I have a quest for you..",
						this.prefix + "Would you like to " + e.getQuest().getDescription() + "?",
						this.prefix + "Click me for more information!"
						));
			} else if (e.getQuest().isAssigned())
			{
				try
				{
					this.QuestTag.remove();
					this.QuestTag = null;
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler
	public void onPropertyEnter(RegionEnterEvent e)
	{
		if (!Worldguard.isPropertyRegion(e.getRegion()))
		{
			return;
		}
		if (Worldguard.getStructureIDbyRegion(e.getRegion()) != this.PropertyID)
		{
			return;
		}
		if (Worldguard.isChildRegion(e.getRegion()))
		{
			return;
		}
		if (!this.npc.isSpawned())
		{
			return;
		}
		Player player = e.getPlayer();
		User user = null;
		
		try
		{
			user = Users.getUser(player.getUniqueId());
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}

		this.tryGreet(user);
		
		Properties.tryMakeQuest(this.PropertyID);
		Properties.tryFinishQuest(this.PropertyID, user);
	}
	
	@EventHandler
	public void onPropertyLeave(RegionLeaveEvent e)
	{
		if (!Worldguard.isPropertyRegion(e.getRegion()))
		{
			return;
		}
		if (Worldguard.getStructureIDbyRegion(e.getRegion()) != this.PropertyID)
		{
			return;
		}
		if (Worldguard.isChildRegion(e.getRegion()))
		{
			return;
		}
		if (!this.npc.isSpawned())
		{
			return;
		}
		Player player = e.getPlayer();
		User user = null;
		
		try
		{
			user = Users.getUser(player.getUniqueId());
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}

		this.tryFarewell(user);
	}
	
	@EventHandler
	public void onClick(NPCRightClickEvent e)
	{
		if (!this.npc.isSpawned())
		{
			return;
		}
		if (e.getNPC() == npc)
		{
			if (e.getClicker() instanceof Player)
			{
				Player player = (Player) e.getClicker();
				UUID uuid = player.getUniqueId();
				User user = null;
				
				try
				{
					user = Users.getUser(uuid);
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(null, player, true);
					return;
				}
				if (npc.isSpawned())
				{
					Entity Enpc = npc.getEntity();
					RegionManager manager = Worldguard.getRegionManager(Enpc.getWorld());
					Integer propertyID = Worldguard.getStructureIDbyRegion("property", Enpc.getLocation(), manager);
					menu.openShopkeeperMenu(user, propertyID);
					player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
				}
			}
		}
	}
	
	@EventHandler
	public void onItemClick(ClickShopItemEvent e)
	{
		if (!this.npc.isSpawned())
		{
			return;
		}
		Integer npcID = e.getnpcID();
		if (npcID == npc.getId())
		{
			User user = e.getUser();
			UUID uuid = user.getUUID();
			Integer relationID = e.getRelationID();
			stock = propertyproduct.getAmount(relationID);
			List<String> sellMSG = new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "That's one heck of an offer you have there!",
					ColorOptions.message + "I only have " + ColorOptions.messagesubjects + stock + ColorOptions.message + " left in stock!"
					));
			if (Bukkit.getPlayer(uuid) != null)
			{
				Player target = Bukkit.getPlayer(uuid);
				Integer MSG = main.getRandom(0, (sellMSG.size()-1));
				if (stock == 0)
				{
					target.sendMessage(ColorOptions.messageformat + "Shopkeeper: " + ColorOptions.message + "I am affraid that item is out of stock!");
					return;
				}
				if (lastSellMSG != MSG)
				{
					target.sendMessage(ColorOptions.messageformat + "Shopkeeper: " + sellMSG.get(MSG));
					lastSellMSG = MSG;
				} else
				{
					MSG = main.getRandom(0, (sellMSG.size()-1));
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(NPCDamageByEntityEvent e)
	{
		if (!this.npc.isSpawned())
		{
			return;
		}
		boolean cancelled = false;
		NPC npc = e.getNPC();
		Entity damager = e.getDamager();
		
		if (npc.getId() != this.npc.getId())
		{
			return;
		}
		
		cancelled = true;
		if (damager instanceof Player)
		{
			Player player = (Player) damager;
			UUID uuid = player.getUniqueId();
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			}
			
			for (Quest quest : user.getQuestList())
			{
				if (quest instanceof QuestIntimidateRival)
				{
					QuestIntimidateRival QIR = (QuestIntimidateRival) quest;
					if (!QIR.isCollected() && !QIR.isCompleted() && QIR.getTargetShopkeeperID() == npc.getId())
					{
						cancelled = false;
						break;
					}
				}
			}
		}
		
		e.setCancelled(cancelled);
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e)
	{
		LivingEntity entity = e.getEntity();
		Entity killer = e.getEntity().getKiller();
		
		if (!CitizensAPI.getNPCRegistry().isNPC(entity))
		{
			return;
		}
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
		if (npc.getId() != this.npc.getId())
		{
			return;
		}

		if (!(killer instanceof Player))
		{
			return;
		}
		Player player = (Player) killer;
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}
		
		for (Quest quest : user.getQuestList())
		{
			if (quest instanceof QuestIntimidateRival)
			{
				QuestIntimidateRival QIR = (QuestIntimidateRival) quest;
				QIR.killShopkeeper(this.npc.getId());
				break;
			}
		}
		if (Properties.hasActiveQuest(this.PropertyID))
		{
			if (!Properties.ActiveQuests.get(this.PropertyID).isAssigned())
			{
				Properties.removeActiveQuest(PropertyID, Properties.ActiveQuests.get(this.PropertyID));
				try
				{
					this.QuestTag.remove();
					this.QuestTag = null;
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		new BukkitRunnable()
		{
			public void run()
			{
				respawn();
			}
		}.runTaskLaterAsynchronously(main, this.respawnTime*20);
	}
}
