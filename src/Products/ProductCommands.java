package Products;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Users.User;
import Users.Users;
import Users.offlineUser;

public class ProductCommands implements CommandExecutor
{
	offlineUser user = new offlineUser();
	Product product = new Product();
	PropertyProduct propertyproduct = new PropertyProduct();
	ProductCategory category = new ProductCategory();
	ItemType type = new ItemType();
	ProductMaterial itemMaterial = new ProductMaterial();
	private Main main;
	public ProductCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> staffcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsbrackets,
			ColorOptions.statsformat + "List of product-commands",
			ColorOptions.stats + "-/product set <name> <category> <grade(1-5)> <minprice> <maxprice> <description>",
			ColorOptions.stats + "-/product get <amount> <name>",
			ColorOptions.stats + "-/product give <player> <amount> <name>",
			ColorOptions.stats + "-/product remove <name/id>",
			ColorOptions.stats + "-/product list",
			ColorOptions.statsbrackets
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("product"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
		        StringBuilder sb = new StringBuilder();
				if (args.length > 0)
				{
					if (player.hasPermission("k&k.product"))
					{
						if (args[0].equalsIgnoreCase("set"))
						{
							if (player.getItemInHand() != null)
							{
								ItemStack item = player.getItemInHand();
								if (item.hasItemMeta())
								{
									ItemMeta meta = item.getItemMeta();
									if (args.length >= 5)
									{
										String name = args[1];
										String category = args[2];
										if (main.isInt(args[3]) && main.isInt(args[4]) && main.isInt(args[5]))
										{
											Integer grade = Integer.valueOf(args[3]);
											Integer minprice = Integer.valueOf(args[4]);
											Integer maxprice = Integer.valueOf(args[5]);
											String description = main.stringBuilder(args, 6, args.length);
											String itemName = meta.getDisplayName();
											
											if (grade > 0 && grade <= 5)
											{
												if (this.category.getCategoryID(category) != null)
												{
													Integer categoryID = this.category.getCategoryID(category);
													if (product.getProductID(itemName, false) == null)
													{
														if (getDBTypeID(player, item) != null)
														{
															Integer itemTypeID = getDBTypeID(player, item);
															Map<Enchantment, Integer> enchants = item.getEnchantments();
															for (Enchantment ench : enchants.keySet())
															{
														          sb.append(ench.getId()).append(":").append(enchants.get(ench)).append(" ");
															}
													        String allArgs = sb.toString().trim();
													        sb.setLength(0);
													        String stringlore = null;
													        if (meta.hasLore())
													        {
													        	List<String> lore = meta.getLore();
													        	for (String line : lore)
													        	{
													        		sb.append(line).append(",");
													        	}
													        	stringlore = sb.toString().trim();
													        }
															product.saveProduct(name, itemName, categoryID, itemTypeID, minprice, maxprice, grade, description, allArgs, stringlore);
															player.sendMessage(ColorOptions.messageachievement + "You succesfully saved a new product named " + itemName + ColorOptions.messageachievement + " under category " + ColorOptions.messagesubjects + category);
														} else
														{
															player.sendMessage(ColorOptions.error + "No matching item-material found in the database!");
														}
													} else
													{
														player.sendMessage(ColorOptions.error + "There already exists an item with this display-name");
													}
												} else
												{
													player.sendMessage(ColorOptions.error + "No product-category has been found named " + category);
												}
											} else
											{
												player.sendMessage(ColorOptions.error + "Grade needs to be a number between 1 and 5, it indicates the rarity of the item");
											}
										} else
										{
					                    	player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: grade: " + args[3] + ", minPrice: " + args[4] + ", maxPrice: " + args[5]);
										}
									} else
									{
										player.sendMessage(ColorOptions.falsecommand + "Usage: /product set <name> <category> <grade(1-5)> <minprice> <maxprice> <description>");
										player.sendMessage(ColorOptions.falsecommand + "NOTE: Hold the item in your hand while performing this command");
									}
								} else
								{
									player.sendMessage(ColorOptions.error + "This item doesn't have a custom display-name!");
								}
							} else
							{
								player.sendMessage(ColorOptions.error + "You need to hold the specific item in your hand");
							}
						} else
						if (args[0].equalsIgnoreCase("get"))
						{
							if (args.length >= 3)
							{
								if (main.isInt(args[1]) && !main.isInt(args[2]))
								{
									Integer amount = Integer.valueOf(args[1]);
									String name = main.stringBuilder(args, 2, args.length);
									if (product.getProductID(name, false) != null)
									{
										Integer productID = product.getProductID(name, false);
										player.getInventory().addItem(product.createPropertyItem(productID, amount, false, false));
										player.sendMessage(ColorOptions.messageachievement + "You received " + amount + " times the " + name + " in your inventory");
									} else
									{
										player.sendMessage(ColorOptions.error + "No product could be found named " + name);
									}
								} else if (main.isInt(args[1]) && main.isInt(args[2]))
								{
									Integer amount = Integer.valueOf(args[1]);
									Integer productID = Integer.valueOf(args[2]);
									if (product.getIDList(false, null, true).contains(productID))
									{
										player.getInventory().addItem(product.createPropertyItem(productID, amount, false, false));
										player.sendMessage(ColorOptions.messageachievement + "You received " + amount + " times the " + product.getProductName(productID, false) + " in your inventory");
									} else
									{
										player.sendMessage(ColorOptions.error + "No product could be found with ID " + productID);
									}
								} else
								{
			                    	player.sendMessage(ColorOptions.error + "The following command arguments need to be numbers: amount: " + args[3]);
								}
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /product get <amount> <name>");
							}
						} else if (args[0].equalsIgnoreCase("give"))
						{
							if (args.length == 4)
							{
								String username = args[1];
								String productname = args[3];
								if (Bukkit.getPlayer(username) != null)
								{
									Player target = Bukkit.getPlayer(username);
									User userTarget = null;
									
									try
									{
										userTarget = Users.getUser(target.getUniqueId());
									} catch (UserNotFoundException ex)
									{
										ErrorHandlers.userNotFoundAction(player, target, false);
										return false;
									} catch (Exception ex)
									{
										ex.printStackTrace();
										ErrorHandlers.userNotFoundAction(player, target, false);
										return false;
									}
									if (main.isInt(args[2]))
									{
										Integer amount = Integer.valueOf(args[2]);
										Integer productID = product.getProductID(productname, false);
										if (productID != null)
										{
											product.giveProduct(player, userTarget, productID, amount);
//											if (target.getInventory().firstEmpty() != -1)
//											{
//												String display = product.getDisplayName(productID, false);
//												target.getInventory().addItem(product.createPropertyItem(productID, amount, false));
//												target.sendMessage(ColorOptions.messageachievement + ColorOptions.messageArrow + "You received " + ColorOptions.messagesubjects + amount + ColorOptions.messageachievement + " times " + display);
//												player.sendMessage(ColorOptions.messageachievement + ColorOptions.messageArrow + "You gave " + ColorOptions.messagesubjects + target.getName() + amount + ColorOptions.messageachievement + " times " + display);
//											} else
//											{
//												player.sendMessage(ColorOptions.error + "Player " + target.getName() + " has no free slots in his inventory!");
//											}
										} else
										{
											player.sendMessage(ColorOptions.error + "No product could be found with name " + productname);
										}
									} else
									{
										player.sendMessage(ColorOptions.error + "The amount must be a number: " + args[2]);
									}
								} else if (this.user.existUser(username))
								{
									player.sendMessage(ColorOptions.error + "This player is not online!");
								} else
								{
									player.sendMessage(ColorOptions.error + "No player could be found named " + username);
								}
							} else
							{
								player.sendMessage(staffcommandhelp.get(4));
							}
						} else
						if (args[0].equalsIgnoreCase("remove"))
						{
							if (args.length >= 2)
							{
								if (main.isInt(args[1]))
								{
									Integer productID = Integer.valueOf(args[1]);
									if (product.getIDList(true, null, true).contains(productID))
									{
										this.removeProduct(player, productID);
									} else
									{
										player.sendMessage(ColorOptions.error + "No product could be found with ID " + productID);
									}
								} else
								{
									String productName = main.stringBuilder(args, 1, args.length);
									if (product.getProductID(productName, false) != null)
									{
										Integer productID = product.getProductID(productName, false);
										this.removeProduct(player, productID);
									} else
									{
										player.sendMessage(ColorOptions.error + "No product could be found named " + productName);
									}
								}			
							} else
							{
								player.sendMessage(ColorOptions.falsecommand + "Usage: /product remove <name/ID>");
							}
						} else
						if (args[0].equalsIgnoreCase("list"))
						{
							this.productList(player);
						}
					} else
					{
						player.sendMessage(ColorOptions.error + "You don't have permission for this command");
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
				sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
	
	public Integer getDBTypeID(Player sender, ItemStack item)
	{
		Integer typeID = null;
		String materialName = item.getType().toString().toLowerCase();
		MaterialData data = item.getData();
		Integer blockID = item.getTypeId();
		String blockIDString = null;
		
		if (data.getData() != 0)
		{
			blockIDString = blockID + ":" + data.getData();
		} else
		{
			blockIDString = blockID.toString();
		}
		typeID = this.type.getIDbyMaterialID(blockIDString);
		if (typeID != null)
		{
			return typeID;
		}
	
		return typeID;
	}
	
	public void removeProduct(Player sender, Integer productID)
	{
		String productName = product.getProductName(productID, false);
		propertyproduct.removeAllbyProduct(productID);
		sender.sendMessage(ColorOptions.messageachievement + "Succesfully removed product " + productName + " from category " + category.getCategoryName(product.getCategoryID(productID, false)) + " from the database and all properties");
		product.removeProduct(productID, false);
	}
	
	public void productList(Player sender)
	{
		sender.sendMessage(ColorOptions.statsformat + "=================================================");
		sender.sendMessage(ColorOptions.statsformat + "List of products:");
		for (Integer productID : product.getIDList(true, null, false))
		{
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");
			String productName = product.getProductName(productID, false);
			String category = this.category.getCategoryName(product.getCategoryID(productID, false));
			Integer minprice = product.getPriceMin(productID);
			Integer maxprice = product.getPriceMax(productID);
			sender.sendMessage(ColorOptions.stats + "-Name: " + productName);
			if (sender.isOp() || sender.hasPermission("k&k.property") || main.ownermodus.containsKey(sender.getUniqueId()))
			{
				sender.sendMessage(ColorOptions.stats + "-ID: " + productID);
			}
			sender.sendMessage(ColorOptions.stats + "-Category: " + category);
			sender.sendMessage(ColorOptions.stats + "-MinPrice: " + minprice);
			sender.sendMessage(ColorOptions.stats + "-MaxPrice: " + maxprice);
			if (propertyproduct.getPropertyListbyProduct(productID).size() != 0)
			{
				sender.sendMessage(ChatColor.GREEN + "-Property-ID's selling this item: " + propertyproduct.getPropertyListbyProduct(productID));
			} else
			{
				sender.sendMessage(ChatColor.RED + "-No properties selling this item");
			}	
			sender.sendMessage(ColorOptions.statsformat + "-------------------------------------------------");

		}
		sender.sendMessage(ColorOptions.statsformat + "=================================================");	
	}
}
