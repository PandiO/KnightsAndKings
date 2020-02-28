package Resources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import Handlers.ColorOptions;
import Main.Main;
import Titles.Title;

public class YmlFile 
{
	protected Title tile = new Title();
	protected Main main = Main.getPlugin(Main.class);

	public Integer getLastID(String fileName)
	{
		Integer lastID = null;
		File file = getFile(fileName);
		if (file != null)
		{
	        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	        lastID = Integer.valueOf(config.get("LastID").toString());
		}
		
		return lastID;
	}
	
	public void saveLastID(String fileName, Integer newID)
	{
		File file = getFile(fileName);
		if (file != null)
		{
			YamlConfiguration config = getConfig(file);
			config.set("LastID", newID);
			saveFile(config, file);
		}
	}
	
	public void saveFile(YamlConfiguration config, File file)
	{
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public File getFile(String fileName)
	{
		File file = new File(main.getDataFolder(), fileName + ".yml");
		if (file.exists())
		{
			return file;
		} else
		{
			return null;
		}
	}
	
	public YamlConfiguration getConfig(File file)
	{
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config;
	}
	
	public void saveBlock(String fileName, Location blockLocation, String originalBlockID, Integer delay)
	{
		File file = getFile(fileName);
		if (file != null)
		{
			YamlConfiguration config = getConfig(file);
			ConfigurationSection section = config.getConfigurationSection("Blocks");
			
			Integer newID = (getLastID(fileName)+1);
			Long expireMillis = (System.currentTimeMillis()+(delay*1000));
			HashMap<String, Object> location = new HashMap<String, Object>();
			
			location.put("X", blockLocation.getX());
			location.put("Y", blockLocation.getY());
			location.put("Z", blockLocation.getZ());
			location.put("Yaw", blockLocation.getYaw());
			location.put("Pitch", blockLocation.getPitch());
			location.put("World", blockLocation.getWorld().getName());
			
			section.createSection("Block_" + newID);
			ConfigurationSection block = section.getConfigurationSection("Block_" + newID);
			block.set("MaterialID", originalBlockID);
			block.set("ExpireMillis", expireMillis);
			block.createSection("Location", location);
			
			config.set("LastID", newID);
			saveFile(config, file);
		}
	}
	
	public List<Integer> getBlockIDList(String fileName, boolean part, Integer amount)
	{
		List<Integer> list = new ArrayList<Integer>();
		File file = getFile(fileName);
		if (file != null)
		{
			YamlConfiguration config = getConfig(file);
			Set<String> set = config.getConfigurationSection("Blocks").getKeys(false);
			if (set != null)
			{
				for (String block : set)
				{
					if (part == true)
					{
						if (list.size() < amount)
						{
							list.add(Integer.valueOf(block.split("_")[1]));
						} else
						{
							break;
						}
					} else
					{
						list.add(Integer.valueOf(block.split("_")[1]));
					}
				}
			}
		}
		
		return list;
	}
	
	public HashMap<String, Object> getBlockValues(String fileName, Integer blockID)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		File file = getFile(fileName);
		if (file != null)
		{
			if (getLastID(fileName) >= blockID)
			{
				YamlConfiguration config = getConfig(file);
				ConfigurationSection section = config.getConfigurationSection("Blocks");
				ConfigurationSection block = section.getConfigurationSection("Block_" + blockID);
				ConfigurationSection locsection = block.getConfigurationSection("Location");
				
				map.put("MaterialID", block.get("MaterialID"));
				map.put("ExpireMillis", block.getLong("ExpireMillis"));
				
				World world = Bukkit.getWorld(locsection.getString("World"));
				Double x = Double.valueOf(locsection.getString("X"));
				Double y = Double.valueOf(locsection.getString("Y"));
				Double z = Double.valueOf(locsection.getString("Z"));
				Float pitch = Float.valueOf(locsection.getString("Pitch"));
				Float yaw = Float.valueOf(locsection.getString("Yaw"));
				map.put("Location", new Location(world, x, y, z, yaw, pitch));
			}
		}
		
		return map;
	}
	
	public Long getCooldownMillis(String fileName, Integer blockID)
	{
		Long millis = null;
		
		File file = getFile(fileName);
		if (file != null)
		{
			if (getLastID(fileName) >= blockID)
			{
				YamlConfiguration config = getConfig(file);
				ConfigurationSection section = config.getConfigurationSection("Blocks");
				ConfigurationSection block = section.getConfigurationSection("Block_" + blockID);				
				millis = block.getLong("ExpireMillis");
			} else
			{
				Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "BlockID not registered!");
			}
		} else
		{
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "File is not found!");
		}
		
		return millis;
	}
	
	public void removeBlock(String fileName, Integer blockID)
	{
		File file = getFile(fileName);
		if (file != null)
		{
			if (getLastID(fileName) >= blockID)
			{
				YamlConfiguration config = getConfig(file);
				config.set("Blocks.Block_" + blockID, null);
				saveFile(config, file);
			}
		}
	}
	
	public void changeBlock(String fileName, Integer blockID, boolean forced)
	{
		HashMap<String, Object> map = getBlockValues(fileName, blockID);
		String materialID = map.get("MaterialID").toString();
		Material material = null;
		Byte data = null;
		if (materialID.contains(":"))
		{
			material = Material.getMaterial(Integer.valueOf(materialID.split(":")[0]));
			data = Byte.valueOf(materialID.split(":")[1]);
		} else
		{
			material = Material.getMaterial(Integer.valueOf(materialID));
		}
		
		Long current = System.currentTimeMillis();
		Long expire = (Long) map.get("ExpireMillis");
		if (forced == false) 
		{ 
			if(expire < current)
			{
				Location loc = (Location) map.get("Location");
				loc.getBlock().setType(material);
				if (data != null)
				{
					loc.getBlock().setData(data);
				}
				removeBlock(fileName, blockID);
			} else
			{
				Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Cooldown not expired!");
			}
		} else
		{
			Location loc = (Location) map.get("Location");
			loc.getBlock().setType(material);
			if (data != null)
			{
				loc.getBlock().setData(data);
			}
			removeBlock(fileName, blockID);
		}
	}
}
