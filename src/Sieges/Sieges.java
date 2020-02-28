package Sieges;

import java.util.concurrent.CopyOnWriteArrayList;

import Main.Main;
import SpawnPoints.SpawnPoint;
import Users.User;

public class Sieges 
{
	static SpawnPoint spawnpoint = new SpawnPoint();
	static Main main = Main.getPlugin(Main.class);
	public static CopyOnWriteArrayList<Siege> Sieges = new CopyOnWriteArrayList<Siege>();
	public static CopyOnWriteArrayList<SiegeScenario> Scenarios = new CopyOnWriteArrayList<SiegeScenario>();
	public static CopyOnWriteArrayList<SiegeCreation> SiegeCreations = new CopyOnWriteArrayList<SiegeCreation>();
	public static CopyOnWriteArrayList<SiegeCreation> stashedSiegeCreations = new CopyOnWriteArrayList<SiegeCreation>();

	public static SiegeCreation getSiegeCreation(User user)
	{
		SiegeCreation sc = null;
		
		for (SiegeCreation SiegeCreations : SiegeCreations)
		{
			if (SiegeCreations.user == user)
			{
				sc = SiegeCreations;
				break;
			}
		}
		
		return sc;
	}
	
	public static Siege findSiege(User user)
	{
		Siege siege = null;
		
		for (Siege sieges : Sieges)
		{
			if (sieges.getParticipating(user))
			{
				siege = sieges;
				break;
			}
		}
		
		return siege;
	}
	
	
	public static void destroySiege(Siege siege)
	{
		siege = null;
		System.gc();
	}
}
