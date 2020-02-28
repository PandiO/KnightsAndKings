package Menu;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Main.Main;
import Users.User;

public class DonatorInfoClick
{
	Menu menu = new Menu();
	private Main main;
	public DonatorInfoClick(Main main) 
	{
		this.main = main;
	}
	
	public void Onclick(InventoryClickEvent e, User user)
	{
		ItemStack clicked = e.getCurrentItem();
		Inventory menu = e.getInventory();
		
		if (ChatColor.stripColor(menu.getName()).equalsIgnoreCase("Donator-ranks' information"))
		{
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
			e.setCancelled(true);
			if (dc.equalsIgnoreCase("back"))
			{
				this.menu.OpenPersonalMenu(user);
			}
		}
	}
}
