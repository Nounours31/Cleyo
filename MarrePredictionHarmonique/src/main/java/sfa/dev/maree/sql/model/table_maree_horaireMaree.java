package sfa.dev.maree.sql.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class table_maree_horaireMaree extends table 
{
	/*
		+-------+------------------+------+-----+---------+----------------+
		| Field | Type             | Null | Key | Default | Extra          |
		+-------+------------------+------+-----+---------+----------------+
		| uid   | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
		| jour  | date             | YES  |     | NULL    |                |
		| uidport  | int             | YES  |     | NULL    |                |
		+-------+------------------+------+-----+---------+----------------+	 
	 */
	static final  private String nomTable = "maree_horaireMaree";
	static final private String[] arg = {
			"uid 		int unsigned auto_increment NOT NULL PRIMARY KEY",
			"jour 		date",
			"uidport 		int"
		};

	private int _port  = 0;
	private java.sql.Date _jour  = null;
	
	public table_maree_horaireMaree ()
	{
		super(nomTable, arg);		
	}
	

	
	public void set_port(int _port) {
		this._port = _port;
	}
	public int get_port() {
		return this._port;
	}

	
	public void set_jour(Date _jour) {
		this._jour = _jour;
	}





	public int AddInCoef() 
	{
		Connection conn = _tool.connect();
		if (conn == null)
			return -1;

		try 
		{
			String SQLmsg = "select uid from "+nomTable+" where ((uidport = ?) and (jour = ?))";
			PreparedStatement preparedStatement = conn.prepareStatement(SQLmsg);
			preparedStatement.setInt(1, _port);
			preparedStatement.setDate(2, _jour);
			ResultSet resultSet = preparedStatement.executeQuery();	
			int uid = _tool.parseuidResultSet(resultSet);

			if (uid < 0)
			{
				SQLmsg = "insert into "+nomTable+" (uidport, jour) values (?, ?)";
				preparedStatement = conn.prepareStatement(SQLmsg);
				preparedStatement.setInt(1, _port);
				preparedStatement.setDate(2, _jour);
				preparedStatement.executeUpdate();		
				
				
				SQLmsg = "select uid from "+nomTable+" where ((uidport = ?) and (jour = ?))";
				preparedStatement = conn.prepareStatement(SQLmsg);
				preparedStatement.setInt(1, _port);
				preparedStatement.setDate(2, _jour);
				resultSet = preparedStatement.executeQuery();	
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
