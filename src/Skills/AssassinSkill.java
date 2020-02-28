package Skills;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class AssassinSkill implements Listener
{
	Skill skill = new Skill();
	private Main main;
	public AssassinSkill(Main main) 
	{
		this.main = main;
	}
	public static Map<UUID, Integer> Cooldowntime = new HashMap<UUID, Integer>();
	public static Map<UUID, Integer> invtime = new HashMap<UUID, Integer>();

	
    @EventHandler
    public void assassin(PlayerToggleSneakEvent e)
    {
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
    	Integer specialskillID = null;
    	if (user.getSpecialSkillID() != -1 && user.getSpecialSkillID() != 0)
    	{
    		specialskillID = user.getSpecialSkillID();
    	} else
    	{
    		return;
    	}
    	String skill = user.getSpecialSkillName();
    	Location ploc = player.getLocation();
    	Double radius = 10D;
    	if (user.getSpecialSkillID() != 0)
    	{
    		if (skill.equalsIgnoreCase("assassin"))
        	{
        		if (!player.getGameMode().equals(GameMode.CREATIVE) || !player.isFlying())
        		{
        			if (!player.isSneaking())
            		{
            			if (Bukkit.getServer().getOnlinePlayers().size() != 0)
            			{
            				if (!Cooldowntime.containsKey(uuid))
            				{
            					if (!invtime.containsKey(uuid))
            					{
                					invtime.put(uuid, Integer.valueOf(4));
                	            	player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Assassin + "Assassin" + ChatColor.GRAY + "]" + ColorOptions.Assassin + "You unleashed the assassin skill!");
                	            	Bukkit.getServer().getWorld(player.getWorld().getName()).playSound(player.getLocation(), SoundHandler.BLAZE_BREATH, 1.0F, 1.0F);
                					for (Player players : Bukkit.getOnlinePlayers())
                					{
                						players.hidePlayer(player);
                					}
                					for (Player players : Bukkit.getOnlinePlayers())
                					{
                						Location plocs = players.getLocation();
                						if (plocs.distance(ploc) <= radius)
                						{
                    						players.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*1, 2));
                						}
                					}
            						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
                        	    	{
                        	            public void run()
                        	            {
                        	            	for (Player players : Bukkit.getOnlinePlayers())
                        	            	{
                                        		players.showPlayer(player);
                        	            	}
                        	            	player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Assassin + "Assassin" + ChatColor.GRAY + "]" + ColorOptions.Assassin + "Skills needs to re-charge!");
                                    		Bukkit.getServer().getWorld(player.getWorld().getName()).playSound(player.getLocation(), SoundHandler.BLAZE_DEATH, 1.0F, 1.0F);
                        	            	Cooldowntime.put(uuid, Integer.valueOf(30));
                        	            	invtime.remove(uuid);
                        	            }
                        	        }, 4*20);
            					} else
            					{
            						
            					}
            				} else
            				{
            					player.playSound(player.getLocation(), SoundHandler.BLAZE_HIT, 2.0F, 2.0F);
            					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Assassin + "Assassin" + ChatColor.GRAY + "]" + ColorOptions.Assassin + "You need to wait " + ColorOptions.messagesubjects + Cooldowntime.get(uuid) + ColorOptions.Assassin + " seconds!");
            				}
            			} else
            			{
            				player.sendMessage(ChatColor.RED + "No players around to hide from");
            			}
            		}
        		}
        	}
    	}
    }
}
