package Handlers;

import org.bukkit.event.HandlerList;

import Users.User;

public class IncomePayoutEvent extends KaKEvent
{	
	public IncomePayoutEvent(User user)
	{
		super(user, 4);
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
