package Skills;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Main.Main;

public class SpecialSkill 
{
	//Get instance of the main class
	Main main = Main.getPlugin(Main.class);
	
	//Return the ID from the matching special skill
	public Integer getSpecialSkillID(String skill)
	{
		//Change the specified skill-name to the correct format
		switch(skill)
		{
		case "ninja":  skill = "Ninja";
				break;
		case "shotbow": skill = "Shotbow";
				break;
		case "pickpocket": skill = "Pickpocket";
			break;
		case "forger": skill = "Forger";
			break;
		case "assassin": skill = "Assassin";
			break;
		case "juggernaut": skill = "Juggernaut";
			break;
		case "avenger": skill = "Avenger";
			break;
		case "None": skill = "None";
			break;
		case "Default": skill = "None";
			break;
		default: skill = "None";
		}
		
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpecialSkill WHERE Name=?;");
			stmt.setString(1, skill);
			
			ResultSet results = stmt.executeQuery();
			
			//CHeck if data is found matching the name specified in the method
			if (results.next())
			{
				//return the result
				return results.getInt("ID");
			} else
			{
				return null;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getDescription(Integer ID)
	{
		String desc = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpecialSkill WHERE ID=?;");
			stmt.setInt(1, ID);
			
			ResultSet results = stmt.executeQuery();
			
			//CHeck if data is found matching the name specified in the method
			if (results.next())
			{
				//return the result
				return results.getString("Description");
			} else
			{
				return null;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return desc;
	}
	
	public ArrayList<Integer> getSpecialSkillList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpecialSkill;");
			
			ResultSet results = stmt.executeQuery();
			
			//CHeck if data is found matching the name specified in the method
			while (results.next())
			{
				//return the result
				list.add(results.getInt("ID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public String getName(Integer specialskillID)
	{
		String name = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpecialSkill WHERE ID=?;");
			stmt.setInt(1, specialskillID);
			
			ResultSet results = stmt.executeQuery();
			
			//Check if data is found matching the name specified in the method
			if (results.next())
			{
				//return the result
				name = results.getString("Name");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return name;
	}
}
