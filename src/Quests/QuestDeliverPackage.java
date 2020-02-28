package Quests;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import Handlers.ColorOptions;
import Products.Product;
import Products.PropertyProduct;
import Properties.Property;
import Properties.PropertyCategory;
import Streets.Street;
import Towns.Town;
import Users.User;

public class QuestDeliverPackage extends Quest
{
	PropertyCategory propertyCategory = new PropertyCategory();
	PropertyProduct propertyProduct = new PropertyProduct();
	Product product = new Product();
	Property property = new Property();
	Street street = new Street();
	Town town = new Town();
	
	protected String categoryName = "warehouse";
	protected Integer targetPropertyID;
	protected String targetPropertyName;
	protected Integer targetStreetID;
	protected Integer targetStreetNumber;
	protected Integer targetTownID;
	protected String targetStreetName;
	protected String targetTownName;
	protected Integer requiredTitle;
	protected Integer shopkeeperID;
	protected Integer productID;
	protected ItemStack deliverable;
	
	public QuestDeliverPackage(String Name, String Description, int PropertyID, int targetPropertyID, int productID, int GoalAmount, int CoinReward, int GemReward, int ExperienceReward, User User)
	{
		super(3, Name, Description, PropertyID, GoalAmount, CoinReward, GemReward, ExperienceReward, (System.currentTimeMillis() + 600*1000));
		
		boolean shouldActivate = true;

		if (targetPropertyID == -1)
		{
			Integer townID = null;
			
			if (main.getRandom(1, 100) <= 50)
			{
				townID = this.street.getTownID(this.property.getStreetID(PropertyID));
			}
			
			CopyOnWriteArrayList<Integer> IDList = new CopyOnWriteArrayList<Integer>(this.property.getIDListbyCategory(this.categoryName, townID));
			if (IDList.contains(this.PropertyID))
			{
				IDList.remove(Integer.valueOf(this.PropertyID));
			}
			if (IDList.size() <= 0)
			{
				IDList = new CopyOnWriteArrayList<Integer>(this.property.getIDListbyCategory(this.categoryName, null));
				IDList.remove(Integer.valueOf(this.PropertyID));
				if (IDList.isEmpty())
				{
					return;
				}
			}
			for (Integer ID : IDList)
			{
				if (Properties.Properties.hasActiveQuest(ID))
				{
					IDList.remove(ID);
				}
			}
			if (IDList.size() <= 0)
			{
				shouldActivate = false;
				return;
			}
			Collections.shuffle(IDList);
			this.targetPropertyID = IDList.get(main.getRandom(0, IDList.size()-1));
			
		}
		this.shopkeeperID = this.property.getNPCID(this.targetPropertyID);
		this.targetPropertyName = this.property.getPropertyName(this.targetPropertyID);
		this.targetStreetID = this.property.getStreetID(this.targetPropertyID);
		this.targetStreetNumber = this.property.getStreetNumber(this.targetPropertyID);
		
		try
		{
			this.targetStreetName = this.street.getStreetName(targetStreetID);
			this.targetTownID = this.street.getTownID(targetStreetID);
			this.targetTownName = this.town.getTownName(targetTownID);
			this.requiredTitle = this.town.getRequiredTitleID(targetTownID);
		} catch (Exception ex)
		{
			shouldActivate = false;
			ex.printStackTrace();
		}
		
		if (productID == -1)
		{
			List<Integer> IDList = this.propertyProduct.getProductListbyProperty(this.PropertyID);
			
			for (Integer ID : IDList)
			{
				Integer relationID = this.propertyProduct.getRelationID(ID, this.PropertyID);
				if (productID == -1)
				{
					productID = ID;
				} else
				{
					Integer price = this.propertyProduct.getPrice(relationID);
					if (price > this.propertyProduct.getPrice(this.propertyProduct.getRelationID(productID, this.PropertyID)))
					{
						productID = ID;
					}
				}
			}
			this.productID = productID;
			ItemStack item = this.product.createPropertyItem(this.productID, 1, false, false);
			if (item.getMaxStackSize() == 1)
			{
				this.GoalAmount = 1;
			} else
			{
				this.GoalAmount = main.getRandom(8, 64);
			}
			this.deliverable = this.product.createAmountItem(
					Material.CHEST,
					1,
					ChatColor.BLUE + "Package", ColorOptions.message + "Package containing " + this.GoalAmount + " " + this.product.getDisplayName(this.productID, false)
					);
		}
		
		if (this.Description == null)
		{
			this.Description = "Deliver " + this.GoalAmount + " pieces of " + this.product.getDisplayName(this.productID, false) + " to a warehouse called the " + this.targetPropertyName;
		}
		
		if (CoinReward == -1)
		{
			int CalculatedCoinReward = 0;
			if (requiredTitle > 0 && requiredTitle < 5)
			{
				CalculatedCoinReward = main.getRandom(2500, 6500);
			} else if (requiredTitle >= 5 && requiredTitle < 10)
			{
				CalculatedCoinReward = main.getRandom(5000, 10000);
			} else if (requiredTitle >= 10 && requiredTitle < 15)
			{
				CalculatedCoinReward = main.getRandom(8000, 15000);
			} else if (requiredTitle >= 15)
			{
				CalculatedCoinReward = main.getRandom(13000, 20000);
			} else
			{
				CalculatedCoinReward = main.getRandom(2500, 10000);
			}
			
			CalculatedCoinReward = ((int) (CalculatedCoinReward * ((float) this.property.getContribution(this.PropertyID)/100)));

			this.CoinReward = CalculatedCoinReward;
		}
		
		if (GemReward == -1)
		{
			if (requiredTitle >= 10 && this.property.getContribution(this.targetPropertyID) >= 10)
			{
				this.GemReward = main.getRandom(5, 10);
			} else
			{
				this.GemReward = 0;
			}
		}
		if (ExperienceReward == -1)
		{
			int CalculatedExpReward = 0;
			if (requiredTitle > 0 && requiredTitle < 5)
			{
				CalculatedExpReward = main.getRandom(25, 150);
			} else if (requiredTitle >= 5 && requiredTitle < 10)
			{
				CalculatedExpReward = main.getRandom(120, 200);
			} else if (requiredTitle >= 10 && requiredTitle < 15)
			{
				CalculatedExpReward = main.getRandom(180, 7500);
			} else if (requiredTitle >= 15)
			{
				CalculatedExpReward = main.getRandom(750, 1500);
			} else
			{
				CalculatedExpReward = main.getRandom(25, 250);
			}

			CalculatedExpReward = ((int) (CalculatedExpReward * ((float) this.property.getContribution(this.PropertyID)/100)));

			this.ExperienceReward = CalculatedExpReward;
		}
		
		if(shouldActivate)
		{
			this.activate();
		}
	}
	
	public Integer getTargetPropertyID()
	{
		return this.targetPropertyID;
	}
	
	public String getTargetCategoryName()
	{
		return this.categoryName;
	}
	
	public String getTargetPropertyName()
	{
		return this.targetPropertyName;
	}
	
	public Integer getTargetStreetID()
	{
		return this.targetStreetID;
	}
	
	public Integer getTargetStreetNumber()
	{
		return this.targetStreetNumber;
	}
	
	public Integer getTargetTownID()
	{
		return this.targetTownID;
	}
	
	public String getTargetTownName()
	{
		return this.targetTownName;
	}
	
	public Integer getRequiredTitle()
	{
		return this.requiredTitle;
	}
	
	public Integer getTargetShopkeeperID()
	{
		return this.shopkeeperID;
	}
	
	public Integer getProductID()
	{
		return this.productID;
	}
	
	public void assign(User user, int index)
	{
		this.isCollected = true;
		asign(user, index);
		user.getPlayer().getInventory().addItem(this.deliverable);
	}
	
	public void Deliver(ItemStack item)
	{
		if (this.isCompleted)
		{
			return;
		}
		if (!item.hasItemMeta())
		{
			return;
		}
		Integer productID = this.product.getProductIDbyDisplayName(item.getItemMeta().getDisplayName(), false);
		if (productID == null)
		{
			return;
		}
		if (productID == this.productID)
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
}
