/**
 * 
 */
package Sieges;

import org.bukkit.Location;

/**
 * @author pandi
 *
 */
public class TempSideObjective 
{
	public int structureID;
	public Location location;
	/**
	 * 
	 */
	public TempSideObjective(Location location, Integer structureID) 
	{
		this.location = location;
		this.structureID = structureID;
	}

}
