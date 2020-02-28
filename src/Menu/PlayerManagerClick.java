package Menu;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.IncomePayoutEvent;
import Handlers.SalaryPayoutEvent;
import Houses.House;
import Main.Main;
import Products.Product;
import Properties.Property;
import Properties.PropertyCategory;
import Skills.Skill;
import Skills.SpecialSkill;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Users.User;
import Users.Users;
import Users.offlineUser;

public class PlayerManagerClick implements Listener
{
	offlineUser user = new offlineUser();
	Skill skill = new Skill();
	Product product = new Product();
	House house = new House();
	Property property = new Property();
	Town town = new Town();
	Street street = new Street();
	Title title = new Title();
	SpawnPoint spawnpoint = new SpawnPoint();
	PropertyCategory propertycat = new PropertyCategory();
	SpecialSkill specialskill = new SpecialSkill();
	Menu menu = new Menu();
	private Main main;
	public PlayerManagerClick(Main main) 
	{
		this.main = main;
	}
	
	List<Integer> removeslots = Arrays.asList(new Integer[] { 
			0, 
			9, 
			18, 
			27, 
			36, 
			45, 
			14,
			23,
			32,
			41
	});
	List<Integer> addslots = Arrays.asList(new Integer[] { 
			2, 
			11, 
			20, 
			29, 
			38, 
			47, 
			16,
			25,
			34,
			43,
	});
	List<Integer> maxslots = Arrays.asList(new Integer[] { 
			3, 
			12, 
			21, 
			30, 
			39, 
			48, 
			17,
			26,
			35,
			44,
	});
	
	
	List<String> displaynamesremove = Arrays.asList(new String[] {
			ChatColor.RED + "Demote with 1 title",
			ChatColor.RED + "Remove experience",
			ChatColor.RED + "Remove coins",
			ChatColor.RED + "Remove gems",
			ChatColor.RED + "Reset cooldown to an hour",
			ChatColor.RED + "Reset cooldown to 12 hours",
			ChatColor.RED + "Remove 1 kill from kill-stats",
			ChatColor.RED + "Add 1 death to death-stats",
			ChatColor.RED + "Remove 1 skillpoint",
			ChatColor.RED + "Remove 1 special skillpoint",
			ChatColor.DARK_RED + "Dragon Blood"
			});
	List<String> displaynamesadd = Arrays.asList(new String[] {
			ColorOptions.messageachievement + "Promote with 1 title",
			ColorOptions.messageachievement + "Add experience",
			ColorOptions.messageachievement + "Add coins",
			ColorOptions.messageachievement + "Add gems",
			ColorOptions.messageachievement + "Set the next cooldown to now",
			ColorOptions.messageachievement + "Set the next cooldown to now",
			ColorOptions.messageachievement + "Add 1 kill to kill-stats",
			ColorOptions.messageachievement + "Remove 1 death from death-stats",
			ColorOptions.messageachievement + "Add 1 skillpoint",
			ColorOptions.messageachievement + "Add 1 special skillpoint"
			});
	List<String> displaynamesmax = Arrays.asList(new String[] {
			ColorOptions.stats + "Give highest title",
			ColorOptions.stats + "Set to max experience",
			ColorOptions.stats + "Set max coins",
			ColorOptions.stats + "Set max gems",
			ColorOptions.stats + "Instant salary payout",
			ColorOptions.stats + "Instant income payout",
			ColorOptions.stats + "Set kill-stats to max",
			ColorOptions.stats + "Set death-stats to 0",
			ColorOptions.stats + "Set max skillpoints",
			ColorOptions.stats + "Set max special skillpoints"
			});
	
	
	@EventHandler
	public void Onclick(InventoryClickEvent e)
	{
		ItemStack clicked = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
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
		Inventory menu = e.getInventory();
		String menuname = ChatColor.stripColor(menu.getName());

		if (menuname.contains("Edit") && menuname.contains("statistics"))
		{
			if (clicked.hasItemMeta())
			{
				String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
				e.setCancelled(true);
				String[] split = menuname.split(" ");
				String target = ChatColor.stripColor(split[1]);
				UUID utarget = this.user.getUUID(target);
				User userTarget = null;
				
				try
				{
					userTarget = Users.getUser(utarget);
				} catch (Exception ex)
				{
					userTarget = new User(utarget);
				}
				
				Integer slot = e.getSlot();
				if (dc.contains("back"))
				{
					this.menu.openPlayerManager(user);
				}
				if (dc.equalsIgnoreCase("kick player"))
				{
					player.performCommand("kick " + target + " You have been kicked for misbehaving");
					this.menu.openPlayerManager(user);
				}
				if (dc.equalsIgnoreCase("ban player"))
				{
					player.performCommand("ban " + target + " You have been banned for misbehaving");
					this.menu.openPlayerManager(user);
				}
				if (dc.equalsIgnoreCase("save changes"))
				{
					openPlayer(user, userTarget);
				}
				if (dc.contains("promote"))
				{
					Integer exp = title.getExpmin(userTarget.getTitleID()+1);
					player.performCommand("experience set " + exp + " " + target);
					openPlayer(user, userTarget);
				}
				if (dc.contains("demote"))
				{
					Integer exp = title.getExpmin(userTarget.getTitleID()-1);
					player.performCommand("experience set " + exp + " " + target);
					openPlayer(user, userTarget);
				} 
				if (dc.equalsIgnoreCase("give highest title"))
				{
					Integer exp = title.getExpmin(18);
					player.performCommand("experience set " + exp + " " + target);
					menu.setItem(1, product.createItem(ColorOptions.messageformat + "Title(" + ColorOptions.messagesubjects + userTarget.getTitleID() + ColorOptions.messageformat + "): " + ColorOptions.messagesubjects + title.getTitleName(userTarget.getTitleID(), userTarget.getGenderID()), new ItemStack(Material.BANNER, 1, (short) 15), false));
					openPlayer(user, userTarget);
				} 
				if (dc.equalsIgnoreCase("experience: " + userTarget.getExperience()))
				{
					List<String>  lore = clicked.getItemMeta().getLore();
					String steps = ChatColor.stripColor(lore.get(0));
					this.setExpStep(user, userTarget, menu, steps, slot);
				}
				if (dc.equalsIgnoreCase("remove experience"))
				{
					List<String> st = menu.getItem(slot+1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					player.performCommand("experience remove " + step + " " + target);
					menu.setItem(10, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, menu.getItem(slot+1).getItemMeta().getLore().get(0)));
					
				}
				if (dc.equalsIgnoreCase("add experience"))
				{
					List<String> st = menu.getItem(slot-1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					player.performCommand("experience add " + step + " " + target);
					menu.setItem(10, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, menu.getItem(slot-1).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("set to max experience"))
				{
					player.performCommand("experience set 99999 " + target);
					menu.setItem(10, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, menu.getItem(slot-2).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("coins: " + userTarget.getCoins()))
				{
					List<String>  lore = clicked.getItemMeta().getLore();
					String steps = ChatColor.stripColor(lore.get(0));
					this.setCoinStep(user, userTarget, menu, steps, slot);
				}
				if (dc.equalsIgnoreCase("remove coins"))
				{
					List<String> st = menu.getItem(slot+1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					player.performCommand("coins remove " + step + " " + target);
					menu.setItem(19, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + userTarget.getCoins(), new ItemStack(Material.GOLD_INGOT, 1), false, menu.getItem(slot+1).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("add coins"))
				{
					List<String> st = menu.getItem(slot-1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					player.performCommand("coins add " + step + " " + target);
					menu.setItem(19, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + userTarget.getCoins(), new ItemStack(Material.GOLD_INGOT, 1), false, menu.getItem(slot-1).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("set max coins"))
				{
					player.performCommand("coins set 999999999 " + target);
					menu.setItem(19, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + userTarget.getCoins(), new ItemStack(Material.GOLD_INGOT, 1), false, menu.getItem(slot-2).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("gems: " + userTarget.getGems()))
				{
					List<String>  lore = clicked.getItemMeta().getLore();
					String steps = ChatColor.stripColor(lore.get(0));
					this.setGemStep(user, userTarget, menu, steps, slot);
				}
				if (dc.equalsIgnoreCase("remove gems"))
				{
					List<String> st = menu.getItem(slot+1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					player.performCommand("gems remove " + step + " " + target);
					menu.setItem(28, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + userTarget.getGems(), new ItemStack(Material.DIAMOND), false, menu.getItem(slot+1).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("add gems"))
				{
					List<String> st = menu.getItem(slot-1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					player.performCommand("gems add " + step + " " + target);
					menu.setItem(28, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + userTarget.getGems(), new ItemStack(Material.DIAMOND), false, menu.getItem(slot-1).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("set max gems"))
				{
					player.performCommand("gems set 9999999 " + target);
					menu.setItem(28, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + userTarget.getGems(), new ItemStack(Material.DIAMOND), false, menu.getItem(slot-2).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("reset cooldown to an hour"))
				{
					userTarget.saveSalaryTime();
					player.sendMessage(ColorOptions.falsecommand + "Set the salary cooldown time to an hour from now");
					player.sendMessage(ColorOptions.falsecommand + "Save the changes to see them");
				}
				if (dc.equalsIgnoreCase("set the next cooldown to now") && slot == 38)
				{
					Bukkit.getServer().getPluginManager().callEvent(new SalaryPayoutEvent(userTarget));
					player.sendMessage(ColorOptions.messageachievement + "Set the salary cooldown of" + ColorOptions.messagesubjects + target + ColorOptions.messageachievement + " to now");
					player.sendMessage(ColorOptions.falsecommand + "Save the changes to see them");
					menu.setItem(19, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + userTarget.getCoins(), new ItemStack(Material.GOLD_INGOT, 1), false, menu.getItem(19).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("instant salary payout"))
				{
					user.SalaryPayout(utarget);
					player.sendMessage(ColorOptions.messageachievement + "Paid out salary for " + ColorOptions.messagesubjects + target);
					player.sendMessage(ColorOptions.falsecommand + "Save the changes to see them");
					menu.setItem(19, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + userTarget.getCoins(), new ItemStack(Material.GOLD_INGOT, 1), false, menu.getItem(19).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("reset cooldown to 12 hours"))
				{
					userTarget.saveIncomeTime();
					player.sendMessage(ColorOptions.falsecommand + "Set the income cooldown time to 12 hours from now");
					player.sendMessage(ColorOptions.falsecommand + "Save the changes to see them");
				}
				if (dc.equalsIgnoreCase("set the next cooldown to now") && slot == 47)
				{
					Bukkit.getServer().getPluginManager().callEvent(new IncomePayoutEvent(userTarget));
					player.sendMessage(ColorOptions.messageachievement + "Set the income cooldown of" + ColorOptions.messagesubjects + target + ColorOptions.messageachievement + " to now");
					player.sendMessage(ColorOptions.falsecommand + "Save the changes to see them");
					menu.setItem(19, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + userTarget.getCoins(), new ItemStack(Material.GOLD_INGOT, 1), false, menu.getItem(19).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("instant income payout"))
				{
					Integer income = userTarget.getIncome();
					String gender = userTarget.getGenderName();
					if (gender.equalsIgnoreCase("male"))
					{
						player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your income: " + ColorOptions.salarysubjects + income);
					} else if (gender.equalsIgnoreCase("female"))
					{
						player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylady! you received your income: " + ColorOptions.salarysubjects + income);
					}
					userTarget.addCoins(income);
					player.sendMessage(ColorOptions.falsecommand + "Save the changes to see them");
					menu.setItem(19, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + userTarget.getCoins(), new ItemStack(Material.GOLD_INGOT, 1), false, menu.getItem(19).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("kills: " + userTarget.getKills()))
				{
					List<String>  lore = clicked.getItemMeta().getLore();
					String steps = ChatColor.stripColor(lore.get(0));
					this.setKillStep(user, userTarget, menu, steps, slot);
				}
				if (dc.equalsIgnoreCase("remove 1 kill from kill-stats"))
				{
					List<String> st = menu.getItem(slot+1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					player.performCommand("kills remove " + target + " " + step);
					menu.setItem(15, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + userTarget.getKills(), new ItemStack(Material.SKULL_ITEM), false, menu.getItem(15).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("add 1 kill to kill-stats"))
				{
					List<String> st = menu.getItem(slot-1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					player.performCommand("kills add " + target + " " + step);
					menu.setItem(15, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + userTarget.getKills(), new ItemStack(Material.SKULL_ITEM), false, menu.getItem(15).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("deaths: " + userTarget.getDeaths()))
				{
					List<String>  lore = clicked.getItemMeta().getLore();
					String steps = ChatColor.stripColor(lore.get(0));
					this.setDeathStep(user, userTarget, menu, steps, slot);
				}
				if (dc.equalsIgnoreCase("add 1 death to death-stats"))
				{
					List<String> st = menu.getItem(slot+1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					userTarget.addDeaths(Integer.valueOf(step));
					player.sendMessage(ColorOptions.falsecommand + "Added " + step + " death(s)");
					menu.setItem(24, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + userTarget.getDeaths(), new ItemStack(Material.SKULL_ITEM), false, menu.getItem(24).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("remove 1 death from death-stats"))
				{
					List<String> st = menu.getItem(slot-1).getItemMeta().getLore();
					String[] spl = st.get(0).split(" ");
					String step = spl[2];
					user.removeDeaths(utarget, Integer.valueOf(step));
					player.sendMessage(ColorOptions.falsecommand + "Removed " + step + " death(s)");
					menu.setItem(24, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + userTarget.getDeaths(), new ItemStack(Material.SKULL_ITEM), false, menu.getItem(24).getItemMeta().getLore().get(0)));
				}
				if (dc.equalsIgnoreCase("remove 1 skillpoint"))
				{
					userTarget.removeSkillPoints(false, Integer.valueOf(1));
					player.sendMessage(ColorOptions.falsecommand + "Removed 1 skillpoint!");
					menu.setItem(33, product.createItem(ColorOptions.messageformat + "Skillpoints: " + ColorOptions.messagesubjects + userTarget.getSkillPoints(false), new ItemStack(Material.EMERALD, 1), false));
				}
				if (dc.equalsIgnoreCase("add 1 skillpoint"))
				{
					userTarget.addSkillPoints(false, Integer.valueOf(1));
					player.sendMessage(ColorOptions.messageachievement + "Added 1 skillpoint!");
					menu.setItem(33, product.createItem(ColorOptions.messageformat + "Skillpoints: " + ColorOptions.messagesubjects + userTarget.getSkillPoints(false), new ItemStack(Material.EMERALD, 1), false));
				}
				if (dc.equalsIgnoreCase("remove 1 special skillpoint"))
				{
					userTarget.removeSkillPoints(true, Integer.valueOf(1));
					player.sendMessage(ColorOptions.falsecommand + "Removed 1 special skillpoint!");
					menu.setItem(42, product.createItem(ColorOptions.messageformat + "Special skillpoints: " + ColorOptions.messagesubjects + userTarget.getSkillPoints(true), new ItemStack(Material.NETHER_STAR, 1), false));
				}
				if (dc.equalsIgnoreCase("add 1 special skillpoint"))
				{
					userTarget.addSkillPoints(true, Integer.valueOf(1));
					player.sendMessage(ColorOptions.messageachievement + "Added 1 special skillpoint!");
					menu.setItem(42, product.createItem(ColorOptions.messageformat + "Special skillpoints: " + ColorOptions.messagesubjects + user.getSkillPoints(true), new ItemStack(Material.NETHER_STAR, 1), false));
				}
				if (dc.equalsIgnoreCase("donator title: " + userTarget.getDonatorName().toLowerCase()))
				{
					player.performCommand("donator remove " + target);
				}
				if (dc.equalsIgnoreCase("set donator title to a noble"))
				{
					player.performCommand("donator set noble " + target);
				}
				if (dc.equalsIgnoreCase("set donator title to a royal"))
				{
					player.performCommand("donator set royal " + target);
				}
				if (dc.equalsIgnoreCase("set donator title to a dragon blood"))
				{
					player.performCommand("donator set dragonblood " + target);
				}
			}
		}
	}
	
	public void PlayerManagerClick(InventoryClickEvent e, User user)
	{
		ItemStack clicked = e.getCurrentItem();
		Player player = user.getPlayer();
		String staff = "k&k.staff";
		String owner = "k&k.owner";
		String coowner = "k&k.co-owner";
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		e.setCancelled(true);
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.OpenPersonalMenu(user);
		}
		for (Player players : Bukkit.getOnlinePlayers())
		{
			if (players.hasPermission(owner) && !player.hasPermission(owner))
			{
				player.sendMessage(ColorOptions.error + "You can't modify this player!");
			} else
			if (player.hasPermission(staff) && !players.hasPermission(staff) && !players.hasPermission(owner) && dc.equalsIgnoreCase(players.getName() + "'s information"))
			{
				User userTarget = null;
				
				try
				{
					userTarget = Users.getUser(players.getUniqueId());
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(player, players, false);
					return;
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(player, players, false);
					return;
				}
				openPlayer(user, userTarget);
				
			} else
			if (player.hasPermission(owner) && dc.equalsIgnoreCase(players.getName() + "'s information") || player.hasPermission(coowner))
			{
				User userTarget = null;
				
				try
				{
					userTarget = Users.getUser(players.getUniqueId());
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(player, players, false);
					return;
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(player, players, false);
					return;
				}
				openPlayer(user, userTarget);
			}
		}
	}
	
	public void openPlayer(User user, User userTarget)
	{
		long current = System.currentTimeMillis();
		Long salaryt = current - userTarget.getSalaryTime();
		Integer salaryi = salaryt.intValue();
		int timer = (salaryi / -1000);
		int rest = 3600 - timer;
		int mins = timer/60;
		int secs = timer%60;
		
		Long incomet = current - userTarget.getIncomeTime();
		Integer incomei = incomet.intValue();
		int itimer = (incomei / -1000);
		int min = itimer/60;
		int hour = min/60;
		int irest = min%60;
		
		Date firstjoin = userTarget.getJoinDate();
		String titlename = userTarget.getTitleName();
		Integer titleID = userTarget.getTitleID();
		Integer experience = userTarget.getExperience();
		Integer coins = userTarget.getCoins();
		Integer gems = userTarget.getGems();
		Integer salary = userTarget.getSalary();
		Integer income = userTarget.getIncome();
		Integer houseamount = userTarget.getHouseAmount(false);
		Integer propertyamount = userTarget.getPropertyAmount(false);
		String specialskill = this.specialskill.getName(userTarget.getSpecialSkillID());
		Integer friendamount = userTarget.getFriendAmount();
		Long salarytime = userTarget.getSalaryTime();
		Long incometime = userTarget.getIncomeTime();
		Integer skillpoints = userTarget.getSkillPoints(false);
		Integer specialskillpoints = userTarget.getSkillPoints(true);
		Integer kills = userTarget.getKills();
		Integer deaths = userTarget.getDeaths();
		String donatortitle = userTarget.getDonatorName();
		
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(userTarget.getUsername());
		skullMeta.setDisplayName(ColorOptions.stats + userTarget.getUsername());
		List<String> lore = Arrays.asList(new String[]{
			ColorOptions.stats + "Firstjoin: " + ColorOptions.statsresults + firstjoin,
			ColorOptions.stats + "Friendamount: " + ColorOptions.statsresults + friendamount,
			ColorOptions.stats + "Friends: " + ColorOptions.statsresults + userTarget.getFriendNames()
		});
		skullMeta.setLore(lore);
		skull.setItemMeta(skullMeta);
		
		Inventory playerm = Bukkit.createInventory(null, 9*6, ColorOptions.statsformat + "Edit " + userTarget.getUsername() + " statistics");
		
		if (titleID < 15)
		{
			playerm.setItem(1, product.createItem(ColorOptions.messageformat + "Title(" + ColorOptions.messagesubjects + titleID + ColorOptions.messageformat + "): " + ColorOptions.messagesubjects + titlename, new ItemStack(Material.BANNER, 1, (short) Short.valueOf(String.valueOf(titleID))), false));
		} else
		{
			playerm.setItem(1, product.createItem(ColorOptions.messageformat + "Title(" + ColorOptions.messagesubjects + titleID + ColorOptions.messageformat + "): " + ColorOptions.messagesubjects + titlename, new ItemStack(Material.BANNER, 1, (short) 15), false));
		}
		playerm.setItem(4, skull);
		playerm.setItem(6, product.createItem(ColorOptions.messageformat + "Next page", new ItemStack(Material.ARROW, 1), false, ChatColor.GRAY + "Click here to go to", ChatColor.GRAY + "the next page of statistics"));
		playerm.setItem(7, product.createItem(ColorOptions.stats + "" + ChatColor.BOLD + "Save changes", new ItemStack(Material.BOOK_AND_QUILL, 1), true, ChatColor.GRAY + "Don't forget to save or changes will be lost!"));
		playerm.setItem(8, product.createItem(ChatColor.RED + "Back", new ItemStack(Material.BARRIER, 1), false, ChatColor.GRAY + "Click here to go back to the Player manager"));
		playerm.setItem(10, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + experience, new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 1"));
		playerm.setItem(13, product.createItem(ColorOptions.falsecommand + "Kick player", new ItemStack(Material.IRON_BARDING, 1), false, ChatColor.GRAY + "Click here to kick this player"));
		playerm.setItem(19, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 1"));
		playerm.setItem(22, product.createItem(ColorOptions.falsecommand + "Ban player", new ItemStack(Material.IRON_BARDING), true, ChatColor.GRAY + "Click here to ban this player permanently"));
		playerm.setItem(28, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + gems, new ItemStack(Material.DIAMOND), false, ChatColor.GRAY + "Steps of 1"));
		playerm.setItem(37, product.createItem(ColorOptions.messageformat + "SalaryTime, current salary: " + ColorOptions.messagesubjects + salary, new ItemStack(Material.WATCH, 1), false, ColorOptions.messageformat + "Next payout in: " + ColorOptions.messagesubjects + mins + " minutes and " + secs + " seconds"));
		playerm.setItem(46, product.createItem(ColorOptions.messageformat + "IncomteTime, current income: " + ColorOptions.messagesubjects + income, new ItemStack(Material.WATCH, 1), false, ColorOptions.messageformat + "Next payout in: " + ColorOptions.messagesubjects + hour + " hours and " + irest + " minutes"));
		playerm.setItem(15, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + kills, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 1"));
		playerm.setItem(24, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + deaths, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 1"));
		playerm.setItem(33, product.createItem(ColorOptions.messageformat + "Skillpoints: " + ColorOptions.messagesubjects + skillpoints, new ItemStack(Material.EMERALD, 1), false));
		playerm.setItem(42, product.createItem(ColorOptions.messageformat + "Special skillpoints: " + ColorOptions.messagesubjects + specialskillpoints, new ItemStack(Material.NETHER_STAR, 1), false));
		if (donatortitle.length() < 2)
		{
			playerm.setItem(50, product.createItem(ColorOptions.messageformat + "Donator title: " + ColorOptions.messagesubjects + "default", new ItemStack(Material.LEATHER_CHESTPLATE, 1), false, ChatColor.GRAY + "Check or edit the donator title", ChatColor.GRAY + "Click here to set to default"));
		} else if (donatortitle.equalsIgnoreCase("noble"))
		{
			playerm.setItem(50, product.createItem(ColorOptions.messageformat + "Donator title: " + ColorOptions.noblesubjects + donatortitle, new ItemStack(Material.GOLD_CHESTPLATE, 1), true, ChatColor.GRAY + "Check or edit the donator title", ChatColor.GRAY + "Click here to set to default"));
		} else if (donatortitle.equalsIgnoreCase("royal"))
		{
			playerm.setItem(50, product.createItem(ColorOptions.messageformat + "Donator title: " + ColorOptions.royalsubjects + donatortitle, new ItemStack(Material.DIAMOND_CHESTPLATE, 1), true, ChatColor.GRAY + "Check or edit the donator title", ChatColor.GRAY + "Click here to set to default"));
		} else if (donatortitle.equalsIgnoreCase("dragon blood"))
		{
			playerm.setItem(50, product.createItem(ColorOptions.messageformat + "Donator title: " + ColorOptions.dbsubjects + donatortitle, new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), true, ChatColor.GRAY + "Check or edit the donator title", ChatColor.GRAY + "Click here to set to default"));
		}
		playerm.setItem(51, product.createItem(ColorOptions.messageformat + "Set donator title to a " + ColorOptions.noblesubjects + "Noble", new ItemStack(Material.STAINED_CLAY, 1, (short) 4), false));
		playerm.setItem(52, product.createItem(ColorOptions.messageformat + "Set donator title to a " + ColorOptions.royalsubjects + "Royal", new ItemStack(Material.STAINED_CLAY, 1, (short) 11), false));
		playerm.setItem(53, product.createItem(ColorOptions.messageformat + "Set donator title to a " + ColorOptions.dbsubjects + "Dragon Blood", new ItemStack(Material.STAINED_CLAY, 1, (short) 14), false));
		
		Integer removecounter = 0;
		Integer addcounter = 0;
		Integer maxcounter = 0;
		for (int i : removeslots)
		{
			playerm.setItem(i, product.createItem(displaynamesremove.get(removecounter), new ItemStack(Material.STAINED_CLAY, 1, (short) 14), false));
			removecounter = removecounter +1;
		}
		for (int i : addslots)
		{
			playerm.setItem(i, product.createItem(displaynamesadd.get(addcounter), new ItemStack(Material.STAINED_CLAY, 1, (short) 5), false));
			addcounter = addcounter +1;
		}
		for (int i : maxslots)
		{
			playerm.setItem(i, product.createItem(displaynamesmax.get(maxcounter), new ItemStack(Material.STAINED_CLAY, 1, (short) 13), true));
			maxcounter = maxcounter +1;
		}
		
		for (int i = 0; i < playerm.getSize(); i++)
    	{
    		if (playerm.getItem(i) == null || playerm.getItem(i).getType() == Material.AIR)
    		{
    			if (i == 3 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 21)
    			{
    				playerm.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0)));
    			} else
    			{
    				playerm.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0)));
    			}
    		}
    	}
		user.getPlayer().openInventory(playerm);
		
	}
	
	public void setExpStep(User user, User userTarget, Inventory menu, String steps, Integer slot)
	{
		String[] split = steps.split(" ");
		String step = split[2];
		if (step.equalsIgnoreCase("1"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 5"));
		} else
		if (step.equalsIgnoreCase("5"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 10"));
		} else
		if (step.equalsIgnoreCase("10"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 50"));
		} else
		if (step.equalsIgnoreCase("50"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 100"));
		} else
		if (step.equalsIgnoreCase("100"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 500"));
		} else
		if (step.equalsIgnoreCase("500"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 1000"));
		} else
		if (step.equalsIgnoreCase("1000"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 2500"));
		} else
		if (step.equalsIgnoreCase("2500"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Experience: " + ColorOptions.messagesubjects + userTarget.getExperience(), new ItemStack(Material.EXP_BOTTLE, 1), false, ChatColor.GRAY + "Steps of 1"));
		}
	}
	public void setCoinStep(User user, User userTarget, Inventory menu, String steps, Integer slot)
	{
		String[] split = steps.split(" ");
		String step = split[2];
		Integer coins = userTarget.getCoins();
		if (step.equalsIgnoreCase("1"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 50"));
		} else
		if (step.equalsIgnoreCase("50"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 100"));
		} else
		if (step.equalsIgnoreCase("100"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 250"));
		} else
		if (step.equalsIgnoreCase("250"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 500"));
		} else
		if (step.equalsIgnoreCase("500"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 5000"));
		} else
		if (step.equalsIgnoreCase("5000"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 20000"));
		} else
		if (step.equalsIgnoreCase("20000"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 100000"));
		} else
		if (step.equalsIgnoreCase("100000"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 1000000"));
		} else
		if (step.equalsIgnoreCase("1000000"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Coins: " + ColorOptions.coinStats + coins, new ItemStack(Material.GOLD_INGOT, 1), false, ChatColor.GRAY + "Steps of 1"));
		}
	}
	public void setGemStep(User user, User userTarget, Inventory menu, String steps, Integer slot)
	{
		String[] split = steps.split(" ");
		String step = split[2];
		Integer gems = userTarget.getGems();
		if (step.equalsIgnoreCase("1"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + gems, new ItemStack(Material.DIAMOND), false, ChatColor.GRAY + "Steps of 5"));
		} else
		if (step.equalsIgnoreCase("5"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + gems, new ItemStack(Material.DIAMOND), false, ChatColor.GRAY + "Steps of 10"));
		} else
		if (step.equalsIgnoreCase("10"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + gems, new ItemStack(Material.DIAMOND), false, ChatColor.GRAY + "Steps of 50"));
		} else
		if (step.equalsIgnoreCase("50"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + gems, new ItemStack(Material.DIAMOND), false, ChatColor.GRAY + "Steps of 200"));
		} else
		if (step.equalsIgnoreCase("200"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + gems, new ItemStack(Material.DIAMOND), false, ChatColor.GRAY + "Steps of 1000"));
		} else
		if (step.equalsIgnoreCase("1000"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Gems: " + ColorOptions.gemStats + gems, new ItemStack(Material.DIAMOND), false, ChatColor.GRAY + "Steps of 1"));
		}
	}
	public void setKillStep(User user, User userTarget, Inventory menu, String steps, Integer slot)
	{
		String[] split = steps.split(" ");
		String step = split[2];
		Integer kills = userTarget.getKills();
		if (step.equalsIgnoreCase("1"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + kills, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 5"));
		} else
		if (step.equalsIgnoreCase("5"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + kills, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 10"));
		} else
		if (step.equalsIgnoreCase("10"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + kills, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 50"));
		} else
		if (step.equalsIgnoreCase("50"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + kills, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 200"));
		} else
		if (step.equalsIgnoreCase("200"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + kills, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of all"));
		} else
		if (step.equalsIgnoreCase("all"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Kills: " + ColorOptions.messagesubjects + kills, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 1"));
		}
	}
	public void setDeathStep(User user, User userTarget, Inventory menu, String steps, Integer slot)
	{
		String[] split = steps.split(" ");
		String step = split[2];
		Integer deaths = userTarget.getDeaths();
		if (step.equalsIgnoreCase("1"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + deaths, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 5"));
		} else
		if (step.equalsIgnoreCase("5"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + deaths, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 10"));
		} else
		if (step.equalsIgnoreCase("10"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + deaths, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 50"));
		} else
		if (step.equalsIgnoreCase("50"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + deaths, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 200"));
		} else
		if (step.equalsIgnoreCase("200"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + deaths, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of all"));
		} else
		if (step.equalsIgnoreCase("all"))
		{
			menu.setItem(slot, product.createItem(ColorOptions.messageformat + "Deaths: " + ColorOptions.messagesubjects + deaths, new ItemStack(Material.SKULL_ITEM), false, ChatColor.GRAY + "Steps of 1"));
		}
	}
}
