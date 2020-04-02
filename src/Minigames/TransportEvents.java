package Minigames;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class TransportEvents implements Listener
{
	WorldGuard worldguard = new WorldGuard();
	Main main = Main.getPlugin(Main.class);
	public TransportEvents(Main main) 
	{
		this.main = main;
		// TODO Auto-generated constructor stub
	}
	
	public static CopyOnWriteArrayList<Transport> pending = new CopyOnWriteArrayList<Transport>();
	
	
//	@EventHandler
//	public void onWalk(PlayerMoveEvent e)
//	{
//		if (!pending.isEmpty())
//		{
//			for (Transport transport : pending)
//			{
//				if (transport.getPlayer() == e.getPlayer())
//				{
//					if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ())
//					{
//						Player player = e.getPlayer();
//						Location loc = player.getLocation();
//						Integer propertyID = worldguard.getStructureIDbyRegion("property", loc, worldguard.getRegionManager(loc.getWorld()));
//						if (propertyID != null)
//						{
//							if (propertyID == transport.getWarehouseID())
//							{
//								transport.reachedTarget();
//							}
//						}
//					}
//				}
//			}
//		}
//	}
	
//	@EventHandler
//	public void onLeave(PlayerQuitEvent e)
//	{
//		if (!pending.isEmpty())
//		{
//			for (Transport transport : pending)
//			{
//				if (transport.getPlayer() == e.getPlayer())
//				{
//					transport.failed(ColorOptions.error + "Player " + transport.getPlayer().getName() + " left while transporting items!", false);
//				}
//			}
//		}
//	}
	
//	@EventHandler
//	public void onCommand(PlayerCommandPreprocessEvent e)
//	{
//		Player player = e.getPlayer();
//		if (!pending.isEmpty())
//		{
//			for (Transport transport : pending)
//			{
//				if (transport.getPlayer() == player)
//				{
//					if (e.getMessage().equalsIgnoreCase("/menu") || e.getMessage().equalsIgnoreCase("/point"))
//					{
//						e.setCancelled(true);
//						player.sendMessage(ColorOptions.error + "You can't perform this command when transporting items!");
//					}
//				}
//			}
//		}
//	}
	
//	@EventHandler
//	public void onKill(PlayerDeathEvent e)
//	{
//		if (e.getEntity() instanceof Player)
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
//				return;
//			} catch (Exception ex)
//			{
//				return;
//			}
//			
//			Transport transport = Users.GetTransport(user);
//			if (transport != null)
//			{
//				if (e.getEntity().getKiller() instanceof Player)
//				{
//					Player killer = (Player) e.getEntity().getKiller();
//					User userKiller = null;
//					
//					try
//					{
//						user = Users.getUser(killer.getUniqueId());
//					} catch (UserNotFoundException ex)
//					{
//						ErrorHandlers.userNotFoundAction(died, killer, false);
//						return;
//					} catch (Exception ex)
//					{
//						ex.printStackTrace();
//						ErrorHandlers.userNotFoundAction(died, killer, false);
//						return;
//					}
//					userKiller.addCoins(transport.getComission());
//					killer.sendMessage(ColorOptions.messageachievement + "You succesfully killed " + transport.getPlayer().getName() + " while transporting items!");
//					killer.sendMessage(ColorOptions.messageachievement + "You received " + transport.getComission() + " coins!");
//					transport.failed(ColorOptions.error + "Player " + transport.getPlayer().getName() + " got killed by " + e.getEntity().getKiller().getName() + " while transporting items!", true);
//				} else
//				{
//					transport.failed(ColorOptions.error + "Player " + transport.getPlayer().getName() + " got killed while transporting items!", true);
//				}
//			}
//		}
//	}
	
//	@EventHandler
//	public void onRespawn(PlayerRespawnEvent e)
//	{
//		Player died = e.getPlayer();
//		User user = null;
//		
//		try
//		{
//			user = Users.getUser(died.getUniqueId());
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, died, true);
//			return;
//		} catch (Exception ex)
//		{
//			return;
//		}
//		
//		Transport transport = Users.GetTransport(user);
//		
//		if (transport != null)
//		{
//			transport.cancel(ColorOptions.error + "You failed to transport the items!");
//		}
//	}
}
