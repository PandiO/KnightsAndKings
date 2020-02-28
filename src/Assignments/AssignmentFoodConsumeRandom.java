package Assignments;

import Main.Main;

public class AssignmentFoodConsumeRandom extends Assignment
{
	Main main = Main.getPlugin(Main.class);
	public AssignmentFoodConsumeRandom(String Name, String Description, int GoalAmount, int CoinReward,
			int GemReward, int ExperienceReward, boolean isDaily) {
		super(6, Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, isDaily);
		if (GoalAmount == -1)
		{
			this.GoalAmount = main.getRandom(64, 128);
		}
		if (Description == null || GoalAmount == -1)
		{
			this.Description = "Consume any food " + this.GoalAmount + " times";
		}
		if (GemReward == -1)
		{
			this.GemReward = 0;
		}
		if (ExperienceReward == -1)
		{
			if (this.GoalAmount > 200)
			{
				this.ExperienceReward = main.getRandom(250, 500);
			} else if (this.GoalAmount <= 200 && this.GoalAmount > 100)
			{
				this.ExperienceReward = main.getRandom(100, 350);
			} else if (this.GoalAmount <= 100 && this.GoalAmount > 50)
			{
				this.ExperienceReward = main.getRandom(50, 100);
			} else
			{
				this.ExperienceReward = main.getRandom(10, 50);
			}
		}
		if (CoinReward == -1)
		{
			if (this.GoalAmount > 200)
			{
				this.CoinReward = main.getRandom(7500, 17500);
			} else if (this.GoalAmount <= 200 && this.GoalAmount > 100)
			{
				this.CoinReward = main.getRandom(4500, 8000);
			} else if (this.GoalAmount <= 100 && this.GoalAmount > 50)
			{
				this.CoinReward = main.getRandom(3500, 5000);
			} else
			{
				this.CoinReward = main.getRandom(1750, 3500);
			}
		}
	}
	
	public void consume(int consumeAmount)
	{
		if (this.isCompleted)
		{
			return;
		}
		this.ProgressAmount = this.ProgressAmount + consumeAmount;
		if (this.ProgressAmount >= this.GoalAmount)
		{
			this.goalReached();
		}
	}
	
	//First saves the parent Assignment to the database, then retrieves the ID of the parent from the database and finally saves the progress of the child (this class)
	public void saveAll()
	{
		this.save();
	}
}
