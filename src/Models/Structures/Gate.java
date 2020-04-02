package Models.Structures;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import DataManager.Worldguard;
import DataManager.Structures.Gates;
import DataManager.spawnpoints.SpawnpointStructures;
import Gates.GateAnimation;
import Handlers.ColorOptions;
import Main.Main;
import Models.spawnpoint.SpawnpointGateGuard;
import Products.Product;
import Products.ProductCategory;
import Users.User;

public class Gate extends Structure
{
	protected Main main = Main.getPlugin(Main.class);
	protected Product product = new Product();
	protected ProductCategory productCategory = new ProductCategory();
	
	protected int materialID;
	protected String faceDirection;
	protected double originalHealth = 200;
	protected double health;
	protected ProtectedRegion gateRegion;
	protected boolean isClosed = false;
	protected List<SpawnpointGateGuard> guardSpawnpoints = new ArrayList<SpawnpointGateGuard>();
	protected int respawnRate = 10; //600
	protected boolean isInvincible = false;
	protected boolean isDestroyed = false;
	protected boolean canRespawn = true;
	protected boolean isActive = true;
	protected ArmorStand GateEntity;
	
	public Gate(int ID, 
			String name, 
			int streetID, 
			int townID, 
			int districtID,
			int materialID, 
			double originalHealth, 
			double health, 
			String faceDirection, 
			boolean isClosed, 
			boolean newGate)
	{
		super (ID, name, streetID, -1, townID, districtID, SpawnpointStructures.InstantiateSpawnpointStructure(ID));

		this.materialID = materialID;
		this.originalHealth = originalHealth;
		this.health = health;
		this.faceDirection = faceDirection;
		this.isClosed = isClosed;
		this.gateRegion = Worldguard.getRegionManager(Bukkit.getWorld("world")).getRegion("gate_" + this.id + "_gate");
		
		Gates.Gates.add(this);
		if (!newGate)
		{
			this.changeGateBlocks(true);
			this.trySetInvincibleEntity();
		}
	}
	
	public ProtectedRegion getRegion()
	{
		return this.gateRegion;
	}
	
	public boolean getClosed()
	{
		return this.isClosed;
	}
	
	public boolean getInvincible()
	{
		return this.isInvincible;
	}
	
	public int getMaterialID()
	{
		return this.materialID;
	}
	
	public String getFaceDirection()
	{
		return this.faceDirection;
	}
	
	public double getOriginalHealth()
	{
		return this.originalHealth;
	}
	
	public double getHealth()
	{
		return this.health;
	}
	
	public ArmorStand getGateEntity()
	{
		return this.GateEntity;
	}
	
	public boolean getDestroyed()
	{
		return this.isDestroyed;
	}
	
	public boolean getRespawn()
	{
		return this.canRespawn;
	}
	
	public boolean getActive()
	{
		return this.isActive;
	}
	
	public void setRespawn(boolean canRespawn)
	{
		this.canRespawn = canRespawn;
	}
	
	public void setDestroyed(boolean isDestroyed)
	{
		this.isDestroyed = isDestroyed;
		this.changeGateBlocks(false);
	}
	
	public void setOriginalHealth(double health)
	{
		this.originalHealth = health;
		if (this.health > this.originalHealth)
		{
			this.setHealth(this.originalHealth);
		}
	}
	
	public void addOriginalHealth(double health)
	{
		this.setOriginalHealth((this.originalHealth+health));
	}
	
	public void removeOriginalHealth(double health)
	{
		this.setOriginalHealth((this.originalHealth-health));
	}
	
	public void setHealth(double health)
	{
		this.health = health;
		this.GateEntity.setCustomName(ColorOptions.message + "Gate: " + this.health + "hp");
		if (this.health <= 0)
		{
			this.destroyGate(true);
		}
		if (this.health > this.originalHealth)
		{
			this.health = this.originalHealth;
		}
	}
	
	public void addHealth(double health)
	{
		this.setHealth((this.health+health));
	}
	
	public void removeHealth(double health)
	{
		this.setHealth((this.health-health));
	}
	
	public void setMaterialID(Integer materialID)
	{
		this.materialID = materialID;
		this.changeGateBlocks(false);
	}
	
	public void remove(CommandSender sender)
	{
		Gates.saveGate(this);
		Gates.Gates.remove(this);
		Gates.destroyGate(this);
		try
		{
			this.GateEntity.remove();
		} catch (Exception ex)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Error while removing Gate Entity");
			ex.printStackTrace();
		}
	}
	
	public boolean removePermanently(CommandSender sender)
	{
		this.setClosed(false);
		boolean removed = false;
		try
		{
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Gates WHERE ID=?");
			stmt.setInt(1, this.id);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Deleted gate with ID " + id + " from the Database at " + main.getTime());
		} catch (Exception ex)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Error while removing gate " + this.getId() + " from the database. Please note that the gate might not have an active gateRegion due to this");
			ex.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Error while removing gate " + this.getId() + " from the database. Please notify a developer");
			removed = false;
		}
		
		try
		{
			Worldguard.getRegionManager(Bukkit.getWorld("world")).removeRegion("gate_" + this.getId());
			removed = true;
		} catch (Exception ex)
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Error while removing the WorldGuard gateRegion of gate " + this.getId() + ". Please note that the gate might have been removed from the database");
			ex.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Error while removing gate " + this.getId() + " from the database. Please notify a developer");
			return removed;
		}
		
		if (removed)
		{
			Gates.Gates.remove(this);
			Gates.destroyGate(this);
			try
			{
				this.GateEntity.remove();
			} catch (Exception ex)
			{
				Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Error while removing Gate Entity");
				ex.printStackTrace();
			}
			removed = true;
		}
		
		return removed;
	}
	
	public Location getGateCenter()
	{
		Location location = null;
       
		//World
		World world = Bukkit.getWorld("world");
		
        //Get top location
        Location top = new Location(world, 0, 0, 0);
        top.setX(gateRegion.getMaximumPoint().getX());
        top.setY(gateRegion.getMaximumPoint().getY());
        top.setZ(gateRegion.getMaximumPoint().getZ());
       
        //Get bottom location
        Location bottom = new Location(world, 0, 0, 0);
        bottom.setX(gateRegion.getMinimumPoint().getX());
        bottom.setY(gateRegion.getMinimumPoint().getY());
        bottom.setZ(gateRegion.getMinimumPoint().getZ());
       
        //Split difference
        double X =  ((bottom.getX() + top.getX())/2);
        double Y =  ((bottom.getY() + top.getY())/1.98);
        double Z =  ((bottom.getZ() + top.getZ())/2);
        //Setup new location
        location = new Location(world, X, Y, Z);
        
        if (this.getFaceDirection().equalsIgnoreCase("North"))
        {
        	location.add(-0.5, 0, -1.5);
        } else if (this.getFaceDirection().equalsIgnoreCase("East"))
        {
        	location.add(1.5, 0, 0.5);
        } else if (this.getFaceDirection().equalsIgnoreCase("South"))
        {
        	location.add(0.5, 0, 1.5);
        } else if (this.getFaceDirection().equalsIgnoreCase("West"))
        {
        	location.add(-1.5, 0, 0.5);
        }
        Main.logMessage(location.toString());
		return location;
	}
	
	public void addRegion(ProtectedRegion region)
	{
		this.gateRegion = region;
	}
	
	public void setInvincible(boolean invincible)
	{
		this.isInvincible = invincible;
		this.trySetInvincibleEntity();
	}
	
	public void toggleClosed()
	{
		if (this.isClosed)
		{
			this.setClosed(false);
		} else
		{
			this.setClosed(true);
		}
	}
	
	public void setClosed(boolean closed)
	{
		this.isClosed = closed;

		this.changeGateBlocks(false);
		//Change event?
	}
	
	//The parameter isInit indicates if the changeGateBlocks method is called when initializing this instance
	public void changeGateBlocks(boolean isInit)
	{
		ItemStack fromMaterial = new ItemStack(Material.AIR);
		ItemStack toMaterial = new ItemStack(Material.AIR);
		
		if (this.isClosed && !this.isDestroyed)
		{
			toMaterial = this.product.createPropertyItem(this.materialID, 1, false, false);
		} else
		{
			fromMaterial = this.product.createPropertyItem(this.materialID, 1, false, false);
		}
		
		try
		{
			new GateAnimation(fromMaterial, toMaterial, this, isInit);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
//		World world = Bukkit.getWorld("world");
//		CuboidRegion curegion = new CuboidRegion(BukkitUtil.getLocalWorld(world), this.region.getMinimumPoint(), this.region.getMaximumPoint());
//		for (BlockVector blockv : curegion) 
//		{
//		    Block block = BukkitUtil.toBlock(new BlockWorldVector(BukkitUtil.getLocalWorld(world), blockv));
//		    if (block.getType() == fromMaterial.getType())
//		    {
//			    block.setType(toMaterial.getType());
//			    block.setData(toMaterial.getData().getData());
//		    }
//		}
//		this.trySetInvincibleEntity(); 
	}
	
	//Creates or removes an entity used to get damage for the gate
	public void trySetInvincibleEntity()
	{
		if (this.GateEntity == null)
		{
			World world = Bukkit.getWorld("world");
			Location location = this.getGateCenter();
			if (location == null)
			{
				Bukkit.getConsoleSender().sendMessage("No location center could be found!");
				return;
			}
			final ArmorStand arm = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
			arm.setCustomName(ColorOptions.message + "Gate: " + this.health + "hp");
			arm.setCustomNameVisible(true);
			arm.setGravity(false);
			arm.setVisible(false);
			arm.setSmall(true);
			arm.setMarker(true);
			Bukkit.getConsoleSender().sendMessage("Spawned GateEntity");
			this.GateEntity = arm;
		}
	}
	
	public void damageGate(User user)
	{
		double damage = 1;
		Player player = user.getPlayer();
		ItemStack item = player.getItemInHand();
		Integer productID = null;
		try
		{
			productID = this.product.getProductIDbyDisplayName(item.getItemMeta().getDisplayName(), false);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			if (!user.inOwnerModus())
			{
				player.getItemInHand().setType(Material.AIR);
				player.updateInventory();
				player.sendMessage(ColorOptions.error + "Items without grades are not allowed!");
			}
			return;
		}
		if (this.isInvincible)
		{
			return;
		}
		
		if (this.GateEntity == null)
		{
			this.trySetInvincibleEntity();
		}

		if (item != null && item.getType() != Material.AIR)
		{
			double itemDamage = this.product.getItemDamage(item);
			
			if (this.productCategory.getCategoryName(this.product.getCategoryID(productID, false)).equalsIgnoreCase("tools"))
			{
				itemDamage *= 2;
			}
			
			itemDamage += this.product.getEnchantmentDamage(item);
			
			if (item.containsEnchantment(Enchantment.DIG_SPEED))
			{
				itemDamage += (0.2 + (0.2 * item.getEnchantmentLevel(Enchantment.DIG_SPEED)));
			}
			if (item.containsEnchantment(Enchantment.FIRE_ASPECT))
			{
				itemDamage += (1 + (1 * item.getEnchantmentLevel(Enchantment.FIRE_ASPECT)));
			}
			
			damage += itemDamage;
		}
		
		this.removeHealth(damage);
	}
	
	public void destroyGate(boolean respawn)
	{
		this.setDestroyed(true);
		Bukkit.getWorld("world").playSound(this.getGateCenter(), Sound.EXPLODE, 6.0F, 1.0F);
		Bukkit.getWorld("world").createExplosion(this.getGateCenter(), 0.0F);
		this.GateEntity.setCustomName(ColorOptions.message + "Gate: " + ColorOptions.error + "Destroyed");
		if (respawn)
		{
			this.tryRespawnGate();
		}
	}
	
	public void tryRespawnGate()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				setDestroyed(false);
				health = originalHealth;
				GateEntity.setCustomName(ColorOptions.message + "Gate: " + health + "hp");
			}
		}.runTaskLaterAsynchronously(main, this.respawnRate*20);
	}
	
	public void toggleActive(boolean active)
	{
		this.isActive = active;
		
		if (!active)
		{
//			this.GateEntity.remove();
//			this.GateEntity = null;
			
			this.GateEntity.setCustomNameVisible(false);
		} else
		{
//			this.trySetInvincibleEntity();
			
			this.GateEntity.setCustomNameVisible(true);
		}
	}
}
