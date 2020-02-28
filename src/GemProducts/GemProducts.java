package GemProducts;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import Products.Product;

public class GemProducts extends Product
{
	
	//Save the minimum price of a product to the database
	public void savePrice(Integer productID, Integer price)
	{
		Integer oldPrice = getPriceMin(productID);
		String name = getProductName(productID, false);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE GemProducts SET GemPrice=? Where ID=?;");
			stmt.setInt(1, price);
			stmt.setInt(2, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a gem-product's price with product-name: " + name + " from " + oldPrice + " to " + price);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the price of a product from the database
	public Integer getPrice(Integer productID)
	{
		Integer price = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				price = results.getInt("GemPrice");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return price;
	}
}
