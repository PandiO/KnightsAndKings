package Skills;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class PickpocketSkill implements Listener
{
	private Main main;
	public PickpocketSkill(Main main) 
	{
		this.main = main;
	}
	
	public static Map<UUID, Long> Cooldown = new HashMap<UUID, Long>();
	public static Map<UUID, UUID> pickpocketed = new HashMap<UUID, UUID>();

	
	@EventHandler
	public void Pickpocket(PlayerInteractEntityEvent e)
	{
		if (e.getPlayer() instanceof Player && e.getRightClicked() instanceof Player)
		{
			Player player = (Player) e.getPlayer();
			Player victim = (Player) e.getRightClicked();
			User user = null;
			User userVictim = null;
			
			try
			{
				user = Users.getUser(player.getUniqueId());
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
			try
			{
				userVictim = Users.getUser(victim.getUniqueId());
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(player, victim, true);
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(player, victim, true);
				return;
			}
			UUID uuid = player.getUniqueId();
			UUID vu = victim.getUniqueId();
			String skill = user.getSpecialSkillName();
			Random rand = new Random();
			Integer chance = main.getRandom(1, 20);
			Integer amount = ((userVictim.getCoins()/100)*chance);
			Integer gemamount = ((userVictim.getGems()/100)*chance);
			World world = Bukkit.getWorld(player.getWorld().getName());
			e.setCancelled(true);
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
				if (skill.equalsIgnoreCase("pickpocket"))
				{
					if (!Cooldown.containsKey(uuid))
					{
						if (rand.nextInt(100) <= 20)
						{
							userVictim.sendCoins(uuid, amount);
							world.playSound(victim.getLocation(), SoundHandler.GHAST_DEATH, 2.0F, 2.0F);
							player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Pickpocket + "Pickpocket" + ChatColor.GRAY + "]" + ColorOptions.Pickpocket + "You pickpocketed " + ColorOptions.messagesubjects + victim.getName() + ColorOptions.Pickpocket + " and stole " + ColorOptions.messagesubjects + amount + ColorOptions.Pickpocket + " coins!");
							if (rand.nextInt(100) <= 8)
							{
								user.addGems(gemamount);
								player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Pickpocket + "Pickpocket" + ChatColor.GRAY + "]" + ChatColor.DARK_PURPLE + "You were lucky and stole " + ColorOptions.gemStats + gemamount + ChatColor.DARK_PURPLE + " gems!");
							}
							pickpocketed.put(vu, uuid);
							Cooldown.put(uuid, Long.valueOf(System.currentTimeMillis() + 900000L));
						} else
						{
							Cooldown.put(uuid, Long.valueOf(System.currentTimeMillis() + 900000L));
							player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Pickpocket + "Pickpocket" + ChatColor.GRAY + "]" + ChatColor.RED + "You tried to pickpocket " + ColorOptions.messagesubjects + victim.getName() + ChatColor.RED + " but failed!");
						}
					} else
					{
						long current = System.currentTimeMillis();
						Long l = current -Cooldown.get(uuid);
						Integer i = l.intValue();
						int timer = (i / -1000);
						int rest = 3600 - timer;
						int mins = timer/60;
						int secs = timer%60;
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "-" + ChatColor.RED + "You need to wait " + mins + " minutes and " + secs + " seconds before you can pickpocket someone again!");

					}
				}
			}
		}
	}
}
