package Currency;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import Donator.Donator;
import Handlers.ColorOptions;
import Handlers.SalaryPayoutEvent;
import Handlers.SoundHandler;
import Main.Main;
import Titles.Title;
import Users.User;

public class SalaryPayout implements Listener
{
	private Main main;
	Donator donator = new Donator();
	Title title = new Title();
	public SalaryPayout(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void Payout(SalaryPayoutEvent e)
	{
		main.DBreconnect();
		User user = e.getUser();
		if (user != null)
		{
			Player player = Bukkit.getServer().getPlayer(user.getUsername());
			Integer titleID = user.getTitleID();
			Integer coins = user.getMultipliedInt(title.getSalary(titleID));
			user.addCoins(coins);
			user.saveSalaryTime();
//			Main.registeredPlayerSalary.put(uuid, user.getSalaryTime());
			String gender = user.getGenderName();
			
			if (gender.equalsIgnoreCase("male"))
			{
				player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your salary: " + ColorOptions.salarysubjects + ColorOptions.formatCurrency(coins));
			} else if (gender.equalsIgnoreCase("female"))
			{
				player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylady! you received your salary: " + ColorOptions.salarysubjects + ColorOptions.formatCurrency(coins));
			} else
			{
				player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your salary: " + ColorOptions.salarysubjects + ColorOptions.formatCurrency(coins));
			}
			player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 3.0F, 3.0F);
		}
	}
}
