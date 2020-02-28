package Tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;

public class IntroTutorial extends Tutorial
{
	private Tutorial tutorial = null;
	private Integer stage = 0;
	public List<ArrayList<String>> messages = new ArrayList<ArrayList<String>>(Arrays.asList(
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "I will explain you some of the basics of " + ColorOptions.KAKColor + "Knights and Kings",
					"",
					ColorOptions.message + "There are multiple goals you can try to achieve here. You can become:",
					ColorOptions.message + "-The master of many " + ColorOptions.messagesubjects + "1v1 or Massive PVP Arenas",
					ColorOptions.message + "-The supplier of resources for the whole " + ColorOptions.messagesubjects + "Kingdom",
					ColorOptions.message + "-The best bussinessman by owning the most " + ColorOptions.messagesubjects + "Properties (shops)",
					ColorOptions.message + "-The best thief by " + ColorOptions.messagesubjects + "Stealing and burgling",
					ColorOptions.message + "Or just be them all at the same time! After all, it's called " + ColorOptions.KAKColor + "Knights and Kings!",
					"",
					ColorOptions.message + "If you are ready for some information about how to play, type " + ColorOptions.messagesubjects + "next"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "The next thing I will show you is how to buy " + ColorOptions.messagesubjects + "Armor",
					ColorOptions.message + "Good Armor and Weapons are essential when walking outside " + ColorOptions.messagesubjects + "Towns",
					ColorOptions.message + "If you walk outside Towns " + ColorOptions.messagesubjects + "Bandits" + ColorOptions.message + " may appear which will attack you",
					ColorOptions.message + "The good part about this is that when you kill them all, you receive a pretty good " + ColorOptions.messagesubjects + "Reward"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "One thing I forgot to tell you earlier is,",
					ColorOptions.message + "Since the developer of this game still didn't invent " + ColorOptions.messagesubjects + "Navigation",
					ColorOptions.message + "You have to find " + ColorOptions.messagesubjects + "Houses, Rooms and Properties " + ColorOptions.message + "by finding the right street and streetnumber",
					"",
					ColorOptions.message + "This can be a pain in the ass sometimes.."
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "Congratulations! You have fully completed the " + ColorOptions.messagesubjects + "Introduction Tutorial",
					ColorOptions.messageachievement + "You are now prepared for the adventures that will come!"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "To finish the " + ColorOptions.messagesubjects + "Intro Tutorial " + ColorOptions.message + "type " + ColorOptions.messagesubjects + "next"
					))
			));

	public IntroTutorial(Tutorial tutorial) 
	{
		this.tutorial = tutorial;
		this.previousLocation = tutorial.previousLocation;
		this.targetLoc = tutorial.targetLoc;
		this.npcLoc = tutorial.npcLoc;
		this.target = tutorial.target;
		this.tutorialName = tutorial.tutorialName;
		this.npc = tutorial.npc;
		this.stage = tutorial.stage;
	}
	
	public void startTutorial()
	{
		if (stage == 0)
		{
			this.stage = 1;
			clearChat();
			target.sendMessage(this.npcPrefix + ColorOptions.message + "If you want to skip a tutorial, type " + ColorOptions.messagesubjects + "next");
			target.sendMessage(this.npcPrefix + ColorOptions.error + "But keep in mind that skipping tutorials means less rewards!");
			BukkitTask task = new BukkitRunnable()
			{
				public void run()
				{
			    	new BukkitRunnable()
					{
						public void run()
						{
							if (stage == 1)
							{
								nextStage(null);
							}
						}
					}.runTaskTimer(main, 0, talkInterval*20);
				}
			}.runTaskLater(main, 5*20);
			this.TutorialTask = task;
		}
		updateStage();
	}
	
	public void nextStage(Integer productID)
	{
		if (stage == 1)
		{
			message(messages.get(0));
			stage++;
		} else if (stage == 2)
		{
			Tutorial roomTutorial = new Tutorial();
			roomTutorial.createTutorial(user, "room");
			stage++;
		} else if (stage == 3)
		{
			message(messages.get(1));
			stage++;
		} else if (stage == 4)
		{
			Tutorial armorTutorial = new Tutorial();
			armorTutorial.createTutorial(user, "armor");
			stage++;
		} else if (stage == 5)
		{
			message(messages.get(2));
			stage++;
		} else if (stage == 6)
		{
			Tutorial foodTutorial = new Tutorial();
			foodTutorial.createTutorial(user, "food");
			stage++;
		} else if (stage == 7)
		{
			Tutorial skillsTutorial = new Tutorial();
			skillsTutorial.createTutorial(user, "skills");
			stage++;
		} else if (stage == 8)
		{
			message(messages.get(4));
			stage++;
		} else if (stage == 9)
		{
			finishTutorial();
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Current stage: " + stage);
		}
		updateStage();
	}
	
	public void previousStage()
	{
		if (stage == 1)
		{
		} else if (stage == 2)
		{
			
		} else if (stage == 3)
		{
			message(messages.get(0));
			stage--;
		} else if (stage == 4)
		{
			message(messages.get(1));
			stage--;
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Current stage: " + stage);
		}
		updateStage();
	}
	
	public void finishTutorial()
	{
		List<String> completeList = new ArrayList<String>();
		completeList.addAll(messages.get(3));
		if (file.containsUUID(target.getUniqueId(), this.tutorialName) == false)
		{
			Bukkit.getServer().getWorld(target.getWorld().getName()).playEffect(target.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
			Integer reward = file.getReward(this.tutorialName);
			Integer experienceReward = file.getExperienceReward(this.tutorialName);
			
			user.addGems(reward);
			user.addExperience(experienceReward, true);
			completeList.addAll(this.rewardMessage);
			completeList.addAll(Arrays.asList(ColorOptions.message + "-" + ColorOptions.gemStats + reward + " Gems", ColorOptions.message + "-" + ColorOptions.statsresults + experienceReward + " Experience"));
			message(completeList);
			
			file.savePlayer(target, tutorialName);
		}
		cancel(completeList);
	}
	
	public void updateStage()
	{
		this.tutorial.stage = this.stage;
	}
}
