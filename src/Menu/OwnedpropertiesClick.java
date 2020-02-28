package Menu;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Houses.House;
import Main.Main;
import Products.Product;
import Properties.Property;
import Skills.Skill;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Users.User;
import Users.Users;

public class OwnedpropertiesClick implements Listener
{
	Skill skill = new Skill();
	Product product = new Product();
	House house = new House();
	Property property = new Property();
	Town town = new Town();
	Street street = new Street();
	Title title = new Title();
	SpawnPoint spawnpoint = new SpawnPoint();
	Menu menu = new Menu();
	private Main main;
	public OwnedpropertiesClick(Main main) 
	{
		this.main = main;
	}
	
	public static Map<UUID, Inventory> propertymenu = new HashMap<UUID, Inventory>();
	private static Map<UUID, Integer> propertyID = new HashMap<UUID, Integer>();

	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if (e.getWhoClicked() instanceof Player)
		{
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
			Integer propertyAmount = user.getPropertyAmount(false);
			Integer propertyMax = user.getPropertyAmount(true);
			Inventory menu = e.getInventory();
			ItemStack clicked = e.getCurrentItem();
			if (MenuClick.ownedpropertiesmap.containsKey(uuid))
			{
				if (menu.getName().equalsIgnoreCase(MenuClick.ownedpropertiesmap.get(uuid).getName()))
				{
					Inventory ownedmenu = MenuClick.ownedpropertiesmap.get(uuid);
					String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
					e.setCancelled(true);
					if (dc.contains("back"))
					{
						this.menu.OpenPersonalMenu(user);
					}
					if (dc.contains("buy a new property"))
					{
						if (propertyAmount >= propertyMax)
						{
							player.sendMessage(ColorOptions.error + "You can't buy more properties, please buy an extra slot in the gem-shop!");
							this.menu.openownedProperty(user);
						} else
						{
							this.menu.openPropertylist(user, 1, null, null);
						}
					}
					if (dc.contains("propertyname"))
					{
						List<String> lore = clicked.getItemMeta().getLore();
						if (this.menu.getMenuStructureID(lore, "property", 0) != null)
						{
							Integer propertyID = this.menu.getMenuStructureID(lore, "property", 0);
							propertymenu.remove(uuid);
							this.propertyID.put(uuid, propertyID);
							openpropertyMenu(propertyID, user);
							player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
						}
					}
				}
			}		
			if (propertymenu.containsKey(uuid))
			{
				if (menu.getName().equalsIgnoreCase(propertymenu.get(uuid).getName()))
				{
					String dc = clicked.getItemMeta().getDisplayName().toLowerCase();
					e.setCancelled(true);
					if (dc.contains("sell"))
					{
						Integer propertyID = this.propertyID.get(uuid);
						opensellconfirm(propertyID, user);
						player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
					}
					if (dc.contains("back"))
					{
						this.menu.openownedProperty(user);
					}
				}
			}
			if (ChatColor.stripColor(menu.getName()).equalsIgnoreCase("property sale"))
			{
				String dc = clicked.getItemMeta().getDisplayName().toLowerCase();
				e.setCancelled(true);
				String confirmname = menu.getItem(4).getItemMeta().getLore().get(0);
				String[] confirmnamesplit = confirmname.split(" ");
				String split = ChatColor.stripColor(confirmnamesplit[7]);
				String propertyName = split;
				Integer propertyID = this.propertyID.get(uuid);
				if (ChatColor.stripColor(dc).equalsIgnoreCase("confirm"))
				{
					property.sellProperty(user, propertyID);
					MenuClick.ownedpropertiesmap.remove(uuid);
					this.menu.openownedProperty(user);
					player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);

				}
				if (dc.contains("cancel"))
				{
					openpropertyMenu(propertyID, user);
				}
				if (dc.contains("back"))
				{
					openpropertyMenu(propertyID, user);
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e)
	{
		String menuname = ChatColor.stripColor(e.getInventory().getName());
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
			return;
		}
		Integer userID = user.getID();
		Integer spawnpointID = user.getSpawnpointID();
		if (menuname.equalsIgnoreCase("choose a property to remove"))
		{
			for (Integer propertyID : property.getIDList(null, null))
			{
				if (property.getPropertyOwnerID(propertyID) == userID)
				{
					if (property.getPropertySpawnPoint(propertyID) != spawnpointID && user.getPropertyAmount(false) > 1)
					{
						Bukkit.getConsoleSender().sendMessage("Removed a property-owner of property with ID " + propertyID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
						property.RemovePropertyOwner(propertyID);
						player.sendMessage(ColorOptions.error + "System removed a property from your propertylist because you closed the Menu!");
						player.sendMessage(ColorOptions.message + "If you think this is a mistake, please contact a staff-member!");
						if (user.getPropertyAmount(false) <= user.getPropertyAmount(true))
						{
							break;
						}
					} else
					{
						Bukkit.getConsoleSender().sendMessage("Removed a property-owner of property with ID " + propertyID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
						property.RemovePropertyOwner(propertyID);
						player.sendMessage(ColorOptions.error + "System removed a property from your propertylist because you closed the Menu!");
						player.sendMessage(ColorOptions.message + "If you think this is a mistake, please contact a staff-member!");
						if (user.getPropertyAmount(false) <= user.getPropertyAmount(true))
						{
							break;
						}
					}
				}
			}
		}
	}
	
	public void ForcedPropertySellClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		ItemStack clicked = e.getCurrentItem();
		String dc = clicked.getItemMeta().getDisplayName().toLowerCase();
		e.setCancelled(true);
		if (dc.contains("propertyname: "))
		{
			String streetName = ChatColor.stripColor(clicked.getItemMeta().getLore().get(0).split(": ")[1]);
			Integer streetNumber = Integer.valueOf(ChatColor.stripColor(clicked.getItemMeta().getLore().get(1).split(": ")[1]));
			String townName = ChatColor.stripColor(clicked.getItemMeta().getLore().get(2).split(": ")[1]);
			Integer townID = town.getTownID(townName);
			Integer streetID = street.getStreetID(streetName, townID);
			Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
			if (propertyID != null)
			{
				property.RemovePropertyOwner(propertyID);
				player.closeInventory();
				player.sendMessage(ColorOptions.messageachievement + "Succesfully removed a property from your propertylist!");
				if (user.getPropertyAmount(false) > user.getPropertyAmount(true))
				{
					this.menu.openForcedPropertySell(user);
					player.sendMessage(ColorOptions.error + "You are still owning more propertys than allowed!");
				}
			} else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "No propertyID could be found on location received from the forced property sell-menu!");
			}
		}
	}
	
	public void openpropertyMenu(Integer propertyID, User user)
	{
		UUID uuid = user.getUUID();
		String propertyName = property.getPropertyName(propertyID);
		Integer propertyPrice = property.getPropertyPrice(propertyID);
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Inventory propertyinfomenu = Bukkit.createInventory(null, 18, ColorOptions.messageformat + "Property info: " + ColorOptions.messagesubjects + propertyName);
    	
		propertyinfomenu.setItem(0, MenuClick.addmenulist(ColorOptions.stats + "Finacial", Material.GOLD_INGOT, ColorOptions.stats + "Coins: " + ColorOptions.coinStats + coinamount, ColorOptions.stats + "Gems: " + ColorOptions.gemStats + gemamount, ColorOptions.stats + "Salary: " + ColorOptions.statsresults + salary, ColorOptions.stats + "Income: " + ColorOptions.statsresults + income));
		propertyinfomenu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your property list"));
		propertyinfomenu.setItem(9, MenuClick.addmenulist(ColorOptions.falsecommand + "Sell", Material.SKULL_ITEM, ChatColor.GRAY + "Click here to sell this property", ChatColor.GRAY + "You will receive " + ColorOptions.coinStats + (propertyPrice/2) + " coins", ColorOptions.falsecommand + "Warning: Your income will decrease with " + property.getIncome(propertyID)));
		propertyinfomenu.setItem(12, MenuClick.addmenulist(ColorOptions.stats + "Income of " + ColorOptions.statsresults + propertyName, Material.PAPER, ColorOptions.statsformat + "Income per 12 hours: " + ColorOptions.statsresults + property.getIncome(propertyID) + " coins"));
		propertyinfomenu.setItem(13, MenuClick.addmenulist(ColorOptions.statsformat + "Propertyname: " + ColorOptions.statsresults + propertyName, Material.ANVIL, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + propertyPrice, ColorOptions.statsformat + "Income: " + ColorOptions.statsresults + property.getIncome(propertyID), ColorOptions.statsformat + "Level: " + ColorOptions.statsresults + "X", ColorOptions.statsformat + "City: " + ColorOptions.statsresults + "X"));
		propertymenu.put(uuid, propertyinfomenu);
		user.getPlayer().openInventory(propertyinfomenu);
	}
	
	public void opensellconfirm(Integer propertyID, User user)
	{
		UUID uuid = user.getUUID();
		String propertyName = property.getPropertyName(propertyID);
		Integer propertyPrice = property.getPropertyPrice(propertyID);
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Inventory sellconfirm = Bukkit.createInventory(null, 18, ColorOptions.messageformat + "Property sale");

		sellconfirm.setItem(0, MenuClick.addmenulist(ColorOptions.stats + "Finacial", Material.GOLD_INGOT, ColorOptions.stats + "Coins: " + ColorOptions.coinStats + coinamount, ColorOptions.stats + "Gems: " + ColorOptions.gemStats + gemamount, ColorOptions.stats + "Salary: " + ColorOptions.statsresults + salary, ColorOptions.stats + "Income: " + ColorOptions.statsresults + income));
		sellconfirm.setItem(4, MenuClick.addmenulist(ColorOptions.falsecommand + "Confirmation", Material.BOOK, ColorOptions.messageformat + "Are you sure you want to sell " + ColorOptions.messagesubjects + propertyName, ColorOptions.messageformat + "You will receive " + ColorOptions.coinStats + (propertyPrice/2) + " coins"));
		sellconfirm.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to", ChatColor.GRAY + "the property information of " + ColorOptions.messagesubjects + propertyName));
		sellconfirm.setItem(12, product.createClayItem(ChatColor.GREEN + "Confirm", true, ""));
		sellconfirm.setItem(14, product.createClayItem(ChatColor.RED + "Cancel", false, ""));
		user.getPlayer().openInventory(sellconfirm);
	}
}
