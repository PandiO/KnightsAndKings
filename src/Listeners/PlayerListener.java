package Listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldGuard;
import Arenas.Duel;
import Assignments.Assignment;
import Assignments.AssignmentTravelDistance;
import DataManager.Creations;
import DataManager.HideandSeeks;
import DataManager.Users2;
import DataManager.Worldguard;
import DataManager.Structures.Gates;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.SoundHandler;
import HideAndSeek.HideAndSeek;
import Houses.House;
import Houses.HouseCommands;
import Main.Main;
import Menu.DuelSetupClick;
import Menu.Menu;
import Minigames.BanditAmbushes;
import Minigames.Transport;
import Models.Structures.Gate;
import Models.creations.Creation;
import Products.Product;
import Properties.Property;
import Properties.PropertyCommands;
import Rooms.Room;
import Rooms.RoomCommands;
import Sieges.Siege;
import Sieges.SiegeMember;
import Sieges.Sieges;
import Skills.Skill;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Teleport.TeleportDelay;
import Towns.Town;
import Towns.TownEvents;
import Tutorial.Tutorial;
import Tutorial.Tutorials;
import Users.User;
import Users.Users;
import Users.offlineUser;
import net.citizensnpcs.api.CitizensAPI;

public class PlayerListener implements Listener
{
	static ArrayList<UUID> newPlayers = new ArrayList<UUID>();

	Room room = new Room();
	Property property = new Property();
	Product product = new Product();
	WorldGuard worldguard = new WorldGuard();
	SpawnPoint spawnpoint = new SpawnPoint();
	Town town = new Town();
	House house = new House();
	Street street = new Street();
	offlineUser offlineUser = new offlineUser(); 
	Skill skill = new Skill();
	private Main main;
	
	public PlayerListener(Main main)
	{
		this.main = main;
	}
	
	@EventHandler
	public void OnLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Main.joinLong.put(uuid, System.currentTimeMillis());
		
		if (player.hasMetadata("NPC"))
		{
			return;
		} else
		if (Users2.ExistUser(uuid))
		{
	    	Users2.InstantiateUser(uuid, false);
			return;
		}

		boolean allowed = false;
		if (Bukkit.hasWhitelist())
		{
			for (OfflinePlayer listed : Bukkit.getWhitelistedPlayers())
			{
				UUID lu = listed.getUniqueId();
				if (uuid.equals(lu))
				{
					allowed = true;
					break;
				}
			}
		} else
		{
			allowed = true;
		}
		
		if (allowed)
		{
			newPlayers.add(uuid);
			main.CreateUser(player.getName(), uuid, "Male", event.getAddress());
	    	Bukkit.broadcastMessage(ColorOptions.messageachievement + "" + ChatColor.BOLD + "► New player " + ColorOptions.messagesubjects + player.getName() + ColorOptions.messageachievement + " entered the Kingdoms! Welcome!");
	    	for (Player players : Bukkit.getOnlinePlayers())
	    	{
	    		players.playSound(players.getLocation(), SoundHandler.NOTE_PLING, 0.5F, 1.0F);
	    	}
		} else
		{
			Bukkit.broadcastMessage(ColorOptions.message + "► Player " + player.getName() + " tried to join for the first time, but is not whitelisted");
		}
	}
	
	@EventHandler
	public void OnJoin(PlayerJoinEvent event)
	{
		/* Prerequisites */
		main.DBreconnect();
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		
	    SimpleDateFormat time = new SimpleDateFormat("dd-MM HH:mm:ss");
	    
	    if (Users2.ExistUser(uuid))
	    {
	    	User user = Users2.InstantiateUser(uuid, false);
	    	Main.logMessage("Joining player");
	    	user.join(player);
	    	
	    	user.setJoinMessage(event);
	    	

			player.sendMessage(ColorOptions.statsbrackets);
			player.sendMessage(ChatColor.YELLOW + 	 "Welcome " + ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " to " + ChatColor.BLUE +  "Knights and Kings");
			player.sendMessage(ChatColor.YELLOW +    "The current time is: " + ChatColor.AQUA + main.getTime());
			player.sendMessage(ChatColor.YELLOW + 	 "Coins: " + ChatColor.BLUE + ColorOptions.formatCurrency(user.getCoins()) + ChatColor.DARK_PURPLE + " Gems: " + ChatColor.BLUE +  ColorOptions.formatCurrency(user.getGems()));
			player.sendMessage(ChatColor.YELLOW +    "Title: " + ChatColor.BLUE +  user.getTitleName());
			player.sendMessage(ChatColor.YELLOW +    "Salary: " + ChatColor.BLUE + ColorOptions.formatCurrency(user.getSalary()));
			player.sendMessage(ChatColor.YELLOW + 	 "Income: " + ChatColor.BLUE + ColorOptions.formatCurrency(user.getIncome()));
			player.sendMessage(ChatColor.GREEN + 	 "Don't forget to vote for useful rewards!");
			player.sendMessage(ColorOptions.statsbrackets);
			
			if (!player.hasPermission("k&k.join.nolocation") && !user.inOwnerModus())
			{
				user.TeleportSpawn();
			} else
			{
				if (user.inOwnerModus())
				{					
					List<Integer> noLocHouses = new ArrayList<Integer>();
					
					for (Integer houseID : house.getHouseIDList(null))
					{
						if (house.getHouseSpawnPoint(houseID) == null)
						{
							noLocHouses.add(houseID);
						}
					}
					
					if (!noLocHouses.isEmpty())
					{
						player.sendMessage(ColorOptions.statsbrackets);
						player.sendMessage(ColorOptions.statsformat + "These houses have no locations set:");
						for (Integer houseID : noLocHouses)
						{
							if (house.getHouseSpawnPoint(houseID) == null)
							{
								player.sendMessage(ColorOptions.statsresults + "- " + houseID);
							}
						}
						player.sendMessage(ColorOptions.statsbrackets);
						player.sendMessage(ColorOptions.falsecommand + "" + ChatColor.BOLD + "Use /house spawnpoint set when standing in the house");
						player.sendMessage(ColorOptions.falsecommand + "" + ChatColor.BOLD + "This will grant the owner of the house a personal spawnpoint into this house");
					}
				}
			}
			
			if (Main.combatlogged.contains(uuid))
			{
				player.sendMessage(ColorOptions.error + "Your inventory got dropped when you logged off in combat!");
				Main.combatlogged.remove(uuid);
			}
	    } else
	    {
	    	Users.newPlayer(player);
	    }
	    Users.updateScoreBoard(null);
	}
	
	@EventHandler
	public void OnQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.findUser(uuid);
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
		
		/**
		 * Afk
		 */
		if (user.isAfk())
		{
			user.removeAfk();
		}
		if (user.GetAfkCommence() != null)
		{
			user.RemoveAfkCommence();
		}
		
		/**
		 * Combatlog
		 */
		if (Main.incombat.containsKey(user))
		{
			player.setHealth(0.0D);
			Main.combatlogged.add(player.getUniqueId());
			Main.incombat.remove(user);
			Main.Notify(ColorOptions.error + "Player " + player.getName() + " logged off while in combat!");
		}
		
		/**
		 * Transport minigame
		 */
		Transport transport = Users.GetTransport(user);
		if (transport != null)
		{
			transport.failed(ColorOptions.error + "Player " + transport.getPlayer().getName() + " left while transporting items!", false);
		}
		
		/**
		 * Tutorial
		 */
		Tutorial intro = Tutorials.getIntroTutorial(player);
		Tutorial tutorial = Tutorials.getTutorial(player);
		if (intro != null)
		{
			intro.cancel(null);
		} else if (tutorial != null)
		{
			tutorial.cancel(null);
		}
		
		/**
		 * Leave message
		 */
		event.setQuitMessage(user.getLeaveMessage());
		
		Long current = System.currentTimeMillis();
		Long playTimeOverall = user.getPlayTime(true);
		Long playTimeToday = user.getPlayTime(false);
		if (Main.joinLong.containsKey(uuid))
		{
			Long join = Main.joinLong.get(uuid);
			user.setPlayTime((playTimeOverall + (current-join)), true);
			user.setPlayTime((playTimeToday + (current-join)), false);
		}
		user.quit();
		
		Users.updateScoreBoard(null);
	}
	
	@EventHandler
	public void OnMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Location location = player.getLocation();
		User user = null;
		Tutorial tutorial = Tutorials.getTutorial(player);
		Tutorial intro = Tutorials.getIntroTutorial(player);
		
		if (CitizensAPI.getNPCRegistry().isNPC(player))
		{
			return;
		}
				
		try
		{
			user = Users2.InstantiateUser(uuid, false);
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
		
		//Afk
		if (user.isAfk())
		{
			if (user.getAfk().teleporting == false)
			{
				user.removeAfk();
			} else
			{
				return;
			}
		}
		user.SetAfkCommence((System.currentTimeMillis() + main.afkTime*1000));
		
		
		//Gates
		if (TownEvents.inTown.containsKey(user))
		{
			Integer townID = TownEvents.inTown.get(user);
			
			for (Gate gate : Gates.Gates)
			{
				if (gate.getTownID() == townID && gate.getGateEntity() != null)
				{
					List<Entity> entities = gate.getGateEntity().getNearbyEntities(Gates.activeRange, Gates.activeRange, Gates.activeRange);
					
					if (entities.isEmpty())
					{
						gate.toggleActive(false);
					} else
					{
						for (Entity entity : entities)
						{
							if (entity instanceof Player)
							{
								gate.toggleActive(true);
								break;
							}
						}
					}
				}
			}
		}
		
		//Bandits
		if (!user.inStaffModus() &&
				!user.inOwnerModus() &&
				!user.inSafeZone() &&
				!user.inMiniGame() &&
				player.getGameMode() == GameMode.SURVIVAL &&
				!player.getAllowFlight() &&
				!BanditAmbushes.hasAmbush(user))
		{
			
			if (user.GetBanditLocation() == null)
			{
				user.SetBanditLocation(location);
			} else if (user.GetBanditLocation().distance(location) >= 5)
			{
				BanditAmbushes.tryAmbush(user);
				user.SetBanditLocation(location);
			}
		}
		
		//Titles
//		if (!titleChangeList.containsKey(uuid))
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
//						titleChangeList.put(uuid, change);
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
//						titleChangeList.put(uuid, currentTitleID-newTitleID);
//						setDemoteLoop(user, currentTitleID);
//					}
//				}
//			}
//		}
		
		//New players
		if (newPlayers.contains(uuid))
		{
			Users.newPlayer(player);
		}
		
		if (event.getFrom().getBlockX() != event.getTo().getBlockX() 
				|| event.getFrom().getBlockY() != event.getTo().getBlockY() 
				|| event.getFrom().getBlockZ() != event.getTo().getBlockZ())
		{
			//Assignments
			for (Assignment assignment : user.getAssignmentList())
			{
				if (assignment instanceof AssignmentTravelDistance)
				{
					AssignmentTravelDistance Assignment = (AssignmentTravelDistance) assignment;
					Assignment.addDistance(1);
					break;
				}
			}
			
			//Transport minigames
			Transport transport = Users.GetTransport(user);
			if (transport != null)
			{
				Integer propertyID = Worldguard.getStructureIDbyRegion("property", location, Worldguard.getRegionManager(location.getWorld()));
				if (propertyID != null)
				{
					if (propertyID == transport.getWarehouseID())
					{
						transport.reachedTarget();
					}
				}
			}
			
			//Regular teleport delay
			if (TeleportDelay.Delay.containsKey(uuid))
			{
				if (!TeleportDelay.hasImmune(uuid))
				{
					TeleportDelay.cancelTeleport(uuid, true);
				}
			}
			
			//Duels in arenas
			if (!main.duelList.isEmpty())
			{
				Duel duel = main.duelList.get(0);
				if (duel.countDownMove)
				{
					for (User fighter : duel.getPlayers())
					{
						if (uuid == fighter.getUUID())
						{
							if (duel.user1.getUUID() == uuid)
							{
								player.teleport(duel.arenaPlayer1);
							} else
							{
								player.teleport(duel.arenaPlayer2);
							}
						}
					}
				}
			}
			
			//Tutorials
			if (tutorial != null || intro != null)
			{
				if (tutorial == null)
				{
					tutorial = intro;
				}
				if (tutorial.stage != null)
				{
					if (tutorial.getName().equalsIgnoreCase("armor tutorial") && tutorial.stage >= 4)
					{
						
					} else if (tutorial.getName().equalsIgnoreCase("food tutorial") && tutorial.stage >= 4)
					{
					} else
					{
						player.teleport(tutorial.GetTargetLocation());
						Tutorials.notAllowed(player);
					}
				} else
				{
					player.teleport(tutorial.GetTargetLocation());
					Tutorials.notAllowed(player);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreCommand(PlayerCommandPreprocessEvent event)
	{
		String message = event.getMessage();
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users2.InstantiateUser(uuid, false);
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
		
		/**
		 * Hide and Seek
		 */
		HideAndSeek hideAndSeek = HideandSeeks.findHideAndSeek(user);
		if (hideAndSeek != null)
		{
			if (hideAndSeek.getInHub() || hideAndSeek.getProgress())
			{
				if (!hideAndSeek.getAllowedCommands().contains(message) && !user.inOwnerModus())
				{
					event.setCancelled(true);
					player.sendMessage(ColorOptions.error + "You are can't use this command while playing Hide and Seek!");
					player.sendMessage(ColorOptions.message + "Type /hs leave to leave");
				}
			}
		}
		
		/**
		 * Transport minigames
		 */
		Transport transport = Users.GetTransport(user);
		if (transport != null)
		{
			if (message.equalsIgnoreCase("/menu") 
					|| message.equalsIgnoreCase("/point"))
			{
				event.setCancelled(true);
				player.sendMessage(ColorOptions.error + "You can't perform this command when transporting items!");
			}
		}
		
		/**
		 * Creations
		 */
		Creation creation = Creations.FindCreation(user);
		if (creation != null)
		{
			creation.processEvent(event);
//			event.setCancelled(true);
//			if (message.equalsIgnoreCase("/stop"))
//			{
//				creation.stop();
//			} else if (message.equalsIgnoreCase("/start"))
//			{
//				creation.start();
//			} else if (message.equalsIgnoreCase("/confirm"))
//			{
//				if (creation.getStage() == creation.getLastStage()+1)
//				{
//					creation.complete();
//				} else
//				{
//					creation.falseCommand(Arrays.asList(
//							ColorOptions.error + "You must complete all previous steps first"
//							));
//				}
//				event.setCancelled(true);
//			} else if (message.equalsIgnoreCase("/undo"))
//			{
//				int stage = creation.getStage();
//				String undoMessage = ColorOptions.error + "Undone the last step";
//				if (creation instanceof ScenarioCreation)
//				{
//					ScenarioCreation sc = (ScenarioCreation) creation;
//					if (sc.getStage() == 3)
//					{
//						if (sc.getSpawnpointTeam1().size() >= 9)
//						{
//							sc.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
//							return;
//						}
//						sc.getSpawnpointTeam1().remove(sc.getSpawnpointTeam1().size()-1);
//						undoMessage = ColorOptions.error + "Undone the last spawnpoint for team 1";
//					} else if (sc.getStage() == 4)
//					{
//						if (sc.getSpawnpointTeam2().size() >= 9)
//						{
//							sc.falseCommand(Arrays.asList(ColorOptions.error + "A maximum of 9 Spawnpoints has been reached"));
//							return;
//						}
//						sc.getSpawnpointTeam2().remove(sc.getSpawnpointTeam2().size()-1);
//						undoMessage = ColorOptions.error + "Undone the last spawnpoint for team 2";
//					} else
//					if (sc.getStage() == 6)
//					{
//						sc.getSideObjectives().remove(sc.getSideObjectives().size()-1);
//						undoMessage = ColorOptions.error + "Undone the last side objective";
//					} else
//					{
//						stage -= 1;
//						undoMessage = ColorOptions.error + "Undone the last step";
//					}
//				}
//				creation.nextStage(stage);
//				player.sendMessage(undoMessage);
//			}
		}
		
		/**
		 * Tutorials
		 */
		Tutorial tutorial = Tutorials.getTutorial(player);
		Tutorial intro = Tutorials.getIntroTutorial(player);
		if (tutorial != null || intro != null)
		{
			event.setCancelled(true);
			if (message.equalsIgnoreCase("/menu"))
			{
				if (tutorial.tutorialName.equalsIgnoreCase("room") || tutorial.tutorialName.equalsIgnoreCase("skills"))
				{
					tutorial.nextStage(null, null);
				}
			} else if (message.equalsIgnoreCase("/next") || message.equalsIgnoreCase("/yes") || message.equalsIgnoreCase("/cancel"))
			{
				if (intro != null && tutorial == null)
				{
					intro.TryNext(message.split("/")[1]);
				} 
				if (tutorial != null)
				{
					tutorial.TryNext(message.split("/")[1]);
				}
			} else
			{
				Tutorials.notAllowed(player);
			}
		}
		
		/**
		 * Reload command
		 */
		if (message.equalsIgnoreCase("/reload") || message.equalsIgnoreCase("/rl") || message.equalsIgnoreCase("/rel"))
		{
			if (player.hasPermission("bukkit.reload") || player.hasPermission("k&k.reload") || player.isOp())
			{
				event.setCancelled(true);
				main.reload(player);
			}
		}
		
		/**
		 * Staffchat
		 */
		if (message.equalsIgnoreCase("/sc"))
		{
			event.setMessage("/staffchat");
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(PlayerChatEvent e)
	{
		String message = e.getMessage();
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		try
		{
			user = Users2.FindUser(uuid);
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
		
		/**
		 * Duel events
		 */
		if (DuelSetupClick.coinBetList.containsKey(uuid))
		{
			e.setCancelled(true);
			if (main.isInt(message))
			{
				Integer coins = Integer.valueOf(message);
				if (user.getCoins() >= coins)
				{
					Inventory inv = DuelSetupClick.coinBetList.get(player.getUniqueId());
					if (ChatColor.stripColor(inv.getItem(45).getItemMeta().getDisplayName()).equalsIgnoreCase(player.getName()))
					{
						User target = Users.getUser(Bukkit.getPlayer(ChatColor.stripColor(inv.getItem(53).getItemMeta().getDisplayName())).getUniqueId());
						if (target.getCoins() >= coins)
						{
							inv.setItem(48, product.addCoinBet(inv.getItem(48), coins));
							inv.setItem(50, product.addCoinBet(inv.getItem(50), coins));
						} else
						{
							player.sendMessage(ColorOptions.error + "Your opponent does not have that much coins! Both bets need to be equal!");
						}
					} else if (ChatColor.stripColor(inv.getItem(53).getItemMeta().getDisplayName()).equalsIgnoreCase(player.getName()))
					{
						User target = Users.getUser(Bukkit.getPlayer(ChatColor.stripColor(inv.getItem(48).getItemMeta().getDisplayName())).getUniqueId());
						if (target.getCoins() >= coins)
						{
							inv.setItem(48, product.addCoinBet(inv.getItem(48), coins));
							inv.setItem(50, product.addCoinBet(inv.getItem(50), coins));
						} else
						{
							player.sendMessage(ColorOptions.error + "Your opponent does not have that much coins! Both bets need to be equal!");
						}
					}
					player.openInventory(inv);
					DuelSetupClick.coinBetList.remove(player.getUniqueId());
				} else
				{
					player.sendMessage(ColorOptions.error + "You don't have enough coins!");
					player.sendMessage(ColorOptions.message + "Your coins: " + ColorOptions.formatCurrency(user.getCoins()));
				}
			} else if (message.equalsIgnoreCase("cancel"))
			{
				Inventory inv = DuelSetupClick.coinBetList.get(player.getUniqueId());
				player.openInventory(inv);
				DuelSetupClick.coinBetList.remove(player.getUniqueId());
			} else
			{
				player.sendMessage(ColorOptions.error + "Please specify a number of coins, or to cancel type 'cancel'");
			}
		}
		
		/**
		 * Donator chat
		 */
		try
		{
			String dn = player.getName();
			String rawmessage = ("" + e.getMessage().charAt(0)).toUpperCase() + e.getMessage().substring(1);
			message = ChatColor.translateAlternateColorCodes('&', rawmessage);
			if (player.hasPermission("k&k.owner"))
			{
				e.setFormat(ColorOptions.ownerformat + "[" + ColorOptions.ownersubjects + "OWNER" + ColorOptions.ownerformat + "]-{" + ColorOptions.ownersubjects + "" + ChatColor.BOLD + user.getTitleName() + ColorOptions.ownerformat + "}- " + ColorOptions.ownersubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
			} else if (player.hasPermission("k&k.co-owner"))
			{
				e.setFormat(ColorOptions.ownerformat + "[" + ColorOptions.ownersubjects + "CO-OWNER" + ColorOptions.ownerformat + "]-{" + ColorOptions.ownersubjects + "" + ChatColor.BOLD + user.getTitleName() + ColorOptions.ownerformat + "}- " + ColorOptions.ownersubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
			} else
			if (player.hasPermission("k&k.staff"))
			{
				e.setFormat(ColorOptions.staffformat + "[" + ColorOptions.staffsubjects + "STAFF" + ColorOptions.staffformat + "]-{" + ColorOptions.staffsubjects + "" + ChatColor.BOLD + user.getTitleName() + ColorOptions.staffformat + "}- " + ColorOptions.staffsubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
			} else
			{
				/*
				String dn = player.getName();
				player.setDisplayName(ColorOptions.nobleformat + "[B]-{" + ColorOptions.noblesubjects + "Noble " + user.getTitleName(pu) + ColorOptions.nobleformat + "}- " + ColorOptions.noblesubjects + dn + ChatColor.WHITE);
				 */
				if (user.getDonatorName().equalsIgnoreCase("noble"))
				{
					e.setFormat(ColorOptions.nobleformat + "-{" + ColorOptions.noblesubjects + "Noble " + user.getTitleName() + ColorOptions.nobleformat + "}- " + ColorOptions.noblesubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
				} else
				if (user.getDonatorName().equalsIgnoreCase("royal"))
				{
					e.setFormat(ColorOptions.royalformat + "-{" + ColorOptions.royalsubjects + "Royal " + user.getTitleName() + ColorOptions.royalformat + "}- " + ColorOptions.royalsubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
				} else
				if (user.getDonatorName().equalsIgnoreCase("dragon blood"))
				{
					e.setFormat(ColorOptions.dbformat + "-{" + ColorOptions.dbsubjects + "Dragon Blood " + user.getTitleName() + ColorOptions.dbformat + "}- " + ColorOptions.dbsubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
				} else
				{
					message = rawmessage;
					e.setFormat(ColorOptions.defaultformat + "-{" + ColorOptions.defaultsubjects + user.getTitleName() + ColorOptions.defaultformat + "}- " + ColorOptions.defaultsubjects + dn + ColorOptions.message + ": " + ChatColor.WHITE + message);
				}
			}
		} catch(Exception exception)
		{
			exception.printStackTrace();
		}
		
		/**
		 * Player mention
		 */
		for (User u : Users2.users)
		{
			if (message.toLowerCase().contains(u.getUsername().toLowerCase()))
			{
				if (u != user)
				{
					if (!u.getMentionDelay().keySet().contains(user.getUUID())
							|| u.getMentionDelay().get(user.getUUID()) == 3)
					{
						u.getPlayer().playSound(u.getPlayer().getLocation(), SoundHandler.NOTE_PLING, 1.0F, 1.0F);
						HashMap<UUID, Integer> mentionDelay = u.getMentionDelay();
						mentionDelay.put(uuid, 0);
					} else
					{
						HashMap<UUID, Integer> mentionDelay = u.getMentionDelay();
						mentionDelay.put(uuid, mentionDelay.get(uuid)+1);
					}
				}
			}
		}
		
		/**
		 * House sell confirm
		 */
		if (HouseCommands.sellconfirm.containsKey(uuid))
		{
			Integer houseID = HouseCommands.sellpropertyID.get(uuid);
			Integer price = house.getHousePrice(houseID);
			Integer streetID = house.getStreetID(houseID);
			Integer townID = street.getTownID(streetID);
			Integer streetNumber = house.getHouseNumber(houseID);
			e.setCancelled(true);
			if (e.getMessage().equalsIgnoreCase("yes"))
			{				
				house.RemoveHouseOwner(houseID);
				user.removeHouseAmount(false, 1);
				user.addCoins((price/2));
				RegionManager manager = Worldguard.getRegionManager(player.getWorld());
				house.removeRegionOwner(player, houseID, manager);

				String houseName = house.getHouseName(houseID);
				player.sendMessage(ColorOptions.messageachievement + "-You succesfully sold " + houseName + " on the " + street.getStreetName(streetID) + " with streetnumber " + streetNumber + " in town " + town.getTownName(townID));
				player.sendMessage(ColorOptions.messageachievement + "-You received " + ColorOptions.formatCurrency((price/2)) + ColorOptions.coinStats + " coins");
				
				if (house.getHouseSpawnPoint(houseID) != 0 && user.getSpawnpointID() == house.getHouseSpawnPoint(houseID))
				{
					user.removeSpawnpoint();
					player.sendMessage(ColorOptions.error + "Your personal spawnpoint has been set to default");
				}
			} else if (e.getMessage().equalsIgnoreCase("no"))
			{
				player.sendMessage(ColorOptions.error + "-You canceled selling your house");
			} else
			{
				player.sendMessage(ColorOptions.error + "-You canceled selling your house");
			}
			HouseCommands.sellconfirm.remove(uuid);
			HouseCommands.sellpropertyID.remove(uuid);
		}
		
		/**
		 * Property sell confirm
		 */
		if (PropertyCommands.sellconfirm.containsKey(uuid))
		{
			Integer propertyID = PropertyCommands.sellpropertyID.get(uuid);
			Integer price = property.getPropertyPrice(propertyID);
			Integer streetID = property.getStreetID(propertyID);
			Integer townID = street.getTownID(streetID);
			e.setCancelled(true);
			if (e.getMessage().equalsIgnoreCase("yes"))
			{				
				property.RemovePropertyOwner(propertyID);
				user.addCoins((price/2));
				RegionManager manager = Worldguard.getRegionManager(player.getWorld());
				property.removeRegionOwner(player, propertyID, manager);

				String propertyName = property.getPropertyName(propertyID);
				player.sendMessage(ColorOptions.messageachievement + "-You succesfully sold " + propertyName + " on the " + street.getStreetName(streetID) + " with streetnumber " + property.getStreetNumber(propertyID) + " in town " + town.getTownName(townID));
				player.sendMessage(ColorOptions.messageachievement + "-You received " + ColorOptions.formatCurrency((price/2)) + ColorOptions.coinStats + " coins");
			} else if (e.getMessage().equalsIgnoreCase("no"))
			{
				player.sendMessage(ColorOptions.error + "-You canceled selling your property");
			} else
			{
				player.sendMessage(ColorOptions.error + "-You canceled selling your property");
			}
			PropertyCommands.sellconfirm.remove(uuid);
			PropertyCommands.sellpropertyID.remove(uuid);
		}
		
		/**
		 * Room sell confirm
		 */
		if (RoomCommands.sellconfirm.containsKey(uuid))
		{
			Integer roomID = RoomCommands.sellRoomID.get(uuid);
			Integer propertyID = room.getPropertyID(roomID);
			String propertyName = property.getPropertyName(propertyID);
			Integer streetID = property.getStreetID(propertyID);
			Integer townID = street.getTownID(streetID);
			Integer streetNumber = property.getStreetNumber(propertyID);
			Integer roomNumber = room.getRoomNumber(roomID);
			
			e.setCancelled(true);
			if (e.getMessage().equalsIgnoreCase("yes"))
			{				
				room.sellRoom(user, roomID);
			} else if (e.getMessage().equalsIgnoreCase("no"))
			{
				player.sendMessage(ColorOptions.error + "-You keep renting a room with roomnumber " + roomNumber + " in the tavern " + propertyName + " on the " + street.getStreetName(streetID) + " with streetnumber " + streetNumber + " in town " + town.getTownName(townID));
			} else
			{
				player.sendMessage(ColorOptions.error + "-You keep renting a room with roomnumber " + roomNumber + " in the tavern " + propertyName + " on the " + street.getStreetName(streetID) + " with streetnumber " + streetNumber + " in town " + town.getTownName(townID));
			}
			RoomCommands.sellconfirm.remove(uuid);
			RoomCommands.sellRoomID.remove(uuid);
		}
		
		/**
		 * Tutorials
		 */
		Tutorial tutorial = Tutorials.getTutorial(player);
		Tutorial intro = Tutorials.getIntroTutorial(player);
		if (tutorial != null || intro != null)
		{
			if (tutorial == null)
			{
				tutorial = intro;
			}
			e.setCancelled(true);
			tutorial.TryNext(e.getMessage());
			e.getRecipients().remove(tutorial.target);
		}
		
		/**
		 * Teleportdelay cancel
		 */
		if (e.getMessage().equalsIgnoreCase("cancel"))
		{
			if (Main.teleportconfirm.containsKey(e.getPlayer().getUniqueId()))
			{
				Main.teleportconfirm.remove(e.getPlayer().getUniqueId());
			}
		}
		
		/**
		 * Creation events
		 */
		Creation creation = Creations.FindCreation(user);
		if (creation != null)
		{
			creation.processEvent(e);
		}
		
		/**
		 * Afk removal
		 */
		if (user.isAfk())
		{
			if (user.getAfk().teleporting == false)
			{
				user.removeAfk();
			}
		}
		user.SetAfkCommence((System.currentTimeMillis()+ main.afkTime*1000));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event)
	{
		if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
		{
			event.setDroppedExp(0);
			event.setDeathMessage(null);
			return;
		}
		Main.logMessage("Firing death event for player " + event.getEntity().getDisplayName());
		
		String deathMessage = null;
		Player player = event.getEntity();
		Player killer = player.getKiller();
		Location deathLoc = player.getLocation();
		UUID uuid = player.getUniqueId();
		User user = Users2.InstantiateUser(uuid, false);
		User userKiller = null;
		
		if (user == null)
		{
			ErrorHandlers.userNotFoundAction(null, player, false);
			event.setDroppedExp(0);
			event.setDeathMessage(deathMessage);
			return;
		}
		
		if (killer != null && !CitizensAPI.getNPCRegistry().isNPC(killer))
		{
			userKiller = Users2.InstantiateUser(killer.getUniqueId(), false);
		}
		
		user.addDeaths(1);
		if (Main.incombat.containsKey(user))
		{
			Main.incombat.remove(user);
		}
		Integer coins = user.getCoins()/Main.dropPercentage;
		user.removeCoins(coins);
		user.setLastDeathLocation(deathLoc);
		event.setDroppedExp(0);
		
		if (userKiller != null)
		{
			Integer exp = userKiller.getMultipliedInt(userKiller.getExpPart(5));
			userKiller.addKills(false, 1, 1);
			userKiller.addExperience(exp, true);
			killer.sendMessage(ColorOptions.messageachievement + "You received " + ColorOptions.messagesubjects + ColorOptions.formatCurrency(exp) + ColorOptions.messageachievement + " experience for killing " + ColorOptions.messagesubjects + user.getUsername());	
		}
		
		/**
		 * Avenger skill
		 */
		User avenger = Users.GetAvenger(user);
		if (user.getSpecialSkillName() != null && user.getSpecialSkillName().equalsIgnoreCase("avenger"))
		{
			user.setAvengerTarget(userKiller);
			user.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "Avenger skill activated!");
			
			
		} else if (avenger != null && avenger == userKiller)
		{
			user.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "You have been avenged!");
			userKiller.setAvengerTarget(null);
			killer.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "You have avenged yourself!");
		}
		
		//Minigames
		Siege siege = Sieges.findSiege(user);
		Transport transport = Users.GetTransport(user);
		
		if (siege != null)
		{
			if (userKiller != null)
			{
				user.sendMessage(ColorOptions.message + "You were killed by " + player.getKiller().getName());
			} else
			{
				user.sendMessage(ColorOptions.message + "You died");
			}
			event.setDroppedExp(0);
			event.setDeathMessage(deathMessage);
			return;
		}
		
		if (transport != null)
		{
			if (userKiller != null)
			{
				userKiller.addCoins(transport.getComission());
				killer.sendMessage(ColorOptions.messageachievement + "You succesfully killed " + transport.getPlayer().getName() + " while transporting items!");
				killer.sendMessage(ColorOptions.messageachievement + "You received " + ColorOptions.formatCurrency(transport.getComission()) + " coins!");
				transport.failed(ColorOptions.error + "Player " + transport.getPlayer().getName() + " got killed by " + event.getEntity().getKiller().getName() + " while transporting items!", true);
				event.setDeathMessage(deathMessage);
			} else
			{
				if (transport != null)
				{
					transport.failed(ColorOptions.error + "Player " + transport.getPlayer().getName() + " got killed while transporting items!", true);
				}
				for (ItemStack item : player.getInventory().getContents())
				{
					if (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "personal menu"))
					{
						user.addKeepItems(item);
					}
				}
			}
			return;
		}
		
		//Normal
		if (userKiller != null)
		{
			Integer blockamount = coins/10000;
			Integer rest = (int) coins%10000;
			user.setLastDeathLocation(deathLoc);
			
			user.removeCoins(coins);
			
			event.getDrops().add(product.createAmountItem(Material.GOLD_INGOT, blockamount, ColorOptions.message + player.getName() + "'s coins", "Coins: 10.000"));
			event.getDrops().add(product.createAmountItem(Material.GOLD_INGOT, 1, ColorOptions.message + player.getName() + "'s coins", "Coins: " + ColorOptions.formatCurrency(rest)));
			player.sendMessage(ColorOptions.message + "You dropped " + ColorOptions.formatCurrency(coins) + " coins when killed by " + player.getKiller().getName());
		}
		
		if (!Main.combatlogged.contains(uuid))
		{
			List<ItemStack> removable = new ArrayList<ItemStack>();
			List<ItemStack> keep = new ArrayList<ItemStack>();
			for (ItemStack content : event.getDrops())
			{
				if (content.hasItemMeta() && content.getItemMeta().hasDisplayName())
				{
					String display = content.getItemMeta().getDisplayName();
					List<String> lore = content.getItemMeta().getLore();
					Integer productID = product.getProductIDbyDisplayName(display, false);
					if ((lore != null) && (lore.contains(ChatColor.RED + "Soulbound"))) 
		  			{
		  				removable.add(content);
		  			} else 
	  				if ((lore != null) && (lore.contains(ChatColor.DARK_GRAY + "Ghosted")))
		  			{
		  				keep.add(content);
		  			} else
					if (productID != null)
					{
						Integer grade = product.getGrade(productID, false);
						if (grade > 3)
						{
							keep.add(content);
						}
					} else
					{
						removable.add(content);
					}
				} else
				{
					removable.add(content);
				}
			}
			event.getDrops().removeAll(removable);
			event.getDrops().removeAll(keep);
			
			user.setKeepItems(keep);
		}
		
		event.setDroppedExp(0);
		event.setDeathMessage(deathMessage);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void respawn(PlayerRespawnEvent e)
	{
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		if (!CitizensAPI.getNPCRegistry().isNPC(player))
		{
			try
			{
				user = Users.getUser(uuid);
			} catch (UserNotFoundException ex)
			{
				ErrorHandlers.userNotFoundAction(null, player, false);
				return;
			} catch (UserIsNpcException ex)
			{
				return;
			} catch (Exception ex)
			{
				return;
			}
			
			/**
			 * Hide and seek minigame
			 */
			HideAndSeek hs = HideandSeeks.findHideAndSeek(user);
			if (hs != null && hs.getProgress())
			{
				Integer townID = hs.getTownID();
				
				try
				{
					e.setRespawnLocation(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointIDbyTown(townID)));
				} catch (Exception ex)
				{
					e.setRespawnLocation(user.getLastDeathLocation());
				}
				return;
			}
			
			/**
			 * Siege minigame
			 */
			Siege siege = Sieges.findSiege(user);
			if (siege != null && siege.getProgress())
			{
				Menu menu = new Menu();
				SiegeMember member = siege.getSiegeMember(user);
				e.setRespawnLocation(member.GetTeam().getSpawnpoints().get(0).getLocation());
				new BukkitRunnable()
				{
					public void run()
					{
						menu.openSiegeSpawnpointMenu(member.getUser(), siege);
					}
				}.runTaskLaterAsynchronously(main, 2*20);
				return;
			}
			
			/**
			 * Transport minigame
			 */
			Transport transport = Users.GetTransport(user);
			if (transport != null)
			{
				transport.cancel(ColorOptions.error + "You failed to transport the items!");
			}
			
			if (user.getSpawnpointID() != 1)
			{
				if (!player.hasPermission("k&k.join.nolocation") || !player.isOp())
				{
					if (spawnpoint.getSpawnPointID("spawn") != null)
					{
						if (spawnpoint.getSpawnPointName(user.getSpawnpointID()) != "spawn")
						{
							e.setRespawnLocation(spawnpoint.getSpawnPointLocation(user.getSpawnpointID()));
						} else
						{
							e.setRespawnLocation(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("spawn")));
						}
					} else if (player.isOp())
					{
						player.sendMessage(ColorOptions.falsecommand + "No spawn-location has been set! This might cause glitches or errors");
					}
				} else
				{
					if (spawnpoint.getSpawnPointID("spawn") != null)
					{
						if (spawnpoint.getSpawnPointName(user.getSpawnpointID()) != "spawn")
						{
							e.setRespawnLocation(spawnpoint.getSpawnPointLocation(user.getSpawnpointID()));
						} else
						{
							e.setRespawnLocation(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("spawn")));
						}
					} else
					{
						player.sendMessage(ColorOptions.falsecommand + "No spawn-location has been set! This might cause glitches or errors");
					}
				}
			} else
			{
				e.setRespawnLocation(spawnpoint.getSpawnPointLocation(user.getSpawnpointID())); 	
			}
			
			for (ItemStack item : user.getKeepItems())
			{
				player.getInventory().addItem(item);
			}
			user.resetKeepItems();
			
			/**
			 * Managing health scale for Health skill
			 */
			Integer healthLevel = user.getHealthID();
			Integer health = skill.getSkillValue("Health", healthLevel);
			if (healthLevel < 7)
			{
				player.setHealthScale(20 +health);
			} else
			{
				player.setHealthScale(32);

			}
			
			/**
			 * Managing speed for Speed skill
			 */
			Integer speedlevel =  user.getSpeedID();
			if (speedlevel == 1)
			{
	 			player.setWalkSpeed(0.22F);
			} else if (speedlevel == 2)
			{
	 			player.setWalkSpeed(0.24F);
			} else if (speedlevel == 3)
			{
	 			player.setWalkSpeed(0.26F);
			} else if (speedlevel == 4)
			{
	 			player.setWalkSpeed(0.28F);
			} else if (speedlevel == 5)
			{
	 			player.setWalkSpeed(0.30F);
			} else if (speedlevel == 6)
			{
	 			player.setWalkSpeed(0.32F);
			} else if (speedlevel == 7)
			{
	 			player.setWalkSpeed(0.32F);
			}
			
			/**
			 * Avenger skill
			 */
			User avengerTarget = user.getAvengerTarget();
			if (avengerTarget != null)
			{
				if (Users2.users.contains(avengerTarget))
				{
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "You have +50% attack damage against " + avengerTarget.getUsername());
				} else
				{
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ColorOptions.Avenger + "Avenger" + ChatColor.GRAY + "]" + ColorOptions.Avenger + "Your killer is not online anymore!");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = Users2.InstantiateUser(uuid, false);
		
		if (user == null)
		{
			return;
		}
		
		
		/**
		 * Creation events
		 */
		Creation creation = Creations.FindCreation(user);
		if (creation != null)
		{
			creation.processEvent(event);
		}
	}
}
