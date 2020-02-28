package Resources;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import Main.Main;

public class BlockRefresh 
{
	protected Main main = Main.getPlugin(Main.class);
	private Integer materialID = null;
	private Byte blockData = null;
	private Location blockLocation = null;
	private Long blockRefresh = null;
	
	public BlockRefresh(Integer materialID, Byte blockData, Location blockLocation, Long blockRefresh)
	{
		this.materialID = materialID;
		this.blockData = blockData;
		this.blockLocation = blockLocation;
		this.blockRefresh = blockRefresh;
	}
	
	public void setBlock()
	{
		blockLocation.getBlock().setTypeIdAndData(materialID, blockData, true);
		Bukkit.getConsoleSender().sendMessage("Block refreshed with type " + Material.getMaterial(materialID).toString());
	}
	
	public void removeRefresh()
	{
		if (BlockBreakEvents.refreshList.contains(this))
		{
			BlockBreakEvents.refreshList.remove(this);
		}
	}
	
	public void RefreshBlock()
	{
		setBlock();
		removeRefresh();
	}
	
	public Integer getMaterialID()
	{
		return this.materialID;
	}
	
	public Byte getData()
	{
		return this.blockData;
	}
	
	public Location getLocation()
	{
		return this.blockLocation;
	}
	
	public Long getRefresh()
	{
		return this.blockRefresh;
	}
}
