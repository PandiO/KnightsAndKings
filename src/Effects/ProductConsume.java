package Effects;

import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Assignments.Assignment;
import Assignments.AssignmentFoodConsumeRandom;
import Exceptions.UserNotFoundException;
import Handlers.ColorOptions;
import Handlers.ErrorHandlers;
import Main.Main;
import Products.Product;
import Users.User;
import Users.Users;

public class ProductConsume implements Listener
{
	Effect effect = new Effect();
	Product product = new Product();
	private Main main;
	public ProductConsume(Main main) 
	{
		this.main = main;
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent e) throws SQLException
	{
		Player player = e.getPlayer();
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
		ItemStack item = e.getItem();
		
		if (item.hasItemMeta())
		{
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName())
			{
				String displayName = meta.getDisplayName();
				if (product.getProductIDbyDisplayName(displayName, false) != null)
				{
					Random random = new Random();
					Integer productID = product.getProductIDbyDisplayName(displayName, false);
					Integer grade = product.getGrade(productID, false);
					Integer chance = 5;
					switch(grade)
					{
					case 4: 
						chance = 15;
						break;
					case 5:
						chance = 25;
						break;
					}
					if (random.nextInt(100) <= chance)
					{
						Integer beneficialChance = 30;
						Integer harmfulChance = 10;
						if (grade == 1)
						{
							harmfulChance = 30;
							beneficialChance = 97;
						} else if (grade == 2)
						{
							harmfulChance = 10;
							beneficialChance = 95;
						} else if (grade == 3)
						{
							harmfulChance = 5;
							beneficialChance = 95;
						} else if (grade == 4)
						{
							harmfulChance = 1;
							beneficialChance = 90;
						} else if (grade == 5)
						{
							harmfulChance = 1;
							beneficialChance = 80;
						}
						
						Integer rand = random.nextInt(100);
						
						Integer effectID = null;
						if (rand <= harmfulChance)
						{
							for (Integer ID : effect.getSpecificIDList(false))
							{
								Integer grade2 = effect.getName(ID).getInt("Grade");
								if (grade2 == 2)
								{
									if (random.nextInt(100) <= 30)
									{
										effectID = ID;
										break;
									}
								} else
								{
									if (random.nextInt(100) <= 50)
									{
										effectID = ID;
										break;
									}
								}
							}
							if (effectID != null)
							{
								addEffect(player, effectID, false);
							}
						}
						if (rand >= beneficialChance)
						{
							for (Integer ID : effect.getSpecificIDList(true))
							{
								Integer grade3 = effect.getName(ID).getInt("Grade");
								if (grade3 == 2)
								{
									if (random.nextInt(100) <= 30)
									{
										effectID = ID;
										break;
									}
								} else
								{
									if (random.nextInt(100) <= 50)
									{
										effectID = ID;
										break;
									}
								}
							}
							if (effectID != null)
							{
								addEffect(player, effectID, true);
							}
						}
					}
					for (Assignment assignment : user.getAssignmentList())
					{
						if (assignment instanceof AssignmentFoodConsumeRandom)
						{
							AssignmentFoodConsumeRandom Assignment = (AssignmentFoodConsumeRandom) assignment;
							Assignment.consume(1);
							break;
						}
					}
				}
			}
		}
	}
	
	public void addEffect(Player player, Integer effectID, boolean beneficial) throws SQLException
	{
		UUID uuid = player.getUniqueId();
		Integer duration = 600;
		Integer delay = 10;
		Integer value = 5;
		switch(effectID)
		{
		case 1:
			duration = main.getRandom(300, 600);
			delay = 30;
			value = main.getRandom(2, 6);
			effect.addMTB(uuid, effectID, duration, delay, value);
			break;
		case 2:
			duration = main.getRandom(150, 600);
			delay = 30;
			effect.addDJ(uuid, effectID, duration, delay, main.getRandom(2, 4), main.getRandom(3, 7), main.getRandom(2, 4));
			break;
		case 3:
			duration = main.getRandom(150, 600);
			delay = 30;
			value = main.getRandom(60, 100);
			effect.addPG(uuid, effectID, duration, delay, value);
			break;
		case 4:
			duration = main.getRandom(300, 1200);
			delay = 30;
			effect.addCB(uuid, effectID, duration, delay, main.getRandom(2000, 10000), main.getRandom(10000, 20000));
			break;
		case 5:
			duration = main.getRandom(600, 3600);
			delay = 120;
			value = main.getRandom(2, 6);
			effect.addHB(uuid, effectID, duration, delay, (double) value);
			break;
		case 6:
			duration = main.getRandom(300, 1200);
			delay = 30;
			effect.addGB(uuid, effectID, duration, delay, main.getRandom(1, 3), main.getRandom(10, 20));
			break;
		case 8:
			duration = main.getRandom(7200, 172800);
			delay = duration/(duration/2);
			Integer random = main.getRandom(0, 100);
			if (random <= 30)
			{
				effect.addRB(uuid, effectID, duration, 3);
			} else if (random <= 60)
			{
				effect.addRB(uuid, effectID, duration, 2);
			} else if (random <= 100)
			{
				effect.addRB(uuid, effectID, duration, 1);
			} else
			{
				effect.addRB(uuid, effectID, duration, 1);
			}
			break;
		}
		
		int min = duration/60;
		
		if (beneficial == true)
		{
			player.sendMessage(ColorOptions.messageachievement + "You received the effect " + effect.getName(effectID).getString("Name") + " for " + min + " minutes!");
		} else
		{
			player.sendMessage(ColorOptions.falsecommand + "You received the effect " + effect.getName(effectID).getString("Name") + " for " + min + " minutes!");

		}
	}
}
