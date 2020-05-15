package KillsDeaths;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Houses.House;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;

public class RespawnLocation implements Listener
{
	House house = new House();
	SpawnPoint spawnpoint = new SpawnPoint();
	private Main main;
	public RespawnLocation(Main main) 
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void Respawn(PlayerRespawnEvent e)
//	{
//		Player player = e.getPlayer();
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
//		if (Main.HideAndSeek != null && Main.HideAndSeek.getProgress() && Main.HideAndSeek.getParticipating(user))
//		{
//			Integer townID = Main.HideAndSeek.getTownID();
//			
//			try
//			{
//				e.setRespawnLocation(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointIDbyTown(townID)));
//			} catch (Exception ex)
//			{
//				e.setRespawnLocation(user.getLastDeathLocation());
//			}
//		} else
//		if (user.getSpawnpointID() != 1)
//		{
//			if (!player.hasPermission("k&k.join.nolocation") || !player.isOp())
//			{
//				if (spawnpoint.getSpawnPointID("spawn") != null)
//				{
//					if (spawnpoint.getSpawnPointName(user.getSpawnpointID()) != "spawn")
//					{
//						e.setRespawnLocation(spawnpoint.getSpawnPointLocation(user.getSpawnpointID()));
//					} else
//					{
//						e.setRespawnLocation(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("spawn")));
//					}
//				} else if (player.isOp())
//				{
//					player.sendMessage(ColorOptions.falsecommand + "No spawn-location has been set! This might cause glitches or errors");
//				}
//			} else
//			{
//				if (spawnpoint.getSpawnPointID("spawn") != null)
//				{
//					if (spawnpoint.getSpawnPointName(user.getSpawnpointID()) != "spawn")
//					{
//						e.setRespawnLocation(spawnpoint.getSpawnPointLocation(user.getSpawnpointID()));
//					} else
//					{
//						e.setRespawnLocation(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("spawn")));
//					}
//				} else
//				{
//					player.sendMessage(ColorOptions.falsecommand + "No spawn-location has been set! This might cause glitches or errors");
//				}
//			}
//		} else
//		{
//			e.setRespawnLocation(spawnpoint.getSpawnPointLocation(user.getSpawnpointID())); 	
//		}
//	}
}
