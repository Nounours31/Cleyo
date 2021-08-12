package sfa.dev.maree.harmonique.model.computeportmaree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sfa.dev.generique.tools.E4AException;
import sfa.dev.generique.tools.E4ALogger;
import sfa.dev.maree.harmonique.model.Astronomie;
import sfa.dev.maree.harmonique.model.Onde;
import sfa.dev.maree.harmonique.model.Ondes;
import sfa.dev.maree.harmonique.model.Trigo;
import sfa.dev.maree.tools.MareeEnv;


public class MareeTools {
	class HarmoniqueInfo {
		double amplitude, phase;
	}
	private static int _portIDInit = -1;
	E4ALogger _logger = E4ALogger.getLogger(MareeTools.class.getCanonicalName());
	
	
	// --------------------------------------------------------
	// hauteur d'eau a une heure donnee
	// --------------------------------------------------------
	public double ComputeHauteurMaree(long heure, int portId) throws IOException, E4AException {
		_logger.debug(String.format("ComputeHauteurMaree - Start epoch: %d [heure CET: %s] - port: %d [port static: %d]", heure, MareeEnv._sdfUI.format(new Date(heure)), portId, _portIDInit));
		if (!isHarmonicInitDone(portId))
			initHarmonique (portId);
		
		// Calcul du Z0 du port
		double hauteur = Ondes._Z0;

		Astronomie astrePosition = new Astronomie(heure);
		Ondes.InitEquilibriumAndPhase (astrePosition.s, astrePosition.h, astrePosition.p, astrePosition.p1, astrePosition.N);
		double heureDecimal = astrePosition.heureDecimale;

		double var;
		for (Onde o : Ondes._table2NC)
		{
			if (o._ampli > 0.)
			{ 
				var = Trigo.reduc360 (o._speed * heureDecimal + o._equilibrium - o._phase);
				hauteur += o._nodeFactor * o._ampli * Trigo.cos_deg(var);
			}
		}
		_logger.debug("ComputeHauteurMaree - h:" + hauteur);
		return hauteur;
	}
	
	// -------------------
	// idem mais en lot
	// -------------------
	public List<HoraireMaree> getHauteurEau(List<Long> horaire, int portId) throws IOException, E4AException
	{
		ArrayList<HoraireMaree> retour = new ArrayList<HoraireMaree>();	
		for (long h : horaire) 
		{
			double a = this.ComputeHauteurMaree(h, portId);
			HoraireMaree hm = new HoraireMaree(h, a);
			retour.add(hm);
		}
		return retour;
	}
	
	
	
	
	// --------------------------------------------------------
	// Besoin de la derivee pour la recherche des etales
	// --------------------------------------------------------
	// -----------------------------------------
	// le 2021-08-12 : pb de precision au passage de minuit dans la calcul de la derivee
	// Pour eviter ce pb, je met un local offset autour de minuit ...
	// il faut envoye la date (du jour courant + offset (< 0 pour passer au jour precedent) (> 0 jour suivant)
	// en conservant le jour de ref pour etre Continue dans les calcul de la derivee
	// ne faire cela que +/- 1/2 heure de minuit ....
	// -----------------------------------------
	private boolean signe_derivee(long heure, double localOffsetEnHeureDecimale, int portId) throws IOException, E4AException
	{ 
		_logger.debug(String.format("signe_derivee - Start epoch: %d [heure CET: %s] - port: %d [port static: %d]", heure, MareeEnv._sdfUI.format(new Date(heure)), portId, _portIDInit));
		if (!isHarmonicInitDone(portId))
			initHarmonique (portId);
		
		Astronomie astrePosition = new Astronomie(heure);
		Ondes.InitEquilibriumAndPhase (astrePosition.s, astrePosition.h, astrePosition.p, astrePosition.p1, astrePosition.N);
		double heureDecimal = astrePosition.heureDecimale;

		// -----------------------------------------
		// le 2021-08-12 : cf commentaire
		// -----------------------------------------
		heureDecimal += localOffsetEnHeureDecimale;
		
		double sens = 0.;
		double var;

		for (Onde o : Ondes._table2NC) {			
			if (o._ampli > 0.)
			{ 
				// la deriv�e est de la forme -vsin(vt-p)
				var = Trigo.reduc360 (o._speed * heureDecimal + o._equilibrium - o._phase);
				sens -= o._nodeFactor * o._ampli * o._speed * Trigo.sin_deg(var);
			}
		}
		
		// return 1 si �tale ou mar�e montante, 0 si mar�e descendante
		boolean bSens = (sens >= 0.);
		_logger.debug("Sens: " + (bSens ? "Montante" : "Descendante") + " - Gradiant: " + sens);
		return bSens;
	}
	
	
	
	
	// --------------------------------------------------------
	// recup de la sinusoide de l ajournee (debug ?)
	// --------------------------------------------------------
	public List<HoraireMaree> MarreGrammeduJour(long heure, int portId) throws IOException, E4AException
	{
		List<Long> horaires = new ArrayList<Long>();
		GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		gc.setTimeInMillis(heure);
		for (int i = 0; i < 24; i++) {
			gc.add(Calendar.HOUR, 1);
			horaires.add (gc.getTimeInMillis());			
		}
			
		List<HoraireMaree> retour = this.getHauteurEau(horaires, portId);
		return retour;
	}


	
	
	
	// --------------------------------------------------------
	// Recherche a partir d'un horaire de la prochaine heure ou la maree va s'inverser
	// --------------------------------------------------------
	private long heureEtaleDeMaree(long EpochDebutDerecherche, int portId) throws IOException, E4AException // heure de la mar�e en heures de 0.0 a 23.99
	{ 
		_logger.debug(String.format("heureEtaleDeMaree - Start epoch: %d [heure CET: %s] - port: %d [port static: %d]", EpochDebutDerecherche, MareeEnv._sdfUI.format(new Date(EpochDebutDerecherche)), portId, _portIDInit));
		/* 
		 la hauteur de la maree est la fonction somme des composants,
	     chaque composant �tant de la forme acos(vt-p).
	     La fonction d�riv�e =-vsin(vt-p) s'annule lorsque la fonction
	     passe par un maxi (pleine mer) ou un mini (basse mer). Elle est
	     positive lorsque la mar�e monte, et n�gative lorsque la mar�e
	     descend. Plut�t que de rechercher les racines de la fonction d�riv�e,
	     on a trouv� plus simple de proc�der par approches successives.
		 */

		long t, t_next, pas; 
		long dh = Trigo.DemiHeureEpoch; // une demi-heure en epoch
		// sens de la maree 1=maree montante 0=maree descendante
		Boolean sens, sens0;

		
		//------------------------------------------------------------------------------
		// est ce que je ne serais pas deja a l'etale ?
		//------------------------------------------------------------------------------
		sens0 = signe_derivee(EpochDebutDerecherche, 0.0, portId);	    // sens de la mar�e a l'instant initial

		// --------------
		// pb du passage de minuit
		// --------------
		double localOffsetEnHeureDecimale = 0.0;
		if (Astronomie.isEpochJourDifferent (EpochDebutDerecherche, EpochDebutDerecherche - Trigo.DemiMinuteEpoch)) {
			localOffsetEnHeureDecimale = -(((double)Trigo.DemiMinuteEpoch) * Trigo.MilliSecEpoch2HeureDecimale);
		}
		else {
			localOffsetEnHeureDecimale = 0.0;
			EpochDebutDerecherche = EpochDebutDerecherche - Trigo.DemiMinuteEpoch;
		}
		
		if (signe_derivee(EpochDebutDerecherche, localOffsetEnHeureDecimale, portId) !=  sens0) {
			_logger.debug(String.format("Etale trouvee [-Directe-] a epoch: %d [heure CET: %s]", EpochDebutDerecherche - Trigo.DemiMinuteEpoch, MareeEnv._sdfUI.format(new Date(EpochDebutDerecherche - Trigo.DemiMinuteEpoch))));
			return EpochDebutDerecherche; // si sens inverse c'est que je l'ai trouvee
		}

		
		//-----------------------------------------------------------------------------
		// non alors je mouline de 1/2 heure en 1/2 heure jusqu'a l'etale
		//-----------------------------------------------------------------------------
		t = EpochDebutDerecherche;			                // on part de l'instant initial
		t_next = EpochDebutDerecherche;			                // on part de l'instant initial
		do                            // on va regarder
		{ 
			t_next = t + dh;		            // les demi-heures suivantes
			localOffsetEnHeureDecimale = 0.0;
			if (Astronomie.isEpochJourDifferent (t, t_next)) {
				localOffsetEnHeureDecimale = (((double)dh) * Trigo.MilliSecEpoch2HeureDecimale);
			}
			else {
				localOffsetEnHeureDecimale = 0.0;
				t = t_next;
			}
			sens = signe_derivee(t, localOffsetEnHeureDecimale, portId);    // quel est le sens de la mar�e
			
			// continuer a avancer
			if (localOffsetEnHeureDecimale > 0.0)
				t = t_next;
			
		}
		while (sens == sens0);	        // jusqu'� ce qu'elle change de sens
		
		// elle a chang� de sens !
		//-----------------------------------------------------------------------------
		// OK j'y suis presque pour etre precis je vais mouliner en minute ce coup ci
		//-----------------------------------------------------------------------------
		sens0 = sens;	    	        // on note le nouveau sens
		pas = 0;
		do			                // on revient en arriere
		{ 
			pas = pas - Trigo.UneMinuteEpoch;	        // minute par minute
			
			localOffsetEnHeureDecimale = 0.0;
			if (Astronomie.isEpochJourDifferent (t, t + pas)) {
				localOffsetEnHeureDecimale = (((double)pas) * Trigo.MilliSecEpoch2HeureDecimale);
			}
			else {
				localOffsetEnHeureDecimale = 0.0;
				t = t + pas;
				pas = 0;
			}

			sens = signe_derivee(t, localOffsetEnHeureDecimale, portId);	// tant que le sens
		}
		while (sens == sens0);	        // est toujours le m�me
		// on a atteint le sens pr�c�dent ! t est l'heure de la mar�e

		// si 30 secondes apr�s, la mar�e �tait toujours dans le m�me sens
		// on prend la minute suivante comme heure de la mar�e
		if (signe_derivee( t + Trigo.DemiMinuteEpoch, 0.0, portId) == sens) 
			t = t + Trigo.DemiMinuteEpoch;

		_logger.debug(String.format("Etale trouvee a epoch: %d [heure CET: %s]", t, MareeEnv._sdfUI.format(new Date(t))));
		return t;
	}
	
	public List<Long> getHoraireEtale(long EpochDebutDerecherche, int portId) throws IOException, E4AException
	{
		ArrayList<Long> retour = new ArrayList<Long>();	

		long h = this.heureEtaleDeMaree (EpochDebutDerecherche, portId);
		retour.add(h);

		EpochDebutDerecherche = h + (5 * Trigo.UneHeureEpoch);
		h = this.heureEtaleDeMaree (EpochDebutDerecherche, portId);
		retour.add(h);
		
		EpochDebutDerecherche = h + (5 * Trigo.UneHeureEpoch);
		h = this.heureEtaleDeMaree (EpochDebutDerecherche, portId);
		retour.add(h);
		
		EpochDebutDerecherche = h + (5 * Trigo.UneHeureEpoch);
		h = this.heureEtaleDeMaree (EpochDebutDerecherche, portId);
		if ((h - EpochDebutDerecherche) < Trigo.JourneeComplete)
			retour.add(h);
		
		return retour;
	}
	
	// --------------------------------------------------------
	// Info de la maree du jour BM / PM
	// --------------------------------------------------------
	public void InfoMareeJour(long EpochDebutDerecherche, int portId, List<HoraireMaree> oPM, List<HoraireMaree> oBM) throws IOException, E4AException
	{
		if ((oPM == null) || (oBM == null))
			return;
		
		List<Long> horaires = getHoraireEtale(EpochDebutDerecherche, portId);
		List<HoraireMaree> infoMaree = getHauteurEau(horaires, portId);
		
		if (infoMaree.get(0).hauteur > infoMaree.get(1).hauteur)
		{
			oPM.add(infoMaree.get(0));			
			oPM.add(infoMaree.get(2));
				
			oBM.add(infoMaree.get(1));	
			if (infoMaree.size() > 3)	
				oBM.add(infoMaree.get(3));
		}
		else
		{
			oBM.add(infoMaree.get(0));
			oBM.add(infoMaree.get(2));			
			
			oPM.add(infoMaree.get(1));
			
			if (infoMaree.size() > 3)
				oPM.add(infoMaree.get(3));
		}
	}
	
	public double getCoefMareeZarbi(long horairePM, long horaireBM, int portId) throws IOException, E4AException
	{
		/*
		 * Calcul du coefficient de mar�e:
		 * -------------------------------
				Le coefficient de mar�e est calcul� pour une pleine mer. On le calcule en faisant le quotient du marnage semi-diurne, 
				par la valeur moyenne du marnage pour les mar�es de vive-eau d'�quinoxe, admise � 6.1 m�tres � Brest.
				
				La formule exacte est la suivante :
					C = (H - No) / U 
					
					avec 
						H: la hauteur de l apleine mer
						No: le niveau moyen de la mer
						U: L'unite de hauteur
				
				
				L'unit� de hauteur correspond � "la valeur moyenne de l'amplitude de la plus grande mar�e qui sui d'un jour et demi environ 
				l'instant de la pleine ou de la nouvelle lune, lors de l'�quinoxe." Il s'agit de la valeur moyenne du marnage pour les mar�es de vive-eau d'�quinoxe.
				
				Le r�sultat est un nombre sans dimension compris entre 20 et 120 et qui varie peu d'un jour sur l'autre. 
				Par convention, le coefficient de mar�es 100 est attribu� au marnage semi-diurne moyen des mar�es de vives-eaux voisines des �quinoxes (21 mars et septembre).
				
			Exemples de coefficients de mar�e:
			----------------------------------	
				Voici quelques exemples de coefficients de mar�e pour bien comprendre la partie th�orique :
					Mar�es extraordinaires de vive-eau d'�quinoxe, le marnage � Brest est admis � 7.32 m�tres, d'o� le coefficient de mar�e maximal de 120.
					Mar�es de vive-eau moyenne : coefficient de mar�es �gal � 95
					Mar�es moyennes : coefficient de mar�es �gal � 70
					Mar�es de morte-eau moyenne : 45- Mar�es de morte-eau les plus faibles, le marnage � Brest est admis � 1.22 m�tres, d'o� le coefficient de mar�e minimal de 20.

				Malgr� tout, dans un m�me port, les hauteurs d'eau pr�dites, correspondant � un m�me coefficient de mar�e, 
				peuvent �tre l�g�rement diff�rentes. En effet, la d�termination des coefficients de mar�es se fait � partir du marnage. 
				Or celui-ci est calcul� en ne tenant compte que des ondes semi-diurnes, alors que les hauteurs d'eau pr�dites, 
				elles, sont calcul�es en prenant en compte toutes les ondes.

			empiriquement par les annuire de maree:
			----------------------------------	
				C = (Hauteur PM - Hauteur BM precedente) / 0.0061 ...

		 */
		
		double h = this.amplitudeSemiDiurne(horairePM, portId);
		h = h - this.amplitudeSemiDiurne(horaireBM, portId);
		h /= 2.0;
		h = 100.0 * h / 3.05;
		System.out.println("Coef :" + h);	
		return (h);
	}
	
	//---------------------------------------------------------------------------
	//-- pour le coef de maree on n'utilise que la marnage Semidiurne
	//---------------------------------------------------------------------------
	private double amplitudeSemiDiurne(long t, int portId) throws IOException, E4AException
	{ 
		if (!isHarmonicInitDone(portId))
			initHarmonique (portId);
		
		Astronomie astrePosition = new Astronomie(t);
		Ondes.InitEquilibriumAndPhase (astrePosition.s, astrePosition.h, astrePosition.p, astrePosition.p1, astrePosition.N);
		double heureDecimal = astrePosition.heureDecimale;

		double amplitude = Ondes._Z0; // 0.;
		double amplitudeDebug = Ondes._Z0; // 0.;
		double var;
		for (Onde o : Ondes._table2NC) {
			
			/* 
			 * les ondes semii diurne on une speed comprise entre 12.85 et 16.1391017
			 *     new ConstituantHarmoniqueUneOnde ( "2Q1"     , 1,-4, 1, 2, 0, -90, 12.8542862, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uO1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fO1),
	   			   new ConstituantHarmoniqueUneOnde ( "OO1"     , 1, 2, 1, 0, 0, +90, 16.1391017, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK2mQ1,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fK2Q1), //like KQ1=K2-Q1
			*/
			if ((o._speed < 12) || (o._speed > 17))
			{ 
				var = Trigo.reduc360 (o._speed * heureDecimal + o._equilibrium - o._phase);
				amplitudeDebug += o._nodeFactor * o._ampli * Trigo.cos_deg(var);
				continue;
			}

			
			if (o._ampli > 0.)
			{ 
				var = Trigo.reduc360 (o._speed * heureDecimal + o._equilibrium - o._phase);
				amplitude +=      o._nodeFactor * o._ampli * Trigo.cos_deg(var);
				amplitudeDebug += o._nodeFactor * o._ampli * Trigo.cos_deg(var);
			}
		}
		System.out.println("heure - Ampli("+t+" - "+amplitudeDebug+") amplitude semi diurne :" + amplitude);
		return amplitude;
	}

	public double getCoefMaree(double hauteurPM, double hauteurBM)
	{
		/*
		 * Calcul du coefficient de mar�e:
		 * -------------------------------
				Le coefficient de mar�e est calcul� pour une pleine mer. On le calcule en faisant le quotient du marnage semi-diurne, 
				par la valeur moyenne du marnage pour les mar�es de vive-eau d'�quinoxe, admise � 6.1 m�tres � Brest.
				
				La formule exacte est la suivante :
					C = (H - No) / U 
					
					avec 
						H: la hauteur de l apleine mer
						No: le niveau moyen de la mer
						U: L'unite de hauteur
				
				
				L'unit� de hauteur correspond � "la valeur moyenne de l'amplitude de la plus grande mar�e qui sui d'un jour et demi environ 
				l'instant de la pleine ou de la nouvelle lune, lors de l'�quinoxe." Il s'agit de la valeur moyenne du marnage pour les mar�es de vive-eau d'�quinoxe.
				
				Le r�sultat est un nombre sans dimension compris entre 20 et 120 et qui varie peu d'un jour sur l'autre. 
				Par convention, le coefficient de mar�es 100 est attribu� au marnage semi-diurne moyen des mar�es de vives-eaux voisines des �quinoxes (21 mars et septembre).
				
			Exemples de coefficients de mar�e:
			----------------------------------	
				Voici quelques exemples de coefficients de mar�e pour bien comprendre la partie th�orique :
					Mar�es extraordinaires de vive-eau d'�quinoxe, le marnage � Brest est admis � 7.32 m�tres, d'o� le coefficient de mar�e maximal de 120.
					Mar�es de vive-eau moyenne : coefficient de mar�es �gal � 95
					Mar�es moyennes : coefficient de mar�es �gal � 70
					Mar�es de morte-eau moyenne : 45- Mar�es de morte-eau les plus faibles, le marnage � Brest est admis � 1.22 m�tres, d'o� le coefficient de mar�e minimal de 20.

				Malgr� tout, dans un m�me port, les hauteurs d'eau pr�dites, correspondant � un m�me coefficient de mar�e, 
				peuvent �tre l�g�rement diff�rentes. En effet, la d�termination des coefficients de mar�es se fait � partir du marnage. 
				Or celui-ci est calcul� en ne tenant compte que des ondes semi-diurnes, alors que les hauteurs d'eau pr�dites, 
				elles, sont calcul�es en prenant en compte toutes les ondes.
				
				
				
			empiriquement par les annuire de maree:
			----------------------------------	
				C = (Hauteur PM - Hauteur BM precedente) / 0.0061 ...
				
		 */
		
		double h = hauteurPM - hauteurBM;
		h /= 0.061;
		System.out.println("Coef :" + h);
		return (h);
	}





	//---------------------------------------------------------------------------	
	private boolean isHarmonicInitDone (int portID) {
		if (_portIDInit == portID)  return true;
		return false;
	}

	private void setHarmonicInit (int portID) {
		_portIDInit = portID;
	}

	private void initHarmonique (int portId) throws IOException, E4AException {
		// Read donnee de maree de un port et mise a jour de ces info
		File portHarmonique = null;
		
		if (portId != 3) 
			throw new E4AException("PortId unknown");
			
		if (portId == 3) 
			portHarmonique = new File ("E:\\WS\\GitHubPerso\\Cleyo\\MarrePredictionHarmonique\\data\\Brest\\Harmonique2020.txt");

		HashMap<String, HarmoniqueInfo> allHarmoniques = this.getHarmoniqueFromFile(portHarmonique);
		Ondes._Z0 = allHarmoniques.get("Z0").amplitude;
		for (Onde o  : Ondes._table2NC) {
			String nom = o._Nom;
			if (!allHarmoniques.containsKey(nom)) {
				o._ampli = 0.0;
				o._phase = 0.0;
			}
			else {
				o._ampli = allHarmoniques.get(nom).amplitude;
				o._phase = allHarmoniques.get(nom).phase;
			}
		}
		setHarmonicInit(portId);
	}
	
	private HashMap<String, HarmoniqueInfo> getHarmoniqueFromFile(File portHarmonique) throws IOException {
		HashMap<String, HarmoniqueInfo> retour = new HashMap<String, HarmoniqueInfo>();
		boolean isStarted = false;
		BufferedReader br = new BufferedReader(new FileReader(portHarmonique));
		String line = null;
		final String regex = "^(\\S+)\\s+([0-9]+\\.[0-9]+)\\s+([0-9]+\\.[0-9]+)$";
		final Pattern pattern = Pattern.compile(regex);


		while ((line = br.readLine()) != null) {
			if (line.startsWith("Z0")) {
				isStarted = true;
				HarmoniqueInfo x = new HarmoniqueInfo();
				x.amplitude = Double.parseDouble(line.replace("Z0", "").replaceAll("\\s", ""));
				x.phase = 0.0;
				retour.put("Z0", x);
				continue;
			}

			if (isStarted) {
				String Nom = ""; 
				String Amplitude = ""; 
				String Phase = "";

				Matcher matcher = pattern.matcher(line);
				matcher.find();
				Nom = matcher.group(1);
				Amplitude = matcher.group(2);
				Phase = matcher.group(3);

				HarmoniqueInfo x = new HarmoniqueInfo();
				x.amplitude = Double.parseDouble(Amplitude);
				x.phase = Double.parseDouble(Phase);
				retour.put(Nom, x);
			}
		}
		br.close();
		return retour;
	}



}
