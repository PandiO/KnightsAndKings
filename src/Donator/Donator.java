package Donator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;

import Main.Main;

public class Donator 
{
	//Get instances of required classes
	static Main main = Main.getPlugin(Main.class);
	
	//Get the total amount of titles currently available in the game
	public Integer getDonatorRankAmount()
	{
		Integer amount = 0;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				amount = amount + 1;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return amount;
	}
	
	//Get a list of all donator names available in the game
	public ArrayList<String> getDonatorNames()
	{
		ArrayList<String> names = new ArrayList<String>();
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				names.add(results.getString("Name").toLowerCase());
			}
			return names;
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return names;
	}
	
	//Get the donator ID through the donator name from the database
	public Integer getDonatorID(String donatorName)
	{
		Integer ID = null;
		String finalName = null;
		
		for (String name : getDonatorNames())
		{
			if (donatorName.equalsIgnoreCase(name))
			{
				finalName = name;
				break;
			}
		}

		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE Name=?;");
			stmt.setString(1, finalName);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				ID = results.getInt("ID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return ID;
	}
	
	//Get the donator name through the donator ID from the database
	public String getDonatorName(Integer ID)
	{
		String name = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE ID=?;");
			stmt.setInt(1, ID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				name = results.getString("Name");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return name;
	}
	
	//Get the multiplier of the matching donator rank
	public Float getDonatorMultiplier(Integer ID)
	{
		Float multiplier = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE ID=?;");
			stmt.setInt(1, ID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				multiplier = results.getFloat("Multiplier");
				return multiplier;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return multiplier;
	}
	
	//Get the primary color of the donator rank
	public static ChatColor getDonatorColorPrimary(Integer donatorID)
	{
		ChatColor primary = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE ID=?;");
			stmt.setInt(1, donatorID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				primary = ChatColor.valueOf(results.getString("PrimaryColor"));
				return primary;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return primary;
	}
	
	//Get the secondary color of the donator rank
	public static ChatColor getDonatorColorSecondary(Integer donatorID)
	{
		ChatColor second = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE ID=?;");
			stmt.setInt(1, donatorID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				second = ChatColor.valueOf(results.getString("SecondColor"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return second;
	}
	
	public Integer getDonatorIDbyString(String donatorName)
	{
		Integer DonatorID = null;
		//Check if the rank variable matches any of the existing ranks
		switch (donatorName)
		{
		case "noble":  DonatorID = 1; break;
		case "royal": DonatorID = 2; break;
		case "dragon blood": DonatorID = 3; break;
		case "dragonblood": DonatorID = 3; break;
		case "default": DonatorID = 0; break;
		default: DonatorID = 0;
		}
		
		return DonatorID;
	}
	
	public ArrayList<Integer> getDonatorIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public Integer getPrice(Integer donatorID)
	{
		Integer price = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE ID=?;");
			stmt.setInt(1, donatorID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				price = results.getInt("Price");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return price;
	}
}
