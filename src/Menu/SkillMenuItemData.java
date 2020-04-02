package Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

import Handlers.ColorOptions;
import Handlers.EnchantmentGlow;

public interface SkillMenuItemData 
{
	
	EnchantmentGlow glow = new EnchantmentGlow(70);
//Points/Back
	public static ItemMeta Points = SkillMenuItems.Skillpoints.getItemMeta();
	public static ItemMeta SpecialPoints = SkillMenuItems.SpecialSkillpoints.getItemMeta();
	public static ItemMeta back = SkillMenuItems.back.getItemMeta();
//Skills
	public static ItemMeta Strength = SkillMenuItems.Strength.getItemMeta();
	public static ItemMeta Speed = SkillMenuItems.Speed.getItemMeta();
	public static ItemMeta Health = SkillMenuItems.Health.getItemMeta();
	public static ItemMeta AttackSpeed = SkillMenuItems.AttackSpeed.getItemMeta();
	public static ItemMeta Defense = SkillMenuItems.Defense.getItemMeta();
	
	public static ItemMeta LastSkill = SkillMenuItems.lastskill.getItemMeta();
	public static ItemMeta SpecialSkill = SkillMenuItems.specialskill.getItemMeta();
	
//SpecialLast
	public static Boolean SpecialStrength = SkillMenuItems.NotEquiped.getItemMeta().addEnchant(glow, 70, true);
//Info
	String StrengthInfo1 = ColorOptions.skillsinfoachieved + "Increases your chance to get Strength for 5";
	String StrengthInfo2 = ColorOptions.skillsinfoachieved + "seconds every time you hit an enemy! Maximum chance of 42%";
	
	String SpeedInfo1 = ColorOptions.skillsinfoachieved + "Increases your walkspeed";
	String SpeedInfo2 = ColorOptions.skillsinfoachieved + "Maximum increasement walkspeed is 160%";

	String HealthInfo1 = ColorOptions.skillsinfoachieved + "Increases your health";
	String HealthInfo2 = ColorOptions.skillsinfoachieved + "If fully upgraded permanently +6 Hearts";
	
	String AttackSpeedInfo1 = ColorOptions.skillsinfoachieved + "Increases chances to";
	String AttackSpeedInfo2 = ColorOptions.skillsinfoachieved + "Inflict 50% more damage to an enemy!";
	String AttackSpeedInfo3 = ColorOptions.skillsinfoachieved + "Maximum chance of 30%";
	
	String DefenseInfo1 = ColorOptions.skillsinfoachieved + "Decreases incoming damage when";
	String DefenseInfo2 = ColorOptions.skillsinfoachieved + "hit by an enemy! Maximum decrease of 30%";
	
	
	String skillPrice = ColorOptions.skillsinfonotachieved + "You need 1 skillpoint";
	String achieved = ColorOptions.skillsinfoachieved + "Achieved!";
	
	String StrengthLore1 = ColorOptions.skillsinfonotachieved + "Increases Chance with 7%";
	String SpeedLore1 = ColorOptions.skillsinfonotachieved + "Increases speed with 10%";
	String HealthLore1 = ColorOptions.skillsinfonotachieved + "Increases health with 1 heart";
	String AttackSpeedLore1 = ColorOptions.skillsinfonotachieved + "Increases Chance with 5%";
	String DefenseLore1 = ColorOptions.skillsinfonotachieved + "Increases Chance with 5%";
	
	public List<String> StrengthLevel7 = new ArrayList<String>(Arrays.asList(
			ColorOptions.message + "25% chance to get", 
			ColorOptions.message + "Strength III for 4 seconds", 
			ColorOptions.message + "when hitting someone"
			));
	public List<String> SpeedLevel7 = new ArrayList<String>(Arrays.asList(
			ColorOptions.message + "25% chance to get",
			ColorOptions.message + "Speed II for 5 seconds",
			ColorOptions.message + "on an incoming hit"
			));
	public List<String> HealthLevel7 = new ArrayList<String>(Arrays.asList(
			ColorOptions.message + "25% chance to get",
			ColorOptions.message + "Regeneration I for 10 seconds",
			ColorOptions.message + "on an incoming hit"
			));
	public List<String> AttackSpeedLevel7 = new ArrayList<String>(Arrays.asList(
			ColorOptions.message + "25% chance to inflict",
			ColorOptions.message + "2x damage at once"
			));
	public List<String> DefenseLevel7 = new ArrayList<String>(Arrays.asList(
			ColorOptions.message + "25% chance to remove",
			ColorOptions.message + "100% incoming damage"
			));
//Points/Back
	public List<String> PointsInfo = Arrays.asList(new String[] 
			{
				ColorOptions.skillsinfoachieved + "Your skillpoints: " 
			});
	public List<String> SpecialPointsInfo = Arrays.asList(new String[] 
			{
				ChatColor.GRAY + "Achieve 10th Title or buy from the shop" 
			});
	String backinfo = ChatColor.GRAY + "Click here to go back to your personal menu";
}
