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
import Scoreboards.ActionBar;
import Sieges.MainObjective;
import Sieges.Scenario;
import Sieges.SideObjective;
import Sieges.Siege;
import Sieges.SiegeMember;
import Sieges.Sieges;
import Users.User;

public class MGTeam 
{
	protected int Number;
	protected String Name;
	protected ChatColor Color = ChatColor.GRAY;
	protected short BannerColor = 5;
	protected Scoreboard Scoreboard;
	protected List<Participant> Members = new ArrayList<Participant>();
	protected List<String> winMessage;
	
	public MGTeam(int number, String name, ChatColor color, short bannerColor, Scoreboard scoreboard, ArrayList<String> winMessage)
	{
		this.Number = number;
		this.Name = name;
		this.Color = color;
		this.BannerColor = bannerColor;
		this.Scoreboard = scoreboard;
		this.winMessage = winMessage;
	}
	
	public int GetNumber()
	{
		return this.Number;
	}
	
	public String GetName()
	{
		return this.Name;
	}
	
	public ChatColor GetColor()
	{
		return this.Color;
	}
	
	public short GetBannerColor()
	{
		return this.BannerColor;
	}
	
	public Scoreboard GetScoreboard()
	{
		return this.Scoreboard;
	}
	
	public List<String> getWinMessage()
	{
		return this.winMessage;
	}
	
	public List<Participant> GetMembers()
	{
		return this.Members;
	}
	
	public List<SiegeMember> GetMembersAsSiegeMembers()
	{
		List<SiegeMember> members = new ArrayList<SiegeMember>();
		
		this.Members.forEach(m -> members.add((SiegeMember)m));
		
		return members;
	}
	
	public List<User> GetMemberUsers()
	{
		List<User> Users = new ArrayList<User>();
		
		this.Members.forEach(m -> Users.add(m.getUser()));
		
		return Users;
	}
	
	public void Reset()
	{
		this.Scoreboard = this.GetScoreboard();
		this.Members.clear();
	}
	
	public void SetName(String name)
	{
		this.Name = name;
	}
	
	public void SetColor(ChatColor color)
	{
		this.Color = color;
	}
	
	public void SetBannerColor(short bannerColor)
	{
		this.BannerColor = bannerColor;
	}
	
	public void SetScoreboard(Scoreboard scoreboard)
	{
		this.Scoreboard = scoreboard;
	}
	
	public void SetWinMessage(List<String> winMessage)
	{
		this.winMessage = winMessage;
	}
	
	public void SetMembers(List<Participant> participants)
	{
		this.Members = participants;
	}
	
	public void SetMembersAsUsers(List<User> users)
	{
		users.forEach(u -> this.Members.add(new Participant(u)));
	}
	
	public void AddMember(Participant participant)
	{
		if (!this.Members.contains(participant))
		{
			this.Members.add(participant);
			participant.SetTeam(this);
		}
	}
	
	public void RemoveMember(Participant participant)
	{
		if (this.Members.contains(participant))
		{
			this.Members.remove(participant);
			participant.SetTeam(null);
		}
	}
	
	public void AnnounceMembersActionBar(ActionBar message)
	{
		this.Members.forEach(m -> message.sendToPlayer(m.getUser().getPlayer()));
	}
	
	public void AnnounceMembers(List<String> message)
	{
		this.GetMemberUsers().forEach(m -> m.sendMessage(message));
	}
}
