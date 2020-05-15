package Minigames;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Main.Main;
import Products.Product;
import Titles.Title;
import Towns.Town;
import Users.User;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

public class BanditKill implements Listener
{
	Town town = new Town();
	Title title = new Title();
	WorldGuard worldguard = new WorldGuard();
	Product product = new Product();
    NPCRegistry registry = CitizensAPI.getNPCRegistry();
    BanditSpawn banditspawn;
    private Main main;
	public BanditKill(Main main)
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void onNPCKill(EntityDeathEvent e)
//	{
//		Entity died = e.getEntity();
//		
//		if (e.getEntity().getKiller() instanceof Player && died.hasMetadata("NPC"))
//		{
//			NPC npc = this.registry.getNPC(died);
//			Player player = (Player) e.getEntity().getKiller();
//			User user = null;
//			
//			try
//			{
//				user = users.getUser(player.getUniqueId());
//			} catch (UserNotFoundException ex)
//			{
//				ErrorHandlers.userNotFoundAction(null, player, true);
//				return;
//			} catch (Exception ex)
//			{
//				ex.printStackTrace();
//				ErrorHandlers.userNotFoundAction(null, player, true);
//				return;
//			}
//			
//			Location loc = npc.getStoredLocation();
//			if (npc.hasTrait(Gladiator.class))
//			{
//				CitizensAPI.getNPCRegistry().deregister(npc);
//				loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, 1));
//			} else
//			if (npc.hasTrait(Bandit_v2.class))
//			{
//				Random random = new Random();
//				if (random.nextInt(1000) <= 80)
//				{
//					loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLD_NUGGET, main.getRandom(1, 3)));
//				} else
//				if (main.getRandom(0, 1000) <= 500)
//				{
//					loc.getWorld().dropItemNaturally(loc, product.getRandomProduct("resources", null));
//				}
//				
//				if (main.getRandom(0, 1000) <= 100)
//				{
//					loc.getWorld().dropItemNaturally(loc, product.getRandomProduct(null, null));
//				} else	
//				if (main.getRandom(0, 1000) <= 80)
//				{
//					loc.getWorld().dropItemNaturally(loc, product.getRandomProduct("armor", null));
//				}
//				if (random.nextInt(10000) <= 5)
//				{
//					loc.getWorld().dropItemNaturally(loc, product.createPropertyItem(product.getProductID("golemheartsword", false), 1, false, false));
//				}
//				if (random.nextInt(100) <= 1)
//				{
//		        	Enchantment ench = new Enchantment();
//		        	EnchantmentGenerator gen = new EnchantmentGenerator(main);
//		        	Integer chosenEnchantmentID = gen.RandomEnchantment(loc, main.getRandom(0,  1));
//	            	ItemStack item = product.createAmountItem(Material.ENCHANTED_BOOK, 1, ColorOptions.getEnchantColor(ench.getEnchantmentGrade(chosenEnchantmentID)) + ench.getEnchantmentName(chosenEnchantmentID), ChatColor.GREEN + "This item contains 1 or 2 levels", ChatColor.GRAY + "Right-click to add this enchantment", ChatColor.GRAY + "to an item from your inventory");
//
//					loc.getWorld().dropItemNaturally(loc, item);
//				}
//				if (random.nextInt(1000) <= 80)
//				{
//					loc.getWorld().dropItemNaturally(loc, product.getRandomProduct("vegetables", null));
//				} else
//				if (random.nextInt(1000) <= 80)
//				{
//					loc.getWorld().dropItemNaturally(loc, product.getRandomProduct("meat", null));
//				} else
//				if (random.nextInt(1000) <= 80)
//				{
//					loc.getWorld().dropItemNaturally(loc, product.getRandomProduct("fish", null));
//				} else
//				if (random.nextInt(1000) <= 80)
//				{
//					loc.getWorld().dropItemNaturally(loc, product.getRandomProduct("baked-goods", null));
//				}
//				
//				if (random.nextInt(1000) <= 50)
//				{
//					loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLD_BLOCK, main.getRandom(1, 1)));
//				}
//				if (random.nextInt(1000) <= 5)
//				{
//					loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DIAMOND, main.getRandom(1, 2)));
//				}
//				if (random.nextInt(1000) <= 3)
//				{
//					loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.DIAMOND_BLOCK, main.getRandom(1, 1)));
//				}
//				CitizensAPI.getNPCRegistry().deregister(npc);
//				
//				if (user != null)
//				{
//					user.addKills(true, 1, 1);
//					for (Assignment assignment : user.getAssignmentList())
//					{
//						if (assignment instanceof AssignmentKill)
//						{
//							AssignmentKill Assignment = (AssignmentKill) assignment;
//							if (!Assignment.isOnlyPlayers())
//							{
//								Assignment.addProgress(1);
//							}
//							break;
//						}
//					}
//				}
//				if (banditspawn.banditID.containsValue(npc.getId()))
//				{
//					for (UUID key : BanditSpawn.banditID.keySet())
//					{
//						if (banditspawn.banditID.get(key).contains(npc.getId()))
//						{
//							if (banditspawn.banditID.get(key).size() > 0)
//							{
//								List<Integer> list = banditspawn.banditID.get(key);
//								list.remove(list.indexOf(npc.getId()));
//								banditspawn.banditID.put(key, list);
//								if (banditspawn.banditID.get(key).size() < 1)
//								{
//									banditAchieved(user);
//									banditspawn.banditID.remove(key);
//								}
//							} else
//							{
//								banditAchieved(user);
//								banditspawn.banditID.remove(key);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
	
	public void banditAchieved(User user)
	{
		Player player = user.getPlayer();
		Integer coins = user.getMultipliedInt(main.getRandom(user.getSalary()/2, user.getSalary()));
		Integer titleID = user.getTitleID();
		Integer exp = 0;
		if (titleID < 18)
		{
			Integer nextrankexp = title.getExpmin(titleID+1);
			Integer currentrankexp = title.getExpmin(titleID);
			Integer part = (nextrankexp - currentrankexp)/8;
			exp = user.getMultipliedInt(main.getRandom(part/10, part));
		}
		
		user.addCoins(coins);
		user.addExperience(exp, true);
		player.sendMessage(ColorOptions.messageachievement + "Congratulations! you've killed all the bandits!");
		player.sendMessage(ColorOptions.statsformat + "You received the following rewards:");
		player.sendMessage(ColorOptions.stats + "Coins: " + ColorOptions.statsresults + coins);
		player.sendMessage(ColorOptions.stats + "Experience: " + ColorOptions.statsresults + exp);


	}
}
