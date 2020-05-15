/**
 * 
 */
package HideAndSeek;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import Handlers.ColorOptions;
import Main.Main;
import Minigames.MGTeam;

/**
 * @author pandi
 *
 */
public class HSTeam extends MGTeam {

	/**
	 * @param number The teamNumber
	 * @param name The name of the team
	 * @param color The chatColor of the team
	 * @param bannerColor The banner color if flags are involved. Otherwise put null
	 * @param scoreboard The bukkit scoreboard for tablist names etc.
	 * @param winMessage The message being displayed when this team wins
	 */
	public HSTeam(int number, String name, ChatColor color, short bannerColor,
			org.bukkit.scoreboard.Scoreboard scoreboard, ArrayList<String> winMessage) {
		super(number, name, color, bannerColor, scoreboard, winMessage);
		// TODO Auto-generated constructor stub
	}
	
	public void CreateSideBarBoard(HideAndSeek hideAndSeek)
	{
		org.bukkit.scoreboard.Scoreboard board = this.GetScoreboard();
		Integer teamNumber = this.GetNumber();
		String teamName = this.GetName();
		Integer boardLength = 5;	
		
		org.bukkit.scoreboard.Objective sideBoard = null;
		sideBoard = board.getObjective("hs_" + teamNumber);
		
		if (sideBoard == null)
		{
			sideBoard = board.registerNewObjective("hs_" + teamNumber, "dummy");
			sideBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
		} else
		{
			sideBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
			for (int i = 1; i < 3; i++)
			{
				HashMap<String, Integer> calcTimeOld = Main.getCalculatedTime(hideAndSeek.getProgressSeconds()+i); 
				board.resetScores("Time remaining: " + ColorOptions.message + "" + calcTimeOld.get("minute") + ":" + calcTimeOld.get("second"));
			}
//			Score timeScoreOld = sideBoard.getScore("Time remaining: " + ColorOptions.message + "" + calcTimeOld.get("minute") + ":" + calcTimeOld.get("second"));
//			timeScoreOld.setScore(0);
		}
		
		sideBoard.setDisplayName(ColorOptions.KAKColor + "Hide and Seek");
		
		Score teamScore = sideBoard.getScore(ColorOptions.message + "You are a " + this.GetColor() + teamName);
		teamScore.setScore(boardLength);
		
		boardLength--;
		
		Score spaceScore = sideBoard.getScore(" ");
		spaceScore.setScore(boardLength);
		
		boardLength--;
		
		String objectiveString = null;
		
		if (this.GetNumber() == 1)
		{
			objectiveString = ColorOptions.error + "Seekers: ";
		} else if (this.GetNumber() == 2)
		{
			objectiveString = ColorOptions.error + "Hiders: ";
		}
		Score objectiveScore = sideBoard.getScore(objectiveString);
		objectiveScore.setScore(boardLength);
		
		Score spaceScore2 = sideBoard.getScore("  ");
		spaceScore2.setScore(boardLength);
		
		boardLength--;
		
		HashMap<String, Integer> calcTime = Main.getCalculatedTime(hideAndSeek.getProgressSeconds()); 
		Score timeScore = sideBoard.getScore("Time remaining: " + ColorOptions.message + "" + calcTime.get("minute") + ":" + calcTime.get("second"));
		timeScore.setScore(boardLength);
	}
	
	/**
	 * Should be called when someone is caught!
	 * @param hideAndSeek
	 * @param oldTeamAmount should be the old team amount of the opposite team
	 */
	public void UpdateSideBarBoard(HideAndSeek hideAndSeek, int oldTeamAmount)
	{
		Scoreboard board = this.GetScoreboard();
		org.bukkit.scoreboard.Objective sideBoard = board.getObjective("hs_" + this.GetNumber());
		int boardSlot = 0;
		
		String objectiveKind = null;
		
		if (this.GetNumber() == 1)
		{
			objectiveKind = ColorOptions.error + "Seekers: ";
		} else if (this.GetNumber() == 2)
		{
			objectiveKind = ColorOptions.error + "Hiders: ";
		}
		
		if (objectiveKind != null)
		{
			Score oldScore = sideBoard.getScore(objectiveKind + oldTeamAmount);
			boardSlot = oldScore.getScore();
			board.resetScores(objectiveKind + oldTeamAmount);
		}
		
		Score newScore = sideBoard.getScore(objectiveKind);
		if (boardSlot > 0)
		{
			newScore.setScore(boardSlot);
		}
	}

}
