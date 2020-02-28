package Treasure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Products.Product;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class Treasure 
{
	WorldGuard worldguard = new WorldGuard();
	SpawnPoint spawnpoint = new SpawnPoint();
	Product product = new Product();
	protected Main main = Main.getPlugin(Main.class);
	private Treasure Treasure = null;
	private Integer ID = null;
	private Integer townID = null;
	private Integer spawnpointID = null;
	private Location location = null;
	private List<Integer> discovered = new ArrayList<Integer>();
	private Integer Grade = null;
	private BukkitTask particleTask = null;
	private BukkitTask soundTask = null;
	private int radius = 1;
	private int amount = 8;
	private int blockid = 41;
	private int speed = 0;
	private int entityradius = 15;
	PacketPlayOutWorldParticles packet = null;
	
	public Treasure(int ID, int Grade, int spawnpointID, int townID, boolean newTreasure)
	{	
		this.Treasure = this;
		this.ID = ID;
		this.Grade = Grade;
		this.spawnpointID = spawnpointID;
		this.townID = townID;
		this.location = this.spawnpoint.getSpawnPointLocation(spawnpointID);
		this.packet = new PacketPlayOutWorldParticles(
					EnumParticle.BLOCK_CRACK,
					true,
					(float) location.getX(),
					(float) location.getY(),
					(float) location.getZ(),
					radius,
					radius,
					radius,
					speed,
					amount,
					blockid);
		
		this.discovered = this.FetchDiscoveredList();
	}
	
	public int GetID()
	{
		return this.ID;
	}
	
	public int GetTownID()
	{
		return this.townID;
	}
	
	public int GetSpawnpointID()
	{
		return this.spawnpointID;
	}
	
	public Location GetLocation()
	{
		return this.location;
	}
	
	public List<Integer> GetDiscoveredList()
	{
		return this.discovered;
	}
	
	public int GetGrade()
	{
		return this.Grade;
	}
	
	public BukkitTask GetParticleTask()
	{
		return this.particleTask;
	}
	
	public BukkitTask GetSoundTask()
	{
		return this.soundTask;
	}
	
	public int GetRadius()
	{
		return this.radius;
	}
	
	public int GetAmount()
	{
		return this.amount;
	}
	
	public int GetBlockID()
	{
		return this.blockid;
	}
	
	public int GetEntityRadius()
	{
		return this.entityradius;
	}
	
	public PacketPlayOutWorldParticles GetParticlePacket()
	{
		return this.packet;
	}
	
	public List<Integer> FetchDiscoveredList()
	{
		List<Integer> List = new ArrayList<Integer>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM TreasureDiscovered WHERE TreasureID=?;");
			stmt.setInt(1, this.ID);
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				List.add(results.getInt("UserID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return List;
	}
	
	public void SaveDiscoveredList()
	{
		new BukkitRunnable()
		{
			
			public void run()
			{
				try 
				{
					PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM TreasureDiscovered WHERE TreasureID = ?;");
					stmt.setInt(1, ID);
					
					stmt.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				main.logMessage("deleted treasure discovered for id " + ID);
				for (Integer userID : discovered)
				{
					try 
					{
						PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO TreasureDiscovered(UserID, TreasureID) VALUES(?, ?);");
						stmt.setInt(1, userID);
						stmt.setInt(2, ID);
						
						stmt.executeUpdate();
					} catch (SQLException e) 
					{
						e.printStackTrace();
					}
				}
			}
			
		}.runTaskAsynchronously(main);
	}
	
	public void AddDiscoveredList(int UserID) 
	{
		if (!this.discovered.contains(UserID))
		{
			this.discovered.add(UserID);
		}
	}
	
	public void RemoveDiscoveredList(int UserID)
	{
		if (this.discovered.contains(UserID))
		{
			this.discovered.remove(UserID);
		}
	}
	
	public boolean HasDiscovered(int UserID)
	{
		boolean Discovered = false;
		
		if (this.discovered.contains(UserID))
		{
			Discovered = true;
		}
		
		return Discovered;
	}
	
	public List<Player> getNearbyPlayers()
	{
		List<Player> list = new ArrayList<Player>();
		
    	for (Entity entity : location.getWorld().getNearbyEntities(location, entityradius, entityradius, entityradius))
    	{
    		if (entity instanceof Player)
    		{
    			UUID uuid = entity.getUniqueId();
    			User userEntity = null;
    			
    			try
    			{
    				userEntity = Users.getUser(uuid);
    			} catch (UserNotFoundException ex)
    			{
    				ErrorHandlers.userNotFoundAction(null, ((Player) entity), true);
    			} catch (Exception ex)
    			{
    				ex.printStackTrace();
    				ErrorHandlers.userNotFoundAction(null, ((Player) entity), true);
    			}
    			if (!HasDiscovered(userEntity.getID()))
    			{
                	list.add((Player) entity);
    			} else if (list.contains(((Player) entity)))
				{
    				list.remove((Player) entity);
				}
    		}
    	}
		
		return list;
	}
	
	public void openTreasure(User user)
	{
		Player player = user.getPlayer();
		Integer rewards = Treasures.getRewardAmount(this);
		
		Inventory menu = Bukkit.createInventory(null, 3*9, Treasures.getTreasureName(this));
		List<ItemStack> list = new ArrayList<ItemStack>(Arrays.asList(
				new ItemStack(Material.GOLD_NUGGET, main.getRandom(16, 48)),
				new ItemStack(Material.GOLD_INGOT, main.getRandom(8, 32)),
				new ItemStack(Material.GOLD_BLOCK, main.getRandom(8, 28)),
				new ItemStack(Material.DIAMOND, main.getRandom(8, 18)),
				new ItemStack(Material.DIAMOND_BLOCK, main.getRandom(8, 12))
				));
		
		for (int i = 0; i < rewards; i++)
		{
			Integer slot = main.getRandom(0, menu.getSize()-1);
			if (menu.getItem(slot) == null || menu.getItem(slot).getType() == Material.AIR)
			{
				if (main.getRandom(0, 100) <= 60)
				{
					menu.setItem(slot, product.getRandomProduct(null, null));
				} else
				{
					menu.setItem(slot, list.get(main.getRandom(0, list.size()-1)));
				}
			}
		}
		
		player.playSound(location, SoundHandler.CHEST_OPEN, 0.5F, 1.0F);
		player.openInventory(menu);
		AddDiscoveredList(user.getID());
	}
	
	public void PlayParticles(List<Player> players)
	{
		new BukkitRunnable()
		{
			public void run()
			{
		    	for (Player target : players)
		    	{
		        	((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
		    	}
			}
			
		}.runTaskAsynchronously(main);
	}
	
	public void StopParticles()
	{
		if (this.particleTask != null)
		{
			particleTask.cancel();
		}
	}
	
	public void PlaySound(List<Player> players)
	{
		new BukkitRunnable()
		{
			public void run()
			{
		    	for (Player target : players)
		    	{
		        	target.playSound(location, SoundHandler.LEVEL_UP, 0.1F, (float) Math.random());
		    	}
			}
			
		}.runTaskAsynchronously(main);
	}
	
	public void StopSound()
	{
		if (this.soundTask != null)
		{
			this.soundTask.cancel();
		}
	}
	
	public void Destroy()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				StopParticles();
				StopSound();
				//SaveDiscoveredList();
				Treasures.treasures.remove(Treasure);
			}
			
		}.runTaskAsynchronously(main);
	}
	
	
}
