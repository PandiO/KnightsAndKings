package Skills;

import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class StrengthEvent implements Listener
{
	Skill skill = new Skill();
	private Main main;
	public StrengthEvent(Main main) 
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void StrengthHit(EntityDamageByEntityEvent e)
//	{
//		if (!main.enableSkills)
//		{
//			return;
//		}
//		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player)
//		{
//			Player player = (Player) e.getDamager();
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
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//				ErrorHandlers.userNotFoundAction(null, player, true);
//				return;
//			}
//			if (user.inSafeZone())
//			{
//				return;
//			}
//			Random chance = new Random();
//			Integer amount = skill.getSkillValue("Strength", user.getStrengthID());
//			if (!user.getFriendList().contains(e.getEntity().getUniqueId()))
//			{
//				if (user.getStrengthID() == 0)
//				{
//					
//				} else
//				{
//					if (user.getStrengthID() != 7)
//					{
//						if (chance.nextInt() <= amount)
//						{
//							player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*5, 1));
//						} else
//						{
//							
//						}
//					} else
//					{
//						if (chance.nextInt() <= amount)
//						{
//							player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*4, 2));
//						} else
//						{
//							
//						}
//					}
//				}
//			}
//		}
//	}
}
