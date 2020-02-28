package Handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import Users.User;

public class ExperienceChangeEvent extends KaKEvent
{
	Player player;
	Integer expamount;
	
	public ExperienceChangeEvent(User user, Integer expamount, Player player)
	{
		super(user, 1);
		this.player = player;
		this.expamount = expamount;
		
	}

	public Player getPlayer()
	{
		return player;
	}
	public Integer getInteger()
	{
		return expamount;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() 
	{
	    return handlers;
	}

	public static HandlerList getHandlerList() 
	{
	    return handlers;
	}
}
