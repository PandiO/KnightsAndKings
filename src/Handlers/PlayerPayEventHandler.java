package Handlers;

import java.util.UUID;

import org.bukkit.event.HandlerList;

import Users.User;

public class PlayerPayEventHandler extends KaKEvent
{
	UUID targetUUID;
	Integer coinamount;
	
	public PlayerPayEventHandler(User user, Integer coinamount, UUID targetUUID)
	{
		super(user, 5);
		this.targetUUID = targetUUID;
		this.coinamount = coinamount;
		
	}
	
	public UUID getTargetUUID()
	{
		return targetUUID;
	}

	public Integer getInteger()
	{
		return coinamount;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}

