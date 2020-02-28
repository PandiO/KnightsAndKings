package Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.Menus;
import Main.Main;
import Products.Product;
import Properties.Property;
import Quests.Quest;
import Quests.QuestDeliverPackage;
import Quests.QuestHarvestResource;
import Users.User;
import Users.Users;

public class QuestMenuClick implements Listener
{
	Product product = new Product();
	Property property = new Property();
	Menu menu = new Menu();
	private Main main;
	public QuestMenuClick(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		ItemStack clicked = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
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
		if (clicked == null || !clicked.hasItemMeta())
		{
			return;
		}
		Inventory menu = e.getInventory();
		String menuname = ChatColor.stripColor(menu.getName());
		
		if (menuname.equalsIgnoreCase("Deliver items for quest"))
		{
			Integer propertyID = Menus.getPropertyIDByInfoItem(menu.getItem(5));
			if (propertyID == null)
			{
				e.setCancelled(true);
				Bukkit.broadcastMessage("Player " + user.getID() + " tried to use a delivery-menu for a property but the PropertyID could not be found:");
				if (!menu.getItem(5).hasItemMeta())
				{
					Bukkit.broadcastMessage("The item that has been analysed for the property information has no itemMeta: contact Developer to search in file src/Menu/QuestMenuClick on line 70-80");
				} else
				{
					Bukkit.broadcastMessage("Displayname of searched menu-item: " + menu.getItem(5).getItemMeta().getDisplayName());
					Bukkit.broadcastMessage("Lore of searched menu-item: " + menu.getItem(5).getItemMeta().getLore());
				}
				player.sendMessage(ColorOptions.error + "Cannot find the property to deliver to, please contact a staff member");
				return;
			}
			this.checkSellMenu(player, menu, e.getRawSlot(), e.getCurrentItem());
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
			
			if (dc.equalsIgnoreCase("back"))
			{
				e.setCancelled(true);
				player.closeInventory();
			}
			if (dc.equalsIgnoreCase("social profile"))
			{
				e.setCancelled(true);
			}
			if (dc.equalsIgnoreCase(" "))
			{
				e.setCancelled(true);
			}
			if (dc.contains("Information about "))
			{
				e.setCancelled(true);
			}
			if (dc.contains("Quest: "))
			{
				e.setCancelled(true);
			}
			if (dc.equalsIgnoreCase("deliver items"))
			{
				e.setCancelled(true);
				
				Integer amountToDeliver = 0;
            	Integer productID = null;
            	
        		Quest quest = Properties.Properties.ActiveQuests.get(propertyID);
            	if (quest instanceof QuestHarvestResource)
            	{
            		QuestHarvestResource rq = (QuestHarvestResource) quest;
            		productID = rq.getProductID();
            	} else if (quest instanceof QuestDeliverPackage)
            	{
            		QuestDeliverPackage  QDP = (QuestDeliverPackage) quest;
            		productID = QDP.getProductID();
            	}
            	
        		for (int i = 19; i < 26; i++)
        		{
        			if (menu.getItem(i) != null && menu.getItem(i).getType() != Material.AIR)
        			{
        				ItemStack item = menu.getItem(i);
        				if (!item.hasItemMeta() || product.getProductIDbyDisplayName(item.getItemMeta().getDisplayName(), false) == null)
        				{
        					player.getInventory().addItem(menu.getItem(i));
    						menu.setItem(i, new ItemStack(Material.AIR, 1));
							player.sendMessage(ColorOptions.error + "This item cannot be delivered!");
        				}
    					ItemMeta itemMeta = menu.getItem(i).getItemMeta();
    					Integer itemID = product.getProductIDbyDisplayName(itemMeta.getDisplayName(), false);
    					if (itemID != productID)
    					{
        					player.getInventory().addItem(menu.getItem(i));
    						menu.setItem(i, new ItemStack(Material.AIR, 1));
							player.sendMessage(ColorOptions.error + "This is not the requested item!");
    					}
    					
    					if (itemID == productID)
						{
							Integer amount = menu.getItem(i).getAmount();
    						amountToDeliver += amount;
    						
    						menu.setItem(i, null);
    						
    		            	if (quest instanceof QuestHarvestResource)
    		            	{
    		            		QuestHarvestResource rq = (QuestHarvestResource) quest;
    		            		rq.Deliver(item);
    		            	} else if (quest instanceof QuestDeliverPackage)
    		            	{
    		            		QuestDeliverPackage QDP = (QuestDeliverPackage) quest;
    		            		QDP.Deliver(item);
    		            	}
    		            	
    		            	if (quest.isCompleted())
    		            	{
    		            		player.closeInventory();
    		            	}
						}
        			}
        		}
    			this.checkSellMenu(player, menu, e.getRawSlot(), e.getCurrentItem());
			}
		}
	}
	
    @EventHandler
    public void onDrag(InventoryDragEvent e)
    {
    	Inventory menu = e.getInventory();
    	String menuname = ChatColor.stripColor(menu.getName());
    	if (menuname.contains("Deliver items for quest"))
    	{
    		Player player = (Player) e.getWhoClicked();
    		for (Integer slot : e.getRawSlots())
    		{
    			this.checkSellMenu(player, menu, slot, e.getNewItems().get(slot));
    		}
    	}
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
    	Inventory menu = e.getInventory();
    	String menuname = ChatColor.stripColor(menu.getName());
    	boolean dropped = false;
    	if (menuname.contains("Deliver items for quest"))
    	{
	    	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
	    	{
	            public void run()
	            {
	            	Player player = (Player) e.getPlayer();
	        		for (int i = 19; i < 26; i++)
	        		{
	        			if (menu.getItem(i) != null && menu.getItem(i).getType() != Material.AIR)
	        			{
	        				if (player.getInventory().firstEmpty() != -1)
	        				{
	        					player.getInventory().addItem(menu.getItem(i));
	        				} else
	        				{
	        					player.getWorld().dropItemNaturally(player.getLocation(), menu.getItem(i));
	        				}
	        			}
	        		}
	            	if (dropped)
	            	{
	        			player.sendMessage(ColorOptions.falsecommand + "No space in your inventory, dropping item(s) on the ground");
	            	}
	            }
	    	}, 10);
    	}
    }
	
    public void checkSellMenu(Player player, Inventory menu, Integer clickSlot, ItemStack dragItem)
    {
		Integer propertyID = Menus.getPropertyIDByInfoItem(menu.getItem(5));
		Quest quest = Properties.Properties.ActiveQuests.get(propertyID);

		if (propertyID != null)
		{
	    	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
	    	{
	            public void run()
	            {
	            	Integer amountToDeliver = 0;
	            	Integer productID = null;
	            	
	            	if (quest instanceof QuestHarvestResource)
	            	{
	            		QuestHarvestResource rq = (QuestHarvestResource) quest;
	            		productID = rq.getProductID();
	            	} else if (quest instanceof QuestDeliverPackage)
	            	{
	            		QuestDeliverPackage  QDP = (QuestDeliverPackage) quest;
	            		productID = QDP.getProductID();
	            	}
	        		for (int i = 19; i < 26; i++)
	        		{
	        			if (menu.getItem(i) != null && menu.getItem(i).getType() != Material.AIR)
	        			{
	        				ItemStack item = menu.getItem(i);
	        				if (!item.hasItemMeta() || product.getProductIDbyDisplayName(item.getItemMeta().getDisplayName(), false) == null)
	        				{
	        					player.getInventory().addItem(menu.getItem(i));
        						menu.setItem(i, new ItemStack(Material.AIR, 1));
    							player.sendMessage(ColorOptions.error + "This item cannot be delivered!");
	        				}
        					ItemMeta itemMeta = menu.getItem(i).getItemMeta();
        					Integer itemID = product.getProductIDbyDisplayName(itemMeta.getDisplayName(), false);
        					if (itemID != productID)
        					{
	        					player.getInventory().addItem(menu.getItem(i));
        						menu.setItem(i, new ItemStack(Material.AIR, 1));
    							player.sendMessage(ColorOptions.error + "This is not the requested item!");
        					}
        					
        					if (itemID == productID)
    						{
        						Bukkit.getConsoleSender().sendMessage("Matching item found! amount: " + menu.getItem(i).getAmount());
    							Integer amount = menu.getItem(i).getAmount();
        						amountToDeliver += amount;
    						}
	        			}
	        		}
	        		Bukkit.getConsoleSender().sendMessage("Total found amount: " + amountToDeliver);
	        		List<String> lore = menu.getItem(3).getItemMeta().getLore();
	        		Bukkit.getConsoleSender().sendMessage(lore.size() + "");
	        		lore.remove(2);
	        		lore.remove(3);
	        		lore.add(ColorOptions.stats + "Total delivered: " + ColorOptions.statsresults + quest.getDeliveredAmount() + "/" + quest.getGoalAmount());
	        		lore.add(ColorOptions.stats + "Current delivery: " + ColorOptions.statsresults + amountToDeliver.toString());
	        		menu.setItem(3, product.setItemDescription(menu.getItem(3), 1, menu.getItem(3).getItemMeta().getDisplayName(), lore));
	            }
	        }, 20);
		}
    }
    
    public void onSell(Integer propertyID, ArrayList<ItemStack> soldItems)
    {
    }
}
