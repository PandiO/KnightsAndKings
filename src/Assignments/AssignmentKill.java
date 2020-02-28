package Assignments;

import java.sql.PreparedStatement;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import Handlers.ColorOptions;
import Main.Main;

public class AssignmentKill extends Assignment
{
	Main main = Main.getPlugin(Main.class);
	protected boolean onlyBandits;
	protected boolean onlyPlayers;
	
	public AssignmentKill(String Name, String Description, boolean onlyBandits, boolean onlyPlayers, int GoalAmount, int CoinReward, int GemReward,
			int ExperienceReward, boolean isDaily) 
	{
		super(4, Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, isDaily);
		this.onlyBandits = onlyBandits;
		this.onlyPlayers = onlyPlayers;
		if (GoalAmount == -1)
		{
			if (this.onlyBandits && !this.onlyPlayers)
			{
				this.GoalAmount = main.getRandom(8, 25);
			} else if (!this.onlyBandits && this.onlyPlayers)
			{
				this.GoalAmount = main.getRandom(1, 8);
			} else
			{
				this.GoalAmount = main.getRandom(4, 15);
			}
		}
		if (Description == null || GoalAmount == -1)
		{
			StringBuilder builder = new StringBuilder();
			builder.append("Kill " + this.GoalAmount);
			if (this.onlyBandits == true && this.onlyPlayers == false)
			{
				builder.append(" Bandits");
			} else if (this.onlyBandits == false && this.onlyPlayers == true)
			{
				builder.append(" Players");
			} else
			{
				builder.append(" Bandits or Players");
			}
			this.Description = builder.toString();
		}
		if (CoinReward == -1)
		{
			if (this.onlyBandits && !this.onlyPlayers)
			{
				if (this.GoalAmount > 15)
				{
					this.CoinReward = main.getRandom(10000, 15000);
				} else if (this.GoalAmount <= 15)
				{
					this.CoinReward = main.getRandom(5000, 10000);
				}
			} else if (!this.onlyBandits && this.onlyPlayers)
			{
				if (this.GoalAmount > 4)
				{
					this.CoinReward = main.getRandom(10000, 15000);
				} else if (this.GoalAmount <= 4)
				{
					this.CoinReward = main.getRandom(5000, 10000);
				}
			} else
			{
				if (this.GoalAmount > 10)
				{
					this.CoinReward = main.getRandom(12000, 17500);
				} else if (this.GoalAmount <= 10)
				{
					this.CoinReward = main.getRandom(5000, 11500);
				}
			}
		}
		if (GemReward == -1)
		{
			this.GemReward = 0;
		}
		if (ExperienceReward == -1)
		{
			if (this.onlyBandits && !this.onlyPlayers)
			{
				if (this.GoalAmount > 15)
				{
					this.ExperienceReward = main.getRandom(100, 200);
				} else if (this.GoalAmount <= 15)
				{
					this.ExperienceReward = main.getRandom(20, 100);
				}
			} else if (!this.onlyBandits && this.onlyPlayers)
			{
				if (this.GoalAmount > 4)
				{
					this.ExperienceReward = main.getRandom(150, 250);
				} else if (this.GoalAmount <= 4)
				{
					this.ExperienceReward = main.getRandom(50, 150);
				}
			} else
			{
				if (this.GoalAmount > 10)
				{
					this.ExperienceReward = main.getRandom(100, 225);
				} else if (this.GoalAmount <= 10)
				{
					this.ExperienceReward = main.getRandom(20, 120);
				}
			}
		}
	}
	
	public boolean isOnlyPlayers()
	{
		return this.onlyPlayers;
	}
	
	public boolean isOnlyBandits()
	{
		return this.onlyBandits;
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
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO AssignmentKill(ID, onlyBandits, onlyPlayers) VALUES(?, ?, ?);");	
					stmt.setInt(1, ID);
					stmt.setBoolean(2, onlyBandits);
					stmt.setBoolean(3, onlyPlayers);
					//Execute the query
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully saved the child progress of a kill assignment of player with ID " + User.getID() + " to the Database!");
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to save child progress from a kill Assignment with ID " + ID + " of player with ID " + User.getID());
				}
			}
		}.runTaskAsynchronously(main);
	}
}
