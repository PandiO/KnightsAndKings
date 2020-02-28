package Kits;

import org.bukkit.entity.Player;

import Main.Main;
import Products.Product;

public class Kit 
{
	Product product = new Product();
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	public void starterKit(Player target)
	{
		target.getInventory().setItemInHand(product.createPropertyItem(product.getProductID("beginnersword", false), 1, false, false));
		target.getInventory().addItem(product.createPropertyItem(product.getProductID("steelaxe", false), 1, false, false));
		target.updateInventory();
	}
}
