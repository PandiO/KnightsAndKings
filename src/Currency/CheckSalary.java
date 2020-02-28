package Currency;

import org.bukkit.event.Listener;

import Main.Main;
import Titles.Title;

public class CheckSalary implements Listener
{
	private Main main;
	Title title = new Title();
	public CheckSalary(Main main) 
	{
		this.main = main;
	}
	
//	public static HashMap<UUID, Integer> Salary = new HashMap<UUID, Integer>();//Manages the time before you recieve your salary
//	public static HashMap<UUID, Integer> Payout = new HashMap<UUID, Integer>();
//	
//	@EventHandler
//	public void RegisterOnJoin(PlayerJoinEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		if (user.existUser(player.getName()))
//		{
//		    long playersalarytime = user.getSalaryTime(uuid);
//		    long playerincometime = user.getIncomeTime(uuid);
//		    Main.registeredPlayerSalary.put(uuid, playersalarytime);
//		    Main.registeredPlayerIncome.put(uuid, playerincometime);
//		}
//	}
//	
//	@EventHandler
//	public void UnregisterOnLeave(PlayerQuitEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		main.registeredPlayerSalary.remove(uuid);
//		main.registeredPlayerIncome.remove(uuid);
//	}
}
