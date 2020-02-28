package Currency;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class GemPickupEvent implements Listener
{
	private Main main;
	public GemPickupEvent(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void GoldPickup(PlayerPickupItemEvent e)
	{
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		User user = Users.getUser(uuid);
		if (user != null)
		{
			Material type = e.getItem().getItemStack().getType();
			if (type == Material.DIAMOND || type == Material.DIAMOND_BLOCK)
			{
				if (!e.getItem().getItemStack().hasItemMeta())
				{
					if (!user.inOwnerModus())
					{
						Random random = new Random();
						e.setCancelled(true);
						if (random.nextInt(100) <= 80)
						{
							Integer gems = 1;
							Integer amount = e.getItem().getItemStack().getAmount();
							if (type == Material.DIAMOND)
							{
								gems = (amount *main.getRandom(1, 15));
								p.playSound(p.getLocation(), SoundHandler.ITEM_PICKUP, 0.5F, 1.0F);
							} else
							if (type == Material.DIAMOND_BLOCK)
							{
								if (random.nextInt(100) <= 1)
								{
									gems = (amount * main.getRandom(30, 150));
								} else
								{
									gems = (amount * main.getRandom(20, 60));
								}
								p.playSound(p.getLocation(), SoundHandler.ITEM_PICKUP, 0.5F, 1.0F);
							}
							gems = user.getMultipliedInt(gems);
							user.addGems(gems);
							e.getItem().remove();
						} else
						{
							p.sendMessage(ColorOptions.message + "Unfortunatly this Gem was of poor quality so you didn't receive any gems!");
						}
					}
				}
			}
		}
	}
}
