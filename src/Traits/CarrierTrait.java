package Traits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import API_methods.WorldGuard;
import Main.Main;
import Products.Product;
import Properties.Property;
import SpawnPoints.SpawnPoint;
import Towns.Town;
import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.ai.event.NavigationStuckEvent;
import net.citizensnpcs.api.trait.Trait;

public class CarrierTrait extends Trait
{
	Property property = new Property();
	SpawnPoint spawnpoint = new SpawnPoint();
	Town town = new Town();
	WorldGuard worldguard = new WorldGuard();
	Main main = Main.getPlugin(Main.class);
	Product product = new Product();
	Integer propertyID = null;
	Integer townID = null;
	boolean walking = false;
	Integer ticks = 0;
	public CarrierTrait()
	{
		super("Carrier");
	}
	
	public void onSpawn()
	{
		Entity Enpc = npc.getEntity();
		Player npc = (Player) Enpc;
//		ItemStack leggings = product.createPropertyItem(product.getProductID("leatherleggings", false), 1, false);
//		ItemStack boots = product.createPropertyItem(product.getProductID("leatherboots", false), 1, false);

//		npc.getInventory().setBoots(boots);
//		npc.getInventory().setLeggings(leggings);
		npc.updateInventory();
	}
	
	public void run()
	{
		runUpdate();
	}
	
	public void runUpdate()
	{
		if (npc.isSpawned())
		{
			Entity Enpc = npc.getEntity();
			if (this.townID != null)
			{
				if (this.propertyID != null)
				{
					if (walking == false)
					{
						this.ticks = 0;
						startWalking();
					} else
					{
						this.ticks++;
						if (this.ticks == 5)
						{
							walking = false;
							runUpdate();
						}
					}
				} else
				{
					fetchWareHouse(this.townID);
				}
			} else
			{
				fetchTown(Enpc);
			}
		}
	}
	
	public void startWalking()
	{
		npc.getNavigator().setTarget(spawnpoint.getSpawnPointLocation(property.getPropertySpawnPoint(this.propertyID)));
		this.walking = true;
	}
	
	public void fetchWareHouse(Integer townID)
	{
		Integer propertyID = null;
		
		List<Integer> properties = property.getIDListbyCategory("warehouse", townID);
		if (properties != null && !properties.isEmpty())
		{
			for (Integer propertyList : properties)
			{
				List<Chest> chests = property.getChestFromRegion("warehouse", propertyList);
				if (!chests.isEmpty())
				{
					propertyID = propertyList;
					break;
				} else
				{
					continue;
				}
			}
		} else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "No warehouses exist! Destroying carrier for town with ID " + this.townID);
			npc.destroy();
		}
		if (propertyID == null)
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("No property found, finding new town!");
			}
			fetchTown(npc.getEntity());
			return;
		}
		
		this.propertyID = propertyID;
		runUpdate();
	}
	
	public void fetchTown(Entity entityNPC)
	{
		Location loc = entityNPC.getLocation();
		Integer townID = worldguard.getStructureIDbyRegion("town", entityNPC.getLocation(), worldguard.getRegionManager(entityNPC.getLocation().getWorld()));
		if (townID != null && this.townID != null && this.townID != townID)
		{
			this.townID = townID;
		} else
		{
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
							lowest = spawnpointID;
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
		}
		
		this.townID = townID;
		runUpdate();
	}
	
	public void transferPackage()
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemStack item : ((Player) npc.getEntity()).getInventory().getContents())
		{
			list.add(item);
		}
		npc.destroy();
		property.fillWarehouse(this.propertyID, list);
	}
	
	public void onDespawn()
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Despawned an npc called " + npc.getName() + " with ID" + npc.getId());
		}
	}
	
	@EventHandler
	public void onCancel(NavigationCancelEvent e)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("NPC with name " + npc.getName() + " with ID " + npc.getId() + " stuck: " + e.getCancelReason().name());
		}
	}
	
	@EventHandler
	public void onStuck(NavigationStuckEvent e)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("NPC with name " + npc.getName() + " with ID " + npc.getId() + " stuck: " + e.getAction().toString());
		}
	}
	
	@EventHandler
	public void onComplete(NavigationCompleteEvent e)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("NPC with name " + npc.getName() + " with ID " + npc.getId() + " arrived");
		}
		transferPackage();
	}
}
