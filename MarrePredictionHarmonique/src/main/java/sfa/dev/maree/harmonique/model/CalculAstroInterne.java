package sfa.dev.maree.harmonique.model;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import sfa.dev.maree.tools.SfaLogger;



public class CalculAstroInterne 
{
	public double s;       	        // mean longitude of moon
	public double h;		    	// mean longitude of sun
	public double p;			// longitude of lunar perigee
	public double p1;			// longitude of solar perigee
	public double N;			// longitude of moon's node
	public double date_astro;
	private static SfaLogger _log = SfaLogger.getLogger(CalculAstroInterne.class.getCanonicalName());
	private GregorianCalendar _gc = null;
	
	public CalculAstroInterne(double _date_astro, GregorianCalendar gc) 
	{
		
		s = h = p = p1 = N = 0.0;
		date_astro = _date_astro;
		_gc = gc;
		
		astronomie();
	}


	//---------------------------------------------------------------------------
	// tous les angles sont exprimes en degres, sauf I qui est en radians
	// les formules sont extraites de ASTRONOMICAL ALGORITHMS par Jean Meeus
	// les références des formules entre parenthèses ainsi que les numéros
	// de page se rapportent à cet ouvrage.
	//---------------------------------------
	// http://jday.sourceforge.net/index.php
	private void astronomie()
	{ 
		
		Date date_astro_java = null;
		double JD = 0.0;
		double deltaT = 0.0;
		double TE = 0.0;
		
		final boolean CLEYO_NOUVEAUCODE = true;
		String msg = "";
		
		if (CLEYO_NOUVEAUCODE)
		{
			msg = "Nouveau Code";
			double TE2, TE3, TE4;
			double M;                     // mean anomaly of the Sun	

			JD = calculJDFromGregorian (_gc);
	
			//------- Find the time T measured in Julian centuries from the epoch J2000 (JDE 2451545.0) ----- (21.1)
			TE 		= (JD - 2451545.0) / 36525.;         
			TE2 	= TE  * TE;
			TE3 	= TE2 * TE;
			TE4 	= TE3 * TE;
	
			// ----------------- Moon's mean longitude --------------------- (45.1)
			s = TrigoEtConstante.reduc360( 218.3164591 + 481267.88134236 * TE  - 0.0013268 * TE2 + TE3 / 538841. - TE4 / 65194000.);

			// ------------------ Sun's mean longitude ----( )-------------- (24.2)
			h = TrigoEtConstante.reduc360(280.46645 + 36000.76983 * TE + 0.0003032 * TE2);
	
			// ------------- longitude of lunar perigee -------------------- (45.7)
			p = TrigoEtConstante.reduc360(83.3532430 + 4069.0137111 * TE - 0.0103238 * TE2 - TE3 / 80053. + TE4 / 18999000.);
	
			// ---------------- mean anomaly of the Sun -------------------- (24.3)
			M = 357.52910 + 35999.05030 * TE - 0.0001559 * TE2 - 0.00000048 * TE3;
	
			// ------------- longitude du perigee du Soleil ----------------
			p1 = TrigoEtConstante.reduc360 (h - M);
	
			// --- longitude of the ascending node of the Moon's orbit ----- page 132 (juste apres 21.1)
			N = TrigoEtConstante.reduc360(125.04452 - 1934.136261 * TE + 0.0020708 * TE2 + TE3 / 450000.);	

			date_astro_java = _gc.getTime();
		}
		else
		{
			msg = "Ancien Code";
			double J2000  = 2451545.0;  // julian date 1st january 2000 12h
			double JDE;
			double T, TE2, TE3, TE4;
			double M;                     // mean anomaly of the Sun
	
			// la partie entière d'une date (classe TDateTime) est le nombre de jours
			// écoulés depuis le 30/12/1899 (Julian day 2415018.5)
			JD = date_astro + 2415018.5;
	
			//----------------------------------------------------------------------------------------
			// pfs from c to Java
			// Date(long date)
			// Allocates a Date object and initializes it to represent the specified number of milliseconds 
			// since the standard base time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
			//---------------------------------------------------------------------------------------
			date_astro_java = new Date((long)(JD * TrigoEtConstante.DAY_TO_MILLISEC));
	
			T 		= (JD - J2000) / 36525.;                               // JULIAN DATE
			deltaT 	= 65;
			JDE 	= JD + deltaT/86400.; // 86400 c'est le nb seconde par jour 24 * 60 * 60
			TE  	= (JDE - J2000) / 36525.;
			TE2 	= TE  * TE;
			TE3 	= TE2 * TE;
			TE4 	= TE3 * TE;
	
			// ----------------- Moon's mean longitude --------------------- (45.1)
			s = TrigoEtConstante.reduc360( 218.3164591 + 481267.88134236 * TE  - 0.0013268 * TE2 + TE3 / 538841. - TE4 / 65194000.);
			// ------------------ Sun's mean longitude ----( )-------------- (24.2)
			h = TrigoEtConstante.reduc360(280.46645 + 36000.76983 * TE + 0.0003032 * TE2);
	
			// ------------- longitude of lunar perigee -------------------- (45.7)
			p = TrigoEtConstante.reduc360(83.3532430 + 4069.0137111 * TE - 0.0103238 * TE2 - TE3 / 80053. + TE4 / 18999000.);
	
			// ---------------- mean anomaly of the Sun -------------------- (24.3)
			M = 357.52910 + 35999.05030 * TE - 0.0001559 * TE2 - 0.00000048 * TE3;
	
			// ------------- longitude du perigee du Soleil ----------------
			p1 = TrigoEtConstante.reduc360 (h - M);
	
			// --- longitude of the ascending node of the Moon's orbit ----- page 132
			N = TrigoEtConstante.reduc360(125.04452 - 1934.136261 * TE + 0.0020708 * TE2 + TE3 / 450000.);
	
			// formules page 116 de "la Marée Océanique Côtière" par Bernard Simon.
			/* s  = 218.3165 + 481267.8804*TE - 0.0016*TE2;
		  h  = 280.4661 +  36000.7698*TE + 0.0003*TE2;
		  p  =  83.3535 +   4069.0215*TE - 0.103*TE2;
		  N  = 234.555  +   1934.1363*TE + 0.0021*TE2;
		  N  = -N;
		  p1 = 282.9384 +      1.7195*TE + 0.0005*TE2;
			 */
	
			// formules de Tamura
			/*
		  s = 218.316656 + 481267.881342*TE - 0.001330*TE2  + 0.0040*cos_deg(133.*TE + 29.);
		  h = 280.466449 +  36000.769822*TE + 0.0003036*TE2 + 0.0018*cos_deg(19.*TE + 159.);
		  p = 83.353243  +   4069.013711*TE - 0.010324*TE2;
		  N = 234.955444 +   1934.136185*TE - 0.002076*TE2;
		  N=-N;
		  p1 = 282.937348 +      1.719533*TE + 0.0004597*TE2;
			 */
		}
		
		//HTMLLogForDebug.AddAstroInHTMLLog (date_astro_java, JD, deltaT, TE, s, h, p, p1, N);
		_log.debug(msg);
		_log.debug("date_astro = " + date_astro);
		_log.debug("date_astro_java = " + date_astro_java);
		_log.debug("JD = " + JD);
		_log.debug("deltaT = " + deltaT);
		_log.debug("T = " + TE);
	}

	
	public static double calculJDFromGregorian (GregorianCalendar Start) 
	{
		GregorianCalendar StartofReform = new GregorianCalendar(1582, Calendar.OCTOBER, 15, 0, 0, 0); 
		int Y = Start.get (Calendar.YEAR);
		int M = Start.get (Calendar.MONTH) + 1;
		double D = Start.get (Calendar.DAY_OF_MONTH);
		double HeureDecimale = (double)Start.get (Calendar.SECOND);
		HeureDecimale = (double) (HeureDecimale / 60.0 + (double)(Start.get (Calendar.MINUTE)));
		HeureDecimale = (double) (HeureDecimale / 60.0 + (double)(Start.get (Calendar.HOUR_OF_DAY)));
		HeureDecimale = (double) (HeureDecimale / 24.0);
		D += HeureDecimale;		
		if (M < 3)
		{
			Y = Y - 1;
			M = M + 12;
		}
		
		int A = (int) (((double)Y) / 100.);
		int B = 2 - A + (int) (((double)A) / 4.);
		
		if (Start.before(StartofReform))
			B = 0;

		_log.debug("D = " + D);
		_log.debug("M = " + M);
		_log.debug("Y = " + Y);
		_log.debug("A = " + A);
		_log.debug("B = " + B);
				
		double JD = (double) ((int)(365.25 * (Y + 4716)) + (int)(30.6001 * (M + 1)) + D + B - 1524.5);
		return JD;
	}

	public static GregorianCalendar calculGregorianFromJD (double JD) 
	{
		double _Z = JD + 0.5;
		int Z = (int)_Z;
		double F = _Z - Z;
	
		int A = 0;
		int Alpha = 0;
		
		if (Z < 2299161)
			A = Z;
		else
		{
			Alpha = (int)((Z - 1867216.25) / 36524.25);
			A = Z + 1 + Alpha - (int) (Alpha / 4.0); 
		}
		
		int B = A + 1524;
		int C = (int)((B - 122.1) / 365.25);
		int D = (int)(365.25 * C);
		int E = (int)((B - D) / 30.6001);
		
		double Jour = B - D - (int)(30.6001 * E) + F;
		int Mois = E - 1;
		if (Mois >= 13)
			Mois -= 12;
		int Year = C - 4716;
		if (Mois <= 2)
			Year++;
		
		_log.debug("Jour = " + Jour);
		_log.debug("Mois = " + Mois);
		_log.debug("Year = " + Year);
		
		double _Heure = (Jour - (int)Jour) * 24.0;
		int Heure = (int)(_Heure);
		
		double _Minute = (_Heure - Heure) * 60.0;
		int Minute = (int)(_Minute);
		
		double _Seconde = (_Minute - Minute) * 60.0;
		int Seconde = (int)(_Seconde);
		
		GregorianCalendar retour = new GregorianCalendar(Year, Mois - 1, (int)Jour, Heure, Minute, Seconde);
		_log.debug("GregorianCalendar = " + retour);
		
		return retour;
	}
}
