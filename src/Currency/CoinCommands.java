package Currency;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Handlers.ColorOptions;
import Handlers.PlayerPayEventHandler;
import Main.Main;
import Users.User;
import Users.Users;

public class CoinCommands implements CommandExecutor
{
	private Main main;
	public CoinCommands(Main main) 
	{
		this.main = main;
	}
	
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		if (label.equalsIgnoreCase("pay"))
	    {
	      if ((args.length != 3) || (args[1].contains("-")))
	      {
	        sender.sendMessage(ColorOptions.falsecommand + "Use /pay <coins/gems> <name> <amount>");
	        return true;
	      }
	      Player player = (Player) sender;
	      User user = Users.getUser(player.getUniqueId());
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
	    	  if (args[0].equalsIgnoreCase("coins"))
		      {
		    	  if (user.getCoins() < Integer.valueOf(args[2]).intValue())
			      {
			        sender.sendMessage(ColorOptions.falsecommand + "Your balance is too low: " + ColorOptions.formatCurrency(user.getCoins()));
			        return true;
			      }
			      if (args.length == 3) 
			      {
			    	  Integer coins = Integer.valueOf(args[2]);
			    	  sender.sendMessage(ColorOptions.currencycolor + "Succesfully sent "+ ColorOptions.names + target.getUsername() + " " + ColorOptions.coinStats + ColorOptions.formatCurrency(coins) + ColorOptions.currencycolor + " Coins.");
			          user.sendCoins(target.getUUID(), Integer.valueOf(args[2]).intValue());
			    	  if (Bukkit.getPlayer(target.getUsername()) != null)
			    	  {
				            target.getPlayer().sendMessage(ColorOptions.currencycolor + "You recived " + ColorOptions.coinStats + ColorOptions.formatCurrency(coins) + ColorOptions.currencycolor + " Coins from " + ColorOptions.names + sender.getName() + ".");
			    	  } else
			    	  {
				            sender.sendMessage(ColorOptions.falsecommand + "This player does not exist!");
			    	  }
			          Bukkit.getServer().getPluginManager().callEvent(new PlayerPayEventHandler(user, Integer.valueOf(args[2]).intValue(), target.getUUID()));
			      } else
			      {
			    	  sender.sendMessage(ColorOptions.falsecommand + "Use /pay <coins/gems> <name> <amount>");
			      }
		      } else if (args[0].equalsIgnoreCase("gems"))
		      {
		    	  if (user.getGems() < Integer.valueOf(args[2]).intValue())
			      {
			        sender.sendMessage(ColorOptions.falsecommand + "Your balance is too low: " + ColorOptions.formatCurrency(user.getGems()));
			        return true;
			      }
			      if (args.length == 3) 
			      {
			          if (main.isInt(args[2]))
			          {
			            sender.sendMessage(ColorOptions.currencycolor + "Succesfully sent "+ ColorOptions.names + target.getUsername() + " " + ColorOptions.gemStats + ColorOptions.formatCurrency(Integer.valueOf(args[2])) + ColorOptions.currencycolor + " Gems.");
			            try
			            {
				            target.getPlayer().sendMessage(ColorOptions.currencycolor + "You recived " + ColorOptions.gemStats + ColorOptions.formatCurrency(Integer.valueOf(args[2])) + ColorOptions.currencycolor + " Gems from " + ColorOptions.names + sender.getName() + ".");
			            } catch(Exception e)
			            {
			            	
			            }
			            user.sendGems(target.getUUID(), Integer.valueOf(args[2]).intValue());
			          }
			          else
			          {
			            sender.sendMessage(ColorOptions.falsecommand + args[2] + " is not a number?");
			          }
			      } else
			      {
			    	  sender.sendMessage(ColorOptions.falsecommand + "Use /pay <coins/gems> <name> <amount>");
			      }
		      } else
		      {
		    	  sender.sendMessage(ColorOptions.falsecommand + "Usage: /pay <coins/gems> <name> <amount>");
		      }
	      }
	    }
		if (label.equalsIgnoreCase("coins"))
		{
			if (sender.hasPermission("k&k.coins"))
			{
				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("set"))
					{
						if (args.length == 3)
						{
							if (main.isInt(args[1]))
							{
								setCoins(sender, args[2], Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else if (args.length == 2 && sender instanceof Player)
						{
							Player p = (Player) sender;
							if (main.isInt(args[1]))
							{
								setCoins(sender, p.getName(), Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /coins set <amount> <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("add"))
					{
						if (args.length == 3)
						{
							if (main.isInt(args[1]))
							{
								addCoins(sender, args[2], Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else if (args.length == 2 && sender instanceof Player)
						{
							Player p = (Player) sender;
							if (main.isInt(args[1]))
							{
								addCoins(sender, p.getName(), Integer.valueOf(args[1]));
							} else
							{
								sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /coins add <amount> <username>");
						}
					} else
					if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del"))
					{
						if (args.length == 3)
						{
							if (args[1].equalsIgnoreCase("all"))
							{
								removeCoins(sender, args[2], 0, true);
							} else
							{
								if (main.isInt(args[1]))
								{
									removeCoins(sender, args[2], Integer.valueOf(args[1]), false);
								} else
								{
									sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
								}
							}
						} else if (args.length == 2 && sender instanceof Player)
						{
							Player p = (Player) sender;
							if (args[1].equalsIgnoreCase("all"))
							{
								removeCoins(sender, p.getName(), 0, true);
							} else
							{
								if (main.isInt(args[1]))
								{
									removeCoins(sender, p.getName(), Integer.valueOf(args[1]), false);
								} else
								{
									sender.sendMessage(ColorOptions.error + "Error: " + args[1] + " is not a number!");
								}
							}
						} else
						{
							sender.sendMessage(ColorOptions.error + "Usage: /coins remove <amount/all> <username>");
						}
					}
				} else
				{
					sender.sendMessage(ColorOptions.error + "Usage: /coins <set/add/remove> <amount> <username>");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "You don't have permission for this command!");
			}
		}
		if (label.equalsIgnoreCase("balance") || label.equalsIgnoreCase("bal"))
		{
			if (args.length == 1)
			{
				if (Users.existUser(args[0]) == true)
				{
					User target = new User(Users.fetchUUIDbyUsername(args[0]));
					sender.sendMessage(ColorOptions.names + args[0] + ColorOptions.currencycolor + " has " + ColorOptions.coinStats + ColorOptions.formatCurrency(target.getCoins()) + " Coins " + ColorOptions.currencycolor + " and " + ColorOptions.gemStats + ColorOptions.formatCurrency(target.getGems()) + " Gems");
				} else
				{
					sender.sendMessage(ColorOptions.error + "Error: " + args[0] + " cannot be found!");
				}
			} else
			{
				if (sender instanceof Player)
				{
					User user = Users.getUser(((Player) sender).getUniqueId()); 
					sender.sendMessage(ColorOptions.currencycolor + "You have " + ColorOptions.coinStats + ColorOptions.formatCurrency(user.getCoins()) + " Coins " + ColorOptions.currencycolor + "and " + ColorOptions.gemStats + ColorOptions.formatCurrency(user.getGems()) + " Gems");
				}
			}
		}
		return false;
	}
	
	private void setCoins(CommandSender sender, String targetUsername, Integer amount)
	{
		try
		{
			if (Users.existUser(targetUsername) == true)
			{
				User target = null;
				UUID uuid = Users.fetchUUIDbyUsername(targetUsername);
				if (Users.getUser(uuid) != null)
				{
					target = Users.getUser(uuid);
				} else
				{
					target = new User(uuid);
				}
				target.setCoins(amount);
				target.destroy();
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Set " + ColorOptions.coinStats + amount + " coins " + ColorOptions.currencycolor + "as your new balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Set " + ColorOptions.coinStats + amount + " coins " + ColorOptions.currencycolor + "as " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "new balance.");
				}
			} else
			{
				sender.sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
	
	private void addCoins(CommandSender sender, String targetUsername, Integer amount)
	{
		try
		{
			if (Users.existUser(targetUsername) == true)
			{
				User target = null;
				UUID uuid = Users.fetchUUIDbyUsername(targetUsername);
				if (Users.getUser(uuid) != null)
				{
					target = Users.getUser(uuid);
				} else
				{
					target = new User(uuid);
				}
				target.addCoins(amount);
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Added " + ColorOptions.coinStats + amount + " coins " + ColorOptions.currencycolor + "to your balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Added " + ColorOptions.coinStats + amount + " coins " + ColorOptions.currencycolor + "to " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "balance.");
				}
				target.destroy();
			} else
			{
				sender.sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
	
	private void removeCoins(CommandSender sender, String targetUsername, Integer amount, boolean all)
	{
		try
		{
			if (Users.existUser(targetUsername) == true)
			{
				User target = null;
				UUID uuid = Users.fetchUUIDbyUsername(targetUsername);
				if (Users.getUser(uuid) != null)
				{
					target = Users.getUser(uuid);
				} else
				{
					target = new User(uuid);
				}
				if (all == true)
				{
					target.setCoins(0);
					if (sender.getName().equalsIgnoreCase(targetUsername))
					{
						sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + "all coins " + ColorOptions.currencycolor + "from your balance.");
					} else
					{
						sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + "all coins " + ColorOptions.currencycolor + "from " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "balance.");
					}
					return;
				} else
				{
					target.removeCoins(amount);
				}
				if (sender.getName().equalsIgnoreCase(targetUsername))
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + amount + " coins " + ColorOptions.currencycolor + "from your balance.");
				} else
				{
					sender.sendMessage(ColorOptions.currencycolor + "Removed " + ColorOptions.coinStats + amount + " coins " + ColorOptions.currencycolor + "from " + ColorOptions.messagesubjects + targetUsername + "'s " + ColorOptions.currencycolor + "balance.");
				}
				target.destroy();
			} else
			{
				sender.sendMessage(ColorOptions.error + "Error: Player " + targetUsername + " cannot be found!");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sender.sendMessage(ColorOptions.error + "Something went wrong, please notify a staff-member");
		}
	}
}

