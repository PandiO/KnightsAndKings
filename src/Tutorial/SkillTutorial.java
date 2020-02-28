package Tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Skills.Skill;

public class SkillTutorial extends Tutorial
{
	private Skill skill = new Skill();
	private Tutorial tutorial = null;
	private Integer stage = 0;
	public List<ArrayList<String>> messages = new ArrayList<ArrayList<String>>(Arrays.asList(
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "An important feature you should not forget are the " + ColorOptions.messagesubjects + "Personal Skills",
					ColorOptions.message + "With these skills you can personalize the way you " + ColorOptions.messagesubjects + "Fight in Battles",
					ColorOptions.message + "You can, for example, choose to focus more on " + ColorOptions.messagesubjects + "Dealing Damage " + ColorOptions.message + "or " + ColorOptions.messagesubjects + "Reduce incoming Damage" 
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Now we will try increasing the level of a " + ColorOptions.messagesubjects + "Personal Skill",
					"",
					ColorOptions.message + "Increasing levels of Skills require " + ColorOptions.messagesubjects + "Skillpoints" + ColorOptions.message + " these can be acquired when leveling up",
					ColorOptions.message + "Open your " + ColorOptions.messagesubjects + "Personal Menu" + ColorOptions.message + " by typing " + ColorOptions.messagesubjects + "/menu"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "Great! Now click on the flashing item!"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Here you have an overview of the " + ColorOptions.messagesubjects + "Personal Skills " + ColorOptions.message + " and your " + ColorOptions.messagesubjects + "Level",
					ColorOptions.message + "Read the description of each skill by " + ColorOptions.messagesubjects + "Hovering over the Icons on the left",
					ColorOptions.message + "Your amount of skillpoints is show with the " + ColorOptions.messagesubjects + "Emerald Icon"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Increase the level of a Skill of your choice by clicking on the " + ColorOptions.messagesubjects + "First upgrade" + ColorOptions.message + "(Red Clay-Icon)"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "Congratulations! You have completed the " + ColorOptions.messagesubjects + "Skills Tutorial"
					))
			));

	public SkillTutorial(Tutorial tutorial) 
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
			target.sendMessage(this.npcPrefix + ColorOptions.message + "If you want to continue to the next part, type " + ColorOptions.messagesubjects + "next");
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
								nextStage();
							}
						}
					}.runTaskTimer(main, 0, talkInterval*20);
				}
			}.runTaskLater(main, 3*20);
			this.TutorialTask = task;
		}
		updateStage();
	}
	
	public void nextStage()
	{
		if (stage == 1)
		{
			message(messages.get(0));
			stage++;
		} else if (stage == 2)
		{
			message(messages.get(1));
			stage++;
		} else if (stage == 3)
		{
			showMenu();
			stage++;
		} else if (stage == 4)
		{
			showSkills();
			stage++;
			new BukkitRunnable()
			{
				public void run()
				{
					nextStage();
				}
			}.runTaskLater(main, 5*20);
		} else if (stage == 5)
		{
			blinkUpgrades();
			stage++;
		} else if (stage == 6)
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Clicked the upgrade");
			}
			boolean upgraded = false;
			HashMap<String, Integer> skillList = user.getSkillLevelList();
			for (String skill : skillList.keySet())
			{
				if (skillList.get(skill) > 0)
				{
					if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Skill higher than 0");
					}
					upgraded = true;
					break;
				}
			}
			if (upgraded)
			{
				finishTutorial();
			} else
			{
				message(messages.get(4));
			}
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
		} else if (stage == 5)
		{
			showMenu();
			stage--;
		} else if (stage == 6)
		{
			showSkills();
			stage--;
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Current stage: " + stage);
		}
		updateStage();
	}
	
	public void showMenu()
	{
		message(messages.get(2));
		menu.OpenPersonalMenu(user);
		menu.setMenuItemBlink(sender, user, target.getOpenInventory().getTopInventory(), 10, subjectInterval);
	}
	
	public void showSkills()
	{
		user.addSkillPoints(false, 1);
		message(messages.get(3));
		menu.openSkillMenu(user);
	}
	
	public void blinkUpgrades()
	{
		message(messages.get(4));
		menu.setMenuItemBlink(sender, user, target.getOpenInventory().getTopInventory(), 1, subjectInterval);
		menu.setMenuItemBlink(sender, user, target.getOpenInventory().getTopInventory(), 10, subjectInterval);
		menu.setMenuItemBlink(sender, user, target.getOpenInventory().getTopInventory(), 19, subjectInterval);
		menu.setMenuItemBlink(sender, user, target.getOpenInventory().getTopInventory(), 28, subjectInterval);
		menu.setMenuItemBlink(sender, user, target.getOpenInventory().getTopInventory(), 37, subjectInterval);
	}
	
	public void finishTutorial()
	{
		List<String> completeList = new ArrayList<String>();
		completeList.addAll(messages.get(5));
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
