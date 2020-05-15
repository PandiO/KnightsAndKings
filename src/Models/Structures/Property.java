/**
 * 
 */
package Models.Structures;

import org.bukkit.Location;

import DataManager.Users2;
import DataManager.spawnpoints.SpawnpointStructures;
import Users.User;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

/**
 * @author pandi
 *
 */
public class Property extends Structure implements Buyable {

	protected int ownerID;
	protected User owner;
	protected int price;
	protected int income;
	protected int level;
	protected int contribution;
	protected int npcID;
	protected NPC npc;
	protected int npcSpawnpointID;
	protected Location npcSpawnpoint;
	protected int categoryID;
	
	/**
	 * @param id
	 * @param name
	 * @param streetID
	 * @param streetNumber
	 * @param townID
	 * @param districtID
	 * @param spawnpoint
	 */
	public Property(int id, 
			String name, 
			int streetID, 
			int streetNumber, 
			int townID, 
			int districtID,
			int ownerID,
			int price,
			int income,
			int level,
			int contribution,
			int npcID,
			int npcSpawnpointID,
			int categoryID
			) {
		super(id, name, streetID, streetNumber, townID, districtID, SpawnpointStructures.InstantiateSpawnpointStructure(id));
		
		this.ownerID = ownerID;
		this.owner = Users2.FindUser(Users2.FetchUUIDbyID(ownerID));
		this.price = price;
		this.income = income;
		this.level = level;
		this.contribution = contribution;
		this.npcID = npcID;
		this.npc = CitizensAPI.getNPCRegistry().getById(npcID);
		this.npcSpawnpointID = npcSpawnpointID;
		//this.npcSpawnpoint = 
		this.categoryID = categoryID;
	}

	/* (non-Javadoc)
	 * @see Models.Structures.Buyable#getPrice()
	 */
	@Override
	public int getPrice() {
		return this.price;
	}

	/* (non-Javadoc)
	 * @see Models.Structures.Buyable#setPrice(int)
	 */
	@Override
	public void setPrice(int price) {
		this.price = price;
	}

	/* (non-Javadoc)
	 * @see Models.Structures.Buyable#getOwnerID()
	 */
	@Override
	public int getOwnerID() {
		return this.ownerID;
	}

	/* (non-Javadoc)
	 * @see Models.Structures.Buyable#setOwnerID(int)
	 */
	@Override
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}

	/* (non-Javadoc)
	 * @see Models.Structures.Buyable#getOwner()
	 */
	@Override
	public User getOwner() {
		return this.owner;
	}

	/* (non-Javadoc)
	 * @see Models.Structures.Buyable#setOwner(users.User)
	 */
	@Override
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public int getIncome() {
		return this.income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getContribution() {
		return this.contribution;
	}

	public void setContribution(int contribution) {
		this.contribution = contribution;
	}

	public int getNpcID() {
		return this.npcID;
	}

	public void setNpcID(int npcID) {
		this.npcID = npcID;
	}

	public NPC getNpc() {
		return this.npc;
	}

	public void setNpc(NPC npc) {
		this.npc = npc;
	}

	public int getCategoryID() {
		return this.categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}
}
