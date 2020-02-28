package Tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;

public class RoomTutorial extends Tutorial
{
	private Tutorial tutorial = null;
	private Integer stage = 0;
	public List<ArrayList<String>> messages = new ArrayList<ArrayList<String>>(Arrays.asList(
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "The " + ColorOptions.messagesubjects + "Personal Menu " + ColorOptions.message + "is the place to manage almost everything in the game.",
					"",
					ColorOptions.message + "You can: ",
					ColorOptions.message + "-Sell or buy " + ColorOptions.messagesubjects + "Houses/Rooms/Properties " + ColorOptions.message + "(Bed-icon & Anvil-icon)",
					ColorOptions.message + "-Teleport to point on the map " + ColorOptions.message + "(Compass-icon)",
					ColorOptions.message + "-Manage your " + ColorOptions.messagesubjects + "Personal Skills " + ColorOptions.message + "(Book-icon).",
					"",
					ColorOptions.message + "And much more! Be sure to check all the icons from your " + ColorOptions.messagesubjects + "Personal menu" + ColorOptions.message + " out for useful stuff!"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Let's start off with renting a " + ColorOptions.messagesubjects + "Room" + ColorOptions.message + ". Owning a House/Room allows you to enter " + ColorOptions.messagesubjects + "Shops " + ColorOptions.message + "(Properties)" + ColorOptions.message + " where you can buy items!",
					"",
					ColorOptions.message + "Open your " + ColorOptions.messagesubjects + "Personal Menu" + ColorOptions.message + " by typing " + ColorOptions.messagesubjects + "/menu"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "Great! Now click on the flashing item to the left!"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Here you have an overview of your owned " + ColorOptions.messagesubjects + "Houses and Rooms",
					ColorOptions.message + "You can unlock slots to own more by " + ColorOptions.messagesubjects + "Leveling up or buy from the Gem Shop"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Now click a room which you can afford and has no owner. You can get information by " + ColorOptions.messagesubjects + "Hovering over a room " + ColorOptions.message + "(Bed-Icon)",
					ColorOptions.error + "Note: If you are inactive for 7 days or longer, your rented room will be sold!"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "Congratulations! You have completed the " + ColorOptions.messagesubjects + "Menu Tutorial",
					ColorOptions.message + "You can teleport to your " + ColorOptions.messagesubjects + "House or Room " + ColorOptions.message + "by typing " + ColorOptions.messagesubjects + "/home"
					))
			));

	public RoomTutorial(Tutorial tutorial) 
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
			showOwnedRooms();
			stage++;
		} else if (stage == 5)
		{
			showRooms();
			stage++;
		} else if (stage == 6)
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
		} else if (stage == 5)
		{
			showMenu();
			stage--;
		} else if (stage == 6)
		{
			showOwnedRooms();
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
		menu.setMenuItemBlink(sender, user, target.getOpenInventory().getTopInventory(), 0, subjectInterval);
	}
	
	public void showOwnedRooms()
	{
		message(messages.get(3));
		menu.openownedHouses(user);
		menu.setMenuItemBlink(sender, user, target.getOpenInventory().getTopInventory(), 2, subjectInterval);
	}
	
	public void showRooms()
	{
		message(messages.get(4));
		menu.openRoomlist(user, 0, null);
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
