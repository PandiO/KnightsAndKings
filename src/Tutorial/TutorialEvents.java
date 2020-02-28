package Tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.PurchaseEvent;
import Main.Main;
import Menu.Menu;
import Products.Product;
import Products.ProductCategory;
import Products.PropertyProduct;
import Users.User;
import Users.Users;

@SuppressWarnings("deprecation")
public class TutorialEvents implements Listener
{
	public static List<Tutorial> tutorials = new ArrayList<Tutorial>();
	public static List<Tutorial> introTutorials = new ArrayList<Tutorial>();
	
	Product product = new Product();
	Menu menu = new Menu();
	ProductCategory productCategory = new ProductCategory();
	PropertyProduct proProduct = new PropertyProduct();
	private Main main;
	public TutorialEvents(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onChat(PlayerChatEvent e)
	{
		Player player = e.getPlayer();
//		if (tutorials.isEmpty() && main.debug)
//		{
//			Bukkit.getConsoleSender().sendMessage("Tutorials is empty!");
//		}
//		if (introTutorials.isEmpty() && main.debug)
//		{
//			Bukkit.getConsoleSender().sendMessage("Intro Tutorials is empty!");
//		}
		Tutorial introTutorial = getIntroTutorial(player);
		Tutorial tut = getTutorial(player);
		if (introTutorial != null && tut == null)
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Intro Tutorial target detected: " + player.getName());
			}
			e.setCancelled(true);
			introTutorial.TryNext(e.getMessage());
			e.getRecipients().remove(introTutorial.target);
		}
		if (tut != null)
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Tutorial target detected: " + player.getName());
			}
			e.setCancelled(true);
			tut.TryNext(e.getMessage());
			e.getRecipients().remove(tut.target);
		}

	}
	
	public void onClick(InventoryClickEvent e, User user)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player player = (Player) e.getWhoClicked();
			Tutorial tut = getTutorial(player);
			Tutorial intro = getIntroTutorial(player);
			
			e.setCancelled(true);
			ItemStack clicked = e.getCurrentItem();
			if (clicked != null && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName())
			{
				String display = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
				if (display.equalsIgnoreCase("start tutorial"))
				{
					if (intro != null && tut == null)
					{
						player.closeInventory();
						intro.TryNext("yes");
					} 
					if (tut != null)
					{
						player.closeInventory();
						tut.TryNext("yes");
					}
				} else if (display.equalsIgnoreCase("skip tutorial"))
				{
					if (intro != null && tut == null)
					{
						player.closeInventory();
						intro.TryNext("cancel");
					} 
					if (tut != null)
					{
						player.closeInventory();
						tut.TryNext("cancel");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e)
	{
		Player player = e.getPlayer();
		Tutorial tutorial = getTutorial(player);
		Tutorial intro = getIntroTutorial(player);
		if (tutorial != null || intro != null)
		{
			e.setCancelled(true);
			if (e.getMessage().equalsIgnoreCase("/menu"))
			{
				if (tutorial.tutorialName.equalsIgnoreCase("room") || tutorial.tutorialName.equalsIgnoreCase("skills"))
				{
					tutorial.nextStage(null, null);
				}
			} else if (e.getMessage().equalsIgnoreCase("/next") || e.getMessage().equalsIgnoreCase("/yes") || e.getMessage().equalsIgnoreCase("/cancel"))
			{
				if (intro != null && tutorial == null)
				{
					intro.TryNext(e.getMessage().split("/")[1]);
				} 
				if (tutorial != null)
				{
					tutorial.TryNext(e.getMessage().split("/")[1]);
				}
			} else
			{
				notAllowed(player);
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		Tutorial tutorial = getTutorial(player);
		Tutorial intro = getIntroTutorial(player);
		if (tutorial != null || intro != null)
		{
			if (tutorial == null)
			{
				tutorial = intro;
//				if (main.debug)
//				{
//					Bukkit.getConsoleSender().sendMessage("Tutorial is null");
//				}
			} else
			{
				Bukkit.getConsoleSender().sendMessage("Intro is null");
			}
			if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ())
			{
				if (tutorial.stage != null)
				{
					if (tutorial.getName().equalsIgnoreCase("armor tutorial") && tutorial.stage >= 4)
					{
						
					} else if (tutorial.getName().equalsIgnoreCase("food tutorial") && tutorial.stage >= 4)
					{
					} else
					{
//						if (main.debug)
//						{
//							Bukkit.getConsoleSender().sendMessage("Found tutorial");
//						}
						player.teleport(tutorial.targetLoc);
						notAllowed(player);
					}
				} else
				{
//					if (main.debug)
//					{
//						Bukkit.getConsoleSender().sendMessage("Found tutorial");
//					}
					player.teleport(tutorial.targetLoc);
					notAllowed(player);
				}
			}
		}
	}
	
	@EventHandler
	public void onOpen(InventoryOpenEvent e)
	{
		if (e.getPlayer() instanceof Player)
		{
			Player player = (Player) e.getPlayer();
			UUID uuid = player.getUniqueId();
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			}
			Tutorial tutorial = getTutorial(player);
			Inventory menu = e.getInventory();
			String menuname = ChatColor.stripColor(menu.getName());
			if (tutorial != null)
			{
				if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("Inventory opening in tutorial detected!");
				}
				if (tutorial.tutorialName.equalsIgnoreCase("food"))
				{
					if (product.getProductID("bread", false) != null)
					{
						if (ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("info about " + ChatColor.stripColor(product.getDisplayName(product.getProductID("bread", false), false))))
						{
							if (main.debug)
							{
								Bukkit.getConsoleSender().sendMessage("Corresponding name found!");
							}
//							menu.setMenuItemBlink(Bukkit.getConsoleSender(), player, e.getInventory(), 3, 15);
						} else if (menuname.contains("Product list of "))
						{
							this.menu.setMenuItemBlink(Bukkit.getConsoleSender(), user, menu, 9, tutorial.subjectInterval);
						} else
						{
							if (main.debug)
							{
								Bukkit.getConsoleSender().sendMessage("No corresponding name found: " + e.getInventory().getName().toString());
							}
						}
					}
				} else if (tutorial.tutorialName.equalsIgnoreCase("armor"))
				{
					if (menuname.contains("Info about Leather "))
					{
						this.menu.setMenuItemBlink(Bukkit.getConsoleSender(), user, menu, 5, tutorial.subjectInterval);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPurchase(PurchaseEvent e)
	{
		User user = e.getUser();
		Integer relationID = e.getRelationID();
		if (main.debug)
		{
			if (relationID != null)
			{
				Bukkit.getConsoleSender().sendMessage("RelatationID: " + relationID);
			} else
			{
				Bukkit.getConsoleSender().sendMessage("RelationID is not found!");
			}
		}
		Integer productID = proProduct.getProductID(e.getRelationID());
		Integer propertyID = proProduct.getPropertyID(e.getRelationID());
		Tutorial tutorial = getTutorial(user.getPlayer());
		if (tutorial != null)
		{
			if (propertyID == 22)
			{
				if (productCategory.getCategoryName(product.getCategoryID(productID ,false)).equalsIgnoreCase("armor"))
				{
					tutorial.nextStage(productID, e.getItem());
				}
			} else if (propertyID == 19)
			{
				if (productCategory.getCategoryName(product.getCategoryID(productID, false)).equalsIgnoreCase("baked-goods"))
				{
					tutorial.nextStage(productID, e.getItem());
				}
			}
			proProduct.saveAmount(relationID, (e.getItem().getAmount() + proProduct.getAmount(relationID)));
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		Tutorial intro = getIntroTutorial(player);
		Tutorial tutorial = getTutorial(player);
		if (intro != null)
		{
			intro.cancel(null);
		} else if (tutorial != null)
		{
			tutorial.cancel(null);
		}
	}
	
	public Tutorial getTutorial(Player player)
	{
		Tutorial tutorial = null;
		
		for (Tutorial tut : tutorials)
		{
			if (tut.target == player)
			{
				tutorial = tut;
				break;
			}
		}
		
		return tutorial;
	}
	
	public Tutorial getIntroTutorial(Player player)
	{
		Tutorial tutorial = null;
		
		for (Tutorial tut : introTutorials)
		{
			if (tut.target == player)
			{
				tutorial = tut;
				break;
			}
		}
		
		return tutorial;
	}
	
	public void notAllowed(Player player)
	{
		player.sendMessage(ColorOptions.message + "If you wish to stop the tutorial, please type " + ColorOptions.error + "cancel");
		player.sendMessage(ColorOptions.message + "If you wish to continue the tutorial, please type " + ColorOptions.messagesubjects + "next");
	}
}
