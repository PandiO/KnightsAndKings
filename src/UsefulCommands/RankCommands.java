package UsefulCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;

public class RankCommands implements CommandExecutor
{
	private Main main;
	public RankCommands(Main main)
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("default"))
		{
			if (args.length > 0)
			{
				if (sender.hasPermission("k&k.ranks.modify"))
				{
					if (args[0].equalsIgnoreCase("set"))
					{
						if (args.length == 2)
						{
							if (Bukkit.getServer().getPlayer(args[1]) != null)
							{
								Player target = Bukkit.getServer().getPlayer(args[1]);
								Bukkit.dispatchCommand(sender, "pex group Owner user remove " + target.getName());
								Bukkit.dispatchCommand(sender, "pex group Builder user remove " + target.getName());
								Bukkit.dispatchCommand(sender, "pex group Staff user remove " + target.getName());
								Bukkit.dispatchCommand(sender, "pex group Default user add " + target.getName());
								sender.sendMessage(ColorOptions.messageachievement + "Added player " + ColorOptions.messagesubjects + target.getName() + ColorOptions.messageachievement + " to the Default rank!");
							} else
							{
								sender.sendMessage(ColorOptions.falsecommand + "Can't find player " + args[1]);
							}
						} else if (args.length > 2)
						{
							for (int i = 1; i < (args.length +1); i++)
							{
								if (Bukkit.getServer().getPlayer(args[i]) != null)
								{
									Player target = Bukkit.getServer().getPlayer(args[i]);
									Bukkit.dispatchCommand(sender, "pex group Owner user remove " + target.getName());
									Bukkit.dispatchCommand(sender, "pex group Builder user remove " + target.getName());
									Bukkit.dispatchCommand(sender, "pex group Staff user remove " + target.getName());
									Bukkit.dispatchCommand(sender, "pex group Default user add " + target.getName());
									sender.sendMessage(ColorOptions.messageachievement + "Added player " + ColorOptions.messagesubjects + target.getName() + ColorOptions.messageachievement + " to the Default rank!");
								} else
								{
									sender.sendMessage(ColorOptions.falsecommand + "Can't find player " + args[i]);
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.falsecommand + "Usage: /default set <player> <player> etc.");
						}
					}
				}
			}
		}
		return false;
	}
}
