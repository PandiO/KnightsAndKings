package Genders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Main.Main;

public class Gender 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	
	public String getGenderName(Integer genderID)
	{
		String gender = null;
		
		//Surround the get statement for donatorID with a try and catch for errors
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Gender WHERE ID=?;");
			stmt.setInt(1, genderID);
			
			ResultSet results = stmt.executeQuery();
			//Check if a name has been found matching the DonatorID
			if (results.next())
			{
				gender = results.getString("Name");
			}	
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return gender;
	}
	
	public String getPrefix(Integer genderID)
	{
		String prefix = null;
		
		//Surround the get statement for donatorID with a try and catch for errors
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Gender WHERE ID=?;");
			stmt.setInt(1, genderID);
			
			ResultSet results = stmt.executeQuery();
			//Check if a name has been found matching the DonatorID
			if (results.next())
			{
				prefix = results.getString("Prefix");
			}	
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return prefix;
	}
	
	public Integer getID(String gender)
	{
		Integer id = null;
		
		//Surround the get statement for donatorID with a try and catch for errors
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Gender WHERE Name=?;");
			stmt.setString(1, gender);
			
			ResultSet results = stmt.executeQuery();
			//Check if a name has been found matching the DonatorID
			if (results.next())
			{
				id = results.getInt("ID");
			}	
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
}
