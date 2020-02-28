package Friends;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Donator.Donator;
import Handlers.AddFriendEvent;
import Handlers.ColorOptions;
import Handlers.SoundHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class FriendCommands implements CommandExecutor
{
	Donator donator = new Donator();
	public Main main;
	public FriendCommands(Main main) 
	{
		this.main = main;
	}
	
	public List<String> commandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of friend-commands",
			ColorOptions.stats + "-/friends add <username>",
			ColorOptions.stats + "-/friends remove <username>",
			ColorOptions.stats + "-/friends list",
			ColorOptions.statsformat + "================================================="
	});
	
	public List<String> consolecommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of friend-commands",
			ColorOptions.stats + "-/friends list <username>",
			ColorOptions.statsformat + "================================================="
	});
	
	public List<String> requestcommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of request-commands",
			ColorOptions.stats + "-/request accept <username>",
			ColorOptions.stats + "-/request deny <username>",
			ColorOptions.stats + "-/request list",
			ColorOptions.statsformat + "================================================="
	});
	
	public List<String> requestconsolecommandhelp = Arrays.asList(new String[] {
			ColorOptions.statsformat + "=================================================",
			ColorOptions.statsformat + "List of request-commands",
			ColorOptions.stats + "-/request list <username>",
			ColorOptions.statsformat + "================================================="
	});
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("friends") || label.equalsIgnoreCase("friend") || label.equalsIgnoreCase("f"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				UUID uuid = player.getUniqueId();
				User user = null;
				if (Users.getUser(uuid) != null)
				{
					user = Users.getUser(uuid);
					if (args.length > 0)
					{
						if (args[0].equalsIgnoreCase("add"))
						{
							if (args.length == 2)
							{
								sendFriendRequest(user, args[1]);
							} else
							{
								player.sendMessage(ColorOptions.error + "Usage: /friends add <username>");
							}
						} else
						if (args[0].equalsIgnoreCase("remove"))
						{
							if (args.length == 2)
							{
								removeFriend(user, args[1]);
							} else
							{
								player.sendMessage(ColorOptions.error + "Usage: /friends remove <username>");
							}
						} else
						if (args[0].equalsIgnoreCase("list"))
						{
							User target = null;
							if (Users.getUser(uuid) != null)
							{
								target = Users.getUser(uuid);
							} else
							{
								target = new User(uuid);
							}
							if (target.getFriendAmount() > 10)
							{
								player.sendMessage(ColorOptions.friendformat + "Your friends:");
								for (String friend : target.getFriendNames())
								{
									player.sendMessage(ColorOptions.friendformat + "- " + ColorOptions.friendresults + friend);
								}
							} else
							{
								player.sendMessage(ColorOptions.friendformat + "Your friends:");
								for (String friend : target.getFriendNames())
								{
									player.sendMessage(ColorOptions.friendformat + "- " + ColorOptions.friendresults + friend);
								}
							}
							target.destroy();
						} else
						{
							for (String message : commandhelp)
							{
								sender.sendMessage(message);
							}
						}
				}
				} else
				{
					for (String message : commandhelp)
					{
						sender.sendMessage(message);
					}
				}
			} else
			{
				if (args.length == 2)
				{
					if (args[0].equalsIgnoreCase("list"))
					{
						if (Users.existUser(args[1]))
						{
							UUID uuid = Users.fetchUUIDbyUsername(args[1]);
							User target = null;
							if (Users.getUser(uuid) != null)
							{
								target = Users.getUser(uuid);
							} else
							{
								target = new User(uuid);
							}
							sender.sendMessage(ColorOptions.friendformat + "Friends of " + args[1] + ":");
							for (String friend : target.getFriendNames())
							{
								sender.sendMessage(ColorOptions.friendformat + "- " + ColorOptions.friendresults + friend);
							}
						}
					}
				} else
				{
					for (String message : consolecommandhelp)
					{
						sender.sendMessage(message);
					}
				}
			}
		}
		
		if (label.equalsIgnoreCase("request"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				UUID uuid = player.getUniqueId();
				User user = null;
				if (Users.getUser(uuid) != null)
				{
					user = Users.getUser(uuid);
					
					if (args.length > 0)
					{
						if (args[0].equalsIgnoreCase("accept"))
						{
							if (args.length == 2)
							{
								acceptRequest(user, args[1]);
							}
						} else
						if (args[0].equalsIgnoreCase("deny"))
						{
							if (args.length == 2)
							{
								denyRequest(user, args[1]);
							}
						} else
						if (args[0].equalsIgnoreCase("list"))
						{
							player.sendMessage(ColorOptions.friendformat + "Your friend-requests:");
							for (UUID targetuuid : user.getFriendRequestList())
							{
								player.sendMessage(ColorOptions.friendformat + "- " + ColorOptions.friendresults + user.getUsername());
							}
						} else
						{
							for (String message : requestcommandhelp)
							{
								sender.sendMessage(message);
							}
						}
					} else
					{
						for (String message : requestcommandhelp)
						{
							sender.sendMessage(message);
						}
					}
				}
			} else
			{
				if (args.length == 2)
				{
					if (args[0].equalsIgnoreCase("list"))
					{
						if (Users.existUser(args[1]))
						{
							User target = null;
							UUID targetUUID = Users.fetchUUIDbyUsername(args[1]);
							if (Users.getUser(targetUUID) != null)
							{
								target = Users.getUser(targetUUID);
							} else
							{
								target = new User(targetUUID);
							}
							
							sender.sendMessage(ColorOptions.friendformat + "Friend-requests of " + args[1] + ":");
							for (String friendNames : target.getFriendRequestNames())
							{
								sender.sendMessage(ColorOptions.friendformat + "- " + ColorOptions.friendresults + friendNames);
							}
						}
					}
				} else
				{
					for (String message : requestconsolecommandhelp)
					{
						sender.sendMessage(message);
					}
				}
			}
		}
		
		if (label.equalsIgnoreCase("requests"))
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				UUID uuid = player.getUniqueId();
				if (args.length == 0)
				{
					User user = null;
					if (Users.getUser(uuid) != null)
					{
						user = Users.getUser(uuid);
						
						player.sendMessage(ColorOptions.friendformat + "Your friend-requests:");
						for (String friendName : user.getFriendRequestNames())
						{
							player.sendMessage(ColorOptions.friendformat + "- " + ColorOptions.friendresults + friendName);
						}
					} else
					{
						sender.sendMessage(ColorOptions.error + "Error found! Please reconnect!");
					}
				} else
				{
					for (String message : requestcommandhelp)
					{
						sender.sendMessage(message);
					}
				}
			} else
			{
				sender.sendMessage(ColorOptions.falsecommand + "You need to be a player to perform this command!");
			}
		}
		return false;
	}
	
	public void sendFriendRequest(User sender, String targetUsername)
	{
		
		try
		{
			if (Users.existUser(targetUsername))
			{
				User target = null;
				UUID targetUUID = Users.fetchUUIDbyUsername(targetUsername);
				if (Users.getUser(targetUUID) != null)
				{
					target = Users.getUser(targetUUID);
				} else
				{
					target = new User(targetUUID);
				}
				
				if (!sender.getUsername().equalsIgnoreCase(targetUsername))
				{
					UUID senderUUID = sender.getUUID();
					//Check if the users are already friends
					if (!sender.getFriendNames().contains(targetUsername.toLowerCase()))
					{
						//Check if the target already sent the sender a request
						if (!sender.getFriendRequestList().contains(targetUUID))
						{
							//Check if the sender already sent the target a request
							if (!target.getFriendRequestList().contains(senderUUID))
							{
								target.saveFriendRequest(targetUUID);
								
								for (Player player : Bukkit.getOnlinePlayers())
								{
									if (player.getName().equalsIgnoreCase(targetUsername))
									{
										player.playSound(player.getLocation(), SoundHandler.ORB_PICKUP, 3.0F, 3.0F);
										player.sendMessage(ColorOptions.friendresults + sender.getUsername() + ColorOptions.friend + " sent you a friend request, type /request accept " + ColorOptions.friendresults + sender.getUsername());
										break;
									}
								}
								
								sender.getPlayer().sendMessage(ColorOptions.friend + "Request has been sent to " + ColorOptions.friendresults + targetUsername);
							} else
							{
								sender.getPlayer().sendMessage(ColorOptions.falsecommand + "You already sent a request to " + targetUsername);
							}
						} else
						{
							sender.getPlayer().sendMessage(ColorOptions.falsecommand + "This player already sent you a request");
						}
					} else
					{
						sender.getPlayer().sendMessage(ColorOptions.falsecommand + "You already are friends with " + targetUsername);
					}	
				} else
				{
					sender.getPlayer().sendMessage(ColorOptions.falsecommand + "You can't invite yourself!");
				}
				target.destroy();
			} else
			{
				sender.getPlayer().sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.getPlayer().sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
	
	public void removeFriend(User sender, String targetUsername)
	{
		try
		{
			if (Users.existUser(targetUsername))
			{
				User target = null;
				UUID targetUUID = Users.fetchUUIDbyUsername(targetUsername);
				if (Users.getUser(targetUUID) != null)
				{
					target = Users.getUser(targetUUID);
				} else
				{
					target = new User(targetUUID);
				}
				
				String senderName = sender.getUsername();
				UUID senderUUID = sender.getUUID();
				if (!senderName.equalsIgnoreCase(targetUsername))
				{
					if (sender.isFriends(targetUUID))
					{
						sender.deleteFriend(targetUUID);
				        Bukkit.getServer().getPluginManager().callEvent(new AddFriendEvent(sender, targetUUID, sender.getPlayer().getWorld()));
						sender.getPlayer().sendMessage(ColorOptions.friendformat + "Removed " + ColorOptions.friendresults + targetUsername + ColorOptions.friendformat + " from your friends list!");
					} else if (target.getFriendRequestList().contains(senderUUID)) 
					{
						target.deleteFriendRequest(senderUUID);
						sender.getPlayer().sendMessage(ColorOptions.friendformat + "Revoked friend request sent to " + ColorOptions.friendresults + targetUsername + ColorOptions.friendformat + "!");
					} else
					{
						sender.getPlayer().sendMessage(ColorOptions.falsecommand + "You are no friends with player!");
					}
				} else
				{
					sender.getPlayer().sendMessage(ColorOptions.friendformat + "Woah you tried to unfriend yourself?! Horrible things happened to people who tried to unfriend themselves!");
				}
				target.destroy();
			} else
			{
				sender.getPlayer().sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.getPlayer().sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
	
	public void acceptRequest(User sender, String targetUsername)
	{
		try
		{
			if (Users.existUser(targetUsername))
			{
				UUID targetUUID = Users.fetchUUIDbyUsername(targetUsername);
				UUID senderUUID = sender.getUUID();
				
				if (!sender.getFriendNames().contains(targetUsername.toLowerCase()))
				{
					if (sender.getFriendRequestList().contains(targetUUID))
					{
						sender.saveFriends(targetUUID); 
				        Bukkit.getServer().getPluginManager().callEvent(new AddFriendEvent(sender, targetUUID, sender.getPlayer().getWorld()));
						sender.getPlayer().sendMessage(ChatColor.GREEN + "Added " + targetUsername + ChatColor.GREEN + " to your friends list");
						
						for (Player player : Bukkit.getOnlinePlayers())
						{
							if (player.getName().equalsIgnoreCase(targetUsername))
							{
								player.sendMessage(ChatColor.GREEN + sender.getUsername() + " Accepted your friend request");
								break;
							}
						}
					} else
					{
						sender.getPlayer().sendMessage(ColorOptions.falsecommand + "You don't have a request from " + targetUsername);
					}
				} else
				{
					sender.getPlayer().sendMessage(ColorOptions.falsecommand + "You already are friends with " + targetUsername);
				}
			} else
			{
				sender.getPlayer().sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.getPlayer().sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
	
	public void denyRequest(User sender, String targetUsername)
	{
		try
		{
			if (Users.existUser(targetUsername))
			{
				UUID targetUUID = Users.fetchUUIDbyUsername(targetUsername);
				UUID senderUUID = sender.getUUID();
				
				if (sender.getFriendRequestList().contains(targetUUID))
				{
					sender.deleteFriendRequest(targetUUID);
					sender.getPlayer().sendMessage(ChatColor.RED + "Denied " + targetUsername + ChatColor.RED + "'s friend request");
				} else
				{
					sender.getPlayer().sendMessage(ColorOptions.falsecommand + "You don't have a request from " + targetUsername);
				}
			} else
			{
				sender.getPlayer().sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.getPlayer().sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
}
