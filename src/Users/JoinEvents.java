package Users;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import Broadcasts.BossBar;
import Donator.Donator;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Houses.House;
import Kits.Kit;
import Main.Main;
import Products.Product;
import SpawnPoints.SpawnPoint;
import Titles.Title;
import Towns.Town;
import Tutorial.Tutorial;

public class JoinEvents implements Listener
{
	BossBar bar = new BossBar();
	offlineUser offlineUser = new offlineUser();
	Donator donator = new Donator();
	House house = new House();
	Title title = new Title();
	Town town = new Town();
	SpawnPoint spawnpoint = new SpawnPoint();
	Kit kit = new Kit();
	private Main main;
	
	public JoinEvents(Main main)
	{
		this.main = main;
	}
	static ArrayList<UUID> newPlayers = new ArrayList<UUID>();
	
	@EventHandler
	public void joinMessage(PlayerJoinEvent e)
	{
		/* Prerequisites */
		main.DBreconnect();
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		
		
	    SimpleDateFormat time = new SimpleDateFormat("dd-MM HH:mm:ss");
		//Send the player a join message containing some statistics about himself
	    if (main.existUser(uuid) && !newPlayers.contains(uuid))
	    {
	    	User user = new User(uuid);
	    	user.join(player);
	    	
	    	user.setJoinMessage(e);
	    	user.setSkillHealth();
	    	user.setSkillSpeed();
			
	    	
	    	if (main.scheduledDonator.containsKey(uuid))
	    	{
	    		new BukkitRunnable()
	    		{
	    			public void run()
	    			{
	    				user.setDonatorRank(main.scheduledDonator.get(uuid));
	    			}
	    		}.runTaskLater(main, 5*20);
	    	}
	    	if (main.scheduledgive.containsKey(uuid))
	    	{
	    		new BukkitRunnable()
	    		{
	    			public void run()
	    			{
	    	    		Product product = new Product();
	    	    		for (Integer productID : main.scheduledgive.get(uuid).keySet())
	    	    		{
	    	    			product.giveProduct(Bukkit.getConsoleSender(), user, productID, main.scheduledgive.get(uuid).get(productID));
	    	    		}
	    			}
	    		}.runTaskLater(main, 6*20);
	    	}
	    	if (this.offlineUser.isUsedAddress(player.getAddress().getAddress()))
	    	{
	    		for (Player target : Bukkit.getOnlinePlayers())
	    		{
	    			if (target.hasPermission("k&k.staff") && !player.hasPermission("k&k.owner"))
	    			{
	    				List<String> alternativeNames = new ArrayList<String>();
	    				for (Integer userID : this.offlineUser.getUserIDlistByAddress(player.getAddress().getAddress()))
	    				{
	    					String username = this.offlineUser.getUserName(Users.fetchUUIDbyID(userID));
	    					if (!username.equalsIgnoreCase(player.getName()))
	    					{
		    					alternativeNames.add(this.offlineUser.getUserName(Users.fetchUUIDbyID(userID)));
	    					}
	    				}
	    				target.sendMessage(ColorOptions.message + "► Player " + ColorOptions.messagesubjects + player.getName() + ColorOptions.message + " joined using an IP address that has already been used!");
	    				target.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Other users(" + alternativeNames.size() +"): " + alternativeNames);
	    			}
	    		}
	    	}
			player.sendMessage(ColorOptions.statsbrackets);
			player.sendMessage(ChatColor.YELLOW + 	 "Welcome " + ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " to " + ChatColor.BLUE +  "Knights and Kings");
			player.sendMessage(ChatColor.YELLOW +    "The current time is: " + ChatColor.AQUA + main.getTime());
			player.sendMessage(ChatColor.YELLOW + 	 "Coins: " + ChatColor.BLUE + ColorOptions.formatCurrency(user.getCoins()) + ChatColor.DARK_PURPLE + " Gems: " + ChatColor.BLUE +  ColorOptions.formatCurrency(user.getGems()));
			player.sendMessage(ChatColor.YELLOW +    "Title: " + ChatColor.BLUE +  user.getTitleName());
			player.sendMessage(ChatColor.YELLOW +    "Salary: " + ChatColor.BLUE + ColorOptions.formatCurrency(user.getSalary()));
			player.sendMessage(ChatColor.YELLOW + 	 "Income: " + ChatColor.BLUE + ColorOptions.formatCurrency(user.getIncome()));
			player.sendMessage(ChatColor.GREEN + 	 "Don't forget to vote for useful rewards!");
			player.sendMessage(ColorOptions.statsbrackets);
	    }
	    Users.updateScoreBoard(null);
	}
	
	@EventHandler
	public void firstJoin(PlayerLoginEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		main.joinLong.put(uuid, System.currentTimeMillis());
		if (main.existUser(uuid) == false)
		{
			boolean allowed = false;
			if (Bukkit.hasWhitelist())
			{
				for (OfflinePlayer listed : Bukkit.getWhitelistedPlayers())
				{
					UUID lu = listed.getUniqueId();
					if (uuid.equals(lu))
					{
						allowed = true;
						break;
					}
				}
			} else
			{
				allowed = true;
			}
			
			if (allowed)
			{
				newPlayers.add(uuid);
				main.CreateUser(player.getName(), uuid, "Male", e.getAddress());
		    	Bukkit.broadcastMessage(ColorOptions.messageachievement + "" + ChatColor.BOLD + "► New player " + ColorOptions.messagesubjects + player.getName() + ColorOptions.messageachievement + " entered the Kingdoms! Welcome!");
		    	for (Player players : Bukkit.getOnlinePlayers())
		    	{
		    		players.playSound(players.getLocation(), SoundHandler.NOTE_PLING, 0.5F, 1.0F);
		    	}
			} else
			{
				Bukkit.broadcastMessage(ColorOptions.message + "► Player " + player.getName() + " tried to join for the first time, but is not whitelisted");
			}
			
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
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
		Long current = System.currentTimeMillis();
		Long playTimeOverall = user.getPlayTime(true);
		Long playTimeToday = user.getPlayTime(false);
		if (main.joinLong.containsKey(uuid))
		{
			Long join = main.joinLong.get(uuid);
			user.setPlayTime((playTimeOverall + (current-join)), true);
			user.setPlayTime((playTimeToday + (current-join)), false);
		}
		user.quit();
	}
	
	@EventHandler
	public void locationJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();

		if (!newPlayers.contains(uuid))
		{
			new BukkitRunnable()
			{
				public void run()
				{
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
					
					if (!player.hasPermission("k&k.join.nolocation") && !main.ownermodus.containsKey(uuid) && Users.existUser(player.getName()))
					{
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
							if (player.hasPermission("k&k.spawnpoint") || main.ownermodus.containsKey(uuid) || player.isOp())
							{
								player.sendMessage(ColorOptions.falsecommand + "No spawn-location has been set! This might cause glitches or errors");
							}
						}
					} else
					{
						if (main.ownermodus.containsKey(uuid))
						{
							boolean nolocation = false;
							
							for (Integer houseID : house.getHouseIDList(null))
							{
								if (house.getHouseSpawnPoint(houseID) == null)
								{
									nolocation = true;
									break;
								}
							}
							
							if (nolocation == true)
							{
								player.sendMessage(ColorOptions.statsbrackets);
								player.sendMessage(ColorOptions.statsformat + "These houses have no locations set:");
								for (Integer houseID : house.getHouseIDList(null))
								{
									if (house.getHouseSpawnPoint(houseID) == null)
									{
										player.sendMessage(ColorOptions.statsresults + "- " + houseID);
									}
								}
								player.sendMessage(ColorOptions.statsbrackets);
								player.sendMessage(ColorOptions.falsecommand + "" + ChatColor.BOLD + "Use /house spawnpoint set when standing in the house");
								player.sendMessage(ColorOptions.falsecommand + "" + ChatColor.BOLD + "This will grant the owner of the house a personal spawnpoint into this house");
							}
						}
					}
				}
			}.runTaskLaterAsynchronously(main, 10);
		} else
		{
	    	newPlayer(player);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (newPlayers.contains(uuid))
		{
			if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ())
			{
		    	newPlayer(player);
			}
		}
	}
	
	public void newPlayer(Player player)
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
    	    	newPlayers.remove(uuid);
            }
        }, 3*20);
	}
}
