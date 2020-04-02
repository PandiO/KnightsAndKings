package Minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import DataManager.Worldguard;
import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Products.Product;
import Products.PropertyProduct;
import Properties.Property;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Towns.Town;
import Users.User;

public class Transport 
{
	SpawnPoint spawnpoint = new SpawnPoint();
	Town town = new Town();
	Street street = new Street();
	Property property = new Property();
	Product product = new Product();
	PropertyProduct proproduct = new PropertyProduct();
	Main main = Main.getPlugin(Main.class);
	
	User user;
	Player player;
	Long startLong;
	Integer maxTime = 300;
	Integer price = 0;
	Integer propertyID;
	HashMap<Integer, ItemStack> playerInv = new HashMap<Integer, ItemStack>();
	String defaultMSG = ColorOptions.message + ColorOptions.messageArrow + "Succesfully cancelled the transport mission!";
	Integer townID;
	String townName;
	Integer warehouseID = null;
	Integer streetID;
	String streetName;
	String warehouseName;
	Integer streetNumber;
	Integer fetchTownTries = 0;
	boolean fullInv = false;
	boolean stop = false;
	boolean started = false;
	
	public Transport(User user, Integer propertyID)
	{
		if (user.getPlayer().getGameMode() == GameMode.SURVIVAL)
		{
			this.startLong = System.currentTimeMillis();
			this.user = user;
			this.player = user.getPlayer();
			this.propertyID = propertyID;
			this.saveInventory();
			this.clearContents();
			this.getClosestTown();
			this.getWareHouse();
			if (this.warehouseID != null)
			{
				this.addList();
				this.tryStorageRetrieve();
				this.sendTarget();
				user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300*20, 1));
			} else
			{
				cancel(ColorOptions.error + "No warehouse could be found!");
			}
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Actual worth: " + price);
			}
		} else
		{
			player.sendMessage(ColorOptions.error + "You need to be in Survival mode to start this mission!");
		}
	}
	
	public void addList()
	{
		Main.transports.add(this);
		if (Main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Added transport. >>Transport.java");
		}
	}
	
	public void removeList()
	{
		Main.transports.remove(this);
	}
	
	public void saveInventory()
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Saving inventory!");
		}
		for (int i = 0; i < 36; i++)
		{
			if (i > 8)
			{
				this.playerInv.put(i, this.player.getInventory().getItem(i));
			}
		}
	}
	
	public void clearContents()
	{
		for (int i = 0; i < 36; i++)
		{
			if (i > 8)
			{
				this.player.getInventory().setItem(i, null);
				this.player.updateInventory();
			}
		}
	}
	
	public void returnInventory()
	{
		this.clearContents();
		for (Integer slot : this.getInventory().keySet())
		{
			this.player.getInventory().setItem(slot, this.playerInv.get(slot));
			this.player.updateInventory();
		}
	}
	
	public void failed(String msg, boolean died)
	{
		Bukkit.broadcastMessage(ColorOptions.error + ColorOptions.rawbrackets);
		Bukkit.broadcastMessage(msg);
		Bukkit.broadcastMessage(ColorOptions.error + ColorOptions.rawbrackets);
		
		if (!died)
		{
			cancel(msg);
		}
	}
	
	public void cancel(String msg)
	{
		this.removeList();
		this.returnInventory();
		if (msg != null)
		{	
			this.player.sendMessage(msg);
		} else
		{
			this.player.sendMessage(this.defaultMSG);
		}
	}
	
	public void tryStorageRetrieve()
	{
		if (!property.getIDList(null, null).contains(this.propertyID))
		{
			this.cancel(ColorOptions.error + ColorOptions.messageArrow + "No property found with the given ID!");
			return;
		}
		Chest fullChest = property.getFullChest(propertyID);
		if (fullChest != null)
		{
			Integer chestCount = 0;
			Inventory chestInv = fullChest.getInventory();
			for (int i = 0; i <36; i++)
			{
				if (chestCount < chestInv.getSize() && i > 8)
				{
					ItemStack item = chestInv.getItem(chestCount);
					if (item != null)
					{
						this.player.getInventory().setItem(i, item);
						this.player.updateInventory();
						chestInv.setItem(chestCount, null);
					}
					chestCount++;
				}
			}
			return;
		}
		
		List<Chest> chests = property.getChestFromRegion("property", propertyID);
		for (Chest chest : chests)
		{
			if (this.fullInv == false)
			{
				this.emptyChest(chest);
			}
		}
		
		calculateWorth();
	}
	
	public void calculateWorth()
	{
		Inventory inv = this.player.getInventory();
		for (int i = 0; i <36; i++)
		{
			if (i > 8)
			{
				ItemStack item = inv.getItem(i);
				if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName())
				{
					Integer productID = product.getProductIDbyDisplayName(item.getItemMeta().getDisplayName(), false);
					if (productID != null)
					{
						this.price = price + (product.getPriceMin(productID)*item.getAmount());
					}
				}
			}
		}
	}
	
	public void emptyChest(Chest chest)
	{
		Inventory chestInv = chest.getInventory();
		Integer chestCount = 0;
		for (int i = 0; i < 36; i++)
		{
			if (chestCount < chestInv.getSize())
			{
				ItemStack item = chestInv.getItem(chestCount);
				if (item != null && this.player.getInventory().getItem(i) == null && i > 8)
				{
					this.player.getInventory().setItem(i, item);
					this.player.updateInventory();
					chestInv.setItem(chestCount, null);
					chestCount++;
				} else if (item == null)
				{
					chestCount++;
				}
			}
		}
		
		if (this.player.getInventory().getItem(36) != null)
		{
			this.fullInv = true;
		}
	}
	
	public void getWareHouse()
	{
		if (!this.stop)
		{
			if (this.townID != null)
			{
				this.townName = town.getTownName(townID);
				List<Integer> properties = property.getIDListbyCategory("warehouse", this.townID);
				if (properties != null && !properties.isEmpty())
				{
					this.warehouseID = properties.get(main.getRandom(0, properties.size()-1));
					this.streetID = property.getStreetID(warehouseID);
					this.streetName = street.getStreetName(streetID);
					this.streetNumber = property.getStreetNumber(warehouseID);
					this.warehouseName = property.getPropertyName(warehouseID);
				} else
				{
					getClosestTown();
					new BukkitRunnable()
					{
						public void run()
						{
							if (fetchTownTries < 5)
							{
								getWareHouse();
								fetchTownTries++;
							} else
							{
								this.cancel();
							}
						}
					}.runTaskLater(main, 1*20);
//					if (this.fetchTownTries < 5)
//					{
//						getClosestTown();
//						getWareHouse();
//						this.fetchTownTries++;
//					} else
//					{
//						cancel(ColorOptions.error + "Couldn't find a town to transport storage to!");
//						return;
//					}
				}
			} else
			{
				cancel(ColorOptions.error + "Couldn't find a town to transport storage to!");
				return;
//				if (this.fetchTownTries < 5)
//				{
//					getClosestTown();
//					getWareHouse();
//					this.fetchTownTries++;
//				} else
//				{
//					cancel(ColorOptions.error + "Couldn't find a town to transport storage to!");
//					return;
//				}
			}
		}
	}
	
	public void getClosestTown()
	{
		Integer townID = null;
		
		Location loc = this.player.getLocation();
		if (this.townID == null)
		{
			Integer tryID = Worldguard.getStructureIDbyRegion("town", loc, Worldguard.getRegionManager(loc.getWorld()));
			
			HashMap<Integer, Double> spawnpointDistance = new HashMap<Integer, Double>();
			for (Integer id : town.getTownIDList())
			{
				String name = town.getTownName(id);
				Integer spawnpointID = spawnpoint.getSpawnPointID(name);
				if (spawnpointID != null)
				{
					Double distance = loc.distance(spawnpoint.getSpawnPointLocation(spawnpointID));
					spawnpointDistance.put(spawnpointID, distance);
				} else
				{
					continue;
				}
			}
			
			if (!spawnpointDistance.isEmpty())
			{
				Integer lowest = (Integer) spawnpointDistance.keySet().toArray()[0];
				for (Integer spawnpointID : spawnpointDistance.keySet())
				{
					if (spawnpointDistance.get(spawnpointID) < spawnpointDistance.get(lowest))
					{
						if (town.getTownID(spawnpoint.getSpawnPointName(spawnpointID)) != this.townID && town.getTownID(spawnpoint.getSpawnPointName(spawnpointID)) != this.townID)
						{
							if (tryID != null)
							{
								if (tryID != town.getTownID(spawnpoint.getSpawnPointName(spawnpointID)))
								{
									lowest = spawnpointID;
								} else
								{
									continue;
								}
							} else
							{
								lowest = spawnpointID;
							}
						} else
						{
							continue;
						}
					}
				}
				
				townID = town.getTownID(spawnpoint.getSpawnPointName(lowest));
			} else if (townID == null)
			{
				townID = town.getTownID("kardenna");
			}			
		} else
		{
			List<Integer> list = town.getTownIDList();
			if (list.size() > 2)
			{
				for (Integer id : list)
				{
					if (id != this.townID)
					{
						townID = id;
						break;
					}
				}
			} else
			{
				cancel(ColorOptions.error + "Couldn't find a town to transport to!");
				this.stop = true;
				return;
			}
		}
		
		this.townID = townID;
	}
	
	public void setStarted()
	{
		this.started = true;
	}
	
	public void setStop()
	{
		this.started = false;
	}
	
	public void sendTarget()
	{
		Player player = this.player;
		player.sendMessage(ColorOptions.statsbrackets);
		player.sendMessage(ColorOptions.statsformat + "Objective: Go to the target Warehouse and deliver the items");
		player.sendMessage(ColorOptions.stats + "Warehouse: " + this.warehouseName);
		player.sendMessage(ColorOptions.stats + "Town: " + this.townName);
		player.sendMessage(ColorOptions.stats + "Street: " + this.streetName);
		player.sendMessage(ColorOptions.stats + "Number: " + this.streetNumber);
		player.sendMessage(ColorOptions.error + "Other players have been notified about your asignment and get rewards for killing you!");
		player.sendMessage(ColorOptions.statsbrackets);
		
		for (Player target : Bukkit.getOnlinePlayers())
		{
			if (target != this.player)
			{
				target.sendMessage(ColorOptions.error + ColorOptions.rawbrackets);
				target.sendMessage(ColorOptions.error + ColorOptions.messageArrow + "Player " + this.player.getName() + " is transporting items!");
				target.sendMessage(ColorOptions.error + ColorOptions.messageArrow + "Kill him for a reward!");
				target.sendMessage(ColorOptions.error + ColorOptions.rawbrackets);
			}
		}
	}
	
	public void reachedTarget()
	{
		Integer comission = getComission();
		try
		{
			property.fillWarehouse(this.warehouseID, this.getTransport());
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		this.clearContents();
		player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 1.0F);
		player.sendMessage(ColorOptions.statsbrackets);
		player.sendMessage(ColorOptions.messageachievement + "Succesfully transported item to the warehouse");
		player.sendMessage(ColorOptions.stats + "Networth of sold item: " + this.price);
		player.sendMessage(ColorOptions.stats + "You received: " + comission);
		cancel(ColorOptions.statsbrackets);
		user.addCoins(comission);
	}
	
	public List<ItemStack> getTransport()
	{
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		
		for (int i = 0; i < 36; i++)
		{
			if (i > 8)
			{
				ItemStack item = this.player.getInventory().getItem(i);
				list.add(item);
			}
		}
		
		return list;
	}
	
	public Integer getComission()
	{
		return 	((Integer) this.price/5);
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public Long getStart()
	{
		return this.startLong;
	}
	
	public Integer getWarehouseID()
	{
		return this.warehouseID;
	}
	
	public Integer getPropertyID()
	{
		return this.propertyID;
	}
	
	public HashMap<Integer, ItemStack> getInventory()
	{
		return this.playerInv;
	}
	
	public boolean isStarted()
	{
		return this.started;
	}
	
	public Long getExpire()
	{
		Long expire = getStart() + Long.valueOf((this.maxTime*1000));
		return expire;
	}
}
