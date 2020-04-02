package Gates;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

import API_methods.WorldEdit;
import API_methods.WorldGuard;
import DataManager.Worldguard;
import DataManager.Structures.Gates;
import Exceptions.CommandExceptions;
import Exceptions.UserIsNpcException;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Models.Structures.Gate;
import Streets.Street;
import Towns.Town;
import Users.User;
import Users.Users;

public class GateCommands implements CommandExecutor
{
	WorldEdit worldedit = new WorldEdit();
	WorldGuard worldguard = new WorldGuard();
	Street street = new Street();
	Town town = new Town();
	public Main main;
	public GateCommands(Main main) 
	{
		this.main = main;
	}
	
	public static List<String> staffcommandhelp = Arrays.asList(new String[] {
			"",
			ColorOptions.statsformat + "List of Gate-commands",
			ColorOptions.stats + "-/gate create",
			ColorOptions.stats + "-/gate remove",
			ColorOptions.stats + "-/gate toggle",
			ColorOptions.stats + "-/gate passthrough",
			ColorOptions.stats + "-/gate invincible",
			ColorOptions.stats + "-/gate info",
			ColorOptions.stats + "-/gate list",
			""
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("gate"))
		{
			if (args.length <= 0)
			{
				for (String msg : staffcommandhelp)
				{
					sender.sendMessage(msg);
				}
				return false;
			}
			
			if (args[0].equalsIgnoreCase("create"))
			{
				Player player = null;
				Integer townID = null;
				Integer streetID = null;
				
				if (!(sender instanceof Player))
				{
					sender.sendMessage(CommandExceptions.SenderNotPlayer);
					return false;
				}
				player = (Player) sender;
				if (args.length != 4)
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /gate create <name> <streetname> <facedirection>");
					return false;
				}
				if (!args[3].equalsIgnoreCase("north") && !args[3].equalsIgnoreCase("east") && !args[3].equalsIgnoreCase("south") && !args[3].equalsIgnoreCase("west"))
				{
					sender.sendMessage(ColorOptions.falsecommand + "The facedirection of the gate must be north, east, south or west");
					sender.sendMessage(ColorOptions.falsecommand + "NOTE: The facedirection must be checked when looking from the inside of the gate to the outside");
					return false;
				}
				if (!sender.hasPermission("k&k.gate"))
				{
					sender.sendMessage(CommandExceptions.NoPermission);
					return false;
				}
				if (worldedit.getWorldEdit().getSelection((Player) sender) == null)
				{
					sender.sendMessage(ColorOptions.falsecommand + "You need to make a worldedit selection first");
					return false;
				}
				townID = this.street.getTownIDbyLocation(((Player) sender).getLocation());
				if (townID == null)
				{
					sender.sendMessage(ColorOptions.falsecommand + "You need to stand inside the town of which you want to add the gate");
					return false;
				}
				streetID = this.street.getStreetID(args[2], townID);
				if (streetID == null)
				{
					sender.sendMessage(ColorOptions.falsecommand + "No street could be found in town " + this.town.getTownName(townID) + " called " + args[2]);
					return false;
				}
				if (Gates.existGate(args[1], streetID, townID))
				{
					sender.sendMessage(ColorOptions.falsecommand + "A gate already exists named " + args[1] + " on street " + args[2] + " in town " + town.getTownName(townID));
					return false;
				}
				sender.sendMessage(ColorOptions.message + "Creating new gate...");
				this.createGate(sender, args[1], streetID, townID, args[3], worldedit.getWorldEdit().getSelection(player));
			} else if (args[0].equalsIgnoreCase("remove"))
			{
				Integer gateID = null;
				Gate gate = null;

				if (!sender.hasPermission("k&k.gate"))
				{
					sender.sendMessage(CommandExceptions.NoPermission);
					return false;
				}
				
				if (args.length != 3 && args.length != 2)
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /gate remove <gateID> OR /gate remove <name> <streetname>");
					return false;
				} else if (args.length == 3)
				{
					Player player = null;
					Integer townID = null;
					Integer streetID = null;
					
					if (!(sender instanceof Player))
					{
						sender.sendMessage(CommandExceptions.SenderNotPlayer);
						return false;
					}
					player = (Player) sender;
					townID = this.street.getTownIDbyLocation(player.getLocation());
					if (townID == null)
					{
						sender.sendMessage(ColorOptions.falsecommand + "You need to stand inside the town of which you want to remove the gate");
						return false;
					}
					streetID = this.street.getStreetID(args[2], townID);
					if (streetID == null)
					{
						sender.sendMessage(ColorOptions.falsecommand + "No street could be found in town " + this.town.getTownName(townID) + " called " + args[2]);
						return false;
					}
					if (!Gates.existGate(args[1], streetID, townID))
					{
						sender.sendMessage(ColorOptions.falsecommand + "No gate with exists named " + args[1] + " on street " + args[2] + " in town " + town.getTownName(townID));
						return false;
					}
					gateID = Gates.fetchGateID(args[1], streetID, townID);
					gate = Gates.findGate(args[1], streetID, townID);
				} else if (args.length == 2)
				{
					if (!main.isInt(args[1]))
					{
						sender.sendMessage(ColorOptions.falsecommand + "The gate ID must be a number: " + args[1]);
						return false;
					}
					if (!Gates.existGate(Integer.valueOf(args[1])))
					{
						sender.sendMessage(ColorOptions.falsecommand + "No gate could be found with ID " + args[1]);
						return false;
					}
					gateID = Integer.valueOf(args[1]);
					gate = Gates.findGate(gateID);
				}
				if (gate == null)
				{
					try
					{
						gate = Gates.instantiateGate(gateID, false);
					} catch (Exception ex)
					{
						sender.sendMessage(ColorOptions.error + "An error occured while fetching the gate's information. Please notify a developer");
						ex.printStackTrace();
						return false;
					}
				}
				sender.sendMessage(ColorOptions.message + "Removing gate...");
				this.removeGate(sender, gate);
			} else if (args[0].equalsIgnoreCase("toggle"))
			{
				Integer gateID = null;
				Gate gate = null;

				if (!sender.hasPermission("k&k.gate.toggle"))
				{
					sender.sendMessage(CommandExceptions.NoPermission);
					return false;
				}
				
				if (args.length == 1)
				{
					Player player = null;
					if (!(sender instanceof Player))
					{
						sender.sendMessage(CommandExceptions.SenderNotPlayer);
						return false;
					}
					
					player = (Player) sender;
					UUID uuid = player.getUniqueId();					
					try
					{
						User user = Users.getUser(uuid);
						player.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Right-click a gate within 3 seconds to open it");
						
						if (Gates.findGateToggle(user) != null)
						{
							player.sendMessage(ColorOptions.error + "You already have an active Gate Toggle task");
							return false;
						}
						
						new GateToggle(user, false);
					} catch (UserNotFoundException ex)
					{
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					} catch (UserIsNpcException ex)
					{
						return false;
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					}
					return false;
				}
				
				if (args.length != 3 && args.length != 2)
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /gate toggle <gateID> OR /gate toggle <name> <streetname>");
					return false;
				} else if (args.length == 3)
				{
					Player player = null;
					Integer townID = null;
					Integer streetID = null;
					
					if (!(sender instanceof Player))
					{
						sender.sendMessage(CommandExceptions.SenderNotPlayer);
						return false;
					}
					player = (Player) sender;
					townID = this.street.getTownIDbyLocation(player.getLocation());
					if (townID == null)
					{
						sender.sendMessage(ColorOptions.falsecommand + "You need to stand inside the town of which you want to toggle the gate");
						return false;
					}
					streetID = this.street.getStreetID(args[2], townID);
					if (streetID == null)
					{
						sender.sendMessage(ColorOptions.falsecommand + "No street could be found in town " + this.town.getTownName(townID) + " called " + args[2]);
						return false;
					}
					if (!Gates.existGate(args[1], streetID, townID))
					{
						sender.sendMessage(ColorOptions.falsecommand + "No gate with exists named " + args[1] + " on street " + args[2] + " in town " + town.getTownName(townID));
						return false;
					}
					gateID = Gates.fetchGateID(args[1], streetID, townID);
					gate = Gates.findGate(args[1], streetID, townID);
				} else if (args.length == 2)
				{
					if (!main.isInt(args[1]))
					{
						sender.sendMessage(ColorOptions.falsecommand + "The gate ID must be a number: " + args[1]);
						return false;
					}
					if (!Gates.existGate(Integer.valueOf(args[1])))
					{
						sender.sendMessage(ColorOptions.falsecommand + "No gate could be found with ID " + args[1]);
						return false;
					}
					gateID = Integer.valueOf(args[1]);
					gate = Gates.findGate(gateID);
				}
				if (gate == null)
				{
					try
					{
						gate = Gates.instantiateGate(gateID, false);
					} catch (Exception ex)
					{
						sender.sendMessage(ColorOptions.error + "An error occured while fetching the gate's information. Please notify a developer");
						ex.printStackTrace();
						return false;
					}
				}
				sender.sendMessage(ColorOptions.message + "Toggling gate...");
				gate.toggleClosed();
			} else if (args[0].equalsIgnoreCase("passthrough") || args[0].equalsIgnoreCase("pt"))
			{
				if (!sender.hasPermission("k&k.gate.passthrough"))
				{
					sender.sendMessage(CommandExceptions.NoPermission);
					return false;
				}
				
				if (args.length == 1)
				{
					Player player = null;
					if (!(sender instanceof Player))
					{
						sender.sendMessage(CommandExceptions.SenderNotPlayer);
						return false;
					}
					
					player = (Player) sender;
					UUID uuid = player.getUniqueId();
					User user = null;
					try
					{
						user = Users.getUser(uuid);
					} catch (UserNotFoundException ex)
					{
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					} catch (UserIsNpcException ex)
					{
						return false;
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, player, true);
						return false;
					}
					if (Gates.findGateToggle(user) != null)
					{
						player.sendMessage(ColorOptions.error + "You already have an active Gate Toggle task");
						return false;
					}
					player.sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Right-click a gate within 3 seconds to pass through");
					player.sendMessage(ColorOptions.message + "It closes 2 seconds after opening");
					new GateToggle(user, true);
				} else
				{
					sender.sendMessage(ColorOptions.error + "You can only use this command to click a gate to pass through it!");
				}
			} else if (args[0].equalsIgnoreCase("invincible"))
			{
				Integer gateID = null;
				Gate gate = null;
				
				if (!sender.hasPermission("k&k.gate"))
				{
					
				}
			} else if (args[0].equalsIgnoreCase("info"))
			{
				Integer gateID = null;
				Gate gate = null;

				if (!sender.hasPermission("k&k.gate.info"))
				{
					sender.sendMessage(CommandExceptions.NoPermission);
					return false;
				}
				
				if (args.length != 3 && args.length != 2)
				{
					sender.sendMessage(ColorOptions.falsecommand + "Usage: /gate info <gateID> OR /gate info <name> <streetname>");
					return false;
				} else if (args.length == 3)
				{
					Player player = null;
					Integer townID = null;
					Integer streetID = null;
					
					if (!(sender instanceof Player))
					{
						sender.sendMessage(CommandExceptions.SenderNotPlayer);
						return false;
					}
					player = (Player) sender;
					townID = this.street.getTownIDbyLocation(player.getLocation());
					if (townID == null)
					{
						sender.sendMessage(ColorOptions.falsecommand + "You need to stand inside the town of the gate you want information about");
						return false;
					}
					streetID = this.street.getStreetID(args[2], townID);
					if (streetID == null)
					{
						sender.sendMessage(ColorOptions.falsecommand + "No street could be found in town " + this.town.getTownName(townID) + " called " + args[2]);
						return false;
					}
					if (!Gates.existGate(args[1], streetID, townID))
					{
						sender.sendMessage(ColorOptions.falsecommand + "No gate with exists named " + args[1] + " on street " + args[2] + " in town " + town.getTownName(townID));
						return false;
					}
					gateID = Gates.fetchGateID(args[1], streetID, townID);
					gate = Gates.findGate(args[1], streetID, townID);
				} else if (args.length == 2)
				{
					if (!main.isInt(args[1]))
					{
						sender.sendMessage(ColorOptions.falsecommand + "The gate ID must be a number: " + args[1]);
						return false;
					}
					if (!Gates.existGate(Integer.valueOf(args[1])))
					{
						sender.sendMessage(ColorOptions.falsecommand + "No gate could be found with ID " + args[1]);
						return false;
					}
					gateID = Integer.valueOf(args[1]);
					gate = Gates.findGate(gateID);
				}
				if (gate == null)
				{
					try
					{
						gate = Gates.instantiateGate(gateID, false);
					} catch (Exception ex)
					{
						sender.sendMessage(ColorOptions.error + "An error occured while fetching the gate's information. Please notify a developer");
						ex.printStackTrace();
						return false;
					}
				}
				this.infoGate(sender, gate);
			} else if (args[0].equalsIgnoreCase("list"))
			{
				this.listGates(sender);
			}
		}
		
		return false;
	}
	
	
	private void createGate(CommandSender sender, String name, int streetID, int townID, String faceDirection, Selection selection)
	{
		Gate gate = null;
		
		try
		{
//			Gates.createGate(sender, name, streetID, townID, -1, faceDirection);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong while creating saving the gate to the database. Please notify a developer");
			return;
		}
		
		gate = Gates.instantiateGate(name, streetID, townID, true);
		if (gate != null)
		{
			try
			{
				ProtectedCuboidRegion region = new ProtectedCuboidRegion(
						"gate_" + gate.getId(),
						new BlockVector(selection.getNativeMinimumPoint()),
						new BlockVector(selection.getNativeMaximumPoint())
						);
				
				Worldguard.getRegionManager(selection.getWorld()).addRegion(region);
				gate.addRegion(region);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				sender.sendMessage(ColorOptions.error + "Something went wrong while creating the gate-gateRegion. Please notify a developer");
				return;
			}
		} else
		{
			sender.sendMessage(ColorOptions.error + "Error while saving and retrieving new Gate from the Database. Please try again or notify a developer");
			return;
		}
		
		sender.sendMessage(ColorOptions.messageachievement + "Succesfully created a new gate called " + name + " on street with ID " + streetID + " in town with ID" + townID);
	}
	
	private void removeGate(CommandSender sender, Gate gate)
	{
		Integer ID = gate.getId();
		boolean removed = false;
		removed = gate.removePermanently(sender);
		if (removed)
		{
			sender.sendMessage(ColorOptions.messageachievement + "Succesfully removed gate " + ID + " from the database and the worldguard file!");
		}
	}
	
	private void infoGate(CommandSender sender, Gate gate)
	{
		sender.sendMessage("");
		sender.sendMessage(ColorOptions.stats + "Gate name: " + gate.getName());
		sender.sendMessage(ColorOptions.stats + "Street: " + street.getStreetName(gate.getStreetID()));
		sender.sendMessage(ColorOptions.stats + "Town: " + town.getTownName(gate.getTownID()));
		sender.sendMessage(ColorOptions.stats + "Status: " + (gate.getClosed() ? ColorOptions.error + "Closed" : ColorOptions.messagesubjects + "Opened"));
		sender.sendMessage(ColorOptions.stats + "Controllable by you: " + (sender.hasPermission("k&k.gate.toggle") ? ColorOptions.messagesubjects + "Yes" : ColorOptions.error + "No"));
		sender.sendMessage("");
	}
	
	private void listGates(CommandSender sender)
	{
		List<Integer> list = Gates.getGateList(-1);
		sender.sendMessage("");
		for (Integer ID : list)
		{
			Gate gate = Gates.instantiateGate(ID, false);
			sender.sendMessage("");
			if (sender.hasPermission("k&k.gate"))
			{
				sender.sendMessage(ColorOptions.stats + "ID: " + ColorOptions.statsresults + gate.getId());
			}
			sender.sendMessage(ColorOptions.stats + "Gate name: " + ColorOptions.statsresults + gate.getName());
			sender.sendMessage(ColorOptions.stats + "Street: " + ColorOptions.statsresults + street.getStreetName(gate.getStreetID()));
			sender.sendMessage(ColorOptions.stats + "Town: " + ColorOptions.statsresults + town.getTownName(gate.getTownID()));
			sender.sendMessage(ColorOptions.stats + "Status: " + ColorOptions.statsresults + ColorOptions.messagesubjects + "Opened");
			sender.sendMessage(ColorOptions.stats + "Controllable by you: " + (sender.hasPermission("k&k.gate.toggle") ? ColorOptions.messagesubjects + "Yes" : ColorOptions.error + "No"));
			sender.sendMessage("");
		}
		sender.sendMessage("");
	}
}
