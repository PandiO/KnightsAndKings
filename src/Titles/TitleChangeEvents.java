package Titles;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import Handlers.TitleChangeEvent;
import Main.Main;
import Skills.Skill;
import Users.User;
import Users.Users;

public class TitleChangeEvents implements Listener
{
	Title title = new Title();
	Skill skill = new Skill();
	private Main main;
	public TitleChangeEvents(Main main) 
	{
		this.main = main;
	}
		
	@EventHandler
	public void onChange(TitleChangeEvent e)
	{
		User user = e.getUser();
		UUID uuid = user.getUUID();
		Integer oldTitleID = e.getOldTitle();
		Integer newTitleID = e.getNewTitle();
		Integer changeAmount;
		boolean promotion = e.isPromoting();
		
		
		if (promotion)
		{
			changeAmount = newTitleID-oldTitleID;
			if (changeAmount == 1)
			{
				userPromotion(user, newTitleID);
			} else if (changeAmount >= 2)
			{
				Main.titleChangeList.put(uuid, changeAmount);
				setPromoteLoop(user, oldTitleID);
			}
		} else
		{
			changeAmount = oldTitleID-newTitleID;
			if (changeAmount == 1)
			{
				userDemotion(user, newTitleID);
			} else if (changeAmount >= 2)
			{
				Main.titleChangeList.put(uuid, changeAmount);
				setDemoteLoop(user, oldTitleID);
			}
		}
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
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		if (!Main.titleChangeList.containsKey(uuid))
//		{
//			Integer currentTitleID = user.getTitleID();
//			Integer newTitleID = title.getTitleIDbyExp(user.getExperience());
//			if (currentTitleID != newTitleID)
//			{
//				Integer change = newTitleID-currentTitleID;
//				if (change > 0)
//				{
//					if (change >= 2)
//					{
//						Main.titleChangeList.put(uuid, change);
//						setPromoteLoop(user, currentTitleID);
//					} else
//					{
//						userPromotion(user, newTitleID);
//					}
//				} else
//				{
//					if (change == -1)
//					{
//						userDemotion(user, newTitleID);
//					} else if (change >= -2)
//					{
//						Main.titleChangeList.put(uuid, currentTitleID-newTitleID);
//						setDemoteLoop(user, currentTitleID);
//					}
//				}
//			}
//		}
//	}
	
	
	//The method to perform when promoting a user
	public void userPromotion(User user, Integer newTitleID)
	{
		Integer currentTitleID = user.getTitleID();
		Integer genderID = user.getGenderID();
		
		//Retrieve all bonusses the user receives
		Integer coinBonus = user.getMultipliedInt(title.getCoinBonus(newTitleID));
		Integer gemBonus = user.getMultipliedInt(title.getGemBonus(newTitleID));
		Integer expBonus = user.getMultipliedInt(title.getExpBonus(newTitleID));
		
		//Calculate how much skill-points a user receives
		Integer skillPointAmount = newTitleID - currentTitleID;
		
		if (user.isOfflineUser() == false)
		{
			Player player = user.getPlayer();
			//Send the promotion message
			for (String message : title.PromotionMessage(genderID, user.getUUID(), newTitleID))
			{
				player.sendMessage(message);
			}
			//Play sounds and effects when promoting
			player.playSound(player.getLocation(), SoundHandler.LEVEL_UP, 1.0F, 1.0F);
			Bukkit.getServer().getWorld(player.getWorld().getName()).playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
			
			//Gives the user special unlocks
			player.sendMessage(ColorOptions.unlockformat + ColorOptions.rawdevidebrackets);
			player.sendMessage(ColorOptions.unlockformat + "Unlocked a " + ColorOptions.unlocksubjects + "SkillPoint");
			if (newTitleID >= 5 && currentTitleID < 5)
			{
				user.addHouseAmount(true, 1);
				user.addPropertyAmount(true, 1);
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for a " + ColorOptions.unlocksubjects + "House");
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for a " + ColorOptions.unlocksubjects + "Property");
			}
			if (newTitleID >= 10 && currentTitleID < 10)
			{
				user.addHouseAmount(true, 1);
				user.addPropertyAmount(true, 1);
				user.addSkillPoints(true, 1);
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for a " + ColorOptions.unlocksubjects + "House");
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for a " + ColorOptions.unlocksubjects + "Property");
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a " + ColorOptions.unlocksubjects + "SpecialSkill-point");
			}
			if (newTitleID >= 12 && currentTitleID < 12)
			{
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for a " + ColorOptions.unlocksubjects + "Quest");
			}
			if (newTitleID >= 15 && currentTitleID < 15)
			{
				user.addHouseAmount(true, 1);
				user.addPropertyAmount(true, 1);
				user.addKeepAmount(true, 1);
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for a " + ColorOptions.unlocksubjects + "House");
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for a " + ColorOptions.unlocksubjects + "Property");
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for a " + ColorOptions.unlocksubjects + "Keep");
				player.sendMessage(ColorOptions.unlockformat + "Unlocked a slot for an " + ColorOptions.unlocksubjects + "Assignment");
			}
			player.sendMessage(ColorOptions.unlockformat + ColorOptions.rawdevidebrackets);
			
			//Sets the experience bar of the user to the corresponding title
			title.setExperienceBar(player, user.getExperience(), newTitleID);
		}
		user.addSkillPoints(false, skillPointAmount);
		user.addCoins(coinBonus);
		user.addGems(gemBonus);
		user.addExperience(expBonus, false);
		user.setTitle(newTitleID);
		user.refreshQuestMaximum();
		user.refreshAssignmentMaximum();
	}
	
	//The method to perform when demoting a user
	public void userDemotion(User user, Integer newTitleID)
	{
		Integer currentTitleID = user.getTitleID();
		Integer genderID = user.getGenderID();
		Integer currentSpecialSkillID = user.getSpecialSkillID();
		Random rand = new Random();
		
		if (user.isOfflineUser() == false)
		{
			Player player = user.getPlayer();
			//Gives the user special unlocks
			player.sendMessage(ColorOptions.demotionformat + ColorOptions.rawdevidebrackets);
			player.sendMessage(ColorOptions.demotionformat + "Lost a " + ColorOptions.demotionsubjects + "SkillPoint");
			if (currentTitleID >= 5 && newTitleID < 5)
			{
				user.removeHouseAmount(true, 1);
				user.removePropertyAmount(true, 1);
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for a " + ColorOptions.demotionsubjects + "House");
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for a " + ColorOptions.demotionsubjects + "Property");
				//Functie in user maken die geforced het houseamount checkt
			}
			if (currentTitleID >= 10 && newTitleID < 10)
			{
				user.removeHouseAmount(true, 1);
				user.removePropertyAmount(true, 1);
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for a " + ColorOptions.demotionsubjects + "House");
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for a " + ColorOptions.demotionsubjects + "Property");
				if (currentSpecialSkillID != 0)
				{
					player.sendMessage(ColorOptions.demotionformat + "Lost your " + ColorOptions.demotionsubjects + "Special Skill: " + user.getSpecialSkillName());
					user.setSpecialSkillName("none");
				} else
				{
					user.removeSkillPoints(false, 1);
					player.sendMessage(ColorOptions.demotionformat + "Lost your " + ColorOptions.demotionsubjects + "Special Skillpoint");
				}
			}
			if (currentTitleID >= 12 && newTitleID < 12)
			{
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for a " + ColorOptions.demotionsubjects + "Quest");
			}
			if (currentTitleID >= 15 && newTitleID < 15)
			{
				user.removeHouseAmount(true, 1);
				user.removePropertyAmount(true, 1);
				user.removeKeepAmount(true, 1);
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for a " + ColorOptions.demotionsubjects + "House");
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for a " + ColorOptions.demotionsubjects + "Property");
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for a " + ColorOptions.demotionsubjects + "Keep");
				player.sendMessage(ColorOptions.demotionformat + "Lost a slot for an " + ColorOptions.demotionsubjects + "Assignment");
			}
			player.sendMessage(ColorOptions.demotionformat + ColorOptions.rawdevidebrackets);
		}
		
		//Remove skillpoints or levels from skills according to the amount of titles 
		if (user.getSkillPoints(false) > 0)
		{
			user.removeSkillPoints(false, 1);
		} else
		{
			HashMap<String, Integer> list = user.getSkillLevelList();
			
			String skill = this.skill.getSkillList().get(rand.nextInt(this.skill.getSkillList().size()));
			Integer level = list.get(skill);
			if (level > 0)
			{
				user.removeSkillID(skill, 1);
			} else
			{
				List<String> sl = this.skill.getSkillList();
				Collections.shuffle(sl);
				for (String sk : sl)
				{
					if (list.get(sk) > 0)
					{
						user.removeSkillID(sk, 1);
					}
				}
			}
		}
		
		if (user.isOfflineUser() == false)
		{
			Player player = user.getPlayer();
			//Send the demotion message
			for (String message : title.DemotionMessage(genderID, user.getUUID(), newTitleID))
			{
				player.sendMessage(message);
			}
			
			//Sets the experience bar of the user to the corresponding title
			title.setExperienceBar(player, user.getExperience(), newTitleID);
		}
		
		user.setTitle(newTitleID);
		user.checkHouseMaximum();
		user.checkPropertyMaximum();
		user.refreshAssignmentMaximum();
		user.refreshQuestMaximum();
	}
	
	private void setPromoteLoop(User user, Integer currentTitleID)
	{
		UUID uuid = user.getUUID();
		new BukkitRunnable()
		{
			public void run()
			{
				if (Main.titleChangeList.containsKey(uuid))
				{
					if (Main.titleChangeList.get(uuid) > 0)
					{
		        		Main.titleChangeList.put(uuid, Main.titleChangeList.get(uuid)-1);
		        		userPromotion(user, user.getTitleID()+1);
					} else
					{
						this.cancel();
						Main.titleChangeList.remove(uuid);
					}
				}
			}
		}.runTaskTimer(main, 0, 2*20);
	}
	
	private void setDemoteLoop(User user, Integer currentTitleID)
	{
		UUID uuid = user.getUUID();
		new BukkitRunnable()
		{
			public void run()
			{
				if (Main.titleChangeList.get(uuid) > 0)
				{
					Integer titleID = user.getTitleID();
					if (titleID-1 < 0)
					{
						this.cancel();
					} else
					{
		        		userDemotion(user, titleID-1);
		        		Main.titleChangeList.put(uuid, Main.titleChangeList.get(uuid)-1);
					}
				} else
				{
					this.cancel();
					Main.titleChangeList.remove(uuid);
				}
			}
		}.runTaskTimer(main, 0, 2*20);
	}
}
