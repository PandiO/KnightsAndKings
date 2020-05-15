package Listeners;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.managers.RegionManager;

import DataManager.Worldguard;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.DoubleDamage;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Menu.Menu;
import Minigames.Participant;
import Minigames.SiegeTeam;
import Scoreboards.ActionBar;
import Sieges.Siege;
import Sieges.SiegeMember;
import Sieges.SiegeSpawnpoint;
import Skills.Skill;
import Users.User;
import Users.Users;
import net.citizensnpcs.api.CitizensAPI;

public class EntityListener implements Listener
{
	private Main main;
	public EntityListener(Main main)
	{
		this.main = main;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageByEntityEvent e)
	{
		if (e.getEntity() instanceof Player)
		{
			Player damaged = (Player) e.getEntity();
			User userDamaged = null;
			try
			{
				userDamaged = Users.getUser(damaged.getUniqueId());
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, damaged, false);
			} catch (UserIsNpcException ex)
			{
				
			} catch (Exception ex)
			{
				ex.printStackTrace();
				//ErrorHandlers.userNotFoundAction(damager, damaged, false);
			}
			
			Player damager = null;
			User userDamager = null;
			
			if (e.getDamager() instanceof Player)
			{
				damager = (Player) e.getDamager();
			} else
			if (e.getDamager() instanceof Arrow)
			{
				final Arrow arrow = (Arrow) e.getDamager();
	            if (arrow.getShooter() instanceof Player)
	            {
	            	damager = (Player) arrow.getShooter();
	            	
	    			/**
	    			 * Juggernaut skill
	    			 */
	    			String world = damaged.getWorld().getName();
	    			if (userDamaged.getSpecialSkillName() != null && userDamaged.getSpecialSkillName().equalsIgnoreCase("juggernaut"))
    				{
    					Bukkit.getWorld(world).playSound(damaged.getLocation(), SoundHandler.ANVIL_USE, 2.0F, 2.0F);
    					e.setCancelled(true);
						Main.logMessage("Juggernaut skill cancelling damage");

    				}
	            }
			}
			
			if (damager != null)
			{
				if (CitizensAPI.getNPCRegistry().isNPC(damager))
				{
					return;
				}
				try
				{
					userDamager = Users.getUser(damager.getUniqueId());
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(null, damager, true);
					return;
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(null, damager, true);
					return;
				}
			} else
			{
				return;
			}
			
			/**
			 * Hide and Seek
			 */
			if (!CitizensAPI.getNPCRegistry().isNPC(damaged) && Main.getHideAndSeekParticipating(userDamager) && Main.getHideAndSeekParticipating(userDamaged))
			{
				Participant pDamager = Main.HideAndSeek.getParticipant(userDamager);
				Participant pDamaged = Main.HideAndSeek.getParticipant(userDamaged);
				
				if (Main.HideAndSeek.getSeekers().GetMembers().contains(pDamager) 
						&& !Main.HideAndSeek.getSeekers().GetMembers().contains(pDamaged)
						&& !Main.HideAndSeek.getHideTime())
				{
					Main.logMessage("found the two participants..");
					Main.HideAndSeek.catchParticipant(userDamager, userDamaged);
				}
			}
			
			/**
			 * AttackSpeed skill
			 */
			Skill skill = new Skill();
			if (userDamager.getAttackSpeedID() > 0)
			{
				Integer amount = skill.getSkillValue("AttackSpeed", userDamager.getAttackSpeedID());
				if (Main.getRandom(0, 100) <= amount)
				{
					double multiplier = 1;
					String message = null;
					if (userDamager.getAttackSpeedID() == 7)
					{
						multiplier = 2;
						message = ChatColor.GREEN + "" + ChatColor.BOLD + "Triple hit!";
					} else
					{
						multiplier = 1.5;
						message = ChatColor.GREEN + "" + ChatColor.BOLD + "Double hit!";
					}
					e.setDamage(e.getDamage()*multiplier);
					Bukkit.getServer().getPluginManager().callEvent(new DoubleDamage(userDamager, damaged, e.getDamage(), multiplier == 2));
					damager.sendMessage(message);
					damager.playSound(damager.getLocation(), SoundHandler.ANVIL_LAND, 0.1F, 2.0F);
					
				}
			}
			
			/**
			 * Defense skill
			 */
			if (!CitizensAPI.getNPCRegistry().isNPC(damaged))
			{
				Integer level = userDamaged.getDefenseID();
				Integer reduction = skill.getSkillValue("Defense", level);
				Double damage = e.getFinalDamage()-((e.getFinalDamage()/100)*Double.valueOf(reduction));
				Random rand = new Random();
				if (level > 0)
				{
					if (level == 7)
					{
						if (rand.nextInt(100) <= 25)
						{
							Main.logMessage("Defense skill cancelling damage");
							e.setCancelled(true);
							damaged.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Damage reduced by 100%!");
							Bukkit.getServer().getWorld(damaged.getWorld().getName()).playSound(damaged.getLocation(), SoundHandler.ANVIL_LAND, 0.1F, 0.3F);
						} else
						{
							e.setDamage(damage);
						}
					} else
					{
						e.setDamage(damage);
					}
				}
			}
			
			/**
			 * Health skill
			 */
			if (!CitizensAPI.getNPCRegistry().isNPC(damaged))
			{
				Integer healthLevel = userDamaged.getHealthID();
				if (healthLevel == 7)
				{
					if (Main.getRandom(0, 100) <= skill.getSkillValue("health", healthLevel))
					{
						damaged.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10*20, 1));
						damaged.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "You have Regeneration I for 10 seconds!");
					}
				}
			}
			
			/**
			 * Ninja skill
			 */
			if (!CitizensAPI.getNPCRegistry().isNPC(damaged))
			{
				Location ploc = damaged.getLocation();
				Double radius = 30D;
				String specialSkillName = userDamaged.getSpecialSkillName();
				if (specialSkillName != null && specialSkillName.equalsIgnoreCase("ninja"))
				{
					if (!Main.ninjaSkill.containsKey(userDamaged))
					{
						Integer random = Main.getRandom(0, 100);
						if (random <= 30)
						{
							Main.logMessage(random.toString());
							Main.ninjaSkill.put(userDamaged, System.currentTimeMillis() + 5*1000);
			            	damaged.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Ninja + "Ninja" + ChatColor.GRAY + "]" + ColorOptions.Ninja + "You unleashed the ninja skill!");
							for (Player online : Bukkit.getOnlinePlayers())
							{
								Location plocs = online.getLocation();
								if (plocs.distance(ploc) <= radius)
								{
									online.hidePlayer(damaged);
									online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*1, 2));
								}
							}
							damager.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have been ninja'd!");
							Bukkit.getWorld(damaged.getWorld().getName()).playSound(damaged.getLocation(), SoundHandler.CREEPER_HISS, 2.0F, 2.0F);
						}
					}
				}
			}
			
			/**
			 * Speed skill
			 */
			if (!CitizensAPI.getNPCRegistry().isNPC(damaged))
			{
				Integer speedLevel = userDamaged.getSpeedID();
				if (speedLevel == 7)
				{
					if (Main.getRandom(0, 100) <= skill.getSkillValue("speed", speedLevel))
					{
						damaged.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 1));
						damaged.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You got Speed II for 5 seconds!");
					}
				}
			}
			
			/**
			 * Strength skill
			 */
			Integer strengthLevel = userDamager.getStrengthID();
			Integer chance = skill.getSkillValue("Strength", strengthLevel);
			if (strengthLevel > 0)
			{
				if (Main.getRandom(0, 100) <= chance)
				{
					Integer strengthPotion = 1;
					Integer seconds = 5;
					
					if (strengthLevel == 7)
					{
						strengthPotion = 2;
						seconds = 4;
					}
					
					damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, seconds*20, strengthPotion));
				}
			}
			
			Main.logMessage("Checking if damaged is not an npc. If not, handling safezone and siege stuff");
			if (!CitizensAPI.getNPCRegistry().isNPC(damaged))
			{
				Main.logMessage("Damaged is no NPC");
				Siege damagedSiege = Sieges.Sieges.findSiege(userDamaged);
				Siege damagerSiege = Sieges.Sieges.findSiege(userDamager);
				if (userDamaged.inSafeZone() || userDamager.inSafeZone() || userDamaged.getFriendList().contains(damager.getUniqueId()))
				{
					Main.logMessage("Damaged is in safezone or damager is friend of damaged");
					if ((damagedSiege == null && damagerSiege == null) 
							&& (!Main.getHideAndSeekParticipating(userDamager) && !Main.getHideAndSeekParticipating(userDamaged)))
					{
						Main.logMessage("Safezone cancelling damage");

						e.setCancelled(true);
						damager.playSound(damager.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
						return;
					}
				}
				
				if (damagedSiege != null && damagerSiege != null)
				{
					SiegeMember damagedMember = damagedSiege.getSiegeMember(userDamaged);
//					if ((damaged.getHealth() - e.getDamage()) < 1)
//					{
//						Menu menu = new Menu();
//						e.setCancelled(true);
//						Participant participant = damagedSiege.getParticipant(userDamaged);
//						SiegeTeam team = damagedSiege.getTeam(participant);
//						SiegeSpawnpoint spawnpoint = team.getSpawnpoints().get(0);
//						
//						Integer exp = userDamager.getMultipliedInt(userDamager.getExpPart(5));
//						if (userDamager != null)
//						{
//							userDamager.addKills(false, 1, 1);
//							userDamager.addExperience(exp, true);
//						}
//						if (userDamaged != null)
//						{
//							userDamaged.addDeaths(1);
//						}
//						damager.sendMessage(ColorOptions.messageachievement + "You received " + ColorOptions.messagesubjects + exp + ColorOptions.messageachievement + " experience for killing " + ColorOptions.messagesubjects + userDamaged.getUsername());
//						damaged.sendMessage(ColorOptions.message + "You were killed by " + userDamager.getUsername());
//						
//						//menu.openSiegeSpawnpointMenu(userDamaged, damagedSiege);
//						damaged.teleport(spawnpoint.getLocation());
//						
//						Bukkit.getServer().getPluginManager().callEvent(new PlayerRespawnEvent(damaged, spawnpoint.getLocation(), false));
//						
//						return;
//					}
					
					if (damagedMember.isSafe())
					{
						damager.sendMessage(ColorOptions.error + "You can't hurt players inside their spawn area!");
						e.setCancelled(true);
					}
				}
			}
			
			ActionBar bar = new ActionBar(ColorOptions.error + "You are now in combat! Do not log off");
			if (userDamager != null)
			{
				if (damager.getGameMode() == GameMode.SURVIVAL && !userDamager.inStaffModus() && !userDamager.inOwnerModus())
				{
					if (!Main.incombat.containsKey(userDamager))
					{
						bar.sendToPlayer(damager);
					}
					Main.incombat.put(userDamager, System.currentTimeMillis() + (Main.combat*1000));
				}
			}
			if (userDamaged != null)
			{
				if (damaged.getGameMode() == GameMode.SURVIVAL && !userDamaged.inStaffModus() && !userDamaged.inOwnerModus())
				{
					if (!Main.incombat.containsKey(userDamaged))
					{
						bar.sendToPlayer(damaged);
					}
					Main.incombat.put(userDamaged, System.currentTimeMillis() + (Main.combat*1000));
				}
			}
			
			Main.logMessage("Handled damage event");
		} else
		if (e.getEntity() instanceof ItemFrame || e.getEntity() instanceof ArmorStand)
		{
			if (e.getDamager() instanceof Player)
			{
				Player player = (Player) e.getDamager();
				UUID uuid = player.getUniqueId();
				Entity itemframe = e.getEntity();
				Location frameloc = itemframe.getLocation();
				RegionManager regionmanager = Worldguard.getWorldGuard().getRegionManager(frameloc.getWorld());
				Integer propertyID = Worldguard.getStructureIDbyRegion("property", frameloc, regionmanager);
				
				if (propertyID != null)
				{
					if (!main.ownermodus.containsKey(uuid) || main.ownermodus.get(uuid) != true)
					{
						e.setCancelled(true);
					}
				} else
				if (e.getEntity() instanceof ArmorStand)
				{
					if (!main.ownermodus.containsKey(uuid) || main.ownermodus.get(uuid) != true)
					{
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			User user = null;
			
			try
			{
				user = Users.getUser(player.getUniqueId());
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
			
			/**
			 * Afk
			 */
			if (user.isAfk())
			{
				if (user.getAfk().teleporting == false)
				{
					user.removeAfk();
				}
			}
			user.SetAfkCommence((System.currentTimeMillis()+main.afkTime*1000));
			
			/**
			 * Avenger skill
			 */
			if (Main.avenger.containsValue(user))
			{
				User avenger = null;
				for (User a : Main.avenger.keySet())
				{
					if (Main.avenger.get(a) == user)
					{
						avenger = a;
						break;
					}
				}
				
				if (avenger != null)
				{
					Double normaldamage = event.getDamage();
            		Double plusdamage = (normaldamage *Double.valueOf(1.5F));
            		player.damage(plusdamage);
				}
			}
		}
	}
}
