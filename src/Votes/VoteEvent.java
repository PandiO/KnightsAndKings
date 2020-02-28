package Votes;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import Donator.Donator;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Main.Main;
import Products.Product;
import Users.User;
import Users.Users;

public class VoteEvent implements Listener
{
	private Main main;
	Product product = new Product();
	public VoteEvent(Main main)
	{
		this.main = main;
	}
	
	@EventHandler
	public void onVote(VotifierEvent e)
	{
		Vote v = e.getVote();
		String username = v.getUsername();
		Player player = Bukkit.getPlayer(username);
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
		String donor = user.getDonatorName();
		
		user.addVotes(Integer.valueOf(1));
		if (donor.equalsIgnoreCase("default"))
		{
			Integer exp = user.getExpPart(10);
			if (main.getRandom(0, 10) <= 4)
			{
				exp = user.getExpPart(15);
			}
			vote(player, main.getRandom(5000, 20000), main.getRandom(0, 5), main.getRandom((Integer) exp/10, exp));
		} else
		if (donor.equalsIgnoreCase("noble"))
		{
			Integer exp = user.getExpPart(10);
			if (main.getRandom(0, 10) <= 4)
			{
				exp = user.getExpPart(15);
			}
			vote(player, main.getRandom(10000, 20000), main.getRandom(1, 8), main.getRandom((Integer) exp/10, exp));
		} else
		if (donor.equalsIgnoreCase("royal"))
		{
			Integer exp = user.getExpPart(10);
			if (main.getRandom(0, 10) <= 4)
			{
				exp = user.getExpPart(15);
			}
			vote(player, main.getRandom(10000, 30000), main.getRandom(2, 12), main.getRandom((Integer) exp/10, exp));
		} else
		if (donor.equalsIgnoreCase("dragon blood"))
		{
			Integer exp = user.getExpPart(10);
			if (main.getRandom(0, 10) <= 4)
			{
				exp = user.getExpPart(15);
			}
			vote(player, main.getRandom(20000, 45000), main.getRandom(5, 20), main.getRandom((Integer) exp/10, exp));
		}
	}
	
	public void vote(Player player, Integer coins, Integer gems, Integer exp)
	{
		Donator donator = new Donator();
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
		Integer donatorID = user.getDonatorID();
		Integer realcoins = coins;
		if (user.getTitleID() == 0)
		{
			realcoins = coins*1;
		} else if (user.getTitleID() > 0)
		{
			realcoins = coins*(user.getTitleID()/2);
		}
		realcoins = user.getMultipliedInt(realcoins);
		gems = user.getMultipliedInt(gems);
		exp = user.getMultipliedInt(exp);
		player.playSound(player.getLocation(), SoundHandler.LEVEL_UP, 0.5F, 1.0F);
		user.addCoins(realcoins);
		user.addGems(gems);
		user.addExperience(exp, true);
		Integer random = main.getRandom(0, 100);
		Bukkit.broadcastMessage(ColorOptions.statsbrackets);
		Bukkit.broadcastMessage(ColorOptions.stats + "Player " + ColorOptions.messagesubjects + player.getName() + ColorOptions.stats + " just voted and received:");
		Bukkit.broadcastMessage(ColorOptions.stats + "-Coins: " + ColorOptions.statsresults + realcoins);
		Bukkit.broadcastMessage(ColorOptions.gemStats + "-Gems: " + ColorOptions.statsresults + gems);
		Bukkit.broadcastMessage(ColorOptions.stats + "-Experience: " + ColorOptions.statsresults + exp);
		Bukkit.broadcastMessage(ColorOptions.messageformat + "Vote now with " + ColorOptions.messagesubjects + "/vote " + ColorOptions.messageformat + "and earn good rewards!");
		Bukkit.broadcastMessage(ColorOptions.statsbrackets);

		List<Integer> idList = product.getIDList(true, null, true);
		Collections.shuffle(idList);
		for (Integer productID : idList)
		{
			if (product.gradeChance(productID))
			{
				player.getInventory().addItem(product.createPropertyItem(productID, 1, false, false));
				player.sendMessage(ColorOptions.message + "You received " + product.getDisplayName(productID, false) + ColorOptions.message + "!");
				break;
			}
		}
		
		if (random <= 50)
		{
			player.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, main.getRandom(2, 20)));
			Bukkit.getWorld(player.getLocation().getWorld().getName()).dropItemNaturally(player.getLocation(), product.createPropertyItem(product.getProductID("cookedmutton", false), main.getRandom(8, 32), false, false));
			Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Extra bonus! " + ColorOptions.messagesubjects + player.getName() + ChatColor.DARK_PURPLE + " received golden nuggets and some cooked mutton!");
		}
		if (random <= 1)
		{
			user.addSkillPoints(false, Integer.valueOf(1));
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "**" + ChatColor.BLUE + "Super lucky vote! " + ColorOptions.messagesubjects + player.getName() + ChatColor.BLUE + " received an extra skillpoint!" + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "**");
		}

	}
}
