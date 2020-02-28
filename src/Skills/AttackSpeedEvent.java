package Skills;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import API_methods.WorldGuard;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.DoubleDamage;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class AttackSpeedEvent implements Listener
{
	Skill skill = new Skill();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public AttackSpeedEvent(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void AttackSpeedHit(EntityDamageByEntityEvent e)
	{
		if (!main.enableSkills)
		{
			return;
		}
		if (e.getEntity() instanceof ArmorStand)
		{
			Bukkit.broadcastMessage("Damage found! " + e.getDamage() + ", " + e.getFinalDamage());
		}
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getDamager();
			Player target = (Player) e.getEntity();
			User userDamager = null;
			User userTarget = null;
			
			try
			{
				userDamager = Users.getUser(player.getUniqueId());
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
				userTarget = Users.getUser(target.getUniqueId());
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(player, target, false);
				return;
			} catch (UserIsNpcException ex)
			{
				this.dealDamage(userDamager, target, e);
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(player, target, false);
				return;
			}
			if (!userDamager.inSafeZone() && !userTarget.inSafeZone())
			{
				this.dealDamage(userDamager, target, e);
			}
		}
	}
	
	public void dealDamage(User userDamager, Player target, EntityDamageByEntityEvent e)
	{
		Player player = userDamager.getPlayer();
		Random chance = new Random();
		if (userDamager.getAttackSpeedID() != 0)
		{
			Integer amount = skill.getSkillValue("AttackSpeed", userDamager.getAttackSpeedID());
			if (userDamager.getAttackSpeedID() <7)
			{
				if (!userDamager.getFriendList().contains(e.getEntity().getUniqueId()))
				{
					if (chance.nextInt(100) <= amount)
					{
						e.setDamage(e.getDamage()*1.5);
						Bukkit.getServer().getPluginManager().callEvent(new DoubleDamage(userDamager, target, e.getDamage(), false));
						player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Double Hit!");
						player.playSound(player.getLocation(), SoundHandler.ANVIL_LAND, 0.1F, 2.0F);
						
					}
				}
			} else
			{
				if (!userDamager.getFriendList().contains(e.getEntity().getUniqueId()))
				{
					if (chance.nextInt(100) <= amount)
					{
						e.setDamage(e.getDamage()*2);
						Bukkit.getServer().getPluginManager().callEvent(new DoubleDamage(userDamager, target, e.getDamage(), true));
						player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Triple Hit!");
						player.playSound(player.getLocation(), SoundHandler.ANVIL_LAND, 0.1F, 2.0F);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void DoubleDamage(DoubleDamage e)
	{
		Player p = e.getPlayer();
		Player target = e.getTarget();
		Double damage = e.getDamage();
		if (e.getMultiplier() == false)
		{
			target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have been " + ChatColor.BOLD + "Double" + " hit!");
		} else
		{
			target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have been " + ChatColor.BOLD + "Triple" + " hit!");

		}
	}
	

}

