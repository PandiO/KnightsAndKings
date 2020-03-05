package Menu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import API_methods.WorldGuard;
import Arenas.Arena;
import Assignments.Assignment;
import Assignments.AssignmentTravelRandom;
import Donator.Donator;
import Exceptions.UserNotFoundException;
import Gates.Gate;
import Genders.Gender;
import Handlers.ColorOptions;
import Handlers.EnchantmentGlow;
import Handlers.ErrorHandlers;
import Handlers.Menus;
import Handlers.SoundHandler;
import HideAndSeek.HideAndSeek;
import Houses.House;
import Main.Main;
import Minigames.MGTeam;
import Minigames.Participant;
import Products.Product;
import Products.ProductCategory;
import Products.PropertyProduct;
import Properties.ItemFrameAdd;
import Properties.Property;
import Properties.PropertyCategory;
import Quests.Quest;
import Quests.QuestDeliverPackage;
import Quests.QuestHarvestResource;
import Quests.QuestIntimidateRival;
import Rooms.Room;
import Sieges.Scenarios;
import Sieges.SideObjective;
import Sieges.Siege;
import Sieges.SiegeMember;
import Sieges.Scenario;
import Sieges.SiegeSpawnpoint;
import Sieges.Sieges;
import Skills.PickpocketSkill;
import Skills.Skill;
import Skills.SpecialSkill;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Tutorial.Tutorial;
import Users.User;
import Users.Users;
import Users.offlineUser;
public class Menu 
{
//	int houselist = 0;
	int ownedhouses = 0;
	int teleport = 1;
	int titles = 2;
	int social = 3;
	int profile = 4;
	int asignments = 5;
	int servants = 6;
	int website = 7;
	int exit = 8;
//	int propertylist = 9;
	int ownedproperties = 9;
	int skills = 10;
	int donator = 11;
	int gemshop = 12;
	int financial = 13;
	int knowledge = 14;
	int events = 15;
	int settings = 16;
	int support = 17;
	int theft = 22;
	
	int playermanager = 36;
	int itemmanager = 37;
	int amountrefresh = 38;
	int pricerefresh = 39;
	int gateManager = 40;
	int eventManager = 41;
	
	public List<Integer> duelMenuFree = Arrays.asList(10, 11, 12, 14, 15, 16, 19, 20, 21, 23, 24, 25);

	public ArrayList<Integer> defaultSlots = new ArrayList<Integer>(Arrays.asList(
			9, 18, 27, 36, 45, 10, 19, 28, 37, 46));
	
	public ArrayList<Integer> nobleSlots = new ArrayList<Integer>(Arrays.asList(
			11, 20, 29, 38, 47, 12, 21, 30, 39, 48));
	
	public ArrayList<Integer> royalSlots = new ArrayList<Integer>(Arrays.asList(
			13, 22, 31, 40, 49, 14, 23, 32, 41, 50));
	
	public ArrayList<Integer> dbSlots = new ArrayList<Integer>(Arrays.asList(
			15, 24, 33, 42, 51, 16, 25, 34, 43, 52, 17, 26, 35, 44, 53));
	
	Arena arena = new Arena();
	offlineUser user = new offlineUser();
	Users users = new Users();
	WorldGuard worldguard = new WorldGuard();
	Donator Donator = new Donator();
	Skill skill = new Skill();
	Product product = new Product();
	House house = new House();
	Property property = new Property();
	Town town = new Town();
	Street street = new Street();
	Title title = new Title();
	SpawnPoint spawnpoint = new SpawnPoint();
	PropertyCategory propertycat = new PropertyCategory();
	ProductCategory productcat = new ProductCategory();
	PropertyProduct proproduct = new PropertyProduct();
	SpecialSkill specialskill = new SpecialSkill();
	Room room = new Room();
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
    public void OpenPersonalMenu(User user)
    {
    	main.DBreconnect();
    	UUID uuid = user.getUUID();
    	Integer titleID = user.getTitleID();
    	
    	String playername = user.getUsername();
    	SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    	Integer experience = user.getExperience();
    	String titlename = user.getTitleName();
    	Integer kills = user.getKills();
    	Integer deaths = user.getDeaths();
    	String firstjoin = format.format(user.getJoinDate());
    	Integer coinamount = user.getCoins();
    	Integer gemamount = user.getGems();
    	Integer salary = user.getSalary();
    	Integer income = user.getIncome();
    	Integer strength = user.getStrengthID();
    	Integer speed = user.getSpeedID();
    	Integer health = user.getHealthID();
    	Integer attackspeed = user.getAttackSpeedID();
    	Integer defense = user.getDefenseID();
    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(user.getUsername());
		skullMeta.setDisplayName(ColorOptions.stats + playername);
		List<String> lore = Arrays.asList(new String[]{
			ColorOptions.stats + "Firstjoin: " + ColorOptions.statsresults + firstjoin,
			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename,
			ColorOptions.stats + "Experience: " + ColorOptions.statsresults + experience,
			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + kills,
			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + deaths
		});
		skullMeta.setLore(lore);
		skull.setItemMeta(skullMeta);
		Integer slots = 27;
    	if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
    	{
    		slots = 45;
    	}
    	Inventory pmenu =  Bukkit.createInventory(null, slots, ColorOptions.messageformat + "Personal menu");

    	if (PickpocketSkill.pickpocketed.containsKey(uuid))
    	{
    		if (user.getGenderName().equalsIgnoreCase("male"))
    		{
        		pmenu.setItem(theft, MenuCommand.addpmenu(ChatColor.RED + "" + ChatColor.BOLD + "Theft report", Material.PAPER, ChatColor.GRAY + "Mylord! we received a report", ChatColor.GRAY + "that money has been stolen", ChatColor.GRAY + "from you!"));
    		} else
    		{
        		pmenu.setItem(theft, MenuCommand.addpmenu(ChatColor.RED + "" + ChatColor.BOLD + "Theft report", Material.PAPER, ChatColor.GRAY + "Mylady! we received a report", ChatColor.GRAY + "that money has been stolen", ChatColor.GRAY + "from you!"));
    		}
    		PickpocketSkill.pickpocketed.remove(uuid);
    	}
    	if (user.inOwnerModus())
    	{
    		pmenu.setItem(amountrefresh, MenuCommand.addpmenu(ColorOptions.stats + "Refresh item-amount", Material.IRON_SWORD, ChatColor.GRAY + "Refresh the daily item amount", ChatColor.GRAY + "of all property-products."));
    		pmenu.setItem(itemmanager, MenuCommand.addpmenu(ColorOptions.stats + "ShopItems Manager", Material.NAME_TAG, ChatColor.GRAY + "Click here to add or remove", ChatColor.GRAY + "items which are sold", ChatColor.GRAY + "by properties"));
    		pmenu.setItem(playermanager, MenuCommand.addpmenu(ColorOptions.stats + "Player Manager", Material.SKULL_ITEM, ChatColor.GRAY + "Check all statistics from online players", ChatColor.GRAY + "and edit them"));
    		pmenu.setItem(pricerefresh, MenuCommand.addpmenu(ColorOptions.stats + "Refresh item-price", Material.GOLD_INGOT, ChatColor.GRAY + "Refresh the weekly item price", ChatColor.GRAY + "of all property-products."));
    		pmenu.setItem(gateManager, MenuCommand.addpmenu(ColorOptions.stats + "Gate Manager", Material.FENCE, ColorOptions.message + "Manage the gates' properties"));
    		pmenu.setItem(eventManager, MenuCommand.addpmenu(ColorOptions.stats + "Event Manager", Material.CAKE, ColorOptions.message + "Manage all active and inactive events"));
    	}
    	pmenu.setItem(ownedhouses, MenuCommand.addpmenu(ColorOptions.stats + "Houses", Material.BED, ChatColor.GRAY + "Click here to see all owned houses"));
    	pmenu.setItem(skills, MenuCommand.addpmenu(ColorOptions.stats + "Skills", Material.BOOK, ChatColor.GRAY + "Click here to see your personal Skills", ChatColor.RED + "Strength: " + ColorOptions.statsresults + strength, ChatColor.AQUA + "Speed: " + ColorOptions.statsresults + speed, ChatColor.LIGHT_PURPLE + "Health: " + ColorOptions.statsresults + health, ChatColor.WHITE + "Attack speed: " + ColorOptions.statsresults + attackspeed, ChatColor.YELLOW + "Defense: " + ColorOptions.statsresults + defense));
    	pmenu.setItem(teleport, MenuCommand.addpmenu(ChatColor.GOLD + "Teleport to points on the map", Material.COMPASS, ChatColor.GRAY + "Teleport to important points in the world", ChatColor.RED + "Price will be paid in gems!"));
    	pmenu.setItem(profile, Menus.getSocialProfile(user));
    	pmenu.setItem(donator, this.getDonatorItem(user));
    	pmenu.setItem(social, MenuCommand.addpmenu(ChatColor.YELLOW + "Social", Material.BANNER, ChatColor.GRAY + "Add/remove/manage friends"));
    	pmenu.setItem(settings, MenuCommand.addpmenu(ChatColor.GRAY + "Settings", Material.LEVER, ChatColor.GRAY + "Modify settings in the game"));
    	pmenu.setItem(support, MenuCommand.addpmenu(ChatColor.YELLOW + "Support", Material.REDSTONE_TORCH_ON, ChatColor.GRAY + "Click here for Tutorials, FAQ", ChatColor.GRAY + "And other useful information"));
    	pmenu.setItem(exit, MenuCommand.addpmenu(ChatColor.RED + "Exit", Material.BARRIER, ChatColor.GRAY + "Exit your personal menu"));	
    	pmenu.setItem(ownedproperties, MenuCommand.addpmenu(ColorOptions.stats + "Properties", Material.ANVIL, ChatColor.GRAY + "Click here to see all owned properties"));
    	pmenu.setItem(knowledge, MenuCommand.addpmenu(ColorOptions.stats + "Knowledge", Material.BOOK, ChatColor.GRAY + "See all discovered Knowledge", ColorOptions.stats + "Latest discovered Knowledge:", ChatColor.GREEN + ""));   	
    	pmenu.setItem(asignments, MenuCommand.addpmenu(ColorOptions.stats + "Assignments", Material.EMPTY_MAP, ChatColor.GRAY + "See all your completed", ChatColor.GRAY + "and current assignments"));
    	pmenu.setItem(financial, MenuCommand.addpmenu(ColorOptions.stats + "Financial", Material.GOLD_INGOT, ColorOptions.stats + "Coins: " + ColorOptions.coinStats + ColorOptions.formatCurrency(coinamount), ColorOptions.stats + "Gems: " + ColorOptions.gemStats + ColorOptions.formatCurrency(gemamount), ColorOptions.stats + "Salary: " + ColorOptions.statsresults + ColorOptions.formatCurrency(salary), ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(income)));
    	if (titleID < 18)
    	{
        	pmenu.setItem(titles, MenuCommand.addpmenu(ColorOptions.stats + "Titles", Material.IRON_HELMET, ColorOptions.stats + "Current title: " + titlename, ColorOptions.stats + "Current salary: " + ColorOptions.formatCurrency(salary), "", ColorOptions.statsformat + "Next title: " + ColorOptions.statsresults + title.getTitleName(titleID+1, user.getGenderID()), ColorOptions.statsformat + "Next salary: " + ColorOptions.statsresults + ColorOptions.formatCurrency(title.getSalary(titleID+1)), ColorOptions.statsformat + "Experience for promotion: " + ColorOptions.statsresults + ColorOptions.formatCurrency((title.getExpmin(titleID+1)-experience)), "", ChatColor.GRAY + "Click here to preview all titles"));
    	} else
    	{
        	pmenu.setItem(titles, MenuCommand.addpmenu(ColorOptions.stats + "Titles", Material.IRON_HELMET, ColorOptions.stats + "Current title: " + titlename, ColorOptions.stats + "Current salary: " + ColorOptions.formatCurrency(salary), "", ColorOptions.statsformat + "You have the highest title available", "", ChatColor.GRAY + "Click here to preview all titles"));
    	}
    	pmenu.setItem(website, MenuCommand.addpmenu(ColorOptions.stats + "Website", Material.SIGN, ChatColor.GRAY + "Click here to open our website"));
    	pmenu.setItem(events, MenuCommand.addpmenu(ColorOptions.stats + "Events", Material.CAKE, ChatColor.GRAY + "Click here to view all active events!"));
    	pmenu.setItem(gemshop, MenuCommand.addpmenu(ColorOptions.gemStats + "Gem-shop", Material.DIAMOND, ChatColor.GRAY + "Buy items with gems"));
    	pmenu.setItem(servants, MenuCommand.addpmenu(ColorOptions.stats + "Pets/Servants", Material.EGG, ChatColor.GRAY + "List of all owned", ChatColor.GRAY + "pets and/or servants"));
    	MenuCommand.pmenu.put(uuid, pmenu);
    	
    	if (main.ownermodus.containsKey(uuid))
    	{
        	pmenu = this.fillEmptyMenu(pmenu, true, " ", "");
    	} else
    	{
    		pmenu = this.fillEmptyMenu(pmenu, false, " ", "");
    	}
    	for (Player p : Bukkit.getOnlinePlayers())
    	{
    		if (p.getUniqueId().equals(uuid))
    		{
    			p.openInventory(pmenu);
    		}
    	}
    }
    
    public static ItemStack setSpecialSkill(String skillname) 
    {
	    EnchantmentGlow glow = new EnchantmentGlow(70);
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.addEnchant(glow, 1, true);
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "KK" + ChatColor.DARK_PURPLE + "Special Skill" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "KK");
        if (skillname.equalsIgnoreCase("ninja"))
        {
            itemMeta.setLore(Arrays.asList(new String[]{
            	ColorOptions.Ninja + "Ninja",
            	ChatColor.GRAY + "30% chance to vanish in combat",
            	ChatColor.GRAY + "For 5 seconds"
            }));
        }
        if (skillname.equalsIgnoreCase("assassin"))
        {
        	itemMeta.setLore(Arrays.asList(new String[]{
                	ColorOptions.Assassin + "Assassin",
                	ChatColor.GRAY + "When crouching 4 seconds invisible",
                	ChatColor.GRAY + "Cooldown of 30 seconds"
                }));
        }
        if (skillname.equalsIgnoreCase("pickpocket"))
        {
        	itemMeta.setLore(Arrays.asList(new String[]{
                	ColorOptions.Pickpocket + "Pickpocket",
                	ChatColor.GRAY + "20% chance to steal coins",
                	ChatColor.GRAY + "When you click someone",
                	ChatColor.GRAY + "Cooldown of 15 minutes"
                }));
        }
        if (skillname.equalsIgnoreCase("juggernaut"))
        {
        	itemMeta.setLore(Arrays.asList(new String[]{
                	ColorOptions.Juggernaut + "Juggernaut",
                	ChatColor.GRAY + "No damage from arrows"
                }));
        }
        if (skillname.equalsIgnoreCase("forger"))
        {
        	itemMeta.setLore(Arrays.asList(new String[]{
                	ColorOptions.Forger + "Forger",
                	ChatColor.GRAY + "20% chance to buy 2 items",
                	ChatColor.GRAY + "For the price of 1"
                }));
        }
        if (skillname.equalsIgnoreCase("shotbow"))
        {
        	itemMeta.setLore(Arrays.asList(new String[]{
                	ColorOptions.Shotbow + "Shotbow",
                	ChatColor.GRAY + "50% chance to shoot 2 arrows",
                	ChatColor.GRAY + "Every time you shoot a bow"
                }));
        }
        if (skillname.equalsIgnoreCase("avenger"))
        {
        	itemMeta.setLore(Arrays.asList(new String[]{
                	ColorOptions.Avenger + "Avenger",
                	ChatColor.GRAY + "+50% attack damage",
                	ChatColor.GRAY + "Against your last killer"
                }));
        }
        if (skillname.equalsIgnoreCase("secondspecialskill"))
        {
        	itemMeta.setLore(Arrays.asList(new String[]{
                	ChatColor.GRAY + "Purchase a second",
                	ChatColor.GRAY + "Special skill with /buy"
                }));
        }
        if (skillname.equalsIgnoreCase("none"))
        {
        	
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    
    public void openSkillMenu(User user)
    {
    	UUID uuid = user.getUUID();
    	List<Integer> skillSlots = new ArrayList<Integer>(Arrays.asList(
    			1, 2, 3, 4, 5, 6 ,7, 
    			10, 11, 12, 13, 14 ,15, 16,
    			19, 20, 21, 22, 23, 24, 25,
    			28, 29, 30, 31, 32, 33, 34,
    			37, 38, 39, 40, 41, 42, 43));
    	Integer strengthlevel = user.getStrengthID();
    	Integer speedlevel = user.getSpeedID();
    	Integer healthlevel = user.getHealthID();
    	Integer attackspeedlevel = user.getAttackSpeedID();
    	Integer defenselevel = user.getDefenseID();
    	
		Inventory menu =  Bukkit.createInventory(null, 45, ColorOptions.skillsformat + "Personal skills");
    	menu.setItem(0, product.createItem(ChatColor.RED + "Strength", SkillMenuItems.Strength, false, ColorOptions.skillsinfoachieved + "Increase your chance to get Strength for 5", ColorOptions.skillsinfoachieved + "seconds every time you hit an enemy!", ColorOptions.skillsinfoachieved + "Maximum chance of 42%"));
    	menu.setItem(9, product.createItem(ChatColor.AQUA + "Speed", SkillMenuItems.Speed, false, ColorOptions.skillsinfoachieved + "Increase your walkspeed", ColorOptions.skillsinfoachieved + "Maximum increasement walkspeed is 160%"));
    	menu.setItem(18, product.createItem(ChatColor.LIGHT_PURPLE + "Health", SkillMenuItems.Health, false, String.valueOf(SkillMenuItemData.HealthInfo1), String.valueOf(SkillMenuItemData.HealthInfo2)));
    	menu.setItem(27, product.createItem(ChatColor.WHITE + "AttackSpeed", SkillMenuItems.AttackSpeed, false, String.valueOf(SkillMenuItemData.AttackSpeedInfo1), String.valueOf(SkillMenuItemData.AttackSpeedInfo2), String.valueOf(SkillMenuItemData.AttackSpeedInfo3)));
    	menu.setItem(36, product.createItem(ChatColor.YELLOW + "Defense", SkillMenuItems.Defense, false, String.valueOf(SkillMenuItemData.DefenseInfo1), String.valueOf(SkillMenuItemData.DefenseInfo2)));
    	menu.setItem(8, product.createItem(ChatColor.RED + "Back", SkillMenuItems.back, false, SkillMenuItemData.backinfo));
		SkillMenuClick.skillmenus.put(uuid, menu);
    	menu.setItem(26, SkillMenuClick.SkillPoints(false, user.getSkillPoints(false), user.getSkillPoints(true)));
  	  	menu.setItem(17, SkillMenuClick.SkillPoints(true, user.getSkillPoints(false), user.getSkillPoints(true)));
  	  	if (user.getSpecialSkillID() != 0)
  	  	{
  	  		menu.setItem(35, setSpecialSkill("secondspecialskill"));
  	  		menu.setItem(44, setSpecialSkill(this.specialskill.getName(user.getSpecialSkillID())));
  	  	} else
  	  	{
  	  		menu.setItem(44, setSpecialSkill("none"));
  	  	}
    	
    	for (Integer slot : skillSlots)
    	{
    		if (slot >= 1 && slot <= 7)
    		{
    			Integer level = 1;
    			boolean achieved = false;
    			String price = null;
    			List<String> lore = new ArrayList<String>(Arrays.asList(SkillMenuItemData.StrengthLore1));
    			
    			switch(slot)
    			{
    			case 2: level = 2;
    			break;
    			case 3: level = 3;
    			break;
    			case 4: level = 4;
    			break;
    			case 5: level = 5;
    			break;
    			case 6: level = 6;
    			break;
    			case 7: level = 7;
    			break;
    			}
    			
    			if (strengthlevel >= level)
    			{
    				achieved = true;
    				price = SkillMenuItemData.achieved;
    			}
    			if (level == 7)
    			{
    				lore.clear();
    				lore.addAll(SkillMenuItemData.StrengthLevel7);
    				if (price != null)
    				{
        				lore.add(price);
    				}
    		    	menu.setItem(slot, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + level, lore));
    			} else
    			{
        			menu.setItem(slot, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + level, achieved, lore));
    			}
    		} else if (slot >= 10 && slot <= 16)
    		{
    			Integer level = 1;
    			boolean achieved = false;
    			String price = null;
    			List<String> lore = new ArrayList<String>(Arrays.asList(SkillMenuItemData.SpeedLore1));
    			
    			switch(slot)
    			{
    			case 11: level = 2;
    			break;
    			case 12: level = 3;
    			break;
    			case 13: level = 4;
    			break;
    			case 14: level = 5;
    			break;
    			case 15: level = 6;
    			break;
    			case 16: level = 7;
    			break;
    			}
    			
    			if (speedlevel >= level)
    			{
    				achieved = true;
    				price = SkillMenuItemData.achieved;
    			}
    			if (level == 7)
    			{
    				lore.clear();
    				lore.addAll(SkillMenuItemData.SpeedLevel7);
    				if (price != null)
    				{
        				lore.add(price);
    				}
    				menu.setItem(slot, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + level, lore));
    			} else
    			{
        			menu.setItem(slot, SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + level, achieved, lore));
    			}	
    		} else if (slot >= 19 && slot <= 25)
    		{
    			Integer level = 1;
    			boolean achieved = false;
    			String price = null;
    			List<String> lore = new ArrayList<String>(Arrays.asList(SkillMenuItemData.HealthLore1));
    			
    			switch(slot)
    			{
    			case 20: level = 2;
    			break;
    			case 21: level = 3;
    			break;
    			case 22: level = 4;
    			break;
    			case 23: level = 5;
    			break;
    			case 24: level = 6;
    			break;
    			case 25: level = 7;
    			break;
    			}
    			
    			if (healthlevel >= level)
    			{
    				achieved = true;
    				price = SkillMenuItemData.achieved;
    			}
    			if (level == 7)
    			{
    				lore.clear();
    				lore.addAll(SkillMenuItemData.HealthLevel7);
    				if (price != null)
    				{
        				lore.add(price);
    				}
    				menu.setItem(slot, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + level, lore));
    			} else
    			{
        			menu.setItem(slot, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + level, achieved, lore));
    			}
    		} else if (slot >= 28 && slot <= 34)
    		{
    			Integer level = 1;
    			boolean achieved = false;
    			String price = null;
    			List<String> lore = new ArrayList<String>(Arrays.asList(SkillMenuItemData.AttackSpeedLore1));
    			
    			switch(slot)
    			{
    			case 29: level = 2;
    			break;
    			case 30: level = 3;
    			break;
    			case 31: level = 4;
    			break;
    			case 32: level = 5;
    			break;
    			case 33: level = 6;
    			break;
    			case 34: level = 7;
    			break;
    			}
    			
    			if (attackspeedlevel >= level)
    			{
    				achieved = true;
    				price = SkillMenuItemData.achieved;
    			}
    			if (level == 7)
    			{
    				lore.clear();
    				lore.addAll(SkillMenuItemData.AttackSpeedLevel7);
    				if (price != null)
    				{
        				lore.add(price);
    				}
    				menu.setItem(slot, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "AttackSpeed upgrade " + ChatColor.DARK_PURPLE + level, lore));
    			} else
    			{
        			menu.setItem(slot, SkillMenuClick.createClayItem(ChatColor.WHITE + "AttackSpeed upgrade " + level, achieved, lore));
    			}
    		} else if (slot >= 37 && slot <= 43)
    		{
    			Integer level = 1;
    			boolean achieved = false;
    			String price = null;
    			List<String> lore = new ArrayList<String>(Arrays.asList(SkillMenuItemData.DefenseLore1));
    			
    			switch(slot)
    			{
    			case 38: level = 2;
    			break;
    			case 39: level = 3;
    			break;
    			case 40: level = 4;
    			break;
    			case 41: level = 5;
    			break;
    			case 42: level = 6;
    			break;
    			case 43: level = 7;
    			break;
    			}
    			
    			if (defenselevel >= level)
    			{
    				achieved = true;
    				price = SkillMenuItemData.achieved;
    			}
    			if (level == 7)
    			{
    				lore.clear();
    				lore.addAll(SkillMenuItemData.DefenseLevel7);
    				if (price != null)
    				{
        				lore.add(price);
    				}
    				menu.setItem(slot, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + level, lore));
    			} else
    			{
        			menu.setItem(slot, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + level, achieved, lore));
    			}
    		}
    	}
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
    	user.getPlayer().openInventory(menu);
//    	skill.setSkill(strengthlevel, speedlevel, healthlevel, attackspeedlevel, defenselevel, uuid);
    }
    
    //Opens the list with available houses in the world
	Map<UUID, Integer> slots = new HashMap<UUID, Integer>();
	public void openHouselist(User user, Integer pagenumber, Integer townID)
	{
		List<Integer> houselist = new ArrayList<Integer>();
		List<Integer> list = house.getHouseIDList(townID);
		if (list.size() > 45)
		{
			Integer startingindex = 0;
			switch(pagenumber)
			{
			case 2: startingindex = 46;
			break;
			case 3: startingindex = 91;
			break;
			case 4: startingindex = 136;
			break;
			case 5: startingindex = 181;
			break;
			case 6: startingindex = 226;
			break;
			case 7: startingindex = 271;
			break;
			case 8: startingindex = 316;
			break;
			case 9: startingindex = 361;
			break;
			case 10: startingindex = 406;
			break;
			default: startingindex = 0;
			break;
			}
			
			for (int i = startingindex; i < startingindex+45; i++)
			{
				houselist.add(list.get(i));
			}
		} else
		{
			houselist.addAll(list);
		}
		Inventory menu =  Bukkit.createInventory(null, main.getMenuSize(houselist.size()), ColorOptions.messagesubjects + "House list");

		Integer houseamount = house.getHouseIDList(townID).size();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Integer houseowned = user.getHouseAmount(false);
		String filterTownName = "All";
		boolean activeFilter = false;
		if (townID != null)
		{
			filterTownName = town.getTownName(townID);
			activeFilter = true;
		}
		
		menu.setItem(0, Menus.getFinancial(user));
    	menu.setItem(1, MenuClick.addmenulist(ColorOptions.stats + "Previous page", Material.ARROW, ChatColor.GRAY + "Click here to go to the previous page of houses", "", ColorOptions.message + "Current page: " + pagenumber));
		menu.setItem(2, this.product.createItem(ColorOptions.message + "Town: " + ColorOptions.statsresults + filterTownName, new ItemStack(((townID != null) ? Material.WATER_BUCKET : Material.BUCKET), 1), activeFilter, ColorOptions.message + "Filter houses for a town only", ColorOptions.message + "Click to change the active town", ColorOptions.message + "Shift-Click to reset"));
//    	menu.setItem(2, MenuClick.addmenulist(ColorOptions.stats + "Town: " + ColorOptions.statsresults + filterTownName, Material.BUCKET, ColorOptions.message + "Filter houses for a town only", ColorOptions.message + "Click to change the active town", ColorOptions.message + "Shift-Click to reset"));
    	menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Houselist", Material.WORKBENCH, ChatColor.GRAY + "List of", ChatColor.GRAY + "all houses in the world", "", ColorOptions.stats + "Current owned houses: " + ColorOptions.statsresults + houseowned));
    	menu.setItem(7, MenuClick.addmenulist(ColorOptions.stats + "Next page", Material.ARROW, ChatColor.GRAY + "Click here to go to the next page of houses", "", ColorOptions.message + "Current page: " + pagenumber));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
    	
    	Integer slot = 9;
    	for (Integer houseID : houselist)
    	{
    		if (house.getHouseName(houseID) != null)
			{
				String houseName = house.getHouseName(houseID);
				Integer price = house.getHousePrice(houseID);
				Integer streetID = house.getStreetID(houseID);
				String streetName = street.getStreetName(streetID);
				String townName = town.getTownName(street.getTownID(streetID));
				Integer streetnumber = house.getHouseNumber(houseID);
				Integer ownerID = house.getHouseOwnerID(houseID);
				
				ItemStack item = this.product.createItem(
						ColorOptions.statsformat + "House: " + ColorOptions.statsresults + houseName,
						new ItemStack(Material.BED, 1),
						false);
				
				List<String> Description = new ArrayList<String>(Arrays.asList(
						ColorOptions.stats + "Street: " + ColorOptions.statsresults + streetName, 
	    				ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
	    				ColorOptions.stats + "Town: " + ColorOptions.statsresults + townName, 
	    				ColorOptions.stats + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price),
	    				""
						));
				
				if (ownerID == 0)
				{
					if (price <= coinamount)
					{
						Description.addAll(Arrays.asList(
								ColorOptions.messageachievement + "Available!",
								ColorOptions.messageachievement + "Click here to buy!"
								));
					} else
					{
						Description.addAll(Arrays.asList(
								ColorOptions.error + "Unavailable!",
								ColorOptions.messageachievement + "You need " + ColorOptions.formatCurrency((price-coinamount)) + " more coins"
								));
					}
				} else
				{
					Description.addAll(Arrays.asList(
							ColorOptions.error + "Unavailable!",
							ColorOptions.stats + "Current owner: " + ColorOptions.statsresults + Users.fetchUsernamebyUUID(Users.fetchUUIDbyID(ownerID))
							));
				}
				
				item = product.addItemDescription(item, Description);
				menu.setItem(slot, item);
//				if (house.getHouseOwnerID(houseID) == 0)
//				{
//					if (house.getHousePrice(houseID) <= coinamount)
//					{
//	    	    		menu.setItem(slot, MenuClick.addmenulist(
//	    	    				ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + houseName, 
//	    	    				Material.BED, 
//	    	    				ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
//	    	    				ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
//	    	    				ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
//	    	    				ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price), 
//	    	    				ChatColor.GREEN + "Available!", 
//	    	    				ChatColor.GRAY + "Click here to buy!" ));
//					} else
//					{
//						Integer coins = (price - coinamount);
//	    	    		menu.setItem(slot, MenuClick.addmenulist(
//	    	    				ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + houseName, 
//	    	    				Material.BED, 
//	    	    				ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
//	    	    				ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
//	    	    				ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
//	    	    				ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price), 
//	    	    				ChatColor.RED + "Unavailable! You need " + coins + " more coins"));
//					}
//				} else
//				{
//					Integer userID = house.getHouseOwnerID(houseID);
//    	    		menu.setItem(slot, MenuClick.addmenulist(
//    	    				ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + houseName, 
//    	    				Material.BED, 
//    	    				ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
//    	    				ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
//    	    				ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
//    	    				ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price), 
//    	    				ChatColor.RED + "Unavailable! Owned by someone else",  
//    	    				ColorOptions.statsformat + "Owner: " + ColorOptions.statsresults + users.fetchUsernamebyUUID(users.fetchUUIDbyID(userID))));
//				}
				slot++;
			}
    	}
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
    	user.getPlayer().openInventory(menu);
	}
	
	public void openPropertylist(User user, Integer pagenumber, Integer townID, Integer blinkPropertyID)
	{
		List<Integer> propertylist = new ArrayList<Integer>();
		List<Integer> list = property.getIDList(null, townID);
		if (list.size() > 45)
		{
			Integer startingindex = 0;
			switch(pagenumber)
			{
			case 2: startingindex = 46;
			break;
			case 3: startingindex = 91;
			break;
			case 4: startingindex = 136;
			break;
			case 5: startingindex = 181;
			break;
			case 6: startingindex = 226;
			break;
			case 7: startingindex = 271;
			break;
			case 8: startingindex = 316;
			break;
			case 9: startingindex = 361;
			break;
			case 10: startingindex = 406;
			break;
			default: startingindex = 0;
			break;
			}
			
			for (int i = startingindex; i < startingindex+45; i++)
			{
				propertylist.add(list.get(i));
			}
		} else
		{
			propertylist.addAll(list);
		}
		Inventory menu =  Bukkit.createInventory(null, main.getMenuSize(propertylist.size()), ColorOptions.messagesubjects + "Property list");

		Integer propertyamount = property.getIDList(null, townID).size();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Integer propertyowned = user.getPropertyAmount(false);
		String filterTownName = "All";
		boolean activeFilter = false;
		if (townID != null)
		{
			filterTownName = town.getTownName(townID);
			activeFilter = true;
		}
		
		menu.setItem(0, Menus.getFinancial(user));
		menu.setItem(1, MenuClick.addpropertylist(ColorOptions.stats + "Previous page", Material.ARROW, ChatColor.GRAY + "Click here to see the previous page of properties", "", ColorOptions.message + "Current page: " + pagenumber));
		menu.setItem(2, this.product.createItem(ColorOptions.message + "Town: " + ColorOptions.statsresults + filterTownName, new ItemStack(((townID != null) ? Material.WATER_BUCKET : Material.BUCKET), 1), activeFilter, ColorOptions.message + "Filter properties for a town only", ColorOptions.message + "Click to change the active town", ColorOptions.message + "Shift-Click to reset"));
//		menu.setItem(2, MenuClick.addmenulist(ColorOptions.stats + "Town: " + ColorOptions.statsresults + filterTownName, Material.BUCKET, ColorOptions.message + "Filter properties for a town only", ColorOptions.message + "Click to change the active town", ColorOptions.message + "Shift-Click to reset"));
		menu.setItem(4, MenuClick.addpropertylist(ColorOptions.stats + "Propertylist", Material.WORKBENCH, ChatColor.GRAY + "List of all properties", ChatColor.GRAY + "in the world", "", ColorOptions.stats + "Total properties in " + filterTownName + ": " + ColorOptions.statsresults + propertylist.size(), ColorOptions.stats + "Current owned properties: " + ColorOptions.statsresults + propertyowned));
		menu.setItem(7, MenuClick.addpropertylist(ColorOptions.stats + "Next page", Material.ARROW, ChatColor.GRAY + "Click here to see the next page of properties", "", ColorOptions.message + "Current page: " + pagenumber));
		menu.setItem(8, MenuClick.addpropertylist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
	  	
    	
    	Integer slot = 9;
		for (Integer propertyID : propertylist)
		{
			if (property.getPropertyName(propertyID) != null)
			{
				String propertyName = property.getPropertyName(propertyID);
				Integer streetID = property.getStreetID(propertyID);
				String streetName = street.getStreetName(streetID);
				Integer streetnumber = property.getStreetNumber(propertyID);
				String townName = town.getTownName(street.getTownID(streetID));
				Integer propertyPrice = property.getPropertyPrice(propertyID);
				Integer propertyIncome = property.getIncome(propertyID);
				
				ItemStack item = this.product.createItem(
						ColorOptions.statsformat + "Property: " + ColorOptions.statsresults + propertyName,
						new ItemStack(Material.ANVIL, 1),
						false);
				
				List<String> Description = new ArrayList<String>(Arrays.asList(
						ColorOptions.stats + "Street: " + ColorOptions.statsresults + streetName,
    					ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + streetnumber,
    					ColorOptions.stats + "Town: " + ColorOptions.statsresults + townName,
    					ColorOptions.stats + "Category: " + ColorOptions.statsresults + propertycat.getCategoryName(property.getCategoryID(propertyID)),
    					ColorOptions.stats + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(propertyPrice),
    					ColorOptions.stats + "income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(propertyIncome),
    					""
						));
				
				if (property.getPropertyOwnerID(propertyID) == 0)
				{
					if (propertyPrice <= coinamount)
					{
						Description.addAll(Arrays.asList(
								ColorOptions.messageachievement + "Available!",
								ColorOptions.messageachievement + "Click here to buy"
								));
					} else
					{
						Description.addAll(Arrays.asList(
								ColorOptions.error + "Unavailable!",
								ColorOptions.error + "You need " + ColorOptions.formatCurrency((propertyPrice-coinamount)) + " more coins"
								));
					}
				} else
				{
					Description.addAll(Arrays.asList(
							ColorOptions.error + "Unavailable!",
							ColorOptions.stats + "Current owner: " + ColorOptions.statsresults + Users.fetchUsernamebyUUID(Users.fetchUUIDbyID(property.getPropertyOwnerID(propertyID)))
							));
				}
				
				item = product.addItemDescription(item, Description);
				menu.setItem(slot, item);
//				if (property.getPropertyOwnerID(propertyID) == 0)
//				{
//					if (property.getPropertyPrice(propertyID) <= coinamount)
//					{
//		    			menu.setItem(slot, MenuClick.addpropertylist(
//		    					ColorOptions.statsformat + "Propertyname: " + ColorOptions.statsresults + property.getPropertyName(propertyID),
//		    					Material.ANVIL,
//		    					ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName,
//		    					ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber,
//		    					ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName,
//		    					ColorOptions.statsformat + "Category: " + ColorOptions.statsresults + propertycat.getCategoryName(property.getCategoryID(propertyID)),
//		    					ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + property.getPropertyPrice(propertyID),
//		    					ColorOptions.statsformat + "income: " + ColorOptions.statsresults + property.getIncome(propertyID),
//		    					ChatColor.GREEN + "Available!",
//		    					ChatColor.GRAY + "Click here to buy!" ));
//					} else
//					{
//						Integer coins = (property.getPropertyPrice(propertyID) - coinamount);
//		    			menu.setItem(slot, MenuClick.addpropertylist(
//		    					ColorOptions.statsformat + "Propertyname: " + ColorOptions.statsresults + property.getPropertyName(propertyID),
//		    					Material.ANVIL,
//		    					ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName,
//		    					ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber,
//		    					ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName,
//		    					ColorOptions.statsformat + "Category: " + ColorOptions.statsresults + propertycat.getCategoryName(property.getCategoryID(propertyID)),
//		    					ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + property.getPropertyPrice(propertyID),
//		    					ColorOptions.statsformat + "income: " + ColorOptions.statsresults + property.getIncome(propertyID),
//		    					ChatColor.RED + "Unavailable! You need " + coins + " more coins"));
//					}
//				} else
//				{
//	    			menu.setItem(slot, MenuClick.addpropertylist(
//	    					ColorOptions.statsformat + "Propertyname: " + ColorOptions.statsresults + property.getPropertyName(propertyID), 
//	    					Material.ANVIL, 
//	    					ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
//	    					ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
//	    					ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
//	    					ColorOptions.statsformat + "Category: " + ColorOptions.statsresults + propertycat.getCategoryName(property.getCategoryID(propertyID)), 
//	    					ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(property.getPropertyPrice(propertyID)), 
//	    					ColorOptions.statsformat + "income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(property.getIncome(propertyID)), 
//	    					ChatColor.RED + "Unavailable! Owned by someone else",  
//	    					ColorOptions.statsformat + "Owner: " + ColorOptions.statsresults + this.user.getUserName(this.user.getUUIDbyID(property.getPropertyOwnerID(propertyID)))));
//				}
				if (blinkPropertyID == propertyID)
				{
					setMenuItemBlink(Bukkit.getConsoleSender(), user, menu, slot, 10);
				}
				slot++;
			}
		}
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
    	user.getPlayer().openInventory(menu);
	}
	
    //Opens the list with available houses in the world
	public void openRoomlist(User user, Integer pagenumber, Integer townID)
	{
		List<Integer> roomlist = new ArrayList<Integer>();
		List<Integer> list = room.getRoomIDList(townID);
		if (list.size() > 45)
		{
			Integer startingindex = 0;
			switch(pagenumber)
			{
			case 2: startingindex = 46;
			break;
			case 3: startingindex = 91;
			break;
			case 4: startingindex = 136;
			break;
			case 5: startingindex = 181;
			break;
			case 6: startingindex = 226;
			break;
			case 7: startingindex = 271;
			break;
			case 8: startingindex = 316;
			break;
			case 9: startingindex = 361;
			break;
			case 10: startingindex = 406;
			break;
			default: startingindex = 0;
			break;
			}
			
			for (int i = startingindex; i < startingindex+45; i++)
			{
				roomlist.add(list.get(i));
			}
		} else
		{
			roomlist.addAll(list);
		}
		Inventory menu =  Bukkit.createInventory(null, main.getMenuSize(roomlist.size()), ColorOptions.messagesubjects + "Room list");
		Integer roomamount = room.getRoomIDList(townID).size();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Integer roomowned = user.getRoomAmount(false);
		String filterTownName = "All";
		if (townID != null)
		{
			filterTownName = town.getTownName(townID);
		}
		
		String ownedRoom = ChatColor.GREEN + "You are currently not renting a room";
		
		if (user.getRoomAmount(false) != 0)
		{
			ownedRoom = ChatColor.RED + "You are already renting a room!";
		}
		menu.setItem(0, Menus.getFinancial(user));
    	menu.setItem(1, MenuClick.addmenulist(ColorOptions.stats + "Previous page", Material.ARROW, ChatColor.GRAY + "Click here to go to the previous page of rooms", "", ColorOptions.message + "Current page: " + pagenumber));
		menu.setItem(2, MenuClick.addpropertylist(ColorOptions.stats + "Town: " + ColorOptions.statsresults + filterTownName, Material.BUCKET, ColorOptions.message + "Filter rooms for a town only", ColorOptions.message + "Click to change the active town", ColorOptions.message + "Shift-Click to reset"));
    	menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Room list", Material.WORKBENCH, ChatColor.GRAY + "List of all rooms in the world.", ColorOptions.message + "The renting price will be paid per hour", ColorOptions.message + "that you are online.", "", ColorOptions.error + "You can only rent 1 room!", "", ownedRoom));
    	menu.setItem(7, MenuClick.addmenulist(ColorOptions.stats + "Next page", Material.ARROW, ChatColor.GRAY + "Click here to go to the next page of rooms", "", ColorOptions.message + "Current page: " + pagenumber));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your owned houses/rooms"));
    	
    	Integer slot = 9;
		for (Integer roomID : roomlist)
		{
			Integer roomNumber = room.getRoomNumber(roomID);
			if (roomNumber != null && roomNumber != 0)
			{
				Integer propertyID = room.getPropertyID(roomID);
				String propertyName = property.getPropertyName(propertyID);
				Integer price = room.getPrice(roomID);
				Integer streetID = property.getStreetID(propertyID);
				String streetName = street.getStreetName(streetID);
				String townName = town.getTownName(street.getTownID(streetID));
				Integer streetnumber = property.getStreetNumber(propertyID);
				if (room.getOwnerID(roomID) == 0)
				{
					if (price <= coinamount)
					{
	    	    		menu.setItem(slot, MenuClick.addmenulist(
	    	    				ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, 
	    	    				Material.BED, 
	    	    				ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
	    	    				ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
	    	    				ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
	    	    				ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, 
	    	    				ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price), 
	    	    				ChatColor.GREEN + "Available!", 
	    	    				ChatColor.GRAY + "Click here to rent!" ));
					} else
					{
						Integer coins = (price - coinamount);
	    	    		menu.setItem(slot, MenuClick.addmenulist(
	    	    				ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, 
	    	    				Material.BED, 
	    	    				ColorOptions.statsresults + propertyName, 
	    	    				ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
	    	    				ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
	    	    				ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
	    	    				ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, 
	    	    				ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price), 
	    	    				ChatColor.RED + "Unavailable! You need " + coins + " more coins"));
					}
				} else
				{
					Integer userID = room.getOwnerID(roomID);
    	    		menu.setItem(slot, MenuClick.addmenulist(
    	    				ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, 
    	    				Material.BED, 
    	    				ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
    	    				ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
    	    				ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
    	    				ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, 
    	    				ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price), 
    	    				ChatColor.RED + "Unavailable! Rented by someone else",  
    	    				ColorOptions.statsformat + "Owner: " + ColorOptions.statsresults + this.user.getUserName(this.user.getUUIDbyID(userID))));
				}
				slot++;
			}
		}
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
    	user.getPlayer().openInventory(menu);
	}
//	//Actually creates the list
//	public static int rcounter = 0;
//	public HashMap<UUID, Integer> lastRoomID = new HashMap<UUID, Integer>();
//	public void createroomlist(Inventory menu, UUID uuid, Player player, Integer pagenumber)
//	{
//		Integer roomAmount = room.getRoomIDList().size();
//		Integer coinamount = user.getCoins(uuid);
//		Integer gemamount = user.getGems(uuid);
//		Integer salary = title.getSalary(user.getTitleID(uuid));
//		Integer income = user.getIncome(uuid);
//		String ownedRoom = ChatColor.GREEN + "You are currently not renting a room";
//		
//		if (user.getRoomAmount(uuid) != 0)
//		{
//			ownedRoom = ChatColor.RED + "You are already renting a room!";
//		}
//		
//		menu.setItem(0, Menus.getFinancial(user));
//    	menu.setItem(1, MenuClick.addmenulist(ColorOptions.stats + "Previous page", Material.ARROW, ChatColor.GRAY + "Click here to go to the previous page of rooms"));
//		menu.setItem(2, MenuClick.addmenulist(ColorOptions.stats + "Town: " + ColorOptions.statsresults + "All", Material.BUCKET, ColorOptions.message + "Filter rooms for a town only", ColorOptions.message + "Click to change the active town"));
//    	menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Room list", Material.WORKBENCH, ChatColor.GRAY + "List of all rooms in the world.", ColorOptions.message + "The renting price will be paid per hour", ColorOptions.message + "that you are online.", "", ColorOptions.error + "You can only rent 1 room!", "", ownedRoom));
//    	menu.setItem(7, MenuClick.addmenulist(ColorOptions.stats + "Next page", Material.ARROW, ChatColor.GRAY + "Click here to go to the next page of rooms"));
//    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your owned houses/rooms"));
//    	
//    	if (roomAmount <= 36)
//    	{
//    		for (int roomID = 1; roomID < 37; roomID++)
//			{
//    			Integer roomNumber = room.getRoomNumber(roomID);
//				if (roomNumber != null && roomNumber != 0)
//				{
//					Integer propertyID = room.getPropertyID(roomID);
//					String propertyName = property.getPropertyName(propertyID);
//					Integer price = room.getPrice(roomID);
//					Integer streetID = property.getStreetID(propertyID);
//					String streetName = street.getStreetName(streetID);
//					String townName = town.getTownName(street.getTownID(streetID));
//					Integer streetnumber = property.getStreetNumber(propertyID);
//					if (room.getOwnerID(roomID) == 0)
//					{
//						if (price <= coinamount)
//						{
//		    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.GREEN + "Available!", ChatColor.GRAY + "Click here to rent!" ));
//		    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//						} else
//						{
//							Integer coins = (price - coinamount);
//		    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.RED + "Unavailable! You need " + coins + " more coins"));
//		    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//						}
//					} else
//					{
//						Integer userID = room.getOwnerID(roomID);
//	    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.RED + "Unavailable! Rented by someone else",  ColorOptions.statsformat + "Owner: " + ColorOptions.statsresults + user.getUserName(user.getUUIDbyID(userID))));
//	    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//					}
//				}
//			}
//    	} else
//    	{
//    		if (pagenumber <= (room.getRoomIDList().size() / 36) +1)
//    		{
//    			if (pagenumber == 1)
//        		{
//        			for (int roomID = 1; roomID < 37; roomID++)
//        			{
//        				Integer roomNumber = room.getRoomNumber(roomID);
//        				if (roomNumber != null && roomNumber != 0)
//        				{
//        					Integer propertyID = room.getPropertyID(roomID);
//        					String propertyName = property.getPropertyName(propertyID);
//        					Integer price = room.getPrice(roomID);
//        					Integer streetID = property.getStreetID(propertyID);
//        					String streetName = street.getStreetName(streetID);
//        					String townName = town.getTownName(street.getTownID(streetID));
//        					Integer streetnumber = property.getStreetNumber(propertyID);
//        					if (room.getOwnerID(roomID) == 0)
//        					{
//        						if (price <= coinamount)
//        						{
//        		    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.GREEN + "Available!", ChatColor.GRAY + "Click here to rent!" ));
//        		    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//        						} else
//        						{
//        							Integer coins = (price - coinamount);
//        		    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.RED + "Unavailable! You need " + coins + " more coins"));
//        		    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//        						}
//        					} else
//        					{
//        						Integer userID = room.getOwnerID(roomID);
//        	    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.RED + "Unavailable! Rented by someone else",  ColorOptions.statsformat + "Owner: " + ColorOptions.statsresults + user.getUserName(user.getUUIDbyID(userID))));
//        	    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//        					}
//        				}
//        			}
//        		} else
//        		if (pagenumber == 2)
//        		{
//        			for (int roomID = 37; roomID < 73; roomID++)
//        			{
//        				Integer roomNumber = room.getRoomNumber(roomID);
//        				if (roomNumber != null && roomNumber != 0)
//        				{
//        					Integer propertyID = room.getPropertyID(roomID);
//        					String propertyName = property.getPropertyName(propertyID);
//        					Integer price = room.getPrice(roomID);
//        					Integer streetID = property.getStreetID(propertyID);
//        					String streetName = street.getStreetName(streetID);
//        					String townName = town.getTownName(street.getTownID(streetID));
//        					Integer streetnumber = property.getStreetNumber(propertyID);
//        					if (room.getOwnerID(roomID) == 0)
//        					{
//        						if (price <= coinamount)
//        						{
//        		    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.GREEN + "Available!", ChatColor.GRAY + "Click here to rent!" ));
//        		    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//        						} else
//        						{
//        							Integer coins = (price - coinamount);
//        		    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.RED + "Unavailable! You need " + coins + " more coins"));
//        		    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//        						}
//        					} else
//        					{
//        						Integer userID = room.getOwnerID(roomID);
//        	    	    		menu.setItem(roomSlots.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + propertyName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + price, ChatColor.RED + "Unavailable! Rented by someone else",  ColorOptions.statsformat + "Owner: " + ColorOptions.statsresults + user.getUserName(user.getUUIDbyID(userID))));
//        	    	    		roomSlots.put(uuid, roomSlots.get(uuid)+1);
//        					}
//        				}
//        			}
//        		}
//    		} else
//    		{
//    			this.openRoomlist(uuid, player, MenuClick.roomlistpage.get(uuid)-1);
//    		}
//    	}
//    	MenuClick.roomlistmap.put(uuid, menu);
//    	rcounter = 0;
//	}
	
	
	Map<UUID, Integer> ownedhouseslot = new HashMap<UUID, Integer>();
	public Inventory openownedHouses(User user)
	{
		UUID uuid = user.getUUID();
		Integer ownedrooms = user.getRoomAmount(false);
		Integer ownedhouses = user.getHouseAmount(false);
		Integer owned = ownedrooms + ownedhouses;
		ownedhouseslot.put(uuid, Integer.valueOf(9));
		Inventory ownedhousesmenu = Bukkit.createInventory(null, main.getMenuSize(owned), ColorOptions.messagesubjects + "Owned houses/rooms");
		createownedhouses(ownedhousesmenu, user, true);
		MenuClick.ownedhousesmap.put(uuid, ownedhousesmenu);
		user.getPlayer().openInventory(ownedhousesmenu);
		return ownedhousesmenu;
	}
	public void createownedhouses(Inventory ownedhouselist, User user, boolean rooms)
	{
		UUID uuid = user.getUUID();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Integer houseowned = user.getHouseAmount(false);
		Integer maxHouses = user.getHouseAmount(true);
		
		ownedhouselist.setItem(0, Menus.getFinancial(user));
    	if (houseowned >= maxHouses)
    	{
    		ownedhouselist.setItem(1, MenuCommand.addpmenu(ColorOptions.stats + "Buy a new house", Material.BED, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "all houses in the world", ColorOptions.error + "All your house-slots are full!", ColorOptions.error + "Promote to a higher title or", ColorOptions.error + "buy a slot from the gem-shop"));
    	} else
    	{
    		ownedhouselist.setItem(1, MenuCommand.addpmenu(ColorOptions.stats + "Buy a new house", Material.BED, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "all houses in the world"));
    	}
    	if (user.getRoomAmount(false) == 1)
    	{
    		ownedhouselist.setItem(2, MenuCommand.addpmenu(ColorOptions.stats + "Rent a room", Material.BED, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "all rooms in the world", ColorOptions.error + "You can't buy more rooms!"));
    	} else
    	{
    		ownedhouselist.setItem(2, MenuCommand.addpmenu(ColorOptions.stats + "Rent a room", Material.BED, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "all rooms in the world"));
    	}
    	ownedhouselist.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Houselist", Material.WORKBENCH, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "your houses", "", ColorOptions.stats + "Current owned houses: " + ColorOptions.statsresults + houseowned, ColorOptions.error + "Maximum of owned houses: " + maxHouses, ColorOptions.error + "Maximum of owned rooms: 1"));
    	ownedhouselist.setItem(7, MenuClick.addmenulist(ChatColor.GRAY + "Settings", Material.LEVER, ChatColor.GRAY + "Modify settings of houses"));
    	ownedhouselist.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
    	if (rooms)
    	{
        	for (int roomID : room.getRoomIDList(null))
        	{
        		if (room.getOwnerID(roomID) == user.getID())
        		{
        			Integer roomNumber = room.getRoomNumber(roomID);
            		Integer propertyID = room.getPropertyID(roomID);
        			Integer streetID = property.getStreetID(propertyID);
        			String streetName = street.getStreetName(streetID);
        			Integer streetnumber = property.getStreetNumber(propertyID);
        			String townName = town.getTownName(street.getTownID(streetID));
        			
        			ItemStack item = product.createItem(
        					ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber,
        					new ItemStack(Material.BED),
        					true,
        					ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName,
        					ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber,
        					ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName,
        					ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + property.getPropertyName(propertyID),
        					ColorOptions.statsformat + "Rentingprice: " + ColorOptions.statsresults + ColorOptions.formatCurrency(room.getPrice(roomID)),
        					ColorOptions.error + "Inactivity longer than 7 days", 
        					ColorOptions.error + "will remove you as owner",
        					"");
        			
        			if (room.getSpawnPointID(roomID) == user.getSpawnpointID())
        			{
        				item = product.addItemDescription(item, Arrays.asList(ColorOptions.messageachievement + "Current spawnpoint!"));
        			}
        			item = product.addItemDescription(item, Arrays.asList(ColorOptions.error + "Click here to stop renting"));
        			
        			ownedhouselist.setItem(ownedhouseslot.get(uuid), item);
    	    		ownedhouseslot.put(uuid, ownedhouseslot.get(uuid)+1);
        		}
        	}
    	}
    	for (int i : house.getHouseIDList(null))
    	{
			Integer streetID = house.getStreetID(i);
			String streetName = street.getStreetName(streetID);
			Integer streetnumber = house.getHouseNumber(i);
			String townName = town.getTownName(street.getTownID(streetID));
    		if (house.getHouseOwnerID(i) != null)
    		{
        		if (house.getHouseOwnerID(i) == user.getID())
        		{
        			List<String> Description = new ArrayList<String>(Arrays.asList(
        					ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
    	    				ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
    	    				ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
    	    				ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(house.getHousePrice(i))));
        			
        			if (house.getHouseSpawnPoint(i) == user.getSpawnpointID())
        			{
        				Description.add(ChatColor.GREEN + "Current spawnpoint!");
        			}
        			Description.add(ChatColor.GRAY + "Click to sell!");
        			
        			ItemStack item = MenuClick.addmenulist(
    	    				ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + house.getHouseName(i), 
    	    				Material.BED);
        			item = this.product.addItemDescription(item, Description);
        			
    	    		ownedhouselist.setItem(ownedhouseslot.get(uuid), item);
    	    		ownedhouseslot.put(uuid, ownedhouseslot.get(uuid)+1);
        		}
    		}
    	}
    	this.fillEmptyMenu(ownedhouselist, false, " ", "");
    	MenuClick.ownedhousesmap.put(uuid, ownedhouselist);
	}
	
//	public void createownedrooms(Inventory menu, UUID uuid)
//	{
//		Integer coinamount = user.getCoins(uuid);
//		Integer gemamount = user.getGems(uuid);
//		Integer salary = title.getSalary(user.getTitleID(uuid));
//		Integer income = user.getIncome(uuid);
//		Integer houseowned = user.getHouseAmount(uuid);
//		
//		menu.setItem(0, Menus.getFinancial(user));
//		menu.setItem(1, MenuCommand.addpmenu(ColorOptions.stats + "Buy a new house", Material.BED, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "all houses in the world"));
//		menu.setItem(2, MenuCommand.addpmenu(ColorOptions.stats + "Rent a room", Material.BED, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "all rooms in the world"));
//		menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Houselist", Material.WORKBENCH, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "your houses", "", ColorOptions.stats + "Current owned houses: " + ColorOptions.statsresults + houseowned));
//		menu.setItem(7, MenuClick.addmenulist(ChatColor.GRAY + "Settings", Material.LEVER, ChatColor.GRAY + "Modify settings of houses"));
//		menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
//    	for (int roomID : room.getRoomIDList())
//    	{
//    		if (room.getOwnerID(roomID) == user.getUserID(uuid))
//    		{
//    			Integer roomNumber = room.getRoomNumber(roomID);
//        		Integer propertyID = room.getPropertyID(roomID);
//    			Integer streetID = property.getStreetID(propertyID);
//    			String streetName = street.getStreetName(streetID);
//    			Integer streetnumber = property.getStreetNumber(propertyID);
//    			String townName = town.getTownName(street.getTownID(streetID));
//    			
//				if (room.getSpawnPointID(roomID) != 0 && user.getSpawnpointID(uuid) == room.getSpawnPointID(roomID))
//    			{
//					menu.setItem(13, MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + property.getPropertyName(propertyID), ColorOptions.statsformat + "Rentingprice: " + ColorOptions.statsresults + room.getPrice(roomID), ChatColor.GREEN + "Current spawnpoint!", ChatColor.GRAY + "Click to stop renting!" ));
//    			} else
//    			{
//					menu.setItem(13, MenuClick.addmenulist(ColorOptions.statsformat + "Roomnumber: " + ColorOptions.statsresults + roomNumber, Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Tavernname: " + ColorOptions.statsresults + property.getPropertyName(propertyID), ColorOptions.statsformat + "Rentingprice: " + ColorOptions.statsresults + room.getPrice(roomID), ChatColor.GRAY + "Click to stop renting!" ));
//    			}
//				this.fillEmptyMenu(menu, false);
//    		}
//    	}
//    	MenuClick.ownedhousesmap.put(uuid, menu);
//
//	}
	
	Map<UUID, Integer> ownedpropertyslot = new HashMap<UUID, Integer>();
	public void openownedProperty(User user)
	{
		UUID uuid = user.getUUID();
		Player player = user.getPlayer();
		Integer ownedproperty = user.getPropertyAmount(false);
		ownedpropertyslot.put(uuid, Integer.valueOf(9));
		if (ownedproperty == 0)
		{
			Inventory ownedpropertiesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned properties");
			createownedproperties(ownedpropertiesmenu, user);
			MenuClick.ownedpropertiesmap.put(uuid, ownedpropertiesmenu);
			player.openInventory(ownedpropertiesmenu);
		} else if (ownedproperty > 0 && ownedproperty <= 9)
		{
			Inventory ownedpropertiesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned properties");
			createownedproperties(ownedpropertiesmenu, user);
			MenuClick.ownedpropertiesmap.put(uuid, ownedpropertiesmenu);
			player.openInventory(ownedpropertiesmenu);
		} else if (ownedproperty > 9 && ownedproperty <= 18)
		{
			Inventory ownedpropertiesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned properties");
			createownedproperties(ownedpropertiesmenu, user);
			MenuClick.ownedpropertiesmap.put(uuid, ownedpropertiesmenu);
			player.openInventory(ownedpropertiesmenu);
		} else if (ownedproperty > 18 && ownedproperty <= 27)
		{
			Inventory ownedpropertiesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned properties");
			createownedproperties(ownedpropertiesmenu, user);
			MenuClick.ownedpropertiesmap.put(uuid, ownedpropertiesmenu);
			player.openInventory(ownedpropertiesmenu);
		} else if (ownedproperty > 27 && ownedproperty <= 36)
		{
			Inventory ownedpropertiesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned properties");
			createownedproperties(ownedpropertiesmenu, user);
			MenuClick.ownedpropertiesmap.put(uuid, ownedpropertiesmenu);
			player.openInventory(ownedpropertiesmenu);
		}
	}
	public void createownedproperties(Inventory ownedpropertieslist, User user)
	{
		UUID uuid = user.getUUID();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Integer propertyowned = user.getPropertyAmount(false);
		Integer propertyMax = user.getPropertyAmount(true);
		
		ownedpropertieslist.setItem(0, Menus.getFinancial(user));
    	if (propertyowned >= propertyMax)
    	{
    		ownedpropertieslist.setItem(1, MenuCommand.addpmenu(ColorOptions.stats + "Buy a new property", Material.ANVIL, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "all properties in the world", ColorOptions.error + "All your property-slots are full!", ColorOptions.error + "Promote to a higher title or", ColorOptions.error + "buy a slot from the gem-shop"));
    	} else
    	{
    		ownedpropertieslist.setItem(1, MenuCommand.addpmenu(ColorOptions.stats + "Buy a new property", Material.ANVIL, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "all properties in the world"));
    	}
		ownedpropertieslist.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Propertylist", Material.WORKBENCH, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "your properties", "", ColorOptions.stats + "Current owned properties: " + ColorOptions.statsresults + propertyowned, ColorOptions.error + "Maximum of owned properties: " + propertyMax));
		ownedpropertieslist.setItem(7, MenuClick.addmenulist(ChatColor.GRAY + "Settings", Material.LEVER, ChatColor.GRAY + "Modify settings of properties"));
		ownedpropertieslist.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
    	for (int i : property.getIDList(null, null))
    	{
			Integer streetID = property.getStreetID(i);
			String streetName = street.getStreetName(streetID);
			Integer streetnumber = property.getStreetNumber(i);
			String townName = town.getTownName(street.getTownID(streetID));
    		if (property.getPropertyOwnerID(i) != null)
    		{
        		if (property.getPropertyOwnerID(i) == user.getID())
        		{
    				ownedpropertieslist.setItem(ownedpropertyslot.get(uuid), MenuClick.addmenulist(
    						ColorOptions.statsformat + "Propertyname: " + ColorOptions.statsresults + property.getPropertyName(i), 
    						Material.ANVIL, 
    						ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, 
    						ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, 
    						ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, 
    						ColorOptions.statsformat + "Category: " + ColorOptions.statsresults + propertycat.getCategoryName(property.getCategoryID(i)), 
    						ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(property.getPropertyPrice(i)), 
    						ColorOptions.statsformat + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(property.getIncome(i)), 
    						ChatColor.GRAY + "Click to view" ));
    	    		ownedpropertyslot.put(uuid, ownedpropertyslot.get(uuid)+1);
        		}
    		}
    	}
		ownedpropertieslist = this.fillEmptyMenu(ownedpropertieslist, false, " ", null);

    	MenuClick.ownedpropertiesmap.put(uuid, ownedpropertieslist);
	}
	
	Map<UUID, Integer> pmmenuslot = new HashMap<UUID, Integer>();
    public void openPlayerManager(User user)
    {
    	Integer amount = Bukkit.getServer().getOnlinePlayers().size();
    	UUID uuid = user.getUUID();
    	Player player = user.getPlayer();
    	pmmenuslot.put(uuid, Integer.valueOf(9));
    	if (amount < 9)
    	{
    		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Online-players Manager");
    		createPlayerManager(menu, user);
	    	player.openInventory(menu);
    	} else if (amount > 9 && amount < 18)
    	{
    		Inventory menu = Bukkit.createInventory(null, 9*3, ColorOptions.stats + "Online-players Manager");
    		createPlayerManager(menu, user);
	    	player.openInventory(menu);
    	} else if (amount > 18 && amount < 27)
    	{
    		Inventory menu = Bukkit.createInventory(null, 9*4, ColorOptions.stats + "Online-players Manager");
    		createPlayerManager(menu, user);
	    	player.openInventory(menu);
    	} else if (amount > 27 && amount < 36)
    	{
    		Inventory menu = Bukkit.createInventory(null, 9*5, ColorOptions.stats + "Online-players Manager");
    		createPlayerManager(menu, user);
	    	player.openInventory(menu);
    	} else if (amount > 36 && amount < 45)
    	{
    		Inventory menu = Bukkit.createInventory(null, 9*5, ColorOptions.stats + "Online-players Manager");
    		createPlayerManager(menu, user);
	    	player.openInventory(menu);
    	} else
    	{
    		
    	}
    }
    
    public void createPlayerManager(Inventory menu, User user)
    {
    	Integer staffamount = 0;
    	Integer owneramount = 0;
    	Integer ownermodus = 0;
    	Integer opamount = 0;
    	
    	ArrayList<String> staffname = new ArrayList<String>();
    	ArrayList<String> ownername = new ArrayList<String>();
    	ArrayList<String> ownermodusname = new ArrayList<String>();
    	ArrayList<String> opname = new ArrayList<String>();
    	
    	for (Player players : Bukkit.getOnlinePlayers())
    	{
    		String name = players.getName();
    		if (players.hasPermission("k&k.staff"))
    		{
    			staffamount = staffamount+1;
    			staffname.add(name);
    		}
    		if (players.hasPermission("k&k.owner"))
    		{
    			owneramount = owneramount+1;
    			ownername.add(name);
    		}
    		if (main.ownermodus.containsKey(players.getUniqueId()) && main.ownermodus.get(players.getUniqueId()) == true)
    		{
    			ownermodus = ownermodus+1;
    			ownermodusname.add(name);
    		}
    		if (players.isOp())
    		{
    			opamount = opamount+1;
    			opname.add(name);
    		}
    		
    		
    	}
    	
    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(user.getUsername());
		skullMeta.setDisplayName(ColorOptions.statsformat + "Current online crewmembers");
		List<String> lore = Arrays.asList(new String[]{
			ColorOptions.stats + "Currently online: " + ColorOptions.statsresults + Bukkit.getOnlinePlayers().size(),
			ColorOptions.stats + "Staff (" + ColorOptions.statsresults + staffamount + ColorOptions.stats + "): " + ColorOptions.statsresults + staffname,
			ColorOptions.stats + "Owner (" + ColorOptions.statsresults + owneramount + ColorOptions.stats + "): " + ColorOptions.statsresults + ownername,
			ColorOptions.stats + "Ownermodus (" + ColorOptions.statsresults + ownermodus + ColorOptions.stats + "): " + ColorOptions.statsresults + ownermodusname,
			ColorOptions.stats + "Op (" + ColorOptions.statsresults + opamount + ColorOptions.stats + "): " + ColorOptions.statsresults + opname,
		});
		skullMeta.setLore(lore);
		skull.setItemMeta(skullMeta);
    	UUID uuid = user.getUUID();
    	
    	menu.setItem(0, skull);
    	menu.setItem(8, MenuCommand.addpmenu(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Exit to your personal menu"));
    	for (Player p : Bukkit.getOnlinePlayers())
    	{
    		UUID pu = p.getUniqueId();    		
        	ItemStack pskull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
    		SkullMeta pskullMeta = (SkullMeta) pskull.getItemMeta(); 
    		pskullMeta.setOwner(p.getName());
    		pskullMeta.setDisplayName(ColorOptions.statsresults + p.getName() + "'s" + ColorOptions.statsformat + " information");
    		List<String> plore = Arrays.asList(new String[]{
    			ColorOptions.stats + "Firstjoin: " + ColorOptions.statsresults + this.user.getJoinDate(pu),
    			ColorOptions.stats + "Title: " + ColorOptions.statsresults + title.getTitleName(this.user.getTitleID(pu), this.user.getGenderID(pu)),
    			ColorOptions.stats + "Experience: " + ColorOptions.statsresults + this.user.getExperience(pu),
    			ColorOptions.stats + "Coins: " + ColorOptions.statsresults + this.user.getCoins(pu),
    			ColorOptions.stats + "Gems: " + ColorOptions.statsresults + this.user.getGems(pu),
    			ColorOptions.stats + "Houses: " + ColorOptions.statsresults + this.user.getHouseAmount(pu),
    			ColorOptions.stats + "Properties: " + ColorOptions.statsresults + this.user.getPropertyAmount(pu),
    			ColorOptions.stats + "Special skill: " + ColorOptions.statsresults + this.user.getSpecialSkillName(pu),
    			ColorOptions.stats + "Friends(" + ColorOptions.statsresults + this.user.getFriendAmount(pu) + ColorOptions.stats + "): " + ColorOptions.statsresults + this.user.getFriendNames(pu),
    			ChatColor.GRAY + "Click to edit statistics"
    		});
    		pskullMeta.setLore(plore);
    		pskull.setItemMeta(pskullMeta);
    		menu.setItem(pmmenuslot.get(uuid), pskull);
    		pmmenuslot.put(uuid, pmmenuslot.get(uuid)+1);
    	}
		menu = this.fillEmptyMenu(menu, false, " ", null);
    	
    }
    
    public void openShopItemsManager(User user)
    {
    	//Integer siAmount = this.getShopItem_Sword_Amount();
    	Integer weaponryamount = 0;
    	Integer armoryamount = 0;
    	Integer archeryamount = 0;
    	Integer jeweleryamount = 0;
    	Integer fisheryamount = 0;
    	Integer butcheryamount = 0;
    	Integer bakeryamount = 0;
    	Integer groceryamount = 0;
    	Integer furnitureamount = 0;
    	Integer witcheryamount = 0;
    	Integer warehouseamount = 0;
    	Integer resourceamount = 0;
    	UUID uuid = user.getUUID();
    	
    	for (Integer propertyID : property.getIDList(null, null))
    	{
    		Integer categoryID = property.getCategoryID(propertyID);
    		String catName = this.propertycat.getCategoryName(categoryID);
    		if (catName.equalsIgnoreCase("weaponry"))
    		{
    			weaponryamount = weaponryamount +1;
    		}
    		if (catName.equalsIgnoreCase("armory"))
    		{
    			armoryamount = armoryamount +1;
    		}
    		if (catName.equalsIgnoreCase("archery"))
    		{
    			archeryamount++;
    		}
    		if (catName.equalsIgnoreCase("jewelery"))
    		{
    			jeweleryamount++;
    		}
    		if (catName.equalsIgnoreCase("fishery"))
    		{
    			fisheryamount = fisheryamount +1;
    		}
    		if (catName.equalsIgnoreCase("butchery"))
    		{
    			butcheryamount = butcheryamount +1;
    		}
    		if (catName.equalsIgnoreCase("bakery"))
    		{
    			bakeryamount = bakeryamount +1;
    		}
    		if (catName.equalsIgnoreCase("grocery"))
    		{
    			groceryamount = groceryamount +1;
    		}
    		if (catName.equalsIgnoreCase("furnitury"))
    		{
    			furnitureamount = furnitureamount +1;
    		}
    		if (catName.equalsIgnoreCase("witchery"))
    		{
    			witcheryamount++;
    		}
    		if (catName.equalsIgnoreCase("warehouse"))
    		{
    			warehouseamount++;
    		}
    		if (catName.equalsIgnoreCase("resources"))
    		{
    			resourceamount++;
    		}
    	}
		Inventory menu = Bukkit.createInventory(null, 9 *3, ColorOptions.stats + "ShopItems Manager");
    	menu.setItem(0, MenuCommand.addpmenu(ColorOptions.statsformat + "Property stats", Material.WORKBENCH, 
    			ColorOptions.stats + "Weaponry amount: " + ColorOptions.statsresults + weaponryamount, 
    			ColorOptions.stats + "Armory amount: " + ColorOptions.statsresults + armoryamount, 
    			ColorOptions.stats + "Archery amount: " + ColorOptions.statsresults + archeryamount, 
    			ColorOptions.stats + "Jewelery amount: " + ColorOptions.statsresults + jeweleryamount, 
    			ColorOptions.stats + "Fishery amount: " + ColorOptions.statsresults + fisheryamount, 
    			ColorOptions.stats + "Butchery amount: " + ColorOptions.statsresults + butcheryamount, 
    			ColorOptions.stats + "Bakery amount: " + ColorOptions.statsresults + bakeryamount, 
    			ColorOptions.stats + "Grocery amount: " + ColorOptions.statsresults + groceryamount, 
    			ColorOptions.stats + "Furnitury amount: " + ColorOptions.statsresults + furnitureamount, 
    			ColorOptions.stats + "Witchery amount: " + ColorOptions.statsresults + witcheryamount, 
    			ColorOptions.stats + "Warehouse amount: " + ColorOptions.statsresults + warehouseamount, 
    			ColorOptions.stats + "Resoures amount: " + ColorOptions.statsresults + resourceamount,
    			ChatColor.GRAY + "These are the total of the current world"));
    	menu.setItem(8, MenuCommand.addpmenu(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Exit to your personal menu"));
    	menu.setItem(9, MenuCommand.addpmenu(ColorOptions.statsformat + "Swords", product.getShopItemManagerCategoryItem("swords"), ChatColor.GRAY + "Click here to manage all swords"));
    	menu.setItem(10, MenuCommand.addpmenu(ColorOptions.statsformat + "Armor", product.getShopItemManagerCategoryItem("armor"), ChatColor.GRAY + "Click here to manage all armor"));
    	menu.setItem(11, MenuCommand.addpmenu(ColorOptions.statsformat + "Bows", product.getShopItemManagerCategoryItem("bows"), ChatColor.GRAY + "Click here to manage all bows"));
    	menu.setItem(12, MenuCommand.addpmenu(ColorOptions.statsformat + "Jewelery", product.getShopItemManagerCategoryItem("jewelery"), ChatColor.GRAY + "Click here to manage all jewelery-items"));
    	menu.setItem(13, MenuCommand.addpmenu(ColorOptions.statsformat + "Fish", product.getShopItemManagerCategoryItem("fish"), ChatColor.GRAY + "Click here to manage all Fish-items"));
    	menu.setItem(14, MenuCommand.addpmenu(ColorOptions.statsformat + "Meat", product.getShopItemManagerCategoryItem("meat"), ChatColor.GRAY + "Click here to manage all Meat-items"));
    	menu.setItem(15, MenuCommand.addpmenu(ColorOptions.statsformat + "Baked-goods", product.getShopItemManagerCategoryItem("baked-goods"), ChatColor.GRAY + "Click here to manage all Bread-items"));
    	menu.setItem(16, MenuCommand.addpmenu(ColorOptions.statsformat + "Vegetables", product.getShopItemManagerCategoryItem("vegetables"), ChatColor.GRAY + "Click here to manage all Vegetable-items"));
    	menu.setItem(17, MenuCommand.addpmenu(ColorOptions.statsformat + "Furniture", product.getShopItemManagerCategoryItem("furniture"), ChatColor.GRAY + "Click here to maage all Furniture-items"));
    	menu.setItem(18, MenuCommand.addpmenu(ColorOptions.statsformat + "Magic", product.getShopItemManagerCategoryItem("magic"), ChatColor.GRAY + "Click here to manage all witchcraft-items"));
    	menu.setItem(19, MenuCommand.addpmenu(ColorOptions.statsformat + "Resources", product.getShopItemManagerCategoryItem("resources"), ColorOptions.message + "Click here to manage all resource-items"));
    	menu.setItem(20, MenuCommand.addpmenu(ColorOptions.statsformat + "Tools", product.getShopItemManagerCategoryItem("tools"), ColorOptions.message + "Click here to manage all tool-items"));
    //	for (String s : this.getShopItemNames())
    //	{
    //		String display = this.getShopItem_Sword_Displayname(s);
    //		Integer damage = this.getSwordDamage_ItemList(s);
    //		String lore = this.getShopItem_Sword_Lore(s);
    //		String desc = this.getShopItem_Sword_Description(s);
    //		Material item = this.getShopItem_Sword_Item(s);
    //		menu.setItem(simenuslot.get(uuid), SwordValues.craftsword(display, damage, new ItemStack(item, 1), ColorOptions.stats + "Lore: ", lore, "", ColorOptions.stats + "Description: ", desc));
    //		simenuslot.put(uuid, simenuslot.get(uuid)+1);
    //	}
		menu = this.fillEmptyMenu(menu, false, " ", null);

    	user.getPlayer().openInventory(menu);
    }
    
	Map<UUID, Integer> itemslot = new HashMap<UUID, Integer>();
	public void openPropertyAddItem(Player player, Integer productID)
	{
		Integer proCat = product.getCategoryID(productID, false);
		Integer propertyamount = 0;
		String propertyCategory = null;
		String productCategory = productcat.getCategoryName(proCat);
		
		propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID, false));
    	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
		propertyamount = property.getIDListbyCategory(propertyCategory, null).size();
		Inventory menu = Bukkit.createInventory(null, main.getMenuSize(propertyamount), ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID, false));
		addpropertiesToProductList(menu, player, propertyamount, productID);

		menu = this.fillEmptyMenu(menu, false, " ", null);

		player.openInventory(menu);
//		
//		if (productCategory.equalsIgnoreCase("swords"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("armor"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("bows"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("jewelery"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("meat"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("fish"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("baked-goods"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("vegetables"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("furniture"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
//		if (productcat.getCategoryName(proCat).equalsIgnoreCase("magic"))
//		{
//			propertyCategory = product.getPropertyCategorybyProduct(product.getCategoryID(productID));
//        	itemslot.put(player.getUniqueId(), Integer.valueOf(9));
//			propertyamount = property.getIDListbyCategory(propertyCategory).size();
//			if (propertyamount < 9)
//			{
//        		Inventory menu = Bukkit.createInventory(null, 9 *2, ColorOptions.stats + "Add a " + ColorOptions.statsresults + product.getDisplayName(productID));
//        		addpropertiesToProductList(menu, player, propertyamount, productID);
//        		player.openInventory(menu);
//        		
//			}
//		}
	}
	
	public void addpropertiesToProductList(Inventory menu, Player player, Integer propertyAmount, Integer productID)
	{
		String displayName = product.getDisplayName(productID, false);
		String itemName = product.getProductName(productID, false);
		Integer categoryID = product.getCategoryID(productID, false);
		String propertyCategory = product.getPropertyCategorybyProduct(categoryID);
		ArrayList<Integer> productIDList = product.getIDListbyCategory(this.productcat.getCategoryName(categoryID), false);
		
    	menu.setItem(0, MenuCommand.addpmenu(ColorOptions.statsformat + "Property stats " + ChatColor.BLACK + productID, Material.WORKBENCH, ColorOptions.stats + propertyCategory + " amount: " + ColorOptions.statsresults + propertyAmount, ChatColor.GRAY + "These are the total of the current world"));
    	menu.setItem(1, MenuCommand.addpmenu(ColorOptions.statsformat + "Click here to get this item" + ChatColor.BLACK + productID, Material.CHEST, ColorOptions.message + "Get this item in your inventory", ColorOptions.message + "to add it to an itemframe"));
    	menu.setItem(4, product.createPropertyItem(productID, 1, false, true));
    	menu.setItem(8, MenuCommand.addpmenu(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Exit to ShopItems manager"));
    	
    	for(Integer propertyID : property.getIDListbyCategory(propertyCategory, null))
    	{
    		String streetName = street.getStreetName(property.getStreetID(propertyID));
    		Integer streetNumber = property.getStreetNumber(propertyID);
    		String townName = town.getTownName(street.getTownID(property.getStreetID(propertyID)));
    		ArrayList<String> location = ColorOptions.getLocationString(streetName, streetNumber, townName);
    		if (!proproduct.getProductListbyProperty(propertyID).contains(productID))
			{
    			menu.setItem(itemslot.get(player.getUniqueId()), MenuClick.addpropertylist(ColorOptions.statsformat + "Propertyname: " + ColorOptions.statsresults + property.getPropertyName(propertyID), Material.ANVIL, location.get(0), location.get(1), location.get(2), ColorOptions.stats + "List of items this property sells:", ColorOptions.statsresults + proproduct.getProductNameListbyProperty(propertyID).toString(), ChatColor.GRAY + "Click here to add item to this property"));
    			itemslot.put(player.getUniqueId(), itemslot.get(player.getUniqueId()) +1);
			} else
			{
    			menu.setItem(itemslot.get(player.getUniqueId()), MenuClick.addpropertylist(ColorOptions.statsformat + "Propertyname: " + ColorOptions.statsresults + property.getPropertyName(propertyID), Material.ANVIL, location.get(0), location.get(1), location.get(2), ColorOptions.stats + "List of items this property sells:", ColorOptions.statsresults + proproduct.getProductNameListbyProperty(propertyID).toString(), ChatColor.RED + "Already selling this item!"));
    			itemslot.put(player.getUniqueId(), itemslot.get(player.getUniqueId()) +1);
			}
    	}
	}
	
	public ItemStack getDonatorItem(User user)
	{
		Donator donator = new Donator();
		ItemStack item = null;
		UUID uuid = user.getUUID();
		Integer donatorID = user.getDonatorID();
		
		switch (donatorID)
		{
		case 0:
			item = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
			break;
		case 1:
			item = new ItemStack(Material.GOLD_CHESTPLATE, 1);
			break;
		case 2:
			item = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
			break;
		case 3:
			item = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
			break;
		}
		
		String displayName = donator.getDonatorColorSecondary(donatorID) + "Current rank: " + donator.getDonatorColorPrimary(donatorID) + donator.getDonatorName(donatorID);

		item = product.createItem(displayName, item, true, ChatColor.GRAY + "Coins/Exp/Gem multiplier: " + donator.getDonatorColorPrimary(donatorID) + donator.getDonatorMultiplier(donatorID));
		
		
		return item;
	}
	
	public Integer getMenuStructureID(List<String> lore, String structureCategory, Integer roomNumber)
	{
		Integer ID = null;
		
		String streetName = ChatColor.stripColor(lore.get(0).replaceAll("Street: ", ""));
		Integer streetNumber = Integer.valueOf(ChatColor.stripColor(lore.get(1).replaceAll("Streetnumber: ", "")));
		String townName = ChatColor.stripColor(lore.get(2).replaceAll("Town: ", ""));
		if (town.getTownID(townName) != null)
		{
			Integer townID = town.getTownID(townName);
			if (street.checkStreet(streetName, townID))
			{
				Integer streetID = street.getStreetID(streetName, townID);
				if (structureCategory.equalsIgnoreCase("property"))
				{
					if (property.getPropertyIDbyLocation(streetID, streetNumber) != null)
					{
						ID = property.getPropertyIDbyLocation(streetID, streetNumber);
					}
				} else if (structureCategory.equalsIgnoreCase("house"))
				{
					if (house.getHouseIDbyLocation(streetID, streetNumber) != null)
					{
						ID = house.getHouseIDbyLocation(streetID, streetNumber);
					}
				} else if (structureCategory.equalsIgnoreCase("room"))
				{
					Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
					if (propertyID != null)
					{
						Integer roomID = room.getRoomID(propertyID, roomNumber);
						if (roomID != null)
						{
							ID = roomID;
						}
					}
				}
			}
		}
		return ID;
	}
	
	public Inventory fillEmptyMenu(Inventory menu, boolean ownermodus, String displayName, String... lore)
	{
    	for (int i = 0; i < menu.getSize(); i++)
    	{
    		if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR)
    		{
    			if (i >= 27 && i <= 35 && ownermodus == true)
    			{
        			menu.setItem(i, MenuCommand.addemptypmenu(displayName, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11), lore));
    			} else
    			{
        			menu.setItem(i, MenuCommand.addemptypmenu(displayName, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0), lore));
    			}
    		}
    	}
    	
    	return menu;
	}
	
	public void openTeleportMenu(User user)
	{
		UUID uuid = user.getUUID();
		Integer genderID = user.getGenderID();
		Integer donatorID = user.getDonatorID();
		Integer titleID = user.getTitleID();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = title.getSalary(user.getTitleID());
		Integer income = user.getIncome();
		
		Integer slotamount = 18;
		Integer spawnpointAmount = spawnpoint.getSpawnPointList(false, false, false, false, false, false).size();
		if (spawnpointAmount > 9)
		{
			slotamount = slotamount+9;
			if (spawnpointAmount > 18)
			{
				slotamount = slotamount+9;
				if (spawnpointAmount > 27)
				{
					slotamount = slotamount+9;
					if (spawnpointAmount > 36)
					{
						slotamount = slotamount+9;
					}
				}
			}
		}
		Inventory menu = Bukkit.createInventory(null, slotamount, ColorOptions.stats + "Teleport to spawnpoints");
		
		menu.setItem(0, Menus.getFinancial(user));
		menu.setItem(4, MenuCommand.addpmenu(ColorOptions.stats + "Information", Material.EMPTY_MAP, ColorOptions.statsformat + "Click a spawnpoint to", ColorOptions.statsformat + "teleport to the location!", "", ColorOptions.statsformat + "Teleportation starts after 3 seconds", "", ColorOptions.error + "The prices are paid in gems!"));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
    	
    	Integer slot = 9;
    	for (Integer spawnpointID : spawnpoint.getSpawnPointList(false, false, false, false, false, false))
    	{
			Integer requiredTitleID = spawnpoint.getRequiredTitleID(spawnpointID);
    		String requiredTitleName = title.getTitleName(requiredTitleID, genderID);
    		if (spawnpoint.getRequiredDonatorID(spawnpointID) == 0)
    		{
    			if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
				{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.defaultformat + "Location: " + ColorOptions.defaultsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ChatColor.GREEN + "Available! Click here to teleport"));
				} else
    			if (requiredTitleID <= titleID)
    			{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.defaultformat + "Location: " + ColorOptions.defaultsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ChatColor.GREEN + "Available! Click here to teleport"));
    			} else
    			{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.defaultformat + "Location: " + ColorOptions.defaultsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ColorOptions.error + "Locked! Reach title " + ColorOptions.messagesubjects + requiredTitleName + ColorOptions.error + " to unlock"));
    			}
    		}
    		if (spawnpoint.getRequiredDonatorID(spawnpointID) == 1)
    		{
    			if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
				{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.nobleformat + "Location: " + ColorOptions.noblesubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ChatColor.GREEN + "Available! Click here to teleport"));
				} else
    			if (donatorID < spawnpoint.getRequiredDonatorID(spawnpointID))
    			{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.nobleformat + "Location: " + ColorOptions.noblesubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ColorOptions.error + "Locked!", ColorOptions.error + "Donator-rank " + ColorOptions.noblesubjects + Donator.getDonatorName(spawnpoint.getRequiredDonatorID(spawnpointID)) + ColorOptions.error + " or higher required!"));
    			} else
    			{
        			if (requiredTitleID <= titleID)
        			{
            			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.nobleformat + "Location: " + ColorOptions.noblesubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ChatColor.GREEN + "Available! Click here to teleport"));
        			} else
        			{
            			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.nobleformat + "Location: " + ColorOptions.noblesubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ColorOptions.error + "Locked! Reach title " + ColorOptions.messagesubjects + requiredTitleName + ColorOptions.error + " to unlock"));
        			}
    			}
    		}
    		if (spawnpoint.getRequiredDonatorID(spawnpointID) == 2)
    		{
    			if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
				{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.royalformat + "Location: " + ColorOptions.royalsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ChatColor.GREEN + "Available! Click here to teleport"));
				} else 
    			if (donatorID < spawnpoint.getRequiredDonatorID(spawnpointID))
    			{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.royalformat + "Location: " + ColorOptions.royalsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ColorOptions.error + "Locked!", ColorOptions.error + "Donator-rank " + ColorOptions.royalsubjects + Donator.getDonatorName(spawnpoint.getRequiredDonatorID(spawnpointID)) + ColorOptions.error + " or higher required!"));
    			} else
    			{
        			if (requiredTitleID <= titleID)
        			{
            			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.royalformat + "Location: " + ColorOptions.royalsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ChatColor.GREEN + "Available! Click here to teleport"));
        			} else
        			{
            			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.royalformat + "Location: " + ColorOptions.royalsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ColorOptions.error + "Locked! Reach title " + ColorOptions.messagesubjects + requiredTitleName + ColorOptions.error + " to unlock"));
        			}
    			}
    		}
    		if (spawnpoint.getRequiredDonatorID(spawnpointID) == 3)
    		{
    			if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
				{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.dbformat + "Location: " + ColorOptions.dbsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ChatColor.GREEN + "Available! Click here to teleport"));
				} else
    			if (donatorID < spawnpoint.getRequiredDonatorID(spawnpointID))
    			{
        			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.dbformat + "Location: " + ColorOptions.dbsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ColorOptions.error + "Locked!", ColorOptions.error + "Donator-rank " + ColorOptions.dbsubjects + Donator.getDonatorName(spawnpoint.getRequiredDonatorID(spawnpointID)) + ColorOptions.error + " required!"));
    			} else
    			{
        			if (requiredTitleID <= titleID)
        			{
            			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.dbformat + "Location: " + ColorOptions.dbsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ChatColor.GREEN + "Available! Click here to teleport"));
        			} else
        			{
            			menu.setItem(slot, MenuClick.addmenulist(ColorOptions.dbformat + "Location: " + ColorOptions.dbsubjects + spawnpoint.getSpawnPointName(spawnpointID), Material.COMPASS, ColorOptions.gemStats + "Price: " + spawnpoint.getSpawnPointPrice(spawnpointID), ColorOptions.error + "Locked! Reach title " + ColorOptions.messagesubjects + requiredTitleName + ColorOptions.error + " to unlock"));
        			}
    			}
    		}
    		slot++;
    	}
    	
		menu = this.fillEmptyMenu(menu, false, " ", null);
    	
    	user.getPlayer().openInventory(menu);
	}
	
	public void openDonatorInfo(User user)
	{
		UUID uuid = user.getUUID();
		Integer donatorID = user.getDonatorID();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		boolean defaultUnlocked = true;
		boolean nobleUnlocked = false;
		boolean royalUnlocked = false;
		boolean dbUnlocked = false;
		String defaultStatus = ColorOptions.error + "Locked! You haven't found this item yet!";
		String nobleStatus = ColorOptions.error + "Locked! Purchase " + ColorOptions.noblesubjects + "Noble " + ColorOptions.error + "with /buy!";
		String royalStatus = ColorOptions.error + "Locked! Purchase " + ColorOptions.royalsubjects + "Royal " + ColorOptions.error + "with /buy!";
		String dbStatus = ColorOptions.error + "Locked! Purchase " + ColorOptions.dbsubjects + "Dragon blood " + ColorOptions.error + "with /buy!";
		String noblePrice = ColorOptions.error + "This rank costs $10";
		String royalPrice = ColorOptions.error + "This rank costs $15";
		String dbPrice = ColorOptions.error + "This rank costs $25";
		
		if (donatorID == 1)
		{
			nobleUnlocked = true;
			noblePrice = ColorOptions.messagesubjects + "Purchased! Congratulations!";
			nobleStatus = ColorOptions.error + "Locked! You haven't found this item yet!";
		}
		if (donatorID == 2)
		{
			royalUnlocked = true;
			royalPrice = ColorOptions.messagesubjects + "Purchased! Congratulations!";
			royalStatus = ColorOptions.error + "Locked! You haven't found this item yet!";
		}
		if (donatorID == 3)
		{
			dbUnlocked = true;
			royalPrice = ColorOptions.messagesubjects + "Purchased! Congratulations!";
			dbStatus = ColorOptions.error + "Locked! You haven't found this item yet!";
		}
		Inventory menu = Bukkit.createInventory(null, 9*6, ColorOptions.stats + "Donator-ranks' information");
		
		menu.setItem(0, Menus.getFinancial(user));
		menu.setItem(4, MenuCommand.addpmenu(ColorOptions.stats + "Information", Material.EMPTY_MAP, ColorOptions.statsformat + "See what rank you currently are", ColorOptions.statsformat + "and see all benefits of", ColorOptions.statsformat + "other ranks.", ColorOptions.statsformat + "All benefits from lower ranks are", ColorOptions.statsformat + "unlocked aswell", "", ColorOptions.error + "Ranks can be purchased with /buy!"));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
	
    	menu.setItem(defaultSlots.get(0), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "helmet", new ItemStack(Material.LEATHER_HELMET), defaultUnlocked, ColorOptions.message + "Find the helmet to", ColorOptions.message + "collect " + ColorOptions.gemStats + "50 gems!", "", ColorOptions.stats + "Collect the whole set to earn", ColorOptions.stats + "the donator-rank " + ColorOptions.noblesubjects + "Noble " + ColorOptions.stats + "for free!", "", defaultStatus));
    	menu.setItem(defaultSlots.get(1), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), defaultUnlocked, ColorOptions.message + "Find the chestplate to", ColorOptions.message + "collect " + ColorOptions.gemStats + "50 gems!", "", ColorOptions.stats + "Collect the whole set to earn", ColorOptions.stats + "the donator-rank " + ColorOptions.noblesubjects + "Noble " + ColorOptions.stats + "for free!", "", defaultStatus));
    	menu.setItem(defaultSlots.get(2), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "leggings", new ItemStack(Material.LEATHER_LEGGINGS), defaultUnlocked, ColorOptions.message + "Find the leggings to", ColorOptions.message + "collect " + ColorOptions.gemStats + "50 gems!", "", ColorOptions.stats + "Collect the whole set to earn", ColorOptions.stats + "the donator-rank " + ColorOptions.noblesubjects + "Noble " + ColorOptions.stats + "for free!", "", defaultStatus));
    	menu.setItem(defaultSlots.get(3), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "boots", new ItemStack(Material.LEATHER_BOOTS), defaultUnlocked, ColorOptions.message + "Find the boots to", ColorOptions.message + "collect " + ColorOptions.gemStats + "50 gems!", "", ColorOptions.stats + "Collect the whole set to earn", ColorOptions.stats + "the donator-rank " + ColorOptions.noblesubjects + "Noble " + ColorOptions.stats + "for free!", "", defaultStatus));
    	menu.setItem(defaultSlots.get(4), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "sword", new ItemStack(Material.STONE_SWORD), defaultUnlocked, ColorOptions.message + "Find this sword to", ColorOptions.message + "collect " + ColorOptions.gemStats + "100 gems!", "", ColorOptions.stats + "Collect the whole set to earn", ColorOptions.stats + "the donator-rank " + ColorOptions.noblesubjects + "Noble " + ColorOptions.stats + "for free!", "", defaultStatus));
    	menu.setItem(defaultSlots.get(5), product.createItem(ColorOptions.defaultformat + "Donator-rank: " + ColorOptions.defaultsubjects + "Default", new ItemStack(Material.IRON_BLOCK), defaultUnlocked, ColorOptions.message + "This is the default donator-rank.", ColorOptions.message + "Coin/Exp/Gem multiplier: " + ColorOptions.coinStats + "1"));
    	menu.setItem(defaultSlots.get(6), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "tournaments " + ColorOptions.defaultformat + "in the arena", new ItemStack(Material.BOOK), defaultUnlocked, ColorOptions.message + "Access to all " + ColorOptions.defaultsubjects + "default" + ColorOptions.message + " daily, weekly and monthly", ColorOptions.message + "tournaments."));
    	menu.setItem(defaultSlots.get(7), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "spawnpoints", new ItemStack(Material.BOOK), defaultUnlocked, ColorOptions.message + "Access to " + ColorOptions.defaultsubjects + "default" + ColorOptions.message + " spawnpoints in towns.", ColorOptions.message + "The teleport delay is 5 seconds."));
    	menu.setItem(defaultSlots.get(8), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "bodyguards", new ItemStack(Material.BOOK), defaultUnlocked, ColorOptions.message + "Access to the " + ColorOptions.defaultsubjects + "default" + ColorOptions.message + " bodyguards:", ColorOptions.message + "Knight, Heavy Knight, Archer, Medic."));
    	menu.setItem(defaultSlots.get(9), product.createItem(ColorOptions.defaultformat + "Default " + ColorOptions.defaultsubjects + "items", new ItemStack(Material.BOOK), defaultUnlocked, ColorOptions.message + "Access to the " + ColorOptions.defaultsubjects + "default" + ColorOptions.message + " items", ColorOptions.message + "sold in the shops like", ColorOptions.message + "food, armor, weapons, magic and furniture."));

    	menu.setItem(nobleSlots.get(0), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "helmet", new ItemStack(Material.GOLD_HELMET), nobleUnlocked, ColorOptions.message + "Find the helmet to", ColorOptions.message + "collect " + ColorOptions.gemStats + "80 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", nobleStatus));
    	menu.setItem(nobleSlots.get(1), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "chestplate", new ItemStack(Material.GOLD_CHESTPLATE), nobleUnlocked, ColorOptions.message + "Find the chestplate to", ColorOptions.message + "collect " + ColorOptions.gemStats + "80 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", nobleStatus));
    	menu.setItem(nobleSlots.get(2), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "leggings", new ItemStack(Material.GOLD_LEGGINGS), nobleUnlocked, ColorOptions.message + "Find the leggings to", ColorOptions.message + "collect " + ColorOptions.gemStats + "80 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", nobleStatus));
    	menu.setItem(nobleSlots.get(3), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "boots", new ItemStack(Material.GOLD_BOOTS), nobleUnlocked, ColorOptions.message + "Find the boots to", ColorOptions.message + "collect " + ColorOptions.gemStats + "80 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", nobleStatus));
    	menu.setItem(nobleSlots.get(4), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "sword", new ItemStack(Material.GOLD_SWORD), nobleUnlocked, ColorOptions.message + "Find this sword to", ColorOptions.message + "collect " + ColorOptions.gemStats + "130 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", nobleStatus));
    	menu.setItem(nobleSlots.get(5), product.createItem(ColorOptions.nobleformat + "Donator-rank: " + ColorOptions.noblesubjects + "Noble", new ItemStack(Material.GOLD_BLOCK), nobleUnlocked, ColorOptions.message + "This is the first donator-rank.", ColorOptions.message + "Coin/Exp/Gem multiplier: " + ColorOptions.coinStats + "1.1", ColorOptions.message + "Chatcolors supported!", "", noblePrice));
    	menu.setItem(nobleSlots.get(6), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "tournaments " + ColorOptions.nobleformat + "in the arena", new ItemStack(Material.BOOK), nobleUnlocked, ColorOptions.message + "Access to all " + ColorOptions.noblesubjects + "noble " + "tournaments."));
    	menu.setItem(nobleSlots.get(7), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "spawnpoints", new ItemStack(Material.BOOK), nobleUnlocked, ColorOptions.message + "Access to " + ColorOptions.noblesubjects + "noble" + ColorOptions.message + " spawnpoints", ColorOptions.message + "at important locations.", ColorOptions.message + "The teleport delay is 3 seconds."));
    	menu.setItem(nobleSlots.get(8), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "bodyguards", new ItemStack(Material.BOOK), nobleUnlocked, ColorOptions.message + "Access to " + ColorOptions.noblesubjects + "noble" + ColorOptions.message + " bodyguards:", ColorOptions.message + "Squire, Lumberjack, Doctor."));
    	menu.setItem(nobleSlots.get(9), product.createItem(ColorOptions.nobleformat + "Noble " + ColorOptions.noblesubjects + "items", new ItemStack(Material.BOOK), nobleUnlocked, ColorOptions.message + "Access to " + ColorOptions.noblesubjects + "noble" + ColorOptions.message + " items", ColorOptions.message + "sold in the shops like", ColorOptions.message + "armor, weapons and furniture."));

    	menu.setItem(royalSlots.get(0), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "helmet", new ItemStack(Material.DIAMOND_HELMET), royalUnlocked, ColorOptions.message + "Find the helmet to", ColorOptions.message + "collect " + ColorOptions.gemStats + "120 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", royalStatus));
    	menu.setItem(royalSlots.get(1), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), royalUnlocked, ColorOptions.message + "Find the chestplate to", ColorOptions.message + "collect " + ColorOptions.gemStats + "120 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", royalStatus));
    	menu.setItem(royalSlots.get(2), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "leggings", new ItemStack(Material.DIAMOND_LEGGINGS), royalUnlocked, ColorOptions.message + "Find the leggings to", ColorOptions.message + "collect " + ColorOptions.gemStats + "120 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", royalStatus));
    	menu.setItem(royalSlots.get(3), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "boots", new ItemStack(Material.DIAMOND_BOOTS), royalUnlocked, ColorOptions.message + "Find the boots to", ColorOptions.message + "collect " + ColorOptions.gemStats + "120 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", royalStatus));
    	menu.setItem(royalSlots.get(4), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "sword", new ItemStack(Material.IRON_SWORD), royalUnlocked, ColorOptions.message + "Find this sword to", ColorOptions.message + "collect " + ColorOptions.gemStats + "150 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", royalStatus));
    	menu.setItem(royalSlots.get(5), product.createItem(ColorOptions.royalformat + "Donator-rank: " + ColorOptions.royalsubjects + "Royal", new ItemStack(Material.DIAMOND_BLOCK), royalUnlocked, ColorOptions.message + "This is the second donator-rank.", ColorOptions.message + "Coin/Exp/Gem multiplier: " + ColorOptions.coinStats + "1.2", ColorOptions.message + "Chatcolors supported!", "", royalPrice));
    	menu.setItem(royalSlots.get(6), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "tournaments " + ColorOptions.royalformat + "in the arena", new ItemStack(Material.BOOK), royalUnlocked, ColorOptions.message + "Access to all " + ColorOptions.royalsubjects + "royal " + "tournaments."));
    	menu.setItem(royalSlots.get(7), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "spawnpoints", new ItemStack(Material.BOOK), royalUnlocked, ColorOptions.message + "Access to " + ColorOptions.royalsubjects + "royal" + ColorOptions.message + " spawnpoints", ColorOptions.message + "at important locations.", ColorOptions.message + "The teleport delay is 3 seconds."));
    	menu.setItem(royalSlots.get(8), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "bodyguards", new ItemStack(Material.BOOK), royalUnlocked, ColorOptions.message + "Access to " + ColorOptions.royalsubjects + "royal" + ColorOptions.message + " bodyguards:", ColorOptions.message + "Lavonian, Miner, Tax collector, Samurai."));
    	menu.setItem(royalSlots.get(9), product.createItem(ColorOptions.royalformat + "Royal " + ColorOptions.royalsubjects + "items", new ItemStack(Material.BOOK), royalUnlocked, ColorOptions.message + "Access to " + ColorOptions.royalsubjects + "royal" + ColorOptions.message + " items", ColorOptions.message + "sold in the shops like", ColorOptions.message + "food, armor, weapons and furniture."));
   	
    	menu.setItem(dbSlots.get(0), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "helmet", new ItemStack(Material.CHAINMAIL_HELMET), dbUnlocked, ColorOptions.message + "Find the helmet to", ColorOptions.message + "collect " + ColorOptions.gemStats + "200 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", dbStatus));
    	menu.setItem(dbSlots.get(1), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), dbUnlocked, ColorOptions.message + "Find the chestplate to", ColorOptions.message + "collect " + ColorOptions.gemStats + "200 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", dbStatus));
    	menu.setItem(dbSlots.get(2), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "leggings", new ItemStack(Material.CHAINMAIL_LEGGINGS), dbUnlocked, ColorOptions.message + "Find the leggings to", ColorOptions.message + "collect " + ColorOptions.gemStats + "200 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", dbStatus));
    	menu.setItem(dbSlots.get(3), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "boots", new ItemStack(Material.CHAINMAIL_BOOTS), dbUnlocked, ColorOptions.message + "Find the boots to", ColorOptions.message + "collect " + ColorOptions.gemStats + "200 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", dbStatus));
    	menu.setItem(dbSlots.get(4), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "sword", new ItemStack(Material.DIAMOND_SWORD), dbUnlocked, ColorOptions.message + "Find this sword to", ColorOptions.message + "collect " + ColorOptions.gemStats + "250 gems!", "", ColorOptions.stats + "Collect the whole set to receive", ColorOptions.stats + "a special set of rewards!", "", dbStatus));
    	menu.setItem(dbSlots.get(5), product.createItem(ColorOptions.dbformat + "Donator-rank: " + ColorOptions.dbsubjects + "Dragon blood", new ItemStack(Material.REDSTONE_BLOCK), dbUnlocked, ColorOptions.message + "This is the third donator-rank.", ColorOptions.message + "Coin/Exp/Gem multiplier: " + ColorOptions.coinStats + "1.5", ColorOptions.message + "Chatcolors supported!", "", dbPrice));
    	menu.setItem(dbSlots.get(6), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "tournaments " + ColorOptions.dbformat + "in the arena", new ItemStack(Material.BOOK), dbUnlocked, ColorOptions.message + "Access to all " + ColorOptions.dbsubjects + "dragon blood " + "tournaments."));
    	menu.setItem(dbSlots.get(7), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "spawnpoints", new ItemStack(Material.BOOK), dbUnlocked, ColorOptions.message + "Access to " + ColorOptions.dbsubjects + "dragon blood" + ColorOptions.message + " spawnpoints", ColorOptions.message + "at important locations.", ColorOptions.message + "The teleport delay is 3 seconds."));
    	menu.setItem(dbSlots.get(8), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "bodyguards", new ItemStack(Material.BOOK), dbUnlocked, ColorOptions.message + "Access to " + ColorOptions.dbsubjects + "dragon blood" + ColorOptions.message + " bodyguards:", ColorOptions.message + "Jeweler, Ghost, Paladin, Archangel."));
    	menu.setItem(dbSlots.get(9), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "items", new ItemStack(Material.BOOK), dbUnlocked, ColorOptions.message + "Access to " + ColorOptions.dbsubjects + "dragon blood" + ColorOptions.message + " items", ColorOptions.message + "sold in the shops like", ColorOptions.message + "armor, weapons and magic."));
    	menu.setItem(dbSlots.get(10), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "pets", new ItemStack(Material.BOOK), dbUnlocked, ColorOptions.message + "Access to " + ColorOptions.dbsubjects + "dragon blood " + "pets."));
    	menu.setItem(dbSlots.get(11), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "back-command", new ItemStack(Material.BOOK), dbUnlocked, ColorOptions.message + "Access to the back-command.", ColorOptions.message + "Allows one to teleport back", ColorOptions.message + "to his point-of-death."));
    	menu.setItem(dbSlots.get(12), product.createItem(ColorOptions.dbformat + "Dragon blood " + ColorOptions.dbsubjects + "teleportation", new ItemStack(Material.BOOK), dbUnlocked, ColorOptions.message + "Access to advanced teleportation.", ColorOptions.message + "Send teleport invites to players", ColorOptions.message + "or invite them to teleport", ColorOptions.message + "to you."));
    	menu.setItem(dbSlots.get(13), product.createItem(ColorOptions.dbsubjects + "Mystical island", new ItemStack(Material.BOOK), dbUnlocked, ColorOptions.message + "Access to the " + ColorOptions.dbsubjects + "Mystical island!", ColorOptions.message + "Find more information on the rewards page."));

		menu = this.fillEmptyMenu(menu, false, " ", null);
    	
    	user.getPlayer().openInventory(menu);
	}
	
	public void openTitleInfo(User user)
	{
		Gender gender = new Gender();
		UUID uuid = user.getUUID();
		Integer titleID = user.getTitleID();
		Integer genderID = user.getGenderID();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer expamount = user.getExperience();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		String titlename = title.getTitleName(titleID, user.getGenderID());
		String gendername = user.getGenderName();
		
    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(user.getUsername());
		skullMeta.setDisplayName(ColorOptions.stats + user.getUsername());
		List<String> lore = Arrays.asList(new String[]{
			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename,
			ColorOptions.stats + "Gender: " + ColorOptions.statsresults + gendername,
			ColorOptions.stats + "Experience: " + ColorOptions.statsresults + ColorOptions.formatCurrency(expamount),
			ColorOptions.stats + "Coins: " + ColorOptions.statsresults + ColorOptions.formatCurrency(coinamount),
			ColorOptions.stats + "Gems: " + ColorOptions.statsresults + ColorOptions.formatCurrency(gemamount),
			"",
			ColorOptions.stats + "Sources of income",
			ColorOptions.stats + "Salary: " + ColorOptions.statsresults + ColorOptions.formatCurrency(salary),
			ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(income)
		});
		skullMeta.setLore(lore);
		skull.setItemMeta(skullMeta);
		
		Inventory menu = Bukkit.createInventory(null, 9*4, ColorOptions.stats + "Title-information");

		menu.setItem(0, skull);
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Information about all titles", Material.IRON_HELMET, 
				ColorOptions.message + "There are currently 18 titles", "", 
				ColorOptions.message + "Get experience from:", 
				ColorOptions.message + "-killing bandits or players", 
				ColorOptions.message + "-selling items", 
				ColorOptions.message + "-completing asignments", 
				ColorOptions.message + "-fighting in the arena",
				ColorOptions.message + "-voting(/vote)"));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
    	
    	Integer slot = 9;
    	for (Integer titleIDs : title.getIDList())
    	{
    		boolean active = false;
    		if (titleIDs <= titleID)
    		{
    			active = true;
    		}
    		if (genderID == 1)
    		{
        		menu.setItem(slot, product.createItem(ColorOptions.stats + "Title: " + ColorOptions.statsresults + title.getTitleName(titleIDs, genderID) + "(" + titleIDs + ")", 
        				new ItemStack(Material.IRON_HELMET), 
        				active, 
        				ColorOptions.stats + gender.getGenderName(2) + " name: " + ColorOptions.statsresults + title.getTitleName(titleIDs, 2), 
        				ColorOptions.stats + "Salary: " + ColorOptions.statsresults + title.getSalary(titleIDs), 
        				ColorOptions.stats + "Required experience: " + ColorOptions.statsresults + title.getExpmin(titleIDs), 
        				"", 
        				ColorOptions.statsformat + "Bonusses when promoted to this title:", 
        				ColorOptions.stats + "Coins: " + ColorOptions.statsresults + title.getCoinBonus(titleIDs), 
        				ColorOptions.stats + "Gems: " + ColorOptions.statsresults + title.getGemBonus(titleIDs), 
        				ColorOptions.stats + "Experience: " + ColorOptions.statsresults + title.getExpBonus(titleIDs),
        				"",
        				ColorOptions.unlockformat + "Acquiring this title unlocks:",
    					ColorOptions.unlockformat + "-A skillpoint for " + ColorOptions.unlocksubjects + "Personal Skills"));
    		} else
    		{
        		menu.setItem(slot, product.createItem(ColorOptions.stats + "Title: " + ColorOptions.statsresults + title.getTitleName(titleIDs, genderID) + "(" + titleIDs + ")", 
        				new ItemStack(Material.IRON_HELMET), 
        				active, 
        				ColorOptions.stats + gender.getGenderName(1) + " name: " + ColorOptions.statsresults + title.getTitleName(titleIDs, 1), 
        				ColorOptions.stats + "Salary: " + ColorOptions.statsresults + title.getSalary(titleIDs), 
        				ColorOptions.stats + "Required experience: " + ColorOptions.statsresults + title.getExpmin(titleIDs), 
        				"", 
        				ColorOptions.statsformat + "Bonusses when promoted to this title:", 
        				ColorOptions.stats + "Coins: " + ColorOptions.statsresults + title.getCoinBonus(titleIDs), 
        				ColorOptions.stats + "Gems: " + ColorOptions.statsresults + title.getGemBonus(titleIDs), 
        				ColorOptions.stats + "Experience: " + ColorOptions.statsresults + title.getExpBonus(titleIDs),
        				"",
        				ColorOptions.unlockformat + "Acquiring this title unlocks:",
    					ColorOptions.unlockformat + "-A skillpoint for " + ColorOptions.unlocksubjects + "Personal Skills"));
        	}
    		if (titleIDs == 5)
    		{
    			ItemStack item = product.addItemDescription(menu.getItem(slot), Arrays.asList(
    					ColorOptions.unlockformat + "-A slot for a " + ColorOptions.unlocksubjects + "House",
    					ColorOptions.unlockformat + "-A slot for a " + ColorOptions.unlocksubjects + "Property"
    					));
    			item.setType(Material.DIAMOND_HELMET);
    			menu.setItem(slot, item);
    		}
    		if (titleIDs == 10)
    		{
    			ItemStack item = product.addItemDescription(menu.getItem(slot), Arrays.asList(
    					ColorOptions.unlockformat + "-A slot for a " + ColorOptions.unlocksubjects + "House",
    					ColorOptions.unlockformat + "-A slot for a " + ColorOptions.unlocksubjects + "Property",
    					ColorOptions.unlockformat + "-A SpecialSkill-point for your " + ColorOptions.unlocksubjects + "Special Skill"
    					));
    			item.setType(Material.DIAMOND_HELMET);
    			menu.setItem(slot, item);
    		}
    		if (titleIDs == 12)
    		{
    			ItemStack item = product.addItemDescription(menu.getItem(slot), Arrays.asList(
    					ColorOptions.unlockformat + "-An extra " + ColorOptions.unlocksubjects + "Quest slot"
    					));
    			menu.setItem(slot, item);
    		}
    		if (titleIDs == 15)
    		{
    			ItemStack item = product.addItemDescription(menu.getItem(slot), Arrays.asList(
    					ColorOptions.unlockformat + "-A slot for a " + ColorOptions.unlocksubjects + "House",
    					ColorOptions.unlockformat + "-A slot for a " + ColorOptions.unlocksubjects + "Property",
    					ColorOptions.unlockformat + "-A slot for a " + ColorOptions.unlocksubjects + "Keep",
    					ColorOptions.unlockformat + "-An extra " + ColorOptions.unlocksubjects + "Assignment slot"
    					));
    			item.setType(Material.DIAMOND_HELMET);
    			menu.setItem(slot, item);
    		}
    		slot++;
    		active = false;
    	}
    	menu = this.fillEmptyMenu(menu, false, " ", "");
    	user.getPlayer().openInventory(menu);
	}
	
	public void openFriendsManager(User user)
	{
		UUID uuid = user.getUUID();
		Integer friendAmount = user.getFriendAmount();
		Integer menuSize = main.getMenuSize(friendAmount);
		String titlename = user.getTitleName();
		String gendername = user.getGenderName();
		Integer expamount = user.getExperience();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer houseamount = user.getHouseAmount(false);
		Integer propertyamount = user.getPropertyAmount(false);
		Integer killamount = user.getKills();
		Integer deathamount = user.getDeaths();
		Integer donatorID = user.getDonatorID();
		
		
    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(user.getUsername());
		skullMeta.setDisplayName(ColorOptions.stats + "Social profile");
		List<String> lore = Arrays.asList(new String[]{
			ColorOptions.stats + "Username: " + ColorOptions.statsresults + user.getUsername() + Donator.getDonatorColorSecondary(donatorID) + "(" + Donator.getDonatorColorPrimary(donatorID) + Donator.getDonatorName(donatorID) + Donator.getDonatorColorSecondary(donatorID) + ")",
			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename,
			ColorOptions.stats + "Gender: " + ColorOptions.statsresults + gendername,
			ColorOptions.stats + "Experience: " + ColorOptions.statsresults + expamount,
			ColorOptions.stats + "Coins: " + ColorOptions.statsresults + coinamount,
			ColorOptions.stats + "Gems: " + ColorOptions.statsresults + gemamount,
			ColorOptions.stats + "Houses: " + ColorOptions.statsresults + houseamount,
			ColorOptions.stats + "Properties: " + ColorOptions.statsresults + propertyamount,
			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + killamount,
			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + deathamount,
			
		});
		skullMeta.setLore(lore);
		skull.setItemMeta(skullMeta);
		
		Inventory menu = Bukkit.createInventory(null, menuSize, ColorOptions.stats + "Manage friends");
	
		menu.setItem(0, skull);
		menu.setItem(1, MenuClick.addmenulist(ColorOptions.stats + "Add new friends", Material.BOOK_AND_QUILL, ColorOptions.message + "Click here to add new friends"));
		if (user.getFriendRequestList().size() > 0)
		{
			menu.setItem(2, MenuClick.addmenulist(ColorOptions.statsresults + "You have new friendrequest(s)!", Material.SIGN, ColorOptions.messageachievement + "New requests: " + user.getFriendRequestList().size()));
		}
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Manage your friends", Material.BANNER, 
				ColorOptions.message + "From here you can add, delete or edit", 
				ColorOptions.message + "permissions of your friends."));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
	
    	Integer slot = 9;
    	for (UUID uuids : user.getFriendList())
    	{
    		Integer donatorIDs = this.user.getDonatorID(uuids);
    		String username = this.user.getUserName(uuids);
        	ItemStack targetSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
    		SkullMeta targetSkullMeta = (SkullMeta) targetSkull.getItemMeta(); 
    		targetSkullMeta.setOwner(username);
    		targetSkullMeta.setDisplayName(ColorOptions.stats + username);
    		List<String> info = Arrays.asList(new String[]{
    			ColorOptions.stats + "Donator-rank: " + Donator.getDonatorColorSecondary(donatorIDs) + "(" + Donator.getDonatorColorPrimary(donatorIDs) + Donator.getDonatorName(donatorIDs) + Donator.getDonatorColorSecondary(donatorIDs) + ")",
    			ColorOptions.stats + "Title: " + ColorOptions.statsresults + this.user.getTitleName(uuids),
    			ColorOptions.stats + "Gender: " + ColorOptions.statsresults + this.user.getGenderName(uuids),
    			ColorOptions.stats + "Experience: " + ColorOptions.statsresults + this.user.getExperience(uuids),
    			ColorOptions.stats + "Coins: " + ColorOptions.statsresults + this.user.getCoins(uuids),
    			ColorOptions.stats + "Gems: " + ColorOptions.statsresults + this.user.getGems(uuids),
    			ColorOptions.stats + "Houses: " + ColorOptions.statsresults + this.user.getHouseAmount(uuids),
    			ColorOptions.stats + "Properties: " + ColorOptions.statsresults + this.user.getPropertyAmount(uuids),
    			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + this.user.getKills(uuids),
    			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + this.user.getDeaths(uuids),
    			
    		});
    		targetSkullMeta.setLore(info);
    		targetSkull.setItemMeta(targetSkullMeta);
    		
    		menu.setItem(slot, targetSkull);
    		slot++;
    	}
    	menu = this.fillEmptyMenu(menu, false, " ", "");
    	user.getPlayer().openInventory(menu);
	}
	
	public void openFriendsAdd(User user)
	{
		UUID uuid = user.getUUID();
		Integer menuSize = main.getMenuSize(Bukkit.getOnlinePlayers().size());
		
    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(user.getUsername());
		skullMeta.setDisplayName(ColorOptions.stats + "Social statistics");
		List<String> info = Arrays.asList(new String[]{
			ColorOptions.stats + "Current friends: " + ColorOptions.statsresults + user.getFriendAmount(),
//			ColorOptions.stats + "Invites from you: " + ColorOptions.statsresults + u,
			ColorOptions.stats + "Open requests: " + ColorOptions.statsresults + user.getFriendRequestList().size(),
			
		});
		skullMeta.setLore(info);
		skull.setItemMeta(skullMeta);
		
		Inventory menu = Bukkit.createInventory(null, menuSize, ColorOptions.stats + "Add friends");

		menu.setItem(0, skull);
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Add new friends", Material.BANNER, ColorOptions.message + "All online players are listed here.", ColorOptions.message + "Click a player to sent a friendrequest"));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your friendsmanager"));

    	if (menuSize > 9)
    	{
        	Integer slot = 9;
        	for (Player target : Bukkit.getOnlinePlayers())
        	{
    			User userTarget = null;
    			
    			try
    			{
    				userTarget = Users.getUser(target.getUniqueId());
    			} catch (UserNotFoundException ex)
    			{
    				ErrorHandlers.userNotFoundAction(user.getPlayer(), target, false);
    				return;
    			} catch (Exception ex)
    			{
    				ex.printStackTrace();
    				ErrorHandlers.userNotFoundAction(user.getPlayer(), target, false);
    				return;
    			}
        		if (userTarget.getPlayer() != user.getPlayer())
        		{
            		UUID uuids = user.getUUID();
            		if (!user.isFriends(uuids))
            		{
            			Integer donatorID = userTarget.getDonatorID();
                		String titlename = userTarget.getTitleName();
                		Integer coinamount = userTarget.getCoins();
                		Integer killamount = userTarget.getKills();
                		Integer deathamount = userTarget.getDeaths();
                		
            			ItemStack targetSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                		SkullMeta targetSkullMeta = (SkullMeta) targetSkull.getItemMeta(); 
                		targetSkullMeta.setOwner(target.getName());
                		targetSkullMeta.setDisplayName(ColorOptions.stats + target.getName());
                		List<String> info2 = Arrays.asList(new String[]{
                			ColorOptions.stats + "Donator-rank: " + Donator.getDonatorColorSecondary(donatorID) + "(" + Donator.getDonatorColorPrimary(donatorID) + Donator.getDonatorName(donatorID) + Donator.getDonatorColorSecondary(donatorID) + ")",
                			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename,
                			ColorOptions.stats + "Coins: " + ColorOptions.statsresults + coinamount,
                			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + killamount,
                			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + deathamount,
                			
                		});
                		targetSkullMeta.setLore(info2);
                		targetSkull.setItemMeta(targetSkullMeta);
                		
                		if (user.getPlayer().hasPermission("k&k.staff") || user.getPlayer().isOp() || main.ownermodus.containsKey(uuid))
                		{
                			menu.setItem(slot, targetSkull);
                			slot++;
                		} else
                		if (!target.hasPermission("k&k.owner") && !target.isOp() && !main.ownermodus.containsKey(uuids))
                		{
                			menu.setItem(slot, targetSkull);
                			slot++;
                		}
            			ItemStack targetInfo = menu.getItem(slot-1);
            			ItemMeta meta = targetInfo.getItemMeta();
            			List<String> lore = meta.getLore();
                		if (userTarget.getFriendRequestList().contains(uuid))
                		{
                			lore.add("");
                			lore.add(ColorOptions.error + "Request already sent!");
                			meta.setLore(lore);
                			targetInfo.setItemMeta(meta);
                		} else
                		if (user.getFriendRequestList().contains(uuids))
                		{
                			lore.add("");
                			lore.add(ColorOptions.error + "This player already invited you!");
                			meta.setLore(lore);
                			targetInfo.setItemMeta(meta);
                		} else
                		{
                			lore.add("");
                			lore.add(ChatColor.GREEN + "Click here to add!");
                			meta.setLore(lore);
                			targetInfo.setItemMeta(meta);
                		}
            		}
        		}
        	}
    	} else
    	{
    		for (int i = 0; i < menu.getSize(); i++)
    		{
    			if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR)
        		{
        			menu.setItem(i, MenuCommand.addemptypmenu(ColorOptions.error + "No players online!", new ItemStack(Material.STAINED_CLAY, 1, (short) 14)));
        		}
    		}
    	}
    	menu = this.fillEmptyMenu(menu, false, " ", "");
    	user.getPlayer().openInventory(menu);
	}
	
	public void openFriendRequests(User user)
	{
		UUID uuid = user.getUUID();
		Integer requestAmount = user.getFriendRequestList().size();
		
    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(user.getUsername());
		skullMeta.setDisplayName(ColorOptions.stats + "Social statistics");
		List<String> lore = Arrays.asList(new String[]{
			ColorOptions.stats + "Current friends: " + ColorOptions.statsresults + user.getFriendAmount(),
//			ColorOptions.stats + "Invites from you: " + ColorOptions.statsresults + u,
			ColorOptions.stats + "Open requests: " + ColorOptions.statsresults + user.getFriendRequestList().size(),
			
		});
		skullMeta.setLore(lore);
		skull.setItemMeta(skullMeta);
		
		Integer menuSize = main.getMenuSize(Bukkit.getOnlinePlayers().size());
		
		Inventory menu = Bukkit.createInventory(null, menuSize, ColorOptions.stats + "Friend requests");

		menu.setItem(0, skull);
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Add new friends", Material.BANNER, ColorOptions.message + "All online players are listed here.", ColorOptions.message + "Click a player to sent a friendrequest"));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your friendsmanager"));
	
    	Integer slot = 9;
    	for (UUID uuids : user.getFriendRequestList())
    	{
    		String username = this.user.getUserName(uuids);
			Integer donatorID = this.user.getDonatorID(uuids);
    		String titlename = this.user.getTitleName(uuids);
    		Integer coinamount = this.user.getCoins(uuids);
    		Integer killamount = this.user.getKills(uuids);
    		Integer deathamount = this.user.getDeaths(uuids);
    		
			ItemStack targetSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
    		SkullMeta targetSkullMeta = (SkullMeta) targetSkull.getItemMeta(); 
    		targetSkullMeta.setOwner(username);
    		targetSkullMeta.setDisplayName(ColorOptions.stats + username);
    		List<String> info = Arrays.asList(new String[]{
    			ColorOptions.stats + "Donator-rank: " + Donator.getDonatorColorSecondary(donatorID) + "(" + Donator.getDonatorColorPrimary(donatorID) + Donator.getDonatorName(donatorID) + Donator.getDonatorColorSecondary(donatorID) + ")",
    			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename,
    			ColorOptions.stats + "Coins: " + ColorOptions.statsresults + coinamount,
    			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + killamount,
    			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + deathamount,
    			"",
    			ColorOptions.message + "Click here to accept/deny"
    			
    		});
    		targetSkullMeta.setLore(info);
    		targetSkull.setItemMeta(targetSkullMeta);
    		
    		menu.setItem(slot, targetSkull);
    		slot++;
    	}
    	menu = this.fillEmptyMenu(menu, false, " ", "");
    	user.getPlayer().openInventory(menu);
	}
	
	public void openFriendRequestOption(User user, UUID targetUUID) 
	{		
		Inventory menu = Bukkit.createInventory(null, 9, ColorOptions.stats + "Friend request from " + ColorOptions.statsresults + this.user.getUserName(targetUUID));
		
		menu.setItem(0, product.createClayItem(ColorOptions.messagesubjects + "Accept", true, ColorOptions.message + "Accept friend request"));
    	menu.setItem(4, MenuClick.addmenulist(ColorOptions.falsecommand + "Cancel", Material.BARRIER, ChatColor.GRAY + "Click here to go back to", ColorOptions.message + "your friendrequests"));
		menu.setItem(8, product.createClayItem(ColorOptions.error + "Deny", false, ColorOptions.message + "Deny friend request"));
    	menu = this.fillEmptyMenu(menu, false, " ", "");
		user.getPlayer().openInventory(menu);
	}
	
	public void openShopkeeperMenu(User user, Integer propertyID)
	{
		UUID uuid = user.getUUID();
		Integer userID = user.getID();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer income = user.getIncome();
		Integer salary = user.getSalary();
		
		Integer productAmount = proproduct.getProductListbyProperty(propertyID).size();
		
		String categoryName = this.propertycat.getCategoryName(property.getCategoryID(propertyID));
		String propertyName = property.getPropertyName(propertyID);
		Integer streetID = property.getStreetID(propertyID);
		Integer townID = street.getTownID(streetID);
		Integer streetNumber = property.getStreetNumber(propertyID);
		String streetName = street.getStreetName(streetID);
		String townName = town.getTownName(townID);
		Integer price = property.getPropertyPrice(propertyID);
		Integer propertyIncome = property.getIncome(propertyID);
		Integer ownerID = property.getPropertyOwnerID(propertyID);
		boolean hasQuest = Properties.Properties.hasActiveQuest(propertyID);
		Quest quest = hasQuest ? Properties.Properties.ActiveQuests.get(propertyID) : null;
		Integer menuSize = main.getMenuSize(productAmount);
		
		
				
		Inventory menu = Bukkit.createInventory(null, menuSize, ColorOptions.stats + "Products of " + ColorOptions.statsresults + propertyName);
		
		menu.setItem(0, Menus.getFinancial(user));
		menu.setItem(3, MenuClick.addmenulist(ColorOptions.statsformat + "Sell items to this " + ColorOptions.statsresults + categoryName, Material.CHEST, ColorOptions.message + "Click here to sell your items"));
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Information about " + ColorOptions.statsresults + propertyName, Material.ANVIL, 
				ColorOptions.statsformat + "Location", 
				ColorOptions.stats + "Street: " + ColorOptions.statsresults + streetName,
				ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + streetNumber,
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + townName,
				"",
				ColorOptions.statsformat + "Price and income",
				ColorOptions.stats  + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price),
				ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(propertyIncome)));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Exit", Material.BARRIER, ColorOptions.message + "Click here to exit"));
    	
    	if (ownerID != null && ownerID != 0)
    	{
    		if (ownerID == userID)
    		{
        		menu.setItem(4, product.addItemDescription(menu.getItem(4), Arrays.asList("", ColorOptions.stats + "You own this " + propertyName)));
    	    	if (property.getFullChest(propertyID) != null)
    	    	{
            		menu.setItem(5, MenuCommand.addpmenu(ColorOptions.stats + "Asignments (" + ColorOptions.statsresults + "1" + ColorOptions.stats + ")", Material.EMPTY_MAP, ChatColor.GRAY + "Start asignment", ChatColor.GRAY + "for this property", "", ColorOptions.statsformat + "It looks like one of the", ColorOptions.statsformat + "chests is full!", "", ColorOptions.messagesubjects + "Click to start!"));
    	    	} else
    	    	{
            		menu.setItem(5, MenuCommand.addpmenu(ColorOptions.stats + "Asignments", Material.EMPTY_MAP, ChatColor.GRAY + "Start asignment", ChatColor.GRAY + "for this property", "", ColorOptions.messagesubjects + "Click to start!"));
    	    	}
    		} else
    		{
        		menu.setItem(4, product.addItemDescription(menu.getItem(4), Arrays.asList(
        				"", 
        				ColorOptions.stats + "Owned by: " + ColorOptions.statsresults + this.user.getUserName(this.user.getUUIDbyID(ownerID)))));
    		}
    	} else
    	{
    		menu.setItem(4, product.addItemDescription(menu.getItem(4), Arrays.asList("", ColorOptions.message + "No owner",
    				ColorOptions.messageachievement + "In /menu go to ", 
    				ColorOptions.messagesubjects + "'Properties>Buy a new Property'",
    				ColorOptions.messageachievement + "to buy!")));
    	}
    	if (Properties.Properties.getQuestDeliverPackage(propertyID, null) != null)
    	{
    		quest = Properties.Properties.getQuestDeliverPackage(propertyID, null);
    		hasQuest = true;
    	}
    	if (hasQuest)
    	{
    		boolean show = true;
    		if (quest.isAssigned() && quest.getUser() != user)
    		{
    			show = false;
    		}
    		if (show)
    		{
        		ItemStack QuestItem = MenuClick.addmenulist(quest.isAssigned() ? ColorOptions.messageformat + "Your current Quest" : ChatColor.YELLOW + "New Quest!", Material.EMPTY_MAP, 
        				ColorOptions.stats + "Name: " + ColorOptions.statsresults + quest.getName(),
        				ColorOptions.stats + "Description: "
        				);
        		QuestItem = this.product.addItemDescription(QuestItem, this.product.getStringLines(quest.getDescription(), 5, ColorOptions.statsresults));
        		List<String> Description = new ArrayList<String>(Arrays.asList(
    					"",
    					ColorOptions.messageachievement + "Reward: ",
    					ColorOptions.coinStats + "Coins: " + ColorOptions.formatCurrency(quest.getCoinReward()),
    					ColorOptions.coinStats + "Experience: " + ColorOptions.formatCurrency(quest.getExperienceReward()),
    					ColorOptions.gemStats + "Gems: " + ColorOptions.formatCurrency(quest.getGemReward()),
    					""
        				));
        		if (quest instanceof QuestIntimidateRival)
    			{
    				QuestIntimidateRival QIR = (QuestIntimidateRival) quest;
    				String targetPropertyName = QIR.getTargetPropertyName();
    				String targetStreetName = this.street.getStreetName(QIR.getTargetStreetID());
    				Integer targetStreetNumber = QIR.getTargetStreetNumber();
    				String targetTownName = QIR.getTargetTownName();
    				
    				Description.addAll(Arrays.asList(
    				ColorOptions.statsformat + "Target information:",
    				ColorOptions.stats + "Name: " + ColorOptions.statsresults + targetPropertyName,
    				ColorOptions.stats + "Street: " + ColorOptions.statsresults + targetStreetName,
    				ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + targetStreetNumber,
    				ColorOptions.stats + "Town: " + ColorOptions.statsresults + targetTownName,
    				""
    						));
    			} else if (quest instanceof QuestDeliverPackage)
    			{
    				QuestDeliverPackage QDP = (QuestDeliverPackage) quest;
    				String targetPropertyName = QDP.getTargetPropertyName();
    				String targetStreetName = this.street.getStreetName(QDP.getTargetStreetID());
    				Integer targetStreetNumber = QDP.getTargetStreetNumber();
    				String targetTownName = QDP.getTargetTownName();
    				
    				Description.addAll(Arrays.asList(
    				ColorOptions.statsformat + "Warehouse information:",
    				ColorOptions.stats + "Name: " + ColorOptions.statsresults + targetPropertyName,
    				ColorOptions.stats + "Street: " + ColorOptions.statsresults + targetStreetName,
    				ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + targetStreetNumber,
    				ColorOptions.stats + "Town: " + ColorOptions.statsresults + targetTownName,
    				""
    						));
    			}
        		if (quest.isAssigned())
        		{
        			if (quest instanceof QuestHarvestResource)
        			{
            			if (!quest.isCollected())
            			{
        					Description.add(ColorOptions.message + "Total gathered: " + quest.getProgressAmount() + "/" + quest.getGoalAmount());
                			Description.add(ColorOptions.message + "You need to harvest the resources");
                			Description.add(ColorOptions.message + "Taking items from chests doesn't count");
            			} else
            			{
            				Description.add(ColorOptions.message + "Total delivered: " + quest.getDeliveredAmount() + "/" + quest.getGoalAmount());
                			Description.add(ColorOptions.messagesubjects + "Click here to deliver items!");
            			}
        			}
        			if (quest instanceof QuestIntimidateRival)
        			{	
        				Description.add(ColorOptions.message + "Status: " + (quest.isCollected() ? ColorOptions.messageachievement + "Killed" : ColorOptions.error + "Alive"));
        			}
        			if (quest instanceof QuestDeliverPackage)
        			{
        				QuestDeliverPackage QDP = (QuestDeliverPackage) quest;
        				if (QDP.getPropertyID() == propertyID)
        				{
        					Description.add(ColorOptions.message + "Go to the selected warehouse to");
        					Description.add(ColorOptions.message + "deliver the package");
        				} else
        				{
            				Description.add(ColorOptions.message + "Total delivered: " + quest.getDeliveredAmount() + "/" + quest.getGoalAmount());
                			Description.add(ColorOptions.messagesubjects + "Click here to deliver items!");
        				}
        			}
        		} else
        		{
        			Description.add(ColorOptions.messagesubjects + "Click here to accept Quest!");
        		}
        		
				QuestItem = this.product.addItemDescription(QuestItem, Description);
        		menu.setItem(2, QuestItem);
    		}
    		
    	}
    	
    	Integer slot = 9;
    	for (Integer productID : proproduct.getProductListbyProperty(propertyID))
    	{
    		Integer relationID = proproduct.getRelationID(productID, propertyID);
    		Integer itemprice = proproduct.getPrice(relationID);
    		Integer stock = proproduct.getAmount(relationID);
    		menu.setItem(slot, product.createPropertyItem(productID, 1, false, false));
    		
    		menu.setItem(slot, product.addItemDescription(menu.getItem(slot), Arrays.asList(
    				"",
    				ColorOptions.stats + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(itemprice),
    				ColorOptions.stats + "Stock: " + ColorOptions.statsresults + ColorOptions.formatCurrency(stock),
    				"",
    				ColorOptions.statsformat + "Click here for more information")));
    		
    		slot++;
    	}
    	
    	menu = this.fillEmptyMenu(menu, false, " ", "");

    	user.getPlayer().openInventory(menu);
    	if (hasQuest && !quest.isAssigned())
    	{
    		new BukkitRunnable()
    		{
    			public void run()
    			{
    	    		new MenuItemBlink(null, user, 2, 10);
    			}
    		}.runTaskLater(main, 20);
    	}
	}
	
	public void openSellMenu(User user, Integer propertyID)
	{
		UUID uuid = user.getUUID();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer income = user.getIncome();
		Integer salary = user.getSalary();
		
		Integer productAmount = proproduct.getProductListbyProperty(propertyID).size();
		
		String categoryName = this.propertycat.getCategoryName(property.getCategoryID(propertyID));
		String propertyName = property.getPropertyName(propertyID);
		Integer streetID = property.getStreetID(propertyID);
		Integer townID = street.getTownID(streetID);
		Integer streetNumber = property.getStreetNumber(propertyID);
		String streetName = street.getStreetName(streetID);
		String townName = town.getTownName(townID);
		Integer price = property.getPropertyPrice(propertyID);
		Integer propertyIncome = property.getIncome(propertyID);
		Integer ownerID = property.getPropertyOwnerID(propertyID);
		
		Inventory menu = Bukkit.createInventory(null, 9*4, ColorOptions.stats + "Sell items to " + ColorOptions.statsresults + propertyName);

		menu.setItem(0, Menus.getFinancial(user));
		menu.setItem(3, MenuClick.addmenulist(ColorOptions.stats + "Sell items", Material.CHEST, ColorOptions.message + "Click here to sell the items", "", ColorOptions.stats + "Price: " + ColorOptions.statsresults + "0"));
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Information about " + ColorOptions.statsresults + propertyName, Material.ANVIL, 
				ColorOptions.statsformat + "Location", 
				ColorOptions.stats + "Street: " + ColorOptions.statsresults + streetName,
				ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + streetNumber,
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + townName,
				"",
				ColorOptions.statsformat + "Price and income",
				ColorOptions.stats  + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price),
				ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(propertyIncome)));
		menu.setItem(5, MenuClick.addmenulist(ColorOptions.stats + "Sell instructions", Material.BOOK, ColorOptions.stats + "Instructions on how to sell items", ColorOptions.message + "1. Drag selected items from", ColorOptions.message + "your inventory to this menu.", "", ColorOptions.message + "2. Click the chest icon to sell.", "", ColorOptions.message + "When an item cannot be sold", ColorOptions.message + "it will be returned to your inventory", ColorOptions.error + "For a list of selected items, go to", ColorOptions.error + "Menu>Settings>ShopItems"));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ColorOptions.message + "Click here to go back"));
    	
    	for (int i = 0; i< menu.getSize(); i++)
    	{
    		if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR)
    		{
        		if (i < 19 || i > 25)
        		{
        			menu.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0)));
        		}
    		}
    	}
    	
    	user.getPlayer().openInventory(menu);
	}
	
	public void openForcedHouseSell(User user)
	{
		UUID uuid = user.getUUID();
		Integer slot = 3;
		Inventory menu = Bukkit.createInventory(null, 9, ColorOptions.stats + "Choose a house to remove");
    	for (int i : house.getHouseIDList(null))
    	{
			Integer streetID = house.getStreetID(i);
			String streetName = street.getStreetName(streetID);
			Integer streetnumber = house.getHouseNumber(i);
			String townName = town.getTownName(street.getTownID(streetID));
    		if (house.getHouseOwnerID(i) != null)
    		{
        		if (house.getHouseOwnerID(i) == user.getID())
        		{
        			if (house.getHouseSpawnPoint(i) == user.getSpawnpointID())
        			{
        				menu.setItem(slot, MenuClick.addmenulist(ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + house.getHouseName(i), Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + house.getHousePrice(i), ChatColor.GREEN + "Current spawnpoint!", ChatColor.RED + "Click to remove!" ));
        			} else
        			{
        	    		menu.setItem(slot, MenuClick.addmenulist(ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + house.getHouseName(i), Material.BED, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + house.getHousePrice(i), ChatColor.RED + "Click to remove!" ));
        			}
        			slot = slot++;
        		}
    		}
    	}

    	this.fillEmptyMenu(menu, false, ColorOptions.error + "Select a house to remove", "");
    	user.getPlayer().openInventory(menu);
	}
	
	public void openForcedPropertySell(User user)
	{
		UUID uuid = user.getUUID();
		Integer slot = 3;
		Inventory menu = Bukkit.createInventory(null, 9, ColorOptions.stats + "Choose a property to remove");
    	for (int i : property.getIDList(null, null))
    	{
			Integer streetID = property.getStreetID(i);
			String streetName = street.getStreetName(streetID);
			Integer streetnumber = property.getStreetNumber(i);
			String townName = town.getTownName(street.getTownID(streetID));
    		if (property.getPropertyOwnerID(i) != null)
    		{
        		if (property.getPropertyOwnerID(i) == user.getID())
        		{
    				menu.setItem(slot, MenuClick.addmenulist(ColorOptions.statsformat + "Propertyname: " + ColorOptions.statsresults + property.getPropertyName(i), Material.ANVIL, ColorOptions.statsformat + "Street: " + ColorOptions.statsresults + streetName, ColorOptions.statsformat + "Streetnumber: " + ColorOptions.statsresults + streetnumber, ColorOptions.statsformat + "Town: " + ColorOptions.statsresults + townName, ColorOptions.statsformat + "Category: " + ColorOptions.statsresults + propertycat.getCategoryName(property.getCategoryID(i)), ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + property.getPropertyPrice(i), ColorOptions.statsformat + "Income: " + ColorOptions.statsresults + property.getIncome(i), ChatColor.RED + "Click to remove" ));
        		}
        		slot = slot++;
    		}
    	}

    	this.fillEmptyMenu(menu, false, ColorOptions.error + "Select a property to remove!", "");
    	user.getPlayer().openInventory(menu);
	}
	
	public void setMenuItemBlink(CommandSender sender, User userTarget, Inventory menu, Integer itemSlot, Integer timeOut)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Trying to blink an item from menu called " + menu.getName());
		}
		UUID targetUUID = userTarget.getUUID();
		if (menu.getItem(itemSlot) != null && menu.getItem(itemSlot).getType() != Material.AIR)
		{
			ItemStack original = menu.getItem(itemSlot);
//			this.originalFlashItem.put(targetUUID, original);
			ItemStack flash = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
			ItemMeta meta = flash.getItemMeta();
			meta.setDisplayName(original.getItemMeta().getDisplayName());
			meta.setLore(original.getItemMeta().getLore());
			flash.setItemMeta(meta);
	    	BukkitTask task = new BukkitRunnable()
			{
				public void run()
				{
					if (menu.getItem(itemSlot) != null && menu.getItem(itemSlot).getType() != Material.AIR)
					{
						if (menu.getItem(itemSlot).getType() != Material.STAINED_GLASS_PANE)
						{
							menu.setItem(itemSlot, flash);
						} else
						{
							menu.setItem(itemSlot, original);
						}
					} else
					{
						this.cancel();
					}
				}
			}.runTaskTimer(main, 0, 10);
			new BukkitRunnable()
			{
				public void run()
				{
					stopMenuItemBlink(sender, userTarget, menu);
				}
			}.runTaskLater(main, timeOut*20);
			
			if (main.flashTask.containsKey(targetUUID))
			{
				List<BukkitTask> list = main.flashTask.get(targetUUID);
				list.add(task);
				main.flashTask.put(targetUUID, list);
				List<Integer> slotList = main.flashItemSlot.get(targetUUID);
				slotList.add(itemSlot);
				main.flashItemSlot.put(targetUUID, slotList);
			}
			sender.sendMessage(ColorOptions.messageachievement + "Succesfully blinking the item of slot " + ColorOptions.messagesubjects + itemSlot + ColorOptions.messageachievement + " at player " + ColorOptions.messagesubjects + userTarget.getUsername());
		} else
		{
			sender.sendMessage(ColorOptions.error + "No item could be found on the itemSlot: " + itemSlot);
		}
	}
	
	public void stopMenuItemBlink(CommandSender sender, User userTarget, Inventory menu)
	{
		UUID targetUUID = userTarget.getUUID();
		if (main.flashTask.containsKey(targetUUID))
		{
			List<BukkitTask> taskList = main.flashTask.get(targetUUID);
			List<Integer> slotList = main.flashItemSlot.get(targetUUID);
			
			Integer slot = main.flashItemSlot.get(targetUUID).get(0);
			BukkitTask task = main.flashTask.get(targetUUID).get(0);
			task.cancel();
//			menu.setItem(slot, originalFlashItem.get(targetUUID));
			main.flashTask.remove(targetUUID);
			main.flashItemSlot.remove(targetUUID);
//			originalFlashItem.remove(targetUUID);
			sender.sendMessage(ColorOptions.messageachievement + "Succesfully stopped blinking the item of slot " + ColorOptions.messagesubjects + slot + ColorOptions.messageachievement + " at player " + ColorOptions.messagesubjects + userTarget.getUsername());
		} else
		{
			sender.sendMessage(ColorOptions.error + "Couldn't find a flash-task for player " + userTarget.getUsername());
		}
	}
	
	public Inventory createDuelMenu(User user1, User user2, Integer arenaID)
	{
		Arena arena = new Arena();
		ResultSet arenaResult = arena.getArena(arenaID);
		String arenaName = null;
		Integer streetID;
		String streetName = null;
		Integer streetNumber = null;
		String townName = null;
		try {
			arenaName = arenaResult.getString("Name");
			streetID = arenaResult.getInt("StreetID");
			streetName = street.getStreetName(streetID);
			streetNumber = arenaResult.getInt("StreetNumber");
			townName = town.getTownName(street.getTownID(streetID));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		UUID uuid1 = user1.getUUID();
		UUID uuid2 = user2.getUUID();
		Integer coinamount1 = user1.getCoins();
		Integer gemamount1 = user1.getGems();
		Integer salary1 = user1.getSalary();
		Integer income1 = user1.getIncome();
		
		Integer coinamount2 = user2.getCoins();
		Integer gemamount2 = user2.getGems();
		Integer salary2 = user2.getSalary();
		Integer income2 = user2.getIncome();
		
		Integer donatorID1 = user1.getDonatorID();
		String titlename1 = user1.getTitleName();
		Integer killamount1 = user1.getKills();
		Integer deathamount1 = user1.getDeaths();
		
		Integer donatorID2 = user2.getDonatorID();
		String titlename2 = user2.getTitleName();
		Integer killamount2 = user2.getKills();
		Integer deathamount2 = user2.getDeaths();
		
		ItemStack player1Skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta player1SkullMeta = (SkullMeta) player1Skull.getItemMeta(); 
		player1SkullMeta.setOwner(user1.getUsername());
		player1SkullMeta.setDisplayName(ColorOptions.stats + user1.getUsername());
		List<String> info1 = Arrays.asList(new String[]{
			ColorOptions.stats + "Donator-rank: " + Donator.getDonatorColorSecondary(donatorID1) + "(" + Donator.getDonatorColorPrimary(donatorID1) + Donator.getDonatorName(donatorID1) + Donator.getDonatorColorSecondary(donatorID1) + ")",
			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename1,
			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + killamount1,
			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + deathamount1,
			"",
			ColorOptions.stats + "Coins: " + ColorOptions.coinStats + ColorOptions.formatCurrency(coinamount1), 
			ColorOptions.stats + "Gems: " + ColorOptions.gemStats + ColorOptions.formatCurrency(gemamount1), 
			ColorOptions.stats + "Salary: " + ColorOptions.statsresults + ColorOptions.formatCurrency(salary1), 
			ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(income1)
		});
		player1SkullMeta.setLore(info1);
		player1Skull.setItemMeta(player1SkullMeta);
		
		ItemStack player2Skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta player2SkullMeta = (SkullMeta) player2Skull.getItemMeta(); 
		player2SkullMeta.setOwner(user2.getUsername());
		player2SkullMeta.setDisplayName(ColorOptions.stats + user2.getUsername());
		List<String> info2 = Arrays.asList(new String[]{
			ColorOptions.stats + "Donator-rank: " + Donator.getDonatorColorSecondary(donatorID2) + "(" + Donator.getDonatorColorPrimary(donatorID2) + Donator.getDonatorName(donatorID2) + Donator.getDonatorColorSecondary(donatorID2) + ")",
			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename2,
			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + killamount2,
			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + deathamount2,
			"",
			ColorOptions.stats + "Coins: " + ColorOptions.coinStats + ColorOptions.formatCurrency(coinamount2), 
			ColorOptions.stats + "Gems: " + ColorOptions.gemStats + ColorOptions.formatCurrency(gemamount2), 
			ColorOptions.stats + "Salary: " + ColorOptions.statsresults + ColorOptions.formatCurrency(salary2), 
			ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(income2)
		});
		player2SkullMeta.setLore(info2);
		player2Skull.setItemMeta(player2SkullMeta);
		
		Inventory menu = Bukkit.createInventory(null, 6*9, ColorOptions.stats + "Choose duel-type and place bets!");
		
    	menu.setItem(0, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ColorOptions.message + "Click here to go back"));
    	menu.setItem(4, product.createItem(ColorOptions.stats + "Arena: " + ColorOptions.statsresults + arenaName, new ItemStack(Material.DIAMOND_AXE, 1), true, ColorOptions.stats + "Streetname: " + ColorOptions.statsresults + streetName, ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + streetNumber, ColorOptions.stats + "Town: " + ColorOptions.statsresults + townName));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ColorOptions.message + "Click here to go back"));
		menu.setItem(45, player1Skull);
		menu.setItem(46, product.createClayItem(ColorOptions.error + "Unready", false, ColorOptions.message + "Both players have to", ColorOptions.message + "set this as ready to duel"));
		menu.setItem(48, MenuClick.addmenulist(ColorOptions.coinStats + "Duel type: Coins", Material.GOLD_BLOCK, ColorOptions.message + "Place an amount of coins", ColorOptions.message + "as bet and the winner will", ColorOptions.message + "receive both bets", "", ColorOptions.stats + "Shit-click here", ColorOptions.stats + "to place a bet!", ColorOptions.coinStats + "Current bet: 0"));
		menu.setItem(50, MenuClick.addmenulist(ColorOptions.coinStats + "Duel type: Coins", Material.GOLD_BLOCK, ColorOptions.message + "Place an amount of coins", ColorOptions.message + "as bet and the winner will", ColorOptions.message + "receive both bets", "", ColorOptions.stats + "Shit-click here", ColorOptions.stats + "to place a bet!", ColorOptions.coinStats + "Current bet: 0"));
		menu.setItem(52, product.createClayItem(ColorOptions.error + "Unready", false, ColorOptions.message + "Both players have to", ColorOptions.message + "set this as ready to duel"));
		menu.setItem(53, player2Skull);
		
    	for (int i = 0; i < menu.getSize(); i++)
    	{
    		if (menu.getItem(i) == null || menu.getItem(i).getType() == Material.AIR)
    		{
    			if (!duelMenuFree.contains(i))
    			{
        			menu.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0)));
    			}
    		}
    	}
    	
    	return menu;
	}
	
	public void openDuelMenu(Player player, Inventory menu)
	{
		player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
		player.openInventory(menu);
	}
	
	public void openArenaMenu(Player player, Integer arenaID)
	{
		ResultSet arenaI = arena.getArena(arenaID);
		Integer streetNumber = null;
		String arenaName = null;
		Integer streetID = null;
		String streetName = null;
		try 
		{
			arenaName = arenaI.getString("Name");
			streetID = arenaI.getInt("StreetID");
			streetName = street.getStreetName(streetID);
			streetNumber = arenaI.getInt("StreetNumber");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		String townName = town.getTownName(street.getTownID(streetID));
		
    	Inventory menu =  Bukkit.createInventory(null, 3*9, ColorOptions.messageformat + "Arena: " + arenaName);
    	
    	menu.setItem(4, product.createItem(ColorOptions.stats + "Name: " + ColorOptions.statsresults + arenaName, new ItemStack(Material.DIAMOND_AXE, 1), true, ColorOptions.stats + "Streetname: " + ColorOptions.statsresults + streetName, ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + streetNumber, ColorOptions.stats + "Town: " + ColorOptions.statsresults + townName));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ColorOptions.message + "Click here to go back"));
    	menu.setItem(12, MenuClick.addmenulist(ColorOptions.stats + "Fight other players", Material.SKULL_ITEM, ColorOptions.message + "Fight 1v1 duels against", ColorOptions.message + "other players on the server"));
    	menu.setItem(14, product.createItem(ColorOptions.stats + "Fight gladiators", new ItemStack(Material.MONSTER_EGG), false, ChatColor.GRAY + "Click here to fight gladiators", ChatColor.GRAY + "To win prestigious rewards!", "", ColorOptions.error + "Coming soon!"));
    	menu = this.fillEmptyMenu(menu, false, " ", "");
    	player.openInventory(menu);
	}
	
	public void openDuelInviteMenu(User user)
	{
		UUID uuid = user.getUUID();
		Integer coinamount1 = user.getCoins();
		Integer gemamount1 = user.getGems();
		Integer salary1 = user.getSalary();
		Integer income1 = user.getIncome();
		
		Integer donatorID1 = user.getDonatorID();
		String titlename1 = user.getTitleName();
		Integer killamount1 = user.getKills();
		Integer deathamount1 = user.getDeaths();
		
		ItemStack player1Skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta player1SkullMeta = (SkullMeta) player1Skull.getItemMeta(); 
		player1SkullMeta.setOwner(user.getUsername());
		player1SkullMeta.setDisplayName(ColorOptions.stats + user.getUsername());
		List<String> info1 = Arrays.asList(new String[]{
			ColorOptions.stats + "Donator-rank: " + Donator.getDonatorColorSecondary(donatorID1) + "(" + Donator.getDonatorColorPrimary(donatorID1) + Donator.getDonatorName(donatorID1) + Donator.getDonatorColorSecondary(donatorID1) + ")",
			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename1,
			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + killamount1,
			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + deathamount1,
			"",
			ColorOptions.stats + "Coins: " + ColorOptions.coinStats + ColorOptions.formatCurrency(coinamount1), 
			ColorOptions.stats + "Gems: " + ColorOptions.gemStats + ColorOptions.formatCurrency(gemamount1), 
			ColorOptions.stats + "Salary: " + ColorOptions.statsresults + ColorOptions.formatCurrency(salary1), 
			ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(income1)
		});
		player1SkullMeta.setLore(info1);
		player1Skull.setItemMeta(player1SkullMeta);
		
    	Inventory menu =  Bukkit.createInventory(null, main.getMenuSize(Bukkit.getOnlinePlayers().size()), ColorOptions.messageformat + "Choose a player to duel");
    	
    	menu.setItem(0, player1Skull);
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ColorOptions.message + "Click here to go back"));
    	
    	Integer slot = 9;
    	if (Bukkit.getOnlinePlayers().size() > 1)
    	{
        	for (Player target : Bukkit.getOnlinePlayers())
        	{
        		UUID targetUUID = target.getUniqueId();
        		if (targetUUID != uuid)
        		{
            		Integer donatorID2 = this.user.getDonatorID(targetUUID);
            		String titlename2 = this.user.getTitleName(targetUUID);
            		Integer killamount2 = this.user.getKills(targetUUID);
            		Integer deathamount2 = this.user.getDeaths(targetUUID);
            		
            		ItemStack player2Skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            		SkullMeta player2SkullMeta = (SkullMeta) player2Skull.getItemMeta(); 
            		player2SkullMeta.setOwner(target.getName());
            		player2SkullMeta.setDisplayName(ColorOptions.stats + "Name: " + ColorOptions.statsresults + target.getName());
            		List<String> info2 = Arrays.asList(new String[]{
            			ColorOptions.stats + "Donator-rank: " + Donator.getDonatorColorSecondary(donatorID2) + "(" + Donator.getDonatorColorPrimary(donatorID2) + Donator.getDonatorName(donatorID2) + Donator.getDonatorColorSecondary(donatorID2) + ")",
            			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titlename2,
            			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + killamount2,
            			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + deathamount2,
            			"",
            			ChatColor.GREEN + "Click here to invite for a duel!"
            		});
            		player2SkullMeta.setLore(info2);
            		player2Skull.setItemMeta(player2SkullMeta);
            		menu.setItem(slot, player2Skull);
            		slot++;
        		}
        	}
    	} else
    	{
    		for (int i = 9; i < 18; i++)
    		{
    			menu.setItem(i, MenuCommand.addemptypmenu(ColorOptions.error + "No players online!", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11), ""));
    		}
    	}
    	this.fillEmptyMenu(menu, false, " ");
    	user.getPlayer().openInventory(menu);
	}
	
	public void openItemCoupon(User user, Integer relationID, Inventory OriginalMenu)
	{
		Integer coins = user.getCoins();
		Integer itemPrice = proproduct.getPrice(relationID);
		Integer OfferAmount = proproduct.getAmount(relationID);
		
		Integer productID = proproduct.getProductID(relationID);
		Integer categoryID = product.getCategoryID(productID, false);
		
		ItemStack coupon = getItemCoupon(user.getPlayer(), categoryID);
		
		if (coupon != null)
		{			
	    	Inventory menu =  Bukkit.createInventory(null, 1*9, ColorOptions.messageformat + "Use " + coupon.getItemMeta().getDisplayName() + ColorOptions.messageformat + "?");
	    	
	    	menu.setItem(0, product.createClayItem(ColorOptions.messagesubjects + "Buy with coupon", true, ColorOptions.message + "Click here to buy with a coupon", ColorOptions.message + "", ColorOptions.error + "Your coupons are shown", ColorOptions.error + "on the right side of this Icon"));
	    	menu.setItem(2, product.addItemDescription(coupon, Arrays.asList(
	    			" ",
	    			ColorOptions.error + "Coupon from your inventory")));
	    	menu.setItem(4, product.createPropertyItem(productID, 1, false, false));
			if (coins >= itemPrice)
			{
				if (proproduct.getAmount(relationID) == 0)
				{
			    	menu.setItem(6, product.createItem(ColorOptions.stats + "This item costs: " + ColorOptions.statsresults + itemPrice, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + OfferAmount, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "This item is out of stock!"));
				} else
				{
			    	menu.setItem(6, product.createItem(ColorOptions.stats + "This item costs: " + ColorOptions.statsresults + itemPrice, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + OfferAmount, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.GREEN + "Click here to buy this item!"));
				}
			} else
			{
				Integer needed = (itemPrice - user.getCoins());
		    	menu.setItem(6, product.createItem(ColorOptions.stats + "This item costs: " + ColorOptions.statsresults + itemPrice, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + OfferAmount, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "You need " + needed + " more coins!"));
			}	    	
			menu.setItem(8, product.createClayItem(ColorOptions.error + "Cancel", false, ColorOptions.message + "Go back to the item information"));
		
	    	this.fillEmptyMenu(menu, false, null, null);
	    	ItemFrameAdd.couponMenu.put(user.getUUID(), menu);
	    	user.getPlayer().openInventory(menu);
	    	user.getPlayer().sendMessage(ColorOptions.messageformat + "Shopkeeper: " + ColorOptions.message + "It seems like you have a coupon for this product!");
			user.getPlayer().getInventory().remove(coupon);
		} else
		{
			user.getPlayer().openInventory(OriginalMenu);
			user.getPlayer().sendMessage(ColorOptions.error + "You don't have a coupon for this category of items!");
		}
	}
	
	public ItemStack getItemCoupon(Player player, Integer productCategoryID)
	{
		ItemStack coupon = null;
		
		for (ItemStack content : player.getInventory().getContents())
		{
			if (content != null && content.getType() != Material.AIR)
			{
				if (content.hasItemMeta() && content.getItemMeta().hasDisplayName())
				{
					if (ChatColor.stripColor(content.getItemMeta().getDisplayName()).equalsIgnoreCase("item coupon"))
					{
						String category = ChatColor.stripColor(content.getItemMeta().getLore().get(0).split(": ")[1]);
						Integer categoryID = this.productcat.getCategoryID(category);
						if (categoryID != null)
						{
							if (categoryID == productCategoryID)
							{
								coupon = content;
								break;
							}
						}
					}
				}
			}
		}
		
		return coupon;
	}
	
	public void openGemShop(User user)
	{
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		ArrayList<Integer> gemproducts = product.getIDListbyCategory("special", true);
		
		Inventory menu = Bukkit.createInventory(null, main.getMenuSize(gemproducts.size()), ColorOptions.gemStats + "Gem-shop");
	
		menu.setItem(0, Menus.getFinancial(user));
    	menu.setItem(4, MenuCommand.addpmenu(ColorOptions.gemStats + "Gem-shop", Material.DIAMOND, ChatColor.GRAY + "Buy special items with gems"));
    	menu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
	
    	Integer slot = 9;
    	if (gemproducts != null && !gemproducts.isEmpty())
    	{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Gem-items found!");
			}
    		for (Integer productID : gemproducts)
    		{
    			ItemStack item = product.createPropertyItem(productID, 1, false, false);
    			Integer price = product.getPriceMax(productID);
    			String itemdesc = product.getDescription(productID, false);
    			List<String> description = new ArrayList<String>(Arrays.asList(
    					"",
    					ColorOptions.stats + "Description: "
    					));
    			String[] split = itemdesc.split(" ");
    			if (itemdesc.split(" ").length > 5)
    			{
    				StringBuilder sb = new StringBuilder();
    				int count = 0;
    		        for(int i = 0; i < split.length; i +=5)
    		        {       
    		            count=i+4;
    		            for(int j=i;j<=count; j++)
    		            {
    		            	if (j < split.length)
    		            	{
    		            		sb.append(split[j]).append(" ");
    		            	}
    		            }
    		            description.add(ColorOptions.statsresults + sb.toString());
    		            sb.delete(0, sb.length());
    		        }
    			}
    			description.addAll(Arrays.asList(
    					"",
    					ColorOptions.gemStats + "Price: " + product.getPriceMax(productID)
    					));
    			if (gemamount >= price)
    			{
    				description.add(ColorOptions.messagesubjects + "Click here to buy!");
    			} else
    			{
    				Integer rest = (price-gemamount);
    				description.add(ColorOptions.error + "You need " + rest + " more gems!");
    			}
    			menu.setItem(slot, product.addItemDescription(item, description));
    			slot++;
    		}
    	} else
    	{
    		for (int i = 9; i < 18; i++)
    		{
    			menu.setItem(i, MenuCommand.addemptypmenu(ColorOptions.error + "No special products available now!", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11), ""));
    		}
    	}
    	
    	this.fillEmptyMenu(menu, false, " ", "");
    	
    	user.getPlayer().openInventory(menu);
	}
	
	public void openTutorialStartMenu(User user)
	{
		Inventory menu = Bukkit.createInventory(null, 3*9, Menus.TutorialStartMenu);
		
    	menu.setItem(11, product.createClayItem(ChatColor.GREEN + "Start Tutorial", true, ColorOptions.message + "Click here to start a tutorial", ColorOptions.message + "about the basics of this server"));
    	menu.setItem(15, product.createClayItem(ChatColor.RED + "Skip Tutorial", false, ColorOptions.message + "Click here to start playing", ColorOptions.message + "without tutorial!"));
	
    	user.getPlayer().openInventory(menu);
	}
	
	public void openAssignmentsMenu(User user)
	{
		
		Inventory menu = Bukkit.createInventory(null, 3*9, Menus.AssignmentMenu);
		
    	menu.setItem(0, Menus.getSocialProfile(user));
		menu.setItem(4, MenuCommand.addpmenu(ColorOptions.messagesubjects + "Assignments", Material.EMPTY_MAP, ChatColor.GRAY + "See the progress of your", ColorOptions.message + "assignments or claim your reward"));
    	menu.setItem(8, Menus.getBackButton(Menus.AssignmentSubMenu));
    	
//    	if (user.getTitleID() < 15)
//    	{
//    		menu.setItem(13, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "Reach the title " + title.getTitleName(15, 0) + "/" + title.getTitleName(15, 1), ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
//    	}
		menu.setItem(13, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "Reach the title " + title.getTitleName(15, 0) + "/" + title.getTitleName(15, 1), ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
		menu.setItem(14, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.noblesubjects + "Noble", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
		menu.setItem(15, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.royalsubjects + "Royal", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
		menu.setItem(16, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.dbsubjects + "Dragon Blood", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
//    	if (user.getDonatorID() == 0)
//    	{
//    		menu.setItem(14, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.noblesubjects + "Noble", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
//    		menu.setItem(15, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.royalsubjects + "Royal", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
//    		menu.setItem(16, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.dbsubjects + "Dragon Blood", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
//    	} else if (user.getDonatorID() == 1)
//    	{
//    		menu.setItem(15, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.royalsubjects + "Royal", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
//    		menu.setItem(16, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.dbsubjects + "Dragon Blood", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
//    	} else if (user.getDonatorID() == 2)
//    	{
//    		menu.setItem(16, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You need to be a " + ColorOptions.dbsubjects + "Dragon Blood", ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
//    	}
    	
    	menu = this.fillEmptyMenu(menu, false, " ", "");

    	Integer AssignmentIndex = 0;
    	for (int i = 10; i < 17; i++)
    	{
    		if (user.getAssignmentList().size() > AssignmentIndex)
    		{
    			Assignment assignment = user.getAssignmentList().get(AssignmentIndex);
    			if (!assignment.isCollected())
    			{
        			ItemStack assignmentItem = MenuClick.addmenulist(
        					ColorOptions.messageformat + assignment.getName(), 
        					Material.EMPTY_MAP, ColorOptions.message + "Description:"
        					);
        			List<String> description = this.product.getStringLines(assignment.getDescription(), 4, ColorOptions.message);
        			description.addAll(Arrays.asList(
        					"",
        					ColorOptions.messageachievement + "Reward: ",
        					ColorOptions.coinStats + "Coins: " + ColorOptions.formatCurrency(assignment.getCoinReward()),
        					ColorOptions.coinStats + "Experience: " + ColorOptions.formatCurrency(assignment.getExperienceReward()),
        					ColorOptions.gemStats + "Gems: " + ColorOptions.formatCurrency(assignment.getGemReward()),
        					"",
        					ColorOptions.message + "Progress: " + assignment.getProgressAmount() + "/" + assignment.getGoalAmount()
        					));
    				assignmentItem = this.product.addItemDescription(assignmentItem, description);

        			menu.setItem(i, assignmentItem);
        			if (assignment.getProgressAmount() > 0 && assignment instanceof AssignmentTravelRandom)
        			{
        				menu.setItem(i, product.addItemDescription(menu.getItem(i), Arrays.asList(ColorOptions.message + ((AssignmentTravelRandom) assignment).getEnteredTownList().toString())));
        			}
        			if (assignment.iscompleted())
        			{
        				ItemStack achieved = product.addItemDescription(menu.getItem(i), Arrays.asList(ColorOptions.messageachievement + "Click here to claim reward!"));
        				achieved.setType(Material.MAP);
        				menu.setItem(i, achieved);
        			}
    			} else
    			{
    				ZonedDateTime time = main.getDate();
    				Long millis = time.toInstant().toEpochMilli();
    				LocalDateTime date = LocalDateTime.of(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), 0, 0, 0).plusDays(1);
    				ZonedDateTime dateTime = date.atZone(main.getZoneId());
    				dateTime = dateTime.minusHours(1);
    				Long expireMillis = dateTime.toInstant().toEpochMilli();
    				Long remainingMillis = expireMillis-millis;
    				Date remainingDate = new Date(remainingMillis);
    				
    				ItemStack completedItem = MenuClick.addmenulist(
    						ColorOptions.messageachievement + "Completed!",
    						Material.WATCH,
    						ColorOptions.message + "You already completed this",
    						ColorOptions.message + "assignment today!",
    						"",
    						ColorOptions.message + "Wait for the next day or",
    						ColorOptions.message + "Refresh now for " + ColorOptions.gemStats + Assignments.Assignments.RefreshPrice + " Gems!",
    						"",
    						ColorOptions.messageachievement + "Time remaining: ",
    						ColorOptions.messageachievement + "" + remainingDate.getHours() + " Hours, " + remainingDate.getMinutes() + " Minutes, " + remainingDate.getSeconds() + " Seconds",
    						"");
    				
    				List<String> description = new ArrayList<String>();
    				if (user.getGems() >= Assignments.Assignments.RefreshPrice)
    				{
    					description.add(ColorOptions.messagesubjects + "Click here to refresh!");
    				} else
    				{
    					description.add(ColorOptions.falsecommand + "You need " + (Assignments.Assignments.RefreshPrice-user.getGems()) + " more gems!");
    				}
    				
    				completedItem = this.product.addItemDescription(completedItem, description);
    				
    				menu.setItem(i, completedItem);
    			}
    			AssignmentIndex++;
    		} else if (menu.getItem(i).getType() == Material.STAINED_GLASS_PANE)
    		{
    			menu.setItem(i, new ItemStack(Material.AIR, 1));
    		}
    	}
    	    	
    	user.getPlayer().openInventory(menu);
    	
	}
	
	public void openAchievementsMenu(User user)
	{
		Inventory menu = Bukkit.createInventory(null, 6*9, Menus.AchievementMenu);
		
		menu.setItem(0, Menus.getSocialProfile(user));
    	menu.setItem(1, MenuClick.addmenulist(ColorOptions.messagesubjects + "Previous", Material.ARROW, ""));
    	menu.setItem(2, MenuClick.addmenulist(ColorOptions.message + "Filter: " + ColorOptions.messagesubjects + "All", Material.BUCKET, ""));
    	menu.setItem(4, MenuClick.addmenulist(ChatColor.GOLD + "Achievements", Material.BOOK, ColorOptions.message + "See all achievements of the game"));
    	menu.setItem(7, MenuClick.addmenulist(ColorOptions.messagesubjects + "Next", Material.ARROW, ""));
    	menu.setItem(8, Menus.getBackButton(Menus.AssignmentSubMenu));
    	
    	menu = this.fillEmptyMenu(menu, false, " ", "");
    	
    	user.getPlayer().openInventory(menu);
	}
	
	public void openQuestsMenu(User user)
	{
		Bukkit.getConsoleSender().sendMessage("Opening quest menu");
		Inventory menu = Bukkit.createInventory(null, 3*9, Menus.QuestMenu);
		
    	menu.setItem(0, Menus.getSocialProfile(user));
		menu.setItem(4, MenuCommand.addpmenu(ChatColor.YELLOW + "Quests", Material.EMPTY_MAP, ChatColor.GRAY + "See the progress of your", ColorOptions.message + "quests received by shopkeepers"));
    	menu.setItem(8, Menus.getBackButton(Menus.AssignmentSubMenu));
    	
		menu.setItem(13, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "Reach the title", ColorOptions.message + title.getTitleName(12, 0) + "/" + title.getTitleName(12, 1), ColorOptions.message + "to unlock this slot!", "", ColorOptions.messageformat + "Click here for more information"));
		menu.setItem(14, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You can unlock this slot", ColorOptions.message + "in the " + Menus.GemShopMenu, "", ColorOptions.messageformat + "Click here for more information"));
		menu.setItem(15, MenuClick.addmenulist(ColorOptions.falsecommand + "Locked!", Material.BARRIER, ColorOptions.message + "You can unlock this slot", ColorOptions.message + "in the " + Menus.GemShopMenu, "", ColorOptions.messageformat + "Click here for more information"));

    	menu = this.fillEmptyMenu(menu, false, " ", "");
    	
    	Integer QuestIndex = 0;
    	for (int i = 10; i < 16; i++)
    	{
    		if (user.getQuestList().size() > QuestIndex)
    		{
    			Quest quest = user.getQuestList().get(QuestIndex);
    			Integer propertyID = quest.getPropertyID();
    			String propertyName = this.property.getPropertyName(propertyID);
    			Integer streetID = this.property.getStreetID(propertyID);
    			String streetName = this.street.getStreetName(streetID);
    			Integer streetNumber = this.property.getStreetNumber(propertyID);
    			String townName = this.town.getTownName(this.street.getTownID(streetID));
    			if (!quest.isCompleted())
    			{
        			ItemStack assignmentItem = MenuClick.addmenulist(
        					ColorOptions.messageformat + quest.getName(), 
        					Material.EMPTY_MAP, ColorOptions.message + "Description:"
        					);
        			List<String> description = this.product.getStringLines(quest.getDescription(), 5, ColorOptions.message);
        			description.addAll(Arrays.asList(
        					"",
        					ColorOptions.statsformat + "Property information:", 
        					ColorOptions.stats + "Name: " + ColorOptions.statsresults + propertyName,
        					ColorOptions.stats + "Street: " + ColorOptions.statsresults + streetName,
        					ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + streetNumber,
        					ColorOptions.stats + "Town: " + ColorOptions.statsresults + townName,
        					"",
        					ColorOptions.messageachievement + "Reward: ",
        					ColorOptions.coinStats + "Coins: " + ColorOptions.formatCurrency(quest.getCoinReward()),
        					ColorOptions.coinStats + "Experience: " + ColorOptions.formatCurrency(quest.getExperienceReward()),
        					ColorOptions.gemStats + "Gems: " + ColorOptions.formatCurrency(quest.getGemReward()),
        					""
        					));
        			if (quest instanceof QuestHarvestResource)
        			{
            			if (!quest.isCollected())
            			{
        					description.add(ColorOptions.message + "Total gathered: " + quest.getProgressAmount() + "/" + quest.getGoalAmount());
            			} else
            			{
            				description.add(ColorOptions.message + "Total delivered: " + quest.getDeliveredAmount() + "/" + quest.getGoalAmount());
            			}
        			}
        			if (quest instanceof QuestIntimidateRival)
        			{
        				QuestIntimidateRival QIR = (QuestIntimidateRival) quest;
        				String targetPropertyName = QIR.getTargetPropertyName();
        				String targetStreetName = this.street.getStreetName(QIR.getTargetStreetID());
        				Integer targetStreetNumber = QIR.getTargetStreetNumber();
        				String targetTownName = QIR.getTargetTownName();
        				
        				description.addAll(Arrays.asList(
        				ColorOptions.statsformat + "Target information:",
        				ColorOptions.stats + "Name: " + ColorOptions.statsresults + targetPropertyName,
        				ColorOptions.stats + "Street: " + ColorOptions.statsresults + targetStreetName,
        				ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + targetStreetNumber,
        				ColorOptions.stats + "Town: " + ColorOptions.statsresults + targetTownName,
        				"",
        				ColorOptions.message + "Status: " + (quest.isCollected() ? ColorOptions.messageachievement + "Killed" : ColorOptions.error + "Alive")
        						));
        			} else
    				if (quest instanceof QuestDeliverPackage)
        			{
    					QuestDeliverPackage QDP = (QuestDeliverPackage) quest;
        				String targetPropertyName = QDP.getTargetPropertyName();
        				String targetStreetName = this.street.getStreetName(QDP.getTargetStreetID());
        				Integer targetStreetNumber = QDP.getTargetStreetNumber();
        				String targetTownName = QDP.getTargetTownName();
        				
        				description.addAll(Arrays.asList(
        				ColorOptions.statsformat + "Warehouse information:",
        				ColorOptions.stats + "Name: " + ColorOptions.statsresults + targetPropertyName,
        				ColorOptions.stats + "Street: " + ColorOptions.statsresults + targetStreetName,
        				ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + targetStreetNumber,
        				ColorOptions.stats + "Town: " + ColorOptions.statsresults + targetTownName,
        				"",
        				ColorOptions.message + "Total delivered: " + quest.getDeliveredAmount() + "/" + quest.getGoalAmount()
        						));
        			}
    				assignmentItem = this.product.addItemDescription(assignmentItem, description);

        			menu.setItem(i, assignmentItem);
//        			if (quest.getProgressAmount() > 0 && quest instanceof GatherResourceQuest)
//        			{
//        				menu.setItem(i, product.addItemDescription(menu.getItem(i), Arrays.asList(ColorOptions.message + ((GatherResourceQuest) quest).getEnteredTownList().toString())));
//        			}
        			if (quest.isCollected())
        			{
        				ItemStack achieved = product.addItemDescription(menu.getItem(i), Arrays.asList(ColorOptions.messageachievement + "Visit the property to collect reward!"));
        				achieved.setType(Material.MAP);
        				menu.setItem(i, achieved);
        			}
    			}
    			QuestIndex++;
    		} else if (menu.getItem(i).getType() == Material.STAINED_GLASS_PANE)
    		{
    			menu.setItem(i, MenuClick.addmenulist(ColorOptions.messageachievement + "No quest accepted!", Material.COMPASS, ColorOptions.message + "Quests can be offered to you", ColorOptions.message + "by shopkeepers when", ColorOptions.message + "you enter a property"));
    		}
    	}
    	
    	user.getPlayer().openInventory(menu);
	}
	
	public void openSupportMenu(User user)
	{
		Inventory menu = Bukkit.createInventory(null, 3*9, Menus.SupportMenu);
		
		menu.setItem(0, Menus.getSocialProfile(user));
		menu.setItem(4, MenuClick.addmenulist(ChatColor.YELLOW + "Support", Material.REDSTONE_TORCH_ON, ColorOptions.message + "If you have any questions", ColorOptions.message + "you will find the answer here!", "", ColorOptions.message + "Start tutorials, find item details", ColorOptions.message + "or read the FAQ.", ColorOptions.message + "It's all here!"));
		menu.setItem(8, Menus.getBackButton(Menus.PersonalMenu));
		
		menu.setItem(12, product.createItem(ChatColor.BLUE + "Tutorials", new ItemStack(Material.LEASH, 1), false, ColorOptions.message + "Having difficulties with playing?" , ColorOptions.message + "Find and start any tutorial", ColorOptions.message + "to understand the game better!"));
		menu.setItem(13, product.createItem(ChatColor.GOLD + "Frequently Asked Questions", new ItemStack(Material.BOOK_AND_QUILL, 1), false, ColorOptions.message + "Read through common questions", ColorOptions.message + "and the awnsers given", ColorOptions.message + "by members of staff!", "", ColorOptions.error + "Coming soon!"));
		menu.setItem(14, product.createItem(ColorOptions.messagesubjects + "List of Items", new ItemStack(Material.DIAMOND_SWORD, 1), false, ColorOptions.message + "A list of almost all items", ColorOptions.message + "that can be found in the game", ColorOptions.message + "along with their specifications!"));
		
		menu = this.fillEmptyMenu(menu, false, " ", "");
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openTutorialMenu(User user)
	{
		Tutorial tutorial = new Tutorial();
		Integer size = tutorial.file.getTutorialNames().size();
		Inventory menu = Bukkit.createInventory(null, main.getMenuSize(size)+9, Menus.TutorialMenu);
		
		menu.setItem(0, Menus.getSocialProfile(user));
		menu.setItem(4, product.createItem(ChatColor.BLUE + "List of tutorials", 
				new ItemStack(Material.LEASH, 1),
				false,
				ColorOptions.message + "Having difficulties with playing?",
				ColorOptions.message + "Find and start any tutorial",
				ColorOptions.message + "to understand the game better!"));
		menu.setItem(8, Menus.getBackButton(Menus.SupportMenu));
		
		int slot = 10;
		for (String tutorialName : tutorial.file.getTutorialNames())
		{
			ItemStack item = product.createItem(ColorOptions.statsresults + tutorialName + " Tutorial", new ItemStack(Material.STRING, 1), false, null);
			List<String> description = product.getStringLines(tutorial.file.getDescription(tutorialName), 4, ColorOptions.message);
			
			description.add("");
			if (tutorial.file.containsUUID(user.getUUID(), tutorialName))
			{
				description.add(ColorOptions.messageachievement + "Completed on " + tutorial.file.getCompleteDate(user.getPlayer(), tutorialName));
			} else
			{
				description.addAll(Arrays.asList(ColorOptions.message + "Complete this tutorial to receive ", ColorOptions.gemStats + "" + tutorial.file.getReward(tutorialName) + " Gems" + ColorOptions.message + " and " + ColorOptions.statsresults + tutorial.file.getExperienceReward(tutorialName) + " Experience"));
			}
			item = product.addItemDescription(item, description);
			
			menu.setItem(slot, item);
			slot++;
		}
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openAssignmentSubMenu(User user)
	{
		Inventory menu = Bukkit.createInventory(null, 3*9, Menus.AssignmentSubMenu);
		
		menu.setItem(0, Menus.getSocialProfile(user));
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.messagesubjects + "Assignments and Achievements", Material.MAP, ColorOptions.message + "Choose to see your", ColorOptions.message + "Daily Assignments and Missions", "", ColorOptions.message + "or check your progress on", ColorOptions.message + "the Achievements"));
		menu.setItem(8, Menus.getBackButton(Menus.PersonalMenu));
		
		menu.setItem(11, product.createItem(Menus.AssignmentMenu, new ItemStack(Material.EMPTY_MAP, 1), false, ColorOptions.message + "Check the progress of your" , ColorOptions.message + "Daily Assignments", ColorOptions.message + "and collect the rewards!"));
		menu.setItem(13, product.createItem(Menus.QuestMenu, new ItemStack(Material.EMPTY_MAP, 1), false, ColorOptions.message + "Check the progress of your", ColorOptions.message + "Quests received by shopkeepers"));
		menu.setItem(15, product.createItem(Menus.AchievementMenu, new ItemStack(Material.BOOK, 1), false, ColorOptions.message + "A list of all achievements", ColorOptions.message + "and their rewards that", ColorOptions.message + "can be collected"));
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openItemListMenu(User user, int pageNumber, int categoryID, int itemBlinkSlot)
	{
		String filterName = (categoryID != -1) ? this.productcat.getCategoryName(categoryID) : "All";
		boolean activeFilter = (categoryID != -1) ? true : false;
		List<Integer> productList = new ArrayList<Integer>();
		List<Integer> list = this.product.getIDList(true, categoryID, true);
		Integer pageAmount = Math.round(((float) list.size()/45));
		if (pageAmount == 0)
		{
			pageAmount = 1;
		}
		if (list.size() > 45)
		{
			Integer startingindex = 0;
			switch(pageNumber)
			{
			case 2: startingindex = 46;
			break;
			case 3: startingindex = 91;
			break;
			case 4: startingindex = 136;
			break;
			case 5: startingindex = 181;
			break;
			case 6: startingindex = 226;
			break;
			case 7: startingindex = 271;
			break;
			case 8: startingindex = 316;
			break;
			case 9: startingindex = 361;
			break;
			case 10: startingindex = 406;
			break;
			default: startingindex = 0;
			break;
			}
			
			for (int i = startingindex; i < startingindex+45; i++)
			{
				if (list.size() > i)
				{
					productList.add(list.get(i));
				} else
				{
					break;
				}
			}
		} else
		{
			productList.addAll(list);
		}
		
		Inventory menu =  Bukkit.createInventory(null, main.getMenuSize(productList.size()), Menus.ItemListMenu);
		
		menu.setItem(0, Menus.getSocialProfile(user));
		menu.setItem(1, this.product.createItem(ColorOptions.messagesubjects + "Previous", new ItemStack(Material.ARROW, 1), false, "", ColorOptions.message + "Page: " + pageNumber + "/" + pageAmount));
		menu.setItem(2, this.product.createItem(ColorOptions.message + "Filter: " + ColorOptions.messagesubjects + filterName, new ItemStack(((categoryID != -1) ? Material.WATER_BUCKET : Material.BUCKET), 1), activeFilter, ""));
		menu.setItem(4, this.product.createItem(ColorOptions.messagesubjects + "List of Items", new ItemStack(Material.DIAMOND_SWORD), false, ColorOptions.message + "A list of all items", ColorOptions.message + "that currently exist in the game", ColorOptions.message + "along with their specifications", "", ColorOptions.message + "Total items: " + list.size()));
		menu.setItem(7, this.product.createItem(ColorOptions.messagesubjects + "Next", new ItemStack(Material.ARROW, 1), false, "", ColorOptions.message + "Page: " + pageNumber + "/" + pageAmount));
		menu.setItem(8, Menus.getBackButton(Menus.SupportMenu));
		
		Integer slot = 9;
		for (Integer productID : productList)
		{
			ItemStack item = this.product.createPropertyItem(productID, 1, false, true);
			menu.setItem(slot, item);
			slot++;
		}
		menu = this.fillEmptyMenu(menu, false, " ", "");
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openQuestRequest(User user)
	{
		Inventory menu = Bukkit.createInventory(null, 3*9, Menus.AssignmentSubMenu);
		
		menu.setItem(0, Menus.getSocialProfile(user));
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.messagesubjects + "Assignments and Achievements", Material.MAP, ColorOptions.message + "Choose to see your", ColorOptions.message + "Daily Assignments and Missions", "", ColorOptions.message + "or check your progress on", ColorOptions.message + "the Achievements"));
		menu.setItem(8, Menus.getBackButton(Menus.PersonalMenu));
		
		menu.setItem(12, product.createItem(ColorOptions.messagesubjects + "Daily Assignments & Missions", new ItemStack(Material.EMPTY_MAP, 1), false, ColorOptions.message + "Check the progress of your" , ColorOptions.message + "Daily Assignments and Missions", ColorOptions.message + "and collect the rewards!"));

		menu.setItem(14, product.createItem(ColorOptions.messageformat + "Achievements", new ItemStack(Material.BOOK, 1), false, ColorOptions.message + "A list of all achievements", ColorOptions.message + "and their rewards that", ColorOptions.message + "can be collected"));
		
		menu = this.fillEmptyMenu(menu, false, " ", "");
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openQuestDeliverMenu(User user, Quest quest)
	{
		Inventory menu = Bukkit.createInventory(null, 4*9, Menus.QuestDeliverMenu);
		
		menu.setItem(0, Menus.getSocialProfile(user));
		menu.setItem(3, MenuClick.addmenulist(ColorOptions.stats + "Deliver items", Material.CHEST, ColorOptions.message + "Click here to deliver the items", "", ColorOptions.stats + "Total delivered: " + ColorOptions.statsresults + quest.getDeliveredAmount() + "/" + quest.getGoalAmount(), ColorOptions.stats + "Current delivery: " + ColorOptions.statsresults + "0"));
		menu.setItem(4, MenuClick.addmenulist(ColorOptions.messagesubjects + "Quest: " + ColorOptions.message + quest.getDescription(), Material.MAP, ColorOptions.message + "Drag the requested items", ColorOptions.message + "into this menu and click the", ColorOptions.message + "chest-item on the left"));
		menu.setItem(5, Menus.getPropertyInfo((quest instanceof QuestDeliverPackage) ? ((QuestDeliverPackage)quest).getTargetPropertyID() : quest.getPropertyID()));
		menu.setItem(8, Menus.getBackButton(null));

		menu = this.fillEmptyMenu(menu, false, " ", "");
		
    	for (int i = 19; i < 26; i++)
    	{
    		menu.setItem(i, new ItemStack(Material.AIR, 1));
    	}
    	
    	user.getPlayer().openInventory(menu);
	}
	
	public void openGateManager(User user)
	{
		Integer activeGates = Gates.Gates.gates.size();
		Inventory menu = Bukkit.createInventory(null, main.getMenuSize(activeGates), Menus.GateManagerMenu);
		
		menu.setItem(4, product.createItem(ColorOptions.stats + "Gate Manager", new ItemStack(Material.FENCE), false, ColorOptions.message + "You can edit all active gates", ColorOptions.message + "Click a gate to edit its properties", "", ColorOptions.message + "Tip: Activate gates by entering", ColorOptions.message + "the town they belong to", "", ColorOptions.stats + "Active: " + ColorOptions.statsresults + activeGates));
		menu.setItem(8, Menus.getBackButton(Menus.PersonalMenu));
		
		int slot = 9;
		for (Gate gate : Gates.Gates.gates)
		{
			ItemStack material = product.createPropertyItem(gate.getMaterialID(), 1, false, false);
			ItemStack gateItem = product.createItem(ColorOptions.messagesubjects + gate.getName(), material, false, 
					ColorOptions.stats + "ID: " + ColorOptions.statsresults + gate.getID(),
					ColorOptions.statsformat + "Location:",
					ColorOptions.stats + "Town: " + ColorOptions.statsresults + town.getTownName(gate.getTownID()),
					ColorOptions.stats + "Street: " + ColorOptions.statsresults + street.getStreetName(gate.getStreetID()),
					ColorOptions.stats + "Facing: " + ColorOptions.statsresults + gate.getFaceDirection(),
					"",
					ColorOptions.stats + "Health: " + ColorOptions.statsresults + gate.getHealth(),
					"",
					ColorOptions.message + "Closed: " + (gate.getClosed() ? ColorOptions.error + "Closed" : ColorOptions.messageachievement + "Opened")
					);
			
			menu.setItem(slot, gateItem);
			slot++;
		}
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openGateInformation(User user, Gate gate)
	{
		Inventory menu = Bukkit.createInventory(null, 3*9, ColorOptions.KAKColor + "Gate information");
		
		ItemStack gateMaterial = product.createPropertyItem(gate.getMaterialID(), 1, false, false);
		ItemStack gateItem = product.createItem(ColorOptions.messagesubjects + gate.getName(), gateMaterial, false, 
				ColorOptions.stats + "ID: " + ColorOptions.statsresults + gate.getID(),
				ColorOptions.statsformat + "Location:",
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + town.getTownName(gate.getTownID()),
				ColorOptions.stats + "Street: " + ColorOptions.statsresults + street.getStreetName(gate.getStreetID()),
				ColorOptions.stats + "Facing: " + ColorOptions.statsresults + gate.getFaceDirection()
				);
		
		menu.setItem(4, gateItem);
		menu.setItem(7, product.createItem(ColorOptions.messageformat + "Save changes", new ItemStack(Material.BOOK_AND_QUILL), false, ColorOptions.message + "Click here to save the changes", ColorOptions.message + "to the Database"));
		menu.setItem(8, Menus.getBackButton(Menus.GateManagerMenu));
		
		menu.setItem(9, product.createItem(ColorOptions.messageachievement + "Add max. health", new ItemStack(Material.STAINED_CLAY, 1, (short) 13), false));
		menu.setItem(10, product.createItem(ColorOptions.message + "Max. health: " + ChatColor.LIGHT_PURPLE + gate.getOriginalHealth(), new ItemStack(Material.GOLDEN_APPLE, 1), false, ColorOptions.message + "Steps: 10"));
		menu.setItem(11, product.createItem(ColorOptions.error + "Remove max. health", new ItemStack(Material.STAINED_CLAY, 1, (short) 14), false));
		
		menu.setItem(13, product.createItem(ColorOptions.message + "Current name: " + ColorOptions.KAKColor + gate.getName(), new ItemStack(Material.SIGN), false, ColorOptions.message + "Click here to change the name"));
		
		menu.setItem(16, product.createItem(ColorOptions.message + "Gate status: " + (gate.getClosed() ? ColorOptions.error + "Closed" : ColorOptions.messageachievement + "Opened"), new ItemStack(Material.LEVER), false, ColorOptions.message + "Click here to toggle the gate"));
		
		menu.setItem(18, product.createItem(ColorOptions.messageachievement + "Add current health", new ItemStack(Material.STAINED_CLAY, 1, (short) 13), false));
		menu.setItem(19, product.createItem(ColorOptions.message + "Current health: " + ChatColor.LIGHT_PURPLE + gate.getHealth(), new ItemStack(Material.APPLE, 1), false, ColorOptions.message + "Steps: 10"));
		menu.setItem(20, product.createItem(ColorOptions.error + "Remove current health", new ItemStack(Material.STAINED_CLAY, 1, (short) 14), false));
		
		menu.setItem(22, product.createItem(ColorOptions.message + "Material: " + product.getDisplayName(gate.getMaterialID(), false), gateMaterial, false, ColorOptions.message + "Click here to change the", ColorOptions.message + "gate's material"));
		
		menu.setItem(25, product.createItem(ColorOptions.message + "Destroyed: " + (gate.getDestroyed() ? ColorOptions.error + "Destroyed" : ColorOptions.messageachievement + "Intact"), new ItemStack(Material.LEVER), false, ColorOptions.message + "Toggle between destroyed and intact"));
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openGateMaterialMenu(User user, Gate gate)
	{
		CopyOnWriteArrayList<Integer> IDList = new CopyOnWriteArrayList<Integer>(product.getIDListbyCategory("resources", false));
		
		for (Integer materialID : IDList)
		{
			ItemStack item = product.createPropertyItem(materialID, 1, false, false);
			if (!item.getType().isBlock())
			{
				IDList.remove(materialID);
			}
		}
		
		Inventory menu = Bukkit.createInventory(null, main.getMenuSize(IDList.size()), ColorOptions.KAKColor + "Choose gate material");
		
		ItemStack gateMaterial = product.createPropertyItem(gate.getMaterialID(), 1, false, false);
		ItemStack gateItem = product.createItem(ColorOptions.messagesubjects + gate.getName(), gateMaterial, false, 
				ColorOptions.stats + "ID: " + ColorOptions.statsresults + gate.getID(),
				ColorOptions.statsformat + "Location:",
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + town.getTownName(gate.getTownID()),
				ColorOptions.stats + "Street: " + ColorOptions.statsresults + street.getStreetName(gate.getStreetID()),
				ColorOptions.stats + "Facing: " + ColorOptions.statsresults + gate.getFaceDirection()
				);
		
		menu.setItem(4, gateItem);
		menu.setItem(8, Menus.getBackButton(ColorOptions.KAKColor + "Gate information"));
		
		int slot = 9;
		for (Integer materialID : IDList)
		{
			ItemStack material = product.createPropertyItem(materialID, 1, false, false);
			material = product.addItemDescription(material, Arrays.asList("", ColorOptions.message + "Click here to select material"));
			
			menu.setItem(slot, material);
			slot++;
		}
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openEventManager(User user)
	{
		Inventory menu = Bukkit.createInventory(null, 2*9, Menus.EventManagerMenu);
		
		menu.setItem(0, product.createItem(ColorOptions.statsformat + "Relevant statistics", new ItemStack(Material.SIGN), false, ColorOptions.stats + "Online players: " + ColorOptions.statsresults + Bukkit.getOnlinePlayers().size()));
		menu.setItem(4, product.createItem(ColorOptions.stats + "Event Manager", new ItemStack(Material.CAKE), false, ColorOptions.message + "Manage all active events", ColorOptions.message + "or edit minigame scenarios"));
		menu.setItem(8, Menus.getBackButton(Menus.PersonalMenu));
		
		menu.setItem(12, product.createItem(ColorOptions.messagesubjects + "Hide And Seek", new ItemStack(Material.LEAVES), false, ColorOptions.message + "Click here to manage"));
		menu.setItem(14, product.createItem(ColorOptions.coinStats + "Siege", new ItemStack(Material.BANNER), false, ColorOptions.message + "Click here to manage", ColorOptions.message + "Active Siege games or", ColorOptions.message + "edit scenarios"));
	
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openHideAndSeekManager(User user)
	{
		HideAndSeek hs = main.HideAndSeek;		
		String status = "Cooldown";
		Inventory menu = Bukkit.createInventory(null, 3*9+main.getMenuSize(hs.getParticipants().size()), Menus.HideAndSeekManagerMenu);
		
		ItemStack hsItem = product.createItem(ColorOptions.messagesubjects + "Information about Hide and Seek", new ItemStack(Material.LEAVES), false);
		List<String> description = new ArrayList<String>(Arrays.asList(
				ColorOptions.message + "Edit properties or participants",
				ColorOptions.message + "of the current Hide and Seek"
				));
		
		if (hs.getCooldown())
		{
			if (!hs.getLastWinners().isEmpty())
			{
				description.addAll(Arrays.asList(
						"",
						ColorOptions.message + "Winners of the previous game:"
						));
				for (Participant winner : hs.getLastWinners())
				{
					User userWinner = winner.getUser();
					description.add(ColorOptions.messagesubjects + userWinner.getUsername());
				}
			}

		}
		if (hs.getMatchmaking())
		{
			status = "Matchmaking";
			description.addAll(Arrays.asList(
					"",
					ColorOptions.stats + "Participants: " + hs.getParticipants().size()
					));			
		}
		if (hs.getProgress())
		{
			status = "In progress";
			description.addAll(Arrays.asList(
					ColorOptions.stats + "Hiders: " + hs.getHiderAmount(),
					ColorOptions.stats + "Seekers: " + hs.getSeekers().size()
					));
		}
		
		hsItem = product.addItemDescription(hsItem, description);
		
		menu.setItem(4, hsItem);
		menu.setItem(8, Menus.getBackButton(Menus.EventManagerMenu));
		
		menu.setItem(9, product.createItem(ColorOptions.messageachievement + "Increase reward", new ItemStack(Material.STAINED_CLAY, 1, (short) 13), false));
		menu.setItem(10, product.createItem(ColorOptions.message + "Reward: " + ChatColor.YELLOW + hs.getReward(), new ItemStack(Material.GOLD_INGOT, 1), false, ColorOptions.message + "Steps: 250"));
		menu.setItem(11, product.createItem(ColorOptions.error + "Decrease reward", new ItemStack(Material.STAINED_CLAY, 1, (short) 14), false));
		
		ItemStack hsStatus = product.createItem(ColorOptions.message + "Current stage: " + ColorOptions.messagesubjects + status, new ItemStack(Material.SIGN), false);
		ItemStack skipItem = product.createItem(ColorOptions.messagesubjects + "Next stage", new ItemStack(Material.ARROW), false, ColorOptions.message + "Click to skip the current stage");
		ItemStack locationItem = product.createItem(ColorOptions.message + "Location: " + (hs.getTownID() != null ? ColorOptions.messagesubjects + hs.getTownName() : ColorOptions.error + "Not set"), new ItemStack(Material.COMPASS), false, ColorOptions.message + "Click to change location", ColorOptions.message + "Cycles through the town list");
		
		List<String> statusDesc = new ArrayList<String>();
		if (hs.getCooldown())
		{
			HashMap<String, Integer> calcTime = main.getCalculatedTime(hs.getCooldownSeconds());
			statusDesc.addAll(Arrays.asList(
					"",
					ColorOptions.message + "Time until matchmaking:",
					ColorOptions.message + "" + calcTime.get("minute") + " minutes and " + calcTime.get("second") + " seconds"
					));
			skipItem = product.addItemDescription(skipItem, Arrays.asList(
					"",
					ColorOptions.message + "Skipping to: Matchmaking"
					));
		} else
		if (hs.getMatchmaking())
		{
			HashMap<String, Integer> calcTime = main.getCalculatedTime(hs.getMatchmakingSeconds());
			statusDesc.addAll(Arrays.asList(
					"",
					ColorOptions.message + "Time until game starts:",
					ColorOptions.message + "" + calcTime.get("minute") + " minutes and " + calcTime.get("second") + " seconds"
					));
			
			skipItem = product.addItemDescription(skipItem, Arrays.asList(
					"",
					ColorOptions.message + "Skipping to: Game start"
					));
			if (hs.getParticipants().size() < 2)
			{
				skipItem = product.addItemDescription(skipItem, Arrays.asList(
						ColorOptions.error + "There are not enough",
						ColorOptions.error + "participants to start!"
						));
			}
		} else
		if (hs.getProgress())
		{
			HashMap<String, Integer> calcTime = main.getCalculatedTime(hs.getProgressSeconds());
			statusDesc.addAll(Arrays.asList(
					"",
					ColorOptions.message + "Time until game ends:",
					ColorOptions.message + "" + calcTime.get("minute") + " minutes and " + calcTime.get("second") + " seconds"
					));
			
			skipItem = product.addItemDescription(skipItem, Arrays.asList(
					"",
					ColorOptions.message + "Skipping to: Game end"
					));
			locationItem = product.addItemDescription(locationItem, Arrays.asList(
					"",
					ColorOptions.error + "Can't change while in progress"
					));
		}
		hsStatus = product.addItemDescription(hsStatus, statusDesc);

		menu.setItem(12, product.createItem(ChatColor.GOLD + "Send to hub", new ItemStack(Material.COMPASS), false, ColorOptions.message + "Click to teleport", ColorOptions.message + "all players to", ColorOptions.message + "the hub of this town"));
		menu.setItem(13, hsStatus);
		menu.setItem(14, skipItem);
		menu.setItem(15, locationItem);
		menu.setItem(16, product.createItem(ColorOptions.message + "Auto-start: " + (hs.getAutostart() ? ColorOptions.messagesubjects + "On" : ColorOptions.error + "Off"), new ItemStack(Material.BEACON), hs.getAutostart(), ColorOptions.message + "Click to toggle"));
		
		for (int i = 18; i < 27; i++)
		{
			menu.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), null));
		}
		
		ItemStack playerItem = product.createItem(ColorOptions.messagesubjects + "Participants manager", new ItemStack(Material.SKULL_ITEM), false, ColorOptions.message + "Manage the participants", ColorOptions.message + "of the current game");
		if (hs.getParticipants().isEmpty() || hs.getParticipants().size() == 0)
		{
			playerItem = product.addItemDescription(playerItem, Arrays.asList(
					"",
					ColorOptions.error + "No participants to manage yet"
					));
			
			for (int i = 36; i < menu.getSize(); i++)
			{
				menu.setItem(i, product.createItem(ColorOptions.error + "No participants", new ItemStack(Material.BARRIER), false));
			}
		} else
		{
			Integer index = 0;
			for (int i = 36; i < menu.getSize(); i++)
			{
				if (index >= hs.getParticipants().size())
				{
					break;
				}
				Participant participant = hs.getParticipants().get(index);
				User userParticipant = participant.getUser();
		    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
				skullMeta.setOwner(userParticipant.getUsername());
				skull = product.setItemDescription(skull, 1, ColorOptions.message + "Player: " + ColorOptions.messagesubjects + userParticipant.getUsername(), null);
				List<String> skullDescription = new ArrayList<String>();
				
				if (hs.getHiders().contains(participant))
				{
					skullDescription.add(ColorOptions.messagesubjects + "Hider");
				} else if (hs.getSeekers().contains(participant))
				{
					skullDescription.add(ColorOptions.error + "Seeker");
				}
				skullDescription.addAll(Arrays.asList(
						"",
						ColorOptions.message + "Click here to manage"
						));
				
				skull = product.addItemDescription(skull, skullDescription);
				
				menu.setItem(i, skull);
				index++;
			}
		}
		
		menu.setItem(31, playerItem);

		if (hs.getParticipants().size() > 18)
		{
			menu.setItem(27, product.createItem(ColorOptions.messagesubjects + "Previous tab", new ItemStack(Material.ARROW), false, ColorOptions.message + "Click to go to the previous", ColorOptions.message + "tab of participants"));
			menu.setItem(35, product.createItem(ColorOptions.messagesubjects + "Next tab", new ItemStack(Material.ARROW), false, ColorOptions.message + "Click to go to the next", ColorOptions.message + "tab of participants"));
		}
		
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().closeInventory();
		user.getPlayer().openInventory(menu);
		
	}
	
	public void openHsPlayerManager(User user, User target)
	{
		HideAndSeek hs = main.HideAndSeek;		

		Inventory menu = Bukkit.createInventory(null, 3*9, ColorOptions.message + "Hide and Seek player");
		
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(target.getUsername());
		skull = product.setItemDescription(skull, 1, ColorOptions.message + "Player: " + ColorOptions.messagesubjects + target.getUsername(), null);
		List<String> skullDescription = new ArrayList<String>();
		
		if (hs.getHiders().contains(target))
		{
			skullDescription.add(ColorOptions.messagesubjects + "Hider");
		} else if (hs.getSeekers().contains(target))
		{
			skullDescription.add(ColorOptions.error + "Seeker");
		}
		
		skull = product.addItemDescription(skull, skullDescription);
		
		menu.setItem(4, skull);
		menu.setItem(8, Menus.getBackButton(Menus.HideAndSeekManagerMenu));
		
		ItemStack switchItem = product.createItem(ColorOptions.messageachievement + "Switch role", new ItemStack(Material.LEAVES), false);
		List<String> switchDescription = new ArrayList<String>();
		
		if (hs.getHiders().contains(target))
		{
			switchDescription.addAll(Arrays.asList(
					ColorOptions.message + "Current role: " + ColorOptions.messagesubjects + "Hider",
					"",
					ColorOptions.message + "Click to switch to " + ColorOptions.error + "Seeker"
					));
		} else
		{
			switchDescription.addAll(Arrays.asList(
					ColorOptions.message + "Current role: " + ColorOptions.error + "Seeker",
					"",
					ColorOptions.message + "Click to switch to " + ColorOptions.messagesubjects + "Hider"
					));
		}
		if (!hs.getProgress())
		{
			switchDescription.add(ColorOptions.error + "Can only be changed while in progress");
		}
		switchItem = product.addItemDescription(switchItem, switchDescription);
		
		menu.setItem(11, switchItem);
		menu.setItem(12, product.createItem(ColorOptions.error + "Kick", new ItemStack(Material.BARRIER), false, ColorOptions.message + "Click to kick from", ColorOptions.message + "Hide and Seek"));
		menu.setItem(13, product.createItem(ColorOptions.KAKColor + "Spectate", new ItemStack(Material.COMPASS), false, ColorOptions.message + "Click to teleport", ColorOptions.message + "to this player"));
		menu.setItem(14, product.createItem(ChatColor.GOLD + "Send to hub", new ItemStack(Material.COMPASS), false, ColorOptions.message + "Click to teleport", ColorOptions.message + "this player to", ColorOptions.message + "the hub of this town"));
		menu.setItem(15, product.createItem(ColorOptions.coinStats + "Hint seekers", new ItemStack(Material.SIGN), false, ColorOptions.message + "Click to notify seekers", ColorOptions.message + "a streetname near the", ColorOptions.message + "location of this player"));
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openSiegeManager(User user)
	{
		List<Siege> siegeList = Sieges.Sieges;
		List<Integer> scenarioList = Scenarios.getIDList();
		
		if (!scenarioList.isEmpty())
		{
			Scenarios.instantiateAll();
		}
		
		Inventory menu = Bukkit.createInventory(null, 3*9+main.getMenuSize(scenarioList.size()), Menus.SiegeManagerMenu);
		
		ItemStack siegeItem = product.createItem(ColorOptions.messagesubjects + "Manage Sieges and Scenarios", new ItemStack(Material.BANNER), false,
				ColorOptions.message + "Edit current sieges or",
				ColorOptions.message + "edit properties of scenarios",
				"",
				ColorOptions.message + "Current amount of scenarios: " + ColorOptions.messagesubjects + scenarioList.size(),
				ColorOptions.message + "Current amount of sieges: " + ColorOptions.messagesubjects + siegeList.size());
		
		menu.setItem(4, siegeItem);
		menu.setItem(8, Menus.getBackButton(Menus.EventManagerMenu));
		
		for (int i = 9; i < 18; i++)
		{
			menu.setItem(i, product.createItem(ColorOptions.error + "No siege", new ItemStack(Material.BARRIER), false, ColorOptions.message + "No active siege has started"));
		}
		Integer siegeSlot = 9;
		for (Siege sieges : siegeList)
		{
			menu.setItem(siegeSlot, product.createItem(ColorOptions.message + "Siege " + ColorOptions.messagesubjects + (siegeList.indexOf(sieges)+1), new ItemStack(Material.BANNER, 1, (short) 1), false, ColorOptions.message + "Click here to edit"));
			siegeSlot++;
		}
		
		for (int i = 18; i < 27; i++)
		{
			menu.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), null));
		}
		
		ItemStack scenariosItem = product.createItem(ColorOptions.messagesubjects + "Scenario manager", new ItemStack(Material.BANNER), false, ColorOptions.message + "Manage the siege", ColorOptions.message + "scenarios");
	
		if (Sieges.Scenarios.isEmpty() || Sieges.Scenarios.size() == 0)
		{
			scenariosItem = product.addItemDescription(scenariosItem, Arrays.asList(
					"",
					ColorOptions.error + "No scenarios to manage yet",
					ColorOptions.message + "Create one with /scenario"
					));
			
			for (int i = 36; i < menu.getSize(); i++)
			{
				menu.setItem(i, product.createItem(ColorOptions.error + "No scenario", new ItemStack(Material.BARRIER), false, ColorOptions.message + "Create one with /scenario"));
			}
		} else
		{
			Integer index = 0;
			for (int i = 36; i < menu.getSize(); i++)
			{
				if (index >= Sieges.Scenarios.size())
				{
					break;
				}
				Scenario scenario = Sieges.Scenarios.get(index);
		    	ItemStack scenarioItem = product.createItem(
		    			ColorOptions.message + "Scenario: " + ColorOptions.messagesubjects + scenario.getID(), 
		    			new ItemStack(Material.BANNER, 1, (short) 4), 
		    			false, 
		    			ColorOptions.stats + "Name: " + ColorOptions.statsresults + scenario.getName(),
		    			ColorOptions.stats + "Town: " + ColorOptions.statsresults + scenario.getTownName(),
		    			ColorOptions.stats + "Min. players: " + ColorOptions.statsresults + scenario.getPlayersMin(),
		    			ColorOptions.stats + "Max. players: " + ColorOptions.statsresults + scenario.getPlayersMax(),
		    			""
		    			);
				List<String> scenarioDesc = new ArrayList<String>();
				
				if (scenario.getActive())
				{
					scenarioDesc.add(ColorOptions.messagesubjects + "Active in a siege");
				}
				scenarioDesc.add(ColorOptions.message + "Click to manage");
				
				scenarioItem = product.addItemDescription(scenarioItem, scenarioDesc);
				
				menu.setItem(i, scenarioItem);
				index++;
			}
		}
		
		menu.setItem(31, scenariosItem);
		
		if (Sieges.Scenarios.size() > 18)
		{
			menu.setItem(27, product.createItem(ColorOptions.messagesubjects + "Previous tab", new ItemStack(Material.ARROW), false, ColorOptions.message + "Click to go to the previous", ColorOptions.message + "tab of scenarios"));
			menu.setItem(35, product.createItem(ColorOptions.messagesubjects + "Next tab", new ItemStack(Material.ARROW), false, ColorOptions.message + "Click to go to the next", ColorOptions.message + "tab of scenarios"));
		}
		
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().closeInventory();
		user.getPlayer().openInventory(menu);
	}
	
	public void openScenarioManager(User user, Scenario scenario)
	{
		Inventory menu = Bukkit.createInventory(null, 4*9+main.getMenuSize(scenario.getSideObjectives().size()), Menus.ScenarioManagerMenu);
		
		ItemStack scenarioItem = product.createItem(ColorOptions.message + "Scenario " + ColorOptions.messagesubjects + scenario.getName(), new ItemStack(Material.BANNER, 1), false, 
				ColorOptions.stats + "ID: " + ColorOptions.statsresults + scenario.getID(),
				ColorOptions.stats + "TownID: " + ColorOptions.statsresults + scenario.getTownID(),
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + scenario.getTownName(),
				ColorOptions.stats + "Entrytitle: " + ColorOptions.statsresults + scenario.getEntryTitle(),
				"",
				ColorOptions.stats + "Spawnpoints " + scenario.getTeam1Color() + "Team 1: " + (scenario.getTeam1Spawnpoints().isEmpty() ? ColorOptions.error + "Not set" : ColorOptions.messagesubjects + "Set"),
				ColorOptions.stats + "Spawnpoints " + scenario.getTeam2Color() + "Team 2: " + (scenario.getTeam2Spawnpoints().isEmpty() ? ColorOptions.error + "Not set" : ColorOptions.messagesubjects + "Set"),
				ColorOptions.stats + "Main objective: " + (scenario.getMainObjectiveID() != null ? ColorOptions.messagesubjects + "Set" : ColorOptions.error + "Not set"),
				ColorOptions.stats + "Side objectives: " + ColorOptions.messagesubjects + scenario.getSideObjectives().size()
				);
		
		if (scenario.getTeam1Spawnpoints().isEmpty() || scenario.getTeam2Spawnpoints().isEmpty() || scenario.getMainObjectiveID() == null)
		{
			scenarioItem = product.addItemDescription(scenarioItem, Arrays.asList(
					"",
					ColorOptions.error + "Scenario can't activate due to",
					ColorOptions.error + "Missing spawnpoints or",
					ColorOptions.error + "main objective"
					));
		}
		
		ItemStack sideObjectivesItem = product.createItem(ColorOptions.messageachievement + "Side Objectives", new ItemStack(Material.BANNER, 1, (short) 15), false, 
				ColorOptions.message + "A list of all Side Objectives",
				ColorOptions.message + "Side Objectives grant bonusses to",
				ColorOptions.message + "the attacking team when destroyed",
				ColorOptions.message + "or the defending team when intact",
				"",
				ColorOptions.message + "You can edit them here");
		
		if (scenario.getSideObjectives() == null || scenario.getSideObjectives().isEmpty())
		{
			sideObjectivesItem = product.addItemDescription(sideObjectivesItem, Arrays.asList(
					ColorOptions.error + "No Side Objectives created yet"
					));
		} else
		{
			sideObjectivesItem = product.addItemDescription(sideObjectivesItem, Arrays.asList(
					ColorOptions.message + "Current amount: " + ColorOptions.messagesubjects + scenario.getSideObjectives().size()
					));
		}
		
		menu.setItem(0, product.createItem(ColorOptions.messageachievement + "Test Scenario", new ItemStack(Material.DIAMOND_SWORD), false, ColorOptions.message + "Click to test", ColorOptions.message + "the behaviour of this", ColorOptions.message + "scenario during siege"));
		menu.setItem(4, scenarioItem);
		menu.setItem(6, product.createItem(ChatColor.GOLD + "Save changes", new ItemStack(Material.BOOK_AND_QUILL), false, ColorOptions.message + "Save changes to Database"));
		menu.setItem(7, product.createItem(ColorOptions.error + "Delete scenario", new ItemStack(Material.LAVA_BUCKET), false, ColorOptions.message + "Delete scenario from Database"));
		menu.setItem(8, Menus.getBackButton(Menus.SiegeManagerMenu));
		
		menu.setItem(9, product.createItem(ColorOptions.messagesubjects + "Add min. players", new ItemStack(Material.STAINED_CLAY, 1, (short) 13), false));
		menu.setItem(18, product.createItem(ColorOptions.message + "Min. players: " + ColorOptions.messagesubjects + scenario.getPlayersMin(), new ItemStack(Material.SKULL_ITEM), false, ColorOptions.message + "Step: 1"));
		menu.setItem(27, product.createItem(ColorOptions.error + "Remove min. players", new ItemStack(Material.STAINED_CLAY, 1, (short) 14), false));
		
		menu.setItem(11, product.createItem(ColorOptions.messagesubjects + "Add max. players", new ItemStack(Material.STAINED_CLAY, 1, (short) 13), false));
		menu.setItem(20, product.createItem(ColorOptions.message + "Max. players: " + ColorOptions.messagesubjects + scenario.getPlayersMax(), new ItemStack(Material.SKULL_ITEM), false, ColorOptions.message + "Step: 1"));
		menu.setItem(29, product.createItem(ColorOptions.error + "Remove max. players", new ItemStack(Material.STAINED_CLAY, 1, (short) 14), false));
		
		menu.setItem(13, product.createItem(ColorOptions.messagesubjects + "Change location", new ItemStack(Material.STAINED_CLAY, 1, (short) 13), false));
		menu.setItem(22, product.createItem(ColorOptions.KAKColor + "Main Objective", new ItemStack(Material.BANNER, 1, (short) 11), false, ColorOptions.message + "Click to teleport"));
		menu.setItem(31, product.createItem(ColorOptions.messageachievement + "Test Main Objective", new ItemStack(Material.DIAMOND_SWORD), false, ColorOptions.message + "Click to test", ColorOptions.message + "the behaviour of this", ColorOptions.message + "Object during siege"));
		
		menu.setItem(24, product.createItem(ColorOptions.KAKColor + "Spawnpoints " + scenario.getTeam1Color() + "Team 1", new ItemStack(Material.BANNER, 1, (short) 4), false, ColorOptions.message + "Click to manage"));
		
		menu.setItem(26, product.createItem(ColorOptions.KAKColor + "Spawnpoint: " + scenario.getTeam2Color() + "Team 2", new ItemStack(Material.BANNER, 1, (short) 1), false, ColorOptions.message + "Click to manage"));

		menu.setItem(40, sideObjectivesItem);
		
		Integer slot = 45;
		for (SideObjective sideObjective : scenario.getSideObjectives())
		{
			if (slot > menu.getSize())
			{
				break;
			}
			menu.setItem(slot, product.createItem(ColorOptions.messageachievement + "Side Objective " + (scenario.getSideObjectives().indexOf(sideObjective)+1), new ItemStack(Material.BANNER, 1, (short) 15), false, ColorOptions.message + "Click to manage"));
			slot++;
		}
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().closeInventory();
		user.getPlayer().openInventory(menu);
	}
	
	public void openSideObjectiveManager(User user, SideObjective objective, Scenario scenario)
	{
		Inventory menu = Bukkit.createInventory(null, 3*9, Menus.SideObjectiveManagerMenu);
		
		menu.setItem(4, product.createItem(ColorOptions.messageachievement + "Side Objective " + (scenario.getSideObjectives().indexOf(objective)+1), new ItemStack(Material.BANNER, 1, (short) 15), false, ColorOptions.stats + "Scenario: " + ColorOptions.statsresults + scenario.getID()));
		menu.setItem(7, product.createItem(ColorOptions.error + "Delete Side Objective", new ItemStack(Material.LAVA_BUCKET), false, ColorOptions.message + "Delete from Scenario"));
		menu.setItem(8, Menus.getBackButton(Menus.SiegeManagerMenu));
		
		
		ItemStack material = (objective.getGateID() != -1 ? product.createPropertyItem(objective.fetchGate().getMaterialID(), 1, false, false) : new ItemStack(Material.FENCE));
		ItemStack gateItem = product.createItem(ColorOptions.messageachievement + "Gate", material, false, 
				ColorOptions.message + "Gate set: " + (objective.getGateID() != -1 ? ColorOptions.messagesubjects + "Set" : ColorOptions.error + "Not set"));
		
		List<String> description = new ArrayList<String>();
		if (objective.getGateID() != -1)
		{
			main.logMessage(objective.getGateID() + "");
			Gate gate = objective.fetchGate();
			
			description.addAll(Arrays.asList(
					ColorOptions.stats + "ID: " + ColorOptions.statsresults + objective.getGateID(),
					ColorOptions.stats + "Name: " + ColorOptions.statsresults + gate.getName(),
					ColorOptions.stats + "Max. Health: " + ColorOptions.statsresults + gate.getOriginalHealth(),
					"",
					ColorOptions.message + "Click to change/remove"
					));	
		} else
		{
			description.addAll(Arrays.asList(
					"",
					ColorOptions.message + "Click to set"
					));
		}
		gateItem = product.addItemDescription(gateItem, description);
		
		menu.setItem(11, gateItem);
		menu.setItem(12, product.createItem(ColorOptions.messagesubjects + "Change location", new ItemStack(Material.COMPASS), false));
		menu.setItem(13, product.createItem(ColorOptions.KAKColor + "Side Objective", new ItemStack(Material.BANNER, 1, (short) 11), false, ColorOptions.message + "Click to teleport"));
		menu.setItem(14, product.createItem(ColorOptions.messageachievement + "Test Side Objective", new ItemStack(Material.DIAMOND_SWORD), false, ColorOptions.message + "Click to test", ColorOptions.message + "the behaviour of this", ColorOptions.message + "Object during siege"));
	
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().closeInventory();
		user.getPlayer().openInventory(menu);
	}
	
	public void openSideObjectiveGate(User user, SideObjective objective, Scenario scenario)
	{
		Gates.Gates.instantiateAll(scenario.getTownID());
		Integer activeGates = Gates.Gates.gates.size();
		Inventory menu = Bukkit.createInventory(null, main.getMenuSize(activeGates), Menus.SOGateMenu);
		
		menu.setItem(4, product.createItem(ColorOptions.stats + "Change gate", new ItemStack(Material.FENCE), false, 
				ColorOptions.message + "Side Objective: " + ColorOptions.messagesubjects + objective.getSubID(), 
				ColorOptions.message + "Scenario: " + ColorOptions.messagesubjects + scenario.getID(), 
				ColorOptions.message + "Gate: " + (objective.getGateID() != -1 ? ColorOptions.messagesubjects + "" + objective.getGateID() : ColorOptions.error + "Not set"),
				"",
				ColorOptions.message + "NOTE: This list is a list",
				ColorOptions.message + "of the gates from the same town",
				ColorOptions.message + "as the scenario is based in"
				));
		menu.setItem(7, product.createItem(ColorOptions.error + "Remove Gate", new ItemStack(Material.LAVA_BUCKET), false, ColorOptions.message + "Remove gate from objective"));
		menu.setItem(8, Menus.getBackButton(Menus.PersonalMenu));
		
		int slot = 9;
		for (Gate gate : Gates.Gates.gates)
		{
			ItemStack material = product.createPropertyItem(gate.getMaterialID(), 1, false, false);
			ItemStack gateItem = product.createItem(ColorOptions.message + "Gate: " + ColorOptions.messagesubjects + gate.getName(), material, false, 
					ColorOptions.stats + "ID: " + ColorOptions.statsresults + gate.getID(),
					ColorOptions.statsformat + "Location:",
					ColorOptions.stats + "Town: " + ColorOptions.statsresults + town.getTownName(gate.getTownID()),
					ColorOptions.stats + "Street: " + ColorOptions.statsresults + street.getStreetName(gate.getStreetID()),
					ColorOptions.stats + "Facing: " + ColorOptions.statsresults + gate.getFaceDirection(),
					"",
					ColorOptions.stats + "Health: " + ColorOptions.statsresults + gate.getHealth(),
					"",
					ColorOptions.message + "Closed: " + (gate.getClosed() ? ColorOptions.error + "Closed" : ColorOptions.messageachievement + "Opened")
					);
			
			List<String> description = new ArrayList<String>();
			
			description.add("");
			if (objective.getGateID() == gate.getID())
			{
				description.add(ColorOptions.error + "Current selected gate");
			} else
			{
				description.add(ColorOptions.messagesubjects + "Click here to select");
			}
			gateItem = product.addItemDescription(gateItem, description);
			
			menu.setItem(slot, gateItem);
			slot++;
		}
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().closeInventory();
		user.getPlayer().openInventory(menu);
	}
	
	public void openSiegeSpawnpointsManager(User user, Scenario scenario, int teamNumber)
	{
		ChatColor teamColor = null;
		List<SiegeSpawnpoint> teamSpawnpoints = new ArrayList<SiegeSpawnpoint>();
		
		if (teamNumber == 1)
		{
			teamColor = scenario.getTeam1Color();
			teamSpawnpoints = scenario.getTeam1Spawnpoints();
		} else if (teamNumber == 2)
		{
			teamColor = scenario.getTeam2Color();
			teamSpawnpoints = scenario.getTeam2Spawnpoints();
		}
				
		Inventory menu = Bukkit.createInventory(null, 2*9+main.getMenuSize(teamSpawnpoints.size()), Menus.SiegeSpawnpointsManagerMenu);
		
		menu.setItem(4, product.createItem(ColorOptions.messageachievement + "Spawnpoints " + teamColor + "Team " + teamNumber, new ItemStack(Material.BANNER, 1, (short) 15), false, 
				ColorOptions.stats + "Scenario: " + ColorOptions.statsresults + scenario.getID(), 
				ColorOptions.stats + "Spawnpoints: " + (teamSpawnpoints.isEmpty() ? ColorOptions.error + "0" : ColorOptions.statsresults + "" + teamSpawnpoints.size())));
		menu.setItem(8, Menus.getBackButton(Menus.SiegeManagerMenu));
		
		for (int i = 18; i < menu.getSize(); i++)
		{
			menu.setItem(i, product.createItem(ColorOptions.error + "No spawnpoint", new ItemStack(Material.BARRIER), false));
		}
		
		Integer slot = 18;
		for (SiegeSpawnpoint spawnpoint : teamSpawnpoints)
		{
			menu.setItem(slot-9, product.createItem(ColorOptions.messagesubjects + "Change location", new ItemStack(Material.COMPASS), false));
			menu.setItem(slot, product.createItem(ColorOptions.KAKColor + "Spawnpoint: " + spawnpoint.getSpawnCountID(), new ItemStack(Material.BANNER, 1, (short) 11), false, ColorOptions.message + "Click to teleport"));
			menu.setItem(slot+9, product.createItem(ColorOptions.error + "Delete Spawnpoint", new ItemStack(Material.LAVA_BUCKET), false, ColorOptions.message + "Delete from Scenario"));
			slot++;
		}
	
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().closeInventory();
		user.getPlayer().openInventory(menu);
	}
	
	public void openEvents(User user)
	{
		Inventory menu = Bukkit.createInventory(null, 2*9, Menus.EventsMenu);
		
		menu.setItem(0, product.createItem(ColorOptions.statsformat + "Relevant statistics", new ItemStack(Material.SIGN), false, ColorOptions.stats + "Online players: " + ColorOptions.statsresults + Bukkit.getOnlinePlayers().size()));
		menu.setItem(4, product.createItem(ColorOptions.stats + "Event Manager", new ItemStack(Material.CAKE), false, ColorOptions.message + "Manage all active events", ColorOptions.message + "or edit minigame scenarios"));
		menu.setItem(8, Menus.getBackButton(Menus.PersonalMenu));
		
		menu.setItem(12, product.createItem(ColorOptions.messagesubjects + "Hide And Seek", new ItemStack(Material.LEAVES), false, ColorOptions.message + "Click here to see", ColorOptions.message + "and join active games"));
		menu.setItem(14, product.createItem(ColorOptions.coinStats + "Siege", new ItemStack(Material.BANNER), false, 
				ColorOptions.message + "Click here to see", 
				ColorOptions.message + "and join active Siege games",
				"",
				ColorOptions.coinStats + "Description",
				ColorOptions.message + "Siege is a minigame where",
				ColorOptions.message + "2 teams fight against eachother.",
				ColorOptions.message + "The defending team defends",
				ColorOptions.message + "a main objective and multiple",
				ColorOptions.message + "side objectives against the attackers,",
				ColorOptions.messageformat + "The goal Siege:",
				ColorOptions.messageformat + "-Attackers: Capture the objectives",
				ColorOptions.messageformat + "-Defenders: Defend the objectives"));
	
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().openInventory(menu);
	}
	
	public void openHideAndSeekOverview(User user)
	{
		
	}
	
	public void openSiegeOverview(User user)
	{
		if (Sieges.findSiege(user) != null)
		{
			this.openSiegeInformation(user, Sieges.findSiege(user), 0);
			return;
		}
		
		Integer participants = 0;
		
		for (Siege siege : Sieges.Sieges)
		{
			participants += siege.getParticipants().size();
		}
		Inventory menu = Bukkit.createInventory(null, 2*9+main.getMenuSize(Sieges.Sieges.size()), Menus.SiegeOverviewMenu);
		
		ItemStack siegesItem = product.createItem(ColorOptions.messagesubjects + "Siege", new ItemStack(Material.BANNER), false,
				ColorOptions.message + "Check the status of",
				ColorOptions.message + "active sieges or join",
				ColorOptions.message + "a siege",
				"",
				ColorOptions.message + "Current amount of sieges: " + ColorOptions.messagesubjects + Sieges.Sieges.size(),
				ColorOptions.message + "Players playing Siege: " + ColorOptions.messagesubjects + participants);
		
		menu.setItem(4, siegesItem);
		menu.setItem(8, Menus.getBackButton(Menus.EventsMenu));
		
		for (int i = 9; i < 18; i++)
		{
			menu.setItem(i, product.createItem(ColorOptions.error + "No siege", new ItemStack(Material.BARRIER), false, ColorOptions.message + "No active siege has started"));
		}
		
		Integer slot = 9;
		for (Siege siege : Sieges.Sieges)
		{
			short bannerColor = 2;
			String scenarioName = ColorOptions.error + "Not set";
			String stage = "Cooldown";
			List<String> timeDescription = new ArrayList<String>();
			
			if (siege.getScenario() != null)
			{
				scenarioName = ColorOptions.messagesubjects + siege.getScenario().getName();
			}
			if (siege.getMatchmaking())
			{
				main.logMessage("Siege in matchmaking");
				HashMap<String, Integer> timeFormat = main.getCalculatedTime(siege.getMatchmakingSeconds());
				timeDescription.addAll(Arrays.asList(
						" ",
						ColorOptions.message + "Time until game starts:",
						ColorOptions.message + "" + timeFormat.get("minute") + " minutes and " + timeFormat.get("second") + " seconds"
						));
				
				stage = "Matchmaking";
				bannerColor = 2;
				if (!siege.getSuggestedScenarioList().isEmpty() && siege.getScenario() == null)
				{
					scenarioName = ColorOptions.message + "Voting..";
				}
			} else if (siege.getCooldown())
			{
				main.logMessage("Siege in cooldown");
				stage = "Cooldown";
				bannerColor = 15;
				HashMap<String, Integer> timeFormat = main.getCalculatedTime(siege.getCooldownSeconds());
				timeDescription.addAll(Arrays.asList(
						" ",
						ColorOptions.message + "Time until matchmaking:",
						ColorOptions.message + "" + timeFormat.get("minute") + " minutes and " + timeFormat.get("second") + " seconds"
						));
			} else if (siege.getProgress())
			{
				main.logMessage("Siege in progress");
				stage = "In progress";
				HashMap<String, Integer> timeFormat = main.getCalculatedTime(siege.getProgressSeconds());
				timeDescription.addAll(Arrays.asList(
						" ",
						ColorOptions.message + "Time until game ends:",
						ColorOptions.message + "" + timeFormat.get("minute") + " minutes and " + timeFormat.get("second") + " seconds"
						));
			}
			main.logMessage(timeDescription.toString());

			ItemStack siegeItem = product.createItem(
	    			ColorOptions.message + "Siege " + ColorOptions.messagesubjects + (Sieges.Sieges.indexOf(siege)+1), 
	    			new ItemStack(Material.BANNER, 1, bannerColor), 
	    			false, 
	    			ColorOptions.message + "Scenario: " + scenarioName,
	    			ColorOptions.message + "Skilled match: " + (siege.getSkilledMatch() ? ColorOptions.error + "Skilled" : ColorOptions.message + "Regular"),
	    			ColorOptions.message + "Joined players: "  + (siege.getParticipants().isEmpty() ? ColorOptions.error + "None" : ColorOptions.messagesubjects + "" + siege.getParticipants().size()),
	    			ColorOptions.message + "Current stage: " + ColorOptions.messagesubjects + stage
	    			);
			List<String> joinDesc = new ArrayList<String>();
			joinDesc.addAll(timeDescription);
			
			joinDesc.add(" ");
			if (siege.getMatchmaking())
			{
				if (siege.getEntryTitle() != null && user.getTitleID() < siege.getEntryTitle())
				{
					joinDesc.add(ColorOptions.error + "Can't join match!");
					if (siege.getSkilledMatch() && siege.getSkilledMin() > siege.getEntryTitle())
					{
						joinDesc.add(ColorOptions.error + "Allowed titles: " + siege.getSkilledMin() + " till " + siege.getSkilledMax());
					} else
					{
						joinDesc.add(ColorOptions.error + "Allowed titles: higher than " + siege.getEntryTitle());
					}
				} else
				if ((siege.getSkilledMatch() && user.getTitleID() >= siege.getSkilledMin() && user.getTitleID() <= siege.getSkilledMax()) || !siege.getSkilledMatch())
				{
					joinDesc.add(ColorOptions.messagesubjects + "Click to join!");
				} else if (siege.getSkilledMatch() && (user.getTitleID() < siege.getSkilledMin() || user.getTitleID() > siege.getSkilledMax()))
				{
					joinDesc.addAll(Arrays.asList(
							ColorOptions.error + "Can't join match!",
							ColorOptions.error + "Allowed titles: " + siege.getSkilledMin() + " till " + siege.getSkilledMax()
							));
				}
			} else
			{
				joinDesc.addAll(Arrays.asList(
						ColorOptions.error + "Can't join match!",
						ColorOptions.error + "Wait for matchmaking to start"));
			}
			
			siegeItem = product.addItemDescription(siegeItem, joinDesc);
			menu.setItem(slot, siegeItem);
			slot++;
		}
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().closeInventory();
		user.getPlayer().openInventory(menu);
	}
	
	public void openSiegeInformation(User user, Siege siege, int pageNumber)
	{
		siege.orderLowToHigh();
		List<Participant> participantList = new ArrayList<Participant>();
		List<Participant> list = siege.getParticipants();
		if (list.size() > 45)
		{
			Integer startingindex = 0;
			switch(pageNumber)
			{
			case 2: startingindex = 18;
			break;
			case 3: startingindex = 36;
			break;
			case 4: startingindex = 54;
			break;
			case 5: startingindex = 72;
			break;
			case 6: startingindex = 90;
			break;
			case 7: startingindex = 108;
			break;
			case 8: startingindex = 126;
			break;
			case 9: startingindex = 144;
			break;
			case 10: startingindex = 162;
			break;
			default: startingindex = 0;
			break;
			}
			
			for (int i = startingindex; i < startingindex+18; i++)
			{
				participantList.add(list.get(i));
			}
		} else
		{
			participantList.addAll(list);
		}
		Inventory menu = Bukkit.createInventory(null, 6*9, Menus.SiegeInformationMenu);
		
		short siegeBannerColor = 2;
		List<String> scenarioItemDesc = new ArrayList<String>();
		String scenarioName = ColorOptions.error + "Not set";
		List<String> timeDescription = new ArrayList<String>();
		List<String> objectiveDescription = new ArrayList<String>(Arrays.asList(
				ColorOptions.messageformat + "Description for objectives",
				ColorOptions.message + "Objectives are indicated by a",
				ColorOptions.messageachievement + "Blue Banner block",
				"",
				ColorOptions.message + "Attackers need to capture",
				ColorOptions.message + "at least the main objective to",
				ColorOptions.message + "win the game.",
				ColorOptions.message + "You can capture an objective by",
				ColorOptions.message + "standing inside a circle around the",
				ColorOptions.message + "banner. Defenders can alse stand",
				ColorOptions.message + "inside the circle to turn the",
				ColorOptions.message + "capture process around",
				""
				));
		List<String> playerDescription = new ArrayList<String>();
		
		if (siege.getScenario() != null)
		{
			Scenario scenario = siege.getScenario();
			scenarioName = ColorOptions.messagesubjects + siege.getScenario().getName();
			
			scenarioItemDesc.addAll(Arrays.asList(
					ColorOptions.message + "Town: " + scenario.getTownName(),
					ColorOptions.message + "EntryTitle if not skilled match: " + scenario.getEntryTitle(),
					"",
					ColorOptions.message + "Min. players: " + scenario.getPlayersMin(),
					ColorOptions.message + "Max. players: " + scenario.getPlayersMax(),
					"",
					ColorOptions.message + "Amount of votes: " + scenario.getVotes()
					));
			objectiveDescription.addAll(Arrays.asList(
					ColorOptions.message + "Total amount of objectives: " + ColorOptions.messagesubjects + (scenario.getSideObjectives().size() + 1),
					""
					));
			
			Integer gateObjectives = 0;
			for (SideObjective objective : scenario.getSideObjectives())
			{
				if (objective.getGateID() != -1)
				{
					gateObjectives++;
				}
			}
			
			objectiveDescription.add(ColorOptions.message + "Objectives with gates: " + (gateObjectives > 0 ? ColorOptions.messagesubjects : ColorOptions.error) + gateObjectives);
			playerDescription.addAll(Arrays.asList(
					ColorOptions.message + "Min. players: " + ColorOptions.messagesubjects + scenario.getPlayersMin(),
					ColorOptions.message + "Max. players: " + ColorOptions.error + scenario.getPlayersMax()
					));
		} else
		{
			objectiveDescription.add(ColorOptions.error + "No objective information yet");
		}
		if (siege.getMatchmaking())
		{
			main.logMessage("Siege in matchmaking");
			HashMap<String, Integer> timeFormat = main.getCalculatedTime(siege.getMatchmakingSeconds());
			timeDescription.addAll(Arrays.asList(
					" ",
					ColorOptions.message + "Time until game starts:",
					ColorOptions.message + "" + timeFormat.get("minute") + " minutes and " + timeFormat.get("second") + " seconds"
					));
			
			if (!siege.getSuggestedScenarioList().isEmpty() && siege.getScenario() == null)
			{
				scenarioName = ColorOptions.message + "Voting..";
				scenarioItemDesc.addAll(Arrays.asList(
						ColorOptions.message + "Voting for scenario..."
						));
				for (Scenario scenarios : siege.getSuggestedScenarioList())
				{
					scenarioItemDesc.addAll(Arrays.asList(
							"",
							ColorOptions.message + "Scenario: " + ColorOptions.messagesubjects + scenarios.getName(),
							ColorOptions.message + "Votes: " + ColorOptions.messagesubjects + scenarios.getVotes().size()
							));
				}
				scenarioItemDesc.addAll(Arrays.asList(
						"",
						ColorOptions.message + "Random scenario",
						ColorOptions.message + "Votes: " + ColorOptions.messagesubjects + siege.getRandomVotes().size(),
						""
						));
				if (!siege.getParticipating(user))
				{
					scenarioItemDesc.add(ColorOptions.error + "Join the Siege to vote");
				}
			}
		} else if (siege.getCooldown())
		{
			this.openSiegeOverview(user);
			user.getPlayer().sendMessage(ColorOptions.error + "Can't view information. Siege is in cooldown!");
		} else if (siege.getProgress())
		{
			main.logMessage("Siege in progress");
			HashMap<String, Integer> timeFormat = main.getCalculatedTime(siege.getProgressSeconds());
			timeDescription.addAll(Arrays.asList(
					" ",
					ColorOptions.message + "Time until game ends:",
					ColorOptions.message + "" + timeFormat.get("minute") + " minutes and " + timeFormat.get("second") + " seconds"
					));
		}
		main.logMessage(timeDescription.toString());


		List<String> joinDesc = new ArrayList<String>();
		joinDesc.addAll(timeDescription);
		
		joinDesc.add(" ");
		
		if (siege.getParticipating(user))
		{
			joinDesc.add(ColorOptions.messagesubjects + "Participating in this Siege");
		} else
		{
			if (siege.getMatchmaking())
			{
				if ((siege.getSkilledMatch() && user.getTitleID() >= siege.getSkilledMin() && user.getTitleID() <= siege.getSkilledMax()) || !siege.getSkilledMatch())
				{
					joinDesc.add(ColorOptions.messagesubjects + "Click to join!");
				} else if (siege.getSkilledMatch() && (user.getTitleID() < siege.getSkilledMin() || user.getTitleID() > siege.getSkilledMax()))
				{
					joinDesc.addAll(Arrays.asList(
							ColorOptions.error + "Can't join match!",
							ColorOptions.error + "Allowed titles: " + siege.getSkilledMin() + " till " + siege.getSkilledMax()
							));
				}
			} else
			{
				joinDesc.addAll(Arrays.asList(
						ColorOptions.error + "Can't join match!",
						ColorOptions.error + "Wait for matchmaking to start"));
			}
		}
		
		ItemStack scenarioItem = product.createItem(
				ColorOptions.message + "Scenario: " + scenarioName, 
				new ItemStack(Material.COMPASS), 
				false
				);
		
		scenarioItem = product.addItemDescription(scenarioItem, scenarioItemDesc);
		
		ItemStack objectiveItem = product.createItem(ColorOptions.messageachievement + "Objective information", new ItemStack(Material.BANNER, 1, (short) 15), false);
		ItemStack playerItem = product.createItem(ColorOptions.messagesubjects + "Joined players", new ItemStack(Material.SKULL_ITEM), false);

		objectiveItem = product.addItemDescription(objectiveItem, objectiveDescription);
		
		menu.setItem(8, Menus.getBackButton(Menus.SiegeOverviewMenu));
		
		playerDescription.addAll(Arrays.asList(
				ColorOptions.message + "Joined players: " + (siege.getParticipants().isEmpty() ? ColorOptions.error : ColorOptions.messagesubjects) + siege.getParticipants().size(),
				""));
		if (!siege.getParticipants().isEmpty())
		{
			playerDescription.add(ColorOptions.message + "Average title of joined players: " + siege.getTitleAverage());
		}
		
		playerItem = product.addItemDescription(playerItem, playerDescription);
		
		if (siege.getParticipating(user))
		{
			Participant participant = siege.getParticipant(user);
			menu.setItem(1, scenarioItem);
			menu.setItem(6, objectiveItem);

			if (siege.getMatchmaking())
			{	
				if (!siege.getSuggestedScenarioList().isEmpty() && siege.getScenario() == null)
				{
					Integer slot = 9;
					for (Scenario scenarios : siege.getSuggestedScenarioList())
					{
						List<String> scenariosDesc = new ArrayList<String>();
						ItemStack scenariosItem = product.createItem(ColorOptions.messagesubjects + scenarios.getName(), 
								new ItemStack(Material.MAP), 
								scenarios.getVotes().contains(participant), 
								ColorOptions.message + "Town: " + ColorOptions.messagesubjects + scenarios.getTownName(),
								ColorOptions.message + "Entrytitle: " + ColorOptions.messagesubjects + scenarios.getEntryTitle(),
								"",
								ColorOptions.message + "Min. players: " + scenarios.getPlayersMin(),
								ColorOptions.message + "Max. players: " + scenarios.getPlayersMax(),
								"",
								ColorOptions.message + "Amount of objectives: " + ColorOptions.messagesubjects + scenarios.getSideObjectives().size(),
								"",
								ColorOptions.message + "Amount of votes: " + ColorOptions.messagesubjects + scenarios.getVotes().size()
								);
						
						if (scenarios.getVotes().contains(participant))
						{
							scenariosDesc.addAll(Arrays.asList(
									"",
									ColorOptions.error + "Voted"
									));
						} else
						{
							scenariosDesc.addAll(Arrays.asList(
									"",
									ColorOptions.messagesubjects + "Click to vote"
									));
						}
						scenariosItem = product.addItemDescription(scenariosItem, scenariosDesc);
						menu.setItem(slot, scenariosItem);
						slot++;
					}
					
					List<String> randomDesc = new ArrayList<String>();
					ItemStack randomItem = product.createItem(ChatColor.DARK_PURPLE + "Random", 
							new ItemStack(Material.EMPTY_MAP), 
							siege.getRandomVotes().contains(participant), 
							ColorOptions.message + "A random map will",
							ColorOptions.message + "be chosen",
							"",
							ColorOptions.message + "Amount of votes: " + ColorOptions.messagesubjects + siege.getRandomVotes().size());
					
					if (siege.getRandomVotes().contains(participant))
					{
						randomDesc.addAll(Arrays.asList(
								"",
								ColorOptions.error + "Voted"
								));
					} else
					{
						randomDesc.addAll(Arrays.asList(
								"",
								ColorOptions.messagesubjects + "Click to vote"
								));
					}
					randomItem = product.addItemDescription(randomItem, randomDesc);
					menu.setItem(11, randomItem);
				}
			}
			String teamName = ColorOptions.error + "None";
			MGTeam team = participant.GetTeam();
			
			if (team != null)
			{
				teamName = team.GetColor() + team.GetName();
				siegeBannerColor = team.GetBannerColor();
			}
			
			joinDesc.addAll(Arrays.asList(
					"",
					ColorOptions.message + "Your team: " + teamName
					));
			
			if (siege.getProgress())
			{
				SiegeMember sMember = (SiegeMember) participant;
				short blockColor = 2;
				boolean currentSpawnpoint = false;
				Integer slot = 9;
				Integer teamNumber = team.GetNumber();
				List<SiegeSpawnpoint> spawnpoints = siege.getScenario().getTeamSpawnpoints(teamNumber);
				for (SiegeSpawnpoint spawnpoint : spawnpoints)
				{
					List<String> spawnDesc = new ArrayList<String>();
					if (sMember.getCurrentSpawnpoint() == spawnpoint)
					{
						blockColor = 1;
						currentSpawnpoint = true;
						spawnDesc.add(ColorOptions.error + "Current spawnpoint");
					} else
					{
						//spawnDesc.add(ColorOptio)
					}
					ItemStack spawnItem = product.createItem(
							ColorOptions.messageachievement + "Spawnpoint " + spawnpoint.getSpawnCountID(), 
							new ItemStack(Material.STAINED_CLAY, 1, blockColor), 
							currentSpawnpoint);
					
					
				}
			}
			
		} else
		{
			menu.setItem(12, scenarioItem);
			menu.setItem(13, objectiveItem);
		}
		
		ItemStack siegeItem = product.createItem(
    			ColorOptions.message + "Siege " + ColorOptions.messagesubjects + (Sieges.Sieges.indexOf(siege)+1), 
    			new ItemStack(Material.BANNER, 1, siegeBannerColor), 
    			false, 
    			ColorOptions.message + "Scenario: " + scenarioName,
    			ColorOptions.message + "Skilled match: " + (siege.getSkilledMatch() ? ColorOptions.error + "Skilled" : ColorOptions.message + "Regular"),
    			ColorOptions.message + "Joined players: "  + (siege.getParticipants().isEmpty() ? ColorOptions.error + "None" : ColorOptions.messagesubjects + "" + siege.getParticipants().size())
    			);
		siegeItem = product.addItemDescription(siegeItem, joinDesc);
		menu.setItem(4, siegeItem);
		
		for (int i = 27; i < 36; i++)
		{
			menu.setItem(i, product.createItem(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), false));
		}
		
		menu.setItem(31, playerItem);
		
		if (siege.getParticipants().size() > 18)
		{
			menu.setItem(27, product.createItem(ColorOptions.messagesubjects + "Previous tab", new ItemStack(Material.ARROW), false, ColorOptions.message + "Click to go to the previous", ColorOptions.message + "tab of participants", "", ColorOptions.message + "Current page: " + ColorOptions.messagesubjects + pageNumber));
			menu.setItem(35, product.createItem(ColorOptions.messagesubjects + "Next tab", new ItemStack(Material.ARROW), false, ColorOptions.message + "Click to go to the next", ColorOptions.message + "tab of participants", "", ColorOptions.message + "Current page: " + ColorOptions.messagesubjects + pageNumber));
		}
		
		Integer slot = 36;
		for (Participant participant : participantList)
		{
			User userParticipant = participant.getUser();
			ItemStack profile = Menus.getSocialProfile(userParticipant);
			main.logMessage(profile.getItemMeta().getLore().size() + "");
	    	ItemStack skull = product.createItem(ColorOptions.message + userParticipant.getUsername(), 
	    			profile, 
	    			(userParticipant.getID() == user.getID() ? true : false)
	    			);
	    	
	    	menu.setItem(slot, skull);
	    	slot++;
		}
		
		
		menu = this.fillEmptyMenu(menu, false, " ", null);
		
		user.getPlayer().closeInventory();
		user.getPlayer().openInventory(menu);
	}
	
//	public Inventory openEventMenu(Player player)
//	{
//		UUID uuid = player.getUniqueId();
//		Integer coinamount = user.getCoins(uuid);
//		Integer gemamount = user.getGems(uuid);
//		Integer salary = title.getSalary(user.getTitleID(uuid));
//		Integer income = user.getIncome(uuid);
//		
//		Inventory menu = Bukkit.createInventory(null, 2*9, ChatColor.YELLOW + "Events");
//	
//		menu.setItem(0, Menus.getFinancial(user));
//		menu.setItem(4, MenuClick.addmenulist(ChatColor.YELLOW + "All Events are listed here", Material.CAKE, ColorOptions.message + "All active events are displayed", ColorOptions.message + "By an enchanted item"));
//	}
}
