package sfa.dev.maree.tools;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class MareeEnv {

	public final static int IdREFMAR_BREST = 3;
	
	public static SimpleDateFormat _sdfCode = null;
	public static SimpleDateFormat _sdfLog = null;
	public static SimpleDateFormat _sdfUI = null;
	public static boolean isInit = MareeEnv.initAll();
	
	static final private boolean initAll()  {
		_sdfCode = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRANCE); 
		_sdfCode.setTimeZone(TimeZone.getTimeZone("UTC"));

		_sdfUI = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRANCE); 
		_sdfUI.setTimeZone(TimeZone.getTimeZone("CET"));

		_sdfLog = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRANCE); 
		_sdfLog.setTimeZone(TimeZone.getTimeZone("CET"));
		return true;
	}

}
