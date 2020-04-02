package Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.sk89q.worldguard.protection.managers.RegionManager;

import API_methods.WorldEdit;
import DataManager.Worldguard;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.ResourceBlockBreakEvent;
import Main.Main;
import Products.ItemType;
import Products.Product;
import Users.User;
import Users.Users;

public class BlockBreakEvents implements Listener
{
	WorldEdit worldedit = new WorldEdit();
	ResourceProperty property = new ResourceProperty();
	ResourceCategory category = new ResourceCategory();
	YmlFile file = new YmlFile();
	Product product = new Product();
	ItemType itemType = new ItemType();
	private Main main;
	public BlockBreakEvents(Main main) 
	{
		this.main = main;
	}
	Location loc;
	ArrayList<Material> minelist = new ArrayList<Material>(Arrays.asList(
			Material.STONE,
			Material.COAL_ORE,
			Material.IRON_ORE,
			Material.GOLD_ORE,
			Material.DIAMOND_ORE,
			Material.EMERALD_ORE
			));
	
	ArrayList<String> woodIDList = new ArrayList<String>(Arrays.asList(
			"17",
			"17:1",
			"17:2",
			"17:3",
			"17:4"
			
			));
	private Integer cooldown = 25;
//	public static HashMap<Block, Long> refreshList = new HashMap<Block, Long>();
	public static List<BlockRefresh> refreshList = new CopyOnWriteArrayList<BlockRefresh>();
		
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e)
	{
		Block block = e.getBlock();
		Location blockLocation = block.getLocation();
		Material type = block.getType();
		Long refreshLong = (System.currentTimeMillis() + (cooldown*1000));
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		User user = null;
		
		if (this.worldedit.getWorldEdit().getSession(player) != null && player.getItemInHand() != null && player.getItemInHand().getType() == Material.WOOD_AXE)
		{
			e.setCancelled(true);
			return;
		}

		
		try
		{
			user = Users.getUser(uuid);
		} catch (UserNotFoundException ex)
		{
			ErrorHandlers.userNotFoundAction(null, player, true);
			e.setCancelled(true);
			return;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			ErrorHandlers.userNotFoundAction(null, player, true);
			e.setCancelled(true);
			return;
		}
		
		RegionManager manager = Worldguard.getRegionManager(blockLocation.getWorld());
		Integer townID = Worldguard.getStructureIDbyRegion("town", blockLocation, manager);
		Integer propertyID = Worldguard.getStructureIDbyRegion("property", blockLocation, manager);
		//Check if player is allowed to break the block and if it should drop loot
		if (user.inOwnerModus() == false && user.inStaffModus() == false && player.getGameMode() == GameMode.SURVIVAL)
		{
			//Check if the block's location is inside the gateRegion of a resource-property (where breaking blocks is allowed)
			if (propertyID != null && property.getResourcePropertyIDList(false, null).contains(propertyID))
			{
//				Integer categoryID = property.getResourceCategory(propertyID);
//				loc = blockLocation;
//				String categoryName = category.getCategoryName(categoryID);
//				//Combine blocktype ID with the data (can FE be the subtype of the material or the block facing a specific direction)
//				String IDwithData = block.getTypeId() + ((block.getData() != -1) ? ":" + block.getData() : null);
//				
//				//Check if the block's material is allowed to be broken
//				if (minelist.contains(block.getType()))
//				{
//					if (categoryName.equalsIgnoreCase("stonequarry") || categoryName.equalsIgnoreCase("coalmine") || categoryName.equalsIgnoreCase("ironmine") || categoryName.equalsIgnoreCase("goldmine") || categoryName.equalsIgnoreCase("gemmine"))
//					{
//				    	file.saveBlock("ore-resources", blockLocation, block.getTypeId() + "", property.getCooldown(propertyID));
//					} else
//					{
//						if (main.debug)
//						{
//							Bukkit.getConsoleSender().sendMessage("Type of property not matching!");
//						}
//						e.setCancelled(true);
//					}
//				}
//				//Wheat
//				else if (block.getType() == Material.getMaterial(59))
//				{
//					refreshList.add(new BlockRefresh(block.getTypeId(), block.getData(), block.getLocation(), refreshLong));
//					loc = blockLocation;
//				} else if (this.woodIDList.contains(IDwithData))
//				{
//					refreshList.add(new BlockRefresh(block.getTypeId(), block.getData(), block.getLocation(), refreshLong));
//					loc = blockLocation;
//				}
//				
//				else
//				{
//					if (main.debug)
//					{
//						Bukkit.getConsoleSender().sendMessage("Blocktype not matching");
//					}
//					e.setCancelled(true);
//				}
//				e.setExpToDrop(0);
				e.setCancelled(true);
				try
				{
					Bukkit.getServer().getPluginManager().callEvent(new ResourceBlockBreakEvent(user, player.getItemInHand(), block, propertyID));
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			} else
			if (townID == null && propertyID == null)
			{
				e.setCancelled(true);
				try
				{
					Bukkit.getServer().getPluginManager().callEvent(new ResourceBlockBreakEvent(user, player.getItemInHand(), block, propertyID));
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
//				if (block.getTypeId() == 17)
//				{	
////					Material mat = block.getType();
////					StringBuilder MaterialID = new StringBuilder();
////					MaterialID.append(mat.getId());
////					if (block.getData() != -1)
////					{
////						MaterialID.append(":");
////						if (block.getData() == 1 || block.getData() == 5 || block.getData() == 9)
////						{
////							MaterialID.append("1");
////						} else if (block.getData() == 2 || block.getData() == 6 || block.getData() == 10)
////						{
////							MaterialID.append("2");
////						} else if (block.getData() == 3 || block.getData() == 7 || block.getData() == 11)
////						{
////							MaterialID.append("3");
////						}
////					}
////					if (product.getProductIDbyItemTypeID(itemType.getTypeIDByBlockID("" + block.getTypeId())) == null)
////					{
////						e.setCancelled(true);
////						return;
////					}
////					
//					Location blockloc = block.getLocation();
//					refreshList.add(new BlockRefresh(block.getTypeId(), block.getData(), block.getLocation(), refreshLong));
////					for (ItemStack item : e.getBlock().getDrops())
////					{
////						e.getPlayer().getInventory().addItem(product.createPropertyItem(product.getProductIDbyItemTypeID(itemType.getTypeIDByBlockID(MaterialID.toString())), item.getAmount(), false, false));
////						player.updateInventory();
////					}
//					blockloc.getBlock().setType(Material.AIR);
////					new BukkitRunnable()
////					{
////						public void run()
////						{
////							blockloc.getBlock().setType(Material.BARRIER);
////						}
////					}.runTaskLater(main, 10);
//					loc = blockLocation;
//					e.setCancelled(true);
//				} else if (block.getType() == Material.getMaterial(59))
//				{
//					refreshList.add(new BlockRefresh(block.getTypeId(), block.getData(), block.getLocation(), refreshLong));
//					loc = blockLocation;
//				} else
//				{
//					e.setCancelled(true);
//				}
			} else
			{
				if (main.debug)
				{
					if (!property.getResourcePropertyIDList(false, null).contains(propertyID))
					{
						Bukkit.getConsoleSender().sendMessage("Not containing!");
					}
				}
				e.setCancelled(true);
			}
		} else
		{
			if (!user.inOwnerModus() && player.getGameMode() != GameMode.CREATIVE)
			{
				e.setCancelled(true);
				player.sendMessage(ColorOptions.error + "You can't break blocks while in ownermode or staffmode");
			}
		}
	}
	
//	@EventHandler
//	public void onDrop(ItemSpawnEvent e)
//	{
//		ArrayList<ItemStack> newDrops = new ArrayList<ItemStack>();
//		ItemStack drop = e.getEntity().getItemStack();
//		Location dropl = e.getLocation();
//		Location droploc = new Location(dropl.getWorld(), dropl.getBlockX(), dropl.getBlockY(), dropl.getBlockZ());
//		if (!drop.hasItemMeta())
//		{
////			if (main.debug)
////			{
////				Bukkit.getConsoleSender().sendMessage("Dropped itemmeta!");
////			}
//			if (loc!= null)
//			{
////				if (main.debug)
////				{
////					Bukkit.getConsoleSender().sendMessage("Matched loc!");
////				}
//				Location location = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//				if (location.equals(droploc))
//				{
//					Material type = drop.getType();
//					if (type == Material.COBBLESTONE)
//					{
//						Integer productID = product.getProductID("cobblestone", false);
//						if (productID != null)
//						{
//							newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
//						}
//					} else if (type == Material.COAL)
//					{
//						Integer productID = product.getProductID("coal", false);
//						if (productID != null)
//						{
//							newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
//						}
//					} else if (type == Material.IRON_ORE)
//					{
//						Integer productID = product.getProductID("ironore", false);
//						if (productID != null)
//						{
//							newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
//						}
//					} else if (type == Material.GOLD_ORE)
//					{
//						Integer productID = product.getProductID("goldore", false);
//						if (productID != null)
//						{
//							newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
//						}
//					} else if (type == Material.DIAMOND)
//					{
//						Integer productID = product.getProductID("diamond", false);
//						if (productID != null)
//						{
//							newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
//						}
//					} else if (type == Material.LOG)
//					{
//						if (main.debug)
//						{
//							Bukkit.getConsoleSender().sendMessage("Log type found: " + type.getId());
//						}
//						Integer productID = product.getProductIDbyItemTypeID(this.itemType.getIDbyMaterialID(""+type.getId()));
//						if (productID != null)
//						{
//							newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
//						} else
//						{
//							if (main.debug)
//							{
//								Bukkit.getConsoleSender().sendMessage("Log type found but no productID matched");
//							}
//						}
//					} else if (type == Material.WHEAT)
//					{
//						Integer productID = product.getProductID("wheat", false);
//						if (productID != null)
//						{
//							newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
//						}
//					} 
////					else if (type == Material.SEEDS) 
////					{
////						Integer productID = product.getProductID("wheat", false);
////						if (productID != null)
////						{
////							newDrops.add(product.createPropertyItem(productID, drop.getAmount(), false, false));
////						}
////					}
//					else
//					{
//						e.setCancelled(true);
//					}
//					e.setCancelled(true);
//					for (ItemStack item : newDrops)
//					{
//						dropl.getWorld().dropItemNaturally(dropl, item);
//					}
//				} else
//				{
//					e.setCancelled(true);
//				}
//			} else
//			{
//				if (main.debug)
//				{
//					//Bukkit.getConsoleSender().sendMessage("Loc not found!");
//				}
//			}
//		}
//	}
}
