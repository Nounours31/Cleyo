package sfa.dev.maree.sql.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class table_maree_uncoeff extends table 
{
	/*
		+---------------+----------+------+-----+---------+----------------+
		| Field         | Type     | Null | Key | Default | Extra          |
		+---------------+----------+------+-----+---------+----------------+
		| uid           | int(11)  | NO   | PRI | NULL    | auto_increment |
		| nom           | tinytext | YES  |     | NULL    |                |
		| amplitude     | double   | YES  |     | NULL    |                |
		| phase         | double   | YES  |     | NULL    |                |
		| uid_tablecoef | int(11)  | YES  |     | NULL    |                |
		+---------------+----------+------+-----+---------+----------------+
	 */
	static final  private String nomTable = "maree_uncoeff";
	static final private String[] arg = {
			"uid 			int unsigned auto_increment NOT NULL PRIMARY KEY",
			"nom            tinytext",
			"amplitude      double",
			"phase          double",
			"uid_tablecoef  int unsigned not null",

		};

	private String _nom = null;
	private double _amplitude = -1.0;
	private double _phase = -1.0;
	private int _uid_tablecoef = 0;
	
	
	public table_maree_uncoeff ()
	{
		super(nomTable, arg);
	}
	
	
	
	
	public table_maree_uncoeff(String _nom, double _amplitude, double _phase, int _uid_tablecoef) 
	{
		super(nomTable, arg);
		this._nom = _nom;
		this._amplitude = _amplitude;
		this._phase = _phase;
		this._uid_tablecoef = _uid_tablecoef;
	}

	
	



	public void set_nom(String _nom) {
		this._nom = _nom;
	}

	public void set_amplitude(double _amplitude) {
		this._amplitude = _amplitude;
	}

	public void set_phase(double _phase) {
		this._phase = _phase;
	}

	public void set_uid_tablecoef(int _uid_tablecoef) {
		this._uid_tablecoef = _uid_tablecoef;
	}




	public boolean AddInUnCoef() 
	{
		Connection conn = _tool.connect();
		if (conn == null)
			return false;

		try {
			String SQLmsg = "insert into  maree_uncoeff (nom, amplitude, phase, uid_tablecoef) values (?, ?, ?, ?)";
			PreparedStatement preparedStatement = conn.prepareStatement(SQLmsg);
			preparedStatement.setString(1, _nom);
			preparedStatement.setDouble(2, _amplitude);
			preparedStatement.setDouble(3, _phase);
			preparedStatement.setInt(4, _uid_tablecoef);
			preparedStatement.executeUpdate();		
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			_tool.close(conn);
		}
		return true;
	}	
}
