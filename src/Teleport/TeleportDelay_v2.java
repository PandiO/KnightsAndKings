package Teleport;

import java.util.UUID;

import org.bukkit.Location;

public class TeleportDelay_v2 
{
	private static UUID Uuid;
	private static Integer Delay;
	private static Location Location;
	private static String Name;
	private static boolean Immune;
	
	public TeleportDelay_v2(UUID uuid, Integer delay, Location targetLocation, String targetName, boolean immune)
	{
		TeleportDelay_v2.Uuid = uuid;
		TeleportDelay_v2.Delay = delay;
		TeleportDelay_v2.Location = targetLocation;
		TeleportDelay_v2.Name = targetName;
		TeleportDelay_v2.Immune = immune;
	}
	
	public static UUID getUUID(TeleportDelay_v2 teleport)
	{
		return Uuid;
	}
	
	public static Integer getDelay(UUID uuid)
	{
		Integer delay = null;
		if (uuid == Uuid)
		{
			delay = Delay;
		}
		
		return delay;
	}
}
