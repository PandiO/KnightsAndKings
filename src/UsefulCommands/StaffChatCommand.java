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
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class StaffChatCommand implements CommandExecutor
{
	private Main main;
	public StaffChatCommand(Main main)
	{
		this.main = main;
	}
	
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of StaffChat-commands",
			ColorOptions.stats + "-/st <message>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("staffchat") || label.equalsIgnoreCase("sc") || label.equalsIgnoreCase("st"))
		{
			if (sender.hasPermission("k&k.staff"))
			{
				if (args.length > 0)
				{
					for (Player target : Bukkit.getOnlinePlayers())
					{
						if (target.hasPermission("k&k.staff"))
						{
							String prefix = null;
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
								prefix = user.getChatPrefix();
							} else
							{
								prefix = ColorOptions.message + "[" + ColorOptions.KAKColor + "CONSOLE" + ColorOptions.message + "]: ";
							}
							String msg = "";
							for (int i = 0; i < args.length; i++)
							{
								if (msg.length() <= 1)
								{
									msg = args[i];
								} else
								{
									msg = msg + " " + args[i];
								}
							}
							target.sendMessage(ColorOptions.message + "[" + ColorOptions.KAKColor + "StaffChat" + ColorOptions.message + "] " + prefix + msg);
				    		target.playSound(target.getLocation(), SoundHandler.NOTE_PLING, 0.5F, 1.0F);
						}
					}
				} else
				{
					sender.sendMessage(commandhelp.get(2));
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		return false;
	}
}
