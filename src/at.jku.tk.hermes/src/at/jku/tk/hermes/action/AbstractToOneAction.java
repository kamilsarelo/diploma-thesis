package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public abstract class AbstractToOneAction implements Action {

	/* Fields *****************************************************************/

	protected Contact contact;

	/* Constructors ***********************************************************/

	public AbstractToOneAction(Contact contact) {
		setContact(contact);
	}

	/* Methods ****************************************************************/

	/* Public */

	public Contact getContact() {
		if (contact == null) {
			throw new NullPointerException();
		}
		return contact;
	}

	public void setContact(Contact contact) {
		if (contact == null) {
			throw new NullPointerException();
		}
		this.contact = contact;
	}

}
