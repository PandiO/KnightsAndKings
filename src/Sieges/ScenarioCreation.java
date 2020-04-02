package Sieges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldguard.protection.managers.RegionManager;

import DataManager.Worldguard;
import Handlers.ColorOptions;
import Main.Main;
import Models.creations.Creation;
import Towns.Town;
import Users.User;

public class ScenarioCreation extends Creation
{
	Main main = Main.getPlugin(Main.class);
	Town town = new Town();
	
	protected String name;
	protected int townID;
	protected int playersMin;
	protected int playersMax;
	protected int expRewardWin;
	protected int coinRewardWin;
	protected int expRewardSideObjective;
	protected int coinRewardSideObjective;
	protected int expRewardCapture;
	protected int coinRewardCapture;
	private List<Location> spawnpointTeam1 = new ArrayList<Location>();
	private List<Location> spawnpointTeam2 = new ArrayList<Location>();
	protected Location mainObjective;
	private List<TempSideObjective> sideObjectives = new ArrayList<TempSideObjective>();
	
	protected List<String> startMessage = new ArrayList<String>(Arrays.asList(
			"",
			"",
			ColorOptions.message + "Welcome to the Siege creation menu",
			ColorOptions.message + "You can create a siege scenario by following a few steps",
			ColorOptions.message + "You can always configure the details later on",
			ColorOptions.message + "To stop the creation, type " + ColorOptions.error + "stop",
			ColorOptions.message + "To undo a step type " + ColorOptions.messageachievement + "undo",
			"",
			ColorOptions.messagesubjects + "Are you ready to start? Type " + ColorOptions.messageachievement + "start/next",
			"",
			""
			));
	
	public List<ArrayList<String>> messages = new ArrayList<ArrayList<String>>(Arrays.asList(
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messageformat + "Please type the name of the new Siege scenario and press enter",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messageformat + "Please type the minimal amount of players needed to start this siege",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messageformat + "Please type the maximal amount of players needed to start this siege",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the spawn locations of " + ColorOptions.KAKColor + "Team 1 (Defenders)",
					ColorOptions.messageformat + "Teams can have multiple spawn locations. Players can choose where to spawn",
					ColorOptions.messageformat + "Stand on the location of the spawnpoint and type 'save'",
					ColorOptions.error + "Start with the most important spawnpoint and end with the least important one",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the spawn locations of " + ColorOptions.error + "Team 2 (Attackers)",
					ColorOptions.messageformat + "Teams can have multiple spawn locations. Players can choose where to spawn",
					ColorOptions.messageformat + "Stand on the location of the spawnpoint and type 'save'",
					ColorOptions.error + "Start with the most important spawnpoint and end with the least important one",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the Main Objective",
					ColorOptions.messageformat + "The main objective is the block or flag the attacking team must destroy to win",
					ColorOptions.messageformat + "Stand on the desired location and type 'save'",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting Side Objectives",
					ColorOptions.messageformat + "Side objectives will grant bonusses to the attacking team when destroyed, or to the defending team if not",
					ColorOptions.messageformat + "Right-click a gate/property to set as Side Objective",
					ColorOptions.messageformat + "or stand on a location other than a gate/property to save",
					ColorOptions.messageformat + "You can set as many as you want",
					"",
					ColorOptions.error + "To undo the last side objective type 'undo'",
					ColorOptions.messagesubjects + "If you have all side objectives set, type 'next'", 
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the rewards for the winning team",
					ColorOptions.messageformat + "Please type the reward-amount of " + ColorOptions.messagesubjects + "experience " + ColorOptions.messageformat + "per person on the winning team",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the rewards for the winning team",
					ColorOptions.messageformat + "Please type the reward-amount of " + ColorOptions.coinStats + "coins " + ColorOptions.messageformat + "per person on the winning team",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the rewards for the amount of captured/defended Side Objectives",
					ColorOptions.messageformat + "Please type the reward-amount of " + ColorOptions.messagesubjects + "experience " + ColorOptions.messageformat + "per person per Side Objective held by their team",
					ColorOptions.message + "The rewards only count for Side Objectives, not the Main Objective",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the rewards for the amount of captured/defended Side Objectives",
					ColorOptions.messageformat + "Please type the reward-amount of " + ColorOptions.coinStats + "coins " + ColorOptions.messageformat + "per person per Side Objective held by their team",
					ColorOptions.message + "The rewards only count for Side Objectives, not the Main Objective",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the rewards for capturing a Side Objective",
					ColorOptions.messageformat + "Please type the reward-amount of " + ColorOptions.messagesubjects + "experience " + ColorOptions.messageformat + "for the player capturing a Side Objective",
					ColorOptions.message + "The rewards only count for Side Objectives, not the Main Objective",
					""
					)),
			new ArrayList<String>(Arrays.asList(
					"",
					ColorOptions.messagesubjects + "Setting the rewards for capturing a Side Objective",
					ColorOptions.messageformat + "Please type the reward-amount of " + ColorOptions.coinStats + "coins " + ColorOptions.messageformat + "for the player capturing a Side Objective",
					ColorOptions.message + "The rewards only count for Side Objectives, not the Main Objective",
					""
					))
			));
	
	public ScenarioCreation(User user)
	{
		super(user);
		
		Location loc = user.getPlayer().getLocation();
		this.townID = Worldguard.getStructureIDbyRegion("town", loc, Worldguard.getRegionManager(loc.getWorld()));
		Sieges.ScenarioCreations.add(this);
		
		this.setStageMessages(new HashMap<Integer, List<String>>() {{
		    put(0, messages.get(0));
		    put(1, messages.get(1));
		    put(2, messages.get(2));
		    put(3, messages.get(3));
		    put(4, messages.get(4));
		    put(5, messages.get(5));
		    put(6, messages.get(6));
		    put(7, messages.get(7));
		    put(8, messages.get(8));
		    put(9, messages.get(9));
		    put(10, messages.get(10));
		    put(11, messages.get(11));
		    put(12, messages.get(12));
		}});
		this.sendMessage(startMessage);
	}
	
	public List<Location> getSpawnpointTeam1() {
		return spawnpointTeam1;
	}

	public void setSpawnpointTeam1(List<Location> spawnpointTeam1) {
		this.spawnpointTeam1 = spawnpointTeam1;
	}

	public List<Location> getSpawnpointTeam2() {
		return spawnpointTeam2;
	}

	public void setSpawnpointTeam2(List<Location> spawnpointTeam2) {
		this.spawnpointTeam2 = spawnpointTeam2;
	}

	public List<TempSideObjective> getSideObjectives() {
		return sideObjectives;
	}

	public void setSideObjectives(List<TempSideObjective> sideObjectives) {
		this.sideObjectives = sideObjectives;
	}

	@Override
	protected List<String> getSpecificConfirmMessage()
	{
		List<String> message = new ArrayList<String>();
		
		message.addAll(Arrays.asList(
				ColorOptions.stats + "Name: " + ColorOptions.statsresults + this.name,
				ColorOptions.stats + "Town: " + ColorOptions.statsresults + this.town.getTownName(this.townID),
				ColorOptions.stats + "Min. players: " + ColorOptions.statsresults + this.playersMin,
				ColorOptions.stats + "Max. players: " + ColorOptions.statsresults + this.playersMax,
				ColorOptions.stats + "Amount of Side Objectives: " + ColorOptions.statsresults + this.getSideObjectives().size(),
				"",
				ColorOptions.messageformat + "Rewards:",
				ColorOptions.stats + "Winning team (per team member): " + ColorOptions.statsresults + this.expRewardWin + " experience, " + this.coinRewardWin + " coins",
				ColorOptions.stats + "No. of Side Objectives (per team per team member): " + ColorOptions.statsresults + this.expRewardSideObjective + " experience, " + this.coinRewardSideObjective + " coins",
				ColorOptions.stats + "Per captured Side Objective (individual): " + ColorOptions.statsresults + this.expRewardCapture + " experience, " + this.coinRewardCapture + " coins",
				""
				));
		
		return message;
	}
//	
//	public void start()
//	{
//		this.nextStage(0);
//	}
//	
//	protected void nextStage(int stage)
//	{
////		if (stage <= -1)
////		{
////			stage = 0;
////			this.Stage = stage;
////		}
//		if (stage == 0)
//		{
//			if (this.Stage == -1)
//			{
//				this.Stage = 0;
//			}
//			this.sendMessage(this.messages.get(0));
//		} else if (stage == 12)
//		{
//			this.sendMessage(Arrays.asList(
//					"",
//					ColorOptions.messagesubjects + "You have completed all steps in the siege creation",
//					ColorOptions.message + "Confirm the information below",
//					ColorOptions.message + "Type " + ColorOptions.messageachievement + "confirm" + ColorOptions.message + " to confirm or " + ColorOptions.error + "undo" + ColorOptions.message + " to undo",
//					"",
//					ColorOptions.stats + "Name: " + ColorOptions.statsresults + this.name,
//					ColorOptions.stats + "Town: " + ColorOptions.statsresults + this.town.getTownName(this.townID),
//					ColorOptions.stats + "Min. players: " + ColorOptions.statsresults + this.playersMin,
//					ColorOptions.stats + "Max. players: " + ColorOptions.statsresults + this.playersMax,
//					ColorOptions.stats + "Amount of Side Objectives: " + ColorOptions.statsresults + this.sideObjectives.size(),
//					"",
//					ColorOptions.messageformat + "Rewards:",
//					ColorOptions.stats + "Winning team (per team member): " + ColorOptions.statsresults + this.expRewardWin + " experience, " + this.coinRewardWin + " coins",
//					ColorOptions.stats + "No. of Side Objectives (per team per team member): " + ColorOptions.statsresults + this.expRewardSideObjective + " experience, " + this.coinRewardSideObjective + " coins",
//					ColorOptions.stats + "Per captured Side Objective (individual): " + ColorOptions.statsresults + this.expRewardCapture + " experience, " + this.coinRewardCapture + " coins",
//					""
//					));
//		} else
//		{
//			new BukkitRunnable()
//			{
//				public void run()
//				{
//					sendMessage(messages.get(stage));
//				}
//			}.runTaskLaterAsynchronously(main, 20);
//		}
//		this.Stage = stage;
//	}
	
	@Override
	protected void create()
	{
		Scenarios.CreateScenario(this);
	}
	
	@Override
	public void processEvent(Event event)
	{
		Player player = user.getPlayer();
		boolean cancel = false;

		if (event instanceof PlayerChatEvent
				|| event instanceof PlayerCommandPreprocessEvent)
		{
			String message = null;
			if (event instanceof PlayerChatEvent)
			{
				message = ((PlayerChatEvent) event).getMessage();
			} else if (event instanceof PlayerCommandPreprocessEvent)
			{
				message = ((PlayerCommandPreprocessEvent) event).getMessage();
				message.replace("/", "");
			}
			
			if (message.equalsIgnoreCase("stop"))
			{
				this.stop();
				cancel = true;
			} else if (message.equalsIgnoreCase("start"))
			{
				this.start();
				cancel = true;
			} else if (message.equalsIgnoreCase("confirm"))
			{
				if (this.getStage() == this.getLastStage()+1)
				{
					this.complete();
				} else
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.error + "You must complete all previous steps first"
							));
				}
				cancel = true;
			} else if (message.equalsIgnoreCase("undo"))
			{
				int stage = this.getStage();
				String undoMessage = ColorOptions.error + "Undone the last step";
				if (this.getStage() == 3)
				{
					if (this.getSpawnpointTeam1().size() >= 9)
					{
						this.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
						return;
					}
					this.getSpawnpointTeam1().remove(this.getSpawnpointTeam1().size()-1);
					undoMessage = ColorOptions.error + "Undone the last spawnpoint for team 1";
				} else if (this.getStage() == 4)
				{
					if (this.getSpawnpointTeam2().size() >= 9)
					{
						this.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
						return;
					}
					this.getSpawnpointTeam2().remove(this.getSpawnpointTeam2().size()-1);
					undoMessage = ColorOptions.error + "Undone the last spawnpoint for team 2";
				} else
				if (this.getStage() == 6)
				{
					this.getSideObjectives().remove(this.getSideObjectives().size()-1);
					undoMessage = ColorOptions.error + "Undone the last side objective";
				} else
				{
					stage -= 1;
					undoMessage = ColorOptions.error + "Undone the last step";
				}
				this.nextStage(stage);
				user.sendMessage(undoMessage);
				cancel = true;
			} else if (this.getStage() == -1) 
			{
				this.falseCommand(Arrays.asList(
						ColorOptions.message + "Type 'start' or 'next' to start the creation process",
						""
						));
			} else if (this.getStage() == 0)
			{
				if (Scenarios.existScenario(message, this.townID))
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "The name of the scenario already exists for this town!",
							""
							));
					return;
				}
				if (message.length() > 3)
				{
					this.name = message;
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the siege name to " + ColorOptions.messagesubjects + message,
							""
							));
					this.nextStage(this.getStage()+1);
				} else
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "The name of the scenario must be more than 3 characters!",
							""
							));
				}
			} else if (this.getStage() == 1 || this.getStage() == 2)
			{
				if (!main.isInt(message))
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "The min/max amount of players must be a number!",
							""
							));
					return;
				}
				if (Integer.valueOf(message) < 0)
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "The min/max amount of players must be a number above 0!",
							""
							));
					return;
				}
				if (this.getStage() == 1)
				{
					this.playersMin = Integer.valueOf(message);
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the minimal amount of players to  " + ColorOptions.messagesubjects + this.playersMin,
							""
							));
				} else if (this.getStage() == 2)
				{
					this.playersMax = Integer.valueOf(message);
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the maximal amount of players to  " + ColorOptions.messagesubjects + this.playersMax,
							""
							));
				}
				this.nextStage(this.getStage()+1);
			} else if (this.getStage() == 3)
			{
				if (message.equalsIgnoreCase("next"))
				{
					this.nextStage(this.getStage()+1);
				} else
				if (message.equalsIgnoreCase("save"))
				{
					if (this.getSpawnpointTeam1().size() >= 9)
					{
						this.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
						return;
					}
					this.getSpawnpointTeam1().add(user.getPlayer().getLocation());
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved a spawnpoint of team 1 to your current location!",
							ColorOptions.messageachievement + "type 'next' when you are done",
							""
							));
					//sc.nextStage(sc.Stage+1);
				} else
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "Type 'save' to save the spawnpoint for team 1",
							""
					));
				}
				
			} else if (this.getStage() == 4)
			{
				if (message.equalsIgnoreCase("next"))
				{
					this.nextStage(this.getStage()+1);
				} else
				if (message.equalsIgnoreCase("save"))
				{
					if (this.getSpawnpointTeam2().size() >= 9)
					{
						this.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
						return;
					}
					this.getSpawnpointTeam2().add(user.getPlayer().getLocation());
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved a spawnpoint of team 2 to your current location!",
							ColorOptions.messageachievement + "type 'next' when you are done",
							""
							));
					//sc.nextStage(sc.Stage+1);
				} else
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "Type 'save' to save the spawnpoint for team 2",
							""
					));
				}
			} else if (this.getStage() == 5)
			{
				if (message.equalsIgnoreCase("save"))
				{
					this.mainObjective = player.getLocation();
					this.sendMessage(Arrays.asList(
							"",
							ColorOptions.messageachievement + "Saved the location for the Main Objective",
							""
							));
					player.playSound(player.getLocation(), Sound.NOTE_PIANO, 0.5F, 1.0F);
					this.nextStage(this.getStage()+1);
				} else
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "Stand on the desired location of the Main Objective and type 'save'",
							""
					));
				}
			} else if (this.getStage() == 6)
			{
				if (message.equalsIgnoreCase("save"))
				{
					if (this.trySaveSideObjective(player.getLocation()))
					{
						player.playSound(player.getLocation(), Sound.NOTE_PIANO, 0.5F, 1.0F);
					}
				} else
				if (message.equalsIgnoreCase("next"))
				{
					this.nextStage(this.getStage()+1);
				} else
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "Right-click a gate/property to save as Side Objective",
							ColorOptions.message + "or stand on a desired location to save other locations",
							ColorOptions.message + "Type 'next' if you are done setting side objectives",
							""
					));
				}
			} else if (this.getStage() >= 7 && this.getStage() <= 12)
			{
				if (!main.isInt(message))
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "The reward amount must be a number!",
							""
							));
					return;
				}
				if (Integer.valueOf(message) < 0)
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.message + "The reward amount of players must be a number above 0!",
							""
							));
					return;
				}
				int argument = Integer.valueOf(message);
				
				if (this.getStage() == 7)
				{
					this.expRewardWin = argument;
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the experience reward for the winning team (per team member) to  " + ColorOptions.messagesubjects + argument,
							""
							));
				} else if (this.getStage() == 8)
				{
					this.coinRewardWin = argument;
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the coin reward for the winning team (per team member) to  " + ColorOptions.messagesubjects + argument,
							""
							));
				} else if (this.getStage() == 9)
				{
					this.expRewardSideObjective = argument;
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the experience reward per captured/defended Side Objective (per team per team member) to  " + ColorOptions.messagesubjects + argument,
							""
							));
				} else if (this.getStage() == 10)
				{
					this.coinRewardSideObjective = argument;
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the coin reward per captured/defended Side Objective (per team per team member) to  " + ColorOptions.messagesubjects + argument,
							""
							));
				} else if (this.getStage() == 11)
				{
					this.expRewardCapture = argument;
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the experience reward per captured Side Objective (individual) to  " + ColorOptions.messagesubjects + argument,
							""
							));
				} else if (this.getStage() == 12)
				{
					this.coinRewardCapture = argument;
					this.sendMessage(Arrays.asList(
							ColorOptions.messageachievement + "Saved the coin reward per captured Side Objective (individual) to  " + ColorOptions.messagesubjects + argument,
							""
							));
				}
				
				this.nextStage(this.getStage()+1);
			}
		} else 
		if (event instanceof PlayerInteractEvent)
		{
			event = (PlayerInteractEvent) event;
			if (((PlayerInteractEvent) event).getAction() != Action.RIGHT_CLICK_BLOCK)
			{
				return;
			}
			
			if (((PlayerInteractEvent) event).getClickedBlock() == null)
			{
				return;
			}
			
//			SiegeObject object = scenario.editMode.get(user);
//			if (object instanceof Objective)
//			{
//				if (event.getClickedBlock().getType() != Material.STANDING_BANNER)
//				{
//					user.getPlayer().sendMessage(ColorOptions.error + "The block must be a standing banner");
//					user.getPlayer().sendMessage(ColorOptions.error + "Type 'stop' to stop editing");
//					return;
//				}
//				event.setCancelled(true);
//				
//				Objective objective = (Objective) object;
//				if (objective.getLocation().equals(event.getClickedBlock().getLocation()))
//				{
//					user.getPlayer().sendMessage(ColorOptions.error + "New location can't be the same as the original one");
//					return;
//				}
//				Bukkit.getConsoleSender().sendMessage(objective.getLocation().toString());
//				Bukkit.getConsoleSender().sendMessage(event.getClickedBlock().getLocation().toString());
//
//				user.getPlayer().sendMessage(ColorOptions.message + "Changing location...");
//				objective.changeLocation(user.getPlayer(), event.getClickedBlock().getLocation());
//				Block block = event.getClickedBlock();
//		        BlockState bs = block.getState();
//		        Banner banner = (Banner) block.getState();
//		        banner.setBaseColor(DyeColor.WHITE);
//		        banner.addPattern(new Pattern(DyeColor.BLUE, PatternType.GRADIENT));
//		        banner.addPattern(new Pattern(DyeColor.WHITE, PatternType.GRADIENT_UP));
//		        banner.update(true);
//				scenario.removeEditMode(user);
//				new Menu().openScenarioManager(user, scenario);
//			}
//			return;
			if (this.getStage() == 5 || this.getStage() == 6)
			{
				if (((PlayerInteractEvent) event).getClickedBlock().getType() != Material.STANDING_BANNER)
				{
					this.falseCommand(Arrays.asList(ColorOptions.error + "The block must be a standing banner"));
					return;
				}
			}
				
			
			if (this.getStage() == 5)
			{
				this.mainObjective = ((PlayerInteractEvent) event).getClickedBlock().getLocation();
				this.sendMessage(Arrays.asList(
						"",
						ColorOptions.messageachievement + "Saved the block as main objective",
						""
						));
				player.playSound(player.getLocation(), Sound.NOTE_PIANO, 0.5F, 1.0F);
				this.nextStage(this.getStage()+1);
			} else if (this.getStage() == 6)
			{
				this.trySaveSideObjective(((PlayerInteractEvent) event).getClickedBlock().getLocation());
			}
		}
		
		if (cancel)
		{
			if (event instanceof PlayerChatEvent)
			{
				((PlayerChatEvent) event).setCancelled(true);
			} else if (event instanceof PlayerCommandPreprocessEvent)
			{
				((PlayerCommandPreprocessEvent) event).setCancelled(true);
			}
		}
	}
	
	protected boolean trySaveSideObjective(Location location)
	{
		if (this.getSideObjectives().size() >= 9)
		{
			this.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Side Objectives has been reached"));
			return false;
		}
		RegionManager regionManager = Worldguard.getRegionManager(location.getWorld());
		Integer structureID = null;
		Integer propertyID = Worldguard.getStructureIDbyRegion("property", location, regionManager);
		Integer gateID = Worldguard.getStructureIDbyRegion("gate", location, regionManager);
		
		if (gateID != null)
		{
			structureID = gateID;
		} else
		if (propertyID != null)
		{
			structureID = propertyID;
		}
		for (TempSideObjective so : this.getSideObjectives())
		{
			if (so.structureID != -1)
			{
				if (so.structureID == propertyID || so.structureID == gateID)
				{
					this.falseCommand(Arrays.asList(
							ColorOptions.error + "This structure is already a side objective ID " + so.structureID
							));
					return false;
				}
			} else
			if (so.location == location)
			{
				this.falseCommand(Arrays.asList(
						ColorOptions.error + "This location is already a side objective"
						));
				return false;
			}
		}
		this.getSideObjectives().add(new TempSideObjective(location, structureID));
		if (structureID != null)
		{
			this.sendMessage(Arrays.asList(
					"",
					ColorOptions.messageachievement + "Saved the structure(" + structureID + ") as a side objective",
					ColorOptions.messageachievement + "type 'next' when you are done",
					""
					));
		} else
		{
			this.sendMessage(Arrays.asList(
					"",
					ColorOptions.messageachievement + "Saved the block as a side objective",
					ColorOptions.messageachievement + "type 'next' when you are done",
					""
					));
		}

		this.getUser().getPlayer().playSound(this.getUser().getPlayer().getLocation(), Sound.NOTE_PIANO, 0.5F, 1.0F);
		return true;
	}
//	
//	protected void stash()
//	{
//		this.stashed = true;
//		Sieges.stashedSiegeCreations.add(this);
//		
//		this.stashTask = new BukkitRunnable()
//		{
//			public void run()
//			{
//				Sieges.stashedSiegeCreations.remove(instance);
//				user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "The stashed Siege scenario is no longer stashed");
//			}
//		}.runTaskLaterAsynchronously(main, 300*20);
//	}
}
