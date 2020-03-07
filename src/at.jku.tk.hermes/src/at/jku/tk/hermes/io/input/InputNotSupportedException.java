package at.jku.tk.hermes.io.input;

/* Class **********************************************************************/

public class InputNotSupportedException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = -1555831552913682351L;

	/* Constructors ***********************************************************/

	public InputNotSupportedException() {}

	public InputNotSupportedException(String message) {
		super(message);
	}

	public InputNotSupportedException(Throwable cause) {
		super(cause);
	}

	public InputNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

}
