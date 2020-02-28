package Menu;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Handlers.ColorOptions;
import Houses.House;
import Main.Main;
import Streets.Street;
import Towns.Town;
import Users.User;
import Users.offlineUser;

public class HouselistClick
{
	offlineUser user = new offlineUser();
	Menu menu = new Menu();
	House house = new House();
	Street street = new Street();
	Town town = new Town();
	private Main main;
	public HouselistClick(Main main) 
	{
		this.main = main;
	}
	
	public void onClick(InventoryClickEvent e, User user)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player p = (Player) e.getWhoClicked();
			ItemStack clicked = e.getCurrentItem();
			UUID uuid = p.getUniqueId();
			Inventory menu = e.getInventory();
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
			List<String> lore = clicked.getItemMeta().getLore();
			e.setCancelled(true);
			Bukkit.getConsoleSender().sendMessage("Found click in onClick");
			if (dc.contains("Back"))
			{
				Bukkit.getConsoleSender().sendMessage("Found  back click in onClick");
				this.menu.openownedHouses(user);
			}
			if (dc.contains("Next"))
			{
				Integer current = Integer.valueOf(e.getCurrentItem().getItemMeta().getLore().get(2).split(": ")[1]);
				this.menu.openHouselist(user, current+1, null);
			}
			if (dc.contains("Previous"))
			{
				Integer current = Integer.valueOf(e.getCurrentItem().getItemMeta().getLore().get(2).split(": ")[1]);
				this.menu.openHouselist(user, current-1, null);
			}
			if (dc.contains("Town: "))
			{
				if (e.getClick() == ClickType.SHIFT_LEFT)
				{
					this.menu.openHouselist(user, 1, null);
					return;
				}
				List<String> townList = town.getTownNameList();
				Integer townID = null;
				String townfilter = dc.split(": ")[1];
				if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("Report: townfilter: " + townfilter);
					Bukkit.getConsoleSender().sendMessage(townList.toString());
				}
				if (townfilter.equalsIgnoreCase("all"))
				{
					townID = town.getTownID(townList.get(0));
				} else if (townList.contains(townfilter))
				{
					Integer index = (townList.indexOf(townfilter)+1);
					if (index < townList.size())
					{
						townID = town.getTownID(townList.get(index));
						if (main.debug)
						{
							Bukkit.getConsoleSender().sendMessage("Report: townfilter: " + townfilter + ", index: " + index + ", next town: " + townList.get(index) + ", list size: " + townList.size());
						}
					} else
					{
						townID = null;
					}
				} else
				{
					if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Town is not a valid town");
						Bukkit.getConsoleSender().sendMessage("Towns: " + townList.toString());
					}
					townID = town.getTownID(townList.get(0));
				}
				this.menu.openHouselist(user, 1, townID);
			}
			if (dc.contains("Housename: "))
			{
				if (this.menu.getMenuStructureID(lore, "house", 0) != null)
				{
					Integer houseID = this.menu.getMenuStructureID(lore, "house", 0);
					if (house.getHouseOwnerID(houseID) == 0)
					{
						p.closeInventory();
						Bukkit.dispatchCommand(p, "house buy " + houseID);
						
					} else if (house.getHouseOwnerID(houseID) == user.getID())
					{
						p.sendMessage(ColorOptions.statsresults + "This house is owned by you!");
					} else
					{
						p.sendMessage(ColorOptions.falsecommand + "This house is already owned by someone else!");
					}
				}
			}
		}
	}
}
