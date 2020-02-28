package Donator;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import Handlers.ColorOptions;
import Main.Main;
import Titles.Title;
import Users.User;
import Users.Users;


public class PlayerJoin_List implements Listener
{
	Scoreboards.Scoreboard scoreboard = new Scoreboards.Scoreboard();
	Title title = new Title();
	Donator donator = new Donator();
	private Main main;
	public PlayerJoin_List(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e)
	{
		//Located in the JoinEvents in the Users package
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = Users.getUser(uuid);
		if (main.existUser(uuid))
		{
			Integer genderID = user.getGenderID();
			Integer titleID = user.getTitleID();
			Integer donatorID = user.getDonatorID();
			if (player.hasPermission("k&k.owner"))
		    {
				if (user.inOwnerModus())
				{
		    		e.setQuitMessage(null);
				} else
				{
		    		e.setQuitMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "★ Owner " + ColorOptions.messagesubjects + player.getName() + ChatColor.DARK_PURPLE + " left the kingdoms!");
				}
		    } else if (player.hasPermission("k&k.co-owner") && !player.hasPermission("k&k.owner"))
		    {
				if (user.inOwnerModus())
				{
		    		e.setQuitMessage(null);
				} else
				{
		    		e.setQuitMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "★ Co-Owner " + ColorOptions.messagesubjects + player.getName() + ChatColor.DARK_PURPLE + " left the kingdoms!");
				}
		    } else
			if (player.hasPermission("k&k.staff") && !player.hasPermission("k&k.co-owner"))
			{
				if (user.inStaffModus())
				{
					e.setQuitMessage(null);
				} else
				{
					e.setQuitMessage(ColorOptions.falsecommand + "" + ChatColor.BOLD + "► Staff-member " + ColorOptions.messagesubjects + player.getName() + ColorOptions.falsecommand + " left the kingdoms!");
				}
			} else if (user.getDonatorName().equalsIgnoreCase("noble"))
			{
				e.setQuitMessage(ColorOptions.falsecommand + "► A " + ColorOptions.noblesubjects + "noble " + title.getTitleName(titleID, genderID) + ColorOptions.falsecommand + " left the kingdoms!");
			} else if (user.getDonatorName().equalsIgnoreCase("royal"))
			{
				e.setQuitMessage(ColorOptions.falsecommand + "► A " + ColorOptions.royalsubjects + "royal " + title.getTitleName(titleID, genderID) + ColorOptions.falsecommand + " left the kingdoms!");
			} else if (user.getDonatorName().equalsIgnoreCase("dragon blood"))
			{
				e.setQuitMessage(ColorOptions.falsecommand + "► A " + ColorOptions.dbsubjects + "dragon blood " + title.getTitleName(titleID, genderID) + ColorOptions.falsecommand + " left the kingdoms!");
			} else
			{
				e.setQuitMessage(null);
			}
		}
		Users.updateScoreBoard(null);
	}
	

}
