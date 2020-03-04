package Sieges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Minigames.Participant;
import Users.User;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class Objective extends SiegeObject
{
	protected Objective instance;
	protected int originalCapturePoints = 500;
	protected int currentCapturePoints;
	protected boolean isActive;
	protected List<User> testingList = new ArrayList<User>();
	protected BukkitTask captureTask;
	protected BukkitTask circleTask;
	protected Block bannerBlock;
	protected Banner banner;
	protected double captureRadius = 2.5;
	protected ArmorStand percentageEntity;
	protected boolean isCaptured;
	
	protected List<List<Pattern>> patternList = new ArrayList<List<Pattern>>();
	
	public Objective(int siegeID, int spawnpointID, int capturePoints, DyeColor originalColor)
	{
		super(siegeID, spawnpointID);
		this.originalCapturePoints = capturePoints;
		this.currentCapturePoints = this.originalCapturePoints;
		this.fetchLocation();
		this.instance = this;
		this.bannerBlock = this.location.getBlock();
		this.banner = (Banner) this.bannerBlock.getState();
		this.setBannerStages(originalColor, DyeColor.RED);
	}
	
	public int getOriginalCapturePoints()
	{
		return this.originalCapturePoints;
	}
	
	public int getCurrentCapturePoints()
	{
		return this.currentCapturePoints;
	}
	
	public boolean getActive()
	{
		return this.isActive;
	}
	
	public List<User> getTestingList()
	{
		return this.testingList;
	}
	
	public BukkitTask getCaptureTask()
	{
		return this.captureTask;
	}
	
	public BukkitTask getCircleTask()
	{
		return this.circleTask;
	}
	
	public Integer calculateCapturePoints()
	{
		Integer defendAmount = 0;
		Integer captureAmount = 0;
		Integer capturePoints = 0;
		
		for (User user : this.getPlayers())
		{
			Player player = user.getPlayer();
			if (this.getLocation().distance(player.getLocation()) <= this.captureRadius)
			{
				int teamNumber = 2;
				Siege siege = Sieges.findSiege(user);
				
				if (siege != null)
				{
					teamNumber = siege.getParticipant(user).GetTeam().GetNumber();
				}
				
				if (teamNumber == 1)
				{
					defendAmount++;
					
					if (defendAmount == 1)
					{
						capturePoints -= 6;
					} else
					{
						Integer step = 3;
						if (this instanceof MainObjective)
						{
							step = 6;
						}
						capturePoints -= step;
					}
				} else
				{
					captureAmount++;
					
					if (captureAmount == 1)
					{
						capturePoints = 5;
					} else
					{
						Integer step = 2;
						if (this instanceof MainObjective)
						{
							step = 5;
						}
						capturePoints+=step;
					}
				}
			} else
			{
				Bukkit.getConsoleSender().sendMessage("Distance too large: " + this.getLocation().distance(player.getLocation()));
			}
		}
		Bukkit.getConsoleSender().sendMessage("CapturePoints: " + capturePoints);
		return capturePoints;
	}
	
	public Block getBannerBlock()
	{
		return this.bannerBlock;
	}
	
	public Banner getBanner()
	{
		return this.banner;
	}
	
	public double getCaptureRadius()
	{
		return this.captureRadius;
	}
	
	public List<User> getPlayers()
	{
		List<User> receivers = new ArrayList<User>();
		
		SiegeScenario scenario = Scenarios.findScenario(this.scenarioID);
		receivers.addAll(this.testingList);
		
		for (Siege sieges : Sieges.Sieges)
		{
			if (sieges.getScenario() == scenario)
			{
				for (Participant part : sieges.getParticipants())
				{
					receivers.add(part.getUser());
				}
				break;
			}
		}
		Bukkit.getConsoleSender().sendMessage("Size: " + receivers.size());

		return receivers;
	}
	
	public Integer getCapturePercentage()
	{
		Integer percentage = 100;
		
		float part = ((float) this.currentCapturePoints)/this.originalCapturePoints;
		percentage -= (int) (part*100);
		
		return percentage;
	}
	
	public ArmorStand getPercentageEntity()
	{
		return this.percentageEntity;
	}
	
	public boolean getCaptured()
	{
		return this.isCaptured;
	}
	
	public void setPercentageEntity()
	{
		if (this.percentageEntity == null)
		{
			World world = this.getLocation().getWorld();
			Location location = this.getLocation().clone().add(0, 1.6, 0);
			if (location == null)
			{
				Bukkit.getConsoleSender().sendMessage("No location center could be found!");
				return;
			}
			final ArmorStand arm = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
			arm.setCustomName(ColorOptions.message + "Captured: " + this.getCapturePercentage() + "%");
			arm.setCustomNameVisible(true);
			arm.setGravity(false);
			arm.setVisible(false);
			arm.setSmall(true);
			arm.setMarker(true);
			this.percentageEntity = arm;
		}
	}
	
	public void changePercentageEntity()
	{
		if (this.percentageEntity != null)
		{
			this.percentageEntity.setCustomName(ColorOptions.message + "Captured: " + this.getCapturePercentage() + "%");
		}
	}
	
	public void removePercentageEntity()
	{
		if (this.percentageEntity != null)
		{
			this.percentageEntity.remove();
		}
	}
	
	public void fetchBanner()
	{
		this.bannerBlock = this.location.getBlock();
		this.banner = (Banner) this.bannerBlock.getState();
	}
	
	public void setOriginalCapturePoints(int capturePoints)
	{
		this.originalCapturePoints = capturePoints;
		if (this.originalCapturePoints > this.currentCapturePoints)
		{
			this.setCurrentCapturePoints(this.originalCapturePoints);
		}
	}
	
	public void setCurrentCapturePoints(int capturePoints)
	{
		if (this.currentCapturePoints <= 0)
		{
			return;
		}
		this.currentCapturePoints = capturePoints;
		Bukkit.getConsoleSender().sendMessage(ColorOptions.error + "New Points: " + this.currentCapturePoints + "/" + this.originalCapturePoints);
		
		float rawPart = ((float) this.currentCapturePoints)/this.originalCapturePoints;
		Integer part = (int)Math.ceil(rawPart*8);
		
		if (this.currentCapturePoints <= 0)
		{
			part = 1;
		} else if (this.currentCapturePoints > 0 && part == 1)
		{
			part = 2;
		}
		Bukkit.getConsoleSender().sendMessage("Part: " + part + ", rawPart: " + rawPart + ", original: " + this.originalCapturePoints + ", current: " + this.currentCapturePoints);
		Integer index = part-1;
		
		if (index < 0)
		{
			index = 0;
		}
		
		this.banner.setPatterns(this.patternList.get(index));
		banner.update(true);
		
		if (this.currentCapturePoints <= 0)
		{
			this.setCaptured(true);
		}
		this.changePercentageEntity();
	}
	
	public void setCaptured(boolean captured)
	{
		if (captured && !this.isCaptured)
		{
			this.isCaptured = captured;
			
			this.stopCaptureTask();
			this.stopCircleTask();
			SiegeScenario scenario = Scenarios.findScenario(this.scenarioID);
			MainObjective mo = scenario.getMainObjective();
			mo.setCurrentCapturePoints(mo.getCurrentCapturePoints()-100);
			for (User user : this.getPlayers())
			{
				user.getPlayer().sendMessage(ColorOptions.messageachievement + "Succesfully captured an Objective!");
			}
		}
	}
	
	public void setActive(boolean active)
	{
		boolean changedValue = false;
		
		if (this.isActive != active)
		{
			changedValue = true;
		}
		this.isActive = active;
		
		if (this.isActive)
		{
			this.startCaptureTask();
			this.startCircleTask();
			this.setPercentageEntity();
			
			if (this.instance instanceof SideObjective)
			{
				SideObjective objective = (SideObjective) this;
				objective.activate(true);
			}
		} else
		{
			this.stopCaptureTask();
			this.stopCircleTask();
			this.removePercentageEntity();
			
			if (this.instance instanceof SideObjective)
			{
				SideObjective objective = (SideObjective) this;
				objective.activate(false);
			}
		}
	}
	
	public void addTestingList(User user)
	{
		if (!this.testingList.contains(user))
		{
			this.testingList.add(user);
		}
		if (this.testingList.size() == 1)
		{
			this.startCircleTask();
			this.startCaptureTask();
			this.setPercentageEntity();
		}
	}
	
	public void removeTestingList(User user)
	{
		if (this.testingList.contains(user))
		{
			this.testingList.remove(user);
		}
		
		if (this.testingList.size() <= 0)
		{
			this.stopCaptureTask();
			this.stopCircleTask();
		}
	}
	
	public void stopCircleTask()
	{
		if (this.circleTask != null)
		{
			this.circleTask.cancel();
		}
	}
	
	public void startCircleTask()
	{
		List<User> receivers = new ArrayList<User>();
		
		receivers.addAll(this.getPlayers());
				
		double originalX = this.getLocation().getX();
		double originalZ = this.getLocation().getZ();
		
		this.circleTask = new BukkitRunnable()
		{
			public void run()
			{
				for(double i = 0; i < 360.0; i+=4) {
					double angle = i * Math.PI / 180;
	                double x = captureRadius * Math.cos(angle);
	                double z = captureRadius * Math.sin(angle);
	                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME,true, (float) (originalX+x), (float) (bannerBlock.getY()+0.1), (float) (originalZ+z), 0, 0, 0, 0, 1);
		            for(User participant : receivers) {
		                ((CraftPlayer)participant.getPlayer()).getHandle().playerConnection.sendPacket(packet);
		            }
	            }
//				for (double i = 0.0; i < 360.0; i += 0.1) 
//				{
//			        double angle = i * Math.PI / 180;
//		            double x = (int)(bannerBlock.getX() + 2 * Math.cos(angle));
//		            double z = (int)(bannerBlock.getZ() + 2 * Math.sin(angle));
//			     
//		            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FLAME,true, (float) (x), (float) (bannerBlock.getY()+0.1), (float) (z), 0, 0, 0, 0, 1);
//		            for(User participant : receivers) {
//		                ((CraftPlayer)participant.getPlayer()).getHandle().playerConnection.sendPacket(packet);
//		            }
//		        }
			}
		}.runTaskTimerAsynchronously(main, 0, 20);
	}
	
	public void stopCaptureTask()
	{
		if (this.captureTask != null)
		{
			this.captureTask.cancel();
		}
	}
	
	public void startCaptureTask()
	{
		this.captureTask = new BukkitRunnable()
		{
			public void run()
			{
				Integer removePoints = calculateCapturePoints();
				setCurrentCapturePoints((getCurrentCapturePoints()-removePoints));
			}
		}.runTaskTimerAsynchronously(main, 0, 1*20);
	}
	
	private void setBannerStages(DyeColor originalColor, DyeColor attackingColor)
	{
		this.patternList.addAll(Arrays.asList(
			new ArrayList<Pattern>(Arrays.asList(
					new Pattern(attackingColor, PatternType.BASE)
					)),
			new ArrayList<Pattern>(Arrays.asList(
					new Pattern(originalColor, PatternType.BASE),
					new Pattern(attackingColor, PatternType.GRADIENT)
					)),
			new ArrayList<Pattern>(Arrays.asList(
					new Pattern(originalColor, PatternType.BASE),
					new Pattern(attackingColor, PatternType.GRADIENT),
					new Pattern(originalColor, PatternType.GRADIENT_UP)
					)),
			new ArrayList<Pattern>(Arrays.asList(
					new Pattern(originalColor, PatternType.BASE),
					new Pattern(attackingColor, PatternType.GRADIENT),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP)
					)),
			new ArrayList<Pattern>(Arrays.asList(
					new Pattern(originalColor, PatternType.BASE),
					new Pattern(attackingColor, PatternType.GRADIENT),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP)
					)),
			new ArrayList<Pattern>(Arrays.asList(
					new Pattern(originalColor, PatternType.BASE),
					new Pattern(attackingColor, PatternType.GRADIENT),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP)
					)),
			new ArrayList<Pattern>(Arrays.asList(
					new Pattern(DyeColor.RED, PatternType.BASE),
					new Pattern(attackingColor, PatternType.GRADIENT),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP),
					new Pattern(originalColor, PatternType.GRADIENT_UP)
					)),
			new ArrayList<Pattern>(Arrays.asList(
					new Pattern(originalColor, PatternType.BASE)
					))
			));
	}
}
