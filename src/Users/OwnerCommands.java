package Users;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;

public class OwnerCommands implements CommandExecutor
{
	Scoreboards.Scoreboard scoreboard = new Scoreboards.Scoreboard();
	private Main main;
	public OwnerCommands(Main main)
	{
		this.main = main;
	}
	
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of owner-commands",
			ColorOptions.stats + "-/ownermode enable (will instantly enable ownermode)",
			ColorOptions.stats + "-/ownermode enable <onquit> (will enable when rejoining the server, no join message triggered)",
			ColorOptions.stats + "-/ownermode disable (will instantly disable ownermode)",
			ColorOptions.stats + "-/ownermode disable <onquit> (will disable when rejoining the server, no leave message triggered)",
			ColorOptions.statsbrackets
	});
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of staff-commands",
			ColorOptions.stats + "-/staffmode enable (will instantly enable staffmode)",
			ColorOptions.stats + "-/staffmode enable <onquit> (will enable when rejoining the server, no join message triggered)",
			ColorOptions.stats + "-/staffmode disable (will instantly disable staffmode)",
			ColorOptions.stats + "-/staffmode disable <onquit> (will disable when rejoining the server, no leave message triggered)",
			ColorOptions.statsbrackets
	});
	
	public static String enabled = ColorOptions.messageformat + "You " + ColorOptions.messageachievement + "enabled " + ColorOptions.messageformat + "owner mode!";
	public static String disabled = ColorOptions.messageformat + "You " + ColorOptions.falsecommand + "disabled " + ColorOptions.messageformat + "owner mode!";
	public static String senabled = ColorOptions.messageformat + "You " + ColorOptions.messageachievement + "enabled " + ColorOptions.messageformat + "staff mode!";
	public static String sdisabled = ColorOptions.messageformat + "You " + ColorOptions.falsecommand + "disabled " + ColorOptions.messageformat + "staff mode!";
	public static HashMap<UUID, Boolean> enableonquit = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, Boolean> disableonquit = new HashMap<UUID, Boolean>();
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("ownermode") || label.equalsIgnoreCase("om"))
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
			if (player.hasPermission("k&k.owner") || player.hasPermission("k&k.co-owner"))
			{
				if (args.length == 0)
				{
					if (user.inOwnerModus())
					{
						user.setOwnerMode(false);
					} else
					{
						user.setOwnerMode(true);
					}
					Users.updateScoreBoard(null);
				} else if (args.length >= 1)
				{
					if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable"))
					{
						if (args.length == 2)
						{
							if (args[1].equalsIgnoreCase("onquit") || args[1].equalsIgnoreCase("oq"))
							{
								enableonquit.put(uuid, true);
								player.sendMessage(ColorOptions.messageachievement + "Owner-modus will be enabled when you leave the server!");
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /ownermode <enable> <onquit>");
							}
						} else
						{
							user.setOwnerMode(true);
						}				
					} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable"))
					{
						if (args.length == 2)
						{
							if (args[1].equalsIgnoreCase("onquit") || args[1].equalsIgnoreCase("oq"))
							{
								disableonquit.put(uuid, true);
								player.sendMessage(ColorOptions.messageachievement + "Owner-modus will be disabled when you leave the server!");
							}
						} else
						{
							user.setOwnerMode(false);
						}
					} else if (args[0].equalsIgnoreCase("help"))
					{
						for (String msg : commandhelp)
						{
							player.sendMessage(msg);
						}
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "Usage: /ownermode <on/off> to toggle ownermode");
					}
					Users.updateScoreBoard(null);
				} else
				{
					player.sendMessage(ColorOptions.falsecommand + "Usage: /ownermode <on/off> to toggle ownermode");
				}
			} else
			{
				player.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
			}
		} else if (label.equalsIgnoreCase("staffmode") || label.equalsIgnoreCase("sm"))
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
			if (player.hasPermission("k&k.staff") && !player.hasPermission("k&k.co-owner"))
			{
				if (args.length == 0)
				{
					if (user.inStaffModus())
					{
						user.setStaffMode(false);
					} else
					{
						user.setStaffMode(true);
					}
					Users.updateScoreBoard(null);
				} else if (args.length >= 1)
				{
					if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable"))
					{
						if (args.length == 2)
						{
							if (args[1].equalsIgnoreCase("onquit") || args[1].equalsIgnoreCase("oq"))
							{
								enableonquit.put(uuid, true);
								player.sendMessage(ColorOptions.messageachievement + "Staff-modus will be enabled when you leave the server!");
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /staffmodus <enable> <onquit>");
							}
						} else
						{
							user.setStaffMode(true);
						}				
					} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable"))
					{
						if (args.length == 2)
						{
							if (args[1].equalsIgnoreCase("onquit") || args[1].equalsIgnoreCase("oq"))
							{
								disableonquit.put(uuid, true);
								player.sendMessage(ColorOptions.messageachievement + "Staff-modus will be disabled when you leave the server!");
							}
						} else
						{
							user.setStaffMode(false);
							
						}
					} else if (args[0].equalsIgnoreCase("help"))
					{
						for (String msg : staffcommandhelp)
						{
							player.sendMessage(msg);
						}
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "Usage: /staffmodus <on/off> to toggle staffmodus");
					}
					Users.updateScoreBoard(null);
				} else
				{
					player.sendMessage(ColorOptions.falsecommand + "Usage: /staffmodus <on/off> to toggle staffmodus");
				}
			}
		}
		return false;
		
	}
}
