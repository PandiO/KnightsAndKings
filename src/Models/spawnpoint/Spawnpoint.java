package Models.spawnpoint;

import org.bukkit.Location;

import com.mysql.jdbc.PreparedStatement;

import DataManager.spawnpoints.Spawnpoints;
import Main.Main;

public class Spawnpoint 
{
	protected int ID;
	protected Location location;
	public Spawnpoint(Integer ID, Location location) {
		this.ID = ID;
		this.location = location;
		
		Spawnpoints.Spawnpoints.add(this);
	}
	
	public int getID() {
		return this.ID;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
}
