package Properties;

import org.bukkit.event.Listener;

import Main.Main;

public class SpawnShopkeepers implements Listener
{
//	Property property = new Property();
//	Town town = new Town();
//	SpawnPoint spawnpoint = new SpawnPoint();
//	PropertyCategory category = new PropertyCategory();
//	NPCRegistry registry = CitizensAPI.getNPCRegistry();
	private Main main;
	public SpawnShopkeepers(Main main) 
	{
		this.main = main;
	}
//	
//	@EventHandler
//	public void onEnter(EnterTownEvent e)
//	{
//		UUID uuid = e.getUUID();
//		Integer townID = e.getTownID();
//		
//		if (!DiscoverTown.playersinTown.isEmpty())
//		{
//			for (UUID targetUUID : DiscoverTown.playersinTown.keySet())
//			{
//				if (DiscoverTown.playersinTown.get(targetUUID) == townID)
//				{
//					spawnShopKeepers(townID);
//					break;
//				}
//			}
//		} else
//		{
//			for (Player player : Bukkit.getOnlinePlayers())
//		    {
//		    	if (player.isOp())
//		    	{
//		    		player.sendMessage(ColorOptions.error + "An error occured when checking players inside a town!");
//		    		player.sendMessage(ColorOptions.message + "users: " + DiscoverTown.playersinTown.keySet());
//		    	}
//		    }
//		}
//	}
//	
//	@EventHandler
//	public void onLeave(LeaveTownEvent e)
//	{
//		UUID uuid = e.getUUID();
//		Integer townID = e.getTownID();
//		if (!DiscoverTown.playersinTown.isEmpty())
//		{
//			for (UUID targetUUID : DiscoverTown.playersinTown.keySet())
//			{
//				if (DiscoverTown.playersinTown.get(targetUUID) == townID)
//				{
//					return;
//				}
//			}
//			for (Integer propertyID : property.getIDList(null, townID))
//			{
//				Integer npcID = property.getNPCID(propertyID);
//				if (npcID != null && npcID != 0)
//				{
//					NPC shopkeeper = registry.getById(npcID);
//					if (shopkeeper != null)
//					{
//						if (shopkeeper.isSpawned())
//						{
//							shopkeeper.despawn();
//							Bukkit.getConsoleSender().sendMessage(ColorOptions.message + "Despawned the shopkeepers in town " + town.getTownName(townID) + " got despawned because town has no active players");
//						}
//					}
//				}
//			}
//		} else
//		{
//			for (Integer propertyID : property.getIDList(null, townID))
//			{
//				Integer npcID = property.getNPCID(propertyID);
//				if (npcID != null && npcID != 0)
//				{
//					NPC shopkeeper = registry.getById(npcID);
//					if (shopkeeper != null)
//					{
//						if (shopkeeper.isSpawned())
//						{
//							shopkeeper.despawn();
//							Bukkit.getConsoleSender().sendMessage(ColorOptions.message + "Despawned the shopkeepers in town " + town.getTownName(townID) + " got despawned because town has no active players");
//						}
//					}
//				}
//			}
//		}
//	}
//	
//	public void spawnShopKeepers(Integer townID)
//	{
//		PropertyCategory propcat = new PropertyCategory();
//		for (Integer propertyID : property.getIDList(null, townID))
//		{
//			Integer NPCID = property.getNPCID(propertyID);
//			if (NPCID == null || NPCID == 0)
//			{
//				Integer spawnpoint = property.getPropertySpawnPoint(propertyID);
//				if (spawnpoint != null && spawnpoint != 0)
//				{
//				    NPC shopkeeper = registry.createNPC(EntityType.VILLAGER, "Shopkeeper");
//				    
//				    String category = propcat.getCategoryName(property.getCategoryID(propertyID));
//					if (category.equalsIgnoreCase("armory") || category.equalsIgnoreCase("weaponry") || category.equalsIgnoreCase("archery") || category.equalsIgnoreCase("warehouse"))
//					{
//						shopkeeper.getTrait(VillagerProfession.class).setProfession(Profession.BLACKSMITH);
//					} else if (category.equalsIgnoreCase("food") || category.equalsIgnoreCase("butchery"))
//					{
//						shopkeeper.getTrait(VillagerProfession.class).setProfession(Profession.BUTCHER);
//					} else if (category.equalsIgnoreCase("bakery") || category.equalsIgnoreCase("grocery"))
//					{
//						shopkeeper.getTrait(VillagerProfession.class).setProfession(Profession.FARMER);
//					} else if (category.equalsIgnoreCase("witchery"))
//					{
//						shopkeeper.getTrait(VillagerProfession.class).setProfession(Profession.PRIEST);
//					} else
//					{
////						Bukkit.broadcastMessage("error");
//					}
//				    shopkeeper.addTrait(Shopkeeper.class);
//
//				    shopkeeper.spawn(spawnpoint.getSpawnPointLocation(spawnpoint));
//				    shopkeeper.setProtected(true);
//				    
//				    
//				    
//				    property.setNPCID(propertyID, shopkeeper.getId());
//		    		Bukkit.getConsoleSender().sendMessage(ColorOptions.message + "Created a shopkeeper for property with ID " + propertyID);
//				} else
//				{
//					for (Player player : Bukkit.getOnlinePlayers())
//				    {
//				    	if (player.isOp())
//				    	{
//				    		player.sendMessage(ColorOptions.error + "Cannot create a shopkeeper for property with ID " + propertyID + ", no spawnpoint set");
//				    	}
//				    }
//				}
//			} else
//			{
//				NPC shopkeeper = registry.getById(NPCID);
//				if (shopkeeper != null)
//				{
//					Integer spawnpoint = property.getPropertySpawnPoint(propertyID);
//					if (spawnpoint != null && spawnpoint != 0)
//					{
//						if (!shopkeeper.isSpawned())
//						{
//							shopkeeper.spawn(spawnpoint.getSpawnPointLocation(spawnpoint));
//							
//				    		Bukkit.getConsoleSender().sendMessage(ColorOptions.message + "Spawned a shopkeeper for property with ID " + propertyID);
//						}
//					} else
//					{
//						for (Player player : Bukkit.getOnlinePlayers())
//					    {
//					    	if (player.isOp())
//					    	{
//					    		player.sendMessage(ColorOptions.error + "Cannot spawn a shopkeeper for property with ID " + propertyID + ", no spawnpoint set");
//					    	}
//					    }
//					}
//				}
//			}
//		}
//	}
}
