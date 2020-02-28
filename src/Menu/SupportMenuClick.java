package Menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import Products.ProductCategory;
import Tutorial.Tutorial;
import Users.User;

public class SupportMenuClick 
{
	Tutorial tutorial = new Tutorial();
	Menu menu = new Menu();
	ProductCategory productCategory = new ProductCategory();
	
	public void onSupportMenuClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		List<String> lore = clicked.getItemMeta().getLore();
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.OpenPersonalMenu(user);
			user.playSound("back");
		}
		if (dc.equalsIgnoreCase("list of items"))
		{
			this.menu.openItemListMenu(user, 1, -1, -1);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("tutorials"))
		{
			this.menu.openTutorialMenu(user);
			user.playSound("succesclick");
		}
	}
	
	public void onTutorialListClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openSupportMenu(user);
			user.playSound("back");
		}
		if (tutorial.file.exist(dc))
		{
			user.playSound("succesclick");
			player.closeInventory();
			tutorial.createTutorial(user, dc);
		}
	}
	
	public void onItemListClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		List<String> lore = clicked.getItemMeta().getLore();
		String rawFilter = ChatColor.stripColor(e.getInventory().getItem(2).getItemMeta().getDisplayName().split(": ")[1]);
		int filterID = (rawFilter.equalsIgnoreCase("all")) ? -1 : this.productCategory.getCategoryID(rawFilter);
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openSupportMenu(user);
			user.playSound("back");
		}
		if (dc.equalsIgnoreCase("previous"))
		{
			Integer current = Integer.valueOf(lore.get(lore.size()-1).split(": ")[1].split("/")[0]);
			this.menu.openItemListMenu(user, current-1, filterID, -1);
		}
		if (dc.equalsIgnoreCase("next"))
		{
			Integer current = Integer.valueOf(lore.get(lore.size()-1).split(": ")[1].split("/")[0]);
			this.menu.openItemListMenu(user, current+1, filterID, -1);
		}
		if (dc.contains("filter: "))
		{
			if (e.getClick() == ClickType.SHIFT_LEFT)
			{
				this.menu.openItemListMenu(user, 1, -1, -1);
				return;
			}
			List<String> categoryList = this.productCategory.getCategoryNameList();
			
			if (filterID == -1)
			{
				filterID = this.productCategory.getCategoryID(categoryList.get(0));
			} else
			if (categoryList.contains(rawFilter))
			{
				Integer index = (categoryList.indexOf(rawFilter)+1);
				if (index < categoryList.size())
				{
					filterID = this.productCategory.getCategoryID(categoryList.get(index));
				} else
				{
					filterID = -1;
				}
			} else
			{
				filterID = -1;
			}
			this.menu.openItemListMenu(user, 1, filterID, -1);
		}
	}
}
