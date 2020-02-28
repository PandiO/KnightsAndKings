package Experience;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import Handlers.ExperienceChangeEvent;
import Handlers.TitleChangeEvent;
import Main.Main;
import Titles.Title;
import Users.User;
import Users.Users;

public class ExperienceChangeEvents implements Listener
{
	Title title = new Title();
	private Main main;
	public ExperienceChangeEvents(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void ExperienceChangeEvent(ExperienceChangeEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = Users.getUser(uuid);
		try
		{
			Integer Experience = user.getExperience();
			Integer currentTitleID = user.getTitleID();
			Integer newTitleID = title.getTitleIDbyExp(Experience);
			Integer difference = newTitleID-currentTitleID;
			if (difference < 0)
			{
		        Bukkit.getServer().getPluginManager().callEvent(new TitleChangeEvent(user, currentTitleID, newTitleID, false));
			} else if (difference > 0)
			{
		        Bukkit.getServer().getPluginManager().callEvent(new TitleChangeEvent(user, currentTitleID, newTitleID, true));
			}
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
}
