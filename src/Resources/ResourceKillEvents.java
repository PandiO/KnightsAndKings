package Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import Assignments.Assignment;
import Assignments.AssignmentHarvestRandom;
import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Main.Main;
import Products.Product;
import Users.User;
import Users.Users;

public class ResourceKillEvents implements Listener
{
	Product product = new Product();
	private Main main;
	public ResourceKillEvents(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onKill(EntityDeathEvent e)
	{
		Entity entity = e.getEntity();
		EntityType type = entity.getType();
		List<ItemStack> drops = e.getDrops();
		List<ItemStack> newDrops = new ArrayList<ItemStack>();
		Assignment Assignment = null;
		User user = null;
		
		if (e.getEntity().getKiller() instanceof Player)
		{
			Player player = (Player) e.getEntity().getKiller();
			UUID uuid = player.getUniqueId();
			
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
		}
		if (user != null)
		{
			for (Assignment assignment : user.getAssignmentList())
			{
				if (assignment instanceof AssignmentHarvestRandom)
				{
					Assignment = (AssignmentHarvestRandom) assignment;
					break;
				}
			}
		}
		for (ItemStack drop : drops)
		{
			if (drop.getType() == Material.RAW_CHICKEN)
			{
				Integer productID = product.getProductID("rawchicken", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.COOKED_CHICKEN)
			{
				Integer productID = product.getProductID("cookedchicken", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.PORK)
			{
				Integer productID = product.getProductID("rawporkchop", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.GRILLED_PORK)
			{
				Integer productID = product.getProductID("cookedporkchop", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.RAW_BEEF)
			{
				Integer productID = product.getProductID("rawbeef", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.COOKED_BEEF)
			{
				Integer productID = product.getProductID("steak", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.MUTTON)
			{
				Integer productID = product.getProductID("rawmutton", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.COOKED_MUTTON)
			{
				Integer productID = product.getProductID("cookedmutton", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.RABBIT)
			{
				Integer productID = product.getProductID("rawrabbit", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.COOKED_RABBIT)
			{
				Integer productID = product.getProductID("cookedrabbit", false);
				if (productID != null)
				{
					newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
				}
			} else if (drop.getType() == Material.GOLD_NUGGET || drop.getType() == Material.GOLD_INGOT || drop.getType() == Material.GOLD_BLOCK || drop.getType() == Material.DIAMOND || drop.getType() == Material.DIAMOND_BLOCK)
			{
				newDrops.add(drop);
			} else if (drop.getType() == Material.GOLD_INGOT)
			{
				
			} else if (drop.hasItemMeta() && product.getProductIDbyDisplayName(drop.getItemMeta().getDisplayName(), false) != null)
			{
				newDrops.add(drop);
			}
			if (Assignment != null && Assignment instanceof AssignmentHarvestRandom)
			{
				AssignmentHarvestRandom assignment = (AssignmentHarvestRandom) Assignment;
				assignment.Harvest(drop);
			}
		}
		e.getDrops().clear();
		e.getDrops().addAll(newDrops);
		
	}
}
