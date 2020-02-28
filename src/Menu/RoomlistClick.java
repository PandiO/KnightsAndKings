package Menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Rooms.Room;
import Streets.Street;
import Towns.Town;
import Tutorial.Tutorial;
import Tutorial.TutorialEvents;
import Users.User;

public class RoomlistClick
{
	Menu menu = new Menu();
	Room room = new Room();
	Street street = new Street();
	Town town = new Town();
	private Main main;
	public RoomlistClick(Main main) 
	{
		this.main = main;
	}
	private Float clickVolume = main.clickVolume;
	
	public void onClick(InventoryClickEvent e, User user)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player player = (Player) e.getWhoClicked();
			ItemStack clicked = e.getCurrentItem();
			
			Tutorial tutorial = null;
			for (Tutorial tut : TutorialEvents.tutorials)
			{
				if (tut.target == player)
				{
					tutorial = tut;
					break;
				}
			}
			String dc = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
			List<String> lore = clicked.getItemMeta().getLore();
			e.setCancelled(true);
			if (tutorial != null)
			{
				if (dc.contains("back"))
				{
					tutorial.previousStage();
				}
				if (tutorial.getName().equalsIgnoreCase("room tutorial"))
				{
					if (tutorial.stage == 6)
					{
						if (dc.contains("roomnumber"))
						{
							Integer roomNumber = Integer.valueOf(dc.split("roomnumber: ")[1]);
							Integer roomID = this.menu.getMenuStructureID(lore, "room", roomNumber);
							if (roomID != null)
							{
								if (room.getOwnerID(roomID) == 0)
								{
									player.closeInventory();
									Bukkit.dispatchCommand(player, "room rent " + roomID);
									
								} else if (room.getOwnerID(roomID) == user.getID())
								{
									player.sendMessage(ColorOptions.statsresults + "You are already renting this room!");
								} else
								{
									player.sendMessage(ColorOptions.falsecommand + "This room is rented by someone else!");
								}
							}
							tutorial.nextStage(null, null);
							player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, clickVolume, 1.0F);
						}
					} else if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Not the correct stage: " + tutorial.stage);
					}
				} else if (main.debug)
				{
					Bukkit.getConsoleSender().sendMessage("Not the same");
				}
			} else
			{
				if (dc.contains("back"))
				{
					this.menu.openownedHouses(user);
				}
				if (dc.contains("next"))
				{
					Integer current = Integer.valueOf(e.getCurrentItem().getItemMeta().getLore().get(2).split(": ")[1]);
					this.menu.openRoomlist(user, current+1, null);
					player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, clickVolume, 1.0F);
				}
				if (dc.contains("previous"))
				{
					Integer current = Integer.valueOf(e.getCurrentItem().getItemMeta().getLore().get(2).split(": ")[1]);
					this.menu.openRoomlist(user, current-1, null);
					player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, clickVolume, 1.0F);
				}
				if (dc.contains("Town: "))
				{
					if (e.getClick() == ClickType.SHIFT_LEFT)
					{
						this.menu.openRoomlist(user, 1, null);
						return;
					}
					List<String> townList = town.getTownNameList();
					Integer townID = null;
					String townfilter = dc.split(": ")[1];
					if (main.debug)
					{
						Bukkit.getConsoleSender().sendMessage("Report: townfilter: " + townfilter);
						Bukkit.getConsoleSender().sendMessage(townList.toString());
					}
					if (townfilter.equalsIgnoreCase("all"))
					{
						townID = town.getTownID(townList.get(0));
					} else if (townList.contains(townfilter))
					{
						Integer index = (townList.indexOf(townfilter)+1);
						if (index < townList.size())
						{
							townID = town.getTownID(townList.get(index));
							if (main.debug)
							{
								Bukkit.getConsoleSender().sendMessage("Report: townfilter: " + townfilter + ", index: " + index + ", next town: " + townList.get(index) + ", list size: " + townList.size());
							}
						} else
						{
							townID = null;
						}
					} else
					{
						if (main.debug)
						{
							Bukkit.getConsoleSender().sendMessage("Town is not a valid town");
							Bukkit.getConsoleSender().sendMessage("Towns: " + townList.toString());
						}
						townID = town.getTownID(townList.get(0));
					}
					this.menu.openRoomlist(user, 1, townID);
				}
				if (dc.contains("roomnumber"))
				{
					Integer roomNumber = Integer.valueOf(dc.split("roomnumber: ")[1]);
					Integer roomID = this.menu.getMenuStructureID(lore, "room", roomNumber);
					if (roomID != null)
					{
						if (room.getOwnerID(roomID) == 0)
						{
							player.closeInventory();
							Bukkit.dispatchCommand(player, "room rent " + roomID);
							
						} else if (room.getOwnerID(roomID) == user.getID())
						{
							player.sendMessage(ColorOptions.statsresults + "You are already renting this room!");
						} else
						{
							player.sendMessage(ColorOptions.falsecommand + "This room is rented by someone else!");
						}
					}
				}
			}
		}
	}
}
