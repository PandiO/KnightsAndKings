package Assignments;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import Handlers.ColorOptions;
import Towns.Town;

public class AssignmentTravelSpecific extends Assignment
{
	Town town = new Town();
	protected int townID;
	public AssignmentTravelSpecific(String Name, String Description, int townID, int GoalAmount, int CoinReward,
			int GemReward, int ExperienceReward, boolean isDaily) {
		super(1, Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, isDaily);
		this.GoalAmount = 1;
		if (townID == -1)
		{
			townID = this.getRandomTown();
		}
		this.townID = townID;
		if (Description == null)
		{
			this.Description = "Travel to the town of " + this.town.getTownName(this.townID);
		}
		if (CoinReward == -1)
		{
			this.CoinReward = 12500;
		}
		if (GemReward == -1)
		{
			this.GemReward = 0;
		}
		if (ExperienceReward == -1)
		{
			this.ExperienceReward = 250;
		}
	}
	
	public int getTownID()
	{
		return this.townID;
	}
	
	protected int getRandomTown()
	{
		List<Integer> townIDList = this.town.getTownIDList();
		townIDList.remove(this.town.getTownID("Wilderness"));
		Collections.shuffle(townIDList);
		
		return townIDList.get(0);
	}
	
	public void enterTown(Integer townID)
	{
		if (this.isCompleted)
		{
			return;
		}
		if (townID == this.townID)
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
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO AssignmentTravelSpecific(ID, TownID) VALUES(?, ?);");	
					stmt.setInt(1, ID);
					stmt.setInt(2, townID);
					//Execute the query
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully saved the child progress of a travel specific assignment of player with ID " + User.getID() + " to the Database!");
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to save child progress from a travel specific Assignment with ID " + ID + " of player with ID " + User.getID());
				}
			}
		}.runTaskAsynchronously(main);
	}
}
