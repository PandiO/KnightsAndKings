package Afk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import DataManager.Users2;
import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;

public class AfkCommand implements CommandExecutor
{
	private Main main;	
	public AfkCommand(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (label.equalsIgnoreCase("afk"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				User user = null;
				
				try
				{
					user = Users2.FindUser(player.getUniqueId());
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(null, player, true);
					return false;
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(null, player, true);
					return false;
				}
				if (!user.isAfk())
				{
					user.setAfk();
				} else
				{
					user.removeAfk();
				}
			}
		}
		return false;
	}
}
