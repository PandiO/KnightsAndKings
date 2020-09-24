package DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Minigames.MiniGame;

public interface MiniGames 
{
	public static CopyOnWriteArrayList<MiniGame> MiniGames = new CopyOnWriteArrayList<MiniGame>();
	
	public static MiniGame findMiniGame(MiniGame miniGame)
	{
		MiniGame mg = null;
		
		for (MiniGame mgs : MiniGames)
		{
			if (mgs == miniGame)
			{
				mg = mgs;
				break;
			}
		}
		
		return mg;
	}
	
	public static List<Integer> getMiniGameIDs()
	{
		List<Integer> ids = new ArrayList<Integer>();
		
		for (MiniGame mg : MiniGames)
		{
			ids.add(mg.getId());
		}
		
		return ids;
	}
	
	public static Integer getUnusedId()
	{
		Integer ids = 0;
		
		List<Integer> idList = getMiniGameIDs();
		
		if (idList.isEmpty())
		{
			return ids;
		}
		
		while(!getMiniGameIDs().contains(ids))
		{
			ids++;
		}
		
		return ids;
	}
}
