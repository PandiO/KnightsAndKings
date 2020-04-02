package commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Exceptions.CommandExceptions;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Models.creations.DistrictCreation;
import Users.User;
import Users.Users;

public class DistrictCommand implements CommandExecutor
{
	public Main main;
	public DistrictCommand(Main main) {
		this.main = main;
	}
	
	public static List<String> commandhelp = new ArrayList<String>(Arrays.asList(
			"",
			ColorOptions.statsformat + "List of District-commands",
			ColorOptions.stats + "-/district create",
			ColorOptions.stats + "-/district remove",
			""
			));
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (label.equalsIgnoreCase("district"))
		{
			if (!sender.hasPermission(command.getPermission()))
			{
				sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command");
				return false;
			}
			if (args.length <= 0)
			{
				for (String msg : commandhelp)
				{
					sender.sendMessage(msg);
				}
				return false;
			}
			
			if (args[0].equalsIgnoreCase("create"))
			{
				Player player = null;
				UUID uuid = null;
				User user = null;
				Integer townID = null;
				
				if (!(sender instanceof Player))
				{
					sender.sendMessage(CommandExceptions.SenderNotPlayer);
					return false;
				}
				player = (Player) sender;
				uuid = player.getUniqueId();
				
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
				
				DistrictCreation creation = new DistrictCreation(user);
				
			} else if (args[0].equalsIgnoreCase("remove"))
			{
				if (args.length != 2)
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /district remove <id>");
					return false;
				}
				
				if (!Main.isInt(args[1]))
				{
					sender.sendMessage(ColorOptions.falsecommand + "The id must be a numeric value");
					return false;
				}
				Integer districtID = Integer.valueOf(args[1]);
				
				if (DataManager.Districts.ExistDistrict(districtID))
				{
					DataManager.Districts.RemoveDistrict(sender, districtID);
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "No district could be found with name/id " + args[1]);
				}
			}
		}
		
		return false;
	}
}
