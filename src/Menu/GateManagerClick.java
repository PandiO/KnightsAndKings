package Menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import DataManager.Structures.Gates;
import Handlers.ColorOptions;
import Models.Structures.Gate;
import Products.Product;
import Users.User;

public class GateManagerClick 
{
	Menu menu = new Menu();
	Product product = new Product();
	
	public void onGateManagerClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		List<String> lore = clicked.getItemMeta().getLore();
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.OpenPersonalMenu(user);
			user.playSound("back");
		}
		if (lore != null && lore.get(0) != null)
		{
			String IDLine = ChatColor.stripColor(lore.get(0));
			Gate gate = Gates.findGate(Integer.valueOf(IDLine.split(": ")[1]));
			if (gate != null)
			{
				Bukkit.getConsoleSender().sendMessage("Gate click found: " + gate.getId());
				this.menu.openGateInformation(user, gate);
				user.playSound("succesclick");
			}
		}
	}
	
	public void onGateInformationClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		Inventory menu = e.getInventory();
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
		List<String> lore = clicked.getItemMeta().getLore();
		ItemStack gateItem = e.getInventory().getItem(4);
		Integer gateID = Integer.valueOf(ChatColor.stripColor(gateItem.getItemMeta().getLore().get(0)).split(": ")[1]);
		Gate gate = Gates.findGate(gateID);
		
		if (gate == null)
		{
			player.closeInventory();
			player.sendMessage(ColorOptions.error + "Something went wrong while retrieving the gate's data. Please notify a developer");
			return;
		}
		
		Integer maxHealthStep = 10;
		Integer healthStep = 10;
		
		List<String> st = menu.getItem(10).getItemMeta().getLore();
		List<String> st2 = menu.getItem(19).getItemMeta().getLore();
		maxHealthStep = Integer.valueOf(st.get(0).split(": ")[1]);
		healthStep = Integer.valueOf(st2.get(0).split(": ")[1]);
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openGateManager(user);
			user.playSound("back");
		}
		if (dc.equalsIgnoreCase("save changes"))
		{
			Gates.saveGate(gate);
			user.getPlayer().sendMessage(ColorOptions.messageachievement + ColorOptions.messageArrow + "Succesfully saved Gate " + gate.getId() + " to the Database!");
			user.playSound("succesclick");
		}
		if (dc.contains("Current name: "))
		{
			player.sendMessage(ColorOptions.error + "This function does not work yet");
		}
		if (dc.contains("Gate status: "))
		{
			gate.toggleClosed();
			menu.setItem(16, product.createItem(ColorOptions.message + "Gate status: " + (gate.getClosed() ? ColorOptions.error + "Closed" : ColorOptions.messageachievement + "Opened"), new ItemStack(Material.LEVER), false, ColorOptions.message + "Click here to toggle the gate"));
			user.playSound("succesclick");
		}
		if (dc.contains("Destroyed: "))
		{
			boolean destroyed = false;
			if (!gate.getDestroyed())
			{
				destroyed = true;
			}
			gate.setDestroyed(destroyed);
			menu.setItem(25, product.createItem(ColorOptions.message + "Destroyed: " + (gate.getDestroyed() ? ColorOptions.error + "Destroyed" : ColorOptions.messageachievement + "Intact"), new ItemStack(Material.LEVER), false, ColorOptions.message + "Toggle between destroyed and intact"));
			user.playSound("succesclick");
		}
		if (dc.contains("Material: "))
		{
			this.menu.openGateMaterialMenu(user, gate);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("add max. health"))
		{
			gate.addOriginalHealth(maxHealthStep);
			menu.setItem(10, product.createItem(ColorOptions.message + "Max. health: " + ChatColor.LIGHT_PURPLE + gate.getOriginalHealth(), new ItemStack(Material.GOLDEN_APPLE, 1), false, ColorOptions.message + "Steps: " + maxHealthStep));
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("remove max. health"))
		{
			gate.removeOriginalHealth(maxHealthStep);
			menu.setItem(10, product.createItem(ColorOptions.message + "Max. health: " + ChatColor.LIGHT_PURPLE + gate.getOriginalHealth(), new ItemStack(Material.GOLDEN_APPLE, 1), false, ColorOptions.message + "Steps: " + maxHealthStep));
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("add current health"))
		{
			if (e.getClick() == ClickType.SHIFT_LEFT)
			{
				gate.setHealth(gate.getOriginalHealth());
			} else
			{
				gate.addHealth(healthStep);
			}
			menu.setItem(19, product.createItem(ColorOptions.message + "Current health: " + ChatColor.LIGHT_PURPLE + gate.getHealth(), new ItemStack(Material.APPLE, 1), false, ColorOptions.message + "Steps: " + healthStep));
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("remove current health"))
		{
			gate.removeHealth(healthStep);
			menu.setItem(19, product.createItem(ColorOptions.message + "Current health: " + ChatColor.LIGHT_PURPLE + gate.getHealth(), new ItemStack(Material.APPLE, 1), false, ColorOptions.message + "Steps: " + healthStep));
			user.playSound("succesclick");
		}
		if (dc.contains("Max. health: "))
		{
			maxHealthStep = nextStep(maxHealthStep);
			menu.setItem(10, product.createItem(ColorOptions.message + "Max. health: " + ChatColor.LIGHT_PURPLE + gate.getOriginalHealth(), new ItemStack(Material.GOLDEN_APPLE, 1), false, ColorOptions.message + "Steps: " + maxHealthStep));
			user.playSound("succesclick");
		}
		if (dc.contains("Current health: "))
		{
			healthStep = nextStep(healthStep);
			menu.setItem(19, product.createItem(ColorOptions.message + "Current health: " + ChatColor.LIGHT_PURPLE + gate.getHealth(), new ItemStack(Material.APPLE, 1), false, ColorOptions.message + "Steps: " + healthStep));
			user.playSound("succesclick");
		}
	}
	
	public void onGateMaterialClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		List<String> lore = clicked.getItemMeta().getLore();
		ItemStack gateItem = e.getInventory().getItem(4);
		Integer gateID = Integer.valueOf(ChatColor.stripColor(gateItem.getItemMeta().getLore().get(0)).split(": ")[1]);
		Gate gate = Gates.findGate(gateID);
		
		if (gate == null)
		{
			player.closeInventory();
			player.sendMessage(ColorOptions.error + "Something went wrong while retrieving the gate's data. Please notify a developer");
			return;
		}
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openGateInformation(user, gate);
			user.playSound("back");
		}
		Integer productID = product.getProductIDbyDisplayName(clicked.getItemMeta().getDisplayName(), false);
		if (productID != null)
		{
			if (this.product.createPropertyItem(productID, 1, false, false).getType().isBlock())
			{
				gate.setMaterialID(productID);
				this.menu.openGateInformation(user, gate);
				user.playSound("succesclick");
			} else
			{
				player.sendMessage(ColorOptions.error + "This material cannot be used for a gate!");
			}
		} else
		{
			player.sendMessage(ColorOptions.error + "Something went wrong while retrieving material information. Please notify a developer");
		}
	}
	
	private Integer nextStep(Integer currentStep)
	{
		Integer step = 10;
		
		if (currentStep == 10)
		{
			step = 20;
		} else if (currentStep == 20)
		{
			step = 50;
		} else if (currentStep == 50)
		{
			step = 100;
		} else if (currentStep == 100)
		{
			step = 10;
		}
		
		return step;
	}
}
