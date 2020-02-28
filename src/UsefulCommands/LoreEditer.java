package UsefulCommands;

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

public class LoreEditer implements CommandExecutor
{
	private Main main;
	public LoreEditer(Main main)
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("lore"))
		{
			Player p = (Player) sender;
			if (p.hasPermission("k&k.lore"))
			{
				if (args.length >= 1)
				{
					if (args[0].equalsIgnoreCase("add"))
					{
						if (args.length > 1)
						{
							if (p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR)
							{
								ItemStack item = p.getItemInHand();
								if (item.hasItemMeta())
								{
									ItemMeta meta = item.getItemMeta();
							        StringBuilder sb = new StringBuilder();
							        for (int i = 1; i < args.length; i++) {
							          sb.append(args[i]).append(" ");
							        }
							        String allArgs = sb.toString().trim();
									List<String> lore = null;
									if (meta.getLore() == null)
									{
										lore = Arrays.asList(allArgs.replace("&", "§"));
									} else
									{
										lore = meta.getLore();
										boolean soulbound = false;
										boolean ghosted = false;
										if (lore.size() > 1)
										{
											for (int i = 0; i < lore.size(); i++)
											{
												if (ChatColor.stripColor(lore.get(i)).contains("Soulbound"))
												{
													lore.remove(lore.indexOf(lore.get(i)));
													soulbound = true;
												} else if (ChatColor.stripColor(lore.get(i)).contains("Ghosted"))
												{
													lore.remove(lore.indexOf(lore.get(i)));
													ghosted = true;
												}
											}
										} else
										{
											if (ChatColor.stripColor(lore.get(0)).equalsIgnoreCase("Soulbound"))
											{
												lore.remove(0);
												soulbound = true;
											} else
											if (ChatColor.stripColor(lore.get(0)).equalsIgnoreCase("Ghosted"))
											{
												lore.remove(0);
												ghosted = true;
											}
										}
										lore.add(allArgs.replace("&", "§"));
										if (soulbound == true)
										{
											lore.add(ChatColor.RED + "Soulbound");
										}
										if (ghosted == true)
										{
											lore.add(ChatColor.DARK_GRAY + "Ghosted");
										}
									}
									meta.setLore(lore);
									item.setItemMeta(meta);
									p.sendMessage(ColorOptions.messageachievement + "Added a line to the lore!");
								} else
								{
									ItemMeta meta = item.getItemMeta();
							        StringBuilder sb = new StringBuilder();
							        for (int i = 1; i < args.length; i++) {
							          sb.append(args[i]).append(" ");
							        }
							        String allArgs = sb.toString().trim();
									List<String> lore = Arrays.asList(allArgs.replace("&", "§"));
									meta.setLore(lore);
									item.setItemMeta(meta);
									p.sendMessage(ColorOptions.messageachievement + "Added a line to the lore!");
								}
							} else
							{
								p.sendMessage(ColorOptions.falsecommand + "You need to have an item in your hand!");
							}
						} else
						{
							p.sendMessage(ColorOptions.falsecommand + "Usage: /lore add <lore>");
						}
					} else
					if (args[0].equalsIgnoreCase("clear"))
					{
						if (p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR)
						{
							ItemStack item = p.getItemInHand();
							if (item.hasItemMeta())
							{
								ItemMeta meta = item.getItemMeta();
								List<String> lore = meta.getLore();
								if (lore.size() > 1)
								{
									for (int i = 0; i < lore.size(); i++)
									{
										if (ChatColor.stripColor(lore.get(i)).contains("Soulbound") || ChatColor.stripColor(lore.get(i)).contains("Ghosted"))
										{
											
										} else
										{
											lore.remove(lore.indexOf(lore.get(i)));
										}
									}
								} else
								{
									if (!lore.get(0).contains("soulbound") || !lore.get(0).contains("ghosted"))
									{
										lore.clear();
									}
								}
								meta.setLore(lore);
								item.setItemMeta(meta);
								p.sendMessage(ColorOptions.messageachievement + "Succesfully removed the lore of this item!");
							} else
							{
								p.sendMessage(ColorOptions.error + "This item doesn't have a lore!");
							}
						} else
						{
							p.sendMessage(ColorOptions.error + "You need to have an item in your hand!");
						}
					} else
					{
						p.sendMessage(ColorOptions.falsecommand + "Usage: /lore <add/clear>");
					}
				} else
				{
					p.sendMessage(ColorOptions.falsecommand + "Usage: /lore <add/clear>");
				}
			} else
			{
				p.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
			}
		}
		return false;
	}
}
