package Skills;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class JuggernautSkill implements Listener
{
	private Main main;
	public JuggernautSkill(Main main) 
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void Juggernaut(EntityDamageByEntityEvent e)
//	{
//		if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow)
//		{
//			Player player = (Player) e.getEntity();
//			UUID uuid = player.getUniqueId();
//			User user = null;
//			
//			try
//			{
//				user = users.getUser(uuid);
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
//			if (!user.inSafeZone())
//			{
//				String world = player.getWorld().getName();
//		    	Integer specialskillID = null;
//		    	if (user.getSpecialSkillID() != -1 && user.getSpecialSkillID() != 0)
//		    	{
//		    		specialskillID = user.getSpecialSkillID();
//		    	} else
//		    	{
//		    		return;
//		    	}
//				if (specialskillID != 0)
//				{
//					if (user.getSpecialSkillName().equalsIgnoreCase("juggernaut"))
//					{
//						Bukkit.getWorld(world).playSound(player.getLocation(), SoundHandler.ANVIL_USE, 2.0F, 2.0F);
//						e.setCancelled(true);
//					}
//				}
//			}
//		}
//	}
}
