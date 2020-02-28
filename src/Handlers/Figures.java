package Handlers;

public class Figures 
{
//	Arguments are type, x, y ,z, radiusX, radiusY, radiusZ, speed/data, amount

	
//////////////Helix on the z-coordinate
//	BukkitTask i = new BukkitRunnable()
//	{
//		double t = 0;
//    	Location play = p.getLocation().clone();
//        public void run()
//        {
//        	t = t + 0.5;
//        	Bukkit.getConsoleSender().sendMessage("Time: " + t);
//        	double x = 1*Math.cos(t);
//        	double y = 1*Math.sin(t);
//        	double z = t;
//        	play.add(x, y, z);
////        	Arguments are type, x, y ,z, radiusX, radiusY, radiusZ, speed/data, amount
//        	PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME,true, (float) (play.getX()), (float) (play.getY()), (float) (play.getZ()), 0, 0, 0, 0, 1);
//			for (Player p1 : Bukkit.getOnlinePlayers())
//			{
//				  ((CraftPlayer) p1).getHandle().playerConnection.sendPacket(packet);
//			}
//			play.subtract(x, y, z);
//			if (t > 100)
//			{
//				this.cancel();
//			}
//        }
//	}.runTaskTimer(main, 0, 1);
	
	
//	BukkitTask i = new BukkitRunnable()
//	{
//		double t = 0;
//		double r = 1.5;
//		float s = 0;
//    	Location play = p.getLocation().clone();
//    	Location play1 = p.getLocation().clone();
//        public void run()
//        {
//        	t = t + 0.5;
//        	Bukkit.getConsoleSender().sendMessage("Time: " + t);
//        	double x = r*Math.cos(t);
//        	double y = r*Math.sin(t);
//        	double z = 0;
//        	play.add(x, y, z);
//        	PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE,true, (float) (play.getX()), (float) (play.getY()), (float) (play.getZ()), 0, 0, 0, s, 1);
//        	((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
//			play.subtract(x, y, z);
//			
//			double x1 = r*Math.cos(t);
//        	double y1 = 0;
//        	double z1 = r*Math.sin(t);
//        	play1.add(x1, y1, z1);
//        	PacketPlayOutWorldParticles packet1 = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE,true, (float) (play1.getX()), (float) (play1.getY()), (float) (play1.getZ()), 0, 0, 0, s, 1);
//        	((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
//			play1.subtract(x1, y1, z1);
//			
//			double x2 = 0;
//        	double y2 = r*Math.cos(t);
//        	double z2 = r*Math.sin(t);
//        	PacketPlayOutWorldParticles packet2 = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE,true, (float) (play.clone().getX()+x2), (float) (play.clone().getY()+y2), (float) (play.clone().getZ()+z2), 0, 0, 0, s, 1);
//        	((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet2);
//			
//			if (t > 100)
//			{
//				this.cancel();
//			}
//        }
//	}.runTaskTimer(main, 0, 2);
}
