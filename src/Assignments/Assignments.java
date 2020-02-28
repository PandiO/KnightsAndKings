package Assignments;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

import Handlers.ColorOptions;
import Main.Main;
import Users.User;

public final class Assignments 
{
	static Main main = Main.getPlugin(Main.class);
	public static int RefreshPrice = 10;

	public static ResultSet fetchAssignments(User user)
	{
		ResultSet data = null;
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Assignments WHERE UserID = ?;");
			stmt.setInt(1, user.getID());
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user

			data = results;
		} catch(Exception e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to fetch data from the Assignment table");
		}
		
		return data;
	}
	
	public static void removeDailyAssignments(User user)
	{
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Assignments WHERE UserID = ?");
			stmt.setInt(1, user.getID());
			//Execute the query
			stmt.executeUpdate();
		} catch(Exception e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to remove Assignments of player with ID " + user.getID() + " from the Assignment table");
		}
	}
	
	public static List<Integer> fetchAssignmentIDList()
	{
		List<Integer> list = new ArrayList<Integer>();
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From AssignmentType;");	
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to fetch data from the Assignment table");
		}
		
		return list;
	}
	
	public static Assignment fetchChildData(Assignment assignment)
	{
		List<Integer> typeList = fetchAssignmentIDList();
		typeList.removeAll(Arrays.asList(3, 6));
		ResultSet results = null;
		
		Integer ID = assignment.getID();
		Integer TypeID = assignment.getTypeID();
		String Name = assignment.getName();
		String Description = assignment.getDescription();
		int GoalAmount = assignment.getGoalAmount();
		int ProgressAmount = assignment.getProgressAmount();
		int CoinReward = assignment.getCoinReward();
		int GemReward = assignment.getGemReward();
		int ExperienceReward = assignment.getExperienceReward();
		boolean IsDaily = assignment.isDaily();
		boolean IsCompleted = assignment.iscompleted();
		
		Assignment Assignment = null;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Select * From ");
		
		switch(TypeID)
		{
		case 1: builder.append("AssignmentTravelSpecific");
		break;
		case 2: builder.append("AssignmentTravelRandom");
		break;
		case 4: builder.append("AssignmentKill");
		break;
		case 5: builder.append("AssignmentHarvestRandom");
		break;
		case 7: builder.append("AssignmentEnterPropertySpecific");
		break;
		default: builder.append("AssignmentTravelSpecific");
		break;
		}
		
		builder.append(" WHERE ID = ?");
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement(builder.toString());
			stmt.setInt(1, assignment.getID());
			//Execute the query
			if (typeList.contains(TypeID))
			{
				ResultSet rawSet = stmt.executeQuery();
				if (rawSet.next())
				{
					results = rawSet;
				}
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Failed to fetch data from the Assignment table");
		}
		if (TypeID == 1)
		{
			Integer TownID = -1;
			try {
				TownID = results.getInt("TownID");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//AssignmentTravelSpecific
			Assignment = new AssignmentTravelSpecific(Name, Description, TownID, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 2)
		{
			//AssignmentTravelRandom
			List<Integer> IDList = null;
			String result = null;
			try {
				result = results.getString("EnteredTownList");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result != null && result.length() > 1)
			{
				IDList = new ArrayList<Integer>();
				if (result.contains(","))
				{
					String[] split = result.split(", ");
					for (String stringID : split)
					{
						IDList.add(Integer.valueOf(stringID));
					}
				} else
				{
					IDList.add(Integer.valueOf(result));
				}
			}
			Assignment = new AssignmentTravelRandom(Name, Description, IDList, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 3)
		{
			//AssignmentTravelDistance
			Assignment = new AssignmentTravelDistance(Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 4)
		{
			//AssignmentKill
			boolean onlyBandits = false;
			boolean onlyPlayers = false;
			try {
				onlyBandits = results.getBoolean("onlyBandits");
				onlyPlayers = results.getBoolean("onlyPlayers");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Assignment = new AssignmentKill(Name, Description, onlyBandits, onlyPlayers, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 5)
		{
			//AssignmentHarvestRandom
			Integer ProductID = -1;
			try {
				ProductID = results.getInt("ProductID");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Assignment = new AssignmentHarvestRandom(Name, Description, ProductID, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 6)
		{
			//AssignmentFoodConsumeRandom
			Assignment = new AssignmentFoodConsumeRandom(Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 7)
		{
			//AssignmentEnterPropertySpecific
			Integer PropertyID = -1;
			try {
				PropertyID = results.getInt("PropertyID");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Assignment = new AssignmentEnterPropertySpecific(Name, Description, PropertyID, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		}
		Assignment.setSavedData(ID, TypeID, ProgressAmount, IsCompleted);
		return Assignment;
	}
	
	public static Assignment createAssignment(User user, int TypeID, String Name, String Description, int GoalAmount, int CoinReward, int GemReward, int ExperienceReward, boolean IsDaily)
	{
		Assignment assignment = null;
		if (TypeID == -1)
		{
			TypeID = main.getRandom(1, 7);
		}
		if (TypeID == 1)
		{
			if (Name == null)
			{
				Name = "Know your Kingdom";
			}
			//AssignmentTravelSpecific
			assignment = new AssignmentTravelSpecific(Name, Description, -1, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 2)
		{
			if (Name == null)
			{
				Name = "Sightseer";
			}
			//AssignmentTravelRandom
			assignment = new AssignmentTravelRandom(Name, Description, null, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 3)
		{
			if (Name == null)
			{
				Name = "Traveller";
			}
			//AssignmentTravelDistance
			assignment = new AssignmentTravelDistance(Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 4)
		{
			if (Name == null)
			{
				Name = "Daily exercise";
			}
			boolean onlyBandits = false;
			boolean onlyPlayers = false;
			Integer odds = main.getRandom(1, 100);
			if (odds <= 50)
			{
				onlyBandits = true;
			} else
			{
				onlyPlayers = true;
			}
			//AssignmentKill
			assignment = new AssignmentKill(Name, Description, onlyBandits, onlyPlayers, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 5)
		{
			if (Name == null)
			{
				Name = "Hard labour";
			}
			//AssignmentHarvest
			assignment = new AssignmentHarvestRandom(Name, Description, -1, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 6)
		{
			if (Name == null)
			{
				Name = "An apple a day..";
			}
			//AssignmentFoodConsumeRandom
			assignment = new AssignmentFoodConsumeRandom(Name, Description, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		} else if (TypeID == 7)
		{
			if (Name == null)
			{
				Name = "Shopping spree";
			}
			//AssignmentEnterPropertySpecific
			assignment = new AssignmentEnterPropertySpecific(Name, Description, -1, GoalAmount, CoinReward, GemReward, ExperienceReward, IsDaily);
		}

		return assignment;
	}
	
	
}
