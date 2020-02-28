package Treasure;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;

public class TreasureCommands implements CommandExecutor
{
	private Main main;
	public TreasureCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of treasure-commands",
			ColorOptions.stats + "-/treasure create <grade>",
			ColorOptions.stats + "-/treasure cancel",
			ColorOptions.stats + "-/treasure activate",
			ColorOptions.stats + "-/treasure deactivate",
			ColorOptions.stats + "-/treasure remove <ID>",
			ColorOptions.stats + "-/treasure info <ID>",
			ColorOptions.stats + "-/treasure list",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("treasure"))
		{
			if (sender.hasPermission("k&k.treasure"))
			{
				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("create"))
					{
						if (args.length == 2)
						{
							if (main.isInt(args[1]))
							{
								Integer grade = Integer.valueOf(args[1]);
								if (grade > 0 && grade < 6)
								{
									if (sender instanceof Player)
									{
										Player player = (Player) sender;
										UUID uuid = player.getUniqueId();
										Treasures.createTreasure.put(uuid, grade);
										player.sendMessage(ColorOptions.message + "Right-click a chest to create a treasure");
									} else
									{
										sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
									}
								} else
								{
									sender.sendMessage(ColorOptions.error + "" + grade + " is not a valid grade, only 1-5 are allowed!");
								}
							} else
							{
								sender.sendMessage(staffcommandhelp.get(2));
							}
						} else
						{
							sender.sendMessage(staffcommandhelp.get(2));
						}
					} else if (args[0].equalsIgnoreCase("cancel"))
					{
						if (sender instanceof Player)
						{
							Player player = (Player) sender;
							UUID uuid = player.getUniqueId();
							if (Treasures.createTreasure.containsKey(uuid))
							{
								Treasures.createTreasure.remove(uuid);
								player.sendMessage(ColorOptions.messageachievement + "Cancelled creation of a treasure!");
							} else
							{
								player.sendMessage(ColorOptions.error + "You didn't start the creation of a treasure!");
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
						}
					} else if (args[0].equalsIgnoreCase("activate"))
					{
						Treasures.instantiateAll(-1);
						sender.sendMessage(ColorOptions.messageachievement + "Succesfully activated all treasures!");
					} else if (args[0].equalsIgnoreCase("deactivate"))
					{
						Treasures.stopAll();
						sender.sendMessage(ColorOptions.messageachievement + "Succesfully de-activated all treasures!");
					} else if (args[0].equalsIgnoreCase("remove"))
					{
						if (args.length != 2)
						{
							
						}
						if (!main.isInt(args[1]))
						{
							
						}
						Integer treasureID = Integer.valueOf(args[1]);
						if (Treasures.findTreasure(treasureID) != null)
						{
							
						}
						
					}
					
					
					else
					{
						for (String msg : staffcommandhelp)
						{
							sender.sendMessage(msg);
						}
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
