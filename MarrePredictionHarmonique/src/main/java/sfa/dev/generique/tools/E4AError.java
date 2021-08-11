package sfa.dev.generique.tools;


public class E4AError  {
	public static final E4AError None = new E4AError ();
	public static final E4AError Fail = new E4AError (1, "Unknown");

	private int _code;
	private String _msg;

	public E4AError() {
		_code = 0;
		_msg = "SUCCESS";
	}


	public E4AError(int i, String message) {
		_code = i;
		_msg = message;
	}

	public E4AError(String message) {
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
