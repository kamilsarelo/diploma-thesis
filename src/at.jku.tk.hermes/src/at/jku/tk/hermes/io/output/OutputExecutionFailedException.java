package at.jku.tk.hermes.io.output;

/* Class **********************************************************************/

public class OutputExecutionFailedException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = 4985105109637401737L;

	/* Constructors ***********************************************************/

	public OutputExecutionFailedException() {}

	public OutputExecutionFailedException(String message) {
		super(message);
	}

	public OutputExecutionFailedException(Throwable cause) {
		super(cause);
	}

	public OutputExecutionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
