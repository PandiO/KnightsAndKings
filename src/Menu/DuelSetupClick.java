package Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Arenas.Duel;
import Arenas.DuelCommands;
import Arenas.DuelInvite;
import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Products.Product;
import Users.User;

public class DuelSetupClick implements Listener
{
	Product product = new Product();
	Menu menu = new Menu();
	Main main = Main.getPlugin(Main.class);
	public Integer senderReady = 46;
	public Integer targetReady = 52;
	public Integer senderType = 48;
	public Integer targetType = 50;
	public DuelSetupClick(Main main) 
	{
		this.main = main;
		// TODO Auto-generated constructor stub
	}
	
	public static HashMap<UUID, Inventory> coinBetList = new HashMap<UUID, Inventory>();
	
	public void onClick(InventoryClickEvent e, User user)
	{
		boolean cancelled = true;
		Player player = (Player) e.getWhoClicked();
		UUID uuid = player.getUniqueId();
		Inventory menu = e.getClickedInventory();
		DuelInvite invite = null;
		for (DuelInvite inv : DuelCommands.inviteList)
		{
			if (inv.sender.getUUID() == uuid)
			{
				invite = inv;
			} else if (inv.target.getUUID() == uuid)
			{
				invite = inv;
			}
		}
		if (invite != null)
		{
			if (invite.sender.getUUID() == uuid)
			{
				if (invite.senderClickList.contains(e.getSlot()) || invite.senderItemList.contains(e.getSlot()))
				{
					ItemStack item = e.getCurrentItem();
					if (item.hasItemMeta())
					{
						String clickedName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
						if (clickedName.equalsIgnoreCase("back"))
						{
							player.closeInventory();
							user.playSound("back");
						} else if (clickedName.equalsIgnoreCase("unready"))
						{
							if (menu.getItem(senderType).getType() == menu.getItem(targetType).getType())
							{
								menu.setItem(e.getSlot(), product.createClayItem(ChatColor.GREEN + "Ready", true, ColorOptions.message + "Both players have to", ColorOptions.message + "set this as ready to duel"));
								checkReady(menu, invite);
								cancelled = true;
							} else
							{
								menu.setItem(e.getSlot(), product.createClayItem(ColorOptions.error + "Unready", false, ColorOptions.message + "Both players have to", ColorOptions.message + "set this as ready to duel", "", ColorOptions.error + "The duel type has to be the same!"));
								player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
								player.sendMessage(ColorOptions.error + "Both players have to set the same duel type!");
							}
						} else if (clickedName.equalsIgnoreCase("ready"))
						{
							menu.setItem(e.getSlot(), product.createClayItem(ColorOptions.error + "Unready", false, ColorOptions.message + "Both players have to", ColorOptions.message + "set this as ready to duel"));
						} else if (clickedName.contains("Duel type: "))
						{
							if (item.getType() == Material.GOLD_BLOCK)
							{
								if (e.getClick() == ClickType.SHIFT_LEFT)
								{
									coinBetList.put(uuid, menu);
									player.closeInventory();
									player.sendMessage(ColorOptions.messageformat + "Type the amount of coins you want to bet:");
								} else
								{
									menu.setItem(e.getSlot(), MenuClick.addmenulist(ColorOptions.stats + "Duel type: Items", Material.CHEST, ColorOptions.message + "Place items in this menu", ColorOptions.message + "as bet and the winner will", ColorOptions.message + "receive both bets"));
								}
							} else if (item.getType() == Material.CHEST)
							{
								menu.setItem(e.getSlot(), MenuClick.addmenulist(ColorOptions.statsformat + "Duel type: Practice", Material.WOOD_SWORD, ColorOptions.message + "Friendly fight without bets"));
							} else if (item.getType() == Material.WOOD_SWORD)
							{
								menu.setItem(e.getSlot(), MenuClick.addmenulist(ColorOptions.coinStats + "Duel type: Coins", Material.GOLD_BLOCK, ColorOptions.message + "Place an amount of coins", ColorOptions.message + "as bet and the winner will", ColorOptions.message + "receive both bets", "", ColorOptions.stats + "Shit-click here", ColorOptions.stats + "to place a bet!", ColorOptions.coinStats + "Current bet: 0"));
							}
						} else if (invite.senderItemList.contains(e.getSlot()))
						{
							if (ChatColor.stripColor(menu.getItem(senderReady).getItemMeta().getDisplayName()).equalsIgnoreCase("Unready"))
							{
								cancelled = false;
							} else
							{
								player.sendMessage(ColorOptions.error + "You cannot add/remove items when " + ChatColor.GREEN + "Ready");
							}
						}
					}
				}
			} else if (invite.target.getUUID() == uuid)
			{
				if (invite.targetClickList.contains(e.getSlot()) || invite.targetItemList.contains(e.getSlot()))
				{
					ItemStack item = e.getCurrentItem();
					if (item.hasItemMeta())
					{
						String clickedName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
						if (clickedName.equalsIgnoreCase("back"))
						{
							player.closeInventory();
						} else if (clickedName.equalsIgnoreCase("unready"))
						{
							if (menu.getItem(senderType).getType() == menu.getItem(targetType).getType())
							{
								menu.setItem(e.getSlot(), product.createClayItem(ChatColor.GREEN + "Ready", true, ColorOptions.message + "Both players have to", ColorOptions.message + "set this as ready to duel"));
								checkReady(menu, invite);
								cancelled = true;
							} else
							{
								menu.setItem(e.getSlot(), product.createClayItem(ColorOptions.error + "Unready", false, ColorOptions.message + "Both players have to", ColorOptions.message + "set this as ready to duel", "", ColorOptions.error + "The duel type has to be the same!"));
								player.playSound(player.getLocation(), SoundHandler.NOTE_BASS, 1.0F, 1.0F);
								player.sendMessage(ColorOptions.error + "Both players have to set the same duel type!");
							}
						} else if (clickedName.equalsIgnoreCase("ready"))
						{
							menu.setItem(e.getSlot(), product.createClayItem(ColorOptions.error + "Unready", false, ColorOptions.message + "Both players have to", ColorOptions.message + "set this as ready to duel"));
						} else if (clickedName.contains("Duel type: "))
						{
							if (item.getType() == Material.GOLD_BLOCK)
							{
								if (e.getClick() == ClickType.SHIFT_LEFT)
								{
									coinBetList.put(uuid, menu);
									player.closeInventory();
									player.sendMessage(ColorOptions.messageformat + "Type the amount of coins you want to bet:");
								} else
								{
									menu.setItem(e.getSlot(), MenuClick.addmenulist(ColorOptions.stats + "Duel type: Items", Material.CHEST, ColorOptions.message + "Place items in this menu", ColorOptions.message + "as bet and the winner will", ColorOptions.message + "receive both bets"));
								}								
							} else if (item.getType() == Material.CHEST)
							{
								menu.setItem(e.getSlot(), MenuClick.addmenulist(ColorOptions.statsformat + "Duel type: Practice", Material.WOOD_SWORD, ColorOptions.message + "Friendly fight without bets"));
							} else if (item.getType() == Material.WOOD_SWORD)
							{
								menu.setItem(e.getSlot(), MenuClick.addmenulist(ColorOptions.coinStats + "Duel type: Coins", Material.GOLD_BLOCK, ColorOptions.message + "Place an amount of coins", ColorOptions.message + "as bet and the winner will", ColorOptions.message + "receive both bets", "", ColorOptions.stats + "Shit-click here", ColorOptions.stats + "to place a bet!", ColorOptions.coinStats + "Current bet: 0"));
							}
						} else if (invite.targetItemList.contains(e.getSlot()))
						{
							if (ChatColor.stripColor(menu.getItem(targetReady).getItemMeta().getDisplayName()).equalsIgnoreCase("Unready"))
							{
								cancelled = false;
							} else
							{
								player.sendMessage(ColorOptions.error + "You cannot add/remove items when " + ChatColor.GREEN + "Ready");
							}
						}
					}
				}
			}
		}
		e.setCancelled(cancelled);
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent e)
	{
		if (e.getWhoClicked() instanceof Player)
		{
			Player player = (Player) e.getWhoClicked();
			UUID uuid = player.getUniqueId();
			Inventory menu = player.getOpenInventory().getTopInventory();
			String menuname = menu.getName();
			if (ChatColor.stripColor(menuname).equalsIgnoreCase("choose duel-type and place bets!"))
			{
				DuelInvite invite = null;
				for (DuelInvite inv : DuelCommands.inviteList)
				{
					if (inv.sender.getUUID() == uuid)
					{
						invite = inv;
					} else if (inv.target.getUUID() == uuid)
					{
						invite = inv;
					}
				}
				if (invite != null)
				{
					if (invite.sender.getUUID() == uuid)
					{
						if (ChatColor.stripColor(menu.getItem(senderReady).getItemMeta().getDisplayName()).equalsIgnoreCase("Ready"))
						{
							player.sendMessage(ColorOptions.error + "You cannot add/remove items when " + ChatColor.GREEN + "Ready");
							e.setCancelled(true);
						}
					} else if (invite.target.getUUID() == uuid)
					{
						if (ChatColor.stripColor(menu.getItem(targetReady).getItemMeta().getDisplayName()).equalsIgnoreCase("Ready"))
						{
							player.sendMessage(ColorOptions.error + "You cannot add/remove items when " + ChatColor.GREEN + "Ready");
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	public void checkReady(Inventory menu, DuelInvite invite)
	{
		if (ChatColor.stripColor(menu.getItem(senderReady).getItemMeta().getDisplayName()).equalsIgnoreCase("Ready") && ChatColor.stripColor(menu.getItem(targetReady).getItemMeta().getDisplayName()).equalsIgnoreCase("Ready"))
		{
			List<ItemStack> itemList = new ArrayList<ItemStack>();
			for (Integer slot : this.menu.duelMenuFree)
			{
				if (menu.getItem(slot) != null && menu.getItem(slot).getType() != Material.AIR)
				{
					itemList.add(menu.getItem(slot));
				}
			}
			Integer coins = 0;
			if (menu.getItem(48).getType() == Material.GOLD_BLOCK)
			{
				coins = Integer.valueOf(menu.getItem(senderType).getItemMeta().getLore().get(menu.getItem(48).getItemMeta().getLore().size()-1).split(": ")[1]);
			}
			
			Player sender = invite.sender.getPlayer();
			Player target = invite.target.getPlayer();

			Integer arenaID = invite.arenaID;
			for (User user : invite.getPlayers())
			{
				user.getPlayer().sendMessage(ColorOptions.messageachievement + "Duel has been approved!");
				user.removeCoins(coins);
			}
			invite.removeInvite();
			Duel duel = new Duel(sender, target, arenaID, "normal", itemList, coins*2);
			duel.tryStart();
		} else
		{
			for (User user : invite.getPlayers())
			{
				user.getPlayer().sendMessage(ColorOptions.messageachievement + "Both players have to be Ready!");
			}
		}
	}
}
