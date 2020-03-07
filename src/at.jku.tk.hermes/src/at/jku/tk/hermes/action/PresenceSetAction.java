package at.jku.tk.hermes.action;

/* Class **********************************************************************/

public final class PresenceSetAction implements Action {

	/* Enumerations ***********************************************************/

	public enum Status {

		/* Enumeration ********************************************************/

		OFFLINE("OFFLINE"),
		ONLINE("ONLINE"),
		AWAY("AWAY"),
		NOT_AVAILABLE("NOT_AVAILABLE"),
		OCCUPIED("OCCUPIED"),
		DO_NOT_DISTURB("DO_NOT_DISTURB"),
		FREE_FOR_CHAT("FREE_FOR_CHAT"),
		INVISIBLE("INVISIBLE"),
		OUT_TO_LUNCH("OUT_TO_LUNCH"),
		ON_THE_PHONE("ON_THE_PHONE");

		/* Constructor ********************************************************/

		private Status(String status) {}

	}

	/* Fields *****************************************************************/

	private Status status;

	/* Constructors ***********************************************************/

	public PresenceSetAction(Status status) {
		setStatus(status);
	}

	/* Methods ****************************************************************/

	/* Public */

	public Status getStatus() {
		if (status == null) {
			throw new NullPointerException();
		}
		return status;
	}

	public void setStatus(Status status) {
		if (status == null) {
			throw new NullPointerException();
		}
		this.status = status;
	}

}
