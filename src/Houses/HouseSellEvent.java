package Houses;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Streets.Street;
import Towns.Town;
import Users.User;
import Users.Users;

public class HouseSellEvent implements Listener
{
	
	House house = new House();
	Town town = new Town();
	Street street = new Street();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public HouseSellEvent(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void Confirm(PlayerChatEvent e)
	{
		Player player = (Player) e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
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
		
		if (HouseCommands.sellconfirm.containsKey(uuid))
		{
			Integer houseID = HouseCommands.sellpropertyID.get(uuid);
			Integer price = house.getHousePrice(houseID);
			Integer streetID = house.getStreetID(houseID);
			Integer townID = street.getTownID(streetID);
			Integer streetNumber = house.getHouseNumber(houseID);
			e.setCancelled(true);
			if (e.getMessage().equalsIgnoreCase("yes"))
			{				
				house.RemoveHouseOwner(houseID);
				user.removeHouseAmount(false, 1);
				user.addCoins((price/2));
				RegionManager manager = worldguard.getRegionManager(player.getWorld());
				house.removeRegionOwner(player, houseID, manager);

				String houseName = house.getHouseName(houseID);
				player.sendMessage(ColorOptions.messageachievement + "-You succesfully sold " + houseName + " on the " + street.getStreetName(streetID) + " with streetnumber " + streetNumber + " in town " + town.getTownName(townID));
				player.sendMessage(ColorOptions.messageachievement + "-You received " + ColorOptions.formatCurrency((price/2)) + ColorOptions.coinStats + " coins");
				
				if (house.getHouseSpawnPoint(houseID) != 0 && user.getSpawnpointID() == house.getHouseSpawnPoint(houseID))
				{
					user.removeSpawnpoint();
					player.sendMessage(ColorOptions.error + "Your personal spawnpoint has been set to default");
				}
			} else if (e.getMessage().equalsIgnoreCase("no"))
			{
				player.sendMessage(ColorOptions.error + "-You canceled selling your house");
			} else
			{
				player.sendMessage(ColorOptions.error + "-You canceled selling your house");
			}
			HouseCommands.sellconfirm.remove(uuid);
			HouseCommands.sellpropertyID.remove(uuid);
		}
	}
}
