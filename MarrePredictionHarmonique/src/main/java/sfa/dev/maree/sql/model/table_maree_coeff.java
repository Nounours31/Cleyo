package sfa.dev.maree.sql.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class table_maree_coeff extends table 
{
	/*
		+---------+------------------+------+-----+---------+----------------+
		| Field   | Type             | Null | Key | Default | Extra          |
		+---------+------------------+------+-----+---------+----------------+
		| uid     | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
		| port    | text             | YES  |     | NULL    |                |
		| type    | enum('METRIC')   | YES  |     | NULL    |                |
		| Metric1 | int(11)          | YES  |     | NULL    |                |
		| Metric2 | int(11)          | YES  |     | NULL    |                |
		| Metric3 | int(11)          | YES  |     | NULL    |                |
		| Metric4 | int(11)          | YES  |     | NULL    |                |
		| Z0      | double           | YES  |     | NULL    |                |
		+---------+------------------+------+-----+---------+----------------+	 
	 */
	static final private String _nomTable  = "maree_coeff";
	static final private String[] arg = {
		"uid 		int unsigned auto_increment NOT NULL PRIMARY KEY",
		"port 		text",
		"type 		enum('METRIC')",
		"Metric1  	int",
		"Metric2  	int",
		"Metric3  	int",
		"Metric4  	int",
		"Z0  		double"
	};
	
	
	private String _port  = null;
	private String _type  = "METRIC";
	private int _Metric1  = -1;
	private int _Metric2  = -1;
	private int _Metric3  = -1;
	private int _Metric4  = -1;
	private double _Z0    = -1.0;
	
	public table_maree_coeff ()
	{
		super(_nomTable, arg);
		
	}
	
	
	public table_maree_coeff(String _port, String _type, int _Metric1, int _Metric2, int _Metric3, int _Metric4, double _Z0) 
	{
		super(_nomTable, arg);
		this._port = _port;
		this._type = _type;
		this._Metric1 = _Metric1;
		this._Metric2 = _Metric2;
		this._Metric3 = _Metric3;
		this._Metric4 = _Metric4;
		this._Z0 = _Z0;
	}


	public void set_port(String _port) {
		this._port = _port;
	}


	public void set_Metric1(int _Metric1) {
		this._Metric1 = _Metric1;
	}


	public void set_Metric2(int _Metric2) {
		this._Metric2 = _Metric2;
	}


	public void set_Metric3(int _Metric3) {
		this._Metric3 = _Metric3;
	}


	public void set_Metric4(int _Metric4) {
		this._Metric4 = _Metric4;
	}


	public void set_Z0(double _Z0) {
		this._Z0 = _Z0;
	}


	public boolean isValid()
	{
		if ((_port != null) && (_Metric1 > -1) && (_Metric2 > -1) && (_Metric3 > -1) && (_Metric4 > -1) && (_Z0 > -1))
			return true;
		return false;
	}
	
	public int AddInCoef() 
	{
		if (!this.isValid())
			return -1;
		
		Connection conn = _tool.connect();
		if (conn == null)
			return -1;

		try 
		{
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery("select uid from maree_coeff where (port = '" + _port + "')");
			int uid = _tool.parseuidResultSet(resultSet);

			if (uid < 0)
			{
				String SQLmsg = "insert into  maree_coeff (port, type, Metric1, Metric2, Metric3, Metric4, Z0) values (?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement preparedStatement = conn.prepareStatement(SQLmsg);
				preparedStatement.setString(1, _port);
				preparedStatement.setString(2, _type);
				preparedStatement.setInt(3, _Metric1);
				preparedStatement.setInt(4, _Metric2);
				preparedStatement.setInt(5, _Metric3);
				preparedStatement.setInt(6, _Metric4);
				preparedStatement.setDouble(7, _Z0);
				preparedStatement.executeUpdate();		
				
				
				resultSet = statement.executeQuery("select uid from maree_coeff where (port = '" + _port + "')");
				uid = _tool.parseuidResultSet(resultSet);
			}
			return uid;
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
