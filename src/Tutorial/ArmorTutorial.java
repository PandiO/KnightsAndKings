package Tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Products.Product;
import Products.PropertyProduct;

public class ArmorTutorial extends Tutorial
{
	PropertyProduct proproduct = new PropertyProduct();
	Product product = new Product();
	private Tutorial tutorial = null;
	private Integer stage = 0;
	protected Integer used = null;
	private List<Integer> productIDList = new ArrayList<Integer>(Arrays.asList(
			product.getProductID("leatherhelmet", false),
			product.getProductID("leatherchestplate", false),
			product.getProductID("leatherleggings", false),
			product.getProductID("leatherboots", false)
			));
	private List<String> armorList = new ArrayList<String>(Arrays.asList(
			product.getDisplayName(product.getProductID("leatherhelmet", false), false),
			product.getDisplayName(product.getProductID("leatherchestplate", false), false),
			product.getDisplayName(product.getProductID("leatherleggings", false), false),
			product.getDisplayName(product.getProductID("leatherboots", false), false)
			));
	public List<ArrayList<String>> messages = new ArrayList<ArrayList<String>>(Arrays.asList(
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Places to buy items like food, armor, weapons and enchantments are called " + ColorOptions.messagesubjects + "Properties (shops)",
					"",
					ColorOptions.message + "They are located in towns and you can enter them when you have a " + ColorOptions.messagesubjects + "House/Room",
					ColorOptions.message + "Some useful things you need to know about " + ColorOptions.messagesubjects + "Properties " + ColorOptions.message + "are:",
					ColorOptions.message + "-The amount of items they sell changes every " + ColorOptions.messagesubjects + "Day",
					ColorOptions.message + "-The price of items changes every " + ColorOptions.messagesubjects + "Week ",
					ColorOptions.message + "-The items are marked with a " + ColorOptions.messagesubjects + "Grade ★",
					ColorOptions.message + "-The Grade ★ indicates the max. level of " + ColorOptions.messagesubjects + "Enchantments" + ColorOptions.message + " possible",
					ColorOptions.message + "-Properties in bigger towns sell " + ColorOptions.messagesubjects + "Higher grade items!",
					"",
					ColorOptions.message + "Also, owning a property will increase your " + ColorOptions.messagesubjects + "Income" + ColorOptions.message + "!"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "Let's start off with buying some essential items such as " + ColorOptions.messagesubjects + "Armor",
					"",
					ColorOptions.message + "If you are ready type " + ColorOptions.messagesubjects + "next" + ColorOptions.message + " and "  + ColorOptions.messagesubjects + "follow me!"
					)),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "We've arrived!",
					ColorOptions.message + "You can buy products in " + ColorOptions.messagesubjects + "2 Different ways:",
					ColorOptions.message + "-Click the " + ColorOptions.messagesubjects + "Shopkeeper",
					ColorOptions.message + "-Click the " + ColorOptions.messagesubjects + "Item Frames",
					"",
					ColorOptions.message + "Now try buying the following items:"
					)),
			new ArrayList<String>(Arrays.asList(ColorOptions.message + "Great! Now buy the other parts:")),
			new ArrayList<String>(Arrays.asList(
					ColorOptions.messageachievement + "Congratulations! You have completed the " + ColorOptions.messagesubjects + "Armor Tutorial"
					))
			));

	public ArmorTutorial(Tutorial tutorial) 
	{
		this.tutorial = tutorial;
		this.previousLocation = tutorial.previousLocation;
		this.npcDestination = spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("armorTutorial"));
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
								nextStage(null);
							}
						}
					}.runTaskTimer(main, 0, talkInterval*20);
				}
			}.runTaskLater(main, 3*20);
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
			message(messages.get(1));
			stage++;
		} else if (stage == 3)
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
		} else if (stage == 4)
		{
			Product product = new Product();
			List<String> completeList = messages.get(2);
			completeList.addAll(armorList);
			for (Integer productIDs : productIDList)
			{
				Integer relationID = proproduct.getRelationID(productIDs, 22);
				proproduct.saveAmount(relationID, proproduct.getAmount(relationID));
			}
			message(completeList);
			target.getInventory().addItem(product.createItem(ChatColor.DARK_PURPLE + "Item Coupon", new ItemStack(Material.PAPER, 4), false, ColorOptions.message + "Category: " + ColorOptions.KAKColor + "Armor", ColorOptions.message + "Use this to buy an item for free!"));
			target.updateInventory();
			used = 0;
			stage++;
		} else if (stage == 5)
		{
			if (productID != null)
			{
				String displayName = product.getDisplayName(productID, false);
				if (armorList.contains(displayName))
				{
					armorList.remove(displayName);
					
					List<String> completeList = new ArrayList<String>();
					completeList.addAll(messages.get(3));
					completeList.addAll(armorList);
					message(completeList);
					used = 1;
					stage++;
				} else
				{
					message(Arrays.asList(ColorOptions.error + "You didn't buy anything!"));
				}
			} else
			{
				message(Arrays.asList(ColorOptions.error + "You didn't buy anything!"));
			}
		} else if (stage == 6)
		{
			if (productID != null)
			{
				String displayName = product.getDisplayName(productID, false);
				if (armorList.contains(displayName))
				{
					armorList.remove(displayName);
					
					List<String> completeList = new ArrayList<String>();
					completeList.addAll(messages.get(3));
					completeList.addAll(armorList);
					message(completeList);
					used = 1;
					stage++;
				} else
				{
					message(Arrays.asList(ColorOptions.error + "You didn't buy anything!"));
				}
			} else
			{
				message(Arrays.asList(ColorOptions.error + "You didn't buy anything!"));
			}
		} else if (stage == 7)
		{
			if (productID != null)
			{
				String displayName = product.getDisplayName(productID, false);
				if (armorList.contains(displayName))
				{
					armorList.remove(displayName);
					
					List<String> completeList = new ArrayList<String>();
					completeList.addAll(messages.get(3));
					completeList.addAll(armorList);
					message(completeList);
					used = 1;
					stage++;
				} else
				{
					message(Arrays.asList(ColorOptions.error + "You didn't buy anything!"));
				}
			} else
			{
				message(Arrays.asList(ColorOptions.error + "You didn't buy anything!"));
			}
		} else if (stage == 8)
		{
			used = 1;
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
		completeList.addAll(messages.get(4));
		if (file.containsUUID(target.getUniqueId(), this.tutorialName) == false)
		{
			Bukkit.getServer().getWorld(target.getWorld().getName()).playEffect(target.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
			Integer reward = file.getReward(this.tutorialName);
			Integer experienceReward = file.getExperienceReward(this.tutorialName);
			
			this.user.addGems(reward);
			this.user.addExperience(experienceReward, true);
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
