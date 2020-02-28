package Skills;

import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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

public class HealthEvent implements Listener
{
	
	Skill skill = new Skill();
	private Main main;
	public HealthEvent(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void Healthjoin(PlayerJoinEvent e)
	{
		//Setting health of a player is managed by the JoinEvents in the Users package
	}
	
	@EventHandler
	public void HealthRespawn(PlayerRespawnEvent e)
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
		if (main.existUser(uuid))
		{
			Integer level = user.getHealthID();
			Integer health = skill.getSkillValue("Health", level);
			if (level < 7)
			{
				player.setHealthScale(20 +health);
			} else
			{
				player.setHealthScale(32);

			}
		}
	}
	
	@EventHandler
	public void Health(NewSkillHandler e)
	{
		User user = e.getUser();
		Player player = user.getPlayer();
		String skill = e.getSkill();
		Integer level = e.getLevel();
		if (skill.equalsIgnoreCase("health"))
		{
			Integer health = this.skill.getSkillValue(skill, level);
			if (health < 7)
			{
				player.setHealthScale(20 +health);
			} else
			{
				player.setHealthScale(32);

			}
		}
	}
	@EventHandler
	public void LastHealthUpgrade(EntityDamageByEntityEvent e)
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
			} catch (UserIsNpcException ex)
			{
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			}
			if (main.existUser(uuid))
			{
				Integer level = user.getHealthID();
				Random rand = new Random();
				if (level == 7)
				{
					if (rand.nextInt(100) <= 50)
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*10, 1));
						player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "You have Regeneration I for 10 seconds!");
					}
				} else
				{
					return;
				}
			}
		}
	}
}
