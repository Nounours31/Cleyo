package sfa.dev.maree.harmonique.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class CalculMareeDuJour 
{
	private CalculMareeInterne _cmi = null;
	private CalculAstroInterne _cai = null;
	File paramFiletd4 = null;
	
	public CalculMareeDuJour (GregorianCalendar jourCalcul, String paramFilePathtd4)
	{
		paramFiletd4 = new File (paramFilePathtd4);
		double date_astro = calculCLeyoDateAstro (jourCalcul.getTime());
		Init(date_astro, jourCalcul);
	}
	
	public static double calculCLeyoDateAstro (Date jourCalcul)
	{
		GregorianCalendar gc = new GregorianCalendar(1899, Calendar.DECEMBER, 30);
		Date jourJulien = gc.getTime(); // new Date(1899, 12, 30); 

		double date_astro = ((double)(jourCalcul.getTime() - jourJulien.getTime())) / TrigoEtConstante.DAY_TO_MILLISEC;
		return date_astro;		
	}
	
	public  String CouvHeureInDoubleToHeureTXT (double heure)
	{
		String retour = "";
		String Heure = Integer.toString((int)heure);
		if (Heure.length() != 2)
			Heure = "0" + Heure;
		retour += Heure + ":";
		
		heure -= (int)heure;
		heure *= 60.0;
		
		Heure = Integer.toString((int)heure);
		if (Heure.length() != 2)
			Heure = "0" + Heure;
		retour += Heure;
		
		return retour;
	}

	private void Init (double dateAstro, GregorianCalendar jourCalcul)
	{
		_cai = new CalculAstroInterne(dateAstro, jourCalcul);
		_cmi = new CalculMareeInterne (paramFiletd4, _cai.s, _cai.h, _cai.p, _cai.p1, _cai.N);
	}
	
	public List<Double> MarreGrammeduJour()
	{
		List<Double> horaires = new ArrayList<Double>();	
		for (double i = 0.0; i < 24.0; i += 0.999)
			horaires.add (i);
			
		List<Double> retour = getHauteurEau(horaires);
		return retour;
	}

	public void InfoMareeJour(List<Double> PM, List<Double> BM)
	{
		if ((PM == null) || (BM == null))
			return;
		
		List<Double> horaires = getHoraireEtale();
		List<Double> hauteur = getHauteurEau(horaires);
		
		if (hauteur.get(0) > hauteur.get(1))
		{
			PM.add(horaires.get(0));
			PM.add(hauteur.get(0));
			
			PM.add(horaires.get(2));
			PM.add(hauteur.get(2));
				
			BM.add(horaires.get(1));
			BM.add(hauteur.get(1));
			
			if (hauteur.size() > 3)
			{
				BM.add(horaires.get(3));
				BM.add(hauteur.get(3));
			}
		}
		else
		{
			BM.add(horaires.get(0));
			BM.add(hauteur.get(0));
			
			BM.add(horaires.get(2));			
			BM.add(hauteur.get(2));	
			
			PM.add(horaires.get(1));
			PM.add(hauteur.get(1));
			
			if (hauteur.size() > 3)
			{
				PM.add(horaires.get(3));
				PM.add(hauteur.get(3));
			}
		}
	}
	
	public double getCoefMaree(double horairePM, double horaireBM)
	{
		/*
		 * Calcul du coefficient de marée:
		 * -------------------------------
				Le coefficient de marée est calculé pour une pleine mer. On le calcule en faisant le quotient du marnage semi-diurne, 
				par la valeur moyenne du marnage pour les marées de vive-eau d'équinoxe, admise à 6.1 mètres à Brest.
				
				La formule exacte est la suivante :
					C = (H - No) / U 
					
					avec 
						H: la hauteur de l apleine mer
						No: le niveau moyen de la mer
						U: L'unite de hauteur
				
				
				L'unité de hauteur correspond à "la valeur moyenne de l'amplitude de la plus grande marée qui sui d'un jour et demi environ 
				l'instant de la pleine ou de la nouvelle lune, lors de l'équinoxe." Il s'agit de la valeur moyenne du marnage pour les marées de vive-eau d'équinoxe.
				
				Le résultat est un nombre sans dimension compris entre 20 et 120 et qui varie peu d'un jour sur l'autre. 
				Par convention, le coefficient de marées 100 est attribué au marnage semi-diurne moyen des marées de vives-eaux voisines des équinoxes (21 mars et septembre).
				
			Exemples de coefficients de marée:
			----------------------------------	
				Voici quelques exemples de coefficients de marée pour bien comprendre la partie théorique :
					Marées extraordinaires de vive-eau d'équinoxe, le marnage à Brest est admis à 7.32 mètres, d'où le coefficient de marée maximal de 120.
					Marées de vive-eau moyenne : coefficient de marées égal à 95
					Marées moyennes : coefficient de marées égal à 70
					Marées de morte-eau moyenne : 45- Marées de morte-eau les plus faibles, le marnage à Brest est admis à 1.22 mètres, d'où le coefficient de marée minimal de 20.

				Malgré tout, dans un même port, les hauteurs d'eau prédites, correspondant à un même coefficient de marée, 
				peuvent être légèrement différentes. En effet, la détermination des coefficients de marées se fait à partir du marnage. 
				Or celui-ci est calculé en ne tenant compte que des ondes semi-diurnes, alors que les hauteurs d'eau prédites, 
				elles, sont calculées en prenant en compte toutes les ondes.

			empiriquement par les annuire de maree:
			----------------------------------	
				C = (Hauteur PM - Hauteur BM precedente) / 0.0061 ...

		 */
		
		double h = _cmi.amplitude(horairePM);
		h = h - _cmi.amplitude(horaireBM);
		h /= 2.0;
		h = 100.0 * h / 3.05;
		System.out.println("Coef :" + h);
		
				
		return (h);
	}

	public double getCoefMareeV2(double hauteurPM, double hauteurBM)
	{
		/*
		 * Calcul du coefficient de marée:
		 * -------------------------------
				Le coefficient de marée est calculé pour une pleine mer. On le calcule en faisant le quotient du marnage semi-diurne, 
				par la valeur moyenne du marnage pour les marées de vive-eau d'équinoxe, admise à 6.1 mètres à Brest.
				
				La formule exacte est la suivante :
					C = (H - No) / U 
					
					avec 
						H: la hauteur de l apleine mer
						No: le niveau moyen de la mer
						U: L'unite de hauteur
				
				
				L'unité de hauteur correspond à "la valeur moyenne de l'amplitude de la plus grande marée qui sui d'un jour et demi environ 
				l'instant de la pleine ou de la nouvelle lune, lors de l'équinoxe." Il s'agit de la valeur moyenne du marnage pour les marées de vive-eau d'équinoxe.
				
				Le résultat est un nombre sans dimension compris entre 20 et 120 et qui varie peu d'un jour sur l'autre. 
				Par convention, le coefficient de marées 100 est attribué au marnage semi-diurne moyen des marées de vives-eaux voisines des équinoxes (21 mars et septembre).
				
			Exemples de coefficients de marée:
			----------------------------------	
				Voici quelques exemples de coefficients de marée pour bien comprendre la partie théorique :
					Marées extraordinaires de vive-eau d'équinoxe, le marnage à Brest est admis à 7.32 mètres, d'où le coefficient de marée maximal de 120.
					Marées de vive-eau moyenne : coefficient de marées égal à 95
					Marées moyennes : coefficient de marées égal à 70
					Marées de morte-eau moyenne : 45- Marées de morte-eau les plus faibles, le marnage à Brest est admis à 1.22 mètres, d'où le coefficient de marée minimal de 20.

				Malgré tout, dans un même port, les hauteurs d'eau prédites, correspondant à un même coefficient de marée, 
				peuvent être légèrement différentes. En effet, la détermination des coefficients de marées se fait à partir du marnage. 
				Or celui-ci est calculé en ne tenant compte que des ondes semi-diurnes, alors que les hauteurs d'eau prédites, 
				elles, sont calculées en prenant en compte toutes les ondes.
				
				
				
			empiriquement par les annuire de maree:
			----------------------------------	
				C = (Hauteur PM - Hauteur BM precedente) / 0.0061 ...
				
		 */
		
		double h = hauteurPM - hauteurBM;
		h /= 0.061;
		System.out.println("Coef :" + h);
		return (h);
	}

	public List<Double> getHoraireEtale()
	{
		ArrayList<Double> retour = new ArrayList<Double>();	

		double h = _cmi.heure_de_etale (0.0);
		retour.add(Double.valueOf(h));

		h = _cmi.heure_de_etale (h + 5.0);
		retour.add(Double.valueOf(h));
		
		h = _cmi.heure_de_etale (h + 5.0);
		retour.add(Double.valueOf(h));
		
		h = _cmi.heure_de_etale (h + 5.0);
		if (h < 24.0)
			retour.add(Double.valueOf(h));
		
		return retour;
	}

	public List<Double> getHauteurEau(List<Double> horaire)
	{
		ArrayList<Double> retour = new ArrayList<Double>();	
		for (Double h : horaire) 
		{
			double a = _cmi.amplitude (h.doubleValue());
			retour.add(Double.valueOf(a));
		}
		return retour;
	}
}
