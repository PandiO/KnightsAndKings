package Assignments;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;

public class Assignment 
{
	protected Main main = Main.getPlugin(Main.class);
	protected int ID = -1;
	protected int TypeID = -1;
	protected String Name;
	protected String Description;
	protected int GoalAmount;
	protected int ProgressAmount;
	protected User User;
	protected int CoinReward;
	protected int GemReward;
	protected int ExperienceReward;
	protected boolean isDaily = false;
	protected boolean isCompleted = false;
	protected boolean isCollected = false;
	protected BukkitTask saveTask;
	protected int RefreshPrice = 10;
	protected Assignment Assignment = this;
	
	public Assignment(int TypeID, String Name, String Description, int GoalAmount, int CoinReward, int GemReward, int ExperienceReward, boolean isDaily)
	{
		this.TypeID = TypeID;
		this.Name = Name;
		this.Description = Description;
		this.GoalAmount = GoalAmount;
		this.CoinReward = CoinReward;
		this.GemReward = GemReward;
		this.ExperienceReward = ExperienceReward;
		this.isDaily = isDaily;
	}
	
	public void setSavedData(int ID, int TypeID, int ProgressAmount, boolean isCompleted)
	{
		this.ID = ID;
		this.TypeID = TypeID;
		this.ProgressAmount = ProgressAmount;
		this.isCompleted = isCompleted;
	}
	
	public int getID()
	{
		return this.ID;
	}
	
	public int getTypeID()
	{
		return this.TypeID;
	}
	
	public String getName()
	{
		return this.Name;
	}
	
	public String getDescription()
	{
		return this.Description;
	}
	
	public int getGoalAmount()
	{
		return this.GoalAmount;
	}
	
	public int getProgressAmount()
	{
		return this.ProgressAmount;
	}
	
	public User getUser()
	{
		return this.User;
	}
	
	public boolean isDaily()
	{
		return this.isDaily;
	}
	
	public boolean iscompleted()
	{
		return this.isCompleted;
	}
	
	public boolean isCollected()
	{
		return this.isCollected;
	}
	
	public int getCoinReward()
	{
		return this.CoinReward;
	}
	
	public int getGemReward()
	{
		return this.GemReward;
	}
	
	public int getExperienceReward()
	{
		return this.ExperienceReward;
	}
	
	public int getRefreshPrice()
	{
		return this.RefreshPrice;
	}
	
	public void addProgress(int ProgressAmount)
	{
		if (this.isCompleted == false)
		{
			this.ProgressAmount = this.ProgressAmount + ProgressAmount;
			
			if (this.ProgressAmount >= this.GoalAmount)
			{
				this.goalReached();
			}
		}
	}
	
	public void asign(User user, boolean isDaily, int index)
	{
		this.User = user;
		this.isDaily = isDaily;
		this.User.addAssignment(this, index);
	}
	
	public void goalReached()
	{
		Player player = this.User.getPlayer();
		this.isCompleted = true;
		player.playSound(player.getLocation(), SoundHandler.LEVEL_UP, 0.1F, 0.1F);
		player.sendMessage(ColorOptions.messageachievement + "You have completed an Assignment!");
		player.sendMessage(ColorOptions.message + "Go to your assignments and claim your reward!");
	}
	
	public void complete()
	{
		this.isCollected = true;
		Player player = this.User.getPlayer();
		this.User.addCoins(CoinReward);
		this.User.addGems(GemReward);
		this.User.addExperience(this.ExperienceReward, true);
		player.sendMessage(ColorOptions.messageachievement + "You received " + ColorOptions.coinStats + ColorOptions.formatCurrency(this.CoinReward) + " Coins, " + ColorOptions.coinStats + ColorOptions.formatCurrency(this.GemReward) + " Gems" + ColorOptions.messageachievement + " and " + ColorOptions.coinStats + ColorOptions.formatCurrency(this.ExperienceReward) + " Experience");
	}
	
	public void save()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				try
				{
					//prepare the query to retrieve uuid of a user by ID
					PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Assignments(Name, Description, GoalAmount, ProgressAmount, UserID, CoinReward, GemReward, ExperienceReward, IsDaily, IsCompleted, IsCollected, TypeID, ListIndex) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");	
					stmt.setString(1, Name);
					stmt.setString(2, Description);
					stmt.setInt(3, GoalAmount);
					stmt.setInt(4, ProgressAmount);
					stmt.setInt(5, User.getID());
					stmt.setInt(6, CoinReward);
					stmt.setInt(7, GemReward);
					stmt.setInt(8, ExperienceReward);
					stmt.setBoolean(9, isDaily);
					stmt.setBoolean(10, isCompleted);
					stmt.setBoolean(11, isCollected);
					stmt.setInt(12, TypeID);
					stmt.setInt(13, User.getAssignmentList().indexOf(Assignment));
					//Execute the query
					stmt.executeUpdate();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully saved the progress of an assignment of player with ID " + User.getID() + " to the Database!");
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to save progress from an Assignment of player with ID " + User.getID());
				}
			}
		}.runTaskAsynchronously(main);
	}
	
	public void retrieveID()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				try
				{
					//prepare the query to retrieve uuid of a user by ID
					PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Assignments WHERE UserID = ? AND Name = ? AND Description = ?;");	
					stmt.setInt(1, User.getID());
					stmt.setString(2, Name);
					stmt.setString(3, Description);
					//Execute the query
					ResultSet result = stmt.executeQuery();
					
					if (result.next())
					{
						ID = result.getInt("ID");
					}
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to fetch the ID of an Assignment of player with ID " + User.getID());
				}
			}
		}.runTaskAsynchronously(main);
	}
	
}
