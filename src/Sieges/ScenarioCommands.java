package Sieges;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import API_methods.WorldGuard;
import Exceptions.CommandExceptions;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class ScenarioCommands implements CommandExecutor
{
	private Main main;
	private WorldGuard worldguard = new WorldGuard();
	public ScenarioCommands(Main main)
	{
		this.main = main;
	}
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			"",
			ColorOptions.statsformat + "List of Scenario-commands",
			ColorOptions.stats + "-/Scenario create",
			ColorOptions.stats + "-/Scenario remove",
			ColorOptions.stats + "-/Scenario spawnpoint",
			ColorOptions.stats + "-/Scenario objective",
			ColorOptions.stats + "-/Scenario info",
			ColorOptions.stats + "-/Scenario list",
			""
	});
	
	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("scenario"))
		{
			if (args.length <= 0)
			{
				for (String msg : staffcommandhelp)
				{
					sender.sendMessage(msg);
				}
				return false;
			}
			
			if (args[0].equalsIgnoreCase("create"))
			{
				Player player = null;
				
				if (!(sender instanceof Player))
				{
					sender.sendMessage(CommandExceptions.SenderNotPlayer);
					return false;
				}
				player = (Player) sender;
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
				
				player.sendMessage(ColorOptions.message + "Starting scenario creation...");
				new ScenarioCreation(user);
			} else if (args[0].equalsIgnoreCase("remove"))
			{
				Player player = null;
				
				if (!(sender instanceof Player))
				{
					sender.sendMessage(CommandExceptions.SenderNotPlayer);
					return false;
				}
				player = (Player) sender;
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
				
				if (args.length != 2)
				{
					user.sendMessage(Arrays.asList(
							ColorOptions.error + "Usage:",
							ColorOptions.error + "/Scenario remove <id> OR",
							ColorOptions.error + "/Scenario remove <name>"
							));
					return false;
				}
				
				Scenario Scenario = null;
				Integer scenarioID = null;
				
				if (main.isInt(args[1]))
				{
					scenarioID = Integer.valueOf(args[1]);
				} else
				{
					Location location = player.getLocation();
					String scenarioName = args[1];
					Integer townID = this.worldguard.getStructureIDbyRegion("town", location, this.worldguard.getRegionManager(location.getWorld()));
					
					if (townID == null)
					{
						user.sendMessage(Arrays.asList(ColorOptions.error + "You need to stand inside the town of the scenario when removing by name"));
						return false;
					}
					scenarioID = Scenarios.getScenarioID(scenarioName, townID);
					
					if (scenarioID == null)
					{
						user.sendMessage(Arrays.asList(ColorOptions.error + "Can't find a Scenario with name " + scenarioName + " in a town with ID " + townID));
					}
				}
				
				if (scenarioID == null)
				{
					user.sendMessage(Arrays.asList(ColorOptions.error + "No Scenario ID found! Please try again or notify a developer"));
					return false;
				}
				Scenario = Scenarios.instantiateScenario(scenarioID, false);
				
				if (Scenario == null)
				{
					user.sendMessage(Arrays.asList(ColorOptions.error + "No Scenario could be found! Please try again or notify a developer"));
					return false;
				}
				
				Scenario.removePermanently(player);
			}
		}
		return false;
	}
}
