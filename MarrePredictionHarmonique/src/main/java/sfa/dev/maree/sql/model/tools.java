package sfa.dev.maree.sql.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;




/*
MySQL message
mysql> create table maree_item (uid bigint UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    -> date DATE NOT NULL,
    -> heure TIME NOT NULL,
    -> hauteur DOUBLE,
    -> coef SMALLINT,
    -> type set('bm', 'pm', 'undef') DEFAULT 'undef');
*/


public class tools 
{
	static boolean initFirst = init();

	private static boolean init() 
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	
	public Connection connect() 
	{	
		if (initFirst)
		{
			Connection connect = null;
			try
			{
				// mysql en service
				// connect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/pierre_fages?"+ "user=root");
				
				// wamp
				String userName = "root";
	            String password = "";
	            String url1="jdbc:mysql://localhost:3306/pierre_fages";
	            connect = DriverManager.getConnection (url1, userName, password);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return null;
			}
			return connect;
		}
		return null;
	}

	public void close(Connection connect) 
	{	
		try
		{
			connect.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return;	
	}


	
	public void writeMetaData(ResultSet resultSet) throws SQLException 
	{
		// 	Now get some metadata from the database
		// Result set get the result of the SQL query

		System.out.println("The columns in the table are: ");

		System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
			System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
		}
	}



	public int parseuidResultSet(ResultSet resultSet) throws SQLException 
	{
		int uid = -1;
		while (resultSet.next()) {
			uid = resultSet.getInt("uid");
		}
		return uid;
	}
	
}
