package Houses;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Arenas.Arena;
import Handlers.ColorOptions;
import Handlers.Messages;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Users.User;
import Users.Users;

public class HomeCommands implements CommandExecutor
{
	SpawnPoint spawnpoint = new SpawnPoint();
	public Main main;
	public HomeCommands(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("home"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				if (player.hasPermission("k&k.home"))
				{
					UUID uuid = player.getUniqueId();
					User user = null;
					
					if ((user = Users.getUser(uuid)) != null)
					{
						Integer spawnpointID = user.getSpawnpointID();
						if (spawnpointID != spawnpoint.getSpawnPointID("spawn"))
						{
							if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
							{
								player.teleport(spawnpoint.getSpawnPointLocation(spawnpointID));
								player.sendMessage(ColorOptions.messageformat + "You teleported to your home");
							} else
							{
								Arena arena = new Arena();
								if (arena.isDuelling(uuid) == true)
								{
									player.sendMessage(ColorOptions.error + "You can't do this when in a duel!");
									return false;
								}
								spawnpoint.tryRegularTeleport(user, spawnpoint.getSpawnPointName(spawnpointID));
							}
						} else
						{
							player.sendMessage(ColorOptions.error + "You don't have a home, buy a house or rent a room to teleport to");
						}
					} else
					{
						player.sendMessage(Messages.userNullPointer);
					}
				} else
				{
					player.sendMessage(ColorOptions.falsecommand + "You don't have permission to perform this command!");
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
			}
		}
		return false;

	}
}
