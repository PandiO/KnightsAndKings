package UsefulCommands;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import Main.Main;

public class EnderchestViewEvent implements Listener
{
	private Main main;
	public EnderchestViewEvent(Main main)
	{
		this.main = main;
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e)
	{
		if (e.getPlayer() instanceof Player)
		{
			Player player = (Player) e.getPlayer();
			UUID uuid = player.getUniqueId();
			if (EnderchestCommand.offlineEnderchest.containsKey(uuid))
			{
				Player target = EnderchestCommand.offlineEnderchest.get(uuid);
				target.saveData();
			}
		}
	}
}
