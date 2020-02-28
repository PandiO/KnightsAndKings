package Products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.Menus;
import Main.Main;
import Menu.Menu;
import Menu.MenuCommand;
import Properties.Property;
import Titles.Title;
import Users.User;
import Users.Users;

public class PropertyProduct 
{
	Property property = new Property();
	ProductCategory category = new ProductCategory();
	Product product = new Product();
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	//Save a connection between a product and a property to the database
	public void savePropertyProduct(Integer propertyID, Integer productID)
	{
		Integer contribution = property.getContribution(propertyID);
		Double itemContribution = (double) (1 + (contribution/100));
		Integer priceMax = Integer.valueOf((int) (product.getPriceMax(productID) * itemContribution));
		Integer price = main.getRandom(product.getPriceMin(productID), priceMax);
		Integer grade = product.getGrade(productID, false);
		Integer amount = product.getItemAmount(contribution, grade, category.getCategoryName(product.getCategoryID(productID, false)));
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO PropertyProducts(PropertyID, ProductID, ItemAmount, Price) VALUES(?, ?, ?, ?);");
			stmt.setInt(1, propertyID);
			stmt.setInt(2, productID);
			stmt.setInt(3, amount);
			stmt.setInt(4, price);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Product with ID " + productID + " has succesfully been added to property with ID " + propertyID + " and it has been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Remove a connection from the database
	public void removePropertyProduct(Integer propertyID, Integer productID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM PropertyProducts WHERE PropertyID=? AND ProductID=?;");
			stmt.setInt(1, propertyID);
			stmt.setInt(2, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Property-product with ID " + productID + " has succesfully been removed from property with ID " + propertyID + "and it has been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Remove all connections from a specific product
	public void removeAllbyProduct(Integer productID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM PropertyProducts WHERE ProductID=?;");
			stmt.setInt(1, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "All propertyproducts for product with ID " + productID + " have succesfully been removed form the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Remove all connections from a specific property
	public void removeAllbyProperty(Integer propertyID)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("DELETE FROM PropertyProducts WHERE PropertyID=?;");
			stmt.setInt(1, propertyID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "All propertyproducts for property with ID " + propertyID + " have succesfully been removed form the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the amount of items a property sells per day of a specific product
	public Integer getProductAmount(Integer propertyID, Integer productID)
	{
		Integer amount = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE PropertyID=? AND ProductID;");
			stmt.setInt(1, propertyID);
			stmt.setInt(2, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				amount = results.getInt("ItemAmount");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return amount;
	}
	
	//Save the daily of items a property sells per day of a specific product
	public void saveDailyProductAmount(Integer propertyID, Integer productID)
	{
		Integer contribution = property.getContribution(propertyID);
		Integer grade = product.getGrade(productID, false);
		Integer amount = product.getItemAmount(contribution, grade, category.getCategoryName(product.getCategoryID(productID, false)));
		String category = this.category.getCategoryName(product.getCategoryID(productID, false));
		if (category.equalsIgnoreCase("meat") || category.equalsIgnoreCase("fish") || category.equalsIgnoreCase("baked-goods"))
		{
			amount = amount*2;
		}
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE PropertyProducts SET ItemAmount=? Where PropertyID=? AND ProductID=?;");
			stmt.setInt(1, amount);
			stmt.setInt(2, propertyID);
			stmt.setInt(3, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The daily item-amount of property-product with ID " + productID + " has succesfully been saved to " + amount + " from property with ID " + propertyID + "and it has been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void saveWeeklyProductPrice(Integer propertyID, Integer productID)
	{
		Double contribution = (double) (1 + (property.getContribution(propertyID)/100));
		Integer grade = product.getGrade(productID, false);
		Integer priceMax = Integer.valueOf((int) (product.getPriceMax(productID) * contribution));
		Integer price = main.getRandom(product.getPriceMin(productID), priceMax);
		String category = this.category.getCategoryName(product.getCategoryID(productID, false));
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE PropertyProducts SET Price=? Where PropertyID=? AND ProductID=?;");
			stmt.setInt(1, price);
			stmt.setInt(2, propertyID);
			stmt.setInt(3, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The weekly item-price of property-product with ID " + productID + " has succesfully been saved to " + price + " from property with ID " + propertyID + "and it has been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Save the amount of items a property sells per day of a specific product
	public void saveSpecialProductAmount(Integer propertyID, Integer productID, Integer amount)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE PropertyProducts SET ItemAmount=? Where PropertyID=? AND ProductID=?;");
			stmt.setInt(1, amount);
			stmt.setInt(2, propertyID);
			stmt.setInt(3, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The daily item-amount of property-product with ID " + productID + " has succesfully been saved to " + amount + " from property with ID " + propertyID + "and it has been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get a list of all product-id's a property sells
	public ArrayList<Integer> getProductListbyProperty(Integer propertyID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE PropertyID=?;");
			stmt.setInt(1, propertyID);

			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("ProductID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//Get a list of all product-id's a property sells
	public ArrayList<String> getProductNameListbyProperty(Integer propertyID)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE PropertyID=?;");
			stmt.setInt(1, propertyID);

			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(product.getProductName(results.getInt("ProductID"), false));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//Get a list of all properties which sell a product
	public ArrayList<Integer> getPropertyListbyProduct(Integer productID)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE ProductID=?;");
			stmt.setInt(1, productID);

			ResultSet results = stmt.executeQuery();
			while (results.next())
			{
				list.add(results.getInt("PropertyID"));
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	//Get the id of every product-property relation
	public ArrayList<Integer> getPropertyProductIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts;");

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
	
	//Get the propertyID of a relation
	public Integer getPropertyID(Integer relationID)
	{
		Integer id = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE ID=?;");
			stmt.setInt(1, relationID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("PropertyID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Get the productID of a relation
	public Integer getProductID(Integer relationID)
	{
		Integer id = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE ID=?;");
			stmt.setInt(1, relationID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("ProductID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Get the price of a product from a relation
	public Integer getPrice(Integer relationID)
	{
		Integer price = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE ID=?;");
			stmt.setInt(1, relationID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				price = results.getInt("Price");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return price;
	}
	
	//Get the amount of a product from a relation
	public Integer getAmount(Integer relationID)
	{
		Integer amount = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE ID=?;");
			stmt.setInt(1, relationID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				amount = results.getInt("ItemAmount");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return amount;
	}
	
	public void saveAmount(Integer relationID, Integer amount)
	{
		if (amount >= 0)
		{
			try 
			{
				PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE PropertyProducts SET ItemAmount=? Where ID=?;");
				stmt.setInt(1, amount);
				stmt.setInt(2, relationID);
				
				stmt.executeUpdate();
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void removeAmount(Integer relationID, Integer amount)
	{
		Integer oldAmount = this.getAmount(relationID);
		Integer newAmount = oldAmount-amount;
		
		if (newAmount >= 0)
		{
			this.saveAmount(relationID, newAmount);
		}
	}
	
	public Integer getRelationID(Integer productID, Integer propertyID)
	{
		Integer id = null;
		
		try
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM PropertyProducts WHERE propertyID=? AND productID=?;");
			stmt.setInt(1, propertyID);
			stmt.setInt(2, productID);
			
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
	
	public void openItemInfo(Player player, Integer relationID)
	{
		Menu menu = new Menu();
		Title title = new Title();
		ProductCategory productCategory = new ProductCategory();
		Integer productID = this.getProductID(relationID);
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}
		Integer coins = user.getCoins();
		Integer OfferAmount = this.getAmount(relationID);
		Integer itemPrice = this.getPrice(relationID);
		String displayName = product.getDisplayName(productID, false);
		ItemStack coupon = menu.getItemCoupon(player, product.getCategoryID(productID, false));
		
		Inventory info = Bukkit.createInventory(null, 9*1, ColorOptions.statsresults + displayName);

		info.setItem(0, Menus.getFinancial(user));
		info.setItem(3, MenuCommand.addpmenu(ColorOptions.stats + "Amount: 1", Material.CHEST, ChatColor.GRAY + "This is the amount of items", ChatColor.GRAY + "you buy per purchase", "", ColorOptions.messageachievement + "Click here to change the amount"));
		info.setItem(4, product.createPropertyItem(productID, 1, false, false));
		if (coins >= itemPrice)
		{
			if (this.getAmount(relationID) == 0)
			{
		    	info.setItem(5, product.createItem(ColorOptions.stats + "This item costs: " + ColorOptions.statsresults + ColorOptions.formatCurrency(itemPrice), new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + OfferAmount, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "This item is out of stock!"));
			} else
			{
		    	info.setItem(5, product.createItem(ColorOptions.stats + "This item costs: " + ColorOptions.statsresults + ColorOptions.formatCurrency(itemPrice), new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + OfferAmount, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.GREEN + "Click here to buy this item!"));
			}
		} else
		{
			if (coupon != null)
			{
		    	info.setItem(5, product.createItem(ColorOptions.stats + "This item costs: " + ColorOptions.statsresults + ColorOptions.formatCurrency(itemPrice), new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + OfferAmount, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.GREEN + "You have " + coupon.getAmount() + " coupons available"));
			} else
			{
				Integer needed = (itemPrice - user.getCoins());
		    	info.setItem(5, product.createItem(ColorOptions.stats + "This item costs: " + ColorOptions.statsresults + ColorOptions.formatCurrency(itemPrice), new ItemStack(Material.NAME_TAG, 1), false, ColorOptions.stats + "Stock: " + ColorOptions.statsresults + OfferAmount, ChatColor.GRAY + "The price and stock amount", ChatColor.GRAY + "may change per day", ChatColor.RED + "You need " + needed + " more coins!"));
			}
		}
    	info.setItem(6, product.createItem(ColorOptions.stats + "Description of this item:", new ItemStack(Material.EMPTY_MAP), false, ColorOptions.statsresults + product.getDescription(productID, false), ColorOptions.stats + "Category: " + ColorOptions.statsresults + productCategory.getCategoryName(product.getCategoryID(productID, false))));
		info.setItem(7, product.createItem(ColorOptions.stats + "Knowledge required for this item: " + ColorOptions.statsresults + "/", new ItemStack(Material.BOOK, 1), false));
    	info.setItem(8, MenuCommand.addpmenu(ColorOptions.falsecommand + "Exit", Material.BARRIER, ChatColor.GRAY + "Exit item information"));

    	for (int i = 0; i < info.getSize(); i++)
    	{
    		if (info.getItem(i) == null || info.getItem(i).getType() == Material.AIR)
    		{
    			if (i == 3 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 21)
    			{
    				info.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0)));
    			} else
    			{
    				info.setItem(i, MenuCommand.addemptypmenu(" ", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0)));
    			}
    		}
    	}
    	player.openInventory(info);
	}
}
