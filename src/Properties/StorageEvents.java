package Properties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import Handlers.ColorOptions;
import Handlers.StorageEvent;
import Main.Main;
import Streets.Street;
import Towns.Town;
import Users.User;

public class StorageEvents implements Listener
{
	Street street = new Street();
	Town town = new Town();
	Property property = new Property();
	private Main main;
	public StorageEvents(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onStorage(StorageEvent e)
	{
		Bukkit.getConsoleSender().sendMessage("Fired!");
		Integer propertyID = e.getID();
		Integer ownerID = property.getPropertyOwnerID(propertyID);
		String name = property.getPropertyName(propertyID);
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		String townName = town.getTownName(street.getTownID(streetID));
		for (User user : main.users)
		{
			Integer userID = user.getID();
			Player target = user.getPlayer();
			if (ownerID == userID)
			{
				target.sendMessage(ColorOptions.messageachievement + ColorOptions.messageArrow + "It looks like the storage of your property is full! Try transporting it to a warehouse");
				target.sendMessage(ColorOptions.statsbrackets);
				target.sendMessage(ColorOptions.stats + "-Name: " + ColorOptions.statsresults + name);
				target.sendMessage(ColorOptions.stats + "-Location: " + ColorOptions.statsresults + "street: " + streetName + ", streetnumber: " + property.getStreetNumber(propertyID) + ", town: " + townName);
				target.sendMessage(ColorOptions.messageachievement + "Start this asignment from your " + ColorOptions.messagesubjects + "Personal Menu" + ColorOptions.messageachievement + " or click the shopkeeper");
				target.sendMessage(ColorOptions.statsbrackets);
			}
		}
	}
}
