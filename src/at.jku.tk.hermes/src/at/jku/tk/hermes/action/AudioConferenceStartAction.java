package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public final class AudioConferenceStartAction extends AbstractToManyAction {

	/* Constructors ***********************************************************/

	public AudioConferenceStartAction(Contact contact, Contact... contacts) {
		super(contact, contacts);
	}

}
