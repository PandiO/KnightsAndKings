package Models;

import org.bukkit.command.CommandSender;

import DataManager.Streets;
import DataManager.Towns;

public class Street 
{
	protected int ID;
	protected String Name;
	protected int TownID;
	protected Town Town;
	
	public Street(int id, String name, int townID)
	{
		this.ID = id;
		this.Name = name;
		this.TownID = townID;
		this.Town = Towns.FindTown(townID);
		
		Streets.Streets.add(this);
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getName() {
		return this.Name;
	}

	public void setName(String name) {
		this.Name = name;
	}
	
	public int getTownID() {
		return this.TownID;
	}
	
	public void setTownID(int townID) {
		this.TownID = townID;
	}
	
	public Town getTown() {
		return this.Town;
	}
	
	public void setTown(Town town) {
		this.Town = town;
	}
	
	public void Dispose(CommandSender sender)
	{
		Streets.SaveStreet(this);
		Streets.Streets.remove(this);
		Streets.DestroyStreet(this);
	}
}
