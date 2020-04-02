/**
 * 
 */
package Models.Structures;

import Main.Main;
import Titles.Title;
import Users.User;

/**
 * @author pandi
 *
 */
public interface Buyable 
{	
	int getPrice();
	
	void setPrice(int price);
	
	int getOwnerID();
	
	void setOwnerID(int ownerID);
	
	User getOwner();
	
	void setOwner(User owner);
	
	public static Integer getTargetedPrice(Integer titleID, Integer factor)
	{
		Title title = new Title();
		Integer price = 0;
		
		Integer salary = title.getSalary(titleID);
		Integer dailySalary = salary*4;
		Integer timePrice = Main.getRandom(dailySalary*30, dailySalary*60);
		for (int i = 0; i < 10; i++)
		{
			timePrice = Main.getRandom(dailySalary*30, dailySalary*60);
			Main.logMessage("price: " + Math.round(timePrice*(1+factor/100)));
		}
		price = Math.round(timePrice*(1+factor/100));
		price = price - price % 1000;
		
		return price;
	}
	
	public static Integer getTargetedIncome(Integer titleID, Integer price, Integer factor)
	{
		Integer income = 0;

		income = Math.round(((price-(price/4))/100));
		income = income - income % 1000;
		
		return income;
	}
}
