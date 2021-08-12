package sfa.dev.positionement.tools;

public class Positionement {
	static final double Rterre = 6371000; // 6378137;
	public Positionement() {}
	
	// https://www.movable-type.co.uk/scripts/latlong.html
	public double distanceOrthodromieDeuxPoint (Coordonees P1, Coordonees P2) {
		double latitude1 = P1.latitude * Math.PI/180; 
		double latitude2 = P2.latitude * Math.PI/180;

		double Longitude1 = P1.longitude * Math.PI/180; 
		double Longitude2 = P2.longitude * Math.PI/180;

		double deltaLatitude = latitude2 - latitude1;
		double deltaLongitude = Longitude2 - Longitude1;

		double a = Math.sin(deltaLatitude/2.0) * Math.sin(deltaLatitude/2.0) + Math.cos(latitude1) * Math.cos(latitude2) * Math.sin(deltaLongitude/2.0) * Math.sin(deltaLongitude/2.0);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

		double d = Rterre * c; // in metres
		
		return d;
	}
	
	
	public double capOrthoDromieDeuxPoint (Coordonees P1, Coordonees P2) {
		double latitude1 = P1.latitude * Math.PI/180; 
		double latitude2 = P2.latitude * Math.PI/180;

		double Longitude1 = P1.longitude * Math.PI/180; 
		double Longitude2 = P2.longitude * Math.PI/180;

		double deltaLongitude = Longitude2 - Longitude1;

		double y = Math.sin(deltaLongitude) * Math.cos(latitude2);
		double x = Math.cos(latitude1)*Math.sin(latitude2) - Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(deltaLongitude);

		double tetha = Math.atan2(y, x);
		double brng = ((tetha * 180.0 / Math.PI) + 360.0); // in degrees
		while (brng > 360.0)
			brng -= 360.0;
		
		return brng;
	}
	
	// A ‘rhumb line’ (or loxodrome) is a path of constant bearing, which crosses all meridians at the same angle.
	public double distanceLoxodromieDeuxPoint (Coordonees P1, Coordonees P2) {
		double latitude1 = P1.latitude * Math.PI/180; 
		double latitude2 = P2.latitude * Math.PI/180;

		double Longitude1 = P1.longitude * Math.PI/180; 
		double Longitude2 = P2.longitude * Math.PI/180;

		double deltaLatitude = latitude2 - latitude1;
		double deltaLongitude = Longitude2 - Longitude1;

		double dPsy = Math.log(Math.tan(Math.PI/4.0 + latitude2/2.0)/Math.tan(Math.PI/4.0 + latitude1/ 2.0));
		double q = Math.abs(dPsy) > 10e-12 ? deltaLatitude/dPsy : Math.cos(dPsy); // E-W course becomes ill-conditioned with 0/0

		// if dLon over 180° take shorter rhumb line across the anti-meridian:
		if (Math.abs(deltaLongitude) > Math.PI) 
			deltaLongitude = ((deltaLongitude > 0) ? -(2.0 * Math.PI-deltaLongitude) : (2.0 * Math.PI + deltaLongitude));

		double d = Math.sqrt(deltaLatitude * deltaLatitude + q * q * deltaLongitude * deltaLongitude) * Rterre;
		
		return d;
	}
	
	//A ‘rhumb line’ (or loxodrome) is a path of constant bearing, which crosses all meridians at the same angle.
	public double capLoxodromieDeuxPoint (Coordonees P1, Coordonees P2) {
		double latitude1 = P1.latitude * Math.PI/180; 
		double latitude2 = P2.latitude * Math.PI/180;

		double Longitude1 = P1.longitude * Math.PI/180; 
		double Longitude2 = P2.longitude * Math.PI/180;

		double deltaLongitude = Longitude2 - Longitude1;

		double dPsy = Math.log(Math.tan(Math.PI/4.0 + latitude2/2.0)/Math.tan(Math.PI/4.0 + latitude1/ 2.0));

		// if dLon over 180° take shorter rhumb line across the anti-meridian:
		if (Math.abs(deltaLongitude) > Math.PI) 
			deltaLongitude = ((deltaLongitude > 0) ? -(2.0 * Math.PI-deltaLongitude) : (2.0 * Math.PI + deltaLongitude));

		double brng = 360.0 + Math.atan2(deltaLongitude, dPsy) * 180.0/Math.PI;
		while (brng > 360.0)
			brng -= 360.0;
		
		return brng;
	}
}
