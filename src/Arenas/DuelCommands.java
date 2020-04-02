package Arenas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import DataManager.Worldguard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

public class DuelCommands implements CommandExecutor
{
	WorldGuard worldguard = new WorldGuard();
	Main main = Main.getPlugin(Main.class);
	public DuelCommands(Main main) 
	{
		this.main = main;
		// TODO Auto-generated constructor stub
	}
	
	
	public static List<DuelInvite> inviteList = new ArrayList<DuelInvite>();
	
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of duel-commands",
			ColorOptions.stats + "-/duel <player>",
			ColorOptions.stats + "-/duel <cancel>",
			ColorOptions.stats + "-/duel <accept/deny>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("duel"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				User user = null;
				
				try
				{
					user = Users.getUser(player.getUniqueId());
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
				Location location = player.getLocation();
				RegionManager manager = Worldguard.getRegionManager(location.getWorld());
				Integer arenaID = Worldguard.getStructureIDbyRegion("arena", location, manager);
				if (args.length == 1)
				{
					if (args[0].equalsIgnoreCase("accept"))
					{
						for (DuelInvite invite : inviteList)
						{
							if (invite.target.getUUID() == player.getUniqueId())
							{
								invite.acceptInvite();
								return false;
							}
						}
						player.sendMessage(ColorOptions.error + "You don't have any duel invites!");
					} else if (args[0].equalsIgnoreCase("deny"))
					{
						for (DuelInvite invite : inviteList)
						{
							if (invite.target.getUUID() == player.getUniqueId())
							{
								invite.denyInvite();
								player.sendMessage(ColorOptions.messageformat + "You denied the invitation of " + ColorOptions.messagesubjects + invite.sender.getUsername());
								return false;
							}
						}
						player.sendMessage(ColorOptions.error + "You don't have any duel invites!");
					} else if (args[0].equalsIgnoreCase("cancel"))
					{
						for (DuelInvite invite : inviteList)
						{
							if (player.getUniqueId() == invite.sender.getUUID())
							{
								invite.cancelInvite();
								return false;
							}
						}
					} else if (Bukkit.getPlayer(args[0]) != null)
					{
						if (arenaID != null)
						{
							Player target = Bukkit.getPlayer(args[0]);
							if (target.getUniqueId() != player.getUniqueId())
							{
								User userTarget = null;
								
								try
								{
									userTarget = Users.getUser(target.getUniqueId());
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
								for (DuelInvite inv : inviteList)
								{
									if (inv.sender.getUUID() == player.getUniqueId() && target.getUniqueId() == inv.target.getUUID())
									{
										player.sendMessage(ColorOptions.error + "You already sent a duel invite to this player!");
										return false;
									}
								}
								new DuelInvite(user, userTarget, arenaID);
								player.sendMessage(ColorOptions.messageachievement + "Succesfully sent a duel invite to player " + ColorOptions.messagesubjects + target.getName());
							} else
							{
								player.sendMessage(ColorOptions.error + "You can't duel yourself!");
							}
						} else
						{
							player.sendMessage(ColorOptions.error + "You need to stand inside an arena to start a duel!");
						}
					} else if (Users.existUser(args[0]))
					{
						player.sendMessage(ColorOptions.error + "This player is currently not online!");
					} else
					{
						for (String msg : commandhelp)
						{
							player.sendMessage(msg);
						}
					}
				} else
				{
					for (String msg : commandhelp)
					{
						player.sendMessage(msg);
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
	
}
