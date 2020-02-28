package Handlers;

import java.util.UUID;

import org.bukkit.event.HandlerList;

import Users.User;

public class SalaryPayoutEvent extends KaKEvent
{
	UUID uplayer;
	
	public SalaryPayoutEvent(User user)
	{
		super(user, 2);
		
	}
	
	public UUID getUUID()
	{
		return uplayer;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
