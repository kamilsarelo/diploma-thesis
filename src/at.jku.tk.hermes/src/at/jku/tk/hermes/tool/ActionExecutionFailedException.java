package at.jku.tk.hermes.tool;

/* Class **********************************************************************/

public class ActionExecutionFailedException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = 8512467514529700502L;

	/* Constructors ***********************************************************/

	public ActionExecutionFailedException() {}

	public ActionExecutionFailedException(String message) {
		super(message);
	}

	public ActionExecutionFailedException(Throwable cause) {
		super(cause);
	}

	public ActionExecutionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
