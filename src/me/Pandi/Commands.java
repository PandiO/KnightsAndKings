package me.Pandi;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import API_methods.WorldGuard;
import Arenas.Duel;
import Assignments.Assignment;
import Assignments.AssignmentEnterPropertySpecific;
import Assignments.AssignmentFoodConsumeRandom;
import Assignments.AssignmentHarvestRandom;
import Assignments.AssignmentKill;
import Assignments.AssignmentTravelDistance;
import Assignments.AssignmentTravelRandom;
import Assignments.AssignmentTravelSpecific;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Houses.House;
import Main.Main;
import Minigames.BanditSpawn;
import Minigames.Transport;
import PathFinding.PathFinder;
import Products.Product;
import Resources.ResourceCommands;
import Resources.ResourceProperty;
import Resources.YmlFile;
import Rooms.Room;
import Scoreboards.ActionBar;
import Skills.Skill;
import SpawnPoints.SpawnPoint;
import Titles.Title;
import Towns.Town;
import Traits.CarrierTrait;
import Traits.Gladiator;
import Traits.TestTrait;
import Tutorial.Tutorial;
import Tutorial.TutorialFile;
import Users.User;
import Users.Users;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;


public class Commands implements CommandExecutor, Listener
{	
	private Main main;
	public Commands(Main main) 
	{
		this.main = main;
	}
	Title title = new Title();
	Skill skill = new Skill();
	Town town = new Town();
	WorldGuard worldguard = new WorldGuard();
	Product product = new Product();
    NPCRegistry registry = CitizensAPI.getNPCRegistry();
	Integer timer = 0;
	BukkitTask task = null;
	Transport tp = null;
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("initiate"))
		{
			if (sender.hasPermission("k&k.initiate"))
			{
				//File to be initiated by this command, contains database information
				File dbconnection = new File(main.getDataFolder(), "db-connection.yml");
				
				
				//Database details, specified by the user in the command
				String host;
				String port;
				String database;
				String user;
				String password;
				
				//List of possible initiations to be performed by the sender
				List<String> possible_initiations = new ArrayList<>(Arrays.asList("database"));
				
				if (args.length >= 1)
				{
					if (args[0].equalsIgnoreCase("database"))
					{
						if (args.length == 6)
						{
							//Database details are specified by the command arguments
							host = args[1];
							port = args[2];
							database = args[3];
							user = args[4];
							password = args[5];
							
							//In case no password is needed
							if (args[5].equalsIgnoreCase("none"))
							{
								password = "";
							}
							
							
							//try and catch statement to create db-connection file with database details
							try
							{
						        dbconnection.createNewFile();
						        YamlConfiguration.loadConfiguration(dbconnection);
						        YamlConfiguration dbconnfile = YamlConfiguration.loadConfiguration(dbconnection);
						        dbconnfile.set("HOST", host.toString());
						        dbconnfile.set("PORT", port);
						        dbconnfile.set("DATABASE", database);
						        dbconnfile.set("USER", user.toString());
						        dbconnfile.set("PASSWORD", password.toString());
						        dbconnfile.save(dbconnection);
							} catch(IOException e)
							{
								sender.sendMessage(ChatColor.RED + "Could not create db-connection file: " + e);
								return false;
							}
							
							//If the command sender is a player he gets the succes-message sent, if he is a console sender he gets the message through the console
							if (sender instanceof Player)
							{
								sender.sendMessage(ChatColor.GREEN + "Database details have been saved!");
								sender.sendMessage(ChatColor.GRAY + "Reload server to establish conenction");

							} else
							{
								sender.sendMessage(ChatColor.GREEN + "Database details have been saved!");
							}
						} else
						{
							//When the sender sends too few command arguments
							sender.sendMessage(ChatColor.RED + "Error: you need to specify the following command arguments:");
							sender.sendMessage(ChatColor.GRAY + "DB-Host - DB-Port - DB-name - DB-username - DB-password");
							sender.sendMessage(ChatColor.GRAY + "Command order: /initiate <database> <host> <port> <name> <username> <password>");
						}
					} else
					{
						//When the sender doesn't specify what to initiate
						sender.sendMessage(ChatColor.RED + "Error: invalid command argument has been specified");
						sender.sendMessage(ChatColor.GRAY + "Possibilities: ");
						for(String s : possible_initiations)
						{
							sender.sendMessage(ChatColor.GRAY + "- " + s);
						}
					}
				} else
				{
					sender.sendMessage(ChatColor.RED + "Error: too few arguments given, use:");
					sender.sendMessage(ChatColor.GRAY + "/initiate <subject>");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		
		if (label.equalsIgnoreCase("test"))
		{
			if (sender.hasPermission("k&k.test"))
			{
				if (sender instanceof Player)
				{
					Player p = (Player) sender;
					String username = p.getName();
					UUID uuid = p.getUniqueId();
					User user = null;
					
					try
					{
						user = Users.getUser(uuid);
					} catch (UserNotFoundException ex)
					{
						ErrorHandlers.userNotFoundAction(null, p, true);
						return false;
					} catch (Exception ex)
					{
						ex.printStackTrace();
						ErrorHandlers.userNotFoundAction(null, p, true);
						return false;
					}
					
					if (args[0].equalsIgnoreCase("user"))
					{
						User userTest = null;
						try
						{
							user = Users.getUser(p.getUniqueId());
						} catch (NullPointerException ex)
						{
							ex.printStackTrace();
							p.sendMessage(ErrorHandlers.userNotFound);
							p.sendMessage(ErrorHandlers.reccommendReload);
						}
						p.sendMessage("This message fires after try-catch statement");
						
					}
					if (args[0].equalsIgnoreCase("leftovers"))
					{
						for(World w: Bukkit.getWorlds())
						{
							for(Entity e: w.getEntities())
							{
								if(e.getName().equals("Z") || e.getName().equals("Zz"))
								{
									e.remove();
								}
							}
						}
					}
					if (args[0].equalsIgnoreCase("trait"))
					{
						sender.sendMessage("Spawning npc with testtrait");
						NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "TestTrait");

						npc.addTrait(TestTrait.class);
						npc.spawn(((Player) sender).getLocation());
					}
					if (args[0].equalsIgnoreCase("reach"))
					{
						user.getQuestList().get(0).goalReached();
					}
					if (args[0].equalsIgnoreCase("loop"))
					{
						StringBuilder sb = new StringBuilder();
						String string = "Het is zijn eigen domme fout omdat hij niet luisterde naar zijn maten";
						String[] split = string.split(" ");
						String newString = null;
	    				int count = 0;
	    				p.sendMessage("Length: " + split.length);
	    		        for(int i = 0; i < split.length; i +=5)
	    		        {       
	    		            count=i+4;
	    		            for(int j=i;j<=count; j++)
	    		            {
	    		            	if (j < split.length)
	    		            	{
	    		            		sb.append(split[j]).append(" ");
	    		            	}
	    		            }
	    		            p.sendMessage(sb.toString());
	    		            sb.delete(0, sb.length());
	    		        }
					}
					if (args[0].equalsIgnoreCase("transport"))
					{
						if (this.tp == null)
						{
							tp = new Transport(user, 22);
						}

						if (args.length == 2)
						{
							if (args[1].equalsIgnoreCase("stop"))
							{
								tp.cancel("Cancelled");
							}
						}
					}
					if (args[0].equalsIgnoreCase("banner"))
					{
						ItemStack i = new ItemStack(Material.BANNER, 1);
					    BannerMeta m = (BannerMeta)i.getItemMeta();

					    m.setBaseColor(DyeColor.WHITE);

					   List<Pattern> patterns = new ArrayList<Pattern>(); //Create a new List called 'patterns'

					    patterns.add(new Pattern(DyeColor.BLUE, PatternType.GRADIENT)); //Add the pattern 'HALF_HORIZONTAL' to the list with red as color
					    patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT_UP)); //Add the pattern 'RHOMBUS_MIDDLE' to the list with black as color
					    patterns.add(new Pattern(DyeColor.WHITE, PatternType.GRADIENT_UP)); //Add the pattern 'RHOMBUS_MIDDLE' to the list with black as color
					    m.setPatterns(patterns);

					    i.setItemMeta(m);
					    
					    ((Player) sender).getInventory().addItem(i);
					}
					if (args[0].equalsIgnoreCase("assign"))
					{
						AssignmentFoodConsumeRandom dfd = new AssignmentFoodConsumeRandom("Healthy eater", null, -1, -1, -1, -1, true);
						AssignmentEnterPropertySpecific fgf = new AssignmentEnterPropertySpecific("Shopping spree", null, -1, -1, -1, -1, -1, true);
						AssignmentTravelSpecific ghg = new AssignmentTravelSpecific("Pathfinder", null, -1, -1, -1, -1, -1, true);
						AssignmentKill kill = new AssignmentKill("Daily killer", null, true, false, -1, -1, -1, -1, true);
						AssignmentTravelDistance distance = new AssignmentTravelDistance("Traveller", null, -1, -1, -1, -1, true);
						AssignmentHarvestRandom ass = new AssignmentHarvestRandom("Harvest Diamond", null, -1, -1, -1, -1, -1, true);
						AssignmentTravelRandom assignment = new AssignmentTravelRandom("Test assignment", null, null, -1, -1, -1, -1, true);
						ghg.asign(user, true, -1);
						dfd.asign(user, true, -1);
						kill.asign(user, true, -1);
						distance.asign(user, true, -1);
						fgf.asign(user, true, -1);
						ass.asign(user, true, -1);
						assignment.asign(user, true, -1);
						user.getPlayer().sendMessage(ColorOptions.message + "You got the " + assignment.getName() + " assinged!");
					}
					if (args[0].equalsIgnoreCase("savep"))
					{
						for (Assignment ass : user.getAssignmentList())
						{
							if (ass instanceof AssignmentTravelSpecific)
							{
								AssignmentTravelSpecific assignment = (AssignmentTravelSpecific) ass;
								assignment.saveAll();
								user.getPlayer().sendMessage(ColorOptions.message + "saved assignment to the DB");
								break;
							}
						}
					}
					if (args[0].equalsIgnoreCase("retrievep"))
					{
						
					}
					if (args[0].equalsIgnoreCase("assignments"))
					{
						if (user.getAssignmentList() == null || user.getAssignmentList().isEmpty())
						{
							sender.sendMessage(ColorOptions.error + "No assignments found!");
						} else
						{
							sender.sendMessage(ColorOptions.messageachievement + "You got " + user.getAssignmentList().size() + " assignments!");
						}
					}
					if (args[0].equalsIgnoreCase("frontlocation"))
					{
						Player player = (Player) sender;
						BanditSpawn bs = new BanditSpawn(main);
						Location front = bs.getRandomBanditLocation(player);
						front.getWorld().dropItemNaturally(front, new ItemStack(Material.GOLD_NUGGET, 2));
						player.sendMessage(ColorOptions.message + "Dropped 2 gold nuggets in front of you!");
					}
					if (args[0].equalsIgnoreCase("timer"))
					{
						if (args.length == 2)
						{
							if (args[1].equalsIgnoreCase("start"))
							{
								timer = 0;
								task = new BukkitRunnable()
										{
											public void run()
											{
												timer++;
											}
										}.runTaskTimer(main, 0, 1*20);
							} else if (args[1].equalsIgnoreCase("stop"))
							{
								if (task != null)
								{
									task.cancel();
								}
								p.sendMessage("Time: " + this.timer);
							}
						}
					}
					if (args[0].equalsIgnoreCase("bar"))
					{
						ActionBar bar = new ActionBar(ColorOptions.message + "TEST");
						bar.sendToPlayer(p);
						
					}
					if (args[0].equalsIgnoreCase("ai"))
					{
						Location[] path;
						Material[] mat;
						Byte[] data;
						SpawnPoint spawnpoint = new SpawnPoint();
						Location start = spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("point1"));
						Location end = spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("point2"));
						
						PathFinder a = new PathFinder(start, end, 1000000, true, 5);
						path = a.getPath();
						
						mat = new Material[path.length];
						data = new Byte[path.length];
						
						for(int i = 0; i < path.length; i++)
						{
							mat[i] = path[i].getBlock().getType();
							data[i] = path[i].getBlock().getData();
							
							path[i].getBlock().setType(Material.GLASS);
						}
					}
					if (args[0].equalsIgnoreCase("npc"))
					{
						SpawnPoint spawnpoint = new SpawnPoint();
						NPCRegistry registry = CitizensAPI.getNPCRegistry();
						try
						{
							NPC test = registry.createNPC(EntityType.PLAYER, "test");
							test.addTrait(CarrierTrait.class);
							test.spawn(p.getLocation());
							Player npc = (Player) test.getEntity();
							npc.getInventory().addItem(product.createPropertyItem(product.getProductID("bread", false), 32, false, false));
							npc.updateInventory();
							test.setProtected(false);
							new BukkitRunnable()
							{
								public void run()
								{
//									test.getNavigator().setTarget((LivingEntity) p, true);
//									test.getNavigator().setTarget(spawnpoint.getSpawnPointLocation(spawnpoint.getSpawnPointID("point2")));

								}
							}.runTaskLater(main, 3*20);
							p.sendMessage("Succesfully spawned npc!");
						} catch(Exception e)
						{
							e.printStackTrace();
							p.sendMessage(ColorOptions.error + "Something went wrong!");
						}

					}
					if (args[0].equalsIgnoreCase("new"))
					{
					    String sword = "chainmail_boots";
					    Bukkit.broadcastMessage(p.getItemInHand().getType().toString());
					    Material swordmat = Material.valueOf(sword.toUpperCase());
					    				    
					    for (Player pl : Bukkit.getOnlinePlayers())
					    {
					    	pl.getInventory().addItem(new ItemStack(swordmat, 1));
					    	pl.updateInventory();
					    }
					}
					if (args[0].equalsIgnoreCase("tut"))
					{
						Tutorial tut = new Tutorial();
						tut.createTutorial(user, "Food");
					}
					if (args[0].equalsIgnoreCase("settut"))
					{
						TutorialFile file = new TutorialFile();
						file.saveTutorial("Menu", 50);
						sender.sendMessage(ColorOptions.messageachievement + "Succesfully saved a tutorial named " + ColorOptions.messagesubjects + "Menu");
					}
					if (args[0].equalsIgnoreCase("duel"))
					{
						Duel duel = new Duel((Player) sender, Bukkit.getPlayer(args[1]), 1, "normal", Arrays.asList(new ItemStack(Material.DIAMOND, 2)), 50000);
						duel.tryStart();
					}
					if (args[0].equalsIgnoreCase("letter"))
					{
						String test = args[1];
						
						p.sendMessage("New output: " + test.substring(0, 1).toUpperCase() + test.substring(1, test.length()));
					}
//					if (args[0].equalsIgnoreCase("loop"))
//					{
//						Integer current = 10;
//						
//						for (int i = current; i > 0; i--)
//						{
//							sender.sendMessage("- " + i);
//						}
//					}
					if (args[0].equalsIgnoreCase("world"))
					{
						sender.sendMessage("Length: " +p.getLocation().getX());
					}
					if (args[0].equalsIgnoreCase("format"))
					{
						sender.sendMessage(ColorOptions.messageachievement + "Hour: " + main.getHourtime(Integer.valueOf(args[1])) + ", Minutes: " + main.getRestMinutetime(Integer.valueOf(args[1])) + ", seconds: " + main.getRestSecondtime(Integer.valueOf(args[1])));
					}
					if (args[0].equalsIgnoreCase("purge"))
					{
						Room room = new Room();
						House house = new House();
						for (Integer roomID : room.getRoomIDList(null))
						{
							room.purgeRoomOwner(roomID);
							sender.sendMessage(ColorOptions.messageachievement + "Purged a room with ID " + roomID);
						}
					}
					if (args[0].equalsIgnoreCase("gladiator"))
					{
						NPC bandit = registry.createNPC(EntityType.PLAYER, "Gladiator");
					    bandit.spawn(p.getLocation());
					    bandit.setProtected(false);
						bandit.addTrait(Gladiator.class);
					}
					if (args[0].equalsIgnoreCase("zone"))
					{
					}
					if (args[0].equalsIgnoreCase("bandits"))
					{
						BanditSpawn.banditID.clear();
						List<Entity> removable = new ArrayList<Entity>();
						for (Entity ent : Bukkit.getWorld("world").getEntities())
						{
							if (ent.getName().equalsIgnoreCase("bandit"))
							{
								removable.add(ent);
							}
						}
						for (NPC npc : registry.sorted())
						{
							if (npc.getName().equalsIgnoreCase("bandit") && !BanditSpawn.getActiveBandits().contains(npc.getId()))
							{
								npc.destroy();
							}
						}
						for (Entity rem : removable)
						{
							rem.remove();
						}
						sender.sendMessage(ColorOptions.messageachievement + "Succesfully reloaded all bandits!");
					}
					if (args[0].equalsIgnoreCase("armor"))
					{
						boolean found = false;
						boolean armor = false;
						for (ItemStack cont : p.getInventory().getContents())
						{
							if (cont != null)
							{
								if (cont.getType() == Material.IRON_CHESTPLATE)
								{
									found = true;
									break;
								}
							}
						}
						for (ItemStack cont : p.getInventory().getArmorContents())
						{
							if (cont != null)
							{
								if (cont.getType() == Material.IRON_CHESTPLATE)
								{
									found = true;
									armor = true;
									break;
								}
							}
						}
						if (found == false)
						{
							p.sendMessage("No chestplate found");
						} else
						{
							p.sendMessage("Found chestplate!");
						}
					}
					if (args[0].equalsIgnoreCase("time"))
					{
//						TimeZone tz = TimeZone.getTimeZone("CET");
						Bukkit.broadcastMessage("Main Zone: " + main.getTime());
//						Bukkit.broadcastMessage("Test zone: " + tz.getDisplayName() + ", Time: " + Calendar.getInstance(tz).getTime().getHours() + ":" + Calendar.getInstance(tz).getTime().getMinutes());
					}
					if (args[0].equalsIgnoreCase("data"))
					{
						Player player = (Player) sender;
						Bukkit.broadcastMessage("Data: "+ player.getItemInHand().getData());
					}
					if (args[0].equalsIgnoreCase("bar"))
					{
						for(Entity entity : Bukkit.getWorld("world").getEntities())
						{
							if (entity.getType() == EntityType.WITHER)
							{
								entity.remove();
								sender.sendMessage(ColorOptions.messageformat + "Removed a wither!");
							}
						}
					}
					if (args[0].equalsIgnoreCase("hunger"))
					{
						((Player) sender).setFoodLevel(2);
					}
					if (args[0].equalsIgnoreCase("skills"))
					{
						if (main.enableSkills)
						{
							main.enableSkills = false;
							p.sendMessage(ColorOptions.error + "Skills are disabled!");
						} else
						{
							main.enableSkills = true;
							p.sendMessage(ColorOptions.messageachievement + "Skills are enabled!");
						}
					}
					if (args[0].equalsIgnoreCase("file"))
					{
						String name = args[1];
						YmlFile r = new YmlFile();
						sender.sendMessage("lastID: " + r.getLastID(name));
					}
					if (args[0].equalsIgnoreCase("flags"))
					{
						sender.sendMessage(ColorOptions.message + "Commencing flag reset for all resource-properties");
						ResourceProperty property = new ResourceProperty();
						ResourceCommands cmd = new ResourceCommands(main);
						for (Integer resourceID : property.getResourcePropertyIDList(false, null))
						{
							cmd.changeFlags((Player) sender, resourceID, property.getResourceCategory(resourceID));
						}
					}
					if (args[0].equalsIgnoreCase("more"))
					{
						if (sender instanceof Player)
						{
							Player player = (Player) sender;
							if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR)
							{
								ItemStack item = player.getItemInHand();
								item.setAmount(64);
								player.setItemInHand(item);
								player.sendMessage(ColorOptions.messageachievement + "Increased the amount of the item in your hand to " + ColorOptions.messagesubjects + "64");
							} else
							{
								player.sendMessage(ColorOptions.error + "You don't have an item n your hand!");
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "You need to be a player to perform this command!");
						}
					}
					if (args[0].equalsIgnoreCase("item"))
					{
						List<Integer> idList = product.getIDList(false, null, true);
						Collections.shuffle(idList);
						for (Integer productID : idList)
						{
							if (product.gradeChance(productID))
							{
								((Player) sender).getInventory().addItem(product.createPropertyItem(productID, 1, false, false));
								sender.sendMessage(ColorOptions.message + "You received " + product.getDisplayName(productID, false) + ColorOptions.message + "!");
								break;
							}
						}
					}
					if (args[0].equalsIgnoreCase("address"))
					{
						Player player = (Player) sender;
						InetAddress address = player.getAddress().getAddress();
						sender.sendMessage(ColorOptions.message + "Your IP address is " + address + ", string variant: " + address.toString().replaceAll("/", ""));
					}
					if (args[0].equalsIgnoreCase("npctest"))
					{
					    NPC npc;
				        registry = CitizensAPI.getNPCRegistry();
				        
				        npc = registry.createNPC(EntityType.PLAYER, p.getDisplayName());
				        npc.spawn(p.getLocation());
				        npc.setProtected(false);
				 
				        int radius = 3;
				        List<Entity> near = p.getNearbyEntities(radius, radius, radius);
				 
				        for (Entity entities : near) {
				 
				            if (entities instanceof Player) {
				 
				                Player players = (Player) entities;
				 
				                players.sendMessage("NPC of " + p.getName() + " has been created.");
				                System.out.println("NPC of " + p.getName() + " has been created.");
				 
				            }
				 
				        }
				        Map<NPC, Location> npcloc = new HashMap<NPC, Location>();
				        NPCRegistry registry = CitizensAPI.getNPCRegistry();
				        Map<NPC, Integer> movementsched = new HashMap<NPC, Integer>();
				        final Location startFrom = npcloc.get(npc);
				        final Random rndGen = new Random();
				        final int[] values = new int[3];
				 
				        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable()
				        {
				            public void run()
				            {
				                for(int i = 0; i < 3; i++) 
				                {
				                	values[i] = rndGen.nextInt(4) + 1;
				 
				                }
				                Location output = startFrom.clone();
				                Random x = new Random();
				                int y = x.nextInt(3) + 1;
				                switch(y){
				                case 1:
				                	output.add(values[0], 0, values[2]);
				                	break;
				                case 2:
				                	output.subtract(values[0], 0, values[2]);
				                	break;
				                case 3:
				                	int k = values[0] - values[0]*2;
				                	output.add(k, 0, values[2]);
				                	break;
				                case 4:
				                	int l = values[2] - values[2]*2;
				                	output.add(values[0], 0, l);
				                	break;
				                }   
				             	npc.getNavigator().setTarget(output);
				                npcloc.put(npc, output);
				            }
				        }, 0L , 200L);
				        movementsched.put(npc, id);
					}
//					    npc.setItemInHand(product.createItem(ColorOptions.messageformat + "TestSword", new ItemStack(Material.DIAMOND_SWORD, 1), true, ChatColor.RED + "Soulbound"));
//					    npc.getInventory().setHelmet(product.createItem(ColorOptions.messageformat + "helmet", new ItemStack(Material.LEATHER_HELMET), false, ChatColor.RED + "Soulbound"));
//					    npc.getInventory().setChestplate(product.createItem(ColorOptions.messageformat + "chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), false, ChatColor.RED + "Soulbound"));
//					    npc.getInventory().setLeggings(product.createItem(ColorOptions.messageformat + "leggings", new ItemStack(Material.LEATHER_LEGGINGS), false, ChatColor.RED + "Soulbound"));
//					    npc.getInventory().setBoots(product.createItem(ColorOptions.messageformat + "boots", new ItemStack(Material.LEATHER_BOOTS), false, ChatColor.RED + "Soulbound"));
				} else
				{
					if (args[0].equalsIgnoreCase("loop"))
					{
						Integer current = 10;
						Integer newi = null;
						
						for (int i = current; i != -1; i--)
						{
							newi = i;
							sender.sendMessage("- " + newi);
							current--;
						}
						sender.sendMessage("End: " + newi);
						
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		return false;
	}
}
