package Traits;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import Handlers.ColorOptions;
import Main.Main;
import Products.Product;
import Users.offlineUser;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.ai.TeleportStuckAction;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.util.PlayerAnimation;

public class Gladiator extends Trait {
	Main main = Main.getPlugin(Main.class);

	@Persist
	private boolean data = false;
	public LivingEntity chasing = null;
	int cleverTicks = 0;
	double speed = 1.5D;
	public int attackRate = 30;
	public int attackRateRanged = 30;
	public int healRate = 30;
	public double health = 20.0D;
	public double chaseRange = 20.0D;
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

	public Gladiator() {
		super("Gladiator");
	}

	public LivingEntity getLivingEntity() {
		return (LivingEntity) this.npc.getEntity();
	}

	public void faceLocation(Location l) {
		this.npc.faceLocation(l.clone().subtract(0.0D, getLivingEntity().getEyeHeight(), 0.0D));
	}

	public void rechase() {
		if (this.chasing != null) {
			chase(this.chasing);
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
//			      if (!event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) {
//			        event.setDamage(EntityDamageEvent.DamageModifier.BASE, (1.0D - getArmor(getLivingEntity())) * event.getDamage(EntityDamageEvent.DamageModifier.BASE));
//			      } else {
//			        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, -getArmor(getLivingEntity()) * event.getDamage(EntityDamageEvent.DamageModifier.BASE));
//			      }
//			      return;
			    }
			    if (event.getDamager().getUniqueId().equals(getLivingEntity().getUniqueId()))
			    {
//			      event.setDamage(EntityDamageEvent.DamageModifier.BASE, getDamage());
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
			    	addTarget(event.getDamager().getUniqueId());
			    }
			  }
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

	public void chase(LivingEntity entity) {
		if ((this.npc.getNavigator().getTargetType() == TargetType.LOCATION)
				&& (this.npc.getNavigator().getTargetAsLocation() != null)
				&& (((this.npc.getNavigator().getTargetAsLocation().getWorld().equals(entity.getWorld()))
						&& (this.npc.getNavigator().getTargetAsLocation().distanceSquared(entity.getLocation()) < 4.0D))
						|| ((this.npc.getNavigator().getTargetAsLocation().getWorld()
								.equals(this.bunny_goal.getWorld()))
								&& (this.npc.getNavigator().getTargetAsLocation()
										.distanceSquared(this.bunny_goal) < 4.0D)))) {
			return;
		}
		this.cleverTicks = 0;
		this.chasing = entity;
		this.chased = true;
		if ((this.npc.getNavigator().getTargetType() == TargetType.ENTITY)
				&& (getTargetFor(this.npc.getNavigator().getEntityTarget()).getUniqueId()
						.equals(entity.getUniqueId()))) {
			return;
		}
		this.npc.getNavigator().getDefaultParameters().stuckAction(null);
		this.npc.getNavigator().setTarget(entity, false);
		this.npc.getNavigator().getLocalParameters().speedModifier((float) speed);
	}

	public void punch(LivingEntity entity) {
		offlineUser user = new offlineUser();
		faceLocation(entity.getLocation());
		swingWeapon();
		entity.damage(getDamage() * (1.0D-(user.getDamageReduced(entity)*.04)));
//		entity.damage(getDamage() * (1.0D - getArmor(entity)));
		Vector relative = entity.getLocation().toVector().subtract(getLivingEntity().getLocation().toVector());
		relative = relative.normalize();
		relative.setY(0.75D);
		relative.multiply(0.5D);
		entity.setVelocity(entity.getVelocity().add(relative));
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

	public boolean isRanged() {
		return (usesBow());
	}

	public ItemStack getArrow() {
		if (!this.npc.hasTrait(Inventory.class)) {
			return this.needsAmmo ? null : new ItemStack(Material.ARROW, 1);
		}
		Inventory inv = (Inventory) this.npc.getTrait(Inventory.class);
		ItemStack[] items = inv.getContents();
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item != null) {
				Material mat = item.getType();
				if (mat == Material.ARROW) {
					return item.clone();
				}
			}
		}
		return this.needsAmmo ? null : new ItemStack(Material.ARROW, 1);
	}

	public boolean usesBow() {
		if (!this.npc.hasTrait(Inventory.class)) {
			return false;
		}
		ItemStack it = ((Inventory) this.npc.getTrait(Inventory.class)).getContents()[0];
		return (it != null) && (it.getType() == Material.BOW) && (getArrow() != null);
	}

	public void swapToRanged() {
		if (!this.npc.hasTrait(Inventory.class)) {
			return;
		}
		int i = 0;
		Inventory inv = (Inventory) this.npc.getTrait(Inventory.class);
		ItemStack[] items = inv.getContents();
		ItemStack held = items[0] == null ? null : items[0].clone();
		boolean edit = false;
		while ((!isRanged()) && (i < items.length - 1)) {
			i++;
			if ((items[i] != null) && (items[i].getType() != Material.AIR)) {
				items[0] = items[i].clone();
				items[i] = new ItemStack(Material.AIR);
				inv.setContents(items);
				edit = true;
			}
		}
		if (edit) {
			items[i] = held;
			inv.setContents(items);
		}
	}

	public void swapToMelee() {
		if (!this.npc.hasTrait(Inventory.class)) {
			return;
		}
		int i = 0;
		Inventory inv = (Inventory) this.npc.getTrait(Inventory.class);
		ItemStack[] items = inv.getContents();
		ItemStack held = items[0] == null ? null : items[0].clone();
		boolean edit = false;
		while ((isRanged()) && (i < items.length - 1)) {
			i++;
			if ((items[i] != null) && (items[i].getType() != Material.AIR)) {
				items[0] = items[i].clone();
				items[i] = new ItemStack(Material.AIR);
				inv.setContents(items);
				edit = true;
			}
		}
		if (edit) {
			items[i] = held;
			inv.setContents(items);
		}
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

	public void addTarget(UUID id) {
		if (id.equals(getLivingEntity().getUniqueId())) {
			return;
		}
		if (!(getEntityForID(id) instanceof LivingEntity)) {
			return;
		}
		addTargetNoBounce(id);
	}

	public void addTargetNoBounce(UUID id) {
		NPCTarget target = new NPCTarget();
		target.targetID = id;
		target.ticksLeft = this.enemyTargetTime;
		this.currentTargets.remove(target);
		this.currentTargets.add(target);
	}

	public float getYaw(Vector vector) {
		double dx = vector.getX();
		double dz = vector.getZ();
		double yaw = 0.0D;
		if (dx != 0.0D) {
			if (dx < 0.0D) {
				yaw = 4.71238898038469D;
			} else {
				yaw = 1.5707963267948966D;
			}
			yaw -= Math.atan(dz / dx);
		} else if (dz < 0.0D) {
			yaw = 3.141592653589793D;
		}
		return (float) (-yaw * 180.0D / 3.141592653589793D);
	}

	public boolean canSee(LivingEntity entity) {
		if (!getLivingEntity().hasLineOfSight(entity)) {
			return false;
		}
		if (this.realistic) {
			float yaw = getLivingEntity().getEyeLocation().getYaw();
			while (yaw < 0.0F) {
				yaw += 360.0F;
			}
			while (yaw >= 360.0F) {
				yaw -= 360.0F;
			}
			Vector rel = entity.getLocation().toVector().subtract(getLivingEntity().getLocation().toVector())
					.normalize();
			float yawHelp = getYaw(rel);
			if ((Math.abs(yawHelp - yaw) >= 90.0F) && (Math.abs(yawHelp + 360.0F - yaw) >= 90.0F)
					&& (Math.abs(yaw + 360.0F - yawHelp) >= 90.0F)) {
				return false;
			}
		}
		return true;
	}

	public double randomAcc() {
		Random random = new Random();
		return random.nextDouble() * this.accuracy * 2.0D - this.accuracy;
	}

	public Vector fixForAcc(Vector input) {
		if ((Double.isInfinite(input.getX())) || (Double.isNaN(input.getX()))) {
			return new Vector(0, 0, 0);
		}
		return new Vector(input.getX() + randomAcc(), input.getY() + randomAcc(), input.getZ() + randomAcc());
	}

	public static double getArrowAngle(Location fireFrom, Location fireTo, double speed, double gravity) {
		Vector delta = fireTo.clone().subtract(fireFrom).toVector();
		double deltaXZ = Math.sqrt(delta.getX() * delta.getX() + delta.getZ() * delta.getZ());
		if (deltaXZ == 0.0D) {
			deltaXZ = 0.1D;
		}
		double deltaY = fireTo.getY() - fireFrom.getY();
		double v2 = speed * speed;
		double v4 = v2 * v2;
		double basic = gravity * (gravity * deltaXZ * deltaXZ + 2.0D * deltaY * v2);
		if (v4 < basic) {
			return Double.NEGATIVE_INFINITY;
		}
		return Math.atan((v2 - Math.sqrt(v4 - basic)) / (gravity * deltaXZ));
	}

	public static double hangtime(double launchAngle, double vel, double deltaY, double gravity) {
		double a = vel * Math.sin(launchAngle);
		double b = -2.0D * gravity * deltaY;
		double a2 = a * a + b;
		if (a2 < 0.0D) {
			return 0.0D;
		}
		return (a + Math.sqrt(a2)) / gravity;
	}

	public double firingMinimumRange() {
		EntityType type = getLivingEntity().getType();
		if ((type == EntityType.WITHER) || (type == EntityType.WITHER)) {
			return 8.0D;
		}
		return 2.0D;
	}

	public AbstractMap.SimpleEntry<Location, Vector> getLaunchDetail(Location target, Vector lead) {
		faceLocation(target);
		double angt = Double.POSITIVE_INFINITY;
		Location start = getLivingEntity().getEyeLocation().clone()
				.add(getLivingEntity().getEyeLocation().getDirection().multiply(firingMinimumRange()));
		double sbase = 20.0D;
		double speedb = 0;
		for (double speeda = sbase; speeda <= sbase + 15.0D; speeda += 5.0D) {
			angt = getArrowAngle(start, target, speeda, 20.0D);
			speedb = speeda;
			if (!Double.isInfinite(angt)) {
				break;
			}
		}
		if (Double.isInfinite(angt)) {
			return null;
		}
		double hangT = hangtime(angt, speedb, target.getY() - start.getY(), 20.0D);
		Location to = target.clone().add(lead.clone().multiply(hangT));
		Vector relative = to.clone().subtract(start.toVector()).toVector();
		double deltaXZ = Math.sqrt(relative.getX() * relative.getX() + relative.getZ() * relative.getZ());
		if (deltaXZ == 0.0D) {
			deltaXZ = 0.1D;
		}
		for (double speeda = sbase; speeda <= sbase + 15.0D; speeda += 5.0D) {
			angt = getArrowAngle(start, to, speeda, 20.0D);
			if (!Double.isInfinite(angt)) {
				break;
			}
		}
		if (Double.isInfinite(angt)) {
			return null;
		}
		relative.setY(Math.tan(angt) * deltaXZ);
		relative = relative.normalize();
		Vector normrel = relative.clone();
		speedb += 1.188D * hangT * hangT;
		relative = relative.multiply(speedb / 20.0D);
		start.setDirection(normrel);
		return new AbstractMap.SimpleEntry(start, relative);
	}

	public void fireArrow(ItemStack type, Location target, Vector lead) {
		AbstractMap.SimpleEntry<Location, Vector> start = getLaunchDetail(target, lead);
		if ((start == null) || (start.getKey() == null)) {
			return;
		}
		Entity arrow;
		arrow = ((Location) start.getKey()).getWorld().spawnEntity((Location) start.getKey(), EntityType.ARROW);
		((Projectile) arrow).setShooter(getLivingEntity());

		arrow.setVelocity(fixForAcc((Vector) start.getValue()));
		if (((Inventory) this.npc.getTrait(Inventory.class)).getContents()[0]
				.containsEnchantment(Enchantment.ARROW_FIRE)) {
			arrow.setFireTicks(10000);
		}
		useItem();
	}

	public void useItem() {
		if ((this.npc.isSpawned()) && ((getLivingEntity() instanceof Player))) {
			BukkitRunnable runner = new BukkitRunnable() {
				public void run() {
					if ((Gladiator.this.npc.isSpawned()) && ((Gladiator.this.getLivingEntity() instanceof Player))) {
						PlayerAnimation.STOP_USE_ITEM.play((Player) Gladiator.this.getLivingEntity());
					}
				}
			};
			runner.runTaskLater(main, 10L);
		}
	}

	public void tryAttack(LivingEntity entity) {
		this.faceLocation(entity.getEyeLocation());
		if (!entity.getWorld().equals(getLivingEntity().getWorld())) {
			return;
		}
		if (!getLivingEntity().hasLineOfSight(entity)) {
			return;
		}
		double dist = getLivingEntity().getEyeLocation().distanceSquared(entity.getEyeLocation());
		if ((this.autoswitch) && (dist > 9.0D)) {
			swapToRanged();
		} else if ((this.autoswitch) && (dist < 9.0D)) {
			swapToMelee();
		}
		NPCAttack sat = new NPCAttack(this.npc);
		Bukkit.getPluginManager().callEvent(sat);
		if (sat.isCancelled()) {
			return;
		}
		addTarget(entity.getUniqueId());
		if (usesBow()) {
			if (canSee(entity)) {
				if (this.timeSinceAttack < this.attackRateRanged) {
					if (this.rangedChase) {
						rechase();
					}
					return;
				}
				this.timeSinceAttack = 0L;
				ItemStack item = getArrow();
				if (item != null) {
					fireArrow(item, entity.getEyeLocation(), entity.getVelocity());
					if (this.needsAmmo) {
						reduceDurability();
						takeArrow();
						grabNextItem();
					}
				}
			} else if (dist < 9.0D) {
				if (this.timeSinceAttack < this.attackRate) {
					if (this.closeChase) {
						rechase();
					}
					return;
				}
				this.timeSinceAttack = 0L;
				punch(entity);
				if ((this.needsAmmo) && (shouldTakeDura())) {
					reduceDurability();
					grabNextItem();
				}
			} else if (this.closeChase) {
				chase(entity);
			}
		} else if (dist < 9.0D) {
			if (this.timeSinceAttack < this.attackRate) {
				if (this.closeChase) {
					rechase();
				}
				return;
			}
			this.timeSinceAttack = 0L;

			punch(entity);
			if ((this.needsAmmo) && (shouldTakeDura())) {
				reduceDurability();
				grabNextItem();
			}
		}
	}

	public boolean shouldTakeDura() {
		Material type;
		type = getLivingEntity().getEquipment().getItemInHand().getType();

		return (type == Material.BOW) || (type == Material.DIAMOND_SWORD) || (type == Material.GOLD_SWORD)
				|| (type == Material.IRON_SWORD) || (type == Material.WOOD_SWORD);
	}

	public void takeOne() {
		ItemStack item = getLivingEntity().getEquipment().getItemInHand();
		if ((item != null) && (item.getType() != Material.AIR)) {
			if (item.getAmount() > 1) {
				item.setAmount(item.getAmount() - 1);
				getLivingEntity().getEquipment().setItemInHand(item);
			} else {
				getLivingEntity().getEquipment().setItemInHand(null);
			}
		}
	}

	public void grabNextItem() {
		if (!this.npc.hasTrait(Inventory.class)) {
			return;
		}
		Inventory inv = (Inventory) this.npc.getTrait(Inventory.class);
		ItemStack[] items = inv.getContents();
		ItemStack held = items[0];
		if ((held != null) && (held.getType() != Material.AIR)) {
			return;
		}
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item != null) {
				item = item.clone();
				Material mat = item.getType();
				Product product = new Product();
				if (product.isWeapon(mat)) {
					if (item.getAmount() > 1) {
						item.setAmount(item.getAmount() - 1);
						items[i] = item;
						items[0] = item.clone();
						items[0].setAmount(1);
						inv.setContents(items);
						item = item.clone();
						item.setAmount(1);
						return;
					}
					items[i] = new ItemStack(Material.AIR);
					items[0] = item.clone();
					inv.setContents(items);
					return;
				}
			}
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

	public void takeArrow() {
		if (!this.npc.hasTrait(Inventory.class)) {
			return;
		}
		Inventory inv = (Inventory) this.npc.getTrait(Inventory.class);
		ItemStack[] items = inv.getContents();
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item != null) {
				Material mat = item.getType();
				if ((mat == Material.ARROW)) {
					if (item.getAmount() > 1) {
						item.setAmount(item.getAmount() - 1);
						items[i] = item;
						inv.setContents(items);
						return;
					}
					items[i] = null;
					inv.setContents(items);
					return;
				}
			}
		}
	}

	private void updateTargets() {
		for (NPCTarget uuid : new HashSet<NPCTarget>(this.currentTargets)) {
			Entity e = getEntityForID(uuid.targetID);
			if (e == null) {
				this.currentTargets.remove(uuid);
			} else if (((e instanceof Player)) && (((Player) e).getGameMode() == GameMode.CREATIVE)) {
				this.currentTargets.remove(uuid);
			} else if (((e instanceof LivingEntity))
					&& (((LivingEntity) e).hasPotionEffect(PotionEffectType.INVISIBILITY))) {
				this.currentTargets.remove(uuid);
			} else if (e.isDead()) {
				this.currentTargets.remove(uuid);
			} else {
				double d = e.getWorld().equals(getLivingEntity().getWorld())
						? e.getLocation().distanceSquared(getLivingEntity().getLocation())
						: 1.0E8D;
				if ((d > this.range * this.range * 4.0D) && (d > this.chaseRange * this.chaseRange * 4.0D)) {
					this.currentTargets.remove(uuid);
					Bukkit.broadcastMessage("range");
				} else if (uuid.ticksLeft > 0L) {
					uuid.ticksLeft -= main.tickRate;
					if (uuid.ticksLeft <= 0L) {
						this.currentTargets.remove(uuid);
						Bukkit.broadcastMessage("timeout");
					}
				}
			}
		}
		if (this.chasing != null) {
			NPCTarget cte = new NPCTarget();
			cte.targetID = this.chasing.getUniqueId();
			if (!this.currentTargets.contains(cte)) {
				this.chasing = null;
				this.npc.getNavigator().cancelNavigation();
			}
		}
	}

	public void runUpdate() {
		this.timeSinceAttack += main.tickRate;
		this.timeSinceHeal += main.tickRate;
		if (getLivingEntity().getLocation().getY() <= 0.0D) {
			getLivingEntity().damage(1.0D);
		}
		if ((this.healRate > 0) && (this.timeSinceHeal > this.healRate)
				&& (getLivingEntity().getHealth() < this.health)) {
			getLivingEntity().setHealth(Math.min(getLivingEntity().getHealth() + 1.0D, this.health));
			this.timeSinceHeal = 0L;
		}
		double crsq = this.chaseRange * this.chaseRange;
		updateTargets();
		boolean goHome = this.chased;
		LivingEntity target = findBestTarget();
		if (target != null) {
			Location near = getLivingEntity().getLocation();
			if ((crsq <= 0.0D) || (near == null) || (near.distanceSquared(target.getLocation()) <= crsq)) {
				this.chasing = target;
				this.cleverTicks = 0;
				tryAttack(target);
				goHome = false;
			}
		} else if ((this.chasing != null) && (this.chasing.isValid())) {
			this.cleverTicks += 1;
			if (this.cleverTicks >= 10) {
				this.chasing = null;
			} else {
				Location near = null;
				if ((crsq <= 0.0D) || (near == null) || (near.distanceSquared(this.chasing.getLocation()) <= crsq)) {
					tryAttack(this.chasing);
					goHome = false;
				}
			}
		}
		if ((goHome) && (this.chaseRange > 0.0D) && (target == null)) {
			Location near = null;
			if ((near != null)
					&& ((this.chasing == null) || (near.distanceSquared(this.chasing.getLocation()) > crsq))) {
				this.npc.getNavigator().getDefaultParameters().stuckAction(TeleportStuckAction.INSTANCE);
				this.npc.getNavigator().setTarget(near);
				this.npc.getNavigator().getLocalParameters().speedModifier((float) this.speed);
				this.chased = false;
			} else {
				if (this.npc.getNavigator().getEntityTarget() != null) {
					this.npc.getNavigator().cancelNavigation();
				}
			}
		} else if ((this.chasing == null) && (this.npc.getNavigator().getEntityTarget() != null)) {
			this.npc.getNavigator().cancelNavigation();
		}
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
			if (!ent.isDead()) {
				double dist = ent.getEyeLocation().distanceSquared(ent.getLocation());
				NPCTarget sct = new NPCTarget();
				sct.targetID = ent.getUniqueId();
				if (((dist < rangesquared) && (shouldTarget(ent)) && (canSee(ent)))
						|| ((dist < crsq) && (this.currentTargets.contains(sct)))) {
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
		if (this.cTick >= main.tickRate) {
			this.cTick = 0;
			runUpdate();
		}
	}
	
	public void onAttach()
	{
		if (this.npc.isSpawned())
	    {
		      getLivingEntity().setMaxHealth(this.health);
		      getLivingEntity().setHealth(this.health);
		}
	}

	public void onSpawn() {
		Product product = new Product();
		Player entityNPC = (Player) npc.getEntity();
		entityNPC.setItemInHand(product.createItem(ColorOptions.messageformat + "TestSword",
				new ItemStack(Material.DIAMOND_SWORD, 1), true, ChatColor.RED + "Soulbound"));
		entityNPC.getInventory().setHelmet(product.createItem(ColorOptions.messageformat + "helmet",
				new ItemStack(Material.IRON_HELMET), false, ChatColor.RED + "Soulbound"));
		entityNPC.getInventory().setChestplate(product.createItem(ColorOptions.messageformat + "chestplate",
				new ItemStack(Material.IRON_CHESTPLATE), false, ChatColor.RED + "Soulbound"));
		entityNPC.getInventory().setLeggings(product.createItem(ColorOptions.messageformat + "leggings",
				new ItemStack(Material.IRON_LEGGINGS), false, ChatColor.RED + "Soulbound"));
		entityNPC.getInventory().setBoots(product.createItem(ColorOptions.messageformat + "boots",
				new ItemStack(Material.IRON_BOOTS), false, ChatColor.RED + "Soulbound"));
	}
	
}
