package Traits;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Minigames.BanditAmbush;
import Minigames.BanditAmbushes;
import Products.Product;
import Users.User;
import Users.Users;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.util.PlayerAnimation;

public class TestTrait extends Trait
{
	Main main = Main.getPlugin(Main.class);
	
	public TestTrait()
	{
		super("TestTrait");
	}
	/**
	 * All rates and their counts (all in seconds except for the updateRate)
	*/
	private int attackRate = 5;
	private int attackRateReal = 5;
	private int attackRateCount = 0;
	private int talkRate = 5;
	private int talkRateCount = 0;
	private int forceMoveRate = 2;
	private int forceMoveRateCount = 0;
	private int healRate = 5;
	private int healRateCount = 0;
	private int noTargetRate = 10;
	private int noTargetRateCount = 0;
	
	private int updateRate = 20;
	private int updateRateCount = 0;
	
	/**
	 * All ranges
	 */
	private int chaseRangeMax = 30;
	private int chaseRangeMin = 3;
	private int noMoveRange = 2;
	private int hitRange = 4;
	private int skipPreferableTargetRange = 10;
	
	/**
	 * All combat related variables
	 */
	private double health = 20;
	private double damage = 2.0;
	private float speed = 1.29F;
	private int attackRateOffsetMax = 2;
	private int attackRateOffsetMin = 0;
	private int attackRateDecreaseChance = 50;
	
	//Marks if the npc is chasing someone or something
	private boolean chasing = false;
	//Marks the location where the npc was when starting a chase (to check if the npc is really chasing)
	private Location startChaseLocation;
	
	
	/**
	 * All bandit behavioural variables
	 * 		param: preferableTargets
	 * 		description: A list of preferable targets, the npc will choose to attack these above other entities (bandits will initially target 1 player)
	 * 
	 * 		param: attackedFirst
	 * 		description: A list of entities which hit the npc. 
	 * 					The npc (bandit) will not attack strangers if variable attackStrangers is set to false, but it will attack entities that hit the npc first
	 * 
	 * 		param: attackStrangers
	 * 		description: The npc will attack strangers if set to true, if not, it will only attack it's initial target and all other entities that hit the npc first
	 * 
	 * 		param: currentTarget
	 * 		description: Entity the npc is currently targetting (following or hitting)
	 */
	//A list of preferable targets, the npc will choose to attack these targets above others
	private List<User> preferableTargets = new CopyOnWriteArrayList<User>();
	//A list of entities that were no targets but began to attack the npc
	private List<Entity> attackedFirst = new CopyOnWriteArrayList<Entity>();
	private boolean attackStrangers = false;
	private LivingEntity currentTarget;
	private BanditAmbush ambush;
	private int level = 1;
	private boolean equipped = false;
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack weapon;
	private boolean noTargetsLeft = false;
	
	@Override
	public void onAttach()
	{
	}
	
	@Override
	public void onSpawn()
	{
		BanditAmbush ambush = BanditAmbushes.getAmbush(this.npc);
		if (ambush != null)
		{
			this.ambush = ambush;
			this.initializeBandit(ambush.getUser());
		}
		npc.setProtected(false);
		this.getLivingEntity().setHealth(this.health);
	}
	
	//Ran every tick (20 ticks = 1 second)
	@Override
	public void run()
	{
		if (!npc.isSpawned())
		{
			return;
		}
//		for (Entity entity : this.getLivingEntity().getNearbyEntities(this.skipPreferableTargetRange, this.skipPreferableTargetRange, this.skipPreferableTargetRange))
//		{
//			this.npc.faceLocation(entity.getLocation());
//		}
		if (this.updateRateCount >= this.updateRate)
		{
			this.runUpdate();
			return;
		}
		this.updateRateCount += 1;
	}
	
	public void runUpdate()
	{
		this.updateRateCount = 0;
		
		this.talkRateCount += 1;
		this.attackRateCount += 1;
		this.healRateCount += 1;
		
		if (!this.equipped || ((Player)this.getLivingEntity()).getItemInHand() == null || ((Player)this.getLivingEntity()).getItemInHand() != this.weapon)
		{
			this.equipBandit();
		}
		
		if ((this.healRate > 0) && (this.healRateCount > this.healRate) && (getLivingEntity().getHealth() < this.health)) 
		{
			getLivingEntity().setHealth(Math.min(getLivingEntity().getHealth() + 1.0D, this.health));
			this.healRateCount = 0;
		}

		this.updateTargets();
		
		/**
		 * Worldguard start
		 * The following piece of code was originally located at the end of the updateTargets() method.
		 * It was moved here because otherwise the tryAttack() method would be called before this was run. 
		 * Same goes for the tryChase() call just below this gateRegion, it was located at the end of the findTarget() method.
		 */
		if (this.currentTarget != null)
		{
			//Note: If the distance between the npc and the currenttarget is larger than chaseRangeMax, the target is already removed from the target list 
			//(thus it will not be selected as new target)
			if (this.getDistance(this.currentTarget.getLocation()) > this.chaseRangeMax || 
					((LivingEntity) this.currentTarget).hasPotionEffect(PotionEffectType.INVISIBILITY))
			{
				this.findTarget();
			}
		} else
		{
			this.findTarget();
		}
		/**
		 * End of gateRegion
		 */

		if (this.currentTarget != null)
		{
			this.tryChase(this.currentTarget);
		}
		this.tryTalk();
		if (this.currentTarget != null)
		{
			this.tryAttack(this.currentTarget);
		}
	}
	
	public void tryTalk()
	{
		if (this.talkRateCount >= this.talkRate)
		{
			for (Entity ent : this.getLivingEntity().getNearbyEntities(5, 3, 5))
			{
				if (ent instanceof Player)
				{
					ent.sendMessage(ColorOptions.error + "I am mad!");
				}
			}
			this.talkRateCount = 0;
		}
	}
	
	public void tryAttack(LivingEntity entity) 
	{
		if (entity == null)
		{
			if (this.currentTarget == null)
			{
				this.findTarget();
				if (this.currentTarget == null)
				{
					return;
				}
			} else
			{
				entity = (LivingEntity) this.currentTarget;
			}
		}
		this.npc.faceLocation(entity.getLocation());
		
		if (!entity.getWorld().equals(getLivingEntity().getWorld())) 
		{
			return;
		}
		
		if (!getLivingEntity().hasLineOfSight(entity)) 
		{
			return;
		}
		double dist = this.getDistance(entity.getLocation());
		if (dist < 4.0D) 
		{
			if (this.attackRateCount < this.attackRateReal) 
			{
				tryChase(entity);
				return;
			}
			this.attackRateCount = 0;
			this.attackRateReal = this.getRealAttackRate();
			punch(entity);
		} else
		{
			tryChase(entity);
		}
	}
	
	public void tryChase(Entity entity)
	{
		double dist = this.getDistance(entity.getLocation());
		this.npc.getNavigator().getDefaultParameters().stuckAction(null);

		if (!this.npc.getNavigator().isNavigating())
		{
			this.chasing = false;
		} else
		{
			Location loc = this.npc.getNavigator().getTargetAsLocation();
			if (entity.getLocation().distance(loc) > this.noMoveRange)
			{
				this.chasing = false;
			}
		}
		
		//Checks if NPC navigator isn't bugged and npc is moving. If not (npc is not moving for some time), than force the npc to restart the chase
		if (this.startChaseLocation != null)
		{
			if (this.getDistance(this.startChaseLocation) < this.noMoveRange)
			{
				this.forceMoveRateCount += 1;
				if (this.forceMoveRateCount >= this.forceMoveRate)
				{
					this.chasing = false;
					this.forceMoveRateCount = 0;
				}
			}
		}
		
		//Check if NPC reached its target or is not navigating at all
		if (dist < this.chaseRangeMin)
		{
			this.chasing = false;
			this.npc.getNavigator().cancelNavigation();
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
		this.npc.getNavigator().cancelNavigation();
		this.npc.getNavigator().getDefaultParameters().stuckAction(null);
		
		this.npc.getNavigator().setTarget(entity.getLocation());
		this.chasing = true;
		this.startChaseLocation = this.npc.getEntity().getLocation();
		this.forceMoveRateCount = 0;
		this.npc.getNavigator().getLocalParameters().speedModifier(this.speed);
	}
	
	public void swingWeapon() {
		if ((this.npc.isSpawned()) && ((getLivingEntity() instanceof Player))) {
			PlayerAnimation.ARM_SWING.play((Player) getLivingEntity());
		}
	}
	
	public void punch(LivingEntity entity)
	{
//		Bukkit.getConsoleSender().sendMessage("Debug 2.0: Punching entity: " + entity.getName() + ", damage: " + this.damage);
		this.swingWeapon();
		entity.damage(this.damage);
//		Bukkit.getConsoleSender().sendMessage("Debug 2.1: Punch complete: " + entity.getName() + ", damage: " + this.damage);
	}
	
	public double getDistance(Location location)
	{
		double dist = getLivingEntity().getLocation().distance(location);
		
		return dist;
	}
	
	public LivingEntity getLivingEntity() {
		return (LivingEntity) this.npc.getEntity();
	}
	
	public void addPreferableTarget(User user)
	{
		if (!this.preferableTargets.contains(user))
		{
			this.preferableTargets.add(user);
		}
	}
	
	public void addAttackedFirstEntity(Entity entity)
	{
		if (this.attackedFirst.contains(entity))
		{
			return;
		}
		
		if (entity instanceof Player)
		{
			Player player = (Player) entity;
			UUID uuid = player.getUniqueId();
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			} catch (UserIsNpcException ex)
			{
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			}
			
			if (this.preferableTargets.contains(user))
			{
				return;
			}
		}
		
		this.attackedFirst.add(entity);
	}
	
	public void findTarget()
	{
		Entity target = null;
		
		if (this.preferableTargets.isEmpty() && this.attackedFirst.isEmpty())
		{
			this.noTargetRateCount += 1;
			if (this.noTargetRateCount >= this.noTargetRate)
			{
				this.noTargetsLeft = true;
				this.getLivingEntity().damage(this.health, null);
				//((Player) this.npc.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10*20, 2));
				this.noTargetRateCount = 0;
			}
			return;
		}
		
		User preferableTarget = null;
		if (!this.preferableTargets.isEmpty())
		{
			if (this.preferableTargets.size() > 1)
			{
				double smallestDistance = 0.0;
				for (User possibleTarget : this.preferableTargets)
				{
					Player player = possibleTarget.getPlayer();
					if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
					{
						continue;
					}
					double distance = this.getDistance(player.getLocation());
					if (smallestDistance == 0.0)
					{
						preferableTarget = possibleTarget;
						smallestDistance = distance;
					} else if (distance < smallestDistance)
					{
						preferableTarget = possibleTarget;
						smallestDistance = distance;
					}
				}
			} else
			{
				preferableTarget = this.preferableTargets.get(0);
			}
		}

		/**
		 * If an algorithm to find targets is needed for an npc with non-specific targets, copy the part below and remove the if statement in the nearbyEntities list.
		 */
		List<Entity> nearbyEntities = this.attackStrangers ? this.getLivingEntity().getNearbyEntities(this.skipPreferableTargetRange, this.skipPreferableTargetRange, this.skipPreferableTargetRange) : new ArrayList<Entity>();
		
		if (!this.attackedFirst.isEmpty())
		{
			if (this.attackedFirst.size() > 1)
			{
				for (Entity possibleTarget : this.attackedFirst)
				{
					double distance = this.getDistance(possibleTarget.getLocation());
					if (distance <= this.skipPreferableTargetRange)
					{
						nearbyEntities.add(possibleTarget);
					}
				}
			} else if (this.getDistance(this.attackedFirst.get(0).getLocation()) <= this.skipPreferableTargetRange)
			{
				nearbyEntities.add(this.attackedFirst.get(0));
			}
		}
		if (!nearbyEntities.isEmpty())
		{
			if (nearbyEntities.size() > 1)
			{
				double smallestDistance = 0.0;
				for (Entity possibleTarget : nearbyEntities)
				{
					if (((LivingEntity)possibleTarget).hasPotionEffect(PotionEffectType.INVISIBILITY))
					{
						continue;
					}
					double distance = this.getDistance(possibleTarget.getLocation());
					if (smallestDistance == 0.0)
					{
						target = possibleTarget;
						smallestDistance = distance;
					} else if (distance < smallestDistance)
					{
						target = possibleTarget;
						smallestDistance = distance;
					}
				}
			} else
			{
				target = nearbyEntities.get(0);
			}
		}
		/**
		 * End of the copy part
		 */
		if (preferableTarget != null)
		{
			if (target != null)
			{
				if (this.getDistance(preferableTarget.getPlayer().getLocation()) <= this.getDistance(target.getLocation()))
				{
					target = preferableTarget.getPlayer();
				}
			} else
			{
				target = preferableTarget.getPlayer();
			}
		}
		if (target != null)
		{
			this.currentTarget = (LivingEntity)target;
		}
	}
	
	public void updateTargets()
	{
		if (this.preferableTargets.isEmpty() && this.attackedFirst.isEmpty())
		{
			return;
		}
		
		if (!this.preferableTargets.isEmpty())
		{
			for(User preferableTarget : this.preferableTargets)
			{
				boolean remove = false;
				if (preferableTarget == null)
				{
					remove = true;
				}
				Player target = preferableTarget.getPlayer();
				if (target.isDead())
				{
					remove = true;
				} else			
				if (this.getDistance(target.getLocation()) > this.chaseRangeMax)
				{
					remove = true;
				} else if (preferableTarget.inOwnerModus() || preferableTarget.inStaffModus() || preferableTarget.inSafeZone())
				{
					remove = true;
				} else if (target.getGameMode() == GameMode.CREATIVE)
				{
					remove = true;
				}
				
				if (remove)
				{
					this.preferableTargets.remove(preferableTarget);
					if (this.currentTarget == ((LivingEntity)target))
					{
						this.currentTarget = null;
					}
					BanditAmbush ambush = BanditAmbushes.getAmbush(preferableTarget);
					
					if (ambush != null)
					{
						ambush.fled();
					}
				}
			}
		}
		
		if (!this.attackedFirst.isEmpty())
		{
			for (Entity attackerTarget : this.attackedFirst)
			{
				boolean remove = false;
				if (attackerTarget == null)
				{
					remove = true;
				}
				LivingEntity target = (LivingEntity)attackerTarget;
				if (target.isDead())
				{
					remove = true;
				} else if (this.getDistance(target.getLocation()) > this.chaseRangeMax)
				{
					remove = true;
				} else if (attackerTarget instanceof Player)
				{
					User user = null;
					
					try
					{
						user = Users.getUser(((Player)attackerTarget).getUniqueId());
					} catch (UserNotFoundException ex)
					{
						ErrorHandlers.userNotFoundAction(null, (Player)attackerTarget, true);
						return;
					} catch (UserIsNpcException ex)
					{
						return;
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, (Player)attackerTarget, true);
						return;
					}
					if (user.inOwnerModus() || 
							user.inStaffModus() || 
							((Player)attackerTarget).getGameMode() == GameMode.CREATIVE || 
								user.inSafeZone())
					{
						remove = true;
					}
				}
				
				if (remove)
				{
					this.attackedFirst.remove(attackerTarget);
					if (this.currentTarget == target)
					{
						this.currentTarget = null;
					}
				}
			}
		}
	}
	
	public int getRealAttackRate()
	{
		int attackRate = this.attackRate;
		
		int offset = main.getRandom(this.attackRateOffsetMin, this.attackRateOffsetMax);
		
		if (main.getRandom(0, 100) <= this.attackRateDecreaseChance)
		{
			attackRate = ((attackRate - offset) < 0 ? attackRate : (attackRate - offset));
		} else
		{
			attackRate += offset;
		}
		return attackRate;
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e)
	{
		LivingEntity entity = e.getEntity();
		Entity killer = e.getEntity().getKiller();
		
		if (!CitizensAPI.getNPCRegistry().isNPC(entity))
		{
			return;
		}
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
		if (npc.getId() != this.npc.getId())
		{
			return;
		}

		this.attackedFirst.clear();
		this.preferableTargets.clear();
		e.getDrops().clear();
		
		BanditAmbush ambush = BanditAmbushes.getAmbush(this.npc);
		if (ambush != null)
		{
			if (!(killer instanceof Player))
			{
				ambush.killBandit(npc, null);
				return;
			}
			Player player = (Player) killer;
			UUID uuid = player.getUniqueId();
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			}
			e.getDrops().addAll(BanditAmbushes.calculateDrops());
			if (this.noTargetsLeft)
			{
				user = null;
			}
			ambush.killBandit(this.npc, user);
		}
		
//		new BukkitRunnable()
//		{
//			public void run()
//			{
//				npc.spawn(npc.getStoredLocation());
//			}
//		}.runTaskLaterAsynchronously(main, 10*20);
	}
	
	@EventHandler
	public void onHit(NPCDamageByEntityEvent e)
	{
		if (!this.npc.isSpawned())
		{
			return;
		}
		NPC npc = e.getNPC();
		
		if (this.npc.getId() != npc.getId())
		{
			return;
		}
		
		this.addAttackedFirstEntity(e.getDamager());
		
	}
	
	public void initializeBandit(User target)
	{
		this.addPreferableTarget(target);
		Integer titleID = target.getTitleID();

		Product product = new Product();
		
		Integer enchLevel = 1;
		Integer enchChance = 60;
		Integer armorMax = 3;
		Integer armorMin = 1;
		
		if (titleID <= 10 && titleID > 5)
		{
			this.level = 2;
		} else if (titleID > 10)
		{
			this.level = 3;
		}
		
		
		if (this.level == 2)
		{
			this.speed += 0.11;
			this.attackRateDecreaseChance += 15;
			armorMin = 2;
			armorMax = 3;
			enchLevel = 2;
			enchChance = 80;
		} else if (this.level == 3)
		{
			this.speed += 0.21;
			this.attackRateDecreaseChance += 25;
			this.attackRate -= 2;
			armorMin = 4;
			enchLevel = 4;
			enchChance = 95;
		}
		
		Integer randomHelmet = main.getRandom(1, 5);
		Integer randomChestplate = main.getRandom(1, 5);
		Integer randomLeggings = main.getRandom(1, 5);
		Integer randomBoots = main.getRandom(1, 5);
		
		Material helmet = Material.LEATHER_HELMET;
		Material chestplate = Material.LEATHER_CHESTPLATE;
		Material leggings = Material.LEATHER_LEGGINGS;
		Material boots = Material.LEATHER_BOOTS;
		
		switch(randomHelmet)
		{
		case 1:
			break;
		case 2:
			helmet = Material.GOLD_HELMET;
			break;
		case 3:
			helmet = Material.IRON_HELMET;
			break;
		case 4:
			helmet = Material.DIAMOND_HELMET;
			break;
		case 5:
			helmet = Material.CHAINMAIL_HELMET;
			break;
		}
		
		switch(randomChestplate)
		{
		case 1:
			break;
		case 2:
			chestplate = Material.GOLD_CHESTPLATE;
			break;
		case 3:
			chestplate = Material.IRON_CHESTPLATE;
			break;
		case 4:
			chestplate = Material.DIAMOND_CHESTPLATE;
			break;
		case 5:
			chestplate = Material.CHAINMAIL_CHESTPLATE;
			break;
		}
		
		switch(randomLeggings)
		{
		case 1:
			break;
		case 2:
			leggings = Material.GOLD_LEGGINGS;
			break;
		case 3:
			leggings = Material.IRON_LEGGINGS;
			break;
		case 4:
			helmet = Material.DIAMOND_LEGGINGS;
			break;
		case 5:
			leggings = Material.CHAINMAIL_LEGGINGS;
			break;
		}
		
		switch(randomBoots)
		{
		case 1:
			break;
		case 2:
			boots = Material.GOLD_BOOTS;
			break;
		case 3:
			boots = Material.IRON_BOOTS;
			break;
		case 4:
			boots = Material.DIAMOND_BOOTS;
			break;
		case 5:
			boots = Material.CHAINMAIL_BOOTS;
			break;
		}
		ItemStack helmetItem = new ItemStack(helmet, 1);
		ItemStack chestplateItem = new ItemStack(chestplate, 1);
		ItemStack leggingsItem = new ItemStack(leggings, 1);
		ItemStack bootsItem = new ItemStack(boots, 1);
		if (helmet == Material.CHAINMAIL_HELMET)
		{
			if (main.getRandom(1, 100) <= enchChance)
			{
				helmetItem.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchLevel);
				helmetItem.addEnchantment(Enchantment.DURABILITY, 3);
			}
		}
		if (chestplate == Material.CHAINMAIL_CHESTPLATE)
		{
			if (main.getRandom(1, 100) <= enchChance)
			{
				chestplateItem.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchLevel);
				chestplateItem.addEnchantment(Enchantment.DURABILITY, 3);
			}
		}
		if (leggings == Material.CHAINMAIL_LEGGINGS)
		{
			if (main.getRandom(1, 100) <= enchChance)
			{
				leggingsItem.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchLevel);
				leggingsItem.addEnchantment(Enchantment.DURABILITY, 3);
			}
		}
		if (boots == Material.CHAINMAIL_BOOTS)
		{
			if (main.getRandom(1, 100) <= enchChance)
			{
				bootsItem.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchLevel);
				bootsItem.addEnchantment(Enchantment.DURABILITY, 3);
			}
		}
		this.helmet = product.createItem(ChatColor.RED + "Helmet", helmetItem, true, ChatColor.RED + "Soulbound");
		this.chestplate = product.createItem(ChatColor.RED + "Chestplate", chestplateItem, true, ChatColor.RED + "Soulbound");
		this.leggings = product.createItem(ChatColor.RED + "Leggings", leggingsItem, true, ChatColor.RED + "Soulbound");
		this.boots = product.createItem(ChatColor.RED + "Boots", bootsItem, true, ChatColor.RED + "Soulbound");
		this.weapon = product.createAmountItem(Material.IRON_SWORD, 1, ChatColor.RED + "Sword", ChatColor.RED + "Soulbound");
		
		this.equipBandit();
	}
	
	public void equipBandit()
	{
		Player bandit = (Player) this.npc.getEntity();
		
		bandit.getInventory().setHelmet(this.helmet);
		bandit.getInventory().setChestplate(this.chestplate);
		bandit.getInventory().setLeggings(this.leggings);
		bandit.getInventory().setBoots(this.boots);
		bandit.getInventory().setItem(0, this.weapon);
		bandit.updateInventory();
		bandit.getInventory().setHeldItemSlot(0);

		this.equipped = true;
	}
}
