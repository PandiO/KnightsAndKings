package Products;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import Handlers.ColorOptions;
import Handlers.EnchantmentGlow;
import Main.Main;
import Properties.Property;
import Properties.PropertyCategory;
import Resources.YmlFile;
import Users.User;

public class Product 
{
	protected Property property = new Property();
	protected ProductCategory productCategory = new ProductCategory();
	protected PropertyCategory propertyCategory = new PropertyCategory();
	public int drop1 = 80;
	public int drop2 = 70;
	public int drop3 = 60;
	public int drop4 = 10;
	public int drop5 = 1;
	//Get instances of required classes
	protected Main main = Main.getPlugin(Main.class);
	
	//Save a product to the database
	public void saveProduct(String name, String displayName, Integer categoryID, Integer itemTypeID, Integer priceMin, Integer priceMax, Integer Grade, String description, String Enchantments, String stringLore)
	{
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("INSERT INTO Products(Name, CategoryID, ItemTypeID, PriceMin, PriceMax, Description, Grade, Enchantments, DisplayName, Lore) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			stmt.setString(1, name.toLowerCase());
			stmt.setInt(2, categoryID);
			stmt.setInt(3, itemTypeID);
			stmt.setInt(4, priceMin);
			stmt.setInt(5, priceMax);
			stmt.setString(6, description);
			stmt.setInt(7, Grade);
			stmt.setString(8, Enchantments);
			stmt.setString(9, displayName);
			stmt.setString(10, stringLore);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New product " + name + " has succesfully been saved to the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the name of a product from the database
	public String getProductName(Integer productID, boolean gemproduct)
	{
		String name = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				name = results.getString("Name");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return name;
	}
	
	//Save the name of a product to the database
	public void saveName(Integer ID, String name, boolean gemproduct)
	{
		String oldName = getProductName(ID, gemproduct);
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("UPDATE GemProducts SET Name=? Where ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("UPDATE Products SET Name=? Where ID=?;");
			}
			
			stmt.setString(1, name);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a product's name with product-ID: " + ID + " from " + oldName + " to " + name);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the id of product from the database
	public Integer getProductID(String name, boolean gemproduct)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE Name=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE Name=?;");
			}
			stmt.setString(1, name);
			
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
	
	//Get the id of product from the database
	public Integer getProductIDbyDisplayName(String displayName, boolean gemproduct)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE DisplayName=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE DisplayName=?;");
			}
			stmt.setString(1, displayName);
			
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
	
	public Integer getProductIDbyItemTypeID(Integer itemtypeID)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ItemTypeID=?;");

			stmt.setInt(1, itemtypeID);
			
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
	
	public List<Integer> getProductsbyItemTypeID(Integer typeID, boolean gemproduct)
	{
		List<Integer> list = new ArrayList<Integer>();
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ItemTypeID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ItemTypeID=?;");
			}
			stmt.setInt(1, typeID);
			
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
	
	//Remove a product from the database
	public void removeProduct(Integer productID, boolean gemproduct)
	{
		String name = getProductName(productID, gemproduct);
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("DELETE FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("DELETE FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Product " + name + " has succesfully been removed from the database!");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the category id of a product from the database
	public Integer getCategoryID(Integer productID, boolean gemproduct)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("CategoryID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Save the category ID of a product to the database
	public void saveCategoryID(Integer productID, Integer categoryID, boolean gemproduct)
	{
		Integer oldID = getCategoryID(productID, gemproduct);
		String name = getProductName(productID, gemproduct);
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("UPDATE GemProducts SET CategoryID=? Where ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("UPDATE Products SET CategoryID=? Where ID=?;");
			}
			stmt.setInt(1, categoryID);
			stmt.setInt(2, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a product's categoryID with product-name: " + name + " from " + oldID + " to " + categoryID);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getDisplayName(Integer productID, boolean gemproduct)
	{
		String display = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				display = results.getString("DisplayName");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return display;
	}
	
	//Get the itemType id of a product from the database
	public Integer getitemTypeID(Integer productID, boolean gemproduct)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("ItemTypeID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Save the itemType ID of a product to the database
	public void saveitemTypeID(Integer ID, Integer itemTypeID, boolean gemproduct)
	{
		Integer oldID = getitemTypeID(ID, gemproduct);
		String name = getProductName(ID, gemproduct);
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("UPDATE GemProducts SET ItemTypeID=? Where ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("UPDATE Products SET ItemTypeID=? Where ID=?;");
			}
			stmt.setInt(1, itemTypeID);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a product's itemTypeID with product-name: " + name + " from " + oldID + " to " + itemTypeID);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the Limited id of a product from the database
	public Integer getLimitedID(Integer productID, boolean gemproduct)
	{
		Integer id = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				id = results.getInt("LimitedID");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return id;
	}
	
	//Save the limited ID of a product to the database
	public void saveLimitedID(Integer ID, Integer limitedID, boolean gemproduct)
	{
		Integer oldID = getLimitedID(ID, gemproduct);
		String name = getProductName(ID, gemproduct);
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("UPDATE GemProducts SET LimitedID=? Where ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("UPDATE Products SET LimitedID=? Where ID=?;");
			}
			stmt.setInt(1, limitedID);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a product's limitedID with product-name: " + name + " from " + oldID + " to " + limitedID);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the grade of a product
	public Integer getGrade(Integer productID, boolean gemproduct)
	{
		Integer grade = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				grade = results.getInt("Grade");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return grade;
	}
	
	//Get the minimum price of a product from the database
	public Integer getPriceMin(Integer productID)
	{
		Integer price = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				price = results.getInt("PriceMin");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return price;
	}
	
	//Save the minimum price of a product to the database
	public void savePriceMin(Integer productID, Integer priceMin)
	{
		Integer oldPrice = getPriceMin(productID);
		String name = getProductName(productID, false);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Products SET PriceMin=? Where ID=?;");
			stmt.setInt(1, priceMin);
			stmt.setInt(2, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a product's minimum price with product-name: " + name + " from " + oldPrice + " to " + priceMin);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the maximum price of a product from the database
	public Integer getPriceMax(Integer productID)
	{
		Integer price = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				price = results.getInt("PriceMax");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return price;
	}
	
	//Save the maximum price of a product to the database
	public void savePriceMax(Integer productID, Integer priceMax)
	{
		Integer oldPrice = getPriceMin(productID);
		String name = getProductName(productID, false);
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("UPDATE Products SET PriceMax=? Where ID=?;");
			stmt.setInt(1, priceMax);
			stmt.setInt(2, productID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a product's maximum price with product-name: " + name + " from " + oldPrice + " to " + priceMax);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get the description of a product from the database
	public String getDescription(Integer productID, boolean gemproduct)
	{
		String desc = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				desc = results.getString("Description");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return desc;
	}
	
	//Save the name of a product to the database
	public void saveDescription(Integer ID, String description, boolean gemproduct)
	{
		String oldDesc = getDescription(ID, gemproduct);
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("UPDATE GemProducts SET Description=? Where ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("UPDATE Products SET Description=? Where ID=?;");
			}
			stmt.setString(1, description);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a product's description with product-ID: " + ID + " from " + oldDesc + " to " + description);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Save the grade of a product to the database
	public void saveGrade(Integer ID, Integer grade, boolean gemproduct)
	{
		Integer oldGrade = getGrade(ID, gemproduct);
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("UPDATE GemProducts SET Grade=? Where ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("UPDATE Products SET Grade=? Where ID=?;");
			}
			stmt.setInt(1, grade);
			stmt.setInt(2, ID);
			
			stmt.executeUpdate();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Updated a product's Grade with product-ID: " + ID + " from " + oldGrade + " to " + grade);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	//Get a list of product id's from the database
	public ArrayList<Integer> getIDList(boolean onlyactive, Integer categoryID, boolean includeGemProducts)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try 
		{
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM Products");
			if (onlyactive)
			{
				builder.append(" WHERE Active=1");
			}
			if (categoryID != null && categoryID != -1)
			{
				builder.append(" AND CategoryID='" + categoryID + "'");
			}
			if (!includeGemProducts && categoryID != null && categoryID != 13)
			{
				builder.append(" AND CategoryID != '" + 13 + "'");
			}
			builder.append(" ORDER BY CategoryID, Grade, ItemTypeID;");
			PreparedStatement stmt = main.getConnection().prepareStatement(builder.toString());
			
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
	
	public Map<Enchantment, Integer> getEnchantments(Integer productID, boolean gemproduct)
	{
		Map<Enchantment, Integer> list = new HashMap<Enchantment, Integer>();
		String enchantcode = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				enchantcode = results.getString("Enchantments");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		if (enchantcode != null)
		{
			String[] splitcode = enchantcode.split(" ");
			for (String code : splitcode)
			{
				if (code.isEmpty() == false)
				{
					String[] split = code.split(":");
					list.put(Enchantment.getById(Integer.valueOf(split[0])), Integer.valueOf(split[1]));
				}
			}
		}
		
		return list;
	}
	
	public ArrayList<String> getLore(Integer productID, boolean gemproduct)
	{
		ArrayList<String> list = new ArrayList<String>();
		String stringlore = null;
		
		try 
		{
			PreparedStatement stmt = null;
			if (gemproduct)
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM GemProducts WHERE ID=?;");
			} else
			{
				stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			}
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				stringlore = results.getString("Lore");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		if (stringlore != null)
		{
			String[] splitline = stringlore.split(",");
			for (String line : splitline)
			{
				list.add(line);
			}
		}
		
		return list;
	}
	
	public ItemStack setItemDescription(ItemStack item, Integer amount, String displayname, List<String> lore)
	{
		
		ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);
        if (lore != null)
        {
            itemMeta.setLore(lore);
        }

        item.setItemMeta(itemMeta);
        
        return item;
	}
	
	public ItemStack addItemDescription(ItemStack item, List<String> lore)
	{
		if (item != null)
		{
			ItemMeta itemMeta = item.getItemMeta();

			List<String> currentlore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();
	        currentlore.addAll(lore);
	        itemMeta.setLore(currentlore);

	        try
	        {
		        item.setItemMeta(itemMeta);
	        } catch (Exception ex)
	        {
	        	ex.printStackTrace();
	        }
		}
        
        return item;
	}
	
	public ItemStack addCoinBet(ItemStack item, Integer coins)
	{
		ItemMeta itemMeta = item.getItemMeta();

		Integer currentcoins = 0;
		List<String> currentlore = itemMeta.getLore();
		
		boolean contains = false;
        for (String line : currentlore)
        {
        	if (ChatColor.stripColor(line).contains("Current bet: "))
        	{
        		Integer oldcoins = Integer.valueOf(line.split(": ")[1]);
        		currentcoins = oldcoins;
        		currentlore.remove(line);
        		contains = true;
                break;
        	}
        }
        if (contains == false)
        {
        	currentlore.add(ColorOptions.coinStats + "Current bet: " + coins);
        } else
        {
        	currentlore.add(ColorOptions.coinStats + "Current bet: " + (currentcoins+coins));
        }
        
        itemMeta.setLore(currentlore);

        item.setItemMeta(itemMeta);
        
        return item;
	}
	
	public Integer getItemAmount(Integer contribution, Integer grade, String productCategory)
	{
		Integer amount = 1;
		
		if (grade == 1)
		{
			switch (contribution)
			{
			case 5: amount = main.getRandom(3, 10);
					break;
			case 10: amount = main.getRandom(8, 15);
					break;
			case 15: amount = main.getRandom(13, 20);
					break;
			case 20: amount = main.getRandom(18, 30);
					break;
			case 30: amount = main.getRandom(20, 40);
					break;
			}
		} else
		if (grade == 2)
		{
			switch (contribution)
			{
			case 5: amount = main.getRandom(3, 8);
					break;
			case 10: amount = main.getRandom(5, 10);
					break;
			case 15: amount = main.getRandom(10, 20);
					break;
			case 20: amount = main.getRandom(12, 30);
					break;
			case 30: amount = main.getRandom(12, 30);
					break;
			}
		} else
		if (grade == 3)
		{
			switch (contribution)
			{
			case 5: amount = main.getRandom(1, 2);
					break;
			case 10: amount = main.getRandom(2, 8);
					break;
			case 15: amount = main.getRandom(8, 15);
					break;
			case 20: amount = main.getRandom(10, 18);
					break;
			case 30: amount = main.getRandom(12, 18);
					break;
			}
		} else
		if (grade == 4)
		{
			switch (contribution)
			{
			case 5: amount = main.getRandom(0, 0);
					break;
			case 10: amount = main.getRandom(0, 4);
					break;
			case 15: amount = main.getRandom(1, 4);
					break;
			case 20: amount = main.getRandom(2, 8);
					break;
			case 30: amount = main.getRandom(4, 10);
					break;
			}
		} else
		if (grade == 5)
		{
			switch (contribution)
			{
			case 5: amount = main.getRandom(0, 0);
					break;
			case 10: amount = main.getRandom(0, 0);
					break;
			case 15: amount = main.getRandom(0, 2);
					break;
			case 20: amount = main.getRandom(0, 6);
					break;
			case 30: amount = main.getRandom(2, 6);
					break;
			}
		}
		
		if (productCategory.equalsIgnoreCase("meat") || productCategory.equalsIgnoreCase("fish") || productCategory.equalsIgnoreCase("baked-goods") || productCategory.equalsIgnoreCase("furniture"))
		{
			amount = amount*20;
		}
		
		return amount;
	}
	
    public ItemStack createItem(String displayname, ItemStack item, Boolean enchanted, String... lore) 
    {
	    EnchantmentGlow glow = new EnchantmentGlow(70);
        ItemMeta itemMeta = item.getItemMeta();
        
    	if (enchanted == true)
    	{
            itemMeta.addEnchant(glow, 1, true);
    	}
    	
        itemMeta.setDisplayName(displayname);
        if (lore.length > 0)
        {
            itemMeta.setLore(Arrays.asList(lore));
        }

        item.setItemMeta(itemMeta);

        return item;
    }
    
//    public static ItemStack addGlow(ItemStack item){
//        net.minecraft.server.v1__R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
//        NBTTagCompound tag = null;
//        if (!nmsStack.hasTag()) {
//            tag = new NBTTagCompound();
//            nmsStack.setTag(tag);
//        }
//        if (tag == null) tag = nmsStack.getTag();
//        NBTTagList ench = new NBTTagList();
//        tag.set("ench", ench);
//        nmsStack.setTag(tag);
//        return CraftItemStack.asCraftMirror(nmsStack);
//    }
    
    public ItemStack createAmountItem(Material Material, Integer amount, String displayname, String... lore) 
    {
    	ItemStack item = new ItemStack(Material, amount);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);
        itemMeta.setLore(Arrays.asList(lore));

        item.setItemMeta(itemMeta);

        return item;
    }
    
    public ItemStack createClayItem(String name, boolean enabled, String... lore) 
    {
        short data = enabled ? (short) 13 : (short) 14;

        ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, data);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    
    public ItemStack createMap(String name, World world, String... lore)
    {
    	ItemStack map = new ItemStack(Material.MAP, 1);
    	MapView view = Bukkit.createMap(world);
    	ItemMeta meta = map.getItemMeta();
    	meta.setDisplayName(name);
    	meta.setLore(Arrays.asList(lore));
    	map.setItemMeta(meta);
    	map.setDurability(view.getId());
    	
    	return map;
    }
    
    public ItemStack createPropertyItem(Integer productID, Integer amount, boolean gemproduct, boolean productInfo)
    {
    	ItemType type = new ItemType();
    	ProductMaterial itemMaterial = new ProductMaterial();

    	ItemStack item = null;
    	Material material = null;
    	Short data = null;
    	String displayName = null;
    	ArrayList<String> lore = null;
    	Map<Enchantment, Integer> enchantments = null;
    	Integer blockID = null;
    	    	
		Integer itemtypeID = this.getitemTypeID(productID, gemproduct);
		Integer materialID = type.getMaterialID(itemtypeID);
		String blockIDString = type.getBlockID(itemtypeID);
		
		//Get the blockID (the material ID in numbers)
		if (blockIDString != null)
		{
			if (blockIDString.contains(":"))
			{
				data = Short.valueOf(blockIDString.split(":")[1]);
				blockID = Integer.valueOf(blockIDString.split(":")[0]);
			} else
			{
				data = 0;
				blockID = Integer.valueOf(blockIDString);
			}
		}
		
		if (Material.getMaterial(blockID) != null)
		{
			material = Material.getMaterial(blockID);
		} else
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "The blockID is not valid! Stopping task!");
			return null;
		}
		
		displayName = this.getDisplayName(productID, gemproduct);
		lore = this.getLore(productID, gemproduct);
		
		
		item = new ItemStack(material, amount, (short) data);
		
		enchantments = this.getEnchantments(productID, gemproduct);
		if (!enchantments.isEmpty())
		{
			for (Enchantment ench : enchantments.keySet())
			{
				item.addUnsafeEnchantment(ench, enchantments.get(ench));
			}
		}
		if (productInfo)
		{
			Integer priceMin = this.getPriceMin(productID);
			Integer priceMax = this.getPriceMax(productID);
			String description = this.getDescription(productID, gemproduct);
			
			lore.addAll(Arrays.asList(
					"",
					ColorOptions.message + "Category: " + this.productCategory.getCategoryName(this.getCategoryID(productID, gemproduct))
					));
			if (priceMin.equals(priceMax))
			{
				lore.add(ColorOptions.message + "Price: " + ColorOptions.coinStats + ColorOptions.formatCurrency(priceMax));
			} else
			{
				lore.addAll(Arrays.asList(
					ColorOptions.message + "Max. price: " + ColorOptions.coinStats + ColorOptions.formatCurrency(this.getPriceMax(productID)),
					ColorOptions.message + "Min. price: " + ColorOptions.coinStats + ColorOptions.formatCurrency(this.getPriceMin(productID))
					));
			}
			if (description != null)
			{
				lore.add(ColorOptions.message + "Description: ");
				lore.addAll(this.getStringLines(this.getDescription(productID, gemproduct), 5, ColorOptions.message));
			}
		}
		item = this.setItemDescription(item, amount, displayName, this.getGradeLore(productID, lore, gemproduct));
    	
    	return item;
    }
    
    public ItemStack createEnchantItem(Integer productID, Map<Enchantment, Integer> enchantments, Integer amount, boolean gemproduct)
    {
    	ItemStack item = null;
    	
    	ItemType type = new ItemType();
    	ProductMaterial itemMaterial = new ProductMaterial();
    	
		Integer itemtypeID = this.getitemTypeID(productID, gemproduct);
		Integer materialID = type.getMaterialID(itemtypeID);
		String material = Material.BARRIER.toString();
		if (materialID != null)
		{
			material = itemMaterial.getMaterial(materialID) + "_" + type.getItemType(itemtypeID);
		} else
		{
			material = type.getItemType(itemtypeID);
		}
		
		String displayName = this.getDisplayName(productID, gemproduct);
		ArrayList<String> lore = this.getLore(productID, gemproduct);
		
		
		item = new ItemStack(Material.valueOf(material.toUpperCase()), amount);
		Map<Enchantment, Integer> enchantmentlist = this.getEnchantments(productID, gemproduct);
		if (!enchantmentlist.isEmpty())
		{
			for (Enchantment ench : enchantmentlist.keySet())
			{
				item.addUnsafeEnchantment(ench, enchantmentlist.get(ench));
			}
		}
		if (!enchantments.isEmpty())
		{
			for (Enchantment ench : enchantments.keySet())
			{
				item.addUnsafeEnchantment(ench, enchantments.get(ench));
			}
		}
		
		ItemStack finalitem = this.setItemDescription(item, amount, displayName, this.getGradeLore(productID, lore, gemproduct));
    	
    	return finalitem;
    }
    
	public String getPropertyCategorybyProduct(Integer categoryID)
	{
		String category = null;
		
		String propertyCat = productCategory.getCategoryName(categoryID);
		
		switch(propertyCat.toLowerCase())
		{
		case "swords": category = "weaponry";
		break;
		case "armor": category = "armory";
		break;
		case "bows": category = "archery";
		break;
		case "jewelery": category = "jewelery";
		break;
		case "fish": category = "fishery";
		break;
		case "meat": category = "butchery";
		break;
		case "baked-goods": category = "bakery";
		break;
		case "vegetables": category = "grocery";
		break;
		case "furniture": category = "furnitury";
		break;
		case "magic": category = "witchery";
		break;
		case "resources": category = "resources";
		break;
		case "tools": category = "resources";
		break;
		}
		
		return category;
	}
	
	public boolean canProductbeSold(Integer productID, Integer propertyID, boolean gemproduct)
	{
		boolean sellable = false;
		
		Integer productCategoryID = this.getCategoryID(productID, gemproduct);
		Integer propertyCategoryID = property.getCategoryID(propertyID);
		String productName = this.getProductName(productID, false);
		String propertyCategory = this.propertyCategory.getCategoryName(propertyCategoryID);
		String productCategory = this.productCategory.getCategoryName(productCategoryID);
		if (propertyCategory.equalsIgnoreCase("weaponry"))
		{
			switch(productCategory.toLowerCase())
			{
			case "swords": sellable = true;
			break;
			}
			switch(productName.toLowerCase())
			{
			case "ironingot": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("armory"))
		{
			switch(productCategory.toLowerCase())
			{
			case "armor": sellable = true;
			break;
			}
			switch(productName.toLowerCase())
			{
			case "ironingot": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("archery"))
		{
			switch(productCategory.toLowerCase())
			{
			case "bows": sellable = true;
			break;
			}
			if (productName.contains("wood"))
			{
				sellable = true;
			}
		}
		if (propertyCategory.equalsIgnoreCase("jewelery"))
		{
			switch(productCategory.toLowerCase())
			{
			case "jewelery": sellable = true;
			break;
			}
			switch(productName.toLowerCase())
			{
			case "ironingot": sellable = true;
			break;
			case "goldingot": sellable = true;
			break;
			case "diamond": sellable = true;
			break;
			case "emerald": sellable = true;
			break;
			case "coal": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("fishery"))
		{
			switch(productCategory.toLowerCase())
			{
			case "fish": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("butchery"))
		{
			switch(productCategory.toLowerCase())
			{
			case "meat": sellable = true;
			break;
			}
			switch(productName.toLowerCase())
			{
			case "coal": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("bakery"))
		{
			switch(productCategory.toLowerCase())
			{
			case "baked-goods": sellable = true;
			break;
			}
			switch(productName.toLowerCase())
			{
			case "wheat": sellable = true;
			break;
			case "coal": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("grocery"))
		{
			switch(productCategory.toLowerCase())
			{
			case "vegetables": sellable = true;
			break;
			}
			switch(productName.toLowerCase())
			{
			case "wheat": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("furnitury"))
		{
			switch(productCategory.toLowerCase())
			{
			case "furniture": sellable = true;
			break;
			}
			if (productName.contains("wood"))
			{
				sellable = true;
			}
		}
		if (propertyCategory.equalsIgnoreCase("resources"))
		{
			switch(productCategory.toLowerCase())
			{
			case "resources": sellable = true;
			break;
			case "tools": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("warehouse"))
		{
			switch(productCategory.toLowerCase())
			{
			case "resources": sellable = true;
			break;
			case "furniture": sellable = true;
			break;
			case "armor": sellable = true;
			break;
			case "swords": sellable = true;
			break;
			case "bows": sellable = true;
			break;
			case "jewelery": sellable = true;
			break;
			case "magic": sellable = true;
			break;
			case "tools": sellable = true;
			break;
			}
		}
		if (propertyCategory.equalsIgnoreCase("witchery"))
		{
			switch(productCategory.toLowerCase())
			{
			case "magic": sellable = true;
			break;
			}
		}
		
		
		return sellable;
	}
	
	public ArrayList<Integer> getIDListbyCategory(String productCategory, boolean onlyactive)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		ProductCategory category = new ProductCategory();

		for (Integer productID : this.getIDList(onlyactive, null, true))
		{
			String catName = category.getCategoryName(this.getCategoryID(productID, false));
			if (catName.equalsIgnoreCase(productCategory))
			{
				list.add(productID);
			}
		}
		
		return list;
	}
	
	public Material getShopItemManagerCategoryItem(String productCategory)
	{
		Material mat = Material.BARRIER;
		
		switch(productCategory.toLowerCase())
		{
		case "swords": return Material.IRON_SWORD;
		case "armor": return Material.IRON_HELMET;
		case "bows": return Material.BOW;
		case "jewelery": return Material.DIAMOND;
		case "fish": return Material.RAW_FISH;
		case "meat": return Material.RAW_BEEF;
		case "baked-goods": return Material.BREAD;
		case "vegetables": return Material.BROWN_MUSHROOM;
		case "furniture": return Material.JUKEBOX;
		case "magic": return Material.POTION;
		case "resources": return Material.IRON_INGOT;
		case "tools": return Material.IRON_PICKAXE;
		}
		return mat;
	}
	
	public ArrayList<String> getGradeLore(Integer productID, List<String> lore, boolean gemproduct)
	{
        StringBuilder sb = new StringBuilder();
		Integer grade = this.getGrade(productID, gemproduct);
		String stars = null;
		for (int i = 0; i < grade; i++)
		{
			sb.append("★ ");
		}
		stars = "§l§bGrade: " + sb.toString().trim();
		lore.add("   ");
		lore.add("   ");
		lore.add(stars);
		
		return new ArrayList<String>(lore);
	}
	
	public Enchantment getEnchantmentfromString(String enchantment)
	{
		Enchantment ench = null;
		if (enchantment.equalsIgnoreCase("sharpness") || enchantment.equalsIgnoreCase("sharp"))
		{
			ench = Enchantment.DAMAGE_ALL;
		} else
		if (enchantment.equalsIgnoreCase("smite"))
		{
			ench = Enchantment.DAMAGE_UNDEAD;
		} else
		if (enchantment.equalsIgnoreCase("baneofarthropods") || enchantment.equalsIgnoreCase("bane of arthropods"))
		{
			ench = Enchantment.DAMAGE_ARTHROPODS;
		} else
		if (enchantment.equalsIgnoreCase("knockback"))
		{
			ench = Enchantment.KNOCKBACK;
		} else
		if (enchantment.equalsIgnoreCase("fireaspect") || enchantment.equalsIgnoreCase("fire") || enchantment.equalsIgnoreCase("fire aspect"))
		{
			ench = Enchantment.FIRE_ASPECT;
		} else
		if (enchantment.equalsIgnoreCase("looting") || enchantment.equalsIgnoreCase("loot"))
		{
			ench = Enchantment.LOOT_BONUS_MOBS;
		} else
		if (enchantment.equalsIgnoreCase("power"))
		{
			ench = Enchantment.ARROW_DAMAGE;
		} else
		if (enchantment.equalsIgnoreCase("punch"))
		{
			ench = Enchantment.ARROW_KNOCKBACK;
		} else
		if (enchantment.equalsIgnoreCase("flame"))
		{
			ench = Enchantment.ARROW_FIRE;
		} else
		if (enchantment.equalsIgnoreCase("infinity"))
		{
			ench = Enchantment.ARROW_INFINITE;
		} else
		if (enchantment.equalsIgnoreCase("protection"))
		{
			ench = Enchantment.PROTECTION_ENVIRONMENTAL;
		} else
		if (enchantment.equalsIgnoreCase("fireprotection") || enchantment.equalsIgnoreCase("fire protection"))
		{
			ench = Enchantment.PROTECTION_FIRE;
		} else
		if (enchantment.equalsIgnoreCase("featherfalling") || enchantment.equalsIgnoreCase("feather falling"))
		{
			ench = Enchantment.PROTECTION_FALL;
		} else
		if (enchantment.equalsIgnoreCase("blast") || enchantment.equalsIgnoreCase("blastprotection") || enchantment.equalsIgnoreCase("blast protection"))
		{
			ench = Enchantment.PROTECTION_EXPLOSIONS;
		} else
		if (enchantment.equalsIgnoreCase("projectile") || enchantment.equalsIgnoreCase("projectileprotection") || enchantment.equalsIgnoreCase("projectile protection"))
		{
			ench = Enchantment.PROTECTION_PROJECTILE;
		} else
		if (enchantment.equalsIgnoreCase("respiration"))
		{
			ench = Enchantment.OXYGEN;
		} else
		if (enchantment.equalsIgnoreCase("aqua") || enchantment.equalsIgnoreCase("aquainfinity") || enchantment.equalsIgnoreCase("aqua infinity"))
		{
			ench = Enchantment.WATER_WORKER;
		} else
		if (enchantment.equalsIgnoreCase("thorns"))
		{
			ench = Enchantment.THORNS;
		} else
		if (enchantment.equalsIgnoreCase("depth") || enchantment.equalsIgnoreCase("depthstrider") || enchantment.equalsIgnoreCase("depth strider"))
		{
			ench = Enchantment.DEPTH_STRIDER;
		} else
		if (enchantment.equalsIgnoreCase("efficiency"))
		{
			ench = Enchantment.DIG_SPEED;
		} else
		if (enchantment.equalsIgnoreCase("silk") || enchantment.equalsIgnoreCase("silktouch") || enchantment.equalsIgnoreCase("silk touch"))
		{
			ench = Enchantment.SILK_TOUCH;
		} else
		if (enchantment.equalsIgnoreCase("unbreaking") || enchantment.equalsIgnoreCase("unb"))
		{
			ench = Enchantment.DURABILITY;
		} else
		if (enchantment.equalsIgnoreCase("fortune"))
		{
			ench = Enchantment.LOOT_BONUS_BLOCKS;
		} else
		if (enchantment.equalsIgnoreCase("luck") || enchantment.equalsIgnoreCase("luckofthesea") || enchantment.equalsIgnoreCase("luck of the sea"))
		{
			ench = Enchantment.LUCK;
		} else
		if (enchantment.equalsIgnoreCase("lure"))
		{
			ench = Enchantment.LURE;
		}
		
		return ench;
	}
	
	public boolean soulbound(ItemStack item)
	{
		boolean sb = false;
		
		if (item.hasItemMeta())
		{
			ItemMeta meta = item.getItemMeta();
			if (meta.hasLore())
			{
				List<String> lore = meta.getLore();
				if (lore.size() > 1)
				{
					for (int i = 0; i < lore.size(); i++)
					{
						if (ChatColor.stripColor(lore.get(i)).contains("Soulbound"))
						{
							sb = true;
							break;
						}
					}
				} else
				{
					if (ChatColor.stripColor(lore.get(0)).contains("Soulbound"))
					{
						sb = true;
					}
				}
			}
		}
		
		return sb;
	}
	
	public boolean ghosted(ItemStack item)
	{
		boolean sb = false;
		
		if (item.hasItemMeta())
		{
			ItemMeta meta = item.getItemMeta();
			if (meta.hasLore())
			{
				List<String> lore = meta.getLore();
				if (lore.size() > 1)
				{
					for (int i = 0; i < lore.size(); i++)
					{
						if (ChatColor.stripColor(lore.get(i)).contains("Ghosted"))
						{
							sb = true;
							break;
						}
					}
				} else
				{
					if (ChatColor.stripColor(lore.get(0)).contains("Ghosted"))
					{
						sb = true;
					}
				}
			}
		}
		
		return sb;
	}
	
	public ItemStack GhostItem(ItemStack item)
	{
		if (item.hasItemMeta() == true)
		{
			ItemMeta meta = item.getItemMeta();
			if (meta.getLore() != null)
			{
				if (!meta.getLore().contains(ChatColor.GRAY + "Ghosted") && !meta.getLore().contains(ChatColor.RED + "Soulbound"))
				{
					List<String> lore = meta.getLore();
					lore.add(ChatColor.DARK_GRAY + "Ghosted");
					meta.setLore(lore);
					item.setItemMeta(meta);
				}
			} else
			{
				meta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "Ghosted"));
				item.setItemMeta(meta);
			}
		} else
		{
			ItemMeta meta = item.getItemMeta();
			meta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "Ghosted"));
			item.setItemMeta(meta);
		}
		
		return item;
	}
	
	public ItemStack SoulboundItem(ItemStack item)
	{
		if (item.hasItemMeta() == true)
		{
			ItemMeta meta = item.getItemMeta();
			if (meta.getLore() != null)
			{
				if (!meta.getLore().contains(ChatColor.RED + "Soulbound") && !meta.getLore().contains(ChatColor.GRAY + "Ghosted"))
				{
					List<String> lore = meta.getLore();
					lore.add(ChatColor.RED + "Soulbound");
					meta.setLore(lore);
					item.setItemMeta(meta);
				}
			} else
			{
				meta.setLore(Arrays.asList(ChatColor.RED + "Soulbound"));
				item.setItemMeta(meta);
			}
		} else
		{
			ItemMeta meta = item.getItemMeta();
			meta.setLore(Arrays.asList(ChatColor.RED + "Soulbound"));
			item.setItemMeta(meta);
		}
		
		return item;
	}
	
	//SoulboundCategory can either be 'soulbound' or 'ghosted' or '', this is to add it at te bottom of the lore
	public ItemStack addCustomEnchantment(ItemStack item, String customEnchantment, Integer level, boolean fixedGrade, boolean gemproduct)
	{
		Products.Enchantment enchant = new Products.Enchantment();
		String finalLevel = "I";
		switch (level)
		{
		case 1: finalLevel = "I";
		break;
		case 2: finalLevel = "II";
		break;
		case 3: finalLevel = "III";
		break;
		}
		if (item.hasItemMeta())
		{
			ItemMeta meta = item.getItemMeta();
			String displayName = meta.getDisplayName();
			List<String> lore = null;
			Integer maxLevel = enchant.getEnchantmentMaxLevel(enchant.getEnchantmentID(customEnchantment));
			if (this.getProductIDbyDisplayName(displayName, gemproduct) != null)
			{
				Integer productID = this.getProductIDbyDisplayName(displayName, gemproduct);
				ArrayList<String> specialLore = new ArrayList<String>();
				lore = meta.getLore();
				for (String line : lore)
				{
					String newLine = ChatColor.stripColor(line);
					if (!newLine.equalsIgnoreCase("soulbound") && !newLine.equalsIgnoreCase("ghosted") && !newLine.contains("Grade:") && !newLine.contains(customEnchantment) && !newLine.equalsIgnoreCase("   "))
					{
						specialLore.add(lore.get(lore.indexOf(line)));
					} else
					if (newLine.contains(customEnchantment))
					{
						String currentLevel = newLine.replaceAll(customEnchantment + " ", "");
						String newLevel = currentLevel + finalLevel;
						switch (newLevel)
						{
						case "IIIIII": finalLevel = this.getCustomEnchantLevel(maxLevel);
						break;
						case "IIIII": finalLevel = this.getCustomEnchantLevel(maxLevel);
						break;
						case "IIII": finalLevel = this.getCustomEnchantLevel(maxLevel);
						break;
						case "III": finalLevel = this.getCustomEnchantLevel(maxLevel);
						break;
						case "II":
							if (maxLevel == 1)
							{
								finalLevel = this.getCustomEnchantLevel(1);
							} else
							{
								finalLevel = this.getCustomEnchantLevel(2);
							}
						break;
						case "I": finalLevel = this.getCustomEnchantLevel(1);
						break;
						}
					}
				}
				lore.clear();
				lore.add(ChatColor.GRAY + customEnchantment + " " + finalLevel);
				for (String line : specialLore)
				{
					lore.add(line);
				}
				if (this.soulbound(item))
				{
					lore.add(ColorOptions.soulbound + "Soulbound");
				}
				if (this.ghosted(item))
				{
					lore.add(ColorOptions.ghosted + "Ghosted");
				}
				meta.setLore(this.getGradeLore(this.getProductIDbyDisplayName(displayName, gemproduct), lore, gemproduct));
				item.setItemMeta(meta);
			}
		} else
		{
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + customEnchantment + " " + finalLevel);
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	public String getCustomEnchantLevel(Integer level)
	{
		String stringLevel = "I";
		
		switch (level)
		{
		case 1: stringLevel = "I";
		break;
		case 2: stringLevel = "II";
		break;
		case 3: stringLevel = "III";
		break;
		case 4: stringLevel = "IV";
		break;
		case 5: stringLevel = "V";
		break;
		case 6: stringLevel = "VI";
		}
		
		return stringLevel;
	}
	
	public boolean isWeapon(Material mat)
	{
		switch (mat)
		{
		case BOW: 
		case DIAMOND_SWORD: 
		case IRON_SWORD: 
		case GOLD_SWORD: 
		case WOOD_SWORD: 
		case DIAMOND_AXE: 
		case IRON_AXE: 
		case GOLD_AXE: 
		case WOOD_AXE: 
		case DIAMOND_PICKAXE: 
		case IRON_PICKAXE: 
		case GOLD_PICKAXE: 
		case WOOD_PICKAXE: 
    	case SNOW_BALL: 
    	case NETHER_STAR: 
    	case BLAZE_ROD: 
    	case DIAMOND_SPADE: 
    	case GOLD_SPADE: 
    	case IRON_SPADE: 
    	case WOOD_SPADE: 
    	return true;
		}
		return false;
	}
	
	public Integer getSellPrice(Integer productID, Integer propertyCategoryID)
	{
		Integer price = null;
		
		Integer priceMin = this.getPriceMin(productID);
		Integer nonWarehousePrice = (priceMin + ((int) (priceMin/100)*10));
		if (this.propertyCategory.getCategoryName(propertyCategoryID).equalsIgnoreCase("warehouse"))
		{
			price = priceMin;
		} else
		{
			price = nonWarehousePrice;
		}
		
		return price;
	}
	
	public boolean gradeChance(Integer productID)
	{
		boolean positive = false;
		
		Integer grade = this.getGrade(productID, false);
		Integer chance = 1;
		if (grade == 1)
		{
			chance = 70;
		} else if (grade == 2)
		{
			chance = 60;
		} else if (grade == 3)
		{
			chance = 40;
		} else if (grade == 4)
		{
			chance = 5;
		} else if (grade == 5)
		{
			chance = 1;
		}
		
		if (main.getRandom(0, 100) <= chance)
		{
			positive = true;
		}
		
		return positive;
	}
	
	public ItemStack getRandomProduct(String productCategory, Integer amount)
	{
		ItemStack item = null;
		Integer productID = null;
		Integer grade = null;
		List<Integer> list = this.productCategory.getCategoryIDList();

		if (productCategory == null)
		{	
			Collections.shuffle(list);
			for (Integer categoryID : list)
			{
				if (!this.getIDListbyCategory(this.productCategory.getCategoryName(categoryID), false).isEmpty())
				{
					productCategory = this.productCategory.getCategoryName(categoryID);
					break;
				}
			}
		}
		
		List<Integer> products = this.getIDListbyCategory(productCategory, true);
		if (main.debug)
		{
			if (products.isEmpty())
			{
				Bukkit.getConsoleSender().sendMessage("Product list is empty");
			} else
			{
				Bukkit.getConsoleSender().sendMessage("Size: " + products.size());
			}
		}
		if (products == null || products.size() < 1)
		{
			products = this.getIDListbyCategory("resources", true);
		}
		if (products.size() == 1)
		{
			productID = products.get(0);
		} else
		{
			Collections.shuffle(products);
//			productID = products.get(main.getRandom(0, products.size()-1));
			for (Integer productIDs : products)
			{
				if (main.getRandom(0, 100) <= this.getdropChance(productIDs))
				{
					productID = productIDs;
					break;
				}
			}
			if (productID == null)
			{
				productID = products.get(main.getRandom(0, products.size()-1));
			}
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Cat: " + productCategory);
			Bukkit.getConsoleSender().sendMessage("ID: " + productID);
			Bukkit.getConsoleSender().sendMessage("DisplayName: " + this.getDisplayName(productID, false));
		}
		grade = this.getGrade(productID, false);
		
		if (amount == null)
		{
			if (productCategory.equalsIgnoreCase("meat") || productCategory.equalsIgnoreCase("fish") || productCategory.equalsIgnoreCase("vegetables") || productCategory.equalsIgnoreCase("baked-goods"))
			{
				switch(grade)
				{
				case 1: amount = main.getRandom(32, 64);
				break;
				case 2: amount = main.getRandom(32, 54);
				break;
				case 3: amount = main.getRandom(24, 48);
				break;
				case 4: amount = main.getRandom(16, 32);
				break;
				case 5: amount = main.getRandom(8, 22);
				break;
				}
			} else if (productCategory.equalsIgnoreCase("resources"))
			{
				switch(grade)
				{
				case 1: amount = main.getRandom(32, 64);
				break;
				case 2: amount = main.getRandom(32, 54);
				break;
				case 3: amount = main.getRandom(18, 32);
				break;
				case 4: amount = main.getRandom(8, 22);
				break;
				case 5: amount = main.getRandom(4, 14);
				break;
				}
			} else if (main.getRandom(0, 10000) <= 5)
			{
				amount = 2;
			} else
			{
				amount = 1;
			}
		}
		ItemStack itemt = this.createPropertyItem(productID, amount, false, false);
		if (itemt.getMaxStackSize() < amount)
		{
			itemt.setAmount(itemt.getMaxStackSize());
		}
		return itemt;
	}
	
	public int getdropChance(Integer productID)
	{
		int chance = this.drop1;
		
		Integer grade = this.getGrade(productID, false);
		
		switch(grade)
		{
		case 2: chance = this.drop2;
		break;
		case 3: chance = this.drop3;
		break;
		case 4: chance = this.drop4;
		break;
		case 5: chance = this.drop5;
		break;
		default: chance = this.drop1;
		break;
		}
		
		return chance;
	}
	
	public ItemStack getLegendarySwordBox()
	{
		Random random = new Random();
		ItemStack sword = this.createPropertyItem(this.getProductID("diamondsword", false), 1, false, false);
		if (main.getRandom(0, 100) <= 30)
		{
			sword = this.createPropertyItem(this.getProductID("golemheartsword", false), 1, false, false);
		}
		
		if (random.nextInt(100) <= 90) {
			sword.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL,
					1 + (int) (Math.random() * (org.bukkit.enchantments.Enchantment.DAMAGE_ALL.getMaxLevel() + 1)));
		} else {
			sword.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 4);
		}
		if (random.nextInt(100) <= 65) {
			sword.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.KNOCKBACK,
					1 + (int) (Math.random() * (org.bukkit.enchantments.Enchantment.DAMAGE_ALL.getMaxLevel() + 1 + 1)));
		}
		if (random.nextInt(100) <= 50) {
			sword.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.FIRE_ASPECT,
					1 + (int) (Math.random() * (org.bukkit.enchantments.Enchantment.DAMAGE_ALL.getMaxLevel() + 1 + 1)));
		}
		if (random.nextInt(100) <= 45) {
			sword.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY,
					1 + (int) (Math.random() * (org.bukkit.enchantments.Enchantment.DAMAGE_ALL.getMaxLevel() + 1 + 1)));
		}
		if (random.nextInt(100) <= 28) {
			if (random.nextInt(100) <= 40) {
				sword = this.addCustomEnchantment(sword, "poison", 1, true, false);
			} else {
				sword = this.addCustomEnchantment(sword, "poison", 2, true, false);
			}
			if (random.nextInt(100) <= 45) {
				sword = this.addCustomEnchantment(sword, "poison", 3, true, false);
			}
			if (random.nextInt(100) <= 45) {
				sword = this.addCustomEnchantment(sword, "blindness", 1, true, false);
			} else {
				sword = this.addCustomEnchantment(sword, "blindness", 2, true, false);
			}
			if (random.nextInt(100) <= 50) {
				sword = this.addCustomEnchantment(sword, "blindness", 3, true, false);
			}
			if (random.nextInt(100) <= 50) {
				sword = this.addCustomEnchantment(sword, "confusion", 1, true, false);
			} else {
				sword = this.addCustomEnchantment(sword, "confusion", 2, true, false);
			}
			if (random.nextInt(100) <= 55) {
				sword = this.addCustomEnchantment(sword, "confusion", 3, true, false);
			}
		}
		if (random.nextInt(100) <= 20) {
			if (random.nextInt(100) <= 30) {
				sword = this.addCustomEnchantment(sword, "poison", 1, true, false);
			} else if (random.nextInt(100) <= 45) 
			{
				sword = this.addCustomEnchantment(sword, "poison", 2, true, false);

			} else if (random.nextInt(100) <= 40) {
				sword = this.addCustomEnchantment(sword, "poison", 3, true, false);

			}
		}
		if (random.nextInt(100) <= 25) {
			if (random.nextInt(100) <= 40) {
				sword = this.addCustomEnchantment(sword, "blindness", 1, true, false);

			}
			if (random.nextInt(100) <= 55) {
				sword = this.addCustomEnchantment(sword, "blindness", 2, true, false);

			}
			if (random.nextInt(100) <= 50) {
				sword = this.addCustomEnchantment(sword, "blindness", 3, true, false);

			}
		}
		if (random.nextInt(100) <= 30) {
			if (random.nextInt(100) <= 45) {
				sword = this.addCustomEnchantment(sword, "confusion", 1, true, false);

			}
			if (random.nextInt(100) <= 60) {
				sword = this.addCustomEnchantment(sword, "confusion", 2, true, false);

			}
			if (random.nextInt(100) <= 55) {
				sword = this.addCustomEnchantment(sword, "confusion", 3, true, false);
			}
		}
		if (random.nextInt(100) <= 5)
		{
			sword = this.addCustomEnchantment(sword, "chaos", 1, true, false);
		}
		if (random.nextInt(100) <= 45)
		{
			sword = this.GhostItem(sword);
		}
		int ench = sword.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DAMAGE_ALL);
		if (ench <= 2) {
			sword.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL,
					1 + (int) (Math.random() * (org.bukkit.enchantments.Enchantment.DAMAGE_ALL.getMaxLevel() + 1) + 1.0D));
		}
		
		return sword;
	}
	
	public ItemStack getRareSwordBox()
	{
		Random random = new Random();
		ItemStack sword = this.createPropertyItem(this.getProductID("bladedsteelsword", false), 1, false, false);
		Integer rand = main.getRandom(0, 100);
		if (rand <= 5)
		{
			sword = this.createPropertyItem(this.getProductID("golemheartsword", false), 1, false, false);
		} else if (rand <= 30)
		{
			sword = this.createPropertyItem(this.getProductID("diamondsword", false), 1, false, false);
		}
		
		if (random.nextInt(100) <= 90) {
			sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL,
					1 + (int) (Math.random() * Enchantment.DAMAGE_ALL.getMaxLevel()));
		} else {
			sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
		}
		if (random.nextInt(100) <= 65) {
			sword.addUnsafeEnchantment(Enchantment.KNOCKBACK,
					1 + (int) (Math.random() * (Enchantment.DAMAGE_ALL.getMaxLevel() + 1)));
		}
		if (random.nextInt(100) <= 50) {
			sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT,
					1 + (int) (Math.random() * (Enchantment.DAMAGE_ALL.getMaxLevel() + 1)));
		}
		if (random.nextInt(100) <= 45) {
			sword.addUnsafeEnchantment(Enchantment.DURABILITY,
					1 + (int) (Math.random() * (Enchantment.DAMAGE_ALL.getMaxLevel() + 1)));
		}
		if (random.nextInt(100) <= 18) {
			if (random.nextInt(100) <= 50) {
				sword = this.addCustomEnchantment(sword, "poison", 1, true, false);
			} else {
				sword = this.addCustomEnchantment(sword, "poison", 2, true, false);

			}
			if (random.nextInt(100) <= 40) {
				sword = this.addCustomEnchantment(sword, "poison", 3, true, false);
			}
			if (random.nextInt(100) <= 55) {
				sword = this.addCustomEnchantment(sword, "blindness", 1, true, false);
			} else {
				sword = this.addCustomEnchantment(sword, "blindness", 2, true, false);
			}
			if (random.nextInt(100) <= 45) {
				sword = this.addCustomEnchantment(sword, "blindness", 3, true, false);
			}
			if (random.nextInt(100) <= 60) {
				sword = this.addCustomEnchantment(sword, "confusion", 1, true, false);
			} else {
				sword = this.addCustomEnchantment(sword, "confusion", 2, true, false);
			}
			if (random.nextInt(100) <= 50) {
				sword = this.addCustomEnchantment(sword, "confusion", 3, true, false);
			}
		}
		if (random.nextInt(100) <= 10) {
			if (random.nextInt(100) <= 40) {
				sword = this.addCustomEnchantment(sword, "poison", 1, true, false);
			} else if (random.nextInt(100) <= 39) {
				sword = this.addCustomEnchantment(sword, "poison", 2, true, false);
			} else if (random.nextInt(100) <= 38) {
				sword = this.addCustomEnchantment(sword, "poison", 3, true, false);
			}
		}
		if (random.nextInt(100) <= 18) {
			if (random.nextInt(100) <= 50) {
				sword = this.addCustomEnchantment(sword, "blindness", 1, true, false);
			} else if (random.nextInt(100) <= 51) {
				sword = this.addCustomEnchantment(sword, "blindness", 2, true, false);
			} else if (random.nextInt(100) <= 49) {
				sword = this.addCustomEnchantment(sword, "blindness", 3, true, false);
			}
		}
		if (random.nextInt(100) <= 22) {
			if (random.nextInt(100) <= 55) {
				sword = this.addCustomEnchantment(sword, "confusion", 1, true, false);
			} else if (random.nextInt(100) <= 54) {
				sword = this.addCustomEnchantment(sword, "confusion", 2, true, false);
			} else if (random.nextInt(100) <= 53) {
				sword = this.addCustomEnchantment(sword, "confusion", 3, true, false);
			}
		}
		if (random.nextInt(100) <= 5)
		{
			sword = this.addCustomEnchantment(sword, "armorrepair", 1, true, false);
		}
		if (random.nextInt(100) <= 45)
		{
			sword = this.SoulboundItem(sword);
		}
		int ench = sword.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
		if (ench <= 2) {
			sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL,
					1 + (int) (Math.random() * Enchantment.DAMAGE_ALL.getMaxLevel()));
		}
		
		return sword;
	}
	
	public boolean giveProduct(CommandSender sender, User userTarget, Integer productID, Integer amount)
	{
		boolean given = false;
		
		String username = userTarget.getUsername();
		
		if (username == null)
		{
			sender.sendMessage(ColorOptions.error + "No player could be found with the given UUID");
			return given;
		}
		
		if (!this.getIDList(true, null, true).contains(productID))
		{
			sender.sendMessage(ColorOptions.error + "No product could be found with ID " + productID);
			return given;
		}
		
		if (Bukkit.getPlayer(username) != null)
		{
			Player target = Bukkit.getPlayer(username);
			ItemStack item = this.createPropertyItem(productID, amount, false, false);
			String display = item.getItemMeta().getDisplayName();
			if (target.getInventory().firstEmpty() != -1)
			{
				target.getInventory().addItem(item);
				target.sendMessage(ColorOptions.messageachievement + ColorOptions.messageArrow + "You received " + ColorOptions.messagesubjects + amount + ColorOptions.messageachievement + " times " + display);
				sender.sendMessage("Gave player " + target.getName() + amount + " times " + display);
				given = true;
			} else if (target.getEnderChest().firstEmpty() != -1)
			{
				target.getInventory().addItem(item);
				target.sendMessage(ColorOptions.messageachievement + ColorOptions.messageArrow + "You received " + ColorOptions.messagesubjects + amount + ColorOptions.messageachievement + " times " + display + ColorOptions.messageachievement + " in your enderchest!");
				sender.sendMessage("Gave player " + target.getName() + amount + " times " + display + " in his enderchest");
				given = true;
			} else
			{
				addScheduledGive(userTarget.getUUID(), productID, amount);
				sender.sendMessage("Scheduled give task for player " + target.getName() + " for " + amount + " times " + display + " because his inventory and enderchest are full!");
			}
		} else
		{
			addScheduledGive(userTarget.getUUID(), productID, amount);
			sender.sendMessage("Scheduled give task for player " + userTarget.getUsername() + " for " + amount + " times " + this.getDisplayName(productID, false) + " because player is not online!");
		}
		
		return given;
	}
	
	public void addScheduledGive(UUID uuid, Integer productID, Integer amount)
	{
		if (main.scheduledgive.containsKey(uuid))
		{
			HashMap<Integer, Integer> list = main.scheduledgive.get(uuid);
			if (list.containsKey(productID))
			{
				list.put(productID, list.get(productID)+amount);
			} else
			{
				list.put(productID, amount);
			}
			main.scheduledgive.put(uuid, list);
		} else
		{
			HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
			list.put(productID, amount);
			main.scheduledgive.put(uuid, list);
		}
	}
	
	public List<String> getStringLines(String string, Integer splitCount, ChatColor textColor)
	{
		List<String> description = new ArrayList<String>();
		
		String[] split = string.split(" ");
		if (string.split(" ").length > splitCount)
		{
			StringBuilder sb = new StringBuilder();
			int count = 0;
	        for(int i = 0; i < split.length; i +=splitCount)
	        {       
	            count=i+(splitCount-1);
	            for(int j=i;j<=count; j++)
	            {
	            	if (j < split.length)
	            	{
	            		sb.append(split[j]).append(" ");
	            	}
	            }
	            description.add(textColor + sb.toString());
	            sb.delete(0, sb.length());
	        }
		} else
		{
			description.add(textColor + string);
		}
		
		return description;
	}
	
	public Integer getBaseDropAmount(Integer productID)
	{
		Integer dropAmount = null;
		
		try 
		{
			PreparedStatement stmt = main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?;");
			stmt.setInt(1, productID);
			
			ResultSet results = stmt.executeQuery();
			if (results.next())
			{
				dropAmount = results.getInt("BaseDropAmount");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return dropAmount;
	}
	
	public Integer getBaseDropAmountbyYml(String productName)
	{
		YmlFile ymlfile = new YmlFile();
		Integer baseDrop = 1;
		
		File file = main.resourceFiles.get("resources-drop-amount");
		YamlConfiguration config = ymlfile.getConfig(file);
		try
		{
			baseDrop = config.getInt(productName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return baseDrop;
	}
	
	public double getItemDamage(ItemStack item)
	{
		double damage = 1;
		
		Material material = item.getType();
		
		if (material == Material.WOOD_SPADE || material == Material.GOLD_SPADE)
		{
			damage += 1;
		} else if (material == Material.STONE_SPADE || material == Material.WOOD_PICKAXE || material == Material.GOLD_PICKAXE)
		{
			damage += 2;
		} else if (material == Material.IRON_SPADE || material == Material.STONE_PICKAXE || material == Material.WOOD_AXE || material == Material.GOLD_AXE)
		{
			damage += 3;
		} else
		if (material == Material.DIAMOND_SPADE || material == Material.IRON_PICKAXE || material == Material.STONE_AXE || material == Material.WOOD_SWORD || material == Material.GOLD_SWORD)
		{
			damage += 4;
		} else if (material == Material.DIAMOND_PICKAXE || material == Material.IRON_AXE || material == Material.STONE_SWORD)
		{
			damage += 5;
		} else if (material == Material.DIAMOND_AXE || material == Material.IRON_SWORD)
		{
			damage += 6;
		} else if (material == Material.DIAMOND_SWORD)
		{
			damage += 7;
		}
		
		return damage;
	}
	
	public double getEnchantmentDamage(ItemStack item)
	{
		double damage = 0;
		
		if (item.containsEnchantment(Enchantment.DAMAGE_ALL))
		{
			damage += (0.5 + (0.5 * item.getEnchantmentLevel(Enchantment.DAMAGE_ALL)));
		}
		
		return damage;
	}
}
