package Handlers;

import org.bukkit.event.HandlerList;

import Users.User;

public class LeaveTownEvent extends KaKEvent
{
	Integer townID;
	
	public LeaveTownEvent(User user, Integer townID)
	{
		super(user, 2);
		this.townID = townID;
	}
	
	public Integer getTownID()
	{
		return townID;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
