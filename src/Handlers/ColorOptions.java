package Handlers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;

import Donator.Donator;
import Main.Main;
import Users.User;

public interface ColorOptions 
{
	Main main = Main.getPlugin(Main.class);

	Donator donator = new Donator();
//0
	//Index...............0
	//FalseCommand........1
	//Currency.Colors.....2
	//Coin.Colors.........3
	//Gem.Colors..........4
	//Stats.Colors........5
	//Friend.Colors.......6
	//Kill.Commands.......7
	
//1 //FalseCommand & ChatFormat
	public ChatColor falsecommand = ChatColor.RED; //ColorOptions.falsecommand
	public ChatColor error = ChatColor.RED;
	public ChatColor message = ChatColor.GRAY;
//2	//Currency Colors
	public ChatColor currencycolor = ChatColor.GOLD; //ColorOptions.currencycolor
	public ChatColor names = ChatColor.GREEN; //ColorOptions.names
	
//3	//Coin Colors
	public ChatColor coinStats = ChatColor.YELLOW; //ColorOptions.coins
	
//4	//Gem Colors
	public ChatColor gemStats = ChatColor.AQUA; //ColorOptions.gems
	
//5	//Stats Colors
	public ChatColor statsformat = ChatColor.GOLD; //ColorOptions.statsformat
	public ChatColor statsresults = ChatColor.GREEN; //ColorOptions.statsoutcome
	public ChatColor stats = ChatColor.AQUA; //ColorOptions.stats
	
//6 //Friend Colors
	public ChatColor friendformat = ChatColor.GOLD; //ColorOptions.friendformat
	public ChatColor friendresults= ChatColor.GREEN; //ColorOptions.friendoutcome
	public ChatColor friend = ChatColor.AQUA; //ColorOptions.friend

//7 //Kill Commands
	public ChatColor dead = ChatColor.RED;

//8 //Title Promotion
	public ChatColor promotionformat = ChatColor.GOLD; //ColorOptions.promotionformat
	public ChatColor promotionresults = ChatColor.GREEN; //ColorOptions.promotionoutcome
	public ChatColor demotionformat = ChatColor.RED;
	public ChatColor demotionsubjects = ChatColor.DARK_RED;
	public ChatColor unlockformat = ChatColor.GREEN;
	public ChatColor unlocksubjects = ChatColor.DARK_GREEN;

//9 //Message formats
	public ChatColor messageformat = ChatColor.GOLD; //ColorOptions.messageformat
	public ChatColor messagesubjects = ChatColor.GREEN; //ColorOptions.messagesubjects
	public ChatColor messageachievement = ChatColor.AQUA; //ColorOptions.messageachievement
//10//Salary format
	public ChatColor salaryformat = ChatColor.YELLOW; //ColorOptions.salaryformat
	public ChatColor salarysubjects = ChatColor.AQUA; //ColoeOptions.salarysubjects
//11//Donator Chat formats
//11.1//Default format
	public ChatColor defaultformat = ChatColor.DARK_GREEN; //ColorOptions.defaultformat
	public ChatColor defaultsubjects = ChatColor.GREEN; //ColorOptions.defaultsubjects
//11.2//Noble format
	public ChatColor nobleformat = donator.getDonatorColorSecondary(donator.getDonatorID("noble")); //ColorOptions.nobleformat
	public ChatColor noblesubjects = donator.getDonatorColorPrimary(donator.getDonatorID("noble")); //ColorOptions.noblesubjects
//11.3//Royal format
	public ChatColor royalformat = donator.getDonatorColorSecondary(donator.getDonatorID("royal")); //ColorOptions.royalformat
	public ChatColor royalsubjects = donator.getDonatorColorPrimary(donator.getDonatorID("royal")); //ColorOptions.royalsubjects
//11.4//Dragon Blood format
	public ChatColor dbformat = donator.getDonatorColorSecondary(donator.getDonatorID("dragon blood")); //ColorOptions.dbformat
	public ChatColor dbsubjects = donator.getDonatorColorPrimary(donator.getDonatorID("dragon blood")); //ColorOptions.dbsubjects
//11.5//Staff format
	public ChatColor staffformat = ChatColor.DARK_AQUA;
	public ChatColor staffsubjects = ChatColor.BLUE;
//11.6//Owner format
	public ChatColor ownerformat = ChatColor.GOLD;
	public ChatColor ownersubjects = ChatColor.DARK_PURPLE;
//12//Skills format
	public ChatColor skillsformat = ChatColor.DARK_RED;
	public ChatColor skillsname = ChatColor.GOLD;
	public ChatColor skillsnotname = ChatColor.RED;
	public ChatColor specialskillsname = ChatColor.DARK_PURPLE;
	public ChatColor skillsinfoachieved = ChatColor.GREEN;
	public ChatColor skillsinfonotachieved = ChatColor.GRAY;
//13//SpecialSkills format
	public ChatColor Ninja = ChatColor.BLACK;
	public ChatColor Shotbow = ChatColor.GOLD;
	public ChatColor Pickpocket = ChatColor.DARK_GRAY;
	public ChatColor Forger = ChatColor.GREEN;
	public ChatColor Assassin = ChatColor.BLUE;
	public ChatColor Juggernaut = ChatColor.DARK_AQUA;
	public ChatColor Avenger = ChatColor.RED;
	
	public String statsbrackets = ColorOptions.statsformat + "=================================================";
	public String halfstatsbrackets = ColorOptions.statsformat + "=========================";
	public String devidestatsbrackets = ColorOptions.statsformat + "--------------------------------------------------";
	public String halfdevidestatsbrackets = ColorOptions.statsformat + "-------------------------";
	
	public String rawbrackets = "=================================================";
	public String rawhalfbrackets = "=========================";
	public String rawdevidebrackets = "--------------------------------------------------";
	public String rawhalfdevidebrackets = "-------------------------";
	public ChatColor KAKColor = ChatColor.BLUE;
	public String KAKFormatshort = ChatColor.GRAY + "[" + ChatColor.BLUE + "K and K" + ChatColor.GRAY + "] ";
	public String KAKFormat = ChatColor.GRAY + "[" + ColorOptions.KAKColor + "Knights and Kings" + ChatColor.GRAY + "] ";
	public String messageArrow = "► ";
	public String star = "★";
	
	public ChatColor soulbound = ChatColor.RED;
	public ChatColor ghosted = ChatColor.DARK_GRAY;
	
	public static List<String> getPlayerStats(User user)
	{
		List<String> msg = new ArrayList<String>(Arrays.asList(
				ColorOptions.statsbrackets,
				ColorOptions.stats + ColorOptions.messageArrow + "Username: " + ColorOptions.statsresults + "Lord " + user.getUsername(),
				ColorOptions.stats + ColorOptions.messageArrow + "Title: " + ColorOptions.statsresults + user.getTitleName(),
				ColorOptions.stats + ColorOptions.messageArrow + "Experience: " + ColorOptions.statsresults + formatCurrency(user.getExperience()),
				ColorOptions.stats + ColorOptions.messageArrow + "First Joined: " + ColorOptions.statsresults + user.getJoinDate(),
				ColorOptions.stats + ColorOptions.messageArrow + "Playtime: " + ColorOptions.statsresults + main.getHourtime(user.getPlayTime(false).intValue()/1000) + " Hours and " + main.getRestMinutetime(user.getPlayTime(false).intValue()/1000) + " minutes",
				ColorOptions.stats + ColorOptions.messageArrow + "Playtime today: " + ColorOptions.statsresults + main.getHourtime(user.getPlayTime(true).intValue()/1000) + " Hours and " + main.getRestMinutetime(user.getPlayTime(true).intValue()/1000) + " minutes",
				ColorOptions.stats + ColorOptions.messageArrow + "Kills (players): " + ColorOptions.statsresults + user.getKills(),
				ColorOptions.stats + ColorOptions.messageArrow + "Deaths: " + ColorOptions.statsresults + user.getDeaths(),
				ColorOptions.stats + ColorOptions.messageArrow + "Coins: " + ColorOptions.statsresults + formatCurrency(user.getCoins()),
				ColorOptions.stats + ColorOptions.messageArrow + "Gems: " + ColorOptions.statsresults + formatCurrency(user.getGems()),
				ColorOptions.stats + ColorOptions.messageArrow + "Friends: " + ColorOptions.statsresults + user.getFriendAmount(),
				ColorOptions.statsbrackets
				));
		try
		{
			if (user.getGenderName().equalsIgnoreCase("Female"))
			{
				msg.add(1, msg.get(1).replace("Lord", "Lady"));
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return msg;
	}
	
	public static String formatCurrency(Integer amount)
	{
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);
		String result = format.format((double) amount);
		return result.split(",")[0];
	}
	
	public static String stars(Integer amount)
	{
		String stars = star;
		
		for (int i = 1; i < amount; i++)
		{
			stars = stars + star;
		}
		
		return stars;
	}
	public static ChatColor getColorbyCode(String code)
	{
		ChatColor color = null;
		switch (code)
		{
		case "0": color = ChatColor.BLACK;
				break;
		case "1": color = ChatColor.DARK_BLUE;
				break;
		case "2": color = ChatColor.DARK_GREEN;
				break;
		case "3": color = ChatColor.DARK_AQUA;
				break;
		case "4": color = ChatColor.DARK_RED;
				break;
		case "5": color = ChatColor.DARK_PURPLE;
				break;
		case "6": color = ChatColor.GOLD;
				break;
		case "7": color = ChatColor.GRAY;
				break;
		case "8": color = ChatColor.DARK_GRAY;
				break;
		case "9": color = ChatColor.BLUE;
				break;
		case "a": color = ChatColor.GREEN;
				break;
		case "c": color = ChatColor.RED;
				break;
		case "d": color = ChatColor.LIGHT_PURPLE;
				break;
		case "e": color = ChatColor.YELLOW;
				break;
		case "f": color = ChatColor.WHITE;
				break;
		default: color = ChatColor.WHITE;
				break;
				
		}
		
		return color;
	}
	
	public static ChatColor getEnchantColor(Integer grade)
	{
		ChatColor color = ChatColor.BLUE;
		
		if (grade == 2)
		{
			color = ChatColor.AQUA;
		} else if (grade == 3)
		{
			color = ChatColor.LIGHT_PURPLE;
		}
		
		return color;
	}
	
	public static ArrayList<String> getLocationString(String streetName, Integer streetNumber, String townName)
	{
		String street = statsformat + "Street: " + statsresults + streetName;
		String streetnumber = statsformat + "Streetnumber: " + statsresults + streetNumber;
		String town = statsformat + "Town: " + statsresults + townName;
		return new ArrayList<String>(Arrays.asList(street, streetnumber, town));
	}
	
	public static List<String> getStringLines(String string, Integer splitCount)
	{
		List<String> description = new ArrayList<String>();
		
		String[] split = string.split(" ");
		if (string.split(" ").length > splitCount)
		{
			StringBuilder sb = new StringBuilder();
			int count = 0;
	        for(int i = 0; i < split.length; i +=splitCount)
	        {       
	            count=i+(splitCount-1);
	            for(int j=i;j<=count; j++)
	            {
	            	if (j < split.length)
	            	{
	            		sb.append(split[j]).append(" ");
	            	}
	            }
	            description.add(sb.toString());
	            sb.delete(0, sb.length());
	        }
		} else
		{
			description.add(string);
		}
		
		return description;
	}
	
	

}
