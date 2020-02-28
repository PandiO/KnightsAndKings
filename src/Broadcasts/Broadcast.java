package Broadcasts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import Handlers.ColorOptions;
import Handlers.Messages;
import Main.Main;
import Tutorial.Tutorial;
import Tutorial.TutorialEvents;

public class Broadcast 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	public HashMap<Integer, List<String>> broadcastList = new HashMap<Integer, List<String>>();
	
    public void startBroadcastTask()
    {
    	broadcastList.put(0, Messages.messages00);
    	broadcastList.put(1, Messages.messages01);
    	broadcastList.put(2, Messages.messages02);
    	broadcastList.put(3, Messages.messages03);
    	broadcastList.put(4, Messages.messages04);
    	broadcastList.put(5, Messages.messages05);
    	broadcastList.put(6, Messages.messages06);
    	broadcastList.put(7, Messages.messages07);
    	broadcastList.put(8, Messages.messages08);
    	broadcastList.put(9, Messages.messages09);
    	broadcastList.put(10, Messages.messages10);
    	broadcastList.put(11, Messages.messages11);
    	broadcastList.put(12, Messages.messages12);
    	broadcastList.put(13, Messages.messages13);
    	broadcastList.put(14, Messages.messages14);
    	broadcastList.put(15, Messages.messages15);
    	broadcastList.put(16, Messages.messages16);

    	new BukkitRunnable()
		{
			public void run()
			{
				if (Bukkit.getOnlinePlayers().size() >= 2)
				{
					Broadcast();
				}
			}
		}.runTaskTimer(main, 20, 600*20);
    }
	
	public void Broadcast()
	{
		Integer msg = 0;
		msg = main.getRandom(0, 16);
		List<Player> list = new ArrayList<Player>();
		for (Tutorial tut : TutorialEvents.tutorials)
		{
			for (Player target : Bukkit.getOnlinePlayers())
			{
				if (tut.target != target)
				{
					if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Player " + target.getName() + " excluded from broadcast due to tutorial");
					}
					list.add(target);
				}
			}
		}
		
		for (Player receiver : list)
		{
			receiver.sendMessage(ColorOptions.statsbrackets);
			for (String lines : broadcastList.get(msg))
			{
				receiver.sendMessage(lines);
			}
			receiver.sendMessage(ColorOptions.statsbrackets);
		}
	}
}
