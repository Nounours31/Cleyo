package sfa.dev.maree.harmonique.model;

import java.io.File;




public class CalculMareeInterne 
{
	/*------------------------------------------------------------------------------------------
	 * Anciens commentaires issues du code C++ de C. Leyo
	 * Permet de se raccrocher au branches du nouveau code
	 * ---------------------------------------------------
	 *	extern constituentsNC table2NC[nb_const];   // table des constantes harmoniques
	 *
	 *	extern double ampli[nb_const];		// amplitude de la constante
	 *	extern double phase[nb_const];		// phase     de la constante
	 *
	 *	extern double s;       	            // mean longitude of moon
	 *	extern double h;		    	    // mean longitude of sun
	 *	extern double p;			        // longitude of lunar perigee
	 *	extern double p1;			        // longitude of solar perigee
	 *	extern double N;			        // longitude of moon's node
	 */
	public double[] equilbrm = null;	// equilibrium argument (theorical phase)
	public double[] nodefctr = null;	// node factor
	public LesConstanteHarmoniques cHarmo = null; // la liste des constavte harmoniques
	public double s, h, p, p1, N; // parametre lunaire et solaire du jour cf CalculAstronomie 

	public CalculMareeInterne (File paramFiletd4, double _s, double _h, double _p, double _p1, double _N)
	{
		s = _s;
		h = _h;
		p = _p;
		p1 = _p1;
		N = _N;
		
		
		// init de constante astro du jour
		cHarmo = LesConstanteHarmoniques.getInstance(paramFiletd4);	
		if (cHarmo.getNbOnde() < 0)
			return;

		// Calcul des ondes
		equilbrm = new double[cHarmo.getNbOnde()];
		nodefctr = new double[cHarmo.getNbOnde()];
		equi_tide();

		// dump des info ondes au cas ou du debug serait necessaire ...
		//HTMLLogForDebug.AddOndesInHTMLLog (cHarmo, equilbrm, nodefctr);
	}


	
	//---------------------------------------------------------------------------
	private void equi_tide()
	{ 
		double T=180.;
		for (int i=0; i < cHarmo.getNbOnde(); i++)
		{ 
			equilbrm[i] = cHarmo._table2NC[i].T*T + cHarmo._table2NC[i].s*s + cHarmo._table2NC[i].h*h;
			equilbrm[i] += cHarmo._table2NC[i].p*p + cHarmo._table2NC[i].p1*p1;
			equilbrm[i] += (double)cHarmo._table2NC[i].deg;
			equilbrm[i] += cHarmo.eval (cHarmo._table2NC[i].u, p, N);// appel fonction pointée par _table2NC
			equilbrm[i] = TrigoEtConstante.reduc360(equilbrm[i]);
			
			nodefctr[i] = cHarmo.eval (cHarmo._table2NC[i].f, p, N);// appel fonction pointée par _table2NC
		}
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
		if (signe_derivee(t0 - 0.5*TrigoEtConstante.uneminute) !=  sens0) return t0;

		
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
			t = t - TrigoEtConstante.uneminute;	        // minute par minute
			sens = signe_derivee(t);	// tant que le sens
		}
		while (sens == sens0);	        // est toujours le même
		// on a atteint le sens précédent ! t est l'heure de la marée

		// si 30 secondes après, la marée était toujours dans le même sens
		// on prend la minute suivante comme heure de la marée
		if (signe_derivee( t + 0.5 * TrigoEtConstante.uneminute) == sens) 
			t = t + TrigoEtConstante.uneminute;

		return t;
	}
	
	
	//---------------------------------------------------------------------------
	private Boolean signe_derivee(double t)
	{ 
		double sens = 0.;
		double var;

		for (int i = 0; i < cHarmo.getNbOnde(); i++)
		{
			if (cHarmo._table2NC[i].ampli>0.)
			{ 
				// la derivée est de la forme -vsin(vt-p)
				var = TrigoEtConstante.reduc360 (cHarmo._table2NC[i].speed * t + equilbrm[i] - cHarmo._table2NC[i].phase);
				sens -= nodefctr[i] * cHarmo._table2NC[i].ampli * cHarmo._table2NC[i].speed * TrigoEtConstante.sin_deg(var);
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
}
