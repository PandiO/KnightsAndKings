package Minigames;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import Main.Main;

public class OcelotSpawn implements Listener
{
	private Main main;
	public OcelotSpawn(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onSpawn(CreatureSpawnEvent e)
	{
		if (e.getEntityType() == EntityType.OCELOT)
		{
			e.setCancelled(false);
		}
	}
}
