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
		 * Calcul du coefficient de mar�e:
		 * -------------------------------
				Le coefficient de mar�e est calcul� pour une pleine mer. On le calcule en faisant le quotient du marnage semi-diurne, 
				par la valeur moyenne du marnage pour les mar�es de vive-eau d'�quinoxe, admise � 6.1 m�tres � Brest.
				
				La formule exacte est la suivante :
					C = (H - No) / U 
					
					avec 
						H: la hauteur de l apleine mer
						No: le niveau moyen de la mer
						U: L'unite de hauteur
				
				
				L'unit� de hauteur correspond � "la valeur moyenne de l'amplitude de la plus grande mar�e qui sui d'un jour et demi environ 
				l'instant de la pleine ou de la nouvelle lune, lors de l'�quinoxe." Il s'agit de la valeur moyenne du marnage pour les mar�es de vive-eau d'�quinoxe.
				
				Le r�sultat est un nombre sans dimension compris entre 20 et 120 et qui varie peu d'un jour sur l'autre. 
				Par convention, le coefficient de mar�es 100 est attribu� au marnage semi-diurne moyen des mar�es de vives-eaux voisines des �quinoxes (21 mars et septembre).
				
			Exemples de coefficients de mar�e:
			----------------------------------	
				Voici quelques exemples de coefficients de mar�e pour bien comprendre la partie th�orique :
					Mar�es extraordinaires de vive-eau d'�quinoxe, le marnage � Brest est admis � 7.32 m�tres, d'o� le coefficient de mar�e maximal de 120.
					Mar�es de vive-eau moyenne : coefficient de mar�es �gal � 95
					Mar�es moyennes : coefficient de mar�es �gal � 70
					Mar�es de morte-eau moyenne : 45- Mar�es de morte-eau les plus faibles, le marnage � Brest est admis � 1.22 m�tres, d'o� le coefficient de mar�e minimal de 20.

				Malgr� tout, dans un m�me port, les hauteurs d'eau pr�dites, correspondant � un m�me coefficient de mar�e, 
				peuvent �tre l�g�rement diff�rentes. En effet, la d�termination des coefficients de mar�es se fait � partir du marnage. 
				Or celui-ci est calcul� en ne tenant compte que des ondes semi-diurnes, alors que les hauteurs d'eau pr�dites, 
				elles, sont calcul�es en prenant en compte toutes les ondes.

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
		 * Calcul du coefficient de mar�e:
		 * -------------------------------
				Le coefficient de mar�e est calcul� pour une pleine mer. On le calcule en faisant le quotient du marnage semi-diurne, 
				par la valeur moyenne du marnage pour les mar�es de vive-eau d'�quinoxe, admise � 6.1 m�tres � Brest.
				
				La formule exacte est la suivante :
					C = (H - No) / U 
					
					avec 
						H: la hauteur de l apleine mer
						No: le niveau moyen de la mer
						U: L'unite de hauteur
				
				
				L'unit� de hauteur correspond � "la valeur moyenne de l'amplitude de la plus grande mar�e qui sui d'un jour et demi environ 
				l'instant de la pleine ou de la nouvelle lune, lors de l'�quinoxe." Il s'agit de la valeur moyenne du marnage pour les mar�es de vive-eau d'�quinoxe.
				
				Le r�sultat est un nombre sans dimension compris entre 20 et 120 et qui varie peu d'un jour sur l'autre. 
				Par convention, le coefficient de mar�es 100 est attribu� au marnage semi-diurne moyen des mar�es de vives-eaux voisines des �quinoxes (21 mars et septembre).
				
			Exemples de coefficients de mar�e:
			----------------------------------	
				Voici quelques exemples de coefficients de mar�e pour bien comprendre la partie th�orique :
					Mar�es extraordinaires de vive-eau d'�quinoxe, le marnage � Brest est admis � 7.32 m�tres, d'o� le coefficient de mar�e maximal de 120.
					Mar�es de vive-eau moyenne : coefficient de mar�es �gal � 95
					Mar�es moyennes : coefficient de mar�es �gal � 70
					Mar�es de morte-eau moyenne : 45- Mar�es de morte-eau les plus faibles, le marnage � Brest est admis � 1.22 m�tres, d'o� le coefficient de mar�e minimal de 20.

				Malgr� tout, dans un m�me port, les hauteurs d'eau pr�dites, correspondant � un m�me coefficient de mar�e, 
				peuvent �tre l�g�rement diff�rentes. En effet, la d�termination des coefficients de mar�es se fait � partir du marnage. 
				Or celui-ci est calcul� en ne tenant compte que des ondes semi-diurnes, alors que les hauteurs d'eau pr�dites, 
				elles, sont calcul�es en prenant en compte toutes les ondes.
				
				
				
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
