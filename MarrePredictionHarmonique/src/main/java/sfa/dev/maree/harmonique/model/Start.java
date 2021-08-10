package sfa.dev.maree.harmonique.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import sfa.dev.maree.tools.Constantes;
import sfa.dev.maree.tools.Env;



public class Start {
	public static void main(String[] args) 
	{
		// --------------------------------------------
		// Init horaire, on va se mettre en UTC pour avoir la paix
		// --------------------------------------------
		TimeZone utc = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(utc);

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(utc);
		
        Constantes._sdf.setTimeZone(utc);
        Constantes._sdf4file.setTimeZone(utc);
		
		
		try 
		{
			//------------------------------------------------------------------------------------------------
			//-- le fichier de donnees du SHOM via Refmar validee
			//------------------------------------------------------------------------------------------------
			File inDir = new File (Env.getRepCoef());
			if (!inDir.exists())
				return ;


			//------------------------------------------------------------------------------------------------
			//-- Choix user d'un fichier IN
			//------------------------------------------------------------------------------------------------
			String[] dowloadfiles = inDir.list();
			short NbDownloadedFiles = 0;
			for (String i : dowloadfiles)
				System.out.println("Indice: " + NbDownloadedFiles++ + " Nom: " + i);


			
			
			
			//------------------------------------------------------------------------------------------------
			//-- choix des fichier et creation si necessaire
			//------------------------------------------------------------------------------------------------
			File infile = new File (inDir, dowloadfiles[choix]);
			
			
			
			
			
			//------------------------------------------------------------------------------------------------
			//-- calcul proprement dit
			//------------------------------------------------------------------------------------------------
			GregorianCalendar gc = new GregorianCalendar(2013, Calendar.JUNE, 22);
			CalculMareeDuJour pa = new  CalculMareeDuJour(gc, infile.getAbsolutePath());
			
			
			List<Double> infomaree = pa.MarreGrammeduJour();
			for (int i = 0; i < infomaree.size(); i++)
			{
				System.out.println("Amplitude [heure" + i + "] = " + infomaree.get(i));
			}
			
			List<Double> horaireEtale = pa.getHoraireEtale();
			List<Double> hauteurEtale = pa.getHauteurEau(horaireEtale);
			for (int i = 0; i < horaireEtale.size(); i++)
			{
				System.out.println("Etale  " + pa.CouvHeureInDoubleToHeureTXT(horaireEtale.get(i)) + " Amplitude = " +  hauteurEtale.get(i));
			}

			List<Double> PM = new ArrayList<Double>(); 
			List<Double> BM = new ArrayList<Double>(); 
			pa.InfoMareeJour(PM, BM);
			int j = 0;
			for (int i = 0; i < PM.size();)
			{
				System.out.println("PM  horaire" + pa.CouvHeureInDoubleToHeureTXT(PM.get(i++)) + " Amplitude = " + PM.get(i++) + " Coef = undef");
			}
			for (int i = 0; i < BM.size();)
			{
				System.out.println("BM  horaire" + pa.CouvHeureInDoubleToHeureTXT(BM.get(i++)) + " Amplitude = " + BM.get(i++) );
			}
			
			System.out.println("PM1" + pa.CouvHeureInDoubleToHeureTXT(PM.get(0)));
			pa.getCoefMaree(PM.get(0), BM.get(0));
			System.out.println("PM2" + pa.CouvHeureInDoubleToHeureTXT(PM.get(2)));
			pa.getCoefMaree(PM.get(2), BM.get(2));

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
