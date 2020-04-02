package Models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import DataManager.Towns;
import Users.User;

public class Town 
{
	protected int ID;
	protected String Name;
	protected String Description;
	protected int RequiredTitleID;
	protected List<Integer> DiscoveredUserIDs = new ArrayList<Integer>();
	protected List<User> EnteredUsers = new ArrayList<User>();

	public Town(int id, String name, String description, int requiredTitleID, List<Integer> discoveredUserIDs)
	{
		this.ID = id;
		this.Name = name;
		this.Description = description;
		this.RequiredTitleID = requiredTitleID;
		this.DiscoveredUserIDs = discoveredUserIDs;
		
		Towns.Towns.add(this);
	}

	public int getID()
	{
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

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(String Description) {
		this.Description = Description;
	}

	public int getRequiredTitleID() {
		return this.RequiredTitleID;
	}

	public void setRequiredTitleID(int RequiredTitleID) {
		this.RequiredTitleID = RequiredTitleID;
	}

	public List<Integer> getDiscoveredUserIDs() {
		return this.DiscoveredUserIDs;
	}

	public void setDiscoveredUserIDs(List<Integer> DiscoveredUserIDs) {
		this.DiscoveredUserIDs = DiscoveredUserIDs;
	}

	public List<User> getEnteredUsers() {
		return this.EnteredUsers;
	}

	public void setEnteredUsers(List<User> EnteredUsers) {
		this.EnteredUsers = EnteredUsers;
	}
	
	public void addEnteredUser(User user)
	{
		if (!this.EnteredUsers.contains(user))
		{
			this.EnteredUsers.add(user);
		}
	}
	
	public void removeEnteredUser(User user)
	{
		if (this.EnteredUsers.contains(user))
		{
			this.EnteredUsers.remove(user);
		}
	}
	
	public void addDiscoveredUserID(Integer userID)
	{
		if (!this.DiscoveredUserIDs.contains(userID))
		{
			this.DiscoveredUserIDs.add(userID);
		}
	}
	
	public void removeDiscoveredUserID(Integer userID)
	{
		if (this.DiscoveredUserIDs.contains(userID))
		{
			this.DiscoveredUserIDs.remove(userID);
		}
	}

	public void Dispose(CommandSender sender)
	{
		Towns.SaveTown(this);
		Towns.Towns.remove(this);
		Towns.DestroyTown(this);
	}
}
