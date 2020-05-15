package Sieges;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;

import com.sk89q.worldguard.protection.managers.RegionManager;

import DataManager.Worldguard;
import DataManager.Structures.Gates;
import Main.Main;
import Models.Structures.Gate;

public class SideObjective extends Objective
{
	protected String name;
	protected int structureID;
	protected Gate gate;
	protected boolean originalGateState;
	
	public SideObjective(String name, int spawnpointID, int scenarioID, Location location)
	{
		super(scenarioID, spawnpointID, 100, DyeColor.WHITE);
		this.name = name;
		if (location != null)
		{
			RegionManager manager = Worldguard.getRegionManager(location.getWorld());

			this.structureID = ObjectUtils.defaultIfNull(Worldguard.getStructureIDbyRegion("gate", location, manager),-1);
			this.gate = Gates.instantiateGate(structureID, false);
		}
		
		if (gate != null)
		{
			this.name = this.gate.getName();
		}
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getGateID()
	{
		return this.structureID;
	}
	
	public Gate getGate()
	{
		return this.gate;
	}
	
	public Gate fetchGate()
	{
		if (this.structureID == -1)
		{
			return null;
		}
		
		Gate gate = null;
		
		gate = DataManager.Structures.Gates.findGate(this.structureID);
		
		if (gate == null)
		{
			gate = DataManager.Structures.Gates.instantiateGate(structureID, false);
		}
		
		this.gate = gate;
		
		return gate;
	}
	
	public void activate(boolean active)
	{
		if (active)
		{
			if (this.gate != null)
			{
				this.originalGateState = gate.getActive();
				this.gate.toggleActive(true);
				this.gate.setClosed(true);
			}
		} else
		{
			if (this.gate != null)
			{
				this.gate.toggleActive(this.originalGateState);
				this.gate.tryRespawnGate();
			}
		}
	}
	
	public void setGate(Gate gate)
	{
		this.gate = gate;
		this.structureID = gate.getId();
	}
	
	public void saveGate()
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE SiegeObjectives SET structureID = ? WHERE ID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, this.gate.getId());
			stmt.setInt(2, this.subID);
			
			stmt.executeUpdate();
			Main.logMessage(ChatColor.GREEN + "SideObjective  " + this.getSubID() + "'s gate with ID " + this.gate.getId() + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void removeGate()
	{
		this.gate = null;
		this.structureID = -1;
	}
	
	public void saveSideObjective()
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE SiegeObjectives SET structureID = ?, SiegeID = ?, SpawnpointID = ? WHERE ID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, this.getGateID());
			stmt.setInt(2, this.getScenarioID());
			stmt.setInt(3, this.getSpawnpointID());
			stmt.setInt(4, this.subID);
			
			stmt.executeUpdate();
			Main.logMessage(ChatColor.GREEN + "Succesfully saved SideObjective to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}
