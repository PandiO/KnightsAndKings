package Donator;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;

@SuppressWarnings("deprecation")
public class DonatorChat implements Listener
{
	private Main main;
	public DonatorChat(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onChat(PlayerChatEvent e)
	{
		Player player = (Player) e.getPlayer();
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
		try
		{
			String dn = player.getName();
			String rawmessage = ("" + e.getMessage().charAt(0)).toUpperCase() + e.getMessage().substring(1);
			String message = ChatColor.translateAlternateColorCodes('&', rawmessage);
			if (player.hasPermission("k&k.owner"))
			{
				e.setFormat(ColorOptions.ownerformat + "[" + ColorOptions.ownersubjects + "OWNER" + ColorOptions.ownerformat + "]-{" + ColorOptions.ownersubjects + "" + ChatColor.BOLD + user.getTitleName() + ColorOptions.ownerformat + "}- " + ColorOptions.ownersubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
			} else if (player.hasPermission("k&k.co-owner"))
			{
				e.setFormat(ColorOptions.ownerformat + "[" + ColorOptions.ownersubjects + "CO-OWNER" + ColorOptions.ownerformat + "]-{" + ColorOptions.ownersubjects + "" + ChatColor.BOLD + user.getTitleName() + ColorOptions.ownerformat + "}- " + ColorOptions.ownersubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
			} else
			if (player.hasPermission("k&k.staff"))
			{
				e.setFormat(ColorOptions.staffformat + "[" + ColorOptions.staffsubjects + "STAFF" + ColorOptions.staffformat + "]-{" + ColorOptions.staffsubjects + "" + ChatColor.BOLD + user.getTitleName() + ColorOptions.staffformat + "}- " + ColorOptions.staffsubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
			} else
			{
				/*
				String dn = player.getName();
				player.setDisplayName(ColorOptions.nobleformat + "[B]-{" + ColorOptions.noblesubjects + "Noble " + user.getTitleName(pu) + ColorOptions.nobleformat + "}- " + ColorOptions.noblesubjects + dn + ChatColor.WHITE);
				 */
				if (user.getDonatorName().equalsIgnoreCase("noble"))
				{
					e.setFormat(ColorOptions.nobleformat + "-{" + ColorOptions.noblesubjects + "Noble " + user.getTitleName() + ColorOptions.nobleformat + "}- " + ColorOptions.noblesubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
				} else
				if (user.getDonatorName().equalsIgnoreCase("royal"))
				{
					e.setFormat(ColorOptions.royalformat + "-{" + ColorOptions.royalsubjects + "Royal " + user.getTitleName() + ColorOptions.royalformat + "}- " + ColorOptions.royalsubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
				} else
				if (user.getDonatorName().equalsIgnoreCase("dragon blood"))
				{
					e.setFormat(ColorOptions.dbformat + "-{" + ColorOptions.dbsubjects + "Dragon Blood " + user.getTitleName() + ColorOptions.dbformat + "}- " + ColorOptions.dbsubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
				} else
				{
					message = rawmessage;
					e.setFormat(ColorOptions.defaultformat + "-{" + ColorOptions.defaultsubjects + user.getTitleName() + ColorOptions.defaultformat + "}- " + ColorOptions.defaultsubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
				}
			}
		} catch(Exception exception)
		{
			exception.printStackTrace();
		}
	}
}
