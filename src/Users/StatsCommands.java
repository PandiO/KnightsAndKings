package Users;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;

public class StatsCommands implements CommandExecutor
{
	private Main main;
	public StatsCommands(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("stats") || label.equalsIgnoreCase("s"))
		{
			Player p = (Player) sender;
			if (args.length == 1)
			{
				UUID targetUUID = Users.fetchUUIDbyUsername(args[0]);
				if (targetUUID != null)
				{
					User target = new User(targetUUID);
					for (String msg : ColorOptions.getPlayerStats(target))
					{
						p.sendMessage(msg);
					}
					target.destroy();
				} else
				{
					p.sendMessage(ColorOptions.falsecommand + "This player does not exist");
				}
			} else
			{
				for (String msg : ColorOptions.getPlayerStats(Users.getUser(p.getUniqueId())))
				{
					p.sendMessage(msg);
				}
			}
		}
		return false;
		
	}
}

