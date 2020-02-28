package Menu;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Handlers.ColorOptions;
import Handlers.EnchantmentGlow;
import Handlers.SoundHandler;
import Main.Main;
import Skills.Skill;
import Tutorial.Tutorial;
import Tutorial.TutorialEvents;
import Users.User;

public class SkillMenuClick
{
	Skill skill = new Skill();
	Menu menu = new Menu();
	private Main main;
	public SkillMenuClick(Main main) 
	{
		this.main = main;
	}
	public static Map<UUID, Inventory> skillmenus = new HashMap<UUID, Inventory>();
    

    public void onClick(InventoryClickEvent e, User user)
    {
		ItemStack clicked = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
		
		Integer nextslevel = user.getStrengthID()+1;
		Integer nextsplevel = user.getSpeedID()+1;
		Integer nexthlevel = user.getHealthID()+1;
		Integer nextaslevel = user.getAttackSpeedID()+1;
		Integer nextdlevel = user.getDefenseID()+1;
		
    	SkillMenuItemData.Points.setDisplayName(ChatColor.GREEN + "Skillpoints: " + user.getSkillPoints(false));
    	SkillMenuItems.Skillpoints.setItemMeta(SkillMenuItemData.Points);
    	if (clicked != null && clicked.hasItemMeta())
		{
			Tutorial tutorial = null;
			for (Tutorial tut : TutorialEvents.tutorials)
			{
				if (tut.target == player)
				{
					tutorial = tut;
					break;
				}
			}
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
			e.setCancelled(true);
			if (tutorial != null)
			{
    			if (clicked.getItemMeta().getDisplayName().toLowerCase().contains("back"))
    			{
    				tutorial.previousStage();
					player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
    			}
    			if (tutorial.getName().equalsIgnoreCase("skills tutorial"))
    			{
    				if (tutorial.stage >= 5)
    				{
        				if (dc.contains("upgrade"))
        				{
        					String skillType = dc.split(" ")[0];
        					Integer skillLevel = Integer.valueOf(dc.split(" ")[2]);
        					tryUpgrade(user, skillType, skillLevel);
        					tutorial.nextStage(null, null);
        				}
    				}
    			}
			} else
			{
				if (clicked.getItemMeta().getDisplayName().toLowerCase().contains("back"))
    			{
    				menu.OpenPersonalMenu(user);
					user.playSound("back");
    			}
    			if (user.getSkillPoints(false) > 0)
    			{
    				if (dc.contains("upgrade"))
    				{
    					String skillType = dc.split(" ")[0];
    					Integer skillLevel = Integer.valueOf(dc.split(" ")[2]);
    					tryUpgrade(user, skillType, skillLevel);
    				}
    			}
    			if (user.getSkillPoints(true) > 0)
    			{
    				if (dc.contains("Special Skill"))
    				{
    					if (clicked.getItemMeta().getLore() == null)
    					{
    						Integer specialskillID = main.getRandom(1, 7);
    						user.setSpecialSkill(specialskillID);
    						user.removeSkillPoints(true, Integer.valueOf(1));
    						this.menu.openSkillMenu(user);
    					    user.playSound("specialskill");
    					}
    				}
    			}
			}
		} 
    }
	
	public void tryUpgrade(User user, String skillType, Integer skillLevel)
	{		
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("SkillType: " + skillType + ", click level: " + skillLevel);
		}
		Integer nextLevel = getSkillLevel(skillType, user)+1;
		if (skillLevel == nextLevel)
		{
			user.removeSkillPoints(false, 1);
			user.addSkillID(skillType, 1);
			this.menu.openSkillMenu(user);
			user.playSound("successkillclick");
		}
	}
	
	public Integer getSkillLevel(String skillType, User user)
	{
		Integer level = null;
		
		HashMap<String, Integer> skillList = user.getSkillLevelList();
		if (skillList.containsKey(skillType))
		{
			level = skillList.get(skillType);
		}
		
		return level;
	}

    public static ItemStack createClayItem(String name, boolean enabled, List<String> listLore, String... lore) 
    {
        short data = enabled ? (short) 13 : (short) 14;

        ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, data);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        
        List<String> completeLore = new ArrayList<String>();
        if (listLore != null)
        {
        	completeLore.addAll(listLore);
        }
        completeLore.addAll(Arrays.asList(lore));
        
        itemMeta.setLore(completeLore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    public static ItemStack setlastupgrade(String name, List<String> lore) 
    {
	    EnchantmentGlow glow = new EnchantmentGlow(70);
        ItemStack itemStack = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.addEnchant(glow, 1, true);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    public static ItemStack SkillPoints(boolean normallast, Integer skillpoints, Integer specialskillpoints)
    {
    	Material points = normallast ? Material.EMERALD : Material.NETHER_STAR;
    	ItemStack itemStack = new ItemStack(points, 1);
    	ItemMeta meta = itemStack.getItemMeta();
    	
    	if (normallast == true)
    	{
    		meta.setDisplayName(ColorOptions.skillsinfoachieved + "Your skillpoints: " + skillpoints);
    	} else
    	{
    		meta.setDisplayName(ColorOptions.specialskillsname + "Your special skillpoints: " + specialskillpoints);
    	}
    	
    	itemStack.setItemMeta(meta);
    	
    	return itemStack;
    }
}
