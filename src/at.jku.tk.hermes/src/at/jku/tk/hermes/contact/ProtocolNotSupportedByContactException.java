package at.jku.tk.hermes.contact;

/* Class **********************************************************************/

public class ProtocolNotSupportedByContactException extends Exception {

	/* Constants **************************************************************/

	private static final long serialVersionUID = -1475966230806357742L;

	/* Constructors ***********************************************************/

	public ProtocolNotSupportedByContactException() {}

	public ProtocolNotSupportedByContactException(String message) {
		super(message);
	}

	public ProtocolNotSupportedByContactException(Throwable cause) {
		super(cause);
	}

	public ProtocolNotSupportedByContactException(String message, Throwable cause) {
		super(message, cause);
	}

}
