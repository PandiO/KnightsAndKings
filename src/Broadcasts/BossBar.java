package Broadcasts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.EnderDragon;

import Events.FridayLottery;
import Handlers.ColorOptions;
import Main.Main;

public class BossBar
{
	FridayLottery lottery = new FridayLottery();
	public static HashMap<UUID, EnderDragon> barList = new HashMap<UUID, EnderDragon>();
	private static Integer lastMSG = 0;
	public static ArrayList<String> messages = new ArrayList<String>(Arrays.asList(
			ColorOptions.error + "This is the open Beta",
			ChatColor.BLUE + "Official opening 1st of December 2018",
			ColorOptions.messageformat + "Enter the weekly lottery with " + ColorOptions.messagesubjects + "/lottery"
			));
	Main main = Main.getPlugin(Main.class);

	
//	public void newBar(Player player)
//	{
//		Location loc = player.getLocation();
//		WorldServer world = ((CraftWorld) player.getLocation().getWorld()).getHandle();
//
//		EntityWither bar;
//		bar = new EntityWither(world);
//        bar.setLocation(loc.getX(), loc.getY() - 50, loc.getZ(), 0, 0);
//		bar.setCustomName(messages.get(0));
//		bar.setInvisible(true);
//		
//		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(bar);
//		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//		barList.put(player.getUniqueId(), bar);
//	}
//	
//	public void updateBar(Player player)
//	{
//		UUID uuid = player.getUniqueId();
//		if (barList.containsKey(uuid))
//		{
//			EntityWither bar = barList.get(uuid);
//			if (lastMSG+1 < messages.size())
//			{
//				bar.setCustomName(messages.get(lastMSG++));
//				lastMSG = lastMSG++;
//			} else
//			{
//				bar.setCustomName(messages.get(0));
//				lastMSG = 0;
//			}
//		}
//	}
//	
//	public void removeBar(Player player)
//	{
//		UUID uuid = player.getUniqueId();
//		if (barList.containsKey(uuid))
//		{
//			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(barList.get(uuid).getId());
//            barList.remove(uuid);
//            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//		}
//	}
//	
//	public void updateBarLocation(Player player)
//	{
//		UUID uuid = player.getUniqueId();
//		if (barList.containsKey(uuid))
//		{
//			Location loc = player.getLocation();
//			EntityWither bar = barList.get(uuid);
//			bar.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
//		}
//	}
}
