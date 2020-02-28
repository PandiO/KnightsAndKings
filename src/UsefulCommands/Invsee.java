package UsefulCommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.mojang.authlib.GameProfile;

import Handlers.ColorOptions;
import Main.Main;
import Users.Users;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;

public class Invsee implements CommandExecutor
{
	private Main main;
	public Invsee(Main main)
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("invsee") || label.equalsIgnoreCase("is"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				if (player.hasPermission("k&k.inventory.*"))
				{
					if (args.length == 1)
					{
						String targetUsername = args[0];
						if (Bukkit.getPlayer(args[0]) != null)
						{
							Player target = Bukkit.getPlayer(args[0]);
							if (!target.hasPermission("k&k.owner") || sender.hasPermission("k&k.owner"))
							{
								Inventory inv = target.getInventory();
								player.openInventory(inv);
								player.sendMessage(ColorOptions.messageformat + "You are now looking into the inventory of " + ColorOptions.messagesubjects + target.getName());
							} else
							{
								player.sendMessage(ColorOptions.error + "You can't open the inventory of this player!");
							}
						} else if (Users.existUser(args[0]))
						{
							OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetUsername);
					        MinecraftServer minecraftserver = MinecraftServer.getServer();
					        GameProfile gameprofile = new GameProfile(offlineTarget.getUniqueId(), offlineTarget.getName());
					        EntityPlayer entity = new EntityPlayer(minecraftserver, minecraftserver.getWorldServer(0), gameprofile, new PlayerInteractManager(minecraftserver.getWorldServer(0)));
					        final Player target = entity == null ? null : entity.getBukkitEntity();
					        if (target != null)
					        {
					        	target.loadData();
					        	if (!target.hasPermission("k&k.owner") || sender.hasPermission("k&k.owner"))
					        	{
						        	player.openInventory(target.getInventory());
									player.sendMessage(ColorOptions.messageformat + "You are now watching " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.messageformat + " inventory");
									EnderchestCommand.offlineEnderchest.put(player.getUniqueId(), target);
					        	} else
								{
									player.sendMessage(ColorOptions.error + "You can't view the Inventory of this player!");
								}
					        } else
					        {
					        	player.sendMessage(ColorOptions.error + "Couldn't find target's data!");
					        }
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Can't find player!");
						}
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "Usage: /invsee <player> to see someone's inventory");
					}
				} else
				{
					player.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command");
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You need to be on a pc-version of minecraft to perform this command");
			}
		}
		if (label.equalsIgnoreCase("inventory"))
		{
			if (sender.hasPermission("k&k.inventory.*"))
			{
				if (args.length > 1)
				{
					String targetUsername = args[1];
					if (Bukkit.getPlayer(args[1]) != null)
					{
						Player target = Bukkit.getPlayer(args[1]);
						if (args[0].equalsIgnoreCase("clear"))
						{
							if (!target.hasPermission("k&k.owner") || sender.hasPermission("k&k.owner"))
							{
								Inventory inv = target.getInventory();
								inv.clear();
								sender.sendMessage(ColorOptions.messageachievement + "You cleared the inventory of " + ColorOptions.messagesubjects + target.getName());
							}
						} else
						{
							sender.sendMessage(ColorOptions.falsecommand + "Usage: /inventory <clear> <player> to clear a player's inventory");
						}
					} else if (Users.existUser(args[1]))
					{
						OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetUsername);
				        MinecraftServer minecraftserver = MinecraftServer.getServer();
				        GameProfile gameprofile = new GameProfile(offlineTarget.getUniqueId(), offlineTarget.getName());
				        EntityPlayer entity = new EntityPlayer(minecraftserver, minecraftserver.getWorldServer(0), gameprofile, new PlayerInteractManager(minecraftserver.getWorldServer(0)));
				        final Player target = entity == null ? null : entity.getBukkitEntity();
				        if (target != null)
				        {
				        	target.loadData();
				        	if (!target.hasPermission("k&k.owner") || sender.hasPermission("k&k.owner"))
				        	{
								Inventory inv = target.getInventory();
								inv.clear();
								sender.sendMessage(ColorOptions.messageachievement + "You cleared the inventory of " + ColorOptions.messagesubjects + target.getName());
				        	} else
							{
								sender.sendMessage(ColorOptions.error + "You can't view the Enderchest of this player!");
							}
				        }
					}
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /inventory <clear> <player> to clear a player's inventory");
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
			}
		}
		return false;
	}
}
