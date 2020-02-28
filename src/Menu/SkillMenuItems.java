package Menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface SkillMenuItems 
{
//Section
	public ItemStack Strength = new ItemStack(Material.IRON_SWORD, 1);
	public ItemStack Speed = new ItemStack(Material.POTION, 1, (short) 8194);
	public ItemStack Health = new ItemStack(Material.POTION, 1, (short) 8193);
	public ItemStack AttackSpeed = new ItemStack(Material.IRON_SWORD, 1);
	public ItemStack Defense = new ItemStack(Material.IRON_CHESTPLATE, 1);
	public ItemStack Special = new ItemStack(Material.ENCHANTED_BOOK, 1);
//Equiped or not
	public ItemStack Equipedcurrent = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
	public ItemStack Equiped = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
	public ItemStack NotEquiped = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
//Rest
	public ItemStack Skillpoints = new ItemStack(Material.EMERALD, 1);
	public ItemStack SpecialSkillpoints = new ItemStack(Material.NETHER_STAR, 1);
	public ItemStack back = new ItemStack(Material.BARRIER, 1);
	public ItemStack lastskill = new ItemStack(Material.BOOK, 1);
	public ItemStack specialskill = new ItemStack(Material.ENCHANTED_BOOK, 1);
	
}
