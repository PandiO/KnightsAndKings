package Arenas;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import Handlers.ColorOptions;
import Main.Main;
import Menu.DuelSetupClick;
import Users.User;
import Users.Users;

public class DuelEvents implements Listener
{
	Main main = Main.getPlugin(Main.class);
	public DuelEvents(Main main) 
	{
		this.main = main;
		// TODO Auto-generated constructor stub
	}
	
//	@EventHandler
//	public void onMove(PlayerMoveEvent e)
//	{
//		Player player = e.getPlayer();
//		UUID uuid = player.getUniqueId();
//		if (!main.duelList.isEmpty())
//		{
//			Duel duel = main.duelList.get(0);
//			if (duel.countDownMove)
//			{
//				for (User fighter : duel.getPlayers())
//				{
//					if (uuid == fighter.getUUID())
//					{
//						if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ())
//						{
//							if (duel.user1.getUUID() == uuid)
//							{
//								player.teleport(duel.arenaPlayer1);
//							} else
//							{
//								player.teleport(duel.arenaPlayer2);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent e)
	{
		if (e.getEntity() instanceof Player && e.getEntity().getKiller() instanceof Player)
		{
			if (!main.duelList.isEmpty())
			{
				Duel duel = main.duelList.get(0);
				if (duel.duelExpire != null)
				{
					Player died = (Player) e.getEntity();
					Player killer = (Player) e.getEntity().getKiller();
					User killeru = Users.getUser(killer.getUniqueId());
					User diedu = Users.getUser(died.getUniqueId());
					if (diedu == duel.user1 || diedu == duel.user2)
					{
						if (killeru == duel.user1 || killeru == duel.user2)
						{
							duel.endDuel(true, killeru, diedu, duel.winTime);
						} else
						{
							duel.endDuel(false, null, null, 1);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		User user = Users.getUser(player.getUniqueId());
		UUID uuid = player.getUniqueId();
		if (!main.duelList.isEmpty())
		{
			for (Duel duel : main.duelList)
			{
				if (duel.user1 == user)
				{
					duel.endDuel(true, duel.user2, duel.user1, 1);
					duel.sendMessage(duel.user2.getUUID(), ColorOptions.error + "Your opponent left!");
				}
				if (duel.user2 == user)
				{
					duel.endDuel(true, duel.user1, duel.user2, 1);
					duel.sendMessage(duel.user1.getUUID(), ColorOptions.error + "Your opponent left!");
				}
			}
		}
		if (!DuelCommands.inviteList.isEmpty())
		{
			for (DuelInvite invite : DuelCommands.inviteList)
			{
				if (invite.sender == player)
				{
					invite.removeInvite();
				} else if (invite.target == player)
				{
					invite.sender.getPlayer().sendMessage(ColorOptions.error + "Your opponent left!");
					invite.removeInvite();
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e)
	{
		if (e.getPlayer() instanceof Player)
		{
			Player player = (Player) e.getPlayer();
			UUID uuid = player.getUniqueId();
			Inventory menu = e.getInventory();
			if (!DuelCommands.inviteList.isEmpty())
			{
				if (e.getInventory().getName().equalsIgnoreCase(ColorOptions.stats + "Choose duel-type and place bets!"))
				{
					for (DuelInvite invite : DuelCommands.inviteList)
					{
						if (invite.sender.getUUID() == uuid)
						{
							if (!DuelSetupClick.coinBetList.containsKey(uuid))
							{
								for (Integer slot : invite.senderItemList)
								{
									if (menu.getItem(slot) != null && menu.getItem(slot).getType() != Material.AIR)
									{
										Users.getUser(invite.sender.getUUID()).inventoryAddItem(menu.getItem(slot));
									}
								}
								invite.target.getPlayer().sendMessage(ColorOptions.error + "Player " + player.getName() + " cancelled the duel setup!");
								player.sendMessage(ColorOptions.error + "You cancelled the duel setup!");
								removeInvite(invite);
							}
							return;
						} else if (invite.target.getUUID() == uuid)
						{
							if (!DuelSetupClick.coinBetList.containsKey(uuid))
							{
								for (Integer slot : invite.targetItemList)
								{
									if (menu.getItem(slot) != null && menu.getItem(slot).getType() != Material.AIR)
									{
										Users.getUser(invite.target.getUUID()).inventoryAddItem(menu.getItem(slot));
									}
								}
								invite.sender.getPlayer().sendMessage(ColorOptions.error + "Player " + invite.target.getUsername() + " cancelled the duel setup!");
								player.sendMessage(ColorOptions.error + "You cancelled the duel setup!");
								removeInvite(invite);
							}
							return;
						}
					}
				}
			}
		}
	}
//	@EventHandler
//	@SuppressWarnings("deprecation")
//	public void onChat(PlayerChatEvent e)
//	{
//		Product product = new Product();
//		Player player = e.getPlayer();
//		User user = Users.getUser(player.getUniqueId());
//		UUID uuid = player.getUniqueId();
//		if (DuelSetupClick.coinBetList.containsKey(uuid))
//		{
//			e.setCancelled(true);
//			if (main.isInt(e.getMessage()))
//			{
//				Integer coins = Integer.valueOf(e.getMessage());
//				if (user.getCoins() >= coins)
//				{
//					Inventory inv = DuelSetupClick.coinBetList.get(player.getUniqueId());
//					if (ChatColor.stripColor(inv.getItem(45).getItemMeta().getDisplayName()).equalsIgnoreCase(player.getName()))
//					{
//						User target = Users.getUser(Bukkit.getPlayer(ChatColor.stripColor(inv.getItem(53).getItemMeta().getDisplayName())).getUniqueId());
//						if (target.getCoins() >= coins)
//						{
//							inv.setItem(48, product.addCoinBet(inv.getItem(48), coins));
//							inv.setItem(50, product.addCoinBet(inv.getItem(50), coins));
//						} else
//						{
//							player.sendMessage(ColorOptions.error + "Your opponent does not have that much coins! Both bets need to be equal!");
//						}
//					} else if (ChatColor.stripColor(inv.getItem(53).getItemMeta().getDisplayName()).equalsIgnoreCase(player.getName()))
//					{
//						User target = Users.getUser(Bukkit.getPlayer(ChatColor.stripColor(inv.getItem(48).getItemMeta().getDisplayName())).getUniqueId());
//						if (target.getCoins() >= coins)
//						{
//							inv.setItem(48, product.addCoinBet(inv.getItem(48), coins));
//							inv.setItem(50, product.addCoinBet(inv.getItem(50), coins));
//						} else
//						{
//							player.sendMessage(ColorOptions.error + "Your opponent does not have that much coins! Both bets need to be equal!");
//						}
//					}
//					player.openInventory(inv);
//					DuelSetupClick.coinBetList.remove(player.getUniqueId());
//				} else
//				{
//					player.sendMessage(ColorOptions.error + "You don't have enough coins!");
//					player.sendMessage(ColorOptions.message + "Your coins: " + ColorOptions.formatCurrency(user.getCoins()));
//				}
//			} else if (e.getMessage().equalsIgnoreCase("cancel"))
//			{
//				Inventory inv = DuelSetupClick.coinBetList.get(player.getUniqueId());
//				player.openInventory(inv);
//				DuelSetupClick.coinBetList.remove(player.getUniqueId());
//			} else
//			{
//				player.sendMessage(ColorOptions.error + "Please specify a number of coins, or to cancel type 'cancel'");
//			}
//		}
//	}
	
	public void removeInvite(DuelInvite invite)
	{
		invite.removeInvite();
	}
}
