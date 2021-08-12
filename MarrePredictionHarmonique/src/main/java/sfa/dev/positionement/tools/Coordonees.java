package sfa.dev.positionement.tools;

public class Coordonees {
	double longitude, latitude;
	
	
    /**
     * Parses a latitude/longitude point from a variety of formats.
     *
     * Latitude & longitude (in degrees) can be supplied as two separate parameters, as a single
     * comma-separated lat/lon string, or as a single object with { lat, lon } or GeoJSON properties.
     *
     * The latitude/longitude values may be numeric or strings; they may be signed decimal or
     * deg-min-sec (hexagesimal) suffixed by compass direction (NSEW); a variety of separators are
     * accepted. Examples -3.62, '3 37 12W', '3°37'12"W'.
     *
     * Thousands/decimal separators must be comma/dot; use Dms.fromLocale to convert locale-specific
     * thousands/decimal separators.
     *
     * @param   {number|string|Object} lat|latlon - Latitude (in degrees) or comma-separated lat/lon or lat/lon object.
     * @param   {number|string}        [lon]      - Longitude (in degrees).
     * @returns {LatLon} Latitude/longitude point.
     * @throws  {TypeError} Invalid point.
     *
     * @example
     *   const p1 = LatLon.parse(52.205, 0.119);                                    // numeric pair 
     *   const p2 = LatLon.parse('52.205', '0.119');                                // numeric string pair 
     *   const p3 = LatLon.parse('52.205, 0.119');                                  // single string numerics
     *   const p4 = LatLon.parse('52°12'18.0"N', '000°07'08.4"E');                  // DMS pair
     *   const p5 = LatLon.parse('52°12'18.0"N, 000°07'08.4"E');                    // single string DMS
     *   const p6 = LatLon.parse({ lat: 52.205, lon: 0.119 });                      // { lat, lon } object numeric
     *   const p7 = LatLon.parse({ lat: '52°12'18.0"N', lng: '000°07'08.4"E' });    // { lat, lng } object DMS
     *   const p8 = LatLon.parse({ type: 'Point', coordinates: [ 0.119, 52.205] }); // GeoJSON
     */    
    
	public Coordonees(double lat, double Longi) {
		latitude = lat;
		longitude = Longi;
	}
}
