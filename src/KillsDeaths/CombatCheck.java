package KillsDeaths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.EnterTownEvent;
import Handlers.ErrorHandlers;
import Main.Main;
import Products.Product;
import Scoreboards.ActionBar;
import Users.User;
import Users.Users;

public class CombatCheck implements Listener
{
	Product product = new Product();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public CombatCheck(Main main)
	{
		this.main = main;
	}
	public static Integer combat = 10;
	public static HashMap<Player, Integer> incombat = new HashMap<Player, Integer>();
	public static List<UUID> combatlogged = new ArrayList<UUID>();
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			Player damager = (Player) e.getDamager();
			Player damaged = (Player) e.getEntity();
			User userDamager = null;
			User userDamaged = null;
			
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
			try
			{
				userDamaged = Users.getUser(damaged.getUniqueId());
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(damager, damaged, false);
			} catch (UserIsNpcException ex)
			{
				
			} catch (Exception ex)
			{
				ex.printStackTrace();
				//ErrorHandlers.userNotFoundAction(damager, damaged, false);
			}
			RegionManager manager = worldguard.getRegionManager(damaged.getWorld());
			if (worldguard.getStructureIDbyRegion("town", damaged.getLocation(), worldguard.getRegionManager(damaged.getWorld())) != null)
			{
				if (worldguard.getStructureIDbyRegion("arena", damaged.getLocation(), manager) == null && worldguard.getStructureIDbyRegion("arena", damager.getLocation(), manager) == null)
				{
					return;
				}
			}
			if (worldguard.getStructureIDbyRegion("property", damaged.getLocation(), manager) != null)
			{
				return;
			}
			ActionBar bar = new ActionBar(ColorOptions.error + "You are now in combat! Do not log off");
			if (userDamager != null)
			{
				if (damager.getGameMode() == GameMode.SURVIVAL && !userDamager.inStaffModus() && !userDamager.inOwnerModus())
				{
					if (!incombat.containsKey(damager))
					{
						bar.sendToPlayer(damager);
					}
					incombat.put(damager, combat);
				}
			}
			if (userDamaged != null)
			{
				if (damaged.getGameMode() == GameMode.SURVIVAL && !userDamaged.inStaffModus() && !userDamaged.inOwnerModus())
				{
					if (!incombat.containsKey(damaged))
					{
						bar.sendToPlayer(damaged);
					}
					incombat.put(damaged, combat);
				}
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		if (incombat.containsKey(player))
		{
//			List<ItemStack> content = new ArrayList<ItemStack>();
//			List<ItemStack> removable = new ArrayList<ItemStack>();
//			for (ItemStack cont : player.getInventory().getContents())
//			{
//				if (cont != null && cont.getType() != Material.AIR)
//				{
//					if (cont.hasItemMeta())
//					{
//						content.add(cont);
//					}
//				}
//			}
//			for (ItemStack cont2 : player.getInventory().getArmorContents())
//			{
//				if (cont2 != null && cont2.getType() != Material.AIR)
//				{
//					if (cont2.hasItemMeta())
//					{
//						content.add(cont2);
//					}
//				}
//			}
//			player.getInventory().clear();
//			for (ItemStack item : content)
//			{
//				if (product.soulbound(item))
//				{
//					removable.add(item);
//				} else
//				{
//					player.getWorld().dropItemNaturally(player.getLocation(), item);
//				}
//			}
//			content.removeAll(removable);
			player.setHealth(0.0D);
			combatlogged.add(player.getUniqueId());
			incombat.remove(player);
			main.notify(ColorOptions.error + "Player " + player.getName() + " logged off while in combat!");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if (combatlogged.contains(uuid))
		{
			player.sendMessage(ColorOptions.error + "Your inventory got dropped when you logged off in combat!");
			combatlogged.remove(uuid);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		if (e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			if (incombat.containsKey(player))
			{
				incombat.remove(player);
			}
		}
	}
	
	@EventHandler
	public void onEnter(EnterTownEvent e)
	{
		Player player = e.getUser().getPlayer();
		if (incombat.containsKey(player))
		{
			incombat.remove(player);
			ActionBar bar = new ActionBar(ColorOptions.messagesubjects + "Out of combat!");
			bar.sendToPlayer(player);
		}
	}
	
}
