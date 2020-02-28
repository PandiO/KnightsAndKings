package Menu;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import Assignments.Assignment;
import Handlers.ColorOptions;
import Handlers.Menus;
import Main.Main;
import Users.User;

public class AssignmentClick 
{
	Main main = Main.getPlugin(Main.class);
	Menu menu = new Menu();
	HashMap<Integer, Integer> slotIndex = new HashMap<Integer, Integer>(){{
		put(9, 0);
		put(10, 1);
		put(11, 2);
		put(12, 3);
		put(13, 4);
		put(14, 5);
		put(15, 6);
		put(16, 7);
		}};
	public void onAssignmentClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		List<String> lore = clicked.getItemMeta().getLore();
		if (dc.equalsIgnoreCase("back"))
		{
			user.playSound("back");
			this.menu.openAssignmentSubMenu(user);
		}
		if (ChatColor.stripColor(lore.get(lore.size()-1)).equalsIgnoreCase("click here to claim reward!"))
		{
			List<Assignment> assignmentList = user.getAssignmentList();
			
			for (Assignment assignment : assignmentList)
			{
				if (assignment.getName().equalsIgnoreCase(dc))
				{
					assignment.complete();
					this.menu.openAssignmentsMenu(user);
					user.playSound("successkillclick");
					break;
				}
			}
		}
		if (dc.equalsIgnoreCase("locked!"))
		{
			String line = ChatColor.stripColor(lore.get(0));
			if (line.contains("Reach the title "))
			{
				this.menu.openTitleInfo(user);
	    		new BukkitRunnable()
	    		{
	    			public void run()
	    			{
	    	    		new MenuItemBlink(null, user, 24, 10);
	    			}
	    		}.runTaskLater(main, 20);
			}
			if (line.contains("Noble") || line.contains("Royal") || line.contains("Dragon Blood"))
			{
				this.menu.openDonatorInfo(user);
			}
		}
		if (dc.equalsIgnoreCase("completed!"))
		{
			int slot = e.getSlot();
			Assignment assignment = null;
			Integer Index = this.slotIndex.get(slot);
			assignment = user.getAssignmentList().get(Index);
			if (user.getGems() >= assignment.getRefreshPrice())
			{
				user.removeGems(assignment.getRefreshPrice());
				Integer index = user.getAssignmentList().indexOf(assignment);
				user.removeAssignment(assignment);
				Assignment newAssignment = Assignments.Assignments.createAssignment(user, assignment.getTypeID(), null, null, -1, -1, -1, -1, true);
				newAssignment.asign(user, true, index);
				player.sendMessage(ColorOptions.messageachievement + "Succesfully refreshed an assignment for " + assignment.getRefreshPrice() + " Gems!");
				this.menu.openAssignmentsMenu(user);
			}
		}
	}
	
	public void onAchievementClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		List<String> lore = clicked.getItemMeta().getLore();
		if (dc.equalsIgnoreCase("back"))
		{
			user.playSound("back");
			this.menu.openAssignmentSubMenu(user);
		}
	}
	
	public void onQuestClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		List<String> lore = clicked.getItemMeta().getLore();
		if (dc.equalsIgnoreCase("back"))
		{
			user.playSound("back");
			this.menu.openAssignmentSubMenu(user);
		}
		if (dc.equalsIgnoreCase("locked!"))
		{
			String line = ChatColor.stripColor(lore.get(0));
			Bukkit.getConsoleSender().sendMessage(line);
			String line2 = ChatColor.stripColor(lore.get(1));
			if (line.contains("Reach the title"))
			{
				this.menu.openTitleInfo(user);
	    		new BukkitRunnable()
	    		{
	    			public void run()
	    			{
	    	    		new MenuItemBlink(null, user, 21, 10);
	    			}
	    		}.runTaskLater(main, 20);
			}
			Bukkit.broadcastMessage(line2 + ", " + ChatColor.stripColor(Menus.GemShopMenu));
			if (line2.contains(ChatColor.stripColor(Menus.GemShopMenu)))
			{
				this.menu.openGemShop(user);
			}
		}
	}
	
	public void onSubClick(InventoryClickEvent e, User user)
	{
		Player player = user.getPlayer();
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		List<String> lore = clicked.getItemMeta().getLore();
		if (dc.equalsIgnoreCase("back"))
		{
			user.playSound("back");
			this.menu.OpenPersonalMenu(user);
		}
		if (dc.equalsIgnoreCase("daily assignments"))
		{
			this.menu.openAssignmentsMenu(user);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("quests"))
		{
			this.menu.openQuestsMenu(user);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("achievements"))
		{
			this.menu.openAchievementsMenu(user);
			user.playSound("succesclick");
		}
	}
}
