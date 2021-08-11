package sfa.dev.maree.harmonique.model;

import sfa.dev.maree.harmonique.model.Ondes.UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude;

public class Onde 
{
	// la table2 est issue à l'origine du "Manual of harmonic analysis and prediction of tides"
	// de P. Shureman. Elle a été complétée par un certain nombre de constantes
	// qui ne figuraient pas dans cette table.
	// La table donne pour chaque constituent, son nom, les coefficients
	// par lesquels il faut multiplier les variables de base, la vitesse
	// angulaire et les fonctions de correction nodale qui s'appliquent.
	// Par la suite, la table a été modifiée en remplacant les fonctions de correction nodale,
	// par celles qui figurent dans la "liste standard des constituents" publiée
	// par le "Tidal Committee" de l'IHO.
	//  T            angle horaire moyen
	//  s            longitude moyenne de la lune
	//  h            longitude moyenne du soleil
	//  p            longitude moyenne du périgée de la lune
	//  p1           longitude moyenne du périgée du soleil
	// deg           phase
	// speed
	//  u            correction nodale de phase
	//  f            facteur de correction nodale d'amplitude

	
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
	
	
	
	public String _Nom;
	public double _ampli = 0.0;
	public double _phase = 0.0;
	
	public double _equilibrium = 0.0;
	public double _nodeFactor = 0.0;

	public int _T;
	public int _s;
	public int _h;
	public int _p;
	public int _p1;
	public int _deg;
	public double _speed;
	public UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude _u; 
	public UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude _f;
	
	//                    T      s      h      p      p1      deg         speed      u       f
	Onde (String Nom, int T, int s, int h, int p, int p1, int deg, double speed, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude u, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude f)
	{
		_Nom 	= Nom;
		_T 		= T;
		_s 		= s;
		_h 		= h;
		_p 		= p;
		_p1 		= p1;
		_deg 	= deg;
		_speed 	= speed;
		_u 		= u;
		_f 		= f;
	}

	public void InitEquilibriumAndPhase(double s, double h, double p, double p1, double N) {
		double T=180.;
		_equilibrium = this._T*T + this._s*s + this._h*h;
		_equilibrium += this._p*p + this._p1*p1;
		_equilibrium += (double)this._deg;
		_equilibrium += Ondes.eval (this._u, p, N);// appel fonction pointée par _table2NC
		_equilibrium = Trigo.reduc360(_equilibrium);
			
		_nodeFactor = Ondes.eval (this._f, p, N);// appel fonction pointée par _table2NC
		
	}
}
