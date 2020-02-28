package Tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Products.Product;
import Products.PropertyProduct;

public class FoodTutorial extends Tutorial
{
	PropertyProduct proproduct = new PropertyProduct();
	Product product = new Product();
	private Tutorial tutorial = null;
	private Integer stage = 0;
	private Integer amount = 32;
	private Integer productID = product.getProductID("bread", false);
	private List<String> foodList = new ArrayList<String>(Arrays.asList(
			product.getDisplayName(product.getProductID("bread", false), false)
			));
	public List<ArrayList<String>> messages = new ArrayList<ArrayList<String>>(Arrays.asList(
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Alright, something that is just as important as armor is " + ColorOptions.messagesubjects + "Food",
					"",
					ColorOptions.message + "Food can also be bought in " + ColorOptions.messagesubjects + "Properties",
					ColorOptions.message + "The quality of the food is marked with " + ColorOptions.messagesubjects + "Grades ★",
					ColorOptions.message + "Food with a high grade has a chance to give a " + ColorOptions.messagesubjects + "Beneficial Effect" + ColorOptions.message + " such as:",
					ColorOptions.message + "-" + ColorOptions.messagesubjects + "Coin booster ",
					ColorOptions.message + "-" + ColorOptions.messagesubjects + "Gem booster",
					ColorOptions.message + "-" + ColorOptions.messagesubjects + "Health booster",
					ColorOptions.message + "-" + ColorOptions.messagesubjects + "Rank booster",
					"",
					ColorOptions.message + "Keep in mind that the chance of receiving these effects is still very little"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Something I must tell you is that lower grade items may give you " + ColorOptions.error + "Harmful Effects" + ColorOptions.message + " such as:",
					ColorOptions.message + "-" + ColorOptions.error + "Money to Blow",
					ColorOptions.message + "-" + ColorOptions.error + "Poisonous Generosity",
					ColorOptions.message + "-" + ColorOptions.error + "Drunken Jeweler",
					"",
					ColorOptions.message + "For a list with descriptions of these effects go to " + ColorOptions.messagesubjects + "Personal Menu > Support > Effects"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "Now let's first get you some food",
					"",
					ColorOptions.message + "If you are ready type " + ColorOptions.messagesubjects + "next" + ColorOptions.message + " and " + ColorOptions.messagesubjects + "follow me!"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "We've arrived!",
					"",
					ColorOptions.message + "To purchase a larger amount of an item at once, click the " + ColorOptions.messagesubjects + "Chest Icon",
					ColorOptions.message + "Now try buying " + ColorOptions.messagesubjects + amount + ColorOptions.message + " of the following item:"
					)),
			new ArrayList<String>(Arrays.asList(ColorOptions.message + "Great! Now try buying some more")),
			new ArrayList<String>(Arrays.asList(ColorOptions.message + "Try buying " + ColorOptions.messagesubjects + amount + " or more" + ColorOptions.message + " of the following item:")),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "Congratulations! You have completed the " + ColorOptions.messagesubjects + "Food Tutorial"
					))
			));

	public FoodTutorial(Tutorial tutorial) 
	{
		this.tutorial = tutorial;
		this.previousLocation = tutorial.previousLocation;
		this.npcDestination = spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("foodTutorial"));
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
								nextStage(null, null);
							}
						}
					}.runTaskTimer(main, 0, talkInterval*20);
				}
			}.runTaskLater(main, 3*20);
			this.TutorialTask = task;
		}
		updateStage();
	}
	
	public void nextStage(Integer productID, ItemStack item)
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
			message(messages.get(2));
			stage++;
		} else if (stage == 4)
		{
			this.npc.getNavigator().cancelNavigation();
			if (this.npcDestination != null)
			{
				try
				{
					this.npc.getNavigator().setTarget(this.npcDestination);
				} catch (Exception e)
				{
					e.printStackTrace();
					cancel(Arrays.asList(ColorOptions.error + "Something went wrong, please contact a staff-member"));
				}
			} else
			{
				cancel(Arrays.asList(ColorOptions.error + "Something went wrong, please contact a staff-member"));
			}
			stage++;
		} else if (stage == 5)
		{
			Integer relationID = proproduct.getRelationID(productID, 19);
			proproduct.saveAmount(relationID, proproduct.getAmount(relationID));
			List<String> completeList = messages.get(3);
			completeList.addAll(foodList);
			message(completeList);
			stage++;
		} else if (stage == 6)
		{
			if (productID != null && item != null)
			{
				String displayName = product.getDisplayName(productID, false);
				if (foodList.contains(displayName))
				{
					if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Food found!");
					}
					if (item.getAmount() >= amount)
					{
						if (main.debug)
						{
							Bukkit.getConsoleSender().sendMessage("Correct amount detected!");
						}
						finishTutorial();
					} else
					{
						this.amount = this.amount-item.getAmount();
						message(messages.get(3));
					}
				} else
				{
					if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Food not corresponding to the food-list!");
					}
					message(Arrays.asList(ColorOptions.error + "You didn't buy anything!"));
				}
			} else
			{
				message(Arrays.asList(ColorOptions.error + "You didn't buy anything!"));
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
		completeList.addAll(messages.get(6));
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
