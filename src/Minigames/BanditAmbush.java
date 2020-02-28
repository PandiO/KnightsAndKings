package Minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;
import Titles.Title;
import Traits.TestTrait;
import Users.User;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class BanditAmbush 
{
	Title title = new Title();
	Main main = Main.getPlugin(Main.class);
	
	private BanditAmbush ambush;
	private User user;
	private int banditAmount = 1;
	private List<Integer> npcIDList = new ArrayList<Integer>();
	private List<NPC> npcList = new ArrayList<NPC>();
	private HashMap<User, Integer> killList = new HashMap<User, Integer>();
	private boolean fled = false;
	
	public BanditAmbush(User user)
	{
		this.user = user;
		Player player = user.getPlayer();
		Integer titleID = user.getTitleID();
		
		if (titleID > 5 && titleID <= 10)
		{
			this.banditAmount = main.getRandom(1, 2);
		} else if (titleID > 10 && titleID <= 15)
		{
			this.banditAmount = main.getRandom(2, 4);
		} else if (titleID > 15)
		{
			this.banditAmount = main.getRandom(3, 6);
		}
		
		BanditAmbushes.addAmbush(this);
		
	    for (int i = 0; i < this.banditAmount; i++)
	    {
		    NPC bandit = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Bandit");
		    bandit.setProtected(false);
		    this.npcIDList.add(bandit.getId());
		    this.npcList.add(bandit);
			bandit.addTrait(TestTrait.class);
			bandit.spawn(BanditAmbushes.getRandomSpawnLocation(player));
		}
	    
		player.sendMessage(ColorOptions.falsecommand + "Watch out! Bandits ambushed you!");
		player.sendMessage(ColorOptions.falsecommand + "Kill them to receive a reward!");
	}
	
	public User getUser()
	{
		return this.user;
	}
	
	public int getBanditAmount()
	{
		return this.banditAmount;
	}
	
	public List<Integer> getNPCIDList()
	{
		return this.npcIDList;
	}
	
	public List<NPC> getNPCList()
	{
		return this.npcList;
	}
	
	public HashMap<User, Integer> getKillList()
	{
		return this.killList;
	}
	
	public boolean containsNPCID(Integer NPCID)
	{
		boolean contains = false;
		
		
		for (Integer NPCIDs : this.npcIDList)
		{
			if (NPCID == NPCIDs)
			{
				contains = true;
				break;
			}
		}
		
		return contains;
	}
	
	public boolean containsNPC(NPC NPC)
	{
		boolean contains = false;
		
		for (NPC bandit : this.npcList)
		{
			if (bandit.getId() == NPC.getId())
			{
				contains = true;
				break;
			}
		}
		
		return contains;
	}
	
	public void killBandit(NPC bandit, User user)
	{
		if (this.npcList.size() <= 0)
		{
			this.killedAll();
			return;
		}
		
		if (!this.npcList.contains(bandit))
		{
			return;
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Bandit kill found!");
		}
		this.npcList.remove(bandit);
		if (user != null)
		{
			if (this.killList.containsKey(user))
			{
				this.killList.put(user, this.killList.get(user)+1);
			} else
			{
				this.killList.put(user, 1);
			}
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Size: " + this.npcList.size());
		}
		if (this.npcList.size() <= 0)
		{
			this.killedAll();
		}
	}
	
	public void killedAll()
	{
		if (this.npcList.size() > 0)
		{
			return;
		}
		Integer coins = user.getMultipliedInt(main.getRandom(user.getSalary()/2, user.getSalary()));
		Integer titleID = user.getTitleID();
		Integer exp = 0;
		if (titleID < 18)
		{
			Integer nextrankexp = title.getExpmin(titleID+1);
			Integer currentrankexp = title.getExpmin(titleID);
			Integer part = (nextrankexp - currentrankexp)/8;
			exp = user.getMultipliedInt(main.getRandom(part/10, part));
		}
		
		Integer banditAmount = this.npcIDList.size();
		coins /= banditAmount;
		exp /= banditAmount;
		
		for (User killer : this.killList.keySet())
		{
			Player player = killer.getPlayer();
			Integer kills = this.killList.get(killer);
			
			killer.addCoins(coins*kills);
			killer.addExperience(exp*kills, true);
			
			player.sendMessage(ColorOptions.messageachievement + "Congratulations! you've killed " + kills + " of " + banditAmount + " bandits!");
			player.sendMessage(ColorOptions.statsformat + "You received the following rewards:");
			player.sendMessage(ColorOptions.stats + "Coins: " + ColorOptions.statsresults + ColorOptions.formatCurrency((coins*kills)));
			player.sendMessage(ColorOptions.stats + "Experience: " + ColorOptions.statsresults + ColorOptions.formatCurrency((exp*kills)));
		}
		
		this.clearAmbush();
	}
	
	public void fled()
	{
		if (!fled)
		{
			Player player = user.getPlayer();
			player.sendMessage(ColorOptions.message + "You succesfully fled from the bandits!");
			this.fled = true;
		}
	}
	
	public void clearAmbush()
	{
		BanditAmbushes.removeAmbush(this);
		for (Integer npcID : this.npcIDList)
		{
			if (CitizensAPI.getNPCRegistry().getById(npcID) != null)
			{
				NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
				npc.destroy();
				CitizensAPI.getNPCRegistry().deregister(npc);
			}
		}
	}
}
