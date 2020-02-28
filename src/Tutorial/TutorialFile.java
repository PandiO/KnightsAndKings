package Tutorial;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import Resources.YmlFile;

public class TutorialFile extends YmlFile
{
	private String fileName = "complete-tutorial";
	
	public void saveTutorial(String tutorialName, Integer reward)
	{
		if (exist(tutorialName) == false)
		{
			File file = this.getFile(fileName);
			YamlConfiguration config = this.getConfig(file);
			ConfigurationSection section = config.getConfigurationSection("Tutorials").createSection(tutorialName);
			section.set("Reward", reward);
			section.set("ExperienceReward", ((int) this.tile.getExpmin(1)/6));
			section.set("Description", "");
			section.createSection("UUIDs");
			this.saveFile(config, file);
		}
	}
	
	public void removeTutorial(String tutorialName)
	{
		if (exist(tutorialName))
		{
			File file = getFile(fileName);
			if (file != null)
			{
				YamlConfiguration config = this.getConfig(file);
				config.set("Tutorials." + tutorialName, null);
				this.saveFile(config, file);
			}
		}
	}

	public List<String> getTutorialNames()
	{
		List<String> list = new ArrayList<String>();
		YamlConfiguration config = this.getConfig(this.getFile(fileName));
		list.addAll(config.getConfigurationSection("Tutorials").getKeys(false));

		return list;
	}
	
	public boolean exist(String tutorialName)
	{
		boolean exist = false;
		
		if (tutorialName.toLowerCase().contains("tutorial"))
		{
			tutorialName = tutorialName.split(" ")[0];
		}
		if (!getTutorialNames().isEmpty())
		{
			for (String name : getTutorialNames())
			{
				if (name.equalsIgnoreCase(tutorialName))
				{
					exist = true;
					break;
				}
			}
		}
		
		return exist;
	}
	
	public List<UUID> getUUIDList(String tutorialName)
	{
		List<UUID> list = new ArrayList<UUID>();
		
		if (exist(tutorialName))
		{
			File file = this.getFile(fileName);
			if (file != null)
			{
				YamlConfiguration config = this.getConfig(file);

				ConfigurationSection tutorial = getTutorial(tutorialName);
				ConfigurationSection UUIDs = tutorial.getConfigurationSection("UUIDs");
				if (!UUIDs.getKeys(false).isEmpty())
				{
					for (String string : tutorial.getConfigurationSection("UUIDs").getKeys(false))
					{
						list.add(UUID.fromString(string));
					}
				}
			}
		}
		
		return list;
	}
	
	public boolean containsUUID(UUID uuid, String tutorialName)
	{
		boolean contains = false;
		
		if (getUUIDList(tutorialName).contains(uuid))
		{
			contains = true;
		}
		
		return contains;
	}
	
	public void savePlayer(Player player, String tutorialName)
	{
		UUID uuid = player.getUniqueId();
		if (exist(tutorialName))
		{
			if (!containsUUID(uuid, tutorialName))
			{
				File file = this.getFile(fileName);
				if (file != null)
				{
					YamlConfiguration config = this.getConfig(file);
					ConfigurationSection section = config.createSection("Tutorials." + tutorialName + ".UUIDs." + uuid.toString());
					section.set("Name", player.getName());
					section.set("Date", main.getTime());
					this.saveFile(config, file);
				}
			}
		}
	}
	
	public String getCompleteDate(Player player, String tutorialName)
	{
		String date = null;
		
		UUID uuid = player.getUniqueId();
		if (exist(tutorialName))
		{
			if (!containsUUID(uuid, tutorialName))
			{
				File file = this.getFile(fileName);
				if (file != null)
				{
					YamlConfiguration config = this.getConfig(file);
					date = config.getString("Tutorials." + tutorialName + "." + uuid.toString() + ".Date");
				}
			}
		}
		
		return date;
	}
	
	public void removePlayer(UUID uuid, String tutorialName)
	{
		if (exist(tutorialName))
		{
			if (!containsUUID(uuid, tutorialName))
			{
				File file = this.getFile(fileName);
				if (file != null)
				{
					YamlConfiguration config = this.getConfig(file);
					config.set("Tutorials." + tutorialName + "." + uuid.toString(), null);
					this.saveFile(config, file);
				}
			}
		}
	}
	
	public Integer getReward(String tutorialName)
	{
		Integer prize = null;
		
		if (exist(tutorialName))
		{
			File file = this.getFile(fileName);
			if (file != null)
			{
				YamlConfiguration config = this.getConfig(file);
				prize = config.getInt("Tutorials." + tutorialName + ".Reward");
			}
		}
		
		return prize;
	}
	
	public Integer getExperienceReward(String tutorialName)
	{
		Integer experience = null;
		
		if (exist(tutorialName))
		{
			File file = this.getFile(fileName);
			if (file != null)
			{
				YamlConfiguration config = this.getConfig(file);
				experience = config.getInt("Tutorials." + tutorialName + ".ExperienceReward");
			}
		}
		
		return experience;
	}
	
	public String getDescription(String tutorialName)
	{
		String description = null;
		
		if (exist(tutorialName))
		{
			File file = this.getFile(fileName);
			if (file != null)
			{
				YamlConfiguration config = this.getConfig(file);
				ConfigurationSection tutorial = getTutorial(tutorialName);
				description = tutorial.getString("Description");
			} else if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("No file could be found named " + fileName);
			}
		} else if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("No tutorial could be found named " + tutorialName);
		}
		
		return description;
	}
	
	public ConfigurationSection getTutorials()
	{
		ConfigurationSection section = null;
		
		File file = this.getFile(fileName);
		if (file != null)
		{
			YamlConfiguration config = this.getConfig(file);
			section = config.getConfigurationSection("Tutorials");
		}
		
		return section;
	}
	
	public ConfigurationSection getTutorial(String tutorialName)
	{
		ConfigurationSection section = null;
		
		if (exist(tutorialName))
		{
			File file = this.getFile(fileName);
			if (file != null)
			{
				YamlConfiguration config = this.getConfig(file);
				ConfigurationSection tutorials = config.getConfigurationSection("Tutorials");
				section = config.getConfigurationSection("Tutorials." + tutorialName);
			}
		}
		
		return section;
	}
}
