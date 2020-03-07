package at.jku.tk.hermes.tool;

/* Class **********************************************************************/

public class ProtocolNotSupportedByToolException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = -6357839888823022373L;

	/* Constructors ***********************************************************/

	public ProtocolNotSupportedByToolException() {}

	public ProtocolNotSupportedByToolException(String message) {
		super(message);
	}

	public ProtocolNotSupportedByToolException(Throwable cause) {
		super(cause);
	}

	public ProtocolNotSupportedByToolException(String message, Throwable cause) {
		super(message, cause);
	}

}
