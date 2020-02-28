package Products;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class CraftEvents implements Listener
{
	ProductCategory category = new ProductCategory();
	Product product = new Product();
	ItemType type = new ItemType();
	Main main = Main.getPlugin(Main.class);
	public CraftEvents(Main main) 
	{
		this.main = main;
		// TODO Auto-generated constructor stub
	}
	
	@EventHandler
	public void onCraft(PrepareItemCraftEvent e)
	{
		Player player = (Player) e.getViewers().get(0);
		UUID uuid = player.getUniqueId();
		
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			e.getInventory().setResult(null);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			e.getInventory().setResult(null);
			return;
		}
		if (!user.inOwnerModus())
		{
			Integer typeID = e.getRecipe().getResult().getTypeId();
			Integer itemTypeID = type.getIDbyMaterialID(typeID.toString());
			if (itemTypeID != null)
			{
				Integer productID = product.getProductIDbyItemTypeID(itemTypeID);
				if (productID != null)
				{
					String category = this.category.getCategoryName(product.getCategoryID(productID, false));
					if (category.equalsIgnoreCase("meat") || category.equalsIgnoreCase("fish") || category.equalsIgnoreCase("baked-goods") || category.equalsIgnoreCase("vegetables") || category.equalsIgnoreCase("furniture"))
					{
						e.getInventory().setResult(product.createPropertyItem(productID, e.getInventory().getResult().getAmount(), false, false));
						return;
					}
				}
			}
			e.getInventory().setResult(null);
		}
	}
}
