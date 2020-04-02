package Friends;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.Listener;

import Main.Main;

public class MentionNameEvent implements Listener
{
	private Main main;
	public MentionNameEvent(Main main) 
	{
		this.main = main;
	}
	public static HashMap<UUID, Integer> click = new HashMap<UUID, Integer>();

//	@EventHandler
//	public void Mention(@SuppressWarnings("deprecation") PlayerChatEvent e)
//	{
//		String message = e.getMessage().toLowerCase();
//		for (Player target: Bukkit.getOnlinePlayers())
//		{
//			UUID tu = target.getUniqueId();
//			if (message.contains(target.getName().toLowerCase()))
//			{
//				if (!target.getName().equals(e.getPlayer().getName()))
//				{
//					if (!click.containsKey(tu))
//					{
//						click.put(tu, Integer.valueOf(0));
//						click.put(tu, click.get(tu) +1);
//						target.playSound(target.getLocation(), SoundHandler.NOTE_PLING, 1.0F, 1.0F);
//					} else
//					{
//						click.put(tu, click.get(tu) +1);
//						if (click.get(tu) == 3)
//						{
//							target.playSound(target.getLocation(), SoundHandler.NOTE_PLING, 1.0F, 1.0F);
//
//							click.remove(tu);
//						}
//					}
//				}
//			}
//		}
//	}
}
