package Menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Main.Main;
import Users.User;

public class MenuItemBlink 
{
	Main main = Main.getPlugin(Main.class);
	
	protected Inventory Menu;
	protected String MenuName;
	protected int ItemSlot;
	protected ItemStack OriginalItem;
	protected ItemStack FlashItem;
	protected CommandSender Sender;
	protected User User;
	protected int FlashTimeOut;
	protected BukkitTask FlashTask;
	protected String ErrorMSG = ColorOptions.error + "Something went wrong: ";
	
	public MenuItemBlink(CommandSender Sender, User UserTarget, int ItemSlot, int FlashTimeOut)
	{
		if (Sender != null)
		{
			this.Sender = Sender;
		} else
		{
			this.Sender = Bukkit.getConsoleSender();
		}
		this.User = UserTarget;
		this.ItemSlot = ItemSlot;
		this.FlashTimeOut = FlashTimeOut;
		try
		{
			this.Menu = UserTarget.getOpenMenu();
		} catch (NullPointerException ex)
		{
			ex.printStackTrace();
			Sender.sendMessage(this.ErrorMSG + ex.getMessage());
			return;
		}
		this.MenuName = this.Menu.getName();
		
		if (!this.isFlashableItem())
		{
			Sender.sendMessage(this.ErrorMSG + "The item detected on the given slot is not flashable!");
			Sender.sendMessage(ColorOptions.error + "Player: " + this.User.getUsername());
			Sender.sendMessage(ColorOptions.error + "Menu: " + this.MenuName);
			Sender.sendMessage(ColorOptions.error + "Slot: " + this.ItemSlot);
			return;
		}
		
		this.OriginalItem = this.Menu.getItem(this.ItemSlot);
		
		this.createFlashItem();
		this.startFlashTask();
	}
	
	private void createFlashItem()
	{
		ItemStack flash = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
		ItemMeta meta = flash.getItemMeta();
		meta.setDisplayName(this.OriginalItem.getItemMeta().getDisplayName());
		meta.setLore(this.OriginalItem.getItemMeta().getLore());
		flash.setItemMeta(meta);
		
		this.FlashItem = flash;
	}
	
	public boolean isFlashableItem()
	{
		boolean isFlashable = true;
		
//		if (this.User.getOpenMenu() == null || this.User.getOpenMenu().getName().equalsIgnoreCase(this.MenuName))
//		{
//			isFlashable = false;
//			return isFlashable;
//		}
		
		if (this.Menu.getItem(ItemSlot) == null)
		{
			isFlashable = false;
			return isFlashable;
		}
		
		Material ItemMaterial = this.Menu.getItem(this.ItemSlot).getType();
		
		if (ItemMaterial == Material.AIR || ItemMaterial == Material.STAINED_GLASS_PANE)
		{
			isFlashable = false;
		}
		
		return isFlashable;
	}
	
	private void startFlashTask()
	{
		this.FlashTask = new BukkitRunnable()
		{
			public void run()
			{
//				if (!isFlashableItem())
//				{
//					this.cancel();
//					Sender.sendMessage(ColorOptions.error + "Error occured while executing flashing task");
//					Sender.sendMessage(ColorOptions.error + "Most likely caused by the player closing the menu");
//					try
//					{
//						Menu.setItem(ItemSlot, OriginalItem);
//					} catch (Exception ex)
//					{
//						
//					}
//				}
				if (Menu.getItem(ItemSlot).getType() != Material.STAINED_GLASS_PANE)
				{
					Menu.setItem(ItemSlot, FlashItem);
				} else
				{
					Menu.setItem(ItemSlot, OriginalItem);
				}
			}
		}.runTaskTimerAsynchronously(main, 0, 10);
		
		new BukkitRunnable()
		{
			public void run()
			{
				FlashTask.cancel();
				Menu.setItem(ItemSlot, OriginalItem);
				Sender.sendMessage(ColorOptions.messageachievement + "Succesfully stopped blinking the item of slot " + ColorOptions.messagesubjects + ItemSlot + ColorOptions.messageachievement + " at player " + ColorOptions.messagesubjects + User.getUsername());
				Menu.setItem(ItemSlot, OriginalItem);
			}
		}.runTaskLaterAsynchronously(main, this.FlashTimeOut*20);
		this.Sender.sendMessage(ColorOptions.messageachievement + "Succesfully blinking the item of slot " + ColorOptions.messagesubjects + this.ItemSlot + ColorOptions.messageachievement + " at player " + ColorOptions.messagesubjects + this.User.getUsername());
		
	}
	
}
