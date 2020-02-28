package Handlers;

import java.util.UUID;

import org.bukkit.event.HandlerList;

import Users.User;

public class ClickShopItemEvent extends KaKEvent
{
	UUID uuid;
	Integer relationID;
	Integer npcID;
	
	public ClickShopItemEvent(User user, Integer relationID, Integer npcID)
	{
		super(user, 2);
		this.relationID = relationID;
		this.npcID = npcID;
	}
	
	public Integer getnpcID()
	{
		return npcID;
	}
	
	public Integer getRelationID()
	{
		return relationID;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
