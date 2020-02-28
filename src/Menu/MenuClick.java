package Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Arenas.ArenaMenuClick;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.Menus;
import Main.Main;
import Products.Product;
import Products.PropertyProduct;
import Skills.Skill;
import Tutorial.Tutorial;
import Tutorial.TutorialEvents;
import Users.User;
import Users.Users;

public class MenuClick implements Listener
{
	Menu menu = new Menu();
	Skill skill = new Skill();
	Product product = new Product();
	PropertyProduct proproduct = new PropertyProduct();
	private Main main;
	public MenuClick(Main main) 
	{
		this.main = main;
	}
	
	public static Map<UUID, Inventory> houselistmap = new HashMap<UUID, Inventory>();
	public static Map<UUID, Integer> houselistpage = new HashMap<UUID, Integer>();
	public static Map<UUID, Inventory> propertylistmap = new HashMap<UUID, Inventory>();
	public static Map<UUID, Integer> propertylistpage = new HashMap<UUID, Integer>();
	public static Map<UUID, Inventory> roomlistmap = new HashMap<UUID, Inventory>();
	public static Map<UUID, Integer> roomlistpage = new HashMap<UUID, Integer>();
	public static Map<UUID, Inventory> ownedhousesmap = new HashMap<UUID, Inventory>();
	public static Map<UUID, Inventory> ownedpropertiesmap = new HashMap<UUID, Inventory>();

	
	@EventHandler
	public void Onclick(InventoryClickEvent e)
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
		if (MenuCommand.pmenu.containsKey(uuid))
		{
			Inventory pmenu = MenuCommand.pmenu.get(uuid);
			String menuName = menu.getName();
			Bukkit.getConsoleSender().sendMessage(menu.getName());
			if (menuName.equalsIgnoreCase(Menus.PersonalMenu))
			{
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
					if (dc.contains("exit"))
					{
						p.closeInventory();
						tutorial.previousStage();
						user.playSound("back");
					}
					if (tutorial.getName().equalsIgnoreCase("room tutorial"))
					{
						if (tutorial.stage == 4)
						{
							if (dc.contains("houses"))
							{
								tutorial.nextStage(null, null);
								user.playSound("succesclick");
							}
						} else if (main.debug)
						{
							Bukkit.getConsoleSender().sendMessage("Not the correct stage: " + tutorial.stage);
						}
					} else if (tutorial.getName().equalsIgnoreCase("skills tutorial"))
					{
						if (tutorial.stage == 4)
						{
							if (dc.contains("skills"))
							{
								tutorial.nextStage(null, null);
								user.playSound("succesclick");
							}
						} else if (main.debug)
						{
							Bukkit.getConsoleSender().sendMessage("Not the correct stage: " + tutorial.stage);
						}
					} else if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Tutorial not the same");
					}
				} else
				{
					if (dc.contains("exit"))
					{
						p.closeInventory();
						user.playSound("back");
					}
					if (dc.contains("skills"))
					{
						this.menu.openSkillMenu(user);
						user.playSound("succesclick");
					}
					if (dc.contains("houselist"))
					{
						this.menu.openHouselist(user, 1, null);
						user.playSound("succesclick");
					}
					if (dc.contains("propertylist"))
					{
						this.menu.openPropertylist(user, 1, null, null);
						user.playSound("succesclick");
					}
					if (dc.contains("houses"))
					{
						this.menu.openownedHouses(user);
						user.playSound("succesclick");
					}
					if (dc.contains("properties"))
					{
						this.menu.openownedProperty(user);
						user.playSound("succesclick");
					}
					if (dc.contains("teleport to points on the map"))
					{
						this.menu.openTeleportMenu(user);
						user.playSound("succesclick");
					}
					if (dc.contains("current rank: "))
					{
						this.menu.openDonatorInfo(user);
						user.playSound("succesclick");
					}
					if (dc.contains("titles"))
					{
						this.menu.openTitleInfo(user);
						user.playSound("succesclick");
					}
					if (dc.contains("website"))
					{
						p.closeInventory();
						p.sendMessage(ColorOptions.stats + "Click: " + "https://www.planetminecraft.com/server/knights-and-kings-4045298/");
					}
					if (dc.contains("social"))
					{
						this.menu.openFriendsManager(user);
						user.playSound("succesclick");
					}
					if (dc.contains("shopitems"))
					{
						this.menu.openShopItemsManager(user);
						user.playSound("succesclick");
					}
					if (dc.contains("player manager"))
					{
						this.menu.openPlayerManager(user);
						user.playSound("succesclick");
					}
					if (dc.contains("refresh item-amount"))
					{
						for (Integer relationID : proproduct.getPropertyProductIDList())
						{
							Integer propertyID = proproduct.getPropertyID(relationID);
							Integer productID = proproduct.getProductID(relationID);
							proproduct.saveDailyProductAmount(propertyID, productID);
						}
						p.sendMessage(ColorOptions.messageachievement + "Succesfully refreshed the item-amount of all property-products");
						user.playSound("succesclick");
					}
					if (dc.contains("refresh item-price"))
					{
						for (Integer relationID : proproduct.getPropertyProductIDList())
						{
							Integer propertyID = proproduct.getPropertyID(relationID);
							Integer productID = proproduct.getProductID(relationID);
							proproduct.saveWeeklyProductPrice(propertyID, productID);;
						}
						p.sendMessage(ColorOptions.messageachievement + "Succesfully refreshed the item-price of all property-products");
						user.playSound("succesclick");
					}
					if (dc.contains("gem-shop"))
					{
						ArrayList<Integer> list = product.getIDListbyCategory("special", true);
						if (list != null && !list.isEmpty())
						{
							if (main.debug)
							{
								Bukkit.getConsoleSender().sendMessage("Gem-items found!");
							}
							this.menu.openGemShop(user);
							user.playSound("succesclick");
						}
					}
					if (dc.contains("assignments"))
					{
						this.menu.openAssignmentSubMenu(user);
						user.playSound("succesclick");
					}
					if (dc.contains("support"))
					{
						this.menu.openSupportMenu(user);
						user.playSound("succesclick");
					}
					if (dc.contains("gate manager"))
					{
						this.menu.openGateManager(user);
						user.playSound("succesclick");
					}
					if (dc.equalsIgnoreCase("event manager"))
					{
						this.menu.openEventManager(user);
						user.playSound("succesclick");
					}
					if (dc.equalsIgnoreCase("events"))
					{
						this.menu.openEvents(user);
						user.playSound("succesclick");
					}
				}
			} else if (menuName.equalsIgnoreCase(Menus.AddFriendsMenu))
			{
				new FriendManagerClick(main).AddFriendsClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.DonatorMenu))
			{
				new DonatorInfoClick(main).Onclick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.DuelSetupMenu))
			{
				new DuelSetupClick(main).onClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.ForcedHouseSell))
			{
				new OwnedhousesClick(main).ForcedHouseRemoveClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.ForcedPropertySell))
			{
				new OwnedpropertiesClick(main).ForcedPropertySellClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.FriendRequestMenu))
			{
				new FriendManagerClick(main).FriendRequestClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.FriendsManagerMenu))
			{
				new FriendManagerClick(main).ManageFriendsClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.GemShopMenu))
			{
				new GemShopClick(main).onClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.HouseListMenu))
			{
				new HouselistClick(main).onClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.PlayerManagerMenu))
			{
				new PlayerManagerClick(main).PlayerManagerClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.PropertyListMenu))
			{
				new PropertylistClick(main).onClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.RoomListMenu))
			{
				new RoomlistClick(main).onClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.ShopItemsManagerMenu))
			{
				new ItemMenuClick(main).ShopItemsManagerClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.SkillMenu))
			{
				new SkillMenuClick(main).onClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.TeleportMenu))
			{
				new SpawnpointClick(main).Onclick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.TitleMenu))
			{
				new TitleInfoClick(main).Onclick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.TutorialStartMenu))
			{
				new TutorialEvents(main).onClick(e, user);
			} else if (ChatColor.stripColor(menuName).contains("Arena: "))
			{
				new ArenaMenuClick(main).onArenaClick(e, user);
			} else if (ChatColor.stripColor(menuName).contains("choose a player to duel"))
			{
				new ArenaMenuClick(main).onDuelClick(e, user);
			} else if (ChatColor.stripColor(menuName).contains("Use ") && ChatColor.stripColor(menuName).contains("Item Coupon"))
			{
				new CouponClick(main).onClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.AssignmentSubMenu))
			{
				new AssignmentClick().onSubClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.AssignmentMenu))
			{
				new AssignmentClick().onAssignmentClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.AchievementMenu))
			{
				new AssignmentClick().onAchievementClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.QuestMenu))
			{
				new AssignmentClick().onQuestClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.SupportMenu))
			{
				new SupportMenuClick().onSupportMenuClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.ItemListMenu))
			{
				new SupportMenuClick().onItemListClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.TutorialMenu))
			{
				new SupportMenuClick().onTutorialListClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.GateManagerMenu))
			{
				new GateManagerClick().onGateManagerClick(e, user);
			} else if (ChatColor.stripColor(menuName).contains("Gate information"))
			{
				new GateManagerClick().onGateInformationClick(e, user);
			} else if (ChatColor.stripColor(menuName).contains("Choose gate material"))
			{
				new GateManagerClick().onGateMaterialClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.EventManagerMenu))
			{
				new EventManagerClick().onEventManagerClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.HideAndSeekManagerMenu))
			{
				new EventManagerClick().onHsManagerClick(e, user);
			} else if (ChatColor.stripColor(menuName).equalsIgnoreCase("Hide and Seek player"))
			{
				new EventManagerClick().onHsPlayerManagerClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.SiegeManagerMenu))
			{
				new EventManagerClick().onSiegeManagerClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.ScenarioManagerMenu))
			{
				new EventManagerClick().onScenarioManagerClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.SideObjectiveManagerMenu))
			{
				new EventManagerClick().onSideObjectiveManagerClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.EventsMenu))
			{
				new EventsClick().onEventsClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.SiegeOverviewMenu))
			{
				new EventsClick().onSiegeOverviewClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.SOGateMenu))
			{
				new EventManagerClick().onSOGateManagerClick(e, user);
			} else if (menuName.equalsIgnoreCase(Menus.SiegeInformationMenu))
			{
				new EventsClick().onSiegeInformationClick(e, user);
			}
			
			else
			{
//				Bukkit.getConsoleSender().sendMessage(Menus.HouseListMenu);
//				Bukkit.getConsoleSender().sendMessage("No menju found!");
			}
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
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
		if (p.getItemInHand().hasItemMeta())
		{
			if (p.getItemInHand().getItemMeta().hasDisplayName())
			{
				if (p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "personal menu"))
				{
					if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
					{
						this.menu.OpenPersonalMenu(user);
					}
				}
			}
		}
	}
    public static ItemStack addmenulist(String displayname, Material type, String... lore) 
    {

        ItemStack itemStack = new ItemStack(type, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayname);
        if (lore != null)
        {
            itemMeta.setLore(Arrays.asList(lore));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
	
    public static ItemStack addpropertylist(String displayname, Material type, String... lore) 
    {

        ItemStack itemStack = new ItemStack(type, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayname);
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    
}
