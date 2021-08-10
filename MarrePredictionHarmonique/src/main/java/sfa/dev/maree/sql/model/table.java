package sfa.dev.maree.sql.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class table 
{
	public tools _tool = null;
	private String _nomTable;
	private String[] _arg;
	
	public table (String nomTable, String[] arg)
	{
		_tool = new tools();
		_nomTable = nomTable;
		_arg = arg;
	}
	
	public int AddTable() 
	{
		Connection conn = _tool.connect();
		if (conn == null)
			return -1;

		try 
		{
			String sql = "create table if not exists " + _nomTable + "(";
			short nbArg = (short) _arg.length;
			for (String x : _arg)
			{
				nbArg--;
				sql += x;
				if (nbArg > 0)
					sql += ",";
			}
			sql += ")";
			System.out.println(sql);
			Statement stmt = conn.createStatement();
		    stmt.executeUpdate(sql);		    
			return 0;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			_tool.close(conn);
		}
		return -1;
	}
}
