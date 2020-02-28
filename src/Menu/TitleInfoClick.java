package Menu;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import Main.Main;
import Users.User;

public class TitleInfoClick
{
	Menu menu = new Menu();
	private Main main;
	public TitleInfoClick(Main main) 
	{
		this.main = main;
	}
	
	public void Onclick(InventoryClickEvent e, User user)
	{
		ItemStack clicked = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
		UUID uuid = player.getUniqueId();

		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		e.setCancelled(true);
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.OpenPersonalMenu(user);
		}
	}
}
