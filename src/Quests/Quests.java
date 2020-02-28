package Quests;

import Main.Main;
import Users.User;

public class Quests 
{
	static Main main = Main.getPlugin(Main.class);
	
	public static Quest createQuest(User user, int TypeID, String Name, String Description, int PropertyID, int GoalAmount, int CoinReward, int GemReward, int ExperienceReward)
	{
		Quest quest = null;
		
		if (TypeID == -1)
		{
			TypeID = main.getRandom(1, 3);
		}
		
		if (TypeID == 1)
		{
			if (Name == null)
			{
				Name = "Put in the hard work";
			}
			
			quest = new QuestHarvestResource(Name, Description, PropertyID, -1, GoalAmount, CoinReward, GemReward, ExperienceReward, user);
		} else if (TypeID == 2)
		{
			if (Name == null)
			{
				Name = "Tricksy business";
			}
			
			quest = new QuestIntimidateRival(Name, Description, PropertyID, -1, 1, CoinReward, GemReward, ExperienceReward, user);
		} else if (TypeID == 3)
		{
			if (Name == null)
			{
				Name = "The postman";
			}
			
			quest = new QuestDeliverPackage(Name, Description, PropertyID, -1, -1, -1, CoinReward, GemReward, ExperienceReward, user);
		}
		
		return quest;
				
	}
}
