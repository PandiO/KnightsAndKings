package Models.Structures;

import org.bukkit.command.CommandSender;

import DataManager.Users;
import DataManager.Structures.Structures;
import DataManager.spawnpoints.SpawnpointStructures;
import Users.User;

public class House extends Structure implements Buyable
{
	protected int ownerID;
	protected User owner;
	protected int price;
	
	public House(int id, 
			String name,
			int streetID,
			int streetNumber,
			int townID,
			int districtID,
			int ownerID,
			int price)
	{
		super(id, 
				name, 
				streetID, 
				streetNumber, 
				townID, 
				districtID, 
				SpawnpointStructures.InstantiateSpawnpointStructure(id));
		
		this.ownerID = ownerID;
		this.owner = Users.FindUser(ownerID);
		this.price = price;
		
		Structures.Structures.add(this);
	}
	
	@Override
	public int getOwnerID() {
		return this.ownerID;
	}
	
	@Override
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}
	
	@Override
	public User getOwner() {
		return this.owner;
	}
	
	@Override
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	@Override
	public int getPrice() {
		return this.price;
	}
	
	@Override
	public void setPrice(int price) {
		this.price = price;
	}
	
	public void Dispose(CommandSender sender)
	{
		//Streets.SaveStreet(this);
		Structures.Structures.remove(this);
		//Streets.DestroyStreet(this);
	}
}
