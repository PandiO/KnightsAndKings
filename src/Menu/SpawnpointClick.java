package Menu;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import Main.Main;
import SpawnPoints.SpawnPoint;
import Users.User;

public class SpawnpointClick
{
	Menu menu = new Menu();
	SpawnPoint spawnpoint = new SpawnPoint();
	private Main main;
	public SpawnpointClick(Main main) 
	{
		this.main = main;
	}
	
	public void Onclick(InventoryClickEvent e, User user)
	{
		ItemStack clicked = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
		UUID uuid = player.getUniqueId();

		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		e.setCancelled(true);
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.OpenPersonalMenu(user);
		}
		if (dc.contains("location: "))
		{
			String targetLocation = dc.split(": ")[1];
			if (spawnpoint.getSpawnPointID(targetLocation) != null)
			{
				Integer spawnpointID = spawnpoint.getSpawnPointID(targetLocation);
				List<String> lore = clicked.getItemMeta().getLore();
				if (lore.get(1) != null && !ChatColor.stripColor(lore.get(1)).contains("Locked"))
				{
					spawnpoint.tryRegularTeleport(user, targetLocation);
					player.closeInventory();
				}
			}
		}
	}
}
