package Teleport;

import org.bukkit.event.Listener;

import Main.Main;

public class TeleportMovement implements Listener
{
	public Main main;
	
	
	public TeleportMovement(Main main2) {
		// TODO Auto-generated constructor stub
	}


//	@EventHandler
//	public void onMovement(PlayerMoveEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		if (TeleportDelay.Delay.containsKey(uuid))
//		{
//			if (!TeleportDelay.hasImmune(uuid))
//			{
//				if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ())
//				{
//					TeleportDelay.cancelTeleport(uuid, true);
//				}
//			}
//		}
//	}
	
//	@EventHandler
//	public void onDamage(EntityDamageEvent e)
//	{
//		if (e.getEntity() instanceof Player)
//		{
//			Player player = (Player) e.getEntity();
//			UUID uuid = player.getUniqueId();
//			
//			if (TeleportDelay.Delay.containsKey(uuid))
//			{
//				if (!TeleportDelay.hasImmune(uuid))
//				{
//					TeleportDelay.cancelTeleport(uuid, false);
//				}
//			}
//		}
//	}
}
