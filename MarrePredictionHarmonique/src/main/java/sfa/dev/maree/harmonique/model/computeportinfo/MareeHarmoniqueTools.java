package sfa.dev.maree.harmonique.model.computeportinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import Jama.Matrix;
import sfa.dev.generique.tools.E4ALogger;
import sfa.dev.maree.harmonique.model.Astronomie;
import sfa.dev.maree.harmonique.model.Onde;
import sfa.dev.maree.harmonique.model.Ondes;
import sfa.dev.maree.harmonique.model.Trigo;



public class MareeHarmoniqueTools {
	class CoupleEpochHauteur {
		long epoch;
		double hauteur;
		
		@Override
		public String toString() {
			return String.format("TimeStamp: %d --- Hauteur: %f", epoch, hauteur);
		}
	}
	
	
	static SimpleDateFormat _sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
	E4ALogger _logger = E4ALogger.getLogger(MareeHarmoniqueTools.class.getCanonicalName());
			
	
	public MareeHarmoniqueTools() {
	}
	
	public void computeHarmonique (int portId) throws ParseException, IOException {
		// ------------------------------------
		// UTC
		// ------------------------------------
		_sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		// ------------------------------------
		// Read donnee de maree de un port
		// ------------------------------------
		File portEchantillon = new File ("E:\\WS\\GitHubPerso\\Cleyo\\MarrePredictionHarmonique\\DataNotInGit\\Brest\\3_2020.json");
		InputStream is = new FileInputStream(portEchantillon);
		JSONTokener tokener = new JSONTokener(is);
        JSONObject rootObject = new JSONObject(tokener);
        JSONArray jsonData = rootObject.getJSONArray("data");
        int nbEchantillon = jsonData.length();
        _logger.debug("NbEchantillon: " + nbEchantillon);
        
		
		// ------------------------------------
		// ------------------------------------
        _logger.debug("Start import des echantillons");
        CoupleEpochHauteur[] _data = new CoupleEpochHauteur[jsonData.length()];
        for (int iIndice = 0; iIndice < jsonData.length(); iIndice++) {
        	JSONObject uneSonde = jsonData.getJSONObject(iIndice);
        	String timeStamp = uneSonde.getString("timestamp");
        	double hauteur = uneSonde.getDouble("value");
        	_logger.lowest(String.format("TimeStamp: %s --- Hauteur: %f", timeStamp, hauteur));
        	
        	long epoch = _sdf.parse (timeStamp).getTime();        	
        	_data[iIndice] = new CoupleEpochHauteur();
        	_data[iIndice].epoch = epoch;
        	_data[iIndice].hauteur = hauteur;
        	_logger.lowest(_data[iIndice].toString());
		}
        
        
		// ------------------------------------
        // Pour chaque horaire calcul de la valeur de la sonde
		// ------------------------------------
		File portHarmonique = new File ("E:\\WS\\GitHubPerso\\Cleyo\\MarrePredictionHarmonique\\DataNotInGit\\Brest\\Harmonique2020.txt");
        _logger.debug("Start calcul des harmoniques vers " + portHarmonique.getAbsolutePath());
        computeHarmoniqueFromData (_data, portHarmonique);
		
	}

	private void computeHarmoniqueFromData(CoupleEpochHauteur[] _data, File portHarmonique) throws IOException {
		// ------------------------------------
		// Calcul du Z0 du port
		// ------------------------------------
		double Z0 = 0.0;
		double Z0_tampon = 0.0;
		int iIndice = 0, iIndice_tampon = 0, tampon = 1024, nbData = _data.length;
		while (iIndice < nbData) {
			while ((iIndice_tampon < tampon) && (iIndice < nbData)) {
				_logger.lowest("h: " + _data[iIndice].hauteur);
				Z0_tampon += _data[iIndice].hauteur;
				iIndice_tampon++;
				iIndice++;
			}
			Z0 += Z0_tampon / ((double)nbData);
			
			Z0_tampon = 0.0;
			iIndice_tampon = 0;
		}
		_logger.debug("Z0 " + Z0);
		
		// Calcul de la hauteur 
		//------------------------------------------------------------------
		// Construction de la matrice
		//------------------------------------------------------------------
		int nbOnde = Ondes._table2NC.length;
		double[] 	vecteur_H = new double [nbData];
		double[][] 	matrix_A = new double [nbData][2 * nbOnde ];
		for (iIndice = 0; iIndice < nbData; iIndice++)
		{			
			Astronomie astrePosition = new Astronomie(_data[iIndice].epoch);
			Ondes.InitEquilibriumAndPhase (astrePosition.s, astrePosition.h, astrePosition.p, astrePosition.p1, astrePosition.N);
			double heureDecimal = astrePosition.heureDecimale;
			
			vecteur_H[iIndice] = _data[iIndice].hauteur - Z0;
			
			int ondeIndice = 0;
			for (Onde o  : Ondes._table2NC) {
				double var = Trigo.reduc360(o._speed * heureDecimal + o._equilibrium);
				double ai = o._nodeFactor * Trigo.cos_deg  (var);
				double bi = o._nodeFactor * Trigo.sin_deg (var);
				matrix_A[iIndice][ondeIndice] = ai; 
				matrix_A[iIndice][ondeIndice + nbOnde] = bi; 
				ondeIndice++;
			}
			if (iIndice % 100 == 0)
				_logger.info(String.format("Mise en matrice de %d / %d", iIndice, nbData));
		}
		_logger.info("Fin de la mise en matrice");

		
		Matrix A = new Matrix(matrix_A);
		_logger.info("Start transpose ...");
		Matrix At = A.transpose();
		_logger.info("OK transpose");

		Matrix X = At.times(A);
		_logger.info("OK At * A");

		Matrix invX = X.inverse();
		_logger.info("OK inverse (At * A)");

		Matrix P = invX.times(At);
		_logger.info("OK inverse (At * A) * At");

		Matrix H = new Matrix(vecteur_H, vecteur_H.length);
		Matrix R = P.times(H);
		_logger.info("OK solution");
		
		File ondes = portHarmonique;
		StringBuffer sb = new StringBuffer();
		sb.append("\"" + "\n");
		sb.append("\"constantes calculées" + "\n");
		sb.append("\"Nom	Amplitude	Phase" + "\n");
		sb.append("METRIC    0   0   0  1\n");
		//------------------------------------------
		// rappel
		//	on a pose: 
		//		x0 = Z0
		//		xi = Ai cos gi
		//		yi = Ai sin gi
		//------------------------------------------
		sb.append("Z0     " + Z0+ "\n");
		
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
			phase *= Trigo.Rad2Deg;
			sb.append(Ondes._table2NC[k]._Nom + "      " + ampli + "      " + phase + "\n");
		}
		FileWriter fw = new FileWriter(ondes, false);
		fw.write(sb.toString());
		fw.close();
		System.out.println(sb.toString());
		System.out.println("Fin!");
	}
}
