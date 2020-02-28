package Quests;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Handlers.QuestStateChangeEvent;
import Handlers.SoundHandler;
import Main.Main;
import Properties.Property;
import Traits.Shopkeeper;
import Users.User;

public class Quest 
{
	protected Property property = new Property();
	protected Main main = Main.getPlugin(Main.class);
	protected int ID = -1;
	protected int TypeID = -1;
	protected String Name;
	protected String Description;
	protected int GoalAmount;
	protected int ProgressAmount;
	protected int DeliveredAmount;
	protected User User;
	protected User RequestedUser;
	protected int PropertyID;
	protected int CoinReward;
	protected int GemReward;
	protected int ExperienceReward;
	protected long ExpireLong;
	protected boolean isAssigned = false;
	protected boolean isCompleted = false;
	protected boolean isCollected = false;
	protected Quest Quest = this;
	protected List<String> ShopkeeperMessage;
	
	public Quest(int TypeID, String Name, String Description, int PropertyID, int GoalAmount, int CoinReward, int GemReward, int ExperienceReward, long ExpireLong)
	{
		this.TypeID = TypeID;
		this.Name = Name;
		this.Description = Description;
		this.PropertyID = PropertyID;
		this.GoalAmount = GoalAmount;
		this.CoinReward = CoinReward;
		this.GemReward = GemReward;
		this.ExperienceReward = ExperienceReward;
		this.ExpireLong = ExpireLong;
		this.ShopkeeperMessage = Arrays.asList(
				Shopkeeper.prefix + "Hello! I have a quest for you..",
				Shopkeeper.prefix + "Would you like to " + Description + "?",
				Shopkeeper.prefix + "Click me for more information!"
				);
	}
	
	public int getID() 
	{
		return this.ID;
	}
	
	public int getTypeID()
	{
		return this.TypeID;
	}
	
	public String getName()
	{
		return this.Name;
	}
	
	public String getDescription()
	{
		return this.Description;
	}
	
	public int getGoalAmount()
	{
		return this.GoalAmount;
	}
	
	public int getProgressAmount()
	{
		return this.ProgressAmount;
	}
	
	public int getDeliveredAmount()
	{
		return this.DeliveredAmount;
	}
	
	public User getUser()
	{
		return this.User;
	}
	
	public int getPropertyID()
	{
		return this.PropertyID;
	}
	
	public int getCoinReward()
	{
		return this.CoinReward;
	}
	
	public int getGemReward()
	{
		return this.GemReward;
	}
	
	public int getExperienceReward()
	{
		return this.ExperienceReward;
	}
	
	public long getExpireTime()
	{
		return this.ExpireLong;
	}
	
	public boolean isAssigned()
	{
		return this.isAssigned;
	}
	
	public boolean isCompleted()
	{
		return this.isCompleted;
	}
	
	public boolean isCollected()
	{
		return this.isCollected;
	}
	
	public List<String> getShopkeeperMessage()
	{
		return this.ShopkeeperMessage;
	}
	
	protected void updateShopkeeperMessage()
	{
		this.ShopkeeperMessage = Arrays.asList(
				Shopkeeper.prefix + "Hello! I have a quest for you..",
				Shopkeeper.prefix + "Would you like to " + Description + "?",
				Shopkeeper.prefix + "Click me for more information!"
				);
	}
	
	protected void activate()
	{
		Properties.Properties.addActiveQuest(this.PropertyID, this);
        Bukkit.getServer().getPluginManager().callEvent(new QuestStateChangeEvent(this.PropertyID, this));
		main.PropertyQuestLong.put(this.PropertyID, (System.currentTimeMillis() + (1800*1000)));
		this.updateShopkeeperMessage();
	}
	
	public void tryAsign(User user)
	{
		this.ShopkeeperMessage = Arrays.asList(
				Shopkeeper.prefix + "Hello! I have a quest for you..",
				Shopkeeper.prefix + "Would you like to " + Description + "?",
				Shopkeeper.prefix + "Click me for more information!"
				);
	}
	
	public void asign(User user, int index)
	{
		this.User = user;
		this.User.addQuest(this, index);
		this.isAssigned = true;
		this.User.getPlayer().sendMessage(ColorOptions.messagesubjects + "You accepted a quest, go to your assignments to view the progress");
        Bukkit.getServer().getPluginManager().callEvent(new QuestStateChangeEvent(this.PropertyID, this));
	}
	
	public void goalReached()
	{
		Player player = this.User.getPlayer();
		this.isCollected = true;
		player.playSound(player.getLocation(), SoundHandler.LEVEL_UP, 0.1F, 0.1F);
		player.sendMessage(ColorOptions.messageachievement + "You have completed a Quest!");
		player.sendMessage(ColorOptions.message + "Go to " + this.property.getPropertyName(this.PropertyID) + " to claim your reward!");
		player.sendMessage(ColorOptions.message + "(Check your assignments-menu for the location)");
	}
	
	public void complete()
	{
		this.isCompleted = true;
		Player player = this.User.getPlayer();
		this.User.addCoins(CoinReward);
		this.User.addGems(GemReward);
		this.User.addExperience(this.ExperienceReward, true);
		player.sendMessage(ColorOptions.messageachievement + "You received " + ColorOptions.coinStats + ColorOptions.formatCurrency(this.CoinReward) + " Coins, " + ColorOptions.coinStats + ColorOptions.formatCurrency(this.GemReward) + " Gems" + ColorOptions.messageachievement + " and " + ColorOptions.coinStats + ColorOptions.formatCurrency(this.ExperienceReward) + " Experience");
		Properties.Properties.removeActiveQuest(this.PropertyID, this);
	}
}
