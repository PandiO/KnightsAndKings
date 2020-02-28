package Menu;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Handlers.PurchaseEvent;
import Main.Main;
import Products.Product;
import Products.PropertyProduct;
import Properties.ItemFrameAdd;
import Users.User;

public class CouponClick implements Listener
{
	WorldGuard worldguard = new WorldGuard();
	PropertyProduct proproduct = new PropertyProduct();
	Menu menu = new Menu();
	Product product = new Product();
	private Main main;
	public CouponClick(Main main) 
	{
		this.main = main;
	}
	
	public void onClick(InventoryClickEvent e, User user)
	{
		Player player = (Player) e.getWhoClicked();
		UUID uuid = player.getUniqueId();

		e.setCancelled(true);
		Integer propertyID = worldguard.getStructureIDbyRegion("property", player.getLocation(), worldguard.getRegionManager(player.getWorld()));
		if (propertyID != null)
		{
			Integer productID = product.getProductIDbyDisplayName(e.getInventory().getItem(4).getItemMeta().getDisplayName(), false);
			if (productID != null)
			{
				Integer relationID = proproduct.getRelationID(productID, propertyID);
				if (relationID != null)
				{
					String dc = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
					if (dc.equalsIgnoreCase("buy with coupon"))
					{
						ItemStack coupon = getCouponfromMenu(e.getInventory());
						if (main.debug)
						{
							Bukkit.getConsoleSender().sendMessage("Coupon amount: " + coupon.getAmount());
						}
						Integer amount = coupon.getAmount();
						coupon.setAmount((amount-1));
						player.getInventory().addItem(coupon);
						player.updateInventory();
						Bukkit.getServer().getPluginManager().callEvent(new PurchaseEvent(user, product.createPropertyItem(productID, 1, false, false), relationID, true));
					} else if (dc.contains("This item costs: "))
					{
						Integer availableAmount = proproduct.getAmount(relationID);
						if (availableAmount >= 1)
						{
							Integer coins = user.getCoins();
							Integer price = proproduct.getPrice(relationID);
							if (coins >= price)
							{
								Bukkit.getServer().getPluginManager().callEvent(new PurchaseEvent(user, product.createPropertyItem(productID, 1, false, false), relationID, false));
							} else
							{
								player.sendMessage(ColorOptions.error + "You don't have enough coins to buy this item. You need " + (price-coins) + " more coins");
							}
						} else
						{
							player.sendMessage(ColorOptions.error + "This item is out of stock, please come back tomorrow!");
						}
					} else if (dc.equalsIgnoreCase("cancel"))
					{
						ItemStack coupon = getCouponfromMenu(e.getInventory());
						player.getInventory().addItem(coupon);
						ItemFrameAdd.couponMenu.remove(player.getUniqueId());
						this.proproduct.openItemInfo(player, relationID);
					} else if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("No clicked name match!");
					}
				} else if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("No relationID found!");
				}
			} else if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("No productID found!");
			}
		} else if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("No propertyID found!");
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e)
	{
		if (e.getPlayer() instanceof Player)
		{
			Player player = (Player) e.getPlayer();
			Inventory menu = e.getInventory();
			String menuname = ChatColor.stripColor(menu.getName());
			if (menuname.contains("Use ") && menuname.contains("Item Coupon"))
			{
				if (ItemFrameAdd.couponMenu.containsKey(player.getUniqueId()))
				{
					player.getInventory().addItem(getCouponfromMenu(menu));
					player.updateInventory();
				}
			}
		}
	}
	
	public ItemStack getCouponfromMenu(Inventory couponMenu)
	{
		ItemStack coupon = couponMenu.getItem(2);
		
		ItemMeta meta = coupon.getItemMeta();
		List<String> lore = meta.getLore();
		lore.remove(" ");
		lore.remove(ColorOptions.error + "Coupon from your inventory");
		meta.setLore(lore);
		coupon.setItemMeta(meta);
		
		return coupon;
	}
}
