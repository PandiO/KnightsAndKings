package Handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import Quests.Quest;

public class QuestStateChangeEvent extends Event
{
	int PropertyID;
	Quest Quest;
	
	public QuestStateChangeEvent(int propertyID, Quest quest)
	{
		this.PropertyID = propertyID;
		this.Quest = quest;
		Bukkit.getConsoleSender().sendMessage("Triggering quest state change event!");
	}
	
	public int getPropertyID()
	{
		return this.PropertyID;
	}
	
	public Quest getQuest()
	{
		return this.Quest;
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
