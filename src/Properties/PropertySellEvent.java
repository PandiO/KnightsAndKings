package Properties;

import org.bukkit.event.Listener;

import API_methods.WorldGuard;
import Main.Main;
import Streets.Street;
import Towns.Town;

public class PropertySellEvent implements Listener
{
	
	Property property = new Property();
	Town town = new Town();
	Street street = new Street();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public PropertySellEvent(Main main) 
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void Confirm(PlayerChatEvent e)
//	{
//		Player player = (Player) e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(uuid);
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		if (PropertyCommands.sellconfirm.containsKey(uuid))
//		{
//			Integer propertyID = PropertyCommands.sellpropertyID.get(uuid);
//			Integer price = property.getPropertyPrice(propertyID);
//			Integer streetID = property.getStreetID(propertyID);
//			Integer townID = street.getTownID(streetID);
//			e.setCancelled(true);
//			if (e.getMessage().equalsIgnoreCase("yes"))
//			{				
//				property.RemovePropertyOwner(propertyID);
//				user.addCoins((price/2));
//				RegionManager manager = worldguard.getRegionManager(player.getWorld());
//				property.removeRegionOwner(player, propertyID, manager);
//
//				String propertyName = property.getPropertyName(propertyID);
//				player.sendMessage(ColorOptions.messageachievement + "-You succesfully sold " + propertyName + " on the " + street.getStreetName(streetID) + " with streetnumber " + property.getStreetNumber(propertyID) + " in town " + town.getTownName(townID));
//				player.sendMessage(ColorOptions.messageachievement + "-You received " + ColorOptions.formatCurrency((price/2)) + ColorOptions.coinStats + " coins");
//			} else if (e.getMessage().equalsIgnoreCase("no"))
//			{
//				player.sendMessage(ColorOptions.error + "-You canceled selling your property");
//			} else
//			{
//				player.sendMessage(ColorOptions.error + "-You canceled selling your property");
//			}
//			PropertyCommands.sellconfirm.remove(uuid);
//			PropertyCommands.sellpropertyID.remove(uuid);
//		}
//	}
}
