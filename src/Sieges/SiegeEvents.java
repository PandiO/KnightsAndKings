package Sieges;

import org.bukkit.event.Listener;

import Main.Main;

public class SiegeEvents implements Listener
{
	private Main main;
	public SiegeEvents(Main main)
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void onMove(PlayerMoveEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		
//		try
//		{
//			user = users.getUser(uuid);
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (UserIsNpcException ex)
//		{
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		for (Scenario scenario : Sieges.Scenarios)
//		{
//			
//		}
//	}
}
