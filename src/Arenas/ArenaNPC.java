package Arenas;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import Handlers.EnterTownEvent;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Traits.ArenaMaster;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class ArenaNPC implements Listener
{
	public Main main;
	Arena arena = new Arena();
	SpawnPoint spawnpoint = new SpawnPoint();
	NPCRegistry registry = CitizensAPI.getNPCRegistry();
	
	public ArenaNPC(Main main) 
	{
		this.main = main;
	}

	@EventHandler
	public void onEnter(EnterTownEvent e)
	{
//		UUID uuid = e.getUUID();
//		Integer townID = e.getTownID();
//		
//		if (!DiscoverTown.playersinTown.isEmpty())
//		{
//			for (UUID targetUUID : DiscoverTown.playersinTown.keySet())
//			{
//				if (DiscoverTown.playersinTown.get(targetUUID) == townID)
//				{
//					spawnArenaMaster(townID);
//					break;
//				}
//			}
//		}
	}
	
	public void spawnArenaMaster(Integer townID)
	{
		for (Integer arenaID : arena.getArenaIDList(townID))
		{
			Bukkit.getConsoleSender().sendMessage("Found arenaID");
			Integer npcID = arena.getNPCID(arenaID);
			if (npcID == 0 || npcID == null)
			{
				Bukkit.getConsoleSender().sendMessage("NPC-ID is null");
				Integer spawnpointID = arena.getSpawnpointID(arenaID, "npc");
				if (spawnpointID != null)
				{
					Bukkit.getConsoleSender().sendMessage("Spawnpoint not null, commencing spawning");
				    NPC npc = registry.createNPC(EntityType.PLAYER, "Paladinen");
				    npc.setName("Arena Owner");
				    npc.addTrait(ArenaMaster.class);
				    npc.setProtected(true);
				    npc.spawn(spawnpoint.getSpawnPointLocation(spawnpointID));
				    arena.setNPCID(arenaID, npc.getId());
				}
			} else
			{
				Bukkit.getConsoleSender().sendMessage("NPC-ID: " + npcID);
			}
		}
	}
}
