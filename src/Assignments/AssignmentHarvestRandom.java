package Assignments;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import Handlers.ColorOptions;
import Products.Product;

public class AssignmentHarvestRandom extends Assignment 
{
	Product product = new Product();
	
	protected List<Integer> productList = new ArrayList<Integer>(Arrays.asList(
			30, 32, 33, 34, 29, 19, 35, 80, 82, 83, 77, 78, 86, 87
			));
	protected int productID;
	protected int Grade;
	protected Material productMaterial;
	
	public AssignmentHarvestRandom(String Name, String Description, int productID, int GoalAmount, int CoinReward, int ExperienceReward,
			int GemReward, boolean isDaily) 
	{
		super(5, Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, isDaily);
		if (productID == -1 || !this.productList.contains(productID))
		{
			Collections.shuffle(this.productList);
			productID = this.productList.get(0);
		}
		this.productID = productID;
		this.Grade = this.product.getGrade(this.productID, false);
		
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
		this.productMaterial = this.product.createPropertyItem(this.productID, 1, false, false).getType();
		if (Description == null || productID == -1)
		{
			this.Description = "Collect " + this.GoalAmount + " times " + this.product.getDisplayName(productID, false);
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
				CalculatedCoinReward = (int) ((int) (CalculatedCoinReward+4000) * ((double)1 + (this.Grade/10)));
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
				CalculatedExpReward = CalculatedExpReward * (1 + (this.Grade/100));
			}
			this.ExperienceReward = CalculatedExpReward;
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
	
	public List<Integer> getProductList()
	{
		return this.productList;
	}
	
	public void Harvest(ItemStack item)
	{
		if (this.isCompleted == false)
		{
			if (item.getType() == this.productMaterial)
			{
				this.ProgressAmount+=item.getAmount();
				
				if (this.ProgressAmount >= this.GoalAmount)
				{
					this.goalReached();
				}
			}
		}
	}
	
	//First saves the parent Assignment to the database, then retrieves the ID of the parent from the database and finally saves the progress of the child (this class)
	public void saveAll()
	{
		this.save();
		
		this.saveTask = new BukkitRunnable()
				{
					public void run()
					{
						retrieveID();
						if (ID != -1)
						{
							saveTask.cancel();
							saveProgress();
						}
					}
				}.runTaskTimerAsynchronously(main, 20, 20);
		
	}
	
	
	//Saves the progress for this specific type of assignment to its matching Database table
	public void saveProgress()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				try
				{
					//prepare the query to retrieve uuid of a user by ID
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO AssignmentHarvestRandom(ID, ProductID) VALUES(?, ?);");	
					stmt.setInt(1, ID);
					stmt.setInt(2, productID);
					//Execute the query
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully saved the child progress of a harvest random assignment of player with ID " + User.getID() + " to the Database!");
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to save child progress from a harvest random Assignment with ID " + ID + " of player with ID " + User.getID());
				}
			}
		}.runTaskAsynchronously(main);
	}
	
}
