package Traits;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import Arenas.Arena;
import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Menu.Menu;
import Products.Product;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;

public class ArenaMaster extends Trait
{
	Arena arena = new Arena();
	Menu menu = new Menu();
	WorldGuard worldguard = new WorldGuard();
	Product product = new Product();
	
	public ArenaMaster()
	{
		super("ArenaMaster");
	}
	
	private Integer arenaID;
	public Player target = null;
	public ArrayList<UUID> shoutList = new ArrayList<UUID>();
	private Integer stock = null;
	private String welcomeMSG = null;
	private String possibleSellMSG = ColorOptions.messageformat + "That's one heck of an offer you have there!";
	private Integer lastSellMSG = null;
	private Integer talkDelay = 20;
	private Long nextTalk = System.currentTimeMillis()+talkDelay*1000;
	private List<UUID> greetingList = new ArrayList<UUID>();
	private Integer openItemDelay = 60;

	
	
	public void onSpawn()
	{
		Entity Enpc = npc.getEntity();
		RegionManager manager = worldguard.getRegionManager(Enpc.getWorld());
		Integer arenaID = worldguard.getStructureIDbyRegion("arena", Enpc.getLocation(), manager);
		if (arenaID != null)
		{
			this.arenaID = arenaID;
		}
		Player npc = (Player) Enpc;
		ItemStack helmet = product.createPropertyItem(product.getProductID("golemhearthelmet", false), 1, false, false);
		ItemStack chestplate = product.createPropertyItem(product.getProductID("golemheartchestplate", false), 1, false, false);
		ItemStack leggings = product.createPropertyItem(product.getProductID("golemheartleggings", false), 1, false, false);
		ItemStack boots = product.createPropertyItem(product.getProductID("golemheartboots", false), 1, false, false);
		List<ItemStack> items = Arrays.asList(helmet, chestplate, leggings, boots);
		for (ItemStack item : items)
		{
			item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		}

		npc.getInventory().setBoots(boots);
		npc.getInventory().setLeggings(leggings);
		npc.getInventory().setChestplate(chestplate);
		npc.getInventory().setHelmet(helmet);
		npc.updateInventory();
	}
	
	public void run()
	{
		if (npc.isSpawned())
		{
			Entity Enpc = npc.getEntity();
			
			List<Entity> entities = this.npc.getEntity().getNearbyEntities(5, 2, 5);
			for (Entity entity : entities)
			{
				if (entity instanceof Player)
				{
					Player target = (Player) entity;
					UUID tu = target.getUniqueId();
					
					npc.faceLocation(target.getLocation());
					String arenaName = null;
					RegionManager manager = worldguard.getRegionManager(Enpc.getWorld());
					try {
						arenaName = arena.getArena(arenaID).getString("Name");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					welcomeMSG = ColorOptions.message + "Welcome to arena " + arenaName; 
					if (worldguard.getStructureIDbyRegion("arena", target.getLocation(), manager) == arenaID)
					{
						if (!greetingList.contains(tu))
						{
							target.sendMessage(ColorOptions.messageformat + "Arena Owner: " + welcomeMSG);
							greetingList.add(tu);
						}
					} else
					{
						if (greetingList.contains(tu))
						{
							greetingList.remove(tu);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClick(NPCRightClickEvent e)
	{
		if (e.getNPC() == npc)
		{
			if (e.getClicker() instanceof Player)
			{
				Player player = (Player) e.getClicker();
				if (npc.isSpawned())
				{
					menu.openArenaMenu(player, arenaID);
					player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
				}
			}
		}
	}
}
