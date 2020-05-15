package Users;

import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import DataManager.Users2;
import Donator.Donator;
import Exceptions.UserIsNpcException;
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
import net.citizensnpcs.api.CitizensAPI;

public final class Users implements Listener
{
	static SpawnPoint spawnpoint = new SpawnPoint();
	static Kit kit = new Kit();
	static offlineUser offlineUser = new offlineUser();
	static Main main = Main.getPlugin(Main.class);

	public static void destroy(User user)
	{
		user = null;
		System.gc();
	}
	
	public static UUID fetchUUIDbyID(Integer userID)
	{
		UUID uuid = null;
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE ID=?;");
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
	
	public static Integer fetchIDbyUUID(UUID uuid)
	{
		Integer userID = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				userID = results.getInt("ID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return userID;
	}
	
	public static String fetchUsernamebyUUID(UUID uuid)
	{
		String username = null;
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				username = results.getString("Username");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return username;
	}
	
	public static UUID fetchUUIDbyUsername(String username)
	{
		UUID uuid = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE Username=?;");
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
	
	public static void clearPlayTimeToday()
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET PlayTimTodaye=0;");
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage("Succesfully cleared the playtime of today!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void setDonator(CommandSender sender, UUID uuid, Integer donatorID)
	{
		User user = new User(uuid);
		Product product = new Product();
		Donator donator = new Donator();
		String donatorName = donator.getDonatorName(donatorID);
		try
		{
			if (user.getID() != -1)
			{
				String targetUsername = user.getUsername();
				user.setDonatorRank(donatorID);
				if (user.isTempDonator())
				{
					user.updatePreviousRankID(donatorID);
				}
				if (Bukkit.getPlayer(uuid) != null)
				{
					if (donatorName.equalsIgnoreCase("noble"))
					{
						sender.sendMessage(ColorOptions.nobleformat + "You knighted " + ColorOptions.noblesubjects + targetUsername + ColorOptions.nobleformat + " to a " + ColorOptions.noblesubjects + donatorName);
						product.giveProduct(sender, user, product.getProductID("rareswordbox", false), 2);
					} else if (donatorName.equalsIgnoreCase("royal"))
					{
						sender.sendMessage(ColorOptions.royalformat + "You knighted " + ColorOptions.royalsubjects + targetUsername + ColorOptions.royalformat + " to a " + ColorOptions.royalsubjects + donatorName);
						product.giveProduct(sender, user, product.getProductID("legendaryswordbox", false), 1);
					} else if (donatorName.equalsIgnoreCase("dragonblood"))
					{
						sender.sendMessage(ColorOptions.dbformat + "You knighted " + ColorOptions.dbsubjects + targetUsername + ColorOptions.dbformat + " to a " + ColorOptions.dbsubjects + donatorName);
						product.giveProduct(sender, user, product.getProductID("legendaryswordbox", false), 2);
					} else if (donatorName.equalsIgnoreCase("default"))
					{
						sender.sendMessage(ColorOptions.messageformat + "You set " + ColorOptions.messagesubjects + targetUsername + ColorOptions.messageformat + " to " + ColorOptions.messagesubjects + donatorName);
					}
					Player target = Bukkit.getPlayer(uuid);
					target.sendMessage(donator.getDonatorColorSecondary(donatorID) + "You got knighted to a " + donator.getDonatorColorPrimary(donatorID) + donatorName + donator.getDonatorColorSecondary(donatorID) + ", Congratulations!");
					target.playSound(target.getLocation(), SoundHandler.LEVEL_UP, 1.0F, 1.0F);
				} else
				{
					main.scheduledDonator.put(uuid, donatorID);
					sender.sendMessage(ColorOptions.message + "Scheduled donator set to " + donatorName + " for player " + targetUsername);
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "Error: Player with uuid " + uuid + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
		Users.updateScoreBoard(null);
		user.destroy();
	}
	
    public double getDamageReduced(Entity entity)
    {
        org.bukkit.inventory.PlayerInventory inv = ((HumanEntity) entity).getInventory();
        ItemStack helmet = null;
        ItemStack boots = null;
        ItemStack chest = null;
        ItemStack pants = null;

        if(inv.getBoots() != null){boots = inv.getBoots();}
            if(inv.getBoots() == null){boots = new ItemStack(Material.LEATHER_BOOTS);}
        if (inv.getHelmet() != null){helmet = inv.getHelmet(); }
            if(inv.getHelmet() == null){helmet = new ItemStack(Material.LEATHER_HELMET);}
        if (inv.getChestplate() != null){chest = inv.getChestplate();}
            if(inv.getChestplate() == null){chest = new ItemStack(Material.LEATHER_CHESTPLATE);}
        if (inv.getLeggings() != null){pants = inv.getLeggings();}
            if(inv.getLeggings() == null){pants = new ItemStack(Material.LEATHER_LEGGINGS);}
        double red = 0.0;
        if (helmet.getType() == null || helmet.getType() == Material.AIR)red = red + 0.0;
        else if(helmet != null && helmet.getType() == Material.LEATHER_HELMET)red = red + 0.04;
        else if(helmet != null && helmet.getType() == Material.GOLD_HELMET)red = red + 0.08;
        else if(helmet != null && helmet.getType() == Material.CHAINMAIL_HELMET)red = red + 0.08;
        else if(helmet != null && helmet.getType() == Material.IRON_HELMET)red = red + 0.08;
        else if(helmet != null && helmet.getType() == Material.DIAMOND_HELMET)red = red + 0.12;
        //
        if (boots.getType() == null || boots.getType() == Material.AIR)red = red + 0;
        else if(boots != null && boots.getType() == Material.LEATHER_BOOTS)red = red + 0.04;
        else if(boots != null && boots.getType() == Material.GOLD_BOOTS)red = red + 0.04;
        else if(boots != null && boots.getType() == Material.CHAINMAIL_BOOTS)red = red + 0.04;
        else if(boots != null && boots.getType() == Material.IRON_BOOTS)red = red + 0.08;
        else if(boots != null && boots.getType() == Material.DIAMOND_BOOTS)red = red + 0.12;
        //
        if (pants.getType() == null || pants.getType() == Material.AIR)red = red + 0;
        else if(pants != null && pants.getType() == Material.LEATHER_LEGGINGS)red = red + 0.08;
        else if(pants != null && pants.getType() == Material.GOLD_LEGGINGS)red = red + 0.12;
        else if(pants != null && pants.getType() == Material.CHAINMAIL_LEGGINGS)red = red + 0.16;
        else if(pants != null && pants.getType() == Material.IRON_LEGGINGS)red = red + 0.20;
        else if(pants != null && pants.getType() == Material.DIAMOND_LEGGINGS)red = red + 0.24;
        //
        if (chest.getType() == null || chest.getType() == Material.AIR)red = red + 0;
        else if(chest != null && chest.getType() == Material.LEATHER_CHESTPLATE)red = red + 0.12;
        else if(chest != null && chest.getType() == Material.GOLD_CHESTPLATE)red = red + 0.20;
        else if(chest != null && chest.getType() == Material.CHAINMAIL_CHESTPLATE)red = red + 0.20;
        else if(chest != null && chest.getType() == Material.IRON_CHESTPLATE)red = red + 0.24;
        else if(chest != null && chest.getType() == Material.DIAMOND_CHESTPLATE)red = red + 0.32;
        return red;
    }
    
	public static void updateScoreBoard(List<User> Users)
	{
		Scoreboard board = new Scoreboard();
		board.setBoard(Users);
		
	}
	
	public boolean isUsedAddress(InetAddress address)
	{
		boolean used = false;
		
		List<Integer> list = getUserIDlistByAddress(address);
		if (!list.isEmpty())
		{
			if (list.size() < 1)
			{
				used = true;
			}
		}
		
		return used;
	}
	
	public List<Integer> getUserIDlistByAddress(InetAddress address)
	{
		List<Integer> userList = new ArrayList<Integer>();
		
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Player WHERE Address=?;");
			stmt.setString(1, address.toString().replaceAll("/", ""));
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				userList.add(results.getInt("ID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return userList;
	}
	
	public static void clearDailyUserStats()
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
	
	public static User findUser(UUID uuid)
	{
		User user = null;
		
		for (User u : Users2.users)
		{
			if (u.getUUID().equals(uuid))
			{
				user = u;
				break;
			}
		}
		
		return user;
	}
	
	public static User getUser(UUID uuid)
	{
		User us = null;
		
		for (User user : Users2.users)
		{
			if (user.getUUID().equals(uuid))
			{
				us = user;
				break;
			}
		}
		if (us == null)
		{
			if (CitizensAPI.getNPCRegistry().getByUniqueId(uuid) != null)
			{
				throw new UserIsNpcException();
			} else
			{
				try {
					throw new Exception("test");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return us;
	}
	
	public static ArrayList<Integer> getTempDonatorIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM DonatorTemp;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("UserID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static boolean existUser(String username)
	{
		boolean exist = false;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Player Where Username=?;");	
			stmt.setString(1, username.toLowerCase());
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
	
	public static void sendStaffMessage(String message)
	{
		for (Player op : Bukkit.getOnlinePlayers())
		{
			if (op.isOp() || op.hasPermission("k&k.staff") || op.hasPermission("k&k.owner"))
			{
				op.sendMessage(ColorOptions.message + "[" + ColorOptions.KAKColor + "StaffChat" + ColorOptions.message + "] " + message);
	    		op.playSound(op.getLocation(), SoundHandler.NOTE_PLING, 0.5F, 1.0F);
			}
		}
	}
	
	public static void newPlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
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
    			if (main.debug)
    			{
    				Bukkit.getConsoleSender().sendMessage("Second title fired!");
    			}
		    	offlineUser.sendTitle(player, ChatColor.BLUE + "Introduction", ColorOptions.message + "Please start the " + ColorOptions.messagesubjects + "Introduction Tutorial", 1, 4, 2);
    		}
    	}.runTaskLater(main, 5*20);
    	player.sendMessage(ColorOptions.messageachievement + "Welcome " + ColorOptions.messagesubjects + player.getName() + ColorOptions.messageachievement + ", nice of you to come by!");
		
    	Tutorial tutorial = new Tutorial();
    	tutorial.createTutorial(user, "intro");
    	
    	kit.starterKit(player);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
    	{
            public void run()
            {
    	    	Main.newPlayers.remove(uuid);
            }
        }, 3*20);
	}
	
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
    		}.runTaskLater(main, 5*20);
    	}
	}
	
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
    		}.runTaskLater(main, 6*20);
    	}
	}
	
	public static void CheckDuplicateAddress(User user)
	{
		offlineUser offlineUser = new offlineUser();
		Player player = user.getPlayer();
    	if (offlineUser.isUsedAddress(player.getAddress().getAddress()))
    	{
    		for (Player target : Bukkit.getOnlinePlayers())
    		{
    			if (target.hasPermission("k&k.staff") && !player.hasPermission("k&k.owner"))
    			{
    				List<String> alternativeNames = new ArrayList<String>();
    				for (Integer userID : offlineUser.getUserIDlistByAddress(player.getAddress().getAddress()))
    				{
    					String username = offlineUser.getUserName(Users.fetchUUIDbyID(userID));
    					if (!username.equalsIgnoreCase(player.getName()))
    					{
	    					alternativeNames.add(offlineUser.getUserName(Users.fetchUUIDbyID(userID)));
    					}
    				}
    				target.sendMessage(ColorOptions.message + "► Player " + ColorOptions.messagesubjects + player.getName() + ColorOptions.message + " joined using an IP address that has already been used!");
    				target.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Other users(" + alternativeNames.size() +"): " + alternativeNames);
    			}
    		}
    	}
	}
	
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
	
	public static User GetAvenger(User user)
	{
		User avenger = null;
		
		for (User u : Users2.users)
		{
			if (u.getAvengerTarget() == user)
			{
				avenger = u;
				break;
			}
		}
		
		return avenger;
	}
}
