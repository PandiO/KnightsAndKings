package Handlers;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.event.HandlerList;

import Users.User;

public class RemoveFriendEvent extends KaKEvent
{
	UUID frienduuid;
	World world;
	
	public RemoveFriendEvent(User user, UUID frienduuid, World world)
	{
		super(user, 3);
		this.frienduuid = frienduuid;
		this.world = world;
	}
	
	public UUID getFriendUUID()
	{
		return frienduuid;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
