package Products;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import Main.Main;

public class SpecialItemEvents implements Listener
{
	Product product =  new Product();
	Enchantment enchantment = new Enchantment();
	private Main main;
	public SpecialItemEvents(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Action action = e.getAction();
		Player player = e.getPlayer();
		
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
		{
			if (player.getItemInHand() != null)
			{
				ItemStack item = player.getItemInHand();
				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
				{
					String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());
					if (display.equalsIgnoreCase("legendary sword box")) 
					{
						e.setCancelled(true);
						player.getInventory().addItem(product.getLegendarySwordBox());
						player.updateInventory();
						ItemStack hand = player.getItemInHand();
						int amount = hand.getAmount();
						if (amount > 1) {
							hand.setAmount(amount - 1);
							player.setItemInHand(hand);
						} else {
							player.setItemInHand(new ItemStack(Material.AIR));
						}
						player.sendMessage(ChatColor.LIGHT_PURPLE + "You received a Legendary Random Sword!");
					} else if (display.equalsIgnoreCase("rare sword box"))
					{
						e.setCancelled(true);
						player.getInventory().addItem(product.getRareSwordBox());
						player.updateInventory();
						ItemStack hand = player.getItemInHand();
						int amount = hand.getAmount();
						if (amount > 1) {
							hand.setAmount(amount - 1);
							player.setItemInHand(hand);
						} else {
							player.setItemInHand(new ItemStack(Material.AIR));
						}
						player.sendMessage(ChatColor.BLUE + "You received a Random Sword!");
					}
				}
			}
		}
	}
}
