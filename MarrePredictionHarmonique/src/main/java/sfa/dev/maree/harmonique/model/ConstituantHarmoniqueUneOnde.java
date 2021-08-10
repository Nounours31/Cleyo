package sfa.dev.maree.harmonique.model;

public class ConstituantHarmoniqueUneOnde 
{
	// la table2 est issue � l'origine du "Manual of harmonic analysis and prediction of tides"
	// de P. Shureman. Elle a �t� compl�t�e par un certain nombre de constantes
	// qui ne figuraient pas dans cette table.
	// La table donne pour chaque constituent, son nom, les coefficients
	// par lesquels il faut multiplier les variables de base, la vitesse
	// angulaire et les fonctions de correction nodale qui s'appliquent.
	// Par la suite, la table a �t� modifi�e en remplacant les fonctions de correction nodale,
	// par celles qui figurent dans la "liste standard des constituents" publi�e
	// par le "Tidal Committee" de l'IHO.
	//  T            angle horaire moyen
	//  s            longitude moyenne de la lune
	//  h            longitude moyenne du soleil
	//  p            longitude moyenne du p�rig�e de la lune
	//  p1           longitude moyenne du p�rig�e du soleil
	// deg           phase
	// speed
	//  u            correction nodale de phase
	//  f            facteur de correction nodale d'amplitude

	
	public String Nom;
	public double ampli = 0.0;
	public double phase = 0.0;
	public int T;
	public int s;
	public int h;
	public int p;
	public int p1;
	public int deg;
	public double speed;
	public UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude u; 
	public UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude f;
	
	//           T  s  h  p p1  deg     speed      u       f
	ConstituantHarmoniqueUneOnde (String _Nom, int _T, int _s, int _h, int _p, int _p1, int _deg, double _speed, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude _u, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude _f)
	{
		Nom 	= _Nom;
		T 		= _T;
		s 		= _s;
		h 		= _h;
		p 		= _p;
		p1 		= _p1;
		deg 	= _deg;
		speed 	= _speed;
		u 		= _u;
		f 		= _f;
	}
}
