package sfa.dev.maree.harmonique.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import Jama.Matrix;
import sfa.dev.maree.tools.Constantes;
import sfa.dev.maree.tools.SfaException;
import sfa.dev.maree.tools.SfaLogger;

public class calculHarmonicFromSHOMData_readMareeGramme 
{
	private File _f = null;
	
	public calculHarmonicFromSHOMData_readMareeGramme() {} 
	
	public void setMareeGramme (File f) throws SfaException {
		if (!f.exists())
			throw new SfaException("Invalide maree grame file");
		
		_f = f;
	}


	private StringBuffer readAllFile (File f) throws IOException {
		StringBuffer retour = new StringBuffer();  
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = br.readLine()) != null) {
			retour.append(line);
		}
		br.close();
		return retour;
	}
	
	public void readValueFromFiles() throws Exception
	{
		List<PointDeMeusureMaree> retour = new ArrayList<PointDeMeusureMaree>();
		File directoryWithData = this._f;
		if (!directoryWithData.exists() || !directoryWithData.isDirectory())
			throw new SfaException ("Invalid rep");
		
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(".json"));
			}
		};
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTF8"));
		for (File maregrame: directoryWithData.listFiles(filter )) {
			StringBuffer sb = readAllFile(maregrame);
			JSONObject json = new JSONObject(sb.toString());
			
			JSONArray jArray = json.getJSONArray("data");
			for (Object oneData : jArray) {
				float value = ((JSONObject)oneData).getFloat("value");
				Date date = sdf.parse(((JSONObject)oneData).getString("timestamp"));
				PointDeMeusureMaree x = new PointDeMeusureMaree(date, value);
				retour.add(x);
			}
		}
		Collections.sort(retour);

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
