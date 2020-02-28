package Products;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import API_methods.WorldEdit;
import API_methods.WorldGuard;
import Genders.Gender;
import Main.Main;
import Properties.Property;
import Titles.Title;
import Users.offlineUser;

public class DropItem implements Listener
{
	WorldGuard worldguard = new WorldGuard();
	Property property = new Property();
	WorldEdit worldedit = new WorldEdit();
	PropertyProduct propertyproduct = new PropertyProduct();
	Product product = new Product();
	offlineUser user = new offlineUser();
	Title title = new Title();
	Gender gender = new Gender();
	ProductCategory productCat = new ProductCategory();
	private Main main;
	public DropItem(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
    public void test(PlayerDropItemEvent e) 
	{
        Entity entity = e.getItemDrop();
        ItemStack item = e.getItemDrop().getItemStack();
        if (item.hasItemMeta())
        {
        	if (item.getItemMeta().hasDisplayName())
        	{
                String display = e.getItemDrop().getItemStack().getItemMeta().getDisplayName();
                entity.setCustomName(display);
                entity.setCustomNameVisible(true);
        	}
        }
    }
	
	@EventHandler
	public void test2(ItemSpawnEvent e)
	{
		Item item = e.getEntity();
        ItemStack stack = item.getItemStack();
        if (stack.hasItemMeta())
        {
        	if (stack.getItemMeta().hasDisplayName())
        	{
                String display = stack.getItemMeta().getDisplayName();
                item.setCustomName(display);
                item.setCustomNameVisible(true);
        	}
        }
	}
}
