package sfa.dev.maree.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class SfaLogger {
	private static eSfaLoggerLevel _envLevel = eSfaLoggerLevel.fatal;
	private String _id = null;
	private final SimpleDateFormat sdf = new SimpleDateFormat("[MM-dd_HH:mm:ss]", Locale.US); 
	
	public static void setEnvLevel (String x) {
		if (x == null) {
			 _envLevel = eSfaLoggerLevel.debug;
			 return;
		}
			
		String y = x.toLowerCase();
		switch (y) {
			case "lowest" : _envLevel = eSfaLoggerLevel.lowest; break;
			case "debug" : _envLevel = eSfaLoggerLevel.debug; break;
			case "info" : _envLevel = eSfaLoggerLevel.info; break;
			case "fatal" : _envLevel = eSfaLoggerLevel.fatal; break;
			default : _envLevel = eSfaLoggerLevel.debug; break;
		}
	}

	public static eSfaLoggerLevel getEnvLevel () {
		return SfaLogger._envLevel;
	}
	
	public String getLogLevel() {
		return SfaLogger._envLevel.get_nom();
	}

	private SfaLogger(String Id) {
		_id = Id;
	}
	
	public synchronized static SfaLogger getLogger(String Id) {
		SfaLogger x = new SfaLogger (Id);
		return x;
	}



	public void debug(String msg) {
		this._log(eSfaLoggerLevel.debug, msg);
	}

	public void info(String msg) {
		this._log(eSfaLoggerLevel.info, msg);
	}

	public void fatal(String msg) {
		this._log(eSfaLoggerLevel.fatal, msg);		
	}

	
	public boolean isActive(eSfaLoggerLevel l) {
		boolean retour = false;
		if (SfaLogger._envLevel.isLessOrEqualThan(l)) {
			retour = true;
		}
		return retour;
	}

	private synchronized void _log (eSfaLoggerLevel l, String msg) {
		if (this.isActive(l)) {
			StringBuffer sb = new StringBuffer();
			sb.append(sdf.format(new Date()));
			sb.append(String.format("[%c>%c][%s]", l.getCode(), SfaLogger._envLevel.getCode(), _id));
			sb.append(msg);
			System.err.println(sb.toString());
		}
		return;
	}


}
