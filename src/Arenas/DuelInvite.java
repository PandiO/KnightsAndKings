package Arenas;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Menu.Menu;
import Users.User;

public class DuelInvite 
{
	Arena arena = new Arena();
	Menu menu = new Menu();
	Main main = Main.getPlugin(Main.class);
	
	Integer defaultTimeOut = 10;
	public List<Integer> senderClickList = Arrays.asList(0, 46, 48);
	public List<Integer> senderItemList = Arrays.asList(10, 11, 12, 19, 20, 21);
	public List<Integer> targetClickList = Arrays.asList(8, 50, 52);
	public List<Integer> targetItemList = Arrays.asList(14, 15, 16, 22, 23, 24);
	public User sender;
	public User target;
	public Integer arenaID;
	Integer timeOut = defaultTimeOut;
	BukkitTask timeOutTask = null;
	
	public DuelInvite(User sender, User target, Integer arenaID)
	{
		this.sender = sender;
		this.target = target;
		this.arenaID = arenaID;
		startTimeOut();
		inviteTarget();
		DuelCommands.inviteList.add(this);
	}
	
	public void acceptInvite()
	{
		sender.getPlayer().sendMessage(ColorOptions.messageachievement + "Player " + ColorOptions.messagesubjects + target.getUsername() + ColorOptions.messageachievement + " accepted your invitation!");
		Inventory duelMenu = menu.createDuelMenu(sender, target, this.arenaID);
		menu.openDuelMenu(sender.getPlayer(), duelMenu);
		menu.openDuelMenu(target.getPlayer(), duelMenu);	
		if (this.timeOutTask != null)
		{
			this.timeOutTask.cancel();
		}
	}
	
	public void inviteTarget()
	{
		if (Bukkit.getOnlinePlayers().contains(target))
		{
			target.getPlayer().playSound(target.getPlayer().getLocation(), SoundHandler.NOTE_PLING, 1.0F, 1.0F);
			target.getPlayer().sendMessage(ColorOptions.messageachievement + "You received a duel invitation from " + ColorOptions.messagesubjects + sender.getUsername());
			target.getPlayer().sendMessage(ColorOptions.message + "You have 10 seconds to respond with " + ColorOptions.messagesubjects + "/duel <accept/deny>");
		}
	}
	
	public void startTimeOut()
	{
		BukkitTask task = new BukkitRunnable()
    	{
    		public void run()
    		{
				removeInvite();
				sender.getPlayer().sendMessage(ColorOptions.error + "You invitation timed out!");
    		}
    	}.runTaskLater(main, timeOut*20);
    	this.timeOutTask = task;
	}
	
	public void cancelInvite()
	{
		sender.getPlayer().sendMessage(ColorOptions.error + "You canceled the duel invite to " + ColorOptions.messagesubjects + target.getUsername());
		removeInvite();
	}
	
	public void denyInvite()
	{
		sender.getPlayer().sendMessage(ColorOptions.error + "Player " + ColorOptions.messagesubjects + target.getUsername() + ColorOptions.error + " denied your invitation!");
		removeInvite();
	}
	
	public List<User> getPlayers()
	{
		return Arrays.asList(sender, target);
	}
	
	public void removeInvite()
	{
		if (this.timeOutTask != null)
		{
			this.timeOutTask.cancel();
		}
		DuelCommands.inviteList.remove(this);
		for (User user : getPlayers())
		{
			if (main.users.contains(user))
			{
				if (user.getPlayer().getOpenInventory().getTopInventory() != null)
				{
					Inventory inv = user.getPlayer().getOpenInventory().getTopInventory();
					if (inv.getName().equalsIgnoreCase(ColorOptions.stats + "Choose duel-type and place bets!"))
					{
						user.getPlayer().closeInventory();
					}
				}
			}
		}
	}
}
