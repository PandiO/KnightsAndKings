package Currency;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import Donator.Donator;
import Handlers.ColorOptions;
import Handlers.IncomePayoutEvent;
import Handlers.SoundHandler;
import Main.Main;
import Properties.Property;
import Users.User;

public class IncomePayout implements Listener
{
	private Main main;
	Donator donator = new Donator();
	Property property = new Property();
	public IncomePayout(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void Payout(IncomePayoutEvent e)
	{
		User user = e.getUser();
		if (user != null)
		{
			user.saveIncomeTime();

			Player player = Bukkit.getServer().getPlayer(user.getUsername());
			Integer income = user.getMultipliedInt(user.getIncome());
			if (property.getIDList(null, null) != null && property.getIDList(null, null).size() != 0 && income > 0)
			{
				user.addCoins(income);
				String gender = user.getGenderName();
				if (gender.equalsIgnoreCase("male"))
				{
					player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylord! you received your income: " + ColorOptions.salarysubjects + ColorOptions.formatCurrency(income));
				} else if (gender.equalsIgnoreCase("female"))
				{
					player.sendMessage(ColorOptions.salaryformat + "" + ChatColor.BOLD + "Mylady! you received your income: " + ColorOptions.salarysubjects + ColorOptions.formatCurrency(income));
				}
				player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 3.0F, 3.0F);
			}
		}
	}
}
