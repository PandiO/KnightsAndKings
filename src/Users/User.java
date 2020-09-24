package Users;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.managers.RegionManager;

import Afk.Afk;
import Assignments.Assignment;
import Assignments.AssignmentEnterPropertySpecific;
import Assignments.AssignmentFoodConsumeRandom;
import Assignments.AssignmentHarvestRandom;
import Assignments.AssignmentKill;
import Assignments.AssignmentTravelDistance;
import Assignments.AssignmentTravelRandom;
import Assignments.AssignmentTravelSpecific;
import DataManager.HideandSeeks;
import DataManager.Users2;
import DataManager.Worldguard;
import Donator.Donator;
import Genders.Gender;
import Handlers.ColorOptions;
import Handlers.ExperienceChangeEvent;
import Handlers.SoundHandler;
import HideAndSeek.HideAndSeek;
import Houses.House;
import KillsDeaths.CombatCheck;
import Main.Main;
import Menu.Menu;
import Properties.Property;
import Quests.Quest;
import Scoreboards.ActionBar;
import Sieges.Siege;
import Skills.Skill;
import Skills.SpecialSkill;
import SpawnPoints.SpawnPoint;
import Titles.Title;
import Towns.Town;
import Tutorial.Tutorial;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class User 
{
	Main main = Main.getPlugin(Main.class);
	SpecialSkill specialSkill = new SpecialSkill();
	User user = this;
	SpawnPoint spawnpoint = new SpawnPoint();
	Gender gender = new Gender();
	Donator donator = new Donator();
	
	//Runtime variables
	boolean isOfflineUser = false;
	boolean fetchedAddress = false;
	boolean fetchedData = false;
	Afk afk = null;
	Long afkCommence;
	HashMap<Integer, Integer> soldItems = new HashMap<Integer, Integer>(); 
	Location banditLocation;
	HashMap<UUID, Integer> mentionDelay = new HashMap<UUID, Integer>();
	List<ItemStack> keepItems = new ArrayList<ItemStack>();
	User avengerTarget;
	
	//Database variables
	Player player = null;
	InetAddress address;
	int attackspeedID;
	List<Assignment> AssignmentList = new ArrayList<Assignment>();
	List<Integer> AssignmentIDList = new ArrayList<Integer>();
	int AssignmentDailyAmount = 3;
	
	int banditKills;
	int banditKillsToday;
	boolean banned = false;
	int blocksBroken;
	int blocksBrokenToday;
	int blocksBrokenWood;
	int blocksBrokenWoodToday;
	int blocksBrokenWheat;
	int blocksBrokenWheatToday;
	int blocksBrokenStone;
	int blocksBrokenStoneToday;
	int blocksBrokenOre;
	int blocksBrokenOreToday;
	
	int coins;
	
	int deaths;
	int defenseID;
	int donatorID;
	String donatorName;
	
	int experience;
	int expierienceNext;
	
	int fishCatch;
	int fishCatchToday;
	int friendAmount;
	ArrayList<UUID> friendRequestList = new ArrayList<UUID>();
	ArrayList<UUID> friendList = new ArrayList<UUID>();
	
	int gems;
	int genderID;
	String genderName;
	
	int healthID;
	int houseAmount;
	int houseAmountMax;
	
	int id;
	int income;
	Long incomeTime;
	
	Date joinDate;
	
	int keepAmount;
	int keepAmountMax;
	int kills;

	Location lastDeathLocation;
	Long lastLogin;
	Long lastLoginNew;
	int level;
		
	boolean ownermode = false;
	boolean ownermodeDisableonQuit = false;
	boolean ownermodeEnableonQuit = false;
	boolean ownermodeonQuit = false;
	
	
	Long playtime;
	Long playtimeToday;
	int previousDonatorID;
	String previousDonatorName;
	int propertyAmount;
	int propertyAmountMax;
	
	List<Quest> QuestList = new ArrayList<Quest>();
	List<Integer> QuestIDList = new ArrayList<Integer>();
	int QuestMaximum = 2;
	
	Long rentTime;
	
	int roomAmount;
	int roomAmountMax;

	int salary;
	Long salaryTime;
	int skillPoints;
	int spawnpointID;
	Location spawnpointLocation;
	int specialskillPoints;
	int specialSkillID;
	String specialSkillName;
	int speedID;
	boolean staffmode = false;
	boolean staffmodeDisableonQuit = false;
	boolean staffmodeEnableonQuit = false;
	boolean staffmodeonQuit = false;
	int strengthID;
	
	int titleID;
	String titleName;
	boolean tombDiscovered;
	Tutorial tutorial;
	
	String username;
	UUID uuid;
	
	int votes;
	
	public User(UUID uuid)
	{	
		if (existUser(uuid))
		{
			Users2.users.add(this);

			ResultSet data = fetchUser(uuid);
			this.setData(uuid, data);
		}
	}
	
	
	
	public boolean existUser(UUID uuid)
	{
		boolean exist = false;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Player Where UUID=?;");	
			stmt.setString(1, uuid.toString());
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				exist = true;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return exist;
	}
	
	public void setData(UUID uuid, ResultSet data)
	{
		SpawnPoint spawnpoint = new SpawnPoint();
		Title title = new Title();
		try 
		{
			try 
			{
				this.address = InetAddress.getByName(data.getString("Address"));
				this.fetchedAddress = true;
			} catch (UnknownHostException e) 
			{
				e.printStackTrace();
			}
			this.attackspeedID = data.getInt("AttackSpeedID");
			
			this.banditKills = data.getInt("BanditKills");
			this.banditKillsToday = data.getInt("BanditKillsToday");
			this.banned = data.getBoolean("Banned");
			this.blocksBroken = data.getInt("BlocksBroken");
			this.blocksBrokenToday = data.getInt("BlocksBrokenToday");
			this.blocksBrokenOre = data.getInt("BlocksBrokenOre");
			this.blocksBrokenOreToday = data.getInt("BlocksBrokenOreToday");
			this.blocksBrokenStone = data.getInt("BlocksBrokenStone");
			this.blocksBrokenStoneToday = data.getInt("BlocksBrokenStoneToday");
			this.blocksBrokenWheat = data.getInt("BlocksBrokenWheat");
			this.blocksBrokenWheatToday = data.getInt("BlocksBrokenWheatToday");
			this.blocksBrokenWood = data.getInt("BlocksBrokenWood");
			this.blocksBrokenWoodToday = data.getInt("BlocksBrokenWoodToday");
			
			this.coins = data.getInt("Coins");
			
			this.deaths = data.getInt("Deaths");
			this.defenseID = data.getInt("DefenseID");
			this.donatorID = data.getInt("DonatorID");
			this.donatorName = donator.getDonatorName(this.donatorID);
			this.experience = data.getInt("Experience");
			
			this.fishCatch = data.getInt("FishCatch");
			this.fishCatchToday = data.getInt("FishCatchToday");
			this.friendAmount = data.getInt("FriendAmount");
			
			this.gems = data.getInt("Gems");
			this.genderID = data.getInt("GenderID");
			this.genderName = this.gender.getGenderName(this.genderID);
			
			this.healthID = data.getInt("HealthID");
			this.houseAmount = data.getInt("HouseAmount");
			this.houseAmountMax = data.getInt("HouseMaximum");
			
			this.id = data.getInt("ID");
			this.income = data.getInt("Income");
			this.incomeTime = data.getLong("IncomeTime");
			
			this.joinDate = data.getDate("JoinDate");
			
			this.keepAmount = data.getInt("KeepAmount");
			this.keepAmountMax = data.getInt("KeepMaximum");
			this.kills = data.getInt("Kills");
			
			this.lastLogin = data.getDate("LastLogin").getTime();
			this.level = data.getInt("Level");
			
			this.playtime = data.getLong("PlayTime");
			this.playtimeToday = data.getLong("PlayTimeToday");
			this.previousDonatorID = data.getInt("PreviousDonatorRank");
			this.previousDonatorName = this.donator.getDonatorName(this.previousDonatorID);
			this.propertyAmount = data.getInt("PropertyAmount");
			this.propertyAmountMax = data.getInt("PropertyMaximum");
			
			this.rentTime = data.getLong("RentTime");
			this.roomAmount = data.getInt("RoomAmount");
			this.roomAmountMax = data.getInt("RoomMaximum");
			
			this.salaryTime = data.getLong("SalaryTime");
			this.skillPoints = data.getInt("SkillPoints");
			this.spawnpointID = data.getInt("SpawnpointID");
			this.spawnpointLocation = spawnpoint.getSpawnPointLocation(this.spawnpointID);
			this.specialSkillID = data.getInt("SpecialSkillID");
			this.specialSkillName = this.specialSkill.getName(this.specialSkillID);
			this.specialskillPoints = data.getInt("SpecialSkillPoints");
			this.speedID = data.getInt("SpeedID");
			this.strengthID = data.getInt("StrengthID");
			
			this.titleID = data.getInt("TitleID");
			this.titleName = title.getTitleName(titleID, genderID);
			this.tombDiscovered = data.getBoolean("TombDiscovered");
			
			this.username = data.getString("Username");
			this.uuid = uuid;
			
			this.votes = data.getInt("Votes");
			
			//Additional
			this.salary = title.getSalary(titleID);
			this.expierienceNext = new Title().getExpmin(this.titleID+1);

			this.refreshAssignmentMaximum();
			this.refreshQuestMaximum();
			this.fetchedData = true;
			Bukkit.getConsoleSender().sendMessage(ColorOptions.messagesubjects + "Succesfully loaded data of player with uuid " + uuid);
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet fetchUser(UUID uuid)
	{
		ResultSet result = null;
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Player Where UUID=?;");	
			stmt.setString(1, uuid.toString());
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				result = results;
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to fetch data of player with uuid " + uuid);
		}
		return result;
	}
	
	public String getSaveQuery()
	{
		String statement = null;
		try 
		{
			String stmt2 = "UPDATE Player SET "
					+ "AttackSpeedID=" + this.attackspeedID + ", "
					+ "BanditKills=" + this.banditKills + ", "
					+ "BanditKillsToday=" + this.banditKillsToday + ", "
					+ "Banned=" + this.banned + ", "
					+ "BlocksBroken=" + this.blocksBroken + ", "
					+ "BlocksBrokenToday=" + this.blocksBrokenToday + ", "
					+ "BlocksBrokenOre=" + this.blocksBrokenOre + ", "
					+ "BlocksBrokenOreToday=" + this.blocksBrokenOreToday + ", "
					+ "BlocksBrokenStone=" + this.blocksBrokenStone + ", "
					+ "BlocksBrokenStoneToday=" + this.blocksBrokenStoneToday + ", "
					+ "BlocksBrokenWheat=" + this.blocksBrokenWheat + ", "
					+ "BlocksBrokenWheatToday=" + this.blocksBrokenWheatToday + ", "
					+ "BlocksBrokenWood=" + this.blocksBrokenWood + ", "
					+ "BlocksBrokenWoodToday=" + this.blocksBrokenWoodToday + ", "
					+ "Coins=" + this.coins + ", "
					+ "Deaths=" + this.deaths + ", "
					+ "DefenseID=" + this.defenseID + ", "
					+ "DonatorID=" + this.donatorID + ", "
					+ "Experience=" + this.experience + ", "
					+ "FriendAmount=" + this.friendAmount + ", "
					+ "FishCatch=" + this.fishCatch + ", "
					+ "FishCatchToday=" + this.fishCatchToday + ", "
					+ "Gems=" + this.gems + ", "
					+ "GenderID=" + this.genderID + ", "
					+ "HealthID=" + this.healthID + ", "
					+ "HouseAmount=" + this.houseAmount + ", "
					+ "HouseMaximum=" + this.houseAmountMax + ", "
					+ "Income=" + this.income + ", "
					+ "IncomeTime=" + this.incomeTime + ", "
					+ "JoinDate='" + this.joinDate + "', "
					+ "KeepAmount=" + this.keepAmount + ", "
					+ "KeepMaximum=" + this.keepAmountMax + ", "
					+ "Kills=" + this.kills + ", "
					+ "LastLogin='" + new java.sql.Date(this.lastLoginNew) + "', "
					+ "Level=" + this.level + ", "
					+ "PlayTime=" + this.playtime.longValue() + ", "
					+ "PlayTimeToday=" + this.playtimeToday.longValue() + ", "
					+ "PreviousDonatorRank=" + this.previousDonatorID + ", "
					+ "PropertyAmount=" + this.propertyAmount + ", "
					+ "PropertyMaximum=" + this.propertyAmountMax + ", "
					+ "RentTime=" + this.rentTime + ", "
					+ "RoomAmount=" + this.roomAmount + ", "
					+ "RoomMaximum=" + this.roomAmountMax + ", "
					+ "SalaryTime=" + this.salaryTime + ", "
					+ "SkillPoints=" + this.skillPoints + ", "
					+ "SpawnPointID=" + this.spawnpointID + ", "
					+ "SpecialSkillID=" + this.specialSkillID + ", "
					+ "SpecialSkillPoints=" + this.specialskillPoints + ", "
					+ "SpeedID=" + this.speedID + ", "
					+ "StrengthID=" + this.strengthID + ", "
					+ "TitleID=" + this.titleID + ", "
					+ "TombDiscovered=" + this.tombDiscovered + ", "
					+ "UUID='" + this.uuid + "', "
					+ "Votes=" + this.votes
					+ " WHERE UUID='" + this.uuid + "' AND Username='" + this.username + "'";
			
//			PreparedStatement stmt = main.getConnection().prepareStatement(
//					"UPDATE Player SET "
//					+ "AttackSpeedID=?, "
//					+ "BanditKills=?, "
//					+ "BanditKillsToday=?, "
//					+ "Banned=?, "
//					+ "Coins=?, "
//					+ "Deaths=?, "
//					+ "DefenseID=?, "
//					+ "DonatorID=?, "
//					+ "Experience=?, "
//					+ "FriendAmount=?, "
//					+ "FishCatch=?, "
//					+ "FishCatchToday=?, "
//					+ "Gems=?, "
//					+ "GenderID=?, "
//					+ "HealthID=?, "
//					+ "HouseAmount=?, "
//					+ "HouseMaximum=?, "
//					+ "Income=?, "
//					+ "IncomeTime=?, "
//					+ "JoinDate=?, "
//					+ "KeepAmount=?, "
//					+ "KeepMaximum=?, "
//					+ "Kills=?, "
//					+ "LastLogin=?, "
//					+ "Level=?, "
//					+ "PlayTime=?, "
//					+ "PlayTimeToday=?, "
//					+ "PreviousDonatorRank=?, "
//					+ "PropertyAmount=?, "
//					+ "PropertyMaximum=?, "
//					+ "RentTime=?, "
//					+ "RoomAmount=?, "
//					+ "RoomMaximum=?, "
//					+ "SalaryTime=?, "
//					+ "SkillPoints=?, "
//					+ "SpawnPointID=?, "
//					+ "SpecialSkillID=?, "
//					+ "SpecialSkillPoints=?, "
//					+ "SpeedID=?, "
//					+ "StrengthID=?, "
//					+ "TitleID=?, "
//					+ "TombDiscovered=?, "
//					+ "UUID=?, "
//					+ "Votes=? "
//					+ "BlocksBroken=?, "
//					+ "BlocksBrokenToday=?, "
//					+ "BlocksBrokenOre=?, "
//					+ "BlocksBrokenOreToday=?, "
//					+ "BlocksBrokenStone=?, "
//					+ "BlocksBrokenStoneToday=?, "
//					+ "BlocksBrokenWheat=?, "
//					+ "BlocksBrokenWheatToday=?, "
//					+ "BlocksBrokenWood=?, "
//					+ "BlocksBrokenWoodToday=?, "
//					+ "WHERE UUID=? AND Username=?"
//					);
//			
//			stmt.setInt(1, this.attackspeedID);
//			
//			stmt.setInt(2, this.banditKills);
//			stmt.setInt(3, this.banditKillsToday);
//			stmt.setBoolean(4, this.banned);
//			
//			stmt.setInt(5, this.coins);
//			
//			stmt.setInt(6, this.deaths);
//			stmt.setInt(7, this.defenseID);
//			stmt.setInt(8, this.donatorID);
//			
//			stmt.setInt(9, this.experience);
//			
//			stmt.setInt(10, this.friendAmount);
//			stmt.setInt(11, this.fishCatch);
//			stmt.setInt(12, this.fishCatchToday);
//			
//			stmt.setInt(13, this.gems);
//			stmt.setInt(14, this.genderID);
//			
//			stmt.setInt(15, this.healthID);
//			stmt.setInt(16, this.houseAmount);
//			stmt.setInt(17, this.houseAmountMax);
//			
//			stmt.setInt(18, this.income);
//			stmt.setLong(19, this.incomeTime);
//			
//			stmt.setLong(20, this.joinDate);
//			
//			stmt.setInt(21, this.keepAmount);
//			stmt.setInt(22, this.keepAmountMax);
//			stmt.setInt(23, this.kills);
//			
//			stmt.setLong(24, this.lastLogin);
//			stmt.setInt(25, this.level);
//			
//			stmt.setLong(26, this.playtime);
//			stmt.setLong(27, this.playtimeToday);
//			stmt.setInt(28, this.previousDonatorID);
//			stmt.setInt(29, this.propertyAmount);
//			stmt.setInt(30, this.propertyAmountMax);
//			
//			stmt.setLong(31, this.rentTime);
//			stmt.setInt(32, this.roomAmount);
//			stmt.setInt(33, this.roomAmountMax);
//			
//			stmt.setLong(34, this.salaryTime);
//			stmt.setInt(35, this.skillPoints);
//			stmt.setInt(36, this.spawnpointID);
//			stmt.setInt(37, this.specialSkillID);
//			stmt.setInt(38, this.specialskillPoints);
//			stmt.setInt(39, this.speedID);
//			stmt.setInt(40, this.strengthID);
//			
//			stmt.setInt(41, this.titleID);
//			stmt.setBoolean(42, this.tombDiscovered);
//			
//			stmt.setString(43, this.uuid.toString());
//			
//			stmt.setInt(44, this.votes);
//			
//			stmt.setInt(45, this.blocksBroken);
//			stmt.setInt(46, this.blocksBrokenToday);
//			stmt.setInt(47, this.blocksBrokenOre);
//			stmt.setInt(48, this.blocksBrokenOreToday);
//			stmt.setInt(49, this.blocksBrokenStone);
//			stmt.setInt(50, this.blocksBrokenStoneToday);
//			stmt.setInt(51, this.blocksBrokenWheat);
//			stmt.setInt(52, this.blocksBrokenWheatToday);
//			stmt.setInt(53, this.blocksBrokenWood);
//			stmt.setInt(54, this.blocksBrokenWoodToday);
//			stmt.setString(55, this.uuid.toString());
//			stmt.setString(56, this.username);
						
			statement = stmt2;
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return statement;
	}
	
	public void saveUser()
	{
		try
		{
			this.saveAssignments();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		new BukkitRunnable()
		{
			public void run()
			{
				PreparedStatement stmt = null;
				try
				{
					stmt = main.getConnection().prepareStatement(getSaveQuery());
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.messagesubjects + "Succesfully saved data of player " + username + ", with ID " + id + ", and uuid " + uuid);
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to save data of player " + username + ", with ID " + id + ", and uuid " + uuid);
				} finally
				{
					if (stmt != null)
					{
						try 
						{
							stmt.close();
						} catch (SQLException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}.runTaskAsynchronously(main);
	}
	
	public void refreshQuestMaximum()
	{
		if (titleID >= 12)
		{
			this.QuestMaximum = 3;
		}
	}
	
	public void refreshAssignmentMaximum()
	{
		if (titleID >= 15)
		{
			this.AssignmentDailyAmount++;
		}
		this.AssignmentDailyAmount = this.AssignmentDailyAmount+this.donatorID;
	}
	
	public void fetchSocial()
	{
		ArrayList<Integer> requestID = new ArrayList<Integer>();
		ArrayList<UUID> friends = new ArrayList<UUID>();
		ArrayList<UUID> requests = new ArrayList<UUID>();
		int userID = this.getID();
		
		try
		{
			PreparedStatement getRequests = main.getConnection().prepareStatement("Select * FROM FriendRequest WHERE PlayerTargetID=?;");
			getRequests.setInt(1, userID);
			
			ResultSet results = getRequests.executeQuery();
			while (results.next())
			{
				requestID.add(results.getInt("PlayerSenderID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Friends WHERE Player1ID=? OR Player2ID=?;");
			stmt.setInt(1, userID);
			stmt.setInt(2, userID);
			
			ResultSet results = stmt.executeQuery();

			while (results.next())
			{
				if (results.getInt("Player1ID") != userID)
				{
					friends.add(Users.fetchUUIDbyID(results.getInt("Player1ID")));
				} else
				if (results.getInt("Player2ID") != userID)
				{
					friends.add(Users.fetchUUIDbyID(results.getInt("Player1ID")));
				}
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		for (int ID : requestID)
		{
			UUID uuid = Users.fetchUUIDbyID(ID);
			if (uuid != null)
			{
				requests.add(uuid);
			}
		}
		this.friendList = friends;
		this.friendRequestList = requests;
	}
	
	public void setOfflineUser(boolean isOfflineUser)
	{
		this.isOfflineUser = isOfflineUser;
	}
	
//	public void addList()
//	{
//		users
//		main.users.add(this);
//	}
	
//	public void removeList()
//	{
//		main.users.remove(this);
//	}
	
	public void join(Player player)
	{
		this.player = player;
    	Users.CheckDuplicateAddress(user);
    	new BukkitRunnable()
    	{
    		public void run()
    		{
    			if (fetchedData)
    			{
        			this.cancel();
        			setDailyAssignments();
        			setLastLogin();
        	    	setSkillHealth();
        	    	setSkillSpeed();
        	    	Users.CheckScheduledRank(user);
        	    	Users.CheckScheduledItems(user);
    			}
    		}
    	}.runTaskTimerAsynchronously(main, 0, 10);
		
		if (this.isAfk())
		{
			if (this.getAfk().teleporting == false)
			{
				this.removeAfk();
			}
		}
		this.afkCommence = (System.currentTimeMillis()+ main.afkTime*1000);
		
//		this.addList();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "After joining: " + Users2.users);
	}
	
	public void quit()
	{
		this.checkMiniGames();
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Before leaving: " + Users2.users);
//		this.removeList();
		this.destroy();
	}
	
	public void destroy()
	{
		saveUser();
		if (this.isOfflineUser == true)
		{
			try
			{
				Main.offlineUsers.remove(this);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		Users2.Destroy(this);
	}
	
	public void refresh()
	{
		try
		{
			ResultSet data = this.fetchUser(this.uuid);
			this.setData(this.uuid, data);
		} catch(Exception e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Couldn't refresh data for player with username " + this.username);
		}
		
	}
	
	public void checkMiniGames()
	{
		Siege siege = Sieges.Sieges.findSiege(this);
		HideAndSeek hs = HideandSeeks.findHideAndSeek(user);
		if (siege != null)
		{
			siege.leavePlayer(this);
		}
		if (hs != null)
		{
			hs.leave(hs.getParticipant(user));
		}
	}
	
	public boolean inMiniGame()
	{
		boolean inMiniGame = false;
		
		if (Sieges.Sieges.findSiege(this) != null
				|| HideandSeeks.findHideAndSeek(user) != null)
		{
			inMiniGame = true;
		}
		
		return inMiniGame;
	}
	
	public boolean isOfflineUser()
	{
		return this.isOfflineUser;
	}
	
	public boolean isFetchedData()
	{
		return this.fetchedData;
	}
	
	public boolean isFetchedAddress()
	{
		return this.fetchedAddress;
	}
	
	public boolean inOwnerModus()
	{
		boolean ownermodus = false;
		
		if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
		{
			ownermodus = true;
		}
		
		return ownermodus;
	}
	
	public boolean inOnQuit()
	{
		boolean onquit = false;
		
		if (OwnerCommands.disableonquit.containsKey(uuid) || OwnerCommands.enableonquit.containsKey(uuid))
		{
			onquit = true;
		}
		
		return onquit;
	}
	
	public boolean inEnableOnQuit()
	{
		boolean enable = false;
		
		if (inOnQuit())
		{
			if (OwnerCommands.enableonquit.containsKey(uuid) && OwnerCommands.enableonquit.get(uuid) == true)
			{
				enable = true;
			}
		}
		
		return enable;
	}
	
	public void setOwnerMode(boolean enable)
	{
		if (enable)
		{
			if (CombatCheck.incombat.containsKey(player))
			{
				CombatCheck.incombat.remove(player);
			}
			main.ownermodus.put(player.getUniqueId(), true);
			for (Player target : Bukkit.getOnlinePlayers())
			{
				if (!target.hasPermission("k&k.staff"))
				{
					target.hidePlayer(player);
				}
			}
			try
			{
				ActionBar bar = new ActionBar(OwnerCommands.enabled);
				bar.sendToPlayer(player);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
//			player.sendMessage(OwnerCommands.enabled);
		} else
		{
			main.ownermodus.put(player.getUniqueId(), false);
			for (Player target : Bukkit.getOnlinePlayers())
			{
				target.showPlayer(player);
			}
			try
			{
				ActionBar bar = new ActionBar(OwnerCommands.disabled);
				bar.sendToPlayer(player);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
//			player.sendMessage(OwnerCommands.disabled);

		}
	}
	
	public boolean inStaffModus()
	{
		boolean staffmodus = false;
		
		if (main.staffmodus.containsKey(uuid) && main.staffmodus.get(uuid) == true)
		{
			staffmodus = true;
		}
		
		return staffmodus;
	}
	
	public void setStaffMode(boolean enable)
	{
		if (enable)
		{
			if (CombatCheck.incombat.containsKey(player))
			{
				CombatCheck.incombat.remove(player);
			}
			main.staffmodus.put(player.getUniqueId(), true);
			for (Player target : Bukkit.getOnlinePlayers())
			{
				if (!target.hasPermission("k&k.staff"))
				{
					target.hidePlayer(player);
				}
			}
			try
			{
				ActionBar bar = new ActionBar(OwnerCommands.senabled);
				bar.sendToPlayer(player);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
//			player.sendMessage(OwnerCommands.senabled);
		} else
		{
			main.staffmodus.put(player.getUniqueId(), false);
			for (Player target : Bukkit.getOnlinePlayers())
			{
				target.showPlayer(player);
			}
			try
			{
				ActionBar bar = new ActionBar(OwnerCommands.sdisabled);
				bar.sendToPlayer(player);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
//			player.sendMessage(OwnerCommands.sdisabled);
		}
		if (main.debug && !main.staffmodus.isEmpty())
		{
			for (UUID uuid : main.staffmodus.keySet())
			{
				Bukkit.getConsoleSender().sendMessage("Contains: " + this.username);
			}
		}
	}
	
	public Afk getAfk()
	{
		return this.afk;
	}
	
	public Long GetAfkCommence()
	{
		return this.afkCommence;
	}
	
	public InetAddress getAdress()
	{
		return this.address;
	}
		
	public Player getPlayer()
	{
		return this.player;
	}
	
	public int getAttackSpeedID()
	{
		return this.attackspeedID;
	}
	
	public int getBanditKills(boolean today)
	{
		if (today)
		{
			return this.banditKillsToday;
		} else
		{
			return this.banditKills;
		}
	}
	
	public boolean getBanned()
	{
		return this.banned;
	}
	
	public int getblocksBroken(boolean today)
	{
		if (today)
		{
			return this.blocksBrokenToday;
		} else
		{
			return this.blocksBroken;
		}
	}
	
	public int getblocksBrokenWood(boolean today)
	{
		if (today)
		{
			return this.blocksBrokenWoodToday;
		} else
		{
			return this.blocksBrokenWood;
		}
	}
	
	public int getblocksBrokenWheat(boolean today)
	{
		if (today)
		{
			return this.blocksBrokenWheatToday;
		} else
		{
			return this.blocksBrokenWheat;
		}
	}
	
	public int getblocksBrokenStone(boolean today)
	{
		if (today)
		{
			return this.blocksBrokenStoneToday;
		} else
		{
			return this.blocksBrokenStone;
		}
	}
	
	public int getblocksBrokenOre(boolean today)
	{
		if (today)
		{
			return this.blocksBrokenOreToday;
		} else
		{
			return this.blocksBrokenOre;
		}
	}
	
	public int getCoins()
	{
		return this.coins;
	}
	
	public int getDeaths()
	{
		return this.deaths;
	}
	
	public int getDefenseID()
	{
		return this.defenseID;
	}
	
	public int getDonatorID()
	{
		return this.donatorID;
	}
	
	public String getDonatorName()
	{
		return this.donatorName;
	}
	
	public int getExperience()
	{
		return this.experience;
	}
	
	public int getExperienceNext()
	{
		return this.expierienceNext;
	}
	
	public ArrayList<UUID> getFriendList()
	{
		return this.friendList;
	}
	
	public int getFriendAmount()
	{
		return this.friendAmount;
	}
	
	public ArrayList<UUID> getFriendRequestList()
	{
		return this.friendRequestList;
	}
	
	public int getFishCatch(boolean today)
	{
		if (today)
		{
			return this.fishCatchToday;
		} else
		{
			return this.fishCatch;
		}
	}
	
	public int getGems()
	{
		return this.gems;
	}
	
	public int getGenderID()
	{
		return this.genderID;
	}
	
	public String getGenderName()
	{
		return this.genderName;
	}
	
	public int getHealthID()
	{
		return this.healthID;
	}
	
	public int getHouseAmount(boolean maximum)
	{
		if (maximum)
		{
			return this.houseAmountMax;
		} else
		{
			return this.houseAmount;
		}
	}
	
	public boolean canBuyHouse()
	{
		boolean canBuy = false;
		
		if (getHouseAmount(true) > getHouseAmount(false)+1)
		{
			canBuy = true;
		}
		
		return canBuy;
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public int getIncome()
	{
		return this.income;
	}
	
	public Long getIncomeTime()
	{
		return this.incomeTime;
	}
	
	public Date getJoinDate()
	{
		return this.joinDate;
	}
	
	public int getKeepAmount(boolean maximum)
	{
		if (maximum)
		{
			return this.keepAmount;
		} else
		{
			return this.keepAmountMax;
		}
	}
	
	public int getKills()
	{
		return this.kills;
	}
	
	public Location getLastDeathLocation()
	{
		return this.lastDeathLocation;
	}
	
	public Long getLastLogin()
	{
		return this.lastLogin;
	}
	
	public int getLevel()
	{
		return this.level;
	}
	
	public Long getPlayTime(boolean today)
	{
		if (today)
		{
			return this.playtimeToday;
		} else
		{
			return this.playtime;
		}
	}
	
	public int getPreviousDonatorID()
	{
		return this.previousDonatorID;
	}
	
	public String getPreviousDonatorName()
	{
		return this.previousDonatorName;
	}
	
	public int getPropertyAmount(boolean maximum)
	{
		if (maximum)
		{
			return this.propertyAmountMax;
		} else
		{
			return this.propertyAmount;
		}
	}
	
	public Long getRentTime()
	{
		return this.rentTime;
	}
	
	public int getRoomAmount(boolean maximum)
	{
		if (maximum)
		{
			return this.roomAmountMax;
		} else
		{
			return this.roomAmount;
		}
	}
	
	public int getSalary()
	{
		return this.salary;
	}
	
	public Long getSalaryTime()
	{
		return this.salaryTime;
	}

	public int getSkillPoints(boolean special)
	{
		if (special)
		{
			return this.specialskillPoints;
		} else
		{
			return this.skillPoints;
		}
	}
	
	public int getSpawnpointID()
	{
		return this.spawnpointID;
	}
	
	public Location getSpawnpointLocation()
	{
		return this.spawnpointLocation;
	}
	
	public int getSpecialSkillID()
	{
		return this.specialSkillID;
	}
	
	public String getSpecialSkillName()
	{
		return this.specialSkillName;
	}
	
	public int getSpeedID()
	{
		return this.speedID;
	}
	
	public int getStrengthID()
	{
		return this.strengthID;
	}
	
	public int getTitleID()
	{
		return this.titleID;
	}
	
	public String getTitleName()
	{
		return this.titleName;
	}
	
	public boolean getTombDiscovered()
	{
		return this.tombDiscovered;
	}
	
	public Tutorial getTutorial()
	{
		return this.tutorial;
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public UUID getUUID()
	{
		return this.uuid;
	}
	
	public int getVotes()
	{
		return this.votes;
	}
	
	public HashMap<Integer, Integer> getSoldItems()
	{
		return this.soldItems;
	}
	
	public boolean isAfk()
	{
		if (this.getAfk() != null)
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public Location GetBanditLocation()
	{
		return this.banditLocation;
	}
	
	public void SetBanditLocation(Location location)
	{
		this.banditLocation = location;
	}
	
	public void RemoveBanditLocation()
	{
		this.banditLocation = null;
	}
	
	public void setAfk()
	{
		if (this.afk != null)
		{
			return;
		}
		try
		{
			this.afk = new Afk(this);
		} catch(Exception e)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Couldn't put player with UUID " + this.getUUID() + " in afk!");
			e.printStackTrace();
		}
	}
	
	public void removeAfk()
	{
		try
		{
			Bukkit.getConsoleSender().sendMessage("Stopped afk");
			this.getAfk().stopAfk();
			this.afk = null;
		} catch(Exception e)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Couldn't stop afk for player with UUID " + this.getUUID());
			e.printStackTrace();
		}
	}
	
	public void SetAfkCommence(Long futureCommence)
	{
		this.afkCommence = futureCommence;
	}
	
	public void RemoveAfkCommence()
	{
		this.afkCommence = null;
	}
	
	public void setCoins(int amount)
	{
		this.coins = amount;
	}
	
	public void addCoins(int amount)
	{
		int OldBalance = this.getCoins();
		int NewBalance = (OldBalance + amount);
		
		setCoins(NewBalance);
	}
	
	public void removeCoins(int amount)
	{
		int OldBalance = this.getCoins();
		
		int NewBalance = (OldBalance - amount);
		
		if (NewBalance < 0)
		{
			NewBalance = 0;
		}
		setCoins(NewBalance);
	}
	
	public void sendCoins(UUID targetUUID, int amount)
	{
		User target = new User(targetUUID);
		try 
		{
			target.addCoins(amount);
			this.removeCoins(amount);
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player " + this.username + " paid " + target.getUsername() + " " + amount + ". Target's new balance is: " + target.getCoins());
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		target.destroy();
	}
	
	public void setGems(int amount)
	{
		this.gems = amount;
	}
	
	public void addGems(int amount)
	{
		int OldBalance = this.getGems();
		int NewBalance = (OldBalance + amount);
		setGems(NewBalance);
	}
	
	public void removeGems(int amount)
	{
		int OldBalance = this.getGems();
		int NewBalance = (OldBalance - amount);
		if (NewBalance < 0)
		{
			NewBalance = 0;
		}
		setGems(NewBalance);
	}
	
	public void sendGems(UUID targetUUID, int amount)
	{
		User target = new User(targetUUID);
		try 
		{
			target.addGems(amount);
			this.removeGems(amount);
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player " + this.username + " paid " + target.getUsername() + " " + amount + ". Target's new balance is: " + target.getGems());
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		target.destroy();
	}
	
	public void setDonatorRank(int donatorID)
	{
		Donator donator = new Donator();
		int CurrentDonatorID = -1;
		String donatorName = donator.getDonatorName(donatorID);
		
		if (this.getDonatorID() != donatorID)
		{
			this.donatorName = donatorName;
			this.donatorID = donatorID;
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + this.username + "'s DonatorRank to " + this.donatorName);
		} else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "User already has that donator rank! Current: " + CurrentDonatorID + ", new: " + donatorID);
		}
//		this.updateScoreBoard();
	}
	
	public void changeGender()
	{		
		if (this.genderID == 1)
		{
			this.genderID = 0;
		} else
		{
			this.genderID = 1;
		}
	}
	
	public void saveSalaryTime()
	{
		Long current = System.currentTimeMillis();
		Long nextpayment = current + 3600000L;
		this.salaryTime = nextpayment;
	}
	
	public void saveIncomeTime()
	{
		Long current = System.currentTimeMillis();
		Long nextpayment = (current + 43200000L);
		this.incomeTime = nextpayment;
	}
	
	public void saveRentTime()
	{
		Long current = System.currentTimeMillis();
		Long nextrent = current + 3600000L;
		this.rentTime = nextrent;
	}
	
	public void removeRentTime()
	{
		this.rentTime = null;
	}
	
	public void setIncome(int amount)
	{
		this.income = amount;
	}
	
	public void addIncome(int amount)
	{
		int oldIncome = getIncome();
		int newIncome = oldIncome + amount;
		setIncome(newIncome);
		
	}

	public void removeIncome(int amount)
	{
		int oldIncome = getIncome();
		int newIncome = oldIncome - amount;
		
		if (newIncome < 0)
		{
			newIncome = 0;
		}
		setIncome(newIncome);
	}
	
	public void setVotes(int amount)
	{	
		this.votes = amount;
	}
	
	public void addVotes(int amount)
	{
		int oldVotes = this.getVotes();
		int newVotes = oldVotes + amount;
		
		setVotes(newVotes);
		
	}
	
	public void removeVotes(UUID uuid, int amount)
	{
		int oldVotes = this.getVotes();
		int newVotes = oldVotes - amount;
		
		if (newVotes < 0)
		{
			newVotes = 0;
		}
		setVotes(newVotes);
	}
	
	public void setKills(boolean bandit, int kills, int killsToday)
	{	
		if (bandit)
		{
			this.banditKills = kills;
			this.banditKillsToday = killsToday;
		} else
		{
			this.kills = kills;
		}
	}
	
	public void addKills(boolean bandit, int kills, int killsToday)
	{
		int oldkills = -1;
		int oldkillsToday = -1;
		if (bandit)
		{
			oldkills = this.getBanditKills(false);
			oldkillsToday = this.getBanditKills(true);
		} else
		{
			oldkills = this.getKills();
		}
		kills = oldkills+kills;
		killsToday = oldkillsToday+killsToday;
		
		
		setKills(bandit, kills, killsToday);
		
	}
	
	public void removeKills(boolean bandit, int kills, int killsToday)
	{
		int oldKills = -1;
		int oldKillsToday = -1;
		
		if (bandit)
		{
			oldKills = this.getBanditKills(false);
			oldKillsToday = this.getBanditKills(true);
		} else
		{
			oldKills = this.getKills();
		}
		
		kills = oldKills-kills;
		killsToday = oldKillsToday-killsToday;
		
		if (kills < 0)
		{
			kills = 0;
		}
		if (killsToday < 0)
		{
			killsToday = 0;
		}
		setKills(bandit, kills, killsToday);
	}
	
	public void setDeaths(int amount)
	{	
		this.deaths = amount;
	}
	
	public void addDeaths(int amount)
	{
		int oldDeaths = this.getDeaths();
		int newDeaths = oldDeaths + amount;
		this.setDeaths(newDeaths);
	}
	
	public void removeDeaths(UUID uuid, int amount)
	{
		int oldDeaths = this.getDeaths();
		int newDeaths = oldDeaths - amount;
		
		if (newDeaths < 0)
		{
			newDeaths = 0;
		}
		this.setDeaths(newDeaths);
	}
	
	public void setTitle(int titleID)
	{
		if (titleID > 18)
		{
			titleID = 18;
		}
		this.titleID = titleID;
	}
	
	public void addTitle(int amount)
	{
		int oldTitleID = this.getTitleID();
		int newTitleID = oldTitleID + amount;
		
		if (newTitleID > 18)
		{
			newTitleID = 18;
		}
		this.setTitle(newTitleID);	
	}
	
	public void removeTitleID(int amount)
	{
		int oldTitleID = this.getTitleID();
		int newTitleID = oldTitleID - amount;
		
		if (newTitleID < 0)
		{
			newTitleID = 0;
		}
		setTitle(newTitleID);
	}
	
	public void setSkillPoints(boolean special, int amount)
	{	
		this.skillPoints = amount;
	}
	
	public void addSkillPoints(boolean special, int amount)
	{
		int oldBalance = this.getSkillPoints(special);
		int newBalance = oldBalance + amount;
		this.setSkillPoints(special, newBalance);
	}
	
	public void removeSkillPoints(boolean special, int amount)
	{
		int oldBalance = this.getSkillPoints(special);
		int newBalance = oldBalance - amount;
		
		if (newBalance < 0)
		{
			newBalance = 0;
		}
		setSkillPoints(special, newBalance);
	}
	
	public HashMap<String, Integer> getSkillLevelList()
	{
		HashMap<String, Integer> list = new HashMap<String, Integer>();
		
		list.put("Strength", this.getStrengthID());
		list.put("Speed", this.getSpeedID());
		list.put("Health", this.getHealthID());
		list.put("AttackSpeed", this.getAttackSpeedID());
		list.put("Defense", this.getDefenseID());
		
		return list;
	}
	
	public void setSkillID(String skill, int amount)
	{
		if (skill.equalsIgnoreCase("strength"))
		{
			this.strengthID = amount;
		} else if (skill.equalsIgnoreCase("speed"))
		{
			this.speedID = amount;
		} else if (skill.equalsIgnoreCase("attackspeed"))
		{
			this.attackspeedID = amount;
		} else if (skill.equalsIgnoreCase("health"))
		{
			this.healthID = amount;
		} else if (skill.equalsIgnoreCase("defense"))
		{
			this.defenseID = amount;
		}
	}
	
	public void addSkillID(String skill, int amount)
	{
		int oldLevel = -1;
		if (skill.equalsIgnoreCase("strength"))
		{
			oldLevel = this.getStrengthID();
		} else if (skill.equalsIgnoreCase("speed"))
		{
			oldLevel = this.getSpeedID();
		} else if (skill.equalsIgnoreCase("attackspeed"))
		{
			oldLevel = this.getAttackSpeedID();
		} else if (skill.equalsIgnoreCase("health"))
		{
			oldLevel = this.getHealthID();
		} else if (skill.equalsIgnoreCase("defense"))
		{
			oldLevel = this.getDefenseID();
		}
		int newLevel = oldLevel + amount;
		
		if (newLevel > 7)
		{
			newLevel = 7;
		}
		setSkillID(skill, newLevel);
	}
	
	public void removeSkillID(String skill, int amount)
	{
		int oldLevel = -1;
		if (skill.equalsIgnoreCase("strength"))
		{
			oldLevel = this.getStrengthID();
		} else if (skill.equalsIgnoreCase("speed"))
		{
			oldLevel = this.getSpeedID();
		} else if (skill.equalsIgnoreCase("attackspeed"))
		{
			oldLevel = this.getAttackSpeedID();
		} else if (skill.equalsIgnoreCase("health"))
		{
			oldLevel = this.getHealthID();
		} else if (skill.equalsIgnoreCase("defense"))
		{
			oldLevel = this.getDefenseID();
		}
		int newLevel = oldLevel - amount;
		
		if (newLevel < 0)
		{
			newLevel = 0;
		}
		this.setSkillID(skill, newLevel);
	}
	
	public void setSkillHealth()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				Integer level = getHealthID();
				Integer health = new Skill().getSkillValue("Health", level);
				if (level < 7)
				{
					player.setHealthScale(20 +health);
				} else
				{
					player.setHealthScale(32);
				}
			}
		}.runTaskAsynchronously(main);
	}
	
	public void setSkillSpeed()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				Integer speedlevel =  getSpeedID();
				if (speedlevel == 1)
				{
		 			player.setWalkSpeed(0.22F);
				} else if (speedlevel == 2)
				{
		 			player.setWalkSpeed(0.24F);
				} else if (speedlevel == 3)
				{
		 			player.setWalkSpeed(0.26F);
				} else if (speedlevel == 4)
				{
		 			player.setWalkSpeed(0.28F);
				} else if (speedlevel == 5)
				{
		 			player.setWalkSpeed(0.30F);
				} else if (speedlevel == 6)
				{
		 			player.setWalkSpeed(0.32F);
				} else if (speedlevel == 7)
				{
		 			player.setWalkSpeed(0.32F);
				}
			}
		}.runTaskAsynchronously(main);
	}
	
	public void setHouseAmount(boolean maximum, int amount)
	{	
		if (maximum)
		{
			this.houseAmountMax = amount;
		} else
		{
			this.houseAmount = amount;
		}
	}
	
	public void addHouseAmount(boolean maximum, int amount)
	{
		int old = -1;
		if (maximum)
		{
			old = this.getHouseAmount(true);
		} else
		{
			old = this.getHouseAmount(false);
		}
		int newAmount = old + amount;
		this.setHouseAmount(maximum, newAmount);
	}
	
	public void removeHouseAmount(boolean maximum, int amount)
	{
		int old = -1;
		old = this.getHouseAmount(maximum);
		int newAmount = old - amount;
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		setHouseAmount(maximum, newAmount);
	}
	
	public void setPropertyAmount(boolean maximum, int amount)
	{	
		if (maximum)
		{
			this.propertyAmountMax = amount;
		} else
		{
			this.propertyAmount = amount;
		}
	}
	
	public void addPropertyAmount(boolean maximum, int amount)
	{
		int old = -1;
		if (maximum)
		{
			old = this.getPropertyAmount(true);
		} else
		{
			old = this.getPropertyAmount(false);
		}
		int newAmount = old + amount;
		
		this.setPropertyAmount(maximum, newAmount);
	}
	
	public void removePropertyAmount(boolean maximum, int amount)
	{
		int old = -1;
		if (maximum)
		{
			old = this.getPropertyAmount(true);
		} else
		{
			old = this.getPropertyAmount(false);
		}
		int newAmount = old - amount;
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		setPropertyAmount(maximum, newAmount);
	}
	
	public void setRoomAmount(boolean maximum, int amount)
	{	
		if (maximum)
		{
			this.roomAmountMax = amount;
		} else
		{
			this.roomAmount = amount;
		}
	}
	
	public void addRoomAmount(boolean maximum, int amount)
	{
		int old = -1;
		if (maximum)
		{
			old = this.getRoomAmount(true);
		} else
		{
			old = this.getRoomAmount(false);
		}
		int newAmount = old + amount;
		setRoomAmount(maximum, newAmount);
	}
	
	public void removeRoomAmount(boolean maximum, int amount)
	{
		int old = -1;
		if (maximum)
		{
			old = this.getRoomAmount(true);
		} else
		{
			old = this.getRoomAmount(false);
		}
		int newAmount = old - amount;
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		setRoomAmount(maximum, newAmount);
	}
	
	public void setKeepAmount(boolean maximum, int amount)
	{	
		if (maximum)
		{
			this.keepAmountMax = amount;
		} else
		{
			this.keepAmount = amount;
		}
	}
	
	public void addKeepAmount(boolean maximum, int amount)
	{
		int old = -1;
		if (maximum)
		{
			old = this.getKeepAmount(true);
		} else
		{
			old = this.getKeepAmount(false);
		}
		int newAmount = old + amount;
		setKeepAmount(maximum, newAmount);
	}
	
	public void removeKeepAmount(boolean maximum, int amount)
	{
		int old = -1;
		if (maximum)
		{
			old = this.getKeepAmount(true);
		} else
		{
			old = this.getKeepAmount(false);
		}
		int newAmount = old - amount;
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		setKeepAmount(maximum, newAmount);
	}
	
	public void setblocksBroken(String category, boolean today, int amount)
	{
		if (category.equalsIgnoreCase("overall"))
		{
			if (today)
			{
				this.blocksBrokenToday = amount;
			} else
			{
				this.blocksBroken = amount;
			}
		} else if (category.equalsIgnoreCase("wood"))
		{
			if (today)
			{
				this.blocksBrokenWoodToday = amount;
			} else
			{
				this.blocksBrokenWood = amount;
			}
		} else if (category.equalsIgnoreCase("wheat"))
		{
			if (today)
			{
				this.blocksBrokenWheatToday = amount;
			} else
			{
				this.blocksBrokenWheat = amount;
			}
		} else if (category.equalsIgnoreCase("stone"))
		{
			if (today)
			{
				this.blocksBrokenStoneToday = amount;
			} else
			{
				this.blocksBrokenStone = amount;
			}
		} else if (category.equalsIgnoreCase("ore"))
		{
			if (today)
			{
				this.blocksBrokenOreToday = amount;
			} else
			{
				this.blocksBrokenOre = amount;
			}
		} else
		{
			if (today)
			{
				this.blocksBrokenToday = amount;
			} else
			{
				this.blocksBroken = amount;
			}
		}
	}
	
	public void addblocksBroken(String category, boolean today, int amount)
	{
		int old = -1;
		if (category.equalsIgnoreCase("overall"))
		{
			old = this.getblocksBroken(today);
		} else if (category.equalsIgnoreCase("wood"))
		{
			old = this.getblocksBrokenWood(today);
		} else if (category.equalsIgnoreCase("wheat"))
		{
			old = this.getblocksBrokenWheat(today);
		} else if (category.equalsIgnoreCase("stone"))
		{
			old = this.getblocksBrokenStone(today);
		} else if (category.equalsIgnoreCase("ore"))
		{
			old = this.getblocksBrokenOre(today);
		} else
		{
			old = this.getblocksBroken(today);
		}
		int newAmount = old+amount;
		this.setblocksBroken(category, today, newAmount);
	}
	
	public void removeblocksBroken(String category, boolean today, int amount)
	{
		int old = -1;
		if (category.equalsIgnoreCase("overall"))
		{
			old = this.getblocksBroken(today);
		} else if (category.equalsIgnoreCase("wood"))
		{
			old = this.getblocksBrokenWood(today);
		} else if (category.equalsIgnoreCase("wheat"))
		{
			old = this.getblocksBrokenWheat(today);
		} else if (category.equalsIgnoreCase("stone"))
		{
			old = this.getblocksBrokenStone(today);
		} else if (category.equalsIgnoreCase("ore"))
		{
			old = this.getblocksBrokenOre(today);
		} else
		{
			old = this.getblocksBroken(today);
		}
		int newAmount = old-amount;
		this.setblocksBroken(category, today, newAmount);
	}
	
	public void setExperience(int amount, boolean expUpdate)
	{
		try 
		{	
			this.experience = amount;
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUsername() + "'s experience to " + amount);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		if (expUpdate)
		{
	        Bukkit.getServer().getPluginManager().callEvent(new ExperienceChangeEvent(this, amount , Bukkit.getPlayer(this.getUsername())));
		}
	}
	
	public void addExperience(int amount, boolean expUpdate)
	{
		int OldExp = this.getExperience();
		int NewExp = (OldExp + amount);
		setExperience(NewExp, expUpdate);
	}
	
	public void removeExperience(int amount, boolean expUpdate)
	{
		int OldExp = this.getExperience();
		int NewExp = (OldExp - amount);
		if (NewExp < 0)
		{
			NewExp = 0;
		}
		setExperience(NewExp, expUpdate);
	}
	
	public void setFriendAmount(int amount)
	{	
		this.friendAmount = amount;
	}
	
	public void addFriendAmount(int amount)
	{
		int oldAmount = this.getFriendAmount();
		int newAmount = oldAmount + amount;
		setFriendAmount(newAmount);
	}
	
	public void removeFriendAmount(int amount)
	{
		int oldAmount = this.getFriendAmount();
		int newAmount = oldAmount - amount;
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		setFriendAmount(newAmount);
	}
	
	public void setSpecialSkill(int specialskillID)
	{	
		this.specialSkillID = specialskillID;
		this.specialSkillName = this.specialSkill.getName(specialskillID);
	}
	
	public void removeSpecialSkillID()
	{
		setSpecialSkill(-1);
	}
	
	public void setSpecialSkillName(String specialSkill)
	{
		int SkillID = 0;
		String skill = null;
		int CurrentSpecialSkillID = this.getSpecialSkillID();
		
		switch (specialSkill)
		{
		case "ninja":  skill = "Ninja";
		case "shotbow": skill = "Shotbow";
		case "pickpocket": skill = "Pickpocket";
		case "forger": skill = "Forger";
		case "assassin": skill = "Assassin";
		case "juggernaut": skill = "Juggernaut";
		case "avenger": skill = "Avenger";
		case "none": skill = "None";
		case "default": skill = "None";
		default: skill = "None";
		}

		SkillID = this.specialSkill.getSpecialSkillID(skill);
		if (CurrentSpecialSkillID != SkillID)
		{
			this.setSpecialSkill(SkillID);
		}
	}
	
	public void addSoldItems(int productID, int amount)
	{
		HashMap<Integer, Integer> list = this.getSoldItems();
		if (list.isEmpty() || !list.containsKey(productID))
		{
			list.put(productID, amount);
		} else if (list.containsKey(productID))
		{
			list.put(productID, list.get(productID)+amount);
		} else
		{
			list.put(productID, amount);
		}
		this.soldItems = list;
	}
	
	public void removeSoldItems(int productID, int amount)
	{
		HashMap<Integer, Integer> list = this.getSoldItems();
		if (!list.isEmpty() && list.containsKey(productID))
		{
			if (list.get(productID) <= amount)
			{
				list.remove(productID);
			} else
			{
				list.put(productID, list.get(productID)-amount);
			}
		}
		this.soldItems = list;
	}
	
	public void setLastDeathLocation(Location location)
	{
		this.lastDeathLocation = location;
	}
	
	public void clearSoldItems()
	{
		this.soldItems.clear();
	}

	public ArrayList<String> getFriendNames()
	{
		ArrayList<String> friends = new ArrayList<String>();
		
		for (UUID tu : this.friendList)
		{
			friends.add(Users.fetchUsernamebyUUID(tu).toLowerCase());
		}
		
		return friends;
	}
	
	public ArrayList<String> getFriendRequestNames()
	{
		ArrayList<String> requests = new ArrayList<String>();
		
		for (UUID tu : this.friendRequestList)
		{
			requests.add(Users.fetchUsernamebyUUID(tu));
		}
		
		return requests;
	}
	
	public void saveFriendRequest(UUID targetUUID)
	{
		int targetID = Users.fetchIDbyUUID(targetUUID);
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO FriendRequest(PlayerSenderID, PlayerTargetID) VALUES (?, ?);");
			stmt.setInt(1, this.getID());
			stmt.setInt(2, targetID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "User: " + this.getUsername() + " sent a friend-request to " + Users.fetchUsernamebyUUID(targetUUID));
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}	
	}
	
	public void deleteFriendRequest(UUID targetUUID)
	{
		int targetID = Users.fetchIDbyUUID(targetUUID);
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM FriendRequest WHERE PlayerSenderID=? AND PlayerTargetID=?;");
			stmt.setInt(1, this.getID());
			stmt.setInt(2, targetID);
			
			stmt.executeUpdate();
			
			PreparedStatement stmt2 = main.getConnection().prepareStatement("DELETE FROM FriendRequest WHERE PlayerSenderID=? AND PlayerTargetID=?;");
			stmt2.setInt(2, this.getID());
			stmt2.setInt(1, targetID);
			
			stmt2.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "A friend-request between " + this.getUsername() + " and " + Users.fetchUsernamebyUUID(targetUUID) + " has been removed");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void ReplyFriendRequest(String targetUsername, boolean reply)
	{
		UUID targetUUID = Users.fetchUUIDbyUsername(targetUsername);
		
		if (this.isFriends(targetUUID) == false)
		{
			if (reply == true)
			{
				this.saveFriends(targetUUID);
			} else
			{
				this.deleteFriendRequest(targetUUID);
			}
		}
	}
	
	public void saveFriends(UUID targetUUID)
	{
		User target = new User(targetUUID);
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Friends(Player1ID, Player2ID) VALUES (?, ?);");
			stmt.setInt(1, this.getID());
			stmt.setInt(2, target.getID());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "User " + this.getUsername() + " is now friends with " + Users.fetchUsernamebyUUID(targetUUID));
			this.deleteFriendRequest(targetUUID);
			target.deleteFriendRequest(targetUUID);
			this.addFriendAmount(1);
			target.addFriendAmount(1);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		target.destroy();
	}
	
	public boolean isFriends(UUID targetUUID)
	{
		boolean isfriends = false;
		
		for (UUID targetuuid : this.friendList)
		{
			if (targetuuid.equals(targetUUID))
			{
				isfriends = true;
				break;
			}
		}
		return isfriends;
	}
	
	public void deleteFriend(UUID targetUUID)
	{
		User target = new User(targetUUID);
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Friends WHERE Player1ID=? AND Player2ID=? OR Player1ID=? AND Player2ID=?;");
			stmt.setInt(1, this.getID());
			stmt.setInt(2, target.getID());
			stmt.setInt(3, target.getID());
			stmt.setInt(4, this.getID());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "User " + this.getUsername() + " is no longer friends with " + target.getUsername());
			this.removeFriendAmount(1);
			target.removeFriendAmount(1);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		target.destroy();
	}
	
	public void saveSpawnpoint(int spawnpointID)
	{
		this.spawnpointID = spawnpointID;
		this.spawnpointLocation = this.spawnpoint.getSpawnPointLocation(spawnpointID);
	}
	
	public void removeSpawnpoint()
	{
		this.spawnpointID = 1;
		this.spawnpointLocation = this.spawnpoint.getSpawnPointLocation(1);
	}
	
	public void setPlayTime(Long time, boolean overall)
	{	
		if (overall)
		{
			this.playtime = time;
		} else
		{
			this.playtimeToday = time;
		}
	}
	
	public void SalaryPayout(UUID uuid)
	{		
		int titleID = this.getTitleID();
		
		this.addCoins(this.getSalary());
		String gender = this.getGenderName();
		
		if (gender.equalsIgnoreCase("male"))
		{
			player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your salary: " + ColorOptions.salarysubjects + this.getSalary());
		} else if (gender.equalsIgnoreCase("female"))
		{
			player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylady! you received your salary: " + ColorOptions.salarysubjects + this.getSalary());
		} else
		{
			player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your salary: " + ColorOptions.salarysubjects + this.getSalary());
		}
		player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 3.0F, 3.0F);
	}
	
	public void IncomePayout(UUID uuid)
	{

		int income = this.getIncome();
		if (income > 0)
		{
			this.addCoins(income);
			String gender = this.getGenderName();
			if (gender.equalsIgnoreCase("male"))
			{
				player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your income: " + ColorOptions.salarysubjects + income);
			} else if (gender.equalsIgnoreCase("female"))
			{
				player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylady! you received your income: " + ColorOptions.salarysubjects + income);
			}
			player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 3.0F, 3.0F);
		}
	}
	
	public void setCatchedFish(boolean today, int amount)
	{
		if (today)
		{
			this.fishCatchToday = amount;
		} else
		{
			this.fishCatch = amount;
		}
	}
	
	public void addCatchedFish(boolean today, int amount)
	{
		int old = -1;
		if (today)
		{
			old = this.getFishCatch(true);
		} else
		{
			old = this.getFishCatch(false);
		}
		int newAmount = (old + amount);
		setCatchedFish(today, newAmount);
	}
	
	public void removeCatchedFish(boolean today, int amount)
	{
		int old = -1;
		if (today)
		{
			old = this.getFishCatch(true);
		} else
		{
			old = this.getFishCatch(false);
		}
		int newAmount = (old - amount);
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		setCatchedFish(today, newAmount);
	}
	
	public String getPreviousDonatorName(UUID uuid)
	{
		String donator = null;
		try 
		{			
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE ID=?;");
			stmt.setInt(1, this.getPreviousDonatorID());
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				donator = results.getString("Name");
			}	
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}	
		return donator;
	}
	
	public void setTempDonator(int donatorID, Long expireTime)
	{		
		Users.setDonator(Bukkit.getConsoleSender(), uuid, donatorID);
		Long current = System.currentTimeMillis();
		Long expire = current + expireTime;
		
		try 
		{			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO DonatorTemp (UserID, DonatorID, TimeStart, TimeEnd, PreviousRankID) VALUES (?, ?, ?, ?, ?);");
			stmt.setInt(1, this.getID());
			stmt.setInt(2, donatorID);
			stmt.setLong(3, current);
			stmt.setLong(4, expire);
			stmt.setInt(5, this.getDonatorID());

			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Added " + this.getUsername() + " to the DonatorTemp table and set his temporarily donatorRank to " + donatorID + " for " + expireTime + " seconds!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public boolean isTempDonator()
	{
		boolean exist = false;
		int userID = this.getID();
		
		if (Users.getTempDonatorIDList().contains(userID))
		{
			exist = true;
		}
		return exist;
	}
	
	public void removeTempDonator()
	{
		int previousDonatorID = this.getPreviousDonatorID();
		try 
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM DonatorTemp WHERE UserID=?");
			stmt.setInt(1, this.getID());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Removed " + this.getUsername() + "'s from the DonatorTemp table and set user to previous rank");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		this.setDonatorRank(previousDonatorID);
		
	}
	
	public void updatePreviousRankID(int donatorID)
	{
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE DonatorTemp SET PreviousRankID=? WHERE UserID=?");
			stmt.setInt(1, donatorID);
			stmt.setInt(2, this.getID());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + this.getUsername() + "'s previous DonatorRankID in the DonatorTemp table and set it to: " + donatorID);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}		
	}
	
	public Long getTempDonatorExpireTime()
	{
		Long expire = null;
		try 
		{			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM DonatorTemp WHERE UserID=?");
			stmt.setInt(1, this.getID());
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				expire = results.getLong("TimeEnd");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return expire;
		
	}
	
	public void upgradeDonator(CommandSender sender)
	{
		Donator donator = new Donator();
		if (this.getID() != -1)
		{
			int donatorID = this.getDonatorID();
			
			if (donatorID < main.largestDonatorID)
			{
				Users.setDonator(sender, uuid, donatorID+1);
				if (this.isTempDonator())
				{
					this.updatePreviousRankID(donatorID+1);
				}
				if (Bukkit.getPlayer(uuid) != null)
				{
					Player target = Bukkit.getPlayer(uuid);
					target.sendMessage(donator.getDonatorColorSecondary(donatorID+1) + "You got knighted to a " + donator.getDonatorColorPrimary(donatorID+1) + donator.getDonatorName(donatorID+1) + donator.getDonatorColorSecondary(donatorID+1) + ", Congratulations!");
					target.playSound(target.getLocation(), SoundHandler.LEVEL_UP, 1.0F, 1.0F);
				}
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "Error: Player with uuid " + uuid + " cannot be found!");
		}
	}
	
	public void downgradeDonator(CommandSender sender)
	{
		Donator donator = new Donator();
		int donatorID = this.getDonatorID();
		
		if (donatorID > 0)
		{
			Users.setDonator(sender, uuid, donatorID-1);
			if (this.isTempDonator())
			{
				this.updatePreviousRankID(donatorID-1);
			}
			if (Bukkit.getPlayer(uuid) != null)
			{
				Player target = Bukkit.getPlayer(uuid);
				target.sendMessage(donator.getDonatorColorSecondary(donatorID-1) + "You got demoted to a " + donator.getDonatorColorPrimary(donatorID-1) + donator.getDonatorName(donatorID-1) + donator.getDonatorColorSecondary(donatorID-1) + ", Congratulations!");
				target.playSound(target.getLocation(), SoundHandler.LEVEL_UP, 1.0F, 1.0F);
			}
		}
	}
	
	public int getMultipliedInt(int integer)
	{
		Donator donator = new Donator();
		Float donatorMultiplier = donator.getDonatorMultiplier(this.getDonatorID());
		
		int finalint = (int) (integer*main.eventMultiplier);
		finalint = (int) (finalint*donatorMultiplier);
		
		return finalint;
	}
	
	public int getExpPart(int part)
	{
		Title title = new Title();
		int titleID = this.getTitleID();
		int finalExp = 10;
		if (titleID < 18)
		{
			int nextExp = title.getExpmin(titleID+1);
			int currentExp = title.getExpmin(titleID);
			int nettoExp = ((nextExp-currentExp)/100);
			finalExp = (int) nettoExp*part;
		} else
		{
			int nextExp = title.getExpmax(titleID);
			int currentExp = title.getExpmin(titleID);
			int nettoExp = ((nextExp-currentExp)/1000);
			finalExp = (int) nettoExp*part;
		}
		
		return finalExp;
	}
	
	public void setLastLogin()
	{
		try {
			this.lastLoginNew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(main.getTime()).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + this.getUsername() + "'s last login date to: " + main.getTime());
	}
	
	public void deleteUser()
	{
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Player WHERE UUID=?");
			stmt.setString(1, uuid.toString());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Deleted user with UUID " + uuid + " from the Database at " + main.getTime());
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		if (Bukkit.getPlayer(uuid) != null)
		{
			Player player = Bukkit.getPlayer(uuid);
			JoinEvents.newPlayers.add(uuid);
			main.CreateUser(player.getName(), uuid, "Male", player.getAddress().getAddress());
		}
	}
	
	public void checkHouseMaximum()
	{
		House house = new House();
		int houseAmount = this.getHouseAmount(false);
		int houseMax = this.getHouseAmount(true);
		int spawnpointID = this.getSpawnpointID();
		int userID = this.getID();
		if (houseAmount > houseMax)
		{
			Menu menu = new Menu();
			if (Bukkit.getPlayer(uuid) != null)
			{
				if (!ChatColor.stripColor(Bukkit.getPlayer(uuid).getOpenInventory().getTopInventory().getName()).equalsIgnoreCase("choose a house to remove"))
				{
					menu.openForcedHouseSell(this);
				}
			} else
			{
				for (int houseID : house.getHouseIDList(null))
				{
					if (house.getHouseOwnerID(houseID) == userID)
					{
						if (house.getHouseSpawnPoint(houseID) != spawnpointID && this.getHouseAmount(false) > 1)
						{
							Bukkit.getConsoleSender().sendMessage("Removed a house-owner of house with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
							house.RemoveHouseOwner(houseID);
							if (this.getHouseAmount(false) <= this.getHouseAmount(true))
							{
								break;
							}
						} else
						{
							Bukkit.getConsoleSender().sendMessage("Removed a house-owner of house with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
							house.RemoveHouseOwner(houseID);
							if (this.getHouseAmount(false) <= this.getHouseAmount(true))
							{
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public void checkPropertyMaximum()
	{
		Property property = new Property();
		int propertyAmount = this.getPropertyAmount(false);
		int propertyMax = this.getPropertyAmount(true);
		int spawnpointID = this.getSpawnpointID();
		int userID = this.getID();
		if (propertyAmount > propertyMax)
		{
			Menu menu = new Menu();
			if (Bukkit.getPlayer(uuid) != null)
			{
				if (!ChatColor.stripColor(Bukkit.getPlayer(uuid).getOpenInventory().getTopInventory().getName()).equalsIgnoreCase("choose a property to remove"))
				{
					menu.openForcedPropertySell(this);
				}
			} else
			{
				for (int houseID : property.getIDList(null, null))
				{
					if (property.getPropertyOwnerID(houseID) == userID)
					{
						if (property.getPropertySpawnPoint(houseID) != spawnpointID && this.getPropertyAmount(false) > 1)
						{
							Bukkit.getConsoleSender().sendMessage("Removed a property-owner of property with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
							property.RemovePropertyOwner(houseID);
							if (propertyAmount <= propertyMax)
							{
								break;
							}
						} else
						{
							Bukkit.getConsoleSender().sendMessage("Removed a property-owner of property with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
							property.RemovePropertyOwner(houseID);
							if (propertyAmount <= propertyMax)
							{
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public Inventory getOpenMenu()
	{
		Inventory menu = null;
		
		menu = this.getPlayer().getOpenInventory().getTopInventory();
		
		if (menu == null)
		{
			throw new NullPointerException("Open inventory request returned no opened inventory for player " + this.getID());
		}
		
		return menu;
	}
	
	public void inventoryAddItem(ItemStack item)
	{
		Inventory inv = player.getInventory();
		Inventory enderchest = player.getEnderChest();
		if (inv.firstEmpty() != -1)
		{
			inv.addItem(item);
			player.updateInventory();
		} else if (enderchest.firstEmpty() != -1)
		{
			enderchest.addItem(item);
			player.sendMessage(ColorOptions.error + "Inventory full, adding item to your enderchest!");
		} else
		{
			player.getWorld().dropItemNaturally(player.getLocation(), item);
			player.sendMessage(ColorOptions.error + "Inventory and enderchest full, dropping item near you!");
		}
	}
	
	public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut)
	{
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PlayerConnection connection = craftPlayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
        IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subTitle + "'}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
        Bukkit.getConsoleSender().sendMessage("Sent!");
    }
	
	public int getPing()
	{
		int ping = 0;
		
		CraftPlayer cp = (CraftPlayer) player; 
		EntityPlayer ep = cp.getHandle(); 
		ping = ep.ping;
		
		return ping; 
	}
	
	public String getChatPrefix()
	{
		String prefix = null;
		String username = player.getName();
		UUID uuid = player.getUniqueId();
		if (player.hasPermission("k&k.owner"))
		{
			prefix = ColorOptions.ownerformat + "[" + ColorOptions.ownersubjects + "OWNER" + ColorOptions.ownerformat + "]-{" + ColorOptions.ownersubjects + "" + ChatColor.BOLD + this.getTitleName() + ColorOptions.ownerformat + "}- " + ColorOptions.ownersubjects + username + ColorOptions.message + ": ";
		} else if (player.hasPermission("k&k.co-owner"))
		{
			prefix = ColorOptions.ownerformat + "[" + ColorOptions.ownersubjects + "CO-OWNER" + ColorOptions.ownerformat + "]-{" + ColorOptions.ownersubjects + "" + ChatColor.BOLD + this.getTitleName() + ColorOptions.ownerformat + "}- " + ColorOptions.ownersubjects + username + ColorOptions.message + ": ";
		} else
		if (player.hasPermission("k&k.staff"))
		{
			prefix = ColorOptions.staffformat + "[" + ColorOptions.staffsubjects + "STAFF" + ColorOptions.staffformat + "]-{" + ColorOptions.staffsubjects + "" + ChatColor.BOLD + this.getTitleName() + ColorOptions.staffformat + "}- " + ColorOptions.staffsubjects + username + ColorOptions.message + ": ";
		} else
		{
			/*
			String username = player.getName();
			player.setDisplayName(ColorOptions.nobleformat + "[B]-{" + ColorOptions.noblesubjects + "Noble " + this.getTitleName(pu) + ColorOptions.nobleformat + "}- " + ColorOptions.noblesubjects + username + ChatColor.WHITE);
			 */
			if (this.getDonatorName().equalsIgnoreCase("noble"))
			{
				prefix = ColorOptions.nobleformat + "-{" + ColorOptions.noblesubjects + "Noble " + this.getTitleName() + ColorOptions.nobleformat + "}- " + ColorOptions.noblesubjects + username + ColorOptions.message + ": ";
			} else
			if (this.getDonatorName().equalsIgnoreCase("royal"))
			{
				prefix = ColorOptions.royalformat + "-{" + ColorOptions.royalsubjects + "Royal " + this.getTitleName() + ColorOptions.royalformat + "}- " + ColorOptions.royalsubjects + username + ColorOptions.message + ": ";
			} else
			if (this.getDonatorName().equalsIgnoreCase("dragon blood"))
			{
				prefix = ColorOptions.dbformat + "-{" + ColorOptions.dbsubjects + "Dragon Blood " + this.getTitleName() + ColorOptions.dbformat + "}- " + ColorOptions.dbsubjects + username + ColorOptions.message + ": ";
			} else
			{
				prefix = ColorOptions.defaultformat + "-{" + ColorOptions.defaultsubjects + this.getTitleName() + ColorOptions.defaultformat + "}- " + ColorOptions.defaultsubjects + username + ColorOptions.message + ": ";
			}
		}
		
		return prefix;
	}
	
	public String getLeaveMessage()
	{
		String message = null;
		String prefix = null;
		ChatColor subject = null;
		String suffix = null;
		String rank = null;
		if (player.hasPermission("k&k.owner"))
	    {
			if (!user.inOwnerModus())
			{
				prefix = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD;
				subject = ColorOptions.messagesubjects;
				suffix = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD;
				rank = "★ Owner";
	    		message = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "★ Owner " + ColorOptions.messagesubjects + player.getName() + ChatColor.DARK_PURPLE + " left the kingdoms!";

			}
	    } else if (player.hasPermission("k&k.co-owner") && !player.hasPermission("k&k.owner"))
	    {
			if (!user.inOwnerModus())
			{
	    		message = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "★ Co-Owner " + ColorOptions.messagesubjects + player.getName() + ChatColor.DARK_PURPLE + " left the kingdoms!";

			}
	    } else
		if (player.hasPermission("k&k.staff") && !player.hasPermission("k&k.co-owner"))
		{
			if (!user.inStaffModus())
			{
				message = ColorOptions.falsecommand + "" + ChatColor.BOLD + "► Staff-member " + ColorOptions.messagesubjects + player.getName() + ColorOptions.falsecommand + " left the kingdoms!";
			}
		} else if (user.getDonatorName().equalsIgnoreCase("noble"))
		{
			message = ColorOptions.falsecommand + "► A " + ColorOptions.noblesubjects + "noble " + this.titleName + ColorOptions.falsecommand + " left the kingdoms!";
		} else if (user.getDonatorName().equalsIgnoreCase("royal"))
		{
			message = ColorOptions.falsecommand + "► A " + ColorOptions.royalsubjects + "royal " + this.titleName + ColorOptions.falsecommand + " left the kingdoms!";
		} else if (user.getDonatorName().equalsIgnoreCase("dragon blood"))
		{
			message = ColorOptions.falsecommand + "► A " + ColorOptions.dbsubjects + "dragon blood " + this.titleName + ColorOptions.falsecommand + " left the kingdoms!";
		}
				
		return message;
	}
	
	public Location getFrontLocation()
	{
		Location loc = null;
		
		Location eyeLocation = player.getEyeLocation();
		Location playerLocation = player.getLocation();
		
		Vector direction = playerLocation.getDirection();
		direction.setY(0);
		direction.normalize();
		direction.multiply(8);
		Bukkit.getConsoleSender().sendMessage("Vector: " + direction.toString());
		loc = eyeLocation.add(direction);
		
		return loc;
	}
	
	public void playSound(String sound)
	{
		if (sound.equalsIgnoreCase("succesclick"))
		{
			player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 1.0F);
		} else if (sound.equalsIgnoreCase("back"))
		{
			player.playSound(player.getLocation(), SoundHandler.NOTE_STICKS, 1.0F, 1.0F);
		} else if (sound.equalsIgnoreCase("failclick"))
		{
			player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
		} else if (sound.equalsIgnoreCase("successkillclick"))
		{
			player.playSound(player.getLocation(), SoundHandler.NOTE_PIANO, 1.0F, 2.0F);
		} else if (sound.equalsIgnoreCase("specialskill"))
		{
		    player.playSound(player.getLocation(), SoundHandler.WITHER_HURT, 1.0F, 1.0F);
		} else if (sound.equalsIgnoreCase("buyitem"))
		{
			player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 2.0F);
		}
	}
	
	public void setJoinMessage(PlayerJoinEvent e)
	{
	    if (this.player.hasPermission("k&k.owner"))
	    {
	    	String joinmsg = ColorOptions.ownerformat + "" + ChatColor.BOLD + "★ Owner " + ColorOptions.ownersubjects + player.getName() + ColorOptions.ownerformat + " entered the kingdoms!";
	    	if (this.inOwnerModus())
	    	{
	    		e.setJoinMessage(null);
	    		for (Player players : Bukkit.getOnlinePlayers())
	    		{
	    			if (!players.hasPermission("k&k.staff"))
					{
						players.hidePlayer(player);
					} 
	    		}
	    	} else if (this.inOnQuit())
	    	{
	    		if (this.inEnableOnQuit())
	    		{
	    			this.setOwnerMode(true);
	    			e.setJoinMessage(null);
	    		} else
	    		{
	    			this.setOwnerMode(false);
					e.setJoinMessage(joinmsg);
	    		}
	    	} else
	    	{
	    		e.setJoinMessage(joinmsg);
	    	}
	    } else if (this.player.hasPermission("k&k.co-owner") && !this.player.hasPermission("k&k.owner"))
	    {
	    	String joinmsg = ColorOptions.ownerformat + "" + ChatColor.BOLD + "★ Co-Owner " + ColorOptions.ownersubjects + player.getName() + ColorOptions.ownerformat + " entered the kingdoms!";
	    	if (this.inOwnerModus())
	    	{
	    		e.setJoinMessage(null);
	    		for (Player players : Bukkit.getOnlinePlayers())
	    		{
	    			if (!players.hasPermission("k&k.staff"))
					{
						players.hidePlayer(player);
					} 
	    		}
	    	} else if (this.inOnQuit())
	    	{
	    		if (this.inEnableOnQuit())
	    		{
	    			this.setOwnerMode(true);
	    			e.setJoinMessage(null);
	    		} else
	    		{
	    			this.setOwnerMode(false);
					e.setJoinMessage(joinmsg);
	    		}
	    	} else
	    	{
	    		e.setJoinMessage(joinmsg);
	    	}
	    } else
		if (this.player.hasPermission("k&k.staff") && !this.player.hasPermission("k&k.owner") && !this.player.hasPermission("k&k.co-owner"))
		{
	    	String joinmsg = ColorOptions.staffformat + "" + ChatColor.BOLD + "► Staff-member " + ColorOptions.staffsubjects + player.getName() + ColorOptions.staffformat + " entered the kingdoms!";
	    	if (this.inStaffModus())
	    	{
	    		e.setJoinMessage(null);
	    		for (Player players : Bukkit.getOnlinePlayers())
	    		{
	    			if (!players.hasPermission("k&k.staff"))
					{
						players.hidePlayer(player);
					} 
	    		}
	    	} else if (this.inOnQuit())
	    	{
	    		if (this.inEnableOnQuit())
	    		{
	    			this.setStaffMode(true);
	    			e.setJoinMessage(null);
	    		} else
	    		{
	    			this.setStaffMode(false);
					e.setJoinMessage(joinmsg);
	    		}
	    	} else
	    	{
	    		e.setJoinMessage(joinmsg);
	    	}
		} else if (this.getDonatorName().equalsIgnoreCase("noble"))
		{
			e.setJoinMessage(Donator.getDonatorColorSecondary(donatorID) + "► A " + Donator.getDonatorColorPrimary(donatorID) + "noble " + this.getTitleName() + Donator.getDonatorColorSecondary(donatorID) + " entered the kingdoms!");
		} else if (this.getDonatorName().equalsIgnoreCase("royal"))
		{
			e.setJoinMessage(Donator.getDonatorColorSecondary(donatorID) + "► A " + Donator.getDonatorColorPrimary(donatorID) + "royal " + this.getTitleName() + Donator.getDonatorColorSecondary(donatorID) + " entered the kingdoms!");
		} else if (this.getDonatorName().equalsIgnoreCase("dragon blood"))
		{
			e.setJoinMessage(Donator.getDonatorColorSecondary(donatorID) + "► A " + Donator.getDonatorColorPrimary(donatorID) + "dragon blood " + this.getTitleName() + Donator.getDonatorColorSecondary(donatorID) + " entered the kingdoms!");
		} else
		{
			e.setJoinMessage(null);
		}
	}
	
	public void setDailyAssignments()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				ZonedDateTime LastLogin = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastLogin), main.getZoneId());
//				Calendar LastLogin = new GregorianCalendar();
//				LastLogin.setTime(new Date(lastLogin));
//				Date date = new Date(lastLogin);
				if (LastLogin.getYear() == main.getDate().getYear() && LastLogin.getDayOfYear() == main.getDate().getDayOfYear())
				{
					ResultSet data = Assignments.Assignments.fetchAssignments(user);
					try {
						while (data.next())
						{
							Integer ID = data.getInt("ID");
							String Name = data.getString("Name");
							String Description = data.getString("Description");
							Integer GoalAmount = data.getInt("GoalAmount");
							Integer ProgressAmount = data.getInt("ProgressAmount");
							Integer CoinReward = data.getInt("CoinReward");
							Integer GemReward = data.getInt("GemReward");
							Integer ExperienceReward = data.getInt("ExperienceReward");
							boolean IsDaily = data.getBoolean("IsDaily");
							boolean IsCompleted = data.getBoolean("IsCompleted");
							Integer TypeID = data.getInt("TypeID");

							Assignment assignment = new Assignment(TypeID, Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);

							assignment.setSavedData(ID, TypeID, ProgressAmount, IsCompleted);

							assignment = Assignments.Assignments.fetchChildData(assignment);
							assignment.asign(user, IsDaily, -1);
						}
						if (AssignmentList.isEmpty())
						{
							createDailyAssignments();
							return;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
				{
					Assignments.Assignments.removeDailyAssignments(user);
					
					createDailyAssignments();
				}
				
			}
		}.runTaskAsynchronously(main);
	}
	
	public void createDailyAssignments()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				//Create new assignments
				List<Integer> IDList = Assignments.Assignments.fetchAssignmentIDList();
				List<Integer> addedTypeIDList = new ArrayList<Integer>();
				while(true)
				{
					if (addedTypeIDList.size() < AssignmentDailyAmount)
					{
						Integer TypeID = main.getRandom(1, IDList.size());
						if (!addedTypeIDList.contains(TypeID))
						{
							Assignment assignment = Assignments.Assignments.createAssignment(user, TypeID, null, null, -1, -1, -1, -1, true);
							assignment.asign(user, true, -1);
							addedTypeIDList.add(TypeID);
						} else
						{
							continue;
						}
					} else
					{
						break;
					}
				}
			}
		}.runTaskAsynchronously(main);
	}
	
	public void saveAssignments()
	{
		Assignments.Assignments.removeDailyAssignments(user);
		
		for (Assignment assignment : AssignmentList)
		{
			if (assignment instanceof AssignmentTravelSpecific)
			{
				((AssignmentTravelSpecific) assignment).saveAll();
			} else if (assignment instanceof AssignmentTravelRandom)
			{
				((AssignmentTravelRandom) assignment).saveAll();
			} else if (assignment instanceof AssignmentTravelDistance)
			{
				((AssignmentTravelDistance) assignment).saveAll();
			} else if (assignment instanceof AssignmentKill)
			{
				((AssignmentKill) assignment).saveAll();
			} else if (assignment instanceof AssignmentHarvestRandom)
			{
				((AssignmentHarvestRandom) assignment).saveAll();
			} else if (assignment instanceof AssignmentFoodConsumeRandom)
			{
				((AssignmentFoodConsumeRandom) assignment).saveAll();
			} else if (assignment instanceof AssignmentEnterPropertySpecific)
			{
				((AssignmentEnterPropertySpecific) assignment).saveAll();
			}
		}
		Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully saved all daily assignments of player with ID " + user.getID());
	}
	
	public void addAssignment(Assignment assignment, int Index)
	{
		if (!this.AssignmentList.contains(assignment))
		{
			if (Index > -1)
			{
				this.AssignmentList.add(Index, assignment);
			} else
			{
				this.AssignmentList.add(assignment);
			}
		}
	}
	
	public void removeAssignment(Assignment assignment)
	{
		if (this.AssignmentList.contains(assignment))
		{
			this.AssignmentList.remove(assignment);
		}
	}
	
	public List<Assignment> getAssignmentList()
	{
		return this.AssignmentList;
	}
	
	public void addQuest(Quest quest, int Index)
	{
		if (!this.QuestList.contains(quest))
		{
			if (Index > -1)
			{
				this.QuestList.add(Index, quest);
			} else
			{
				this.QuestList.add(quest);
			}
		}
	}
	
	public void removeQuest(Quest quest)
	{
		if (this.QuestList.contains(quest))
		{
			this.QuestList.remove(quest);
		}
	}
	
	public HashMap<UUID, Integer> getMentionDelay() {
		return mentionDelay;
	}
	
	public void setMentionDelay(HashMap<UUID, Integer> mentionDelay) {
		this.mentionDelay = mentionDelay;
	}
	
	public List<Quest> getQuestList()
	{
		return this.QuestList;
	}
	
	public User getAvengerTarget()
	{
		return this.avengerTarget;
	}
	
	public void setAvengerTarget(User target)
	{
		this.avengerTarget = target;
	}
	
	public List<ItemStack> getKeepItems()
	{
		return this.keepItems;
	}
	
	public void setKeepItems(List<ItemStack> items)
	{
		this.keepItems.addAll(items);
	}
	
	public void addKeepItems(ItemStack item)
	{
		this.keepItems.add(item);
	}
	
	public void resetKeepItems()
	{
		this.keepItems.clear();
	}
	
	public boolean inSafeZone()
	{
		Town town = new Town();
		House house = new House();
		boolean safe = false;
		
		Location location = this.getPlayer().getLocation();
		RegionManager manager = Worldguard.getRegionManager(location.getWorld());
		Integer townID = Worldguard.getStructureIDbyRegion("town", location, manager);
		Integer propertyID = Worldguard.getStructureIDbyRegion("property", location, manager);

		if (propertyID != null)
		{
			safe = true;
		} else
		if (townID != null && townID != town.getTownID("wilderness"))
		{
			if (!Worldguard.isArenaBattleground(location, manager))
			{
				safe = true;
			}
		}
		
		return safe;
	}
	
	public String getEyeDirection()
	{
		String direction = null;
		
		float Yaw = this.getPlayer().getLocation().getYaw();
		
		if (Yaw <= 22.5F && Yaw >= -22.5F)
		{
			direction = "North";
		} else if (Yaw >= 22.5F && Yaw <= 67.5F)
		{
			direction = "East";
		} else
		{
			
		}
		
		return direction;
	}
	
	public void pushBack()
	{
		Vector directionVector = this.player.getLocation().getDirection().normalize();
		this.player.setVelocity(this.player.getVelocity().add(directionVector.multiply(-2)));
	}
	
	public void sendMessage(List<String> message)
	{
		for (String string : message)
		{
			this.getPlayer().sendMessage(string);
		}
	}
	
	public void sendMessage(String message)
	{
		this.getPlayer().sendMessage(message);
	}
	
	public void TeleportSpawn()
	{
		Town town = new Town();
		//Later de discoer check toevoegen voor kardenna
		if (spawnpoint.getSpawnPointID("spawn") != null)
		{
			Integer spawnpointID = spawnpoint.getSpawnPointID("spawn");
			
			if (user.getSpawnpointID() != spawnpointID)
			{
				Integer userSpawn = user.getSpawnpointID();
				spawnpoint.teleport(user, spawnpoint.getSpawnPointLocation(userSpawn));
			} else if (town.getUserIDListbyTown(town.getTownID("kardenna")).contains(user.getID()))
			{
				spawnpoint.teleport(user, spawnpoint.getSpawnPointLocation(spawnpointID));

			} else
			{
				spawnpoint.teleport(user, spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("new")));
			}
		} else
		{
			if (player.hasPermission("k&k.spawnpoint") || user.inOwnerModus() || player.isOp())
			{
				player.sendMessage(ColorOptions.falsecommand + "No spawn-location has been set! This might cause glitches or errors");
			}
		}
	}
}
