package Handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import Products.PropertyProduct;
import Properties.ItemFrameAdd;
import Users.User;

public class PurchaseEvent extends KaKEvent
{
	PropertyProduct propertyProduct = new PropertyProduct();
	User user;
	ItemStack item;
	Integer relationID;
	
	public PurchaseEvent(User user, ItemStack item, Integer relationID, boolean coupon)
	{
		super(user, 8);
		Integer price = propertyProduct.getPrice(relationID)*item.getAmount();
		propertyProduct.removeAmount(relationID, item.getAmount());
		user.getPlayer().getInventory().addItem(item);
		if (coupon)
		{
			ItemFrameAdd.couponMenu.remove(user.getUUID());
			user.getPlayer().sendMessage(ColorOptions.messageachievement + "You used a " + ChatColor.DARK_PURPLE + "Item Coupon " + ColorOptions.messageachievement + "to buy " + item.getItemMeta().getDisplayName());
		} else
		{
			user.removeCoins(price);
			user.getPlayer().sendMessage(ColorOptions.messageachievement + "You succesfully bought " + item.getItemMeta().getDisplayName() + ColorOptions.messageachievement + " for " + ColorOptions.messagesubjects + price + " coins!");
		}
		user.getPlayer().closeInventory();
		user.getPlayer().playSound(user.getPlayer().getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 2.0F);
		
		this.user = user;
		this.item = item;
		this.relationID = relationID;
		Bukkit.getConsoleSender().sendMessage("Event fired");
	}
	
	public User getUser()
	{
		return this.user;
	}
	public ItemStack getItem()
	{
		return item;
	}
	public Integer getRelationID()
	{
		return relationID;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
