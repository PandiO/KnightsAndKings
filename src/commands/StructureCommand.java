/**
 * 
 */
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
import Models.creations.StructureCreation;
import Users.User;
import Users.Users;

/**
 * @author pandi
 *
 */
public class StructureCommand implements CommandExecutor
{

	/**
	 * 
	 */
	public Main main;
	public StructureCommand(Main main) {
		this.main = main;
	}
	
	public static List<String> commandhelp = new ArrayList<String>(Arrays.asList(
			"",
			ColorOptions.statsformat + "List of Structure-commands",
			ColorOptions.stats + "-/structure create <structureType>",
			ColorOptions.stats + "-/structure remove",
			""
			));
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (label.equalsIgnoreCase("structure"))
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
				if (args.length != 2)
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /structure create <structureType>");
					return false;
				}
				String structureType = args[1].toLowerCase();
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
				
				StructureCreation creation = new StructureCreation(user, structureType);
			}
		}
		return false;
	}
}
