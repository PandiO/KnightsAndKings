package Handlers;

import org.bukkit.entity.Player;

public interface ErrorHandlers 
{
	public String userNotFound = ColorOptions.error + "User could not be found.";
	public String userDataNotLoaded = ColorOptions.error + "Data could not be loaded.";
	public String reccommendReload = ColorOptions.error + "Try logging off and on to solve the problem";
	
	public static void userNotFoundAction(Player sender, Player target, boolean targetIsSender)
	{
		if (sender != null)
		{
			sender.sendMessage(userNotFound);
		}
		target.sendMessage(userNotFound);
		if (targetIsSender)
		{
			target.sendMessage(reccommendReload);
		} else
		{
			target.sendMessage(userDataNotLoaded);
			target.sendMessage(reccommendReload);
		}
	}
}
