package DataManager.spawnpoints;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import Main.Main;

public interface SpawnpointSiegeScenarios 
{
	public static List<Integer> FetchSpawnpointIDs(Integer scenarioID)
	{
		List<Integer> list = null;
		
		try
		{
			PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM SpawnpointSiegeScenario WHERE ScenarioID = ?");
			stmt.setInt(1, scenarioID);
			
			ResultSet results = stmt.executeQuery();
			
			while (results.next())
			{
				list.add(results.getInt("SpawnpointID"));
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return list;
	}
}
