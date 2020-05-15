/**
 * 
 */
package Minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import Handlers.ColorOptions;
import Main.Main;
import Sieges.MainObjective;
import Sieges.Objective;
import Sieges.Scenario;
import Sieges.SideObjective;
import Sieges.Siege;
import Sieges.SiegeMember;
import Sieges.SiegeSpawnpoint;
import Sieges.Sieges;

/**
 * @author pandi
 *
 */
public class SiegeTeam extends MGTeam 
{
	public Siege siege;
	public List<SiegeSpawnpoint> spawnpoints = new ArrayList<SiegeSpawnpoint>();
	public List<Objective> heldObjectives = new ArrayList<Objective>();
	/**
	 * @param number
	 * @param name
	 * @param color
	 * @param bannerColor
	 * @param scoreboard
	 * @param winMessage
	 */
	public SiegeTeam(Siege siege, int number, String name, ChatColor color, short bannerColor,
			org.bukkit.scoreboard.Scoreboard scoreboard, ArrayList<String> winMessage) {
		super(number, name, color, bannerColor, scoreboard, winMessage);
		// TODO Auto-generated constructor stub
		
		this.siege = siege;
	}
	
	public List<SiegeSpawnpoint> getSpawnpoints()
	{
		return this.spawnpoints;
	}
	
	public SiegeSpawnpoint getSpawnpoint(String name)
	{
		SiegeSpawnpoint spawnpoint = null;
		
		for (SiegeSpawnpoint s : this.spawnpoints)
		{
			if (s.getName().equalsIgnoreCase(name))
			{
				spawnpoint = s;
				break;
			}
		}
		
		return spawnpoint;
	}
	
	public void setSpawnpoints(List<SiegeSpawnpoint> spawnpoints)
	{
		this.spawnpoints = spawnpoints;
	}
	
	public void addSpawnpoint(SiegeSpawnpoint spawnpoint)
	{
		if (!this.spawnpoints.contains(spawnpoint))
		{
			this.spawnpoints.add(spawnpoint);
		}
	}
	
	public List<Objective> getHeldObjectives()
	{
		return this.heldObjectives;
	}
	
	public Objective getHeldObjective(String name)
	{
		Objective objective = null;
		
		for (Objective o : this.heldObjectives)
		{
			if (o instanceof SideObjective)
			{
				SideObjective so = (SideObjective) o;
				if (so.getName().equalsIgnoreCase(name))
				{
					objective = so;
					break;
				}
			}
		}
		
		return objective;
	}
	
	public void setHeldObjectives(List<Objective> objectives)
	{
		this.heldObjectives = objectives;
	}
	
	public void addHeldObjectives(Objective objective)
	{
		if (!this.heldObjectives.contains(objective))
		{
			this.heldObjectives.add(objective);
		}
	}
	
	public void removeHeldObjectives(Objective objective)
	{
		if (this.heldObjectives.contains(objective))
		{
			this.heldObjectives.remove(objective);
			
			for (SiegeMember m : this.GetMembersAsSiegeMembers())
			{
				if (m.getCurrentSpawnpoint().getSpawnpointID() == objective.getSpawnpointID())
				{
					m.setCurrentSpawnpoint(this.getSpawnpoints().get(0));
				}
			}
		}
	}
	
	public void CreateSideBarBoard(Siege siege)
	{
		org.bukkit.scoreboard.Scoreboard board = this.GetScoreboard();
		Integer teamNumber = this.GetNumber();
		String teamName = this.GetName();
		Scenario scenario = siege.getScenario();
		Integer boardLength = 5;	
		boardLength += scenario.getSideObjectives().size();
		
		org.bukkit.scoreboard.Objective sideBoard = null;
		sideBoard = board.getObjective("siege_" + Sieges.Sieges.indexOf(siege) + "_" + teamNumber);
		
		if (sideBoard == null)
		{
			sideBoard = board.registerNewObjective("siege_" + Sieges.Sieges.indexOf(siege) + "_" + teamNumber, "dummy");
			sideBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
		} else
		{
			sideBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
			for (int i = 1; i < 3; i++)
			{
				HashMap<String, Integer> calcTimeOld = Main.getCalculatedTime(siege.progressSeconds+i); 
				board.resetScores("Time remaining: " + ColorOptions.message + "" + calcTimeOld.get("minute") + ":" + calcTimeOld.get("second"));
			}
//			Score timeScoreOld = sideBoard.getScore("Time remaining: " + ColorOptions.message + "" + calcTimeOld.get("minute") + ":" + calcTimeOld.get("second"));
//			timeScoreOld.setScore(0);
		}
		
		sideBoard.setDisplayName(ColorOptions.KAKColor + "Siege");
		
		Score teamScore = sideBoard.getScore(ColorOptions.message + "Team: " + this.GetColor() + teamName);
		teamScore.setScore(boardLength);
		
		boardLength--;
		
		Score spaceScore = sideBoard.getScore(" ");
		spaceScore.setScore(boardLength);
		
		boardLength--;
		
		MainObjective MO = scenario.getMainObjective();
		Score MOScore = sideBoard.getScore(ColorOptions.message + "Main Objective: " + MO.getCapturePercentage() + "% Captured");
		MOScore.setScore(boardLength);
		
		boardLength--;
		
		for (SideObjective SO : scenario.getSideObjectives())
		{
			Score SOScore = sideBoard.getScore(ColorOptions.message + SO.getName() + ": " + SO.getCapturePercentage() + "% Captured");
			SOScore.setScore(boardLength);
			boardLength--;
		}
		
		Score spaceScore2 = sideBoard.getScore("  ");
		spaceScore2.setScore(boardLength);
		
		boardLength--;
		
		HashMap<String, Integer> calcTime = Main.getCalculatedTime(siege.progressSeconds); 
		Score timeScore = sideBoard.getScore("Time remaining: " + ColorOptions.message + "" + calcTime.get("minute") + ":" + calcTime.get("second"));
		timeScore.setScore(boardLength);
		
		this.SetScoreboard(board);
	}

	public void UpdateSideBarBoard(Siege siege, Objective objective, int oldPercentage)
	{
		Scoreboard board = this.GetScoreboard();
		org.bukkit.scoreboard.Objective sideBoard = board.getObjective("siege_" + Sieges.Sieges.indexOf(siege) + "_" + this.GetNumber());
		int boardSlot = 0;
		
		String objectiveKind = null;
		
		if (objective instanceof MainObjective)
		{
			objectiveKind = ColorOptions.message + "Main Objective: ";
		} else if (objective instanceof SideObjective)
		{
			SideObjective so = (SideObjective)objective;
			objectiveKind = ColorOptions.message + so.getName() + ": ";
		}
		
		if (objectiveKind != null)
		{
			String scoreName = objectiveKind + oldPercentage + "% Captured";
			Score oldScore = sideBoard.getScore(scoreName);
			boardSlot = oldScore.getScore();
			board.resetScores(scoreName);
		}
		
		Score newScore = sideBoard.getScore(objectiveKind + objective.getCapturePercentage() + "% Captured");
		if (boardSlot > 0)
		{
			newScore.setScore(boardSlot);
		}
	}
	
	public void ResetSideBarBoard()
	{
		Scoreboard board = this.GetScoreboard();
		org.bukkit.scoreboard.Objective sideBoard = board.getObjective("siege_" + Sieges.Sieges.indexOf(siege) + "_" + this.GetNumber());
		
		sideBoard.unregister();
	}
	
	@Override
	public void Reset()
	{
		this.Scoreboard = this.GetScoreboard();
		this.Members.clear();
		
		this.ResetSideBarBoard();
	}
}
