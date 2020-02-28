package Users;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import Donator.Donator;
import Exceptions.UserNotFoundException;
import Genders.Gender;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.ExperienceChangeEvent;
import Handlers.SoundHandler;
import Houses.House;
import KillsDeaths.CombatCheck;
import Main.Main;
import Menu.Menu;
import Products.Product;
import Properties.Property;
import Scoreboards.ActionBar;
import Scoreboards.Scoreboard;
import Skills.SpecialSkill;
import Titles.Title;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;


public class offlineUser
{
	Title title = new Title();
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	//Current maximum skill level that can be achieved
	private Integer maxLevelSkill = 7;
	
	SpecialSkill spsk = new SpecialSkill();
	
	Gender gender = new Gender();
	
	
	
	//Constructor for the user class
	public offlineUser()
	{
	}
	
	//Checks if a user exists with the given username
	public boolean existUser(String username)
	{
		boolean exist = false;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Player Where Username=?;");	
			stmt.setString(1, username.toLowerCase());
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				exist = true;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return exist;
	}
	
	//Get the username of a user from the database
	public String getUserName(UUID uuid)
	{
		String username = null;
		
		try 
		{
			username = getUserData(uuid).getString("Username");
			return username;
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return username;
	}
	
	//Get the ID of a user from the database
	public Integer getUserID(UUID uuid)
	{
		Integer userID = null;
		
		try
		{
			userID = getUserData(uuid).getInt("ID");
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return userID;
	}
	
	//Get the uuid of a user by ID from the database
	public UUID getUUIDbyID(Integer userID)
	{
		UUID uuid = null;
		
		try
		{
			//prepare the query to retrieve uuid of a user by ID
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Player Where ID=?;");	
			stmt.setInt(1, userID);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				uuid = UUID.fromString(results.getString("UUID"));
				return uuid;
			} else
			{
				return uuid;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return uuid;
	}
	
	//Get the UUID of a user from the database
	public UUID getUUID(String username)
	{
		UUID uuid = null;
		try
		{
			//prepare the query to retrieve uuid of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Player Where Username=?;");	
			stmt.setString(1, username);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				uuid = UUID.fromString(results.getString("UUID"));
				return uuid;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return uuid;
	}
	
	//Get the level of a user from the Database
	public String getTitleName(UUID uuid)
	{
		String gender = this.getGenderName(uuid);
		try
		{
			PreparedStatement getID = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			getID.setString(1, uuid.toString());
			
			ResultSet results = getID.executeQuery();
			
			if (results.next())
			{
				PreparedStatement getTitle = main.getConnection().prepareStatement("Select * FROM Titles WHERE ID=?;");
				getTitle.setString(1, results.getString("TitleID"));
				
				ResultSet title = getTitle.executeQuery();
				title.next();
				
				return title.getString(gender + "Name");
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
	
	//Get the gender of a user from the Database
	public String getGenderName(UUID uuid)
	{
		String gender = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				gender = this.gender.getGenderName(results.getInt("GenderID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		
		return gender;
	}
	
	//Get the gender ID of a user from the database
	public Integer getGenderID(UUID uuid)
	{
		Integer id = null;

		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				id = results.getInt("GenderID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Get all information of a target user from the database
	public ResultSet getTargetUserData(UUID targetuuid)
	{
		//Cunstruct the resultset, if the query fails this method will return null
		ResultSet results = null;
		try 
		{
			//prepare the query to retrieve all information of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Player Where UUID=?;");	
			stmt.setString(1, targetuuid.toString());
			//Execute the query
			results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				return results;
			} else
			{
				return results;
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return results;
	}
	
	//Get all information of a user from the database
	public ResultSet getUserData(UUID uuid)
	{
		
		//Cunstruct the resultset, if the query fails this method will return null
		ResultSet results = null;
		try 
		{
			//prepare the query to retrieve all information of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From Player Where UUID=?;");	
			stmt.setString(1, uuid.toString());
			//Execute the query
			results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				return results;
			} else
			{
				return results;
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return results;
	}
	
	//Get the experience of a user from the Database
	public Integer getExperience(UUID uuid)
	{
		Integer exp = null;
		try 
		{
			exp = getUserData(uuid).getInt("Experience");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return exp;
	}
	
	//Update the experience of a user with a certain amount of coins
	public void saveExperience(UUID uuid, Integer amount, boolean expUpdate)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET Experience=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s experience to " + amount);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		if (expUpdate)
		{
	        Bukkit.getServer().getPluginManager().callEvent(new ExperienceChangeEvent(null, amount , Bukkit.getPlayer(this.getUserName(uuid))));
		}
	}
	
	//Add experience to a user
	public void addExperience(UUID uuid, Integer amount, boolean expUpdate)
	{
		//Get the balance before the addition of the amount specified in the method
		Integer OldExp = getExperience(uuid);
		//Add the old balance with the amount
		Integer NewExp = (OldExp + amount);
		
		//Save the new balance to the database
		saveExperience(uuid, NewExp, expUpdate);
	}
	
	//Remove experience from a user
	public void removeExperience(UUID uuid, Integer amount, boolean expUpdate)
	{
		//Get the experience before substraction the amount specified in the method
		Integer OldExp = getExperience(uuid);
		//Substract the amount of the old experience
		Integer NewExp = (OldExp - amount);
		
		if (NewExp < 0)
		{
			NewExp = 0;
		}
		//Save the new experience to the database
		saveExperience(uuid, NewExp, expUpdate);
	}
	
	//Get the coin-amount of a user from the Database
	public Integer getCoins(UUID uuid)
	{
		main.DBreconnect();
		Integer coins = null;
		try 
		{
			coins = getUserData(uuid).getInt("Coins");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return coins;
	}
	
	//Update the coin balance of a user with a certain amount of coins
	public void saveCoins(UUID uuid, Integer amount)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET Coins=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s coin balance to " + amount);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add coins to the balance of a user
	public void addCoins(UUID uuid, Integer amount)
	{
		//Get the balance before the addition of the amount specified in the method
		Integer OldBalance = getCoins(uuid);
		//Add the old balance with the amount
		Integer NewBalance = (OldBalance + amount);
		
		//Save the new balance to the database
		saveCoins(uuid, NewBalance);
	}
	
	//Remove coins from the balance of a user
	public void removeCoins(UUID uuid, Integer amount)
	{
		//Get the balance before substraction the amount specified in the method
		Integer OldBalance = getCoins(uuid);
		//Substract the amount of the old balance
		Integer NewBalance = (OldBalance - amount);
		
		if (NewBalance < 0)
		{
			NewBalance = 0;
		}
		//Save the new balance to the database
		saveCoins(uuid, NewBalance);
	}
	
	//Send coins from the balance of a user to another's balance
	public void sendCoins(UUID uuid, UUID targetUUID, Integer amount)
	{
		//Construct the target balance
		Integer TargetOldBalance = null;
		Integer SenderOldBalance = null;
		
		Integer TargetNewBalance = null;
		Integer SenderNewBalance = null;
		try 
		{
			//Get the nessecairy data from the database
			TargetOldBalance = getTargetUserData(targetUUID).getInt("Coins");
			SenderOldBalance = getUserData(uuid).getInt("Coins");
			
			//Substract and add the amount
			TargetNewBalance = (TargetOldBalance + amount);
			SenderNewBalance = (SenderOldBalance - amount);
			
			//Save the new balances to the database
			saveCoins(uuid, SenderNewBalance);
			addCoins(targetUUID, TargetNewBalance);
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player " + getUserName(uuid) + " paid " + getUserName(targetUUID) + " " + amount + ". Target's new balance is: " + TargetNewBalance);
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the gem balance of a user from the database
	public Integer getGems(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("Gems");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save the gem balance of a user to the database
	public void saveGems(UUID uuid, Integer amount)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET Gems=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s gem balance to " + amount);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add gems to the balance of a user
	public void addGems(UUID uuid, Integer amount)
	{
		//Get the balance before the addition of the amount specified in the method
		Integer OldBalance = getGems(uuid);
		//Add the old balance with the amount
		Integer NewBalance = (OldBalance + amount);
		
		//Save the new balance to the database
		saveGems(uuid, NewBalance);
	}
	
	//Remove gems from the balance of a user
	public void removeGems(UUID uuid, Integer amount)
	{
		//Get the balance before substraction the amount specified in the method
		Integer OldBalance = getGems(uuid);
		//Substract the amount of the old balance
		Integer NewBalance = (OldBalance - amount);
		
		if (NewBalance < 0)
		{
			NewBalance = 0;
		}
		//Save the new balance to the database
		saveGems(uuid, NewBalance);
	}
	
	//Send gems from the balance of a user to another's balance
	public void sendGems(UUID senderuuid, UUID targetUUID, Integer amount)
	{
		//Construct the target balance
		Integer TargetOldBalance = null;
		Integer SenderOldBalance = null;
		
		Integer TargetNewBalance = null;
		Integer SenderNewBalance = null;
		try 
		{
			//Get the nessecairy data from the database
			TargetOldBalance = getTargetUserData(targetUUID).getInt("Gems");
			SenderOldBalance = getUserData(senderuuid).getInt("Gems");
			
			//Substract and add the amount
			TargetNewBalance = (TargetOldBalance + amount);
			SenderNewBalance = (SenderOldBalance - amount);
			
			//Save the new balances to the database
			saveGems(senderuuid, SenderNewBalance);
			
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET Gems=? Where UUID=?;");
				stmt.setInt(1, TargetNewBalance);
				stmt.setString(2, targetUUID.toString());
				
				stmt.executeUpdate();
				//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
				Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getTargetUserData(targetUUID).getString("Username") + "'s gem balance to " + amount);
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the donator rank of a user
	public String getDonatorName(UUID uuid)
	{
		//Construct the Variables needed to get the name of the related DonatorID
		Integer DonatorID = null;
		String DonatorName = null;
		
		//Surround the get statement for donatorID with a try and catch for errors
		try 
		{
			DonatorID = getUserData(uuid).getInt("DonatorID");
			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE ID=?;");
			stmt.setInt(1, DonatorID);
			
			ResultSet results = stmt.executeQuery();
			//Check if a name has been found matching the DonatorID
			if (results.next())
			{
				DonatorName = results.getString("Name");
				return DonatorName;
			} else
			{
				return DonatorName;
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return DonatorName;
	}
	
	public Integer getDonatorID(UUID uuid)
	{
		Integer donatorID = null;
		
		try 
		{			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			//Check if a name has been found matching the DonatorID
			if (results.next())
			{
				donatorID = results.getInt("DonatorID");
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return donatorID;
	}
	
	//Set the donator rank of a user
	public void setDonatorRank(UUID uuid, Integer donatorID)
	{
		Scoreboard scoreboard = new Scoreboard();
		Donator donator = new Donator();
		//Construct the DonatorID to the default rank, which is 0
		Integer CurrentDonatorID = null;
		String donatorName = donator.getDonatorName(donatorID);
		try 
		{
			CurrentDonatorID = getUserData(uuid).getInt("DonatorID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Something went wrong when retrieving the donatorID, please note a developer");
		}
		
		if (CurrentDonatorID != donatorID)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET DonatorID=? Where UUID=?;");
				stmt.setInt(1, donatorID);
				stmt.setString(2, uuid.toString());
				
				stmt.executeUpdate();
				//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
				Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s DonatorRank to " + getDonatorName(uuid));
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Donator rank of " + ChatColor.GRAY  + getUserName(uuid) + ChatColor.GREEN + " has succesfully been updated to " + ChatColor.GRAY + donatorName); 
				Users.updateScoreBoard(null);
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		} else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "User already has that donator rank! Current: " + CurrentDonatorID + ", new: " + donatorID);
		}		
	}
	
	//Change gender to the opposite sex
	public void changeGender(UUID uuid)
	{
		String gender = null;
		
		try 
		{
			gender = this.getGenderName(uuid);
			
			if (gender.equalsIgnoreCase("male"))
			{
				saveGender(uuid, this.gender.getID("Female"));
			} else if (gender.equalsIgnoreCase("female"))
			{
				saveGender(uuid, this.gender.getID("Male"));
			} else
			{
				saveGender(uuid, this.gender.getID("Male"));
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	//Save the gender of a user to the database
	public void saveGender(UUID uuid, Integer genderID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET GenderID=? Where UUID=?;");
			stmt.setInt(1, genderID);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s gender to " + this.gender.getGenderName(genderID));
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Save the time a user has to wait before its next salary payout, time is saved in millis
	public void saveSalaryTime(UUID uuid)
	{
		Long current = System.currentTimeMillis();
		Long nextpayment = current + 3600000L;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET SalaryTime=? Where UUID=?;");
			stmt.setLong(1, nextpayment);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated salary time(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s salaryTime to the next hour");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the salary time of a user from the database
	public Long getSalaryTime(UUID uuid)
	{
		Long time = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				time = results.getLong("SalaryTime");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return time;
	}
	
	//Save the time a user has to wait before its next income payout, time is saved in millis
	public void saveIncomeTime(UUID uuid)
	{
		Long current = System.currentTimeMillis();
		Long nextpayment = (current + 43200000L);
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET IncomeTime=? Where UUID=?;");
			stmt.setLong(1, nextpayment);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated income time(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s incomeTime to the next 12 hours");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	//Get the income time of a user from the database
	public Long getIncomeTime(UUID uuid)
	{
		Long time = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				time = results.getLong("IncomeTime");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return time;
	}
	
	//Save the time a user has to wait before its next rentpayment, time is saved in millis
	public void saveRentTime(UUID uuid)
	{
		Long current = System.currentTimeMillis();
		Long nextpayment = current + 3600000L;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET RentTime=? Where UUID=?;");
			stmt.setLong(1, nextpayment);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated salary time(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s rentTime to the next hour");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Save the time a user has to wait before its next rentpayment, time is saved in millis
	public void removeRentTime(UUID uuid)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET RentTime=null Where UUID=?;");
			stmt.setString(1, uuid.toString());
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated salary time(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Removed " + getUserName(uuid) + "'s rentTime");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the rent time of a user from the database
	public Long getRentTime(UUID uuid)
	{
		Long time = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				time = results.getLong("RentTime");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return time;
	}
	
	//Get the income of a user from the database
	public Integer getIncome(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("Income");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//save the income of a user to the database
	public void saveIncome(UUID uuid, Integer amount)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET Income=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated income(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s income to " + amount);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount to the income of a user
	public void addIncome(UUID uuid, Integer amount)
	{
		//Get the old income of the user from the database
		Integer oldIncome = getIncome(uuid);
		Integer newIncome = oldIncome + amount;
		
		saveIncome(uuid, newIncome);
		
	}
	
	//Remove an amount of the income of a user
	public void removeIncome(UUID uuid, Integer amount)
	{
		//Get the old income of the user from the database
		Integer oldIncome = getIncome(uuid);
		Integer newIncome = oldIncome - amount;
		
		if (newIncome < 0)
		{
			newIncome = 0;
		}
		saveIncome(uuid, newIncome);
	}
	
	//Get the amount of votes of a user
	public Integer getVotes(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("Votes");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of votes of a user to the database
	public void saveVotes(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET Votes=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of votes to a user's votes
	public void addVotes(UUID uuid, Integer amount)
	{
		//Get the old amount of votes of the user from the database
		Integer oldVotes = getVotes(uuid);
		Integer newVotes = oldVotes + amount;
		
		saveVotes(uuid, newVotes);
		
	}
	
	//Remove an amount of votes from a user's votes
	public void removeVotes(UUID uuid, Integer amount)
	{
		//Get the old amount of votes of the user from the database
		Integer oldVotes = getVotes(uuid);
		Integer newVotes = oldVotes - amount;
		
		if (newVotes < 0)
		{
			newVotes = 0;
		}
		saveVotes(uuid, newVotes);
	}	
	
	//Get the amount of kills of a user
	public Integer getKills(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("Kills");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of kills of a user to the database
	public void saveKills(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET Kills=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of kills to a user's kills
	public void addKills(UUID uuid, Integer amount)
	{
		//Get the old amount of kills of the user from the database
		Integer oldKills = getKills(uuid);
		Integer newKills = oldKills + amount;
		
		saveKills(uuid, newKills);
		
	}
	
	//Remove an amount of kills from a user's kills
	public void removeKills(UUID uuid, Integer amount)
	{
		//Get the old amount of kills of the user from the database
		Integer oldKills = getKills(uuid);
		Integer newKills = oldKills - amount;
		
		if (newKills < 0)
		{
			newKills = 0;
		}
		saveKills(uuid, newKills);
	}
	
	//Get the amount of deaths of a user
	public Integer getDeaths(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("Deaths");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of deaths of a user to the database
	public void saveDeaths(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET Deaths=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of deaths to a user
	public void addDeaths(UUID uuid, Integer amount)
	{
		//Get the old amount of deaths of the user from the database
		Integer oldDeaths = getDeaths(uuid);
		Integer newDeaths = oldDeaths + amount;
		
		saveDeaths(uuid, newDeaths);
		
	}
	
	//Remove an amount of deaths from a user
	public void removeDeaths(UUID uuid, Integer amount)
	{
		//Get the old amount of deaths of the user from the database
		Integer oldDeaths = getDeaths(uuid);
		Integer newDeaths = oldDeaths - amount;
		
		if (newDeaths < 0)
		{
			newDeaths = 0;
		}
		saveDeaths(uuid, newDeaths);
	}
	
	//Get the amount of deaths of a user
	public Integer getTitleID(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("TitleID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save a TitleID of a user to the database
	public void saveTitleID(UUID uuid, Integer TitleID)
	{
		if (TitleID > 18)
		{
			TitleID = 18;
		}
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET TitleID=? Where UUID=?;");
			stmt.setInt(1, TitleID);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of titles to a user's TitleID
	public void addTitleID(UUID uuid, Integer TitleIDAmount)
	{
		//Get the old title ID of the user from the database
		Integer oldTitleID = getTitleID(uuid);
		Integer newTitleID = oldTitleID + TitleIDAmount;
		
		if (newTitleID > 18)
		{
			newTitleID = 18;
		}
		saveTitleID(uuid, newTitleID);
		
	}
	
	//Remove an amount of titles from a user's TitleID
	public void removeTitleID(UUID uuid, Integer TitleIDAmount)
	{
		//Get the old title ID of the user from the database
		Integer oldTitleID = getTitleID(uuid);
		Integer newTitleID = oldTitleID - TitleIDAmount;
		
		if (newTitleID < 0)
		{
			newTitleID = 0;
		}
		saveTitleID(uuid, newTitleID);
	}
	
	//Get the amount of skillpoints of a user
	public Integer getSkillPoints(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("SkillPoints");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of skillpoints of a user to the database
	public void saveSkillPoints(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET SkillPoints=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of skillpoints to a user
	public void addSkillPoints(UUID uuid, Integer amount)
	{
		//Get the old amount of skillpoints of the user from the database
		Integer oldBalance = getSkillPoints(uuid);
		Integer newBalance = oldBalance + amount;
		
		saveSkillPoints(uuid, newBalance);
		
	}
	
	//Remove an amount of skillpoints from a user
	public void removeSkillPoints(UUID uuid, Integer amount)
	{
		//Get the old amount of skillpoints of the user from the database
		Integer oldBalance = getSkillPoints(uuid);
		Integer newBalance = oldBalance - amount;
		
		if (newBalance < 0)
		{
			newBalance = 0;
		}
		saveSkillPoints(uuid, newBalance);
	}
	
	//Get the amount of special skillpoints of a user
	public Integer getSpecialSkillPoints(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("SpecialSkillPoints");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of special skillpoints of a user to the database
	public void saveSpecialSkillPoints(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET SpecialSkillPoints=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of special skillpoints to a user
	public void addSpecialSkillPoints(UUID uuid, Integer amount)
	{
		//Get the old amount of special skillpoints of the user from the database
		Integer oldBalance = getSpecialSkillPoints(uuid);
		Integer newBalance = oldBalance + amount;
		
		saveSpecialSkillPoints(uuid, newBalance);
		
	}
	
	//Remove an amount of special skillpoints from a user
	public void removeSpecialSkillPoints(UUID uuid, Integer amount)
	{
		//Get the old amount of special skillpoints of the user from the database
		Integer oldBalance = getSpecialSkillPoints(uuid);
		Integer newBalance = oldBalance - amount;
		
		if (newBalance < 0)
		{
			newBalance = 0;
		}
		saveSpecialSkillPoints(uuid, newBalance);
	}
	
	//Get a hashmap with all skills and their levels of a user
	public HashMap<String, Integer> getSkillLevelList(UUID uuid)
	{
		HashMap<String, Integer> list = new HashMap<String, Integer>();
		
		list.put("Strength", this.getStrengthLevelID(uuid));
		list.put("Speed", this.getSpeedLevelID(uuid));
		list.put("Health", this.getHealthLevelID(uuid));
		list.put("AttackSpeed", this.getAttackSpeedLevelID(uuid));
		list.put("Defense", this.getDefenseLevelID(uuid));
		
		return list;
	}
	
	public void addSkillLevel(UUID uuid, String SkillType, Integer amount)
	{
		if (SkillType.equalsIgnoreCase("Strength"))
		{
			this.addStrengthLevelID(uuid, amount);
		} else
		if (SkillType.equalsIgnoreCase("Speed"))
		{
			this.addSpeedLevelID(uuid, amount);
		} else
		if (SkillType.equalsIgnoreCase("Health"))
		{
			this.addHealthLevelID(uuid, amount);
		} else
		if (SkillType.equalsIgnoreCase("AttackSpeed"))
		{
			this.addAttackSpeedLevelID(uuid, amount);
		} else
		if (SkillType.equalsIgnoreCase("Defense"))
		{
			this.addDefenseLevelID(uuid, amount);
		}
	}
	
	//Remove a skill-level(universal method)
	public void removeSkillLevel(UUID uuid, String Skill, Integer amount)
	{
		if (Skill.equalsIgnoreCase("Strength"))
		{
			this.removeStrengthLevelID(uuid, amount);
		} else
		if (Skill.equalsIgnoreCase("Speed"))
		{
			this.removeSpeedLevelID(uuid, amount);
		} else
		if (Skill.equalsIgnoreCase("Health"))
		{
			this.removeHealthLevelID(uuid, amount);
		} else
		if (Skill.equalsIgnoreCase("AttackSpeed"))
		{
			this.removeAttackSpeedLevelID(uuid, amount);
		} else
		if (Skill.equalsIgnoreCase("Defense"))
		{
			this.removeDefenseLevelID(uuid, amount);
		}
	}
	
	//Get the strength level of a user
	public Integer getStrengthLevelID(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("StrengthID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of special skillpoints of a user to the database
	public void saveStrengthLevelID(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET StrengthID=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of strength levels to a user
	public void addStrengthLevelID(UUID uuid, Integer amount)
	{
		//Get the old strength level of the user from the database
		Integer oldLevel = getStrengthLevelID(uuid);
		Integer newLevel = oldLevel + amount;
		
		//Check if the level isn't higher than the current maximum, this is specified at the top of the class
		if (newLevel > maxLevelSkill)
		{
			newLevel = maxLevelSkill;
		}
		
		saveStrengthLevelID(uuid, newLevel);
		
	}
	
	//Remove an amount of strength levels from a user
	public void removeStrengthLevelID(UUID uuid, Integer amount)
	{
		//Get the old strength level of the user from the database
		Integer oldLevel = getStrengthLevelID(uuid);
		Integer newLevel = oldLevel - amount;
		
		if (newLevel < 0)
		{
			newLevel = 0;
		}
		saveStrengthLevelID(uuid, newLevel);
	}
	
	//Get the speed level of a user
	public Integer getSpeedLevelID(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("SpeedID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an strength level ID of a user to the database
	public void saveSpeedLevelID(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET SpeedID=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of speed levels to a user
	public void addSpeedLevelID(UUID uuid, Integer amount)
	{
		//Get the old speed level of the user from the database
		Integer oldLevel = getSpeedLevelID(uuid);
		Integer newLevel = oldLevel + amount;
		
		//Check if the level isn't higher than the current maximum, this is specified at the top of the class
		if (newLevel > maxLevelSkill)
		{
			newLevel = maxLevelSkill;
		}
		
		saveSpeedLevelID(uuid, newLevel);
		
	}
	
	//Remove an amount of speed levels from a user
	public void removeSpeedLevelID(UUID uuid, Integer amount)
	{
		//Get the old speed level of the user from the database
		Integer oldLevel = getSpeedLevelID(uuid);
		Integer newLevel = oldLevel - amount;
		
		if (newLevel < 0)
		{
			newLevel = 0;
		}
		saveSpeedLevelID(uuid, newLevel);
	}
	
	//Get the health level of a user
	public Integer getHealthLevelID(UUID uuid)
	{
		Integer id = null;
		try 
		{
			id = getUserData(uuid).getInt("HealthID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Save an health level ID of a user to the database
	public void saveHealthLevelID(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET HealthID=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of health levels to a user
	public void addHealthLevelID(UUID uuid, Integer amount)
	{
		//Get the old health level of the user from the database
		Integer oldLevel = getHealthLevelID(uuid);
		Integer newLevel = oldLevel + amount;
		
		//Check if the level isn't higher than the current maximum, this is specified at the top of the class
		if (newLevel > maxLevelSkill)
		{
			newLevel = maxLevelSkill;
		}
		
		saveHealthLevelID(uuid, newLevel);
		
	}
	
	//Remove an amount of health levels from a user
	public void removeHealthLevelID(UUID uuid, Integer amount)
	{
		//Get the old health level of the user from the database
		Integer oldLevel = getHealthLevelID(uuid);
		Integer newLevel = oldLevel - amount;
		
		if (newLevel < 0)
		{
			newLevel = 0;
		}
		saveHealthLevelID(uuid, newLevel);
	}
	
	//Get the attack speed level of a user
	public Integer getAttackSpeedLevelID(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("AttackSpeedID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an attack speed level ID of a user to the database
	public void saveAttackSpeedLevelID(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET AttackSpeedID=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of attack speed levels to a user
	public void addAttackSpeedLevelID(UUID uuid, Integer amount)
	{
		//Get the old attack speed level of the user from the database
		Integer oldLevel = getAttackSpeedLevelID(uuid);
		Integer newLevel = oldLevel + amount;
		
		//Check if the level isn't higher than the current maximum, this is specified at the top of the class
		if (newLevel > maxLevelSkill)
		{
			newLevel = maxLevelSkill;
		}
		
		saveAttackSpeedLevelID(uuid, newLevel);
		
	}
	
	//Remove an amount of attack speed levels from a user
	public void removeAttackSpeedLevelID(UUID uuid, Integer amount)
	{
		//Get the old attack speed level of the user from the database
		Integer oldLevel = getAttackSpeedLevelID(uuid);
		Integer newLevel = oldLevel - amount;
		
		if (newLevel < 0)
		{
			newLevel = 0;
		}
		saveAttackSpeedLevelID(uuid, newLevel);
	}
	
	//Get the defense level of a user
	public Integer getDefenseLevelID(UUID uuid)
	{
		Integer level = null;
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				level = results.getInt("DefenseID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return level;
	}
	
	//Save an defense level ID of a user to the database
	public void saveDefenseLevelID(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET DefenseID=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of defense levels to a user
	public void addDefenseLevelID(UUID uuid, Integer amount)
	{
		//Get the old defense level of the user from the database
		Integer oldLevel = getDefenseLevelID(uuid);
		Integer newLevel = oldLevel + amount;
		
		//Check if the level isn't higher than the current maximum, this is specified at the top of the class
		if (newLevel > maxLevelSkill)
		{
			newLevel = maxLevelSkill;
		}
		
		saveDefenseLevelID(uuid, newLevel);
		
	}
	
	//Remove an amount of defense levels from a user
	public void removeDefenseLevelID(UUID uuid, Integer amount)
	{
		//Get the old defense level of the user from the database
		Integer oldLevel = getDefenseLevelID(uuid);
		Integer newLevel = oldLevel - amount;
		
		if (newLevel < 0)
		{
			newLevel = 0;
		}
		saveDefenseLevelID(uuid, newLevel);
	}
	
	//Get the maximum of houses a user owns
	public Integer getHouseMaximum(UUID uuid)
	{
		Integer maximum = null;
		try 
		{
			maximum = getUserData(uuid).getInt("HouseMaximum");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return maximum;
	}
	
	//Save the max owned houses of a user to the database
	public void saveHouseMaximum(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET HouseMaximum=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of maximum owned houses of a user
	public void addHouseMaximum(UUID uuid, Integer amount)
	{
		//Get the old maximum of owned houses of the user from the database
		Integer oldMax = getHouseMaximum(uuid);
		Integer newMax = oldMax + amount;
		
		saveHouseMaximum(uuid, newMax);
		
	}
	
	//Remove an amount of maximum owned houses from a user
	public void removeHouseMaximum(UUID uuid, Integer amount)
	{
		//Get the old maximum of owned houses of the user from the database
		Integer oldMax = getHouseMaximum(uuid);
		Integer newMax = oldMax - amount;
		
		if (newMax < 0)
		{
			newMax = 0;
		}
		saveHouseMaximum(uuid, newMax);
	}
	
	//Get the amount of houses a user owns
	public Integer getHouseAmount(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("HouseAmount");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of owned houses of a user to the database
	public void saveHouseAmount(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET HouseAmount=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of owned houses of a user
	public void addHouseAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of owned houses of the user from the database
		Integer oldAmount = getHouseAmount(uuid);
		Integer newAmount = oldAmount + amount;
		
		saveHouseAmount(uuid, newAmount);
		
	}
	
	//Remove an amount of owned houses from a user
	public void removeHouseAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of owned houses of the user from the database
		Integer oldAmount = getHouseAmount(uuid);
		Integer newAmount = oldAmount - amount;
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		saveHouseAmount(uuid, newAmount);
	}
	
	//Get the maximum of houses a user owns
	public Integer getPropertyMaximum(UUID uuid)
	{
		Integer maximum = null;
		try 
		{
			maximum = getUserData(uuid).getInt("PropertyMaximum");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return maximum;
	}
	
	//Save the max owned houses of a user to the database
	public void savePropertyMaximum(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET PropertyMaximum=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of maximum owned houses of a user
	public void addPropertyMaximum(UUID uuid, Integer amount)
	{
		//Get the old maximum of owned houses of the user from the database
		Integer oldMax = getPropertyMaximum(uuid);
		Integer newMax = oldMax + amount;
		
		savePropertyMaximum(uuid, newMax);
		
	}
	
	//Remove an amount of maximum owned houses from a user
	public void removePropertyMaximum(UUID uuid, Integer amount)
	{
		//Get the old maximum of owned houses of the user from the database
		Integer oldMax = getPropertyMaximum(uuid);
		Integer newMax = oldMax - amount;
		
		if (newMax < 0)
		{
			newMax = 0;
		}
		savePropertyMaximum(uuid, newMax);
	}
	
	//Get the amount of properties a user owns
	public Integer getPropertyAmount(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("PropertyAmount");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of owned properties of a user to the database
	public void savePropertyAmount(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET PropertyAmount=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of owned properties of a user
	public void addPropertyAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of owned properties of the user from the database
		Integer oldAmount = getPropertyAmount(uuid);
		Integer newAmount = oldAmount + amount;
		
		savePropertyAmount(uuid, newAmount);
		
	}
	
	//Remove an amount of owned properties from a user
	public void removePropertyAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of owned properties of the user from the database
		Integer oldAmount = getPropertyAmount(uuid);
		Integer newAmount = oldAmount - amount;
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		savePropertyAmount(uuid, newAmount);
	}
	
	//Get the maximum of houses a user owns
	public Integer getRoomMaximum(UUID uuid)
	{
		Integer maximum = null;
		try 
		{
			maximum = getUserData(uuid).getInt("RoomMaximum");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return maximum;
	}
	
	//Save the max owned houses of a user to the database
	public void saveRoomMaximum(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET RoomMaximum=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of maximum owned houses of a user
	public void addRoomMaximum(UUID uuid, Integer amount)
	{
		//Get the old maximum of owned houses of the user from the database
		Integer oldMax = getRoomMaximum(uuid);
		Integer newMax = oldMax + amount;
		
		saveRoomMaximum(uuid, newMax);
		
	}
	
	//Remove an amount of maximum owned houses from a user
	public void removeRoomMaximum(UUID uuid, Integer amount)
	{
		//Get the old maximum of owned houses of the user from the database
		Integer oldMax = getRoomMaximum(uuid);
		Integer newMax = oldMax - amount;
		
		if (newMax < 0)
		{
			newMax = 0;
		}
		saveRoomMaximum(uuid, newMax);
	}
	
	public Integer getRoomAmount(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("RoomAmount");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of owned rooms of a user to the database
	public void saveRoomAmount(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET RoomAmount=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of owned rooms of a user
	public void addRoomAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of owned rooms of the user from the database
		Integer oldAmount = getRoomAmount(uuid);
		Integer newAmount = oldAmount + amount;
		
		saveRoomAmount(uuid, newAmount);
		
	}
	
	//Remove an amount of owned rooms from a user
	public void removeRoomAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of owned rooms of the user from the database
		Integer oldAmount = getRoomAmount(uuid);
		Integer newAmount = oldAmount - amount;
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		saveRoomAmount(uuid, newAmount);
	}
	
	//Get the maximum of houses a user owns
	public Integer getKeepMaximum(UUID uuid)
	{
		Integer maximum = null;
		try 
		{
			maximum = getUserData(uuid).getInt("KeepMaximum");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return maximum;
	}
	
	//Save the max owned houses of a user to the database
	public void saveKeepMaximum(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET KeepMaximum=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of maximum owned houses of a user
	public void addKeepMaximum(UUID uuid, Integer amount)
	{
		//Get the old maximum of owned houses of the user from the database
		Integer oldMax = getKeepMaximum(uuid);
		Integer newMax = oldMax + amount;
		
		saveKeepMaximum(uuid, newMax);
		
	}
	
	//Remove an amount of maximum owned houses from a user
	public void removeKeepMaximum(UUID uuid, Integer amount)
	{
		//Get the old maximum of owned houses of the user from the database
		Integer oldMax = getKeepMaximum(uuid);
		Integer newMax = oldMax - amount;
		
		if (newMax < 0)
		{
			newMax = 0;
		}
		saveKeepMaximum(uuid, newMax);
	}
	
	//Get the amount of keeps a user owns
	public Integer getKeepAmount(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("KeepAmount");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of owned keeps of a user to the database
	public void saveKeepAmount(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET KeepAmount=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of owned keeps of a user
	public void addKeepAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of owned keeps of the user from the database
		Integer oldAmount = getKeepAmount(uuid);
		Integer newAmount = oldAmount + amount;
		
		saveKeepAmount(uuid, newAmount);
		
	}
	
	//Remove an amount of owned keeps from a user
	public void removeKeepAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of owned keeps of the user from the database
		Integer oldAmount = getKeepAmount(uuid);
		Integer newAmount = oldAmount - amount;
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		saveKeepAmount(uuid, newAmount);
	}
	
	//Get the amount of keeps a user owns
	public Integer getFriendAmount(UUID uuid)
	{
		try 
		{
			return getUserData(uuid).getInt("FriendAmount");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Save an amount of friends of a user to the database
	public void saveFriendAmount(UUID uuid, Integer amount)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET FriendAmount=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add an amount of friends to a user's friend amount
	public void addFriendAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of friends of the user from the database
		Integer oldAmount = getFriendAmount(uuid);
		Integer newAmount = oldAmount + amount;
		
		saveFriendAmount(uuid, newAmount);
		
	}
	
	//Remove an amount of friends from a user
	public void removeFriendAmount(UUID uuid, Integer amount)
	{
		//Get the old amount of friends of the user from the database
		Integer oldAmount = getFriendAmount(uuid);
		Integer newAmount = oldAmount - amount;
		
		if (newAmount < 0)
		{
			newAmount = 0;
		}
		saveFriendAmount(uuid, newAmount);
	}
	
	//Get the special skill ID of a user owns
	public Integer getSpecialSkillID(UUID uuid)
	{
		Integer SkillID = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				SkillID = results.getInt("SpecialSkillID");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return SkillID;
	}
	
	//Save a special skill ID of a user to the database
	public void saveSpecialSkillID(UUID uuid, Integer specialskillID)
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET SpecialSkillID=? Where UUID=?;");
			stmt.setInt(1, specialskillID);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add a special skill ID to a user's friend amount
	public void addSpecialSKillID(UUID uuid, Integer amount)
	{
		//Get the old special skill ID of the user from the database
		Integer oldID = getFriendAmount(uuid);
		Integer newID = oldID + amount;
		
		if (newID > 8)
		{
			newID = 8;
		}
		
		saveSpecialSkillID(uuid, newID);
		
	}
	
	//Remove a special skill ID from a user
	public void removeSpecialSkillID(UUID uuid, Integer amount)
	{
		//Get the old special skill ID of the user from the database
		Integer oldID = getSpecialSkillID(uuid);
		Integer newID = oldID - amount;
		
		if (newID < 0)
		{
			newID = 0;
		}
		saveSpecialSkillID(uuid, newID);
	}
	
	//Get the special skill from the Database
	public String getSpecialSkillName(UUID uuid)
	{
		String SkillName = null;
		Integer SkillID = getSpecialSkillID(uuid);
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM SpecialSkill WHERE ID=?;");
			stmt.setInt(1, SkillID);
			
			ResultSet results = stmt.executeQuery();
			
			if (results.next())
			{
				SkillName = results.getString("Name");
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return SkillName;
		
	}
	
	//Set the special skill of a user
	public void setSpecialSkillName(UUID uuid, String specialSkill)
	{
		//Construct the SkillID to the default rank, which is 0
		Integer SkillID = 0;
		String skill = null;
		Integer CurrentSpecialSkillID = null;
		try 
		{
			CurrentSpecialSkillID = getUserData(uuid).getInt("SpecialSkillID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		//Check if the rank variable matches any of the existing ranks
		switch (specialSkill)
		{
		case "ninja":  skill = "Ninja";
		case "shotbow": skill = "Shotbow";
		case "pickpocket": skill = "Pickpocket";
		case "forger": skill = "Forger";
		case "assassin": skill = "Assassin";
		case "juggernaut": skill = "Juggernaut";
		case "avenger": skill = "Avenger";
		case "none": skill = "None";
		case "default": skill = "None";
		default: skill = "None";
		}
		
		//Get the id of the skill given in the method arguments, a.k.a. the skill that is preferred
		SkillID = spsk.getSpecialSkillID(skill);
		
		if (CurrentSpecialSkillID != SkillID)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET SpecialSkillID=? Where UUID=?;");
				stmt.setInt(1, SkillID);
				stmt.setString(2, uuid.toString());
				
				stmt.executeUpdate();
				//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
				Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s SpecialSKill to " + skill);
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Get the friend requests of a user
	public ArrayList<UUID> getFriendRequestList(UUID uuid)
	{
		ArrayList<Integer> requestID = new ArrayList<Integer>();
		ArrayList<UUID> requests = new ArrayList<UUID>();
		Integer userID = null;
		
		try 
		{
			userID = getUserData(uuid).getInt("ID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		try
		{
			PreparedStatement getRequests = main.getConnection().prepareStatement("Select * FROM FriendRequest WHERE PlayerTargetID=?;");
			getRequests.setInt(1, userID);
			
			ResultSet results = getRequests.executeQuery();
			while (results.next())
			{
				requestID.add(results.getInt("PlayerSenderID"));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		for (Integer ID : requestID)
		{
			requests.add(this.getUUIDbyID(ID));
		}
		return requests;
	}
	
	//Save a friend request to the database
	public void saveFriendRequest(UUID senderUUID, UUID targetUUID)
	{
		Integer senderID = null;
		Integer targetID = null;
		
		try 
		{
			senderID = getUserData(senderUUID).getInt("ID");
			targetID = getUserData(targetUUID).getInt("ID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		//Save the request
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO FriendRequest(PlayerSenderID, PlayerTargetID) VALUES (?, ?);");
			stmt.setInt(1, senderID);
			stmt.setInt(2, targetID);
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "User: " + getUserName(senderUUID) + " sent a friend-request to " + getUserName(targetUUID));
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	//Remove a friend request to the database
	public void deleteFriendRequest(UUID senderUUID, UUID targetUUID)
	{
		Integer senderID = null;
		Integer targetID = null;
		
		try 
		{
			senderID = getUserData(senderUUID).getInt("ID");
			targetID = getUserData(targetUUID).getInt("ID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		//Save the request
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM FriendRequest WHERE PlayerSenderID=? AND PlayerTargetID=?;");
			stmt.setInt(1, senderID);
			stmt.setInt(2, targetID);
			
			stmt.executeUpdate();
			
			PreparedStatement stmt2 = main.getConnection().prepareStatement("DELETE FROM FriendRequest WHERE PlayerSenderID=? AND PlayerTargetID=?;");
			stmt2.setInt(2, senderID);
			stmt2.setInt(1, targetID);
			
			stmt2.executeUpdate();
			//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "A friend-request between " + getUserName(senderUUID) + " and " + getUserName(targetUUID) + " has been removed");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	//Reply to a friend request with a positive(accept) response or a negative(deny) response
	public void ReplyFriendRequest(UUID uuid, String targetUsername, boolean reply)
	{
		UUID targetUUID = getUUID(targetUsername);
		
		//Check if the request is accepted or denied
		if (isFriends(uuid, targetUUID) == false)
		{
			if (reply == true)
			{
				saveFriends(uuid, targetUUID);
			} else
			{
				deleteFriendRequest(uuid, targetUUID);
			}
		}
	}
	
	//Set a friendship between two users in the database
	public void saveFriends(UUID senderUUID, UUID targetUUID)
	{
		Integer SenderID = null;
		Integer TargetID = null;
		
		try 
		{
			SenderID = getUserData(senderUUID).getInt("ID");
			TargetID = getUserData(targetUUID).getInt("ID");
		} catch (SQLException e1) 
		{
			e1.printStackTrace();
		}
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Friends(Player1ID, Player2ID) VALUES (?, ?);");
			stmt.setInt(1, SenderID);
			stmt.setInt(2, TargetID);
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "User " + getUserName(senderUUID) + " is now friends with " + getUserName(targetUUID));
			deleteFriendRequest(senderUUID, targetUUID);
			addFriendAmount(senderUUID, 1);
			addFriendAmount(targetUUID, 1);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Remove a friend request to the database
	public void deleteFriend(UUID senderUUID, UUID targetUUID)
	{
		Integer senderID = null;
		Integer targetID = null;
		
		try 
		{
			senderID = getUserData(senderUUID).getInt("ID");
			targetID = getUserData(targetUUID).getInt("ID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		//Save the request
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Friends WHERE Player1ID=? AND Player2ID=? OR Player1ID=? AND Player2ID=?;");
			stmt.setInt(1, senderID);
			stmt.setInt(2, targetID);
			stmt.setInt(3, targetID);
			stmt.setInt(4, senderID);
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "User " + getUserName(senderUUID) + " is no longer friends with " + getUserName(targetUUID));
			removeFriendAmount(senderUUID, 1);
			removeFriendAmount(targetUUID, 1);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	//Get the friends of a user
	public ArrayList<UUID> getFriends(UUID uuid)
	{
		ArrayList<UUID> friends = new ArrayList<UUID>();
		Integer ID = null;
		
		//Get the ID of the user
		try 
		{
			ID = getUserData(uuid).getInt("ID");
		} catch (SQLException e1) 
		{
			e1.printStackTrace();
		}
		
		try 
		{
			//Check two rows of the table for the friends that have a match with the user's ID
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Friends WHERE Player1ID=? OR Player2ID=?;");
			stmt.setInt(1, ID);
			stmt.setInt(2, ID);
			
			ResultSet results = stmt.executeQuery();

			//Add all results to the arraylist
			while (results.next())
			{
				if (results.getInt("Player1ID") != ID)
				{
					friends.add(getUUIDbyID(results.getInt("Player1ID")));
				} else
				if (results.getInt("Player2ID") != ID)
				{
					friends.add(getUUIDbyID(results.getInt("Player2ID")));
				}
			}
			
			return friends;
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return friends;
	}
	
	public ArrayList<String> getFriendNames(UUID uuid)
	{
		ArrayList<UUID> list = this.getFriends(uuid);
		ArrayList<String> friends = new ArrayList<String>();
		
		for (UUID tu : list)
		{
			friends.add(this.getUserName(tu) + ", ");
		}
		
		return friends;
	}
	
	//Check if a user is already friends with another user
	public boolean isFriends(UUID senderUUID, UUID targetUUID)
	{
		boolean isfriends = false;
		ArrayList<UUID> friends = getFriends(senderUUID);
		
		for (UUID targetuuid : friends)
		{
			if (targetuuid.equals(targetUUID))
			{
				isfriends = true;
				break;
			}
		}
		
		return isfriends;
	}
	
	//Check if a player is lucky or not
	public boolean isLucky(UUID uuid)
	{
		Integer PlayerID = null;
		boolean islucky = false;
		
		try 
		{
			PlayerID = getUserData(uuid).getInt("ID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
			//Check two rows of the table for the friends that have a match with the user's ID
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Lucky WHERE PlayerID=?;");
			stmt.setInt(1, PlayerID);
			
			ResultSet results = stmt.executeQuery();

			//Add all results to the arraylist
			if (results.next())
			{
				islucky = true;
				return islucky;
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return islucky;
	}
	
	//Get all players who are lucky
	public ArrayList<String> getLuckyList()
	{
		ArrayList<Integer> luckyPlayerID = new ArrayList<Integer>();
		ArrayList<String> luckyPlayerName = new ArrayList<String>();
		
		try 
		{
			//Check two rows of the table for the friends that have a match with the user's ID
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Lucky;");
			
			ResultSet results = stmt.executeQuery();

			//Add all results to the arraylist
			while (results.next())
			{
				luckyPlayerID.add(results.getInt("PlayerID"));
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		for (Integer ID : luckyPlayerID)
		{
			String username = getUserName(getUUIDbyID(ID));
			luckyPlayerName.add(username);
		}
		
		return luckyPlayerName;
	}
	
	//Get the time a player's lucky time expires(returns the time in Integer, millis)
	public Integer getLuckyTimeExpire(UUID uuid)
	{
		Integer playerID = null;
		Integer timeExpire = null;
		
		try 
		{
			playerID = getUserData(uuid).getInt("ID");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		try 
		{
			//Check two rows of the table for the friends that have a match with the user's ID
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Lucky WHERE PlayerID=?;");
			stmt.setInt(1, playerID);
			
			ResultSet results = stmt.executeQuery();

			//Add all results to the arraylist
			if (results.next())
			{
				timeExpire = results.getInt("Expire");
				return timeExpire;
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return timeExpire;
	}
	
	//Get the spawnpoint-ID of a user
	public Integer getSpawnpointID(UUID uuid)
	{
		Integer id = null;
		
		try 
		{
			//Check two rows of the table for the friends that have a match with the user's ID
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();

			//Add all results to the arraylist
			if (results.next())
			{
				id = results.getInt("SpawnpointID");
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Save the spawnpoint-ID of a user
	public void saveSpawnpoint(UUID uuid, Integer spawnpointID)
	{
		Integer id = this.getUserID(uuid);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET SpawnpointID=? Where ID=?;");
			stmt.setInt(1, spawnpointID);
			stmt.setInt(2, id);
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s spawnpoint to " + spawnpointID);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Remove the spawnpoint of a user(set it to the default spawnpoint)
	public void removeSpawnpoint(UUID uuid)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET SpawnpointID=? Where UUID=?;");
			stmt.setInt(1, 1);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			//Send the console a 'report' of the updated gem balance(to create a log to check when someone is exploiting a glitch)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Removed " + getUserName(uuid) + "'s spawnpoint and set it to default: " + 1);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public Date getJoinDate(UUID uuid)
	{
		Date date = null;
		
		try 
		{
			//Check two rows of the table for the friends that have a match with the user's ID
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();

			//Add all results to the arraylist
			if (results.next())
			{
				date = results.getDate("JoinDate");
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return date;
	}
	
	public Date getLastLoginDate(UUID uuid)
	{
		Date date = null;
		
		try 
		{
			//Check two rows of the table for the friends that have a match with the user's ID
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();

			//Add all results to the arraylist
			if (results.next())
			{
				date = results.getDate("LastLogin");
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return date;
	}
	
	public Long getPlayTime(UUID uuid, boolean overall)
	{
		Long time = null;
		
		try 
		{
			//Check two rows of the table for the friends that have a match with the user's ID
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();

			//Add all results to the arraylist
			if (results.next())
			{
				if (overall)
				{
					time = results.getLong("PlayTime");
				} else
				{
					time = results.getLong("PlayTimeToday");
				}
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return time;
	}
	
	public void clearPlayTimeToday()
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET PlayTimTodaye=0;");
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void savePlayTime(UUID uuid, Long time, boolean overall)
	{		
		try 
		{
			PreparedStatement stmt = null;
			if (overall)
			{
				stmt = main.getConnection().prepareStatement("UPDATE Player SET PlayTime=? Where UUID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("UPDATE Player SET PlayTimeToday=? Where UUID=?;");
			}
			stmt.setLong(1, time);;
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void SalaryPayout(UUID uuid)
	{
		main.DBreconnect();
		
		Player player = Bukkit.getServer().getPlayer(this.getUserName(uuid));
		Integer titleID = this.getTitleID(uuid);
		
		this.addCoins(uuid, title.getSalary(titleID));
		String gender = this.getGenderName(uuid);
		
		if (gender.equalsIgnoreCase("male"))
		{
			player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your salary: " + ColorOptions.salarysubjects + title.getSalary(titleID));
		} else if (gender.equalsIgnoreCase("female"))
		{
			player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylady! you received your salary: " + ColorOptions.salarysubjects + title.getSalary(titleID));
		} else
		{
			player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your salary: " + ColorOptions.salarysubjects + title.getSalary(titleID));
		}
		player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 3.0F, 3.0F);
	}
	
	public void IncomePayout(UUID uuid)
	{

		Player player = Bukkit.getServer().getPlayer(this.getUserName(uuid));
		Integer income = this.getIncome(uuid);
		if (income > 0)
		{
			this.addCoins(uuid, income);
			String gender = this.getGenderName(uuid);
			if (gender.equalsIgnoreCase("male"))
			{
				player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your income: " + ColorOptions.salarysubjects + income);
			} else if (gender.equalsIgnoreCase("female"))
			{
				player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylady! you received your income: " + ColorOptions.salarysubjects + income);
			}
			player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 3.0F, 3.0F);
		}
	}
	
	//Get the coin-amount of a user from the Database
	public Integer getBanditKills(UUID uuid)
	{
		Integer kills = null;
		try 
		{
			kills = getUserData(uuid).getInt("BanditKills");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return kills;
	}
	
	//Update the coin balance of a user with a certain amount of coins
	public void saveBanditKills(UUID uuid, Integer amount)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET BanditKills=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add coins to the balance of a user
	public void addBanditKills(UUID uuid, Integer amount)
	{
		//Get the balance before the addition of the amount specified in the method
		Integer OldKills = getBanditKills(uuid);
		//Add the old balance with the amount
		Integer NewKills = (OldKills + amount);
		
		//Save the new balance to the database
		saveBanditKills(uuid, NewKills);
	}
	
	//Remove coins from the balance of a user
	public void removeBanditKills(UUID uuid, Integer amount)
	{
		//Get the balance before substraction the amount specified in the method
		Integer OldKills = getBanditKills(uuid);
		//Substract the amount of the old balance
		Integer NewKills = (OldKills - amount);
		
		if (NewKills < 0)
		{
			NewKills = 0;
		}
		//Save the new balance to the database
		saveBanditKills(uuid, NewKills);
	}
	
	//Get the coin-amount of a user from the Database
	public Integer getCatchedFish(UUID uuid)
	{
		main.DBreconnect();
		Integer coins = null;
		try 
		{
			coins = getUserData(uuid).getInt("FishCatch");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return coins;
	}
	
	//Update the coin balance of a user with a certain amount of coins
	public void saveCatchedFish(UUID uuid, Integer amount)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET FishCatch=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add coins to the balance of a user
	public void addCatchedFish(UUID uuid, Integer amount)
	{
		//Get the balance before the addition of the amount specified in the method
		Integer Old = getCatchedFish(uuid);
		//Add the old balance with the amount
		Integer New = (Old + amount);
		
		//Save the new balance to the database
		saveCatchedFish(uuid, New);
	}
	
	//Remove coins from the balance of a user
	public void removeCatchedFish(UUID uuid, Integer amount)
	{
		//Get the balance before substraction the amount specified in the method
		Integer Old = getCatchedFish(uuid);
		//Substract the amount of the old balance
		Integer New = (Old - amount);
		
		if (New < 0)
		{
			New = 0;
		}
		//Save the new balance to the database
		saveCatchedFish(uuid, New);
	}
	
	//Get the coin-amount of a user from the Database
	public Integer getRestlessSoulKills(UUID uuid)
	{
		main.DBreconnect();
		Integer coins = null;
		try 
		{
			coins = getUserData(uuid).getInt("RestlessSoulKills");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return coins;
	}
	
	//Update the coin balance of a user with a certain amount of coins
	public void saveRestlessSoulKills(UUID uuid, Integer amount)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET RestlessSoulKills=? Where UUID=?;");
			stmt.setInt(1, amount);
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Add coins to the balance of a user
	public void addRestlessSoulKills(UUID uuid, Integer amount)
	{
		//Get the balance before the addition of the amount specified in the method
		Integer Old = getRestlessSoulKills(uuid);
		//Add the old balance with the amount
		Integer New = (Old + amount);
		
		//Save the new balance to the database
		saveRestlessSoulKills(uuid, New);
	}
	
	//Remove coins from the balance of a user
	public void removeRestlessSoulKills(UUID uuid, Integer amount)
	{
		//Get the balance before substraction the amount specified in the method
		Integer Old = getRestlessSoulKills(uuid);
		//Substract the amount of the old balance
		Integer New = (Old - amount);
		
		if (New < 0)
		{
			New = 0;
		}
		//Save the new balance to the database
		saveRestlessSoulKills(uuid, New);
	}
	
	public String getPreviousDonatorName(UUID uuid)
	{
		//Construct the Variables needed to get the name of the related DonatorID
		Integer DonatorID = null;
		String DonatorName = null;
		
		//Surround the get statement for donatorID with a try and catch for errors
		try 
		{
			DonatorID = getUserData(uuid).getInt("PreviousDonatorRank");
			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Donator WHERE ID=?;");
			stmt.setInt(1, DonatorID);
			
			ResultSet results = stmt.executeQuery();
			//Check if a name has been found matching the DonatorID
			if (results.next())
			{
				DonatorName = results.getString("Name");
				return DonatorName;
			} else
			{
				return DonatorName;
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return DonatorName;
	}
	
	public Integer getPreviousDonatorID(UUID uuid)
	{
		Integer donatorID = null;
		
		try 
		{			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * FROM Player WHERE UUID=?;");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			//Check if a name has been found matching the DonatorID
			if (results.next())
			{
				donatorID = results.getInt("PreviousDonatorRank");
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return donatorID;
	}
	
	public void setTempDonator(UUID uuid, Integer donatorID, Long expireTime)
	{
		Integer currentID = this.getDonatorID(uuid);
		
		this.setDonator(Bukkit.getConsoleSender(), uuid, donatorID);
		Long current = System.currentTimeMillis();
		Long expire = current + expireTime;
		
		try 
		{			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO DonatorTemp (UserID, DonatorID, TimeStart, TimeEnd, PreviousRankID) VALUES (?, ?, ?, ?, ?);");
			stmt.setInt(1, this.getUserID(uuid));
			stmt.setInt(2, donatorID);
			stmt.setLong(3, current);
			stmt.setLong(4, expire);
			stmt.setInt(5, currentID);

			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Added " + getUserName(uuid) + " to the DonatorTemp table and set his temporarily donatorRank to " + donatorID + " for " + expireTime + " seconds!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public Integer getPreviousRankID(UUID uuid)
	{
		Integer rankID = null;
		try 
		{			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM DonatorTemp WHERE UserID=?");
			stmt.setInt(1, this.getUserID(uuid));
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				rankID = results.getInt("PreviousRankID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return rankID;
		
	}
	
	public void removeTempDonator(UUID uuid)
	{
		Integer previousDonatorID = this.getPreviousRankID(uuid);
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM DonatorTemp WHERE UserID=?");
			stmt.setInt(1, this.getUserID(uuid));
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Removed " + getUserName(uuid) + "'s from the DonatorTemp table and set user to previous rank");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		this.setDonatorRank(uuid, previousDonatorID);
		
	}
	
	public ArrayList<Integer> getTempDonatorIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM DonatorTemp;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("UserID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public void updatePreviousRankID(UUID uuid, Integer donatorID)
	{
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE DonatorTemp SET PreviousRankID=? WHERE UserID=?");
			stmt.setInt(1, donatorID);
			stmt.setInt(2, this.getUserID(uuid));
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s previous DonatorRankID in the DonatorTemp table and set it to: " + donatorID);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}		
	}
	
	public Long getTempDonatorExpireTime(UUID uuid)
	{
		Long expire = null;
		try 
		{			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM DonatorTemp WHERE UserID=?");
			stmt.setInt(1, this.getUserID(uuid));
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				expire = results.getLong("TimeEnd");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return expire;
		
	}
	
	public void setDonator(CommandSender sender, UUID uuid, Integer donatorID)
	{
		Product product = new Product();
		Donator donator = new Donator();
		String donatorName = donator.getDonatorName(donatorID);
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction((Player) sender, null, false);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction((Player) sender, null, false);
			return;
		}
		Bukkit.getConsoleSender().sendMessage("Donator set found!");
		try
		{
			if (this.getUserID(uuid) != null)
			{
				Bukkit.getConsoleSender().sendMessage("user found");
				String targetUsername = this.getUserName(uuid);
				this.setDonatorRank(uuid, donatorID);
				if (this.isTempDonator(uuid))
				{
					this.updatePreviousRankID(uuid, donatorID);
				}
				if (Bukkit.getPlayer(uuid) != null)
				{
					Bukkit.getConsoleSender().sendMessage("Player found");
					if (donatorName.equalsIgnoreCase("noble"))
					{
						sender.sendMessage(ColorOptions.nobleformat + "You knighted " + ColorOptions.noblesubjects + targetUsername + ColorOptions.nobleformat + " to a " + ColorOptions.noblesubjects + donatorName);
						product.giveProduct(sender, user, product.getProductID("rareswordbox", false), 2);
					} else if (donatorName.equalsIgnoreCase("royal"))
					{
						sender.sendMessage(ColorOptions.royalformat + "You knighted " + ColorOptions.royalsubjects + targetUsername + ColorOptions.royalformat + " to a " + ColorOptions.royalsubjects + donatorName);
						product.giveProduct(sender, user, product.getProductID("legendaryswordbox", false), 1);
					} else if (donatorName.equalsIgnoreCase("dragonblood"))
					{
						sender.sendMessage(ColorOptions.dbformat + "You knighted " + ColorOptions.dbsubjects + targetUsername + ColorOptions.dbformat + " to a " + ColorOptions.dbsubjects + donatorName);
						product.giveProduct(sender, user, product.getProductID("legendaryswordbox", false), 2);
					} else if (donatorName.equalsIgnoreCase("default"))
					{
						sender.sendMessage(ColorOptions.messageformat + "You set " + ColorOptions.messagesubjects + targetUsername + ColorOptions.messageformat + " to " + ColorOptions.messagesubjects + donatorName);
					}
					Player target = Bukkit.getPlayer(uuid);
					target.sendMessage(donator.getDonatorColorSecondary(donatorID) + "You got knighted to a " + donator.getDonatorColorPrimary(donatorID) + donatorName + donator.getDonatorColorSecondary(donatorID) + ", Congratulations!");
					target.playSound(target.getLocation(), SoundHandler.LEVEL_UP, 1.0F, 1.0F);
				} else
				{
					main.scheduledDonator.put(uuid, donatorID);
					sender.sendMessage(ColorOptions.message + "Scheduled donator set to " + donatorName + " for player " + targetUsername);
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "Error: Player with uuid " + uuid + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
		this.updateScoreBoard();
	}
	
	public void upgradeDonator(CommandSender sender, UUID uuid)
	{
		Donator donator = new Donator();
		if (this.getUserID(uuid) != null)
		{
			Integer donatorID = donator.getDonatorID(this.getDonatorName(uuid));
			
			if (donatorID < main.largestDonatorID)
			{
				setDonator(sender, uuid, donatorID+1);
				if (this.isTempDonator(uuid))
				{
					this.updatePreviousRankID(uuid, donatorID+1);
				}
				if (Bukkit.getPlayer(uuid) != null)
				{
					Player target = Bukkit.getPlayer(uuid);
					target.sendMessage(donator.getDonatorColorSecondary(donatorID+1) + "You got knighted to a " + donator.getDonatorColorPrimary(donatorID+1) + donator.getDonatorName(donatorID+1) + donator.getDonatorColorSecondary(donatorID+1) + ", Congratulations!");
					target.playSound(target.getLocation(), SoundHandler.LEVEL_UP, 1.0F, 1.0F);
				}
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "Error: Player with uuid " + uuid + " cannot be found!");
		}
	}
	
	public void downgradeDonator(CommandSender sender, UUID uuid)
	{
		Donator donator = new Donator();
		if (this.getUserID(uuid) != null)
		{
			Integer donatorID = donator.getDonatorID(this.getDonatorName(uuid));
			
			if (donatorID > 0)
			{
				setDonator(sender, uuid, donatorID-1);
				if (this.isTempDonator(uuid))
				{
					this.updatePreviousRankID(uuid, donatorID-1);
				}
				if (Bukkit.getPlayer(uuid) != null)
				{
					Player target = Bukkit.getPlayer(uuid);
					target.sendMessage(donator.getDonatorColorSecondary(donatorID-1) + "You got demoted to a " + donator.getDonatorColorPrimary(donatorID-1) + donator.getDonatorName(donatorID-1) + donator.getDonatorColorSecondary(donatorID-1) + ", Congratulations!");
					target.playSound(target.getLocation(), SoundHandler.LEVEL_UP, 1.0F, 1.0F);
				}
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "Error: Player with uuid " + uuid + " cannot be found!");
		}
	}
	
	public boolean isTempDonator(UUID uuid)
	{
		boolean exist = false;
		Integer userID = this.getUserID(uuid);
		
		if (this.getTempDonatorIDList().contains(userID))
		{
			exist = true;
		}
		
		return exist;
	}
	
	public Integer getMultipliedInt(UUID uuid, Integer integer)
	{
		Donator donator = new Donator();
		Float donatorMultiplier = donator.getDonatorMultiplier(this.getDonatorID(uuid));
		
		Integer finalint = (int) (integer*main.eventMultiplier);
		finalint = (int) (finalint*donatorMultiplier);
		
		return finalint;
	}
	
	public void saveLastLogin(UUID uuid)
	{
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Player SET LastLogin=? WHERE UUID=?");
			stmt.setString(1, main.getTime());
			stmt.setString(2, uuid.toString());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated " + getUserName(uuid) + "'s last login date to: " + main.getTime());
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}	
	}
	
	public void deleteUser(UUID uuid)
	{
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Player WHERE UUID=?");
			stmt.setString(1, uuid.toString());
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Deleted user with UUID " + uuid + " from the Database at " + main.getTime());
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		if (Bukkit.getPlayer(uuid) != null)
		{
			Player player = Bukkit.getPlayer(uuid);
			JoinEvents.newPlayers.add(uuid);
			main.CreateUser(player.getName(), uuid, "Male", player.getAddress().getAddress());
		}
	}
	
    public double getDamageReduced(Entity entity)
    {
        org.bukkit.inventory.PlayerInventory inv = ((HumanEntity) entity).getInventory();
        ItemStack helmet = null;
        ItemStack boots = null;
        ItemStack chest = null;
        ItemStack pants = null;

        if(inv.getBoots() != null){boots = inv.getBoots();}
            if(inv.getBoots() == null){boots = new ItemStack(Material.LEATHER_BOOTS);}
        if (inv.getHelmet() != null){helmet = inv.getHelmet(); }
            if(inv.getHelmet() == null){helmet = new ItemStack(Material.LEATHER_HELMET);}
        if (inv.getChestplate() != null){chest = inv.getChestplate();}
            if(inv.getChestplate() == null){chest = new ItemStack(Material.LEATHER_CHESTPLATE);}
        if (inv.getLeggings() != null){pants = inv.getLeggings();}
            if(inv.getLeggings() == null){pants = new ItemStack(Material.LEATHER_LEGGINGS);}
        double red = 0.0;
        if (helmet.getType() == null || helmet.getType() == Material.AIR)red = red + 0.0;
        else if(helmet != null && helmet.getType() == Material.LEATHER_HELMET)red = red + 0.04;
        else if(helmet != null && helmet.getType() == Material.GOLD_HELMET)red = red + 0.08;
        else if(helmet != null && helmet.getType() == Material.CHAINMAIL_HELMET)red = red + 0.08;
        else if(helmet != null && helmet.getType() == Material.IRON_HELMET)red = red + 0.08;
        else if(helmet != null && helmet.getType() == Material.DIAMOND_HELMET)red = red + 0.12;
        //
        if (boots.getType() == null || boots.getType() == Material.AIR)red = red + 0;
        else if(boots != null && boots.getType() == Material.LEATHER_BOOTS)red = red + 0.04;
        else if(boots != null && boots.getType() == Material.GOLD_BOOTS)red = red + 0.04;
        else if(boots != null && boots.getType() == Material.CHAINMAIL_BOOTS)red = red + 0.04;
        else if(boots != null && boots.getType() == Material.IRON_BOOTS)red = red + 0.08;
        else if(boots != null && boots.getType() == Material.DIAMOND_BOOTS)red = red + 0.12;
        //
        if (pants.getType() == null || pants.getType() == Material.AIR)red = red + 0;
        else if(pants != null && pants.getType() == Material.LEATHER_LEGGINGS)red = red + 0.08;
        else if(pants != null && pants.getType() == Material.GOLD_LEGGINGS)red = red + 0.12;
        else if(pants != null && pants.getType() == Material.CHAINMAIL_LEGGINGS)red = red + 0.16;
        else if(pants != null && pants.getType() == Material.IRON_LEGGINGS)red = red + 0.20;
        else if(pants != null && pants.getType() == Material.DIAMOND_LEGGINGS)red = red + 0.24;
        //
        if (chest.getType() == null || chest.getType() == Material.AIR)red = red + 0;
        else if(chest != null && chest.getType() == Material.LEATHER_CHESTPLATE)red = red + 0.12;
        else if(chest != null && chest.getType() == Material.GOLD_CHESTPLATE)red = red + 0.20;
        else if(chest != null && chest.getType() == Material.CHAINMAIL_CHESTPLATE)red = red + 0.20;
        else if(chest != null && chest.getType() == Material.IRON_CHESTPLATE)red = red + 0.24;
        else if(chest != null && chest.getType() == Material.DIAMOND_CHESTPLATE)red = red + 0.32;
        return red;
    }

	public Integer getExpPart(UUID uuid, Integer part)
	{
		Title title = new Title();
		Integer titleID = this.getTitleID(uuid);
		Integer finalExp = 10;
		if (titleID < 18)
		{
			Integer nextExp = title.getExpmin(titleID+1);
			Integer currentExp = title.getExpmin(titleID);
			Integer nettoExp = ((nextExp-currentExp)/100);
			finalExp = (Integer) nettoExp*part;
		} else
		{
			Integer nextExp = title.getExpmin(titleID);
			Integer currentExp = title.getExpmin(titleID);
			Integer nettoExp = ((nextExp-currentExp)/100);
			finalExp = (Integer) nettoExp*part;
		}
		
		return finalExp;
	}
	
	public InetAddress getAdress(UUID uuid)
	{
		InetAddress address = null;
		
		try 
		{			
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Player WHERE UUID=?");
			stmt.setString(1, uuid.toString());
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				try {
					address = InetAddress.getByName(results.getString("Address"));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return address;
	}
	
	public void checkHouseMaximum(UUID uuid)
	{
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, Bukkit.getPlayer(uuid), true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, Bukkit.getPlayer(uuid), true);
			return;
		}
		House house = new House();
		Integer houseAmount = this.getHouseAmount(uuid);
		Integer houseMax = this.getHouseMaximum(uuid);
		Integer spawnpointID = this.getSpawnpointID(uuid);
		Integer userID = this.getUserID(uuid);
		if (houseAmount > houseMax)
		{
			Menu menu = new Menu();
			if (Bukkit.getPlayer(uuid) != null)
			{
				if (!ChatColor.stripColor(Bukkit.getPlayer(uuid).getOpenInventory().getTopInventory().getName()).equalsIgnoreCase("choose a house to remove"))
				{
					menu.openForcedHouseSell(user);
				}
			} else
			{
				for (Integer houseID : house.getHouseIDList(null))
				{
					if (house.getHouseOwnerID(houseID) == userID)
					{
						if (house.getHouseSpawnPoint(houseID) != spawnpointID && this.getHouseAmount(uuid) > 1)
						{
							Bukkit.getConsoleSender().sendMessage("Removed a house-owner of house with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
							house.RemoveHouseOwner(houseID);
							if (this.getHouseAmount(uuid) <= this.getHouseMaximum(uuid))
							{
								break;
							}
						} else
						{
							Bukkit.getConsoleSender().sendMessage("Removed a house-owner of house with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
							house.RemoveHouseOwner(houseID);
							if (this.getHouseAmount(uuid) <= this.getHouseMaximum(uuid))
							{
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public void checkPropertyMaximum(UUID uuid)
	{
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, Bukkit.getPlayer(uuid), true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, Bukkit.getPlayer(uuid), true);
			return;
		}
		Property property = new Property();
		Integer propertyAmount = this.getPropertyAmount(uuid);
		Integer propertyMax = this.getPropertyMaximum(uuid);
		Integer spawnpointID = this.getSpawnpointID(uuid);
		Integer userID = this.getUserID(uuid);
		if (propertyAmount > propertyMax)
		{
			Menu menu = new Menu();
			if (Bukkit.getPlayer(uuid) != null)
			{
				if (!ChatColor.stripColor(Bukkit.getPlayer(uuid).getOpenInventory().getTopInventory().getName()).equalsIgnoreCase("choose a property to remove"))
				{
					menu.openForcedPropertySell(user);
				}
			} else
			{
				for (Integer houseID : property.getIDList(null, null))
				{
					if (property.getPropertyOwnerID(houseID) == userID)
					{
						if (property.getPropertySpawnPoint(houseID) != spawnpointID && this.getPropertyAmount(uuid) > 1)
						{
							Bukkit.getConsoleSender().sendMessage("Removed a property-owner of property with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
							property.RemovePropertyOwner(houseID);
							if (propertyAmount <= propertyMax)
							{
								break;
							}
						} else
						{
							Bukkit.getConsoleSender().sendMessage("Removed a property-owner of property with ID " + houseID + " because the owner with ID " + userID + " closed the forced-sell-menu!") ;
							property.RemovePropertyOwner(houseID);
							if (propertyAmount <= propertyMax)
							{
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public Inventory getOpenMenu(Player target)
	{
		Inventory menu = null;
		
		menu = target.getOpenInventory().getTopInventory();
		
		return menu;
	}
	
	public void inventoryAddItem(Player player, ItemStack item)
	{
		Inventory inv = player.getInventory();
		Inventory enderchest = player.getEnderChest();
		if (inv.firstEmpty() != -1)
		{
			inv.addItem(item);
			player.updateInventory();
		} else if (enderchest.firstEmpty() != -1)
		{
			enderchest.addItem(item);
			player.sendMessage(ColorOptions.error + "Inventory full, adding item to your enderchest!");
		} else
		{
			player.getWorld().dropItemNaturally(player.getLocation(), item);
			player.sendMessage(ColorOptions.error + "Inventory and enderchest full, dropping item near you!");
		}
	}
	
	public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut)
	{
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PlayerConnection connection = craftPlayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
        IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subTitle + "'}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
        Bukkit.getConsoleSender().sendMessage("Sent!");
    }
	
	public void updateScoreBoard()
	{
		Scoreboard board = new Scoreboard();
		board.setBoard(null);
		
	}
	
//	public void setScoreBoard(Player player)
//	{
//		Scoreboards.Scoreboard scoreboard = new Scoreboards.Scoreboard();
//		UUID uuid = player.getUniqueId();
//	    if (player.hasPermission("k&k.owner"))
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to owner team!");
//	    	}
//	    	if (this.inOwnerModus(uuid))
//	    	{
//	    		for (Player target : Bukkit.getOnlinePlayers())
//	    		{
//	    			if (!target.hasPermission("k&k.staff"))
//	    			{
//	    				target.hidePlayer(player);
//	    			}
//	    		}
//	    		scoreboard.setScoreBoard(player, "owner", true);
//	    	} else
//	    	{
//	    		if (OwnerCommands.enableonquit.containsKey(uuid) && OwnerCommands.enableonquit.get(uuid) == true)
//	    		{
//	    			main.ownermodus.put(uuid, true);
//					player.sendMessage(OwnerCommands.enabled);
//					for (Player players : Bukkit.getOnlinePlayers())
//					{
//						if (!players.hasPermission("k&k.staff"))
//						{
//							players.hidePlayer(player);
//						} else
//						{
//							
//						}
//					}
//					scoreboard.setScoreBoard(player, "owner", true);
//	    		} else if (OwnerCommands.disableonquit.containsKey(uuid) && OwnerCommands.disableonquit.get(uuid) == true)
//	    		{
//	    			main.ownermodus.put(uuid, false);
//					player.sendMessage(OwnerCommands.disabled);
//					for (Player players : Bukkit.getOnlinePlayers())
//					{
//						players.showPlayer(player);
//					}
//					scoreboard.setScoreBoard(player, "owner", false);
//	    		} else
//	    		{
//		    		scoreboard.setScoreBoard(player, "owner", false);
//	    		}
//	    	}
//	    } else
//	    if (player.hasPermission("k&k.co-owner") && !player.hasPermission("k&k.owner"))	
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to co-owner team!");
//	    	}
//	    	if (this.inOwnerModus(uuid))
//	    	{
//	    		for (Player target : Bukkit.getOnlinePlayers())
//	    		{
//	    			if (!target.hasPermission("k&k.staff"))
//	    			{
//	    				target.hidePlayer(player);
//	    			}
//	    		}
//	    		scoreboard.setScoreBoard(player, "co-owner", true);
//	    	} else
//	    	{
//	    		if (OwnerCommands.enableonquit.containsKey(uuid) && OwnerCommands.enableonquit.get(uuid) == true)
//	    		{
//	    			main.ownermodus.put(uuid, true);
//					player.sendMessage(OwnerCommands.enabled);
//					for (Player players : Bukkit.getOnlinePlayers())
//					{
//						if (!players.hasPermission("k&k.staff"))
//						{
//							players.hidePlayer(player);
//						} else
//						{
//							
//						}
//					}
//					scoreboard.setScoreBoard(player, "co-owner", true);
//	    		} else if (OwnerCommands.disableonquit.containsKey(uuid) && OwnerCommands.disableonquit.get(uuid) == true)
//	    		{
//	    			main.ownermodus.put(uuid, false);
//					player.sendMessage(OwnerCommands.disabled);
//					for (Player players : Bukkit.getOnlinePlayers())
//					{
//						players.showPlayer(player);
//					}
//					scoreboard.setScoreBoard(player, "co-owner", false);
//	    		} else
//	    		{
//		    		scoreboard.setScoreBoard(player, "co-owner", false);
//	    		}
//	    	}
//	    } else if (player.hasPermission("k&k.staff") && !player.hasPermission("k&k.owner"))
//		{
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to staff team!");
//	    	}
//	    	if (this.inOwnerModus(uuid))
//	    	{
//	    		for (Player target : Bukkit.getOnlinePlayers())
//	    		{
//	    			if (!target.hasPermission("k&k.staff"))
//	    			{
//	    				target.hidePlayer(player);
//	    			}
//	    		}
//	    		scoreboard.setScoreBoard(player, "staff", true);
//	    	} else
//	    	{
//	    		if (OwnerCommands.enableonquit.containsKey(uuid) && OwnerCommands.enableonquit.get(uuid) == true)
//	    		{
//	    			main.ownermodus.put(uuid, true);
//					player.sendMessage(OwnerCommands.enabled);
//					for (Player players : Bukkit.getOnlinePlayers())
//					{
//						if (!players.hasPermission("k&k.staff"))
//						{
//							players.hidePlayer(player);
//						} else
//						{
//							
//						}
//					}
//					scoreboard.setScoreBoard(player, "staff", true);
//	    		} else if (OwnerCommands.disableonquit.containsKey(uuid) && OwnerCommands.disableonquit.get(uuid) == true)
//	    		{
//	    			main.ownermodus.put(uuid, false);
//					player.sendMessage(OwnerCommands.disabled);
//					for (Player players : Bukkit.getOnlinePlayers())
//					{
//						players.showPlayer(player);
//					}
//					scoreboard.setScoreBoard(player, "staff", false);
//	    		} else
//	    		{
//		    		scoreboard.setScoreBoard(player, "staff", false);
//	    		}
//	    	}
//		} else if (this.getDonatorName(uuid).equalsIgnoreCase("noble"))
//		{
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to noble team!");
//	    	}
//			scoreboard.setScoreBoard(player, "noble", false);
//		} else if (this.getDonatorName(uuid).equalsIgnoreCase("royal"))
//		{
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to royal team!");
//	    	}
//			scoreboard.setScoreBoard(player, "royal", false);
//		} else if (this.getDonatorName(uuid).equalsIgnoreCase("dragon blood"))
//		{
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to db team!");
//	    	}
//			scoreboard.setScoreBoard(player, "db", false);
//		} else
//		{
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to default team!");
//	    	}
//			scoreboard.setScoreBoard(player, "default", false);
//		}
//	}
	
	public boolean isUsedAddress(InetAddress address)
	{
		boolean used = false;
		
		List<Integer> list = getUserIDlistByAddress(address);
		if (!list.isEmpty())
		{
			if (list.size() < 1)
			{
				used = true;
			}
		}
		
		return used;
	}
	
	public List<Integer> getUserIDlistByAddress(InetAddress address)
	{
		List<Integer> userList = new ArrayList<Integer>();
		
		try 
		{	
			//Prepare the search query
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Player WHERE Address=?;");
			stmt.setString(1, address.toString().replaceAll("/", ""));
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				userList.add(results.getInt("ID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return userList;
	}
	
	public Integer getPing(Player player)
	{
		Integer ping = null;
		
		CraftPlayer cp = (CraftPlayer) player; 
		EntityPlayer ep = cp.getHandle(); 
		ping = ep.ping;
		
		return ping; 
	}
	
	public boolean inOwnerModus(UUID uuid)
	{
		boolean ownermodus = false;
		
		if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
		{
			ownermodus = true;
		}
		
		return ownermodus;
	}
	
	public boolean inOnQuit(UUID uuid)
	{
		boolean onquit = false;
		
		if (OwnerCommands.disableonquit.containsKey(uuid) || OwnerCommands.enableonquit.containsKey(uuid))
		{
			onquit = true;
		}
		
		return onquit;
	}
	
	public boolean inEnableOnQuit(UUID uuid)
	{
		boolean enable = false;
		
		if (inOnQuit(uuid))
		{
			if (OwnerCommands.enableonquit.containsKey(uuid) && OwnerCommands.enableonquit.get(uuid) == true)
			{
				enable = true;
			}
		}
		
		return enable;
	}
	
	public void setOwnerMode(Player player, boolean enable)
	{
		if (enable)
		{
			if (CombatCheck.incombat.containsKey(player))
			{
				CombatCheck.incombat.remove(player);
			}
			main.ownermodus.put(player.getUniqueId(), true);
			for (Player target : Bukkit.getOnlinePlayers())
			{
				if (!target.hasPermission("k&k.staff"))
				{
					target.hidePlayer(player);
				}
			}
			try
			{
				ActionBar bar = new ActionBar(OwnerCommands.enabled);
				bar.sendToPlayer(player);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
//			player.sendMessage(OwnerCommands.enabled);
		} else
		{
			main.ownermodus.put(player.getUniqueId(), false);
			for (Player target : Bukkit.getOnlinePlayers())
			{
				target.showPlayer(player);
			}
			try
			{
				ActionBar bar = new ActionBar(OwnerCommands.disabled);
				bar.sendToPlayer(player);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
//			player.sendMessage(OwnerCommands.disabled);

		}
	}
	
	public boolean inStaffModus(UUID uuid)
	{
		boolean staffmodus = false;
		
		if (main.staffmodus.containsKey(uuid) && main.staffmodus.get(uuid) == true)
		{
			staffmodus = true;
		}
		
		return staffmodus;
	}
	
	public void setStaffMode(Player player, boolean enable)
	{
		if (enable)
		{
			if (CombatCheck.incombat.containsKey(player))
			{
				CombatCheck.incombat.remove(player);
			}
			main.staffmodus.put(player.getUniqueId(), true);
			for (Player target : Bukkit.getOnlinePlayers())
			{
				if (!target.hasPermission("k&k.staff"))
				{
					target.hidePlayer(player);
				}
			}
			try
			{
				ActionBar bar = new ActionBar(OwnerCommands.senabled);
				bar.sendToPlayer(player);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
//			player.sendMessage(OwnerCommands.senabled);
		} else
		{
			main.staffmodus.put(player.getUniqueId(), false);
			for (Player target : Bukkit.getOnlinePlayers())
			{
				target.showPlayer(player);
			}
			try
			{
				ActionBar bar = new ActionBar(OwnerCommands.sdisabled);
				bar.sendToPlayer(player);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
//			player.sendMessage(OwnerCommands.sdisabled);
		}
		if (main.debug && !main.staffmodus.isEmpty())
		{
			for (UUID uuid : main.staffmodus.keySet())
			{
				Bukkit.getConsoleSender().sendMessage("Contains: " + this.getUserName(uuid));
			}
		}
	}
	
	public String getChatPrefix(Player player)
	{
		String prefix = null;
		String username = player.getName();
		UUID uuid = player.getUniqueId();
		if (player.hasPermission("k&k.owner"))
		{
			prefix = ColorOptions.ownerformat + "[" + ColorOptions.ownersubjects + "OWNER" + ColorOptions.ownerformat + "]-{" + ColorOptions.ownersubjects + "" + ChatColor.BOLD + this.getTitleName(uuid) + ColorOptions.ownerformat + "}- " + ColorOptions.ownersubjects + username + ColorOptions.message + ": ";
		} else if (player.hasPermission("k&k.co-owner"))
		{
			prefix = ColorOptions.ownerformat + "[" + ColorOptions.ownersubjects + "CO-OWNER" + ColorOptions.ownerformat + "]-{" + ColorOptions.ownersubjects + "" + ChatColor.BOLD + this.getTitleName(uuid) + ColorOptions.ownerformat + "}- " + ColorOptions.ownersubjects + username + ColorOptions.message + ": ";
		} else
		if (player.hasPermission("k&k.staff"))
		{
			prefix = ColorOptions.staffformat + "[" + ColorOptions.staffsubjects + "STAFF" + ColorOptions.staffformat + "]-{" + ColorOptions.staffsubjects + "" + ChatColor.BOLD + this.getTitleName(uuid) + ColorOptions.staffformat + "}- " + ColorOptions.staffsubjects + username + ColorOptions.message + ": ";
		} else
		{
			/*
			String username = player.getName();
			player.setDisplayName(ColorOptions.nobleformat + "[B]-{" + ColorOptions.noblesubjects + "Noble " + this.getTitleName(pu) + ColorOptions.nobleformat + "}- " + ColorOptions.noblesubjects + username + ChatColor.WHITE);
			 */
			if (this.getDonatorName(uuid).equalsIgnoreCase("noble"))
			{
				prefix = ColorOptions.nobleformat + "-{" + ColorOptions.noblesubjects + "Noble " + this.getTitleName(uuid) + ColorOptions.nobleformat + "}- " + ColorOptions.noblesubjects + username + ColorOptions.message + ": ";
			} else
			if (this.getDonatorName(uuid).equalsIgnoreCase("royal"))
			{
				prefix = ColorOptions.royalformat + "-{" + ColorOptions.royalsubjects + "Royal " + this.getTitleName(uuid) + ColorOptions.royalformat + "}- " + ColorOptions.royalsubjects + username + ColorOptions.message + ": ";
			} else
			if (this.getDonatorName(uuid).equalsIgnoreCase("dragon blood"))
			{
				prefix = ColorOptions.dbformat + "-{" + ColorOptions.dbsubjects + "Dragon Blood " + this.getTitleName(uuid) + ColorOptions.dbformat + "}- " + ColorOptions.dbsubjects + username + ColorOptions.message + ": ";
			} else
			{
				prefix = ColorOptions.defaultformat + "-{" + ColorOptions.defaultsubjects + this.getTitleName(uuid) + ColorOptions.defaultformat + "}- " + ColorOptions.defaultsubjects + username + ColorOptions.message + ": ";
			}
		}
		
		return prefix;
	}
	
	public Location getFrontLocation(Player player)
	{
		Location loc = null;
		
		Location eyeLocation = player.getEyeLocation();
		Location playerLocation = player.getLocation();
		
		Vector direction = playerLocation.getDirection();
		direction.setY(0);
		direction.normalize();
		direction.multiply(8);
		Bukkit.getConsoleSender().sendMessage("Vector: " + direction.toString());
		loc = eyeLocation.add(direction);
		
		return loc;
	}
	
	public void playSound(Player player, String sound)
	{
		if (sound.equalsIgnoreCase("succesclick"))
		{
			player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 1.0F);
		} else if (sound.equalsIgnoreCase("back"))
		{
			player.playSound(player.getLocation(), SoundHandler.NOTE_STICKS, 1.0F, 1.0F);
		} else if (sound.equalsIgnoreCase("failclick"))
		{
			player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
		} else if (sound.equalsIgnoreCase("successkillclick"))
		{
			player.playSound(player.getLocation(), SoundHandler.NOTE_PIANO, 1.0F, 2.0F);
		} else if (sound.equalsIgnoreCase("specialskill"))
		{
		    player.playSound(player.getLocation(), SoundHandler.WITHER_HURT, 1.0F, 1.0F);
		} else if (sound.equalsIgnoreCase("buyitem"))
		{
			player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 2.0F);
		}
	}

}
