package Scoreboards;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import Afk.AfkEvents;
import Handlers.ColorOptions;
import HideAndSeek.HideAndSeek;
import Main.Main;
import Minigames.MGTeam;
import Minigames.Participant;
import Sieges.Siege;
import Users.OwnerCommands;
import Users.User;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public class Scoreboard 
{
	
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);

	public PacketPlayOutPlayerListHeaderFooter getTabLayout()
	{
		PacketPlayOutPlayerListHeaderFooter headerfooter = new PacketPlayOutPlayerListHeaderFooter();
		try {
		    Field header = headerfooter.getClass().getDeclaredField("a");
		    Field footer = headerfooter.getClass().getDeclaredField("b");
		    header.setAccessible(true);
		    footer.setAccessible(true);
		    header.set(headerfooter, ChatSerializer.a("\"§7Welcome to §9Knights and Kings\""));
		    footer.set(headerfooter, ChatSerializer.a("\"§aVote for awesome bonuses! Vote with §6/Vote\""));
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		
		return headerfooter;
	}
	
	public org.bukkit.scoreboard.Scoreboard getScoreBoard()
	{
		ScoreboardManager manager = Bukkit.getScoreboardManager();
	    org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
	    
	    //Default rank
	    Team defaults = board.registerNewTeam("default");
	    defaults.setPrefix("§7");
	    
	    //First donatorrank
	    Team noble = board.registerNewTeam("noble");
	    noble.setPrefix("§e");
	    
	    //Second donatorrank
	    Team royal = board.registerNewTeam("royal");
	    royal.setPrefix("§b");
	    
	    //Third donatorrank
	    Team dragonblood = board.registerNewTeam("dragonblood");
	    dragonblood.setPrefix("§c");

	    
	    //Default staffrank
	    Team staff = board.registerNewTeam("staff");
	    staff.setPrefix("§9§l");
	    //Sepcial staff mode with enhanced capabilities
	    Team staffmode = board.registerNewTeam("staffmode");
	    staffmode.setPrefix("§9§o");
	    
	    //Co-owner rank, rank just below owner rank
	    Team co_owner = board.registerNewTeam("co_owner");
	    co_owner.setPrefix("§5§l");
	    
	    //Owner rank
	    Team owner = board.registerNewTeam("owner");
	    owner.setPrefix("§5§l");
	    //Special owner mode with enhanced capabilities, also for co-owner
	    Team ownermode = board.registerNewTeam("ownermode");
	    ownermode.setPrefix("§5§o");
	    
	    //Afk
	    Team afk = board.registerNewTeam("afk");
	    afk.setPrefix("§7§oAFK - ");
	    
	    //Hide and Seek
	    //Hider
	    Team hsHider = board.registerNewTeam("hshider");
	    hsHider.setPrefix("§aHider");
	    hsHider.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
	    hsHider.setCanSeeFriendlyInvisibles(true);    
	    //Seeker
	    Team hsSeeker = board.registerNewTeam("hsseeker");
	    hsSeeker.setPrefix("§7[§cS§7] §c");
	    hsSeeker.setCanSeeFriendlyInvisibles(true);
	    	
	    //Siege
	    //Team 1
	    Team siege1 = board.registerNewTeam("siege1");
	    siege1.setPrefix("§9");
	    siege1.setAllowFriendlyFire(false);
	    Team siege2 = board.registerNewTeam("siege2");
	    siege2.setPrefix("§c");
	    siege2.setAllowFriendlyFire(false);
	    
	    //Health
	    Objective health = board.registerNewObjective("Health", "health");
	    health.setDisplaySlot(DisplaySlot.BELOW_NAME);
	    health.setDisplayName(ColorOptions.error + "❤");
	    
	    return board;
	}
	
	public Team FetchTeam(org.bukkit.scoreboard.Scoreboard board, String team)
	{
		Team subjectTeam = null;
		
		for (Team teams : board.getTeams())
		{
			if (teams.getName().equalsIgnoreCase(team))
		    {
		    	subjectTeam = teams;
		    }
		}
	    
	    return subjectTeam;
	}
	
	public Team GetTeam(User user, org.bukkit.scoreboard.Scoreboard board)
	{
		UUID uuid = user.getUUID();
		Player player = user.getPlayer();
		if (board == null)
		{
			board = this.getScoreBoard();
		}
		Team team = null;
		
		Siege siege = Sieges.Sieges.findSiege(user);
		if (siege != null && siege.getProgress())
		{
			Participant participant = siege.getParticipant(user);
			MGTeam mGTeam = participant.GetTeam();
			MGTeam enemyTeam = null;
			Main.logMessage("Found participating siege for user " + user.getUsername());
			siege.createScoreboard(participant.GetTeam());
			team = FetchTeam(board, "siege" + mGTeam.GetNumber());
			
			if (mGTeam.GetNumber() == 1)
			{
				enemyTeam = siege.GetTeam2();
			} else if (mGTeam.GetNumber() == 2)
			{
				enemyTeam = siege.GetTeam1();
			}
			
			for (Participant enemy : enemyTeam.GetMembers())
			{
				this.FetchTeam(board, "siege" + enemyTeam.GetNumber()).addPlayer(enemy.getUser().getPlayer());
			}
		} else
		if (Main.HideAndSeek != null && Main.HideAndSeek.getParticipating(user) && Main.HideAndSeek.getProgress())
		{
			Main.logMessage("Setting hs scoreboard for " + user.getUsername());
			Participant participant = Main.HideAndSeek.getParticipant(user);
			for (User target : main.users)
			{
				if (!Main.HideAndSeek.getParticipating(target))
				{
					player.hidePlayer(target.getPlayer());
					target.getPlayer().hidePlayer(player);
				}
			}
			if (Main.HideAndSeek.getSeekers().contains(participant))
			{
				team = FetchTeam(board, "hsseeker");
			} else
			{
				team = FetchTeam(board, "hshider");
			}
		} else
	    if (player.hasPermission("k&k.owner"))
	    {
	    	if (main.debug)
	    	{
	    		Bukkit.getConsoleSender().sendMessage("adding to owner team!");
	    	}
	    	if (user.inOwnerModus())
	    	{
	    		for (Player target : Bukkit.getOnlinePlayers())
	    		{
	    			if (!target.hasPermission("k&k.staff"))
	    			{
	    				target.hidePlayer(player);
	    			}
	    		}
	    		team = FetchTeam(board, "ownermode");
	    	} else
	    	{
    			team = FetchTeam(board, "owner");
	    	}
	    } else
	    if (player.hasPermission("k&k.co-owner") && !player.hasPermission("k&k.owner"))	
	    {
	    	if (main.debug)
	    	{
	    		Bukkit.getConsoleSender().sendMessage("adding to co-owner team!");
	    	}
	    	if (user.inOwnerModus())
	    	{
	    		for (Player target : Bukkit.getOnlinePlayers())
	    		{
	    			if (!target.hasPermission("k&k.staff"))
	    			{
	    				target.hidePlayer(player);
	    			}
	    		}
	    		team = FetchTeam(board, "ownermode");
	    	} else
	    	{
	    		team = FetchTeam(board, "co_owner");
	    	}
	    } else if (player.hasPermission("k&k.staff") && !player.hasPermission("k&k.co-owner"))
		{
	    	if (main.debug)
	    	{
	    		Bukkit.getConsoleSender().sendMessage("adding to staff team!");
	    	}
	    	if (user.inStaffModus())
	    	{
	    		for (Player target : Bukkit.getOnlinePlayers())
	    		{
	    			if (!target.hasPermission("k&k.staff"))
	    			{
	    				target.hidePlayer(player);
	    			}
	    		}
	    		team = FetchTeam(board, "staffmode");
	    	} else
	    	{
	    		if (user.inOnQuit())
	    		{
	    			if (user.inEnableOnQuit())
	    			{
	    				user.setStaffMode(true);
						player.sendMessage(OwnerCommands.enabled);
						for (Player players : Bukkit.getOnlinePlayers())
						{
							if (!players.hasPermission("k&k.staff"))
							{
								players.hidePlayer(player);
							}
						}
						team = FetchTeam(board, "staffmode");
	    			} else
	    			{
						team = FetchTeam(board, "staff");
	    			}
	    		} else
	    		{
					team = FetchTeam(board, "staff");
	    		}
	    	}
		} else if (user.getDonatorName().equalsIgnoreCase("noble"))
		{
	    	if (main.debug)
	    	{
	    		Bukkit.getConsoleSender().sendMessage("adding to noble team!");
	    	}
			team = FetchTeam(board, "noble");
		} else if (user.getDonatorName().equalsIgnoreCase("royal"))
		{
	    	if (main.debug)
	    	{
	    		Bukkit.getConsoleSender().sendMessage("adding to royal team!");
	    	}
			team = FetchTeam(board, "royal");
		} else if (user.getDonatorName().equalsIgnoreCase("dragon blood"))
		{
	    	if (main.debug)
	    	{
	    		Bukkit.getConsoleSender().sendMessage("adding to db team!");
	    	}
			team = FetchTeam(board, "dragonblood");
		} else
		{
	    	if (main.debug)
	    	{
	    		Bukkit.getConsoleSender().sendMessage("adding to default team!");
	    	}
			team = FetchTeam(board, "default");
		}
	    if (AfkEvents.getPlayerAfk(player) != null)
	    {
		    team = FetchTeam(board, "afk");
	    }
	    
	    return team;
	}
	
	public void setBoard(List<User> Users)
	{
		Main.logMessage("Setting board");
		List<User> TargetUsers = null;
		org.bukkit.scoreboard.Scoreboard defaultBoard = getScoreBoard();
		
		if (Users != null)
		{
			TargetUsers = Users;
		} else
		{
			TargetUsers = main.users;
		}
		
		for (User user : TargetUsers)
		{
			Player player = user.getPlayer();
			Team team = null;
			
			Siege siege = Sieges.Sieges.findSiege(user);
			HideAndSeek hideAndSeek = Main.HideAndSeek;
			
			if (siege != null)
			{
				defaultBoard = siege.getParticipant(user).GetTeam().GetScoreboard();
			} else
			{
				player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
				player.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
				for(OfflinePlayer score : player.getScoreboard().getPlayers())
				{
			        player.getScoreboard().resetScores(score);
				}
				team = this.GetTeam(user, defaultBoard);
			}
			
			team = this.GetTeam(user, defaultBoard);
			
			team.addPlayer(player);
			
			player.setScoreboard(defaultBoard);
			player.setHealth(player.getHealth());
			
			( (CraftPlayer) player).getHandle().playerConnection.sendPacket(this.getTabLayout());
		}
	}
}
