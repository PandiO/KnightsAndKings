package Skills;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;


public class AvengerSkill implements Listener
{
	private Main main;
	public AvengerSkill(Main main) 
	{
		this.main = main;
	}
	
	public static HashMap<UUID, UUID> avenger = new HashMap<UUID, UUID>();
	
//	@EventHandler
//	public void Avenger(PlayerDeathEvent e)
//	{
//		if (e.getEntity() instanceof Player && e.getEntity().getKiller() instanceof Player)
//		{
//			Player died = (Player) e.getEntity();
//			Player killer = (Player) died.getKiller();
//			UUID uuid = died.getUniqueId();
//			UUID Ku = killer.getUniqueId();
//			User userKiller = null;
//			User userDied = null;
//			
//			try
//			{
//				userKiller = users.getUser(killer.getUniqueId());
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(null, killer, true);
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//				ErrorHandlers.userNotFoundAction(null, killer, true);
//			}
//			try
//			{
//				userDied = users.getUser(died.getUniqueId());
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(null, died, true);
//				return;
//			} catch (Exception ex)
//			{
//				return;
//			}
//	    	Integer specialskillID = null;
//	    	if (userDied.getSpecialSkillID() != -1 && userDied.getSpecialSkillID() != 0)
//	    	{
//	    		specialskillID = userDied.getSpecialSkillID();
//	    	} else
//	    	{
//	    		return;
//	    	}
//			if (specialskillID != 0)
//			{
//				if (userDied.getSpecialSkillName().equalsIgnoreCase("avenger"))
//				{
//					avenger.put(uuid, Ku);
//					died.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "Avenger skill activated!");
//				} else
//				{
//					
//				}
//				if (avenger.containsValue(died))
//				{
//					died.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "You have been avenged!");
//					avenger.remove(Ku);
//					killer.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "You have avenged yourself!");
//
//				} else
//				{
//					
//				}
//			}
//		} else
//		{
//			
//		}
//	}
	
//	@EventHandler
//	public void AttackDamage(PlayerRespawnEvent e)
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
//		if (user.getSpecialSkillName().equalsIgnoreCase("avenger"))
//		{
//			if (avenger.containsKey(uuid))
//			{
//				Player target = Bukkit.getPlayer(avenger.get(uuid));
//				if (target != null)
//				{
//					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "You have +50% attack damage against " + target.getName());
//				} else
//				{
//					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "Your killer is not online anymore!");
//				}
//			} else
//			{
//				
//			}
//		} else
//		{
//			
//		}
//	}
	
//	@EventHandler
//	public void Kill(EntityDamageEvent e)
//	{
//		if (e.getEntity() instanceof Player)
//		{
//			Player avenged = (Player) e.getEntity();
//			UUID uuid = avenged.getUniqueId();
//			if (avenger.containsValue(uuid))
//			{
//				for (Entry<UUID, UUID> entry : avenger.entrySet()) 
//				{
//		            if (entry.getValue().equals(uuid)) 
//		            {
//		                for (Player players : Bukkit.getOnlinePlayers())
//		                {
//		                	if (players.getUniqueId().equals(entry.getKey()))
//		                	{
//		                		Double normaldamage = e.getDamage();
//		                		Double plusdamage = (normaldamage *Double.valueOf(1.5F));
//		                		avenged.damage(plusdamage);
//		                	} else
//		                	{
//		                		
//		                	}
//		                }
//		            } else
//		            {
//		            	
//		            }
//		        }
//			} else
//			{
//				
//			}
//		} else
//		{
//			
//		}
//	}
}
