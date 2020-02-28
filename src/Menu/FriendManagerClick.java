package Menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;
import Users.offlineUser;

public class FriendManagerClick implements Listener
{
	offlineUser user = new offlineUser();
	Menu menu = new Menu();
	private Main main;
	public FriendManagerClick(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void Onclick(InventoryClickEvent e)
	{
		ItemStack clicked = e.getCurrentItem();
		Player p = (Player) e.getWhoClicked();
		User user = null;
		
		try
		{
			user = Users.getUser(p.getUniqueId());
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, p, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, p, true);
			return;
		}
		Inventory menu = e.getInventory();
		
		if (ChatColor.stripColor(menu.getName()).contains("Friend request from "))
		{
			if (clicked.hasItemMeta())
			{
				String username = ChatColor.stripColor(menu.getName()).split("from ")[1];
				String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
				e.setCancelled(true);
				if (dc.equalsIgnoreCase("cancel"))
				{
					this.menu.openFriendRequests(user);
				}
				if (dc.equalsIgnoreCase("accept"))
				{
					Bukkit.dispatchCommand(p, "request accept " + username);
					this.menu.openFriendRequests(user);
				}
				if (dc.equalsIgnoreCase("deny"))
				{
					Bukkit.dispatchCommand(p, "request deny " + username);
					this.menu.openFriendRequests(user);
				}
			}
		}
	}
	
	public void ManageFriendsClick(InventoryClickEvent e, User user)
	{
		ItemStack clicked = e.getCurrentItem();
		if (clicked.hasItemMeta())
		{
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
			e.setCancelled(true);
			if (dc.equalsIgnoreCase("back"))
			{
				this.menu.OpenPersonalMenu(user);
			}
			if (dc.equalsIgnoreCase("add new friends"))
			{
				this.menu.openFriendsAdd(user);
				user.playSound("succesclick");
			}
			if (dc.equalsIgnoreCase("you have new friendrequest(s)!"))
			{
				this.menu.openFriendRequests(user);
				user.playSound("succesclick");
			}
		}
	}
	
	public void AddFriendsClick(InventoryClickEvent e, User user)
	{
		ItemStack clicked = e.getCurrentItem();
		if (clicked.hasItemMeta())
		{
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
			e.setCancelled(true);
			if (dc.equalsIgnoreCase("back"))
			{
				this.menu.openFriendsManager(user);
			}
			
			if (this.user.getUUID(dc) != null)
			{
				if (clicked.hasItemMeta())
				{
					List<String> lore = clicked.getItemMeta().getLore();
					String line = lore.get(lore.size()-1);
					if (ChatColor.stripColor(line).equalsIgnoreCase("click here to add!"))
					{
						Bukkit.dispatchCommand(user.getPlayer(), "friends add " + dc);
						this.menu.openFriendsAdd(user);
						user.playSound("succesclick");
					}
				}
			}
		}
	}
	
	public void FriendRequestClick(InventoryClickEvent e, User user)
	{
		ItemStack clicked = e.getCurrentItem();
		if (clicked.hasItemMeta())
		{
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
			e.setCancelled(true);
			if (dc.equalsIgnoreCase("back"))
			{
				this.menu.openFriendsManager(user);
			}
			if (this.user.getUUID(dc) != null)
			{
				this.menu.openFriendRequestOption(user, this.user.getUUID(dc));
			}
		}
	}
}
