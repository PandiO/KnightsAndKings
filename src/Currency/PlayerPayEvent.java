package Currency;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import Handlers.ColorOptions;
import Handlers.PlayerPayEventHandler;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;

public class PlayerPayEvent implements Listener
{
	private Main main;
	public PlayerPayEvent(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void Pay(PlayerPayEventHandler e)
	{
		User user = e.getUser();
		Player player = user.getPlayer();
		if (user != null)
		{
			int coinamount = e.getInteger();
			int experience = (int) Math.round(((coinamount/100)*1));
			user.addExperience(experience, true);
			player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 1.0F, 1.0F);
			player.sendMessage(ColorOptions.messageachievement + "You received " + ColorOptions.coinStats + experience + ColorOptions.messageachievement + " experience by trading with " + ColorOptions.messagesubjects + user.getUsername());
		}
	}
}
