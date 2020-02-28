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
import Rooms.Room;
import Skills.Skill;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Tutorial.Tutorial;
import Tutorial.TutorialEvents;
import Users.User;
import Users.Users;

public class OwnedhousesClick implements Listener
{
	Skill skill = new Skill();
	Product product = new Product();
	House house = new House();
	Property property = new Property();
	Town town = new Town();
	Room room = new Room();
	Street street = new Street();
	Title title = new Title();
	SpawnPoint spawnpoint = new SpawnPoint();
	Menu menu = new Menu();
	private Main main;
	public OwnedhousesClick(Main main) 
	{
		this.main = main;
	}
	public static Map<UUID, Inventory> housemenu = new HashMap<UUID, Inventory>();
	private static Map<UUID, Integer> HouseID = new HashMap<UUID, Integer>();
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if (e.getWhoClicked() instanceof Player)
		{
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
			Integer houseAmount = user.getHouseAmount(false);
			Integer roomAmount = user.getRoomAmount(false);
			Integer houseMax = user.getHouseAmount(true);
			Inventory menu = e.getInventory();
			ItemStack clicked = e.getCurrentItem();
			if (MenuClick.ownedhousesmap.containsKey(uuid))
			{
				if (menu.getName().equalsIgnoreCase(MenuClick.ownedhousesmap.get(uuid).getName()))
				{
					Inventory ownedmenu = MenuClick.ownedhousesmap.get(uuid);
					Tutorial tutorial = null;
					for (Tutorial tut : TutorialEvents.tutorials)
					{
						if (tut.target == p)
						{
							tutorial = tut;
							break;
						}
					}
					String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
					e.setCancelled(true);
					if (tutorial != null)
					{
						if (dc.contains("back"))
						{
							tutorial.previousStage();
						}
						if (tutorial.getName().equalsIgnoreCase("room tutorial"))
						{
							if (tutorial.stage == 5)
							{
								if (dc.contains("rent a room"))
								{
									if (roomAmount == 0)
									{
										tutorial.nextStage(null, null);
									} else
									{
										p.sendMessage(ColorOptions.error + "Tutorial ended: You already have the maximum amount of owned rooms!");
										tutorial.cancel(null);
									}
								}
							}
						}
					} else
					{
						if (dc.contains("back"))
						{
							this.menu.OpenPersonalMenu(user);
						}
						if (dc.contains("buy a new house"))
						{
							if (houseAmount >= houseMax)
							{
								p.sendMessage(ColorOptions.error + "You can't buy more houses, please buy an extra slot in the gem-shop!");
								this.menu.openownedHouses(user);
							} else
							{
								this.menu.openHouselist(user, 1, null);
							}
						}
						if (dc.contains("rent a room"))
						{
							if (roomAmount == 0)
							{
								this.menu.openRoomlist(user, 1, null);
							} else
							{
								p.sendMessage(ColorOptions.error + "You can't buy more rooms!");
								this.menu.openownedHouses(user);
							}
						}
						if (dc.contains("housename"))
						{
							List<String> lore = clicked.getItemMeta().getLore();
							if (this.menu.getMenuStructureID(lore, "house", 0) != null)
							{
								Integer houseID = this.menu.getMenuStructureID(lore, "house", 0);
								housemenu.remove(uuid);
								this.HouseID.put(uuid, houseID);
								openhouseMenu(houseID, user);
								p.playSound(p.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
							}
						}
						if (dc.contains("roomnumber"))
						{
							Integer roomNumber = Integer.valueOf(dc.split("roomnumber: ")[1]);
							List<String> lore = clicked.getItemMeta().getLore();
							Integer roomID = this.menu.getMenuStructureID(lore, "room", roomNumber);
							if (roomID != null)
							{
								housemenu.remove(uuid);
								this.HouseID.put(uuid, roomID);
								openroomsellconfirm(roomID, user);
								p.playSound(p.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
							}
						}
					}
				}
			}		
			if (housemenu.containsKey(uuid))
			{
				if (menu.getName().equalsIgnoreCase(housemenu.get(uuid).getName()))
				{
					String dc = clicked.getItemMeta().getDisplayName().toLowerCase();
					e.setCancelled(true);
					if (dc.contains("sell"))
					{
						Integer houseID = this.HouseID.get(uuid);
						opensellconfirm(houseID, user);
						p.playSound(p.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
					}
					if (dc.contains("back"))
					{
						this.menu.openownedHouses(user);
					}
				}
			}
			if (ChatColor.stripColor(menu.getName()).equalsIgnoreCase("house sale"))
			{
				String dc = clicked.getItemMeta().getDisplayName().toLowerCase();
				e.setCancelled(true);
				String confirmname = menu.getItem(4).getItemMeta().getLore().get(0);
				String[] confirmnamesplit = confirmname.split(" ");
				String split = ChatColor.stripColor(confirmnamesplit[7]);
				String houseName = split;
				Integer houseID = this.HouseID.get(uuid);
				if (ChatColor.stripColor(dc).equalsIgnoreCase("confirm"))
				{
					house.sellHouse(user, houseID);
					if (house.getHouseSpawnPoint(houseID) == user.getSpawnpointID())
					{
						user.removeSpawnpoint();
					}
					MenuClick.ownedhousesmap.remove(uuid);
					this.menu.openownedHouses(user);
					p.playSound(p.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);

				}
				if (dc.contains("cancel"))
				{
					openhouseMenu(houseID, user);
				}
				if (dc.contains("back"))
				{
					openhouseMenu(houseID, user);
				}
			}
			if (ChatColor.stripColor(menu.getName()).equalsIgnoreCase("stop renting a room"))
			{
				String dc = clicked.getItemMeta().getDisplayName().toLowerCase();
				e.setCancelled(true);
				Integer roomID = Integer.valueOf(ChatColor.stripColor(menu.getItem(4).getItemMeta().getDisplayName()).split("Confirmation ")[1]);
				if (ChatColor.stripColor(dc).equalsIgnoreCase("confirm"))
				{
					room.sellRoom(user, roomID);
					MenuClick.ownedhousesmap.remove(uuid);
					this.menu.openownedHouses(user);
					p.playSound(p.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);

				}
				if (dc.contains("cancel"))
				{
					this.menu.openownedHouses(user);
				}
				if (dc.contains("back"))
				{
					this.menu.openownedHouses(user);
				}
			}
			if (ChatColor.stripColor(menu.getName()).equalsIgnoreCase("choose a house to remove"))
			{
				
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
		if (menuname.equalsIgnoreCase("choose a house to remove"))
		{
			for (Integer houseID : house.getHouseIDList(null))
			{
				if (house.getHouseOwnerID(houseID) == userID)
				{
					if (house.getHouseSpawnPoint(houseID) != spawnpointID && user.getHouseAmount(false) > 1)
					{
						Bukkit.getConsoleSender().sendMessage("Removed a house-owner of house with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
						house.RemoveHouseOwner(houseID);
						player.sendMessage(ColorOptions.error + "System removed a house from your houselist because you closed the Menu!");
						player.sendMessage(ColorOptions.message + "If you think this is a mistake, please contact a staff-member!");
						if (user.getHouseAmount(false) <= user.getHouseAmount(true))
						{
							break;
						}
					} else
					{
						Bukkit.getConsoleSender().sendMessage("Removed a house-owner of house with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
						house.RemoveHouseOwner(houseID);
						player.sendMessage(ColorOptions.error + "System removed a house from your houselist because you closed the Menu!");
						player.sendMessage(ColorOptions.message + "If you think this is a mistake, please contact a staff-member!");
						if (user.getHouseAmount(false) <= user.getHouseAmount(true))
						{
							break;
						}
					}
				}
			}
		}
	}
	
	public void ForcedHouseRemoveClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		ItemStack clicked = e.getCurrentItem();
		String dc = clicked.getItemMeta().getDisplayName().toLowerCase();
		e.setCancelled(true);
		if (dc.contains("housename: "))
		{
			String streetName = ChatColor.stripColor(clicked.getItemMeta().getLore().get(0).split(": ")[1]);
			Integer streetNumber = Integer.valueOf(ChatColor.stripColor(clicked.getItemMeta().getLore().get(1).split(": ")[1]));
			String townName = ChatColor.stripColor(clicked.getItemMeta().getLore().get(2).split(": ")[1]);
			Integer townID = town.getTownID(townName);
			Integer streetID = street.getStreetID(streetName, townID);
			Integer houseID = house.getHouseIDbyLocation(streetID, streetNumber);
			if (houseID != null)
			{
				house.RemoveHouseOwner(houseID);
				player.closeInventory();
				player.sendMessage(ColorOptions.messageachievement + "Succesfully removed a house from your houselist!");
				if (user.getHouseAmount(false) > user.getHouseAmount(true))
				{
					this.menu.openForcedHouseSell(user);
					player.sendMessage(ColorOptions.error + "You are still owning more houses than allowed!");
				}
			} else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "No houseID could be found on location received from the forced house sell-menu!");
			}
		}
	}
	
	
	public void openhouseMenu(Integer houseID, User user)
	{
		String houseName = house.getHouseName(houseID);
		Integer housePrice = house.getHousePrice(houseID);
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Inventory ownedhousesmenu = Bukkit.createInventory(null, 18, ColorOptions.messageformat + "House info: " + ColorOptions.messagesubjects + houseName);
    	
		ownedhousesmenu.setItem(0, MenuClick.addmenulist(ColorOptions.stats + "Finacial", Material.GOLD_INGOT, ColorOptions.stats + "Coins: " + ColorOptions.coinStats + coinamount, ColorOptions.stats + "Gems: " + ColorOptions.gemStats + gemamount, ColorOptions.stats + "Salary: " + ColorOptions.statsresults + salary, ColorOptions.stats + "Income: " + ColorOptions.statsresults + income));
		ownedhousesmenu.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your house list"));
		ownedhousesmenu.setItem(9, MenuClick.addmenulist(ColorOptions.falsecommand + "Sell", Material.SKULL_ITEM, ChatColor.GRAY + "Click here to sell this house", ChatColor.GRAY + "You will receive " + ColorOptions.coinStats + (housePrice/2) + " coins", ColorOptions.falsecommand + "Tip: Remove all decorations before selling the house"));
		ownedhousesmenu.setItem(13, MenuClick.addmenulist(ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + houseName, Material.BED, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + housePrice, ColorOptions.statsformat + "Level: " + ColorOptions.statsresults + "X", ColorOptions.statsformat + "City: " + ColorOptions.statsresults + "X"));
		housemenu.put(user.getUUID(), ownedhousesmenu);
		user.getPlayer().openInventory(ownedhousesmenu);
	}
	public void opensellconfirm(Integer houseID, User user)
	{
		String houseName = house.getHouseName(houseID);
		Integer housePrice = house.getHousePrice(houseID);
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Inventory sellconfirm = Bukkit.createInventory(null, 18, ColorOptions.messageformat + "House sale");

		sellconfirm.setItem(0, MenuClick.addmenulist(ColorOptions.stats + "Finacial", Material.GOLD_INGOT, ColorOptions.stats + "Coins: " + ColorOptions.coinStats + coinamount, ColorOptions.stats + "Gems: " + ColorOptions.gemStats + gemamount, ColorOptions.stats + "Salary: " + ColorOptions.statsresults + salary, ColorOptions.stats + "Income: " + ColorOptions.statsresults + income));
		sellconfirm.setItem(4, MenuClick.addmenulist(ColorOptions.falsecommand + "Confirmation", Material.BOOK, ColorOptions.messageformat + "Are you sure you want to sell " + ColorOptions.messagesubjects + houseName, ColorOptions.messageformat + "You will receive " + ColorOptions.coinStats + (housePrice/2) + " coins"));
		sellconfirm.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to", ChatColor.GRAY + "the house information of " + ColorOptions.messagesubjects + houseName));
		sellconfirm.setItem(12, product.createClayItem(ChatColor.GREEN + "Confirm", true, ""));
		sellconfirm.setItem(14, product.createClayItem(ChatColor.RED + "Cancel", false, ""));
		user.getPlayer().openInventory(sellconfirm);
	}
	public void openroomsellconfirm(Integer roomID, User user)
	{
		Integer roomNumber = room.getRoomNumber(roomID);
		Integer propertyID = room.getPropertyID(roomID);
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		Integer streetnumber = property.getStreetNumber(propertyID);
		String townName = town.getTownName(street.getTownID(streetID));
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Inventory sellconfirm = Bukkit.createInventory(null, 18, ColorOptions.messageformat + "Stop renting a room");

		sellconfirm.setItem(0, MenuClick.addmenulist(ColorOptions.stats + "Finacial", Material.GOLD_INGOT, ColorOptions.stats + "Coins: " + ColorOptions.coinStats + coinamount, ColorOptions.stats + "Gems: " + ColorOptions.gemStats + gemamount, ColorOptions.stats + "Salary: " + ColorOptions.statsresults + salary, ColorOptions.stats + "Income: " + ColorOptions.statsresults + income));
		sellconfirm.setItem(4, MenuClick.addmenulist(ColorOptions.falsecommand + "Confirmation " + ChatColor.BLACK + roomID, Material.BOOK, 
				ColorOptions.messageformat + "Are you sure you want stop renting", 
				ColorOptions.messageformat + "a room with roomnumber " + ColorOptions.messagesubjects + roomNumber,
				ColorOptions.messageformat + "in tavern " + property.getPropertyName(propertyID),
				ColorOptions.messageformat + "on the " + ColorOptions.messagesubjects + streetName + ColorOptions.messageformat + " at number " + ColorOptions.messagesubjects + streetnumber,
				ColorOptions.messageformat + "in the town of " + ColorOptions.messagesubjects + townName,
				ColorOptions.error + "You currently pay " + room.getPrice(roomID) + " per playhour"));
		sellconfirm.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to", ChatColor.GRAY + "your houses/rooms overview"));
		sellconfirm.setItem(12, product.createClayItem(ChatColor.GREEN + "Confirm", true, ""));
		sellconfirm.setItem(14, product.createClayItem(ChatColor.RED + "Cancel", false, ""));
		user.getPlayer().openInventory(sellconfirm);
	}
	Map<UUID, Integer> ownedhouseslot = new HashMap<UUID, Integer>();
	public void openownedHouses(User user)
	{
		Integer ownedhouses = user.getHouseAmount(false);
		UUID uuid = user.getUUID();
		Player player = user.getPlayer();
		ownedhouseslot.put(uuid, Integer.valueOf(9));
		if (ownedhouses == 0)
		{
			Inventory ownedhousesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned houses");
			createownedhouses(ownedhousesmenu, user);
			MenuClick.ownedhousesmap.put(uuid, ownedhousesmenu);
			player.openInventory(ownedhousesmenu);
		} else if (ownedhouses > 0 && ownedhouses <= 9)
		{
			Inventory ownedhousesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned houses");
			createownedhouses(ownedhousesmenu, user);
			MenuClick.ownedhousesmap.put(uuid, ownedhousesmenu);
			player.openInventory(ownedhousesmenu);
		} else if (ownedhouses > 9 && ownedhouses <= 18)
		{
			Inventory ownedhousesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned houses");
			createownedhouses(ownedhousesmenu, user);
			MenuClick.ownedhousesmap.put(uuid, ownedhousesmenu);
			player.openInventory(ownedhousesmenu);
		} else if (ownedhouses > 18 && ownedhouses <= 27)
		{
			Inventory ownedhousesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned houses");
			createownedhouses(ownedhousesmenu, user);
			MenuClick.ownedhousesmap.put(uuid, ownedhousesmenu);
			player.openInventory(ownedhousesmenu);
		} else if (ownedhouses > 27 && ownedhouses <= 36)
		{
			Inventory ownedhousesmenu = Bukkit.createInventory(null, 18, ColorOptions.messagesubjects + "Owned houses");
			createownedhouses(ownedhousesmenu, user);
			MenuClick.ownedhousesmap.put(uuid, ownedhousesmenu);
			player.openInventory(ownedhousesmenu);
		}
	}
	public void createownedhouses(Inventory ownedhouselist, User user)
	{
		UUID uuid = user.getUUID();
		Integer coinamount = user.getCoins();
		Integer gemamount = user.getGems();
		Integer salary = user.getSalary();
		Integer income = user.getIncome();
		Integer houseowned = user.getHouseAmount(false);
		
		ownedhouselist.setItem(0, MenuClick.addmenulist(ColorOptions.stats + "Finacial", Material.GOLD_INGOT, ColorOptions.stats + "Coins: " + ColorOptions.coinStats + coinamount, ColorOptions.stats + "Gems: " + ColorOptions.gemStats + gemamount, ColorOptions.stats + "Salary: " + ColorOptions.statsresults + salary, ColorOptions.stats + "Income: " + ColorOptions.statsresults + income));
    	ownedhouselist.setItem(4, MenuClick.addmenulist(ColorOptions.stats + "Houselist", Material.WORKBENCH, ChatColor.GRAY + "See the list of", ChatColor.GRAY + "your houses", "", ColorOptions.stats + "Current owned houses: " + ColorOptions.statsresults + houseowned));
    	ownedhouselist.setItem(7, MenuClick.addmenulist(ChatColor.GRAY + "Settings", Material.LEVER, ChatColor.GRAY + "Modify settings of houses"));
    	ownedhouselist.setItem(8, MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, ChatColor.GRAY + "Click here to go back to your personal menu"));
    	for (int i : house.getHouseIDList(null))
    	{
    		if (house.getHouseOwnerID(i) != 0)
    		{
        		if (house.getHouseOwnerID(i) == user.getID())
        		{
        			if (spawnpoint.getSpawnPointName(user.getSpawnpointID()).equalsIgnoreCase("house_" + i))
        			{
        	    		ownedhouselist.setItem(ownedhouseslot.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + house.getHouseName(i), Material.BED, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + house.getHousePrice(i), ChatColor.GREEN + "Current spawnpoint!", ChatColor.GRAY + "Click to sell!" ));
        	    		ownedhouseslot.put(uuid, ownedhouseslot.get(uuid)+1);
        			} else
        			{
        	    		ownedhouselist.setItem(ownedhouseslot.get(uuid), MenuClick.addmenulist(ColorOptions.statsformat + "Housename: " + ColorOptions.statsresults + house.getHouseName(i), Material.BED, ColorOptions.statsformat + "Price: " + ColorOptions.statsresults + house.getHousePrice(i), ChatColor.GRAY + "Click to sell!" ));
        	    		ownedhouseslot.put(uuid, ownedhouseslot.get(uuid)+1);
        			}
        		}
    		}
    	}
    	MenuClick.ownedhousesmap.put(uuid, ownedhouselist);
	}
}
