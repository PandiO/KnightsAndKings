package Rooms;

import org.bukkit.event.Listener;

import API_methods.WorldGuard;
import Main.Main;
import Properties.Property;
import Streets.Street;
import Towns.Town;

public class RoomSellEvent implements Listener
{
	
	Room room = new Room();
	Town town = new Town();
	Property property = new Property();
	Street street = new Street();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public RoomSellEvent(Main main) 
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
//		if (RoomCommands.sellconfirm.containsKey(uuid))
//		{
//			Integer roomID = RoomCommands.sellRoomID.get(uuid);
//			Integer propertyID = room.getPropertyID(roomID);
//			String propertyName = property.getPropertyName(propertyID);
//			Integer streetID = property.getStreetID(propertyID);
//			Integer townID = street.getTownID(streetID);
//			Integer streetNumber = property.getStreetNumber(propertyID);
//			Integer roomNumber = room.getRoomNumber(roomID);
//			
//			e.setCancelled(true);
//			if (e.getMessage().equalsIgnoreCase("yes"))
//			{				
//				room.sellRoom(user, roomID);
//			} else if (e.getMessage().equalsIgnoreCase("no"))
//			{
//				player.sendMessage(ColorOptions.error + "-You keep renting a room with roomnumber " + roomNumber + " in the tavern " + propertyName + " on the " + street.getStreetName(streetID) + " with streetnumber " + streetNumber + " in town " + town.getTownName(townID));
//			} else
//			{
//				player.sendMessage(ColorOptions.error + "-You keep renting a room with roomnumber " + roomNumber + " in the tavern " + propertyName + " on the " + street.getStreetName(streetID) + " with streetnumber " + streetNumber + " in town " + town.getTownName(townID));
//			}
//			RoomCommands.sellconfirm.remove(uuid);
//			RoomCommands.sellRoomID.remove(uuid);
//		}
//	}
}
