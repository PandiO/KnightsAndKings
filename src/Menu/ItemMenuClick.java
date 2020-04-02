package Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Minigames.Transport;
import Products.Product;
import Products.ProductCategory;
import Products.PropertyProduct;
import Properties.Property;
import Properties.PropertyCategory;
import Quests.Quest;
import Quests.QuestDeliverPackage;
import Streets.Street;
import Towns.Town;
import Users.User;
import Users.Users;
import Users.offlineUser;

public class ItemMenuClick implements Listener
{
	WorldGuard worldguard = new WorldGuard();
	offlineUser user = new offlineUser();
	Product product = new Product();
	Property property = new Property();
	Menu menu = new Menu();
	Street street = new Street();
	Town town = new Town();
	PropertyProduct proproduct = new PropertyProduct();
	ProductCategory productCat = new ProductCategory();
	PropertyCategory propertyCat = new PropertyCategory();
	private Main main;
	public ItemMenuClick(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		ItemStack clicked = e.getCurrentItem();
		Player p = (Player) e.getWhoClicked();
		UUID uuid = p.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
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
		if (clicked == null)
		{
			return;
		}
		Inventory menu = e.getInventory();
		String menuname = ChatColor.stripColor(menu.getName());
		if (menuname.contains("Overview"))
		{
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
			e.setCancelled(true);
			if (dc.equalsIgnoreCase("back"))
			{
				this.menu.openShopItemsManager(user);
			}
			Integer productID = product.getProductID(dc.replace(" ", ""), false);
			if (productID != null)
			{
				this.menu.openPropertyAddItem(p, productID);
			}
		} else if (menuname.contains("Products of "))
		{
			ItemMeta meta = menu.getItem(4).getItemMeta();
			if (meta != null)
			{
				String streetname = ChatColor.stripColor(meta.getLore().get(1).split(": ")[1]);
				Integer streetnumber = Integer.valueOf(ChatColor.stripColor(meta.getLore().get(2).split(": ")[1]));
				String townname = ChatColor.stripColor(meta.getLore().get(3).split(": ")[1]);
				Integer streetID = street.getStreetID(streetname, town.getTownID(townname));
				Integer propertyID = property.getPropertyIDbyLocation(streetID, streetnumber);
				if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("Street: " + streetname + ", town: " + townname + ", ID: " + propertyID);
				}
				if (propertyID != null && propertyID != 0)
				{
					String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
					e.setCancelled(true);
					if (dc.equalsIgnoreCase("exit"))
					{
						p.closeInventory();
					}
					if (dc.contains("Information about "))
					{
						this.menu.openPropertylist(user, 1, null, propertyID);
					}
					if (dc.contains("Asignments"))
					{
						new Transport(user, propertyID);
						p.closeInventory();
					}
					if (dc.contains("Quest"))
					{
						Quest quest = Properties.Properties.ActiveQuests.get(propertyID);
						
						if (quest == null && Properties.Properties.getQuestDeliverPackage(propertyID, null) != null)
						{
							quest = Properties.Properties.getQuestDeliverPackage(propertyID, null);
						}
						
						if (quest.isCollected())
						{
							if (quest instanceof QuestDeliverPackage && ((QuestDeliverPackage) quest).getTargetPropertyID() != propertyID)
							{
								p.sendMessage(ColorOptions.error + "Go to the selected warehouse to deliver the package!");
								return;
							}
							this.menu.openQuestDeliverMenu(user, quest);
						} else if (quest.getUser() != null && quest.getUser() == user)
						{
							p.sendMessage(ColorOptions.error + "You need to harvest/mine the requested items first!");
						} else if (!quest.isAssigned())
						{
							if (quest instanceof QuestDeliverPackage)
							{
								if (p.getInventory().firstEmpty() == -1)
								{
									p.sendMessage(ColorOptions.error + "You need atleast 1 empty slot in your inventory for the package to deliver!");
									return;
								}
								((QuestDeliverPackage)quest).assign(user, -1);
								p.closeInventory();
								return;
							}
							quest.asign(user, -1);
							p.closeInventory();
						} else
						{
							p.sendMessage(ColorOptions.error + "Sorry! This quest is already accepted by someone else");
						}
					}
					if (dc.contains("Sell items to this "))
					{
						this.menu.openSellMenu(user, propertyID);
					}
					for (Integer productID : proproduct.getProductListbyProperty(propertyID))
					{
						if (product.getProductIDbyDisplayName(clicked.getItemMeta().getDisplayName(), false) == productID)
						{
							Integer relationID = proproduct.getRelationID(productID, propertyID);
							if (relationID != null && relationID != 0)
							{
								proproduct.openItemInfo(p, relationID);
							}
						}
					}
				} else
				{
					p.closeInventory();
					p.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Player " + p.getName() + " was able to click a shopkeeper-inventory with no propertyID! " + streetname + ", " + streetnumber + ", " + townname);
				}
			}
		} else if (menuname.contains("Sell items to "))
		{
			this.checkSellMenu(p, menu, e.getRawSlot(), e.getCurrentItem());
			ItemMeta meta = menu.getItem(4).getItemMeta();
			if (meta != null)
			{
				String streetname = ChatColor.stripColor(meta.getLore().get(1).split(": ")[1]);
				Integer streetnumber = Integer.valueOf(ChatColor.stripColor(meta.getLore().get(2).split(": ")[1]));
				String townname = ChatColor.stripColor(meta.getLore().get(3).split(": ")[1]);
				Integer streetID = street.getStreetID(streetname, town.getTownID(townname));
				Integer propertyID = property.getPropertyIDbyLocation(streetID, streetnumber);
				if (propertyID != null && propertyID != 0)
				{
					if (clicked.hasItemMeta())
					{
						String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
						if (dc.equalsIgnoreCase("back"))
						{
							e.setCancelled(true);
							p.closeInventory();
						}
						if (dc.equalsIgnoreCase("financial"))
						{
							e.setCancelled(true);
						}
						if (dc.equalsIgnoreCase(" "))
						{
							e.setCancelled(true);
						}
						if (dc.equalsIgnoreCase("sell items"))
						{
							e.setCancelled(true);
							Integer price = 0;
							ArrayList<ItemStack> soldItems = new ArrayList<ItemStack>();
							for (int i = 19; i < 26; i++)
							{
								if (menu.getItem(i) != null && menu.getItem(i).getType() != Material.AIR)
								{
									if (menu.getItem(i).hasItemMeta())
									{
										ItemMeta itemMeta = menu.getItem(i).getItemMeta();
										Integer productID = product.getProductIDbyDisplayName(itemMeta.getDisplayName(), false);
										if (productID != null)
										{
		        							Integer amount = menu.getItem(i).getAmount();
		        							Integer sellPrice = product.getSellPrice(productID, property.getCategoryID(propertyID));
			        						price = price + (sellPrice*amount);
											soldItems.add(menu.getItem(i));
											menu.setItem(i, null);
										} else
										{
											p.sendMessage(ColorOptions.error + "You wanted to sell items which are not selected!");
											return;
										}
									} else
									{
										p.sendMessage(ColorOptions.error + "You wanted to sell items which are not selected!");
										return;
									}
								}
							}
							try
							{
								onSell(propertyID, soldItems);
							} catch (Exception e2)
							{
								e2.printStackTrace();
							}
							user.addCoins(price);
							p.sendMessage(ColorOptions.messageachievement + "Succesfully sold the items to this shop! You received " + ColorOptions.messageformat + ColorOptions.formatCurrency(price) + ColorOptions.messageachievement + " coins!");
							p.closeInventory();
						}
						if (dc.contains("Information about "))
						{
							e.setCancelled(true);
							this.menu.openPropertylist(user, 1, null, null);
						}
						if (dc.equalsIgnoreCase("sell instructions"))
						{
							e.setCancelled(true);
						}
					}
				} else
				{
					p.closeInventory();
					p.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Player " + p.getName() + " was able to click a shopkeeper-inventory with no propertyID! " + streetname + ", " + streetnumber + ", " + townname);
				}
			}
		}
		if (menuname.contains("Add a ") && menu.getItem(1).hasItemMeta() && menu.getItem(1).getType() == Material.CHEST)
		{
			Integer productID = Integer.valueOf(ChatColor.stripColor(menu.getItem(1).getItemMeta().getDisplayName().split("item")[1]));
			String productCategory = this.productCat.getCategoryName(product.getCategoryID(productID, false));
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
			List<String> lore = clicked.getItemMeta().getLore();
			e.setCancelled(true);
			if (dc.equalsIgnoreCase("back"))
			{
				openShopItem(p, productCategory);
			}
			if (dc.contains("click here to get this item"))
			{
				p.performCommand("product get 64 " + productID);
				p.playSound(p.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);			
			}
			for (Integer i : property.getIDList(null, null))
			{
				if (dc.equalsIgnoreCase("propertyname: " + property.getPropertyName(i)))
				{
					String streetName = ChatColor.stripColor(lore.get(0).split(": ")[1]);
					String streetNumber = ChatColor.stripColor(lore.get(1).split(": ")[1]);
					String townName = ChatColor.stripColor(lore.get(2).split(": ")[1]);
					if (town.getTownID(townName) != null)
					{
						Integer townID = town.getTownID(townName);
						if (street.checkStreet(streetName, townID))
						{
							Integer streetID = street.getStreetID(streetName, townID);
							if (property.getPropertyIDbyLocation(streetID, Integer.valueOf(streetNumber)) != null)
							{
								Integer propertyID = property.getPropertyIDbyLocation(streetID, Integer.valueOf(streetNumber));
								if(!clicked.getItemMeta().getLore().contains("Already selling this item!"))
								{
									if (!proproduct.getProductListbyProperty(propertyID).contains(productID))
									{
										proproduct.savePropertyProduct(propertyID, productID);
										this.menu.openPropertyAddItem(p, productID);
									} else
									{
										p.playSound(p.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
									}
								} else
								{
									p.playSound(p.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
								}
							} else
							{
								p.sendMessage(ColorOptions.error + "No property could be found on street " + streetName + " with number " + streetNumber + " in the town of " + townName);
							}
						} else
						{
							p.sendMessage(ColorOptions.error + "No street in town " + townName + " could be found named " + streetName);
						}
					} else
					{
						p.sendMessage(ColorOptions.error + "No town could be found named " + townName);
					}
				}
			}
		}
//		for (String s : main.getShopItems_ArmorItemNames())
//		{
//			if (menuname.equalsIgnoreCase("add a " + s.toLowerCase()))
//			{
//				String[] splitmenu = menuname.split(" ");
//				String itemName = splitmenu[2];
//				String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
//				e.setCancelled(true);
//				if (dc.equalsIgnoreCase("back"))
//				{
//					openShopItem(p, "armor");
//				}
//				for (Integer i : main.getPropertyIDList())
//				{
//					if (dc.equalsIgnoreCase("propertyname: " + main.getPropertyName(i)))
//					{
//						if(!clicked.getItemMeta().getLore().contains("already selling this item!"))
//						{
//							if (!main.getArmoryItemNameList(i).contains(main.getShopItems_ArmorName(s)))
//							{
//								main.addArmoryItem(i, main.getShopItems_ArmorName(s));
//								main.openPropertyAddItem(p, "armor", itemName);
//							} else
//							{
//								p.playSound(p.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
//							}
//						}
//					}
//				}
//			}
//		}
	}
	
	public void ShopItemsManagerClick(InventoryClickEvent e, User user)
	{
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
		e.setCancelled(true);
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.OpenPersonalMenu(user);
		}
		if (productCat.getCategoryNameList().contains(dc))
		{
			productCat.getCategoryNameList().indexOf(dc);
			openShopItem(user.getPlayer(), dc);
			user.getPlayer().playSound(user.getPlayer().getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
		}
	}
	
	static Map<UUID, Integer> siswordmenuslot = new HashMap<UUID, Integer>();
	static Map<UUID, Integer> siarmormenuslot = new HashMap<UUID, Integer>();
	static Map<UUID, Integer> sibreadmenuslot = new HashMap<UUID, Integer>();
	static Map<UUID, Integer> simeatmenuslot = new HashMap<UUID, Integer>();
	static Map<UUID, Integer> sivegetablesmenuslot = new HashMap<UUID, Integer>();

    public void openShopItem(Player player, String productCategory)
    {
		Integer ItemAmount = product.getIDListbyCategory(productCategory, false).size();
    	UUID uuid = player.getUniqueId();
    	siswordmenuslot.put(uuid, Integer.valueOf(9));
    	if (ItemAmount <= 45)
    	{
    		Inventory simenu = Bukkit.createInventory(null, main.getMenuSize(ItemAmount), ColorOptions.stats + "Overview (" + ColorOptions.statsresults + productCategory + ColorOptions.stats + ")");
    		createProductShopItems(simenu, player, productCategory);
			MenuClick.propertylistmap.put(uuid, simenu);
	    	player.openInventory(simenu);
    	} else
    	{
    		player.sendMessage(ColorOptions.error + "Something went wrong, please notify a developer");
    		System.out.println(ChatColor.RED + "Error on item-amount: Products.ItemMenuClick line: 173-215");
    	}
    }
    
	
	
    public void createProductShopItems(Inventory menu, Player player, String productCategory)
    {
    	Integer productAmount = 0;
    	for (Integer productID : product.getIDList(true, null, true))
    	{
    		if (this.productCat.getCategoryName(product.getCategoryID(productID, false)).equalsIgnoreCase(productCategory))
    		{
    			productAmount++;
    		}
    	}
    	Integer weaponryamount = 0;
    	Integer armoryamount = 0;
    	Integer archeryamount = 0;
    	Integer jeweleryamount = 0;
    	Integer fisheryamount = 0;
    	Integer butcheryamount = 0;
    	Integer bakeryamount = 0;
    	Integer groceryamount = 0;
    	Integer furnitureamount = 0;
    	Integer witcheryamount = 0;
    	Integer warehouseamount = 0;
    	Integer resourceamount = 0;
    	UUID uuid = player.getUniqueId();
    	
    	for (Integer propertyID : property.getIDList(null, null))
    	{
    		Integer categoryID = property.getCategoryID(propertyID);
    		String catName = this.propertyCat.getCategoryName(categoryID);
    		if (catName.equalsIgnoreCase("weaponry"))
    		{
    			weaponryamount = weaponryamount +1;
    		}
    		if (catName.equalsIgnoreCase("armory"))
    		{
    			armoryamount = armoryamount +1;
    		}
    		if (catName.equalsIgnoreCase("archery"))
    		{
    			archeryamount++;
    		}
    		if (catName.equalsIgnoreCase("jewelery"))
    		{
    			jeweleryamount++;
    		}
    		if (catName.equalsIgnoreCase("fishery"))
    		{
    			fisheryamount = fisheryamount +1;
    		}
    		if (catName.equalsIgnoreCase("butchery"))
    		{
    			butcheryamount = butcheryamount +1;
    		}
    		if (catName.equalsIgnoreCase("bakery"))
    		{
    			bakeryamount = bakeryamount +1;
    		}
    		if (catName.equalsIgnoreCase("grocery"))
    		{
    			groceryamount = groceryamount +1;
    		}
    		if (catName.equalsIgnoreCase("furnitury"))
    		{
    			furnitureamount = furnitureamount +1;
    		}
    		if (catName.equalsIgnoreCase("witchery"))
    		{
    			witcheryamount++;
    		}
    		if (catName.equalsIgnoreCase("warehouse"))
    		{
    			warehouseamount++;
    		}
    		if (catName.equalsIgnoreCase("resources"))
    		{
    			resourceamount++;
    		}
    	}
    	menu.setItem(0, MenuCommand.addpmenu(ColorOptions.statsformat + "Property stats", Material.WORKBENCH, 
    			ColorOptions.stats + "Weaponry amount: " + ColorOptions.statsresults + weaponryamount, 
    			ColorOptions.stats + "Armory amount: " + ColorOptions.statsresults + armoryamount, 
    			ColorOptions.stats + "Archery amount: " + ColorOptions.statsresults + archeryamount, 
    			ColorOptions.stats + "Jewelery amount: " + ColorOptions.statsresults + jeweleryamount, 
    			ColorOptions.stats + "Fishery amount: " + ColorOptions.statsresults + fisheryamount, 
    			ColorOptions.stats + "Butchery amount: " + ColorOptions.statsresults + butcheryamount, 
    			ColorOptions.stats + "Bakery amount: " + ColorOptions.statsresults + bakeryamount, 
    			ColorOptions.stats + "Grocery amount: " + ColorOptions.statsresults + groceryamount, 
    			ColorOptions.stats + "Furnitury amount: " + ColorOptions.statsresults + furnitureamount, 
    			ColorOptions.stats + "Witchery amount: " + ColorOptions.statsresults + witcheryamount, 
    			ColorOptions.stats + "Warehouse amount: " + ColorOptions.statsresults + warehouseamount, 
    			ColorOptions.stats + "Resoures amount: " + ColorOptions.statsresults + resourceamount,
    			ChatColor.GRAY + "These are the total of the current world"));
    	menu.setItem(4, MenuCommand.addpmenu(ColorOptions.stats + "Available " + productCategory + ": " + ColorOptions.statsformat + productAmount, product.getShopItemManagerCategoryItem(productCategory)));
    	menu.setItem(8, MenuCommand.addpmenu(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Exit to ShopItems manager"));
    	for (Integer productID : product.getIDListbyCategory(productCategory, false))
    	{
    		menu.setItem(siswordmenuslot.get(uuid), product.addItemDescription(product.createPropertyItem(productID, 1, false, false), Arrays.asList("", ColorOptions.stats + "Minimum price: " + ColorOptions.statsresults + product.getPriceMin(productID), ColorOptions.stats + "Maximum price: " + ColorOptions.statsresults + product.getPriceMax(productID))));
    		siswordmenuslot.put(uuid, siswordmenuslot.get(uuid)+1);
    	}
    }
    
    public void checkSellMenu(Player player, Inventory menu, Integer clickSlot, ItemStack dragItem)
    {
    	PropertyCategory category = new PropertyCategory();
    	ItemMeta meta = menu.getItem(4).getItemMeta();
		String streetname = ChatColor.stripColor(meta.getLore().get(1).split(": ")[1]);
		Integer streetnumber = Integer.valueOf(ChatColor.stripColor(meta.getLore().get(2).split(": ")[1]));
		String townname = ChatColor.stripColor(meta.getLore().get(3).split(": ")[1]);
		Integer streetID = street.getStreetID(streetname, town.getTownID(townname));
		Integer propertyID = property.getPropertyIDbyLocation(streetID, streetnumber);
		if (propertyID != null)
		{
	    	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
	    	{
	            public void run()
	            {
	            	Integer price = 0;
	        		for (int i = 19; i < 26; i++)
	        		{
	        			if (menu.getItem(i) != null && menu.getItem(i).getType() != Material.AIR)
	        			{
	        				if (menu.getItem(i).hasItemMeta())
	        				{
	        					ItemMeta itemMeta = menu.getItem(i).getItemMeta();
	        					Integer productID = product.getProductIDbyDisplayName(itemMeta.getDisplayName(), false);
	        					if (productID != null)
	        					{
	        						if (product.canProductbeSold(productID, propertyID, false))
	        						{
	        							Integer amount = menu.getItem(i).getAmount();
	        							Integer sellPrice = product.getSellPrice(productID, property.getCategoryID(propertyID));
		        						price = price + (sellPrice*amount);
	        						} else
	        						{
	        							player.getInventory().addItem(menu.getItem(i));
		        						menu.setItem(i, new ItemStack(Material.AIR, 1));
	        							player.sendMessage(ColorOptions.error + "You can't sell items of this category to this shop!");
	        						}
	        					} else
	        					{
        							player.getInventory().addItem(menu.getItem(i));
	        						menu.setItem(i, new ItemStack(Material.AIR, 1));
        							player.sendMessage(ColorOptions.error + "You can't sell this item!");
	        					}
	        				}
	        			}
	        		}
	        		List<String> lore = menu.getItem(3).getItemMeta().getLore();
	        		lore.remove(2);
	        		lore.add(ColorOptions.stats + "Price: " + ColorOptions.statsresults + "" + ColorOptions.formatCurrency(price));
	        		menu.setItem(3, product.setItemDescription(menu.getItem(3), 1, menu.getItem(3).getItemMeta().getDisplayName(), lore));
	            }
	        }, 10);
		}
    }
    
    @EventHandler
    public void onDrag(InventoryDragEvent e)
    {
    	Inventory menu = e.getInventory();
    	String menuname = ChatColor.stripColor(menu.getName());
    	if (menuname.contains("Sell items to "))
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
    	if (menuname.contains("Sell items to "))
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
    
    public void onSell(Integer propertyID, ArrayList<ItemStack> soldItems)
    {
    	property.fillWarehouse(propertyID, soldItems);
//    	NPCRegistry registry = CitizensAPI.getNPCRegistry();
//    	NPC shopkeeper = registry.getById(property.getNPCID(propertyID));
//    	World world = shopkeeper.getStoredLocation().getWorld();
//        RegionManager manager = worldguard.getWorldGuard().getGlobalRegionManager().get(shopkeeper.getStoredLocation().getWorld());
//        ProtectedRegion gateRegion = manager.getRegion("property_" + propertyID);
//        
//        if (gateRegion != null)
//        {
//    		CuboidRegion curegion = new CuboidRegion(BukkitUtil.getLocalWorld(world), gateRegion.getMinimumPoint(), gateRegion.getMaximumPoint());
//    		for (BlockVector blockv : curegion) 
//    		{
//    		    Block block = BukkitUtil.toBlock(new BlockWorldVector(BukkitUtil.getLocalWorld(world), blockv));
//    		    if (block.getType() == Material.CHEST)
//    		    {
//    		    	Chest chest = (Chest) block.getState();
//    		    	Inventory chestInv = chest.getBlockInventory();
//    		    	for (ItemStack item : soldItems)
//    		    	{
//        		    	if (chestInv.firstEmpty() != -1)
//        		    	{
//        		    		chestInv.addItem(item);
//        		    	} else
//        		    	{
//        		    		break;
//        		    	}
//    		    	}
//    		    	break;
//    		    }
//    		}
//        }
    }
}

