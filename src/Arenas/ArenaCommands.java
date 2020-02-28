package Arenas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

import API_methods.WorldEdit;
import API_methods.WorldGuard;
import Handlers.ColorOptions;
import Main.Main;
import Regions.Region;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Towns.Town;

public class ArenaCommands implements CommandExecutor
{
	Street street = new Street();
	Arena arena = new Arena();
	Town town = new Town();
	WorldGuard worldguard = new WorldGuard();
	WorldEdit worldedit = new WorldEdit();
	Region region = new Region();
	SpawnPoint spawnpoint = new SpawnPoint();
	public Main main;
	public ArenaCommands(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of arena-commands",
			ColorOptions.stats + "-/arena create <arenaName> <streetName> <townName> <streetNumber>",
			ColorOptions.stats + "-/arena remove <arenaName>",
			ColorOptions.stats + "-/arena part add <arenaName> optional: if you nee to specify the battleground, add <battleground> to the command",
			ColorOptions.stats + "-/arena part remove <arenaName> <partID>",
			ColorOptions.stats + "-/arena location set <locationName>",
			ColorOptions.stats + "-/arena location remove <locationName> <arenaName>",
			ColorOptions.stats + "-/arena npc refresh <arenaID>",
			ColorOptions.stats + "-/arena list",
			ColorOptions.statsbrackets
	});
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of arena-commands",
			ColorOptions.stats + "-/arena list",
			ColorOptions.statsbrackets
	});
	public static List<String> consolecommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of arena-commands",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("arena"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
                RegionManager manager = worldguard.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
				if (player.hasPermission("k&k.arena"))
				{
					if (args.length > 0)
					{
						//Get the selection of worldedit a user has made to register as a house
						Selection selection = worldedit.getWorldEdit().getSelection(player);
	                    if (args[0].equalsIgnoreCase("create"))
	                    {
	                    	if (args.length == 5)
	                    	{
	                    		String name = args[1];
	                    		String streetName = args[2];
	                    		String townName = args[3];
	                    		
	                    		if (selection != null)
	                    		{
				                    if (main.isInt(args[4]))
				                    {
				                    	Integer streetNumber = Integer.valueOf(args[4]);
				                    	if (town.getTownID(townName) != null)
				                    	{
				                    		Integer townID = town.getTownID(townName);
				                    		if (street.checkStreet(streetName, townID))
				                    		{
				                    			Integer streetID = street.getStreetID(streetName, townID);
				                    			if (arena.checkStreetNumber(streetNumber, streetID))
				                    			{
				                    				//The region will be created with the worldGuard API, this is nessecairy to check if there are any intersecting regions
				                    				ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				                    						"arena",
				                    						new BlockVector(selection.getNativeMinimumPoint()),
				                    						new BlockVector(selection.getNativeMaximumPoint())
				                    						);
				                    				if (this.region.checkUniqueRegion(manager, region, "arena"))
				                    				{
			                    						createArena(player, name, townID, streetID, streetNumber, manager, selection);
				                    				} else
				                    				{
				                    					player.sendMessage(ColorOptions.error + "You tried to overlap a different arena region!");
				                    				}
				                    			} else
				                    			{
				                    				player.sendMessage(ColorOptions.error + "You tried to use a street-number that is already in use!");
				                    			}
				                    		} else
				                    		{
				                    			player.sendMessage(ColorOptions.error + "No street could be found named " + streetName + " in the town of " + town.getTownName(townID));
				                    		}
				                    	} else
				                    	{
				                    		player.sendMessage(ColorOptions.error + "No town could be found named " + townName);
				                    	}
				                    } else
				                    {
				                    	player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: streetnumber: " + args[4] + ", income: " + args[6] + ", price: " + args[7] + " and contribution: " + args[8]);
				                    }
	                    		} else
	                    		{
	                    			player.sendMessage(ColorOptions.error + "You need to make a WorldEdit-selection first");
	                    		}
	                    	} else
	                    	{
	                    		player.sendMessage(ColorOptions.falsecommand + "Usage: /arena create <name> <streetName> <townName> <streetNumber> <category> <income> <price> <contribution(" + main.contribution + ")>");
	                    	}
	                    } else
	                    if (args[0].equalsIgnoreCase("remove"))
	                    {
	                    	if (args.length == 2 && !main.isInt(args[1]))
							{
								String arenaName = args[1];
								if (arena.getArenaID(arenaName) != null)
								{
									Integer arenaID = arena.getArenaID(arenaName);
									try {
										removeArena(player, arenaID, manager);
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "There is no arena named " + arenaName + " on this continent");
								}
							} else
							if (args.length == 2)
							{
								if (main.isInt(args[1]))
								{
									Integer arenaID = Integer.valueOf(args[1]);
									if (arena.getArenaIDList(null).contains(arenaID))
									{
										try {
											removeArena(player, arenaID, manager);
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "There is no arena with ID: " + arenaID);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "Error: arena ID needs to be a number: " + args[1]);
									player.sendMessage(ColorOptions.falsecommand + "Usage: /arena remove <arena-name> OR");
									player.sendMessage(ColorOptions.falsecommand + "/arena remove <arenaID>");
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /arena remove <arena-name> OR");
								player.sendMessage(ColorOptions.falsecommand + "/arena remove <arenaID>");
							}
	                    } else
	                    if (args[0].equalsIgnoreCase("part"))
	                    {
	                    	if (args.length >= 3)
	                    	{
	                    		if (args[1].equalsIgnoreCase("add"))
	                    		{
	                    			if (args.length >= 3)
	                    			{
	                    				if (main.isInt(args[2]))
	                    				{
	                    					if (selection != null)
	                    					{
	                    						if (arena.getArenaIDList(null).contains(Integer.valueOf(args[2])))
	                    						{
	                    							Integer arenaID = Integer.valueOf(args[2]);
	                    							//The region will be created with the worldGuard API, this is nessecairy to check if there are any intersecting regions
				                    				ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				                    						"arena",
				                    						new BlockVector(selection.getNativeMinimumPoint()),
				                    						new BlockVector(selection.getNativeMaximumPoint())
				                    						);
				                    				if (this.region.checkSameRegionID(manager, region, "arena", arenaID))
				                    				{
				                    					boolean battleground = false;
				                    					if (args.length == 4 && args[3].equalsIgnoreCase("battleground"))
				                    					{
				                    						battleground = true;
				                    					}
				                    					try {
															this.addArenaRegion(player, battleground, arenaID, manager, selection);
														} catch (SQLException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
				                    				} else
				                    				{
				                    					player.sendMessage(ColorOptions.error + "You tried to overlap a different arena-region!");
				                    				}
	                    						} else
	                    						{
	                    							player.sendMessage(ColorOptions.error + "No arena could be found with ID " + args[2]);
	                    						}
	                    					} else
	                    					{
	                    						player.sendMessage(ColorOptions.error + "You need to make a selection first!");
	                    					}
	                    				} else
	                    				{
	                    					player.sendMessage(ColorOptions.falsecommand + "The following command arguments need to be numbers: <arenaID> " + args[2]);
	                    				}
	                    			} else
	                    			{
	                    				player.sendMessage(ColorOptions.falsecommand + "Usage: /arena part add <arenaID> <battleground>(add battleground if the region is a battleground)");
	                    			}
	                    		} else
	                    		if (args[1].equalsIgnoreCase("remove"))
	                    		{
	                    			if (args.length == 4)
	                    			{
	                    				if (main.isInt(args[3]))
	                    				{
                    						String arenaName = args[2];
                    						if (arena.getArenaID(arenaName) != null)
                    						{
                    							Integer arenaID = arena.getArenaID(arenaName);
	                    						if (arena.checkPartID(arenaID, Integer.valueOf(args[3])))
	                    						{
	                    							Integer partID = Integer.valueOf(args[3]);
	                    							try {
														this.removeArenaRegion(player, arenaID, partID, manager);
													} catch (SQLException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
	                    							player.sendMessage(ColorOptions.messageachievement + "You succesfully remove a part with ID " + partID + " from arena named " + arenaName);
	                    						} else
	                    						{
	                    							player.sendMessage(ColorOptions.error + "No part of arena with ID " + arenaID + " could be found with part-ID " + args[3]);
	                    						}
                    						} else
                    						{
                    							player.sendMessage(ColorOptions.error + "No arena could be found named " + arenaName);
                    						}
	                    				} else
	                    				{
	                    					player.sendMessage(ColorOptions.falsecommand + "The following command arguments need to be numbers: partID: " + args[3]);
	                    				}
	                    			} else
	                    			{
	                    				player.sendMessage(ColorOptions.falsecommand + "Usage: /arena part remove <arenaName> <partID>");
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
								player.sendMessage(ColorOptions.falsecommand + "Usage: /arena part <add/remove>");
	                    	}
	                    } else if (args[0].equalsIgnoreCase("location"))
	                    {
	                    	if (args.length >= 2)
	                    	{
	                    		if (args[1].equalsIgnoreCase("set"))
	                    		{
	                    			if (args.length == 3)
	                    			{
		                    			String locationName = args[2];
		                    			if (arena.validLoc(locationName))
		                    			{
		                    				Location location = player.getLocation();
											if (manager.getApplicableRegions(location) != null)
											{
												if (this.region.getRegion(location, "arena", manager) != null)
												{
													ProtectedRegion region = this.region.getRegion(location, "arena", manager);
													Integer arenaID = this.region.getRegionID(region);
													spawnpoint.saveSpawnPoint("arena_" + arenaID + "_" + locationName, "0", 0, "0", "", location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
													try 
													{
														player.sendMessage(ColorOptions.messageachievement + "Succesfully set the spawnpoint for category " + locationName + " for arena " + arena.getArena(arenaID).getString("Name"));
													} catch (SQLException e) 
													{
														e.printStackTrace();
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "You are not standing in a arena-region");
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "You are not standing in any regions");
											}
		                    			} else
		                    			{
		                    				player.sendMessage(ColorOptions.error + "No location-category in arena's could be found named " + locationName);
		                    			}
	                    			} else
	                    			{
	                    				player.sendMessage(ColorOptions.falsecommand + "Usage: /arena location set <locationCategory> while standing in an arena");
	                    			}
	                    		} else
	                    		if (args[1].equalsIgnoreCase("remove"))
	                    		{
	                    			if (args.length == 4)
	                    			{
	                    				String locationName = args[2];
		                    			if (arena.validLoc(locationName))
		                    			{
		                    				String arenaName = args[3];
		                    				if (arena.getArenaID(arenaName) != null)
		                    				{
		                    					Integer arenaID = arena.getArenaID(arenaName);
		                    					if (arena.getSpawnpointID(arenaID, locationName) != null)
		                    					{
		                    						Integer spawnpointID = arena.getSpawnpointID(arenaID, locationName);
		                    						arena.removeSpawnPoint(arenaID, locationName);
		                    						spawnpoint.removeSpawnPoint(spawnpointID);
		                    						player.sendMessage(ColorOptions.messageachievement + "Succesfully removed spawnpoint " + locationName + " from arena " + arenaName);
		                    					} else
		                    					{
		                    						player.sendMessage(ColorOptions.error + "No spawnpoint has been set for location " + locationName + " in arena " + arenaName);
		                    					}
		                    				} else
		                    				{
		                    					player.sendMessage(ColorOptions.error + "No arena could be found named " + arenaName);
		                    				}
		                    			} else
		                    			{
		                    				player.sendMessage(ColorOptions.error + "No location-category in arena's are named " + locationName);
		                    			}
	                    			} else
	                    			{
	                    				player.sendMessage(ColorOptions.falsecommand + "Usage: /arena locaiton remove <locationCategory> <arenaName>");
	                    			}
	                    		} else
	                    		{
	                    			player.sendMessage(ColorOptions.falsecommand + "Usage: /arena location <set/remove>");
	                    		}
	                    	} else
	                    	{
	                    		player.sendMessage(ColorOptions.falsecommand + "Usage: /arena location <set/remove>");
	                    	}
	                   	} else if (args[0].equalsIgnoreCase("npc"))
	                   	{
	                   		if (args.length == 3)
	                   		{
	                   			if (args[1].equalsIgnoreCase("refresh"))
	                   			{
	                   				if (main.isInt(args[2]))
	                   				{
	                   					Integer arenaID = Integer.valueOf(args[2]);
	                   					if (arena.getArenaIDList(null).contains(arenaID))
	                   					{
	                   						arena.refreshNPC(sender, arenaID);
	                   					} else
	                   					{
	                   						player.sendMessage(ColorOptions.error + "No arena could be found with ID " + args[2]);
	                   					}
	                   				} else
	                   				{
	                   					player.sendMessage(ColorOptions.error + "Arena ID must be a number: " + args[2]);
	                   				}
	                   			} else
	                   			{
	                   				player.sendMessage(staffcommandhelp.get(8));
	                   			}
	                   		} else
	                   		{
                   				player.sendMessage(staffcommandhelp.get(8));
	                   		}
	                   	} else
	                    {
	                    	if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("sell") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("list"))
							{
								
							} else
							{
								for (String message : staffcommandhelp)
								{
									player.sendMessage(message);
								}
							}
	                    }
	                    
					} else
					{
						if (args.length > 0)
						{
							if (!args[0].equalsIgnoreCase("list"))
							{
								for (String message : staffcommandhelp)
								{
									player.sendMessage(message);
								}	
							}
						}
					}
				}
				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("list"))
					{
						try {
							this.arenaList(player);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else
				{
					if (player.hasPermission("k&k.arena"))
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
 			}
		}
		return false;
	}
	
	private void createArena(Player sender, String name, Integer townID, Integer streetID, Integer streetNumber, RegionManager regionManager, Selection worldEditSelection)
	{
		//Save the house to the database
		arena.saveArena(name, streetID, streetNumber);

		//Get the houseID of the new house
		Integer arenaID = arena.getArenaID(name);
		
		//The region will be created with the worldGuard API
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				"arena_" + arenaID,
				new BlockVector(worldEditSelection.getNativeMinimumPoint()),
				new BlockVector(worldEditSelection.getNativeMaximumPoint())
				);
		
		regionManager.addRegion(region);
		
		//Set all flags for the region
		region.setFlag(DefaultFlag.ENTRY, State.ALLOW);
		region.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, "");
		region.setFlag(DefaultFlag.GREET_MESSAGE, ColorOptions.messageformat + "You are entering the arena " + name);
		region.setPriority(Integer.valueOf(11));

		//Manage some additional flags
		sender.performCommand("rg flag " + ("arena_" + arenaID) + " deny-blocks any");
	
		sender.sendMessage(ColorOptions.messageformat + "Succesfully created an arena with the name " + ColorOptions.messagesubjects + name + ColorOptions.messageformat + " located in " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(streetID)) + " by the " + street.getStreetName(streetID) + " with number " + streetNumber);
	}
	
	public void removeArena(Player sender, Integer arenaID, RegionManager manager) throws SQLException
	{
		ResultSet arenaI = arena.getArena(arenaID);
		Integer streetID = arenaI.getInt("StreetID");
		Integer townID = street.getTownID(streetID);
		String streetName = street.getStreetName(streetID);
		Integer number = arenaI.getInt("StreetNumber");
		String townName = town.getTownName(townID);
		String arenaName = arenaI.getString("Name");
		
		for (Integer subID : arena.getArenaPartList(arenaID))
		{
			manager.removeRegion("arena_" + arenaID + "," + subID);
			arena.removeArenaPart(arenaID, subID);
		}
		
		manager.removeRegion("arena_" + arenaID);
		arena.removeArenabyID(arenaID);
		
    	sender.sendMessage(ColorOptions.messageformat + "The arena with ID " + ColorOptions.messagesubjects + arenaID + ColorOptions.messageformat + " and name " + ColorOptions.messagesubjects + arenaName + ColorOptions.messageformat + " with number " + number + " on the " + streetName + " in " + townName + " has succesfully been removed");
	}
	
	public void addArenaRegion(Player sender, boolean battleGround, Integer arenaID, RegionManager manager, Selection selection) throws SQLException
	{
		ResultSet arenaI = arena.getArena(arenaID);
		arena.saveArenaPart(arenaID);
		String regionName = "arena_" + arenaID;
		Integer regionPrio = 11;
		Integer partID = null;
		ArrayList<Integer> partList = arena.getArenaPartList(arenaID);
		
		//Check for every partID if there is an existing region, if not, that id is the new partID
		for (Integer id : partList)
		{
			if (!manager.getRegions().containsKey("arena_" + arenaID + "," + id) && !manager.getRegions().containsKey("arena_" + arenaID + "," + id + "-battleground"))
			{
				partID = id;
				break;
			}
		}
		
		if (battleGround)
		{
			regionName = regionName + "," + partID + "-battleground";
			regionPrio = 12;
		} else
		{
			regionName = regionName + "," + partID;
		}

		//Create the actual WorldGuard region
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                regionName,
                new BlockVector(selection.getNativeMinimumPoint()),
                new BlockVector(selection.getNativeMaximumPoint())
				);
		manager.addRegion(region);
		region.setPriority(regionPrio);
		if (battleGround)
		{
			region.setFlag(DefaultFlag.PVP, State.ALLOW);
			region.setFlag(DefaultFlag.ENTRY, State.DENY);
			RegionGroupFlag entryFlag = DefaultFlag.ENTRY.getRegionGroupFlag();
			RegionGroupFlag pvpFlag = DefaultFlag.PVP.getRegionGroupFlag();
			try 
			{
				entryFlag.parseInput(worldguard.getWorldGuard(), null, "non_members");
				pvpFlag.parseInput(worldguard.getWorldGuard(), null, "non_members");
			} catch (InvalidFlagFormat e) 
			{
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Try to set the arena region as parent of the sub-region
		try 
		{
			region.setParent(manager.getRegion("arena_" + arenaID));
		} catch (CircularInheritanceException e) 
		{
			e.printStackTrace();
		}
		
		sender.sendMessage(ColorOptions.messageformat + "Succesfully added a part to arena " + ColorOptions.messagesubjects + arenaI.getString("Name") + ColorOptions.messageformat + " on street " + ColorOptions.messagesubjects + street.getStreetName(arenaI.getInt("StreetID")) + ColorOptions.messageformat + " in the town of " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(arenaI.getInt("StreetID"))));

	}
	
	public void removeArenaRegion(Player sender, Integer arenaID, Integer partID, RegionManager manager) throws SQLException
	{
		ResultSet arenaI = arena.getArena(arenaID);
		String arenaName = arenaI.getString("Name");
		
		if (manager.getRegion("arena_" + arenaID + "," + partID) != null)
		{
			manager.removeRegion("arena_" + arenaID + "," + partID);
		} else
		{
			manager.removeRegion("arena_" + arenaID + "," + partID + "-battleground");
		}
		arena.removeArenaPart(arenaID, partID);
		
		sender.sendMessage(ColorOptions.messageformat+ "Removed a part of arena " + ColorOptions.messagesubjects + arenaName + ColorOptions.messageformat + " with ID " + ColorOptions.messagesubjects + partID + ColorOptions.messageformat + " on street " + ColorOptions.messagesubjects + street.getStreetName(arenaI.getInt("StreetID")) + ColorOptions.messageformat + " in the town of " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(arenaI.getInt("StreetID"))));
	}
	
	public void arenaList(Player sender) throws SQLException
	{
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of arenas:");
		for (Integer arenaID : arena.getArenaIDList(null))
		{
			ResultSet arenaI = arena.getArena(arenaID);
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			String arenaName = arenaI.getString("Name");
			sender.sendMessage(ColorOptions.stats + "-Name: " + arenaName);
			if (sender.isOp() || sender.hasPermission("k&k.arena") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-ID: " + arenaID);
			}	
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
}
