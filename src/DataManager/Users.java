/**
 * 
 */
package DataManager;

import Users.User;

/**
 * @author pandi
 *
 */
public interface Users {
	public static User FindUser(int ID)
	{
		User user = null;
		
		for (User u : Main.Main.users)
		{
			if (u.getID() == ID)
			{
				user = u;
				break;
			}
		}
		
		return user;
	}
	
	public static User FindUser(String userName)
	{
		User user = null;
		
		for (User u : Main.Main.users)
		{
			if (u.getUsername().equalsIgnoreCase(userName))
			{
				user = u;
				break;
			}
		}
		
		return user;
	}
}
