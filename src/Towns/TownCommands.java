package Towns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

import API_methods.WorldEdit;
import DataManager.Worldguard;
import Donator.Donator;
import Handlers.ColorOptions;
import Houses.House;
import Main.Main;
import SpawnPoints.SpawnPoint;
import Titles.Title;

public class TownCommands implements CommandExecutor
{
	Donator donator = new Donator();
	SpawnPoint spawnpoint = new SpawnPoint();
	Title title = new Title();
	House house = new House();
	Town town = new Town();
	WorldEdit worldedit = new WorldEdit();
	public Main main;
	public TownCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of town-commands",
			ColorOptions.stats + "-/town list",
			ColorOptions.stats + "-/town info <name>",
			ColorOptions.statsbrackets
	});
	
	public List<String> consolecommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of town-commands",
			ColorOptions.stats + "-/town list",
			ColorOptions.stats + "-/town info <name/id>",
			ColorOptions.statsbrackets
	});
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of town-commands",
			ColorOptions.stats + "-/town create <name> <requiredtitle> <description>",
			ColorOptions.stats + "NOTE: set requiredtitle to serf if none is required",
			ColorOptions.stats + "-/town remove <name/id>",
			ColorOptions.stats + "-/town part add <name/id>",
			ColorOptions.stats + "-/town part remove <name/id> <part-id>",
			ColorOptions.stats + "-/town info <name/id>",
			ColorOptions.stats + "-/town purge <id/all>",
			ColorOptions.stats + "-/town list",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("town"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				if (player.hasPermission("k&k.town"))
				{
					if (args.length > 0)
					{
						//Get the selection of worldedit a user has made to register as a house
						Selection selection = worldedit.getWorldEdit().getSelection(player);
	                    RegionManager manager = Worldguard.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
	                    
						if (args[0].equalsIgnoreCase("create"))
						{
							//Command: /town create(0) name(1) titleID(2) description(3-end)
							if (args.length > 4)
							{
								if (selection != null)
								{
									String description = main.stringBuilder(args, 3, args.length);
									if (main.isInt(args[2]))
									{
										if (title.getIDList().contains(Integer.valueOf(args[2])))
										{
											Integer requiredTitleID = Integer.valueOf(args[2]);
											Location max = selection.getMaximumPoint();
						                    Location min = selection.getMinimumPoint();
						                    ApplicableRegionSet regionsmax = manager.getApplicableRegions(max); 
						                    ApplicableRegionSet regionsmin = manager.getApplicableRegions(min); 
											if (town.checkTownbyLocation(player.getLocation()) == false)
											{
												if (checkTownRegion(regionsmax) == false && checkTownRegion(regionsmin) == false)
												{
													createTown(player, args[1], requiredTitleID, description, manager, selection);
												} else
												{
													player.sendMessage(ColorOptions.error + "You are overlapping another town, canceled creation.");
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "You are already in an existing town!");
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No title can be found with id " + args[2]);
										}
									} else
									{
										if (title.getTitleID(args[2]) != null)
										{
											Integer requiredTitleID = title.getTitleID(args[2]);
											Location max = selection.getMaximumPoint();
						                    Location min = selection.getMinimumPoint();
						                    ApplicableRegionSet regionsmax = manager.getApplicableRegions(max); 
						                    ApplicableRegionSet regionsmin = manager.getApplicableRegions(min); 
											if (town.checkTownbyLocation(player.getLocation()) == false)
											{
												if (checkTownRegion(regionsmax) == false && checkTownRegion(regionsmin) == false)
												{
													createTown(player, args[1], requiredTitleID, description, manager, selection);
												} else
												{
													player.sendMessage(ColorOptions.error + "You are overlapping another town, canceled creation.");
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "You are already in an existing town!");
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No title can be found named " + args[2]);
										}
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "You need to make a WorldEdit selection first!");
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /town create <name> <requiredtitle> <description>");
							}
						} else
						if (args[0].equalsIgnoreCase("remove"))
						{
							if (args.length == 2)
							{
								if (main.isInt(args[1]))
								{
									if (town.getTownIDList().contains(Integer.valueOf(args[1])))
									{
										Integer townID = Integer.valueOf(args[1]);
										removeTown(player, townID, manager);
									} else
									{
										player.sendMessage(ColorOptions.error + "No town can be found with ID " + args[1]);
									}
								} else
								{
									if (town.getTownID(args[1]) != null)
									{
										Integer townID = town.getTownID(args[1]);
										removeTown(player, townID, manager);
									} else
									{
										player.sendMessage(ColorOptions.error + "No town can be found with name " + args[1]);
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /town remove <name/id>");
							}
						} else
						if (args[0].equalsIgnoreCase("part"))
						{
							if (args.length >= 3)
							{
								if (args[1].equalsIgnoreCase("add"))
								{
									if (args.length == 3)
									{
										if (selection != null)
										{
											Location max = selection.getMaximumPoint();
						                    Location min = selection.getMinimumPoint();
						                    ApplicableRegionSet regionsmax = manager.getApplicableRegions(max); 
						                    ApplicableRegionSet regionsmin = manager.getApplicableRegions(min); 
						                    if (main.isInt(args[2]))
											{
												if (town.getTownIDList().contains(Integer.valueOf(args[2])))
												{
													Integer townID = Integer.valueOf(args[2]);
													if (checkSameTownRegion(regionsmax, townID) == true && checkSameTownRegion(regionsmin, townID) == true)
													{
														addTownRegion(player, townID, manager, selection);
													} else
													{
														player.sendMessage(ColorOptions.error + "You are overlapping another town, canceled creation.");
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "No town can be found with ID " + args[2]);
												}
											} else
											{
												if (town.getTownID(args[2]) != null)
												{
													Integer townID = town.getTownID(args[2]);
													if (checkSameTownRegion(regionsmax, townID) == true && checkSameTownRegion(regionsmin, townID) == true)
													{
														addTownRegion(player, townID, manager, selection);
													} else
													{
														player.sendMessage(ColorOptions.error + "You are overlapping another town, canceled creation.");
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "No town can be found with name " + args[2]);
												}
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "You need to make a WorldEdit selection first!");
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "Usage: /town part add <name/id>");
									}
								} else
								if (args[1].equalsIgnoreCase("remove"))
								{
									if (args.length == 4)
									{
										if (main.isInt(args[3]))
										{
											Integer partID = Integer.valueOf(args[3]);
											if (main.isInt(args[2]))
											{
												if (town.checkTown(Integer.valueOf(args[2])))
												{
													Integer townID = Integer.valueOf(args[2]);
													if (town.checkPartID(townID, partID))
													{
														removeTownRegion(player, townID, partID, manager);
													} else
													{
														player.sendMessage(ColorOptions.error + "No part of the town with ID " + townID + " can be found with partID " + partID);
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "No town can be found with ID " + args[2]);
												}
											} else
											{
												if (town.getTownID(args[2]) != null)
												{
													Integer townID = town.getTownID(args[2]);
													if (town.checkPartID(townID, partID))
													{
														removeTownRegion(player, townID, partID, manager);
													} else
													{
														player.sendMessage(ColorOptions.error + "No part of the town with ID " + townID + " can be found with partID " + partID);
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "No town can be found named " + args[2]);
												}
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "The part ID needs to be a number: " + args[2]);
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage: /town part remove <name/id> <part-id>");
									}
								} else
								{
									for (String message : staffcommandhelp)
									{
										player.sendMessage(message);
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /town part <add/remove>");
							}
						} else if (args[0].equalsIgnoreCase("purge"))
						{
							if (args.length != 2)
							{
								for (String message : staffcommandhelp)
								{
									player.sendMessage(message);
								}
								return false;
							}
							
							if (args[1].equalsIgnoreCase("all"))
							{
								for (Integer townID : this.town.getTownIDList())
								{
									this.purgeTown(player, townID);
								}
							} else
							{
								if (!main.isInt(args[1]))
								{
									for (String message : staffcommandhelp)
									{
										player.sendMessage(message);
									}
									return false;
								}
								
								Integer townID = Integer.valueOf(args[1]);
								
								if (!this.town.getTownIDList().contains(townID))
								{
									for (String message : staffcommandhelp)
									{
										player.sendMessage(message);
									}
									return false;
								}
								
								this.purgeTown(player, townID);
							}
						} else
						{
							for (String message : staffcommandhelp)
							{
								player.sendMessage(message);
							}
						}
					} else
					{
						for (String message : staffcommandhelp)
						{
							player.sendMessage(message);
						}
					}
				}
				if (args.length >= 1)
				{
					if (args[0].equalsIgnoreCase("list"))
					{
						townList(player);
					} else
					if (args[0].equalsIgnoreCase("info"))
					{
						if (args.length == 2)
						{
							if (main.isInt(args[1]))
							{
								if (town.checkTown(Integer.valueOf(args[1])))
								{
									Integer townID = Integer.valueOf(args[1]);
									townInfo(sender, townID);

								} else
								{
									sender.sendMessage(ColorOptions.error + "No town could be found with ID " + args[1]);
								}
							} else
							{
								if (town.getTownID(args[1]) != null)
								{
									Integer townID = town.getTownID(args[1]);
									townInfo(sender, townID);
								} else
								{
									sender.sendMessage(ColorOptions.error + "No town could be found named " + args[1]);
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.falsecommand + "Usage: /town info <name/id>");
						}
					}
				} else
				{
					if (player.hasPermission("k&k.town"))
					{
						for (String message : staffcommandhelp)
						{
							player.sendMessage(message);
						}
					} else
					{
						for (String message : commandhelp)
						{
							player.sendMessage(message);
						}
					}
				}
			} else
			{
				if (args.length >= 1)
				{
					if (args[0].equalsIgnoreCase("list"))
					{
						sender.sendMessage(ColorOptions.statsformat + "=================================================");
						sender.sendMessage(ColorOptions.statsformat + "List of towns:");
						for (Integer townID : town.getTownIDList())
						{
							sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
							String townName = town.getTownName(townID);
							String description = town.getTownDescription(townID);
							sender.sendMessage(ColorOptions.stats + "-Name: " + townName);
							sender.sendMessage(ColorOptions.stats + "-ID: " + townID);
							if (spawnpoint.getSpawnPointIDbyTown(townID) != null)
							{
								sender.sendMessage(ColorOptions.stats + "-Spawnpoint: " + ChatColor.GREEN + "yes");
							} else
							{
								sender.sendMessage(ColorOptions.stats + "-Spawnpoint: " + ChatColor.RED + "no");
							}
							sender.sendMessage(ColorOptions.stats + "-Description: " + description);
							
							sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

						}
						sender.sendMessage(ColorOptions.statsformat + "=================================================");	
					} else
					if (args[0].equalsIgnoreCase("info"))
					{
						if (args.length == 2)
						{
							if (main.isInt(args[1]))
							{
								if (town.checkTown(Integer.valueOf(args[1])))
								{
									Integer townID = Integer.valueOf(args[1]);
									townInfo(sender, townID);

								} else
								{
									sender.sendMessage(ColorOptions.error + "No town could be found with ID " + args[1]);
								}
							} else
							{
								if (town.getTownID(args[1]) != null)
								{
									Integer townID = town.getTownID(args[1]);
									townInfo(sender, townID);
								} else
								{
									sender.sendMessage(ColorOptions.error + "No town could be found named " + args[1]);
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.falsecommand + "Usage: /town info <name/id>");
						}
					} else
					{
						for (String message : consolecommandhelp)
						{
							sender.sendMessage(message);
						}
					}
				} else
				{
					for (String message : consolecommandhelp)
					{
						sender.sendMessage(message);
					}
				}
			}
		}
		return false;
	}
	
	public boolean checkTownRegion(ApplicableRegionSet regions)
	{
		boolean city = false;
		
		for (ProtectedRegion region : regions)
		{
			if (region.getId().contains("city"))
			{
				city = true;
				break;
			}
		}
		
		return city;
	}
	
	public boolean checkSameTownRegion(ApplicableRegionSet regions, Integer townID)
	{
		boolean city = true;
		
		for (ProtectedRegion region : regions)
		{
			if (region.getId().contains("city"))
			{
				String[] split = region.getId().split("_");
				Integer id = Integer.valueOf(split[1]);
				if (id == townID)
				{
					return city;
				} else
				{
					city = false;
					break;
				}
			}
		}
		
		return city;
	}
	
	public void createTown(Player sender, String name, Integer requiredTitleID, String description, RegionManager regionManager, Selection worldEditSelection)
	{
		for (String t : town.getTownNameList())
		{
			if (t.equalsIgnoreCase(name))
			{
				sender.sendMessage(ColorOptions.error + "Thr given town-name is already used by another town!");
				return;
			}
		}
		
		//Save the new town to the database
		town.saveTown(name, requiredTitleID, description);
		
		//Get the town ID of the new town from the database
		Integer townID = town.getTownID(name);
		
		//Create the actual wordguard gateRegion
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				"town_" + townID,
				new BlockVector(worldEditSelection.getNativeMinimumPoint()),
				new BlockVector(worldEditSelection.getNativeMaximumPoint())
				);
		regionManager.addRegion(region);
		
		region.setFlag(DefaultFlag.ENTRY, State.ALLOW);
		region.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY); 
		region.setFlag(DefaultFlag.PVP, State.DENY);
		region.setFlag(DefaultFlag.DAMAGE_ANIMALS, State.ALLOW);
		region.setFlag(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, State.DENY);
		region.setFlag(DefaultFlag.DENY_MESSAGE, "");
		region.setPriority(Integer.valueOf(8));
		RegionGroupFlag entryFlag = DefaultFlag.ENTRY.getRegionGroupFlag();
		try 
		{
			entryFlag.parseInput(Worldguard.getWorldGuard(), null, "non_members");
		} catch (InvalidFlagFormat e) 
		{
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		sender.sendMessage(ColorOptions.messageachievement + "Succesfully created a town called " + ColorOptions.messagesubjects + name + ColorOptions.messageachievement + " with ID " + ColorOptions.messagesubjects + townID + ColorOptions.messageachievement + " and required title " + ColorOptions.messagesubjects + title.getTitleName(requiredTitleID, 1));

	}
	
	public void removeTown(Player sender, Integer townID, RegionManager manager)
	{
		String townName = town.getTownName(townID);
		
		if (spawnpoint.getSpawnPointIDbyTown(townID) != null)
		{
			Integer spawnpointID = spawnpoint.getSpawnPointIDbyTown(townID);
			spawnpoint.removeSpawnPoint(spawnpointID);
			spawnpoint.removeTownSpawnPoint(spawnpointID);
		}
		
		for (Integer subID : town.getTownPartList(townID))
		{
			manager.removeRegion("town_" + townID + "," + subID);
			town.removeTownPart(townID, subID);
		}
		
		manager.removeRegion("town_" + townID);
		town.removeTownbyID(townID);
		
    	sender.sendMessage(ColorOptions.messageformat + "The town with ID " + ColorOptions.messagesubjects + townID + ColorOptions.messageformat + " and name " + ColorOptions.messagesubjects + townName + ColorOptions.messageformat + " has succesfully been removed");

	}
	
	public void addTownRegion(Player sender, Integer townID, RegionManager manager, Selection selection)
	{
		town.saveTownPart(townID);
		Integer partID = null;
		ArrayList<Integer> partList = town.getTownPartList(townID);
		
		//Check for every partID if there is an existing gateRegion, if not, that id is the new partID
		for (Integer id : partList)
		{
			if (!manager.getRegions().containsKey("town_" + townID + "," + id))
			{
				partID = id;
				break;
			}
		}
		
		//Create the actual WorldGuard gateRegion
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                "town_" + townID + "," + partID,
                new BlockVector(selection.getNativeMinimumPoint()),
                new BlockVector(selection.getNativeMaximumPoint())
				);
		//manager.addRegion(gateRegion);
		
		//Try to set the house gateRegion as parent of the sub-gateRegion
		try 
		{
			region.setParent(manager.getRegion("town_" + townID));
		} catch (CircularInheritanceException e) 
		{
			e.printStackTrace();
		}
		
		sender.sendMessage(ColorOptions.messageformat + "Succesfully added a part to the town of " + ColorOptions.messagesubjects + town.getTownName(townID));

	}
	
	public void removeTownRegion(Player sender, Integer townID, Integer partID, RegionManager manager)
	{
		String townName = town.getTownName(townID);
		
		manager.removeRegion("town_" + townID + "," + partID);
		town.removeTownPart(townID, partID);
		
		sender.sendMessage(ColorOptions.messageachievement + "Removed a part of town " + ColorOptions.messagesubjects + townName + ColorOptions.messageachievement + " with ID " + ColorOptions.messagesubjects + partID);
	}
	
	public void townList(Player sender)
	{
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of towns:");
		for (Integer townID : town.getTownIDList())
		{
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			String townName = town.getTownName(townID);
			String description = town.getTownDescription(townID);
			sender.sendMessage(ColorOptions.stats + "-Name: " + townName);
			if (sender.isOp() || sender.hasPermission("k&k.town") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-ID: " + townID);
			}
			if (spawnpoint.getSpawnPointIDbyTown(townID) != null)
			{
				sender.sendMessage(ColorOptions.stats + "-Spawnpoint: " + ChatColor.GREEN + "yes");
			} else
			{
				sender.sendMessage(ColorOptions.stats + "-Spawnpoint: " + ChatColor.RED + "no");
			}
			sender.sendMessage(ColorOptions.stats + "-Description: " + description);
			
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
	
	public void townInfo(CommandSender sender, Integer townID)
	{
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "Information about " + town.getTownName(townID));
		if (sender.isOp() || sender.hasPermission("k&k.town"))
		{
			sender.sendMessage(ColorOptions.stats + "-ID: " + townID);
		}
		if (spawnpoint.getSpawnPointIDbyTown(townID) != null)
		{
			sender.sendMessage(ColorOptions.stats + "-Spawnpoint: " + ChatColor.GREEN + "yes");
		} else
		{
			sender.sendMessage(ColorOptions.stats + "-Spawnpoint: " + ChatColor.RED + "no");
		}
		sender.sendMessage(ColorOptions.stats + "-Description: " + town.getTownDescription(townID));
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		
	}
	
	public void purgeTown(CommandSender sender, Integer townID)
	{
		if (townID == this.town.getTownID("wilderness"))
		{
			return;
		}
		sender.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Purging command not available yet");
		RegionManager manager = Worldguard.getRegionManager(Bukkit.getWorld("world"));
		String townName = this.town.getTownName(townID);
		ProtectedRegion region = manager.getRegion("town_" + townID);
		
		try
		{
			region.setFlag(DefaultFlag.PVP, null);
		} catch (Exception ex)
		{
			region.setFlag(DefaultFlag.PVP, State.ALLOW);
			ex.printStackTrace();
		}
//		String townName = this.town.getTownName(townID);
//		sender.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Purging " + townName + "...");
//		Bukkit.getConsoleSender().sendMessage("Trying to purge town " + townName + "...");
//		RegionManager manager = this.Worldguard.getRegionManager(Bukkit.getWorld("world"));
//
//		List<Integer> partList = town.getTownPartList(townID);
//		
//		for (String regionName : manager.getRegions().keySet())
//		{
//			if (regionName.contains("town_" + townID + ","))
//			{
//				Integer partID = Integer.valueOf(regionName.split("town_" + townID + ",")[1]);
//				if (!partList.contains(partID))
//				{
//					partList.add(partID);
//				}
//			}
//		}
//		
//		if (!partList.isEmpty())
//		{
//			boolean noErrors = true;
//			Integer errors = 0;
//			Bukkit.getConsoleSender().sendMessage("Town still has child-regions, commencing removal..");
//			sender.sendMessage(ColorOptions.message + "Town still has child-regions, commencing removal..");
//			for (Integer partID : partList)
//			{
//				Bukkit.getConsoleSender().sendMessage("Trying to remove child-gateRegion with ID " + townID + "," + partID);
//				sender.sendMessage(ColorOptions.message + "Trying to remove child-gateRegion with ID " + townID + "," + partID);
//				try
//				{
//					manager.removeRegion("town_" + townID + "," + partID, RemovalStrategy.REMOVE_CHILDREN);
//					this.town.removeTownPart(townID, partID);
//				} catch (Exception ex)
//				{
//					noErrors = false;
//					errors++;
//					Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Removal of child-gateRegion with ID " + townID + "," + partID + " failed");
//					sender.sendMessage(ColorOptions.error + "Removal of child-gateRegion with ID " + townID + "," + partID + " failed");
//					ex.printStackTrace();
//				}
//			}
//			if (noErrors)
//			{
//				Bukkit.getConsoleSender().sendMessage(ColorOptions.messageachievement + "Succesfully removed all child-regions of town " + townName);
//				sender.sendMessage(ColorOptions.messageachievement + "Succesfully removed all child-regions of town " + townName);
//			} else
//			{
//				Bukkit.getConsoleSender().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Removed child-regions of town " + townName + " with " + errors + " erros");
//				sender.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Removed child-regions of town " + townName + " with " + errors + " erros");
//			}
//		} else
//		{
//			sender.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Nothing to purge for town " + townName);
//			Bukkit.getConsoleSender().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Nothing to purge for town " + townName);
//		}
	}
}
