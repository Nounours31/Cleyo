package sfa.dev.maree.harmonique.model.computeportmaree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sfa.dev.generique.tools.E4AException;
import sfa.dev.maree.harmonique.model.Astronomie;
import sfa.dev.maree.harmonique.model.Onde;
import sfa.dev.maree.harmonique.model.Ondes;
import sfa.dev.maree.harmonique.model.Trigo;


public class MareeTools {
	class HarmoniqueInfo {
		double amplitude, phase;
	}
	
	private static int _portIDInit = -1;
	
	
	public List<Double> MarreGrammeduJour(long heure, int portId)
	{
		List<Double> horaires = new ArrayList<Double>();	
		for (double i = 0.0; i < 24.0; i += 0.999)
			horaires.add (i);
			
		List<Double> retour = getHauteurEau(horaires);
		return retour;
	}
	
	public double ComputeHauteurMaree(long heure, int portId) throws IOException, E4AException {
		if (!isHarmonicInitDone(portId))
			initHarmonique (portId);
		
		// Calcul du Z0 du port
		double hauteur = Ondes._Z0;

		Astronomie astrePosition = new Astronomie(heure);
		Ondes.InitEquilibriumAndPhase (astrePosition.s, astrePosition.h, astrePosition.p, astrePosition.p1, astrePosition.N);

		double var;
		for (Onde o : Ondes._table2NC)
		{
			if (o._ampli > 0.)
			{ 
				var = Trigo.reduc360 (o._speed * astrePosition.Epoch2JourDecimal(heure) + o._equilibrium - o._phase);
				hauteur += o._nodeFactor * o._ampli * Trigo.cos_deg(var);
			}
		}
		return hauteur;
	}
	


	public void InfoMareeJour(List<Double> PM, List<Double> BM)
	{
		if ((PM == null) || (BM == null))
			return;
		
		List<Double> horaires = getHoraireEtale();
		List<Double> hauteur = getHauteurEau(horaires);
		
		if (hauteur.get(0) > hauteur.get(1))
		{
			PM.add(horaires.get(0));
			PM.add(hauteur.get(0));
			
			PM.add(horaires.get(2));
			PM.add(hauteur.get(2));
				
			BM.add(horaires.get(1));
			BM.add(hauteur.get(1));
			
			if (hauteur.size() > 3)
			{
				BM.add(horaires.get(3));
				BM.add(hauteur.get(3));
			}
		}
		else
		{
			BM.add(horaires.get(0));
			BM.add(hauteur.get(0));
			
			BM.add(horaires.get(2));			
			BM.add(hauteur.get(2));	
			
			PM.add(horaires.get(1));
			PM.add(hauteur.get(1));
			
			if (hauteur.size() > 3)
			{
				PM.add(horaires.get(3));
				PM.add(hauteur.get(3));
			}
		}
	}
	
	public double getCoefMaree(double horairePM, double horaireBM)
	{
		/*
		 * Calcul du coefficient de marée:
		 * -------------------------------
				Le coefficient de marée est calculé pour une pleine mer. On le calcule en faisant le quotient du marnage semi-diurne, 
				par la valeur moyenne du marnage pour les marées de vive-eau d'équinoxe, admise à 6.1 mètres à Brest.
				
				La formule exacte est la suivante :
					C = (H - No) / U 
					
					avec 
						H: la hauteur de l apleine mer
						No: le niveau moyen de la mer
						U: L'unite de hauteur
				
				
				L'unité de hauteur correspond à "la valeur moyenne de l'amplitude de la plus grande marée qui sui d'un jour et demi environ 
				l'instant de la pleine ou de la nouvelle lune, lors de l'équinoxe." Il s'agit de la valeur moyenne du marnage pour les marées de vive-eau d'équinoxe.
				
				Le résultat est un nombre sans dimension compris entre 20 et 120 et qui varie peu d'un jour sur l'autre. 
				Par convention, le coefficient de marées 100 est attribué au marnage semi-diurne moyen des marées de vives-eaux voisines des équinoxes (21 mars et septembre).
				
			Exemples de coefficients de marée:
			----------------------------------	
				Voici quelques exemples de coefficients de marée pour bien comprendre la partie théorique :
					Marées extraordinaires de vive-eau d'équinoxe, le marnage à Brest est admis à 7.32 mètres, d'où le coefficient de marée maximal de 120.
					Marées de vive-eau moyenne : coefficient de marées égal à 95
					Marées moyennes : coefficient de marées égal à 70
					Marées de morte-eau moyenne : 45- Marées de morte-eau les plus faibles, le marnage à Brest est admis à 1.22 mètres, d'où le coefficient de marée minimal de 20.

				Malgré tout, dans un même port, les hauteurs d'eau prédites, correspondant à un même coefficient de marée, 
				peuvent être légèrement différentes. En effet, la détermination des coefficients de marées se fait à partir du marnage. 
				Or celui-ci est calculé en ne tenant compte que des ondes semi-diurnes, alors que les hauteurs d'eau prédites, 
				elles, sont calculées en prenant en compte toutes les ondes.

			empiriquement par les annuire de maree:
			----------------------------------	
				C = (Hauteur PM - Hauteur BM precedente) / 0.0061 ...

		 */
		
		double h = this.amplitude(horairePM);
		h = h - this.amplitude(horaireBM);
		h /= 2.0;
		h = 100.0 * h / 3.05;
		System.out.println("Coef :" + h);
		
				
		return (h);
	}

	public double getCoefMareeV2(double hauteurPM, double hauteurBM)
	{
		/*
		 * Calcul du coefficient de marée:
		 * -------------------------------
				Le coefficient de marée est calculé pour une pleine mer. On le calcule en faisant le quotient du marnage semi-diurne, 
				par la valeur moyenne du marnage pour les marées de vive-eau d'équinoxe, admise à 6.1 mètres à Brest.
				
				La formule exacte est la suivante :
					C = (H - No) / U 
					
					avec 
						H: la hauteur de l apleine mer
						No: le niveau moyen de la mer
						U: L'unite de hauteur
				
				
				L'unité de hauteur correspond à "la valeur moyenne de l'amplitude de la plus grande marée qui sui d'un jour et demi environ 
				l'instant de la pleine ou de la nouvelle lune, lors de l'équinoxe." Il s'agit de la valeur moyenne du marnage pour les marées de vive-eau d'équinoxe.
				
				Le résultat est un nombre sans dimension compris entre 20 et 120 et qui varie peu d'un jour sur l'autre. 
				Par convention, le coefficient de marées 100 est attribué au marnage semi-diurne moyen des marées de vives-eaux voisines des équinoxes (21 mars et septembre).
				
			Exemples de coefficients de marée:
			----------------------------------	
				Voici quelques exemples de coefficients de marée pour bien comprendre la partie théorique :
					Marées extraordinaires de vive-eau d'équinoxe, le marnage à Brest est admis à 7.32 mètres, d'où le coefficient de marée maximal de 120.
					Marées de vive-eau moyenne : coefficient de marées égal à 95
					Marées moyennes : coefficient de marées égal à 70
					Marées de morte-eau moyenne : 45- Marées de morte-eau les plus faibles, le marnage à Brest est admis à 1.22 mètres, d'où le coefficient de marée minimal de 20.

				Malgré tout, dans un même port, les hauteurs d'eau prédites, correspondant à un même coefficient de marée, 
				peuvent être légèrement différentes. En effet, la détermination des coefficients de marées se fait à partir du marnage. 
				Or celui-ci est calculé en ne tenant compte que des ondes semi-diurnes, alors que les hauteurs d'eau prédites, 
				elles, sont calculées en prenant en compte toutes les ondes.
				
				
				
			empiriquement par les annuire de maree:
			----------------------------------	
				C = (Hauteur PM - Hauteur BM precedente) / 0.0061 ...
				
		 */
		
		double h = hauteurPM - hauteurBM;
		h /= 0.061;
		System.out.println("Coef :" + h);
		return (h);
	}

	public List<Double> getHoraireEtale()
	{
		ArrayList<Double> retour = new ArrayList<Double>();	

		double h = this.heure_de_etale (0.0);
		retour.add(Double.valueOf(h));

		h = this.heure_de_etale (h + 5.0);
		retour.add(Double.valueOf(h));
		
		h = this.heure_de_etale (h + 5.0);
		retour.add(Double.valueOf(h));
		
		h = this.heure_de_etale (h + 5.0);
		if (h < 24.0)
			retour.add(Double.valueOf(h));
		
		return retour;
	}

	public List<Double> getHauteurEau(List<Double> horaire)
	{
		ArrayList<Double> retour = new ArrayList<Double>();	
		for (Double h : horaire) 
		{
			double a = this.amplitude (h.doubleValue());
			retour.add(Double.valueOf(a));
		}
		return retour;
	}
	
	

	//---------------------------------------------------------------------------
	public double heure_de_etale(double t0) // heure de la marée en heures de 0.0 a 23.99
	{ 
		/* la hauteur de la maree est la fonction somme des composants,
	     chaque composant étant de la forme acos(vt-p).
	     La fonction dérivée =-vsin(vt-p) s'annule lorsque la fonction
	     passe par un maxi (pleine mer) ou un mini (basse mer). Elle est
	     positive lorsque la marée monte, et négative lorsque la marée
	     descend. Plutôt que de rechercher les racines de la fonction dérivée,
	     on a trouvé plus simple de procéder par approches successives.
		 */

		double t, dh;
		// sens de la maree 1=maree montante 0=maree descendante
		Boolean sens, sens0;

		
		//------------------------------------------------------------------------------
		// est ce que je ne serais pas deja a l'etale ?
		//------------------------------------------------------------------------------
		dh = 0.5;                       // une demi-heure
		sens0 = signe_derivee(t0);	    // sens de la marée a l'instant initial
		// si 30 secondes avant, la marée n'était pas dans le même sens, c'est
		// qu' elle a changé de sens entre-temps ! heure marée = instant initial
		if (signe_derivee(t0 - 0.5*Trigo.uneminute) !=  sens0) return t0;

		
		//-----------------------------------------------------------------------------
		// non alors je mouline de 1/2 heure en 1/2 heure jusqu'a l'etale
		//-----------------------------------------------------------------------------
		t = t0;			                // on part de l'instant initial
		do                            // on va regarder
		{ 
			t = t + dh;		            // les demi-heures suivantes
			sens = signe_derivee(t);    // quel est le sens de la marée
		}
		while (sens == sens0);	        // jusqu'à ce qu'elle change de sens
		
		// elle a changé de sens !
		//-----------------------------------------------------------------------------
		// OK j'y suis presque pour etre precis je vais mouliner en minute ce coup ci
		//-----------------------------------------------------------------------------
		sens0 = sens;	    	        // on note le nouveau sens
		do			                // on revient en arriere
		{ 
			t = t - Trigo.uneminute;	        // minute par minute
			sens = signe_derivee(t);	// tant que le sens
		}
		while (sens == sens0);	        // est toujours le même
		// on a atteint le sens précédent ! t est l'heure de la marée

		// si 30 secondes après, la marée était toujours dans le même sens
		// on prend la minute suivante comme heure de la marée
		if (signe_derivee( t + 0.5 * Trigo.uneminute) == sens) 
			t = t + Trigo.uneminute;

		return t;
	}
	
	
	//---------------------------------------------------------------------------
	private Boolean signe_derivee(double t)
	{ 
		double sens = 0.;
		double var;

		for (Onde o : Ondes._table2NC) {			
			if (o._ampli > 0.)
			{ 
				// la derivée est de la forme -vsin(vt-p)
				var = Trigo.reduc360 (o._speed * t + o._equilibrium - o._phase);
				sens -= o._nodeFactor * o._ampli * o._speed * Trigo.sin_deg(var);
			}
		}
		
		// return 1 si étale ou marée montante, 0 si marée descendante
		return (sens >= 0.);
	}

	
	//---------------------------------------------------------------------------
	public double amplitude(double t)
	{ 
		double amplitude = cHarmo._Z0; // 0.;
		double var;
		for (int i = 0; i < cHarmo.getNbOnde(); i++)
		{
			if (cHarmo._table2NC[i].ampli > 0.)
			{ 
				var = TrigoEtConstante.reduc360 (cHarmo._table2NC[i].speed * t + equilbrm[i] - cHarmo._table2NC[i].phase);
				amplitude += nodefctr[i] * cHarmo._table2NC[i].ampli * TrigoEtConstante.cos_deg(var);
			}
		}
		return amplitude;
	}

	//---------------------------------------------------------------------------
	//-- pour le coef de maree on n'utilise que la marnage Semidiurne
	//---------------------------------------------------------------------------
	public double amplitudeSemiDiurne(double t)
	{ 
		double amplitude = cHarmo._Z0; // 0.;
		double amplitudeDebug = cHarmo._Z0; // 0.;
		double var;
		for (int i = 0; i < cHarmo.getNbOnde(); i++)
		{
			
			/* 
			 * les ondes semii diurne on une speed comprise entre 12.85 et 16.1391017
			 *     new ConstituantHarmoniqueUneOnde ( "2Q1"     , 1,-4, 1, 2, 0, -90, 12.8542862, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uO1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fO1),
	   			   new ConstituantHarmoniqueUneOnde ( "OO1"     , 1, 2, 1, 0, 0, +90, 16.1391017, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK2mQ1,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fK2Q1), //like KQ1=K2-Q1
			*/
			if ((cHarmo._table2NC[i].speed < 12) || (cHarmo._table2NC[i].speed > 17))
			{ 
				var = TrigoEtConstante.reduc360 (cHarmo._table2NC[i].speed * t + equilbrm[i] - cHarmo._table2NC[i].phase);
				amplitudeDebug += nodefctr[i] * cHarmo._table2NC[i].ampli * TrigoEtConstante.cos_deg(var);
				continue;
			}

			
			if (cHarmo._table2NC[i].ampli > 0.)
			{ 
				var = TrigoEtConstante.reduc360 (cHarmo._table2NC[i].speed * t + equilbrm[i] - cHarmo._table2NC[i].phase);
				amplitude +=      nodefctr[i] * cHarmo._table2NC[i].ampli * TrigoEtConstante.cos_deg(var);
				amplitudeDebug += nodefctr[i] * cHarmo._table2NC[i].ampli * TrigoEtConstante.cos_deg(var);
			}
		}
		System.out.println("heure - Ampli("+t+" - "+amplitudeDebug+") amplitude semi diurne :" + amplitude);
		return amplitude;
	}

	//---------------------------------------------------------------------------
	private double amplimax()
	{ 
		double ampmax = 0.;
		for (int i=0;i<cHarmo.getNbOnde();i++)
			ampmax = ampmax + cHarmo._table2NC[i].ampli;
		return ampmax;
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
			portHarmonique = new File ("E:\\WS\\GitHubPerso\\Cleyo\\MarrePredictionHarmonique\\DataNotInGit\\Brest\\Harmonique2020.txt");

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
