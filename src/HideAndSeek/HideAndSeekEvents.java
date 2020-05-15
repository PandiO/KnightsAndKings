package HideAndSeek;

import java.util.UUID;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mewin.WGRegionEvents.events.RegionLeaveEvent;

import API_methods.WorldGuard;
import DataManager.Worldguard;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Minigames.Participant;
import Scoreboards.ActionBar;
import Users.User;
import Users.Users;

public class HideAndSeekEvents implements Listener
{
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public HideAndSeekEvents(Main main)
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void onCommand(PlayerCommandPreprocessEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(uuid);
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
//		if (Main.HideAndSeek == null)
//		{
//			return;
//		}
//		if (!Main.HideAndSeek.getParticipating(user))
//		{
//			return;
//		}
//		if (!Main.HideAndSeek.getInHub())
//		{
//			if (!Main.HideAndSeek.getProgress())
//			{
//				return;
//			}
//		}
//		
//		if (!Main.HideAndSeek.getAllowedCommands().contains(e.getMessage()) && !user.inOwnerModus())
//		{
//			e.setCancelled(true);
//			player.sendMessage(ColorOptions.error + "You are can't use this command while playing Hide and Seek!");
//			player.sendMessage(ColorOptions.message + "Type /hs leave to leave");
//		}
//	}
	
//	@EventHandler
//	public void onLeave(PlayerQuitEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(uuid);
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
//		if (Main.HideAndSeek == null)
//		{
//			return;
//		}
//		
//		if (!Main.HideAndSeek.getParticipating(user))
//		{
//			return;
//		}
//		
//		Main.HideAndSeek.leave(user);
//	}
	
//	@EventHandler
//	public void onDamage(EntityDamageByEntityEvent e)
//	{
//		Entity damagedEntity = e.getEntity();
//		
//		if (!(damagedEntity instanceof Player))
//		{
//			return;
//		}
//		
//		Player playerDamaged = (Player) damagedEntity;
//		UUID uuid = playerDamaged.getUniqueId();
//		User userDamaged = null;
//		User userDamager = null;
//		Participant partDamaged = null;
//		Participant partDamager = null;
//		
//		try
//		{
//			userDamaged = users.getUser(uuid);
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, playerDamaged, true);
//			return;
//		} catch (UserIsNpcException ex)
//		{
//			
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, playerDamaged, true);
//			return;
//		}
//		
//		if (Main.HideAndSeek == null)
//		{
//			return;
//		}
//		
//		if (!Main.HideAndSeek.getParticipating(userDamaged))
//		{
//			return;
//		}
//		Main.logMessage("damageEvent found for hide and seek");
//		
//		e.setCancelled(true);
//		partDamaged = Main.HideAndSeek.getParticipant(userDamaged);
//		
//		if (e.getDamager() instanceof Player)
//		{
//			Player playerDamager = (Player) e.getDamager();
//			UUID uuidDamager = playerDamager.getUniqueId();
//			
//			try
//			{
//				userDamager = users.getUser(uuidDamager);
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(null, playerDamager, true);
//				return;
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//				ErrorHandlers.userNotFoundAction(null, playerDamager, true);
//				return;
//			}
//		}
//		
//		if (e.getDamager() instanceof Arrow)
//		{
//			final Arrow arrow = (Arrow) e.getDamager();
//			if (!(arrow.getShooter() instanceof Player))
//			{
//				return;
//			}
//			
//			Player playerDamager = (Player) arrow.getShooter();
//			UUID uuidDamager = playerDamager.getUniqueId();
//			
//			try
//			{
//				userDamager = users.getUser(uuidDamager);
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(null, playerDamager, true);
//				return;
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//				ErrorHandlers.userNotFoundAction(null, playerDamager, true);
//				return;
//			}
//			
//		}
//		
//		if (!Main.HideAndSeek.getParticipating(userDamager))
//		{
//			return;
//		}
//		
//		partDamager = Main.HideAndSeek.getParticipant(userDamager);
//		
//		if (partDamager == null || partDamaged == null)
//		{
//			return;
//		}
//		
//		if (!Main.HideAndSeek.Seekers.contains(partDamager))
//		{
//			return;
//		}
//		
//		if (Main.HideAndSeek.Seekers.contains(partDamaged))
//		{
//			return;
//		}
//		
//		if (Main.HideAndSeek.hideTime)
//		{
//			return;
//		}
//		Main.logMessage("found the two participants..");
//		Main.HideAndSeek.catchParticipant(userDamager, userDamaged);
//	}
	
	@EventHandler
	public void onHungerDecrease(FoodLevelChangeEvent e)
	{
		Entity entity = e.getEntity();
		
		if (!(entity instanceof Player))
		{
			return;
		}
		
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
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}
		
		if (Main.HideAndSeek == null)
		{
			return;
		}
		
		if (!Main.HideAndSeek.getParticipating(user))
		{
			return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onTownLeave(RegionLeaveEvent e)
	{
		Integer townID = Worldguard.getStructureIDbyRegion(e.getRegion());
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}
		
		if (Main.HideAndSeek == null)
		{
			return;
		}
		
		if (!Main.HideAndSeek.getParticipating(user))
		{
			return;
		}
		
		if (!Worldguard.isTownRegion(e.getRegion()))
		{
			return;
		}
		
		if (Worldguard.isChildRegion(e.getRegion()))
		{
			return;
		}
		
		if (Main.HideAndSeek.getTownID() != townID)
		{
			return;
		}
		
		if (!Main.HideAndSeek.getProgress())
		{
			return;
		}
		
		if (Main.HideAndSeek.getFinished())
		{
			return;
		}
		
		//e.setCancelled(true);
		user.pushBack();
		ActionBar greetMessage = new ActionBar(ColorOptions.error + "You can't leave this town while playing Hide and Seek!");
		greetMessage.sendToPlayer(player);
//		player.sendMessage(ColorOptions.message + "Type /hs leave to leave");
	}
}
