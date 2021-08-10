package sfa.dev.maree.sql.model;

import java.util.GregorianCalendar;


public class DateHauteurSQL implements Comparable<DateHauteurSQL>
{
	java.sql.Date _jour;
	java.sql.Time _horaire;
	double _hauteur;
	String _type = "";
	
	public DateHauteurSQL(double heure, double hauteur, String string, GregorianCalendar gc)
	{
		String retour = "";
		String Heure = Integer.toString((int)heure);
		if (Heure.length() != 2)
			Heure = "0" + Heure;
		retour += Heure + ":";
		
		heure -= (int)heure;
		heure *= 60.0;
		
		Heure = Integer.toString((int)heure);
		if (Heure.length() != 2)
			Heure = "0" + Heure;
		retour += (Heure + ":00");
		

		_horaire = java.sql.Time.valueOf(retour);
		_hauteur = hauteur;
		_type = string;
		_jour = new java.sql.Date (gc.getTimeInMillis());
	}


	@Override
	public int compareTo(DateHauteurSQL p) {
		if (this._jour.equals(p._jour))
			return this._horaire.compareTo(p._horaire);
		return this._jour.compareTo(p._jour);
	}
}
