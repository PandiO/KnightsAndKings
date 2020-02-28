package Resources;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Streets.Street;
import Towns.Town;
import Users.User;
import Users.Users;

public class ResourceCommands implements CommandExecutor
{
	ResourceProperty resourceProperty = new ResourceProperty();
	ResourceCategory resourceCategory = new ResourceCategory();
	YmlFile file = new YmlFile();
	WorldGuard worldguard = new WorldGuard();
	Street street = new Street();
	Town town = new Town();
	public Main main;
	public ResourceCommands(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of resourceProperty-commands",
			ColorOptions.stats + "-/rp create <resourceCategory> <streetName> <streetNumber> <townName>",
			ColorOptions.stats + "-/rp remove <propertyID>",
			ColorOptions.stats + "-/rp blocks <reload>",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("resourceproperty") || label.equalsIgnoreCase("rp"))
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
				
				if (player.hasPermission("k&k.resourceproperty"))
				{
					if (args.length >= 1)
					{
						String subCommand = args[0];
						if (subCommand.equalsIgnoreCase("create"))
						{
							if (args.length == 3)
							{
								Integer categoryID = resourceCategory.getCategoryID(args[1]);
								if (categoryID != null)
								{
									if (main.isInt(args[2]))
									{
										Integer propertyID = Integer.valueOf(args[1]);
										if (resourceProperty.getIDList(null, null).contains(propertyID))
										{
											if (!resourceProperty.getResourcePropertyIDList(true, categoryID).contains(propertyID))
											{
												this.createResourceProperty(player, propertyID, categoryID);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No property could be found with ID " + propertyID);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "The propertyID has to be a number: " + args[2]);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "No resource-category could be found named " + args[1]);
								}
							} else if (args.length == 5)
							{
								String category = args[1];
								String streetName = args[2];
								String townName = args[4];
								if (main.isInt(args[3]))
								{
									Integer streetNumber = Integer.valueOf(args[3]);
									Integer townID = town.getTownID(townName);
									if (townID != null)
									{
										Integer streetID = street.getStreetID(streetName, townID);
										if (streetID != null)
										{
											Integer propertyID = resourceProperty.getPropertyIDbyLocation(streetID, streetNumber);
											if (propertyID != null)
											{
												Integer categoryID = resourceCategory.getCategoryID(args[1]);
												if (categoryID != null)
												{
													if (!resourceProperty.getResourcePropertyIDList(true, categoryID).contains(propertyID))
													{
														this.createResourceProperty(player, propertyID, categoryID);
													} else
													{
														player.sendMessage(ColorOptions.error + "This property is already registered as a Resource-property!");
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "No resource-category could be found named " + args[1]);
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "No property could be found with on the " + streetName + " with number " + streetNumber + " in the town of " + townName);
											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No street could be found named " + streetName + " in the town of " + townName);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No town could be found named " + townName);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "The streetnumber has to be a number: " + args[3]);
								}
							} else
							{
								player.sendMessage(staffcommandhelp.get(2));
							}
						} else if (subCommand.equalsIgnoreCase("remove"))
						{
							if (args.length == 2)
							{
								if (main.isInt(args[2]))
								{
									Integer propertyID = Integer.valueOf(args[1]);
									if (resourceProperty.getIDList(null, null).contains(propertyID))
									{
										if (resourceProperty.getResourcePropertyIDList(false, null).contains(propertyID))
										{
											resourceProperty.removeResourceProperty(propertyID);
											RegionManager manager = worldguard.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
							                ProtectedRegion region = manager.getRegion("property_" + propertyID);
							                if (region != null)
							                {
							                	region.setFlag(DefaultFlag.BLOCK_BREAK, State.DENY);
							                }
											player.sendMessage(ColorOptions.messageachievement + "Succesfully removed a Resourceproperty of a property with ID " + ColorOptions.messagesubjects + propertyID);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No property could be found with ID " + propertyID);
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "The propertyID has to be a number: " + args[2]);
								}
							} else
							{
								player.sendMessage(staffcommandhelp.get(3));
							}
						} else if (subCommand.equalsIgnoreCase("blocks"))
						{
							if (args.length == 2)
							{
								if (args[1].equalsIgnoreCase("reload"))
								{
									if (BlockBreakEvents.refreshList.size() > 50)
									{
										player.sendMessage(ColorOptions.message + "This might take a while..");
									}
									main.refreshResources(user);					    			
								} else
								{
									player.sendMessage(staffcommandhelp.get(4));
								}
							} else
							{
								player.sendMessage(staffcommandhelp.get(4));
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
				} else
				{
					player.sendMessage(ColorOptions.falsecommand + "You don't have permission for this command!");
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
			}
		}
		
		return false;
	}
	
	public void createResourceProperty(Player player, Integer propertyID, Integer categoryID)
	{
		String categoryName = resourceCategory.getCategoryName(categoryID);
		resourceProperty.saveResourceProperty(propertyID, categoryID);
        RegionManager manager = worldguard.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
        ProtectedRegion region = manager.getRegion("property_" + propertyID);
        if (region != null)
        {
        	region.setFlag(DefaultFlag.BLOCK_BREAK, State.ALLOW);
            String destroyBlock = null;
            if (categoryName.equalsIgnoreCase("stonequarry"))
            {
            	destroyBlock = "1";
            } else if (categoryName.equalsIgnoreCase("coalmine"))
            {
            	destroyBlock = "1 16";
            } else if (categoryName.equalsIgnoreCase("ironmine"))
            {
            	destroyBlock = "1 15";
            } else if (categoryName.equalsIgnoreCase("goldmine"))
            {
            	destroyBlock = "1 14";
            } else if (categoryName.equalsIgnoreCase("gemmine"))
            {
            	destroyBlock = "1 56";
            } else if (categoryName.equalsIgnoreCase("wheatfarm"))
            {
            	destroyBlock = "59";
            } else if (categoryName.equalsIgnoreCase("lumberjack"))
            {
            	destroyBlock = "17";
            }
    		player.performCommand("rg flag " + ("property_" + propertyID) + " allow-blocks " + destroyBlock);
        }
		player.sendMessage(ColorOptions.messageachievement + "Succesfully created a Resourceproperty of category " + ColorOptions.messagesubjects + resourceCategory.getCategoryName(categoryID) + ColorOptions.messageachievement + " of property with ID " + ColorOptions.messagesubjects + propertyID);
	}
	
	public void changeFlags(Player player, Integer propertyID, Integer categoryID)
	{
		String categoryName = resourceCategory.getCategoryName(categoryID);
        RegionManager manager = worldguard.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
        ProtectedRegion region = manager.getRegion("property_" + propertyID);
        if (region != null)
        {
        	player.performCommand("rg flag " + ("property_" + propertyID) + " allow-blocks");
        	player.performCommand("rg flag " + ("property_" + propertyID) + " deny-blocks");
        	region.setFlag(DefaultFlag.BLOCK_BREAK, State.ALLOW);
            String destroyBlock = null;
            if (categoryName.equalsIgnoreCase("stonequarry"))
            {
            	destroyBlock = "1";
            } else if (categoryName.equalsIgnoreCase("coalmine"))
            {
            	destroyBlock = "1, 16";
            } else if (categoryName.equalsIgnoreCase("ironmine"))
            {
            	destroyBlock = "1, 15";
            } else if (categoryName.equalsIgnoreCase("goldmine"))
            {
            	destroyBlock = "1, 14";
            } else if (categoryName.equalsIgnoreCase("gemmine"))
            {
            	destroyBlock = "1, 56";
            } else if (categoryName.equalsIgnoreCase("wheatfarm"))
            {
            	destroyBlock = "59";
            } else if (categoryName.equalsIgnoreCase("lumberjack"))
            {
            	destroyBlock = "17";
            }
    		player.performCommand("rg flag " + ("property_" + propertyID) + " allow-blocks " + destroyBlock);
    		player.sendMessage(ColorOptions.messageachievement + "Reset build flags for resource-property with ID " + propertyID);
        } else
        {
        	player.sendMessage(ColorOptions.error + "Couldn't find region for resource-property with ID " + propertyID);
        }
	}
}
