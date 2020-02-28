package Titles;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class NextTitleCommands implements CommandExecutor
{
	Title title = new Title();
	private Main main;
	public NextTitleCommands(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("nexttitle"))
		{
			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
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
			if (args.length == 1)
			{
				String userName = args[0];
				User userTarget = null;
				
				try
				{
					userTarget = Users.getUser(Users.fetchUUIDbyUsername(userName));
				} catch (Exception ex)

				{
					sender.sendMessage(ColorOptions.message + "User not online, searching for offline player..");
					try
					{
						userTarget = new User(Users.fetchUUIDbyUsername(userName));
						userTarget.setOfflineUser(true);
					} catch (Exception e)
					{
						ErrorHandlers.userNotFoundAction(player, null, false);
						return false;
					}
				}
				String gender = userTarget.getGenderName();
				Integer genderID = userTarget.getGenderID();
				if (userTarget.getTitleID() == Integer.valueOf(18))
				{
					if (gender.equalsIgnoreCase("male"))
					{
						player.sendMessage(ColorOptions.messagesubjects + args[0] + ColorOptions.messageformat + " carries the highest title to be achieved! might consider congratulate the Highlord?");							
					}
					if (gender.equalsIgnoreCase("female"))
					{
						player.sendMessage(ColorOptions.messagesubjects + args[0] + ColorOptions.messageformat + " carries the highest title to be achieved! might consider congratulate the Highlady");							
					}
					if (gender.equalsIgnoreCase("none"))
					{
						player.sendMessage(ColorOptions.messagesubjects + args[0] + ColorOptions.messageformat + " carries the highest title to be achieved! might consider congratulate this person, yet we do not know his gender(might be a ghost?)");							
					}
				} else
				{
					Integer targetTitleID = userTarget.getTitleID();
					player.sendMessage(ColorOptions.messagesubjects + userTarget.getUsername() + ColorOptions.messageformat + " Needs " + ColorOptions.messagesubjects + (title.getExpmin(targetTitleID+1)-userTarget.getExperience()) + ColorOptions.messageformat + " experience to be promoted to " + ColorOptions.messagesubjects + title.getTitleName(targetTitleID+1, genderID));
				}
				if (userTarget.isOfflineUser())
				{
					userTarget.destroy();
				}
			} else 
			{
				Integer genderID = user.getGenderID();
				Integer titleID = user.getTitleID();
				if (titleID == Integer.valueOf(18))
				{
					String gender = user.getGenderName();
					if (gender.equalsIgnoreCase("male"))
					{
						player.sendMessage(ColorOptions.messageformat + "You carry the highest title to be achieved! congratulations Highlord");							
					}
					if (gender.equalsIgnoreCase("female"))
					{
						player.sendMessage(ColorOptions.messageformat + "You carry the highest title to be achieved! congratulations Highlady");							
					}
					if (gender.equalsIgnoreCase("none"))
					{
						player.sendMessage(ColorOptions.messageformat + "You carry the highest title to be achieved! congratulations! (might consider /changegender)");							
					}
				} else
				{
					player.sendMessage(ColorOptions.messageformat + "You Need " + ColorOptions.messagesubjects + (title.getExpmin(titleID+1)-user.getExperience()) + ColorOptions.messageformat + " experience to be promoted to " + ColorOptions.messagesubjects + title.getTitleName(user.getTitleID()+1, genderID));
				}
			}
		}
		return false;
		
	}
}
