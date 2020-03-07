package at.jku.tk.hermes.core;

/* Class **********************************************************************/

public class ParameterException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = -7874656658604634909L;

	/* Constructors ***********************************************************/

	public ParameterException() {}

	public ParameterException(String message) {
		super(message);
	}

	public ParameterException(Throwable cause) {
		super(cause);
	}

	public ParameterException(String message, Throwable cause) {
		super(message, cause);
	}

}
