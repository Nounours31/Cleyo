package sfa.dev.maree.tools;

public enum eSfaLoggerLevel {
	lowest (0, "ALL", 'L'),
	debug (300, "DEBUG", 'D'),
	info (400, "Info", 'I'),
	fatal (500, "PROD", 'P');
	
	
	private int _level;
	private String _nom;
	private char _code;
	private eSfaLoggerLevel(int l, String n, char c) {
		_level = l;
		_nom = n;
		_code = c;
	}
	
	public int get_level() {
		return _level;
	}
	public String get_nom() {
		return _nom;
	}

	@Override
	public String toString() {
		return _nom;
	}

	public char getCode() {
		return _code;
	}
	
	public boolean isGreaterOrEqualThan (eSfaLoggerLevel x) {
		return (this._level >= x._level);
	}

	public boolean isLessOrEqualThan (eSfaLoggerLevel x) {
		return (this._level <= x._level);
	}

	
}
