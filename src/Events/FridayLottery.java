package Events;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Resources.YmlFile;
import Users.User;
import Users.Users;

public class FridayLottery 
{
	YmlFile file = new YmlFile();
	Main main = Main.getPlugin(Main.class);
	private String fileName = "friday-lottery";
	public static boolean closedEntry = false;
	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	public DayOfWeek getExpireDay()
	{
		DayOfWeek day = null;
		
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		day = DayOfWeek.valueOf(config.getString("ExpireDay").toUpperCase());
		
		return day;
	}
	
	public Date getExpireTime()
	{
		Date time = null;
		
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		try {
			time = format.parse(config.getString("ExpireTime"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return time;
	}
	
	public Integer getPrize()
	{
		Integer prize = null;
		
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		prize = config.getInt("Prize");
		
		return prize;
	}
	
	public Integer getPrice()
	{
		Integer price = null;
		
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		price = config.getInt("Price");
		
		return price;
	}
	
	public Integer getParticipants()
	{
		Integer participants = null;
		
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		participants = config.getInt("Participants");
		
		return participants;
	}
	
	public void setParticipants(Integer amount)
	{
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		config.set("Participants", amount);
		try {
			config.save(file.getFile(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addParticipants(Integer amount)
	{
		Integer current = getParticipants();
		this.setParticipants(current+amount);
	}
	
	public void removeParticipants(Integer amount)
	{
		Integer current = getParticipants();
		setParticipants(current-amount);
	}
	
	public List<UUID> getPlayers()
	{
		List<UUID> list = new ArrayList<UUID>();
		
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		ConfigurationSection section = config.getConfigurationSection("Players");
		Set<String> players = section.getKeys(false);
		for (String uuidString : players)
		{
			UUID uuid = UUID.fromString(uuidString);
			list.add(uuid);
		}
		
		return list;
	}
	
	public Integer getAddressMatches(InetAddress address)
	{
		Integer matches = 0;
		
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		for (UUID uuid : getPlayers())
		{
			InetAddress possiblematch = null;
			try {
				possiblematch = InetAddress.getByName(config.getString("Players." + uuid.toString() + ".address"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (possiblematch == address)
			{
				matches++;
			}
			
		}
		
		return matches;
	}
	
	public void addPlayer(Player player, InetAddress address)
	{
		UUID uuid = player.getUniqueId();
		User user = Users.getUser(uuid);
		try
		{
			String username = player.getName();
			if (closedEntry == false)
			{
				if (!getPlayers().contains(uuid))
				{
					if (getAddressMatches(address) < 3)
					{
						if (user.getCoins() >= getPrice())
						{
							user.removeCoins(getPrice());
							YamlConfiguration config = file.getConfig(file.getFile(fileName));
							config.set("Prize", getPrize()+50000);
							ConfigurationSection players = config.getConfigurationSection("Players");
							ConfigurationSection playersection = players.createSection(uuid.toString());
							playersection.set("Username", username);
							playersection.set("Address", address.toString().replaceAll("/", ""));
							try {
								config.save(file.getFile(fileName));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							addParticipants(1);
							player.sendMessage(ColorOptions.messageachievement + "You are now participating in the weekly lottery with a jackpot of " + ColorOptions.messagesubjects + getPrize() + " coins!");
							player.sendMessage(ColorOptions.messageformat + "The winner will be announced on " + ColorOptions.messagesubjects + getExpireDay() + ColorOptions.messageformat + " at " + ColorOptions.messagesubjects + format.format(getExpireTime()));
							player.sendMessage(ColorOptions.message + "(Central European Time)");
						} else
						{
							player.sendMessage(ColorOptions.error + "You don't have enough coins! Entry price is " + getPrice());
						}
					} else
					{
						player.sendMessage(ColorOptions.error + "There are max 3 players per IP address allowed to participate!");
					}
				} else
				{
					player.sendMessage(ColorOptions.error + "You are already participating in the lottery!");
				}
			} else
			{
				player.sendMessage(ColorOptions.error + "Entries to this lottery drawing are closed, please wait for the drawing");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public UUID drawWinner()
	{
		UUID winner = null;
		
		List<UUID> list = getPlayers();
		List<UUID> participants = new ArrayList<UUID>();
		
		for (UUID uuid : list)
		{
//			User user = new User(uuid);
//			if (user.isLucky())
//			{
//				participants.add(uuid);
//			}
//			user.destroy();
		}
		participants.addAll(list);
		Collections.shuffle(participants);
		winner = participants.get(main.getRandom(0, participants.size()-1));
		
		return winner;
	}
	
	public void endLottery()
	{
		closedEntry = true;
		List<UUID> participants = getPlayers();
		if (getParticipants() >= 1)
		{
			UUID winner = drawWinner();
			Bukkit.broadcastMessage(ColorOptions.message + "[Lottery]" + ColorOptions.messageachievement + "► Announcing the winner of the weekly lottery in " + ColorOptions.messagesubjects + "5 seconds!");
			BukkitTask i = new BukkitRunnable()
			{
	            public void run()
	            {
	    			for (Player player : Bukkit.getOnlinePlayers())
					{
						  player.playSound(player.getLocation(), SoundHandler.CLICK, 1.0F, 0.1F);
					}
	            }
			}.runTaskTimer(main, 0, 1*20);
	    	new BukkitRunnable()
	    	{
	    		public void run()
	    		{
	    			User user = null;
	    			if (Users.getUser(winner) != null)
	    			{
	    				user = Users.getUser(winner);
	    			} else
	    			{
	    				user = new User(winner);
	    			}
					i.cancel();
					Bukkit.broadcastMessage(ColorOptions.message + "[Lottery]" + ColorOptions.messageachievement + "► The winner of this weekly lottery is " + ColorOptions.messagesubjects + Users.fetchUsernamebyUUID(winner) + ColorOptions.messageachievement + " with a prize of " + ColorOptions.messagesubjects + getPrize() + " coins!");
					user.addCoins(getPrize());
					for (Player player : Bukkit.getOnlinePlayers())
					{
						player.playSound(player.getLocation(), SoundHandler.FIREWORK_BLAST, 1.0F, 0.1F);
						if (player.getUniqueId() == winner)
						{
							player.getWorld().playSound(player.getLocation(), SoundHandler.LEVEL_UP, 1.0F, 0.1F);
							player.getWorld().playEffect(player.getLocation(), Effect.HEART, 1.0F);
							player.sendMessage(ColorOptions.message + "[Lottery]" + ColorOptions.messageachievement + "► You won the lottery and received " + ColorOptions.messagesubjects + getPrize() + " coins!");
						}
					}
					user.destroy();
	    		}
	    	}.runTaskLater(main, 5*20);
		} else
		{
			Bukkit.broadcastMessage(ColorOptions.message + "[Lottery]" + ColorOptions.error + "► The minimum of 6 players was not reached, no winner has been drawn!");
			for (UUID uuid : participants)
			{
				User user = null;
				if (Users.getUser(uuid) != null)
				{
					user = Users.getUser(uuid);
				} else
				{
					user = new User(uuid);
				}
				user.addCoins(getPrice());
				if (Bukkit.getPlayer(uuid) != null)
				{
					Bukkit.getPlayer(uuid).sendMessage(ColorOptions.message + "[Lottert]► Your entry fee of " + getPrice() + " has been refunded to your balance");
				}
			}
		}
    	new BukkitRunnable()
    	{
    		public void run()
    		{
				clearLottery();
				closedEntry = false;
    		}
    	}.runTaskLater(main, 6*20);
	}
	
	public void clearLottery()
	{
		YamlConfiguration config = file.getConfig(file.getFile(fileName));
		config.set("Prize", 250000);
		for (UUID uuid : getPlayers())
		{
			config.set("Players." + uuid.toString(), null);
		}
		try {
			config.save(file.getFile(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setParticipants(0);
	}
}
