package sfa.dev.maree.tools;

import java.io.File;

public class MareeEnv {

	private static String RepDonnee = "E:\\WS\\svn\\maree\\data";
	private static String RepDonneeDownload = "E:\\WS\\svn\\maree\\data\\download";
	private static String RepDonneeDownloadCorrigee = "E:\\WS\\svn\\maree\\data\\downloadCorrigee";
	private static String RepDonneeCheck = "E:\\WS\\svn\\maree\\data\\Check";

	
	private static String WSBase = "E:/WS/GitHubPerso/Cleyo/MarrePredictionHarmonique";
	private static String RepCoef = MareeEnv.WSBase + File.separatorChar + "Data";

	public static String getRepCoef() {
		return RepCoef;
	}

	public final static int IdREFMAR_BREST = 3;
}
