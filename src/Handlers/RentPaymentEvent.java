package Handlers;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import Users.User;

public class RentPaymentEvent extends KaKEvent
{
	UUID uuid;
	Player player;
	
	public RentPaymentEvent(User user)
	{
		super(user, 3);		
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
