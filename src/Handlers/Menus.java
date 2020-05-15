package Handlers;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import Donator.Donator;
import Menu.MenuClick;
import Properties.Property;
import Streets.Street;
import Towns.Town;
import Users.User;

public class Menus {
	public static String PersonalMenu = ColorOptions.messageformat + "Personal menu";
	public static String DuelSetupMenu = ColorOptions.stats + "Choose duel-type and place bets!";
	public static String DonatorMenu = ColorOptions.stats + "Donator-ranks' information";
	public static String FriendsManagerMenu = ColorOptions.stats + "Manage friends";
	public static String AddFriendsMenu = ColorOptions.stats + "Add friends";
	public static String FriendRequestMenu = ColorOptions.stats + "Friend requests";
	public static String GemShopMenu = ColorOptions.gemStats + "Gem-shop";
	public static String SkillMenu = ColorOptions.skillsformat + "Personal skills";
	public static String HouseListMenu = ColorOptions.messagesubjects + "House list";
	public static String PropertyListMenu = ColorOptions.messagesubjects + "Property list";
	public static String RoomListMenu = ColorOptions.messagesubjects + "Room list";
	public static String OwnedHousesMenu = ColorOptions.messagesubjects + "Owned houses/rooms";
	public static String OwnedPropertyMenu = ColorOptions.messagesubjects + "Owned properties";
	public static String PlayerManagerMenu = ColorOptions.stats + "Online-players Manager";
	public static String ShopItemsManagerMenu = ColorOptions.stats + "ShopItems Manager";
	public static String TeleportMenu = ColorOptions.stats + "Teleport to spawnpoints";
	public static String TitleMenu = ColorOptions.stats + "Title-information";
	public static String ForcedHouseSell = ColorOptions.stats + "Choose a house to remove";
	public static String ForcedPropertySell = ColorOptions.stats + "Choose a property to remove";
	public static String TutorialStartMenu = ColorOptions.messagesubjects + "Start Tutorial?";
	public static String AssignmentMenu = ColorOptions.messagesubjects + "Daily Assignments";
	public static String QuestMenu = ChatColor.YELLOW + "Quests";
	public static String AchievementMenu = ChatColor.GOLD + "Achievements";
	public static String SupportMenu = ColorOptions.coinStats + "In-game support";
	public static String ItemListMenu = ColorOptions.messagesubjects + "List of existing Items";
	public static String AssignmentSubMenu = ColorOptions.messagesubjects + "Assignments";
	public static String QuestDeliverMenu = ColorOptions.messagesubjects + "Deliver items for quest";
	public static String TutorialMenu = ChatColor.BLUE + "List of tutorials";
	public static String GateManagerMenu = ColorOptions.stats + "Gate Manager";
	public static String SOGateMenu = ColorOptions.messageachievement + "Change Gate";
	public static String EventManagerMenu = ColorOptions.stats + "Event Manager";
	public static String EventsMenu = ColorOptions.coinStats + "Events list";
	public static String HideAndSeekManagerMenu = ColorOptions.messagesubjects + "Manage Hide And Seek";
	public static String SiegeManagerMenu = ColorOptions.coinStats + "Manage Siege";
	public static String SiegeOverviewMenu = ColorOptions.coinStats + "Active Sieges";
	public static String SiegeInformationMenu = ColorOptions.coinStats + "Siege Information";
	public static String ScenarioManagerMenu = ColorOptions.messagesubjects + "Manage Scenario";
	public static String SideObjectiveManagerMenu = ColorOptions.messagesubjects + "Manage Side Objective";
	public static String SiegeSpawnpointsManagerMenu = ColorOptions.messagesubjects + "Manage Siege Spawnpoints";
	public static String SiegeSpawnpointsMenu = ColorOptions.messagesubjects + "Choose a place to spawn";
	
	
	public static ItemStack getFinancial(User user)
	{
		ItemStack item = null;
		
		int Coins = user.getCoins();
		int Gems = user.getGems();
		int Salary = user.getSalary();
		int Income = user.getIncome();
		
		item = MenuClick.addmenulist(
				ColorOptions.stats + "Financial", 
				Material.GOLD_INGOT, 
				ColorOptions.stats + "Coins: " + ColorOptions.coinStats + ColorOptions.formatCurrency(Coins), 
				ColorOptions.stats + "Gems: " + ColorOptions.gemStats + ColorOptions.formatCurrency(Gems), 
				ColorOptions.stats + "Salary: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Salary), 
				ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Income));
	
		return item;
	}
	
	public static ItemStack getSocialProfile(User user)
	{
		String userName = user.getUsername();
		String titleName = user.getTitleName();
		String genderName = user.getGenderName();
		int Exp = user.getExperience();
		int Coins = user.getCoins();
		int Gems = user.getGems();
		int houseAmount = user.getHouseAmount(false);
		int propertyAmount = user.getPropertyAmount(false);
		int Kills = user.getKills();
		int Deaths = user.getDeaths();
		int donatorID = user.getDonatorID();
		ChatColor Secondary = Donator.getDonatorColorSecondary(donatorID);
		ChatColor Primary = Donator.getDonatorColorPrimary(donatorID);
		
    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(user.getUsername());
		skullMeta.setDisplayName(ColorOptions.stats + "Social profile");
		List<String> lore = Arrays.asList(new String[]{
			ColorOptions.stats + "Username: " + ColorOptions.statsresults + userName + Secondary + "(" + Primary + user.getDonatorName() + Secondary + ")",
			ColorOptions.stats + "Title: " + ColorOptions.statsresults + titleName,
			ColorOptions.stats + "Gender: " + ColorOptions.statsresults + genderName,
			ColorOptions.stats + "Experience: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Exp),
			ColorOptions.stats + "Coins: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Coins),
			ColorOptions.stats + "Gems: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Gems),
			ColorOptions.stats + "Houses: " + ColorOptions.statsresults + houseAmount,
			ColorOptions.stats + "Properties: " + ColorOptions.statsresults + propertyAmount,
			ColorOptions.stats + "Kills: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Kills),
			ColorOptions.stats + "Deaths: " + ColorOptions.statsresults + ColorOptions.formatCurrency(Deaths),
			
		});
		skullMeta.setLore(lore);
		skull.setItemMeta(skullMeta);
		
		return skull;
	}
	
	public static ItemStack getBackButton(String previousMenuTitle)
	{
		ItemStack item = null;
		
		item = MenuClick.addmenulist(ColorOptions.falsecommand + "Back", Material.BARRIER, previousMenuTitle != null ? ChatColor.GRAY + "Click here to go back to " + previousMenuTitle : ColorOptions.message + "Click here to exit");
		
		return item;
	}
	
	public static ItemStack getPropertyInfo(Integer propertyID)
	{
		Property property = new Property();
		Street street = new Street();
		Town town = new Town();
		ItemStack item = null;
		
		String propertyName = property.getPropertyName(propertyID);
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		Integer streetNumber = property.getStreetNumber(propertyID);
		Integer townID = street.getTownID(streetID);
		String townName = town.getTownName(townID);
		Integer price = property.getPropertyPrice(propertyID);
		Integer propertyIncome = property.getIncome(propertyID);
		
		
		item = MenuClick.addmenulist(ColorOptions.stats + "Information about " + ColorOptions.statsresults + propertyName, Material.ANVIL, 
				ColorOptions.statsformat + "Location", 
				ColorOptions.stats + "Street: " + ColorOptions.statsresults + streetName,
				ColorOptions.stats + "Streetnumber: " + ColorOptions.statsresults + streetNumber,
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + townName,
				"",
				ColorOptions.statsformat + "Price and income",
				ColorOptions.stats  + "Price: " + ColorOptions.statsresults + ColorOptions.formatCurrency(price),
				ColorOptions.stats + "Income: " + ColorOptions.statsresults + ColorOptions.formatCurrency(propertyIncome));
		
		return item;
	}
	
	public static Integer getPropertyIDByInfoItem(ItemStack item)
	{
		Property property = new Property();
		Street street = new Street();
		Town town = new Town();
		Integer propertyID = null;
		
		if (!item.hasItemMeta())
		{
			return null;
		}
		if (!item.getItemMeta().hasLore())
		{
			return null;
		}
		
		List<String> lore = item.getItemMeta().getLore();
		
		String streetName = ChatColor.stripColor(lore.get(1)).split(": ")[1];
		Integer streetNumber = Integer.valueOf(ChatColor.stripColor(lore.get(2)).split(": ")[1]);
		String townName = ChatColor.stripColor(lore.get(3)).split(": ")[1];

		Integer streetID = street.getStreetID(streetName, town.getTownID(townName));
		
		propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
		
		return propertyID;
	}

}
