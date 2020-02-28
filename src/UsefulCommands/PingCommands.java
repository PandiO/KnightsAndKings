package UsefulCommands;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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

public class PingCommands implements CommandExecutor
{
	private Main main;	
	public PingCommands(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of ping-commands",
			ColorOptions.stats + "-/ping",
			ColorOptions.stats + "-/ping <player>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("ping"))
		{
			if (args.length == 0)
			{
				if (sender instanceof Player)
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
					Integer ping = user.getPing();
					player.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Your ping is " + ping + "ms");
					if (ping > 100)
					{
						player.sendMessage(ColorOptions.error + ColorOptions.messageArrow + "A ping above 100 might cause inconveniences");
					}
				} else
				{
					sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
				}
			} else if (args.length == 1)
			{
				String username = args[0];
				Player target = Bukkit.getPlayer(username);
				UUID targetUUID = target.getUniqueId();
				User userTarget = null;
				
				try
				{
					userTarget = Users.getUser(targetUUID);
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(((Player) sender), target, false);
					return false;
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(((Player) sender), target, false);
					return false;
				}
				if (target != null)
				{
					sender.sendMessage(ColorOptions.message + "The ping of player " + username + " is " + userTarget.getPing() + "ms");
				} else if (Users.existUser(username))
				{
					sender.sendMessage(ColorOptions.error + "This player is not online");
				} else
				{
					sender.sendMessage(ColorOptions.error + "No player could be found named " + username);
				}
			} else
			{
				for (String msg : commandhelp)
				{
					sender.sendMessage(msg);
				}
			}
		}
		return false;
	}
}
