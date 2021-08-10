package sfa.dev.maree.harmonique.model;

/***********************************************************************
 * 
 * Calcl de trigo et les constante de l'appli
 *
 ***********************************************************************/
public class TrigoEtConstante 
{
	public final static double uneminute = 1.0 / 60.0;		    // une minute exprimée en heures
	public final static double DAY_TO_MILLISEC = 24.0 * 60.0 * 60.0 * 1000.0;
	public final static double DEG_TO_RAD = java.lang.Math.PI / 180.0;
	public final static double RadToDeg = 180.0 / java.lang.Math.PI;

	public static double reduc360(double d) 
	{
		while (d > 360.0)
			d -= 360.0;	
		while (d < 0.0)
			d+= 360.0;
		return d;
	}
	
	public static double cos_deg(double n2) 
	{
		return java.lang.Math.cos(TrigoEtConstante.DEG_TO_RAD * n2);
	}
	
	public static double sin_deg(double n2) 
	{
		return java.lang.Math.sin(TrigoEtConstante.DEG_TO_RAD * n2);
	}	
}
