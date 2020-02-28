package Skills;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class NinjaSkill implements Listener
{
	private Main main;
	public NinjaSkill(Main main) 
	{
		this.main = main;
	}
	
	public static Map<UUID, Integer> ninjatime = new HashMap<UUID, Integer>();

	
	@EventHandler
	public void Ninja(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			Player damager = (Player) e.getDamager();
			Player damaged = (Player) e.getEntity();
			User userDamaged = null;
			try
			{
				userDamaged = Users.getUser(damaged.getUniqueId());
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, damaged, true);
				return;
			} catch (UserIsNpcException ex)
			{
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, damaged, true);
				return;
			}
			if (userDamaged.inSafeZone())
			{
				return;
			}
			UUID uuid = damaged.getUniqueId();
			Location ploc = damaged.getLocation();
			Double radius = 30D;
			Random rand = new Random();
	    	Integer specialskillID = null;
	    	if (userDamaged.getSpecialSkillID() != -1 && userDamaged.getSpecialSkillID() != 0)
	    	{
	    		specialskillID = userDamaged.getSpecialSkillID();
	    	} else
	    	{
	    		return;
	    	}
			if (specialskillID != 0)
			{
				if (userDamaged.getSpecialSkillName().equalsIgnoreCase("ninja"))
				{
					if (!ninjatime.containsKey(uuid))
					{
						if (rand.nextInt(100) <= 30)
						{
							ninjatime.put(uuid, Integer.valueOf(5));
			            	damaged.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Ninja + "Ninja" + ChatColor.GRAY + "]" + ColorOptions.Ninja + "You unleashed the ninja skill!");
							for (Player online : Bukkit.getOnlinePlayers())
							{
								Location plocs = online.getLocation();
								if (plocs.distance(ploc) <= radius)
								{
									online.hidePlayer(damaged);
									online.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*1, 2));
								}
							}
							damager.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have been ninja'd!");
							Bukkit.getWorld(damaged.getWorld().getName()).playSound(damaged.getLocation(), SoundHandler.CREEPER_HISS, 2.0F, 2.0F);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
			    	    	{
			    	            public void run()
			    	            {
			    	            	for (Player players : Bukkit.getOnlinePlayers())
			    	            	{
			                    		players.showPlayer(damaged);
			    	            	}
			    	            	damaged.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Ninja + "Ninja" + ChatColor.GRAY + "]" + ColorOptions.Ninja + "Ninja skill is deactivated!");
			                		Bukkit.getServer().getWorld(damaged.getWorld().getName()).playSound(damaged.getLocation(), SoundHandler.BLAZE_DEATH, 1.0F, 1.0F);
			    	            	ninjatime.remove(uuid);
			    	            }
			    	        }, 5*20);
						}
					}
				}
			}
		}
	}
}
