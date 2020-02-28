package Rooms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Properties.Property;
import Regions.Region;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Users.User;
import Users.Users;
import Users.offlineUser;

public class RoomCommands implements CommandExecutor
{
	Town town = new Town();
	Street street = new Street();
	Property property = new Property();
	WorldGuard worldguard = new WorldGuard();
	Room room = new Room();
	WorldEdit worldedit = new WorldEdit();
	Region Region = new Region();
	SpawnPoint spawnpoint = new SpawnPoint();
	Title title = new Title();
	offlineUser user = new offlineUser();
	public Main main;
	public RoomCommands(Main main) 
	{
		this.main = main;
	}
	
	//These 2 Hashmaps are for the confirmation of selling a room
	public static HashMap<UUID, Boolean> sellconfirm = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, Integer> sellRoomID = new HashMap<UUID, Integer>();
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of room-commands",
			ColorOptions.stats + "-/room create <streetName> <streetNumber> <townName> <roomNumber> <price>",
			ColorOptions.stats + "-/room remove <streetName> <streetNumber> <townName> <roomNumber>",
			ColorOptions.stats + "-/room part add <roomID>",
			ColorOptions.stats + "-/room part remove <roomID> <partID>",
			ColorOptions.stats + "-/room spawnpoint add (When standing inside the room)",
			ColorOptions.stats + "-/room spawnpoint remove <roomID>",
			ColorOptions.stats + "-/room rent <streetName> <streetNumber> <townName> <roomNumber>",
			ColorOptions.stats + "-/room sell <streetName> <streetNumber> <townName> <roomNumber>",
			ColorOptions.stats + "-/room info <streetName> <streetNumber> <townName> <roomNumber>",
			ColorOptions.stats + "-/room purge <roomID/all>",
			ColorOptions.stats + "-/room list",
			ColorOptions.statsbrackets
	});
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of room-commands",
			ColorOptions.stats + "-/house buy <houseName>",
			ColorOptions.stats + "-/house sell <houseName>",
			ColorOptions.stats + "-/house info <houseName>",
			ColorOptions.stats + "-/house list",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("room"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				UUID uuid = player.getUniqueId();
				User user = null;
				
				try
				{
					user = Users.getUser(uuid);
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(null, player, true);
					return false;
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(null, player, true);
					return false;
				}
                RegionManager manager = worldguard.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
				if (args.length > 0)
				{
					if (player.hasPermission("k&k.rooms"))
					{
						String subCommand = args[0];
						if (subCommand.equalsIgnoreCase("create"))
						{
							if (args.length == 6)
							{
								Selection selection = worldedit.getWorldEdit().getSelection(player);
								if (selection != null)
								{
									Location max = selection.getMaximumPoint();
				                    Location min = selection.getMinimumPoint();
				                    ApplicableRegionSet regionsmax = manager.getApplicableRegions(max); 
				                    ApplicableRegionSet regionsmin = manager.getApplicableRegions(min); 
				                    if (main.isInt(args[2]) && main.isInt(args[4]) && main.isInt(args[5]))
				                    {
				                    	String streetName = args[1];
				                    	Integer streetNumber = Integer.valueOf(args[2]);
				                    	String townName = args[3];
				                    	Integer roomNumber = Integer.valueOf(args[4]);
				                    	Integer price = Integer.valueOf(args[5]);
				                    	
				                    	Integer townID = town.getTownID(townName);
				                    	if (townID != null)
				                    	{
				                    		Integer streetID = street.getStreetID(streetName, townID);
				                    		if (streetID != null)
				                    		{
				                    			Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
				                    			if (propertyID != null)
				                    			{
				                    				if (room.checkRoom(propertyID, roomNumber) == false)
				                    				{
				                    					ProtectedCuboidRegion region = new ProtectedCuboidRegion(
					                    						"room",
					                    						new BlockVector(selection.getNativeMinimumPoint()),
					                    						new BlockVector(selection.getNativeMaximumPoint())
					                    						);
				                    					if (Region.checkUniqueRegion(manager, region, "room"))
					                    				{
						                    				//Trigger the method that handles the creation of the house
							                    			this.createRoom(player, propertyID, roomNumber, price, manager, selection);
					                    				} else
					                    				{
					                    					player.sendMessage(ColorOptions.error + "You tried to overlap a different room region!");
					                    				}
				                    				} else
				                    				{
				                    					player.sendMessage(ColorOptions.falsecommand + "The room-number you tried to use already exists");
				                    				}
				                    			} else
				                    			{
				                    				player.sendMessage(ColorOptions.falsecommand + "No tavern could be found on the " + streetName + " with number " + streetNumber);
				                    			}
				                    		} else
				                    		{
				                    			player.sendMessage(ColorOptions.falsecommand + "No street could be found named " + streetName + " in the town of " + townName);
				                    		}
				                    	} else
				                    	{
				                    		player.sendMessage(ColorOptions.falsecommand + "No town could be found named " + townName);
				                    	}
				                    } else
				                    {
				                    	player.sendMessage(ColorOptions.falsecommand + "The street-number, room-number and price have to be numbers: " + args[3] + ", " + args[5] + ", " + args[6]);
				                    }
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "You must make a worldEdit-selection first");
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /room create <tavernName> <streetName> <streetNumber> <townName> <roomNumber> <price>");
							}
						} else
						if (subCommand.equalsIgnoreCase("remove"))
						{
							if (args.length == 5)
							{
								if (main.isInt(args[2]) && main.isInt(args[4]))
			                    {
			                    	String streetName = args[1];
			                    	Integer streetNumber = Integer.valueOf(args[2]);
			                    	String townName = args[3];
			                    	Integer roomNumber = Integer.valueOf(args[4]);
			                    	
			                    	Integer townID = town.getTownID(townName);
			                    	if (townID != null)
			                    	{
			                    		Integer streetID = street.getStreetID(streetName, townID);
			                    		if (streetID != null)
			                    		{
			                    			Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
			                    			if (propertyID != null)
			                    			{
			                    				Integer roomID = room.getRoomID(propertyID, roomNumber);
			                    				if (roomID != null)
			                    				{
			                    					this.removeRoom(player, roomID, manager);
			                    				} else
			                    				{
			                    					player.sendMessage(ColorOptions.falsecommand + "No room could be found with roomNumber " + roomNumber);
			                    				}
			                    			} else
			                    			{
			                    				player.sendMessage(ColorOptions.falsecommand + "No tavern could be found on the " + streetName + " with number " + streetNumber);
			                    			}
			                    		} else
			                    		{
			                    			player.sendMessage(ColorOptions.falsecommand + "No street could be found named " + streetName + " in the town of " + townName);
			                    		}
			                    	} else
			                    	{
			                    		player.sendMessage(ColorOptions.falsecommand + "No town could be found named " + townName);
			                    	}
			                    } else
			                    {
			                    	player.sendMessage(ColorOptions.falsecommand + "The street-number and room-number have to be numbers: " + args[3] + ", " + args[5]);
			                    }
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /room remove <tavernName> <streetName> <streetNumber> <townName> <roomNumber>");
							}
						} else
						if (subCommand.equalsIgnoreCase("part"))
						{
							if (args.length >= 3)
							{
								if (args[1].equalsIgnoreCase("add"))
								{
									if (args.length == 3)
									{
										if (main.isInt(args[2]))
										{
											Integer roomID = Integer.valueOf(args[2]);
											Selection selection = worldedit.getWorldEdit().getSelection(player);
											if (selection != null)
											{
												if (room.getRoomIDList(null).contains(roomID))
												{
													//The region will be created with the worldGuard API, this is nessecairy to check if there are any intersecting regions
													ProtectedCuboidRegion region = new ProtectedCuboidRegion(
															"room",
															new BlockVector(selection.getNativeMinimumPoint()),
															new BlockVector(selection.getNativeMaximumPoint())
															);
													if (Region.checkSameRegionID(manager, region, "room", roomID))
													{
														this.addRoomRegion(player, roomID, manager, selection);
													} else
													{	
														player.sendMessage(ColorOptions.error + "You tried to overlap a different room region!");
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "No room could be found with ID " + roomID);
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "You must make a worldEdit-selection first!");
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "The roomID has to be a number!");
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage: /room part add <roomID>");
									}
								} else
								if (args[1].equalsIgnoreCase("remove"))
								{
									if (args.length == 4)
									{
										if (main.isInt(args[2]) && main.isInt(args[3]))
										{
											Integer roomID = Integer.valueOf(args[2]);
											Integer partID = Integer.valueOf(args[3]);
											if (room.getRoomIDList(null).contains(roomID))
											{
												if (room.checkPartID(roomID, partID))
												{
													this.removeRoomRegion(player, roomID, partID, manager);
												} else
												{
													player.sendMessage(ColorOptions.error + "No part of room with ID " + roomID + " could be found with part-ID " + partID);
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "No room could be found with ID " + roomID);
											}
										} else
										{
											player.sendMessage(ColorOptions.falsecommand + "The following command arguments need to be numbers: roomID: " + args[2] + ", partID: " + args[3]);
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage: /room part remove <roomID> <partID>");
									}
								} else
	                    		{
	                    			player.sendMessage(ColorOptions.falsecommand + "Usage: /room part <add/remove>");
	                    		}
							} else
                    		{
                    			for (String message : staffcommandhelp)
								{
									player.sendMessage(message);
								}
                    		}
						} else
						if (subCommand.equalsIgnoreCase("spawnpoint"))
						{
							if (args.length > 1)
							{
								if (args[1].equalsIgnoreCase("set"))
								{
									Location location = player.getLocation();
									if (manager.getApplicableRegions(location) != null)
									{
										if (Region.getRegion(location, "room", manager) != null)
										{
											ProtectedRegion region = Region.getRegion(location, "room", manager);
											Integer roomID = Region.getRegionID(region);
											if (room.getSpawnPointID(roomID) == 0)
											{
												spawnpoint.saveSpawnPoint("room_" + roomID, "0", 0, "0", "", location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
												room.saveSpawnPointID(roomID, spawnpoint.getSpawnPointID("room_" + roomID));
												player.sendMessage(ColorOptions.messageachievement + "Succesfully set the spawnpoint for room with ID" + roomID);
											} else
											{
												player.sendMessage(ColorOptions.error + "This room already has a spawnpoint");
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "You are not standing in a room-region");
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "You are not standing in any regions");
									}
								} else
								if (args[1].equalsIgnoreCase("remove"))
								{
									if (args.length == 3)
									{
										if (main.isInt(args[2]))
										{
											Integer roomID = Integer.valueOf(args[2]);
											if (room.getRoomIDList(null).contains(roomID))
											{
												if (room.getSpawnPointID(roomID) != 0)
												{
													Integer spawnpointID = room.getSpawnPointID(roomID);
													Integer ownerID = room.getOwnerID(roomID);
													if (ownerID != 0)
													{
														UUID useruuid = Users.fetchUUIDbyID(ownerID);
														User userTarget = Users.getUser(useruuid);
														String username = null;
														if (userTarget != null)
														{
															userTarget.removeSpawnpoint();
															username = userTarget.getUsername();
														} else
														{
															this.user.removeSpawnpoint(useruuid);
															username = this.user.getUserName(useruuid);
														}
														player.sendMessage(ColorOptions.messageachievement + "Set the spawnpoint of the owner of this room, " + username + ", to default");
													} else
													{
														player.sendMessage(ColorOptions.messageachievement + "No user-spawnpoint was affected");
													}
													room.removeRoomSpawnPoint(roomID);
													spawnpoint.removeSpawnPoint(spawnpointID);
													player.sendMessage(ColorOptions.messageachievement + "Succesfully removed the spawnpoint of a room with ID " + roomID);
												} else
												{
													player.sendMessage(ColorOptions.error + "This room doesn't have a spawnpoint");
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "No room could be found with ID " + args[2]);
											}
										} else
										{
											player.sendMessage(ColorOptions.falsecommand + "The roomID has to be a number!");
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage: /room spawnpoint remove <roomID>");
									}
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "Usage: /room spawnpoint <set/remove>");
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
							if (!args[0].equalsIgnoreCase("rent") && !args[0].equalsIgnoreCase("sell") && !args[0].equalsIgnoreCase("info") && !args[0].equalsIgnoreCase("list"))
							{
								for (String message : staffcommandhelp)
								{
									player.sendMessage(message);
								}
							}
						}
					}
					if (args[0].equalsIgnoreCase("rent"))
					{
						if (args.length == 5)
						{
							String streetName = args[1];
							String townName = args[3];
							if (main.isInt(args[2]) && main.isInt(args[4]))
							{
								Integer streetNumber = Integer.valueOf(args[2]);
								Integer roomNumber = Integer.valueOf(args[4]);
								Integer townID = town.getTownID(townName);
								if (townID != null)
								{
									Integer streetID = street.getStreetID(streetName, townID);
									if (streetID != null)
									{
										Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
										if (propertyID != null)
										{
											Integer roomID = room.getRoomID(propertyID, roomNumber);
											if (roomID != null)
											{
												this.rentRoom(user, roomID, manager);
											} else
											{
												player.sendMessage(ColorOptions.error + "No room could be found with roomNumber " + roomNumber + " in the tavern on the " + streetName + " with streetNumber " + streetNumber + " in the town of " + townName);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No tavern could be found on the " + streetName + " with streetNumber " + streetNumber + " in the town of " + townName);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No street could be found named " + streetName + " in the town of " + townName);
									}
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "No town could be found named " + townName);
								}
							} else
							{
		                    	player.sendMessage(ColorOptions.falsecommand + "The street-number and room-number have to be numbers: " + args[2] + ", " + args[4]);
							}
						} else if (args.length == 2)
						{
							if (main.isInt(args[1]))
							{
								Integer roomID = Integer.valueOf(args[1]);
								if (room.getRoomIDList(null).contains(roomID))
								{
									this.rentRoom(user, roomID, manager);
								} else
								{
									player.sendMessage(ColorOptions.error + "No room could be found with ID " + roomID);
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "The roomID has to be a number!");
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Usage: /room rent <streetName> <streetNumber> <townName> <roomNumber>");
						}
					} else
					if (args[0].equalsIgnoreCase("sell"))
					{
						if (args.length == 5)
						{
							String streetName = args[1];
							String townName = args[3];
							if (main.isInt(args[2]) && main.isInt(args[4]))
							{
								Integer streetNumber = Integer.valueOf(args[2]);
								Integer roomNumber = Integer.valueOf(args[4]);
								Integer townID = town.getTownID(townName);
								if (townID != null)
								{
									Integer streetID = street.getStreetID(streetName, townID);
									if (streetID != null)
									{
										Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
										if (propertyID != null)
										{
											Integer roomID = room.getRoomID(propertyID, roomNumber);
											if (roomID != null)
											{
												this.sellConfirm(user, roomID, manager);
											} else
											{
												player.sendMessage(ColorOptions.error + "No room could be found with roomNumber " + roomNumber + " in the tavern on the " + streetName + " with streetNumber " + streetNumber + " in the town of " + townName);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No tavern could be found on the " + streetName + " with streetNumber " + streetNumber + " in the town of " + townName);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No street could be found named " + streetName + " in the town of " + townName);
									}
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "No town could be found named " + townName);
								}
							} else
							{
		                    	player.sendMessage(ColorOptions.falsecommand + "The street-number and room-number have to be numbers: " + args[2] + ", " + args[4]);
							}
						} else if (args.length == 2)
						{
							if (main.isInt(args[1]))
							{
								Integer roomID = Integer.valueOf(args[1]);
								if (room.getRoomIDList(null).contains(roomID))
								{
									this.sellConfirm(user, roomID, manager);
								} else
								{
									player.sendMessage(ColorOptions.error + "No room could be found with ID " + roomID);
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "The roomID has to be a number!");
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Usage: /room sell <streetName> <streetNumber> <townName> <roomNumber>");
						}
					} else
					if (args[0].equalsIgnoreCase("info"))
					{
						if (args.length == 5)
						{
							String streetName = args[1];
							String townName = args[3];
							if (main.isInt(args[2]) && main.isInt(args[4]))
							{
								Integer streetNumber = Integer.valueOf(args[2]);
								Integer roomNumber = Integer.valueOf(args[4]);
								Integer townID = town.getTownID(townName);
								if (townID != null)
								{
									Integer streetID = street.getStreetID(streetName, townID);
									if (streetID != null)
									{
										Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
										if (propertyID != null)
										{
											Integer roomID = room.getRoomID(propertyID, roomNumber);
											if (roomID != null)
											{
												this.infoRoom(user, roomID);
											} else
											{
												player.sendMessage(ColorOptions.error + "No room could be found with roomNumber " + roomNumber + " in the tavern on the " + streetName + " with streetNumber " + streetNumber + " in the town of " + townName);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No tavern could be found on the " + streetName + " with streetNumber " + streetNumber + " in the town of " + townName);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No street could be found named " + streetName + " in the town of " + townName);
									}
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "No town could be found named " + townName);
								}
							} else
							{
		                    	player.sendMessage(ColorOptions.falsecommand + "The street-number and room-number have to be numbers: " + args[2] + ", " + args[4]);
							}
						} else if (args.length == 2)
						{
							if (main.isInt(args[1]))
							{
								Integer roomID = Integer.valueOf(args[1]);
								if (room.getRoomIDList(null).contains(roomID))
								{
									this.infoRoom(user, roomID);
								} else
								{
									player.sendMessage(ColorOptions.error + "No room could be found with ID " + roomID);
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "The roomID has to be a number!");
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Usage: /room sell <streetName> <streetNumber> <townName> <roomNumber>");
						}
					} else if (args[0].equalsIgnoreCase("purge"))
					{
						if (args.length == 2)
						{
							Integer roomID = null;
							boolean all = false;
							if (args[1].equalsIgnoreCase("all"))
							{
								all = true;
							} else
							if (main.isInt(args[1]))
							{
								Integer id = Integer.valueOf(args[1]);
								if (room.getRoomIDList(null).contains(id))
								{
									roomID = id;
								} else
								{
									sender.sendMessage(ColorOptions.error + "No room could be found with ID " + id);
								}
							} else
							{
								sender.sendMessage(ColorOptions.error + "No correct argument found: " + args[1]);
							}
							
							if (all)
							{
								for (Integer roomID2 : room.getRoomIDList(null))
								{
									room.purgeRoomOwner(roomID2);
									sender.sendMessage(ColorOptions.messageachievement + "Purged a room with ID " + roomID2);
								}
							} else if (roomID != null)
							{
								room.purgeRoomOwner(roomID);
								sender.sendMessage(ColorOptions.messageachievement + "Purged a room with ID " + roomID);
							} else
							{
								sender.sendMessage(ColorOptions.error + "");
							}
						} else
						{
							sender.sendMessage(staffcommandhelp.get(11));
						}
					} else
					if (args[0].equalsIgnoreCase("list"))
					{
						this.roomList(user);
					}  else
					{
						if (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("remove") && !args[0].equalsIgnoreCase("part") && !args[0].equalsIgnoreCase("spawnpoint"))
						{
							for (String message : staffcommandhelp)
							{
								player.sendMessage(message);
							}
						}
					}
				}  else
				{
					if (player.hasPermission("k&k.room"))
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
				sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
	
	private void createRoom(Player sender, Integer propertyID, Integer roomNumber, Integer price, RegionManager regionManager, Selection worldEditSelection)
	{
		//Save the room to the database
		room.saveRoom(propertyID, roomNumber, price);
		
		//Get the roomID of the new room
		Integer roomID = room.getRoomID(propertyID, roomNumber);
		
		//The region will be created with the worldGuard API
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				"room_" + roomID,
				new BlockVector(worldEditSelection.getNativeMinimumPoint()),
				new BlockVector(worldEditSelection.getNativeMaximumPoint())
				);
		
		regionManager.addRegion(region);
		
		//Set all flags for the region
		region.setFlag(DefaultFlag.ENTRY, State.DENY);
		region.setFlag(DefaultFlag.DENY_MESSAGE, "");
		region.setFlag(DefaultFlag.FEED_AMOUNT, Integer.valueOf(20));
		region.setFlag(DefaultFlag.FEED_DELAY, Integer.valueOf(1));
		region.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, "");
		region.setFlag(DefaultFlag.CHEST_ACCESS, State.ALLOW);
		region.setPriority(Integer.valueOf(12));
		//Set the flag that manages the access of players other than the owner of the house
		RegionGroupFlag entryFlag = DefaultFlag.ENTRY.getRegionGroupFlag();
		try 
		{
			entryFlag.parseInput(worldguard.getWorldGuard(), null, "non_members");
		} catch (InvalidFlagFormat e) 
		{
			// Auto-generated catch block
			e.printStackTrace();
		}
		//Manage some additional flags
		sender.performCommand("rg flag " + ("room_" + roomID) + " deny-blocks any");
		sender.performCommand("rg flag " + ("room_" + roomID) + " allow-blocks chest, 58, 26, bookshelf, furnace");
	
		sender.sendMessage(ColorOptions.messageformat + "Succesfully created a room in tavern " + ColorOptions.messagesubjects + property.getPropertyName(propertyID) + ColorOptions.messageformat + " located in " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(property.getStreetID(propertyID))) + " with room-number " + roomNumber);
		sender.sendMessage(ColorOptions.messageformat + "You can configurate it's spawnpoint by using " + ColorOptions.messagesubjects + "/room spawnpoint set");
	}
	
	public void removeRoom(Player sender, Integer roomID, RegionManager manager)
	{		
		//If the house had an owner, remove a house-amount from the owner
		if (room.getOwnerID(roomID) != 0)
		{
			Integer ID = room.getOwnerID(roomID);
			UUID uuid = Users.fetchUUIDbyID(ID);
			User userTarget = Users.getUser(uuid);
			if (userTarget != null)
			{
				userTarget.removeRoomAmount(false, 1);
			} else
			{
				this.user.removeRoomAmount(uuid, 1);
			}
		}
		
		//If the house had a spawnpoint, remove it from the database
		if (room.getSpawnPointID(roomID) != null)
		{
			spawnpoint.removeSpawnPoint(room.getSpawnPointID(roomID));
		}
		
		for (Integer subID : room.getRoomPartList(roomID))
		{
			manager.removeRegion("room_" + roomID + "," + subID);
			room.removeRoomPart(roomID, subID);;
		}
		
		manager.removeRegion("room_" + roomID);
		room.removeRoom(roomID);
		
    	sender.sendMessage(ColorOptions.messageformat + "The room with ID " + ColorOptions.messagesubjects + roomID + ColorOptions.messageformat + " has succesfully been removed");
	}
	
	public void addRoomRegion(Player sender, Integer roomID, RegionManager manager, Selection selection)
	{
		room.saveRoomPart(roomID);
		Integer partID = null;
		ArrayList<Integer> partList = room.getRoomPartList(roomID);
		
		//Check for every partID if there is an existing region, if not, that id is the new partID
		for (Integer id : partList)
		{
			if (!manager.getRegions().containsKey("room_" + roomID + "," + id))
			{
				partID = id;
				break;
			}
		}
		
		//Create the actual WorldGuard region
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                "room_" + roomID + "," + partID,
                new BlockVector(selection.getNativeMinimumPoint()),
                new BlockVector(selection.getNativeMaximumPoint())
				);
		manager.addRegion(region);
		
		//Try to set the house region as parent of the sub-region
		try 
		{
			region.setParent(manager.getRegion("room_" + roomID));
		} catch (CircularInheritanceException e) 
		{
			e.printStackTrace();
		}
		
		sender.sendMessage(ColorOptions.messageformat + "Succesfully added a part to a room with ID " + ColorOptions.messagesubjects + roomID);

	}
	
	public void removeRoomRegion(Player sender, Integer roomID, Integer partID, RegionManager manager)
	{	
		manager.removeRegion("room_" + roomID + "," + partID);
		room.removeRoomPart(roomID, partID);
		
		sender.sendMessage(ColorOptions.messageformat+ "Removed a part of room with ID " + ColorOptions.messagesubjects + roomID + ColorOptions.messageformat + " with roomID " + ColorOptions.messagesubjects + partID);
	}
	
	public void rentRoom(User user, Integer roomID, RegionManager manager)
	{
		Player sender = user.getPlayer();
		UUID uuid = user.getUUID();
		Integer coins = user.getCoins();
		Integer price = room.getPrice(roomID);
		
		Integer propertyID = room.getPropertyID(roomID);
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		Integer streetNumber = property.getStreetNumber(propertyID);
		
		if (coins >= price)
		{
			if (room.getOwnerID(roomID) == 0)
			{
				if (user.getRoomAmount(false) < user.getRoomAmount(true))
				{
					if (user.getRoomAmount(false) == 0)
					{
						room.saveOwnerID(roomID, uuid);
						user.addRoomAmount(false, 1);
						user.removeCoins(price);
						user.saveRentTime();
						room.purgeRoomOwner(roomID);
//						room.addRegionOwner(sender, roomID, manager);
						
						if (room.getSpawnPointID(roomID) != 0)
						{
							Integer spawnpointID = room.getSpawnPointID(roomID);
							user.saveSpawnpoint(spawnpointID);
		    				sender.sendMessage(ColorOptions.messageformat + "" + ChatColor.BOLD + "Your respawn location has been set to this room");
		    				sender.sendMessage(ColorOptions.messageformat + "" + ChatColor.BOLD + "you can change it in " + ColorOptions.messagesubjects + "Personal menu>Settings");
						}
			    		sender.sendMessage(ColorOptions.messageachievement + "You have succesfully bought a room with number " + ColorOptions.messagesubjects + room.getRoomNumber(roomID) + ColorOptions.messageachievement + " on the " + ColorOptions.messagesubjects + streetName + ColorOptions.messageachievement + " with streetnumber " + ColorOptions.messagesubjects + streetNumber + ColorOptions.messageachievement + " in town " + ColorOptions.messagesubjects + town.getTownName(townID));
			    		sender.sendMessage(ColorOptions.messageachievement + "You will pay " + ColorOptions.messagesubjects + price + ColorOptions.messageachievement + " coins per hour that you are online!");
					}
				} else if (user.getRoomAmount(false) == user.getRoomAmount(true))
				{
					sender.sendMessage(ColorOptions.error + "You can't rent any more rooms!");
				} else
				{
					sender.sendMessage(ColorOptions.error + "You can't rent a room!");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "This room is owned by someone else");
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "You don't have enough coins, you need " + (price-coins) + " more coins");
		}
	}
	
	public void sellConfirm(User user, Integer roomID, RegionManager manager)
	{
		UUID uuid = user.getUUID();
		Player sender = user.getPlayer();
		Integer propertyID = room.getPropertyID(roomID);
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		Integer streetNumber = property.getStreetNumber(propertyID);
		
		if (room.getOwnerID(roomID) == user.getID())
		{
			sellconfirm.put(uuid, true);
			sellRoomID.put(uuid, roomID);
			
			sender.sendMessage(ColorOptions.messageformat + "-Are you sure you want to stop renting a room:");
			sender.sendMessage(ColorOptions.messageformat + "-roomnumber" + ColorOptions.messagesubjects + room.getRoomNumber(roomID) + ColorOptions.messageformat + " in tavern " + ColorOptions.messagesubjects + property.getPropertyName(propertyID) + ColorOptions.messageformat + " on the " + ColorOptions.messagesubjects + streetName + ColorOptions.messageformat + " with streetnumber " + ColorOptions.messagesubjects + streetNumber + ColorOptions.messageformat + " in town " + ColorOptions.messagesubjects + town.getTownName(townID));
			sender.sendMessage(ColorOptions.messageformat + "-Type " + ChatColor.GREEN + "yes" + ColorOptions.messageformat + " or " + ChatColor.RED + "no");
		} else
		{
			sender.sendMessage(ColorOptions.error + "This room is owned by someone else");
		}
	}
	
	public void infoRoom(User user, Integer roomID)
	{
		Player sender = user.getPlayer();
		Integer roomNumber = room.getRoomNumber(roomID);
		Integer propertyID = room.getPropertyID(roomID);
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		String townName = town.getTownName(townID);
		Integer streetNumber = property.getStreetNumber(propertyID);
		
		sender.sendMessage(ColorOptions.halfstatsbrackets + ChatColor.BOLD + "" + ColorOptions.statsresults + "Information" + ColorOptions.halfstatsbrackets);
		if (sender.hasPermission("k&k.room") || main.ownermodus.containsKey(sender.getUniqueId()))
		{
			sender.sendMessage(ColorOptions.stats + "-ID: " + ColorOptions.statsresults + roomID);
		}
		sender.sendMessage(ColorOptions.stats + "-roomnumber: " + ColorOptions.statsresults + roomNumber);
		sender.sendMessage(ColorOptions.stats + "-Location: " + ColorOptions.statsresults + "tavern-name: " + property.getPropertyName(propertyID) + ColorOptions.statsresults + ", street: " + streetName + ", streetnumber: " + streetNumber + ", town: " + townName);
		sender.sendMessage(ColorOptions.stats + "-Rentingprice: " + ColorOptions.statsresults + room.getPrice(roomID));
		
		if (room.getOwnerID(roomID) == 0)
		{
			sender.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + "-");
		} else
		{
			Integer userID = room.getOwnerID(roomID);
			UUID uuid = this.user.getUUIDbyID(userID);
			Integer genderID = this.user.getGenderID(uuid);
			Integer titleID = this.user.getTitleID(uuid);
			
			sender.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + this.user.getUserName(uuid));
			sender.sendMessage(ColorOptions.stats + "-Titlename: " + ColorOptions.statsresults + title.getTitleName(titleID, genderID));
			sender.sendMessage(ColorOptions.stats + "-Experience: " + ColorOptions.statsresults + this.user.getExperience(uuid));
		}
		sender.sendMessage(ColorOptions.statsbrackets);
	}
	
	public void roomList(User user)
	{
		Player sender = user.getPlayer();
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of rooms:");
		for (Integer roomID : room.getRoomIDList(null))
		{
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			Integer roomNumber = room.getRoomNumber(roomID);
			Integer propertyID = room.getPropertyID(roomID);
			Integer streetID = property.getStreetID(propertyID);
			String streetName = street.getStreetName(streetID);
			Integer townID = street.getTownID(streetID);
			String townName = town.getTownName(townID);
			Integer streetNumber = property.getStreetNumber(propertyID);
			Integer price = room.getPrice(roomID);
			sender.sendMessage(ColorOptions.stats + "-roomnumber: " + roomNumber);
			sender.sendMessage(ColorOptions.stats + "-Location: " + ColorOptions.statsresults + "tavern-name: " + property.getPropertyName(propertyID) + ColorOptions.statsresults + ", street: " + streetName + ", streetnumber: " + streetNumber + ", town: " + townName);
			if (sender.isOp() || sender.hasPermission("k&k.room") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-ID: " + roomID);
			}
			sender.sendMessage(ColorOptions.stats + "-Rentingprice: " + price);
			if (room.getOwnerID(roomID) == 0)
			{
				sender.sendMessage(ChatColor.GREEN + "Available!");
			} else if (room.getOwnerID(roomID) == user.getID())
			{
				sender.sendMessage(ColorOptions.statsresults + "Rented by you!");
			} else
			{
				Integer userID = room.getOwnerID(roomID);
				UUID uuid = this.user.getUUIDbyID(userID);
				
				sender.sendMessage(ChatColor.RED + "Unavailable. Rented by: " + this.user.getUserName(uuid));
			}
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
}
