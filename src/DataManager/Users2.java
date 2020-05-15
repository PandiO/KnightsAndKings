/**
 * 
 */
package DataManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Kits.Kit;
import Main.Main;
import Minigames.Transport;
import Products.Product;
import Scoreboards.Scoreboard;
import SpawnPoints.SpawnPoint;
import Tutorial.Tutorial;
import Users.User;
import Users.offlineUser;

/**
 * @author pandi
 *
 */
public interface Users2 
{
public static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<User>();
	
	/**
	 * Instantiates a new User instance for the given UUID, also checks if an instance can be found in the Online User list. 
	 * If no instance is found, it checks if a record exists and returns null if not.
	 * @param uuid The UUID of the user that needs to be instantiated
	 * @param newUser Boolean for additional tasks for new users
	 * @return The user instance if alrteady present or if instantiated. Null if no record exists in the Database
	 */
	public static User InstantiateUser(UUID uuid, boolean newUser)
	{
		User user = FindUser(uuid);
		
		if (user != null)
		{
			return user;
		}
		
		if (!ExistUser(uuid))
		{
			return null;
		}
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Player WHERE UUID = ?");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				user = new User(uuid);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return user;
	}
	
	/**
	 * Retrieves the user's data from the Database
	 * @param uuid The UUID of the user that needs to be fetched
	 * @return The ResultSet of the user's dataset
	 */
	public static ResultSet FetchUser(UUID uuid)
	{
		ResultSet result = null;
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = Main.getConnection().prepareStatement("Select * From Player Where UUID=?;");	
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
	
	/**
	 * Fetches the UUID of a user by ID
	 * @param userID The ID of the user
	 * @return The UUID of the user if found. Null if nothing is found
	 */
	public static UUID FetchUUIDbyID(Integer userID)
	{
		UUID uuid = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("Select * FROM Player WHERE ID=?;");
			stmt.setInt(1, userID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				uuid = UUID.fromString(results.getString("UUID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return uuid;
	}
	
	/**
	 * Fetches the UUID of a user by Username
	 * @param username The Username of the user
	 * @return The UUID of the user if found. Null if nothing is found
	 */
	public static UUID FetchUUIDbyUsername(String username)
	{
		UUID uuid = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("Select * FROM Player WHERE Username=?;");
			stmt.setString(1, username);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				uuid = UUID.fromString(results.getString("UUID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return uuid;
	}
	
	/**
	 * Finds a user instance in the list of online users. Returns null if no instance is found.
	 * @param uuid The UUID of the user instance that needs to be found
	 * @return The user instance if a match of the UUID has been found. Null if no instance is found.
	 */
	public static User FindUser(UUID uuid)
	{
		User user = null;
		
		for (User u : users)
		{
			if (u.getUUID() == uuid)
			{
				return u;
			}
		}
		
	
		return user;
	}
	
	/**
	 * Checks in the Database if a user record with the given UUID exists
	 * @param uuid The UUID of the user that needs to be checked
	 * @return True if the Database contains a record with this UUID, false if not
	 */
	public static boolean ExistUser(UUID uuid)
	{
		boolean exist = false;
		
		try 
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM Player WHERE UUID = ?");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				exist = true;
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return exist;
	}
	
	/**
	 * Handles tasks for new players such as starting a tutorial. This method needs to be called after the new player's user instance is created
	 * @param player The new player
	 */
	public static void NewPlayer(Player player)
	{
		SpawnPoint spawnpoint = new SpawnPoint();
		offlineUser offlineUser = new offlineUser();
		Kit kit = new Kit();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = FindUser(uuid);
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
		if (spawnpoint.getSpawnPointID("new") != null)
    	{
    		player.teleport(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("new")));
    	} else if (spawnpoint.getSpawnPointID("spawn") != null)
    	{
    		player.teleport(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("spawn")));
    	}
    	user.sendTitle(ChatColor.BLUE + "Knights and Kings", ColorOptions.message + "Welcome " + ColorOptions.messageformat + player.getName() + ColorOptions.message + "!", 1, 2, 1);
    	new BukkitRunnable()
    	{
    		public void run()
    		{
    			if (Main.debug)
    			{
    				Bukkit.getConsoleSender().sendMessage("Second title fired!");
    			}
		    	offlineUser.sendTitle(player, ChatColor.BLUE + "Introduction", ColorOptions.message + "Please start the " + ColorOptions.messagesubjects + "Introduction Tutorial", 1, 4, 2);
    		}
    	}.runTaskLater(Main.getPlugin(Main.class), 5*20);
    	player.sendMessage(ColorOptions.messageachievement + "Welcome " + ColorOptions.messagesubjects + player.getName() + ColorOptions.messageachievement + ", nice of you to come by!");
		
    	Tutorial tutorial = new Tutorial();
    	tutorial.createTutorial(user, "intro");
    	
    	kit.starterKit(player);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable()
    	{
            public void run()
            {
    	    	Main.newPlayers.remove(uuid);
            }
        }, 3*20);
	}
	
	/**
	 * Checks and sets the user into it's new rank if this was scheduled (A rank gets scheduled if the user is offline on the time of purchase)
	 * @param user The user that needs to be checked
	 */
	public static void CheckScheduledRank(User user)
	{
		UUID uuid = user.getUUID();
    	if (Main.scheduledDonator.containsKey(uuid))
    	{
    		new BukkitRunnable()
    		{
    			public void run()
    			{
    				user.setDonatorRank(Main.scheduledDonator.get(uuid));
    			}
    		}.runTaskLater(Main.getPlugin(Main.class), 5*20);
    	}
	}
	
	/**
	 * Checks and adds scheduled items to the users inventory (Items get scheduled if the user is offline at the time of purchasing)
	 * @param user The user that needs to be checked (Mainly every user that joins)
	 */
	public static void CheckScheduledItems(User user)
	{
		UUID uuid = user.getUUID();
    	if (Main.scheduledgive.containsKey(uuid))
    	{
    		new BukkitRunnable()
    		{
    			public void run()
    			{
    	    		Product product = new Product();
    	    		for (Integer productID : Main.scheduledgive.get(uuid).keySet())
    	    		{
    	    			product.giveProduct(Bukkit.getConsoleSender(), user, productID, Main.scheduledgive.get(uuid).get(productID));
    	    		}
    			}
    		}.runTaskLater(Main.getPlugin(Main.class), 6*20);
    	}
	}
	
	/**
	 * Sends a message to all users that have certain permissions and ranks
	 * @param message The message to be sent to the users
	 */
	public static void SendStaffMessage(List<String> message)
	{
		for (Player op : Bukkit.getOnlinePlayers())
		{
			if (op.isOp() || op.hasPermission("k&k.staff") || op.hasPermission("k&k.owner"))
			{
	    		op.playSound(op.getLocation(), SoundHandler.NOTE_PLING, 0.5F, 1.0F);
				for (String msg : message)
				{
					op.sendMessage(ColorOptions.message + "[" + ColorOptions.KAKColor + "StaffChat" + ColorOptions.message + "] " + msg);
				}
			}
		}
	}
	
	/**
	 * Checks if the ip address a user joins from is already used. 
	 * If so, sending a message to all online staff-members with the usernames of the users that join from the same address
	 * @param user
	 */
	public static void CheckDuplicateAddress(User user)
	{
		offlineUser offlineUser = new offlineUser();
		Player player = user.getPlayer();
    	if (offlineUser.isUsedAddress(player.getAddress().getAddress()))
    	{
			List<String> alternativeNames = new ArrayList<String>();
			for (Integer userID : offlineUser.getUserIDlistByAddress(player.getAddress().getAddress()))
			{
				String username = offlineUser.getUserName(FetchUUIDbyID(userID));
				if (!username.equalsIgnoreCase(player.getName()))
				{
					alternativeNames.add(offlineUser.getUserName(FetchUUIDbyID(userID)));
				}
			}
			SendStaffMessage(Arrays.asList(ColorOptions.message + "► Player " + ColorOptions.messagesubjects + player.getName() + ColorOptions.message + " joined using an IP address that has already been used!",
					ColorOptions.message + ColorOptions.messageArrow + "Other users(" + alternativeNames.size() +"): " + alternativeNames));
    	}
	}
	
	/**
	 * Gets the transport minigame instance of a user
	 * @param user The user you want the transport instance from
	 * @return The transport instance if present, null if not
	 */
	public static Transport GetTransport(User user)
	{
		Transport transport = null;
		
		for (Transport t : Main.transports)
		{
			if (t.getPlayer().getUniqueId() == user.getUUID())
			{
				transport = t;
				break;
			}
		}
		
		return transport;
	}
	
	/**
	 * Gets the avenger user by the target.
	 * @param user The user that might be a avenger target
	 * @return The user that is avenging himself on the user param
	 */
	public static User GetAvenger(User user)
	{
		User avenger = null;
		
		for (User u : users)
		{
			if (u.getAvengerTarget() == user)
			{
				avenger = u;
				break;
			}
		}
		
		return avenger;
	}
	
	/**
	 * Clears some daily statistics of users in the Database
	 */
	public static void ClearDailyUserStats()
	{
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = Main.getConnection().prepareStatement("UPDATE Player SET "
					+ "PlayTimeToday=0, "
					+ "BlocksBrokenToday=0, "
					+ "BlocksBrokenOreToday=0, "
					+ "BlocksBrokenStoneToday=0, "
					+ "BlocksBrokenWheat=0, "
					+ "BlocksBrokenWoodToday=0, "
					+ "BanditKillsToday=0, "
					+ "FishCatchToday=0;");
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes the user instance from the users list and destroys the instance.
	 * @param user The User instance that needs to be destroyed
	 */
	public static void Destroy(User user)
	{
		users.remove(user);
		user = null;
		System.gc();
	}
	
	/**
	 * Updates the Bukkit scoreboard (Tab list etc.)
	 * @param users A list of users who needs to receive the updated scoreboard
	 */
	public static void UpdateScoreBoard(List<User> Users)
	{
		Scoreboard board = new Scoreboard();
		board.setBoard(Users);
		
	}
}
