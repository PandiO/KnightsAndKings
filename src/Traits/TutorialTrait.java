package Traits;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import Handlers.ColorOptions;
import Main.Main;
import Products.Product;
import Tutorial.Tutorial;
import Tutorial.TutorialEvents;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.ai.event.NavigationStuckEvent;
import net.citizensnpcs.api.trait.Trait;

public class TutorialTrait extends Trait
{
	Main main = Main.getPlugin(Main.class);
	Product product = new Product();
	public TutorialTrait()
	{
		super("Tutorial");
	}
	
	public void onSpawn()
	{
		Entity Enpc = npc.getEntity();
		Player npc = (Player) Enpc;
		ItemStack helmet = product.createPropertyItem(product.getProductID("chainmailhelmet", false), 1, false, false);
		ItemStack chestplate = product.createPropertyItem(product.getProductID("chainmailchestplate", false), 1, false, false);
		ItemStack leggings = product.createPropertyItem(product.getProductID("chainmailleggings", false), 1, false, false);
		ItemStack boots = product.createPropertyItem(product.getProductID("chainmailboots", false), 1, false, false);
		List<ItemStack> items = Arrays.asList(helmet, chestplate, leggings, boots);
		for (ItemStack item : items)
		{
			item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		}

		npc.getInventory().setBoots(boots);
		npc.getInventory().setLeggings(leggings);
		npc.getInventory().setChestplate(chestplate);
		npc.getInventory().setHelmet(helmet);
		npc.updateInventory();
		npc.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 2));
	}
	
	public void run()
	{
		if (npc.isSpawned())
		{
			Entity Enpc = npc.getEntity();
			
			List<Entity> entities = this.npc.getEntity().getNearbyEntities(10, 2, 10);
			for (Entity entity : entities)
			{
				if (entity instanceof Player)
				{
					Player target = (Player) entity;
					UUID tu = target.getUniqueId();
					
					if(npc.getNavigator().getTargetType() != TargetType.LOCATION)
					{
						npc.faceLocation(target.getLocation());
					} else
					{
						if (main.debug)
						{
							Bukkit.getConsoleSender().sendMessage("Walking to destination..");
						}
					}
				}
			}
		}
	}
	
	public void onDespawn()
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Despawned an npc called " + npc.getName() + " with ID" + npc.getId());
		}
	}
	
	@EventHandler
	public void onCancel(NavigationCancelEvent e)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("NPC with name " + npc.getName() + " with ID " + npc.getId() + " stuck: " + e.getCancelReason().name());
		}
		for (Tutorial tut : TutorialEvents.tutorials)
		{
			if (tut.npc == npc)
			{
				tut.cancel(Arrays.asList(ColorOptions.error + "Something went wrong, please contact a staff-member"));
			}
		}
	}
	
	@EventHandler
	public void onStuck(NavigationStuckEvent e)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("NPC with name " + npc.getName() + " with ID " + npc.getId() + " stuck: " + e.getAction().toString());
		}
		for (Tutorial tut : TutorialEvents.tutorials)
		{
			if (tut.npc == npc)
			{
				tut.cancel(Arrays.asList(ColorOptions.error + "Something went wrong, please contact a staff-member"));
			}
		}
	}
	
	@EventHandler
	public void onComplete(NavigationCompleteEvent e)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("NPC with name " + npc.getName() + " with ID " + npc.getId() + " arrived");
		}
		for (Tutorial tut : TutorialEvents.tutorials)
		{
			if (tut.npc == npc && tut.getName().equalsIgnoreCase("armor tutorial"))
			{
				if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("Armor tutorial detected!");
				}
				tut.nextStage(null, null);
			}
		}
	}
}
