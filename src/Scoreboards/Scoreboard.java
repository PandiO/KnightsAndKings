package Scoreboards;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import Afk.AfkEvents;
import Handlers.ColorOptions;
import Main.Main;
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
	
	public org.bukkit.scoreboard.Scoreboard getScoreBoard()
	{
		ScoreboardManager manager = Bukkit.getScoreboardManager();
	    org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
	    Team defaults = board.registerNewTeam("default");
	    Team noble = board.registerNewTeam("noble");
	    Team royal = board.registerNewTeam("royal");
	    Team dragonblood = board.registerNewTeam("dragonblood");
	    Team staff = board.registerNewTeam("staff");
	    Team staffmode = board.registerNewTeam("staffmode");
	    Team owner = board.registerNewTeam("owner");
	    Team ownermode = board.registerNewTeam("ownermode");
	    Team co_owner = board.registerNewTeam("co_owner");
	    Team afk = board.registerNewTeam("afk");
	    Team hsHider = board.registerNewTeam("hshider");
	    Team hsSeeker = board.registerNewTeam("hsseeker");
	    
	    defaults.setPrefix("§7");
	    noble.setPrefix("§e");
	    royal.setPrefix("§b");
	    dragonblood.setPrefix("§c");
	    staff.setPrefix("§9§l");
	    staffmode.setPrefix("§9§o");
	    owner.setPrefix("§5§l");
	    ownermode.setPrefix("§5§o");
	    co_owner.setPrefix("§5§l");
	    afk.setPrefix("§7§oAFK - ");
	    hsHider.setPrefix("§aHider");
	    hsSeeker.setPrefix("§7[§cS§7] §c");
	    
	    hsHider.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
	    hsHider.setCanSeeFriendlyInvisibles(true);
	    hsSeeker.setCanSeeFriendlyInvisibles(true);
	    Objective health = board.registerNewObjective("Health", "health");
	    health.setDisplaySlot(DisplaySlot.BELOW_NAME);
	    health.setDisplayName(ColorOptions.error + "❤");
	    
	    return board;
	}
	
	public Team getTeam(Player player, org.bukkit.scoreboard.Scoreboard board, String team)
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
	
	public void setBoard(List<User> Users)
	{
		Main.logMessage("Setting board");
		List<User> TargetUsers = null;
		org.bukkit.scoreboard.Scoreboard board = getScoreBoard();
		
		if (Users != null)
		{
			TargetUsers = Users;
		} else
		{
			TargetUsers = main.users;
		}
		
		for (User user : TargetUsers)
		{
			Main.logMessage("Setting board for user " + user.getUsername());
			UUID uuid = user.getUUID();
			Player player = user.getPlayer();
			Team team = null;
			
			Siege siege = Sieges.Sieges.findSiege(user);
			if (siege != null && siege.getProgress())
			{
				Main.logMessage("Found participating siege for user " + user.getUsername());
				siege.createScoreboard(board, user);
			} else
			if (Main.HideAndSeek.getParticipating(user) && Main.HideAndSeek.getProgress())
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
					main.logMessage("player in seekers: " + participant.getUser().getUsername());
					team = getTeam(player, board, "hsseeker");
				} else
				{
					main.logMessage("player in hiders: " + participant.getUser().getUsername());
					team = getTeam(player, board, "hshider");
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
		    		team = getTeam(player, board, "ownermode");
		    	} else
		    	{
	    			team = getTeam(player, board, "owner");
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
		    		team = getTeam(player, board, "ownermode");
		    	} else
		    	{
		    		team = getTeam(player, board, "co_owner");
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
		    		team = getTeam(player, board, "staffmode");
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
							team = getTeam(player, board, "staffmode");
		    			} else
		    			{
							team = getTeam(player, board, "staff");
		    			}
		    		} else
		    		{
						team = getTeam(player, board, "staff");
		    		}
		    	}
			} else if (user.getDonatorName().equalsIgnoreCase("noble"))
			{
		    	if (main.debug)
		    	{
		    		Bukkit.getConsoleSender().sendMessage("adding to noble team!");
		    	}
				team = getTeam(player, board, "noble");
			} else if (user.getDonatorName().equalsIgnoreCase("royal"))
			{
		    	if (main.debug)
		    	{
		    		Bukkit.getConsoleSender().sendMessage("adding to royal team!");
		    	}
				team = getTeam(player, board, "royal");
			} else if (user.getDonatorName().equalsIgnoreCase("dragon blood"))
			{
		    	if (main.debug)
		    	{
		    		Bukkit.getConsoleSender().sendMessage("adding to db team!");
		    	}
				team = getTeam(player, board, "dragonblood");
			} else
			{
		    	if (main.debug)
		    	{
		    		Bukkit.getConsoleSender().sendMessage("adding to default team!");
		    	}
				team = getTeam(player, board, "default");
			}
		    if (AfkEvents.getPlayerAfk(player) != null)
		    {
			    team = getTeam(player, board, "afk");
		    }
		    
		    if (team != null)
		    {
		    	team.addPlayer(player);
		    } else
		    {
		    	board.getTeam("default").addPlayer(player);
		    }
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
			( (CraftPlayer) player).getHandle().playerConnection.sendPacket(headerfooter);
		}
		for (User user : main.users)
		{
			Player player = user.getPlayer();
			player.setScoreboard(board);
			//player.setHealth(player.getHealth());
		}
	}
	
	
//	public void setScoreBoard(Player player, String team, Boolean specialmodus)
//	{
//	    ScoreboardManager manager = Bukkit.getScoreboardManager();
//	    org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
//	    Team defaults = board.registerNewTeam("default");
//	    Team noble = board.registerNewTeam("noble");
//	    Team royal = board.registerNewTeam("royal");
//	    Team dragonblood = board.registerNewTeam("dragonblood");
//	    Team staff = board.registerNewTeam("staff");
//	    Team staffmode = board.registerNewTeam("staffmode");
//	    Team owner = board.registerNewTeam("owner");
//	    Team ownermode = board.registerNewTeam("ownermode");
//	    Team co_owner = board.registerNewTeam("co_owner");
//	    Team afk = board.registerNewTeam("afk");
//	    
//	    defaults.setPrefix("§7");
//	    noble.setPrefix("§e");
//	    royal.setPrefix("§b");
//	    dragonblood.setPrefix("§c");
//	    staff.setPrefix("§9§l");
//	    staffmode.setPrefix("§9§o");
//	    owner.setPrefix("§5§l");
//	    ownermode.setPrefix("§5§o");
//	    co_owner.setPrefix("§5§l");
//	    afk.setPrefix("§7§o[AFK] ");
//		PacketPlayOutPlayerListHeaderFooter headerfooter = new PacketPlayOutPlayerListHeaderFooter();
//		try {
//		    Field header = headerfooter.getClass().getDeclaredField("a");
//		    Field footer = headerfooter.getClass().getDeclaredField("b");
//		    header.setAccessible(true);
//		    footer.setAccessible(true);
//		    header.set(headerfooter, ChatSerializer.a("\"§7Welcome to §9Knights and Kings\""));
//		    footer.set(headerfooter, ChatSerializer.a("\"§aVote for awesome bonuses! Vote with §6/Vote\""));
//		} catch (Exception ex) {
//		    ex.printStackTrace();
//		}
//		( (CraftPlayer) player).getHandle().playerConnection.sendPacket(headerfooter);
//		
//		Team subjectTeam = null;
//	    if (team.equalsIgnoreCase("default"))
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to default team 2!");
//	    	}
//			defaults.addPlayer(player);
//			subjectTeam = defaults;
//	    }
//	    if (team.equalsIgnoreCase("noble"))
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to noble team 2!");
//	    	}
//	    	noble.addPlayer(player);
//			subjectTeam = noble;
//	    }
//	    if (team.equalsIgnoreCase("royal"))
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to royal team 2!");
//	    	}
//	    	royal.addPlayer(player);
//			subjectTeam = royal;
//	    }
//	    if (team.equalsIgnoreCase("dragon blood") || team.equalsIgnoreCase("dragonblood") || team.equalsIgnoreCase("db"))
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to db team 2!");
//	    	}
//	    	dragonblood.addPlayer(player);
//			subjectTeam = dragonblood;
//	    }
//	    if (team.equalsIgnoreCase("staff"))
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to staff team 2!");
//	    	}
//	    	if (specialmodus == true)
//	    	{
//	    		staffmode.addPlayer(player);
//				subjectTeam = staffmode;
//	    	} else
//	    	{
//	    		staff.addPlayer(player);
//				subjectTeam = staff;
//	    	}
//	    }
//	    if (team.equalsIgnoreCase("co-owner"))
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to co-owner team 2!");
//	    	}
//	    	if (specialmodus == true)
//	    	{
//	    		ownermode.addPlayer(player);
//				subjectTeam = ownermode;
//	    	} else
//	    	{
//	    		co_owner.addPlayer(player);
//				subjectTeam = co_owner;
//	    	}
//	    }
//	    if (team.equalsIgnoreCase("owner"))
//	    {
//	    	if (main.debug)
//	    	{
//	    		Bukkit.getConsoleSender().sendMessage("adding to owner team 2!");
//	    	}
//	    	if (specialmodus == true)
//	    	{
//	    		ownermode.addPlayer(player);
//				subjectTeam = ownermode;
//	    	} else
//	    	{
//	    		owner.addPlayer(player);
//				subjectTeam = owner;
//	    	}
//	    }
//	    for (Team teams : board.getTeams())
//	    {
//	    	if (teams.getName().equalsIgnoreCase(subjectTeam.getName()))
//	    	{
//	    		if (main.debug)
//	    		{
//	    			Bukkit.getConsoleSender().sendMessage("Team found " + teams.getName());
//	    		}
//		    	teams.addPlayer(player);
//	    	}
//	    }
//	    player.setScoreboard(board);
//	    player.setHealth(player.getHealth());
////	    this.updateList(player, board);
//	    
//	}
//	
//	public void updateList(User user, org.bukkit.scoreboard.Scoreboard board)
//	{
//		Player player = user.getPlayer();
//		UUID uuid = user.getUUID();
//		if (player.hasPermission("k&k.owner"))
//		{
//			if (user.inOwnerModus())
//			{
//				user.setOwnerMode(true);
//				board.getTeam("ownermode");
//			} else
//			{
//				user.setOwnerMode(false);
//				board.getTeam("owner");
//			}
//		} else
//		if (player.hasPermission("k&k.co-owner") && !player.hasPermission("k&k.owner"))
//		{
//			if (user.inOwnerModus())
//			{
//				user.setOwnerMode(true);
//				board.getTeam("ownermode");
//			} else
//			{
//				user.setOwnerMode(false);
//				board.getTeam("co-owner");
//			}
//		} else
//		if (player.hasPermission("k&k.staff") && !player.hasPermission("k&k.co-owner"))
//		{
//			if (user.inStaffModus())
//			{
//				user.setStaffMode(true);
//				board.getTeam("staffmode");
//			} else
//			{
//				user.setStaffMode(false);
//				board.getTeam("staff");
//			}
//		} else if (user.getDonatorName().equalsIgnoreCase("noble"))
//		{
//			board.getTeam("noble").addPlayer(player);
//		} else if (user.getDonatorName().equalsIgnoreCase("royal"))
//		{
//			board.getTeam("royal").addPlayer(player);
//		} else if (user.getDonatorName().equalsIgnoreCase("dragon blood"))
//		{
//			board.getTeam("dragonblood").addPlayer(player);
//		} else
//		{
//			board.getTeam("default").addPlayer(player);
//		}
//		PacketPlayOutPlayerListHeaderFooter headerfooter = new PacketPlayOutPlayerListHeaderFooter();
//		try {
//		    Field header = headerfooter.getClass().getDeclaredField("a");
//		    Field footer = headerfooter.getClass().getDeclaredField("b");
//		    header.setAccessible(true);
//		    footer.setAccessible(true);
//		    header.set(headerfooter, ChatSerializer.a("\"§9Welcome to §6Knights and Kings\""));
//		    footer.set(headerfooter, ChatSerializer.a("\"§aVote for awesome bonuses! Vote with §6/Vote\""));
//		} catch (Exception ex) {
//		    ex.printStackTrace();
//		}
//		((CraftPlayer) player).getHandle().playerConnection.sendPacket(headerfooter);
//		player.setScoreboard(board);
//	}
}
