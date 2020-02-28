package UsefulCommands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import Arenas.Arena;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;

public class EnderchestCommand implements CommandExecutor
{
	private Main main;	
	public EnderchestCommand(Main main) 
	{
		this.main = main;
	}
	
	public static HashMap<UUID, Player> offlineEnderchest = new HashMap<UUID, Player>();
		
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of enderchest-commands",
			ColorOptions.stats + "-/enderchest",
			ColorOptions.statsbrackets
	});
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of enderchest-commands",
			ColorOptions.stats + "-/enderchest <check> <player> to view a players enderchest",
			ColorOptions.stats + "-/enderchest <open> <player> to open an enderchest for a player",
			ColorOptions.stats + "-/enderchest to open your enderchest",
			ColorOptions.stats + "-/endercehst <help>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (label.equalsIgnoreCase("enderchest") || label.equalsIgnoreCase("ec"))
		{
			if (sender instanceof Player)
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
				if (player.hasPermission("k&k.enderchest.others"))
				{
					if (args.length >= 1)
					{
						String subCommand = args[0];
						if (subCommand.equalsIgnoreCase("check"))
						{
							if (args.length == 2)
							{
								String targetUsername = args[1];
								User userTarget = null;
								
								try
								{
									userTarget = Users.getUser(uuid);
								} catch (UserNotFoundException ex)
								{
									ErrorHandlers.userNotFoundAction(player, Bukkit.getPlayer(targetUsername), false);
									return false;
								} catch (Exception ex)
								{
									ex.printStackTrace();
									ErrorHandlers.userNotFoundAction(player, Bukkit.getPlayer(targetUsername), false);
									return false;
								}
								if (Bukkit.getPlayer(targetUsername) != null)
								{
									Player target = Bukkit.getPlayer(targetUsername);
									if (!target.hasPermission("k&k.owner"))
									{
										player.openInventory(target.getEnderChest());
										player.sendMessage(ColorOptions.messageformat + "You are now watching " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.messageformat + " enderchest");
									} else
									{
										player.sendMessage(ColorOptions.error + "You can't view the Enderchest of this player!");
									}
								} else
								{
							        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetUsername);
							        MinecraftServer minecraftserver = MinecraftServer.getServer();
							        GameProfile gameprofile = new GameProfile(offlineTarget.getUniqueId(), offlineTarget.getName());
							        EntityPlayer entity = new EntityPlayer(minecraftserver, minecraftserver.getWorldServer(0), gameprofile, new PlayerInteractManager(minecraftserver.getWorldServer(0)));
							        final Player target = entity == null ? null : entity.getBukkitEntity();
							        if (target != null)
							        {
							        	target.loadData();
							        	if (!target.hasPermission("k&k.owner"))
							        	{
								        	player.openInventory(target.getEnderChest());
											player.sendMessage(ColorOptions.messageformat + "You are now watching " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.messageformat + " enderchest");
											offlineEnderchest.put(uuid, target);
							        	} else
										{
											player.sendMessage(ColorOptions.error + "You can't view the Enderchest of this player!");
										}
							        }
								}
							} else
							{
								player.sendMessage(staffcommandhelp.get(2));
							}
						} else if (subCommand.equalsIgnoreCase("open"))
						{
							if (args.length == 2)
							{
								String targetUsername = args[1];
								User userTarget = null;
								
								try
								{
									userTarget = Users.getUser(uuid);
								} catch (Exception ex)
								{
									if (Users.existUser(targetUsername) == true)
									{
										player.sendMessage(ColorOptions.error + "This player is not online!");
									} else
									{
										player.sendMessage(ColorOptions.error + "No player could be found named " + targetUsername);
									}
									return false;
								}
								Player target = userTarget.getPlayer();
								target.openInventory(target.getEnderChest());
								player.sendMessage(ColorOptions.messageformat + "Opened " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.messageformat + " enderchest");
							} else
							{
								player.sendMessage(staffcommandhelp.get(3));
							}
						} else if (subCommand.equalsIgnoreCase("help"))
						{
							for (String msg : staffcommandhelp)
							{
								player.sendMessage(msg);
							}
						} else
						{
							for (String msg : staffcommandhelp)
							{
								player.sendMessage(msg);
							}
						}
					} else
					{
						player.openInventory(player.getEnderChest());
					}
				} else
				if (player.hasPermission("k&k.enderchest"))
				{
					Arena arena = new Arena();
					if (arena.isDuelling(uuid) == true)
					{
						player.sendMessage(ColorOptions.error + "You can't do this when in a duel!");
						return false;
					}
					player.openInventory(player.getEnderChest());
				} else
				{
					player.sendMessage(ColorOptions.error + "You don't have permission for this command!");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
}
