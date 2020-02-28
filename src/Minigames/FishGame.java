package Minigames;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.ExperienceChangeEvent;
import Handlers.SoundHandler;
import Main.Main;
import Products.Product;
import Titles.Title;
import Users.User;
import Users.Users;

public class FishGame implements Listener
{
	Title title = new Title();
	Product product = new Product();
	private Main main;
	public FishGame(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onCatch(PlayerFishEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}
		if (user.getTitleID() >= 3)
		{
			if (player.getItemInHand().hasItemMeta())
			{
				Integer productID = product.getProductIDbyDisplayName(player.getItemInHand().getItemMeta().getDisplayName(), false);
				if (productID != null)
				{
					Item item = (Item)e.getCaught();
			        if (e.getState() == State.CAUGHT_FISH)
			        {
			        	if (item.getItemStack().getType() == Material.RAW_FISH)
			        	{
			        		item.setItemStack(product.createPropertyItem(product.getProductID("rawfish", false), item.getItemStack().getAmount(), false, false));
			        		user.addCatchedFish(true, 1);
							user.addExperience(Integer.valueOf(1), true);
					        Bukkit.getServer().getPluginManager().callEvent(new ExperienceChangeEvent(user, Integer.valueOf(1), player));
					        player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 1.0F);
					        player.sendMessage(ColorOptions.messageformat + "► You caught something! You received " + ColorOptions.messagesubjects + "1 experience");
				        	player.sendMessage(ColorOptions.messageformat + "► You can sell 16 fish to a Fishery for " + ColorOptions.messagesubjects + "8500 coins");
			        	} else
			        	{
			        		if (main.getRandom(0, 1000) <= 1)
			        		{
			        			user.addSkillPoints(false, Integer.valueOf(1));
			        			player.sendMessage(ColorOptions.messageachievement + "► You received a skill-point!");
			        		}
			        		if (main.getRandom(0, 1000) <= 100)
			        		{
			        			Integer amount = main.getRandom(0, 4);
			        			item.setItemStack(new ItemStack(Material.GOLD_NUGGET, amount));
			        			player.sendMessage(ColorOptions.messageachievement + "► You received " + ColorOptions.messagesubjects + amount + " gold nuggets!");
			        		}
			        		if (main.getRandom(0, 1000) <= 1)
			        		{
			        			Integer amount = main.getRandom(8, 24);
			        			item.setItemStack(new ItemStack(Material.GOLD_NUGGET, amount));
			        			player.sendMessage(ColorOptions.messageachievement + "► You received " + ColorOptions.messagesubjects + amount + " gold nuggets!");
			        		}
			        		if (main.getRandom(0, 1000) <= 10)
			        		{
			        			Integer amount = main.getRandom(0, 2);
			        			item.setItemStack(new ItemStack(Material.GOLD_INGOT, amount));
			        			player.sendMessage(ColorOptions.messageachievement + "► You received " + ColorOptions.messagesubjects + amount + " gold ingots!");
			        		}
			        		if (main.getRandom(0, 1000) <= 1)
			        		{
			        			Integer amount = main.getRandom(0, 6);
			        			item.setItemStack(new ItemStack(Material.GOLD_INGOT, amount));
			        			player.sendMessage(ColorOptions.messageachievement + "► You received " + ColorOptions.messagesubjects + amount + " gold ingots!");
			        		}
			        		if (main.getRandom(0, 10000) <= 1)
			        		{
			        			Integer amount = main.getRandom(0, 6);
			        			item.setItemStack(new ItemStack(Material.GOLD_BLOCK, amount));
			        			player.sendMessage(ColorOptions.messageachievement + "► You received " + ColorOptions.messagesubjects + amount + " gold blocks!");
			        		}
			        	}
			        }
				} else
				{
					e.setCancelled(true);
					player.sendMessage(ColorOptions.error + "You can only catch fish with an item from a shop!");
				}
			} else
			{
				e.setCancelled(true);
				player.sendMessage(ColorOptions.error + "You can only catch fish with an item from a shop!");
			}
		} else
		{
			e.setCancelled(true);
			player.sendMessage(ColorOptions.falsecommand + "You need to be a " + ColorOptions.messagesubjects + title.getTitleName(3, 1) + ColorOptions.falsecommand + "/" + ColorOptions.messagesubjects + title.getTitleName(3, 2) + ColorOptions.falsecommand + " or above to sell fish");
		}
	}
}
