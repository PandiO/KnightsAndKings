/**
 * 
 */
package Models.spawnpoint;

import org.bukkit.Location;

/**
 * @author pandi
 *
 */
public class SpawnpointStructure extends Spawnpoint {

	protected int structureID;
	/**
	 * @param ID
	 * @param location
	 */
	public SpawnpointStructure(Integer ID, Integer structureID, Location location) {
		super(ID, location);
		this.structureID = structureID;
	}
	
	public int getStructureID() {
		return this.structureID;
	}

}
