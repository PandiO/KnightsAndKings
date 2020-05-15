package Afk;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class AfkEvents implements Listener
{
	private Main main;
	public AfkEvents(Main main)
	{
		this.main = main;
	}
	
	public static List<Afk> afk = new CopyOnWriteArrayList<Afk>();
	public static ConcurrentHashMap<User, Long> possibleAfk = new ConcurrentHashMap<User, Long>();
	
//	@EventHandler
//	public void onWalk(PlayerMoveEvent e)
//	{
//		Player player = e.getPlayer();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(player.getUniqueId());
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		if (user.isAfk())
//		{
//			if (user.getAfk().teleporting == false)
//			{
//				user.removeAfk();
//			} else
//			{
//				return;
//			}
//		}
//		user.SetAfkCommence((System.currentTimeMillis()+main.afkTime*1000));
////		possibleAfk.put(user, (System.currentTimeMillis()+ main.afkTime*1000));
//	}
	
//	@EventHandler
//	public void onChat(@SuppressWarnings("deprecation") PlayerChatEvent e)
//	{
//		Player player = e.getPlayer();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(player.getUniqueId());
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		if (user.isAfk())
//		{
//			if (user.getAfk().teleporting == false)
//			{
//				user.removeAfk();
//			}
//		}
//		user.SetAfkCommence((System.currentTimeMillis()+ main.afkTime*1000));
////		possibleAfk.put(user, (System.currentTimeMillis()+ main.afkTime*1000));
//	}
	
//	@EventHandler
//	public void onDamage(EntityDamageEvent e)
//	{
//		if (e.getEntity() instanceof Player)
//		{
//			Player player = (Player) e.getEntity();
//			User user = null;
//			
//			try
//			{
//				user = users.getUser(player.getUniqueId());
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(null, player, true);
//				return;
//			} catch (UserIsNpcException ex)
//			{
//				return;
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//				ErrorHandlers.userNotFoundAction(null, player, true);
//				return;
//			}
//			
//			if (user.isAfk())
//			{
//				if (user.getAfk().teleporting == false)
//				{
//					user.removeAfk();
//				}
//			}
//			user.SetAfkCommence((System.currentTimeMillis()+main.afkTime*1000));
////			possibleAfk.put(user, (System.currentTimeMillis()+ main.afkTime*1000));
//		}
//	}
	
//	@EventHandler
//	public void onJoin(PlayerJoinEvent e)
//	{
//		Player player = e.getPlayer();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(player.getUniqueId());
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		if (user.isAfk())
//		{
//			if (user.getAfk().teleporting == false)
//			{
//				user.removeAfk();
//			}
//		}
//		user.SetAfkCommence((System.currentTimeMillis()+main.afkTime*1000));
////		possibleAfk.put(user, (System.currentTimeMillis()+ main.afkTime*1000));
//	}
	
//	@EventHandler
//	public void onQuit(PlayerQuitEvent e)
//	{
//		Player player = e.getPlayer();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(player.getUniqueId());
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		if (user.isAfk())
//		{
//			user.removeAfk();
//		}
////		if (this.possibleAfk.containsKey(user))
////		{
////			this.possibleAfk.remove(user);
////		}
//		if (user.GetAfkCommence() != null)
//		{
//			user.RemoveAfkCommence();
//		}
//	}
	
	public static Afk getPlayerAfk(Player player)
	{
		Afk tafk = null;
		
		for (Afk afks : afk)
		{
			if (afks.getPlayer() == player)
			{
				tafk = afks;
				break;
			}
		}
		
		return tafk;
	}
}
