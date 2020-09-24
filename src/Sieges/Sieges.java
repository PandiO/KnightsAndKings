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
	public static CopyOnWriteArrayList<Scenario> Scenarios = new CopyOnWriteArrayList<Scenario>();
	public static CopyOnWriteArrayList<ScenarioCreation> ScenarioCreations = new CopyOnWriteArrayList<ScenarioCreation>();
	public static CopyOnWriteArrayList<ScenarioCreation> stashedSiegeCreations = new CopyOnWriteArrayList<ScenarioCreation>();

	public static ScenarioCreation getSiegeCreation(User user)
	{
		ScenarioCreation sc = null;
		
		for (ScenarioCreation ScenarioCreations : ScenarioCreations)
		{
			if (ScenarioCreations.getUser() == user)
			{
				sc = ScenarioCreations;
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
