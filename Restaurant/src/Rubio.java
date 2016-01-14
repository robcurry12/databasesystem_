import java.text.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.lang.*;

public class Rubio 
{			
	private static final String dbUrl = "jdbc:mysql://localhost:3306/rubio";
	private static final String username = "root";
	private static final String password = "";
	
								/*RUNNING MENU METHODS */
	/**
	 * Prints out the names of the five servers at Rubio's Restaurant
	 */
	public static void printServers()
	{
        String query = "SELECT CONCAT(last_name, ', ', first_name) AS full_name" +
        " FROM servers ORDER BY last_name";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Servers at Rubios :");
            while(rs.next())
            {
                
                String fullname = rs.getString("full_name");
                
                System.out.println(
                    "Name:     " + fullname);
                System.out.println("--------------------");
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * Prints out the names of the tables of the indicated size that are free
	 * @param size - the desired size of table
	 */
	public static void freeTables()
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("What size table are you looking for?");
		System.out.println("We have tables that seat 2, 4, 6, & 10.");
		int sizeTable = scan.nextInt();
		while(((sizeTable < 1) || (sizeTable > 10)) || (sizeTable != 2) && (sizeTable != 4) && (sizeTable != 6) && (sizeTable != 10))
		{	
			System.out.println("Invalid table size. Please enter another size");
			sizeTable = scan.nextInt();
		}
        String query = "SELECT table_id, type, size " +
        				"FROM tables " + 
        				"WHERE occupied = 0 AND size = '"+ sizeTable +"';";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Free tables:");
            while(rs.next())
            {
                
                int tableid = rs.getInt("table_id");
                String type = rs.getString("type");
                int SIZE = rs.getInt("size");

                System.out.println("------------------------------");
                System.out.println(
                    "Table:     " + tableid);
                System.out.println(
                    "Type:     " + type);
                System.out.println(
                     "Size:     " + SIZE);
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * Assigns a party to a table (or bar stool) if there is one available. 
	 * @param partyname The name of the party entered
	 * @param size The size of the part entered
	 * @return The id of the table that the party will be seated at. 
	 */
	public static int assignParty(String partyname, int size)
	{
		int tableid = -1;
		if(size % 2 != 0)
			size++;
		else if((size > 6) && (size <= 10))
			size = 10;
		String query1 = "SELECT table_id " +
				"FROM tables " + 
				"WHERE occupied = 0 AND size = '"+ size +"';";
		 try (Connection connection = DriverManager.getConnection(
	                dbUrl, username, password);
	             Statement statement = connection.createStatement();
	             ResultSet rs = statement.executeQuery(query1))
	        {
			 	if(!rs.next())
			 		return tableid;
			 	else
			 		tableid = rs.getInt("table_id");
	        }
	        catch(SQLException e)
	        {
	            System.out.println(e.getMessage());
	        }
		
		 String query2 = "INSERT INTO parties (name, size, server_id, table_id, time_seated, status) " +
				 		"VALUES ('" + partyname + "', '" + size + "', null, '" + tableid + "', NOW(), 'waitor');";
			 try (Connection connection = DriverManager.getConnection(
		                dbUrl, username, password);)
		        {
				 	Statement statement = connection.createStatement();
	             	int rs = statement.executeUpdate(query2);
				 	System.out.println("Table located!");
		        }
		        catch(SQLException e)
		        {
		            System.out.println(e.getMessage());
		        }
			 
			 String query3 = "UPDATE tables " +
				 		"SET occupied = 1 " +
				 		"WHERE table_id = '"+ tableid + "'";
			 try (Connection connection = DriverManager.getConnection(
		                dbUrl, username, password);)
		        {
				 	Statement statement = connection.createStatement();
				 	int rs = statement.executeUpdate(query3);
				 	System.out.println("You will now be seated!");
		        }
		        catch(SQLException e)
		        {
		            System.out.println(e.getMessage());
		        }
		return tableid;
	}
	
	/**
	 * Gets the id of the server who served the newest party
	 * @return The id of the server who served the newest party
	 */
	public static int getCurrServer()
	{
		int currServer = 0;
		String query = "SELECT server_id " +
						"FROM parties " +
						"WHERE server_id IS NOT NULL " +
						"ORDER BY party_id DESC";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
			if(rs.next())
				currServer = rs.getInt("server_id");
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
		return currServer;
	}
	
	/**
	 * Assigns a server to a party that has just been seated
	 * @param partyname The name of the party. This is used to determine the party_id.
	 * @param serverid The next server due up to seat a party
	 */
	public static int assignServer(int partyid, int serverid)
	{
		String query1 = "SELECT party_id " + 
						"FROM parties " + 
						"WHERE server_id IS NULL AND party_id = '" + partyid + "' " +
						"ORDER BY party_id DESC";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);
		     Statement statement = connection.createStatement();
		     ResultSet rs = statement.executeQuery(query1))
		{
		    if(!rs.next())
		    {
		        return -1;
		    }
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		
		String query2 = "UPDATE parties " + 
						"SET server_id = '" + serverid + "'" +
						"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query2);
			return 1;
		    
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		return 1;
	}

	/**
	 * Prints out the portion of the menu requested
	 */
	public static void printMenu(int a)
	{
		int i = 1;
		if(a == 1)
		{
			String query1 = "SELECT name, price, description " +
							"FROM food " + 
							"WHERE food_id < 6";
			try (Connection connection = DriverManager.getConnection(
	                dbUrl, username, password);
	             Statement statement = connection.createStatement();
	             ResultSet rs = statement.executeQuery(query1))
	        {
	            System.out.println("Appetizers :");
	            while(rs.next())
	            {
	            	System.out.println("------------------------------");
	                String foodname = rs.getString("name");
	                double foodprice = rs.getDouble("price");
	                String description = rs.getString("description");
	                System.out.println(
	                    i+ "      " + foodname + "     " + "$" + foodprice + "     " + description);
	                i++;
	            }
	            System.out.println("------------------------------");
	        }
	        catch(SQLException e)
	        {
	            System.out.println(e.getMessage());
	        }	
		}
		if (a == 2)
		{
			String query1 = "SELECT name, price, description " +
							"FROM food " + 
							"WHERE food_id > 5 AND food_id < 11";
			try (Connection connection = DriverManager.getConnection(
	                dbUrl, username, password);
	             Statement statement = connection.createStatement();
	             ResultSet rs = statement.executeQuery(query1))
	        {
	            System.out.println("Entrees:");
	            while(rs.next())
	            {
	            	System.out.println("------------------------------");
	                String foodname = rs.getString("name");
	                double foodprice = rs.getDouble("price");
	                String description = rs.getString("description");
	                System.out.println(
		                    i+ "      " + foodname + "     " + "$" + foodprice + "     " + description);
		                i++;
	            }
	            System.out.println("------------------------------");
	        }
	        catch(SQLException e)
	        {
	            System.out.println(e.getMessage());
	        }	
		}
		else if(a == 3)
		{
			String query1 = "SELECT name, price, description " +
							"FROM food " + 
							"WHERE food_id > 10 AND food_id < 16";
			try (Connection connection = DriverManager.getConnection(
	                dbUrl, username, password);
	             Statement statement = connection.createStatement();
	             ResultSet rs = statement.executeQuery(query1))
	        {
	            System.out.println("Desserts:");
	            while(rs.next())
	            {
	            	System.out.println("------------------------------");
	                String foodname = rs.getString("name");
	                double foodprice = rs.getDouble("price");
	                String description = rs.getString("description");
	                System.out.println(
		                    i+ "      " + foodname + "     " + "$" + foodprice + "     " + description);
		                i++;
	            }
	            System.out.println("------------------------------");
	        }
	        catch(SQLException e)
	        {
	            System.out.println(e.getMessage());
	        }	
		}
		else if(a == 4)
		{
			String query1 = "SELECT name, price, description " +
							"FROM food " + 
							"WHERE food_id > 15";
			try (Connection connection = DriverManager.getConnection(
	                dbUrl, username, password);
	             Statement statement = connection.createStatement();
	             ResultSet rs = statement.executeQuery(query1))
	        {
	            System.out.println("Beverage:");
	            while(rs.next())
	            {
	            	System.out.println("------------------------------");
	                String foodname = rs.getString("name");
	                double foodprice = rs.getDouble("price");
	                String description = rs.getString("description");
	                System.out.println(
		                    i+ "      " + foodname + "     " + "$" + foodprice + "     " + description);
		                i++;
	            }
	            System.out.println("------------------------------");
	        }
	        catch(SQLException e)
	        {
	            System.out.println(e.getMessage());
	        }	
		}
	}

	/**
	 * Returns the id of the party seated at a given table
	 * @param tableid The table number in which a party is seated at
	 * @return The party id of the party seated at the given table
	 */
	public static int partyAtTable(int tableid)
	{
		int partyid = -1;
		String query = "SELECT party_id " +
						"FROM parties " +
						"WHERE table_id = '" + tableid + "' AND time_left IS NULL";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
			if(rs.next())
			{
            	partyid = rs.getInt("party_id");
			}
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }	
		return partyid;
	}
	
	/**
	 * Determines if the table id entered is in fact occupied by a party
	 * @param tableid The table id that is being checked for a party
	 * @return A 1 or 0; 1 meaning the table is occupied or 0 meaning the table is vacant
	 */
	public static int checkOccupied(int tableid)
	{
		int occupied = 0;
		String query = "SELECT occupied " +
						"FROM tables " +
						"WHERE table_id = '"+ tableid + "'";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);
		     Statement statement = connection.createStatement();
		     ResultSet rs = statement.executeQuery(query))
		{
		    if(rs.next())
		    {
		    	occupied = rs.getInt("occupied");
		    }
		    else
		    	occupied = -1;
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}	
		return occupied;
	}
	
	/**
	 * Takes the order of the party one item at a time
	 * @param partyid The id of the party having their order being taken
	 */
	public static void takeOrder(int partyid, int menuPrint)
	{
		Scanner scan = new Scanner(System.in);
		Scanner scan5 = new Scanner(System.in);
		Scanner scan6 = new Scanner(System.in);
		System.out.println("What would you like to order?");
		printMenu(menuPrint);
		System.out.println("6. I do not want to order from this menu");
		int order = scan.nextInt();
		while((order < 1) || (order > 6))
		{
			System.out.println("Invalid menu item! What would you like to order?");
			printMenu(1);
			System.out.println("6. I do not want to order from this menu");
			order = scan.nextInt();
		}
		if((order == 6) && (menuPrint == 1))
			System.out.println("Exiting appetizers menu");
		else if((order == 6) && (menuPrint == 2))
			System.out.println("Exiting entrees menu");
		else if((order == 6) && (menuPrint == 3))
			System.out.println("Exiting desserts menu");
		else if((order == 6) && (menuPrint == 4))
			System.out.println("Exiting beverages menu");
		else
		{
			System.out.println("Any special instructions for this order? (y/n)");
			String YNspecial = scan5.nextLine();
			
			while((!YNspecial.equalsIgnoreCase("y")) && (!YNspecial.equalsIgnoreCase("n")))
			{
				System.out.println("Please enter Y or N. \nAny special instructions for this order? (Y/N)");
				YNspecial = scan6.nextLine();
			}
			if(YNspecial.equalsIgnoreCase("y"))
			{
				System.out.println("What is your special instruction?");
				String special = scan6.nextLine();
				if(menuPrint == 2)
					recordOrder(order+5, partyid, special);
				else if(menuPrint == 3)
					recordOrder(order+10, partyid, special);
				else if(menuPrint == 4)
					recordOrder(order+15, partyid, special);
				else
					recordOrder(order, partyid, special);
			}
			else
				if(menuPrint == 2)
					recordOrder(order+5, partyid, null);
				else if(menuPrint == 3)
					recordOrder(order+10, partyid, null);
				else if(menuPrint == 4)
					recordOrder(order+15, partyid, null);
				else
					recordOrder(order, partyid, null);
		}	
	}
	
	/**
	 * Records the order of a party member one menu item at a time in the database
	 * @param foodorder The id of the food that wants to be ordered by the patron
	 * @param partyid The party id of the party ordering the food
	 * @param special Any special instruction the patron wishes to be applied to their order
	 */
	public static void recordOrder(int foodorder, int partyid, String special)
	{
		
		String query = "INSERT INTO orders VALUES " + 
						"(DEFAULT, '" + partyid + "', '" + foodorder + "', '" + special + "')";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query);
		    System.out.println("Order has been taken!");
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		
		String query2 = "UPDATE parties " +
						"SET status = 'ordered' " +
						"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query2);
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Uses the partyAtTable method to get the party_id 
	 * @return The party_id of the party at the entered table
	 */
	public static int getPartyID()
	{
		Scanner scan = new Scanner(System.in);
		int partyid;
		boolean exitloop = false;
		System.out.println("What table are you seated at?");
		int tableNum = scan.nextInt(); 
		while(exitloop == false)
		{
			while((tableNum > 28) || (tableNum < 1))
			{
				System.out.println("Invalid table number. What table are you seated at?");
				tableNum = scan.nextInt();
			}
			while(checkOccupied(tableNum) == 0)
			{
				System.out.println("This table is vacant. What table are you seated at?");
				tableNum = scan.nextInt();
			}
			if(((tableNum <= 28) && (tableNum >= 1)) && (checkOccupied(tableNum) == 1))
				exitloop = true;
		}
		partyid = partyAtTable(tableNum);
		return partyid;
	}
	
	/**
	 * Prints out the order of the given party id
	 * @param partyid The id of the party whose order is being printed
	 */
	public static int printOrder(int partyid)
	{
		int i = 1;
		String query = "SELECT f.name, o.special, f.price " +
						"FROM food f JOIN orders o " + 
						"ON f.food_id = o.food_id " +
						"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Your party's order :");
            while(rs.next())
            {
                System.out.println("-------------------");
                String foodname = rs.getString("name");
                String special = rs.getString("special");
                double price = rs.getDouble("price");
                System.out.println(
                i + "     " + foodname + "     " + special + "     $" + price);
                i++;
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
		return i;
	}
	
	/**
	 * Changes the special instruction on a food order item
	 * @param numItem The number of the food item in the order
	 * @param partyid The id of the party that the food order 
	 * @param special
	 */
	public static void changeOrder(int numItem, int partyid, String special)
	{
		int i = 1;
		int orderid = 0, foodid = 0;
		String newspecial = "";
		String query = "SELECT f.food_id, f.name, o.special, o.order_id, f.price " +
						"FROM food f JOIN orders o " + 
						"ON f.food_id = o.food_id " +
						"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Your party's order :");
            while(rs.next())
            {
                if(i == numItem)
                {
                	newspecial = rs.getString("special");
                	foodid = rs.getInt("food_id"); 
                	orderid = rs.getInt("order_id");
                	break;
                }
                i++;
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
		
		String query2 = "UPDATE orders " +
						"SET special = '"+ newspecial + "' " +
						"WHERE food_id = '"+ foodid + "' AND order_id = '"+ orderid + "'";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query2);
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Cancels the order made by the party
	 * @param numItem The number of the item in the order to be cancelled
	 * @param partyid The id of the party that wishes to cancel an order
	 */
	public static void cancelOrder(int numItem, int partyid)
	{
		int i = 1;
		int orderid = 0, foodid = 0;
		String query = "SELECT o.order_id " +
						"FROM food f JOIN orders o " + 
						"ON f.food_id = o.food_id " +
						"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Your party's order :");
            while(rs.next())
            {
                if(i == numItem)
                {
                	orderid = rs.getInt("order_id");
                	break;
                }
                i++;
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
		
		String query2 = "DELETE FROM orders " +
						"WHERE order_id = '" + orderid + "'";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query2);
		    System.out.println("Order cancelled!");
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		
	}
	
	/**
	 * Sets the table to not occupied and marks the party as left
	 * @param partyid The id of the party that has left
	 */
	public static void partyLeftfreeTable()
	{
		Scanner scan = new Scanner(System.in);
		boolean exitloop = false;
		System.out.println("What table are you seated at?");
		int tableNum = scan.nextInt(); 
		while(exitloop == false)
		{
			while((tableNum > 28) || (tableNum < 1))
			{
				System.out.println("Invalid table number. What table are you seated at?");
				tableNum = scan.nextInt();
			}
			while(checkOccupied(tableNum) == 0)
			{
				System.out.println("This table is vacant. What table are you seated at?");
				tableNum = scan.nextInt();
			}
			if(((tableNum <= 28) && (tableNum >= 1)) && (checkOccupied(tableNum) == 1))
				exitloop = true;
		}
		int tableid = tableNum;
		int partyid = partyAtTable(tableNum);

		String query1 = "UPDATE parties " +
						"SET time_left = NOW(), " + 
						"status = 'left' " +
						"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);)
        {
			Statement statement = connection.createStatement();
            int rs = statement.executeUpdate(query1);
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
		
		String query3 = "UPDATE tables " +
						"SET occupied = DEFAULT " + 
						"WHERE table_id = '" + tableid + "'";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query3);
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		System.out.println("Table has been freed! Free at last!");
	}
	
	/**
	 * Calculates the total bill of a party
	 * @return The total of the bill of the party
	 */
	public static double calculateTotal()
	{
		Scanner scan40 = new Scanner(System.in);
		boolean exitloop = false;
		System.out.println("What table are you seated at?");
		int tableNum = scan40.nextInt(); 
		while(exitloop == false)
		{
			while((tableNum > 28) || (tableNum < 1))
			{
				System.out.println("Invalid table number. What table are you seated at?");
				tableNum = scan40.nextInt();
			}
			while(checkOccupied(tableNum) == 0)
			{
				System.out.println("This table is vacant. What table are you seated at?");
				tableNum = scan40.nextInt();
			}
			if(((tableNum <= 28) && (tableNum >= 1)) && (checkOccupied(tableNum) == 1))
				exitloop = true;
		}
		int partyid = partyAtTable(tableNum);
		double total = 1;
		
		String query1 = "SELECT SUM(f.price) as total_due " + 
						"FROM food f JOIN orders o " +
						"ON	f.food_id = o.food_id " +
						"JOIN parties p " +
						"ON	p.party_id = o.party_id " +
						"WHERE p.party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query1))
        {
			if(rs.next())
				total = rs.getDouble("total_due");
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
		String query = "UPDATE parties " +
						"SET total = '"+ total + "' " + 
						"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
        dbUrl, username, password);)
        {
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query);
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		return total;
	}
	
	/**
	 * Gets the size of the party that is sitting at the given table
	 * @return The size of the party
	 */
	public static double calculateSplitTotal()
	{
		Scanner scan = new Scanner(System.in);
		boolean exitloop = false;
		System.out.println("What table are you seated at?");
		int tableNum = scan.nextInt(); 
		while(exitloop == false)
		{
			while((tableNum > 28) || (tableNum < 1))
			{
				System.out.println("Invalid table number. What table are you seated at?");
				tableNum = scan.nextInt();
			}
			while(checkOccupied(tableNum) == 0)
			{
				System.out.println("This table is vacant. What table are you seated at?");
				tableNum = scan.nextInt();
			}
			if(((tableNum <= 28) && (tableNum >= 1)) && (checkOccupied(tableNum) == 1))
				exitloop = true;
		}
		int partyid = partyAtTable(tableNum);
		int partySize = 0;
		String query12 = "SELECT size " +
						"FROM parties " + 
						"WHERE party_id = '" + partyid + "'";
		        try (Connection connection = DriverManager.getConnection(
		                dbUrl, username, password);
		             Statement statement = connection.createStatement();
		             ResultSet rs = statement.executeQuery(query12))
		        {
		            if(rs.next())
		            {
		                partySize = rs.getInt("size");
		            }
		        }
		        catch(SQLException e)
		        {
		            System.out.println(e.getMessage());
		        }
		        double total = 1;
				
			String query1 = "SELECT SUM(f.price) as total_due " + 
							"FROM food f JOIN orders o " +
							"ON	f.food_id = o.food_id " +
							"JOIN parties p " +
							"ON	p.party_id = o.party_id " +
							"WHERE p.party_id = '" + partyid + "'";
				try (Connection connection = DriverManager.getConnection(
		                dbUrl, username, password);
		             Statement statement = connection.createStatement();
		             ResultSet rs = statement.executeQuery(query1))
		        {
					if(rs.next())
						total = rs.getDouble("total_due");
		        }
		        catch(SQLException e)
		        {
		            System.out.println(e.getMessage());
		        }
				String query = "UPDATE parties " +
								"SET total = '"+ total + "' " + 
								"WHERE party_id = '" + partyid + "'";
				try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);)
		        {
					Statement statement = connection.createStatement();
				    int rs = statement.executeUpdate(query);
				}
				catch(SQLException e)
				{
				    System.out.println(e.getMessage());
				}
				total = total / partySize;
				String query11 = "UPDATE parties " +
						"SET split_check = 1, " +
						"num_checks = '" + partySize + "' " +
						"WHERE party_id = '" + partyid + "'";
				try (Connection connection = DriverManager.getConnection(
						dbUrl, username, password);)
		        {
					Statement statement = connection.createStatement();
				    int rs = statement.executeUpdate(query11);
				}
				catch(SQLException e)
				{
				    System.out.println(e.getMessage());
				}
				return total;
		}
	
	/**
	 * Calculates the tip of the party
	 * @return The value of the tip left by the party
	 */
	public static double leaveTip()
	{
		Scanner scan = new Scanner(System.in);
		boolean exitloop = false;
		System.out.println("What table are you seated at?");
		int tableNum = scan.nextInt(); 
		while(exitloop == false)
		{
			while((tableNum > 28) || (tableNum < 1))
			{
				System.out.println("Invalid table number. What table are you seated at?");
				tableNum = scan.nextInt();
			}
			while(checkOccupied(tableNum) == 0)
			{
				System.out.println("This table is vacant. What table are you seated at?");
				tableNum = scan.nextInt();
			}
			if(((tableNum <= 28) && (tableNum >= 1)) && (checkOccupied(tableNum) == 1))
				exitloop = true;
		}
		int partyid = partyAtTable(tableNum);
		double total = 1;
		int partySize = 0;
		String query1 = "SELECT SUM(f.price) as total_due, p.size " + 
						"FROM food f JOIN orders o " +
						"ON	f.food_id = o.food_id " +
						"JOIN parties p " +
						"ON	p.party_id = o.party_id " +
						"WHERE p.party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query1))
        {
			if(rs.next())
			{	
				total = rs.getDouble("total_due");
				partySize = rs.getInt("size");
			}
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
		String query = "UPDATE parties " +
						"SET total = '"+ total + "' " + 
						"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
        dbUrl, username, password);)
        {
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query);
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		double tip = 0;
		if(partySize < 8)
			tip = total * .15;
		else
			tip = total * .20;
		String query20 = "UPDATE parties " +
				"SET tip = '"+ tip + "' " + 
				"WHERE party_id = '" + partyid + "'";
		try (Connection connection = DriverManager.getConnection(
		dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
		    int rs = statement.executeUpdate(query20);
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		return tip;
	}
	
	/**
	 * Takes a reservation for a party that is incoming
	 * @param size The size of the incoming party
	 * @param name The name of the incoming party
	 */
	public static void takeReservation(int size, String name)
	{
		String query = "INSERT INTO reservations (reserve_id, size, reserve_name, reserve_time) " +
						"VALUES (DEFAULT, '" + size + "','" + name + "', NOW());";
		try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);)
        {
			Statement statement = connection.createStatement();
            int rs = statement.executeUpdate(query);
		 	System.out.println("Reservation recorded!");
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}

	/**
	 * Seats a reservation at a table after they have entered the restaurant
	 * @param name The name of the party that created the reservation
	 */
	public static void seatReservation(String name)
	{
		int sizeOfReservation = 0;
		String query1 = "SELECT size " + 
				"FROM reservations " + 
				"WHERE reserve_name = '" + name + "' " +
				"ORDER BY reserve_id DESC";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);
		     Statement statement = connection.createStatement();
		     ResultSet rs = statement.executeQuery(query1))
		{
		    if(rs.next())
		    {
		    	sizeOfReservation = rs.getInt("size");
		    }
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		
		String query2 = "INSERT INTO parties (name, size, server_id, table_id, time_seated, status) " +
						"VALUES ('"+ name + "', '" + sizeOfReservation + "',  null, null, NOW(), 'waitor');";
		try (Connection connection = DriverManager.getConnection(
				dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
			int rs = statement.executeUpdate(query2);
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
		
		int tableid = assignParty(name, sizeOfReservation);
		String query3 = "UPDATE parties " +
						"SET table_id = '" + tableid + "' " +
						"WHERE name = '" + name + "' AND table_id IS NULL";
		try (Connection connection = DriverManager.getConnection(
				dbUrl, username, password);)
		{
			Statement statement = connection.createStatement();
			int rs = statement.executeUpdate(query3);
			System.out.println("Reservation " + name + " has been seated at table " + tableid + " !");
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
	}
								
								/* MANAGING MENU METHODS */
	/**
	 * Calculates the number of receipts that were entered into the system
	 * @return The total number of receipts 
	 */
 	public static int numReceipts()
	{
        String query = "SELECT SUM(num_checks) AS total " +
        				"FROM parties;";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            while(rs.next())
            {       
                int total = rs.getInt("total");
                return total;
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return 0;
	}
	
	/**
	 * Prints out the top 5 most ordered menu items
	 */
	public static void top5Items()
	{
        String query = "SELECT o.food_id, f.name, COUNT(o.food_id) AS num_ordered " + 
        				"FROM orders o JOIN food f " + 
        				"ON o.food_id = f.food_id " +
        				"GROUP BY food_id " +
        				"ORDER BY num_ordered DESC " +
        				"LIMIT 5;";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Top 5 Menu Items :");
            while(rs.next())
            {
            	System.out.println("------------------------------");
                String foodName = rs.getString("name");
                int numOrders = rs.getInt("num_ordered");

                System.out.println(
                    "Menu item:     " + foodName);
                System.out.println(
                      "Amount ordered:     " + numOrders);
                    
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * Prints the top 3 most ordered drinks
	 */
	public static void top3Drinks()
	{
        String query = "SELECT o.food_id, f.name, COUNT(o.food_id) AS num_ordered " + 
        				"FROM orders o JOIN food f " + 
        				"ON o.food_id = f.food_id " +
        				"GROUP BY food_id " +
        				"HAVING food_id > 15 " +
        				"ORDER BY num_ordered DESC " +
        				"LIMIT 3";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Top 3 Drinks :");
            while(rs.next())
            {
            	System.out.println("------------------------------");
                String drinkName = rs.getString("name");
                int numOrders = rs.getInt("num_ordered");

                System.out.println(
                    "Drink title:     " + drinkName);
                System.out.println(
                      "Amount ordered:     " + numOrders);
                    
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * Prints the top 3 most ordered appetizers
	 */
	public static void top3Apps()
	{
        String query = "SELECT o.food_id, f.name, COUNT(o.food_id) AS num_ordered " + 
        				"FROM orders o JOIN food f " + 
        				"ON o.food_id = f.food_id " +
        				"GROUP BY food_id " +
        				"HAVING food_id < 6 " +
        				"ORDER BY num_ordered DESC " +
        				"LIMIT 3;";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Top 3 Appetizers :");
            while(rs.next())
            {
            	System.out.println("------------------------------");
                String drinkName = rs.getString("name");
                int numOrders = rs.getInt("num_ordered");

                System.out.println(
                    "Appetizer title:     " + drinkName);
                System.out.println(
                     "Amount ordered:     " + numOrders);
                    
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * Calculates the average party size
	 * @return - the average party size
	 */
	public static double avgPartySize()
	{
        String query = "SELECT FORMAT(SUM(size) / COUNT(*), 2) as average_party_size " + 
        			   "FROM parties";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            while(rs.next())
            {       
                double avgGuest = rs.getDouble("average_party_size");
                return avgGuest;
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
		return 0;
	}
	
	/**
	 * This prints out the number of parties each server has served
	 */
	public static void numPartiesServed()
	{
        String query = "SELECT p.server_id, CONCAT(s.first_name, ' ', s.last_name) AS full_name, COUNT(*) as parties_served " + 
        				"FROM parties p JOIN servers s " + 
        				"ON p.server_id = s.server_id " +
        				"GROUP BY server_id;";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("# of parties served :");
            while(rs.next())
            {
            	System.out.println("------------------------------");   
                String fullname = rs.getString("full_name");
                int numParties = rs.getInt("parties_served");

                System.out.println(
                    "Name:          " + fullname);
                System.out.println(
                     "Parties served:     " + numParties);
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * Prints out the #1 tip earner
	 */
	public static void bestTipEarner()
	{
        String query = "SELECT p.server_id, CONCAT(s.first_name, ' ', s.last_name) AS full_name, SUM(tip) as total_tips " + 
        				"FROM parties p JOIN servers s " +
        				"ON p.server_id = s.server_id " +
        				"GROUP BY server_id " +
        				"ORDER BY total_tips DESC " + 
        				"LIMIT 1";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("#1 Tip Earner");
            while(rs.next())
            {
            	System.out.println("------------------------------");   
                String fullname = rs.getString("full_name");
                int totaltips = rs.getInt("total_tips");

                System.out.println(
                    "Name:          " + fullname);
                System.out.println(
                     "Total tips:     " + "$" + totaltips);
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}

	/**
	 * Prints out the #1 Salesperson (i.e. the server who served the parties with the highest average bill)
	 */
	public static void bestSalesperson()
	{
        String query = "SELECT p.server_id, CONCAT(s.first_name, ' ', s.last_name) AS full_name, FORMAT(AVG(p.total), 2) as avg_check_total " +
        				"FROM parties JOIN servers s " +
        				"GROUP BY p.server_id " +
        				"ORDER BY avg_check_total DESC " +
        				"LIMIT 1";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("#1 Salesperson");
            while(rs.next())
            {
            	System.out.println("------------------------------");   
                String fullname = rs.getString("full_name");
                int avgBill = rs.getInt("avg_check_total");

                System.out.println(
                    "Name:          " + fullname);
                System.out.println(
                     "Total tips:     " + "$" + avgBill);
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}

	/**
	 * Prints out the length of each party's stay. Only prints out parties who have left the restaurant
	 */
	public static void lengthOfStay()
	{
        String query = "SELECT name, timediff(time_left, time_seated) AS length_of_visit " +
        				"FROM parties " +
        				"WHERE time_left IS NOT NULL " +
        				"GROUP BY party_id";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Time of Stay: ");
            while(rs.next())
            {
            	System.out.println("------------------------------");   
                String partyname = rs.getString("name");
                Time lengthofvisit = (rs.getTime("length_of_visit"));

                System.out.println(
                    "Party Name:     " + partyname);
                System.out.println(
                     "Length of visit:     " +  lengthofvisit);
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	/**
	 * Prints out the amount of money from tips that the top-tip earner is taking home along with the amount of
	 * money all other servers will be taking home.
	 */
	public static void splitTips()
	{
        String query = "SELECT FORMAT((SUM(p.tip) / 6) * 2, 2) AS best_tips_earner, CONCAT(s.last_name, ', ', s.first_name) AS full_name " +
        				"FROM parties p JOIN servers s ON p.server_id = s.server_id ";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Take Home Tips: ");
            while(rs.next())
            {
            	System.out.println("------------------------------");   
                String servername = rs.getString("full_name");
                double topTipearner = rs.getDouble("best_tips_earner");

                System.out.println(
                    "Top Tip Earner:     " + servername);
                System.out.println(
                     "Taking Home:     " +  "$" + topTipearner);
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        
        String query2 = "SELECT FORMAT((SUM(tip) / 6),2) AS tips_earned " +
				"FROM parties";
		try (Connection connection = DriverManager.getConnection(
		        dbUrl, username, password);
		     Statement statement = connection.createStatement();
		     ResultSet rs = statement.executeQuery(query2))
		{
		    while(rs.next())
		    {
		    	System.out.println("------------------------------");   
		        double tips = rs.getDouble("tips_earned");
		
		        System.out.println(
		             "All other servers are recieving:     " +  "$" + tips + " each");
		    }
		    System.out.println();
		}
		catch(SQLException e)
		{
		    System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Prints out a log of all the parties that have left including their time seated, left and their bill total
	 */
	public static void partyTimes()
	{
        String query = "SELECT party_id, name, time_seated, time_left, total " + 
        				"FROM parties " + 
        				"WHERE time_left IS NOT NULL";
        try (Connection connection = DriverManager.getConnection(
                dbUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query))
        {
            System.out.println("Party Log: ");
            while(rs.next())
            {
            	System.out.println("------------------------------");   
                String partyname = rs.getString("name");
                Time timeseated = (rs.getTime("time_seated"));
                Time timeleft = (rs.getTime("time_left"));
                double total = rs.getDouble("total");

                System.out.println(
                    "Party Name:      " + partyname);
                System.out.println(
                     "Time seated:     " +  timeseated);
                System.out.println(
                       "Time left:       " +  timeleft);
                System.out.println(
                        "Total:           " +  "$" + total);
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
	}
	
	public static void main(String[] args) 
	{
		Scanner scan = new Scanner (System.in);
		int enter;
		int enter2;
		int action = 0;
		int partySize = 0;
		int currServer = getCurrServer();
		
		System.out.println("---* Welcome to Rubio's Trattoria! *---");
		System.out.println("---* What types of actions would you like to perform! *---");
		System.out.println("---* 1. Operational *---");
		System.out.println("---* 2. Managerial *---");
		System.out.println("---* 3. Leave the restarant *---");
		
		enter = scan.nextInt();
		
		while((enter != 1) && (enter != 2) && (enter != 3))
		{
			System.out.println("---* Invalid entry! *---");
			System.out.println("---* What types of actions would you like to perform! *---");
			System.out.println("---* 1. Operational *---");
			System.out.println("---* 2. Managerial *---");
			System.out.println("---* 3. Leave the restarant *---");
			enter = scan.nextInt();
		}
		if(enter == 3)	//Farewell message
			System.out.println("Good day to you!");
		else
		{
			while(enter != 3)
			{
				if(enter == 1)
				{
					do
					{
						System.out.println("---* You chose to run the restaurant! *---");
						System.out.println("---* Here are your options *---");
						System.out.println("---* 1. Retrieve the names of all servers *---");
						System.out.println("---* 2. Retrieve a list of all free tables of a given size *---");
						System.out.println("---* 3. Assign a party to a free table *---");
						System.out.println("---* 4. Sit someone at the bar *---");
						System.out.println("---* 5. Assign a server to a party *---");
						System.out.println("---* 6. Take orders for the patrons in a party *---");
						System.out.println("---* 7. Allow a patron to change his/her order or cancel an ordered item *---");
						System.out.println("---* 8. Record any special intructions for preparing the meal *---");
						System.out.println("---* 9. Calculate the total due for a party *---");
						System.out.println("---* 10. Calculate the total due for an individual diner if check is split *---");
						System.out.println("---* 11. Allocate a tip for a server (for parties of fewer than 8 patrons) *---");
						System.out.println("---* 12. Assess a 20% gratuity (for parties of 8 or more) *---");
						System.out.println("---* 13. Free a table for a party that has left *---");
						System.out.println("---* 14. Take a reservation *---");
						System.out.println("---* 15. Seat a party that had a reservation *---");
						System.out.println("---* 16. Return to actions menu *---");
						action = scan.nextInt();
						
						if(action == 1)
							printServers();
						if(action == 2)
							freeTables();
						if(action == 3)
						{
							Scanner scan2 = new Scanner (System.in);
							System.out.println("What is the name of your party?");
							String nameOfParty = scan2.nextLine();
							
							while(nameOfParty.equals(""))
							{
								System.out.println("Invalid party name. You must enter a name for your party!");
								nameOfParty = scan2.nextLine();
							}
							
							System.out.println("What is the size of your party?");
							int sizeOfParty = scan2.nextInt();
							
							while((sizeOfParty < 1) || (sizeOfParty > 10))
							{
								System.out.println("Invalid party size. Please enter a size between 1 and 10");
								sizeOfParty = scan2.nextInt();
							}
							int seatedAt = assignParty(nameOfParty, sizeOfParty);
						
							if(seatedAt == -1)
								System.out.println("There are currently no available seats at this moment.");
							else
								System.out.println(nameOfParty + " will be seated at table " + seatedAt);
						}
						if(action == 4)
						{
							Scanner scan3 = new Scanner (System.in);
							System.out.println("What is your name?");
							String barname = scan3.nextLine();
							
							while(barname.equals(""))
							{
								System.out.println("Invalid name. You must enter a name!");
								barname = scan3.nextLine();
							}
							int seatedAt = assignParty(barname, 1);
							System.out.println(barname + " will be seated at bar stool " + seatedAt);
						}
						if(action == 5)
						{
							Scanner scan40 = new Scanner(System.in);
							boolean exitloop = false;
							System.out.println("What table are you seated at?");
							int tableNum = scan40.nextInt(); 
							while(exitloop == false)
							{
								while((tableNum > 28) || (tableNum < 1))
								{
									System.out.println("Invalid table number. What table are you seated at?");
									tableNum = scan40.nextInt();
								}
								while(checkOccupied(tableNum) == 0)
								{
									System.out.println("This table is vacant. What table are you seated at?");
									tableNum = scan40.nextInt();
								}
								if(((tableNum <= 28) && (tableNum >= 1)) && (checkOccupied(tableNum) == 1))
									exitloop = true;
							}
							int partyid = partyAtTable(tableNum);
							if(currServer == 5)
								currServer = 1;
							else
								currServer++;
							int serverAssigned = assignServer(partyid, currServer);
							if(serverAssigned == -1)
								System.out.println("That party is already being served!");
							else
								System.out.println("The party has been assigned a server");
						}
						if(action == 6)
						{
							int partyid = getPartyID();
							boolean exit = false;
							while(exit != true)
							{
								System.out.println("What would you like to order?");
								System.out.println("1. Appetizers");
								System.out.println("2. Entrees");
								System.out.println("3. Desserts");
								System.out.println("4. Beverages");
								System.out.println("5. I don't want to order");
								int choice = scan.nextInt();
								
								while((choice < 1) || (choice > 5))
								{
									System.out.println("Invalid choice! What would you like to order?");
									System.out.println("1. Appetizers");
									System.out.println("2. Entrees");
									System.out.println("3. Desserts");
									System.out.println("4. Beverages");
									System.out.println("5. I don't want to order");
									choice = scan.nextInt();
								}
								while((choice > 0) && (choice < 5))
								{
									if(choice == 1)
										takeOrder(partyid, 1);
									else if(choice == 2)
										takeOrder(partyid, 2);
									else if(choice == 3)
										takeOrder(partyid, 3);
									else if(choice == 4)
										takeOrder(partyid, 4);
									
									System.out.println("What would you like to order?");
									System.out.println("1. Appetizers");
									System.out.println("2. Entrees");
									System.out.println("3. Desserts");
									System.out.println("4. Beverages");
									System.out.println("5. I don't want to order");
									choice = scan.nextInt();
								}
								if(choice == 5)
								{
									System.out.println("Order finished!");
									exit = true;
								}
							}
						}
						if(action == 7)
						{
							Scanner scan7 = new Scanner(System.in);
							int partyid = getPartyID();
							int numItems = printOrder(partyid);
							System.out.println("What do you want to do with your order?");
							System.out.println("1. Change order");
							System.out.println("2. Cancel order");
							int choice = scan.nextInt();
							while((choice > 2) || (choice < 1))
							{
								System.out.println("Invalid choice. What do you want to do with your order?");
								System.out.println("1. Change order");
								System.out.println("2. Cancel order");
								choice = scan.nextInt();
							}
							if(choice == 1)
							{	
								printOrder(partyid);
								System.out.println(numItems + ". Do not change anything");
								System.out.println("Which number order item do you want to change?");
								int change = scan.nextInt();
								while((change < 1) || (change > numItems))
								{
									System.out.println("Incorrect entry!");
									printOrder(partyid);
									System.out.println(numItems + ". Do not change anything");
									System.out.println("Which number order item do you want to change?");
									change = scan.nextInt();
								}
								if(change == numItems)
									System.out.println("Exiting order change menu");
								else if(change <= numItems)
								{
									System.out.println("Do you want to change....?");
									System.out.println("1. The order item");
									System.out.println("2. The special instruction");
									int change2 = scan.nextInt();
									while((change2 < 1) || (change2 > 2))
									{
										System.out.println("Incorrect entry!");
										printOrder(partyid);
										System.out.println("Do you want to change....?");
										System.out.println("1. The order item");
										System.out.println("2. The special instruction");
										change2 = scan.nextInt();
									}
									if(change2 == 1)
									{
										cancelOrder(change, partyid);
										boolean exit = false;
										while(exit != true)
										{
											System.out.println("What would you like to order?");
											System.out.println("1. Appetizers");
											System.out.println("2. Entrees");
											System.out.println("3. Desserts");
											System.out.println("4. Beverages");
											System.out.println("5. I don't want to order");
											int choice2 = scan.nextInt();
											
											while((choice2 < 1) || (choice2 > 5))
											{
												System.out.println("Invalid choice! What would you like to order?");
												System.out.println("1. Appetizers");
												System.out.println("2. Entrees");
												System.out.println("3. Desserts");
												System.out.println("4. Beverages");
												System.out.println("5. I don't want to order");
												choice2 = scan.nextInt();
											}
											while((choice2 > 0) && (choice2 < 5))
											{
												if(choice2 == 1)
													takeOrder(partyid, 1);
												else if(choice2 == 2)
													takeOrder(partyid, 2);
												else if(choice2 == 3)
													takeOrder(partyid, 3);
												else if(choice2 == 4)
													takeOrder(partyid, 4);
												System.out.println("Order successfully changed!");
												exit = true;
												break;
											}
											if(choice == 5)
											{
												System.out.println("Order successfully changed!");
												exit = true;
											}
										}
									}
									if(change2 == 2)
									{
										printOrder(partyid);
										System.out.println(numItems + ". Do not change anything");
										System.out.println("Which number order item do you want to change?");
										int changeSpec = scan.nextInt();
										while((changeSpec < 1) || (changeSpec > numItems))
										{
											System.out.println("Incorrect entry!");
											printOrder(partyid);
											System.out.println(numItems + ". Do not change anything");
											System.out.println("Which number order item do you want to change?");
											changeSpec = scan.nextInt();
										}
										if(changeSpec == numItems)
											System.out.println("Exiting order change menu");
										else if(changeSpec <= numItems)
										{
											System.out.println("What is your special instruction");
											String special = scan7.nextLine();
											changeOrder(changeSpec, partyid, special);
										}
									}
								}
							}
							else if(choice == 2)
							{
								printOrder(partyid);
								System.out.println(numItems + ". Do not cancel anything");
								System.out.println("Which number order item do you want to cancel?");
								int cancel = scan.nextInt();
								while((cancel < 1) || (cancel > numItems))
								{
									System.out.println("Incorrect entry!");
									printOrder(partyid);
									System.out.println(numItems + ". Do not cancel anything");
									System.out.println("Which number order item do you want to cancel?");
									cancel = scan.nextInt();
								}
								if(cancel == numItems)
									System.out.println("Exiting order change menu");
								else if(cancel <= numItems)
									cancelOrder(cancel, partyid);
							}
						}
						if(action == 8)
							System.out.println("If you wish to change or add to an order, please select the 7th option from the operational menu");
						if(action == 9)
							System.out.println("$" + calculateTotal() + " is the total of this party");
						if(action == 10)
							System.out.println("$" + calculateSplitTotal() + " is the total due from each member of the party");
						if(action == 11)
						{
							double tip = leaveTip();
							System.out.println("You left $" + tip + " as a tip");
						}
						if(action == 12)
						{
							double tip = leaveTip();
							System.out.println("You left $" + tip + " as a tip");
						}
						if(action == 13)
							partyLeftfreeTable();
						if(action == 14)
						{
							Scanner scan5 = new Scanner (System.in);
							System.out.println("What is the name of your party?");
							String nameOfParty = scan5.nextLine();
							
							while(nameOfParty.equalsIgnoreCase(""))
							{
								System.out.println("Invalid party name. You must enter a valid party name!");
								nameOfParty = scan5.nextLine();
							}
							
							System.out.println("What is the size of your party?");
							int sizeOfParty = scan5.nextInt();
							
							while((sizeOfParty < 1) || (sizeOfParty > 10))
							{
								System.out.println("Invalid party size. Please enter a size between 1 and 10");
								sizeOfParty = scan5.nextInt();
							}
							takeReservation(sizeOfParty, nameOfParty);
						}
						if(action == 15)
						{
							Scanner scan6 = new Scanner (System.in);
							System.out.println("What is the name of your party?");
							String nameOfParty = scan6.nextLine();
							
							while(nameOfParty.equalsIgnoreCase(""))
							{
								System.out.println("Invalid party name. You must enter a valid party name!");
								nameOfParty = scan6.nextLine();
							}
							seatReservation(nameOfParty);
						}
					} while(((action < 1) || (action > 16)) || (action != 16));
					if(action == 16)
						System.out.println("Exiting running menu...");
				}
					else
					{
						do
						{
							System.out.println("---* You chose to manage the restaurant! *---");
							System.out.println("---* Here are your options *---");
							System.out.println("---* 1. Compute the total receipts *---");
							System.out.println("---* 2. Determine the top 5 items ordered  *---");
							System.out.println("---* 3. Determine the top 3 drinks ordered *---");
							System.out.println("---* 4. Determine the top 3 appetizers ordered *---");
							System.out.println("---* 5. Calculate the average number of guests per table *---");
							System.out.println("---* 6. Determine how many parties each server served *---");
							System.out.println("---* 7. Determine which server recieved the most tips and how much they were *---");
							System.out.println("---* 8. Determine which server servered the parties with the highest average bill *---");
							System.out.println("---* 9. Determine how long a party took to eat a meal *---");
							System.out.println("---* 10. Split the tips among servers, giving the top-tip earner twice as much money *---");
							System.out.println("---* 11. Retrieve a list of parties, showing the time they were seated, the time they left  and how much they spent *---");
							System.out.println("---* 12. Return to the actions menu *---");
							action = scan.nextInt();
							
							if(action == 1)
								System.out.println("# of receipts: " + numReceipts());
							if(action == 2)
								top5Items();
							if(action == 3)
								top3Drinks();
							if(action == 4)
								top3Apps();
							if(action == 5)
								System.out.println("The average party size is: " + avgPartySize());
							if(action == 6)
								numPartiesServed();
							if(action == 7)
								bestTipEarner();
							if(action == 8)
								bestSalesperson();
							if(action == 9)
								lengthOfStay();
							if(action == 10)
								splitTips();
							if(action == 11)
								partyTimes();
		
						} while(((action < 1) || (action > 12)) || (action != 12));
						if(action == 12)
							System.out.println("Exiting managing menu...");
					}
				do
				{
					System.out.println("---* Welcome to Rubio's Trattoria! *---");
					System.out.println("---* What types of actions would you like to perform! *---");
					System.out.println("---* 1. Operational *---");
					System.out.println("---* 2. Managerial *---");
					System.out.println("---* 3. Leave the restarant *---");
					enter = scan.nextInt();
				} while((enter != 3) &&(enter != 2) &&(enter != 1)) ;
			}
				if(enter == 3)		// Farewell message
					System.out.println("Good day sir!");
		}
	}
}
