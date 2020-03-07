package at.jku.tk.hermes.tool;

/* Class **********************************************************************/

public class ActionNotSupportedException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = 8512467514529700502L;

	/* Constructors ***********************************************************/

	public ActionNotSupportedException() {}

	public ActionNotSupportedException(String message) {
		super(message);
	}

	public ActionNotSupportedException(Throwable cause) {
		super(cause);
	}

	public ActionNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

}
