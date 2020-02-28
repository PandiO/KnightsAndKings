package Products;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Properties.Property;
import Properties.PropertyCategory;

public class EnchantbookClick implements Listener
{
	WorldGuard worldguard = new WorldGuard();
	Property property = new Property();
	Enchantment enchant = new Enchantment();
	Product product = new Product();
	PropertyCategory propertyCat = new PropertyCategory();
	private Main main;
	public EnchantbookClick(Main main) 
	{
		this.main = main;
	}
	HashMap<UUID, Inventory> invList = new HashMap<UUID, Inventory>();
	HashMap<UUID, Integer> enchList = new HashMap<UUID, Integer>();
	HashMap<UUID, ItemStack> enchantBook = new HashMap<UUID, ItemStack>();
	
	@EventHandler
	public void onClick(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		if (player.getItemInHand() != null)
		{
			Material type = e.getPlayer().getItemInHand().getType();
			if (type == Material.ENCHANTED_BOOK)
			{
				ItemStack item = e.getPlayer().getItemInHand();
				if (item.hasItemMeta())
				{
					for (Integer enchantmentID : enchant.getIDList())
					{
						if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase(enchant.getEnchantmentName(enchantmentID)))
						{
							invList.put(player.getUniqueId(), player.getInventory());
							enchList.put(player.getUniqueId(), enchantmentID);
							enchantBook.put(player.getUniqueId(), item);
							player.openInventory(player.getInventory());
							player.sendMessage(ColorOptions.message + "Select an item from your inventory to enchant..");
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e)
	{
		UUID uuid = e.getPlayer().getUniqueId();
		if (invList.containsKey(uuid))
		{
			invList.remove(uuid);
			enchList.remove(uuid);
			enchantBook.remove(uuid);
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player player = (Player) e.getWhoClicked();
			UUID uuid = player.getUniqueId();
			if (invList.containsKey(uuid))
			{
				if (invList.get(uuid).equals(e.getInventory()))
				{
					e.setCancelled(true);
					ItemStack item = e.getCurrentItem();
					if (item.hasItemMeta())
					{
						String displayName = item.getItemMeta().getDisplayName();
						if (product.getProductIDbyDisplayName(displayName, false) != null && !product.soulbound(item))
						{
							Integer enchantmentID = enchList.get(uuid);
							Integer productID = product.getProductIDbyDisplayName(displayName, false);
							Integer enchantLevel = 1;
							Random random = new Random();
							Integer enchantLevels = this.canEnchant(item, productID, enchantmentID);
							if (random.nextInt(100) <= 20 && enchantLevels >= 2)
							{
								enchantLevel = 2;
							}
							if (enchantLevels >= 1)
							{
								if (enchant.customEnhantment(enchantmentID) == false)
								{
									org.bukkit.enchantments.Enchantment enchantment = product.getEnchantmentfromString(enchant.getEnchantmentName(enchantmentID));
									if (item.containsEnchantment(enchantment))
									{
										item.addUnsafeEnchantment(enchantment, item.getEnchantmentLevel(enchantment)+enchantLevel);
									} else
									{
										item.addUnsafeEnchantment(enchantment, enchantLevel);
									}
								} else
								{
									item = product.addCustomEnchantment(item, enchant.getEnchantmentName(enchantmentID), enchantLevel, true, false);
								}
								invList.remove(uuid);
								enchList.remove(uuid);
								this.removeItemfromInventory(enchantBook.get(uuid), player);
								player.playSound(player.getLocation(), SoundHandler.ENDERMAN_TELEPORT, 1.0F, 1.0F);
								player.sendMessage(ColorOptions.messageachievement + "Succesfully enchanted the selected item!");
								enchantBook.remove(uuid);
								player.closeInventory();
							} else
							{
								player.sendMessage(ColorOptions.error + "This item reached the max. enchantmentlevel for this enchantment");
							}
						} else
						{
							player.sendMessage(ColorOptions.error + "This item cannot be enchanted!");
						}
					} else
					{
						player.sendMessage(ColorOptions.error + "This item cannot be enchanted!");
					}
				}
			}
		}
	}
	
	public Integer canEnchant(ItemStack item, Integer productID, Integer enchantmentID)
	{
		Integer enchantLevels = 0;
		Integer maxEnchantLevel = Math.round((enchant.getEnchantmentMaxLevel(enchantmentID)/(6-product.getGrade(productID, false).intValue())));
		Integer currentEnchantLevel = 0;
		String enchantName = enchant.getEnchantmentName(enchantmentID);
		if (product.getEnchantmentfromString(enchantName) != null)
		{
			org.bukkit.enchantments.Enchantment enchantment = product.getEnchantmentfromString(enchant.getEnchantmentName(enchantmentID));
			if (item.containsEnchantment(enchantment))
			{
				currentEnchantLevel = item.getEnchantmentLevel(enchantment);
			}
			if (currentEnchantLevel < maxEnchantLevel)
			{
				for (int i = 1; i < maxEnchantLevel+1; i++)
				{
					if (currentEnchantLevel+i <= maxEnchantLevel)
					{
						enchantLevels = i;
					}
				}
			}
		} else
		{
			enchantLevels = 2;
		}
		
		return enchantLevels;
	}
	
	public void removeItemfromInventory(ItemStack item, Player player)
	{
		String displayName = item.getItemMeta().getDisplayName();
		for (int i = 0; i < player.getInventory().getSize(); i++)
		{
			ItemStack slot = player.getInventory().getItem(i);
			if (slot != null && slot.getType() != Material.AIR)
			{
				if (slot.hasItemMeta())
				{
					if (slot.getItemMeta().hasDisplayName())
					{
						if (slot.getItemMeta().getDisplayName().equals(displayName))
						{
							player.getInventory().setItem(i, new ItemStack(Material.AIR));
							break;
						}
					}
				}
			}
		}
	}
	
}
