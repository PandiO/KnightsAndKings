package Properties;

import java.util.HashMap;

import org.bukkit.Bukkit;

import Main.Main;
import Quests.Quest;
import Quests.QuestDeliverPackage;
import Quests.QuestIntimidateRival;
import Users.User;

public final class Properties 
{
	static Main main = Main.getPlugin(Main.class);

	public static void destroy(User user)
	{
		user = null;
		System.gc();
	}
	
	//Temporairy solution for linking a quest to a property, in the future this has to be done by creating an instance of a property class for each property that has been visited by a player (the instance can be destroyed if no player visited the property in 15 minutes after creation),
	//then store the quest in the instance to make sure only one quest is active at a time from one property
	public static HashMap<Integer, Quest> ActiveQuests = new HashMap<Integer, Quest>();
	
	public static void addActiveQuest(int propertyID, Quest quest)
	{
		if (!ActiveQuests.containsKey(propertyID))
		{
			ActiveQuests.put(propertyID, quest);
			Bukkit.getConsoleSender().sendMessage("Added a new quest for a property with ID " + propertyID);
		}
	}
	
	//Either the propertyID is given or the quest, or both. 
	public static void removeActiveQuest(int propertyID, Quest quest)
	{
		if (propertyID > -1)
		{
			if (ActiveQuests.containsKey(propertyID))
			{
				ActiveQuests.remove(propertyID);
			}
		} else if (quest != null)
		{
			if (ActiveQuests.containsValue(quest))
			{				
				for (Integer keys : ActiveQuests.keySet())
				{
					if (ActiveQuests.get(keys) == quest)
					{
						ActiveQuests.remove(keys);
						break;
					}
				}
			}
		}
	}
	
	public static boolean hasActiveQuest(int propertyID)
	{
		boolean hasQuest = false;
		
		if (ActiveQuests.containsKey(propertyID))
		{
			hasQuest = true;
		}
		
		return hasQuest;
	}
	
	public static void tryMakeQuest(int propertyID)
	{
		if (!Properties.ActiveQuests.containsKey(propertyID))
		{
			if (!Main.PropertyQuestLong.containsKey(propertyID))
			{
				for (Quest quest : ActiveQuests.values())
				{
					if (quest instanceof QuestDeliverPackage)
					{
						QuestDeliverPackage QDP = (QuestDeliverPackage)quest;
						if (propertyID == QDP.getTargetPropertyID())
						{
							return;
						}
					}
				}
				Bukkit.getConsoleSender().sendMessage("Trying to make quest!");
				if (main.getRandom(0, 100) <= 20)
				{
					Quests.Quests.createQuest(null, -1, null, null, propertyID, -1, -1, -1, -1);
					Bukkit.getConsoleSender().sendMessage("Created a new quest for property with ID " + propertyID);
				}
			}
		}
	}
	
	public static Quest getQuestDeliverPackage(Integer targetPropertyID, Integer propertyID)
	{
		Quest quest = null;
		
		if (targetPropertyID != null)
		{
			for (Quest quests : ActiveQuests.values())
			{
				if (quests instanceof QuestDeliverPackage)
				{
					QuestDeliverPackage QDP = (QuestDeliverPackage) quests;
					if (QDP.getTargetPropertyID() == targetPropertyID)
					{
						quest = quests;
						break;
					}
				}
			}
		}
		
		return quest;
	}
	
	public static void tryFinishQuest(int propertyID, User user)
	{
		if (Properties.ActiveQuests.containsKey(propertyID))
		{
			Quest quest = Properties.ActiveQuests.get(propertyID);
			if (quest.getUser() == user)
			{
				if (quest.isCollected())
				{
					if (quest instanceof QuestIntimidateRival)
					{
						QuestIntimidateRival QIR = (QuestIntimidateRival) quest;
						QIR.complete();
					}
				}
			}
		}
	}
}
