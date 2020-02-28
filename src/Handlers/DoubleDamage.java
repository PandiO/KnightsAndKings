package Handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import Users.User;

public class DoubleDamage extends KaKEvent
{
	Player player;
	Player target;
	Double damage;
	Boolean multiplier;
	
	public DoubleDamage(User user, Player target, Double damage, Boolean multiplier)
	{
		super(user, 2);
		this.player = user.getPlayer();
		this.target = target;
		this.damage = damage;
		this.multiplier = multiplier;
		
	}
	
	public Player getPlayer()
	{
		return player;
	}
	public Player getTarget()
	{
		return target;
	}
	public Double getDamage()
	{
		return damage;
	}
	public Boolean getMultiplier()
	{
		return multiplier;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
