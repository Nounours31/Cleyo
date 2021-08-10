package sfa.dev.maree.sql.model;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class DateHauteurSQLForMareeCalcul 
{
	static List<DateHauteurSQL> _data = new ArrayList<DateHauteurSQL>();
	
	
	static public void add(List<Double> pM, List<Double> bM, GregorianCalendar gc) 
	{
		List<DateHauteurSQL> data = new ArrayList<DateHauteurSQL>();
		
		for (int i = 0; i < pM.size();)
		{
			DateHauteurSQL x = new DateHauteurSQL (pM.get(i++), pM.get(i++), "PM", gc); // horaire , amplitude
			data.add (x);
		}
		for (int i = 0; i < bM.size();)
		{
			DateHauteurSQL x = new DateHauteurSQL (bM.get(i++), bM.get(i++), "BM", gc); // horaire , amplitude
			data.add (x);
		}
		_data.addAll(data);
		Collections.sort(_data);
		
		while (_data.size() > 12)
			_data.remove(0);
	}
	
	
	
	static public int nbInfoMaree(GregorianCalendar gc) 
	{
		Date jour = new java.sql.Date (gc.getTimeInMillis());
		int nbinfo = 0;
		for (int i = 0; i < _data.size(); i++ )
			if (_data.get(i)._jour.equals(jour))
				nbinfo  ++;
		return nbinfo;
	}
	
	static public Time getHeure(int indice, GregorianCalendar gc) 
	{
		if (DateHauteurSQLForMareeCalcul.nbInfoMaree(gc) < indice)
			return null;
		
		List<DateHauteurSQL> data = new ArrayList<DateHauteurSQL>();
		Date jour = new java.sql.Date (gc.getTimeInMillis());
		for (int i = 0; i < _data.size(); i++ )
			if (_data.get(i)._jour.equals(jour))
				data.add(_data.get(i));
		return data.get(indice)._horaire;
		
	}

	static public double getHauteur(int indice, GregorianCalendar gc) 
	{
		if (DateHauteurSQLForMareeCalcul.nbInfoMaree(gc) < indice)
			return -1.0;
		
		List<DateHauteurSQL> data = new ArrayList<DateHauteurSQL>();
		Date jour = new java.sql.Date (gc.getTimeInMillis());
		for (int i = 0; i < _data.size(); i++ )
			if (_data.get(i)._jour.equals(jour))
				data.add(_data.get(i));
		return data.get(indice)._hauteur;
	}
	
	static public int getCoef(int indice, GregorianCalendar gc) 
	{
		if (DateHauteurSQLForMareeCalcul.nbInfoMaree(gc) < indice)
			return 0;
		
		List<DateHauteurSQL> data = new ArrayList<DateHauteurSQL>();
		Date jour = new java.sql.Date (gc.getTimeInMillis());
		
		for (int i = 0; i < _data.size(); i++ )
			if (_data.get(i)._jour.equals(jour))
				data.add(_data.get(i));
		
		if (data.get(indice)._type.equals("BM"))
			return 0;
		
		double hBM = 0;
		if (indice == 0)
		{
			List<DateHauteurSQL> dataprev = new ArrayList<DateHauteurSQL>();
			Date jour2 = new java.sql.Date (gc.getTimeInMillis() - (24 * 60 * 60 * 1000));
			
			for (int i = 0; i < _data.size(); i++ )
				if (_data.get(i)._jour.equals(jour2))
					dataprev.add(_data.get(i));
			
			hBM = dataprev.get(dataprev.size() - 1)._hauteur;
		}
		else
			hBM = data.get(indice - 1)._hauteur;
		
		double hPM = data.get(indice)._hauteur;
		
		return (int)((hPM - hBM) / 0.061);
	}
	
	
	static public String getType(int indice, GregorianCalendar gc) 
	{
		if (DateHauteurSQLForMareeCalcul.nbInfoMaree(gc) < indice)
			return null;
		
		List<DateHauteurSQL> data = new ArrayList<DateHauteurSQL>();
		Date jour = new java.sql.Date (gc.getTimeInMillis());
		for (int i = 0; i < _data.size(); i++ )
			if (_data.get(i)._jour.equals(jour))
				data.add(_data.get(i));
		return data.get(indice)._type;
	}
}
