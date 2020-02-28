package Handlers;

import org.bukkit.event.HandlerList;

import Users.User;

public class NewSkillHandler extends KaKEvent
{
	String skill;
	Integer level;
	
	public NewSkillHandler(User user, String skill, Integer level)
	{
		super(user, 10);
		this.skill = skill;
		this.level = level;
		
	}
	public String getSkill()
	{
		return skill;
	}
	public Integer getLevel()
	{
		return level;
	}
	
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
