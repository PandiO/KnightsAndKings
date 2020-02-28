package Properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Products.Enchantment;
import Products.Product;
import Users.User;
import Users.Users;
import Users.offlineUser;

public class EnchantmentGenerator implements Listener
{
	Integer price = 250000;
	offlineUser user = new offlineUser();
	Enchantment enchantment = new Enchantment();
	WorldGuard worldguard = new WorldGuard();
	Property property = new Property();
	Product product = new Product();
	PropertyCategory propertyCat = new PropertyCategory();
	private Main main;
	public EnchantmentGenerator(Main main) 
	{
		this.main = main;
	}
	
	List<Location> generatorList = new ArrayList<Location>();
	
	@EventHandler
	public void onClick(PlayerInteractEvent e)
	{
		Action action = e.getAction();
		if (action.equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME)
		{
			Location location = e.getClickedBlock().getLocation();
			if (worldguard.getStructureIDbyRegion("property", location, worldguard.getRegionManager(location.getWorld())) != null)
			{
				Player player = e.getPlayer();
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
				Integer propertyID = worldguard.getStructureIDbyRegion("property", location, worldguard.getRegionManager(location.getWorld()));
				if (propertyCat.getCategoryName(property.getCategoryID(propertyID)).equalsIgnoreCase("witchery"))
				{
					if (inUse(location) == false)
					{
						Integer coins = user.getCoins();
						if (coins >= price)
						{
							generatorList.add(location);
							if (main.getRandom(0, 100) <= 1)
							{
								Integer playernumber = main.getRandom(0, Bukkit.getOnlinePlayers().size()-1);
								Integer counter = 0;
								for (Player target : Bukkit.getOnlinePlayers())
								{
									if (counter == playernumber)
									{
										UUID tu = target.getUniqueId();
										user.removeCoins(price);
										Integer prize = price*main.getRandom(1, 3);
										this.user.addCoins(tu, prize);
										target.playSound(target.getLocation(), SoundHandler.ORB_PICKUP, 0.5F, 1.0F);
										target.sendMessage(ColorOptions.messageachievement + "You received " + prize + " coins!");
									}
									counter++;
								}
							} else
							{
								user.removeCoins(price);
							}
							Integer categoryID = null;
							if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR)
							{
								ItemStack item = player.getItemInHand();
								if (item.hasItemMeta())
								{
									String displayName = item.getItemMeta().getDisplayName();
									if (product.getProductIDbyDisplayName(displayName, false) != null)
									{
										categoryID = product.getCategoryID(product.getProductIDbyDisplayName(displayName, false), false);
									}
								}
							}
							Integer enchantmentID = RandomEnchantment(categoryID);
							if (enchantmentID != null)
							{
								Integer grade = this.enchantment.getEnchantmentGrade(enchantmentID);
								boolean Lightning = false;
								if (grade == 3)
								{
									Lightning = true;
								}
								GeneratorAnimation(player, location, enchantmentID, enchantment.getIDList().size(), Lightning);
							} else
							{
								enchantmentID = RandomEnchantment(categoryID);
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "You do not have enough coins, you need " + (250000-coins) + " more coins!");
						}
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "This generator is alreadty in use");
					}
				}
			}
		}
	}
	
	
	public Integer RandomEnchantment(Integer categoryID)
	{
		Integer enchantmentID = null;
		List<Integer> list = enchantment.getIDList();
		Collections.shuffle(list);
		for (Integer enchantID : list)
		{
			Random random = new Random();
			Integer grade = enchantment.getEnchantmentGrade(enchantID);
			Integer enchantCategoryID = enchantment.getPreferCategory(enchantID);
			Integer chance = 30;
			if (grade == 2)
			{
				chance = 5;
			} else if (grade == 3)
			{
				chance = 2;
			}
			if (categoryID == enchantCategoryID)
			{
				chance = chance*4;
			}
			if (random.nextInt(100) <= chance)
			{
				if (categoryID != enchantCategoryID)
				{
					if (random.nextInt(100) <= 50)
					{
						enchantmentID = enchantID;
					} else
					{
						continue;
					}
				} else
				{
					enchantmentID = enchantID;
				}
				break;
			}
		}
		
		return enchantmentID;
	}
	
	public void GeneratorAnimation(Player player, Location generatorLoc, Integer chosenEnchantmentID, Integer listSize, boolean Lightning)
	{
		player.sendMessage(ColorOptions.message + "Starting the ritual...");
		BukkitTask i = new BukkitRunnable()
		{
        	Enchantment ench = new Enchantment();
        	Product product = new Product();
        	Item dropped;
        	Location drop = generatorLoc.add(0, 1, 0);
            public void run()
            {
            	if (dropped != null)
            	{
            		dropped.remove();
            	}
            	String enchantment = ench.getEnchantmentName(main.getRandom(1, listSize));
            	Integer grade = ench.getEnchantmentGrade(ench.getEnchantmentID(enchantment));
            	ItemStack item = product.createAmountItem(Material.ENCHANTED_BOOK, 1, ColorOptions.getEnchantColor(grade) + enchantment, ChatColor.GREEN + "This item contains 1 or 2 levels", ChatColor.GRAY + "Right-click to add this enchantment", ChatColor.GRAY + "to an item from your inventory");
            	generatorLoc.getWorld().playSound(generatorLoc, SoundHandler.CLICK, 1.0F, 1.0F);
            	dropped = generatorLoc.getWorld().dropItem(drop, item);
            	dropped.setPickupDelay(80);
            	dropped.teleport(generatorLoc);
            	new BukkitRunnable()
            	{
            		public void run()
            		{
            			dropped.remove();
            		}
            	}.runTaskLater(main, 3);
            }
		}.runTaskTimer(main, 0, 3);
    	new BukkitRunnable()
    	{
    		public void run()
    		{
    			if (Lightning == true)
    			{
    				generatorLoc.getWorld().strikeLightningEffect(generatorLoc);
    			}
            	
    		}
    	}.runTaskLater(main, 1*20);
    	new BukkitRunnable()
    	{
        	Product product = new Product();
        	Enchantment ench = new Enchantment();
    		public void run()
    		{
    			i.cancel();
            	ItemStack item = product.createAmountItem(Material.ENCHANTED_BOOK, 1, ColorOptions.getEnchantColor(ench.getEnchantmentGrade(chosenEnchantmentID)) + ench.getEnchantmentName(chosenEnchantmentID), ChatColor.GREEN + "This item contains 1 or 2 levels", ChatColor.GRAY + "Right-click to add this enchantment", ChatColor.GRAY + "to an item from your inventory");
            	Item dropped = generatorLoc.getWorld().dropItemNaturally(generatorLoc.add(0, 1, 0), item);
            	dropped.setPickupDelay(80);
            	generatorLoc.getWorld().playSound(generatorLoc, SoundHandler.ANVIL_LAND, 1.0F, 1.0F);
//            	try
//            	{
//                	generatorLoc.getWorld().playEffect(generatorLoc, Effect.FLAME, 10);	
//            	} catch (Exception ex)
//            	{
//            		ex.printStackTrace();
//            	}
            	new BukkitRunnable()
            	{
            		public void run()
            		{
            			dropped.remove();
            		}
            	}.runTaskLater(main, 2*20);
    		}
    	}.runTaskLater(main, 2*20);
    	BukkitTask giveItem = new BukkitRunnable()
    	{
        	Product product = new Product();
        	Enchantment ench = new Enchantment();
    		public void run()
    		{
            	generatorList.remove(generatorLoc);
            	ItemStack item = product.createAmountItem(Material.ENCHANTED_BOOK, 1, ColorOptions.getEnchantColor(ench.getEnchantmentGrade(chosenEnchantmentID)) + ench.getEnchantmentName(chosenEnchantmentID), ChatColor.GREEN + "This item contains 1 or 2 levels", ChatColor.GRAY + "Right-click to add this enchantment", ChatColor.GRAY + "to an item from your inventory");
            	generatorLoc.getWorld().playSound(generatorLoc, SoundHandler.ORB_PICKUP, 1.0F, 1.0F);
            	Inventory inv = player.getInventory();
            	if (inv.firstEmpty() != -1)
            	{
                	player.getInventory().addItem(item);
            	} else
            	{
            		player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
            		player.sendMessage(ColorOptions.error + "Inventory full, dropped item on the ground!");
            	}
//				Bukkit.getServer().getPluginManager().callEvent(new PurchaseEvent(player, item));
            	player.playSound(player.getLocation(), SoundHandler.ITEM_PICKUP, 1.0F, 1.0F);
            	player.sendMessage(ColorOptions.messageachievement + "Ritual as been succesfully completed!");
    		}
    	}.runTaskLater(main, 4*20);
	}
	
	public boolean inUse(Location location)
	{
		boolean inuse = false;
		if (!generatorList.isEmpty())
		{
			for (Location loc : generatorList)
			{
				Double x = loc.getX();
				Double z = loc.getZ();
				if (location.getX() == x && location.getZ() == z)
				{
					inuse = true;
					break;
				}
			}
		}
		
		return inuse;
	}
}
