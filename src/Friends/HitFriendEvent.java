package Friends;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class HitFriendEvent implements Listener
{
	private Main main;
	WorldGuard worldguard = new WorldGuard();
	public HitFriendEvent(Main main) 
	{
		this.main = main;
	}
	
	
	@EventHandler
	public void FriendHit(EntityDamageByEntityEvent e1)
	{
		if (e1.getEntity() instanceof Player)
		{
			if (Users.existUser(e1.getEntity().getName()))
			{
				Player damaged = (Player) e1.getEntity();
				if (e1.getDamager() instanceof Player)
				{
					Player damager = (Player) e1.getDamager();
					if (Users.existUser(damager.getName()))
					{
						User userDamaged = Users.getUser(damaged.getUniqueId());
						User userDamager = Users.getUser(damager.getUniqueId());
						
						if (userDamaged != null && userDamager != null)
						{
							RegionManager manager = worldguard.getRegionManager(damager.getWorld());
							if (userDamaged.getFriendList().contains(damager.getUniqueId()) && worldguard.getStructureIDbyRegion("arena", damager.getLocation(), manager) == null)
							{
								e1.setCancelled(true);
								damager.playSound(damager.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
							} else
							{
								return;
							}
						}
					}
				}
				if (e1.getDamager() instanceof Arrow)
				{
		            final Arrow arrow = (Arrow) e1.getDamager();
		            if (arrow.getShooter() instanceof Player)
		            {
		            	Player damagerarrow = (Player) arrow.getShooter();
		            	User shooter = null;
		            	if (Users.existUser(damagerarrow.getName()) && Users.getUser(damagerarrow.getUniqueId()) != null)
		            	{
		            		shooter = Users.getUser(damagerarrow.getUniqueId());
		            		
							RegionManager manager = worldguard.getRegionManager(damagerarrow.getWorld());
			            	if (shooter.getFriendList().contains(((Player) e1.getEntity()).getUniqueId()) && worldguard.getStructureIDbyRegion("arena", damagerarrow.getLocation(), manager) == null)
			            	{
			            		e1.setCancelled(true);
			            		damagerarrow.playSound(damagerarrow.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
			            	} else
			        		{
			        			return;
			        		}
		            	}
		            } else
		    		{
		    			return;
		    		}
				} else
				{
					return;
				}
			}
		}
	}
}
