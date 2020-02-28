package Quests;

import java.util.Collections;
import java.util.List;

import Properties.Property;
import Properties.PropertyCategory;
import Streets.Street;
import Towns.Town;
import Users.User;

public class QuestIntimidateRival extends Quest
{
	PropertyCategory propertyCategory = new PropertyCategory();
	Property property = new Property();
	Street street = new Street();
	Town town = new Town();
	
	protected Integer categoryID;
	protected String categoryName;
	protected Integer targetPropertyID;
	protected String targetPropertyName;
	protected Integer targetStreetID;
	protected Integer targetStreetNumber;
	protected Integer targetTownID;
	protected String targetStreetName;
	protected String targetTownName;
	protected Integer requiredTitle;
	protected Integer shopkeeperID;
	
	public QuestIntimidateRival(String Name, String Description, int PropertyID, int targetPropertyID, int GoalAmount, int CoinReward, int GemReward, int ExperienceReward, User User)
	{
		super(2, Name, Description, PropertyID, GoalAmount, CoinReward, GemReward, ExperienceReward, (System.currentTimeMillis() + 600*1000));
		
		boolean shouldActivate = true;
		
		this.categoryID = this.property.getCategoryID(this.PropertyID);
		this.categoryName = this.propertyCategory.getCategoryName(this.categoryID);
		
		if (targetPropertyID == -1)
		{
			Integer townID = null;
			
			if (main.getRandom(1, 100) <= 50)
			{
				townID = this.street.getTownID(this.property.getStreetID(this.PropertyID));
			}
			
			List<Integer> IDList = this.property.getIDListbyCategory(this.categoryName, townID);
			if (IDList.contains(this.PropertyID))
			{
				IDList.remove(IDList.indexOf(Integer.valueOf(this.PropertyID)));
			}
			if (IDList.isEmpty())
			{
				IDList = this.property.getIDListbyCategory(this.categoryName, null);
				IDList.remove(Integer.valueOf(this.PropertyID));
				if (IDList.isEmpty())
				{
					return;
				}
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
			this.requiredTitle = this.town.getRequiredTitleID(this.targetTownID);
		} catch (Exception ex)
		{
			shouldActivate = false;
			ex.printStackTrace();
		}
		
		if (this.Description == null)
		{
			this.Description = "Find and kill the shopkeeper from the rival " + this.categoryName + ", the " + this.targetPropertyName;
		}
		
		if (CoinReward == -1)
		{
			int CalculatedCoinReward = 0;
			if (requiredTitle > 0 && requiredTitle < 5)
			{
				CalculatedCoinReward = main.getRandom(2500, 9500);
			} else if (requiredTitle >= 5 && requiredTitle < 10)
			{
				CalculatedCoinReward = main.getRandom(7000, 17500);
			} else if (requiredTitle >= 10 && requiredTitle < 15)
			{
				CalculatedCoinReward = main.getRandom(15000, 30000);
			} else if (requiredTitle >= 15)
			{
				CalculatedCoinReward = main.getRandom(25000, 38000);
			} else
			{
				CalculatedCoinReward = main.getRandom(2500, 12000);
			}
			
			CalculatedCoinReward = ((int) (CalculatedCoinReward * ((float) this.property.getContribution(this.targetPropertyID)/100)));

			this.CoinReward = CalculatedCoinReward;
		}
		if (GemReward == -1)
		{
			if (requiredTitle >= 10 && this.property.getContribution(this.targetPropertyID) >= 10)
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
			if (requiredTitle > 0 && requiredTitle < 5)
			{
				CalculatedExpReward = main.getRandom(25, 150);
			} else if (requiredTitle >= 5 && requiredTitle < 10)
			{
				CalculatedExpReward = main.getRandom(150, 400);
			} else if (requiredTitle >= 10 && requiredTitle < 15)
			{
				CalculatedExpReward = main.getRandom(800, 1200);
			} else if (requiredTitle >= 15)
			{
				CalculatedExpReward = main.getRandom(1200, 1800);
			} else
			{
				CalculatedExpReward = main.getRandom(25, 250);
			}

			CalculatedExpReward = ((int) (CalculatedExpReward * ((float) this.property.getContribution(this.targetPropertyID)/100)));

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
	
	public Integer getTargetCategory()
	{
		return this.categoryID;
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
	
	public void killShopkeeper(Integer npcID)
	{
		if (this.shopkeeperID.equals(npcID) && !this.isCollected)
		{
			this.ProgressAmount += 1;
			this.goalReached();
		}
	}
}
