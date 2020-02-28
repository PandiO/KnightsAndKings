package Assignments;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import Handlers.ColorOptions;
import Towns.Town;

public class AssignmentTravelRandom extends Assignment {
	Town town = new Town();
	protected List<Integer> enteredTownList = new ArrayList<Integer>();

	public AssignmentTravelRandom(String Name, String Description, List<Integer> EnteredTownList, int GoalAmount, int CoinReward, int GemReward, int ExperienceReward, boolean isDaily) 
	{
		super(2, Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, isDaily);
		if (GoalAmount == -1)
		{
			this.GoalAmount = main.getRandom(2, this.town.getTownIDList().size()-1);
		}
		if (Description == null || GoalAmount == -1)
		{
			this.Description = "Travel to " + this.GoalAmount + " different towns";
		}
		if (CoinReward == -1)
		{
			this.CoinReward = ((int) 3000 * this.GoalAmount);
		}
		if (GemReward == -1)
		{
			this.GemReward = 0;
			if (this.GoalAmount == 5)
			{
				this.GemReward = 40;
			}
		}
		if (ExperienceReward == -1)
		{
			this.ExperienceReward = ((int) 100 * this.GoalAmount);
		}
		if (EnteredTownList != null && !EnteredTownList.isEmpty())
		{
			this.enteredTownList = EnteredTownList;
		}
	}
	
	public List<String> getEnteredTownList()
	{
		List<String> list = new ArrayList<String>();
		
		for (Integer townID : this.enteredTownList)
		{
			list.add(town.getTownName(townID));
		}
		
		return list;
	}
	
	public void enterTown(Integer townID)
	{
		if (!this.isCompleted)
		{
			Player player = this.User.getPlayer();
			if (!this.enteredTownList.contains(townID))
			{
				this.enteredTownList.add(townID);
				this.ProgressAmount++;
				
				if (this.ProgressAmount < this.GoalAmount)
				{
					this.User.playSound("successkillclick");
					player.sendMessage(ColorOptions.messageachievement + "You have entered " + this.ProgressAmount + "/" + this.GoalAmount + " towns!");
				} else
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
				String EnteredTownList = null;
				if (enteredTownList != null && !enteredTownList.isEmpty())
				{
					StringBuilder builder = new StringBuilder();
					builder.append(enteredTownList.get(0));
					for (int i = 1; i < enteredTownList.size(); i++)
					{
						builder.append(", " + enteredTownList.get(i));
					}
					EnteredTownList = builder.toString();
				}
				try
				{
					//prepare the query to retrieve uuid of a user by ID
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO AssignmentTravelRandom(ID, EnteredTownList) VALUES(?, ?);");	
					stmt.setInt(1, ID);
					stmt.setString(2, EnteredTownList);
					//Execute the query
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully saved the child progress of a travel random assignment of player with ID " + User.getID() + " to the Database!");
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to save child progress from a travel random Assignment with ID " + ID + " of player with ID " + User.getID());
				}
			}
		}.runTaskAsynchronously(main);
	}

}
