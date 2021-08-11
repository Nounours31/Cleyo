package sfa.dev.maree.harmonique.model.computeportmaree;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public 	class HoraireMaree {
	static SimpleDateFormat _sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
	
	public HoraireMaree(long t, double h) {epoch = t; hauteur = h;}
	public long epoch;
	public double hauteur;
	
	@Override
	public String toString() {
		GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		gc.setTimeInMillis(epoch);
		return String.format("Epoch: %d (%s) -- Hauteur: %f", epoch, _sdf.format (gc.getTime()), hauteur);
	}
}
