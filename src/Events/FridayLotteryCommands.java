package Events;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;
import Users.User;

public class FridayLotteryCommands implements CommandExecutor
{
	FridayLottery lottery = new FridayLottery();
	public Main main;
	public FridayLotteryCommands(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of Lottery-commands",
			ColorOptions.stats + "-/lottery <enter>",
			ColorOptions.stats + "-/lottery <info>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("lottery"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				UUID uuid = player.getUniqueId();
				if (args.length == 1)
				{
					String subCommand = args[0];
					if (subCommand.equalsIgnoreCase("enter"))
					{
						User user = new User(uuid);
						lottery.addPlayer(player, user.getAdress());
						user.destroy();
					} else if (subCommand.equalsIgnoreCase("info"))
					{
						List<String> info = Arrays.asList(new String[] {
								ColorOptions.statsbrackets,
								ColorOptions.statsformat + "Information about the friday-lottery",
								ColorOptions.stats + "-Drawing: " + ColorOptions.statsresults + lottery.getExpireDay() + ColorOptions.stats + " at " + ColorOptions.statsresults + lottery.format.format(lottery.getExpireTime()) + ColorOptions.message + " (Central European Time)",
								ColorOptions.stats + "-Prize: " + ColorOptions.statsresults + lottery.getPrize() + ColorOptions.message + " (250000 + (50000x amount of participants))",
								ColorOptions.stats + "-Entry price: " + ColorOptions.statsresults + lottery.getPrice(),
								ColorOptions.statsbrackets
						});
						for (String msg : info)
						{
							player.sendMessage(msg);
						}
					} else
					{
						for (String msg : commandhelp)
						{
							player.sendMessage(msg);
						}
					}
				} else
				{
					for (String msg : commandhelp)
					{
						player.sendMessage(msg);
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
}
