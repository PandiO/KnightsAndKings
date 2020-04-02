package Models.district;

import org.bukkit.command.CommandSender;

import DataManager.Districts;
import DataManager.Towns;
import Models.Town;

public class District 
{
	protected int ID;
    protected String Name;
    protected int TownID;
    protected Town Town;

    public District(int id, String name, int townID)
    {
    	this.ID = id;
    	this.Name = name;
    	this.TownID = townID;
    	this.Town = Towns.FindTown(townID);
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

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getTownID() {
        return this.TownID;
    }

    public void setTownID(int TownID) {
        this.TownID = TownID;
    }

    public Town getTown() {
        return this.Town;
    }

    public void setTown(Town Town) {
        this.Town = Town;
    }
    
	public void Dispose(CommandSender sender)
	{
		Districts.SaveDistrict(this);
		Districts.Districts.remove(this);
		Districts.DestroyDistrict(this);
	}
}
