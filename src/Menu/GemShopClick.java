package Menu;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import Handlers.ColorOptions;
import Main.Main;
import Products.Product;
import Users.User;

public class GemShopClick
{
	Product product = new Product();
	Menu menu = new Menu();
	private Main main;
	public GemShopClick(Main main) 
	{
		this.main = main;
	}
	
	public void onClick(InventoryClickEvent e, User user)
	{
		Player player = (Player) e.getWhoClicked();

		e.setCancelled(true);
		ItemStack clicked = e.getCurrentItem();
		if (clicked != null && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName())
		{
			String display = clicked.getItemMeta().getDisplayName();
			if (ChatColor.stripColor(display).equalsIgnoreCase("back"))
			{
				this.menu.OpenPersonalMenu(user);
			}
			Integer productID = null;
			for (Integer productIDs : product.getIDList(true, 13, true))
			{
				String displayName = product.getDisplayName(productIDs, false);
				if (display.equalsIgnoreCase(displayName))
				{
					productID = productIDs;
					break;
				}
			}
			
			if (productID != null)
			{
				Integer price = product.getPriceMax(productID);
				Integer gems = user.getGems();
				if (gems < price)
				{
					player.sendMessage(ColorOptions.error + "You need " + (price-gems) + " more gems to buy this!");
					return;
				}
				if (player.getInventory().firstEmpty() == -1)
				{
					player.sendMessage(ColorOptions.error + "Your inventory must contain atleast 1 free slot!");
					return;
				}
				user.removeGems(price);
				player.closeInventory();
				player.getInventory().addItem(product.createPropertyItem(productID, 1, false, false));
				player.updateInventory();
				player.sendMessage(ColorOptions.messageachievement + ColorOptions.messageArrow + "You purchased a " + display + " for " + price + ColorOptions.gemStats + " Gems");
				user.playSound("buyitem");
			}
		}
	}
}
