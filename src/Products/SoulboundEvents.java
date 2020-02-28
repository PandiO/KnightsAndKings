package Products;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import KillsDeaths.KillDeathStat;
import Main.Main;

public class SoulboundEvents implements Listener
{
	private Main main;
	public SoulboundEvents(Main main) 
	{
		this.main = main;
	}
	
	HashMap<UUID, List<ItemStack>> keep = new HashMap<UUID, List<ItemStack>>();
	
  	@EventHandler
  	public void onPlayerDeathe(PlayerDeathEvent event)
  	{
  		List<ItemStack> removed = new ArrayList<ItemStack>();
  		List<ItemStack> ghosted = new ArrayList<ItemStack>();
  		UUID uuid = event.getEntity().getUniqueId();
  		for (ItemStack item : event.getDrops())
  		{
  			ItemMeta meta = item.getItemMeta();
  			List<String> lore = meta.getLore();
  			if ((lore != null) && (lore.contains(ChatColor.RED + "Soulbound"))) 
  			{
  				removed.add(item);
  			} else if ((lore != null) && (lore.contains(ChatColor.DARK_GRAY + "Ghosted")))
  			{
  				ghosted.add(item);
  				removed.add(item);
  			}
  		}
		if (KillDeathStat.respawn.containsKey(uuid))
		{
			List<ItemStack> list = KillDeathStat.respawn.get(uuid);
			for (ItemStack item : ghosted)
			{
				if (!list.contains(item))
				{
					list.add(item);
				}
			}
			KillDeathStat.respawn.put(uuid, list);
		} else
		{
			KillDeathStat.respawn.put(uuid, ghosted);
		}
  		event.getDrops().removeAll(removed);
  	}
  	
//  	@EventHandler
//  	public void onRespawn(PlayerRespawnEvent e)
//  	{
//  		UUID uuid = e.getPlayer().getUniqueId();
//  		if (keep.containsKey(uuid))
//  		{
//  			for (ItemStack i : keep.get(uuid))
//  			{
//  	  			e.getPlayer().getInventory().addItem(i);
//  			}
//  			e.getPlayer().updateInventory();
//  			keep.remove(uuid);
//  		}
//  	}
  	
  	@EventHandler
  	public void onDrop(PlayerDropItemEvent e)
  	{
  		ItemStack item = e.getItemDrop().getItemStack();
  		if (item.hasItemMeta() == true)
  		{
  			ItemMeta meta = item.getItemMeta();
  			if (meta.getLore() != null)
  			{
  				if (meta.getLore().contains(ChatColor.RED + "Soulbound"))
  				{
  					e.setCancelled(true);
  				}
  			}
  		}
  	}
}
