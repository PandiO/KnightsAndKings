package Handlers;

import java.util.UUID;

import org.bukkit.event.HandlerList;

import Users.User;

public class TitleChangeEvent extends KaKEvent
{
	UUID uuid;
	Integer oldTitle;
	Integer newTitle;
	boolean promotion;
	
	//The boolean promotion is used to check if the player is going up in title (true) or going down (false)
	public TitleChangeEvent(User user, Integer oldTitle, Integer newTitle, boolean promotion)
	{
		super(user, 10);
		this.oldTitle = oldTitle;
		this.newTitle = newTitle;
		this.promotion = promotion;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	public Integer getOldTitle()
	{
		return oldTitle;
	}
	public Integer getNewTitle()
	{
		return newTitle;
	}
	public boolean isPromoting()
	{
		return promotion;
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
