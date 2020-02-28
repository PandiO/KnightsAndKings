package Handlers;

import Users.User;

public class EnterTownEvent extends KaKEvent
{
	Integer townID;
	
	public EnterTownEvent(User user, Integer townID)
	{
		super(user, 2);
		
		this.townID = townID;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public Integer getTownID()
	{
		return townID;
	}
}
