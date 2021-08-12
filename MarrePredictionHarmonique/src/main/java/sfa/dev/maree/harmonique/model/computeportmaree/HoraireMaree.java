package sfa.dev.maree.harmonique.model.computeportmaree;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import sfa.dev.maree.tools.MareeEnv;

public 	class HoraireMaree {
	
	public HoraireMaree(long t, double h) {
		epoch = t; 
		hauteur = h;
		MareeEnv._sdfCode.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public long epoch;
	public double hauteur;
	
	@Override
	public String toString() {
		GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		gc.setTimeInMillis(epoch);
		return String.format("Epoch: %d (%s UTC) -- Hauteur: %f", epoch, MareeEnv._sdfCode.format (gc.getTime()), hauteur);
	}
}
