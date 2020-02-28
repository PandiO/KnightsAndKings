package NPCs;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;
import Properties.Property;
import SpawnPoints.SpawnPoint;
import Towns.Town;
import Traits.Shopkeeper;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class ShopkeeperCommands implements CommandExecutor
{
	Town town = new Town();
	Property property = new Property();
	SpawnPoint spawnpoint = new SpawnPoint();
	public Main main;
	public ShopkeeperCommands(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("shopkeeper"))
		{
			if (sender.hasPermission("k&k.shopkeeper"))
			{
				if (args.length == 1)
				{
					if (args[0].equalsIgnoreCase("reload"))
					{
						reloadShopkeeper(sender);
					} else
					{
						sender.sendMessage(ColorOptions.falsecommand + "Usage: /shopkeeper reload");
					}
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /shopkeeper reload");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		return false;
	}
	
	public static void reloadShopkeeper(CommandSender sender)
	{
		Property property = new Property();
		SpawnPoint spawnpoint = new SpawnPoint();
		for (Integer propertyID : property.getIDList(null, null))
		{
			NPCRegistry registry = CitizensAPI.getNPCRegistry();
			Integer npcID = property.getNPCID(propertyID);
			if (npcID != null && npcID != 0)
			{
				NPC shopkeeper = registry.getById(npcID);
				if (shopkeeper != null)
				{
					shopkeeper.removeTrait(Shopkeeper.class);
					if (shopkeeper.isSpawned())
					{
						shopkeeper.despawn();
						Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Despawned the shopkeepers of property with ID " + propertyID + " in order to reload the traits!");
					}
					shopkeeper.addTrait(Shopkeeper.class);
					
					Integer spawnpointID = property.getPropertySpawnPoint(propertyID);
					if (spawnpointID != null && spawnpointID != 0)
					{
						if (!shopkeeper.isSpawned())
						{
							shopkeeper.spawn(spawnpoint.getSpawnPointLocation(spawnpointID));
							
				    		Bukkit.getConsoleSender().sendMessage(ColorOptions.message + "Spawned a shopkeeper for property with ID " + propertyID);
						}
					} else
					{
						for (Player player : Bukkit.getOnlinePlayers())
					    {
					    	if (player.isOp())
					    	{
					    		player.sendMessage(ColorOptions.error + "Cannot spawn a shopkeeper for property with ID " + propertyID + ", no spawnpoint set");
					    	}
					    }
					}
				} else
				{
					sender.sendMessage(ColorOptions.error + "Cannot find a NPC with the ID " + npcID + " received from property with ID " + propertyID);
				}
			}
		}
		sender.sendMessage(ColorOptions.messageachievement + "Succesfully reloaded all shopkeepers!");
	}
}
