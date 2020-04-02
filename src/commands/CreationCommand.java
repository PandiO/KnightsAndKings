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

import DataManager.Creations;
import Exceptions.CommandExceptions;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Models.creations.Creation;
import Users.User;
import Users.Users;

/**
 * @author pandi
 *
 */
public class CreationCommand implements CommandExecutor{

	private Main main;
	/**
	 * 
	 */
	public CreationCommand(Main main) {
		this.main = main;
	}
	
	public static List<String> commandhelp = new ArrayList<String>(Arrays.asList(
			"",
			ColorOptions.statsformat + "List of Creation-commands",
			ColorOptions.stats + "-/creation list",
			ColorOptions.stats + "-/creation <creationID>",
			""
			));
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (label.equalsIgnoreCase("creation"))
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
			String message = args[0];
			
			if (Main.isInt(message))
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
				Integer creationID = Integer.valueOf(message);
				
				Creation creation = Creations.FindStashedCreation(creationID);
				
				if (creation != null)
				{
					creation.setUser(user);
					creation.nextStage(creation.getLastStage()+1);
				} else
				{
					player.sendMessage(ColorOptions.error + "No stashed creation could be found with ID " + creationID);
				}
			} else
			if (message.equalsIgnoreCase("list"))
			{
				sender.sendMessage("");
				for (Creation c : Creations.StashedCreations)
				{
					sender.sendMessage("");
					sender.sendMessage(ColorOptions.stats + "ID: " + ColorOptions.statsresults + c.getCreationID());
					sender.sendMessage(ColorOptions.stats + "Creation subject: " + ColorOptions.statsresults + c.getCreationType());
					sender.sendMessage(ColorOptions.stats + "User: " + ColorOptions.statsresults + c.getUser().getUsername());
					sender.sendMessage(ColorOptions.stats + "No. stages: " + ColorOptions.statsresults + c.getLastStage());
					sender.sendMessage(ColorOptions.stats + "Current stage: " + ColorOptions.statsresults + c.getStage());
					sender.sendMessage("");
				}
				sender.sendMessage("");
			}
		}
		return false;
	}

}
