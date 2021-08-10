package sfa.dev.maree.harmonique.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import Jama.Matrix;
import sfa.dev.maree.tools.Constantes;
import sfa.dev.maree.tools.SfaException;
import sfa.dev.maree.tools.SfaLogger;

public class calculHarmonicFromSHOMData 
{
	private ArrayList<Double> 				echantillonsDuJour = null; // jourjulienEchantillon [nb jour]; 
	private ArrayList<ArrayList<Double>> 	echantillonsDeTousLesJours = null; // echantillon [nb jour] [heure] avec 0 <= heure < 24; 
	private double[][] matrix_A = null;  //  liste de [nb onde] de vecteur contenant [nb echantillon]
	private double[] vecteur_H = null;  //  vecteur_H [nb echantillon]
	
	private SfaLogger _log = null;
	private File _dataFile = null;
	private File _td4File = null;
	


	/*========================================================================================
	  from : http://cleyo.free.fr/marees/DetermineHarmo.php

	  Methode:
	  Détermination des constantes harmoniques

			Si l'on ne dispose pas des constantes pour le port auquel on s'intéresse, on pourra soit essayer de les évaluer à partir des constantes d'un port proche, soit les calculer. Il faut pour cela disposer de nombreuses observations de la marée dans le port considéré. L'idéal est de disposer de la hauteur d'eau heure par heure pour tous les jours d'une année. On procède au calcul à l'envers: on part des résultats (heures et hauteurs de la marée pour tous les jours de l'année) et on remonte à la source (les constantes) par un calcul d'analyse harmonique. On dispose d’un ensemble d’équations (une pour chaque observation). On connaît la fréquences des ondes et on recherche les coefficients qui permettent de vérifier les équations. Il faut tout d’abord exprimer, sous une forme linéaire, la fonction donnant la hauteur d’eau à l’instant t.

			h(t) = Z0 + "SOMME sur i pour chaque onde"< fi Ai cos[ Wi t + (V0 + u )i - gi ] >
			avec pour chaque onde i:
				fi: 		le node factor [nodefctr] 
				Ai: 		l'amplitude ==> inconnue
				Wi: 		la speed de l'onde
				t: 			le temps horaire de la journee
				(V0 + u )i: l'equilibrium
				gi: 		la phase ==> inconnue



			On sait que cos(a-b )= cosa cosb + sina sinb . On en déduit

			h(t) = Z0 + "SOMME sur i pour chaque onde"< fi cos[ Wi t + (V0+ u )i ] Ai cos gi + fi sin[ Wi t + (V0+ u )i ] Ai sin gi>

			Posons: x0 = Z0
			xi = Ai cos gi
			yi = Ai sin gi


			h(t) = x0 + "SOMME sur i pour chaque onde"< fi cos[ Wi t + ( V0 + u)i ] xi + fi sin[ Wi t + ( V0+ u )i ] yi >

			C'est une équation de la forme h(t) = 1 x0 + a1 x1 + a2 x2 + a3 x3 +… … … + an xn + b1 y1 + b2 y2 + b3 y3 +… … … + bn yn
			avec ai = fi cos[ Wi t + (V0+ u )i ] et bi = fi sin[ Wi t + (V0+ u )i ]
			ou
				ai = nodefctr[i] * cos (speed[i] * t + equilibrium[i])
				bi = nodefctr[i] * SIN (speed[i] * t + equilibrium[i])

			les termes connus sont h(t) 1 a1 a2 … … … a>n-1 an b1 b2 … … … bn-1 bn
			les inconnues x0 x1 x2… … … xn-1 xn y1 y2 … … … yn-1 yn

			Pour obtenir des résultats valables, il faut disposer d'au moins une année d'observation. 
			Si l’on observe la marée d’heure en heure pendant un an on disposera de 365 x 24 = 8760 équations !

			Le nombre d’inconnues pourra varier entre une vingtaine (si l’on se contente de calculer les principales composantes) 
			à plus d’une centaine (si l’on cherche à déterminer le plus de composantes  possibles). 

			L’ensemble de ces équations forme un système d’équations linéaires.
			La solution à ce type de système peut être trouvée grâce au calcul matriciel. Il n’y a pas une solution unique 
			mais un ensemble de solutions approchées. On cherchera la solution qui 
			minimise l’écart type : moyenne des carrés de la différence entre la hauteur calculée et la hauteur observée. 
			On forme tout d'abord la matrice A ayant
				- un nombre de lignes égal au nombre d'observations.
				- un nombre de colonnes égal à 1 + 2 fois le nombre de constantes recherchées.

			On remplit la matrice avec les valeurs 1 a1 a2 ……… an-1 an b1 b2 ……… bn-1 bn 
			On forme le vecteur H avec les hauteurs observées h(t)
			On calcule la matrice A_t transposée de A 
					(le nombre de lignes et de colonnes de A_t est égal au nombre de colonnes et de lignes de A)
			On calcule la matrice X produit de A_t par A (la matrice X est une matrice carrée)
			On calcule la matrice X-1 inverse de X puis la matrice P produit de X-1 par A_t
			Le vecteur résultat R est égal au produit de P par H

			c'est : http://fr.wikipedia.org/wiki/M%C3%A9thode_des_moindres_carr%C3%A9s
				Dans le cas d'équations linéaires surdéterminées à coefficients constants, il existe une solution simple. 
				Si nous disposons d'équations expérimentales surdéterminées sous la forme A.x = b
				La norme du résidu est minimum si et seulement si satisfait les équations normales :
						(At A)x = At b

	======================================================================================*/

	public calculHarmonicFromSHOMData(File infile, File outfile) 
	{
		_log =  SfaLogger.getLogger(calculHarmonicFromSHOMData.class.getCanonicalName());
		_dataFile = infile;
		_td4File = outfile;
		
		this.init();
	}
	
	public void init() 
	{
		try
		{
			readValueFromFile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public void eval() throws IOException
	{
		/*            <-    nb onde  (colone)->
		 * Matrix_A = ( 1 A1 ... An   B1 ... Bn)   ^
		 * 			  (						   )   | nb echantillon (ligne)
		 * 			  ( 1 A1 ... An   B1 ... Bn)	v
		 *
		 * A(i, j) i eme ligne; jieme colone
		 *  donc 0 < i < nb echantillon
		 *  donc 0 < j < nb onde
		 */ 
		/*-----------------------------------------------------
		Pour chaque jour valide (23 echantillons)
			calcul des info astro du jour
			Pour chaque onde
				calcul de l'equilibrium
				calcul du node factor
				calcul de 
					ai = nodefctr[i] * cos (speed[i] * t + equilibrium[i])
					bi = nodefctr[i] * SIN (speed[i] * t + equilibrium[i])
		-----------------------------------------------------*/
		_log.debug("Nb jour julien = " + echantillonsDuJour.size());
		_log.debug("Nb jour d'echantillon = " + echantillonsDeTousLesJours.size());
		_log.debug("Echantillon par jour (stat du jour 1) = " + echantillonsDeTousLesJours.get(0).size());
		_log.debug("Y a t il des pb de jour non consecutif ? " + checkJourConsecutif (echantillonsDuJour));
		
		int nbEchantillonTotal = getNbechantillon (echantillonsDeTousLesJours);
		int indiceechantillon = 0;
		vecteur_H = new double [nbEchantillonTotal];
		for (int i = 0; i < echantillonsDeTousLesJours.size(); i++)
		{
			for (int j = 0; j < echantillonsDeTousLesJours.get(i).size(); j++)
			{
				vecteur_H[indiceechantillon] = echantillonsDeTousLesJours.get(i).get(j);
				indiceechantillon++;
			}
		}

		//------------------------------------------------------------------
		// calcul de l'onde moyenne
		//------------------------------------------------------------------
		double z0 = 0.0;
		for (int i = 0; i < vecteur_H.length; i++)
			z0 += vecteur_H[i];		
		z0 /= (double)(vecteur_H.length);

		
		//------------------------------------------------------------------
		// Construction de la matrice
		//------------------------------------------------------------------
		indiceechantillon = 0;
		for (int i = 0; i < echantillonsDeTousLesJours.size(); i++)
		{
			// pour i : liste des echantillons du jour
			GregorianCalendar gc = CalculAstroInterne.calculGregorianFromJD(echantillonsDuJour.get(i));
			CalculAstroInterne _cai = new CalculAstroInterne(echantillonsDuJour.get(i), gc);
			CalculMareeInterne _cmi = new CalculMareeInterne (null, _cai.s, _cai.h, _cai.p, _cai.p1, _cai.N);

			// pour j : les 24 du jour
			for (int j = 0; j < echantillonsDeTousLesJours.get(i).size(); j++)
			{
				if (matrix_A == null)
					matrix_A = new double[nbEchantillonTotal][2 * _cmi.cHarmo._table2NC.length];

				vecteur_H[indiceechantillon] = echantillonsDeTousLesJours.get(i).get(j) - z0;

				for (int k = 0; k < _cmi.cHarmo._table2NC.length; k++)
				{
					double var = TrigoEtConstante.reduc360(_cmi.cHarmo._table2NC[k].speed * ((double)j) + _cmi.equilbrm[k]);
					double ai = _cmi.nodefctr[k] * TrigoEtConstante.cos_deg  (var);
					double bi = _cmi.nodefctr[k] * TrigoEtConstante.sin_deg (var);
					matrix_A[indiceechantillon][k] = ai; 
					matrix_A[indiceechantillon][k+_cmi.cHarmo._table2NC.length] = bi; 
				}
				
				System.out.println("Echantillon : " + indiceechantillon);
				indiceechantillon++;
			}
		}
		
		

		System.out.println("Fin de la mise en matrice");
		Matrix A = new Matrix(matrix_A);

		Matrix At = A.transpose();
		System.out.println("OK transpose");

		Matrix X = At.times(A);
		System.out.println("OK At * A");

		Matrix invX = X.inverse();
		System.out.println("OK inverse (At * A)");

		Matrix P = invX.times(At);
		System.out.println("OK inverse (At * A) * At");

		Matrix H = new Matrix(vecteur_H, vecteur_H.length);
		Matrix R = P.times(H);
		System.out.println("OK solution");
		
		File ondes = _td4File;
		FileWriter fw = new FileWriter(ondes, false);
		fw.write("\"" + "\n");
		fw.write("\"constantes calculées" + "\n");
		fw.write("\"Nom	Amplitude	Phase" + "\n");
		fw.write("METRIC    0   0   0  1\n");
		//------------------------------------------
		// rappel
		//	on a pose: 
		//		x0 = Z0
		//		xi = Ai cos gi
		//		yi = Ai sin gi
		//------------------------------------------
		fw.write("Z0     " + z0+ "\n");
		
		int nbOnde = (int)(R.getRowDimension() / 2); 
		String[] OndeName = LesConstanteHarmoniques.getOndeName();
		for (int k = 0; k < nbOnde; k++)
		{
			double xi = R.get(k, 0);
			double yi = R.get(k + nbOnde, 0);
			
			double ampli = Math.sqrt(xi * xi + yi * yi);
			if (ampli < 0.001)
				continue;
			
			double phase = Math.acos(xi / ampli);
			if (yi < 0)
				phase = 2.0 * Math.PI - phase; 
			phase *= (180.0 / Math.PI);
			_log.debug(OndeName[k] + "      " + ampli + "      " + phase);
			fw.write(OndeName[k] + "      " + ampli + "      " + phase + "\n");
		}
		fw.close();
		System.out.println("Fin!");

	}

	private String checkJourConsecutif(ArrayList<Double> jourjulienEchantillon2) 
	{
		String retour = "OK";
		double cur, prev = -1.0;
		for (int i = 0; i < jourjulienEchantillon2.size(); i++)
		{
			cur = jourjulienEchantillon2.get(i);
			if (prev < 0)
				prev = cur - 1.0;
			
			if (cur - prev != 1.0)
			{
				retour = "KO pour i = " + i; 
				break;
			}
			prev = cur;
		}
		return retour;
	}


	private int getNbechantillon(ArrayList<ArrayList<Double>> echantillons) 
	{
		int nb = 0;
		for (int i = 0; i < echantillons.size(); i++)
		{
			if (echantillons.get(i).size() != 24)
				return 0;
			else
				nb += 24;
		}
		return nb;
	}

	
	
	E:\WS\GitHubPerso\Cleyo\MarrePredictionHarmonique\Data\StMalo
	//----------------------------------------------------------------------------------------
	//- Si OK recup de hauteur, horaire
	//----------------------------------------------------------------------------------------
	private boolean getvaliddata(String line, PointDeMeusureMaree response) // date, haute 
	{
		try
		{
			// 01/01/2008 00:00:00;9.100;4
			Pattern pat = Pattern.compile("^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2};[0-9]*\\.?[0-9]*$");
			Matcher match = pat.matcher(line);
			boolean xtest = match.find();
			_log.debug("line : " + line);
			_log.debug("isvaliddata : " + xtest);

			if (xtest)
			{
				//----------------------------------------------------
				// parse de la date
				//----------------------------------------------------
				Pattern patdate = Pattern.compile("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}");
				Matcher matchdate = patdate.matcher(line);
				xtest = matchdate.find();
				_log.debug("isvaliddata  date match : " + xtest);
				
				if (xtest)
				{
					String date = matchdate.group(0);
					GregorianCalendar xdate = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.FRANCE);
					xdate.setTime(Constantes._sdf.parse(date));				
					response.set_horaireMaree(xdate);
					line = matchdate.replaceAll("xxxxxx");
				}
				else
				{
					throw new SfaException("Date match KO");
				}

				
				//----------------------------------------------------
				// parse de la hauteur
				//----------------------------------------------------
				Pattern patFloat = Pattern.compile("[0-9]*\\.[0-9]*");
				Matcher matchFloat = patFloat.matcher(line);
				xtest = matchFloat.find();
				_log.debug("isvaliddata  Float match : " + xtest);
				
				if (xtest)
				{
					double hauteur = Float.parseFloat(matchFloat.group(0));
					response.set_hauteurEau(hauteur);
				}
				else
				{
					throw new SfaException("Hauteur match KO");
				}
			}
			else
			{
				throw new SfaException("Line match KO");
			}
			return true;
		}
		catch (Exception e)
		{
			_log.fatal (e.getMessage());
		}
		return false;
	}
	

	public void readValueFromFile() throws Exception
	{
		Date StartImport = new Date();
		_log.info("Start import");

		File date = _dataFile;
		if (!date.exists())
			return;
		if (!date.isFile())
			return;

		//-------------------------------------------------------
		// Init si necessaire
		//-------------------------------------------------------
		if (echantillonsDeTousLesJours == null)
			echantillonsDeTousLesJours = new ArrayList<ArrayList<Double>>();
		if (echantillonsDuJour == null)
			echantillonsDuJour = new ArrayList<Double>();

		
		FileReader sr;
		try 
		{
			sr = new FileReader(date);
			BufferedReader br = new BufferedReader(sr);

			double cur = 0;	
			double prev = 0;
			int indicejour = 0;
			boolean init = true;
			
			ArrayList<Double> HauteurEauDeLaJournee = null;
			
			String lue = br.readLine();
			_log.info("Start read");

			while (lue != null)
			{
				//-------------------------------------------------------
				// date de l'echantillon
				//-------------------------------------------------------
				PointDeMeusureMaree x = new PointDeMeusureMaree();
				
				if (this.getvaliddata (lue, x))
				{
					//--------------------------------------------------
					// La ref des calcul est a midi
					//--------------------------------------------------
					GregorianCalendar jourcourant = x.get_horaireMaree();					
					GregorianCalendar jourcourantMidi = new GregorianCalendar(jourcourant.get(Calendar.YEAR), 
							jourcourant.get(Calendar.MONTH), 
							jourcourant.get(Calendar.DAY_OF_MONTH), 
							0, 0, 0);
										

					prev = cur;
					cur = CalculAstroInterne.calculJDFromGregorian(jourcourantMidi);
					if (init)
					{
						prev = cur;
						HauteurEauDeLaJournee = new ArrayList<Double>();
						init = false;
					}
					
					
					if (prev != cur) // un nouveau jour
					{
						echantillonsDuJour.add(prev);
						echantillonsDeTousLesJours.add(HauteurEauDeLaJournee);
						HauteurEauDeLaJournee = new ArrayList<Double>();
						prev = cur;
						_log.info("Ajout de " + prev);
					}
					
					//-------------------------------------------------------
					// Ajout des echantillons
					//-------------------------------------------------------
					HauteurEauDeLaJournee.add(x.get_hauteurEau());
					
					//-------------------------------------------------------
					// Suivant
					//-------------------------------------------------------
					lue = br.readLine();
				}
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} catch (IOException e) {
			e.printStackTrace();
			throw (e);
		}
		finally
		{
			long dureeImport = (new Date()).getTime() - StartImport.getTime();	
			_log.info("End import. Duree (ms) = " + dureeImport);
		}
		_log.info(DumpListCouplee (echantillonsDuJour, echantillonsDeTousLesJours));
	}
	
	public static String DumpListCouplee(ArrayList<Double> jourjulienEchantillon, ArrayList<ArrayList<Double>> echantillons) 
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < jourjulienEchantillon.size(); i++)
		{
			sb.append("Jour  : " + Constantes._sdf.format(CalculAstroInterne.calculGregorianFromJD(jourjulienEchantillon.get(i)).getTime()));
			sb.append(" # Jour Julien : " + jourjulienEchantillon.get(i));
			ArrayList<Double> hauteurs = echantillons.get(i);

			for (int j = 0; j < hauteurs.size(); j++)
			{
				sb.append("[(" + j +":00)-("+hauteurs.get(j)+")]");
			}
		}
		return sb.toString();
	}
}
