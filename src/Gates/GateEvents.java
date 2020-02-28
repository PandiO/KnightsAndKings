package Gates;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import API_methods.WorldGuard;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Towns.Town;
import Towns.TownEvents;
import Users.User;
import Users.Users;

public class GateEvents implements Listener
{
	Town town = new Town();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public GateEvents(Main main)
	{
		this.main = main;
	}
	
	@EventHandler
	public void onGateHit(BlockDamageEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getBlock();
		Location blockLocation = block.getLocation();
		Integer gateID = this.worldguard.getStructureIDbyRegion("gate", blockLocation, this.worldguard.getRegionManager(blockLocation.getWorld()));
		UUID uuid = player.getUniqueId();
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
		
		if (gateID == null)
		{
			return;
		}
		
		Gate gate = Gates.findGate(gateID);
		
		if (gate == null)
		{
			return;
		}

		gate.damageGate(user);
	}
	
	@EventHandler
	public void onGateMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		
		User user = null;
		
		try
		{
			user = Users.getUser(player.getUniqueId());
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
		
		if (!TownEvents.inTown.containsKey(user))
		{
			return;
		}
		
		Integer townID = TownEvents.inTown.get(user);
		
		for (Gate gate : Gates.gates)
		{
			if (gate.getTownID() == townID && gate.getGateEntity() != null)
			{
				List<Entity> entities = gate.getGateEntity().getNearbyEntities(Gates.activeRange, Gates.activeRange, Gates.activeRange);
				
				if (entities.isEmpty())
				{
					gate.toggleActive(false);
				} else
				{
					for (Entity entity : entities)
					{
						if (entity instanceof Player)
						{
							gate.toggleActive(true);
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onGateTouch(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();
		
		User user = null;
		
		try
		{
			user = Users.getUser(player.getUniqueId());
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
		
		if (block == null || block.getType() == Material.AIR || e.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		
		if (Gates.findGateToggle(user) != null)
		{
			GateToggle toggle = Gates.findGateToggle(user);
			toggle.setTimeOut(System.currentTimeMillis() + 4000L);
			Location loc = block.getLocation();
			Integer gateID = this.worldguard.getStructureIDbyRegion("gate", loc, this.worldguard.getRegionManager(loc.getWorld()));
			if (gateID != null)
			{
				Gate gate = Gates.findGate(gateID);
				
				if (gate != null)
				{
					toggle.setGate(gate);
					toggle.getRequestTimeOut().cancel();
					gate.toggleClosed();
					player.sendMessage(ColorOptions.messageachievement + "Succesfully toggled gate " + gate.getName());
					
					if (toggle.isPassthrough())
					{
						toggle.startPassthroughTask();
					}
				} else
				{
					player.sendMessage(ColorOptions.error + "Error while finding the gate of the clicked block!");
				}
			} else
			{
				player.sendMessage(ColorOptions.error + "Error while finding the gate of the clicked block!");
			}
		}
	}
}
