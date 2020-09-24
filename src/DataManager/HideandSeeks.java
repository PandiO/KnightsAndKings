package DataManager;

import java.util.concurrent.CopyOnWriteArrayList;

import HideAndSeek.HideAndSeek;
import Users.User;

public interface HideandSeeks 
{
	public static CopyOnWriteArrayList<HideAndSeek> HideAndSeeks = new CopyOnWriteArrayList<HideAndSeek>();
	
	public static HideAndSeek findHideAndSeek(User user)
	{
		HideAndSeek hs = null;
		
		for (HideAndSeek h : HideAndSeeks)
		{
			if (h.getParticipating(user))
			{
				hs = h;
				break;
			}
		}
		
		return hs;
	}
	
	public static HideAndSeek findHideAndSeek(Integer hsID)
	{
		HideAndSeek hs = null;
		
		for (HideAndSeek h : HideAndSeeks)
		{
			if (h.getId() == hsID)
			{
				hs = h;
				break;
			}
		}
		
		return hs;
	}
	
	public static void destroyHideAndSeek(HideAndSeek hs)
	{
		hs = null;
		System.gc();
	}
}
