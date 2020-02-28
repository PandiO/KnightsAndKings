package UsefulCommands;


import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Handlers.ColorOptions;
import Main.Main;
import Products.Product;

public class EnchantmentCommand implements CommandExecutor
{
	
	private Main main;
	public EnchantmentCommand(Main main)
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("enchant"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player) sender;
				if (p.hasPermission("k&k.enchant"))
				{
					if (args.length == 2)
					{
						if (p.getItemInHand() != null || p.getItemInHand().getType() != Material.AIR)
						{
							addEnchant(p, args[0], Integer.valueOf(args[1]));
						} else
						{
							p.sendMessage(ColorOptions.falsecommand + "You need to have an item in your hand!");
						}
					} else
					if (args.length == 1)
					{
						if (args[0].equalsIgnoreCase("all"))
						{
							for (Enchantment ench : Enchantment.values())
							{
								p.getItemInHand().addUnsafeEnchantment(ench, Integer.valueOf(10));
							}
						} else
						{
							p.sendMessage(ColorOptions.falsecommand + "Usage: /enchant <enchantment/all>");
						}
					} else
					{
						p.sendMessage(ColorOptions.falsecommand + "Usage: /enchant <enchantment/all>");
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
	
	public void addEnchant(Player player, String enchantment, Integer level)
	{
		Product product = new Product();
		ItemStack item = player.getItemInHand();
		if (product.getEnchantmentfromString(enchantment) != null)
		{
			item.addUnsafeEnchantment(product.getEnchantmentfromString(enchantment), level);
			player.sendMessage(ColorOptions.messageachievement + "Succesfully added enchantment " + ColorOptions.messagesubjects + product.getEnchantmentfromString(enchantment) + ColorOptions.messageachievement + " with level " + ColorOptions.messagesubjects + level);
		} else
		{
			player.sendMessage(ColorOptions.falsecommand + "Unknown enchantment: " + enchantment);
		}
	}
}
