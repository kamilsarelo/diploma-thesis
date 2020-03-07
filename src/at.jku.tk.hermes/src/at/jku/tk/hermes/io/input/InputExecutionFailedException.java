package at.jku.tk.hermes.io.input;

/* Class **********************************************************************/

public class InputExecutionFailedException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = 6402106611549691368L;

	/* Constructors ***********************************************************/

	public InputExecutionFailedException() {}

	public InputExecutionFailedException(String message) {
		super(message);
	}

	public InputExecutionFailedException(Throwable cause) {
		super(cause);
	}

	public InputExecutionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
