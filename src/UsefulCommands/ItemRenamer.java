package UsefulCommands;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Handlers.ColorOptions;
import Main.Main;

public class ItemRenamer implements CommandExecutor
{
	private Main main;
	public ItemRenamer(Main main)
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("rename") && sender.hasPermission("k&k.rename"))
		{
			Player p = (Player) sender;
			if (p.hasPermission("k&k.rename"))
			{
				if (args.length > 0)
				{
					if (p.getItemInHand() != null)
					{
						
				        StringBuilder sb = new StringBuilder();
				        for (int i = 0; i < args.length; i++) {
				          sb.append(args[i]).append(" ");
				        }
				        String allArgs = sb.toString().trim();
						ItemStack item = p.getItemInHand();
						p.setItemInHand(renameitem(item, getColor(args[0]) + allArgs.replace("&", "§")));
						//Bukkit.getServer().getPluginManager().callEvent(new Buyarmor(p, item, p.getInventory().getHeldItemSlot(), allArgs));
					} else
					{
						p.sendMessage(ColorOptions.falsecommand + "You need to have an item in your hand!");
					}
				} else
				{
					p.sendMessage(ColorOptions.falsecommand + "Usage: /rename <itemname>. Color codes can be used with '&'");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		return false;
		
	}
	
	public ChatColor getColor(String color)
	{
		if (color.equalsIgnoreCase("&0"))
		{
			return ChatColor.BLACK;
		} else
		if (color.equalsIgnoreCase("&1"))
		{
			return ChatColor.DARK_BLUE;
		} else
		if (color.equalsIgnoreCase("&2"))
		{
			return ChatColor.DARK_GREEN;
		} else
		if (color.equalsIgnoreCase("&3"))
		{
			return ChatColor.DARK_AQUA;
		} else
		if (color.equalsIgnoreCase("&4"))
		{
			return ChatColor.DARK_RED;
		} else
		if (color.equalsIgnoreCase("&5"))
		{
			return ChatColor.DARK_PURPLE;
		} else
		if (color.equalsIgnoreCase("&6"))
		{
			return ChatColor.GOLD;
		} else
		if (color.equalsIgnoreCase("&7"))
		{
			return ChatColor.GRAY;
		} else
		if (color.equalsIgnoreCase("&8"))
		{
			return ChatColor.DARK_GRAY;
		} else
		if (color.equalsIgnoreCase("&9"))
		{
			return ChatColor.BLUE;
		} else
		if (color.equalsIgnoreCase("&a"))
		{
			return ChatColor.GREEN;
		} else
		if (color.equalsIgnoreCase("&b"))
		{
			return ChatColor.AQUA;
		} else
		if (color.equalsIgnoreCase("&c"))
		{
			return ChatColor.RED;
		} else
		if (color.equalsIgnoreCase("&d"))
		{
			return ChatColor.LIGHT_PURPLE;
		} else
		if (color.equalsIgnoreCase("&e"))
		{
			return ChatColor.YELLOW;
		} else
		{
			return ChatColor.WHITE;
		}
	}
	
    public static ItemStack renameitem(ItemStack item, String displayname) 
    {   
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);

        item.setItemMeta(itemMeta);

        return item;
    }
    public static ItemStack renameshopitem(ItemStack item, String displayname, Set<ItemFlag> itemFlags, String... lore) 
    {
        ItemMeta itemMeta = item.getItemMeta();
        for (ItemFlag f : itemFlags)
        {
        	itemMeta.addItemFlags(f);
        }
        itemMeta.setDisplayName(displayname);
        itemMeta.setLore(Arrays.asList(lore));

        item.setItemMeta(itemMeta);

        return item;
    }
}
