package Skills;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import DataManager.Users2;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.PurchaseEvent;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;

public class ForgerSkill implements Listener
{
	private Main main;
	public ForgerSkill(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onPurchase(PurchaseEvent e)
	{
		User user = e.getUser();
		Player player = user.getPlayer();
		UUID uuid = player.getUniqueId();
		if (!Users2.users.contains(user))
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}
		if (user.getSpecialSkillName().equalsIgnoreCase("forger"))
		{
			ItemStack item = e.getItem();
			if (Bukkit.getPlayer(uuid) != null)
			{
				Random random = new Random();
				if (random.nextInt(100) <= 20)
				{
					Location loc = player.getLocation();
					Inventory inv = player.getInventory();
					if (inv.firstEmpty() != -1)
					{
						inv.addItem(item);
					} else
					{
						loc.getWorld().dropItemNaturally(loc, item);
						player.sendMessage(ColorOptions.error + "Inventory full, dropped item on the ground!");
					}
					loc.getWorld().playSound(loc, SoundHandler.ANVIL_USE, 0.8F, 1.0F);
					player.sendMessage(ColorOptions.Forger + "[Forger]-" + ColorOptions.messageachievement + "Purchase succesfully duplicated!");
				}
			}
		}
	}
}
