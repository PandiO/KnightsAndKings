package Gates;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import DataManager.Structures.Gates;
import Main.Main;
import Models.Structures.Gate;

public class GateAnimation 
{
	private Main main = Main.getPlugin(Main.class);
	
	private Gate gate;
	private ProtectedRegion region;
	private CuboidRegion cuRegion;
	private boolean isClosed;
	private ItemStack fromMaterial;
	private ItemStack toMaterial;
	private double maxY;
	private double minY;
	private double currentY;
	private World world;
	private Sound animationSound;
	private int animationSpeed = 10;
	private BukkitTask animationTask;
	private boolean isInit;
	
	public GateAnimation(ItemStack fromMaterial, ItemStack toMaterial, Gate gate, boolean isInit)
	{
		this.gate = gate;
		this.world = Bukkit.getWorld("world");
		this.region = gate.getRegion();
		this.cuRegion = new CuboidRegion(BukkitUtil.getLocalWorld(world), region.getMinimumPoint(), region.getMaximumPoint());
		this.isClosed = gate.getClosed();
		this.fromMaterial = fromMaterial;
		this.toMaterial = toMaterial;
		this.maxY = region.getMaximumPoint().getBlockY();
		this.minY = region.getMinimumPoint().getBlockY();
		this.isInit = isInit;
		if (this.isClosed)
		{
			this.currentY = this.maxY;
			this.animationSound = Sound.CHEST_CLOSE;
		} else
		{
			this.currentY = this.minY;
			this.animationSound = Sound.CHEST_OPEN;
		}
		if (this.gate.getDestroyed())
		{
			this.animationSpeed = 1;
		}
		this.playAnimation();
	}
	
	public void playAnimation()
	{
		if (!isInit)
		{
			this.world.playSound(gate.getGateCenter(), this.animationSound, 2.0F, 0.5F);
		}
		this.animationTask = new BukkitRunnable()
		{
			public void run()
			{
				for (BlockVector blockv : cuRegion) 
				{
				    Block block = BukkitUtil.toBlock(new BlockWorldVector(BukkitUtil.getLocalWorld(world), blockv));
				    if (block.getLocation().getY() == currentY)
				    {
					    if (block.getType() == fromMaterial.getType())
					    {
						    block.setType(toMaterial.getType());
						    block.setData(toMaterial.getData().getData());
					    }
				    }
				}
				if (isClosed)
				{
					currentY -= 1;
					if (currentY < minY)
					{
						complete();
					}
				} else
				{
					currentY += 1;
					if (currentY > maxY)
					{
						complete();
					}
				}
			}
		}.runTaskTimerAsynchronously(main, 0, this.animationSpeed);
	}
	
	public void complete()
	{
		for (BlockVector blockv : cuRegion) 
		{
		    Block block = BukkitUtil.toBlock(new BlockWorldVector(BukkitUtil.getLocalWorld(world), blockv));
		    block.setType(toMaterial.getType());
		    block.setData(toMaterial.getData().getData());
		}
		this.animationTask.cancel();
		this.gate.trySetInvincibleEntity();
		Gates.destroyGateAnimationTask(this);
	}
}
