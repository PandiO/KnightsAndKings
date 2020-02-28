package Skills;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class SkillPointPickupEvent implements Listener
{
	private Main main;
	public SkillPointPickupEvent(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void EmeraldPickup(PlayerPickupItemEvent e)
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
		Material type = e.getItem().getItemStack().getType();
		if (type == Material.EMERALD || type == Material.EMERALD_BLOCK)
		{
			if (!e.getItem().getItemStack().hasItemMeta())
			{
				if (!main.ownermodus.containsKey(uuid) || main.ownermodus.get(uuid) == false)
				{
					Random random = new Random();
					e.setCancelled(true);
					if (random.nextInt(100) <= 80)
					{
						Integer points = 1;
						Integer amount = e.getItem().getItemStack().getAmount();
						if (type == Material.EMERALD)
						{
							points = (amount *main.getRandom(0, 1));
						} else
						if (type == Material.EMERALD_BLOCK)
						{
							if (random.nextInt(100) <= 1)
							{
								points = (amount * main.getRandom(2, 5));
							} else
							{
								points = (amount * main.getRandom(4, 10));
							}
						}
						player.playSound(player.getLocation(), SoundHandler.ITEM_PICKUP, 0.5F, 1.0F);

						points = user.getMultipliedInt(points);
						user.addSkillPoints(false, points);
						e.getItem().remove();
					} else
					{
						player.sendMessage(ColorOptions.message + "Unfortunatly this Gem was of poor quality so you didn't receive any gems!");
					}
				}
			}
		}
	}
}
