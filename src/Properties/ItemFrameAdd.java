package Properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldEdit;
import DataManager.Worldguard;
import Exceptions.UserNotFoundException;
import Genders.Gender;
import Handlers.ClickShopItemEvent;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.PurchaseEvent;
import Handlers.SoundHandler;
import Main.Main;
import Menu.Menu;
import Menu.MenuCommand;
import Products.EnchantbookClick;
import Products.Product;
import Products.ProductCategory;
import Products.PropertyProduct;
import Titles.Title;
import Users.User;
import Users.Users;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class ItemFrameAdd implements Listener
{
	Property property = new Property();
	WorldEdit worldedit = new WorldEdit();
	PropertyProduct propertyproduct = new PropertyProduct();
	Menu menu = new Menu();
	Product product = new Product();
	Title title = new Title();
	Gender gender = new Gender();
	ProductCategory productCat = new ProductCategory();
	Products.Enchantment enchantment = new Products.Enchantment();
	private Main main;
	public ItemFrameAdd(Main main) 
	{
		this.main = main;
	}
	
	HashMap<UUID, Location> framelocations = new HashMap<UUID, Location>();
	HashMap<UUID, ItemStack> enchantItem = new HashMap<UUID, ItemStack>();
	public static HashMap<UUID, Inventory> couponMenu = new HashMap<UUID, Inventory>();
	
	@EventHandler
	public void Add(PlayerInteractEntityEvent e)
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
		if (e.getRightClicked() instanceof ItemFrame)
		{
			ItemFrame itemframe = (ItemFrame) e.getRightClicked();
			Location frameloc = itemframe.getLocation();
			RegionManager regionmanager = Worldguard.getWorldGuard().getRegionManager(frameloc.getWorld());
			ApplicableRegionSet regionset = regionmanager.getApplicableRegions(frameloc);
			if (Worldguard.getStructureIDbyRegion("property", frameloc, regionmanager) != null && Worldguard.getStructureIDbyRegion("property", player.getLocation(), regionmanager) != null)
			{
				Integer propertyID = Worldguard.getStructureIDbyRegion("property", frameloc, regionmanager);
				if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
				{
					
				} else
				{
					e.setCancelled(true);
					ItemStack frameitem = itemframe.getItem();
					if (frameitem != null && frameitem.getType() != Material.AIR)
					{
						framelocations.put(uuid, frameloc);
						if (frameitem.getType() == Material.ENCHANTED_BOOK || frameitem.getType() == Material.BOOK)
						{
							String enchantName = ChatColor.stripColor(frameitem.getItemMeta().getDisplayName());
							if (enchantment.getEnchantmentID(enchantName) != null)
							{
								Integer enchantmentID = enchantment.getEnchantmentID(enchantName);
								if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR)
								{
									ItemStack item = player.getItemInHand();
									if (item.hasItemMeta())
									{
										if (product.getProductIDbyDisplayName(item.getItemMeta().getDisplayName(), false) != null)
										{
											if (product.soulbound(item) == false)
											{
												EnchantbookClick book = new EnchantbookClick(main);
												Integer productID = product.getProductIDbyDisplayName(item.getItemMeta().getDisplayName(), false);
												Integer enchantLevels = book.canEnchant(item, productID, enchantmentID);
												if (enchantLevels >= 1)
												{
													openEnchantInfo(user, enchantName, propertyID, productID);
													player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 2.0F);
													enchantItem.put(uuid, player.getItemInHand());
												} else
												{
													player.sendMessage(ColorOptions.falsecommand + "This item reached the max. level for this enchantment");
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "Soulbound items can't be enchanted!");
											}
										} else
										{
											player.sendMessage(ColorOptions.falsecommand + "This item cannot be enchanted");
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "This item cannot be enchanted");
									}
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "You need to have an item in your hands");
								}
							} else
							{
								player.sendMessage(ColorOptions.message + "This enchantment is not available at the moment");
							}
							
						} else
						{
							String itemName = ChatColor.stripColor(frameitem.getItemMeta().getDisplayName().replaceAll(" ", ""));
							if (product.getProductID(itemName, false) != null)
							{
								Integer productID = product.getProductID(itemName, false);
								if (propertyproduct.getPropertyListbyProduct(productID).contains(propertyID))
								{
									Material itemMaterial = frameitem.getType();
									Integer relationID = propertyproduct.getRelationID(productID, propertyID);
									Bukkit.getServer().getPluginManager().callEvent(new ClickShopItemEvent(user, relationID, property.getNPCID(propertyID)));
									this.propertyproduct.openItemInfo(player, relationID);
									player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 2.0F);
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "I'm sorry " + gender.getPrefix(user.getGenderID()) + ", this item has been sold out");
								}
							}
						}
					}
				}
			} else
			{
				if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
				{
					
				} else
				{
					e.setCancelled(true);
				}
			}
		} else
		if (e.getRightClicked().getType() == EntityType.ARMOR_STAND)
		{
			if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
			{
			} else
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void ArmorStandInterAct(PlayerInteractAtEntityEvent e)
	{
		EntityType type = e.getRightClicked().getType();
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if (type == EntityType.ARMOR_STAND)
		{
			if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
			{
			} else
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void menuClose(InventoryCloseEvent e)
	{
		UUID uuid = e.getPlayer().getUniqueId();
	}
	
//	@EventHandler
//	public void DamageFrame(EntityDamageByEntityEvent e)
//	{
//		if (e.getDamager() instanceof Player && e.getEntity() instanceof ItemFrame)
//		{
//			Player player = (Player) e.getDamager();
//			UUID uuid = player.getUniqueId();
//			Entity itemframe = e.getEntity();
//			Location frameloc = itemframe.getLocation();
//			RegionManager regionmanager = Worldguard.getWorldGuard().getRegionManager(frameloc.getWorld());
//			ApplicableRegionSet regionset = regionmanager.getApplicableRegions(frameloc);
//			for (final ProtectedRegion r : regionset)
//		    {
//				if (r.getId().contains("property"))
//				{
//					if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
//					{
//						
//					} else
//					{
//						e.setCancelled(true);
//					}
//				}
//		    }
//		} else if (e.getDamager() instanceof Player && e.getEntity() instanceof ArmorStand)
//		{
//			Player player = (Player) e.getDamager();
//			UUID uuid = player.getUniqueId();
//			if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
//			{
//				
//			} else
//			{
//				e.setCancelled(true);
//			}
//		}
//	}
	
	@EventHandler
	public void InfoClick(InventoryClickEvent e)
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
		Inventory menu = e.getInventory();
		String menuname = ChatColor.stripColor(menu.getName());
		Integer propertyID = Worldguard.getStructureIDbyRegion("property", player.getLocation(), Worldguard.getRegionManager(player.getWorld()));
		for (Integer productID : product.getIDList(true, null, false))
		{
			String s = ChatColor.stripColor(product.getDisplayName(productID, false));
			if (menuname.equalsIgnoreCase(s))
			{
				if (propertyID != null)
				{
					e.setCancelled(true);
					if (clicked != null && clicked.hasItemMeta())
					{
						String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
						if (dc.equalsIgnoreCase("exit"))
						{
							player.closeInventory();
						}
						if (clicked.getType() == Material.NAME_TAG)
						{
							Integer relationID = propertyproduct.getRelationID(productID, propertyID);
							Integer coins = user.getCoins();
							Integer amount = Integer.valueOf(menu.getItem(3).getItemMeta().getDisplayName().split(": ")[1]);
							if (main.debug)
							{
								Bukkit.getConsoleSender().sendMessage("Amount: " + amount);
							}
							Integer price = propertyproduct.getPrice(relationID)*amount;
							if (this.menu.getItemCoupon(player, product.getCategoryID(productID, false)) != null)
							{
								this.menu.openItemCoupon(user, relationID, menu);
							} else
							{
								if (coins >= price)
								{
									Integer availableAmount = propertyproduct.getAmount(relationID);
									if (availableAmount >= amount)
									{
										ItemStack item = product.createPropertyItem(productID, amount, false, false);
										Bukkit.getServer().getPluginManager().callEvent(new PurchaseEvent(user, item, relationID, false));
									} else
									{
										if (availableAmount == 0)
										{
											player.sendMessage(ColorOptions.error + "This item is out of stock, please come back tomorrow!");
										} else
										{
											player.sendMessage(ColorOptions.error + "The amount of items you want is not in stock, please decrease the amount");
										}
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "You don't have enough coins to buy this item. You need " + (price-coins) + " more coins");
								}
							}
						}
						if (clicked.getType() == Material.CHEST)
						{
							this.setItemStep(user, menu, clicked.getItemMeta().getDisplayName(), propertyID, productID);
						}
					}
				} else
				{
					player.closeInventory();
					player.sendMessage(ColorOptions.error + "You need to stand in a property to use this menu!");
				}
			}
		}
		for (Integer enchantmentID : this.enchantment.getIDList())
		{
			String enchantmentName = this.enchantment.getEnchantmentName(enchantmentID);
			if (menuname.equalsIgnoreCase("Enchant item with " + enchantmentName))
			{
				if (propertyID != null)
				{
					e.setCancelled(true);
					String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
					if (dc.equalsIgnoreCase("exit"))
					{
						player.closeInventory();
					}
					if (clicked.getType() == Material.NETHER_STAR)
					{
						List<String> lore = clicked.getItemMeta().getLore();
						Integer coins = user.getCoins();
						Enchantment enchantment = product.getEnchantmentfromString(enchantmentName);
						if (enchantItem.get(uuid).containsEnchantment(enchantment))
						{
							if (enchantItem.get(uuid).getEnchantmentLevel(enchantment) < this.enchantment.getEnchantmentMaxLevel(enchantmentID))
							{
								
							} else 
							{
								player.sendMessage(ColorOptions.falsecommand + "This enchantment has reached the max. level");
								break;
							}
						}
						if (dc.equalsIgnoreCase("default chance"))
						{
							String[] splitChance =  ChatColor.stripColor(lore.get(0)).split(" ");
							String[] splitPrice = ChatColor.stripColor(lore.get(1)).split(" ");
							Integer minChance = Integer.valueOf(splitChance[3].replace("%", ""));
							Integer maxChance = Integer.valueOf(splitChance[5].replace("%", ""));
							Integer price = Integer.valueOf(splitPrice[1]);
							if (coins >= price)
							{
								player.closeInventory();
								playEnchantAnimation(player);
								addEnchantItem(user, enchantment, price, minChance, maxChance, false);
							}
						} else
						if (dc.equalsIgnoreCase("slightly increased chance"))
						{
							String[] splitChance =  ChatColor.stripColor(lore.get(0)).split(" ");
							String[] splitPrice = ChatColor.stripColor(lore.get(1)).split(" ");
							Integer minChance = Integer.valueOf(splitChance[1].replace("%", ""));
							Integer price = Integer.valueOf(splitPrice[1]);
							if (coins >= price)
							{
								player.closeInventory();
								playEnchantAnimation(player);
								addEnchantItem(user, enchantment, price, minChance, minChance, false);
							}
						} else
						if (dc.equalsIgnoreCase("increased chance"))
						{
							String[] splitChance =  ChatColor.stripColor(lore.get(0)).split(" ");
							String[] splitPrice = ChatColor.stripColor(lore.get(1)).split(" ");
							Integer minChance = Integer.valueOf(splitChance[1].replace("%", ""));
							Integer price = Integer.valueOf(splitPrice[1]);
							if (coins >= price)
							{
								player.closeInventory();
								playEnchantAnimation(player);
								addEnchantItem(user, enchantment, price, minChance, minChance, false);
							}
						} else
						if (dc.equalsIgnoreCase("extremely increased chance"))
						{
							String[] splitChance =  ChatColor.stripColor(lore.get(0)).split(" ");
							String[] splitPrice = ChatColor.stripColor(lore.get(1)).split(" ");
							Integer minChance = Integer.valueOf(splitChance[1].replace("%", ""));
							Integer price = Integer.valueOf(splitPrice[1]);
							if (coins >= price)
							{
								player.closeInventory();
								playEnchantAnimation(player);
								addEnchantItem(user, enchantment, price, minChance, minChance, false);
							}
						} else
						if (dc.equalsIgnoreCase("super extreme chance"))
						{
							String[] splitChance =  ChatColor.stripColor(lore.get(0)).split(" ");
							String[] splitPrice = ChatColor.stripColor(lore.get(3)).split(": ");
							Integer minChance = Integer.valueOf(splitChance[1].replace("%", ""));
							Integer price = Integer.valueOf(splitPrice[1]);
							if (coins >= price)
							{
								player.closeInventory();
								playEnchantAnimation(player);
								addEnchantItem(user, enchantment, price, minChance, minChance, true);
							}
						}
					}
				} else
				{
					player.closeInventory();
					player.sendMessage(ColorOptions.error + "You need to stand in a property to use this menu!");
				}
			}
		}
	}
	//Amount
	//price
	//displayname
	//material
	//lore
	//description
	//knowledge
	
	public void openEnchantInfo(User user, String enchantmentName, Integer propertyID, Integer productID)
	{
		ItemStack item = user.getPlayer().getItemInHand();
		if (item.hasItemMeta() && product.getProductIDbyDisplayName(item.getItemMeta().getDisplayName(), false) != null)
		{
			if (product.getEnchantmentfromString(enchantmentName) != null)
			{
				UUID uuid = user.getPlayer().getUniqueId();
				Enchantment enchantment = product.getEnchantmentfromString(enchantmentName);
				Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
				Integer contribution = property.getContribution(propertyID);
				Integer enchantmentID = this.enchantment.getEnchantmentID(enchantmentName);
				Integer maxsuccesChance = 100;
				Integer price = this.enchantment.getEnchantmentPrice(enchantmentID);
				enchantments.putAll(user.getPlayer().getItemInHand().getEnchantments());
				Integer coins = user.getCoins();
				switch (contribution)
				{
				case 5:
					maxsuccesChance = Integer.valueOf((int) (100 - (100 * (main.contributionExponent * 5))));
					break;
				case 10:
					maxsuccesChance = Integer.valueOf((int) (100 - (100 * (main.contributionExponent * 4))));
					break;
				case 15:
					maxsuccesChance = Integer.valueOf((int) (100 - (100 * (main.contributionExponent * 3))));
					break;
				case 20:
					maxsuccesChance = Integer.valueOf((int) (100 - (100 * (main.contributionExponent * 2))));
					break;
				case 30:
					maxsuccesChance = Integer.valueOf((int) (100 - (100 * (main.contributionExponent * 0))));
					break;
				}
				
				Integer option1lowest = maxsuccesChance/10;
				Integer option1highest = 7*(maxsuccesChance/10);
				Integer option2 = 3*(maxsuccesChance/10);
				Integer option3 = maxsuccesChance/2;
				Integer option4 = 4*(maxsuccesChance/5);
				Integer price5 = (int) ((int) price+(price*0.8));
				Integer price4 = (int) ((int) price+(price*0.5));
				Integer price3 = (int) ((int) price+(price*0.3));
				Integer price2 = (int) ((int) price+(price*0.1));

				
				Inventory info = Bukkit.createInventory(null, 9*3, ColorOptions.stats + "Enchant item with " + ColorOptions.statsresults + enchantmentName);

				info.setItem(0, MenuCommand.addpmenu(ColorOptions.stats + "Finacial", Material.GOLD_INGOT, ColorOptions.stats + "Coins: " + ColorOptions.coinStats + user.getCoins(), ColorOptions.stats + "Gems: " + ColorOptions.gemStats + user.getGems(), ColorOptions.stats + "Salary: " + ColorOptions.statsresults + user.getSalary(), ColorOptions.stats + "Income: " + ColorOptions.statsresults + user.getIncome()));
		    	if (item.containsEnchantment(enchantment))
		    	{
		    		info.setItem(4, MenuCommand.addpmenu(enchantmentName, Material.ENCHANTED_BOOK, ChatColor.GRAY + "Increase the level of", ChatColor.GRAY + "this enchantment by 1", ChatColor.RED + "Maximum chance of succes is: " + maxsuccesChance));
		    		enchantments.put(enchantment, item.getEnchantmentLevel(enchantment)+1);
		    	} else
		    	{
		    		info.setItem(4, MenuCommand.addpmenu(enchantmentName, Material.ENCHANTED_BOOK, ChatColor.GRAY + "Add this enchantment to", ChatColor.GRAY + "your current item", ChatColor.RED + "Maximum chance of succes is: " + maxsuccesChance));
		    		enchantments.put(enchantment, 1);
		    	}
		    	info.setItem(9, item);
		    	info.setItem(13, MenuCommand.addpmenu(ChatColor.GREEN + "<Left is your current item", Material.ENCHANTMENT_TABLE, ChatColor.GREEN + "Right is your future item>"));  	
		    	info.setItem(17, product.createEnchantItem(productID, enchantments, 1, false));
	    		info.setItem(20, MenuCommand.addpmenu(ChatColor.GRAY + "Default chance", Material.NETHER_STAR, ChatColor.GRAY + "A chance between " + option1lowest + "% and " + option1highest + "% of succes", ColorOptions.statsformat + "Price: " + price, ChatColor.RED + "You need " + (price-coins) + " more coins"));
		    	info.setItem(21, MenuCommand.addpmenu(ChatColor.DARK_BLUE + "Slightly increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option2 + "% chance of success", ColorOptions.statsformat + "Price: " + price2, ChatColor.RED + "You need " + (price2-coins) + " more coins"));
		    	info.setItem(22, MenuCommand.addpmenu(ChatColor.BLUE + "Increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option3 + "% chance of success", ColorOptions.statsformat + "Price: " + price3, ChatColor.RED + "You need " + (price3-coins) + " more coins"));
		    	info.setItem(23, MenuCommand.addpmenu(ChatColor.AQUA + "Extremely increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option4 + "% chance of success", ColorOptions.statsformat + "Price: " + price4, ChatColor.RED + "You need " + (price4-coins) + " more coins"));
		    	info.setItem(24, MenuCommand.addpmenu(ChatColor.GREEN + "Super extreme chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option4 + "% chance of succes", ColorOptions.statsformat + "Price: " + price5, ChatColor.GREEN + "Additionally, a chance of +2 levels", ChatColor.GREEN + "instead of +1", ChatColor.RED + "You need " + (price5-coins) + " more coins"));
		    	if (coins >= price5)
		    	{
		    		info.setItem(20, MenuCommand.addpmenu(ChatColor.GRAY + "Default chance", Material.NETHER_STAR, ChatColor.GRAY + "A chance between " + option1lowest + "% and " + option1highest + "% of succes", ColorOptions.statsformat + "Price: " + price, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(21, MenuCommand.addpmenu(ChatColor.DARK_BLUE + "Slightly increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option2 + "% chance of success", ColorOptions.statsformat + "Price: " + price2, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(22, MenuCommand.addpmenu(ChatColor.BLUE + "Increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option3 + "% chance of success", ColorOptions.statsformat + "Price: " + price3, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(23, MenuCommand.addpmenu(ChatColor.AQUA + "Extremely increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option4 + "% chance of success", ColorOptions.statsformat + "Price: " + price4, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(24, MenuCommand.addpmenu(ChatColor.GREEN + "Super extreme chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option4 + "% chance of succes", ChatColor.GREEN + "Additionally, a chance of +2 levels", ChatColor.GREEN + "instead of +1", ColorOptions.statsformat + "Price: " + price5, ChatColor.GREEN + "Click here to buy"));
		    	}
		    	if (coins >= price4)
		    	{
		    		info.setItem(20, MenuCommand.addpmenu(ChatColor.GRAY + "Default chance", Material.NETHER_STAR, ChatColor.GRAY + "A chance between " + option1lowest + "% and " + option1highest + "% of succes", ColorOptions.statsformat + "Price: " + price, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(21, MenuCommand.addpmenu(ChatColor.DARK_BLUE + "Slightly increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option2 + "% chance of success", ColorOptions.statsformat + "Price: " + price2, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(22, MenuCommand.addpmenu(ChatColor.BLUE + "Increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option3 + "% chance of success", ColorOptions.statsformat + "Price: " + price3, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(23, MenuCommand.addpmenu(ChatColor.AQUA + "Extremely increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option4 + "% chance of success", ColorOptions.statsformat + "Price: " + price4, ChatColor.GREEN + "Click here to buy"));
		    	}
		    	if (coins >= price3)
		    	{
		    		info.setItem(20, MenuCommand.addpmenu(ChatColor.GRAY + "Default chance", Material.NETHER_STAR, ChatColor.GRAY + "A chance between " + option1lowest + "% and " + option1highest + "% of succes", ColorOptions.statsformat + "Price: " + price, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(21, MenuCommand.addpmenu(ChatColor.DARK_BLUE + "Slightly increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option2 + "% chance of success", ColorOptions.statsformat + "Price: " + price2, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(22, MenuCommand.addpmenu(ChatColor.BLUE + "Increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option3 + "% chance of success", ColorOptions.statsformat + "Price: " + price3, ChatColor.GREEN + "Click here to buy"));
		    	}
		    	if (coins >= price2)
		    	{
		    		info.setItem(20, MenuCommand.addpmenu(ChatColor.GRAY + "Default chance", Material.NETHER_STAR, ChatColor.GRAY + "A chance between " + option1lowest + "% and " + option1highest + "% of succes", ColorOptions.statsformat + "Price: " + price, ChatColor.GREEN + "Click here to buy"));
			    	info.setItem(21, MenuCommand.addpmenu(ChatColor.DARK_BLUE + "Slightly increased chance", Material.NETHER_STAR, ChatColor.GRAY + "A " + option2 + "% chance of success", ColorOptions.statsformat + "Price: " + price2, ChatColor.GREEN + "Click here to buy"));
		    	}
		    	if (coins >= price)
		    	{
		    		info.setItem(20, MenuCommand.addpmenu(ChatColor.GRAY + "Default chance", Material.NETHER_STAR, ChatColor.GRAY + "A chance between " + option1lowest + "% and " + option1highest + "% of succes", ColorOptions.statsformat + "Price: " + price, ChatColor.GREEN + "Click here to buy"));
			    }
				info.setItem(7, product.createItem(ColorOptions.stats + "Knowledge required for this enchantment: " + ColorOptions.statsresults + "/", new ItemStack(Material.BOOK, 1), false));
		    	info.setItem(8, MenuCommand.addpmenu(ColorOptions.falsecommand + "Exit", Material.BARRIER, ChatColor.GRAY + "Exit item information"));

		    	for (int i = 0; i < info.getSize(); i++)
		    	{
		    		if (info.getItem(i) == null || info.getItem(i).getType() == Material.AIR)
		    		{
		    			if (i == 3 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 21)
		    			{
		    				info.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0)));
		    			} else
		    			{
		    				info.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0)));
		    			}
		    		}
		    	}
		    	user.getPlayer().openInventory(info);
			}
		}
	}
	
	public void playEnchantAnimation(Player player)
	{
		UUID uuid = player.getUniqueId();
		Location loc = framelocations.get(uuid);
		ItemStack itemstack = enchantItem.get(uuid);
		Item item = player.getWorld().dropItem(loc.add(0.2,0,0.2), itemstack);
		item.setPickupDelay(30*20);
		
		player.sendMessage(ColorOptions.message + "Starting the ritual...");
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 2);
		BukkitTask i = new BukkitRunnable()
		{
            public void run()
            {
            	PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME,true, (float) (loc.getX()), (float) (loc.getY()+0.5), (float) (loc.getZ()), 0, 20, 0, 0, 5);
    			for (Player p1 : Bukkit.getOnlinePlayers())
				{
					  ((CraftPlayer) p1).getHandle().playerConnection.sendPacket(packet);
				}
            }
		}.runTaskTimer(main, 0, 1);
    	new BukkitRunnable()
    	{
    		public void run()
    		{
				item.remove();
				i.cancel();
    		}
    	}.runTaskLater(main, 2*20);
	}
	
	public void addEnchantItem(User user, Enchantment enchantment, Integer price, Integer minChance, Integer maxChance, boolean superExtreme)
	{
		UUID uuid = user.getUUID();
		Player player = user.getPlayer();
		ItemStack item = enchantItem.get(uuid);
		Inventory inv = player.getInventory();
		Location loc = player.getLocation();
		player.getInventory().remove(enchantItem.get(uuid));
		player.getWorld().playSound(loc, SoundHandler.PORTAL_TRIGGER, 0.5F, 1.0F);
		Random random = new Random();
    	new BukkitRunnable()
    	{
    		public void run()
    		{
    			if (user.getCoins() >= price)
    			{
    				if (random.nextInt(100) <= main.getRandom(minChance, maxChance))
    				{
    	    			loc.getWorld().strikeLightningEffect(loc);
    					Integer enchLevel = 1;
    					player.getWorld().playSound(loc, SoundHandler.BLAZE_BREATH, 0.5F, 1.0F);
    					if (superExtreme == true)
    					{
        					if (random.nextInt(100) <= 15)
        					{
        						enchLevel = enchLevel+1;
        					}
    					}
    					if (item.containsEnchantment(enchantment))
    					{
    						if ((item.getEnchantmentLevel(enchantment)+enchLevel) <= 6)
    						{
    							item.addUnsafeEnchantment(enchantment, item.getEnchantmentLevel(enchantment)+enchLevel);
    						}
    					} else
    					{
    						item.addUnsafeEnchantment(enchantment, enchLevel);
    					}
    					user.removeCoins(price);
        				player.sendMessage(ColorOptions.messageachievement + "Succesfully completed the ritual!");
            			
    				} else
    				{
    					player.getWorld().playSound(loc, SoundHandler.BLAZE_DEATH, 0.5F, 1.0F);
    					player.sendMessage(ColorOptions.falsecommand + "The ritual has failed, no enchantment added");
    				}
    			} else
    			{
    				player.sendMessage(ColorOptions.falsecommand + "You don't have enough coins to complete the ritual");
    			}
    			
    			//Adding the item to the user's inventory, if full, dropping item on the ground
    			if (inv.firstEmpty() != -1)
				{
					inv.addItem(item);
				} else
				{
					player.getWorld().dropItemNaturally(loc, item);
    				player.sendMessage(ColorOptions.falsecommand + "No space in your inventory, dropping item on the ground");
				}
    		}
    	}.runTaskLater(main, 2*20);
    	this.enchantItem.remove(uuid);
    	this.framelocations.remove(uuid);
	}
	
	public void setItemStep(User user, Inventory menu, String displayName, Integer propertyID, Integer productID)
	{
		UUID uuid = user.getUUID();
		Player player = user.getPlayer();
		Integer coins = user.getCoins();
		Integer relationID = this.propertyproduct.getRelationID(productID, propertyID);
		Integer stock = propertyproduct.getAmount(relationID);
		Integer price = propertyproduct.getPrice(relationID);
		
		String step = displayName.split(": ")[1];
		Integer stepamount = 1;
		if (step.equalsIgnoreCase("1"))
		{
			stepamount = 8;
			if (stock >= stepamount)
			{
				menu.setItem(3, MenuCommand.addpmenu(ColorOptions.stats + "Amount: " + stepamount, Material.CHEST, ChatColor.GRAY + "This is the amount of items", ChatColor.GRAY + "you buy per purchase"));
				if (coins >= price*stepamount)
				{
					menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.GREEN + "Click here to buy this item!"));
				} else
				{
			    	menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "You need " + ((price*stepamount)-coins) + " more coins!"));
				}
			} else
			{
				user.getPlayer().playSound(user.getPlayer().getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
				user.getPlayer().sendMessage(ColorOptions.error + "You cannot buy more than there is in stock of this item!");
			}
		} else
		if (step.equalsIgnoreCase("8"))
		{
			stepamount = 16;
			if (stock >= stepamount)
			{
				menu.setItem(3, MenuCommand.addpmenu(ColorOptions.stats + "Amount: " + stepamount, Material.CHEST, ChatColor.GRAY + "This is the amount of items", ChatColor.GRAY + "you buy per purchase"));
				if (coins >= price*stepamount)
				{
					menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.GREEN + "Click here to buy this item!"));
				} else
				{
			    	menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "You need " + ((price*stepamount)-coins) + " more coins!"));
				}
			} else
			{
				player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
				player.sendMessage(ColorOptions.error + "You cannot buy more than there is in stock of this item!");
			}
		} else
		if (step.equalsIgnoreCase("16"))
		{
			stepamount = 32;
			if (stock >= stepamount)
			{
				menu.setItem(3, MenuCommand.addpmenu(ColorOptions.stats + "Amount: " + stepamount, Material.CHEST, ChatColor.GRAY + "This is the amount of items", ChatColor.GRAY + "you buy per purchase"));
				if (coins >= price*stepamount)
				{
					menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.GREEN + "Click here to buy this item!"));
				} else
				{
			    	menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "You need " + ((price*stepamount)-coins) + " more coins!"));
				}
			} else
			{
				player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
				player.sendMessage(ColorOptions.error + "You cannot buy more than there is in stock of this item!");
			}
		} else
		if (step.equalsIgnoreCase("32"))
		{
			stepamount = 64;
			if (stock >= stepamount)
			{
				menu.setItem(3, MenuCommand.addpmenu(ColorOptions.stats + "Amount: " + stepamount, Material.CHEST, ChatColor.GRAY + "This is the amount of items", ChatColor.GRAY + "you buy per purchase"));
				if (coins >= price*stepamount)
				{
					menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.GREEN + "Click here to buy this item!"));
				} else
				{
			    	menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "You need " + ((price*stepamount)-coins) + " more coins!"));
				}
			} else
			{
				player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
				player.sendMessage(ColorOptions.error + "You cannot buy more than there is in stock of this item!");
			}		
		} else
		if (step.equalsIgnoreCase("64"))
		{
			stepamount = 1;
			if (stock >= stepamount)
			{
				menu.setItem(3, MenuCommand.addpmenu(ColorOptions.stats + "Amount: " + stepamount, Material.CHEST, ChatColor.GRAY + "This is the amount of items", ChatColor.GRAY + "you buy per purchase"));
				if (coins >= price*stepamount)
				{
					menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.GREEN + "Click here to buy this item!"));
				} else
				{
			    	menu.setItem(5, product.createItem(ColorOptions.stats + "" + stepamount + " items cost: " + ColorOptions.statsresults + price*stepamount, new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + stock, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "You need " + ((price*stepamount)-coins) + " more coins!"));
				}
			}	
		}
	}
}
