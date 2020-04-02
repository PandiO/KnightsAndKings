package Currency;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import DataManager.Worldguard;
import Handlers.ColorOptions;
import Handlers.RentPaymentEvent;
import Handlers.SoundHandler;
import Main.Main;
import Properties.Property;
import Rooms.Room;
import Users.User;
import Users.Users;

public class RentPayment implements Listener
{
	private Main main;
	Room room = new Room();
	Property property = new Property();
	WorldGuard worldguard = new WorldGuard();
	public RentPayment(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void Payout(RentPaymentEvent e)
	{
		UUID uuid = e.getUUID();
		User user = Users.getUser(uuid);
		if (user != null)
		{
			user.saveRentTime();

			Player player = Bukkit.getServer().getPlayer(user.getUsername());
			Integer roomID = room.getRoomIDbyOwner(uuid);
			if (roomID != null && roomID != 0)
			{
				Integer rent = room.getPrice(roomID);
				if (user.getCoins() >= rent)
				{
					user.removeCoins(rent);
					
					String gender = user.getGenderName();
					if (gender.equalsIgnoreCase("male"))
					{
						player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Mylord! You paid the rent for your room: " +  rent);
					} else if (gender.equalsIgnoreCase("female"))
					{
						player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Mylady! You paid the rent for your room: " + rent);
					}
				} else
				{
					player.sendMessage(ColorOptions.error + "" + ChatColor.BOLD + "You couldn't pay the rent of your room so you were forced to sell it!");
					
					room.removeOwnerID(roomID);
					user.removeRoomAmount(false, 1);
					user.removeRentTime();
					RegionManager manager = Worldguard.getRegionManager(player.getWorld());
					room.removeRegionOwner(player.getUniqueId(), roomID, manager);
					
					if (room.getSpawnPointID(roomID) != 0 && user.getSpawnpointID() == room.getSpawnPointID(roomID))
					{
						user.removeSpawnpoint();
						player.sendMessage(ColorOptions.error + "Your personal spawnpoint has been set to default");
					}
				}
				player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
			}
		}
	}
}
