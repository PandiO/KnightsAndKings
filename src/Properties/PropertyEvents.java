package Properties;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldGuard;
import Assignments.Assignment;
import Assignments.AssignmentEnterPropertySpecific;
import Exceptions.UserNotFoundException;
import Handlers.ErrorHandlers;
import Main.Main;
import Products.Product;
import Towns.Town;
import Users.User;
import Users.Users;

public class PropertyEvents implements Listener
{
	Town town = new Town();
	Product product = new Product();
	WorldGuard worldguard = new WorldGuard();
	private Main main;
	public PropertyEvents(Main main)
	{
		this.main = main;
	}
	
	@EventHandler
	public void onEnter(RegionEnterEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		ProtectedRegion region = e.getRegion();
		
		if (this.worldguard.isPropertyRegion(region))
		{
			Integer propertyID = this.worldguard.getStructureIDbyRegion(region);
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
			
			if (e.isCancelled() == false)
			{
				for (Assignment assignment : user.getAssignmentList())
				{
					if (assignment instanceof AssignmentEnterPropertySpecific)
					{
						AssignmentEnterPropertySpecific Assignment = (AssignmentEnterPropertySpecific) assignment;
						Assignment.enterProperty(propertyID);
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(RegionLeaveEvent e)
	{
		
	}
}
