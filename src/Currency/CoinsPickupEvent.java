package Currency;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class CoinsPickupEvent implements Listener
{
	private Main main;
	public CoinsPickupEvent(Main main) 
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
			if (type == Material.GOLD_NUGGET || type == Material.GOLD_INGOT || type == Material.GOLD_BLOCK)
			{
				if (!user.inOwnerModus())
				{
					ItemStack item = e.getItem().getItemStack();
					e.setCancelled(true);
					e.getItem().remove();
					Integer coins = 100;
					Integer amount = item.getAmount();
					if (!item.hasItemMeta())
					{
						if (type == Material.GOLD_NUGGET)
						{
							coins = (amount *main.getRandom(100, 10000));
						}
						if (type == Material.GOLD_INGOT)
						{
							coins = (amount * main.getRandom(10000, 25000));
						}
						if (type == Material.GOLD_BLOCK)
						{
							coins = (amount * main.getRandom(25000, 80000));
						}
						coins =	user.getMultipliedInt(coins);
					} else
					{
						ItemMeta meta = item.getItemMeta();
						if (meta.hasLore())
						{
							coins = Integer.valueOf(meta.getLore().get(0).split(": ")[1]);
							coins = coins*amount;
						}
					}
					p.playSound(p.getLocation(), SoundHandler.ITEM_PICKUP, 0.5F, 1.0F);
					user.addCoins(coins);
					if (e.getItem() != null)
					{
						e.getItem().remove();
					}
				}
			}
		}
	}
}
