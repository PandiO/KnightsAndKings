package Tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Main.Main;
import Menu.Menu;
import SpawnPoints.SpawnPoint;
import Users.User;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class Tutorial 
{
	Menu menu = new Menu();
	public TutorialFile file = new TutorialFile();
	public Player target = null;
	public User user = null;
	public NPC npc = null;
	public Integer stage = 0;
	public String npcPrefix = ColorOptions.message + "[" + ColorOptions.stats + "Tutorial" + ColorOptions.message + "]: ";
	protected CommandSender sender = (CommandSender) Bukkit.getConsoleSender();
	protected Main main = Main.getPlugin(Main.class);
	protected SpawnPoint spawnpoint = new SpawnPoint();
	protected NPCRegistry registry = CitizensAPI.getNPCRegistry();
	protected Location targetLoc = null;
	protected Location npcLoc = null;
	protected Location npcDestination = null;
	protected String tutorialName = null;
	protected Location previousLocation = null;
	protected Integer talkInterval = 10;
	protected Integer subjectInterval = 20;
	protected BukkitTask TutorialTask = null;
	protected List<String> rewardMessage = new ArrayList<String>(Arrays.asList(
			ColorOptions.messageachievement + "Congratulations! You've completed this tutorial for the first time!",
			ColorOptions.messageachievement + "You received the following " + ColorOptions.messagesubjects + "Rewards:"
			));
	protected List<String> introContinueMessage = new ArrayList<String>(Arrays.asList(
			ColorOptions.message + "To continue to the next part, type " + ColorOptions.messagesubjects + "next"
			));
	private RoomTutorial roomTutorial = null;
	private ArmorTutorial armorTutorial = null;
	private FoodTutorial foodTutorial = null;
	private SkillTutorial skillTutorial = null;
	private IntroTutorial introTutorial = null;
	
	public void createTutorial(User user, String tutorialType)
	{
		if (tutorialType.toLowerCase().contains("tutorial"))
		{
			tutorialType = tutorialType.split(" ")[0];
		}
		if (isValid(tutorialType))
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Validated");
			}
			this.previousLocation = user.getPlayer().getLocation();
			this.targetLoc = spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("new"));
			this.npcLoc = spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("tutorial"));
			this.user = user;
			this.target = user.getPlayer();
			this.tutorialName = (tutorialType.substring(0, 1).toUpperCase() + tutorialType.substring(1));
			if (tutorialName.equalsIgnoreCase("intro"))
			{
				TutorialEvents.introTutorials.add(this);
			} else
			{
				TutorialEvents.tutorials.add(this);
			}
			user.getPlayer().teleport(targetLoc);
			if (getIntroTutorial(user) != null)
			{
				if (getIntroTutorial(user) == this)
				{
					this.npc = createNPC();
				} else
				{
					this.npc = getIntroTutorial(user).npc;
				}
			} else
			{
				this.npc = createNPC();
			}
			confirmTutorial();
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Player: " + user.getUsername() + ", Tutorial: " + tutorialType);
			}
		} else if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Not valid tutorial");
		}
	}
	
	public String getName()
	{
		return this.tutorialName + " Tutorial";
	}
	
	public void cancel(List<String> message)
	{
		if (this.TutorialTask != null)
		{
			this.TutorialTask.cancel();
		}
		removeList();
		target.teleport(previousLocation);
		if (message != null)
		{
			message(message);
		} else
		{
			target.sendMessage(ColorOptions.messageachievement + "You ended the tutorial " + ColorOptions.messagesubjects + getName());
		}
		if (this.armorTutorial != null)
		{
			if (this.armorTutorial.used < 4)
			{
				Integer leftover = (4-this.armorTutorial.used);
				ItemStack item = null;
				for (ItemStack content : target.getInventory().getContents())
				{
					if (content != null && content.getType() != org.bukkit.Material.AIR)
					{
						if (content.hasItemMeta())
						{
							if (ChatColor.stripColor(content.getItemMeta().getDisplayName()).equalsIgnoreCase("item coupon"))
							{
								item = content;
								break;								
							}
						}
					}
				}
				if (item != null)
				{
					target.getInventory().remove(item);
					item.setAmount(item.getAmount()-leftover);
					target.getInventory().addItem(item);
					target.updateInventory();
				}
			}
		}
		if (getIntroTutorial(this.user) != null)
		{
			if (getIntroTutorial(this.user).npc != this.npc)
			{
				this.npc.destroy();
			}
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Intro tutorial found when cancelling");
			}
			if (getIntroTutorial(this.user) == this)
			{
				if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("Intro tutorial class match detected");
				}
				removeIntroList();
				this.npc.destroy();
			} else
			if (getIntroTutorial(this.user).stage != null && getIntroTutorial(this.user).stage > 0)
			{
				if (getIntroTutorial(this.user).stage == 9)
				{
					if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Intro tutorial stage 8 match detected");
					}
					removeIntroList();
					this.npc.destroy();
				} else
				{
					target.sendMessage("");
					target.sendMessage(this.npcPrefix + ColorOptions.message + this.introContinueMessage.get(0));
				}
			} else
			{
				target.sendMessage(this.npcPrefix + ColorOptions.message + "You can always find the tutorials by typing " + ColorOptions.messagesubjects + "/menu" + ColorOptions.message + " and go to " + ColorOptions.messagesubjects + "Support > Tutorials");
			}
		} else
		{
			this.npc.destroy();
		}
		if (main.debug)
		{
			for (Tutorial tut : TutorialEvents.tutorials)
			{
				Bukkit.getConsoleSender().sendMessage("Target: " + tut.user.getUsername());
			}
		}
	}
	
	public void confirmTutorial()
	{
		clearChat();
//		if (file.containsUUID(target.getUniqueId(), tutorialName))
//		{
//			target.sendMessage(this.npcPrefix + ColorOptions.error + "Hey I know you! You already completed this tutorial once");
//			target.sendMessage("");
//		}
//		target.sendMessage(this.npcPrefix + ColorOptions.message + ": Are you sure you wish to start the " + ColorOptions.messagesubjects + this.getName());
//		target.sendMessage(ColorOptions.message + "Please type " + ColorOptions.messagesubjects + "yes" + ColorOptions.message + " to start or " + ColorOptions.error + "cancel" + ColorOptions.message + " to cancel");
		this.menu.openTutorialStartMenu(this.user);
	}
	
	public NPC createNPC()
	{
		NPC npc = registry.createNPC(EntityType.PLAYER, "Tutorial");
		npc.addTrait(Traits.TutorialTrait.class);
		npc.setProtected(true);
		npc.spawn(npcLoc);
		return npc;
	}
	
	public boolean isValid(String type)
	{
		boolean valid = false;
		
		switch(type.toLowerCase())
		{
		case "room": valid = true;
		break;
		case "armor": valid = true;
		break;
		case "food": valid = true;
		break;
		case "skills": valid = true;
		break;
		case "intro": valid = true;
		break;
		}
		
		return valid;
	}
	
	public void clearChat()
	{
		for (int i = 0; i <20; i++)
		{
			if (i == 10)
			{
				target.sendMessage(ColorOptions.message + "If you wish to stop the tutorial, please type " + ColorOptions.error + "cancel");
				target.sendMessage(ColorOptions.message + "If you wish to continue the tutorial, please type " + ColorOptions.messagesubjects + "next");
			} else
			{
				target.sendMessage("");
			}
		}
	}
	
	public void message(List<String> message)
	{
		clearChat();
		for (String msg : message)
		{
			if (msg.length() > 1)
			{
				target.sendMessage(npcPrefix + msg);
			} else
			{
				target.sendMessage(msg);
			}
		}
	}
	
	public void TryNext(String argument)
	{
		String name = this.getName();
		if (argument.equalsIgnoreCase("cancel"))
		{
			this.cancel(null);
		} else
		{
			if (name.equalsIgnoreCase("room tutorial"))
			{
				if (argument.equalsIgnoreCase("yes"))
				{
					this.startTutorial();
				} else if (argument.equalsIgnoreCase("next") || argument.equalsIgnoreCase("skip"))
				{
					this.nextStage(null, null);
				} else
				{
					invalidArgs();
				}
			} else if (name.equalsIgnoreCase("armor tutorial"))
			{
				if (argument.equalsIgnoreCase("yes"))
				{
					this.startTutorial();
				} else if (argument.equalsIgnoreCase("next") || argument.equalsIgnoreCase("skip"))
				{
					this.nextStage(null, null);
				} else
				{
					invalidArgs();
				}
			} else if (name.equalsIgnoreCase("food tutorial"))
			{
				if (argument.equalsIgnoreCase("yes"))
				{
					this.startTutorial();
				} else if (argument.equalsIgnoreCase("next") || argument.equalsIgnoreCase("skip"))
				{
					this.nextStage(null, null);
				} else
				{
					invalidArgs();
				}
			} else if (name.equalsIgnoreCase("skills tutorial"))
			{
				if (argument.equalsIgnoreCase("yes"))
				{
					this.startTutorial();
				} else if (argument.equalsIgnoreCase("next") || argument.equalsIgnoreCase("skip"))
				{
					this.nextStage(null, null);
				} else
				{
					invalidArgs();
				}
			} else if (name.equalsIgnoreCase("intro tutorial"))
			{
				if (argument.equalsIgnoreCase("yes"))
				{
					this.startTutorial();
				} else if (argument.equalsIgnoreCase("next") || argument.equalsIgnoreCase("skip"))
				{
					this.nextStage(null, null);
				} else
				{
					invalidArgs();
				}
			}
		}
	}
	
	public void startTutorial()
	{
		if (this.getName().equalsIgnoreCase("room tutorial"))
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Starting room tutorial for " + this.user.getUsername());
			}
			this.roomTutorial = new RoomTutorial(this);
			this.roomTutorial.startTutorial();
		} else if (this.getName().equalsIgnoreCase("armor tutorial"))
		{
			this.armorTutorial = new ArmorTutorial(this);
			this.armorTutorial.startTutorial();
		} else if (this.getName().equalsIgnoreCase("food tutorial"))
		{
			this.foodTutorial = new FoodTutorial(this);
			this.foodTutorial.startTutorial();
		} else if (this.getName().equalsIgnoreCase("skills tutorial"))
		{
			this.skillTutorial = new SkillTutorial(this);
			this.skillTutorial.startTutorial();
		} else if (this.getName().equalsIgnoreCase("intro tutorial"))
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Starting intro tutorial for " + this.user.getUsername());
			}
			this.introTutorial = new IntroTutorial(this);
			this.introTutorial.startTutorial();
		}
	}
	
	public void nextStage(Integer productID, ItemStack item)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Stage: " + stage);
		}
		if (stage > 0)
		{
			if (this.getName().equalsIgnoreCase("room tutorial"))
			{
				this.roomTutorial.nextStage();
			} else if (this.getName().equalsIgnoreCase("armor tutorial"))
			{
				this.armorTutorial.nextStage(productID);
			} else if (this.getName().equalsIgnoreCase("food tutorial"))
			{
				this.foodTutorial.nextStage(productID, item);
			} else if (this.getName().equalsIgnoreCase("skills tutorial"))
			{
				this.skillTutorial.nextStage();
			} else if (this.getName().equalsIgnoreCase("intro tutorial"))
			{
				this.introTutorial.nextStage(null);
			}
		} else
		{
			this.menu.openTutorialStartMenu(this.user);
			target.sendMessage(ColorOptions.message + "If you wish to stop the tutorial, please type " + ColorOptions.error + "cancel");
			target.sendMessage(ColorOptions.message + "If you wish to continue the tutorial, please type " + ColorOptions.messagesubjects + "yes");
		}
	}
	
	public void previousStage()
	{
		if (this.getName().equalsIgnoreCase("room tutorial"))
		{
			this.roomTutorial.previousStage();;
		} else if (this.getName().equalsIgnoreCase("armor tutorial"))
		{
			this.armorTutorial.previousStage();
		} else if (this.getName().equalsIgnoreCase("skills tutorial"))
		{
			this.skillTutorial.previousStage();
		}
	}
	
	public void removeList()
	{
		Tutorial targetTutorial = null;
		for (Tutorial tut : TutorialEvents.tutorials)
		{
			if (tut.user == this.user)
			{
				targetTutorial = tut;
				break;
			}
		}
		TutorialEvents.tutorials.remove(targetTutorial);
	}
	
	public void removeIntroList()
	{
		List<Tutorial> targetTutorial = new ArrayList<Tutorial>();
		for (Tutorial tut : TutorialEvents.introTutorials)
		{
			if (tut.user == this.user)
			{
				targetTutorial.add(tut);
			}
		}
		if (!targetTutorial.isEmpty())
		{
			for (Tutorial tutorial : targetTutorial)
			{
				TutorialEvents.introTutorials.remove(tutorial);
			}
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Removed the Intro tutorial of " + this.user.getUsername() + " form the list!");
			if (!TutorialEvents.introTutorials.isEmpty())
			{
				Bukkit.getConsoleSender().sendMessage("Intro-list not empty: " + TutorialEvents.introTutorials.toString());
			}
		}
	}
	
	public Tutorial getIntroTutorial(User user)
	{
		Tutorial tutorial = null;
		
		for (Tutorial tut : TutorialEvents.introTutorials)
		{
			if (tut.user == user)
			{
				tutorial = tut;
				break;
			}
		}
		
		return tutorial;
	}
	
	public void invalidArgs()
	{
		if (stage > 0)
		{
			target.sendMessage(ColorOptions.message + "If you wish to stop the tutorial, please type " + ColorOptions.error + "cancel");
			target.sendMessage(ColorOptions.message + "If you wish to continue the tutorial, please type " + ColorOptions.messagesubjects + "next");
		} else
		{
			this.menu.openTutorialStartMenu(this.user);
			target.sendMessage(ColorOptions.message + "If you wish to stop the tutorial, please type " + ColorOptions.error + "cancel");
			target.sendMessage(ColorOptions.message + "If you wish to continue the tutorial, please type " + ColorOptions.messagesubjects + "yes");
		}
	}
}
