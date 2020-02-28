package Assignments;

public class AssignmentTravelDistance extends Assignment{
		
	public AssignmentTravelDistance(String Name, String Description, int GoalAmount, int CoinReward,
			int GemReward, int ExperienceReward, boolean isDaily) 
	{
		super(3, Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, isDaily);
		if (GoalAmount == -1)
		{
			this.GoalAmount = this.main.getRandom(250, 2500);
		}
		if (Description == null || GoalAmount == -1)
		{
			this.Description = "Travel for a distance of " + this.GoalAmount + " blocks";
		}
		if (CoinReward == -1)
		{
			if (this.GoalAmount > 2000)
			{
				this.CoinReward = main.getRandom(12500, 15000);
			} else if (this.GoalAmount <= 2000 && this.GoalAmount > 1500)
			{
				this.CoinReward = main.getRandom(7500, 12500);
			} else if (this.GoalAmount <= 1500 && this.GoalAmount > 1000)
			{
				this.CoinReward = main.getRandom(5000, 7500);
			} else
			{
				this.CoinReward = main.getRandom(2250, 5000);
			}
		}
		if (GemReward == -1)
		{
			this.GemReward = 0;
			if (this.GoalAmount == 2500)
			{
				this.GemReward = 50;
			}
		}
		if (ExperienceReward == -1)
		{
			if (this.GoalAmount > 2000)
			{
				this.ExperienceReward = main.getRandom(250, 450);
			} else if (this.GoalAmount <= 2000 && this.GoalAmount > 1500)
			{
				this.ExperienceReward = main.getRandom(175, 250);
			} else if (this.GoalAmount <= 1500 && this.GoalAmount > 1000)
			{
				this.ExperienceReward = main.getRandom(125, 175);
			} else
			{
				this.ExperienceReward = main.getRandom(30, 120);
			}
		}
	}
	
	public void addDistance(int distanceInBlocks)
	{
		if (this.isCompleted == false)
		{
			this.ProgressAmount = this.ProgressAmount + distanceInBlocks;
			
			if (this.ProgressAmount >= this.GoalAmount)
			{
				this.goalReached();
			}
		}
	}
	
	//First saves the parent Assignment to the database, then retrieves the ID of the parent from the database and finally saves the progress of the child (this class)
	public void saveAll()
	{
		this.save();
	}

}
