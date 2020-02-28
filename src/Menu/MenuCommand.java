package Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Arenas.Arena;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class MenuCommand implements CommandExecutor
{
	Menu menu = new Menu();
	private Main main;
	public MenuCommand(Main main) 
	{
		this.main = main;
	}
	
	public static Map<UUID, Inventory> pmenu = new HashMap<UUID, Inventory>();
	public static Map<UUID, Inventory> simenu = new HashMap<UUID, Inventory>();
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("menu"))
		{
			if (sender instanceof Player)
			{
				if (sender.hasPermission("k&k.menu"))
				{
					Player p = (Player) sender;
					UUID uuid = p.getUniqueId();
					User user = null;
					
					try
					{
						user = Users.getUser(uuid);
					} catch (UserNotFoundException ex)
					{
						ErrorHandlers.userNotFoundAction(null, p, true);
						return false;
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, p, true);
						return false;
					}
					Arena arena = new Arena();
					if (arena.isDuelling(uuid) == true)
					{
						p.sendMessage(ColorOptions.error + "You can't do this when in a duel!");
						return false;
					}
					String playername = p.getName();
					menu.OpenPersonalMenu(user);
					p.playSound(p.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
				} else
				{
					sender.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command");
				}
			}
		}
		return false;
		
	}
	
    public static ItemStack addpmenu(String displayname, Material type, String... lore) 
    {

        ItemStack itemStack = new ItemStack(type, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayname);
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    public static ItemStack addemptypmenu(String displayname, ItemStack item, String... lore) 
    {
    	
        ItemMeta itemMeta = item.getItemMeta();

        if (displayname != null)
        {
            itemMeta.setDisplayName(displayname);
        } else
        {
            itemMeta.setDisplayName(" ");
        }
        if (lore != null && !lore.equals(""))
        {
            itemMeta.setLore(Arrays.asList(lore));
        }

        item.setItemMeta(itemMeta);

        return item;
    }
}
