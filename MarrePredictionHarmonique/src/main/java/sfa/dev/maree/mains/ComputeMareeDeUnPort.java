package sfa.dev.maree.mains;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import sfa.dev.maree.harmonique.model.computeportmaree.HoraireMaree;
import sfa.dev.maree.harmonique.model.computeportmaree.MareeTools;

import sfa.dev.maree.tools.MareeEnv;



public class ComputeMareeDeUnPort {


	static SimpleDateFormat _sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 

	public static void main(String[] args) throws Exception {
		// UTC
		_sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		// Pour chaque horaire calcul de la valeur de la sonde
		MareeTools mareeTools = new MareeTools();
		long epoch = _sdf.parse("2021/08/11 09:40:00").getTime();		
		double h = mareeTools.ComputeHauteurMaree(epoch, MareeEnv.IdREFMAR_BREST);

		System.out.println("Maree: " + h);
		
		
		
		
		long epochMinuit = _sdf.parse("2021/08/11 00:00:01").getTime();	
		List<HoraireMaree> infomaree = mareeTools.MarreGrammeduJour(epochMinuit, MareeEnv.IdREFMAR_BREST);
		System.out.println("Maree gramme");
		for (int i = 0; i < infomaree.size(); i++)
		{
			System.out.println(infomaree.get(i).toString());
		}
		
		List<Long> horaireEtale = mareeTools.getHoraireEtale(epochMinuit, MareeEnv.IdREFMAR_BREST);
		List<HoraireMaree> hauteurEtale = mareeTools.getHauteurEau(horaireEtale, MareeEnv.IdREFMAR_BREST);
		System.out.println("Recherche des Etales de maree");
		for (int i = 0; i < horaireEtale.size(); i++)
		{
			System.out.println(hauteurEtale.get(i));
		}

		
		
		List<HoraireMaree> PM = new ArrayList<HoraireMaree>(); 
		List<HoraireMaree> BM = new ArrayList<HoraireMaree>(); 
		mareeTools.InfoMareeJour(epochMinuit, MareeEnv.IdREFMAR_BREST, PM, BM);
		
		System.out.println("Les PMs:");
		for (int i = 0; i < PM.size(); i++)
		{
			System.out.println(PM.get(i).toString());
		}
		System.out.println("Les BMs:");
		for (int i = 0; i < BM.size(); i++)
		{
			System.out.println(BM.get(i).toString());
		}
		
		double coef = mareeTools.getCoefMaree(PM.get(0).hauteur, BM.get(0).hauteur);
		System.out.println("Coef: " + coef);

		coef = mareeTools.getCoefMaree(PM.get(1).hauteur, BM.get(1).hauteur);
		System.out.println("Coef: " + coef);

		
	}






}
