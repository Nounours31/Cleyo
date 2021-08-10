package sfa.dev.maree.tools;


public class SfaError  {
	public static final SfaError None = new SfaError ();
	public static final SfaError Fail = new SfaError (1, "Unknown");

	private int _code;
	private String _msg;

	public SfaError() {
		_code = 0;
		_msg = "SUCCESS";
	}


	public SfaError(int i, String message) {
		_code = i;
		_msg = message;
	}

	public SfaError(String message) {
		_code = 1;
		_msg = message;
	}

	
	public boolean SUCCEEDED () {
		return (_code > 0 ? false : true);
	}
	
	
	@Override
	public String toString() {
		if (this._msg == null)
			this._msg = "FAIL - No Info, regarder la log du tomee avec un peu de bol ...";
		
		if (this._msg.length() == 0) {
			if (this.SUCCEEDED()) return "SUCCESS";
			return "FAIL";
		}
		return this._msg;
	}

}
