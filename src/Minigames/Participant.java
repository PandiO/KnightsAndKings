package Minigames;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Main.Main;
import Users.User;

public class Participant
{
	protected Main main = Main.getPlugin(Main.class);
	protected User user;
	protected MGTeam Team;
	protected Location beforeJoinLocation;
	protected HashMap<Integer, ItemStack> storedInventory = new HashMap<Integer, ItemStack>();
	
	
	public Participant(User user) 
	{
		this.user = user;
	}
	
	public User getUser()
	{
		return this.user;
	}
	
	public MGTeam GetTeam()
	{
		return this.Team;
	}
	
	public Location getBeforeJoinLocation()
	{
		return this.beforeJoinLocation;
	}
	
	public HashMap<Integer, ItemStack> getStoredInventory()
	{
		return this.storedInventory;
	}
	
	public void SetTeam(MGTeam team)
	{
		this.Team = team;
	}
	
	public void setBeforeJoinLocation(Location location)
	{
		this.beforeJoinLocation = location;
	}
	
	public void setStoredInventory()
	{
		Player player = user.getPlayer();
		for (int i = 0; i < 36; i++)
		{
			this.storedInventory.put(i, player.getInventory().getItem(i));
		}
	}
	
	public void returnBeforeJoinLocation()
	{
		if (this.beforeJoinLocation == null)
		{
			Main.logError("Participant should be returned to before join location, but location is null!");
			return;
		}
		this.user.getPlayer().teleport(this.beforeJoinLocation);
	}
	
	public void returnStoredInventory()
	{
		this.clearContents();
		Player player = this.user.getPlayer();
		for (Integer slot : this.storedInventory.keySet())
		{
			player.getInventory().setItem(slot, this.storedInventory.get(slot));
			player.updateInventory();
		}
	}
	
	public void clearContents()
	{
		for (int i = 0; i < 36; i++)
		{
			user.getPlayer().getInventory().setItem(i, null);
			user.getPlayer().updateInventory();
		}
	}
}
