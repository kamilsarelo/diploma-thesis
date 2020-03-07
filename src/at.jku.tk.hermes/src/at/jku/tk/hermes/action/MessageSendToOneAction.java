package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public final class MessageSendToOneAction extends AbstractToOneAction {

	/* Fields *****************************************************************/

	private String message;

	/* Constructors ***********************************************************/

	public MessageSendToOneAction(Contact contact, String message) {
		super(contact);
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
