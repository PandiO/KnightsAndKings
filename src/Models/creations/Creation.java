package Models.creations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import API_methods.WorldEdit;
import DataManager.Creations;
import Handlers.ColorOptions;
import Main.Main;
import Users.User;

public class Creation 
{
	protected WorldEdit worldedit = new WorldEdit();
	Main main = Main.getPlugin(Main.class);

	protected int creationID;
	protected String creationType;
	protected int stage;
	protected int firstStage;
	protected int lastStage;
	protected Creation instance;
	protected User user;
	protected boolean confirmStop = false;
	protected BukkitTask confirmTask;
	protected boolean stashed = false;
	protected BukkitTask stashTask;
	
	protected List<String> startMessages = new ArrayList<String>(Arrays.asList(
			"",
			"",
			ColorOptions.message + "Welcome to the $ Creation menu",
			ColorOptions.message + "You can create a $ by following a few steps",
			ColorOptions.message + "You can always configure the details later on",
			ColorOptions.message + "To stop the creation, type " + ColorOptions.error + "stop",
			ColorOptions.message + "To undo a step type " + ColorOptions.messageachievement + "undo",
			"",
			ColorOptions.messagesubjects + "Are you ready to start? Type " + ColorOptions.messageachievement + "start/next",
			"",
			""
			));
	protected HashMap<Integer, List<String>> stageMessages;
	
	public Creation(User user) {
		creationID = (int) System.currentTimeMillis();
		this.instance = this;
		this.user = user;
		Creations.Creations.add(this);
	}

	public int getCreationID() {
		return this.creationID;
	}

	public void setCreationID(int creationID) {
		this.creationID = creationID;
	}
	
	public String getCreationType()	{
		return this.creationType;
	}

	public int getStage() {
		return this.stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}
	
	public int getLastStage() {
		return this.lastStage;
	}
	
	public void setLastStage(int lastStage) {
		this.lastStage = lastStage;
	}

	public Creation getInstance() {
		return this.instance;
	}

	public void setInstance(Creation instance) {
		this.instance = instance;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<String> getStartMessage()
	{
		return this.startMessages;
	}
	
	public void setStartMessage(String creationType)
	{
		List<String> startMessage = new ArrayList<String>();
		for (String message : this.startMessages)
		{
			startMessage.add(message.replace("$", creationType));
		}
		
		this.startMessages = startMessage;
	}
	
	public HashMap<Integer, List<String>> getStageMessages()
	{
		return this.stageMessages;
	}
	
	protected void setStageMessages(HashMap<Integer, List<String>> stageMessages)
	{
		this.stageMessages = stageMessages;
		
		if (this.stageMessages.isEmpty())
		{
			Main.logError("Stagemessages empty: " + stageMessages.size());
		}
		this.lastStage = this.calculateLastStage(this.stageMessages);
		this.firstStage = this.calculateFirstStage();
	}
	
	protected void addStageMessage(List<String> message)
	{
		this.stageMessages.put(this.calculateLastStage(this.stageMessages), message);
		
		this.lastStage = this.calculateLastStage(this.stageMessages);
	}
	
	protected Integer calculateFirstStage()
	{
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(stageMessages.keySet());
		
		Collections.sort(list);
		
		return list.get(0);
	}
	
	protected Integer calculateLastStage(HashMap<Integer, List<String>> messages)
	{
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(messages.keySet());
		
		Collections.sort(list, Collections.reverseOrder());
		
		return list.get(0);
	}
	
	protected List<String> getConfirmMessage()
	{
		List<String> message = new ArrayList<String>(Arrays.asList(
				"",
				ColorOptions.messagesubjects + "You have completed all steps in the Creation",
				ColorOptions.message + "Confirm the information below",
				ColorOptions.message + "Type " + ColorOptions.messageachievement + "confirm" + ColorOptions.message + " to confirm or " + ColorOptions.error + "undo" + ColorOptions.message + " to undo",
				""
				));
		
		message.addAll(this.getSpecificConfirmMessage());
		
		return message;
	}
	
	protected List<String> getSpecificConfirmMessage()
	{
		return new ArrayList<String>();
	}
	
	public void start()
	{
		this.nextStage(this.firstStage);
	}
	
	public void nextStage(int stage)
	{
		if (stage == this.firstStage)
		{
			this.sendMessage(this.stageMessages.get(this.firstStage));
		} else if (stage == lastStage+1)
		{
			this.sendMessage(this.getConfirmMessage());
		} else
		{
			new BukkitRunnable()
			{
				public void run()
				{
					sendMessage(stageMessages.get(stage));
				}
			}.runTaskLaterAsynchronously(main, 20);
		}
		this.stage = stage;
		Main.logMessage("Current stage: " + stage);
	}
	
	public void stop()
	{
		
		if (!this.confirmStop)
		{
			this.sendMessage(Arrays.asList(
					"",
					ColorOptions.error + "Are you sure you want to stop the Creation?",
					ColorOptions.error + "Type stop within 3 seconds to confirm",
					""
					));
			this.confirmTask = new BukkitRunnable()
			{
				public void run()
				{
					confirmStop = false;
				}
			}.runTaskLaterAsynchronously(this.main, 3*20);
			
			confirmStop = true;
		} else
		{
			Creations.Creations.remove(this);
			user.getPlayer().sendMessage(ColorOptions.error + "Stopped Creation mode");
			if (this.stashed)
			{
				user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "The Creation session has been stashed for " + ColorOptions.messagesubjects + "5 minutes" + ColorOptions.message + " in case the save process fails");
			}
		}
	}
	
	protected void stash()
	{
		this.stashed = true;
		Creations.StashedCreations.add(this);
		user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "The Creation session has been stashed for later use. Type '/creation " + this.getCreationID() + "' to enter this session");
		user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "The Creation session will be stashed for 5 minutes.");
		
		this.stashTask = new BukkitRunnable()
		{
			public void run()
			{
				Creations.StashedCreations.remove(instance);
				user.getPlayer().sendMessage(ColorOptions.message + ColorOptions.messageArrow + "The stashed Creation session is no longer stashed");
			}
		}.runTaskLaterAsynchronously(main, 300*20);
	}
	
	public void sendMessage(List<String> message)
	{
		for (String msg : message)
		{
			this.user.getPlayer().sendMessage(msg);
		}
	}
	
	public void falseCommand(List<String> error)
	{
		List<String> errormsg = new ArrayList<String>(Arrays.asList(
				"",
				ColorOptions.error + "You are in Creation mode",
				ColorOptions.message + "Type 'stop' to stop",
				""
				));
		if (error != null)
		{
			errormsg.addAll(error);
		}
		
		this.sendMessage(errormsg);
	}
	
	public void complete()
	{
		this.user.getPlayer().sendMessage(ColorOptions.messageachievement + "Succesfully completed the Creation steps!");
		this.confirmStop = true;
		this.stop();
		this.create();
		this.stash();
	}
	
	protected void create()
	{
		
	}
	
	public void processEvent(Event event)
	{
		Main.logError("Error while processing event for creation instance of type " + this.creationType + ", ID " + this.creationID + ". This creation probably has no sub-method for processing the events.");
		user.sendMessage(ColorOptions.error + "Something went wrong processing your action. Please notify a developer");
	}
}
