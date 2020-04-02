package Tutorial;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import Handlers.ColorOptions;

public class Tutorials 
{
	public static List<Tutorial> tutorials = new ArrayList<Tutorial>();
	public static List<Tutorial> introTutorials = new ArrayList<Tutorial>();
	
	public static Tutorial getTutorial(Player player)
	{
		Tutorial tutorial = null;
		
		for (Tutorial tut : tutorials)
		{
			if (tut.target == player)
			{
				tutorial = tut;
				break;
			}
		}
		
		return tutorial;
	}
	
	public static Tutorial getIntroTutorial(Player player)
	{
		Tutorial tutorial = null;
		
		for (Tutorial tut : introTutorials)
		{
			if (tut.target == player)
			{
				tutorial = tut;
				break;
			}
		}
		
		return tutorial;
	}
	
	public static void notAllowed(Player player)
	{
		player.sendMessage(ColorOptions.message + "If you wish to stop the tutorial, please type " + ColorOptions.error + "cancel");
		player.sendMessage(ColorOptions.message + "If you wish to continue the tutorial, please type " + ColorOptions.messagesubjects + "next");
	}
}
