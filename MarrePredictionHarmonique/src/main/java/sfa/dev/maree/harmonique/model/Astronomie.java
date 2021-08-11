package sfa.dev.maree.harmonique.model;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import sfa.dev.generique.tools.E4ALogger;



public class Astronomie 
{
	public double s;       	        // mean longitude of moon
	public double h;		    	// mean longitude of sun
	public double p;			// longitude of lunar perigee
	public double p1;			// longitude of solar perigee
	public double N;			// longitude of moon's node
	private E4ALogger _log = E4ALogger.getLogger(Astronomie.class.getCanonicalName());
	public double heureDecimale;
	private static GregorianCalendar StartofReform = Astronomie.InitReform();

	static private GregorianCalendar InitReform() {
		GregorianCalendar rc = new GregorianCalendar(1582, Calendar.OCTOBER, 15, 0, 0, 0); 
		rc.setTimeZone(TimeZone.getTimeZone("UTC"));
		return rc;
	}


	public Astronomie(long epoch) 
	{
		s = h = p = p1 = N = 0.0;		
		InitAstroInfo(epoch);
	}


	//---------------------------------------------------------------------------
	// tous les angles sont exprimes en degres, sauf I qui est en radians
	// les formules sont extraites de ASTRONOMICAL ALGORITHMS par Jean Meeus
	// les références des formules entre parenthèses ainsi que les numéros
	// de page se rapportent à cet ouvrage.
	//---------------------------------------
	// http://jday.sourceforge.net/index.php
	private void InitAstroInfo(long epoch)
	{ 
		double jourDecimal = 0.0;
		double tempsDecimal2000 = 0.0;
		double tempsDecimal2000Carre, tempsDecimal2000Cube, tempsDecimal2000Quadratique;
		double M;                     // mean anomaly of the Sun	

		jourDecimal = Epoch2JourDecimal (epoch);

		//------- Find the time T measured in Julian centuries from the epoch J2000 (JDE 2451545.0) ----- (21.1)
		tempsDecimal2000 			= (jourDecimal - 2451545.0) / 36525.;         
		tempsDecimal2000Carre 		= tempsDecimal2000  * tempsDecimal2000;
		tempsDecimal2000Cube 		= tempsDecimal2000Carre * tempsDecimal2000;
		tempsDecimal2000Quadratique = tempsDecimal2000Cube * tempsDecimal2000;

		// ----------------- Moon's mean longitude --------------------- (45.1)
		s = Trigo.reduc360( 218.3164591 + 481267.88134236 * tempsDecimal2000  - 0.0013268 * tempsDecimal2000Carre + tempsDecimal2000Cube / 538841. - tempsDecimal2000Quadratique / 65194000.);

		// ------------------ Sun's mean longitude ----( )-------------- (24.2)
		h = Trigo.reduc360(280.46645 + 36000.76983 * tempsDecimal2000 + 0.0003032 * tempsDecimal2000Carre);

		// ------------- longitude of lunar perigee -------------------- (45.7)
		p = Trigo.reduc360(83.3532430 + 4069.0137111 * tempsDecimal2000 - 0.0103238 * tempsDecimal2000Carre - tempsDecimal2000Cube / 80053. + tempsDecimal2000Quadratique / 18999000.);

		// ---------------- mean anomaly of the Sun -------------------- (24.3)
		M = 357.52910 + 35999.05030 * tempsDecimal2000 - 0.0001559 * tempsDecimal2000Carre - 0.00000048 * tempsDecimal2000Cube;

		// ------------- longitude du perigee du Soleil ----------------
		p1 = Trigo.reduc360 (h - M);

		// --- longitude of the ascending node of the Moon's orbit ----- page 132 (juste apres 21.1)
		N = Trigo.reduc360(125.04452 - 1934.136261 * tempsDecimal2000 + 0.0020708 * tempsDecimal2000Carre + tempsDecimal2000Cube / 450000.);	
		
		//------- heure decimale du jour
		heureDecimale = Epoch2HeureDecimalDuJour(epoch);
	}


	public double Epoch2HeureDecimalDuJour (long epochMaree) 
	{		
		GregorianCalendar start = new GregorianCalendar (TimeZone.getTimeZone("UTC"));
		start.setTimeInMillis(epochMaree);

		double HeureDecimale = (double)start.get (Calendar.SECOND);
		HeureDecimale = (double) (HeureDecimale / 60.0 + (double)(start.get (Calendar.MINUTE)));
		HeureDecimale = (double) (HeureDecimale / 60.0 + (double)(start.get (Calendar.HOUR_OF_DAY)));
		return HeureDecimale;
	}

	public double Epoch2JourDecimal (long epochMaree) 
	{		
		GregorianCalendar start = new GregorianCalendar (TimeZone.getTimeZone("UTC"));
		start.setTimeInMillis(epochMaree);

		int Y = start.get (Calendar.YEAR);
		int M = start.get (Calendar.MONTH) + 1;
		double D = start.get (Calendar.DAY_OF_MONTH);
		double HeureDecimale = (double)start.get (Calendar.SECOND);
		HeureDecimale = (double) (HeureDecimale / 60.0 + (double)(start.get (Calendar.MINUTE)));
		HeureDecimale = (double) (HeureDecimale / 60.0 + (double)(start.get (Calendar.HOUR_OF_DAY)));
		HeureDecimale = (double) (HeureDecimale / 24.0);
		D += HeureDecimale;		
		if (M < 3)
		{
			Y = Y - 1;
			M = M + 12;
		}

		int A = (int) (((double)Y) / 100.);
		int B = 2 - A + (int) (((double)A) / 4.);

		if (start.before(StartofReform))
			B = 0;

		_log.debug("D = " + D);
		_log.debug("M = " + M);
		_log.debug("Y = " + Y);
		_log.debug("A = " + A);
		_log.debug("B = " + B);

		double jourDecimal = (double) ((int)(365.25 * (Y + 4716)) + (int)(30.6001 * (M + 1)) + D + B - 1524.5);
		return jourDecimal;
	}

	public long JourDecimal2Epoch (double JD) 
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

		return retour.getTimeInMillis();
	}
}
