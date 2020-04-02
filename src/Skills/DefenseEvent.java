package Skills;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import API_methods.WorldGuard;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class DefenseEvent implements Listener
{
	Skill skill = new Skill();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public DefenseEvent(Main main) 
	{
		this.main = main;
	}

	//IS located in the HitFriendEvent
	
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void onHit(EntityDamageByEntityEvent e)
//	{
//		if (!main.enableSkills)
//		{
//			return;
//		}
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
//			if (!user.inSafeZone())
//			{
//				Integer level = user.getDefenseID();
//				Integer reduction = skill.getSkillValue("Defense", level);
//				Double damage = e.getFinalDamage()-((e.getFinalDamage()/100)*Double.valueOf(reduction));
//				Random rand = new Random();
//				if (level > 0)
//				{
//					if (level == 7)
//					{
//						if (rand.nextInt(100) <= 50)
//						{
//							e.setCancelled(true);
//							player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Incoming damage has been reduced by 100%!");
//							Bukkit.getServer().getWorld(player.getWorld().getName()).playSound(player.getLocation(), SoundHandler.ANVIL_LAND, 0.1F, 0.3F);
//						} else
//						{
//							e.setDamage(damage);
//						}
//					} else
//					{
//						e.setDamage(damage);
//						Bukkit.getServer().getWorld(player.getWorld().getName()).playSound(player.getLocation(), SoundHandler.ANVIL_LAND, 0.1F, 0.3F);
//					}
//				}
//			}
//		}
//	}

}
