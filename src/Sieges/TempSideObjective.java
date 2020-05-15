/**
 * 
 */
package Sieges;

import org.bukkit.Location;

/**
 * @author pandi
 *
 */
public class TempSideObjective extends TempSpawnpoint
{
	public int structureID;
	/**
	 * 
	 */
	public TempSideObjective(String name, Location location, Integer structureID) 
	{
		super (name, location);
		if (structureID != null)
		{
			this.structureID = structureID;
		}
	}

}
