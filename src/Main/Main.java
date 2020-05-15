package Main;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Afk.Afk;
import Afk.AfkCommand;
import Afk.AfkEvents;
import Arenas.ArenaCommands;
import Arenas.ArenaNPC;
import Arenas.ArenaTouch;
import Arenas.Duel;
import Arenas.DuelCommands;
import Arenas.DuelEvents;
import Broadcasts.BossBarEvents;
import Broadcasts.Broadcast;
import Currency.CheckSalary;
import Currency.CoinCommands;
import Currency.CoinsPickupEvent;
import Currency.GemCommands;
import Currency.GemPickupEvent;
import Currency.IncomePayout;
import Currency.PlayerPayEvent;
import Currency.RentPayment;
import Currency.SalaryPayout;
import DataManager.Users2;
import DataManager.Structures.Gates;
import Donator.DonatorChat;
import Donator.DonatorCommands;
import Donator.PlayerJoin_List;
import Effects.ProductConsume;
import Events.FridayLottery;
import Events.FridayLotteryCommands;
import Experience.ExperienceChangeEvents;
import Experience.ExperienceCommands;
import Friends.FriendCommands;
import Friends.FriendInteract;
import Friends.HitFriendEvent;
import Friends.MentionNameEvent;
import Friends.UpdateFriendRegions;
import Gates.GateCommands;
import Gates.GateEvents;
import Gates.GateToggle;
import Handlers.ColorOptions;
import Handlers.EnchantmentGlow;
import Handlers.IncomePayoutEvent;
import Handlers.RentPaymentEvent;
import Handlers.SalaryPayoutEvent;
import Handlers.SoundHandler;
import Handlers.WeatherChange;
import HideAndSeek.HideAndSeek;
import HideAndSeek.HideAndSeekCommands;
import HideAndSeek.HideAndSeekEvents;
import Houses.HomeCommands;
import Houses.HouseCommands;
import Houses.HouseSellEvent;
import Houses.HouseTouch;
import KillsDeaths.CombatCheck;
import KillsDeaths.KillCommands;
import KillsDeaths.KillDeathStat;
import KillsDeaths.RespawnLocation;
import Listeners.DoubleDamageListener;
import Listeners.EntityListener;
import Listeners.PlayerListener;
import Menu.CouponClick;
import Menu.DuelSetupClick;
import Menu.FriendManagerClick;
import Menu.ItemMenuClick;
import Menu.MenuClick;
import Menu.MenuCommand;
import Menu.OwnedhousesClick;
import Menu.OwnedpropertiesClick;
import Menu.PlayerManagerClick;
import Menu.QuestMenuClick;
import Minigames.BanditKill;
import Minigames.BanditSpawn;
import Minigames.DiscoverTown;
import Minigames.FishGame;
import Minigames.OcelotSpawn;
import Minigames.Participant;
import Minigames.Transport;
import Minigames.TransportEvents;
import Models.Structures.Gate;
import NPCs.ShopkeeperCommands;
import Products.CraftEvents;
import Products.DropItem;
import Products.EnchantbookClick;
import Products.ProductCommands;
import Products.PropertyProduct;
import Products.SoulboundCommands;
import Products.SoulboundEvents;
import Products.SpecialItemEvents;
import Properties.EnchantmentGenerator;
import Properties.HomelessEnter;
import Properties.ItemFrameAdd;
import Properties.PropertyCategoryCommands;
import Properties.PropertyCommands;
import Properties.PropertyEvents;
import Properties.PropertySellEvent;
import Properties.PropertyTouch;
import Properties.SpawnShopkeepers;
import Properties.StorageEvents;
import Resources.BlockBreakEvents;
import Resources.BlockRefresh;
import Resources.ResourceCommands;
import Resources.ResourceKillEvents;
import Resources.ResourceProperty;
import Resources.YmlFile;
import Rooms.Room;
import Rooms.RoomCommands;
import Rooms.RoomSellEvent;
import Scoreboards.ActionBar;
import Sieges.ScenarioCommands;
import Sieges.ScenarioCreationEvents;
import Sieges.Scenarios;
import Sieges.Siege;
import Sieges.SiegeCommands;
import Sieges.SiegeEvents;
import Skills.AssassinSkill;
import Skills.AttackSpeedEvent;
import Skills.AvengerSkill;
import Skills.DefenseEvent;
import Skills.ForgerSkill;
import Skills.HealthEvent;
import Skills.JuggernautSkill;
import Skills.NinjaSkill;
import Skills.PickpocketSkill;
import Skills.ShotbowSkill;
import Skills.SkillPointPickupEvent;
import Skills.SpecialSkillCommands;
import Skills.SpeedEvent;
import Skills.StrengthEvent;
import SpawnPoints.SpawnPoint;
import SpawnPoints.SpawnPointCommands;
import Streets.StreetCommands;
import Teleport.TeleportDelay;
import Teleport.TeleportMovement;
import Titles.NextTitleCommands;
import Titles.TitleChangeEvents;
import Towns.TownCommands;
import Towns.TownEvents;
import Traits.ArenaMaster;
import Traits.Bandit;
import Traits.Bandit_v2;
import Traits.CarrierTrait;
import Traits.Gladiator;
import Traits.Shopkeeper;
import Traits.TestTrait;
import Traits.TutorialTrait;
import Treasure.Treasure;
import Treasure.TreasureCommands;
import Treasure.TreasureEvents;
import Treasure.Treasures;
import Tutorial.Tutorial;
import Tutorial.TutorialCommands;
import Tutorial.TutorialEvents;
import UsefulCommands.DiscordCommand;
import UsefulCommands.EnchantmentCommand;
import UsefulCommands.EnderchestCommand;
import UsefulCommands.EnderchestViewEvent;
import UsefulCommands.FlyMode;
import UsefulCommands.GameModeCommand;
import UsefulCommands.HealCommands;
import UsefulCommands.Invsee;
import UsefulCommands.ItemRenamer;
import UsefulCommands.LoreEditer;
import UsefulCommands.MessageCommands;
import UsefulCommands.PageCommand;
import UsefulCommands.PingCommands;
import UsefulCommands.PlayerTeleportCommand;
import UsefulCommands.RankCommands;
import UsefulCommands.ReloadCommand;
import UsefulCommands.SpawnCommand;
import UsefulCommands.StaffChatCommand;
import UsefulCommands.WeatherChangeCommand;
import Users.JoinEvents;
import Users.OwnerCommands;
import Users.StatsCommands;
import Users.User;
import Users.UserCommands;
import Users.Users;
import Votes.VoteCommand;
import Votes.VoteEvent;
import commands.CreationCommand;
import commands.DistrictCommand;
import commands.StructureCommand;
import me.Pandi.Commands;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;


public class Main extends JavaPlugin
{
	//Instance of the database connection
	public static Connection connection;	
	//Gets the description file of this plugin
	PluginDescriptionFile description = this.getDescription();
	public static boolean debug = true;
	public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	public Double enchantExponent = 1.8;
	public static Float clickVolume = 1.0F;
	public Double contributionExponent = 0.06;
	public static Double eventMultiplier = 1.0;
	public int tickRate = 10;
	public static Integer largestDonatorID = 3;
	public Integer afkTime = 300;
	public Integer maxTitleID = 18;
	public static Integer combat = 10;
	public static Integer dropPercentage = 10;
	
	public static boolean enableSkills = true;
	public static boolean leveledTownEnter = false;
	
	public static Calendar now = Calendar.getInstance();
	SimpleDateFormat day = new SimpleDateFormat("dd");

	//Database connection file
	File dbconnection = new File(getDataFolder(), "db-connection.yml");
	File fridayLottery = new File(getDataFolder(), "friday-lottery.yml");
	File tutorialComplete = new File(getDataFolder(), "complete-tutorial.yml");
	
	public HashMap<String, File> resourceFiles = new HashMap<String, File>() {{
		put("ore-resources", new File(getDataFolder(), "ore-resources.yml"));
		put("wood-resources", new File(getDataFolder(), "wood-resources.yml"));
		put("harvest-resources", new File(getDataFolder(), "harvest-resources.yml"));
		put("resources-drop-amount", new File(getDataFolder(), "resources-drop-amount.yml"));
		}};
	
	public List<Duel> duelList = new ArrayList<Duel>();
	
	//Specify the plugin directory
	File plugindir = new File(getDataFolder() + "/");
	
	//Ownermodus to grant user in it special abilities  to manage other users
	public static HashMap<UUID, Boolean> ownermodus = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, Boolean> staffmodus = new HashMap<UUID, Boolean>();
	
	//Every player that joins the game will be registered in here, to check the salary time of a user
	public static HashMap<UUID, Long> registeredPlayerSalary = new HashMap<UUID, Long>();
	public static HashMap<UUID, Long> registeredPlayerIncome = new HashMap<UUID, Long>();
	public static HashMap<UUID, List<BukkitTask>> flashTask = new HashMap<UUID, List<BukkitTask>>();
	public static HashMap<UUID, List<Integer>> flashItemSlot = new HashMap<UUID, List<Integer>>();
	public static HashMap<UUID, HashMap<Integer, Integer>> scheduledgive = new HashMap<UUID, HashMap<Integer, Integer>>();
	public static HashMap<UUID, Integer> scheduledDonator = new HashMap<UUID, Integer>();
	public static HashMap<UUID, Long> joinLong = new HashMap<UUID, Long>();
	public static HashMap<Integer, Long> PropertyQuestLong = new HashMap<Integer, Long>();
//	public static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<User>();
	public static CopyOnWriteArrayList<User> offlineUsers = new CopyOnWriteArrayList<User>();
	public static CopyOnWriteArrayList<UUID> newPlayers = new CopyOnWriteArrayList<UUID>();
	public static CopyOnWriteArrayList<UUID> combatlogged = new CopyOnWriteArrayList<UUID>();
	public static HashMap<User, Long> incombat = new HashMap<User, Long>();
	public static HashMap<User, Long> ninjaSkill = new HashMap<User, Long>();
	public static HashMap<User, User> avenger = new HashMap<User, User>();
	public static CopyOnWriteArrayList<Transport> transports = new CopyOnWriteArrayList<Transport>();
	public static HashMap<UUID, Integer> titleChangeList = new HashMap<UUID, Integer>();
	public static Map<UUID, Integer> teleportconfirm = new HashMap<UUID, Integer>();
	
	public static HashMap<UUID, String> msgReceived = new HashMap<UUID, String>();
	
	public static HideAndSeek HideAndSeek = null;
	
	@SuppressWarnings("rawtypes")
	public static List<ArrayList> playerList = new ArrayList<ArrayList>(Arrays.asList(HomelessEnter.homelessList));
	
	public static List<Integer> contribution = Arrays.asList(5, 10, 15, 20, 30);
	private PluginManager pluginManager = getServer().getPluginManager();
	
	@SuppressWarnings({ "rawtypes", "serial" })
	private HashMap<Class, String> traits = new HashMap<Class, String>() {{
		put(Bandit.class, "Bandit");
		put(Gladiator.class, "Gladiator");
		put(Shopkeeper.class, "Shopkeeper");
		put(ArenaMaster.class, "ArenaMaster");
		put(TutorialTrait.class, "Tutorial");
		put(Bandit_v2.class, "Bandit_v2");
		put(CarrierTrait.class, "Carrier");
		put(TestTrait.class, "TestTrait");
		}};
	
	//Runs when plugin gets enabled in the server
	public void onEnable()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				clearAfkLeftovers();
			}
		}.runTaskLaterAsynchronously(this, 2*20);
		createTutorialCompleteFile();
		//Check if the plugin directory exists. If not, create it
		if(!plugindir.exists())
		{
			plugindir.mkdirs();
		}
		if (!fridayLottery.exists())
		{
			YmlFile file = new YmlFile();
			try {
				fridayLottery.createNewFile();
				YamlConfiguration config = file.getConfig(fridayLottery);
				config.set("ExpireDay", "Friday");
				config.set("ExpireTime", "20:00:00");
				config.set("Prize", 250000);
				config.set("Price", 25000);
				config.set("Participants", 0);
				config.createSection("Players");
				config.save(fridayLottery);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		for (File file : this.resourceFiles.values())
		{
			if (!file.exists())
			{
				if (file.getName().contains("drop-amount"))
				{
					try
					{
						file.createNewFile();
						YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
				        yml.set("cobblestone", 1);
				        yml.set("stone", 1);
				        yml.set("coalore", 1);
				        yml.set("ironore", 1);
				        yml.set("goldore", 1);
				        yml.set("lapislazuliore", 1);
				        yml.set("redstoneore", 1);
				        yml.set("diamondore", 1);
				        yml.set("emeraldore", 1);
				        yml.set("coal", 1);
				        yml.set("ironingot", 1);
				        yml.set("goldingot", 1);
				        yml.set("lapislazuli", 4);
				        yml.set("redstone", 4);
				        yml.set("diamond", 1);
				        yml.set("emerald", 1);
				        yml.set("oakwood", 1);
				        yml.set("sprucewood", 1);
				        yml.set("birchwood", 1);
				        yml.set("junglewood", 1);
				        yml.set("acaciawood", 1);
				        yml.set("darkoakwood", 1);
				        yml.save(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
				{
					try
					{
						file.createNewFile();
						YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
				        yml.set("LastID", 0);
				        yml.createSection("Blocks");
				        yml.save(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		//Send console info message that the plugin has been enabled
	    this.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Knights and Kings game version: " + description.getVersion() + " created by: Pandi Oldenzeel" + " has been " + ChatColor.GREEN + "Enabled!");
	    
	    //Try to establish database-connection
	    DBconnect();
	    //Register all commands from other classes
	    try {
			if (!this.connection.isClosed())
			{
			    registercommands();
			    registerEvents();
			    registerTraits(true);
			    registerGlow();
			    
			    //Start the task that checks user's salary payouts
			    this.startvariousTasks();
			    this.startOcelotTask();
			    this.startEventTask();
			    Treasures.instantiateAll(-1);
			    now.setTimeZone(TimeZone.getTimeZone("GMT-01:00"));
			    try {
					Broadcast.class.newInstance().startBroadcastTask();
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    try
			    {
			    	for (Player target : Bukkit.getOnlinePlayers())
			    	{
			    		User user = Users2.InstantiateUser(target.getUniqueId(), false);
			    		user.join(target);
			    	}
			    } catch(Exception e)
			    {
			    	e.printStackTrace();
			    	for (Player target : Bukkit.getOnlinePlayers())
			    	{
			    		target.kickPlayer(ColorOptions.error + "Please relog due to server reload");
			    	}
			    }
			    Users.updateScoreBoard(null);
			    //Treasures.activateTreasures();
			    //Check if the 15 minutes for the database connection check have passed
			    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() 
				{
					public void run() 
					{
						try 
						{
							if (getConnection() == null && getConnection().isClosed())
							{
								DBconnect();
							} else
							{
								DBreconnect();
							}
						} catch (SQLException e) 
			 	    	{
							e.printStackTrace();
						}
					}		
				}, 20, 900*20);
			} else
			{
				Bukkit.getPluginManager().disablePlugin(this);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Runs when plugin gets disabled in the server
	public void onDisable() 
    {
		this.saveUsers(null, true);
		for (Gate gate : DataManager.Structures.Gates.Gates)
		{
			DataManager.Structures.Gates.saveGate(gate);
		}
		for (Siege siege : Sieges.Sieges.Sieges)
		{
			siege.stopSiege();
		}
		for (Player player : Bukkit.getOnlinePlayers())
		{
			player.closeInventory();
		}
		for (Tutorial tut : TutorialEvents.tutorials)
		{
			tut.cancel(Arrays.asList(ColorOptions.error + "Tutorial cancelled due to a restart of the game!"));
		}
		for (Tutorial tut : TutorialEvents.introTutorials)
		{
			tut.cancel(Arrays.asList(ColorOptions.error + "Tutorial cancelled due to a restart of the game!"));
		}
		for (Afk afk : AfkEvents.afk)
		{
			afk.stopAfk();;
		}
		//this.refreshResources(null);
		Treasures.stopAll();
		this.clearAfkLeftovers();
	    registerTraits(false);
//		Property property = new Property();
//		NPCRegistry registry = CitizensAPI.getNPCRegistry();
//		for (Integer propertyID : property.getIDList(false, null, false, null))
//		{
//			Integer npcID = property.getNPCID(propertyID);
//			if (npcID != null && npcID != 0)
//			{
//				NPC shopkeeper = registry.getById(npcID);
//				if (shopkeeper != null)
//				{
//					if (shopkeeper.isSpawned())
//					{
//						shopkeeper.despawn();
//						Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Despawned the shopkeepers of property with ID " + propertyID + " in order to disable the plugin!");
//					}
//				}
//			}
//		}
		//Send the console a message that the plugin has been disabled
	    this.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Knights and Kings game version: " + description.getVersion() + " created by: Pandi Oldenzeel" + " has been " + ChatColor.RED + "Disabled!");
    }
	
	@SuppressWarnings("unchecked")
	public void registerTraits(boolean enable)
	{
		for (@SuppressWarnings("rawtypes") Class trait : this.traits.keySet())
		{
			if (enable)
			{
				CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(trait).withName(traits.get(trait)));
			} else
			{
				CitizensAPI.getTraitFactory().deregisterTrait(TraitInfo.create(trait).withName(traits.get(trait)));
			}
		}
	}
	
	public void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            EnchantmentGlow glow = new EnchantmentGlow(70);
            Enchantment.registerEnchantment(glow);
        }
        catch (IllegalArgumentException e){
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
	
	public void registercommands()
	{
		getCommand("district").setExecutor(new DistrictCommand(this));
		getCommand("town2").setExecutor(new commands.TownCommand(this));
		getCommand("structure").setExecutor(new StructureCommand(this));
		getCommand("creation").setExecutor(new CreationCommand(this));
		
		getCommand("afk").setExecutor(new AfkCommand(this));
		getCommand("initiate").setExecutor(new Commands(this));
		getCommand("test").setExecutor(new Commands(this));
		getCommand("arena").setExecutor(new ArenaCommands(this));
		getCommand("duel").setExecutor(new DuelCommands(this));
		getCommand("coins").setExecutor(new CoinCommands(this));
		getCommand("pay").setExecutor(new CoinCommands(this));
		getCommand("gems").setExecutor(new GemCommands(this));
		getCommand("balance").setExecutor(new CoinCommands(this));
		getCommand("balance").setAliases(Arrays.asList("bal"));
		getCommand("lottery").setExecutor(new FridayLotteryCommands(this));
		getCommand("experience").setExecutor(new ExperienceCommands(this));
		getCommand("experience").setAliases(Arrays.asList("exp"));
		getCommand("donator").setExecutor(new DonatorCommands(this));
		getCommand("friends").setExecutor(new FriendCommands(this));
		getCommand("friends").setAliases(Arrays.asList("friend", "f"));
		getCommand("request").setExecutor(new FriendCommands(this));
		getCommand("requests").setExecutor(new FriendCommands(this));
		getCommand("gate").setExecutor(new GateCommands(this));
		getCommand("hideandseek").setExecutor(new HideAndSeekCommands(this));
		getCommand("hideandseek").setAliases(Arrays.asList("hs"));
		getCommand("house").setExecutor(new HouseCommands(this));
		getCommand("home").setExecutor(new HomeCommands(this));
		
		getCommand("kills").setExecutor(new KillCommands(this));
		getCommand("menu").setExecutor(new MenuCommand(this));
		getCommand("shopkeeper").setExecutor(new ShopkeeperCommands(this));
		getCommand("product").setExecutor(new ProductCommands(this));
		getCommand("soulbound").setExecutor(new SoulboundCommands(this));
		getCommand("ghosted").setExecutor(new SoulboundCommands(this));
		getCommand("property").setExecutor(new PropertyCommands(this));
		getCommand("propertycategory").setExecutor(new PropertyCategoryCommands(this));
		getCommand("propertycategory").setAliases(Arrays.asList("pc"));
		getCommand("resourceproperty").setExecutor(new ResourceCommands(this));
		getCommand("resourceproperty").setAliases(Arrays.asList("rp"));
		getCommand("room").setExecutor(new RoomCommands(this));
		getCommand("scenario").setExecutor(new ScenarioCommands(this));
		getCommand("siege").setExecutor(new SiegeCommands(this));
		getCommand("specialskill").setExecutor(new SpecialSkillCommands(this));
		getCommand("spawnpoint").setExecutor(new SpawnPointCommands(this));
		getCommand("spawnpoint").setAliases(Arrays.asList("sp"));
		getCommand("point").setExecutor(new SpawnPointCommands(this));
		getCommand("point").setAliases(Arrays.asList("warp"));
		
		getCommand("street").setExecutor(new StreetCommands(this));
		
		getCommand("nexttitle").setExecutor(new NextTitleCommands(this));
		
		getCommand("town").setExecutor(new TownCommands(this));
		getCommand("treasure").setExecutor(new TreasureCommands(this));
		getCommand("tutorial").setExecutor(new TutorialCommands(this));
		getCommand("discord").setExecutor(new DiscordCommand(this));
		getCommand("enchant").setExecutor(new EnchantmentCommand(this));
		getCommand("enderchest").setExecutor(new EnderchestCommand(this));
		getCommand("enderchest").setAliases(Arrays.asList("ec"));
		getCommand("fly").setExecutor(new FlyMode(this));
		getCommand("gamemode").setExecutor(new GameModeCommand(this));
		getCommand("gamemode").setAliases(Arrays.asList("gm"));
		getCommand("invsee").setExecutor(new Invsee(this));
		getCommand("invsee").setAliases(Arrays.asList("is"));
		getCommand("inventory").setExecutor(new Invsee(this));
		getCommand("rename").setExecutor(new ItemRenamer(this));
		getCommand("lore").setExecutor(new LoreEditer(this));
		getCommand("message").setExecutor(new MessageCommands(this));
		getCommand("message").setAliases(Arrays.asList("msg"));
		getCommand("reply").setExecutor(new MessageCommands(this));
		getCommand("reply").setAliases(Arrays.asList("r"));
		getCommand("page").setExecutor(new PageCommand(this));
		getCommand("tpa").setExecutor(new PlayerTeleportCommand(this));
		getCommand("default").setExecutor(new RankCommands(this));
		getCommand("spawn").setExecutor(new SpawnCommand(this));
		getCommand("ping").setExecutor(new PingCommands(this));
		getCommand("weather").setExecutor(new WeatherChangeCommand(this));
		getCommand("knightsandkings").setExecutor(new ReloadCommand(this));
		getCommand("knightsandkings").setAliases(Arrays.asList("k&k"));
		getCommand("staffchat").setExecutor(new StaffChatCommand(this));
		getCommand("staffchat").setAliases(Arrays.asList("sc", "st"));
		getCommand("heal").setExecutor(new HealCommands(this));
		
		getCommand("ownermode").setExecutor(new OwnerCommands(this));
		getCommand("ownermode").setAliases(Arrays.asList("om"));
		getCommand("staffmode").setExecutor(new OwnerCommands(this));
		getCommand("staffmode").setAliases(Arrays.asList("sm"));
		getCommand("stats").setExecutor(new StatsCommands(this));
		getCommand("user").setExecutor(new UserCommands(this));
		
		getCommand("vote").setExecutor(new VoteCommand(this));
	}
	
	
	public void registerEvents()
	{
		pluginManager.registerEvents(new EntityListener(this), this);
		pluginManager.registerEvents(new PlayerListener(this), this);
		pluginManager.registerEvents(new DoubleDamageListener(this), this);
		
		pluginManager.registerEvents(new AfkEvents(this), this);
		pluginManager.registerEvents(new Commands(this), this);
		pluginManager.registerEvents(new DuelEvents(this), this);
		pluginManager.registerEvents(new ArenaTouch(this), this);
		pluginManager.registerEvents(new ArenaNPC(this), this);
		pluginManager.registerEvents(new BossBarEvents(this), this);
		pluginManager.registerEvents(new ExperienceChangeEvents(this), this);
		pluginManager.registerEvents(new DonatorChat(this), this);
		pluginManager.registerEvents(new PlayerJoin_List(this), this);
		pluginManager.registerEvents(new CoinsPickupEvent(this), this);
		pluginManager.registerEvents(new GemPickupEvent(this), this);
		pluginManager.registerEvents(new IncomePayout(this), this);
		pluginManager.registerEvents(new SalaryPayout(this), this);
		pluginManager.registerEvents(new RentPayment(this), this);
		pluginManager.registerEvents(new PlayerPayEvent(this), this);
		pluginManager.registerEvents(new WeatherChange(this), this);
		
		pluginManager.registerEvents(new CheckSalary(this), this);
		pluginManager.registerEvents(new MentionNameEvent(this), this);
		pluginManager.registerEvents(new ProductConsume(this), this);
		pluginManager.registerEvents(new HitFriendEvent(this), this);
		pluginManager.registerEvents(new FriendInteract(this), this);
		pluginManager.registerEvents(new UpdateFriendRegions(this), this);
		pluginManager.registerEvents(new GateEvents(this), this);
		
		pluginManager.registerEvents(new HideAndSeekEvents(this), this);
		pluginManager.registerEvents(new HouseSellEvent(this), this);
		pluginManager.registerEvents(new HouseTouch(this), this);
		pluginManager.registerEvents(new KillDeathStat(this), this);
		pluginManager.registerEvents(new CombatCheck(this), this);
		pluginManager.registerEvents(new TransportEvents(this), this);
		pluginManager.registerEvents(new OcelotSpawn(this), this);
		pluginManager.registerEvents(new RespawnLocation(this), this);
		pluginManager.registerEvents(new FishGame(this), this);
		pluginManager.registerEvents(new BanditSpawn(this), this);
		pluginManager.registerEvents(new BanditKill(this), this);
		pluginManager.registerEvents(new DiscoverTown(this), this);
		pluginManager.registerEvents(new MenuClick(this), this);
		pluginManager.registerEvents(new OwnedhousesClick(this), this);
		pluginManager.registerEvents(new OwnedpropertiesClick(this), this);
		pluginManager.registerEvents(new EnchantmentGenerator(this), this);
		pluginManager.registerEvents(new PlayerManagerClick(this), this);
		pluginManager.registerEvents(new ItemMenuClick(this), this);
		pluginManager.registerEvents(new CouponClick(this), (this));
		pluginManager.registerEvents(new DuelSetupClick(this), this);
		pluginManager.registerEvents(new FriendManagerClick(this), this);
		pluginManager.registerEvents(new QuestMenuClick(this), this);
		
		pluginManager.registerEvents(new SoulboundEvents(this), this);
		pluginManager.registerEvents(new SpecialItemEvents(this), this);
		pluginManager.registerEvents(new DropItem(this), this);
		pluginManager.registerEvents(new CraftEvents(this), this);
		pluginManager.registerEvents(new EnchantbookClick(this), this);
		pluginManager.registerEvents(new PropertySellEvent(this), this);
		pluginManager.registerEvents(new PropertyTouch(this), this);
		pluginManager.registerEvents(new PropertyEvents(this), this);
		pluginManager.registerEvents(new SpawnShopkeepers(this), this);
		pluginManager.registerEvents(new HomelessEnter(this), this);
		pluginManager.registerEvents(new ItemFrameAdd(this), this);
		pluginManager.registerEvents(new StorageEvents(this), this);
		pluginManager.registerEvents(new ResourceKillEvents(this), this);
		pluginManager.registerEvents(new BlockBreakEvents(this), this);
		pluginManager.registerEvents(new RoomSellEvent(this), this);
		pluginManager.registerEvents(new ScenarioCreationEvents(this), this);
		pluginManager.registerEvents(new SiegeEvents(this), this);
		pluginManager.registerEvents(new StrengthEvent(this), this);
		pluginManager.registerEvents(new AttackSpeedEvent(this), this);
		pluginManager.registerEvents(new SpeedEvent(this), this);
		pluginManager.registerEvents(new DefenseEvent(this), this);
		pluginManager.registerEvents(new HealthEvent(this), this);
		pluginManager.registerEvents(new PickpocketSkill(this), this);
		pluginManager.registerEvents(new AssassinSkill(this), this);
		pluginManager.registerEvents(new AvengerSkill(this), this);
		pluginManager.registerEvents(new ForgerSkill(this), this);
		pluginManager.registerEvents(new JuggernautSkill(this), this);
		pluginManager.registerEvents(new NinjaSkill(this), this);
		pluginManager.registerEvents(new ShotbowSkill(this), this);
		pluginManager.registerEvents(new SkillPointPickupEvent(this), this);
		pluginManager.registerEvents(new TitleChangeEvents(this), this);
		pluginManager.registerEvents(new TownEvents(this), this);
		pluginManager.registerEvents(new TeleportMovement(this), this);
		pluginManager.registerEvents(new EnderchestViewEvent(this), this);
		pluginManager.registerEvents(new TreasureEvents(this), this);
		pluginManager.registerEvents(new TutorialEvents(this), this);
		pluginManager.registerEvents(new ReloadCommand(this), this);
		pluginManager.registerEvents(new JoinEvents(this), this);
		
		pluginManager.registerEvents(new VoteEvent(this), this);
	}
	
	public void DBconnect()
	{
	    //Check if db-connection file exists, if not print error message
	    if (this.dbconnection.exists())
	    {
	    	//Load information from the db-conenction file
	    	YamlConfiguration dbfile = YamlConfiguration.loadConfiguration(dbconnection);
	    	
    		//Specify the database details loaded from the db-connection file
	    	String host = "localhost";
	    	String port = "8080";
	    	String database = "KnightAndKings";
	    	String username = "root";
	    	String password = "";
	    	
	    	if (dbfile.getString("HOST") != null)
	    	{
	    		host = dbfile.getString("HOST");
	    	}
	    	if (dbfile.getString("PORT") != null)
	    	{
	    		port = dbfile.getString("PORT");
	    	}
	    	if (dbfile.getString("DATABASE") != null)
	    	{
	    		database = dbfile.getString("DATABASE");
	    	}
	    	if (dbfile.getString("USER") != null)
	    	{
	    		username = dbfile.getString("USER");
	    	}
	    	if (dbfile.getString("PASSWORD") != null)
	    	{
	    		password = dbfile.getString("PASSWORD");
	    	}
	    	String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
	    	
	    	try
	    	{
	    		synchronized (this)
	    		{
	 	    	   if (getConnection() != null && !getConnection().isClosed())
		    	   {
	 		    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Connection is already established!");
		    	   }
	    		}
	    		
	    		Class.forName("com.mysql.jdbc.Driver");
	    		
	    		
	    		try
	    		{
		    		Class.forName("com.mysql.jdbc.Driver").newInstance();
		            Connection con = DriverManager.getConnection(url, username, password);
		            setConnection(con);
//					setConnection( DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));
					
	    		} catch(Exception e)
	    		{
		    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not load database details: " + e);
		    		Bukkit.getPluginManager().disablePlugin(this);
	    		}
	    	} catch(SQLException e)
	    	{
	    		e.printStackTrace();
	    	} catch(ClassNotFoundException e)
	    	{
	    		e.printStackTrace();
	    	} catch(Exception e)
	    	{
	    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not load database details: " + e);
	    	}
	    } else
	    {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not find database-details file! This may cause errors in the game");
	    }
	}
	
	public void DBreconnect()
	{
		try 
		{
			getConnection().close();
			DBconnect();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	public static Connection getConnection() 
	{
		return connection;
	}

	public void setConnection(Connection connection) 
	{
		this.connection = connection;
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Database connection has been established!");
	}
	
	//Check if a user exists in the database
	public boolean existUser(UUID uuid)
	{
		boolean exists = false;
		try 
		{
			//prepare the query to retrieve all information of a user
			PreparedStatement stmt = getConnection().prepareStatement("Select * From Player Where UUID=?;");	
			stmt.setString(1, uuid.toString());
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				exists = true;
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return exists;
	}
	
	//Method to create a new user
	public void CreateUser (String username, UUID UUID, String gender, InetAddress address)
	{
		Integer genderID = 1;
		if (gender.equalsIgnoreCase("male"))
		{
			genderID = 1;
		} else if (gender.equalsIgnoreCase("female"))
		{
			genderID = 2;
		} else
		{
			genderID = 1;
		}
		saveUser(username, UUID, genderID, address);
		new BukkitRunnable()
		{
			public void run()
			{
				User user = new User(UUID);
				user.join(Bukkit.getPlayer(UUID));
			}
		}.runTaskLaterAsynchronously(this, 2*20);
	}
	
	//Save information in the Database
	public void saveUser(String username, UUID uuid, Integer genderID, InetAddress address)
	{
		try 
		{
			PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO Player(Username, UUID, GenderID, JoinDate, LastLogin, Address) VALUES(?, ?, ?, ?, ?, ?);");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setString(1, username.toLowerCase());
			stmt.setString(2, uuid.toString());
			stmt.setInt(3, genderID);
			stmt.setString(4, this.getTime());
			stmt.setString(5, this.getTime());
			String ip = address.toString().replaceAll("/", "");
			stmt.setString(6, ip); 
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New player " + username + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Checks if an object is an integer or not
    public static boolean isInt(String str) 
    {
        try 
        {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) 
        {
            return false;
        }
    }
    
    //Return a random number between the two given numbers
    public static int getRandom(int lower, int upper) 
    {
    	Integer random = 0;
    	
    	Random Random = new Random();
    	
    	if (lower == upper)
    	{
    		return upper;
    	} else
    	{
        	random = Random.nextInt((upper - lower) + 1) + lower;
    	}
    	
    	
        return random;
    }
    
    public String stringBuilder(String[] args, Integer beginargs, Integer endargs)
    {
    	StringBuilder buffer = new StringBuilder();
    	
    	for(int i = 0; i < args.length; i++)
    	{
    		if (i >= beginargs && i <= endargs)
    		{
        	    buffer.append(' ').append(args[i]);
    		}
    	}
    	
    	return buffer.toString().substring(1);
    }

    public void startvariousTasks()
    {
    	Users.updateScoreBoard(null);
    	new BukkitRunnable()
    	{
    		
        	Room room = new Room();
    		PropertyProduct propertyproduct = new PropertyProduct();
			TeleportDelay delay = new TeleportDelay();
    		public void run()
    		{
    			Main main = Main.getPlugin(Main.class);
    			if (HideAndSeek == null)
    			{
    	        	HideAndSeek = new HideAndSeek();
    			}
    			if (Sieges.Sieges.Sieges.size() < 1)
    			{
    				Sieges.Sieges.Sieges.add(new Siege());
    			}
				Long current = System.currentTimeMillis();
    			delay.updateDelay();
    			List<Integer> tempdonators = Users.getTempDonatorIDList();
				for (User user : Users2.users)
				{
					if (current > user.getSalaryTime())
					{
						Bukkit.getServer().getPluginManager().callEvent(new SalaryPayoutEvent(user));
					}
					if (current > user.getIncomeTime())
					{
						Bukkit.getServer().getPluginManager().callEvent(new IncomePayoutEvent(user));
					}
				}
				for (Integer roomID : room.getRoomIDList(null))
				{
					Integer userID = room.getOwnerID(roomID);
					if (userID != null && userID != 0)
					{
						User owner = new User(Users.fetchUUIDbyID(userID));
						Long last = owner.getLastLogin();
						if (current > (last + ((86400*7)*1000)))
						{
							room.removeOwnerID(roomID);
							Bukkit.getConsoleSender().sendMessage("Removed the owner with ID " + userID + " of room with ID " + roomID + " because the owner didn't join in 7 days after his last login!");
						} else if (current > owner.getRentTime())
						{
							Bukkit.getServer().getPluginManager().callEvent(new RentPaymentEvent(owner));
						}
					}
				}
    			for (UUID uuid : AssassinSkill.Cooldowntime.keySet())
    			{
    				AssassinSkill.Cooldowntime.put(uuid, Integer.valueOf(AssassinSkill.Cooldowntime.get(uuid) -1));
    				if (AssassinSkill.Cooldowntime.get(uuid).intValue() == 0)
    				{
    					AssassinSkill.Cooldowntime.remove(uuid);
    				}
    			}
    			if (PickpocketSkill.Cooldown.size() > 0)
    			{
    				for (UUID key : PickpocketSkill.Cooldown.keySet())
    				{
    				    long victimcooldown = Long.valueOf(PickpocketSkill.Cooldown.get(key));
    					if (current > victimcooldown)
    					{
    						PickpocketSkill.Cooldown.remove(key);
    					}
    				}
    			}
    			for (Integer userID : tempdonators)
    			{
    				User user = new User(Users.fetchUUIDbyID(userID));
    				if (current > user.getTempDonatorExpireTime())
    				{
    					user.removeTempDonator();
    					if (Bukkit.getPlayer(user.getUUID()) != null)
    					{
    						Player player = Bukkit.getPlayer(user.getUUID());
    						player.sendMessage(ColorOptions.error + "Your rank-booster expired and you have been demoted to your previous rank!");
    					}
    				}
    			}
    			for (UUID uuid : HomelessEnter.homelessList)
    			{
    				Player player = Bukkit.getPlayer(uuid);
    				if (player != null)
    				{
    					player.damage(2);
    				}
    			}
    			Iterator<User> ninjaKeys = ninjaSkill.keySet().iterator();
    			while(ninjaKeys.hasNext())
    			{
    				User user = ninjaKeys.next();
    				if (current > ninjaSkill.get(user))
    				{
    					Player player = user.getPlayer();
    					for (Player players : Bukkit.getOnlinePlayers())
    	            	{
                    		players.showPlayer(player);
    	            	}
    	            	player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Ninja + "Ninja" + ChatColor.GRAY + "]" + ColorOptions.Ninja + "Ninja skill is deactivated!");
                		Bukkit.getServer().getWorld(player.getWorld().getName()).playSound(player.getLocation(), SoundHandler.BLAZE_DEATH, 1.0F, 1.0F);
    	            	ninjaKeys.remove();
    				}
    			}
    			Iterator<User> inCombat = incombat.keySet().iterator();
    			while(inCombat.hasNext())
    			{
    				User user = inCombat.next();
    				if (current > incombat.get(user))
    				{
    					inCombat.remove();
    					ActionBar bar = new ActionBar(ColorOptions.messagesubjects + "Out of combat!");
    					bar.sendToPlayer(user.getPlayer());
    				}
    			}
    			FridayLottery fridayLot = new FridayLottery();
    			Date expireTime = fridayLot.getExpireTime();
    			if (getDate().getDayOfWeek() == fridayLot.getExpireDay() && getDate().getHour() == expireTime.getHours() && getDate().getMinute() == expireTime.getMinutes() && getDate().getSecond() == expireTime.getSeconds())
    			{
    				fridayLot.endLottery();
    			}
    			if (getDate().getHour() == 0 && getDate().getSecond() >= 0 && getDate().getSecond() <= 1 && getDate().getMinute() == 1)
    			{
    				for (Integer relationID : propertyproduct.getPropertyProductIDList())
    				{
    					propertyproduct.saveDailyProductAmount(propertyproduct.getPropertyID(relationID), propertyproduct.getProductID(relationID));
    				}
    				Users.clearDailyUserStats();
    			}
    			for (Player target : CombatCheck.incombat.keySet())
    			{
    				if (Bukkit.getOnlinePlayers().contains(target))
    				{
    					Integer seconds = (CombatCheck.incombat.get(target))-1;
    					
    					if (seconds > 0)
    					{
        					ActionBar bar = new ActionBar(ColorOptions.error + "In combat for " + seconds + " seconds!");
        					bar.sendToPlayer(target);
        					CombatCheck.incombat.put(target, seconds);
    					} else
    					{
    						ActionBar bar = new ActionBar(ColorOptions.messagesubjects + "Out of combat!");
        					bar.sendToPlayer(target);
        					CombatCheck.incombat.remove(target);
    					}
    				} else
    				{
    					CombatCheck.incombat.remove(target);
    				}
    			}
    			for (BlockRefresh block : BlockBreakEvents.refreshList)
    			{
    				if (block.getRefresh() < current)
    				{
    					block.RefreshBlock();
    					continue;
    				}
    			}
//    			for (User user : AfkEvents.possibleAfk.keySet())
//    			{
//    				if (AfkEvents.possibleAfk.get(user) < current)
//    				{
//    					user.setAfk();
//    				}
//    			}
    			for (User user : Users2.users)
    			{
    				if (user.GetAfkCommence() < current)
    				{
    					user.setAfk();
    				}
    			}
    			for (Transport transport : Main.transports)
    			{
    				Long expire = transport.getExpire();
    				if (current > expire)
    				{
    					transport.cancel(ColorOptions.error + ColorOptions.messageArrow + "Your time is up!");
    				}
    			}
    			for (Integer PropertyID : Main.PropertyQuestLong.keySet())
    			{
    				if (Main.PropertyQuestLong.get(PropertyID) < current)
    				{
    					Main.PropertyQuestLong.remove(PropertyID);
    				}
    			}
    			for (GateToggle toggles : Gates.GateToggles)
    			{
    				if (current >= toggles.getTimeOut())
    				{
    					toggles.remove();
    				}
    			}
    		}
    	}.runTaskTimer(this, 10*20, 20);
    	new BukkitRunnable()
    	{
			YmlFile file = new YmlFile();
			ResourceProperty property = new ResourceProperty();
    		public void run()
    		{
    			String oreName = "ore-resources";
    			String woodName = "wood-resources";
    			String harvestName = "harvest-resources";
				Long current = System.currentTimeMillis();
				Integer harvestBlocks = file.getBlockIDList(harvestName, false, null).size();
				Integer oreBlocks = file.getBlockIDList(oreName, false, null).size();
				Integer woodBlocks = file.getBlockIDList(woodName, false, null).size();
				boolean orePart = false;
				boolean woodPart = false;
				boolean harvestPart = false;
				if (oreBlocks > 50)
				{
					orePart = true;
				}
				if (woodBlocks > 50)
				{
					woodPart = true;
				}
				if (harvestBlocks > 50)
				{
					harvestPart = true;
				}
    			for (Integer blockID : file.getBlockIDList(oreName, orePart, 25))
    			{
    				Long cooldown = file.getCooldownMillis(oreName, blockID);
    				Long rest = (cooldown-current);
    				if (rest < Long.valueOf(10*1000) && rest > Long.valueOf(9*1000))
    				{
    					property.checkPlayersbeforeChange(oreName, blockID, 10);
    				}
    				if (cooldown < current)
    				{
    					file.changeBlock(oreName, blockID, false);
    				}
    			}
    			for (Integer blockID : file.getBlockIDList(woodName, woodPart, 25))
    			{
    				Long cooldown = file.getCooldownMillis(woodName, blockID);
    				Long rest = (cooldown-current);
    				if (rest < Long.valueOf(10*1000) && rest > Long.valueOf(9*1000))
    				{
    					property.checkPlayersbeforeChange(woodName, blockID, 10);
    				}
    				if (cooldown < current)
    				{
    					file.changeBlock(woodName, blockID, false);
    				}
    			}
    			for (Integer blockID : file.getBlockIDList(harvestName, harvestPart, 25))
    			{
    				Long cooldown = file.getCooldownMillis(harvestName, blockID);
    				Long rest = (cooldown-current);
    				if (rest < Long.valueOf(10*1000) && rest > Long.valueOf(9*1000))
    				{
    					property.checkPlayersbeforeChange(harvestName, blockID, 10);
    				}
    				if (cooldown < current)
    				{
    					file.changeBlock(harvestName, blockID, false);
    				}
    			}
    			for (Treasure treasure : Treasures.treasures)
    			{
    				treasure.SaveDiscoveredList();
    			}
    		}
    	}.runTaskTimer(this, 0, 300*20);
	    new BukkitRunnable()
	    {
	    	public void run()
	    	{
	    		for (Treasure treasure : Treasures.treasures)
	    		{
	    			List<Player> players = treasure.getNearbyPlayers();
	    			treasure.PlaySound(players);
	    			treasure.PlayParticles(players);
	    		}
	    	}
	    }.runTaskTimer(this, 10*20, 4*20);
//    	new BukkitRunnable()
//    	{
//			BossBar bar = new BossBar();
//    		public void run()
//    		{
//    			for (Player player : Bukkit.getOnlinePlayers())
//    			{
//    				UUID uuid = player.getUniqueId();
//    				if (bar.barList.containsKey(uuid))
//    				{
//    					bar.updateBar(player);
//    				} else
//    				{
//    					bar.newBar(player);
//    				}
//    			}
//    		}
//    	}.runTaskTimer(this, 0, 1*20);
//    	new BukkitRunnable()
//    	{
//    		public void run()
//    		{
//    			ShopkeeperCommands.reloadShopkeeper(Bukkit.getConsoleSender());
//    		}
//    	}.runTaskTimer(this, 0, 3600*20);
    	new BukkitRunnable()
    	{
    		public void run()
    		{
    			Long current = System.currentTimeMillis();
    			for (Duel duel : duelList)
    			{
    				if (duel.prepareExpire != null)
    				{
    					if (duel.prepareExpire < current)
    					{
    						duel.tryStart();
    					} else if (((int)(duel.prepareExpire-current)/1000) == 10)
    					{
    						Integer remaining = ((int)(duel.prepareExpire-current)/1000);
        					for (User user : duel.getPlayers())
        					{
        						duel.sendMessage(user.getUUID(), ColorOptions.error + "" + ChatColor.BOLD + remaining + " seconds of preperation left!");
        						user.getPlayer().playSound(user.getPlayer().getLocation(), SoundHandler.NOTE_PLING, 1.0F, 0.1F);
        					}
    					}
    				} else
    				if (duel.duelExpire != null)
    				{
    					if (duel.duelExpire < current)
    					{
    						duel.endDuel(false, null, null, 1);
    					} else if (((int)(duel.duelExpire-current)/1000) <= 10)
        				{
        					Integer remaining = ((int)(duel.duelExpire-current)/1000);
        					for (User user : duel.getPlayers())
        					{
        						duel.sendMessage(user.getUUID(), ColorOptions.error + "" + ChatColor.BOLD + remaining + " seconds left!");
        					}
        				}
    				}
    			}
    		}
    	}.runTaskTimer(this, 0, 1*20);
    }
    
    public void startOcelotTask()
    {
    	new BukkitRunnable()
    	{
    		public void run()
    		{
				if (Bukkit.getOnlinePlayers().size() >= 3)
				{
					Bukkit.getServer().broadcastMessage(ColorOptions.messageachievement + "Loot-ocelots have spawned in all towns!");
					SpawnPoint spawnpoint = new SpawnPoint();
					Integer count = 0;
					for (Integer spawnpointID : spawnpoint.getSpawnPointList(false, false, true, false, false, false))
					{
						if (spawnpoint.getSpawnPointName(spawnpointID).contains("ocelot"))
						{
							Location loc = spawnpoint.getSpawnPointLocation(spawnpointID);
							loc.getWorld().spawnEntity(loc, EntityType.OCELOT);
							count++;
						}
					}
					for (Player player : Bukkit.getOnlinePlayers())
					{
						player.playSound(player.getLocation(), SoundHandler.CAT_MEOW, 1.0F, 1.0F);
					}
					Bukkit.broadcastMessage(ColorOptions.messageachievement + "A total of " + count + " have spawned. Kill them to earn money, exp and other bonusses!");
				}
    		}
    	}.runTaskTimer(this, 0, 600*20);
    }
    
    public void startEventTask()
    {
    	this.startFridayBroadcast();
    	new BukkitRunnable()
    	{
    		public void run()
    		{
    		    if (getCalendar().getDayOfWeek() == DayOfWeek.FRIDAY)
    		    {
    		    	Main.eventMultiplier = Main.eventMultiplier +0.5;
    		    } else
    		    {
    		    	if ((Main.eventMultiplier-0.5) >= 1)
    		    	{
    		    		Main.eventMultiplier = Main.eventMultiplier-0.5;
    		    	}
    		    }
    		}
    	}.runTaskTimer(this, 0, 3600*20);
    }
    
    public void startFridayBroadcast()
    {
		if (getCalendar().getDayOfWeek() == DayOfWeek.FRIDAY)
		{
	    	new BukkitRunnable()
	    	{
	    		public void run()
	    		{
	    			if (getCalendar().getDayOfWeek() == DayOfWeek.FRIDAY)
	    			{
		    		    Bukkit.broadcastMessage(ColorOptions.statsbrackets);
		    		    Bukkit.broadcastMessage(ColorOptions.dbformat + "It's friday! This means that the friday-event is on!");
		    		    Bukkit.broadcastMessage(ColorOptions.dbsubjects + "Enjoy bonusses of 50% on salary/income/votes and picking op gold/diamonds/emeralds!");
		    		    Bukkit.broadcastMessage(ColorOptions.dbformat + "Don't forget about the lottery with a minimum jackpot of 250.000 coins!");
		    		    Bukkit.broadcastMessage(ColorOptions.statsbrackets);
	    			}
	    		}
	    	}.runTaskTimer(this, 0, 930*20);
		}
    }
    
    public static boolean getHideAndSeekParticipating(User user)
    {
    	boolean participating = false;
    	
    	if (HideAndSeek == null)
    	{
    		return participating;
    	}
    	
    	for (Participant part : HideAndSeek.getParticipants())
    	{
    		if (part.getUser() == user)
    		{
    			participating = true;
    			break;
    		}
    	}
    	
    	return participating;
    }
    
    public boolean checkContribution(Integer contribution)
    {
    	boolean exist = false;
    	
    	if (this.contribution.contains(contribution))
    	{
    		exist = true;
    	}
    	
    	return exist;
    }
    
    public static ZoneId getZoneId()
    {
    	return ZoneId.of("Europe/Amsterdam");
    }
    
    public static ZonedDateTime getCalendar()
    {
    	ZoneId tz = getZoneId();
    	ZonedDateTime zdt = ZonedDateTime.now(tz);
    
    	return zdt;
    }
    
    public static ZonedDateTime getDate()
    {
    	ZonedDateTime now = getCalendar();
//    	test.set
//    	String time = now.getDayOfMonth() + "-" + now.getMonthValue() + "-" + now.getYear() + " " + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();
    	
    	return now;
    }
    
    public static String getTime()
    {    
    	DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//	    SimpleDateFormat dt = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    	String time = dt.format(Main.getDate());
    	
    	return time;
    }
    
    public java.sql.Date getSQLDate()
    {
    	ZonedDateTime now = this.getCalendar();
    	LocalDate local = now.toLocalDate();
    	
    	java.sql.Date sqlDate = java.sql.Date.valueOf(local);
    	
    	return sqlDate;
    }
    
    public Integer getMenuSize(Integer units)
    {
    	Integer base = 1;
    	
    	if (units >= 0 && units <= 9)
    	{
    		base = base+1;
    	} else
    	if (units > 9 && units <= 18)
    	{
    		base = base+2;
    	} else
    	if (units > 18 && units <= 27)
    	{
    		base = base+3;
    	} 
    	else
    	if (units > 27 && units <= 36)
        {
        	base = base+4;
        }
    	else
        if (units > 36 && units <= 45)
        {
            base = base+5;
        } else
        {
        	base = base+5;
        }
    	return (9*base);
    }
    
    //Calculate the amount of millis a specific timevalue is FE time=10 and timeValue=hours -> 36000000 millis
    public Long calculateTimeValue(Integer time, String timeValue)
    {
    	Long finalTime = 1000L;
    	
    	if (timeValue.equalsIgnoreCase("seconds") || timeValue.equalsIgnoreCase("second"))
    	{
    		finalTime = finalTime * (time);
    	} else
    	if (timeValue.equalsIgnoreCase("minutes") || timeValue.equalsIgnoreCase("minute"))
    	{
    		finalTime = finalTime * (time*60);
    	} else
    	if (timeValue.equalsIgnoreCase("hours") || timeValue.equalsIgnoreCase("hour"))
    	{
    		finalTime = finalTime * (time*3600);
    	} else
    	if (timeValue.equalsIgnoreCase("days") || timeValue.equalsIgnoreCase("day"))
    	{
    		finalTime = finalTime * (time*86400);
    	}
    		
    	return finalTime;
    }
    
    public String getStringtime(Integer seconds)
    {
    	String timeString = null;
    	
    	Integer hours = seconds / 3600;
    	Integer minutes = (seconds % 3600) / 60;
    	Integer secs = seconds % 60;

    	timeString = String.format("%02d:%02d:%02d", hours, minutes, secs);
    	
    	return timeString;
    }
    
    //Returns a hashmap with the keys hour, minute, second. The value is the value of that timevalue
    public static HashMap<String, Integer> getCalculatedTime(Integer seconds)
    {
    	HashMap<String, Integer> timeValues = new HashMap<String, Integer>();
    	
    	Integer hours = seconds / 3600;
    	Integer minutes = (seconds % 3600) / 60;
    	Integer secs = seconds % 60;
    	
    	timeValues.put("hour", hours);
    	timeValues.put("minute", minutes);
    	timeValues.put("second", secs);
    	
    	return timeValues;
    }
    
    public Integer getHourtime(Integer seconds)
    {
    	return (seconds/3600);
    }
    
    public Integer getRestMinutetime(Integer seconds)
    {
    	return ((seconds % 3600) / 60);
    }
    
    public Integer getRestSecondtime(Integer seconds)
    {
    	return (seconds%60);
    }
    
    public void createTutorialCompleteFile()
    {
    	if (!this.tutorialComplete.exists())
    	{
    		YmlFile file = new YmlFile();
			try {
				tutorialComplete.createNewFile();
				YamlConfiguration config = file.getConfig(tutorialComplete);
				config.set("tutorialAmount", 0);
				config.createSection("Tutorials");
				config.save(tutorialComplete);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
    	}
    }
    
    public static void Notify(String message)
    {
    	Bukkit.broadcastMessage(ColorOptions.KAKFormat + "" + ChatColor.BOLD + message);
    }
    
	public void clearAfkLeftovers()
	{
		Bukkit.getConsoleSender().sendMessage("Starting to clear leftovers: Afk- and Quest-particles");
		for(World w: Bukkit.getWorlds())
		{
			Bukkit.getConsoleSender().sendMessage("Amount of entities found: " + w.getEntities().size());
			for(Entity e: w.getEntities())
			{
				if(e.getName().equals("Z") || e.getName().equals("Zz") || e.getName().equals(ChatColor.YELLOW + "Quest available!"))
				{
					e.remove();
				} else if (ChatColor.stripColor(e.getName()).contains("Gate: ") || ChatColor.stripColor(e.getName()).contains("Gate health: "))
				{
					boolean activeGate = false;
					for (Gate gate : DataManager.Structures.Gates.Gates)
					{
						if (gate.getGateEntity() == e)
						{
							activeGate = true;
							break;
						}
					}
					if (activeGate)
					{
						continue;
					} else
					{
						e.remove();
					}
				} else if (ChatColor.stripColor(e.getName()).contains("Captured: "))
				{
					e.remove();
				}
			}
		}
	}
	
	public void saveUsers(User userSender, boolean force)
	{
		try 
		{
			Statement stmt = Main.getConnection().createStatement();
			for (User user : Users2.users)
			{
				if (!force)
				{
					user.saveAssignments();
				}
				stmt.addBatch(user.getSaveQuery());
			}
			stmt.executeBatch();
			Bukkit.getConsoleSender().sendMessage(ColorOptions.messagesubjects + "Succesfully saved all users to the database!");
			if (userSender != null)
			{
				userSender.getPlayer().sendMessage(ColorOptions.messagesubjects + "Succesfully saved all users to the database!");
			}
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to save all users to the database!");
			if (userSender != null)
			{
				userSender.getPlayer().sendMessage(ColorOptions.error + "Failed to save all users to the database! Check the console for more information");
			}
		}
		
	}
	
	public void refreshResources(User user)
	{
//		new BukkitRunnable()
//		{
//			public void run()
//			{
//				Main main = Main.getPlugin(Main.class);
//				YmlFile file = new YmlFile();
//    			String oreName = "ore-resources";
//    			String woodName = "wood-resources";
//    			String harvestName = "harvest-resources";
//				Integer harvestBlocks = file.getBlockIDList(harvestName, false, null).size();
//				Integer oreBlocks = file.getBlockIDList(oreName, false, null).size();
//				Integer woodBlocks = file.getBlockIDList(woodName, false, null).size();
//				boolean orePart = false;
//				boolean woodPart = false;
//				boolean harvestPart = false;
//				if (oreBlocks > 50)
//				{
//					orePart = true;
//				}
//				if (woodBlocks > 50)
//				{
//					woodPart = true;
//				}
//				if (harvestBlocks > 50)
//				{
//					harvestPart = true;
//				}
//				
//				for (BlockRefresh block : BlockBreakEvents.refreshList)
//    			{
//					block.RefreshBlock();
//    			}
//				
//				if (orePart)
//				{
//					new BukkitRunnable()
//					{
//						public void run()
//						{
//			    			for (Integer blockID : file.getBlockIDList(oreName, file.getBlockIDList(oreName, false, null).size() > 50 ? true : false, 25))
//			    			{
//								file.changeBlock(oreName, blockID, true);
//			    			}
//						}
//					}.runTaskTimerAsynchronously(main, 0, 1*20);
//				} else
//				{
//	    			for (Integer blockID : file.getBlockIDList(oreName, orePart, 25))
//	    			{
//						file.changeBlock(oreName, blockID, true);
//	    			}
//				}
//				
//				if (woodPart)
//				{
//					new BukkitRunnable()
//					{
//						public void run()
//						{
//			    			for (Integer blockID : file.getBlockIDList(woodName, file.getBlockIDList(woodName, false, null).size() > 50 ? true : false, 25))
//			    			{
//								file.changeBlock(woodName, blockID, true);
//			    			}
//						}
//					}.runTaskTimerAsynchronously(main, 0, 1*20);
//				} else
//				{
//	    			for (Integer blockID : file.getBlockIDList(woodName, woodPart, 25))
//	    			{
//						file.changeBlock(woodName, blockID, true);
//	    			}
//				}
//				
//				if (harvestPart)
//				{
//					new BukkitRunnable()
//					{
//						public void run()
//						{
//			    			for (Integer blockID : file.getBlockIDList(harvestName, file.getBlockIDList(harvestName, false, null).size() > 50 ? true : false, 25))
//			    			{
//								file.changeBlock(harvestName, blockID, true);
//			    			}
//						}
//					}.runTaskTimerAsynchronously(main, 0, 1*20);
//				} else
//				{
//	    			for (Integer blockID : file.getBlockIDList(harvestName, harvestPart, 25))
//	    			{
//						file.changeBlock(harvestName, blockID, true);
//	    			}
//				}
//				
//				if (user != null)
//				{
//					user.getPlayer().sendMessage(ColorOptions.messageachievement + "Succesfully reloaded all resource blocks!");
//				}
//			}
//		}.runTask(this);
	}
	
	public boolean IsDay(World world)
	{
		boolean isDay = true;
		
		if (world.getTime() > 12300 || world.getTime() < 23850)
		{
			isDay = false;
		}
		
		return isDay;
	}
	
	public static void logMessage(String message)
	{
		if (debug)
		{
			Bukkit.getConsoleSender().sendMessage(message);
		}
	}
	
	public static void logError(String message)
	{
		if (debug)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + message);
			Users.sendStaffMessage(ColorOptions.error + message);
		}
	}
	
	public void reload(CommandSender sender)
	{
//		boolean noWarnings = true;
//		boolean savedAll = false;
//		users.sendStaffMessage(ColorOptions.message + "Saving and reloading " + ColorOptions.messagesubjects + getDescription().getName() + ColorOptions.message + " version " + ColorOptions.messagesubjects + getDescription().getVersion());
//		
//		Stream.of(
//				new Runnable()
//				{
//					public void run()
//					{
//						users.sendStaffMessage("Saving user data...");
//						try
//						{
//							saveUsers(null, false);
//							users.sendStaffMessage("User data saved.");
//						} catch (Exception ex)
//						{
//							ex.printStackTrace();
//							users.sendStaffMessage(ColorOptions.error + "Error while saving users. Please notify a developer");
//							users.sendStaffMessage(ColorOptions.error + "Failed to save Knights and Kings. Preventing server reload..");
//							users.sendStaffMessage(ColorOptions.error + "Please try to reload again.");
//							return;
//						}
//					}
//				},
//				new Runnable()
//				{
//					public void run()
//					{
//						users.sendStaffMessage("Saving resource-blocks...");
//						try
//						{
//							refreshResources(null);
//							users.sendStaffMessage("Resource-blocks saved.");
//						} catch (Exception ex)
//						{
//							ex.printStackTrace();
//							users.sendStaffMessage(ColorOptions.error + "Error while saving resource-blocks. Please notify a developer");
//							users.sendStaffMessage(ColorOptions.error + "Failed to save Knights and Kings. Preventing server reload..");
//							users.sendStaffMessage(ColorOptions.error + "Please try to reload again.");
//							return;
//						}
//					}
//				},
//				new Runnable()
//				{
//					public void run()
//					{
//						users.sendStaffMessage("Saving SiegeScenarios");
//						try
//						{
//							Scenarios.saveAll();
//							users.sendStaffMessage("SiegeScenarios saved.");
//						} catch (Exception ex)
//						{
//							ex.printStackTrace();
//							users.sendStaffMessage(ColorOptions.error + "Error while saving SiegeScenarios. Please notify a developer");
//							users.sendStaffMessage(ColorOptions.error + "Failed to save Knights and Kings. Preventing server reload..");
//							users.sendStaffMessage(ColorOptions.error + "Please try to reload again.");
//							return;
//						}
//					}
//				},
//				new Runnable()
//				{
//					public void run()
//					{
//						users.sendStaffMessage("Saving Gates");
//						try
//						{
//							Gates.gates.saveAll();
//							users.sendStaffMessage("Gates saved.");
//						} catch (Exception ex)
//						{
//							ex.printStackTrace();
//							users.sendStaffMessage(ColorOptions.error + "Error while saving Gates. Please notify a developer");
//							users.sendStaffMessage(ColorOptions.error + "Failed to save Knights and Kings. Preventing server reload..");
//							users.sendStaffMessage(ColorOptions.error + "Please try to reload again.");
//							return;
//						}
//					}
//				},
//				new Runnable()
//				{
//					public void run()
//					{
//						users.sendStaffMessage("Clearing leftover entities...");
//						try
//						{
//							clearAfkLeftovers();
//							users.sendStaffMessage("Leftovers cleared.");
//						} catch (Exception ex)
//						{
//							ex.printStackTrace();
//							users.sendStaffMessage(ColorOptions.error + "Error while clearing leftovers. Please notify a developer");
//							users.sendStaffMessage(ColorOptions.error + "Failed to save Knights and Kings. Preventing server reload..");
//							users.sendStaffMessage(ColorOptions.error + "Please try to reload again.");
//							return;
//						}
//					}
//				}
//				).forEach(r -> r.run());
//		
////		users.sendStaffMessage("Saving and stopping treasures...");
////		try
////		{
////			Treasures.stopTreasures();
////			users.sendStaffMessage("Treasures saved.");
////		} catch (Exception ex)
////		{
////			ex.printStackTrace();
////			users.sendStaffMessage(ColorOptions.error + "Error while saving treasures. Please notify a developer");
////			noWarnings = false;
////		}
//		
//		users.sendStaffMessage(ColorOptions.messagesubjects + "Succesfully saved " + getDescription().getName() + " version " + getDescription().getVersion());
//		users.sendStaffMessage(ColorOptions.message + "Commencing regular reload..");
//		Bukkit.getServer().reload();
		boolean noWarnings = true;
		Users.sendStaffMessage(ColorOptions.message + "Saving and reloading " + ColorOptions.messagesubjects + getDescription().getName() + ColorOptions.message + " version " + ColorOptions.messagesubjects + getDescription().getVersion());
		
		Users.sendStaffMessage("Saving user data...");
		try
		{
			saveUsers(null, false);
			Users.sendStaffMessage("User data saved.");
		} catch (Exception ex)
		{
			ex.printStackTrace();
			Users.sendStaffMessage(ColorOptions.error + "Error while saving users. Please notify a developer");
			noWarnings = false;
		}

		Users.sendStaffMessage("Saving resource-blocks...");
		try
		{
			refreshResources(null);
			Users.sendStaffMessage("Resource-blocks saved.");
		} catch (Exception ex)
		{
			ex.printStackTrace();
			Users.sendStaffMessage(ColorOptions.error + "Error while saving resource-blocks. Please notify a developer");
			noWarnings = false;
		}
		
//		users.sendStaffMessage("Saving and stopping treasures...");
//		try
//		{
//			Treasures.stopTreasures();
//			users.sendStaffMessage("Treasures saved.");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			users.sendStaffMessage(ColorOptions.error + "Error while saving treasures. Please notify a developer");
//			noWarnings = false;
//		}
		
		Users.sendStaffMessage("Saving SiegeScenarios");
		try
		{
			Scenarios.saveAll();
			Users.sendStaffMessage("SiegeScenarios saved.");
		} catch (Exception ex)
		{
			ex.printStackTrace();
			Users.sendStaffMessage(ColorOptions.error + "Error while saving SiegeScenarios. Please notify a developer");
			noWarnings = false;
		}
		
		Users.sendStaffMessage("Saving Gates");
		try
		{
			DataManager.Structures.Gates.saveAll();
			Users.sendStaffMessage("Gates saved.");
		} catch (Exception ex)
		{
			ex.printStackTrace();
			Users.sendStaffMessage(ColorOptions.error + "Error while saving Gates. Please notify a developer");
			noWarnings = false;
		}

		Users.sendStaffMessage("Clearing leftover entities...");
		try
		{
			clearAfkLeftovers();
			Users.sendStaffMessage("Leftovers cleared.");
		} catch (Exception ex)
		{
			ex.printStackTrace();
			Users.sendStaffMessage(ColorOptions.error + "Error while clearing leftovers. Please notify a developer");
			noWarnings = false;
		}
		
		Users.sendStaffMessage("Clearing Temp. regions");
		try
		{
			DataManager.Creations.ClearCreations();
			Users.sendStaffMessage("Temporal regions cleared.");
		} catch (Exception ex)
		{
			ex.printStackTrace();
			Users.sendStaffMessage(ColorOptions.error + "Error while clearing Temporal regions. Please notify a developer");
			noWarnings = false;
		}
		
		if (!noWarnings)
		{
			Users.sendStaffMessage(ColorOptions.error + "Failed to save Knights and Kings. Preventing server reload..");
			Users.sendStaffMessage(ColorOptions.error + "Please try to reload again.");
			return;
		}

		new BukkitRunnable()
		{
			public void run()
			{
				Users.sendStaffMessage(ColorOptions.messagesubjects + "Succesfully saved " + getDescription().getName() + " version " + getDescription().getVersion());
				Users.sendStaffMessage(ColorOptions.message + "Commencing regular reload..");
				Bukkit.getServer().dispatchCommand(sender, "reload");
			}
		}.runTaskLaterAsynchronously(this, 2*20);
	}
}
