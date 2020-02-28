package Properties;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Main.Main;

public class PropertyCategoryCommands implements CommandExecutor
{
	PropertyCategory category = new PropertyCategory();
	public Main main;
	public PropertyCategoryCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of category-commands",
			ColorOptions.stats + "-/point list",
			ColorOptions.stats + "-/point <name>",
			ColorOptions.statsbrackets
	});
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of propertyCategory-commands",
			ColorOptions.stats + "-/propertycategory set <name> <description>",
			ColorOptions.stats + "-/propertycategory remove <name/id>",
			ColorOptions.stats + "-/propertycategory rename <oldname> <newname>",
			ColorOptions.stats + "-/propertycategory changedescription <name/id> <description>",
			ColorOptions.stats + "-/propertycatgory list",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("propertycategory") || label.equalsIgnoreCase("pc"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				if (player.hasPermission("k&k.propertycategory"))
				{
					if (args.length > 0)
					{
						if (args[0].equalsIgnoreCase("set"))
						{
							if (args.length >= 2)
							{
								String categoryName = args[1];
								if (category.getCategoryID(categoryName) == null)
								{
									String description = main.stringBuilder(args, 2, args.length);
									category.saveCategory(categoryName, description);
									player.sendMessage(ColorOptions.messageachievement + "Succesfully saved a new property-category with name " + categoryName + " with description: " + description);
								} else
								{
									player.sendMessage(ColorOptions.error + "There already exists a category with this name");
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /propertycategory <set> <name> <description>");
							}
						} else
						if (args[0].equalsIgnoreCase("remove"))
						{
							if (args.length == 2)
							{
								if (main.isInt(args[1]))
								{
									if (category.getCategoryIDList().contains(Integer.valueOf(args[1])))
									{
										Integer categoryID = Integer.valueOf(args[1]);
										String categoryName = category.getCategoryName(categoryID);
										category.removeCategory(categoryID);
										player.sendMessage(ColorOptions.messageachievement + "Succesfully removed a property-category with name " + categoryName + " with ID " + categoryID);
									} else
									{
										player.sendMessage(ColorOptions.error + "No category could be found with ID " + args[1]);
									}
								} else
								{
									if (category.getCategoryID(args[1]) != null)
									{
										Integer categoryID = category.getCategoryID(args[1]);
										category.removeCategory(categoryID);
										player.sendMessage(ColorOptions.messageachievement + "Succesfully removed a property-category with name " + args[1]);
									} else
									{
										player.sendMessage(ColorOptions.error + "No category could be found named " + args[1]);
									}
								}
							}
						} else
						if (args[0].equalsIgnoreCase("rename"))
						{
							if (args.length == 3)
							{
								if (main.isInt(args[1]))
								{
									if (category.getCategoryName(Integer.valueOf(args[1])) != null)
									{
										Integer categoryID = Integer.valueOf(args[1]);
										if (category.getCategoryID(args[2]) == null)
										{
											String oldName = category.getCategoryName(categoryID);
											category.saveName(categoryID, args[2]);
											player.sendMessage(ColorOptions.messageachievement + "Succesfully renamed property-category " + oldName + " to " + args[2]);
										} else
										{
											player.sendMessage(ColorOptions.error + "This name is already used by another property-category: " + args[2]);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No property-category has been found with ID " + args[1]);
									}
								} else
								{
									if (category.getCategoryID(args[1]) != null)
									{
										Integer categoryID = category.getCategoryID(args[1]);
										if (category.getCategoryID(args[2]) == null)
										{
											String oldName = category.getCategoryName(categoryID);
											category.saveName(categoryID, args[2]);
											player.sendMessage(ColorOptions.messageachievement + "Succesfully renamed property-category " + oldName + " to " + args[2]);
										} else
										{
											player.sendMessage(ColorOptions.error + "This name is already used by another property-category: " + args[2]);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "No property-category has bee found named " + args[1]);
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "Usage: /propertycategory rename <oldname/ID> <newname>");
							}
						} else
						if (args[0].equalsIgnoreCase("changedescription"))
						{
							if (args.length >= 3)
							{
								if (main.isInt(args[1]))
								{
									if (category.getCategoryName(Integer.valueOf(args[1])) != null)
									{
										Integer categoryID = Integer.valueOf(args[1]);
										String name = category.getCategoryName(categoryID);
										String oldDesc = category.getCategoryDescription(categoryID);
										String newDesc = main.stringBuilder(args, 2, args.length);
										category.saveDescription(categoryID, newDesc);
										player.sendMessage(ColorOptions.messageachievement + "Succesfully changed the description of property-category " + name + " from: " + oldDesc + " to: " + newDesc);
									} else
									{
										player.sendMessage(ColorOptions.error + "No property-category has been found with ID " + args[1]);
									}
								} else
								{
									if (category.getCategoryID(args[1]) != null)
									{
										Integer categoryID = category.getCategoryID(args[1]);
										String name = category.getCategoryName(categoryID);
										String oldDesc = category.getCategoryDescription(categoryID);
										String newDesc = main.stringBuilder(args, 2, args.length);
										category.saveDescription(categoryID, newDesc);
										player.sendMessage(ColorOptions.messageachievement + "Succesfully changed the description of property-category " + name + " from: " + oldDesc + " to: " + newDesc);
									} else
									{
										player.sendMessage(ColorOptions.error + "No property-category has bee found named " + args[1]);
									}
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /propertycategory changedescription <name/id> <description>");
							}
						} else
						if (args[0].equalsIgnoreCase("list"))
						{
							propertycategoryList(player);
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
					if (args.length == 1)
					{
						if (args[0].equalsIgnoreCase("list"))
						{
							propertycategoryList(player);
						} else
						{
							for (String message : commandhelp)
							{
								player.sendMessage(message);
							}
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
	
	
	public void propertycategoryList(Player sender)
	{
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of property-categories:");
		for (Integer categoryID : category.getCategoryIDList())
		{
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			String categoryName = category.getCategoryName(categoryID);
			String description = category.getCategoryDescription(categoryID);
			sender.sendMessage(ColorOptions.stats + "-Name: " + categoryName);
			if (sender.isOp() || sender.hasPermission("k&k.propertycategory") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-ID: " + categoryID);
			}
			sender.sendMessage(ColorOptions.stats + "-Description: " + description);
			
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
}
