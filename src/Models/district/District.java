package Models.district;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import DataManager.Districts;
import DataManager.Towns;
import Models.Town;
import Models.Structures.Gate;
import Models.Structures.Structure;

public class District 
{
	protected int ID;
    protected String Name;
    protected int TownID;
    protected Town Town;
    protected List<Structure> Structures = new ArrayList<Structure>();

    public District(int id, String name, int townID)
    {
    	this.ID = id;
    	this.Name = name;
    	this.TownID = townID;
    	this.Town = Towns.InstantiateTown(townID, false);
    	
    	Districts.Districts.add(this);
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
    
    public List<Structure> getStructureList()
    {
    	return this.Structures;
    }
    
    public List<Gate> getGateList()
    {
    	List<Gate> list = new ArrayList<Gate>();
    	
    	for (Structure s : this.Structures)
    	{
    		if (s instanceof Gate)
    		{
    			list.add(((Gate)s));
    		}
    	}
    	
    	return list;
    }
    
    public void addStructureList(Structure structure)
    {
    	if (!this.Structures.contains(structure))
    	{
    		this.Structures.add(structure);
    	}
    }
    
    public void removeStructureList(Structure structure)
    {
    	if (this.Structures.contains(structure))
    	{
    		this.Structures.remove(structure);
    	}
    }
    
	public void Destroy(CommandSender sender)
	{
		Districts.SaveDistrict(this);
		Districts.Districts.remove(this);
		Districts.DestroyDistrict(this);
	}
}
