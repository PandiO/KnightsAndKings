package Skills;

import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Handlers.NewSkillHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class SpeedEvent implements Listener
{
	private Main main;
	public SpeedEvent(Main main) 
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void SpeedWalk(PlayerJoinEvent e)
//	{
//		//Managed by the JoinEvents class in the Users package
//	}
	
//	@EventHandler
//	public void SpeedWalk2(PlayerRespawnEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		
//		try
//		{
//			user = Users.getUser(uuid);
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
//		if (main.existUser(uuid))
//		{
//			Integer speedlevel =  user.getSpeedID();
//			if (speedlevel == 1)
//			{
//	 			player.setWalkSpeed(0.22F);
//			} else if (speedlevel == 2)
//			{
//	 			player.setWalkSpeed(0.24F);
//			} else if (speedlevel == 3)
//			{
//	 			player.setWalkSpeed(0.26F);
//			} else if (speedlevel == 4)
//			{
//	 			player.setWalkSpeed(0.28F);
//			} else if (speedlevel == 5)
//			{
//	 			player.setWalkSpeed(0.30F);
//			} else if (speedlevel == 6)
//			{
//	 			player.setWalkSpeed(0.32F);
//			} else if (speedlevel == 7)
//			{
//	 			player.setWalkSpeed(0.32F);
//			}
//		}
//	}
	
	@EventHandler
	public void SpeedWalk3(NewSkillHandler e)
	{
		User user = e.getUser();
		Player player = user.getPlayer();
		String skill = e.getSkill();
		Integer speedlevel = e.getLevel();
		if (skill.equalsIgnoreCase("speed"))
		{
			if (speedlevel == 1)
			{
	 			player.setWalkSpeed(0.22F);
			} else if (speedlevel == 2)
			{
	 			player.setWalkSpeed(0.24F);
			} else if (speedlevel == 3)
			{
	 			player.setWalkSpeed(0.26F);
			} else if (speedlevel == 4)
			{
	 			player.setWalkSpeed(0.28F);
			} else if (speedlevel == 5)
			{
	 			player.setWalkSpeed(0.30F);
			} else if (speedlevel == 6)
			{
	 			player.setWalkSpeed(0.32F);
			} else if (speedlevel == 7)
			{
	 			player.setWalkSpeed(0.32F);
			}
		}
	}
//	@EventHandler 
//	public void SpeedHit(EntityDamageByEntityEvent e)
//	{
//		if (e.getEntity() instanceof Player)
//		{
//			Player player = (Player) e.getEntity();
//			UUID uuid = player.getUniqueId();
//			User user = null;
//			
//			try
//			{
//				user = Users.getUser(uuid);
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
//			if (main.existUser(uuid))
//			{
//				Integer level = user.getSpeedID();
//				Random rand = new Random();
//				if (level == 7)
//				{
//					if (rand.nextInt(100) <= 50)
//					{
//						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 1));
//						player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You got Speed II for 5 seconds!");
//					}
//				}
//			}
//		} else
//		{
//			return;
//		}
//	}
}
