package KillsDeaths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import API_methods.WorldGuard;
import Assignments.Assignment;
import Assignments.AssignmentKill;
import DataManager.Worldguard;
import Donator.Donator;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.ExperienceChangeEvent;
import Main.Main;
import Products.Product;
import Users.User;
import Users.Users;

public class KillDeathStat implements Listener
{
	WorldGuard worldguard = new WorldGuard();
	Donator donator = new Donator();
	Product product = new Product();
	private Main main;
	public KillDeathStat(Main main) 
	{
		this.main = main;
	}
	public Integer part = 10;
	public HashMap<UUID, ItemStack> menu = new HashMap<UUID, ItemStack>();
	public static HashMap<UUID, List<ItemStack>> respawn = new HashMap<UUID, List<ItemStack>>();
	
//	@EventHandler
//	public void Kill(PlayerDeathEvent e)
//	{
//		String message = null;
//		Location deathLoc = e.getEntity().getLocation();
//		if (e.getEntity().getKiller() instanceof Player && e.getEntity() instanceof Player)
//		{
//			Player killer = (Player) e.getEntity().getKiller();
//			Player died = (Player) e.getEntity();
//			UUID ku = killer.getUniqueId();
//			UUID du = died.getUniqueId();
//			User userKiller = null;
//			User userDied = null;
//			
//			try
//			{
//				userKiller = Users.getUser(ku);
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(null, killer, true);
//				e.setDroppedExp(0);
//				e.setDeathMessage(message);
//				return;
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//				ErrorHandlers.userNotFoundAction(null, killer, true);
//				e.setDroppedExp(0);
//				e.setDeathMessage(message);
//				return;
//			}
//			try
//			{
//				userDied = Users.getUser(du);
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(killer, died, false);
//				e.setDroppedExp(0);
//				e.setDeathMessage(message);
//				return;
//			} catch (UserIsNpcException ex)
//			{
//				e.setDroppedExp(0);
//				e.setDeathMessage(message);
//				return;
//			} catch (Exception ex)
//			{
//				e.setDroppedExp(0);
//				e.setDeathMessage(message);
//				return;
//			}
//			Integer donatorID = userDied.getDonatorID();
//			Integer coins = userDied.getCoins();
//			Integer exp = userKiller.getMultipliedInt(userKiller.getExpPart(5));
//			if (Worldguard.getStructureIDbyRegion("arena", deathLoc, Worldguard.getRegionManager(died.getWorld())) == null)
//			{
//				Integer dropamount = (coins/10);
//				Integer blockamount = dropamount/10000;
//				Integer rest = (int) dropamount%10000;
//				userDied.setLastDeathLocation(deathLoc);
//				
//				userDied.removeCoins(dropamount);
//				
//				e.getDrops().add(product.createAmountItem(Material.GOLD_INGOT, blockamount, ColorOptions.message + died.getName() + "'s coins", "Coins: " + 10000));
//				e.getDrops().add(product.createAmountItem(Material.GOLD_INGOT, 1, ColorOptions.message + died.getName() + "'s coins", "Coins: " + rest));
//				died.sendMessage(ColorOptions.message + "You dropped " + part + "% of your coins when killed by " + killer.getName());
//			}
//			if (!Main.combatlogged.contains(du))
//			{
//				List<ItemStack> removable = new ArrayList<ItemStack>();
//				List<ItemStack> keep = new ArrayList<ItemStack>();
//				for (ItemStack content : e.getDrops())
//				{
//					if (content.hasItemMeta() && content.getItemMeta().hasDisplayName())
//					{
//						String display = content.getItemMeta().getDisplayName();
//						Integer productID = product.getProductIDbyDisplayName(display, false);
//						if (productID != null)
//						{
//							Integer grade = product.getGrade(productID, false);
//							if (grade > 3)
//							{
//								keep.add(content);
//							}
//						} else
//						{
//							removable.add(content);
//						}
//					} else
//					{
//						removable.add(content);
//					}
//				}
//				e.getDrops().removeAll(removable);
//				e.getDrops().removeAll(keep);
//				if (respawn.containsKey(du))
//				{
//					List<ItemStack> list = respawn.get(du);
//					for (ItemStack item : keep)
//					{
//						if (!list.contains(item))
//						{
//							list.add(item);
//						}
//					}
//					respawn.put(du, list);
//				} else
//				{
//					respawn.put(du, keep);
//				}
//				if (main.debug)
//				{
//					Bukkit.getConsoleSender().sendMessage("Respawn: " + respawn.keySet());
//				}
//			}
//			for (Assignment assignment : userKiller.getAssignmentList())
//			{
//				if (assignment instanceof AssignmentKill)
//				{
//					AssignmentKill Assignment = (AssignmentKill) assignment;
//					if (!Assignment.isOnlyBandits())
//					{
//						Assignment.addProgress(1);
//					}
//					break;
//				}
//			}
//			if (userKiller != null)
//			{
//				userKiller.addKills(false, 1, 1);
//				userKiller.addExperience(exp, true);
//			}
//			if (userDied != null)
//			{
//				userDied.addDeaths(1);
//			}
//			killer.sendMessage(ColorOptions.messageachievement + "You received " + ColorOptions.messagesubjects + exp + ColorOptions.messageachievement + " experience for killing " + ColorOptions.messagesubjects + died.getName());				
//		} else if (e.getEntity() instanceof Player)
//		{
//			Player died = (Player) e.getEntity();
//			User user = null;
//			
//			try
//			{
//				user = Users.getUser(died.getUniqueId());
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(null, died, true);
//				e.setDeathMessage(message);
//				e.setDroppedExp(0);
//				return;
//			} catch (UserIsNpcException ex)
//			{
//				e.setDroppedExp(0);
//				e.setDeathMessage(message);
//				return;
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//				ErrorHandlers.userNotFoundAction(null, died, true);
//				e.setDroppedExp(0);
//				e.setDeathMessage(message);
//				return;
//			}
//			UUID du = died.getUniqueId();
//			
//			Integer coins = user.getCoins()/5;
//			
//			user.removeCoins(coins);
//			user.setLastDeathLocation(deathLoc);
//			
//			user.addDeaths(1);
//			e.setDroppedExp(0);
//			for (ItemStack item : died.getInventory().getContents())
//			{
//				if (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "personal menu"))
//				{
//					menu.put(du, item);
//				}
//			}
//			died.sendMessage(ColorOptions.message + "You lost 20% of your coins when you died!");
//		}
//		e.setDroppedExp(0);
//		e.setDeathMessage(message);
//	}
	
	@EventHandler
	public void ExpMobKill(EntityDeathEvent e)
	{
		e.setDroppedExp(0);
		if (e.getEntityType() == EntityType.OCELOT && e.getEntity().getKiller() instanceof Player)
		{
			Player p = e.getEntity().getKiller();
			UUID uuid = p.getUniqueId();
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, p, true);
				e.setDroppedExp(0);
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, p, true);
				e.setDroppedExp(0);
				return;
			}
			Integer exp = user.getExpPart(12);
			Integer coins = main.getRandom(1000, 12000);
			user.addExperience(exp, true);
			user.addCoins(coins);
	        Bukkit.getServer().getPluginManager().callEvent(new ExperienceChangeEvent(user, exp, p));
	        p.sendMessage(ColorOptions.messageachievement + "You received " + ColorOptions.coinStats + coins + " coins" + ColorOptions.messageachievement + " and " + ColorOptions.messagesubjects + exp + " experience!");
	        e.getEntity().getLocation().getWorld().dropItemNaturally(e.getEntity().getLocation(), product.getRandomProduct(null, null));
	        if (main.getRandom(0, 100) <= 1)
	        {
	        	user.addSkillPoints(false, 1);
	        	p.sendMessage(ColorOptions.staffsubjects + "You received a skillpoint!");
	        } else
	        if (main.getRandom(0, 100) <= 5)
	        {
	        	Integer gems = user.getMultipliedInt(main.getRandom(20, 200));
	        	user.addGems(gems);
	        	p.sendMessage(ColorOptions.staffsubjects + "You received " + ColorOptions.gemStats + gems + ColorOptions.staffsubjects + " gems!");
	        } else
	        if (main.getRandom(0, 100) <= 15)
	        {
	        	Integer gems = user.getMultipliedInt(main.getRandom(2, 50));
	        	user.addGems(gems);
	        	p.sendMessage(ColorOptions.staffsubjects + "You received " + ColorOptions.gemStats + gems + ColorOptions.staffsubjects + " gems!");
	        }
		}
	}
	
//	@EventHandler
//	public void respawn(PlayerRespawnEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		if (menu.containsKey(uuid))
//		{
//			e.getPlayer().getInventory().addItem(menu.get(uuid));
//			menu.remove(uuid);
//		}
//		if (respawn.containsKey(uuid))
//		{
//			for (ItemStack item : respawn.get(uuid))
//			{
//				player.getInventory().addItem(item);
//			}
//			player.updateInventory();
//			respawn.remove(uuid);
//		}
//	}
}
