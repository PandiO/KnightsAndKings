package Skills;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Main.Main;


public class Skill 
{
	//Get instances of required classes
	Main main = Main.getPlugin(Main.class);
	
	//Get the correct name of a skill
	public String getSkillName(String name)
	{
		if (name.equalsIgnoreCase("strength"))
		{
			name = "Strength";
		} else if (name.equalsIgnoreCase("speed"))
		{
			name = "Speed";
		} else if (name.equalsIgnoreCase("health"))
		{
			name = "Health";
		} else if (name.equalsIgnoreCase("defense"))
		{
			name = "Defense";
		} else if (name.equalsIgnoreCase("attack speed") || name.equalsIgnoreCase("attackspeed"))
		{
			name = "AttackSpeed";
		} else
		{
			name = null;
		}
		
		return name;
	}
	
	//Get a list of id's a specific skill
	public ArrayList<Integer> getSkillIDList(String name)
	{
		ArrayList<Integer> IDlist = new ArrayList<Integer>();
		String skillName = null;
		skillName = getSkillName(name);
		
		try
		{
			//prepare the query to retrieve uuid of a user
			PreparedStatement stmt = main.getConnection().prepareStatement("Select * From ?;");	
			stmt.setString(1, skillName);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			while (results.next())
			{
				IDlist.add(results.getInt(skillName + "ID"));
			}
			return IDlist;
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return IDlist;
	}
	
	//Get a list of all skills
	public List<String> getSkillList()
	{
		List<String> list = new ArrayList<String>();
		
		list.add("Strength");
		list.add("Speed");
		list.add("Health");
		list.add("AttackSpeed");
		list.add("Defense");
		
		return list;
	}
	
	//Get the value of a skill by id and skill-name
	public Integer getSkillValue(String skill, Integer level)
	{
		Integer value = null;
		String skillName = getSkillName(skill);
		try
		{
			PreparedStatement stmt = null;
			//prepare the query to retrieve uuid of a user
			if (skillName.equalsIgnoreCase("attackspeed"))
			{
				stmt = main.getConnection().prepareStatement("Select * From AttackSpeed WHERE ID=?;");	
			} else
			if (skillName.equalsIgnoreCase("health"))
			{
				stmt = main.getConnection().prepareStatement("Select * From Health WHERE ID=?;");	
			} else
			if (skillName.equalsIgnoreCase("speed"))
			{
				stmt = main.getConnection().prepareStatement("Select * From Speed WHERE ID=?;");	
			} else
			if (skillName.equalsIgnoreCase("strength"))
			{
				stmt = main.getConnection().prepareStatement("Select * From Strength WHERE ID=?;");	
			} else
			if (skillName.equalsIgnoreCase("defense"))
			{
				stmt = main.getConnection().prepareStatement("Select * From Defense WHERE ID=?;");	
			}
			stmt.setInt(1, level);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				value = results.getInt("Val");
				return value;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return value;
	}
	
	//Get the slot of a skill by id and skill-name
	public Integer getSkillSlot(String skill, Integer level)
	{
		Integer value = null;
		String skillName = getSkillName(skill);
				
		try
		{
			//prepare the query to retrieve uuid of a user
			String stringquery = "Select * From $skill WHERE ID=?;";
			String query = stringquery.replace("$skill", skillName);
			PreparedStatement stmt = main.getConnection().prepareStatement(query);	
			stmt.setInt(1, level);
			//Execute the query
			ResultSet results = stmt.executeQuery();
			//Check if information has been found that matches the uuid of the user
			if (results.next())
			{
				value = results.getInt("Slot");
				return value;
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return value;
	}
	
//	public void setSkill(int strengthlevel, int speedlevel, int healthlevel, int attackspeedlevel, int defenselevel, UUID uuid)
//    {
//		Menu menu = new Menu();
//      Integer sslot = this.getSkillSlot("strength", strengthlevel);
//      Integer spslot = this.getSkillSlot("speed", speedlevel);
//      Integer hslot = this.getSkillSlot("health", healthlevel);
//      Integer asslot = this.getSkillSlot("attackspeed", attackspeedlevel);
//      Integer dslot = this.getSkillSlot("defense", defenselevel);
//      Inventory SkillsMenu = SkillMenuClick.skillmenus.get(uuid);
//      SkillsMenu.setItem(26, SkillMenuClick.SkillPoints(false, user.getSkillPoints(uuid), user.getSpecialSkillPoints(uuid)));
//  	  SkillsMenu.setItem(17, SkillMenuClick.SkillPoints(true, user.getSkillPoints(uuid), user.getSpecialSkillPoints(uuid)));
//      if (user.getSpecialSkillID(uuid) != 0)
//      {
//    	  SkillsMenu.setItem(35, menu.setSpecialSkill("secondspecialskill"));
//    	  SkillsMenu.setItem(44, menu.setSpecialSkill(user.getSpecialSkillName(uuid)));
//      } else
//      {
//    	  SkillsMenu.setItem(44, menu.setSpecialSkill("none"));
//      }
//      if (strengthlevel == 7)
//      {
//    	  SkillsMenu.setItem(sslot-0, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Strength III for 4 seconds", ChatColor.GRAY + "when hitting someone", ChatColor.GREEN + "Achieved!"));
//          SkillsMenu.setItem(sslot-1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-1), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-2), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-3, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-3), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-4, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-4), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-5, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-5), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-6, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-6), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//
//      } else if (strengthlevel == 6)
//      {
//    	  SkillsMenu.setItem(sslot+1, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Strength III for 4 seconds", ChatColor.GRAY + "when hitting someone"));
//          SkillsMenu.setItem(sslot, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-1), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-2), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-3, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-3), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-4, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-4), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-5, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-5), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//
//      } else if (strengthlevel == 5)
//      {
//    	  SkillsMenu.setItem(sslot+2, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Strength III for 4 seconds", ChatColor.GRAY + "when hitting someone"));
//    	  SkillsMenu.setItem(sslot+1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//          SkillsMenu.setItem(sslot+0, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-1), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-2), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-3, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-3), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-4, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-4), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//
//      } else if (strengthlevel == 4)
//      {
//    	  SkillsMenu.setItem(sslot+3, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Strength III for 4 seconds", ChatColor.GRAY + "when hitting someone"));
//    	  SkillsMenu.setItem(sslot+2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+2), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//          SkillsMenu.setItem(sslot, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-1), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-2), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-3, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-3), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//
//      } else if (strengthlevel == 3)
//      {
//    	  SkillsMenu.setItem(sslot+4, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Strength III for 4 seconds", ChatColor.GRAY + "when hitting someone"));
//    	  SkillsMenu.setItem(sslot+3, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+3), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+2), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//          SkillsMenu.setItem(sslot, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + strengthlevel, true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-1), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-2), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//
//      } else if (strengthlevel == 2)
//      {
//    	  SkillsMenu.setItem(sslot+5, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Strength III for 4 seconds", ChatColor.GRAY + "when hitting someone"));
//    	  SkillsMenu.setItem(sslot+4, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+4), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+3, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+3), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+2), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//          SkillsMenu.setItem(sslot, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + strengthlevel, true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(sslot-1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel-1), true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//
//      } else if (strengthlevel == 1)
//      {
//    	  SkillsMenu.setItem(sslot+6, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Strength III for 4 seconds", ChatColor.GRAY + "when hitting someone"));
//    	  SkillsMenu.setItem(sslot+5, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+4, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+3, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//          SkillsMenu.setItem(sslot, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + strengthlevel, true, SkillMenuItemData.StrengthLore1, SkillMenuItemData.achieved));
//
//      } else if (strengthlevel == 0)
//      {
//    	  SkillsMenu.setItem(sslot+6, SkillMenuClick.setlastupgrade(ChatColor.RED + "Strength upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Strength III for 4 seconds", ChatColor.GRAY + "when hitting someone"));
//    	  SkillsMenu.setItem(sslot+5, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+6), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+4, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+5), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+3, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+4), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+2, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+3), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot+1, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+2), false, SkillMenuItemData.StrengthLore1));
//    	  SkillsMenu.setItem(sslot, SkillMenuClick.createClayItem(ChatColor.RED + "Strength upgrade " + (strengthlevel+1), false, SkillMenuItemData.StrengthLore1));
//      }
//      if (speedlevel == 7)
//      {
//    	  SkillsMenu.setItem(spslot-0, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Speed II for 5 seconds", ChatColor.GRAY + "on an incoming hit", ChatColor.GREEN + "Achieved!"));
//          SkillsMenu.setItem((spslot-1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-1), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-2), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-3), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-3), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-4), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-4), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-5), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-5), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-6), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-6), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//      } else if (speedlevel == 6)
//      {
//    	  SkillsMenu.setItem(spslot+1, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Speed II for 5 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem((spslot), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-1), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-2), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-3), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-3), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-4), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-4), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-5), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-5), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (speedlevel == 5)
//      {
//    	  SkillsMenu.setItem(spslot+2, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Speed II for 5 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem((spslot+1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+1), false, SkillMenuItemData.SpeedLore1));
//    	  SkillsMenu.setItem((spslot-0), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-1), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-2), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-3), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-3), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-4), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-4), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (speedlevel == 4)
//      {
//    	  SkillsMenu.setItem(spslot+3, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Speed II for 5 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem((spslot+2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+2), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+1), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-1), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-2), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-3), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-3), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (speedlevel == 3)
//      {
//    	  SkillsMenu.setItem(spslot+4, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Speed II for 5 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem((spslot+3), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+3), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+2), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+1), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-1), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-2), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (speedlevel == 2)
//      {
//    	  SkillsMenu.setItem(spslot+5, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Speed II for 5 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem((spslot+4), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+4), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+3), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+3), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+2), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+1), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem((spslot-1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel-1), true, SkillMenuItemData.SpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (speedlevel == 1)
//      {
//    	  SkillsMenu.setItem(spslot+6, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Speed II for 5 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem((spslot+5), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+5), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+4), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+4), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+3), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+3), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+2), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+1), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel), true, SkillMenuItemData.achieved));
//
//      } else if (speedlevel == 0)
//      {
//    	  SkillsMenu.setItem(spslot+6, SkillMenuClick.setlastupgrade(ChatColor.AQUA + "Speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Speed II for 5 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem((spslot+5), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+6), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+4), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+5), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+3), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+4), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+2), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+3), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+1), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+2), false, SkillMenuItemData.SpeedLore1));
//          SkillsMenu.setItem((spslot+0), SkillMenuClick.createClayItem(ChatColor.AQUA + "Speed upgrade " + (speedlevel+1), false, SkillMenuItemData.SpeedLore1));
//      }
//      if (healthlevel == 7)
//      {
//    	  SkillsMenu.setItem(hslot, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Regeneration I for 10 seconds", ChatColor.GRAY + "on an incoming hit", ChatColor.GREEN + "Achieved!"));
//          SkillsMenu.setItem(hslot-1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-1), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-2), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-3, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-3), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-4, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-4), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-5, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-5), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-6, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-6), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//
//      } else if (healthlevel == 6)
//      {
//    	  SkillsMenu.setItem(hslot, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Regeneration I for 10 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem(hslot, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + healthlevel, true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-1), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-2), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-3, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-3), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-4, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-4), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-5, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-5), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//
//      } else if (healthlevel == 5)
//      {
//    	  SkillsMenu.setItem(hslot+2, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Regeneration I for 10 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem(hslot+1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+1), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + healthlevel, true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-1), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-2), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-3, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-3), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-4, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-4), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//
//      } else if (healthlevel == 4)
//      {
//    	  SkillsMenu.setItem(hslot+3, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Regeneration I for 10 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem(hslot+2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+2), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+1), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + healthlevel, true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-1), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-2), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-3, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-3), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//
//      } else if (healthlevel == 3)
//      {
//    	  SkillsMenu.setItem(hslot+4, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Regeneration I for 10 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem(hslot+3, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+3), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+2), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+1), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + healthlevel, true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-1), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-2), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//
//      } else if (healthlevel == 2)
//      {
//    	  SkillsMenu.setItem(hslot+5, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Regeneration I for 10 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem(hslot+4, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+4), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+3, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+3), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+2), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+1), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + healthlevel, true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(hslot-1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel-1), true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//
//      } else if (healthlevel == 1)
//      {
//    	  SkillsMenu.setItem(hslot+6, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Regeneration I for 10 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem(hslot+5, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+5), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+4, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+4), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+3, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+3), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+2), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+1), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + healthlevel, true, SkillMenuItemData.HealthLore1, SkillMenuItemData.achieved));
//
//      } else if (healthlevel == 0)
//      {
//    	  SkillsMenu.setItem(hslot+6, SkillMenuClick.setlastupgrade(ChatColor.LIGHT_PURPLE + "Health upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to get", ChatColor.GRAY + "Regeneration I for 10 seconds", ChatColor.GRAY + "on an incoming hit"));
//          SkillsMenu.setItem(hslot+5, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+6), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+4, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+5), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+3, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+4), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+2, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+3), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+1, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+2), false, SkillMenuItemData.HealthLore1));
//          SkillsMenu.setItem(hslot+0, SkillMenuClick.createClayItem(ChatColor.LIGHT_PURPLE + "Health upgrade " + (healthlevel+1), false, SkillMenuItemData.HealthLore1));
//      }
//      if (attackspeedlevel == 7)
//      {
//    	  SkillsMenu.setItem(asslot-0, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "Attack speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to inflict", ChatColor.GRAY + "2x damage at once", ChatColor.GREEN + "Achieved!"));
//          SkillsMenu.setItem(asslot-1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-1), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-2), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-3, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-3), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-4, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-4), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-5, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-5), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-6, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-6), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (attackspeedlevel == 6)
//      {
//    	  SkillsMenu.setItem(asslot+1, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "Attack speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to inflict", ChatColor.GRAY + "2x damage at once"));
//          SkillsMenu.setItem(asslot-0, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + attackspeedlevel, true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-1), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-2), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-3, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-3), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-4, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-4), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-5, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-5), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (attackspeedlevel == 5)
//      {
//    	  SkillsMenu.setItem(asslot+2, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "Attack speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to inflict", ChatColor.GRAY + "2x damage at once"));
//          SkillsMenu.setItem(asslot+1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+1), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + attackspeedlevel, true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-1), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-2), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-3, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-3), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-4, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-4), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (attackspeedlevel == 4)
//      {
//    	  SkillsMenu.setItem(asslot+3, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "Attack speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to inflict", ChatColor.GRAY + "2x damage at once"));
//          SkillsMenu.setItem(asslot+2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+2), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+1), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + attackspeedlevel, true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-1), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-2), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-3, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-3), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (attackspeedlevel == 3)
//      {
//    	  SkillsMenu.setItem(asslot+4, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "Attack speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to inflict", ChatColor.GRAY + "2x damage at once"));
//          SkillsMenu.setItem(asslot+3, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+3), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+2), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+1), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-1), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-2), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (attackspeedlevel == 2)
//      {
//    	  SkillsMenu.setItem(asslot+5, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "Attack speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to inflict", ChatColor.GRAY + "2x damage at once"));
//          SkillsMenu.setItem(asslot+4, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+4), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+3, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+3), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+2), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+1), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(asslot-1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel-1), true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (attackspeedlevel == 1)
//      {
//    	  SkillsMenu.setItem(asslot+6, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "Attack speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to inflict", ChatColor.GRAY + "2x damage at once"));
//          SkillsMenu.setItem(asslot+5, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+5), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+4, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+4), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+3, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+3), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+2), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+1), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + attackspeedlevel, true, SkillMenuItemData.AttackSpeedLore1, SkillMenuItemData.achieved));
//
//      } else if (attackspeedlevel == 0)
//      {
//    	  SkillsMenu.setItem(asslot+6, SkillMenuClick.setlastupgrade(ChatColor.WHITE + "Attack speed upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to inflict", ChatColor.GRAY + "2x damage at once"));
//          SkillsMenu.setItem(asslot+5, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+6), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+4, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+5), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+3, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+4), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+2, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+3), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+1, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+2), false, SkillMenuItemData.AttackSpeedLore1));
//          SkillsMenu.setItem(asslot+0, SkillMenuClick.createClayItem(ChatColor.WHITE + "Attack speed upgrade " + (attackspeedlevel+1), false, SkillMenuItemData.AttackSpeedLore1));
//      }
//      if (defenselevel == 7)
//      {
//    	  SkillsMenu.setItem(dslot-0, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to remove", ChatColor.GRAY + "100% incoming damage", ChatColor.GREEN + "Achieved!"));
//          SkillsMenu.setItem(dslot-1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-1), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-2), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-3, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-3), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-4, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-4), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-5, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-5), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-6, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-6), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//      } else if (defenselevel == 6)
//      {
//    	  SkillsMenu.setItem(dslot+1, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to remove", ChatColor.GRAY + "100% incoming damage"));
//          SkillsMenu.setItem(dslot-0, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + defenselevel, true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-1), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-2), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-3, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-3), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-4, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-4), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-5, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-5), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//
//      } else if (defenselevel == 5)
//      {
//    	  SkillsMenu.setItem(dslot+2, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to remove", ChatColor.GRAY + "100% incoming damage"));
//          SkillsMenu.setItem(dslot+1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + defenselevel+1, false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + defenselevel, true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-1), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-2), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-3, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-3), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-4, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-4), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//
//      } else if (defenselevel == 4)
//      {
//    	  SkillsMenu.setItem(dslot+3, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to remove", ChatColor.GRAY + "100% incoming damage"));
//          SkillsMenu.setItem(dslot+2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+2), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+1), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + defenselevel, true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-1), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-2), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-3, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-3), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//
//      } else if (defenselevel == 3)
//      {
//    	  SkillsMenu.setItem(dslot+4, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to remove", ChatColor.GRAY + "100% incoming damage"));
//          SkillsMenu.setItem(dslot+3, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+3), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+2), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+1), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + defenselevel, true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-1), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-2), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//
//      } else if (defenselevel == 2)
//      {
//    	  SkillsMenu.setItem(dslot+5, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to remove", ChatColor.GRAY + "100% incoming damage"));
//          SkillsMenu.setItem(dslot+4, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+4), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+3, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+3), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+2), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+1), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + defenselevel, true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//          SkillsMenu.setItem(dslot-1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel-1), true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//
//      } else if (defenselevel == 1)
//      {
//    	  SkillsMenu.setItem(dslot+6, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to remove", ChatColor.GRAY + "100% incoming damage"));
//          SkillsMenu.setItem(dslot+5, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+5), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+4, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+4), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+3, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+3), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+2), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+1), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + defenselevel, true, SkillMenuItemData.DefenseLore1, SkillMenuItemData.achieved));
//
//      } else if (defenselevel == 0)
//      {
//    	  SkillsMenu.setItem(dslot+6, SkillMenuClick.setlastupgrade(ChatColor.YELLOW + "Defense upgrade " + ChatColor.DARK_PURPLE + "7", ChatColor.GRAY + "50% chance to remove", ChatColor.GRAY + "100% incoming damage"));
//          SkillsMenu.setItem(dslot+5, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+6), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+4, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+5), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+3, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+4), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+2, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+3), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+1, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+2), false, SkillMenuItemData.DefenseLore1));
//          SkillsMenu.setItem(dslot+0, SkillMenuClick.createClayItem(ChatColor.YELLOW + "Defense upgrade " + (defenselevel+1), false, SkillMenuItemData.DefenseLore1));
//      }
//    }
}
