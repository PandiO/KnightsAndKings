package Products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Main.Main;

public class ItemType 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	ProductMaterial material = new ProductMaterial();
	
	//Save an itemtype to the database
	public void saveItemType(Integer MaterialID, String itemType)
	{
		if (material.getMaterialIDList().contains(MaterialID))
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO ItemTypes(MaterialID, ItemType) VALUES(?, ?);");
				stmt.setInt(1, MaterialID);
				stmt.setString(2, itemType);
				
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Get the item type by id from the database
	public String getItemType(Integer id)
	{
		String type = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ItemTypes WHERE ID=?;");
			stmt.setInt(1, id);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				type = results.getString("ItemType");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return type;
	}
	
	//Get the material id from the database
	public Integer getMaterialID(Integer id)
	{
		Integer materialID = null;
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ItemTypes WHERE ID=?;");
			stmt.setInt(1, id);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				if (results.getString("MaterialID") == null)
				{
					return null;
				} else
				{
					materialID = results.getInt("MaterialID");
				}
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return materialID;
	}
	
	//Get a itemType-id from the database
	public Integer getID(String ItemType, Integer MaterialID)
	{
		Integer id = null;
		if (MaterialID != null)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ItemTypes WHERE ItemType=? AND MaterialID=?;");
				stmt.setString(1, ItemType);
				stmt.setInt(2, MaterialID);
				
				ResultSet results = stmt.executeQuery();
				if (results.next())
				{
					id = results.getInt("ID");
				}
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		} else
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ItemTypes WHERE ItemType=?;");
				stmt.setString(1, ItemType);
				
				ResultSet results = stmt.executeQuery();
				if (results.next())
				{
					id = results.getInt("ID");
				}
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		
		return id;
	}
	
	public Integer getIDbyMaterialID(String materialID)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ItemTypes WHERE BlockID=?;");
			stmt.setString(1, materialID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("ID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Get a list of all id's currently in the database
	public ArrayList<Integer> getIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ItemTypes;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("ID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//Remove an item type from the database
	public void removeItemType(Integer id)
	{
		String itemtype = this.getItemType(id);
		String materialname = material.getMaterial(this.getMaterialID(id));
		String material = materialname + "_" + itemtype;
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM ItemType WHERE ID=?;");
			stmt.setInt(1, id);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "ItemType " + material + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getBlockID(Integer typeID)
	{
		String blockID = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ItemTypes WHERE ID=?;");
			stmt.setInt(1, typeID);
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				blockID = results.getString("BlockID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return blockID;
	}
	
	public Integer getTypeIDByBlockID(String blockID)
	{
		Integer typeID = null;
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM ItemTypes WHERE BlockID=?;");
			stmt.setString(1, blockID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				typeID = results.getInt("ID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return typeID;
	}
}
