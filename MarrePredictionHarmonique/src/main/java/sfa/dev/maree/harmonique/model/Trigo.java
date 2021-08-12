package sfa.dev.maree.harmonique.model;

public class Trigo {

	public static final double Deg2Rad = (Math.PI / 180.0);
	public static final double Rad2Deg = (180.0 / Math.PI);
	public static final double uneminute = 1.0 / 24.0;

	public static final long DemiMinuteEpoch =  500 * 60;
	public static final long UneMinuteEpoch  = 1000 * 60;
	public static final long DemiHeureEpoch  =  500 * 3600; // 0.5 * 1000 * 3600;
	public static final long UneHeureEpoch   = 1000 * 3600;
	public static final long JourneeComplete = UneHeureEpoch * 24;
	public static final double MilliSecEpoch2HeureDecimale = (1.0 / (1000.0 * 60 * 24));

	public static double reduc360(double d) {
		while (d >= 360.0)
			d -= 360.0;
		
		while (d < 0.0)
			d += 360.0;
		
		return d;
	}

	public static double cos_deg(double var) {
		return Math.cos(var * Deg2Rad);
	}

	public static double sin_deg(double var) {
		return Math.sin(var * Deg2Rad);
	}

	
}
