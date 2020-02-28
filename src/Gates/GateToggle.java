package Gates;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import Handlers.ColorOptions;
import Main.Main;
import Users.User;

public class GateToggle 
{
	Main main = Main.getPlugin(Main.class);
	
	private User user;
	private Gate gate;
	private boolean passthrough;
	private String toggleType = "Toggle";
	private BukkitTask requestTimeOut;
	private BukkitTask passthroughTask;
	private long timeOut;
	
	public GateToggle(User user, boolean passthrough)
	{
		this.user = user;
		this.passthrough = passthrough;
		
		if (passthrough)
		{
			toggleType = "Passthrough";
		}
		this.startRequestTimeOutTask();
		Gates.toggles.add(this);
	}
	
	public User getUser()
	{
		return this.user;
	}
	
	public Gate getGate()
	{
		return this.gate;
	}
	
	public boolean isPassthrough()
	{
		return this.passthrough;
	}
	
	public BukkitTask getRequestTimeOut()
	{
		return this.requestTimeOut;
	}
	
	public BukkitTask getPassthroughTask()
	{
		return this.passthroughTask;
	}
	
	public long getTimeOut()
	{
		return this.timeOut;
	}
	
	public void setTimeOut(long timeOut)
	{
		this.timeOut = timeOut;
	}
	
	public void setGate(Gate gate)
	{
		this.gate = gate;
	}
	
	public void startRequestTimeOutTask()
	{
		this.timeOut = System.currentTimeMillis() + 4000L;
		this.requestTimeOut = new BukkitRunnable()
		{
			public void run()
			{
				user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Gate " + toggleType + " timed out..");
				remove();
			}
		}.runTaskLaterAsynchronously(main, 3*20);
	}
	
	public void startPassthroughTask()
	{
		this.passthroughTask = new BukkitRunnable()
		{
			public void run()
			{
				gate.setClosed(true);
				user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "Gate closed..");
				remove();
			}
		}.runTaskLaterAsynchronously(main, 2*20);
	}
	
	public void remove()
	{
		try
		{
			this.requestTimeOut.cancel();
		} catch (Exception ex)
		{
			
		}
		try
		{
			this.passthroughTask.cancel();
		} catch (Exception ex)
		{
			
		}
		Gates.toggles.remove(this);
		Gates.destroyGateToggle(this);
	}
}
