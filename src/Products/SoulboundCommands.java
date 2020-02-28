package Products;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Handlers.ColorOptions;
import Main.Main;

public class SoulboundCommands implements CommandExecutor
{
	Product product = new Product();
	private Main main;
	public SoulboundCommands(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> soulboundhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of soulbound-commands",
			ColorOptions.stats + "-/soulbound <add>",
			ColorOptions.stats + "-/soulbound <remove>",
			ColorOptions.statsformat + "================================================="
	});
	
	public static List<String> ghostedhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of ghosted-commands",
			ColorOptions.stats + "-/ghosted <add>",
			ColorOptions.stats + "-/ghosted <remove>",
			ColorOptions.statsformat + "================================================="
	});
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("soulbound"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				if (p.hasPermission("k&k.soulbound"))
				{
					if (p.getItemInHand() != null || p.getItemInHand().getType() != Material.AIR)
					{
						ItemStack item = p.getItemInHand();
						if (args.length == 1)
						{
							if (args[0].equalsIgnoreCase("add"))
							{
								if (p.getItemInHand().hasItemMeta() == true)
								{
									ItemMeta meta = item.getItemMeta();
									if (meta.getLore() != null)
									{
										if (!meta.getLore().contains(ChatColor.RED + "Soulbound") && !meta.getLore().contains(ChatColor.GRAY + "Ghosted"))
										{
											p.setItemInHand(product.SoulboundItem(item));
											p.sendMessage(ColorOptions.messageachievement + "Succesfully soulbound the item in your hand!");
										} else
										{
											p.sendMessage(ColorOptions.falsecommand + "This item is already soulbound/ghosted!");
										}
									} else
									{
										p.setItemInHand(product.SoulboundItem(item));
										p.sendMessage(ColorOptions.messageachievement + "Succesfully soulbound the item in your hand!");
									}
								} else
								{
									p.setItemInHand(product.SoulboundItem(item));
									p.sendMessage(ColorOptions.messageachievement + "Succesfully soulbound the item in your hand!");
								}
							} else
							if (args[0].equalsIgnoreCase("remove"))
							{
								if (item.hasItemMeta() == true)
								{
									ItemMeta meta = item.getItemMeta();
									if (meta.hasLore() == true)
									{
										if (meta.getLore().contains(ChatColor.RED + "Soulbound"))
										{
											List<String> lore = meta.getLore();
											lore.remove(ChatColor.RED + "Soulbound");
											meta.setLore(lore);
											item.setItemMeta(meta);
											p.updateInventory();
											p.sendMessage(ColorOptions.messageachievement + "Succesfully un-soulbound the item in your hand!");
										} else
										{
											p.sendMessage(ColorOptions.falsecommand + "This item isn't soulbound!");
										}
									} else
									{
										p.sendMessage(ColorOptions.falsecommand + "This item isn't soulbound");
									}
								} else
								{
									p.sendMessage(ColorOptions.falsecommand + "This item isn't soulbound");
								}
							} else
							{
								for (String s : soulboundhelp)
								{
									p.sendMessage(s);
								}
							}
						} else
						{
							for (String s : soulboundhelp)
							{
								p.sendMessage(s);
							}
						}
					} else
					{
						p.sendMessage(ColorOptions.falsecommand + "You need to have an item in your hand!");
					}
				} else
				{
					p.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
			}
		}
		if (label.equalsIgnoreCase("ghosted"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				if (p.hasPermission("k&k.ghosted"))
				{
					if (p.getItemInHand() != null || p.getItemInHand().getType() != Material.AIR)
					{
						ItemStack item = p.getItemInHand();
						if (args.length == 1)
						{
							if (args[0].equalsIgnoreCase("add"))
							{
								if (p.getItemInHand().hasItemMeta() == true)
								{
									ItemMeta meta = item.getItemMeta();
									if (meta.getLore() != null)
									{
										if (!meta.getLore().contains(ChatColor.GRAY + "Ghosted") && !meta.getLore().contains(ChatColor.RED + "Soulbound"))
										{
											p.setItemInHand(product.GhostItem(item));
											p.sendMessage(ColorOptions.messageachievement + "Succesfully ghosted the item in your hand!");
										} else
										{
											p.sendMessage(ColorOptions.falsecommand + "This item is already ghosted/soulbound!");
										}
									} else
									{
										p.setItemInHand(product.GhostItem(item));
										p.sendMessage(ColorOptions.messageachievement + "Succesfully ghosted the item in your hand!");
									}
								} else
								{
									p.setItemInHand(product.GhostItem(item));
									p.sendMessage(ColorOptions.messageachievement + "Succesfully soulbound the item in your hand!");
								}
							} else
							if (args[0].equalsIgnoreCase("remove"))
							{
								if (item.hasItemMeta() == true)
								{
									ItemMeta meta = item.getItemMeta();
									if (meta.hasLore() == true)
									{
										if (meta.getLore().contains(ChatColor.DARK_GRAY + "Ghosted"))
										{
											List<String> lore = meta.getLore();
											lore.remove(ChatColor.DARK_GRAY + "Ghosted");
											meta.setLore(lore);
											item.setItemMeta(meta);
											p.updateInventory();
											p.sendMessage(ColorOptions.messageachievement + "Succesfully un-ghosted the item in your hand!");
										} else
										{
											p.sendMessage(ColorOptions.falsecommand + "This item isn't ghosted!");
										}
									} else
									{
										p.sendMessage(ColorOptions.falsecommand + "This item isn't ghosted");
									}
								} else
								{
									p.sendMessage(ColorOptions.falsecommand + "This item isn't ghosted");
								}
							} else
							{
								for (String s : ghostedhelp)
								{
									p.sendMessage(s);
								}
							}
						} else
						{
							for (String s : ghostedhelp)
							{
								p.sendMessage(s);
							}
						}
					} else
					{
						p.sendMessage(ColorOptions.falsecommand + "You need to have an item in your hand!");
					}
				} else
				{
					p.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
}
