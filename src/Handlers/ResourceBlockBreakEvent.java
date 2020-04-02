package Handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import Assignments.Assignment;
import Assignments.AssignmentHarvestRandom;
import Main.Main;
import Products.Product;
import Quests.Quest;
import Quests.QuestHarvestResource;
import Resources.BlockBreakEvents;
import Resources.BlockRefresh;
import Resources.ResourceCategory;
import Resources.ResourceProperty;
import Resources.YmlFile;
import Users.User;

public class ResourceBlockBreakEvent extends KaKEvent implements Cancellable
{
	ResourceProperty property = new ResourceProperty();
	ResourceCategory category = new ResourceCategory();
	Product product = new Product();
	YmlFile file = new YmlFile();
	Main main = Main.getPlugin(Main.class);
	protected boolean isCancelled;
	protected User user;
	protected ItemStack usedTool;
	protected Block block;
	protected ItemStack dropItem;
	protected Integer dropAmount;
	protected Integer propertyID;
	protected Integer cooldown = 25;
	
	public ResourceBlockBreakEvent(User user, ItemStack usedTool, Block block, Integer propertyID)
	{
		super(user, 2);
		this.user = user;
		this.usedTool = usedTool;
		this.block = block;
		this.propertyID = propertyID;
		
		this.calculateDropItem();
	}
	
	private void calculateDropItem()
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Attempting to find and calculate drops for broken block type " + this.block.getType().toString());
		}
		List<ItemStack> dropItem = new ArrayList<ItemStack>();
		Material breakMaterial = this.block.getType();
		Integer productID = null;
		String productName = null;
		boolean requiresCustomCooldown = false;
		
		//Wheat
		if (breakMaterial.getId() == 59)
		{
			productName = "wheat";
		} else if (breakMaterial == Material.STONE)
		{
			productName = "cobblestone";
			
			if (this.usedTool.containsEnchantment(Enchantment.SILK_TOUCH))
			{
				productName = "stone";
			}
			requiresCustomCooldown = true;
		} else if (breakMaterial == Material.COAL_ORE)
		{
			productName = "coal";
			
			if (this.usedTool.containsEnchantment(Enchantment.SILK_TOUCH))
			{
				productName = "coalore";
			}
			requiresCustomCooldown = true;
		} else if (breakMaterial == Material.IRON_ORE)
		{
			productName = "ironore";
			requiresCustomCooldown = true;
		} else if (breakMaterial == Material.GOLD_ORE)
		{
			productName = "goldore";
			requiresCustomCooldown = true;
		} else if (breakMaterial == Material.REDSTONE_ORE)
		{
			productName = "redstone";
			requiresCustomCooldown = true;
		} else if (breakMaterial == Material.DIAMOND_ORE)
		{
			productName = "diamond";
			requiresCustomCooldown = true;
		} else if (breakMaterial == Material.EMERALD_ORE)
		{
			productName = "emerald";
			requiresCustomCooldown = true;
		} else if (breakMaterial == Material.LOG)
		{
			productName = "oakwood";
			if (block.getData() != -1)
			{
				if (block.getData() == 1 || block.getData() == 5 || block.getData() == 9)
				{
					productName = "sprucewood";
				} else if (block.getData() == 2 || block.getData() == 6 || block.getData() == 10)
				{
					productName = "birchwood";
				} else if (block.getData() == 3 || block.getData() == 7 || block.getData() == 11)
				{
					productName = "junglewood";
				}
			}
		} else if (breakMaterial == Material.LOG_2)
		{
			productName = "acaciawood";
			if (block.getData() != -1)
			{
				if (block.getData() == 1 || block.getData() == 3 || block.getData() == 5)
				{
					productName = "darkoakwood";
				}
			}
		}
		productID = this.product.getProductID(productName, false);
		
		if (productID == null)
		{
			this.setCancelled(true);
			Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "Couldn't proceed with ResourceBlockBreakEvent because of missing productID for item " + productName + " retrieved from material " + this.block.getType().toString() +", user involed: " + user.getID());
			//user.getPlayer().sendMessage(ColorOptions.error + "Something went wrong when dropping resource-item. Please contact a staff-member");
			return;
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Found item to drop for block type " + this.block.getType().toString());
		}
		
		if (requiresCustomCooldown)
		{
			String categoryName = category.getCategoryName(property.getResourceCategory(propertyID));
			if (!categoryName.equalsIgnoreCase("stonequarry") 
					&& !categoryName.equalsIgnoreCase("coalmine") 
					&& !categoryName.equalsIgnoreCase("ironmine") 
					&& !categoryName.equalsIgnoreCase("goldmine") 
					&& !categoryName.equalsIgnoreCase("gemmine"))
			{
				if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("Requiring custom cooldown but block is not in property gateRegion for block type " + this.block.getType().toString());
				}
				this.setCancelled(true);
				return;
			}
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Requiring custom cooldown for block type " + this.block.getType().toString());
			}
		}
		
		Integer calculatedAmount = this.getCalculatedAmount(productID);
		if (calculatedAmount > 1)
		{
			for (int i = 0; i < calculatedAmount; i++)
			{
				dropItem.add(this.product.createPropertyItem(productID, 1, false, false));
			}
		} else
		{
			dropItem.add(this.product.createPropertyItem(productID, calculatedAmount, false, false));
		}
		
		this.checkAssignments(dropItem);
		this.checkQuests(dropItem);
		
		this.handleRegeneration(requiresCustomCooldown);
		Location blockLocation = block.getLocation();
		blockLocation.setDirection(user.getPlayer().getLocation().getDirection());
		for (ItemStack drop : dropItem)
		{
			block.getLocation().getWorld().dropItemNaturally(blockLocation, drop);
		}
	}
	
	public void handleRegeneration(boolean isCustom)
	{
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Handling cooldown for block type " + this.block.getType().toString());
		}
		if (isCustom)
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Saving custom cooldown for block type " + this.block.getType().toString());
			}
	    	file.saveBlock("ore-resources", this.block.getLocation(), block.getTypeId() + "", this.property.getCooldown(propertyID));
		} else
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Saving defualt cooldown for block type " + this.block.getType().toString());
			}
			BlockBreakEvents.refreshList.add(new BlockRefresh(block.getTypeId(), block.getData(), block.getLocation(), (System.currentTimeMillis() + (cooldown*1000))));
		}
		this.block.setType(Material.AIR);
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Block type changed to air");
		}
	}
	
	private Integer getCalculatedAmount(Integer productID)
	{
		Integer dropAmount = 1;
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Calculating drop amount for drops of block type " + this.block.getType().toString());
		}
		dropAmount = this.product.getBaseDropAmount(productID);
		if (this.usedTool.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
		{
			if (main.debug)
			{
				Bukkit.getConsoleSender().sendMessage("Found fortune enchantment on usedTool for block type " + this.block.getType().toString());
			}
			Integer level = this.usedTool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			Integer random = main.getRandom(1, 100);
			if (level == 1)
			{
				if (random <= 33)
				{
					dropAmount *= 2;
				}
			} else if (level == 2)
			{
				if (random <= 25)
				{
					dropAmount *= 3;
				} else if (random <= 50)
				{
					dropAmount *= 2;
				}
			} else if (level == 3)
			{
				if (random <= 20)
				{
					dropAmount *= 4;
				} else if (random <= 40)
				{
					dropAmount *= 3;
				} else if (random <= 60)
				{
					dropAmount *= 2;
				}
			} else if (level == 4)
			{
				if (random <= 20)
				{
					dropAmount *= 5;
				} else if (random <= 40)
				{
					dropAmount *= 4;
				} else if (random <= 60)
				{
					dropAmount *= 3;
				} else if (random <= 80)
				{
					dropAmount *= 2;
				}
			} else if (level > 4)
			{
				if (random <= 20)
				{
					dropAmount *= 5;
				} else if (random <= 40)
				{
					dropAmount *= 4;
				} else if (random <= 60)
				{
					dropAmount *= 3;
				} else
				{
					dropAmount *= 2;
				}
			}
		}
		if (main.debug)
		{
			Bukkit.getConsoleSender().sendMessage("Found amount to drop for drops of block type " + this.block.getType().toString() + ", amount: " + dropAmount);
		}
		this.dropAmount = dropAmount;
		return dropAmount;
	}
	
	private void checkAssignments(List<ItemStack> drops)
	{
		for (Assignment assignment : user.getAssignmentList())
		{
			if (assignment instanceof AssignmentHarvestRandom)
			{
				AssignmentHarvestRandom Assignment = (AssignmentHarvestRandom) assignment;
				for (ItemStack drop : drops)
				{
					Assignment.Harvest(drop);
				}
				break;
			}
		}
	}
	
	private void checkQuests(List<ItemStack> drops)
	{
		for (Quest quest : user.getQuestList())
		{
			if (quest instanceof QuestHarvestResource)
			{
				QuestHarvestResource Quest = (QuestHarvestResource) quest;
				for (ItemStack drop : drops)
				{
					Bukkit.getConsoleSender().sendMessage("Item type: " + drop.getType().toString());
					Quest.Harvest(drop);
				}
				break;
			}
		}
	}
	
	@Override
	public boolean isCancelled() {
	    return this.isCancelled;
	}
	 
	@Override
	public void setCancelled(boolean arg0) {
	    this.isCancelled = arg0;
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
