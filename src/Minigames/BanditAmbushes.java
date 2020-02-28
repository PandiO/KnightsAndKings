package Minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import Handlers.ColorOptions;
import Main.Main;
import Products.Enchantment;
import Products.Product;
import Properties.EnchantmentGenerator;
import Users.User;
import net.citizensnpcs.api.npc.NPC;

public class BanditAmbushes 
{
	static Product product = new Product();
	static Main main = Main.getPlugin(Main.class);
	
	public static List<BanditAmbush> ambushes = new ArrayList<BanditAmbush>();
	
	public static void addAmbush(BanditAmbush ambush)
	{
		if (ambushes.contains(ambush))
		{
			return;
		}
		ambushes.add(ambush);
	}
	
	public static void removeAmbush(BanditAmbush ambush)
	{
		if(!ambushes.contains(ambush))
		{
			return;
		}
		ambushes.remove(ambush);
		ambush = null;
		System.gc();
	}
	
	public static boolean hasAmbush(User user)
	{
		boolean hasAmbush = false;
		
		if (getAmbush(user) != null)
		{
			hasAmbush = true;
		}
		
		return hasAmbush;
	}
	
	public static BanditAmbush getAmbush(User user)
	{
		BanditAmbush ambush = null;
		
		for (BanditAmbush ambushes : ambushes)
		{
			if (ambushes.getUser() == user)
			{
				ambush = ambushes;
				break;
			}
		}
		
		return ambush;
	}
	
	public static BanditAmbush getAmbush(NPC npc)
	{
		BanditAmbush ambush = null;
		
		for (BanditAmbush ambushes : ambushes)
		{
			if (ambushes.containsNPC(npc))
			{
				ambush = ambushes;
				break;
			}
		}
		
		return ambush;
	}
	
	public static void tryAmbush(User user)
	{
		Player player = user.getPlayer();
		Location location = player.getLocation();
		Integer spawnChance = 10;
		
		if (!main.IsDay(location.getWorld()))
		{
			spawnChance *= 10;
		}
		
		if (main.getRandom(0, 1000) <= spawnChance)
		{
			new BanditAmbush(user);
		}
	}
	
	public static Location getRandomSpawnLocation(Player player)
	{
		Location finalLoc = player.getLocation();
		
		Location eyeLocation = player.getEyeLocation();
		Location playerLocation = player.getLocation();
		
		Vector direction = playerLocation.getDirection();
		direction.setY(0);
		direction.normalize();
		direction.multiply(8);
		Location front = eyeLocation.add(direction);
		
		Double x = front.getX();
		Double z = front.getZ();
		Double randomX = Double.valueOf(main.getRandom(x.intValue(), x.intValue()+3));
		Double randomZ = Double.valueOf(main.getRandom(z.intValue(), z.intValue()+3));
		front.setX(randomX);
		front.setZ(randomZ);
		finalLoc = front;
//		front.getWorld().dropItemNaturally(front, new ItemStack(Material.GOLD_BLOCK, 64));

		return finalLoc;
	}
	
	public static List<ItemStack> calculateDrops()
	{
		List<ItemStack> drops = new ArrayList<ItemStack>();
		
		Random random = new Random();
		if (random.nextInt(1000) <= 80)
		{
			drops.add( new ItemStack(Material.GOLD_NUGGET, main.getRandom(1, 3)));
		} else
		if (main.getRandom(0, 1000) <= 500)
		{
			drops.add( product.getRandomProduct("resources", null));
		}
		
		if (main.getRandom(0, 1000) <= 100)
		{
			drops.add( product.getRandomProduct(null, null));
		} else	
		if (main.getRandom(0, 1000) <= 80)
		{
			drops.add( product.getRandomProduct("armor", null));
		}
		if (random.nextInt(10000) <= 5)
		{
			drops.add( product.createPropertyItem(product.getProductID("golemheartsword", false), 1, false, false));
		}
		if (random.nextInt(100) <= 1)
		{
        	Enchantment ench = new Enchantment();
        	EnchantmentGenerator gen = new EnchantmentGenerator(main);
        	Integer chosenEnchantmentID = gen.RandomEnchantment(main.getRandom(0,  1));
        	ItemStack item = product.createAmountItem(Material.ENCHANTED_BOOK, 1, ColorOptions.getEnchantColor(ench.getEnchantmentGrade(chosenEnchantmentID)) + ench.getEnchantmentName(chosenEnchantmentID), ChatColor.GREEN + "This item contains 1 or 2 levels", ChatColor.GRAY + "Right-click to add this enchantment", ChatColor.GRAY + "to an item from your inventory");

        	drops.add( item);
		}
		if (random.nextInt(1000) <= 80)
		{
			drops.add( product.getRandomProduct("vegetables", null));
		} else
		if (random.nextInt(1000) <= 80)
		{
			drops.add( product.getRandomProduct("meat", null));
		} else
		if (random.nextInt(1000) <= 80)
		{
			drops.add( product.getRandomProduct("fish", null));
		} else
		if (random.nextInt(1000) <= 80)
		{
			drops.add( product.getRandomProduct("baked-goods", null));
		}
		
		if (random.nextInt(1000) <= 50)
		{
			drops.add( new ItemStack(Material.GOLD_BLOCK, main.getRandom(1, 1)));
		}
		if (random.nextInt(1000) <= 5)
		{
			drops.add( new ItemStack(Material.DIAMOND, main.getRandom(1, 2)));
		}
		if (random.nextInt(1000) <= 3)
		{
			drops.add( new ItemStack(Material.DIAMOND_BLOCK, main.getRandom(1, 1)));
		}
		
		return drops;
	}
}
