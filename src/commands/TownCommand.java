package commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import DataManager.Towns;
import Exceptions.CommandExceptions;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Models.creations.TownCreation;
import Users.User;
import Users.Users;

public class TownCommand implements CommandExecutor
{

	public Main main;
	public TownCommand(Main main) {
		this.main = main;
	}
	
	public static List<String> commandhelp = new ArrayList<String>(Arrays.asList(
			"",
			ColorOptions.statsformat + "List of Town-commands",
			ColorOptions.stats + "-/town create",
			ColorOptions.stats + "-/town remove",
			ColorOptions.stats + "-/town purge",
			""
			));
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (label.equalsIgnoreCase("town2"))
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
				Integer townID = null;
				User user = null;
				
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
				
				TownCreation creation = new TownCreation(user);
			} else
			if (args[0].equalsIgnoreCase("remove"))
			{
				if (args.length != 2)
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /town remove <name/id>");
					return false;
				}
				
				Integer townID = null;
				if (!Main.isInt(args[1]))
				{
					String townName = args[1];
					
					townID = DataManager.Towns.FetchTownID(townName);
				} else
				{
					townID = Integer.valueOf(args[1]);
				}
				
				if (DataManager.Towns.ExistTown(townID))
				{
					DataManager.Towns.RemoveTown(sender, townID);
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "No town could be found with name/id " + args[1]);
				}
			} else
			if (args[0].equalsIgnoreCase("purge"))
			{
				for (Integer townID : Towns.GetIDList())
				{
					sender.sendMessage(ColorOptions.message + "Purging town " + townID);
					Towns.PurgeTown(townID);
				}
				sender.sendMessage(ColorOptions.messageachievement + "Purged all towns!");
			}
		}
		return false;
	}

}
