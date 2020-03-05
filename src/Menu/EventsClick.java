package Menu;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Handlers.ColorOptions;
import Main.Main;
import Products.Product;
import Sieges.Siege;
import Sieges.Scenario;
import Towns.Town;
import Users.User;

public class EventsClick 
{
	Main main = Main.getPlugin(Main.class);
	Menu menu = new Menu();
	Product product = new Product();
	Town town = new Town();
	
	public void onEventsClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.OpenPersonalMenu(user);
			user.playSound("back");
		}
		if (dc.equalsIgnoreCase("hide and seek"))
		{
			if (main.HideAndSeek == null)
			{
				user.getPlayer().sendMessage(ColorOptions.error + "The Hide and Seek didn't start yet!");
			}
			this.menu.openHideAndSeekOverview(user);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("siege"))
		{
			this.menu.openSiegeOverview(user);
			user.playSound("succesclick");
		}
	}
	
	public void onSiegeOverviewClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openEvents(user);
			user.playSound("back");
		}
		
		if (dc.contains("siege "))
		{
			Integer index = Integer.valueOf(dc.split(" ")[1])-1;
			Siege siege = Sieges.Sieges.Sieges.get(index);
			
			if (siege == null)
			{
				user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving Siege information. Please notify a Developer");
				return;
			}
			
			this.menu.openSiegeInformation(user, siege, 0);
			user.playSound("succesclick");
		}
	}
	
	public void onSiegeInformationClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		Inventory menu = e.getInventory();
		ItemStack siegeItem = menu.getItem(4);
		Integer index = (Integer.valueOf(ChatColor.stripColor(siegeItem.getItemMeta().getDisplayName()).split(" ")[1])-1);
		Siege siege = Sieges.Sieges.Sieges.get(index);
		
		ItemStack pageItem = menu.getItem(27);
		Integer pageNumber = 0;
		if (pageItem.hasItemMeta() && pageItem.getItemMeta().hasLore())
		{
			pageNumber = Integer.valueOf(ChatColor.stripColor(pageItem.getItemMeta().getLore().get(3)).split(": ")[1]);
		}
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		
		if (siege == null)
		{
			this.menu.openSiegeOverview(user);
			user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving Siege information. Please try again");
		}
		
		if (dc.equalsIgnoreCase("back"))
		{
			if (siege.getParticipating(user))
			{
				this.menu.openEvents(user);
			} else
			{
				this.menu.openSiegeOverview(user);
			}
			user.playSound("back");
		}
		
		if (dc.contains("siege "))
		{
			siege.joinPlayer(user);
			user.getPlayer().sendMessage(ColorOptions.messageachievement + "Succesfully joined game of Siege!");
			
			this.menu.openSiegeInformation(user, siege, 0);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("next tab"))
		{
			this.menu.openSiegeInformation(user, siege, pageNumber++);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("previous tab"))
		{
			if (pageNumber > 0)
			{
				this.menu.openSiegeInformation(user, siege, pageNumber--);
				user.playSound("succesclick");
			}
		}
		if (dc.equalsIgnoreCase("random"))
		{
			siege.setRandomVotes(siege.getParticipant(user));
			user.sendMessage(ColorOptions.message + "Voted for a random scenario");
			user.playSound("succesclick");
			this.menu.openSiegeInformation(user, siege, 0);
		}
		for (Scenario scenario : siege.getSuggestedScenarioList())
		{
			if (dc.equalsIgnoreCase(scenario.getName()))
			{
				siege.setScenarioVote(siege.getParticipant(user), scenario);
				user.sendMessage(ColorOptions.message + "Voted for a scenario " + ColorOptions.messagesubjects + scenario.getName());
				user.playSound("succesclick");
				this.menu.openSiegeInformation(user, siege, 0);
				break;
			}
		}
	}
}
