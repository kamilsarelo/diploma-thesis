package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public final class MessageSendToManyAction extends AbstractToManyAction {

	/* Fields *****************************************************************/

	private String message;

	/* Constructors ***********************************************************/

	public MessageSendToManyAction(Contact contact, String message, Contact... contacts) {
		super(contact, contacts);
		setMessage(message);
	}

	/* Methods ****************************************************************/

	/* Public */

	public String getMessage() {
		if (message == null) {
			throw new NullPointerException();
		}
		return message;
	}

	public void setMessage(String message) {
		if (message == null) {
			throw new NullPointerException();
		}
		this.message = message;
	}

}
