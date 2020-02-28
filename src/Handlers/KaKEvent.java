package Handlers;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import Users.User;

public class KaKEvent extends Event
{
	protected User user;
	//Range from 1 to 10
	protected Integer eventValue;
	
	public KaKEvent(User user, Integer eventValue)
	{
		this.user = user;
		this.eventValue = eventValue;
	}
	
	public User getUser()
	{
		return this.user;
	}
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
