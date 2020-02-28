package UsefulCommands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;

public class GameModeCommand implements CommandExecutor
{
	private Main main;
	public GameModeCommand(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("gamemode") || label.equalsIgnoreCase("gm"))
		{
			if (sender.hasPermission("k&k.gamemode"))
			{
				if (args.length >= 1)
				{
					if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c"))
					{
						if (args.length == 1)
						{
							Player p = (Player) sender;
							p.setGameMode(GameMode.CREATIVE);
							p.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "creative");
						} else if (args.length == 2)
						{
							if (sender.hasPermission("k&k.gamemode.others"))
							{
								for (Player target : Bukkit.getServer().getOnlinePlayers())
								{
									if (args[1].equalsIgnoreCase(target.getName()))
									{
										target.setGameMode(GameMode.CREATIVE);
										target.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "creative");
										sender.sendMessage(ColorOptions.messageachievement + "You have set the gamemode of "+ ColorOptions.messagesubjects + target.getName() + ColorOptions.messageachievement + " to " + ColorOptions.messagesubjects + "creative");
									}
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.falsecommand + "Usage: /gamemode <0/1/2> <player>");
						}
					} else if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s"))
					{
						if (args.length == 1)
						{
							Player p = (Player) sender;
							p.setGameMode(GameMode.SURVIVAL);
							p.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "survival");
						} else if (args.length == 2)
						{
							if (sender.hasPermission("k&k.gamemode.others"))
							{
								for (Player target : Bukkit.getServer().getOnlinePlayers())
								{
									if (args[1].equalsIgnoreCase(target.getName()))
									{
										target.setGameMode(GameMode.SURVIVAL);
										target.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "survival");
										sender.sendMessage(ColorOptions.messageachievement + "You have set the gamemode of "+ ColorOptions.messagesubjects + target.getName() + ColorOptions.messageachievement + " to " + ColorOptions.messagesubjects + "survival");
									}
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.falsecommand + "Usage: /gamemode <0/1/2> <player>");
						}
					} else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("sp"))
					{
						if (args.length == 1)
						{
							Player p = (Player) sender;
							p.setGameMode(GameMode.SPECTATOR);
							p.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "spectator");
						} else if (args.length == 2)
						{
							if (sender.hasPermission("k&k.gamemode.others"))
							{
								for (Player target : Bukkit.getServer().getOnlinePlayers())
								{
									if (args[1].equalsIgnoreCase(target.getName()))
									{
										target.setGameMode(GameMode.SPECTATOR);
										target.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "spectator");
										sender.sendMessage(ColorOptions.messageachievement + "You have set the gamemode of "+ ColorOptions.messagesubjects + target.getName() + ColorOptions.messageachievement + " to " + ColorOptions.messagesubjects + "spectator");
									}
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.falsecommand + "Usage: /gamemode <0/1/2> <player>");
						}
					} else
					{
						sender.sendMessage(ColorOptions.falsecommand + "Usage: /gamemode <0/1/2> <player>");
					}
				} else
				{
					Player p = (Player) sender;
					if (p.getGameMode() == GameMode.SURVIVAL)
					{
						p.setGameMode(GameMode.CREATIVE);
						p.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "creative");
					} else if (p.getGameMode() == GameMode.CREATIVE)
					{
						p.setGameMode(GameMode.SURVIVAL);
						p.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "survival");
					} else if (p.getGameMode() == GameMode.SPECTATOR)
					{
						p.setGameMode(GameMode.SURVIVAL);
						p.sendMessage(ColorOptions.messageachievement + "Your gamemode has been set to "+ ColorOptions.messagesubjects + "survival");
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
			}
		}
		
		return false;
		
	}
}
