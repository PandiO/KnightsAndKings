package Sieges;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Menu.Menu;
import Users.User;
import Users.Users;

public class ScenarioCreationEvents implements Listener
{
	
	private Main main;
	public ScenarioCreationEvents(Main main)
	{
		this.main = main;
	}
	
//	@EventHandler
//	public void commandEvent (PlayerCommandPreprocessEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		ScenarioCreation sc = null;
//		
//		try
//		{
//			user = Users.getUser(uuid);
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		sc = Sieges.getSiegeCreation(user);
//		
//		if (sc == null)
//		{
//			return;
//		}
//		
//		if (e.getMessage().equalsIgnoreCase("/stop"))
//		{
//			sc.stop();
//			e.setCancelled(true);
//		} else if (e.getMessage().equalsIgnoreCase("/undo"))
//		{
//			if (sc.getStage() == 3)
//			{
//				if (sc.getSpawnpointTeam1().size() >= 9)
//				{
//					sc.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
//					return;
//				}
//				sc.getSpawnpointTeam1().remove(sc.getSpawnpointTeam1().size()-1);
//				sc.nextStage(sc.getStage());
//				player.sendMessage(ColorOptions.error + "Undone the last spawnpoint for team 1");
//			} else if (sc.getStage() == 4)
//			{
//				if (sc.getSpawnpointTeam2().size() >= 9)
//				{
//					sc.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
//					return;
//				}
//				sc.getSpawnpointTeam2().remove(sc.getSpawnpointTeam2().size()-1);
//				sc.nextStage(sc.getStage());
//				player.sendMessage(ColorOptions.error + "Undone the last spawnpoint for team 2");
//			} else
//			if (sc.getStage() == 6)
//			{
//				sc.getSideObjectives().remove(sc.getSideObjectives().size()-1);
//				sc.nextStage(sc.getStage());
//				player.sendMessage(ColorOptions.error + "Undone the last side objective");
//			} else
//			{
//				sc.nextStage(sc.getStage()-1);
//				player.sendMessage(ColorOptions.error + "Undone the last step");
//			}
//			e.setCancelled(true);
//		} else if (e.getMessage().equalsIgnoreCase("/start"))
//		{
//			sc.start();
//			e.setCancelled(true);
//		} else if (e.getMessage().equalsIgnoreCase("/confirm"))
//		{
//			if (sc.getStage() == sc.getLastStage()+1)
//			{
//				sc.complete();
//			} else
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.error + "You must complete all previous steps first"
//						));
//			}
//			e.setCancelled(true);
//		}
//	}
	
//	@EventHandler
//	public void chatEvent (PlayerChatEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		User user = null;
//		ScenarioCreation sc = null;
//		
//		try
//		{
//			user = Users.getUser(uuid);
//		} catch (UserNotFoundException ex)
//		{
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			ErrorHandlers.userNotFoundAction(null, player, true);
//			return;
//		}
//		
//		sc = Sieges.getSiegeCreation(user);
//		
//		if (sc == null)
//		{
//			Scenario scenario = null;
//			for (Scenario scenarios : Sieges.Scenarios)
//			{
//				if (scenarios.editMode.containsKey(user))
//				{
//					scenario = scenarios;
//					break;
//				}
//			}
//			
//			if (scenario == null)
//			{
//				return;
//			}
//			
//			if (e.getMessage().equalsIgnoreCase("stop"))
//			{
//				scenario.removeEditMode(user);
//				new Menu().openScenarioManager(user, scenario);
//				return;
//			}
//			
//			SiegeObject object = scenario.editMode.get(user);
//			if (object instanceof SiegeSpawnpoint)
//			{
//				e.setCancelled(true);
//				SiegeSpawnpoint sp = (SiegeSpawnpoint) object;
//				
//				if (e.getMessage().equalsIgnoreCase("save"))
//				{
//					user.getPlayer().sendMessage(ColorOptions.message + "Changing location...");
//					if (sp.getLocation().distance(user.getPlayer().getLocation()) <= 1)
//					{
//						user.getPlayer().sendMessage(ColorOptions.error + "New location can't be the same as the original one");
//						return;
//					}
//					sp.changeLocation(user.getPlayer(), user.getPlayer().getLocation());
//					scenario.removeEditMode(user);
//					new Menu().openScenarioManager(user, scenario);
//				} else
//				{
//					user.getPlayer().sendMessage(ColorOptions.error + "Type 'save' on the location you want to save or 'stop' to stop editing");
//				}
//			}
//			return;
//		}
//		e.setCancelled(true);
//		
//		if (!main.isInt(e.getMessage()) && e.getMessage().length() <= 3)
//		{
//			
//			if (sc.getStage() == -1)
//			{
//				player.sendMessage(ColorOptions.message + "Or type 'start' to start the creation");
//			} else if (sc.getStage() >= 0 && sc.getStage() <= 2)
//			{
//				if (sc.getStage() == 0)
//				{
//					player.sendMessage(ColorOptions.message + "The name of the scenario must be more than 3 characters!");
//				} else
//				{
//					player.sendMessage(ColorOptions.message + "The min/max amount of players must be a number!");
//				}
//			}
//		}
//		
//		if (e.getMessage().equalsIgnoreCase("stop"))
//		{
//			sc.stop();
//		} else if (e.getMessage().equalsIgnoreCase("undo"))
//		{
//			if (sc.getStage() == 3)
//			{
//				sc.getSpawnpointTeam1().remove(sc.getSpawnpointTeam1().size()-1);
//				sc.nextStage(sc.getStage());
//				player.sendMessage(ColorOptions.error + "Undone the last spawnpoint for team 1");
//			} else if (sc.getStage() == 4)
//			{
//				sc.getSpawnpointTeam2().remove(sc.getSpawnpointTeam2().size()-1);
//				sc.nextStage(sc.getStage());
//				player.sendMessage(ColorOptions.error + "Undone the last spawnpoint for team 2");
//			} else
//			if (sc.getStage() == 6)
//			{
//				sc.getSideObjectives().remove(sc.getSideObjectives().size()-1);
//				sc.nextStage(sc.getStage());
//				player.sendMessage(ColorOptions.error + "Undone the last side objective");
//			} else
//			{
//				sc.nextStage(sc.getStage()-1);
//				player.sendMessage(ColorOptions.error + "Undone the last step");
//			}
//		} else if (e.getMessage().equalsIgnoreCase("start"))
//		{
//			sc.start();
//		} else if (e.getMessage().equalsIgnoreCase("confirm"))
//		{
//			if (sc.getStage() == sc.getLastStage()+1)
//			{
//				sc.complete();
//			} else
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.error + "You must complete all previous steps first"
//						));
//			}
//		} else if (sc.getStage() == -1) 
//		{
//			if (e.getMessage().equalsIgnoreCase("start") || e.getMessage().equalsIgnoreCase("next"))
//			{
//				sc.start();
//			} else
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "Type 'start' or 'next' to start the creation process",
//						""
//						));
//			}
//		} else if (sc.getStage() == 0)
//		{
//			if (Scenarios.existScenario(e.getMessage(), sc.townID))
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "The name of the scenario already exists for this town!",
//						""
//						));
//				return;
//			}
//			if (e.getMessage().length() > 3)
//			{
//				sc.name = e.getMessage();
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the siege name to " + ColorOptions.messagesubjects + e.getMessage(),
//						""
//						));
//				sc.nextStage(sc.getStage()+1);
//			} else
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "The name of the scenario must be more than 3 characters!",
//						""
//						));
//			}
//		} else if (sc.getStage() == 1 || sc.getStage() == 2)
//		{
//			if (!main.isInt(e.getMessage()))
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "The min/max amount of players must be a number!",
//						""
//						));
//				return;
//			}
//			if (Integer.valueOf(e.getMessage()) < 0)
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "The min/max amount of players must be a number above 0!",
//						""
//						));
//				return;
//			}
//			if (sc.getStage() == 1)
//			{
//				sc.playersMin = Integer.valueOf(e.getMessage());
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the minimal amount of players to  " + ColorOptions.messagesubjects + sc.playersMin,
//						""
//						));
//			} else if (sc.getStage() == 2)
//			{
//				sc.playersMax = Integer.valueOf(e.getMessage());
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the maximal amount of players to  " + ColorOptions.messagesubjects + sc.playersMax,
//						""
//						));
//			}
//			sc.nextStage(sc.getStage()+1);
//		} else if (sc.getStage() == 3)
//		{
//			if (e.getMessage().equalsIgnoreCase("next"))
//			{
//				sc.nextStage(sc.getStage()+1);
//			} else
//			if (e.getMessage().equalsIgnoreCase("save"))
//			{
//				if (sc.getSpawnpointTeam1().size() >= 9)
//				{
//					sc.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
//					return;
//				}
//				sc.getSpawnpointTeam1().add(player.getLocation());
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved a spawnpoint of team 1 to your current location!",
//						ColorOptions.messageachievement + "type 'next' when you are done",
//						""
//						));
//				//sc.nextStage(sc.Stage+1);
//			} else
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "Type 'save' to save the spawnpoint for team 1",
//						""
//				));
//			}
//			
//		} else if (sc.getStage() == 4)
//		{
//			if (e.getMessage().equalsIgnoreCase("next"))
//			{
//				sc.nextStage(sc.getStage()+1);
//			} else
//			if (e.getMessage().equalsIgnoreCase("save"))
//			{
//				if (sc.getSpawnpointTeam2().size() >= 9)
//				{
//					sc.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
//					return;
//				}
//				sc.getSpawnpointTeam2().add(player.getLocation());
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved a spawnpoint of team 2 to your current location!",
//						ColorOptions.messageachievement + "type 'next' when you are done",
//						""
//						));
//				//sc.nextStage(sc.Stage+1);
//			} else
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "Type 'save' to save the spawnpoint for team 2",
//						""
//				));
//			}
//		} else if (sc.getStage() == 5)
//		{
//			sc.falseCommand(Arrays.asList(
//					ColorOptions.message + "Right-click a block/banner to save the main objective",
//					""
//			));
//		} else if (sc.getStage() == 6)
//		{
//			if (e.getMessage().equalsIgnoreCase("next"))
//			{
//				sc.nextStage(sc.getStage()+1);
//			} else
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "Right-click a block/banner to save a side objective",
//						ColorOptions.message + "Type 'next' if you are done setting side objectives",
//						""
//				));
//			}
//		} else if (sc.getStage() >= 7 && sc.getStage() <= 12)
//		{
//			if (!main.isInt(e.getMessage()))
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "The reward amount must be a number!",
//						""
//						));
//				return;
//			}
//			if (Integer.valueOf(e.getMessage()) < 0)
//			{
//				sc.falseCommand(Arrays.asList(
//						ColorOptions.message + "The reward amount of players must be a number above 0!",
//						""
//						));
//				return;
//			}
//			int argument = Integer.valueOf(e.getMessage());
//			
//			if (sc.getStage() == 7)
//			{
//				sc.expRewardWin = argument;
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the experience reward for the winning team (per team member) to  " + ColorOptions.messagesubjects + argument,
//						""
//						));
//			} else if (sc.getStage() == 8)
//			{
//				sc.coinRewardWin = argument;
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the coin reward for the winning team (per team member) to  " + ColorOptions.messagesubjects + argument,
//						""
//						));
//			} else if (sc.getStage() == 9)
//			{
//				sc.expRewardSideObjective = argument;
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the experience reward per captured/defended Side Objective (per team per team member) to  " + ColorOptions.messagesubjects + argument,
//						""
//						));
//			} else if (sc.getStage() == 10)
//			{
//				sc.coinRewardSideObjective = argument;
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the coin reward per captured/defended Side Objective (per team per team member) to  " + ColorOptions.messagesubjects + argument,
//						""
//						));
//			} else if (sc.getStage() == 11)
//			{
//				sc.expRewardCapture = argument;
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the experience reward per captured Side Objective (individual) to  " + ColorOptions.messagesubjects + argument,
//						""
//						));
//			} else if (sc.getStage() == 12)
//			{
//				sc.coinRewardCapture = argument;
//				sc.sendMessage(Arrays.asList(
//						ColorOptions.messageachievement + "Saved the coin reward per captured Side Objective (individual) to  " + ColorOptions.messagesubjects + argument,
//						""
//						));
//			}
//			
//			sc.nextStage(sc.getStage()+1);
//		}
//		
//	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		ScenarioCreation sc = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			return;
		}
		
		sc = Sieges.getSiegeCreation(user);
		
		if (sc == null)
		{
			Scenario scenario = null;
			for (Scenario scenarios : Sieges.Scenarios)
			{
				if (scenarios.editMode.containsKey(user))
				{
					scenario = scenarios;
					break;
				}
			}
			
			if (scenario == null)
			{
				return;
			}
			
			if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			{
				return;
			}
			
			if (e.getClickedBlock() == null)
			{
				return;
			}
			
			SiegeObject object = scenario.editMode.get(user);
			if (object instanceof Objective)
			{
				if (e.getClickedBlock().getType() != Material.STANDING_BANNER)
				{
					user.getPlayer().sendMessage(ColorOptions.error + "The block must be a standing banner");
					user.getPlayer().sendMessage(ColorOptions.error + "Type 'stop' to stop editing");
					return;
				}
				e.setCancelled(true);
				
				Objective objective = (Objective) object;
				if (objective.getLocation().equals(e.getClickedBlock().getLocation()))
				{
					user.getPlayer().sendMessage(ColorOptions.error + "New location can't be the same as the original one");
					return;
				}
				Bukkit.getConsoleSender().sendMessage(objective.getLocation().toString());
				Bukkit.getConsoleSender().sendMessage(e.getClickedBlock().getLocation().toString());

				user.getPlayer().sendMessage(ColorOptions.message + "Changing location...");
				objective.changeLocation(user.getPlayer(), e.getClickedBlock().getLocation());
				Block block = e.getClickedBlock();
		        BlockState bs = block.getState();
		        Banner banner = (Banner) block.getState();
		        banner.setBaseColor(DyeColor.WHITE);
		        banner.addPattern(new Pattern(DyeColor.BLUE, PatternType.GRADIENT));
		        banner.addPattern(new Pattern(DyeColor.WHITE, PatternType.GRADIENT_UP));
		        banner.update(true);
				scenario.removeEditMode(user);
				new Menu().openScenarioManager(user, scenario);
			}
			return;
		}
		
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		
		if (e.getClickedBlock() == null)
		{
			return;
		}
		
		if (sc.getStage() == 5 || sc.getStage() == 6)
		{
			if (e.getClickedBlock().getType() != Material.STANDING_BANNER)
			{
				sc.falseCommand(Arrays.asList(ColorOptions.error + "The block must be a standing banner"));
				return;
			}
		}
			
		
		if (sc.getStage() == 5)
		{
			sc.mainObjective = e.getClickedBlock().getLocation();
			sc.sendMessage(Arrays.asList(
					"",
					ColorOptions.messageachievement + "Saved the block as main objective",
					""
					));
			player.playSound(player.getLocation(), Sound.NOTE_PIANO, 0.5F, 1.0F);
			sc.nextStage(sc.getStage()+1);
		} else if (sc.getStage() == 6)
		{
			if (sc.getSideObjectives().size() >= 9)
			{
				sc.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Side Objectives has been reached"));
				return;
			}
			for (TempSideObjective so : sc.getSideObjectives())
			{
				if (so.location == e.getClickedBlock().getLocation())
				{
					sc.falseCommand(Arrays.asList(
							ColorOptions.error + "This block is already a side objective"
							));
					return;
				}
			}
			sc.getSideObjectives().add(new TempSideObjective(e.getClickedBlock().getLocation(), null));
			sc.sendMessage(Arrays.asList(
					"",
					ColorOptions.messageachievement + "Saved the block as a side objective",
					ColorOptions.messageachievement + "type 'next' when you are done",
					""
					));
			player.playSound(player.getLocation(), Sound.NOTE_PIANO, 0.5F, 1.0F);
		}
	}
}
