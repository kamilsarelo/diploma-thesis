package at.jku.tk.hermes.io.output;

/* Class **********************************************************************/

public class OutputNotSupportedException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = 8862833297376528722L;

	/* Constructors ***********************************************************/

	public OutputNotSupportedException() {}

	public OutputNotSupportedException(String message) {
		super(message);
	}

	public OutputNotSupportedException(Throwable cause) {
		super(cause);
	}

	public OutputNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

}
