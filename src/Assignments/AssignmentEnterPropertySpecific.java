package Assignments;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import Handlers.ColorOptions;
import Properties.Property;
import Streets.Street;
import Towns.Town;

public class AssignmentEnterPropertySpecific extends Assignment
{
	Property property = new Property();
	Town town = new Town();
	Street street = new Street();
	
	protected int propertyID;
	protected int townID;
	protected int streetID;
	protected int streetNumber;
	protected String townName;
	protected String streetName;
	protected String propertyName;
	
	public AssignmentEnterPropertySpecific(String Name, String Description, int propertyID, int GoalAmount, int CoinReward,
			int GemReward, int ExperienceReward, boolean isDaily) 
	{
		super(7, Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, isDaily);
		if (propertyID == -1)
		{
			List<Integer> list = this.property.getIDList(null, null);
			Collections.shuffle(list);
			propertyID = list.get(0);
		}
		if (CoinReward == -1)
		{
			this.CoinReward = 10000;
		}
		if (GemReward == -1)
		{
			this.GemReward = 5;
		}
		if (ExperienceReward == -1)
		{
			this.ExperienceReward = 0;
		}
		
		this.propertyID = propertyID;
		this.streetID = this.property.getStreetID(this.propertyID);
		this.townID = this.street.getTownID(this.streetID);
		this.streetNumber = this.property.getStreetNumber(this.propertyID);
		this.townName = this.town.getTownName(this.townID);
		this.streetName = this.street.getStreetName(this.streetID);
		this.propertyName = this.property.getPropertyName(this.propertyID);
		this.GoalAmount = 1;
		
		if (Description == null || propertyID == -1)
		{
			this.Description = "Find and enter the " + this.propertyName + " on " + this.streetName + " at number " + this.streetNumber;
		}
	}
	
	public int getPropertyID()
	{
		return this.propertyID;
	}
	
	public int getTownID()
	{
		return this.townID;
	}
	
	public int getStreetID()
	{
		return this.streetID;
	}
	
	public int getStreetNumber()
	{
		return this.streetNumber;
	}
	
	public String getTownName()
	{
		return this.townName;
	}
	
	public String getStreetName()
	{
		return this.streetName;
	}
	
	public String getPropertyName()
	{
		return this.propertyName;
	}
	
	public void enterProperty(int propertyID)
	{
		if (this.isCompleted)
		{
			return;
		}
		if (this.propertyID == propertyID)
		{
			this.ProgressAmount++;
			this.goalReached();
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
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO AssignmentEnterPropertySpecific(ID, PropertyID) VALUES(?, ?);");	
					stmt.setInt(1, ID);
					stmt.setInt(2, propertyID);
					//Execute the query
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully saved the child progress of a enter-property specific assignment of player with ID " + User.getID() + " to the Database!");
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to save child progress from a enter-property specific Assignment with ID " + ID + " of player with ID " + User.getID());
				}
			}
		}.runTaskAsynchronously(main);
	}
	
}
