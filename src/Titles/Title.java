package Titles;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import Genders.Gender;
import Handlers.ColorOptions;
import Main.Main;

public class Title 
{
	Gender gender = new Gender();
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	//Get the name of a title matching the gender
	public String getTitleName(Integer titleID, Integer genderID)
	{
		String titlename = null;
		String gender = "Male";
		
		if (genderID == 1)
		{
			gender = "Male";
		} else
		{
			gender = "Female";
		}
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles WHERE ID=?;");
			stmt.setInt(1, titleID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				titlename = results.getString(gender + "Name");
				return titlename;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return titlename;
	}
	
	//Get the salary matching the titleID
	public Integer getSalary(Integer titleID)
	{
		Integer salary = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles WHERE ID=?;");
			stmt.setInt(1, titleID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				salary = results.getInt("Salary");
				return salary;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return salary;
	}
	
	//Get the coin bonus matching the titleID
	public Integer getCoinBonus(Integer titleID)
	{
		Integer coinbonus = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles WHERE ID=?;");
			stmt.setInt(1, titleID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				coinbonus = results.getInt("CoinBonus");
				return coinbonus;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return coinbonus;
	}
	
	//Get the gem bonus matching the titleID
	public Integer getGemBonus(Integer titleID)
	{
		Integer gembonus = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles WHERE ID=?;");
			stmt.setInt(1, titleID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				gembonus = results.getInt("GemBonus");
				return gembonus;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return gembonus;
	}
	
	//Get the exp bonus matching the titleID
	public Integer getExpBonus(Integer titleID)
	{
		Integer expbonus = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles WHERE ID=?;");
			stmt.setInt(1, titleID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				expbonus = results.getInt("ExpBonus");
				return expbonus;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return expbonus;
	}
	
	//Get the minimum exp nessecairy matching the titleID
	public int getExpmin(Integer titleID)
	{
		Integer minexp = -1;
		if (titleID <= main.maxTitleID)
		{
			try
			{	
				PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles WHERE ID=?;");
				stmt.setInt(1, titleID);
				
				ResultSet results = stmt.executeQuery();
				if (results.next())
				{
					minexp = results.getInt("MinExp");
					return minexp;
				}
			} catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return minexp;
	}
	
	//Get the maximum exp nessecairy matching the titleID
	public Integer getExpmax(Integer titleID)
	{
		Integer maxexp = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles WHERE ID=?;");
			stmt.setInt(1, titleID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				maxexp = results.getInt("MaxExp");
				return maxexp;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return maxexp;
	}
	
	//Get the title ID through the title name from the database
	public Integer getTitleID(String name)
	{
		Integer id = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Titles WHERE MaleName=? OR FemaleName=? ");
			stmt.setString(1, name);
			stmt.setString(2, name);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("ID");
				return id;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Get the total amount of titles currently available in the game
	public Integer getTitleAmount()
	{
		Integer amount = 0;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles;");
			
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
	
	//Get a list of all title id's
	public ArrayList<Integer> getIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
			return list;
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public Integer getTitleIDbyExp(Integer experience)
	{
		Integer ID = null;
		
		try
		{	
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Titles WHERE MinExp <= ? AND MaxExp >= ?;");
			stmt.setInt(1, experience);
			stmt.setInt(2, experience);
			
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
	
	public void setExperienceBar(Player player, Integer experience, Integer newTitleID)
	{
		Integer maximumExp = this.getExpmax(newTitleID);
		
		double ExpBarPart = (double) experience/maximumExp;
		double ExpBarPercentage = ExpBarPart * 100;
		float ExpBar = ((float) ExpBarPercentage / 100);
		player.setExp(ExpBar);
		player.setLevel(newTitleID);
	}
	
	//If a user promotes to a higher title, this is the method to send the corresponding promotion message
	public List<String> PromotionMessage(Integer genderID, UUID uuid, int newTitleID)
	{	
		String titlename = this.getTitleName(newTitleID, genderID);
		String prefix = gender.getPrefix(genderID);
		Integer coinbonus = this.getCoinBonus(newTitleID);
		Integer gembonus = this.getGemBonus(newTitleID);
		Integer expbonus = this.getExpBonus(newTitleID);
		
		List<String> Titlemessage = Arrays.asList(new String[] {
				ColorOptions.statsbrackets,
				ColorOptions.promotionformat + prefix + ", you have been promoted to " + ColorOptions.promotionresults + titlename,
				ColorOptions.promotionformat + "You received " + ColorOptions.promotionresults + coinbonus + ColorOptions.coinStats + " Coins!",
				ColorOptions.promotionformat + "You received " + ColorOptions.promotionresults + gembonus + ColorOptions.gemStats + " Gems!",
				ColorOptions.promotionformat + "You received " + ColorOptions.promotionresults + expbonus + ColorOptions.coinStats + " Exp!",
				ColorOptions.statsbrackets});
		
		return Titlemessage;
	}
	
	//If a user demotes to a lower title, this is the method to send the corresponding demotion message
	public List<String> DemotionMessage(Integer genderID, UUID uuid, int newTitleID)
	{
		String newtitlename = this.getTitleName(newTitleID, genderID);
		String prefix = gender.getPrefix(genderID);
		List<String> Titlemessage = Arrays.asList(new String[] {
				ColorOptions.falsecommand + "=================================================",
				ColorOptions.falsecommand + prefix + ", you have been demoted to " + ChatColor.DARK_RED + newtitlename,
				ColorOptions.falsecommand + "================================================="});
		
		return Titlemessage;
	}
}
