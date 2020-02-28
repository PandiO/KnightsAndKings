package Handlers;

import org.bukkit.block.Chest;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StorageEvent extends Event implements Cancellable
{
	Integer propertyID;
	Chest chest;
	boolean cancelled = false;
	
	public StorageEvent(Integer propertyID, Chest chest)
	{
		this.propertyID = propertyID;
		this.chest = chest;
		
	}
	
	public Integer getID()
	{
		return propertyID;
	}
	
	public Chest getChest()
	{
		return chest;
	}
	
    @Override
    public boolean isCancelled() 
    {
        return false;
    }

    @Override
    public void setCancelled(boolean arg0) 
    {
        cancelled = arg0;
    }
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
