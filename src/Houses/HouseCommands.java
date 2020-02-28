package Houses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
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
import Donator.Donator;
import Exceptions.UserNotFoundException;
import Genders.Gender;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Regions.Region;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Users.User;
import Users.Users;
import Users.offlineUser;

public class HouseCommands implements CommandExecutor
{
	Title title = new Title();
	Gender gender = new Gender();
	Donator donator = new Donator();
	House house = new House();
	Street street = new Street();
	Town town = new Town();
	SpawnPoint spawnpoint = new SpawnPoint();
	Region region = new Region();
	WorldEdit worldedit = new WorldEdit();
	WorldGuard worldguard = new WorldGuard();
	public Main main;
	public HouseCommands(Main main) 
	{
		this.main = main;
	}
	
	//These 2 Hashmaps are for the confirmation of selling a property
	public static HashMap<UUID, Boolean> sellconfirm = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, Integer> sellpropertyID = new HashMap<UUID, Integer>();
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of house-commands",
			ColorOptions.stats + "-/house create <houseName> <townName> <streetName> <houseNumber> <titleID> <grade(1-5)>",
			ColorOptions.stats + "-/house remove <townName> <streetName> <streetNumber>",
			ColorOptions.stats + "-/property part add <propertyID>",
			ColorOptions.stats + "-/property part remove <propertyID> <partID>",
			ColorOptions.stats + "-/house spawnpoint set (When standing inside the house)",
			ColorOptions.stats + "-/house spawnpoint remove <streetname> <streetnumber> <townname>",
			ColorOptions.stats + "-/house buy <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/house sell <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/house info <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/house purge <houseID/all>",
			ColorOptions.stats + "-/house list",
			ColorOptions.statsbrackets
	});
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of house-commands",
			ColorOptions.stats + "-/house buy <houseName>",
			ColorOptions.stats + "-/house sell <houseName>",
			ColorOptions.stats + "-/house info <houseName>",
			ColorOptions.stats + "-/house list",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("house"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
                RegionManager manager = worldguard.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
				if (args.length > 0)
				{
					if (player.hasPermission("k&k.house.*"))
					{
						//Get the selection of worldedit a user has made to register as a house
						Selection selection = worldedit.getWorldEdit().getSelection(player);
						if (args[0].equalsIgnoreCase("create"))
						{
							if (args.length == 7)
							{
								if (selection != null)
								{
									Location max = selection.getMaximumPoint();
				                    Location min = selection.getMinimumPoint();
				                    ApplicableRegionSet regionsmax = manager.getApplicableRegions(max); 
				                    ApplicableRegionSet regionsmin = manager.getApplicableRegions(min); 
				                    //Command is /house create(0) name(1) townname(2) streetname(3) housenumber(4) price(5)
				                    if (main.isInt(args[4]) && main.isInt(args[5]) && main.isInt(args[6]))
				                    {
				                		//Get the ID of the city where the location is
				                    	String houseName = args[1];
				                    	String townName = args[2];
				                    	String streetName = args[3];
				                    	Integer houseNumber = Integer.valueOf(args[4]);
				                    	Integer requiredTitleID = Integer.valueOf(args[5]);
				                    	Integer grade = Integer.valueOf(args[6]);
				                    	if (requiredTitleID < 0 || requiredTitleID > 18)
				                    	{
				                    		player.sendMessage(ColorOptions.error + "TitleID must be between 0 and 18: "+ requiredTitleID);
				                    		return false;
				                    	}
				                    	if (grade < 1 || grade > 5)
				                    	{
				                    		player.sendMessage(ColorOptions.error + "Grade must be between 1 and 5: " + grade);
				                    		return false;
				                    	}
				                		if (town.getTownID(townName) != null)
				                		{
				                			Integer townID = town.getTownID(townName);
				                			if (street.checkStreet(streetName, townID))
					                    	{
					                    		Integer StreetID = street.getStreetID(streetName, townID);
					                    		if (house.checkStreetNumber(houseNumber, StreetID))
					                    		{
				                    				//The region will be created with the worldGuard API, this is nessecairy to check if there are any intersecting regions
				                    				ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				                    						"house",
				                    						new BlockVector(selection.getNativeMinimumPoint()),
				                    						new BlockVector(selection.getNativeMaximumPoint())
				                    						);
				                    				if (this.region.checkUniqueRegion(manager, region, "house"))
				                    				{
					                    				//Trigger the method that handles the creation of the house
						                    			createHouse(player, houseName, townID, streetName, houseNumber, requiredTitleID, grade, manager, selection);
				                    				} else
				                    				{
				                    					player.sendMessage(ColorOptions.error + "You tried to overlap a different house region!");
				                    				}
					                    		} else
					                    		{
					                    			player.sendMessage(ColorOptions.error + "This housenumber is already taken by another house in this street");
					                    		}
					                    	} else
					                    	{
					                    		player.sendMessage(ColorOptions.error + "There is no street called " + streetName);
					                    	}
				                		} else
				                		{
				                			player.sendMessage(ColorOptions.error + "You are not standing in a configured city");
				                		}
				                    } else
				                    {
				                    	player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: housenumber: " + args[4] + ", titleID: " + args[5] + ", grade: " + args[6]);
				                    }
								} else
								{
				                	player.sendMessage(ColorOptions.falsecommand + "You must first make a worldEdit selection!");
								}
							} else
							{
								player.sendMessage(staffcommandhelp.get(2));
							}
						} else
						if (args[0].equalsIgnoreCase("remove"))
						{
							if (args.length == 4)
							{
								String townName = args[1];
								String streetName = args[2];
								String houseNumberString = args[3];
								if (town.getTownID(townName) != null)
								{
									Integer townID = town.getTownID(townName);
									if (street.checkStreet(streetName, townID))
									{
										Integer streetID = street.getStreetID(streetName, townID);
										if (main.isInt(houseNumberString))
										{
											Integer houseNumber = Integer.valueOf(houseNumberString);
											if (house.checkStreetNumber(houseNumber, streetID) == false)
											{
												Integer houseID = house.getHouseIDbyLocation(streetID, houseNumber);
												removeHouse(player, houseID, manager);
											} else
											{
												player.sendMessage(ColorOptions.error + "There is no house on the " + streetName + " with number " + houseNumber + " in the town of " + townName);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "Error: housenumber needs to be a number: " + houseNumberString);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "There is no street named " + streetName + " in the town of " + townName);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "There is no town named " + townName + " on this continent");
								}
							} else
							if (args.length == 2)
							{
								if (main.isInt(args[1]))
								{
									Integer houseID = Integer.valueOf(args[1]);
									if (house.getHouseIDList(null).contains(houseID))
									{
										removeHouse(player, houseID, manager);
									} else
									{
										player.sendMessage(ColorOptions.error + "There is no house with ID: " + houseID);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "Error: house ID needs to be a number: " + args[1]);
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /house remove <cityname> <streetname> <housenumber> OR");
								player.sendMessage(ColorOptions.falsecommand + "/house remove <houseID>");
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
		                			   if (main.isInt(args[2]))
		                			   {
		                				   if (selection != null)
		                				   {
		                					   if (house.getHouseIDList(null).contains(Integer.valueOf(args[2])))
		                					   {
		                						   Integer propertyID = Integer.valueOf(args[2]);
		                						   //The region will be created with the worldGuard API, this is nessecairy to check if there are any intersecting regions
		                						   ProtectedCuboidRegion region = new ProtectedCuboidRegion(
		                								   "house",
		                								   new BlockVector(selection.getNativeMinimumPoint()),
		                								   new BlockVector(selection.getNativeMaximumPoint())
		                								   );
		                						   if (this.region.checkSameRegionID(manager, region, "house", propertyID))
		                						   {
		                							   this.addHouseRegion(player, propertyID, manager, selection);
		                						   } else
		                						   {	
		                							   player.sendMessage(ColorOptions.error + "You tried to overlap a different house region!");
		                						   }
		                					   } else
		                					   {
		                						   player.sendMessage(ColorOptions.error + "No house could be found with ID " + args[2]);
		                					   }
		                				   } else
		                				   {
		                					   player.sendMessage(ColorOptions.error + "You need to make a selection first!");
		                				   }
		                			   } else
		                			   {
		                				   player.sendMessage(ColorOptions.falsecommand + "The following command arguments need to be numbers: <houseID> " + args[2]);
		                			   }
		                		   } else
		                		   {
		                			   player.sendMessage(ColorOptions.falsecommand + "Usage: /house part add <houseID>");
		                		   }
		                	   } else
		                		   if (args[1].equalsIgnoreCase("remove"))
		                		   {
		                			   if (args.length == 4)
		                			   {
		                				   if (main.isInt(args[2]) && main.isInt(args[3]))
		                				   {
		                					   if (house.getHouseIDList(null).contains(Integer.valueOf(args[2])))
		                					   {
		                						   Integer houseID = Integer.valueOf(args[2]);
		                						   if (house.checkPartID(houseID, Integer.valueOf(args[3])))
		                						   {
		                							   Integer partID = Integer.valueOf(args[3]);
		                							   this.removeHouseRegion(player, houseID, partID, manager);
		                						   } else
		                						   {
		                							   player.sendMessage(ColorOptions.error + "No part of house with ID " + houseID + " could be found with part-ID " + args[3]);
		                						   }
		                					   } else
		                					   {
		                						   player.sendMessage(ColorOptions.error + "No house could be found with ID " + args[2]);
		                					   }
		                				   } else
		                				   {
		                					   player.sendMessage(ColorOptions.falsecommand + "The following command arguments need to be numbers: houseID: " + args[2] + ", partID: " + args[3]);
		                				   }
		                			   } else
		                			   {
		                				   player.sendMessage(ColorOptions.falsecommand + "Usage: /house part remove <houseID> <partID>");
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
									player.sendMessage(ColorOptions.falsecommand + "Usage: /house part <add/remove>");
		                    	}
						} else
						if (args[0].equalsIgnoreCase("spawnpoint"))
						{
							if (args.length > 1)
							{
								if (args[1].equalsIgnoreCase("set"))
								{
									Location location = player.getLocation();
									if (manager.getApplicableRegions(location) != null)
									{
										if (this.region.getRegion(location, "house", manager) != null)
										{
											ProtectedRegion region = this.region.getRegion(location, "house", manager);
											Integer houseID = this.region.getRegionID(region);
											if (house.getHouseSpawnPoint(houseID) == 0)
											{
												spawnpoint.saveSpawnPoint("house_" + houseID, "0", 0, "0", "", location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
												house.saveHouseSpawnPoint(houseID, spawnpoint.getSpawnPointID("house_" + houseID));
												player.sendMessage(ColorOptions.messageachievement + "Succesfully set the spawnpoint for house " + house.getHouseName(houseID) + " on " + street.getStreetName(house.getStreetID(houseID)) + " with streetnumber " + house.getHouseNumber(houseID) + " in town " + town.getTownName(street.getTownID(house.getStreetID(houseID))));
											} else
											{
												player.sendMessage(ColorOptions.error + "This house already has a spawnpoint");
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "You are not standing in a house-region");
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "You are not standing in any regions");
									}
								} else
								if (args[1].equalsIgnoreCase("remove"))
								{
									if (args.length >= 3)
									{
										if (main.isInt(args[2]))
										{
											if (house.getHouseIDList(null).contains(Integer.valueOf(args[2])))
											{
												Integer houseID = Integer.valueOf(args[2]);
												if (house.getHouseSpawnPoint(houseID) != 0)
												{
													Integer spawnpointID = house.getHouseSpawnPoint(houseID);
													if (house.getHouseOwnerID(houseID) != 0)
													{
														UUID ownerUUID = Users.fetchUUIDbyID(house.getHouseOwnerID(houseID));
														User user = null;
														try
														{
															user = Users.getUser(ownerUUID);
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
														user.removeSpawnpoint();
														String username = user.getUsername();
														player.sendMessage(ColorOptions.messageachievement + "Set the spawnpoint of the owner of this house, " + username + ", to default");
													} else
													{
														player.sendMessage(ColorOptions.messageachievement + "No user-spawnpoint was affected");
													}
													house.removeHouseSpawnPoint(houseID);
													spawnpoint.removeSpawnPoint(spawnpointID);
													player.sendMessage(ColorOptions.messageachievement + "Succesfully removed the spawnpoint of a house with ID " + houseID);
												} else
												{
													player.sendMessage(ColorOptions.error + "This house doesn't have a spawnpoint");
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "No house could be found with ID " + args[2]);
											}
										} else if (args.length == 5)
										{
											String streetName = args[2];
											String townName = args[4];
											if (main.isInt(args[3]))
											{
												Integer streetNumber = Integer.valueOf(args[3]);
												if (town.getTownID(townName) != null)
												{
													Integer townID = town.getTownID(townName);
													if (street.checkStreet(streetName, townID))
													{
														Integer streetID = street.getStreetID(streetName, townID);
														if (house.getHouseIDbyLocation(streetID, streetNumber) != null)
														{
															Integer houseID = house.getHouseIDbyLocation(streetID, streetNumber);
															if (house.getHouseSpawnPoint(houseID) != null)
															{
																Integer spawnpointID = house.getHouseSpawnPoint(houseID);
																if (house.getHouseOwnerID(houseID) != 0)
																{
																	UUID ownerUUID = Users.fetchUUIDbyID(house.getHouseOwnerID(houseID));
																	User user = null;
																	try
																	{
																		user = Users.getUser(ownerUUID);
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
																	user.removeSpawnpoint();
																	String username = user.getUsername();
																	player.sendMessage(ColorOptions.messageachievement + "Set the spawnpoint of the owner of this house, " + username + ", to default");
																} else
																{
																	player.sendMessage(ColorOptions.messageachievement + "No user-spawnpoint was affected");
																}
																house.removeHouseSpawnPoint(houseID);
																spawnpoint.removeSpawnPoint(spawnpointID);
																player.sendMessage(ColorOptions.messageachievement + "Succesfully removed the spawnpoint of a house with ID " + houseID);
															} else
															{
																player.sendMessage(ColorOptions.error + "This house doesn't have a spawnpoint");
															}
														} else
														{
															player.sendMessage(ColorOptions.error + "No house could be found on the " + streetName + " with streetnumber " + streetNumber + " in town " + townName);
														}
													} else
													{
														player.sendMessage(ColorOptions.error + "No street could be found in " + townName + " named " + streetName);
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "No town could be found named " + townName);
												}
											} else
											{
						                    	player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: housenumber: " + args[3]);
											}
										} else
										{
											player.sendMessage(ColorOptions.falsecommand + "Usage:");
											player.sendMessage(ColorOptions.falsecommand + "/house spawnpoint remove <id>");
											player.sendMessage(ColorOptions.falsecommand + "/house spawnpoint remove <streetname> <streetnumber> <townname>");
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage:");
										player.sendMessage(ColorOptions.falsecommand + "/house spawnpoint remove <id>");
										player.sendMessage(ColorOptions.falsecommand + "/house spawnpoint remove <streetname> <streetnumber> <townname>");
									}
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "Usage: /house spawnpoint <set/remove>");
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /house spawnpoint <set/remove>");
							}
						} else
						{
							if (!args[0].equalsIgnoreCase("buy") && !args[0].equalsIgnoreCase("sell") && !args[0].equalsIgnoreCase("info") && !args[0].equalsIgnoreCase("list"))
							{
								for (String message : staffcommandhelp)
								{
									player.sendMessage(message);
								}
							}
						}
					}
					if (args[0].equalsIgnoreCase("buy"))
					{
						if (args.length == 4)
						{
							String streetName = args[1];
							String townName = args[3];
							if (main.isInt(args[2]))
							{
								Integer streetNumber = Integer.valueOf(args[2]);
								if (town.getTownID(townName) != null)
								{
									Integer townID = town.getTownID(townName);
									if (street.checkStreet(streetName, townID))
									{
										Integer streetID = street.getStreetID(streetName, townID);
										if (house.getHouseIDbyLocation(streetID, streetNumber) != null)
										{
											Integer houseID = house.getHouseIDbyLocation(streetID, streetNumber);
											this.buyHouse(player, houseID, manager);
										} else
										{
											player.sendMessage(ColorOptions.error + "No house could be found on street " + streetName + " with number " + streetNumber);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No street could be found named " + streetName + " in town " + townName);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "No town could be found named " + townName);
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: streetNumber: " + args[2]);
							}
						} else if (args.length == 2)
						{
							if (main.isInt(args[1]))
							{
								Integer houseID = Integer.valueOf(args[1]);
								if (house.getHouseIDList(null).contains(houseID))
								{
									this.buyHouse(player, houseID, manager);
								} else
								{
									player.sendMessage(ColorOptions.error + "No house could be found with ID " + houseID);
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: houseID: " + args[1]);
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Usage: /house buy <streetName> <streetNumber> <townName>");
						}
					} else
					if (args[0].equalsIgnoreCase("sell"))
					{
						if (args.length == 4)
						{
							String streetName = args[1];
							String townName = args[3];
							if (main.isInt(args[2]))
							{
								Integer streetNumber = Integer.valueOf(args[2]);
								if (town.getTownID(townName) != null)
								{
									Integer townID = town.getTownID(townName);
									if (street.checkStreet(streetName, townID))
									{
										Integer streetID = street.getStreetID(streetName, townID);
										if (house.getHouseIDbyLocation(streetID, streetNumber) != null)
										{
											Integer houseID = house.getHouseIDbyLocation(streetID, streetNumber);
											this.sellConfirm(player, houseID, manager);
										} else
										{
											player.sendMessage(ColorOptions.error + "No house could be found on street " + streetName + " with number " + streetNumber);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No street could be found named " + streetName + " in town " + townName);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "No town could be found named " + townName);
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: streetNumber: " + args[2]);
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Usage: /house sell <streetName> <streetNumber> <townName>");
						}
					} else
					if (args[0].equalsIgnoreCase("info"))
					{
						if (args.length == 4)
						{
							String streetName = args[1];
							String townName = args[3];
							if (main.isInt(args[2]))
							{
								Integer streetNumber = Integer.valueOf(args[2]);
								if (town.getTownID(townName) != null)
								{
									Integer townID = town.getTownID(townName);
									if (street.checkStreet(streetName, townID))
									{
										Integer streetID = street.getStreetID(streetName, townID);
										if (house.getHouseIDbyLocation(streetID, streetNumber) != null)
										{
											Integer houseID = house.getHouseIDbyLocation(streetID, streetNumber);
											this.infoHouse(player, houseID);
										} else
										{
											player.sendMessage(ColorOptions.error + "No house could be found on street " + streetName + " with number " + streetNumber);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No street could be found named " + streetName + " in town " + townName);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "No town could be found named " + townName);
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: streetNumber: " + args[2]);
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Usage: /house info <streetName> <streetNumber> <townName>");
						}
					} else if (args[0].equalsIgnoreCase("purge"))
					{
						if (args.length == 2)
						{
							Integer houseID = null;
							boolean all = false;
							if (args[1].equalsIgnoreCase("all"))
							{
								all = true;
							} else
							if (main.isInt(args[1]))
							{
								Integer id = Integer.valueOf(args[1]);
								if (house.getHouseIDList(null).contains(id))
								{
									houseID = id;
								} else
								{
									sender.sendMessage(ColorOptions.error + "No house could be found with ID " + id);
								}
							} else
							{
								sender.sendMessage(ColorOptions.error + "No correct argument found: " + args[1]);
							}
							
							if (all)
							{
								for (Integer houseID2 : house.getHouseIDList(null))
								{
									house.purgeHouseOwner(houseID2);
									sender.sendMessage(ColorOptions.messageachievement + "Purged a house with ID " + houseID2);
								}
							} else if (houseID != null)
							{
								house.purgeHouseOwner(houseID);
								sender.sendMessage(ColorOptions.messageachievement + "Purged a house with ID " + houseID);
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
						this.houseList(player);
					}
				} else
				{
					if (player.hasPermission("k&k.house"))
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
				sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
	
	
	private void createHouse(Player sender, String name, Integer CityID, String streetName, Integer houseNumber, Integer titleID, Integer grade, RegionManager regionManager, Selection worldEditSelection)
	{
		Integer salary = title.getSalary(titleID);
		Integer dailySalary = salary*4;
		Integer timePrice = main.getRandom(dailySalary*30, dailySalary*60);
		Integer price = Integer.valueOf((int) (Math.round((timePrice*(1+(grade/10)))/100.0)*100));
		//Save the house to the database
		house.saveHouse(name, streetName, houseNumber, price, CityID);
		
		//Get the street ID of the new house
		Integer StreetID = street.getStreetID(streetName, CityID);
		//Get the houseID of the new house
		Integer houseID = house.getHouseID(name, StreetID, houseNumber);
		
		//The region will be created with the worldGuard API
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				"house_" + houseID,
				new BlockVector(worldEditSelection.getNativeMinimumPoint()),
				new BlockVector(worldEditSelection.getNativeMaximumPoint())
				);
//		TEST
//		Polygonal2DRegion weRegion = new Polygonal2DRegion((LocalWorld) worldEditSelection.getWorld(), region.getPoints(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockY());
		CuboidRegion curegion = new CuboidRegion(BukkitUtil.getLocalWorld(worldEditSelection.getWorld()), region.getMinimumPoint(), region.getMaximumPoint());
		for (BlockVector block : curegion) 
		{
		    Block bukkitBlock = BukkitUtil.toBlock(new BlockWorldVector(BukkitUtil.getLocalWorld(worldEditSelection.getWorld()), block));
		    if (bukkitBlock.getType() == Material.CHEST)
		    {
		    	Bukkit.broadcastMessage("Chest detected!");
		    }
		    // Do something with the block
		}
//		for (BlockVector block : weRegion) 
//		{
//		    Block bukkitBlock = BukkitUtil.toBlock(new BlockWorldVector((LocalWorld) worldEditSelection.getWorld(), block));
//		    if (bukkitBlock.getType() == Material.CHEST)
//		    {
//		    	Bukkit.broadcastMessage("Chest detected!");
//		    }
//		    // Do something with the block
//		}
		
//		TEST
		regionManager.addRegion(region);
		
		//Set all flags for the region
		region.setFlag(DefaultFlag.ENTRY, State.DENY);
		region.setFlag(DefaultFlag.FEED_AMOUNT, Integer.valueOf(20));
		region.setFlag(DefaultFlag.FEED_DELAY, Integer.valueOf(1));
		region.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, "");
		region.setFlag(DefaultFlag.DENY_MESSAGE, "");
		region.setPriority(Integer.valueOf(11));
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
		sender.performCommand("rg flag " + ("house_" + houseID) + " deny-blocks any");
//		sender.performCommand("rg flag " + ("house_" + houseID) + " allow-blocks chest, furnace, anvil, painting, 58, 26, bookshelf, torch");
		sender.sendMessage(ColorOptions.messageformat + "Succesfully created a house with the name " + ColorOptions.messagesubjects + name + ColorOptions.messageformat + " located in " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(StreetID)) + " by the " + streetName + " with number " + houseNumber);
		sender.sendMessage(ColorOptions.messageformat + "You can configurate it's spawnpoint by using " + ColorOptions.messagesubjects + "/house spawnpoint set");
	}
	
	public void removeHouse(Player sender, Integer houseID, RegionManager manager)
	{
		Integer streetID = house.getStreetID(houseID);
		String streetName = street.getStreetName(streetID);
		Integer number = house.getHouseNumber(houseID);
		String cityName = town.getTownName(street.getTownID(house.getStreetID(houseID)));
		
		//If the house had an owner, remove a house-amount from the owner
		if (house.getHouseOwnerID(houseID) != 0)
		{
			UUID ownerUUID = Users.fetchUUIDbyID(house.getHouseOwnerID(houseID));
			User user = Users.getUser(ownerUUID);
			String username = null;
			if (user != null)
			{
				if (house.getHouseSpawnPoint(houseID) == user.getSpawnpointID())
				{
					user.removeSpawnpoint();
				}
				try
				{
					user.removeHouseAmount(false, 1);
				} catch (Exception ex)
				{
					ex.printStackTrace();
					sender.sendMessage(ColorOptions.error + "The house-amount of the owner couldn't be updated");
					sender.sendMessage(ColorOptions.error + "UUID: " + ownerUUID);
				}
				username = user.getUsername();
			} else
			{
				offlineUser offlineUser = new offlineUser();
				if (house.getHouseSpawnPoint(houseID) == offlineUser.getSpawnpointID(ownerUUID))
				{
					offlineUser.removeSpawnpoint(ownerUUID);
				}
				try
				{
					offlineUser.removeHouseAmount(ownerUUID, 1);
				} catch (Exception ex)
				{
					ex.printStackTrace();
					sender.sendMessage(ColorOptions.error + "The house-amount of the owner couldn't be updated");
					sender.sendMessage(ColorOptions.error + "UUID: " + ownerUUID);
				}
				username = offlineUser.getUserName(ownerUUID);
			}
			try
			{
				sender.sendMessage(ColorOptions.messageachievement + "Updated the list of houses of the owner " + username);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "UUID: " + ownerUUID);
				sender.sendMessage(ColorOptions.error + "Something went wrong updating the owner of this house!");
			}
		}
		
		//If the house had a spawnpoint, remove it from the database
		if (house.getHouseSpawnPoint(houseID) != null)
		{
			spawnpoint.removeSpawnPoint(house.getHouseSpawnPoint(houseID));
		}
		
		for (Integer subID : house.getHousePartList(houseID))
		{
			manager.removeRegion("house_" + houseID + "," + subID);
			house.removeHousePart(houseID, subID);
		}
		
		manager.removeRegion("house_" + houseID);
		house.removeHousebyID(houseID);
		
    	sender.sendMessage(ColorOptions.messageformat + "The house with ID " + ColorOptions.messagesubjects + houseID + ColorOptions.messageformat + " with number " + number + " on the " + streetName + " in " + cityName + " has succesfully been removed");
	}
	
	public void addHouseRegion(Player sender, Integer houseID, RegionManager manager, Selection selection)
	{
		house.saveHousePart(houseID);
		Integer partID = null;
		ArrayList<Integer> partList = house.getHousePartList(houseID);
		
		//Check for every partID if there is an existing region, if not, that id is the new partID
		for (Integer id : partList)
		{
			if (!manager.getRegions().containsKey("house_" + houseID + "," + id))
			{
				partID = id;
				break;
			}
		}
		
		//Create the actual WorldGuard region
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                "house_" + houseID + "," + partID,
                new BlockVector(selection.getNativeMinimumPoint()),
                new BlockVector(selection.getNativeMaximumPoint())
				);
		//manager.addRegion(region);
		
		//Try to set the house region as parent of the sub-region
		try 
		{
			region.setParent(manager.getRegion("house_" + houseID));
		} catch (CircularInheritanceException e) 
		{
			e.printStackTrace();
		}
		
		sender.sendMessage(ColorOptions.messageformat + "Succesfully added a part to the " + ColorOptions.messagesubjects + house.getHouseName(houseID) + ColorOptions.messageformat + " located in " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(house.getStreetID(houseID))) + " by the " + street.getStreetName(house.getStreetID(houseID)) + " with number " + house.getHouseNumber(houseID));

	}
	
	public void buyHouse(Player sender, Integer houseID, RegionManager manager)
	{
		UUID uuid = sender.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, sender, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, sender, true);
			return;
		}
		
		Integer coins = user.getCoins();
		Integer price = house.getHousePrice(houseID);
		Integer houseAmount = user.getHouseAmount(false);
		Integer houseMax = user.getHouseAmount(true);
		
		Integer streetID = house.getStreetID(houseID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		Integer streetNumber = house.getHouseNumber(houseID);
		if (user.canBuyHouse() == false)
		{
			sender.sendMessage(ColorOptions.error + "You can't buy more houses, please buy an extra slot in the gem-shop!");
			return;
		}
		if (coins >= price)
		{
			if (house.getHouseOwnerID(houseID) == 0)
			{
				house.saveHouseOwner(houseID, user);
				user.addHouseAmount(false, 1);
				user.removeCoins(price);
				
				house.addRegionOwner(sender, houseID, manager);
				
				
				if (user.getHouseAmount(false) == 0)
				{
    				sender.sendMessage(ColorOptions.messageachievement + "Congratulations " + gender.getPrefix(user.getGenderID())  + " with your first house!");
				}
				if (house.getHouseSpawnPoint(houseID) != 0)
				{
					Integer spawnpointID = house.getHouseSpawnPoint(houseID);
					user.saveSpawnpoint(spawnpointID);
    				sender.sendMessage(ColorOptions.messageformat + "" + ChatColor.BOLD + "Your respawn location has been set to this house");
    				sender.sendMessage(ColorOptions.messageformat + "" + ChatColor.BOLD + "you can change it in " + ColorOptions.messagesubjects + "Personal menu>Settings");
				}
	    		sender.sendMessage(ColorOptions.messageachievement + "You have succesfully bought " + ColorOptions.messagesubjects + house.getHouseName(houseID) + ColorOptions.messageachievement + " on the " + ColorOptions.messagesubjects + streetName + ColorOptions.messageachievement + " with streetnumber " + ColorOptions.messagesubjects + streetNumber + ColorOptions.messageachievement + " in town " + ColorOptions.messagesubjects + town.getTownName(townID));
	    		sender.sendMessage(ColorOptions.messageachievement + "You paid " + ColorOptions.messagesubjects + ColorOptions.formatCurrency(price) + ColorOptions.messageachievement + " coins!");
			} else
			{
				sender.sendMessage(ColorOptions.error + "This house is owned by someone else");
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "You don't have enough coins, you need " + ColorOptions.formatCurrency((price-coins)) + " more coins");
		}
	}
	
	public void sellConfirm(Player sender, Integer houseID, RegionManager manager)
	{
		UUID uuid = sender.getUniqueId();
		User user = null;
		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, sender, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, sender, true);
			return;
		}
		
		Integer price = house.getHousePrice(houseID);
		
		Integer streetID = house.getStreetID(houseID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		Integer streetNumber = house.getHouseNumber(houseID);
		
		if (house.getHouseOwnerID(houseID) == user.getID())
		{
			sellconfirm.put(uuid, true);
			sellpropertyID.put(uuid, houseID);
			
			sender.sendMessage(ColorOptions.messageformat + "-Are you sure you want to sell:");
			sender.sendMessage(ColorOptions.messageformat + "-" + ColorOptions.messagesubjects + house.getHouseName(houseID) + ColorOptions.messageformat + " on the " + ColorOptions.messagesubjects + streetName + ColorOptions.messageformat + " with streetnumber " + ColorOptions.messagesubjects + streetNumber + ColorOptions.messageformat + " in town " + ColorOptions.messagesubjects + town.getTownName(townID));
			sender.sendMessage(ColorOptions.messageformat + "-You will receive " + ColorOptions.messagesubjects + ColorOptions.formatCurrency((price/2)) + ColorOptions.coinStats + " coins.");
			sender.sendMessage(ColorOptions.messageformat + "-Type " + ChatColor.GREEN + "yes" + ColorOptions.messageformat + " or " + ChatColor.RED + "no");
		} else
		{
			sender.sendMessage(ColorOptions.error + "This house is owned by someone else");
		}
	}
	
	public void infoHouse(Player sender, Integer houseID)
	{
		User user = null;
		
		try
		{
			user = Users.getUser(sender.getUniqueId());
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, sender, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, sender, true);
			return;
		}
		
		String name = house.getHouseName(houseID);
		Integer price = house.getHousePrice(houseID);
		Integer streetID = house.getStreetID(houseID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		String townName = town.getTownName(townID);
		Integer streetNumber = house.getHouseNumber(houseID);
		sender.sendMessage(ColorOptions.halfstatsbrackets + ChatColor.BOLD + "" + ColorOptions.statsresults + "Information" + ColorOptions.halfstatsbrackets);
		if (sender.hasPermission("k&k.house") || main.ownermodus.containsKey(sender.getUniqueId()))
		{
			sender.sendMessage(ColorOptions.stats + "-ID: " + ColorOptions.statsresults + houseID);
		}
		sender.sendMessage(ColorOptions.stats + "-Name: " + ColorOptions.statsresults + name);
		sender.sendMessage(ColorOptions.stats + "-Location: " + ColorOptions.statsresults + "street: " + streetName + ", streetnumber: " + streetNumber + ", town: " + townName);
		sender.sendMessage(ColorOptions.stats + "-Price: " + ColorOptions.statsresults + price);
		if (house.getHouseOwnerID(houseID) == 0) 
		{
			sender.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + "-");
		} else
		{
			Integer userID = house.getHouseOwnerID(houseID);
			UUID uuid = user.getUUID();
			Integer genderID = user.getGenderID();
			Integer titleID = user.getTitleID();
			
			sender.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + user.getUsername());
			sender.sendMessage(ColorOptions.stats + "-Titlename: " + ColorOptions.statsresults + title.getTitleName(titleID, genderID));
			sender.sendMessage(ColorOptions.stats + "-Experience: " + ColorOptions.statsresults + user.getExperience());
		}
		sender.sendMessage(ColorOptions.statsbrackets);
	}
	
	public void houseList(Player sender)
	{
		User user = null;
		
		try
		{
			user = Users.getUser(sender.getUniqueId());
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, sender, true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, sender, true);
			return;
		}
		
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of houses:");
		for (Integer houseID : house.getHouseIDList(null))
		{
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			String houseName = house.getHouseName(houseID);
			Integer price = house.getHousePrice(houseID);
			sender.sendMessage(ColorOptions.stats + "-Name: " + houseName);
			if (sender.isOp() || sender.hasPermission("k&k.house") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-ID: " + houseID);
			}
			sender.sendMessage(ColorOptions.stats + "-Price: " + price);
			if (house.getHouseOwnerID(houseID) == 0)
			{
				sender.sendMessage(ChatColor.GREEN + "Available!");
			} else if (house.getHouseOwnerID(houseID) == user.getID())
			{
				sender.sendMessage(ColorOptions.statsresults + "Owned by you!");
			} else
			{
				Integer userID = house.getHouseOwnerID(houseID);
				UUID uuid = user.getUUID();
				
				sender.sendMessage(ChatColor.RED + "Unavailable. Owner: " + user.getUsername());
			}
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
	
	public void removeHouseRegion(Player sender, Integer houseID, Integer partID, RegionManager manager)
	{
		String propertyName = house.getHouseName(houseID);
		
		manager.removeRegion("house_" + houseID + "," + partID);
		house.removeHousePart(houseID, partID);
		
		sender.sendMessage(ColorOptions.messageformat+ "Removed a part of house " + ColorOptions.messagesubjects + propertyName + ColorOptions.messageformat + " with ID " + ColorOptions.messagesubjects + partID + ColorOptions.messageformat + " on street " + ColorOptions.messagesubjects + street.getStreetName(house.getStreetID(houseID)) + ColorOptions.messageformat + " in the town of " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(house.getStreetID(houseID))));
	}
}
