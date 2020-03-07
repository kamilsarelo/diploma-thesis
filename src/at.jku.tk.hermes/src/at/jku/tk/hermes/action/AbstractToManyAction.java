package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import java.util.ArrayList;
import java.util.List;

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public abstract class AbstractToManyAction implements Action {

	/* Fields *****************************************************************/

	protected final List<Contact> contacts = new ArrayList<Contact>();


	/* Constructors ***********************************************************/

	public AbstractToManyAction(
			Contact contact,
			Contact... contacts
	) {
		addContact(contact);
		for (Contact furtherContact : contacts) {
			addContact(furtherContact);
		}
	}

	/* Methods ****************************************************************/

	/* Public */

	public List<Contact> getContacts() {
		synchronized (contacts) {
			List<Contact> clone = new ArrayList<Contact>();
			for (Contact contact : contacts) {
				clone.add(contact);
			}
			return clone;
		}
	}

	public boolean addContact(Contact contact) {
		if (contact == null) {
			throw new NullPointerException();
		}
		synchronized (contacts) {
			if (! contacts.contains(contact)) {
				return contacts.add(contact);
			}
		}
		return false;
	}

	public boolean removeContact(Contact contact) {
		if (contact == null) {
			throw new NullPointerException();
		}
		synchronized (contacts) {
			return contacts.remove(contact);
		}
	}

}
