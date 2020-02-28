package Quests;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Products.Product;
import Properties.Property;
import Properties.PropertyCategory;
import Users.User;

public class QuestHarvestResource extends Quest
{
	PropertyCategory propertyCategory = new PropertyCategory();
	Property property = new Property();
	Product product = new Product();
	
	protected int CategoryID;
	protected ArrayList<Integer> RequestedResources;
	protected int productID;
	protected int Grade;
	protected Material productMaterial;
	
	public QuestHarvestResource(String Name, String Description, int PropertyID, int ProductID, int GoalAmount, int CoinReward, int GemReward, int ExperienceReward, User User)
	{
		super(1, Name, Description, PropertyID, GoalAmount, CoinReward, GemReward, ExperienceReward, System.currentTimeMillis() + 900*1000);
		
		boolean shouldActivate = true;

		this.CategoryID = this.property.getCategoryID(this.PropertyID);
		try
		{
			this.RequestedResources = this.propertyCategory.getRequestedProductList(this.CategoryID);
			if (this.RequestedResources == null || this.RequestedResources.isEmpty())
			{
				shouldActivate = false;
				return;
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
			shouldActivate = false;
			return;
		}
		if (ProductID == -1)
		{
			Collections.shuffle(RequestedResources);
			ProductID = this.RequestedResources.get(0);
		}
		this.productID = ProductID;
		this.Grade = this.product.getGrade(this.productID, false);
		this.productMaterial = this.product.createPropertyItem(this.productID, 1, false, false).getType();
		
		if (this.GoalAmount == -1)
		{
			switch (this.product.getGrade(this.productID, false))
			{
			case 1: this.GoalAmount = main.getRandom(20, 256);
			break;
			case 2: this.GoalAmount = main.getRandom(20, 192);
			break;
			case 3: this.GoalAmount = main.getRandom(20, 64);
			break;
			case 4: this.GoalAmount = main.getRandom(10, 64);
			break;
			case 5: this.GoalAmount = main.getRandom(4, 15);
			break;
			default: this.GoalAmount = main.getRandom(20, 96);
			break;
			}
		}
		
		if (Description == null || productID == -1)
		{
			this.Description = "Gather and deliver " + this.GoalAmount + " times " + this.product.getDisplayName(productID, false);
		}
		
		if (CoinReward == -1)
		{
			int CalculatedCoinReward = 0;
			if (this.GoalAmount > 200)
			{
				CalculatedCoinReward = main.getRandom(20000, 30000);
			} else if (this.GoalAmount <= 200 && this.GoalAmount > 150)
			{
				CalculatedCoinReward = main.getRandom(15000, 21500);
			} else if (this.GoalAmount <= 150 && this.GoalAmount > 96)
			{
				CalculatedCoinReward = main.getRandom(10000, 15000);
			} else if (this.GoalAmount <= 96 && this.GoalAmount > 32)
			{
				CalculatedCoinReward = main.getRandom(5000, 10000);
			} else
			{
				CalculatedCoinReward = main.getRandom(2500, 4500);
			}
			if (this.Grade > 2)
			{
				CalculatedCoinReward = (int) ((int) (CalculatedCoinReward+4000) * ((double)1 + (this.Grade/10)) + (1000 * ((double) (1 + (property.getContribution(this.PropertyID)/100)))));
			}
			this.CoinReward = CalculatedCoinReward;
		}
		if (GemReward == -1)
		{
			if (this.Grade > 3 && this.GoalAmount > 196)
			{
				this.GemReward = main.getRandom(5, 18);
			} else
			{
				this.GemReward = 0;
			}
		}
		if (ExperienceReward == -1)
		{
			int CalculatedExpReward = 0;
			if (this.GoalAmount > 200)
			{
				CalculatedExpReward = main.getRandom(1200, 1800);
			} else if (this.GoalAmount <= 200 && this.GoalAmount > 150)
			{
				CalculatedExpReward = main.getRandom(800, 1200);
			} else if (this.GoalAmount <= 150 && this.GoalAmount > 96)
			{
				CalculatedExpReward = main.getRandom(400, 800);
			} else if (this.GoalAmount <= 96 && this.GoalAmount > 32)
			{
				CalculatedExpReward = main.getRandom(150, 400);
			} else
			{
				CalculatedExpReward = main.getRandom(25, 150);
			}
			if (this.Grade > 2)
			{
				CalculatedExpReward = CalculatedExpReward * (1 + ((this.Grade/100)+(property.getContribution(this.PropertyID)/100)));
			}
			this.ExperienceReward = CalculatedExpReward;
		}
		
		if(shouldActivate)
		{
			this.activate();
		}
	}
	
	public int getProductID()
	{
		return this.productID;
	}
	
	public Material getMaterial()
	{
		return this.productMaterial;
	}
	
	public void Harvest(ItemStack item)
	{
		if (!this.isCompleted && !this.isCollected)
		{
			if (item.getType() == this.productMaterial)
			{
				this.ProgressAmount+=item.getAmount();
				
				if (this.ProgressAmount >= this.GoalAmount)
				{
					this.goalCollected();
				}
			}
		}
	}
	
	public void Deliver(ItemStack item)
	{
		if (this.isCompleted)
		{
			return;
		}
		if (!this.isCollected)
		{
			return;
		}
		if (item.getType() == this.productMaterial)
		{
			Integer exceedAmount = item.getAmount()-(this.GoalAmount-this.DeliveredAmount);
			this.DeliveredAmount += item.getAmount();
			
			if (this.DeliveredAmount >= this.GoalAmount)
			{
				this.complete();
				
				if (exceedAmount > 0)
				{
					item.setAmount(exceedAmount);
					this.getUser().getPlayer().getInventory().addItem(item);
					this.getUser().getPlayer().updateInventory();
				}
			}
		}
	}
	
	public void goalCollected()
	{
		Player player = this.User.getPlayer();
		this.isCollected = true;
		player.playSound(player.getLocation(), SoundHandler.LEVEL_UP, 0.1F, 0.1F);
		player.sendMessage(ColorOptions.messageachievement + "You have collected enough items for a quest!");
		player.sendMessage(ColorOptions.message + "Go to " + this.property.getPropertyName(this.PropertyID) + " and deliver the items to receive your reward!");
		player.sendMessage(ColorOptions.message + "(Check your assignments-menu for the location)");
	}
	
	public void tryDeliver()
	{
		Player player = this.User.getPlayer();
		Inventory inv = this.User.getPlayer().getInventory();
		int foundAmount = 0;
		
		for (ItemStack item : inv.getContents())
		{
			player.sendMessage("Material " + item.getType().toString());
			if (item.getType() == this.productMaterial)
			{
				foundAmount += item.getAmount();
				if (foundAmount >= this.GoalAmount)
				{
					break;
				}
				Bukkit.broadcastMessage("Found total: " + foundAmount);
			}
		}
		if (foundAmount == this.GoalAmount)
		{
			this.deliver();
		} else
		{
			player.sendMessage(ColorOptions.error + "You don't have enough of the requested item!");
		}
	}
	
	public void deliver()
	{
		Player player = this.User.getPlayer();
		Inventory inv = this.User.getPlayer().getInventory();
		int deliveredAmount = 0;
		Bukkit.broadcastMessage("Starting to remove " + this.GoalAmount + " items");
		for (int slot = 0; slot < 36; slot++)
		{
			ItemStack item = inv.getItem(slot);
			if (item.getType() == this.productMaterial)
			{
				Bukkit.broadcastMessage("Found a matching type..");
				Integer amount = item.getAmount();
				Bukkit.broadcastMessage("Amount of found type: " + amount);
				if (amount > (this.GoalAmount-deliveredAmount))
				{
					Integer newAmount = item.getAmount()-(this.GoalAmount-deliveredAmount);
					item.setAmount(newAmount);
					inv.setItem(slot, item);
					deliveredAmount = this.GoalAmount;
					Bukkit.broadcastMessage("Found amount is more than the requested amount, removing needed amount of " + (this.GoalAmount-deliveredAmount));
					Bukkit.broadcastMessage("Delivered amount is set to " + deliveredAmount);
					break;
				} else if (amount == (this.GoalAmount-deliveredAmount))
				{
					inv.setItem(slot, null);
					deliveredAmount = this.GoalAmount;
					Bukkit.broadcastMessage("Found amount is equal to requested amount, removing the whole item");
					Bukkit.broadcastMessage("Delivered amount is set to " + deliveredAmount);
					break;
				} else if (amount < (this.GoalAmount-deliveredAmount))
				{
					inv.setItem(slot, null);
					deliveredAmount += amount;
					Bukkit.broadcastMessage("Found amount is smaller than requested amount, removing the whole item");
					Bukkit.broadcastMessage("Delivered amount is set to " + deliveredAmount);
				}
			}
			player.updateInventory();
		}
		Bukkit.broadcastMessage("Final delivered amount is " + deliveredAmount);
		if (deliveredAmount == this.GoalAmount)
		{
			this.complete();
		}
	}
}
