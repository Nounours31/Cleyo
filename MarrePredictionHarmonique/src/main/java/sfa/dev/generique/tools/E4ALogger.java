package sfa.dev.generique.tools;

import java.util.Date;
import sfa.dev.maree.tools.MareeEnv;

public final class E4ALogger {
	private static eE4ALoggerLevel _envLevel = eE4ALoggerLevel.fatal;
	private String _id = null;
	
	public static void setEnvLevel (String x) {
		if (x == null) {
			 _envLevel = eE4ALoggerLevel.debug;
			 return;
		}
			
		String y = x.toLowerCase();
		switch (y) {
			case "lowest" : _envLevel = eE4ALoggerLevel.lowest; break;
			case "debug" : _envLevel = eE4ALoggerLevel.debug; break;
			case "info" : _envLevel = eE4ALoggerLevel.info; break;
			case "fatal" : _envLevel = eE4ALoggerLevel.fatal; break;
			default : _envLevel = eE4ALoggerLevel.debug; break;
		}
	}

	public static eE4ALoggerLevel getEnvLevel () {
		return E4ALogger._envLevel;
	}
	
	public String getLogLevel() {
		return E4ALogger._envLevel.get_nom();
	}

	private E4ALogger(String Id) {
		_id = Id;
	}
	
	public synchronized static E4ALogger getLogger(String Id) {
		E4ALogger x = new E4ALogger (Id);
		return x;
	}



	public void lowest(String msg) {
		this._log(eE4ALoggerLevel.lowest, msg);
	}

	public void debug(String msg) {
		this._log(eE4ALoggerLevel.debug, msg);
	}

	public void info(String msg) {
		this._log(eE4ALoggerLevel.info, msg);
	}

	public void fatal(String msg) {
		this._log(eE4ALoggerLevel.fatal, msg);		
	}

	
	public boolean isActive(eE4ALoggerLevel l) {
		boolean retour = false;
		if (E4ALogger._envLevel.isLessOrEqualThan(l)) {
			retour = true;
		}
		return retour;
	}

	private synchronized void _log (eE4ALoggerLevel l, String msg) {
		if (this.isActive(l)) {
			StringBuffer sb = new StringBuffer();
			sb.append(MareeEnv._sdfLog.format(new Date()));
			sb.append(String.format("[%c>%c][%s]", l.getCode(), E4ALogger._envLevel.getCode(), _id));
			sb.append(msg);
			System.err.println(sb.toString());
		}
		return;
	}


}
