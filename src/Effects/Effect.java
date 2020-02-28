package Effects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Donator.Donator;
import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Products.EnchantbookClick;
import Products.Product;
import Users.User;
import Users.Users;

public class Effect 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	public List<Integer> getIDList()
	{
		List<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Effects GROUP BY Benefical;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List<Integer> getSpecificIDList(boolean Beneficial)
	{
		List<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = null;
			if (Beneficial == true)
			{
				stmt = main.getConnection().prepareStatement("Select * From Effects WHERE Beneficial=True;");
			} else
			{
				stmt = main.getConnection().prepareStatement("Select * From Effects WHERE Beneficial=False;");
			}
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public ResultSet getName(Integer effectID)
	{
		ResultSet set = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Effects Where ID=?;");
			stmt.setInt(1, effectID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				set = results;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return set;
	}
	
	public Integer getID(String effectName)
	{
		Integer ID = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Effects Where Name=?;");
			stmt.setString(1, effectName);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				ID = results.getInt("ID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ID;
	}
	
	//Paramater duration is the duration of the effect in seconds
	//Parameter delay is the delay between each run of the effect
	//Parameter value is the amount of something the effect does	
	public void addMTB(UUID uuid, Integer effectID, Integer duration, Integer delay, Integer percentage)
	{
		Player player = Bukkit.getPlayer(uuid);
		User user = Users.getUser(uuid);
		try
		{
			Integer coins = user.getCoins();
			BukkitTask task = new BukkitRunnable()
			{
				public void run()
				{
					if (effectID == 1)
					{
						List<Entity> nearby = player.getNearbyEntities(10, 10, 10);
						for (Entity entity : nearby)
						{
							if (entity instanceof Player)
							{
								Player target = (Player) entity;
								if (Users.existUser(target.getName()))
								{
									Integer coinAmount = main.getRandom(1, (coins/100)*percentage);
									user.sendCoins(target.getUniqueId(), coinAmount);
									player.sendMessage(ColorOptions.error + "You were in a generous mood and gave " + target.getName() + " " + coinAmount + " coins!");
									target.sendMessage(ColorOptions.messageachievement + player.getName() + " was in a generous mood and gave you " + coinAmount + ColorOptions.coinStats + " coins!");
									break;
								}
							}
						}
					}
				}
			}.runTaskTimer(main, 0, delay*20);
			
			new BukkitRunnable()
			{
				public void run()
				{
					task.cancel();	
				}
			}.runTaskLater(main, duration*20);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addDJ(UUID uuid, Integer effectID, Integer duration, Integer delay, Integer goldAmountMin, Integer goldAmountMax, Integer gemAmountMax)
	{
		Product product = new Product();
		Player player = Bukkit.getPlayer(uuid);
		User user = Users.getUser(uuid);
		try
		{
			Integer coins = user.getCoins();
			BukkitTask task = new BukkitRunnable()
			{
				public void run()
				{
					if (effectID == 2)
					{
						Integer goldAmount = main.getRandom(goldAmountMin, goldAmountMax);
						Integer diamondAmount = main.getRandom(0, gemAmountMax);
						
						Integer coinAmount = (coins/100)*2;
						Integer gemAmount = (user.getGems()/100)*1;
						
						Integer coin = coinAmount*goldAmount;
						Integer gem = diamondAmount*gemAmount;
						user.removeCoins(coin);
						user.removeGems(gem);
						
						Location loc = player.getLocation();
						for (int i = 0; i < goldAmount; i++)
						{
							loc.getWorld().dropItemNaturally(new Location(loc.getWorld(), loc.getX()+main.getRandom(-3, 3), loc.getY()+main.getRandom(0, 3), loc.getZ()+main.getRandom(-3, 3)), product.createAmountItem(Material.GOLD_NUGGET,  goldAmount, "", ChatColor.BLACK + "Coins: " + coinAmount));
						}
						for (int i = 0; i < gemAmount; i++)
						{
							loc.getWorld().dropItemNaturally(new Location(loc.getWorld(), loc.getX()+main.getRandom(-3, 3), loc.getY()+main.getRandom(0, 3), loc.getZ()+main.getRandom(-3, 3)), product.createAmountItem(Material.DIAMOND, gemAmount, "", ChatColor.BLACK + "Gems: " + gemAmount));
						}
						player.playSound(loc, SoundHandler.CHEST_CLOSE, 0.5F, 1.0F);

					}
				}
			}.runTaskTimer(main, 0, delay*20);
			
			new BukkitRunnable()
			{
				public void run()
				{
					task.cancel();	
				}
			}.runTaskLater(main, duration*20);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addPG(UUID uuid, Integer effectID, Integer duration, Integer delay, Integer dropChance)
	{
		Player player = Bukkit.getPlayer(uuid);
		BukkitTask task = new BukkitRunnable()
		{
			public void run()
			{
				if (effectID == 3)
				{
					List<ItemStack> items = new ArrayList<ItemStack>();
					for (ItemStack item : player.getInventory().getContents())
					{
						if (item != null && item.getType() != Material.AIR)
						{
							items.add(item);
						}
					}
					Collections.shuffle(items);
					Location loc = player.getLocation();
					for (ItemStack i : items)
					{
						if (main.getRandom(0, 100) <= dropChance)
						{
							loc.getWorld().dropItemNaturally(new Location(loc.getWorld(), loc.getX()+main.getRandom(-3, 3), loc.getY()+main.getRandom(0, 3), loc.getZ()+main.getRandom(-3, 3)), i);
							EnchantbookClick click = new EnchantbookClick(main);
							click.removeItemfromInventory(i, player);
							player.playSound(loc, SoundHandler.CHEST_CLOSE, 0.5F, 1.0F);
							break;
						}
					}
				}
			}
		}.runTaskTimer(main, 0, delay*20);
		
		new BukkitRunnable()
		{
			public void run()
			{
				task.cancel();	
			}
		}.runTaskLater(main, duration*20);
	}
	
	public void addCB(UUID uuid, Integer effectID, Integer duration, Integer delay, Integer coinAmountMin, Integer coinAmountMax)
	{
		Player player = Bukkit.getPlayer(uuid);
		User user = Users.getUser(uuid);
		try
		{
			BukkitTask task = new BukkitRunnable()
			{
				public void run()
				{
					if (effectID == 4)
					{
						Integer coin = main.getRandom(coinAmountMin, coinAmountMax);
						user.addCoins(coin);
						player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "Coin Booster" + ChatColor.GRAY + "]" + ColorOptions.messageachievement + "You received " + coin + " coins!");
						player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 0.5F, 1.0F);
					}
				}
			}.runTaskTimer(main, 0, delay*20);
			
			new BukkitRunnable()
			{
				public void run()
				{
					task.cancel();	
				}
			}.runTaskLater(main, duration*20);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addGB(UUID uuid, Integer effectID, Integer duration, Integer delay, Integer gemAmountMin, Integer gemAmountMax)
	{
		Player player = Bukkit.getPlayer(uuid);
		User user = Users.getUser(uuid);
		try
		{
			BukkitTask task = new BukkitRunnable()
			{
				public void run()
				{
					if (effectID == 6)
					{
						Integer gem = main.getRandom(gemAmountMin, gemAmountMax);
						user.addGems(gem);
						player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Gem Booster" + ChatColor.GRAY + "]" + ColorOptions.messageachievement + "You received " + gem + " gems!");
						player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 0.5F, 1.0F);
					}
				}
			}.runTaskTimer(main, 0, delay*20);
			
			new BukkitRunnable()
			{
				public void run()
				{
					task.cancel();	
				}
			}.runTaskLater(main, duration*20);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addHB(UUID uuid, Integer effectID, Integer duration, Integer delay, Double healthScale)
	{
		Player player = Bukkit.getPlayer(uuid);
		BukkitTask task = new BukkitRunnable()
		{
			public void run()
			{
				if (effectID == 5)
				{
					player.setHealthScale(player.getHealthScale()+ healthScale);
				}
			}
		}.runTaskTimer(main, 0, delay*20);
		
		new BukkitRunnable()
		{
			public void run()
			{
				task.cancel();	
			}
		}.runTaskLater(main, duration*20);
	}
	
	//RankBoost duration is in seconds
	public void addRB(UUID uuid, Integer effectID, Integer duration, Integer rankID)
	{
		Donator donator = new Donator();
		Player player = Bukkit.getPlayer(uuid);	
		User user = Users.getUser(uuid);
		try
		{
			user.setTempDonator(rankID, main.calculateTimeValue(duration, "seconds"));
			player.sendMessage(ColorOptions.messageachievement + "You got a rank-booster and you have been knighted to " + donator.getDonatorName(rankID));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
