package Products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Main.Main;

public class ProductMaterial 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	//Save a new material to the database
	public void saveMaterial(Integer ID, String Material)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Materials(ID, Material) VALUES(?, ?);");
			stmt.setInt(1, ID);
			stmt.setString(2, Material);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the material that matches the id
	public String getMaterial(Integer materialID)
	{
		String material = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Materials WHERE ID=?;");
			stmt.setInt(1, materialID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				material = results.getString("Material");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return material;
	}
	
	//Remove a material from the database
	public void removeMaterial(Integer ID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM Materials WHERE ID=?;");
			stmt.setInt(1, ID);
			
			stmt.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the ID matching the material
	public Integer getMaterialID(String material)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Materials WHERE Material=?;");
			stmt.setString(1, material);
			
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
	
	//Get a list of all ID's from the database
	public ArrayList<Integer> getMaterialIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Materials;");
			
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
	
	//Get a list of all Names from the database
	public ArrayList<String> getMaterialNameList()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Materials;");
			
			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getString("Material"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
}
