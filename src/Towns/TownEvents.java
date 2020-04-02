package Towns;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import Assignments.Assignment;
import Assignments.AssignmentTravelRandom;
import Assignments.AssignmentTravelSpecific;
import DataManager.Worldguard;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.EnterTownEvent;
import Handlers.ErrorHandlers;
import Handlers.LeaveTownEvent;
import Handlers.SoundHandler;
import Main.Main;
import Minigames.BanditSpawn;
import Scoreboards.ActionBar;
import Titles.Title;
import Users.User;
import Users.Users;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class TownEvents implements Listener
{
	Town town = new Town();
	Title title = new Title();
	private Main main;
	public TownEvents(Main main)
	{
		this.main = main;
	}
	
	public static HashMap<User, Integer> inTown = new HashMap<User, Integer>();
	
	@EventHandler
	public void onEnter(RegionEnterEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		ProtectedRegion region = e.getRegion();
		
		if (Worldguard.isTownRegion(region))
		{
			Integer townID = Worldguard.getStructureIDbyRegion(region);
			Integer requiredTitleID = town.getRequiredTitleID(townID);
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			} catch (UserIsNpcException ex)
			{
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			}
			
			if (!inTown.containsKey(user) || inTown.get(user) != townID)
			{
				if (main.leveledTownEnter)
				{
					if (user.getTitleID() < requiredTitleID)
					{
						player.sendMessage(ColorOptions.falsecommand + "Halt! We do not want scum like you in this town! Come back when you are a " + ColorOptions.messagesubjects + (title.getTitleName(requiredTitleID, 1)) + " or a " + (title.getTitleName(requiredTitleID, 2)));
						e.setCancelled(true);
						
					}
				}
				
				if (e.isCancelled() == false)
				{
					DataManager.Structures.Gates.instantiateAll(townID);
					
					
					ActionBar greetMessage = new ActionBar(ColorOptions.message + "Entering the town of " + ColorOptions.messagesubjects + this.town.getTownName(townID));
					greetMessage.sendToPlayer(player);
					player.playSound(player.getLocation(), SoundHandler.DOOR_OPEN, 0.5F, 1.0F);
					inTown.put(user, townID);
					
					if (!town.getUserIDListbyTown(townID).contains(user.getID()))
					{
						town.saveDiscoveredTown(townID, user.getID());
						Integer part = user.getExpPart(4);
						Integer exp = user.getMultipliedInt(main.getRandom((Integer) part/4, part));
						
						Integer coins = user.getMultipliedInt(main.getRandom(1000, 50000));
						Integer gems = user.getMultipliedInt(main.getRandom(5, 15));
						user.addExperience(exp.intValue(), true);
					    user.addCoins(coins);
					    user.addGems(gems);
					    player.sendMessage(ColorOptions.messageachievement + "You discovered a new town and received " + ColorOptions.messagesubjects + coins + ColorOptions.messageachievement + " coins, " + ColorOptions.messagesubjects + gems + ColorOptions.messageachievement + " gems and " + ColorOptions.messagesubjects + exp + ColorOptions.messageachievement + " experience!");
					    player.playSound(player.getLocation(), SoundHandler.LEVEL_UP, 0.6F, 1.0F);
					}
					
					Bukkit.getServer().getPluginManager().callEvent(new EnterTownEvent(user, townID));
				}
			}
			if (e.isCancelled() == false)
			{
				for (Assignment assignment : user.getAssignmentList())
				{
					if (assignment instanceof AssignmentTravelRandom)
					{
						AssignmentTravelRandom Assignment = (AssignmentTravelRandom) assignment;
						Assignment.enterTown(townID);
					}
					if (assignment instanceof AssignmentTravelSpecific)
					{
						AssignmentTravelSpecific Assignment = (AssignmentTravelSpecific) assignment;
						Assignment.enterTown(townID);
					}
				}
				if (BanditSpawn.banditID.containsKey(uuid))
				{
				    NPCRegistry registry = CitizensAPI.getNPCRegistry();
					for (Integer i : BanditSpawn.banditID.get(uuid))
					{
						NPC bandit = registry.getById(i);
						registry.deregister(bandit);
					}
					BanditSpawn.banditID.remove(uuid);
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(RegionLeftEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		ProtectedRegion region = e.getRegion();
		
		if (Worldguard.isTownRegion(region))
		{
			Integer townID = Worldguard.getStructureIDbyRegion(region);
			User user = null;
			
			try
			{
				user = Users.getUser(uuid);
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			} catch (Exception ex)
			{
				ex.printStackTrace();
				ErrorHandlers.userNotFoundAction(null, player, true);
				return;
			}
			
			if (inTown.containsKey(user))
			{
				RegionManager regionmanager = Worldguard.getRegionManager(player.getWorld());
				if (Worldguard.getStructureIDbyRegion("town", player.getLocation(), regionmanager) == null)
				{
					ActionBar greetMessage = new ActionBar(ColorOptions.message + "Leaving the town of " + ColorOptions.falsecommand + this.town.getTownName(townID));
					greetMessage.sendToPlayer(player);
					player.playSound(player.getLocation(), SoundHandler.DOOR_CLOSE, 0.5F, 1.0F);
					Bukkit.getServer().getPluginManager().callEvent(new LeaveTownEvent(user, inTown.get(user)));
					inTown.remove(user);
				}
			}
		}
	}
	
//	@EventHandler
//	public void onDamage(EntityDamageByEntityEvent e)
//	{
//		Entity entity = e.getEntity();
//		Location location = entity.getLocation();
//		RegionManager manager = Worldguard.getRegionManager(location.getWorld());
//		Integer townID = Worldguard.getStructureIDbyRegion("town", location, manager);
//		if (townID == null)
//		{
//			return;
//		}
//		
//		if (!(entity instanceof Player))
//		{
//			return;
//		}
//		
//		Player player = (Player) entity;
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		
//		try
//		{
//			user = Users.getUser(uuid);
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (UserIsNpcException ex)
//		{
//			
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		if (Worldguard.isArenaBattleground(location, manager))
//		{
//			return;
//		}
//		
//		if (townID != this.town.getTownID("wilderness"))
//		{
//			e.setCancelled(true);
//			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Prevented pvp event");
//		}
//	}
}
