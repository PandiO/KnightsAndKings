package UsefulCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;

public class FlyMode implements CommandExecutor 
{

	private Main main;
	
	public FlyMode(Main main) 
	{
		this.main = main;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (cmd.getName().equalsIgnoreCase("fly")) 
		{
			Player p = (Player) sender;
			if (p.hasPermission("k&k.fly")) 
			{
				if (args.length > 0) 
				{ 
					if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("on")) 
					{
						if (args.length == 2)
						{
							for (Player target : Bukkit.getOnlinePlayers())
							{
								if (target.getName().equalsIgnoreCase(args[1]))
								{
									boolean allowFlight = target.getAllowFlight();
									if (allowFlight) 
									{
										p.sendMessage(ColorOptions.falsecommand + target.getName() + " is already in fly mode!");
									} else 
									{
										target.setAllowFlight(true);
										p.sendMessage(ColorOptions.messageformat + "Fly mode enabled for " + ColorOptions.messagesubjects + target.getName() + ColorOptions.messageformat + "!");
									}
								}
							}
						} else
						{
							boolean allowFlight = p.getAllowFlight();
							if (allowFlight) 
							{
								p.sendMessage(ColorOptions.falsecommand + "You are already in fly mode!");
							} else 
							{
								p.setAllowFlight(true);
								p.sendMessage(ColorOptions.messageformat + "Fly mode is enabled!");
							}
						}
					} else if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("off")) 
					{
						if (args.length == 2)
						{
							for (Player target : Bukkit.getOnlinePlayers())
							{
								if (target.getName().equalsIgnoreCase(args[1]))
								{
									if(!(p.getAllowFlight())) 
									{
										p.sendMessage(ColorOptions.falsecommand + "You are not in fly mode!");
									} else 
									{
										target.setAllowFlight(false);
										p.sendMessage(ColorOptions.messageformat + "Fly mode is disabled!");
									}
								}
							}
						} else
						{
							if(!(p.getAllowFlight())) 
							{
								p.sendMessage(ColorOptions.falsecommand + "You are not in fly mode!");
							} else 
							{
								p.setAllowFlight(false);
								p.sendMessage(ColorOptions.messageformat + "Fly mode is disabled!");
							}
						}
					} else 
					{
						p.sendMessage(ColorOptions.falsecommand + "Usage: /fly <enable/disable>");
					}
				} else
				{
					if (p.getAllowFlight() == false)
					{
						p.setAllowFlight(true);
						p.sendMessage(ColorOptions.messageformat + "Fly mode is enabled!");
					} else
					if (p.getAllowFlight() == true)
					{
						p.setAllowFlight(false);
						p.sendMessage(ColorOptions.messageformat + "Fly mode is disabled!");
					}
				}
				
				
			} else {
				p.sendMessage(ColorOptions.falsecommand + "You don't have the permission to execute this command!");
			}
			
			
		}
		return false;
	}

}
