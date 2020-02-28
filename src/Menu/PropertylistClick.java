package Menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Properties.Property;
import Streets.Street;
import Towns.Town;
import Users.User;

public class PropertylistClick
{
	private Main main;
	Property property = new Property();
	Street street = new Street();
	Town town = new Town();
	Menu menu = new Menu();
	public PropertylistClick(Main main) 
	{
		this.main = main;
	}
	private Float clickVolume = main.clickVolume;
	
	public void onClick(InventoryClickEvent e, User user)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player player = (Player) e.getWhoClicked();
			ItemStack clicked = e.getCurrentItem();
			
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
			List<String> lore = clicked.getItemMeta().getLore();
			e.setCancelled(true);
			if (dc.contains("Back"))
			{
				this.menu.openownedProperty(user);
			}
			if (dc.contains("Next"))
			{
				Integer current = Integer.valueOf(e.getCurrentItem().getItemMeta().getLore().get(2).split(": ")[1]);
				this.menu.openPropertylist(user, current+1, null, null);
				player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, this.clickVolume, 1.0F);
			}
			if (dc.contains("Previous"))
			{
				Integer current = Integer.valueOf(e.getCurrentItem().getItemMeta().getLore().get(2).split(": ")[1]);
				this.menu.openPropertylist(user, current-1, null, null);
				player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, this.clickVolume, 1.0F);
			}
			if (dc.contains("Town: "))
			{
				if (e.getClick() == ClickType.SHIFT_LEFT)
				{
					this.menu.openPropertylist(user, 1, null, null);
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
				this.menu.openPropertylist(user, 1, townID, null);
			}
			if (dc.contains("Property: "))
			{
				if (this.menu.getMenuStructureID(lore, "property", 0) != null)
				{
					Integer propertyID = this.menu.getMenuStructureID(lore, "property", 0);
					if (property.getPropertyOwnerID(propertyID) == 0)
					{
						player.closeInventory();
						Bukkit.dispatchCommand(player, "property buy " + propertyID);
						
					} else if (property.getPropertyOwnerID(propertyID) == user.getID())
					{
						player.sendMessage(ColorOptions.statsresults + "This property is owned by you!");
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "This property is already owned by someone else!");
					}
				}
			}
		}
	}
}
