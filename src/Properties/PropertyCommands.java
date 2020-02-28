package Properties;

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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

import API_methods.WorldEdit;
import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Genders.Gender;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Products.PropertyProduct;
import Regions.Region;
import SpawnPoints.SpawnPoint;
import Streets.Street;
import Titles.Title;
import Towns.Town;
import Users.User;
import Users.Users;
import Users.offlineUser;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class PropertyCommands implements CommandExecutor
{
	Property property = new Property();
	PropertyProduct propertyproduct = new PropertyProduct();
	SpawnPoint spawnpoint = new SpawnPoint();
	PropertyCategory category = new PropertyCategory();
	Town town = new Town();
	offlineUser user = new offlineUser();
	Users Users = new Users();
	Title title = new Title();
	Gender gender = new Gender();
	Street street = new Street();
	Region region = new Region();
	WorldGuard worldguard = new WorldGuard();
	WorldEdit worldedit = new WorldEdit();
	public Main main;
	public PropertyCommands(Main main) 
	{
		this.main = main;
	}
	
	//These 2 Hashmaps are for the confirmation of selling a property
	public static HashMap<UUID, Boolean> sellconfirm = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, Integer> sellpropertyID = new HashMap<UUID, Integer>();
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of property-commands",
			ColorOptions.stats + "-/property create <propertyName> <streetName> <townName> <streetNumber> <category> <titleID> <contribution>",
			ColorOptions.stats + "-/property remove <houseName>",
			ColorOptions.stats + "-/property part add <propertyID>",
			ColorOptions.stats + "-/property part remove <propertyID> <partID>",
			ColorOptions.stats + "-/property spawnpoint set (While standing inside the property)",
			ColorOptions.stats + "-/property spawnpoint remove <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/property buy <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/property sell <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/property info <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/property quest <reload/collect/remove>",
			ColorOptions.stats + "-/property purge <propertyID/all>",
			ColorOptions.stats + "-/property shopkeepercheck",
			ColorOptions.stats + "-/property list",
			ColorOptions.statsbrackets
	});
	public static List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of property-commands",
			ColorOptions.stats + "-/property buy <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/property sell <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/property info <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/property list",
			ColorOptions.statsbrackets
	});
	public static List<String> consolecommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of property-commands",
			ColorOptions.stats + "-/property list",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("property"))
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
				if (player.hasPermission("k&k.property"))
				{
					if (args.length > 0)
					{
						//Get the selection of worldedit a user has made to register as a house
						Selection selection = worldedit.getWorldEdit().getSelection(player);
	                    if (args[0].equalsIgnoreCase("create"))
	                    {
	                    	if (args.length == 8)
	                    	{
	                    		String name = args[1];
	                    		String streetName = args[2];
	                    		String townName = args[3];
	                    		
	                    		if (selection != null)
	                    		{
									Location max = selection.getMaximumPoint();
				                    Location min = selection.getMinimumPoint();
				                    ApplicableRegionSet regionsmax = manager.getApplicableRegions(max); 
				                    ApplicableRegionSet regionsmin = manager.getApplicableRegions(min); 
				                    if (main.isInt(args[4]) && main.isInt(args[6]) && main.isInt(args[7]))
				                    {
				                    	Integer streetNumber = Integer.valueOf(args[4]);
				                    	String category = args[5];
				                    	Integer titleID = Integer.valueOf(args[6]);
				                    	Integer contribution = Integer.valueOf(args[7]);
				                    	if (title.getIDList().contains(titleID))
				                    	{
				                    		if (main.checkContribution(contribution))
					                    	{
					                    		if (town.getTownID(townName) != null)
						                    	{
						                    		Integer townID = town.getTownID(townName);
						                    		if (street.checkStreet(streetName, townID))
						                    		{
						                    			Integer streetID = street.getStreetID(streetName, townID);
						                    			if (property.checkStreetNumber(streetNumber, streetID))
						                    			{
						                    				//The region will be created with the worldGuard API, this is nessecairy to check if there are any intersecting regions
						                    				ProtectedCuboidRegion region = new ProtectedCuboidRegion(
						                    						"property",
						                    						new BlockVector(selection.getNativeMinimumPoint()),
						                    						new BlockVector(selection.getNativeMaximumPoint())
						                    						);
						                    				if (this.region.checkUniqueRegion(manager, region, "property"))
						                    				{
						                    					if (this.category.getCategoryID(category) != null)
						                    					{
						                    						Integer categoryID = this.category.getCategoryID(category);
						                    						createProperty(player, name, townID, streetID, streetNumber, categoryID, titleID, contribution, manager, selection);
						                    					} else
						                    					{
						                    						player.sendMessage(ColorOptions.error + "No category could be found named " + category);
						                    					}
						                    				} else
						                    				{
						                    					player.sendMessage(ColorOptions.error + "You tried to overlap a different property region!");
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
					                    		player.sendMessage(ColorOptions.error + "The contribution you specified is not correct, correct contributions are: " + main.contribution);
					                    	}
				                    	} else
				                    	{
				                    		player.sendMessage(ColorOptions.error + "No title could be found with ID " + titleID);
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
	                    		player.sendMessage(ColorOptions.falsecommand + "Usage: /property create <name> <streetName> <townName> <streetNumber> <category> <titleID> <contribution(" + main.contribution + ")>");
	                    	}
	                    } else
	                    if (args[0].equalsIgnoreCase("remove"))
	                    {
	                    	if (args.length == 4)
							{
								String townName = args[1];
								String streetName = args[2];
								String streetNumberString = args[3];
								if (town.getTownID(townName) != null)
								{
									Integer townID = town.getTownID(townName);
									if (street.checkStreet(streetName, townID))
									{
										Integer streetID = street.getStreetID(streetName, townID);
										if (main.isInt(streetNumberString))
										{
											Integer streetNumber = Integer.valueOf(streetNumberString);
											if (property.checkStreetNumber(streetNumber, streetID) == false)
											{
												Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
												removeProperty(player, propertyID, manager);
											} else
											{
												player.sendMessage(ColorOptions.error + "There is no property called on the " + streetName + " with number " + streetNumber + " in the town of " + townName);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "Error: streetnumber needs to be a number: " + streetNumberString);
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
									Integer propertyID = Integer.valueOf(args[1]);
									if (property.getIDList(null, null).contains(propertyID))
									{
										removeProperty(player, propertyID, manager);
									} else
									{
										player.sendMessage(ColorOptions.error + "There is no property with ID: " + propertyID);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "Error: property ID needs to be a number: " + args[1]);
									player.sendMessage(ColorOptions.falsecommand + "Usage: /property remove <town-name> <streetname> <housenumber> OR");
									player.sendMessage(ColorOptions.falsecommand + "/property remove <propertyID>");
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /property remove <town-name> <streetname> <housenumber> OR");
								player.sendMessage(ColorOptions.falsecommand + "/property remove <propertyID>");
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
	                    						if (property.getIDList(null, null).contains(Integer.valueOf(args[2])))
	                    						{
	                    							Integer propertyID = Integer.valueOf(args[2]);
	                    							//The region will be created with the worldGuard API, this is nessecairy to check if there are any intersecting regions
				                    				ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				                    						"property",
				                    						new BlockVector(selection.getNativeMinimumPoint()),
				                    						new BlockVector(selection.getNativeMaximumPoint())
				                    						);
				                    				if (this.region.checkSameRegionID(manager, region, "property", propertyID))
				                    				{
				                    					this.addPropertyRegion(player, propertyID, manager, selection);
				                    				} else
				                    				{
				                    					player.sendMessage(ColorOptions.error + "You tried to overlap a different property region!");
				                    				}
	                    						} else
	                    						{
	                    							player.sendMessage(ColorOptions.error + "No property could be found with ID " + args[2]);
	                    						}
	                    					} else
	                    					{
	                    						player.sendMessage(ColorOptions.error + "You need to make a selection first!");
	                    					}
	                    				} else
	                    				{
	                    					player.sendMessage(ColorOptions.falsecommand + "The following command arguments need to be numbers: <propertyID> " + args[2]);
	                    				}
	                    			} else
	                    			{
	                    				player.sendMessage(ColorOptions.falsecommand + "Usage: /property part add <propertyID>");
	                    			}
	                    		} else
	                    		if (args[1].equalsIgnoreCase("remove"))
	                    		{
	                    			if (args.length == 4)
	                    			{
	                    				if (main.isInt(args[2]) && main.isInt(args[3]))
	                    				{
	                    					if (property.getIDList(null, null).contains(Integer.valueOf(args[2])))
	                    					{
	                    						Integer propertyID = Integer.valueOf(args[2]);
	                    						if (property.checkPartID(propertyID, Integer.valueOf(args[3])))
	                    						{
	                    							Integer partID = Integer.valueOf(args[3]);
	                    							this.removePropertyRegion(player, propertyID, partID, manager);
	                    						} else
	                    						{
	                    							player.sendMessage(ColorOptions.error + "No part of property with ID " + propertyID + " could be found with part-ID " + args[3]);
	                    						}
	                    					} else
	                    					{
	                    						player.sendMessage(ColorOptions.error + "No property could be found with ID " + args[2]);
	                    					}
	                    				} else
	                    				{
	                    					player.sendMessage(ColorOptions.falsecommand + "The following command arguments need to be numbers: propertyID: " + args[2] + ", partID: " + args[3]);
	                    				}
	                    			} else
	                    			{
	                    				player.sendMessage(ColorOptions.falsecommand + "Usage: /property part remove <propertyID> <partID>");
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
								player.sendMessage(ColorOptions.falsecommand + "Usage: /property part <add/remove>");
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
	                    				if (this.region.getRegion(location, "property", manager) != null)
	                    				{
	                    					ProtectedRegion region = this.region.getRegion(location, "property", manager);
											Integer propertyID = this.region.getRegionID(region);
											if (property.getPropertySpawnPoint(propertyID) == 0)
											{
												spawnpoint.saveSpawnPoint("property_" + propertyID, "0", 0, "0", "", location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
												property.savePropertySpawnPoint(propertyID, spawnpoint.getSpawnPointID("property_" + propertyID));
												player.sendMessage(ColorOptions.messageachievement + "Succesfully set the spawnpoint for property " + property.getPropertyName(propertyID) + " on " + street.getStreetName(property.getStreetID(propertyID)) + " with streetnumber " + property.getStreetNumber(propertyID) + " in town " + town.getTownName(street.getTownID(property.getStreetID(propertyID))));
											} else
											{
												player.sendMessage(ColorOptions.error + "This property already has a spawnpoint");
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "You are not standing in a property-region");
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
											if (property.getIDList(null, null).contains(Integer.valueOf(args[2])))
											{
												Integer propertyID = Integer.valueOf(args[2]);
												if (property.getPropertySpawnPoint(propertyID) != 0)
												{
													Integer spawnpointID = property.getPropertySpawnPoint(propertyID);
													property.removePropertySpawnPoint(propertyID);
													spawnpoint.removeSpawnPoint(spawnpointID);
													player.sendMessage(ColorOptions.messageachievement + "Succesfully removed the spawnpoint of a property with ID " + propertyID);
												} else
												{
													player.sendMessage(ColorOptions.error + "This property doesn't have a spawnpoint");
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "No property could be found with ID " + args[2]);
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
														if (property.getPropertyIDbyLocation(streetID, streetNumber) != null)
														{
															Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
															if (property.getPropertySpawnPoint(propertyID) != null)
															{
																Integer spawnpointID = property.getPropertySpawnPoint(propertyID);
																property.removePropertySpawnPoint(propertyID);
																spawnpoint.removeSpawnPoint(spawnpointID);
																player.sendMessage(ColorOptions.messageachievement + "Succesfully removed the spawnpoint of a property with ID " + propertyID);
															} else
															{
																player.sendMessage(ColorOptions.error + "This property doesn't have a spawnpoint");
															}
														} else
														{
															player.sendMessage(ColorOptions.error + "No property could be found on the " + streetName + " with streetnumber " + streetNumber + " in town " + townName);
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
						                    	player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: streetnumber: " + args[3]);
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
										player.sendMessage(ColorOptions.falsecommand + "/property spawnpoint remove <id>");
										player.sendMessage(ColorOptions.falsecommand + "/property spawnpoint remove <streetname> <streetnumber> <townname>");
									}
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "Usage: /property spawnpoint <set/remove>");
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /property spawnpoint <set/remove>");
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
							if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("sell") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("purge") || args[0].equalsIgnoreCase("shopkeepercheck"))
							{
								
							} else
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
										if (property.getPropertyIDbyLocation(streetID, streetNumber) != null)
										{
											Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
											this.buyProperty(user, propertyID, manager);
										} else
										{
											player.sendMessage(ColorOptions.error + "No property could be found on street " + streetName + " with number " + streetNumber);
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
								Integer propertyID = Integer.valueOf(args[1]);
								if (property.getIDList(null, null).contains(propertyID))
								{
									this.buyProperty(user, propertyID, manager);
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "No property could be found with ID " + propertyID);
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: propertyID: " + args[1]);
							}
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "Usage: /property buy <streetName> <streetNumber> <townName>");
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
										if (property.getPropertyIDbyLocation(streetID, streetNumber) != null)
										{
											Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
											this.sellConfirm(user, propertyID, manager);
										} else
										{
											player.sendMessage(ColorOptions.error + "No property could be found on street " + streetName + " with number " + streetNumber);
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
							player.sendMessage(ColorOptions.falsecommand + "Usage: /property sell <streetName> <streetNumber> <townName>");
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
										if (property.getPropertyIDbyLocation(streetID, streetNumber) != null)
										{
											Integer propertyID = property.getPropertyIDbyLocation(streetID, streetNumber);
											this.infoProperty(user, propertyID);
										} else
										{
											player.sendMessage(ColorOptions.error + "No property could be found on street " + streetName + " with number " + streetNumber);
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
							player.sendMessage(ColorOptions.falsecommand + "Usage: /property info <streetName> <streetNumber> <townName>");
						}
					} else if (args[0].equalsIgnoreCase("purge"))
					{
						if (args.length == 2)
						{
							Integer propertyID = null;
							boolean all = false;
							if (args[1].equalsIgnoreCase("all"))
							{
								all = true;
							} else
							if (main.isInt(args[1]))
							{
								Integer id = Integer.valueOf(args[1]);
								if (property.getIDList(null, null).contains(id))
								{
									propertyID = id;
								} else
								{
									sender.sendMessage(ColorOptions.error + "No property could be found with ID " + id);
								}
							} else
							{
								sender.sendMessage(ColorOptions.error + "No correct argument found: " + args[1]);
							}
							sender.sendMessage(ColorOptions.message + "Purging all shopkeepers for all properties...");
							property.purgePropertyNPC(sender);
							
							if (all)
							{
								for (Integer propertyID2 : property.getIDList(null, null))
								{
									property.purgePropertyOwner(propertyID2);
									sender.sendMessage(ColorOptions.messageachievement + "Purged a property with ID " + propertyID2);
								}
							} else if (propertyID != null)
							{
								property.purgePropertyOwner(propertyID);
								sender.sendMessage(ColorOptions.messageachievement + "Purged a property with ID " + propertyID);
							} else
							{
								sender.sendMessage(ColorOptions.error + "");
							}
						} else
						{
							sender.sendMessage(staffcommandhelp.get(11));
						}
					} else if (args[0].equalsIgnoreCase("shopkeepercheck"))
					{
						Integer shopkeepernames = 0;
						Integer propertyshopkeepers = 0;
						NPCRegistry registry = CitizensAPI.getNPCRegistry();
						for (Entity entity : player.getWorld().getEntities())
						{
							if (registry.isNPC(entity))
							{
								NPC npc = registry.getNPC(entity);
								if (npc.getName().equalsIgnoreCase("shopkeeper"))
								{
									shopkeepernames++;
								}
							} else if (entity.getName().equalsIgnoreCase("shopkeeper"))
							{
								shopkeepernames++;
							}
						}
						for (Integer propertyID : property.getIDList(null, null))
						{
							if (property.getNPCID(propertyID) != null && property.getNPCID(propertyID) != 0)
							{
								propertyshopkeepers++;
							}
						}
						
						if (shopkeepernames == propertyshopkeepers)
						{
							sender.sendMessage(ColorOptions.messageachievement + "Succes! There are " + ColorOptions.messagesubjects + shopkeepernames + ColorOptions.messageachievement + " npc's named 'shopkeeper' and " + ColorOptions.messagesubjects + propertyshopkeepers + ColorOptions.messageachievement + " properties with a shopkeeper!");
						} else
						{
							sender.sendMessage(ColorOptions.error + "Error! There are " + ColorOptions.messagesubjects + shopkeepernames + ColorOptions.error + " npc's named 'shopkeeper' and " + ColorOptions.messagesubjects + propertyshopkeepers + ColorOptions.error + " properties with a shopkeeper!");
							sender.sendMessage(ColorOptions.error + ColorOptions.messageArrow + "This could also be caused by memorysaving, if players are not in a town the npc's despawn");
							sender.sendMessage(ColorOptions.error + ColorOptions.messageArrow + "If you think this is not the case try using the command /property purge all");
						}
					} else
					if (args[0].equalsIgnoreCase("list"))
					{
						this.propertyList(user);
					}
				} else
				{
					if (player.hasPermission("k&k.property"))
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
 				if (args.length == 1)
 				{
 					if (args[0].equalsIgnoreCase("list"))
 					{
 						sender.sendMessage(ColorOptions.statsformat + "=================================================");
 						sender.sendMessage(ColorOptions.statsformat + "List of properties:");
 						for (Integer propertyID : property.getIDList(null, null))
 						{
 							sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
 							String propertyName = property.getPropertyName(propertyID);
 							String category = this.category.getCategoryName(property.getCategoryID(propertyID));
 							Integer price = property.getPropertyPrice(propertyID);
 							Integer income = property.getIncome(propertyID);
 							sender.sendMessage(ColorOptions.stats + "-Name: " + propertyName);
							sender.sendMessage(ColorOptions.stats + "-ID: " + propertyID);
 							sender.sendMessage(ColorOptions.stats + "-Category: " + category);
 							sender.sendMessage(ColorOptions.stats + "-Price: " + price);
 							sender.sendMessage(ColorOptions.stats + "-Income: " + income);
 							if (property.getPropertyOwnerID(propertyID) == 0)
 							{
 								sender.sendMessage(ChatColor.GREEN + "Available!");
 							} else
 							{
 								Integer userID = property.getPropertyOwnerID(propertyID);
 								UUID uuid = Users.fetchUUIDbyID(userID);
 								
 								sender.sendMessage(ChatColor.RED + "Unavailable. Owner: " + Users.fetchUsernamebyUUID(uuid));
 							}	
 							sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

 						}
 						sender.sendMessage(ColorOptions.statsformat + "=================================================");	
 					} else
 					{
 						sender.sendMessage(ColorOptions.falsecommand + "Usage: /property list");
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
	
	private void createProperty(Player sender, String name, Integer townID, Integer streetID, Integer streetNumber, Integer categoryID, Integer titleID, Integer contribution, RegionManager regionManager, Selection worldEditSelection)
	{
		Integer salary = title.getSalary(titleID);
		Integer dailySalary = salary*4;
		Integer timePrice = main.getRandom(dailySalary*30, dailySalary*60);
		Integer price = Integer.valueOf((int) (Math.round((timePrice*(1+(contribution/100)))/100.0)*100));
		Integer income = Integer.valueOf((int) (Math.round(((price-(price/4))/100)/100.0)*100));
		
		//Save the house to the database
		property.saveProperty(name, streetID, streetNumber, income, price, categoryID, contribution);

		//Get the houseID of the new house
		Integer propertyID = property.getPropertyID(name, streetID, streetNumber);
		
		//The region will be created with the worldGuard API
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
				"property_" + propertyID,
				new BlockVector(worldEditSelection.getNativeMinimumPoint()),
				new BlockVector(worldEditSelection.getNativeMaximumPoint())
				);
		
		regionManager.addRegion(region);
		
		//Set all flags for the region
		region.setFlag(DefaultFlag.ENTRY, State.ALLOW);
		region.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, "");
		region.setFlag(DefaultFlag.DENY_MESSAGE, "");
		region.setPriority(Integer.valueOf(11));

		//Manage some additional flags
		sender.performCommand("rg flag " + ("property_" + propertyID) + " deny-blocks any");
	
		sender.sendMessage(ColorOptions.messageformat + "Succesfully created a property with the name " + ColorOptions.messagesubjects + name + ColorOptions.messageformat + " located in " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(streetID)) + " by the " + street.getStreetName(streetID) + " with number " + streetNumber + " in category " + category.getCategoryName(categoryID));
		sender.sendMessage(ColorOptions.messageachievement + "The price and income are balanced for the titles " + ColorOptions.messagesubjects + title.getTitleName(titleID, 1) + ColorOptions.messageachievement + " or above");
		sender.sendMessage(ColorOptions.messageachievement + "Price: " + ColorOptions.messagesubjects + price + ColorOptions.messageachievement + ", Income: " + ColorOptions.messagesubjects + income);
	}
	
	public void removeProperty(Player sender, Integer propertyID, RegionManager manager)
	{
		Integer streetID = property.getStreetID(propertyID);
		Integer townID = street.getTownID(streetID);
		String streetName = street.getStreetName(streetID);
		Integer number = property.getStreetNumber(propertyID);
		String townName = town.getTownName(townID);
		String propertyName = property.getPropertyName(propertyID);
		
		//If the house had an owner, remove a house-amount from the owner
		if (property.getPropertyOwnerID(propertyID) != 0)
		{
			Integer userID = property.getPropertyOwnerID(propertyID);
			UUID uuid = Users.fetchUUIDbyID(userID);
			User userTarget = Users.getUser(uuid);
			
			if (userTarget != null)
			{
				userTarget.removePropertyAmount(false, 1);
			} else
			{
				this.user.removePropertyAmount(uuid, 1);
			}
		}
		
		for (Integer subID : property.getPropertyPartList(propertyID))
		{
			manager.removeRegion("property_" + propertyID + "," + subID);
			property.removePropertyPart(propertyID, subID);
		}
		
		manager.removeRegion("property_" + propertyID);
		propertyproduct.removeAllbyProperty(propertyID);
		property.removePropertybyID(propertyID);
		
    	sender.sendMessage(ColorOptions.messageformat + "The property with ID " + ColorOptions.messagesubjects + propertyID + ColorOptions.messageformat + " and name " + ColorOptions.messagesubjects + propertyName + ColorOptions.messageformat + " with number " + number + " on the " + streetName + " in " + townName + " has succesfully been removed");
	}
	
	public void addPropertyRegion(Player sender, Integer propertyID, RegionManager manager, Selection selection)
	{
		property.savePropertyPart(propertyID);
		Integer partID = null;
		ArrayList<Integer> partList = property.getPropertyPartList(propertyID);
		
		//Check for every partID if there is an existing region, if not, that id is the new partID
		for (Integer id : partList)
		{
			if (!manager.getRegions().containsKey("property_" + propertyID + "," + id))
			{
				partID = id;
				break;
			}
		}
		
		//Create the actual WorldGuard region
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                "property_" + propertyID + "," + partID,
                new BlockVector(selection.getNativeMinimumPoint()),
                new BlockVector(selection.getNativeMaximumPoint())
				);
		//manager.addRegion(region);
		
		//Try to set the property region as parent of the sub-region
		try 
		{
			region.setParent(manager.getRegion("property_" + propertyID));
		} catch (CircularInheritanceException e) 
		{
			e.printStackTrace();
		}
		
		sender.sendMessage(ColorOptions.messageformat + "Succesfully added a part to property " + ColorOptions.messagesubjects + property.getPropertyName(propertyID) + ColorOptions.messageformat + " on street " + ColorOptions.messagesubjects + street.getStreetName(property.getStreetID(propertyID)) + ColorOptions.messageformat + " in the town of " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(property.getStreetID(propertyID))));

	}
	
	public void removePropertyRegion(Player sender, Integer propertyID, Integer partID, RegionManager manager)
	{
		String propertyName = property.getPropertyName(propertyID);
		
		manager.removeRegion("property_" + propertyID + "," + partID);
		property.removePropertyPart(propertyID, partID);
		
		sender.sendMessage(ColorOptions.messageformat+ "Removed a part of property " + ColorOptions.messagesubjects + propertyName + ColorOptions.messageformat + " with ID " + ColorOptions.messagesubjects + partID + ColorOptions.messageformat + " on street " + ColorOptions.messagesubjects + street.getStreetName(property.getStreetID(propertyID)) + ColorOptions.messageformat + " in the town of " + ColorOptions.messagesubjects + town.getTownName(street.getTownID(property.getStreetID(propertyID))));
	}
	
	public void buyProperty(User user, Integer propertyID, RegionManager manager)
	{
		UUID uuid = user.getUUID();
		Player sender = user.getPlayer();
		Integer coins = user.getCoins();
		Integer price = property.getPropertyPrice(propertyID);
		Integer income = property.getIncome(propertyID);
		Integer propertyAmount = user.getPropertyAmount(false);
		Integer propertyMax = user.getPropertyAmount(true);
		
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		Integer streetNumber = property.getStreetNumber(propertyID);
		
		if (propertyAmount+1 > propertyMax)
		{
			user.getPlayer().sendMessage(ColorOptions.error + "You can't buy more properties, please buy an extra slot in the gem-shop!");
			return;
		}
		if (coins >= price)
		{
			if (property.getPropertyOwnerID(propertyID) == 0)
			{
				property.savePropertyOwner(propertyID, user);
				user.saveIncomeTime();
				user.removeCoins(price);
				
				property.addRegionOwner(user.getPlayer(), propertyID, manager);
				
				if (user.getPropertyAmount(false) == 0)
				{
    				sender.sendMessage(ColorOptions.messageachievement + "Congratulations " + gender.getPrefix(user.getGenderID())  + " with your first property!");
				}
	    		sender.sendMessage(ColorOptions.messageachievement + "You have succesfully bought " + ColorOptions.messagesubjects + property.getPropertyName(propertyID) + ColorOptions.messageachievement + " on the " + ColorOptions.messagesubjects + streetName + ColorOptions.messageachievement + " with streetnumber " + ColorOptions.messagesubjects + streetNumber + ColorOptions.messageachievement + " in town " + ColorOptions.messagesubjects + town.getTownName(townID));
	    		sender.sendMessage(ColorOptions.messageachievement + "You paid " + ColorOptions.messagesubjects + ColorOptions.formatCurrency(price) + ColorOptions.messageachievement + " coins!");
			} else
			{
				sender.sendMessage(ColorOptions.error + "This property is owned by someone else");
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "You don't have enough coins, you need " + ColorOptions.formatCurrency((price-coins)) + " more coins");
		}
	}
	
	public void sellConfirm(User user, Integer propertyID, RegionManager manager)
	{
		UUID uuid = user.getUUID();
		Player sender = user.getPlayer();
		Integer price = property.getPropertyPrice(propertyID);
		
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		Integer streetNumber = property.getStreetNumber(propertyID);
		
		if (property.getPropertyOwnerID(propertyID) == user.getID())
		{
			sellconfirm.put(uuid, true);
			sellpropertyID.put(uuid, propertyID);
			
			sender.sendMessage(ColorOptions.messageformat + "-Are you sure you want to sell:");
			sender.sendMessage(ColorOptions.messageformat + "-" + ColorOptions.messagesubjects + property.getPropertyName(propertyID) + ColorOptions.messageformat + " on the " + ColorOptions.messagesubjects + streetName + ColorOptions.messageformat + " with streetnumber " + ColorOptions.messagesubjects + streetNumber + ColorOptions.messageformat + " in town " + ColorOptions.messagesubjects + town.getTownName(townID));
			sender.sendMessage(ColorOptions.messageformat + "-You will receive " + ColorOptions.messagesubjects + ColorOptions.formatCurrency((price/2)) + ColorOptions.coinStats + " coins.");
			sender.sendMessage(ColorOptions.messageformat + "-Type " + ChatColor.GREEN + "yes" + ColorOptions.messageformat + " or " + ChatColor.RED + "no");
		} else
		{
			sender.sendMessage(ColorOptions.error + "This property is owned by someone else");
		}
	}
	
	public void infoProperty(User user, Integer propertyID)
	{
		Player sender = user.getPlayer();
		String name = property.getPropertyName(propertyID);
		Integer price = property.getPropertyPrice(propertyID);
		Integer income = property.getIncome(propertyID);
		Integer streetID = property.getStreetID(propertyID);
		String streetName = street.getStreetName(streetID);
		Integer townID = street.getTownID(streetID);
		String townName = town.getTownName(townID);
		Integer level = property.getLevel(propertyID);
		String category = this.category.getCategoryName(property.getCategoryID(propertyID));
		sender.sendMessage(ColorOptions.halfstatsbrackets + ChatColor.BOLD + "" + ColorOptions.statsresults + "Information" + ColorOptions.halfstatsbrackets);
		if (sender.hasPermission("k&k.property") || main.ownermodus.containsKey(sender.getUniqueId()))
		{
			sender.sendMessage(ColorOptions.stats + "-ID: " + ColorOptions.statsresults + propertyID);
		}
		sender.sendMessage(ColorOptions.stats + "-Name: " + ColorOptions.statsresults + name);
		sender.sendMessage(ColorOptions.stats + "-Category: " + ColorOptions.statsresults + category);
		sender.sendMessage(ColorOptions.stats + "-Location: " + ColorOptions.statsresults + "street: " + streetName + ", streetnumber: " + property.getStreetNumber(propertyID) + ", town: " + townName);
		sender.sendMessage(ColorOptions.stats + "-Price: " + ColorOptions.statsresults + price);
		sender.sendMessage(ColorOptions.stats + "-Income: " + ColorOptions.statsresults + income);
		sender.sendMessage(ColorOptions.stats + "-Level: " + ColorOptions.statsresults + level);
		if (property.getPropertyOwnerID(propertyID) == 0)
		{
			sender.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + "-");
		} else
		{
			Integer userID = property.getPropertyOwnerID(propertyID);
			UUID uuid = this.user.getUUIDbyID(userID);
			Integer genderID = this.user.getGenderID(uuid);
			Integer titleID = this.user.getTitleID(uuid);
			
			sender.sendMessage(ColorOptions.stats + "-Owner: " + ColorOptions.statsresults + this.user.getUserName(uuid));
			sender.sendMessage(ColorOptions.stats + "-Titlename: " + ColorOptions.statsresults + title.getTitleName(titleID, genderID));
			sender.sendMessage(ColorOptions.stats + "-Experience: " + ColorOptions.statsresults + this.user.getExperience(uuid));
		}
		sender.sendMessage(ColorOptions.statsbrackets);
	}
	
	public void propertyList(User user)
	{
		Player sender = user.getPlayer();
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of properties:");
		for (Integer propertyID : property.getIDList(null, null))
		{
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			String propertyName = property.getPropertyName(propertyID);
			String category = this.category.getCategoryName(property.getCategoryID(propertyID));
			Integer price = property.getPropertyPrice(propertyID);
			Integer income = property.getIncome(propertyID);
			sender.sendMessage(ColorOptions.stats + "-Name: " + propertyName);
			if (sender.isOp() || sender.hasPermission("k&k.property") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-ID: " + propertyID);
			}
			sender.sendMessage(ColorOptions.stats + "-Category: " + category);
			sender.sendMessage(ColorOptions.stats + "-Price: " + price);
			sender.sendMessage(ColorOptions.stats + "-Income: " + income);
			if (property.getPropertyOwnerID(propertyID) == 0)
			{
				sender.sendMessage(ChatColor.GREEN + "Available!");
			} else if (property.getPropertyOwnerID(propertyID) == user.getID())
			{
				sender.sendMessage(ColorOptions.statsresults + "Owned by you!");
			} else
			{
				Integer userID = property.getPropertyOwnerID(propertyID);
				UUID uuid = this.user.getUUIDbyID(userID);
				
				sender.sendMessage(ChatColor.RED + "Unavailable. Owner: " + this.user.getUserName(uuid));
			}	
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
	
}
