package Treasure;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;

public class TreasureEvents implements Listener
{
	private Main main;
	private SpawnPoint spawnpoint = new SpawnPoint();
	public TreasureEvents(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
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
		Integer userID = user.getID();
		Action action = e.getAction();
		Block block = e.getClickedBlock();
		if (action == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.CHEST)
		{
			Location location = block.getLocation();
			Treasure treasure = Treasures.findTreasure(location);
			if (Treasures.createTreasure.containsKey(uuid))
			{
				e.setCancelled(true);
				if (treasure != null)
				{
					player.sendMessage(ColorOptions.error + "This chest already is a treasure!");
				} else
				{
					Treasures.createTreasure(location, Treasures.createTreasure.get(user.getUUID()));
					int spawnpointID = this.spawnpoint.getSpawnPointIDbyLocation(location);
					
					if (spawnpointID != -1)
					{
						Treasures.instantiateTreasure(Treasures.getTreasureID(spawnpointID), true);
					}
					player.sendMessage(ColorOptions.messageachievement + "Succesfully created a treasure for this chest!");
					Treasures.createTreasure.remove(uuid);
				}
			} else
			{
				if (treasure != null)
				{
					e.setCancelled(true);
					if (treasure.GetDiscoveredList().isEmpty() || !treasure.GetDiscoveredList().contains(userID))
					{
						player.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Opening treasure...");
						treasure.openTreasure(user);
					} else
					{
						player.sendMessage(ColorOptions.error + "You already discovered this treasure!");
					}
				} else
				{
					main.logMessage("No treasure found on location");
				}
			}
		} else if (Treasures.createTreasure.containsKey(uuid))
		{
			e.setCancelled(true);
			player.sendMessage(ColorOptions.error + "Right-click a chest to create a treasure or type /treasure cancel");
		}
	}
}
