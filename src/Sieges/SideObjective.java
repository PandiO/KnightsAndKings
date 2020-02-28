package Sieges;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import Gates.Gate;

public class SideObjective extends Objective
{
	protected int GateID;
	protected Gate gate;
	protected boolean originalGateState;
	
	public SideObjective(int subID, int siegeID, int spawnpointID, int gateID)
	{
		super(siegeID, spawnpointID, 100, DyeColor.WHITE);
		this.subID = subID;
		this.GateID = gateID;
	}
	
	public int getGateID()
	{
		return this.GateID;
	}
	
	public Gate getGate()
	{
		return this.gate;
	}
	
	public Gate fetchGate()
	{
		if (this.GateID == -1)
		{
			return null;
		}
		
		Gate gate = null;
		
		gate = Gates.Gates.findGate(this.GateID);
		
		if (gate == null)
		{
			gate = Gates.Gates.instantiateGate(GateID, false);
		}
		
		this.gate = gate;
		
		return gate;
	}
	
	public void activate(boolean active)
	{
		if (active)
		{
			this.originalGateState = gate.getActive();
			this.gate.toggleActive(true);
		} else
		{
			this.gate.toggleActive(this.originalGateState);
			this.gate.tryRespawnGate();
		}
	}
	
	public void setGate(Gate gate)
	{
		this.gate = gate;
		this.GateID = gate.getID();
	}
	
	public void saveGate()
	{	
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE SiegeObjectives SET GateID = ? WHERE ID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, this.gate.getID());
			stmt.setInt(2, this.subID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "SideObjective  " + this.getSubID() + "'s gate with ID " + this.gate.getID() + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void removeGate()
	{
		this.gate = null;
		this.GateID = -1;
	}
	
	public void saveSideObjective()
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE SiegeObjectives SET GateID = ?, SiegeID = ?, SpawnpointID = ? WHERE ID = ?;");
			//Username will be saved in all lower case in order to prevent discommunication when searching for the a username with capital letters
			stmt.setInt(1, this.getGateID());
			stmt.setInt(2, this.getSiegeID());
			stmt.setInt(3, this.getSpawnpointID());
			stmt.setInt(4, this.subID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Succesfully saved SideObjective to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}
