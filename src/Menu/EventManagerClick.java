package Menu;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import Gates.Gate;
import Handlers.ColorOptions;
import HideAndSeek.HideAndSeek;
import Main.Main;
import Minigames.Participant;
import Products.Product;
import Sieges.Scenarios;
import Sieges.SideObjective;
import Sieges.SiegeObject;
import Sieges.Scenario;
import Sieges.SiegeSpawnpoint;
import Towns.Town;
import Users.User;

public class EventManagerClick 
{
	Main main = Main.getPlugin(Main.class);
	Menu menu = new Menu();
	Product product = new Product();
	Town town = new Town();
	
	public void onEventManagerClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.OpenPersonalMenu(user);
			user.playSound("back");
		}
		if (dc.equalsIgnoreCase("hide and seek"))
		{
			if (main.HideAndSeek == null)
			{
				user.getPlayer().sendMessage(ColorOptions.error + "The Hide and Seek didn't start yet!");
			}
			this.menu.openHideAndSeekManager(user);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("siege"))
		{
			this.menu.openSiegeManager(user);
			user.playSound("succesclick");
		}
	}
	
	public void onHsManagerClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		Inventory menu = e.getInventory();
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		HideAndSeek hs = main.HideAndSeek;
		
		Integer step = 250;
		
		List<String> st = menu.getItem(10).getItemMeta().getLore();
		step = Integer.valueOf(st.get(0).split(": ")[1]);

		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openEventManager(user);
			user.playSound("back");
		}
		if (dc.contains("auto-start: "))
		{
			hs.setAutostart(hs.getAutostart() ? false : true);
			menu.setItem(16, product.createItem(ColorOptions.message + "Auto-start: " + (hs.getAutostart() ? ColorOptions.messagesubjects + "On" : ColorOptions.error + "Off"), new ItemStack(Material.BEACON), hs.getAutostart(), ColorOptions.message + "Click to toggle"));
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("increase reward"))
		{
			hs.setReward(hs.getReward() + step);
			menu.setItem(10, product.createItem(ColorOptions.message + "Reward: " + ChatColor.YELLOW + hs.getReward(), new ItemStack(Material.GOLD_INGOT, 1), false, ColorOptions.message + "Steps: " + step));
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("decrease reward"))
		{
			hs.setReward(hs.getReward() - step);
			menu.setItem(10, product.createItem(ColorOptions.message + "Reward: " + ChatColor.YELLOW + hs.getReward(), new ItemStack(Material.GOLD_INGOT, 1), false, ColorOptions.message + "Steps: " + step));
			user.playSound("succesclick");
		}
		if (dc.contains("reward: "))
		{
			step = nextStep(step);
			menu.setItem(10, product.createItem(ColorOptions.message + "Reward: " + ChatColor.YELLOW + hs.getReward(), new ItemStack(Material.GOLD_INGOT, 1), false, ColorOptions.message + "Steps: " + step));
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("send to hub"))
		{
			for (Participant participant : hs.getParticipants())
			{
				hs.sendToHub(participant);
			}
			user.playSound("succesclick");
		}
		if (dc.contains("location: "))
		{
			if (!hs.getProgress())
			{
				List<Integer> IDList = town.getTownIDList();
				IDList.remove(town.getTownID("wilderness"));
				
				if (hs.getTownID() == null)
				{
					hs.setTown(IDList.get(0));
				}else
				{
					Integer newIndex = IDList.indexOf(hs.getTownID())+1;

					if (IDList.size() <= newIndex)
					{
						newIndex = 0;
					}
					hs.setTown(IDList.get(newIndex));
				}
				menu.setItem(15, product.createItem(ColorOptions.message + "Location: " + (hs.getTownID() != null ? ColorOptions.messagesubjects + hs.getTownName() : ColorOptions.error + "Not set"), new ItemStack(Material.COMPASS), false, ColorOptions.message + "Click to change location", ColorOptions.message + "Cycles through the town list"));
				user.playSound("succesclick");
			}
		}
		if (dc.equalsIgnoreCase("next stage"))
		{
			if (hs.getCooldown())
			{
				hs.skipStage(user);
			} else
			if (hs.getMatchmaking())
			{
				if (hs.getMatchmakingSeconds() > 16)
				{
					hs.setMatchmakingSeconds(16);
				} else
				{
					user.getPlayer().sendMessage(ColorOptions.error + "Already skipping Matchmaking");
				}
			} else
			if (hs.getProgress())
			{
				if (hs.getProgressSeconds() > 2)
				{
					hs.setProgressSeconds(2);
				} else
				{
					user.getPlayer().sendMessage(ColorOptions.error + "Already skipping Game");
				}
			}
			user.playSound("succesclick");
		}
		if (dc.contains("player: "))
		{
			String username = dc.split(": ")[1];
			
			UUID uuid = Users.Users.fetchUUIDbyUsername(username);
			User target = Users.Users.getUser(uuid);
			
			if (target != null)
			{
				this.menu.openHsPlayerManager(user, target);
				user.playSound("succesclick");
			} else
			{
				user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving User information");
			}
		}
		
	}
	
	public void onHsPlayerManagerClick(InventoryClickEvent e, User user)
	{
		HideAndSeek hs = main.HideAndSeek;

		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		
		ItemStack targetInformation = e.getInventory().getItem(4);
		User target = null;
		Participant participant = null;
		String username = ChatColor.stripColor(targetInformation.getItemMeta().getDisplayName()).split(": ")[1];
		UUID uuid = Users.Users.fetchUUIDbyUsername(username);
		target = Users.Users.getUser(uuid);
		
		if (target == null)
		{
			this.menu.openHideAndSeekManager(user);
			user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving target's information. Please notify a developer");
			return;
		}
		participant = hs.getParticipant(target);
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openHideAndSeekManager(user);
			user.playSound("back");
		}
		
		if (dc.equalsIgnoreCase("switch role"))
		{
			if (hs.getProgress())
			{
				if (hs.getHiders().contains(participant))
				{
					hs.setSeeker(participant);
				} else if (hs.getSeekers().contains(participant))
				{
					hs.removeSeeker(participant);
				}
				user.playSound("succesclick");
				this.menu.openHideAndSeekManager(user);
			} else
			{
				user.getPlayer().sendMessage(ColorOptions.error + "You can only change this while game is in progress");
			}
		}
		if (dc.equalsIgnoreCase("kick"))
		{
			hs.removeParticipant(target);
			user.playSound("succesclick");
			this.menu.openHideAndSeekManager(user);
		}
		if (dc.equalsIgnoreCase("spectate"))
		{
			user.getPlayer().teleport(target.getPlayer().getLocation());
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
		}
		if (dc.equalsIgnoreCase("send to hub"))
		{
			hs.sendToHub(hs.getParticipant(target));
			user.playSound("succesclick");
			this.menu.openHideAndSeekManager(user);
		}
		if (dc.equalsIgnoreCase("hint seekers"))
		{
			user.getPlayer().sendMessage(ColorOptions.error + "This function is coming soon");
			this.menu.openHideAndSeekManager(user);
		}
	}
	
	public void onSiegeManagerClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openEventManager(user);
			user.playSound("back");
		}
		if (dc.contains("scenario: "))
		{
			Integer scenarioID = Integer.valueOf(dc.split(": ")[1]);
			Scenario scenario = Scenarios.findScenario(scenarioID);
			
			if (scenario == null)
			{
				user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving scenario information");
				return;
			}
			
			this.menu.openScenarioManager(user, scenario);
			user.playSound("succesclick");
		}
	}
	
	public void onScenarioManagerClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		ItemStack scenarioItem = e.getInventory().getItem(4);
		List<String> scenarioLore = scenarioItem.getItemMeta().getLore();
		Integer scenarioID = Integer.valueOf(ChatColor.stripColor(scenarioLore.get(0)).split(": ")[1]);
		Scenario scenario = Scenarios.findScenario(scenarioID);
		
		if (scenario == null)
		{
			user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving scenario!");
			return;
		}
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openSiegeManager(user);
			user.playSound("back");
			return;
		}
		if (dc.equalsIgnoreCase("save changes"))
		{
			scenario.save();
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("delete scenario"))
		{
			scenario.removePermanently(user.getPlayer());
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
		}
		if (dc.equalsIgnoreCase("main objective"))
		{
			if (scenario.getMainObjective() == null)
			{
				user.getPlayer().sendMessage(ColorOptions.error + "No Main Objective could be found! Please notify a developer");
				return;
			}
			scenario.teleportObjective(user, scenario.getMainObjective());
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
		}
		if (dc.equalsIgnoreCase("spawnpoints team 1"))
		{
			if (scenario.getTeam1Spawnpoints().isEmpty())
			{
				user.getPlayer().sendMessage(ColorOptions.error + "No Spawnpoints for team 1 could be found! Please notify a developer");
				return;
			}
			user.getPlayer().closeInventory();
			this.menu.openSiegeSpawnpointsManager(user, scenario, 1);
			user.playSound("succesclick");
		}
		if (dc.equalsIgnoreCase("spawnpoints team 2"))
		{
			if (scenario.getTeam2Spawnpoints().isEmpty())
			{
				user.getPlayer().sendMessage(ColorOptions.error + "No Spawnpoints for team 2 could be found! Please notify a developer");
				return;
			}
			user.getPlayer().closeInventory();
			this.menu.openSiegeSpawnpointsManager(user, scenario, 2);
			user.playSound("succesclick");
		}
		if (dc.contains("side objective"))
		{
			Integer index = Integer.valueOf(dc.split(" ")[2]);
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
			this.menu.openSideObjectiveManager(user, scenario.getSideObjectives().get(index-1), scenario);
		}
		if (dc.equalsIgnoreCase("change location"))
		{
			SiegeObject object = null;
			if (e.getSlot() == 13)
			{
				object = scenario.getMainObjective();
				user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Right-click a standing banner to change to location of the Main Objective");
			}
			scenario.setEditMode(user, object);
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Type 'stop' to stop editing");
		}
		if (dc.equalsIgnoreCase("test main objective"))
		{
			scenario.getMainObjective().addTestingList(user);
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Test is succesful if you see a circle of flames around the banner");
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "and if it slowly changes color when you stand inside the circle");
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "NOTE: Testing stop automatically after 20 seconds!");
			new BukkitRunnable()
			{
				public void run()
				{
					scenario.getMainObjective().removeTestingList(user);
					user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Testing of Side objective stopped..");
				}
			}.runTaskLaterAsynchronously(main, 20*20);
		}
		if (dc.equalsIgnoreCase("test scenario"))
		{
			scenario.addTestingList(user);
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Test is succesful if you see circles of flames around the objectives");
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "and if it slowly changes color when you stand inside the circle");
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "NOTE: Testing stop automatically after 1 minute!");
			new BukkitRunnable()
			{
				public void run()
				{
					scenario.removeTestingList(user);
					user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Testing of Scenario stopped..");
				}
			}.runTaskLaterAsynchronously(main, 60*20);
		}
	}
	
	public void onSideObjectiveManagerClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		ItemStack scenarioItem = e.getInventory().getItem(4);
		List<String> scenarioLore = scenarioItem.getItemMeta().getLore();
		Integer scenarioID = Integer.valueOf(ChatColor.stripColor(scenarioLore.get(0)).split(": ")[1]);
		Integer objectiveIndex = Integer.valueOf(ChatColor.stripColor(scenarioItem.getItemMeta().getDisplayName()).split(" ")[2]);
		Scenario scenario = Scenarios.findScenario(scenarioID);
		
		if (scenario == null)
		{
			user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving scenario!");
			return;
		}
		
		SideObjective objective = scenario.getSideObjectives().get(objectiveIndex-1);
		
		if (objective == null)
		{
			user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving Side Objective!");
			return;
		}
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openScenarioManager(user, scenario);
			user.playSound("back");
			return;
		}
		if (dc.equalsIgnoreCase("side objective"))
		{
			Bukkit.getConsoleSender().sendMessage("SiegeID: " + objective.getScenarioID());
			scenario.teleportObjective(user, objective);
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
		}
		if (dc.equalsIgnoreCase("delete side objective"))
		{
			scenario.removeSideObjective(user.getPlayer(), objective);
			user.playSound("succesclick");
			this.menu.openScenarioManager(user, scenario);
		}
		if (dc.equalsIgnoreCase("change location"))
		{
			scenario.setEditMode(user, objective);
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Right-click a standing banner to change to location of this side Objective");
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Type 'stop' to stop editing");
		}
		if (dc.equalsIgnoreCase("test side objective"))
		{
			objective.addTestingList(user);
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Test is succesful if you see a circle of flames around the banner");
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "and if it slowly changes color when you stand inside the circle");
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "NOTE: Testing stop automatically after 20 seconds!");
			new BukkitRunnable()
			{
				public void run()
				{
					objective.removeTestingList(user);
					user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Testing of Side objective stopped..");
				}
			}.runTaskLaterAsynchronously(main, 20*20);
		}
		if (dc.equalsIgnoreCase("gate"))
		{
			this.menu.openSideObjectiveGate(user, objective, scenario);
			user.playSound("succesclick");
		}
	}
	
	public void onSiegeSpawnpointManagerClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		ItemStack scenarioItem = e.getInventory().getItem(4);
		List<String> scenarioLore = scenarioItem.getItemMeta().getLore();
		Integer scenarioID = Integer.valueOf(ChatColor.stripColor(scenarioLore.get(0)).split(": ")[1]);
		Integer team  = Integer.valueOf(ChatColor.stripColor(scenarioItem.getItemMeta().getDisplayName()).split(" ")[2]);
		Scenario scenario = Scenarios.findScenario(scenarioID);
		
		if (scenario == null)
		{
			user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving scenario!");
			return;
		}
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openScenarioManager(user, scenario);
			user.playSound("back");
			return;
		}
		if (dc.contains("spawnpoint: "))
		{
			Integer spawnCountID = Integer.valueOf(dc.split(": ")[1]);
			Bukkit.getConsoleSender().sendMessage("spawnCountID: " + spawnCountID);
			scenario.teleportSpawnpoint(user, scenario.getSiegeSpawnpoint(team, spawnCountID));
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
		}
		if (dc.equalsIgnoreCase("delete spawnpoint"))
		{
			ItemStack item = e.getInventory().getItem(e.getSlot()-9);
			Integer spawnCountID = Integer.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(": ")[1]);
			Bukkit.getConsoleSender().sendMessage("spawnCountID: " + spawnCountID);
			SiegeSpawnpoint spawnpoint = scenario.getSiegeSpawnpoint(team, spawnCountID);
			scenario.removeSpawnpoint(user.getPlayer(), spawnpoint);
			user.playSound("succesclick");
			this.menu.openSiegeSpawnpointsManager(user, scenario, team);
		}
		if (dc.equalsIgnoreCase("change location"))
		{
			ItemStack item = e.getInventory().getItem(e.getSlot()-9);
			Integer spawnCountID = Integer.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(": ")[1]);
			Bukkit.getConsoleSender().sendMessage("spawnCountID: " + spawnCountID);
			SiegeSpawnpoint spawnpoint = scenario.getSiegeSpawnpoint(team, spawnCountID);
			scenario.setEditMode(user, spawnpoint);
			user.playSound("succesclick");
			user.getPlayer().closeInventory();
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Stand on the new location and type 'save' to change the location");
			user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Type 'stop' to stop editing");
		}
	}
	
	public void onSOGateManagerClick(InventoryClickEvent e, User user)
	{
		e.setCancelled(true);
		
		ItemStack clicked = e.getCurrentItem();
		String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
		ItemStack scenarioItem = e.getInventory().getItem(4);
		List<String> scenarioLore = scenarioItem.getItemMeta().getLore();
		Integer scenarioID = Integer.valueOf(ChatColor.stripColor(scenarioLore.get(1)).split(": ")[1]);
		Integer subID = Integer.valueOf(ChatColor.stripColor(scenarioLore.get(0)).split(": ")[1]);
		Scenario scenario = Scenarios.findScenario(scenarioID);
		
		if (scenario == null)
		{
			user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving scenario!");
			return;
		}

		SideObjective objective = scenario.getSideObjective(subID);
		
		if (objective == null)
		{
			user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving Side Objective!");
			return;
		}
		
		if (dc.equalsIgnoreCase("back"))
		{
			this.menu.openSideObjectiveManager(user, objective, scenario);
			user.playSound("back");
			return;
		}
		if (dc.equalsIgnoreCase("remove gate"))
		{
			if (objective.getGateID() == -1)
			{
				user.getPlayer().sendMessage(ColorOptions.error + "Can't remove gate because this objective has no gate set");
				user.playSound("failclick");
				return;
			}
			objective.removeGate();
			user.playSound("succesclick");
			this.menu.openSideObjectiveManager(user, objective, scenario);
		}
		if (dc.contains("gate: "))
		{
			Integer gateID = Integer.valueOf(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getLore().get(0)).split(": ")[1]);
			Gate gate = Gates.Gates.findGate(gateID);
			if (gate == null)
			{
				user.getPlayer().sendMessage(ColorOptions.error + "Error while retrieving Gate. Please notify a developer");
				user.playSound("failclick");
				return;
			}
			if (objective.getGateID() == gateID)
			{
				user.getPlayer().sendMessage(ColorOptions.error + "Can't select same gate!");
				user.playSound("failclick");
				return;
			}
			
			objective.setGate(gate);
			user.playSound("succesclick");
			this.menu.openSideObjectiveManager(user, objective, scenario);
		}
		
	}
	
	private Integer nextStep(Integer currentStep)
	{
		Integer step = 250;
		
		if (currentStep == 250)
		{
			step = 500;
		} else if (currentStep == 500)
		{
			step = 1000;
		} else if (currentStep == 1000)
		{
			step = 10000;
		} else if (currentStep == 10000)
		{
			step = 250;
		}
		
		return step;
	}
}
