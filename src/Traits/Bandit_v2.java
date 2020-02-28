package Traits;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import Main.Main;
import Minigames.BanditSpawn;
import Users.offlineUser;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.util.PlayerAnimation;

public class Bandit_v2 extends Trait
{
	Main main = Main.getPlugin(Main.class);

	@Persist
	private boolean data = false;
	public boolean chasing = false;
	public Location startChaseLocation = null;
	public int timeForceMove = 5*20;
	public int timeNoMove = 0;
	int cleverTicks = 0;
	double speed = 2.5D;
	public int attackRate = 30;
	public int attackRateRanged = 30;
	public int healRate = 30;
	public double health = 15.0D;
	public double chaseRange = 5.0D;
	public double range = 20.0D;
	public double damage = -1.0D;
	public double accuracy = 0.0D;
	public double armor = -1.0D;
	public long enemyTargetTime = 0L;
	public long timeSinceAttack = 0L;
	public long timeSinceHeal = 0L;
	public long guardingUpper = 0L;
	public long guardingLower = 0L;
	public boolean needsAmmo = false;
	public boolean chased = false;
	public boolean autoswitch = false;
	public boolean realistic = false;
	public boolean rangedChase = false;
	public boolean closeChase = true;
	Location bunny_goal = new Location(null, 0.0D, 0.0D, 0.0D);

	public HashSet<NPCTarget> currentTargets = new HashSet();
	private HashSet<UUID> greetedAlready = new HashSet();

	public Bandit_v2() {
		super("Bandit_v2");
		
	}
	public LivingEntity getLivingEntity() {
		return (LivingEntity) this.npc.getEntity();
	}

	public void faceLocation(Location l) {
		this.npc.faceLocation(l.clone().subtract(0.0D, getLivingEntity().getEyeHeight(), 0.0D));
	}

	public void rechase() {
		if (this.chasing == false) {
			chase(this.findBestTarget());
		}
	}

	public void swingWeapon() {
		if ((this.npc.isSpawned()) && ((getLivingEntity() instanceof Player))) {
			PlayerAnimation.ARM_SWING.play((Player) getLivingEntity());
		}
	}

	@EventHandler
	public void onDamageTaken(EntityDamageByEntityEvent event) {
		  {
			    if (!this.npc.isSpawned()) {
			      return;
			    }
			    if (event.isCancelled()) {
			      return;
			    }
			    if (event.getEntity().getUniqueId().equals(getLivingEntity().getUniqueId()))
			    {
			      if (!event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) {
			        event.setDamage(EntityDamageEvent.DamageModifier.BASE, (1.0D - getArmor(getLivingEntity())) * event.getDamage(EntityDamageEvent.DamageModifier.BASE));
			      } else {
			        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, -getArmor(getLivingEntity()) * event.getDamage(EntityDamageEvent.DamageModifier.BASE));
			      }
			      return;
			    }
			    if (event.getDamager().getUniqueId().equals(getLivingEntity().getUniqueId()))
			    {
			      event.setDamage(EntityDamageEvent.DamageModifier.BASE, getDamage());
			    }
			    if ((event.getDamager() instanceof Projectile))
			    {
			      ProjectileSource source = ((Projectile)event.getDamager()).getShooter();
			      if (((source instanceof LivingEntity)) && (((LivingEntity)source).getUniqueId().equals(getLivingEntity().getUniqueId())))
			      {
			        double dam = getDamage();
			        double modder = event.getDamage(EntityDamageEvent.DamageModifier.BASE);
			        double rel = modder == 0.0D ? 1.0D : dam / modder;
			        event.setDamage(EntityDamageEvent.DamageModifier.BASE, dam);
			        for (EntityDamageEvent.DamageModifier mod : EntityDamageEvent.DamageModifier.values()) {
			          if ((mod != EntityDamageEvent.DamageModifier.BASE) && (event.isApplicable(mod)))
			          {
			            event.setDamage(mod, event.getDamage(mod) * rel);
			          }
			        }
			      }
			    } else
			    {
			    	chase((LivingEntity) event.getDamager());
			    }
			  }
	}
	
	public double getDistance(Location location)
	{
		double dist = getLivingEntity().getEyeLocation().distanceSquared(location);
		
		return dist;
	}

	public void chase(Entity entity)
	{
		double dist = this.getDistance(entity.getLocation());
		
		if (!this.npc.getNavigator().isNavigating())
		{
			this.chasing = false;
		}
		
		//Checks if NPC navigator isn't bugged and npc is moving. If not (npc is not moving for some time), than force the npc to restart the chase
		if (this.startChaseLocation != null)
		{
			if (this.getDistance(this.startChaseLocation) < 2.0D)
			{
				this.timeNoMove++;
				if (timeNoMove >= this.timeForceMove)
				{
					this.chasing = false;
					this.timeNoMove = 0;
				}
			}
		}
		
		//Check if NPC reached its target or is not navigating at all
		if (dist < 5.0D)
		{
			this.chasing = false;
			return;
		}
		
		if (!chasing)
		{
			this.startChase(entity);
		}
	}
	
	public void startChase(Entity entity)
	{
		if (!npc.isSpawned())
		{
			return;
		}
		Bukkit.getConsoleSender().sendMessage("Debug 3.2: chasing false, setting new chase");
		this.npc.getNavigator().cancelNavigation();
		Bukkit.getConsoleSender().sendMessage("Debug 3.3: cancelled navigation");
		this.npc.getNavigator().getDefaultParameters().stuckAction(null);
		Bukkit.getConsoleSender().sendMessage("Debug 3.4: reset stuckation?");
		this.npc.getNavigator().setTarget(entity.getLocation());
		Bukkit.getConsoleSender().sendMessage("Debug 3.5: Set target to chase: " + entity.getName());
		this.chasing = true;
		this.startChaseLocation = this.npc.getEntity().getLocation();
		this.timeNoMove = 0;
		this.npc.getNavigator().getLocalParameters().speedModifier(1.24F);
	}
	
	public Entity getTargetFor(EntityTarget targ) {
		try {
			Method meth = EntityTarget.class.getMethod("getTarget", new Class[0]);
			meth.setAccessible(true);
			return (LivingEntity) meth.invoke(targ, new Object[0]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void addTarget(UUID uuid) {
		if (uuid.equals(getLivingEntity().getUniqueId())) {
			return;
		}
		if (!(getEntityForID(uuid) instanceof LivingEntity)) {
			return;
		}
		addTargetNoBounce(uuid);
	}

	public void addTargetNoBounce(UUID id) {
		NPCTarget target = new NPCTarget();
		target.targetID = id;
		target.ticksLeft = this.enemyTargetTime;
		this.currentTargets.remove(target);
		this.currentTargets.add(target);
	}

	public void punch(LivingEntity entity) 
	{
		offlineUser user = new offlineUser();
		faceLocation(entity.getLocation());
		swingWeapon();
		entity.damage(getDamage() * (1.0D-(user.getDamageReduced(entity)+.04)));
	}

	public double getDamage() {
		if (this.damage < 0.0D) {
			ItemStack weapon;
			weapon = getLivingEntity().getEquipment().getItemInHand();

			if (weapon == null) {
				return 1.0D;
			}
			double multiplier = 1.0D;
			multiplier += ((weapon.getItemMeta() == null) || (!weapon.getItemMeta().hasEnchant(Enchantment.DAMAGE_ALL))
					? 0.0D
					: weapon.getItemMeta().getEnchantLevel(Enchantment.DAMAGE_ALL) * 0.2D);
			switch (weapon.getType()) {
			case BOW:
				return 6.0D * (1.0D + ((weapon.getItemMeta() == null)
						|| (!weapon.getItemMeta().hasEnchant(Enchantment.ARROW_DAMAGE)) ? 0.0D
								: weapon.getItemMeta().getEnchantLevel(Enchantment.ARROW_DAMAGE) * 0.3D));
			case DIAMOND_SWORD:
				return 7.0D * multiplier;
			case IRON_SWORD:
				return 6.0D * multiplier;
			case STONE_SWORD:
				return 5.0D * multiplier;
			case GOLD_SWORD:
			case WOOD_SWORD:
				return 4.0D * multiplier;
			case DIAMOND_AXE:
			case IRON_AXE:
			case STONE_AXE:
			case GOLD_AXE:
			case WOOD_AXE:
				return 3.0D * multiplier;
			case DIAMOND_PICKAXE:
			case IRON_PICKAXE:
			case STONE_PICKAXE:
			case GOLD_PICKAXE:
			case WOOD_PICKAXE:
				return 2.0D * multiplier;
			}
			return 1.0D * multiplier;
		}
		return this.damage;
	}

	public double getArmor(LivingEntity ent) {
		if (this.armor < 0.0D) {
			double baseArmor = 0.0D;
			ItemStack helmet = ent.getEquipment().getHelmet();
			if ((helmet != null) && (helmet.getType() == Material.DIAMOND_HELMET)) {
				baseArmor += 0.12D;
			}
			if ((helmet != null) && (helmet.getType() == Material.GOLD_HELMET)) {
				baseArmor += 0.08D;
			}
			if ((helmet != null) && (helmet.getType() == Material.IRON_HELMET)) {
				baseArmor += 0.08D;
			}
			if ((helmet != null) && (helmet.getType() == Material.LEATHER_HELMET)) {
				baseArmor += 0.04D;
			}
			if ((helmet != null) && (helmet.getType() == Material.CHAINMAIL_HELMET)) {
				baseArmor += 0.08D;
			}
			ItemStack chestplate = ent.getEquipment().getChestplate();
			if ((chestplate != null) && (chestplate.getType() == Material.DIAMOND_CHESTPLATE)) {
				baseArmor += 0.32D;
			}
			if ((chestplate != null) && (chestplate.getType() == Material.GOLD_CHESTPLATE)) {
				baseArmor += 0.2D;
			}
			if ((chestplate != null) && (chestplate.getType() == Material.IRON_CHESTPLATE)) {
				baseArmor += 0.24D;
			}
			if ((chestplate != null) && (chestplate.getType() == Material.LEATHER_CHESTPLATE)) {
				baseArmor += 0.12D;
			}
			if ((chestplate != null) && (chestplate.getType() == Material.CHAINMAIL_CHESTPLATE)) {
				baseArmor += 0.2D;
			}
			ItemStack leggings = ent.getEquipment().getLeggings();
			if ((leggings != null) && (leggings.getType() == Material.DIAMOND_LEGGINGS)) {
				baseArmor += 0.24D;
			}
			if ((leggings != null) && (leggings.getType() == Material.GOLD_LEGGINGS)) {
				baseArmor += 0.12D;
			}
			if ((leggings != null) && (leggings.getType() == Material.IRON_LEGGINGS)) {
				baseArmor += 0.2D;
			}
			if ((leggings != null) && (leggings.getType() == Material.LEATHER_LEGGINGS)) {
				baseArmor += 0.08D;
			}
			if ((leggings != null) && (leggings.getType() == Material.CHAINMAIL_LEGGINGS)) {
				baseArmor += 0.16D;
			}
			ItemStack boots = ent.getEquipment().getBoots();
			if ((boots != null) && (boots.getType() == Material.DIAMOND_BOOTS)) {
				baseArmor += 0.12D;
			}
			if ((boots != null) && (boots.getType() == Material.GOLD_BOOTS)) {
				baseArmor += 0.04D;
			}
			if ((boots != null) && (boots.getType() == Material.IRON_BOOTS)) {
				baseArmor += 0.08D;
			}
			if ((boots != null) && (boots.getType() == Material.LEATHER_BOOTS)) {
				baseArmor += 0.04D;
			}
			if ((boots != null) && (boots.getType() == Material.CHAINMAIL_BOOTS)) {
				baseArmor += 0.04D;
			}
			return Math.min(baseArmor, 0.8D);
		}
		return this.armor;
	}

	private Entity getEntityForID(UUID id) {
		Entity entity = null;
		for (Entity e : getLivingEntity().getWorld().getEntities()) {
			if (e.getUniqueId().equals(id)) {
				entity = e;
				break;
			}
		}

		return entity;
	}

//	public void tryAttack(LivingEntity entity) {
//		this.faceLocation(entity.getEyeLocation());
//		if (!entity.getWorld().equals(getLivingEntity().getWorld())) {
//			return;
//		}
//		if (!getLivingEntity().hasLineOfSight(entity)) {
//			return;
//		}
//		double dist = getLivingEntity().getEyeLocation().distanceSquared(entity.getEyeLocation());
//		NPCAttack sat = new NPCAttack(this.npc);
//		Bukkit.getPluginManager().callEvent(sat);
//		if (sat.isCancelled()) {
//			return;
//		}
//		if (dist < 4.0D) {
//			if (this.timeSinceAttack < this.attackRate) 
//			{
//				if (this.closeChase) 
//				{
//					rechase();
//				}
//				return;
//			}
//			this.timeSinceAttack = 0L;
//
//			punch(entity);
//		} else
//		{
//			chase(entity);
//		}
//	}
	public void tryAttack(LivingEntity entity) 
	{
		this.npc.faceLocation(entity.getLocation());
		
		if (!entity.getWorld().equals(getLivingEntity().getWorld())) 
		{
//			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Error 1.0: TestTrait no matching world when attacking!");
			return;
		}
		
		if (!getLivingEntity().hasLineOfSight(entity)) 
		{
//			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Error 1.1: TestTrait no line of sight when attacking!");
			return;
		}
		double dist = this.getDistance(entity.getLocation());
//		Bukkit.getConsoleSender().sendMessage("Debug 1.0: Dist: " + dist + ", " + this.timeSinceAttack);
		this.timeSinceAttack ++;
		if (dist < 5.5D) 
		{
//			Bukkit.getConsoleSender().sendMessage("Debug 1.1: Dist smaller than 4, trying attack: " + dist);
			if (this.timeSinceAttack < this.attackRate) 
			{
//				Bukkit.getConsoleSender().sendMessage("Debug 1.1.0: TimeSinceAttack smaller than attackrate, chasing entity: " + this.timeSinceAttack + ", rate: " + this.attackRate);
				chase(entity);
				return;
			}
//			Bukkit.getConsoleSender().sendMessage("Debug 1.2: Attackrate matched, attacking and setting timesinceattack to 0: " + this.timeSinceAttack + ", rate: " + this.attackRate);
			this.timeSinceAttack = 0;
//			Bukkit.getConsoleSender().sendMessage("Debug 1.3: Reset timesinceattack, punching..: " + this.timeSinceAttack);
			punch(entity);
		} else
		{
//			Bukkit.getConsoleSender().sendMessage("Debug 1.0.1: Distance larger than 4, chasing..: " + dist);
			chase(entity);
		}
	}

	public void reduceDurability() {
		ItemStack item = getLivingEntity().getEquipment().getItemInHand();
		if ((item != null) && (item.getType() != Material.AIR)) {
			if (item.getDurability() >= item.getType().getMaxDurability() - 1) {
				getLivingEntity().getEquipment().setItemInHand(null);
			} else {
				item.setDurability((short) (item.getDurability() + 1));
				getLivingEntity().getEquipment().setItemInHand(item);
			}
		}
	}

	private void updateTargets() 
	{
		offlineUser user = new offlineUser();
		for (NPCTarget uuid : new HashSet<NPCTarget>(this.currentTargets)) 
		{
			Entity e = getEntityForID(uuid.targetID);
			if (e != null)
			{
				chase((LivingEntity)e);
			}
			if (e == null) 
			{
				this.currentTargets.remove(uuid);
			} else if (((e instanceof Player)) && (((Player) e).getGameMode() == GameMode.CREATIVE)) 
			{
				this.currentTargets.remove(uuid);
			} else if (((e instanceof LivingEntity)) && (((LivingEntity) e).hasPotionEffect(PotionEffectType.INVISIBILITY))) 
			{
				this.currentTargets.remove(uuid);
			} else if (e.isDead()) 
			{
				this.currentTargets.remove(uuid);
			} else 
			{
				double d = e.getWorld().equals(getLivingEntity().getWorld()) ? e.getLocation().distanceSquared(getLivingEntity().getLocation())	: 1.0E8D;
				if ((d > this.range * this.range * 4.0D) && (d > this.chaseRange * this.chaseRange * 4.0D)) 
				{
					this.currentTargets.remove(uuid);
				} else if (uuid.ticksLeft > 0L) 
				{
					uuid.ticksLeft -= main.tickRate;
					if (uuid.ticksLeft <= 0L) 
					{
						this.currentTargets.remove(uuid);
					}
				}
			}
		}
		if (this.chasing == false) 
		{
			NPCTarget cte = new NPCTarget();
			if (!this.currentTargets.contains(cte)) {
				//this.chasing = null;
				this.npc.getNavigator().cancelNavigation();
			}
		}
	}

	public void runUpdate() 
	{
		Player entityNPC = (Player) npc.getEntity();
		entityNPC.getInventory().setHeldItemSlot(0);
		this.timeSinceAttack += main.tickRate;
		this.timeSinceHeal += main.tickRate;
		if (getLivingEntity().getLocation().getY() <= 0.0D) 
		{
			getLivingEntity().damage(1.0D);
		}
		if ((this.healRate > 0) && (this.timeSinceHeal > this.healRate) && (getLivingEntity().getHealth() < this.health)) 
		{
			getLivingEntity().setHealth(Math.min(getLivingEntity().getHealth() + 1.0D, this.health));
			this.timeSinceHeal = 0L;
		}
		double crsq = this.chaseRange * this.chaseRange;
		updateTargets();
		boolean goHome = this.chased;
		LivingEntity target = findBestTarget();
		if (target != null) 
		{
			Location near = getLivingEntity().getLocation();
			if ((crsq <= 0.0D) || (near == null) || (near.distanceSquared(target.getLocation()) <= crsq)) 
			{
				//this.chasing = target;
				this.cleverTicks = 0;
				tryAttack(target);
				goHome = false;
			}
		}
//		else if ((this.chasing != null) && (this.chasing.isValid())) 
//		{
//			Location near = null;
//			if ((crsq <= 0.0D) || (near == null) || (near.distanceSquared(this.chasing.getLocation()) <= crsq)) 
//			{
//				tryAttack(this.chasing);
//				goHome = false;
//			}
//		}
		if ((goHome) && (this.chaseRange > 0.0D) && (target == null)) 
		{

//			Location near = null;
//			if ((near != null)
//					&& ((this.chasing == null) || (near.distanceSquared(this.chasing.getLocation()) > crsq))) {
//				this.npc.getNavigator().getDefaultParameters().stuckAction(TeleportStuckAction.INSTANCE);
//				this.npc.getNavigator().setTarget(near);
//				this.npc.getNavigator().getLocalParameters().speedModifier((float) this.speed);
//				this.chased = false;
//			} else {
//				if (this.npc.getNavigator().getEntityTarget() != null) {
//					this.npc.getNavigator().cancelNavigation();
//				}
//			}
			updateTargets();
		}
//		 else if ((this.chasing == null) && (this.npc.getNavigator().getEntityTarget() != null)) 
//		{
//			this.npc.getNavigator().cancelNavigation();
//		}
	}

	public boolean shouldTarget(LivingEntity entity) {
		boolean target = false;
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (entity instanceof Player && (Player) entity == player)
			{
				target = true;
			}
		}
		return target;
	}

	public LivingEntity findBestTarget() {
		double rangesquared = this.range * this.range;
		double crsq = this.chaseRange * this.chaseRange;
		LivingEntity closest = null;
		for (NPCTarget target : this.currentTargets) {
			LivingEntity ent = (LivingEntity) Bukkit.getPlayer(target.targetID);
			if (ent != null && !ent.isDead()) {
				double dist = ent.getEyeLocation().distanceSquared(ent.getLocation());
				NPCTarget sct = new NPCTarget();
				sct.targetID = ent.getUniqueId();
				if (((dist < crsq) && (this.currentTargets.contains(sct)))) 
				{
					rangesquared = dist;
					closest = ent;
				}
			}
		}
		return closest;
	}

	public int cTick = 0;

	public void run() {
		if (!this.npc.isSpawned()) {
			return;
		}
		this.cTick += 1;
		if (this.cTick >= main.tickRate) 
		{
			this.cTick = 0;
			runUpdate();
		}
	}

	public void onSpawn() {
		Player entityNPC = (Player) npc.getEntity();
		for (UUID uuid : BanditSpawn.banditID.keySet())
		{
			if (BanditSpawn.banditID.get(uuid).contains(this.npc.getId()))
			{
				//equipBandit(entityNPC, uuid);
				addTarget(uuid);
				break;
			}
		}
	}
	
//	public void equipBandit(Player bandit, User target)
//	{
//		Product product = new Product();
//		
//		Integer enchLevel = 1;
//		Integer enchChance = 30;
//		Integer armorMax = 5;
//		Integer armorMin = 1;
//		
//		Integer titleID = target.getTitleID();
//		
//		try
//		{
//			ItemStack sword = product.createAmountItem(Material.IRON_SWORD, 1, ChatColor.RED + "Sword", ChatColor.RED + "Soulbound");
//			bandit.getInventory().setItem(0, sword);
//			bandit.updateInventory();
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		if (titleID <= 5)
//		{
//			enchChance = 60;
//			armorMax = 3;
//			
//		} else if (titleID <= 10 && titleID > 5)
//		{
//			armorMin = 2;
//			armorMax = 3;
//			enchLevel = 2;
//			enchChance = 80;
//		} else if (titleID > 10)
//		{
//			armorMin = 4;
//			enchLevel = 4;
//			enchChance = 95;
//		}
//		
//		Integer randomHelmet = main.getRandom(1, 5);
//		Integer randomChestplate = main.getRandom(1, 5);
//		Integer randomLeggings = main.getRandom(1, 5);
//		Integer randomBoots = main.getRandom(1, 5);
//		
//		Material helmet = Material.LEATHER_HELMET;
//		Material chestplate = Material.LEATHER_CHESTPLATE;
//		Material leggings = Material.LEATHER_LEGGINGS;
//		Material boots = Material.LEATHER_BOOTS;
//		
//		switch(randomHelmet)
//		{
//		case 1:
//			break;
//		case 2:
//			helmet = Material.GOLD_HELMET;
//			break;
//		case 3:
//			helmet = Material.IRON_HELMET;
//			break;
//		case 4:
//			helmet = Material.DIAMOND_HELMET;
//			break;
//		case 5:
//			helmet = Material.CHAINMAIL_HELMET;
//			break;
//		}
//		
//		switch(randomChestplate)
//		{
//		case 1:
//			break;
//		case 2:
//			chestplate = Material.GOLD_CHESTPLATE;
//			break;
//		case 3:
//			chestplate = Material.IRON_CHESTPLATE;
//			break;
//		case 4:
//			chestplate = Material.DIAMOND_CHESTPLATE;
//			break;
//		case 5:
//			chestplate = Material.CHAINMAIL_CHESTPLATE;
//			break;
//		}
//		
//		switch(randomLeggings)
//		{
//		case 1:
//			break;
//		case 2:
//			leggings = Material.GOLD_LEGGINGS;
//			break;
//		case 3:
//			leggings = Material.IRON_LEGGINGS;
//			break;
//		case 4:
//			helmet = Material.DIAMOND_LEGGINGS;
//			break;
//		case 5:
//			leggings = Material.CHAINMAIL_LEGGINGS;
//			break;
//		}
//		
//		switch(randomBoots)
//		{
//		case 1:
//			break;
//		case 2:
//			boots = Material.GOLD_BOOTS;
//			break;
//		case 3:
//			boots = Material.IRON_BOOTS;
//			break;
//		case 4:
//			boots = Material.DIAMOND_BOOTS;
//			break;
//		case 5:
//			boots = Material.CHAINMAIL_BOOTS;
//			break;
//		}
//		
//		if (helmet == Material.CHAINMAIL_HELMET)
//		{
//			ItemStack item = new ItemStack(helmet, 1);
//			if (main.getRandom(1, 100) <= enchChance)
//			{
//				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchLevel);
//				item.addEnchantment(Enchantment.DURABILITY, 3);
//			}
//			bandit.getInventory().setHelmet(product.createItem(ChatColor.RED + "Helmet", item, true, ChatColor.RED + "Soulbound"));
//			bandit.updateInventory();
//		} else
//		{
//			ItemStack item = new ItemStack(helmet, 1);
//			bandit.getInventory().setHelmet(product.createItem(ChatColor.RED + "Helmet", item, true, ChatColor.RED + "Soulbound"));
//		}
//		if (chestplate == Material.CHAINMAIL_CHESTPLATE)
//		{
//			ItemStack item = new ItemStack(chestplate, 1);
//			if (main.getRandom(1, 100) <= enchChance)
//			{
//				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchLevel);
//				item.addEnchantment(Enchantment.DURABILITY, 3);
//			}
//			bandit.getInventory().setChestplate(product.createItem(ChatColor.RED + "Chestplate", item, true, ChatColor.RED + "Soulbound"));
//			bandit.updateInventory();
//		} else
//		{
//			ItemStack item = new ItemStack(chestplate, 1);
//			bandit.getInventory().setChestplate(product.createItem(ChatColor.RED + "Chestplate", item, true, ChatColor.RED + "Soulbound"));
//		}
//		if (leggings == Material.CHAINMAIL_LEGGINGS)
//		{
//			ItemStack item = new ItemStack(leggings, 1);
//			if (main.getRandom(1, 100) <= enchChance)
//			{
//				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchLevel);
//				item.addEnchantment(Enchantment.DURABILITY, 3);
//			}
//			bandit.getInventory().setLeggings(product.createItem(ChatColor.RED + "Leggings", item, true, ChatColor.RED + "Soulbound"));
//			bandit.updateInventory();
//		} else
//		{
//			ItemStack item = new ItemStack(leggings, 1);
//			bandit.getInventory().setLeggings(product.createItem(ChatColor.RED + "Leggings", item, true, ChatColor.RED + "Soulbound"));
//		}
//		if (boots == Material.CHAINMAIL_BOOTS)
//		{
//			ItemStack item = new ItemStack(boots, 1);
//			if (main.getRandom(1, 100) <= enchChance)
//			{
//				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchLevel);
//				item.addEnchantment(Enchantment.DURABILITY, 3);
//			}
//			bandit.getInventory().setBoots(product.createItem(ChatColor.RED + "Boots", item, true, ChatColor.RED + "Soulbound"));
//			bandit.updateInventory();
//		} else
//		{
//			ItemStack item = new ItemStack(boots, 1);
//			bandit.getInventory().setBoots(product.createItem(ChatColor.RED + "Boots", item, true, ChatColor.RED + "Soulbound"));
//		}
//		ItemStack item = product.createAmountItem(Material.IRON_SWORD, 1, ChatColor.RED + "Sword", ChatColor.RED + "Soulbound");
//		bandit.getInventory().setItemInHand(item);
//		bandit.updateInventory();
//		if (main.debug)
//		{
//			Bukkit.getConsoleSender().sendMessage("Gave item in hand!");
//		}
//		bandit.updateInventory();
//	}
}
