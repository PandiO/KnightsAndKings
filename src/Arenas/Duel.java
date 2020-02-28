package Arenas;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Scoreboards.ActionBar;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;

public class Duel implements Listener
{
	Arena arena = new Arena();
	SpawnPoint spawnpoint = new SpawnPoint();
	WorldGuard worldguard = new WorldGuard();
	Main main = Main.getPlugin(Main.class);
	
	public Integer gapTime = 10;
	public Integer winTime = 5;
	public Integer countDown = 5;
	public Integer prepTime = 30;
	public Integer duelTime = 300;
	public boolean Queue = false;
	public boolean countDownMove = false;
	public Long duelExpire = null;
	public Long prepareExpire = null;
	public User user1;
	public User user2;
	public Integer arenaID;
	public String duelType;
	private Location arenaExit;
	private Location arenaCatacombs;
	public Location arenaPlayer1;
	public Location arenaPlayer2;
	private Location spawn;
	public List<ItemStack> itemPrize;
	public Integer coinPrize;
	
	public Duel(Player player1, Player player2, Integer arenaID, String duelType, List<ItemStack> itemPrize, Integer coinPrize)
	{
		Integer arenaExit = spawnpoint.getSpawnPointID("Arena_" + arenaID + "_Exit");
		Integer arenaCatacombs = spawnpoint.getSpawnPointID("Arena_" + arenaID + "_Catacombs");
		Integer arenaPlayer1 = spawnpoint.getSpawnPointID("Arena_" + arenaID + "_Player1");
		Integer arenaPlayer2 = spawnpoint.getSpawnPointID("Arena_" + arenaID + "_Player2");
		Integer spawnID = spawnpoint.getSpawnPointID("spawn");
		
		this.itemPrize = itemPrize;
		this.coinPrize = coinPrize;
		this.user1 = Users.getUser(player1.getUniqueId());
		this.user2 = Users.getUser(player2.getUniqueId());
		
		if (!duelType.equalsIgnoreCase("normal"))
		{
			removeDuel(ColorOptions.error + "Cancelled duel because no duel-type could be found named '" + duelType + "'!", true);
			return;
		}
		if (!arena.getArenaIDList(null).contains(arenaID))
		{
			removeDuel(ColorOptions.error + "Cancelled duel because no arena could be found!", true);
			return;
		}
		if (arenaExit == null)
		{
			removeDuel(ColorOptions.error + "Cancelled duel because this arena has no Exit!", true);
			return;
		}
		if (arenaPlayer1 == null || arenaPlayer2 == null)
		{
			removeDuel(ColorOptions.error + "Cancelled duel because this arena cannot spawn you!", true);
			return;
		}
		this.arenaExit = spawnpoint.getSpawnPointLocation(arenaExit);
		this.arenaCatacombs = spawnpoint.getSpawnPointLocation(arenaCatacombs);
		this.arenaPlayer1 = spawnpoint.getSpawnPointLocation(arenaPlayer1);
		this.arenaPlayer2 = spawnpoint.getSpawnPointLocation(arenaPlayer2);
		this.spawn = spawnpoint.getSpawnPointLocation(spawnID);
		this.arenaID = arenaID;
		this.duelType = duelType;
		main.duelList.add(this);
	}

	//Must happen when a fight ends or when a duel gets created
	public void tryStart()
	{
		boolean preperation = true;
		if (this.arenaCatacombs == null)
		{
			preperation = false;
		}
		if (main.duelList.indexOf(this) == 0)
		{
			if (this.Queue)
			{
				startCountDown();
			} else
			if (preperation)
			{
				startPreperation(this.prepTime);
			} else
			{
				startCountDown();
			}
		} else if (main.duelList.indexOf(this) == 1)
		{
			Long current = System.currentTimeMillis();
			Duel firstDuel = main.duelList.get(0);
			//Get how long the current duel will last and add 5 seconds of space to it
			Integer duelDuration = Integer.valueOf((int) ((firstDuel.duelExpire-current)/1000));
			if (preperation)
			{
				startPreperation(duelDuration+this.gapTime);
			}
		} else
		{
			for (User user : getPlayers())
			{
				sendMessage(user.getUUID(), ColorOptions.messageformat + "Your duel is number " + main.duelList.indexOf(this) + " in line, please wait for the other duels to finish");
				sendMessage(user.getUUID(), ColorOptions.message + "The current duel is between " + ColorOptions.messagesubjects + main.duelList.get(0).user1.getUsername() + ColorOptions.message + " and " + ColorOptions.messagesubjects + main.duelList.get(0).user2.getUsername());
			}			
		}
	}
	
	public void startPreperation(Integer prepareTime)
	{
		//Will prevent double preperation
		this.Queue = true;
		Bukkit.getConsoleSender().sendMessage("Queue turned on!");
		
		Long current = System.currentTimeMillis();
		Integer hour = main.getHourtime(prepareTime);
		Integer minute = main.getRestMinutetime(prepareTime);
		Integer seconds = main.getRestSecondtime(prepareTime);
		String prepareString = ColorOptions.messageformat + "Duel starts in ";
		if (hour >= 1)
		{
			prepareString = prepareString + ColorOptions.messagesubjects + hour + " hour, " + minute + " minutes and " + seconds + " seconds!";
		} else if (minute >= 1)
		{
			prepareString = prepareString + ColorOptions.messagesubjects + minute + " minutes and " + seconds + " seconds!";
		} else
		{
			prepareString = prepareString + ColorOptions.messagesubjects + seconds + " seconds!";
		}
		if (Bukkit.getPlayer(this.user1.getUUID()) == null)
		{
			removeDuel(ColorOptions.error + "Duel cancelled, player " + this.user1.getUsername() + " left!", true);
			return;
		} else
		{
			sendMessage(this.user1.getUUID(), prepareString);
		}
		if (Bukkit.getPlayer(this.user2.getUUID()) == null)
		{
			removeDuel(ColorOptions.error + "Duel cancelled, player " + this.user2.getUsername() + " left!", true);
			return;
		} else
		{
			sendMessage(this.user2.getUUID(), prepareString);
		}
		this.user1.getPlayer().teleport(arenaCatacombs);
		this.user2.getPlayer().teleport(arenaCatacombs);
		this.prepareExpire = current + (1000*prepareTime);
		Bukkit.getConsoleSender().sendMessage("Preperation expiration set!");
		Bukkit.getConsoleSender().sendMessage("DuelList: " + main.duelList);
	}
	
	public void startCountDown()
	{
		this.prepareExpire = null;
		this.countDownMove = true;
		ProtectedRegion arena = worldguard.getRegionManager(Bukkit.getWorld("world")).getRegion("arena_" + this.arenaID);
		arena.getMembers().addPlayer(this.user1.getUUID());
		arena.getMembers().addPlayer(this.user2.getUUID());
		ActionBar countdownBar = new ActionBar(ColorOptions.error + "Duel will start in " + ChatColor.BOLD + this.countDown + ChatColor.RESET + ColorOptions.error + " seconds!");
		for (User user : getPlayers())
		{
			countdownBar.sendToPlayer(user.getPlayer());
//			sendMessage(uuid, ColorOptions.error + "Duel will start in " + ChatColor.BOLD + this.countDown + ChatColor.RESET + ColorOptions.error + " seconds!");
		}
		if (Bukkit.getPlayer(this.user1.getUUID()) != null)
		{
			teleport(this.user1.getUUID(), arenaPlayer1);
		} else
		{
			removeDuel(ColorOptions.error + "Duel cancelled, player " + this.user1.getUsername() + " left!", true);
			return;
		}
		if (Bukkit.getPlayer(this.user2.getUUID()) != null)
		{
			teleport(this.user2.getUUID(), arenaPlayer2);
		} else
		{
			removeDuel(ColorOptions.error + "Duel cancelled, player " + this.user2.getUsername() + " left!", true);
			return;
		}
		new BukkitRunnable()
    	{
    		public void run()
    		{
				if (countDown <= 0)
				{
					startDuel();
					this.cancel();
				} else if (countDown == 5)
				{
					countDown--;
				} else
				{
					for (User user : getPlayers())
					{
						ActionBar countdown = new ActionBar(ColorOptions.error + "Duel will start in " + ChatColor.BOLD + countDown + ChatColor.RESET + ColorOptions.error + " seconds!");
						Player pl = user.getPlayer();
						pl.playSound(user.getPlayer().getLocation(), SoundHandler.CLICK, 1.0F, 1.0F);
						countdown.sendToPlayer(pl);
//						sendMessage(uuid, ColorOptions.error + "Duel will start in " + ChatColor.BOLD + countDown + ChatColor.RESET + ColorOptions.error + " seconds!");
					}
					countDown--;
				}
    		}
    	}.runTaskTimer(main, 0, 1*20);
	}
	
	public void startDuel()
	{
		this.countDownMove = false;
		Long current = System.currentTimeMillis();
		this.duelExpire = (current+(this.duelTime*1000));
		for (User user : getPlayers())
		{
			ActionBar fight = new ActionBar(ColorOptions.error + "" + ChatColor.BOLD + ">>>>>FIGHT!<<<<<");
			Player pl = user.getPlayer();
			pl.playSound(user.getPlayer().getLocation(), SoundHandler.WITHER_SPAWN, 1.0F, 0.5F);
			fight.sendToPlayer(pl);
//			sendMessage(uuid, ColorOptions.error + "" + ChatColor.BOLD + ">>>>>FIGHT!<<<<<");
		}
	}
	
	public void endDuel(boolean kill, User winner, User loser, Integer winTime)
	{
		if (kill)
		{
			String losername;
			String winnername;
			if (winner == this.user1)
			{
				losername = this.user2.getUsername();
				winnername = this.user1.getUsername();
			} else
			{
				losername = this.user1.getUsername();
				winnername = this.user2.getUsername();
			}
			if (Bukkit.getPlayer(winner.getUUID()) != null)
			{
				Player player = winner.getPlayer();
				sendMessage(loser.getUUID(), ColorOptions.messageachievement + "Player " + ColorOptions.messagesubjects + winner.getUsername() + ColorOptions.messageachievement + " won the duel!");
				if (this.arenaExit != null)
				{
					teleport(loser.getUUID(), this.arenaExit);
				} else
				{
					teleport(loser.getUUID(), this.spawn);
				}
				new BukkitRunnable()
		    	{
		    		public void run()
		    		{
		    			if (Bukkit.getPlayer(winner.getUUID()) != null)
						{
							removeDuel("", false);
						}
		    		}
		    	}.runTaskLater(main, gapTime*20);
				new BukkitRunnable()
		    	{
		    		public void run()
		    		{
		    			if (Bukkit.getPlayer(winner.getUUID()) != null)
						{
							sendMessage(winner.getUUID(), ColorOptions.messageachievement + "Player " + ColorOptions.messagesubjects + winner.getUsername() + ColorOptions.messageachievement + " won the duel!");
							if (arenaExit == null)
							{
								teleport(winner.getUUID(), spawn);
							} else
							{
								teleport(winner.getUUID(), arenaExit);
							}
						}
		    		}
		    	}.runTaskLater(main, winTime*20);
				if (coinPrize != null && coinPrize > 0)
				{
					winner.addCoins(coinPrize);
					player.sendMessage(ColorOptions.messageachievement + "Congratulations! You won " + ColorOptions.messagesubjects + this.coinPrize + ColorOptions.messageachievement + " coins by winning the duel against " + ColorOptions.messagesubjects + losername);
				}
				if (this.itemPrize != null && !this.itemPrize.isEmpty())
				{
					player.sendMessage(ColorOptions.messageachievement + "Congratulations! You won the following items by winning the duel against " + ColorOptions.messagesubjects + losername + ":");
					for (ItemStack item : this.itemPrize)
					{
						Inventory inv = player.getInventory();
						player.sendMessage(item.getItemMeta().getDisplayName() + ColorOptions.messageachievement + " x " + item.getAmount());
						if (inv.firstEmpty() != -1)
						{
							inv.addItem(item);
						} else if (player.getEnderChest().firstEmpty() != -1)
						{
							player.getEnderChest().addItem(item);
							player.sendMessage(ColorOptions.error + "No space in your inventory, dropped item in your enderchest!");
						} else
						{
							player.sendMessage(ColorOptions.error + "No space in your inventory, dropped item on the ground!");
							player.getWorld().dropItemNaturally(player.getLocation(), item);
						}
					}
				}
			} else
			{
				removeDuel(ColorOptions.messageachievement + "Player " + ColorOptions.messagesubjects + winnername + ColorOptions.messageachievement + " won the duel!", true);
			}
		} else
		{
			removeDuel(ColorOptions.messageachievement + "The duel ended in a draw!", true);
		}
	}
	
	public void removeDuel(String message, boolean teleport)
	{
		if (arena.duelWaitList.containsKey(this))
		{
			arena.duelWaitList.remove(this);
		}
		main.duelList.remove(this);
		if (teleport)
		{
			for (User user : getPlayers())
			{
				Player pl = user.getPlayer();
				ActionBar bar = new ActionBar(message);
				bar.sendToPlayer(pl);
//				sendMessage(uuid, message);
				if (arenaExit == null)
				{
					teleport(user.getUUID(), this.spawn);
				} else
				{
					teleport(user.getUUID(), this.arenaExit);
				}
			}
		}
		ProtectedRegion arena = worldguard.getRegionManager(Bukkit.getWorld("world")).getRegion("arena_" + this.arenaID);
		arena.getMembers().clear();
		if (!main.duelList.isEmpty())
		{
			main.duelList.get(0).tryStart();
		}
	}
	
	public void teleport(UUID uuid, Location location)
	{
		if (Bukkit.getPlayer(uuid) != null)
		{
			Bukkit.getPlayer(uuid).teleport(location);
		}
	}
	
	public void sendMessage(UUID uuid, String message)
	{
		if (Bukkit.getPlayer(uuid) != null)
		{
			Player player = Bukkit.getPlayer(uuid);
			player.sendMessage(message);
		}
	}
	
	public List<User> getPlayers()
	{
		return Arrays.asList(user1, user2);
	}
}
