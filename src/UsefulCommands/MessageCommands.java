package UsefulCommands;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Users.Users;

public class MessageCommands implements CommandExecutor
{
	private Main main;	
	public MessageCommands(Main main) 
	{
		this.main = main;
	}
		
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of message-commands",
			ColorOptions.stats + "-/message <player> <message>",
			ColorOptions.stats + "-/reply <message>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (label.equalsIgnoreCase("message") || label.equalsIgnoreCase("msg"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				if (args.length >= 2)
				{
					String targetUsername = args[0];
					if (!targetUsername.equalsIgnoreCase(player.getName()))
					{
						if (Bukkit.getPlayer(targetUsername) != null)
						{
							String message = "";
							for (int i = 1; i < args.length; i++)
							{
								if (message.length() < 1)
								{
									message = args[i];
								} else
								{
									message = message + " " + args[i];
								}
							}
							Player target = Bukkit.getPlayer(targetUsername);
							UUID senderUUID = player.getUniqueId();
							UUID targetUUID = target.getUniqueId();
							main.msgReceived.put(targetUUID, player.getName());
							main.msgReceived.put(player.getUniqueId(), targetUsername);
							sender.sendMessage(ColorOptions.message + "[" + ChatColor.BLUE + "me -> " + target.getName() + ColorOptions.message + "] " + message);
							target.sendMessage(ColorOptions.message + "[" + ChatColor.BLUE + sender.getName() + " -> me" + ColorOptions.message + "] " + message);
				    		target.playSound(target.getLocation(), SoundHandler.NOTE_PLING, 1.0F, 1.0F);
							
							socialSpy(player, target, message);
						} else
						{
							if (Users.existUser(targetUsername))
							{
								sender.sendMessage(ColorOptions.error + "This player is not online!");
							} else
							{
								sender.sendMessage(ColorOptions.error + "No player could be found named " + targetUsername);
							}
						}
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "You can't message yourself! Pretty weird that you tried tho..");
					}
				} else
				{
					sender.sendMessage(commandhelp.get(2));
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
			}
		}
		if (label.equalsIgnoreCase("reply") || label.equalsIgnoreCase("r"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				UUID uuid = player.getUniqueId();
				if (main.msgReceived.containsKey(uuid))
				{
					if (args.length >= 1)
					{
						String targetUsername = main.msgReceived.get(uuid);
						if (Bukkit.getPlayer(targetUsername) != null)
						{
							String message = "";
							for (int i = 0; i < args.length; i++)
							{
								message = message + " " + args[i];
							}
							Player target = Bukkit.getPlayer(targetUsername);
							UUID targetUUID = target.getUniqueId();
							sender.sendMessage(ColorOptions.message + "[" + ChatColor.BLUE + "me -> " + target.getName() + ColorOptions.message + "] " + message);
							target.sendMessage(ColorOptions.message + "[" + ChatColor.BLUE + sender.getName() + " -> me" + ColorOptions.message + "] " + message);
				    		target.playSound(target.getLocation(), SoundHandler.NOTE_PLING, 1.0F, 1.0F);
							main.msgReceived.put(targetUUID, player.getName());
							
							socialSpy(player, target, message);
						} else
						{
							sender.sendMessage(ColorOptions.error + "This player is not online anymore!");
						}
					} else
					{
						player.sendMessage(commandhelp.get(3));
					}
				} else
				{
					player.sendMessage(ColorOptions.error + "You have nobody to reply to!");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
	
	public void socialSpy(Player sender, Player target, String message)
	{
		UUID senderUUID = sender.getUniqueId();
		UUID targetUUID = target.getUniqueId();
		for (Player staff : Bukkit.getOnlinePlayers())
		{
			UUID uuid = staff.getUniqueId();
			if (staff.hasPermission("k&k.staff") && !staff.hasPermission("k&k.owner"))
			{
				if (!sender.hasPermission("k&k.owner") && uuid != senderUUID && uuid != targetUUID)
				{
					staff.sendMessage(ColorOptions.message + "[" + ChatColor.BLUE + sender.getName() + " -> " + target.getName() + ColorOptions.message + "] " + message);
				}
			} else
			if (main.ownermodus.containsKey(uuid) && main.ownermodus.get(uuid) == true)
			{
				if (uuid != senderUUID && targetUUID != uuid)
				{
					staff.sendMessage(ColorOptions.message + "[" + ChatColor.BLUE + sender.getName() + " -> " + target.getName() + ColorOptions.message + "] " + message);
				}
			}
		}
	}
}
