package Skills;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class ShotbowSkill implements Listener
{
	private Main main;
	public ShotbowSkill(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void Shotbow(EntityShootBowEvent e)
	{
		if (e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
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
			Random rand = new Random();
			String worldname = player.getWorld().getName();
	    	Integer specialskillID = null;
	    	if (user.getSpecialSkillID() != -1 && user.getSpecialSkillID() != 0)
	    	{
	    		specialskillID = user.getSpecialSkillID();
	    	} else
	    	{
	    		return;
	    	}
			if (specialskillID != 0)
			{
				if (user.getSpecialSkillName().equalsIgnoreCase("shotbow"))
				{
					if (rand.nextInt(100) <= 40)
					{
						Vector velocity = e.getProjectile().getVelocity();
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
	        	    	{
	        	            public void run()
	        	            {
	        	            	Arrow arrow = player.launchProjectile(Arrow.class);
	        					arrow.setVelocity(velocity);
	        					arrow.setShooter(player);
	        					Bukkit.getWorld(worldname).playEffect(player.getLocation(), Effect.BOW_FIRE, 1, 1);
	        	            }
	        	        }, 5);
					} else
					{
						
					}
				} else
				{
					
				}
			}
		} else
		{
			
		}
	}
}
