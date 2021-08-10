package sfa.dev.maree.sql.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class table_maree_unhoraireMaree extends table 
{
	/*
		+-----------------------+------------------+------+-----+---------+----------------+
		| Field                 | Type             | Null | Key | Default | Extra          |
		+-----------------------+------------------+------+-----+---------+----------------+
		| uid                   | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
		| heure                 | time             | YES  |     | NULL    |                |
		| hauteur               | double           | YES  |     | NULL    |                |
		| coef                  | tinyint(4)       | YES  |     | NULL    |                |
		| type                  | enum('BM','PM')  | YES  |     | NULL    |                |
		| uid_tablehoraireMaree | int(11)          | YES  |     | NULL    |                |
		+-----------------------+------------------+------+-----+---------+----------------+	 
	 */

	static final  private String nomTable = "maree_unhoraireMaree";
	static final private String[] arg = {
			"uid 			int unsigned auto_increment NOT NULL PRIMARY KEY",
			"heure                 time",
			"hauteur               double",
			"coef                  tinyint",
			"type                  enum('BM','PM')",
			"uid_tablehoraireMaree int unsigned not null"
		};

	private Time _heure  = null;
	private double _hauteur  = 0.0;
	private int _coef  = 0;
	private String _type  = null;
	private int _uid_tablehoraireMaree = 0;
	
	public table_maree_unhoraireMaree ()
	{
		super(nomTable, arg);		
	}
	
	public void set_heure(Time _heure) {
		this._heure = _heure;
	}

	public void set_hauteur(double _hauteur) {
		this._hauteur = _hauteur;
	}

	public void set_coef(int _coef) {
		this._coef = _coef;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	public void set_uid_tablehoraireMaree(int _uid_tablehoraireMaree) {
		this._uid_tablehoraireMaree = _uid_tablehoraireMaree;
	}






	public int AddInCoef() 
	{
		Connection conn = _tool.connect();
		if (conn == null)
			return -1;

		try 
		{
			String SQLmsg = "select uid from "+nomTable+" where ((heure = ?) and (uid_tablehoraireMaree = ?))";
			PreparedStatement preparedStatement = conn.prepareStatement(SQLmsg);
			preparedStatement.setTime(1, _heure);
			preparedStatement.setInt(2, _uid_tablehoraireMaree);
			ResultSet resultSet = preparedStatement.executeQuery();	
			int uid = _tool.parseuidResultSet(resultSet);

			if (uid < 0)
			{
				SQLmsg = "insert into "+nomTable+" (heure, hauteur, coef, type, uid_tablehoraireMaree) values (?, ?, ?, ?, ?)";
				preparedStatement = conn.prepareStatement(SQLmsg);
				preparedStatement.setTime(1, _heure);
				preparedStatement.setDouble(2, _hauteur);
				System.out.println(_coef);
				preparedStatement.setInt(3, _coef);
				preparedStatement.setString(4, _type);
				preparedStatement.setDouble(5, _uid_tablehoraireMaree);
				preparedStatement.executeUpdate();		
				
				
				SQLmsg = "select uid from "+nomTable+" where ((heure = ?) and (uid_tablehoraireMaree = ?))";
				preparedStatement = conn.prepareStatement(SQLmsg);
				preparedStatement.setTime(1, _heure);
				preparedStatement.setInt(2, _uid_tablehoraireMaree);
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

	public double get_hauteur() {
		return _hauteur;
	}	
}
