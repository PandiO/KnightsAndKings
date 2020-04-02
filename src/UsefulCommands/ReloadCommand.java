package UsefulCommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import Handlers.ColorOptions;
import Main.Main;

public class ReloadCommand implements CommandExecutor, Listener
{
	public Main main;
	public ReloadCommand(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of reload-commands",
			ColorOptions.stats + "-/k&k reload",
			ColorOptions.statsbrackets
	});
	
//	@EventHandler
//	public void onReloadCommand(PlayerCommandPreprocessEvent e)
//	{
//		if (e.getMessage().equalsIgnoreCase("/reload") || e.getMessage().equalsIgnoreCase("/rl") || e.getMessage().equalsIgnoreCase("/rel"))
//		{
//			Player player = e.getPlayer();
//			if (player.hasPermission("bukkit.reload") || player.hasPermission("k&k.reload") || player.isOp())
//			{
//				e.setCancelled(true);
//				main.reload(player);
//			}
//		}
//	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("knightsandkings") || label.equalsIgnoreCase("k&k"))
		{
			if (sender.hasPermission("k&k.reload"))		
			{
				if (args.length == 1)
				{
					if (args[0].equalsIgnoreCase("reload"))
					{
						main.reload(sender);
					} else
					{
						sender.sendMessage(staffcommandhelp.get(2));
					}
				} else
				{
					for (String msg : staffcommandhelp)
					{
						sender.sendMessage(msg);
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		return false;
	}
}
