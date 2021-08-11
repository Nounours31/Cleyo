package sfa.dev.maree.mains;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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
		
		
		
		List<Double> infomaree = mareeTools.MarreGrammeduJour();
		for (int i = 0; i < infomaree.size(); i++)
		{
			System.out.println("Amplitude [heure" + i + "] = " + infomaree.get(i));
		}
		
		List<Double> horaireEtale = mareeTools.getHoraireEtale();
		List<Double> hauteurEtale = mareeTools.getHauteurEau(horaireEtale);
		for (int i = 0; i < horaireEtale.size(); i++)
		{
			System.out.println("Etale  " + mareeTools.CouvHeureInDoubleToHeureTXT(horaireEtale.get(i)) + " Amplitude = " +  hauteurEtale.get(i));
		}

		List<Double> PM = new ArrayList<Double>(); 
		List<Double> BM = new ArrayList<Double>(); 
		pa.InfoMareeJour(PM, BM);
		int j = 0;
		for (int i = 0; i < PM.size();)
		{
			System.out.println("PM  horaire" + mareeTools.CouvHeureInDoubleToHeureTXT(PM.get(i++)) + " Amplitude = " + PM.get(i++) + " Coef = undef");
		}
		for (int i = 0; i < BM.size();)
		{
			System.out.println("BM  horaire" + mareeTools.CouvHeureInDoubleToHeureTXT(BM.get(i++)) + " Amplitude = " + BM.get(i++) );
		}
		
		System.out.println("PM1" + mareeTools.CouvHeureInDoubleToHeureTXT(PM.get(0)));
		mareeTools.getCoefMaree(PM.get(0), BM.get(0));
		System.out.println("PM2" + mareeTools.CouvHeureInDoubleToHeureTXT(PM.get(2)));
		mareeTools.getCoefMaree(PM.get(2), BM.get(2));

		
	}






}
