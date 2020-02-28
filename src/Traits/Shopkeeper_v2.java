package Traits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import API_methods.WorldGuard;
import Exceptions.UserNotFoundException;
import Handlers.ClickShopItemEvent;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Handlers.QuestStateChangeEvent;
import Handlers.SoundHandler;
import Main.Main;
import Menu.Menu;
import Products.PropertyProduct;
import Properties.Property;
import Properties.PropertyCategory;
import Users.User;
import Users.Users;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;

public class Shopkeeper_v2 extends Trait
{
	WorldGuard worldguard = new WorldGuard();
	Menu menu = new Menu();
	PropertyCategory category = new PropertyCategory();
	Property property = new Property();
	PropertyProduct propertyproduct = new PropertyProduct();
	Main main = Main.getPlugin(Main.class);
	
	public Shopkeeper_v2()
	{
		super("Shopkeeper_v2");
	}
	
	public Player target = null;
	public ArrayList<UUID> shoutList = new ArrayList<UUID>();
	private Integer stock = null;
	private String prefix = ColorOptions.message + "[" + ColorOptions.messageformat + "Shopkeeper" + ColorOptions.message + "] ";
	private String welcomeMSG = null;
	private String possibleSellMSG = ColorOptions.messageformat + "That's one heck of an offer you have there!";
	private Integer lastSellMSG = null;
	private Integer talkDelay = 20;
	private Long nextTalk = System.currentTimeMillis()+talkDelay*1000;
	private List<User> greetingList = new ArrayList<User>();
	private Integer openItemDelay = 60;
	private boolean fullmsg = false;
	private int fullmsgcount = 0;
	private ProtectedRegion PropertyRegion = null;
	private int PropertyID = -1;
	private RegionManager regionManager = null;
	private ArmorStand QuestTag = null;
	
	@Override
	public void onSpawn()
	{
		if (this.regionManager == null)
		{
			this.regionManager = this.worldguard.getRegionManager(npc.getEntity().getWorld());
		}
		if (this.PropertyRegion == null)
		{
			this.PropertyRegion = this.worldguard.getRegion(this.worldguard.getAvailableRegions(npc.getEntity().getLocation()), "Property");
		}
		if (PropertyID == -1)
		{
			try
			{
				this.worldguard.getStructureIDbyRegion(this.PropertyRegion);
				this.welcomeMSG = this.prefix + ColorOptions.message + "Welcome to my " + category.getCategoryName(property.getCategoryID(this.PropertyID)) + ", the " + property.getPropertyName(this.PropertyID); 
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	
	public void run()
	{
		if (npc.isSpawned())
		{
			Entity Enpc = npc.getEntity();
			Long talkDelayMillis = Long.valueOf(talkDelay *1000);
			
			//List<Entity> entities = this.npc.getEntity().getNearbyEntities(5, 2, 5);

			for (User user : this.getNearbyUsers())
			{
				Bukkit.broadcastMessage(user.getUsername());
				Player target = user.getPlayer();
				Location location = target.getLocation();
				npc.faceLocation(location);
				Integer targetPropertyID = null;
//				try
//				{
//					targetPropertyID = this.worldguard.getStructureIDbyRegion("property", location, this.worldguard.getRegionManager(Enpc.getWorld()));
//				} catch (Exception ex)
//				{
//					ex.printStackTrace();
//				}
//				if (targetPropertyID != null && targetPropertyID == this.PropertyID)
//				{
//					this.tryGreet(user);
//					
//					if (this.isOwner(user))
//					{
//						if (fullmsg == false)
//						{
//							target.sendMessage(this.prefix + ColorOptions.error + " A storage is full, transport it to a warehouse otherwise we can't buy new items!");
//							fullmsg = true;
//						} else if (fullmsgcount == 60*20)
//						{
//							fullmsgcount = 0;
//							fullmsg = false;
//						} else
//						{
//							fullmsgcount++;
//						}
//					}
//				} else
//				{
//					this.tryFarewell(user);
//				}
			}
			if (System.currentTimeMillis() >= this.nextTalk)
			{
				this.afkSuggest();
				this.talk();
				
				this.nextTalk = System.currentTimeMillis() + (this.talkDelay*1000);
			}
//			for (Entity entity : entities)
//			{
//				if (entity instanceof Player && !entity.hasMetadata("NPC"))
//				{
//					Player target = (Player) entity;
//					UUID uuid = target.getUniqueId();
//					User userTarget = null;
//					
//					try
//					{
//						userTarget = Users.getUser(uuid);
//					} catch (Exception ex)
//					{
//						ErrorHandlers.userNotFoundAction(null, target, true);
//						return;
//					}
//					npc.faceLocation(target.getLocation());
//
//					
//					RegionManager manager = worldguard.getRegionManager(Enpc.getWorld());
//					if (worldguard.getStructureIDbyRegion("property", target.getLocation(), manager) == this.PropertyID)
//					{
//						if (!greetingList.contains(uuid))
//						{
//							target.sendMessage(this.welcomeMSG);
//							greetingList.add(uuid);
//						}
//						if (property.getPropertyOwnerID(this.PropertyID) != null && userTarget.getID() == property.getPropertyOwnerID(this.PropertyID) && property.getFullChest(this.PropertyID) != null)
//						{
//							if (fullmsg == false)
//							{
//								target.sendMessage(this.prefix + ColorOptions.error + " A storage is full, transport it to a warehouse otherwise we can't buy new items!");
//								fullmsg = true;
//							} else if (fullmsgcount == 60*20)
//							{
//								fullmsgcount = 0;
//								fullmsg = false;
//							} else
//							{
//								fullmsgcount++;
//							}
//						}
//						
//					} else
//					{
//						if (greetingList.contains(uuid))
//						{
//							greetingList.remove(uuid);
//						}
//					}
//				}
//			}
		}
	}
	
	public void tryGreet(User user)
	{
		if (!greetingList.contains(user))
		{
			if (this.isOwner(user))
			{
				user.getPlayer().sendMessage(this.prefix + " Hello boss, how are you doing?");
			} else
			{
				user.getPlayer().sendMessage(this.welcomeMSG);
			}
			greetingList.add(user);
		}
	}
	
	public void tryFarewell(User user)
	{
		if (greetingList.contains(user))
		{
			if (this.isOwner(user))
			{
				String timeOfDay = "day";
				if (!main.IsDay(user.getPlayer().getWorld()))
				{
					timeOfDay = "evening";
				}
				user.getPlayer().sendMessage(this.prefix + " Have a good " + timeOfDay + ", boss!");
			}
			greetingList.remove(user);
		}
	}
	
	public boolean isOwner(User user)
	{
		boolean isOwner = false;
		
		try
		{
			if (this.property.getPropertyOwnerID(this.PropertyID) == user.getID())
			{
				isOwner = true;
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return isOwner;
	}
	
	public void talk()
	{
		for (User user : this.getNearbyUsers())
		{
			
		}
	}
	
	public void afkSuggest()
	{
		if (this.PropertyID != -1)
		{
			for (User user : this.getNearbyUsers())
			{
				if (user.isAfk() && user.getPlayer().getOpenInventory() == null)
				{
					List<Integer> ProductList = this.propertyproduct.getProductListbyProperty(this.PropertyID);
					Integer relationID = this.propertyproduct.getRelationID(ProductList.get(main.getRandom(0, ProductList.size()-1)), this.PropertyID);
					this.propertyproduct.openItemInfo(user.getPlayer(), relationID);
					user.getPlayer().sendMessage(this.prefix + "This might be a good offer for you");
				} else
				{
					break;
				}
			}
		}
	}
	
	public List<User> getNearbyUsers()
	{
		Bukkit.getConsoleSender().sendMessage("Getting nearby users");
		List<User> NearbyUsers = new ArrayList<User>();
		
		for (Entity entity : this.npc.getEntity().getNearbyEntities(5, 2, 5))
		{
			if (entity instanceof Player && !entity.hasMetadata("NPC"))
			{
				Player target = (Player) entity;
				Bukkit.broadcastMessage("found player " + target.getName());
				UUID uuid = target.getUniqueId();
				User userTarget = null;
				
				try
				{
					userTarget = Users.getUser(uuid);
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(null, target, true);
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(null, target, true);
				}
				NearbyUsers.add(userTarget);
			}
		}
		
		return NearbyUsers;
	}
	
	@EventHandler
	public void onQuestCreation(QuestStateChangeEvent e)
	{
		Bukkit.getConsoleSender().sendMessage("Firing quest state change event!");
		if (e.getPropertyID() == this.PropertyID)
		{
			if (!e.getQuest().isAssigned())
			{
				this.QuestTag = (ArmorStand) this.npc.getEntity().getWorld().spawnEntity(this.npc.getEntity().getLocation().add(0, 1, 0), EntityType.ARMOR_STAND);
				this.QuestTag.setCustomName(ChatColor.YELLOW + "Quest available!");
				
				this.QuestTag.setCustomNameVisible(true);
				this.QuestTag.setGravity(false);
				this.QuestTag.setVisible(false);
				this.QuestTag.setSmall(true);
				this.QuestTag.setMarker(true);
			} else if (e.getQuest().isAssigned())
			{
				try
				{
					this.QuestTag.remove();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler
	public void onClick(NPCRightClickEvent e)
	{
		if (e.getNPC() == npc)
		{
			if (e.getClicker() instanceof Player)
			{
				Player player = (Player) e.getClicker();
				UUID uuid = player.getUniqueId();
				User user = null;
				
				try
				{
					user = Users.getUser(uuid);
				} catch (UserNotFoundException ex)
				{
					ErrorHandlers.userNotFoundAction(null, player, true);
					return;
				} catch (Exception ex)
				{
					ex.printStackTrace();
					ErrorHandlers.userNotFoundAction(null, player, true);
					return;
				}
				if (npc.isSpawned())
				{
					Entity Enpc = npc.getEntity();
					RegionManager manager = worldguard.getRegionManager(Enpc.getWorld());
					Integer propertyID = worldguard.getStructureIDbyRegion("property", Enpc.getLocation(), manager);
					menu.openShopkeeperMenu(user, propertyID);
					player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 2.0F, 1.0F);
				}
			}
		}
	}
	
	@EventHandler
	public void onItemClick(ClickShopItemEvent e)
	{
		Integer npcID = e.getnpcID();
		if (npcID == npc.getId())
		{
			User user = e.getUser();
			UUID uuid = user.getUUID();
			Integer relationID = e.getRelationID();
			stock = propertyproduct.getAmount(relationID);
			List<String> sellMSG = new ArrayList<String>(Arrays.asList(
					ColorOptions.message + "That's one heck of an offer you have there!",
					ColorOptions.message + "I only have " + ColorOptions.messagesubjects + stock + ColorOptions.message + " left in stock!"
					));
			if (Bukkit.getPlayer(uuid) != null)
			{
				Player target = Bukkit.getPlayer(uuid);
				Integer MSG = main.getRandom(0, (sellMSG.size()-1));
				if (stock == 0)
				{
					target.sendMessage(ColorOptions.messageformat + "Shopkeeper: " + ColorOptions.message + "I am affraid that item is out of stock!");
					return;
				}
				if (lastSellMSG != MSG)
				{
					target.sendMessage(ColorOptions.messageformat + "Shopkeeper: " + sellMSG.get(MSG));
					lastSellMSG = MSG;
				} else
				{
					MSG = main.getRandom(0, (sellMSG.size()-1));
				}
			}
		}
	}
}
