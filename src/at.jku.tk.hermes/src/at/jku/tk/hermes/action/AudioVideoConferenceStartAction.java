package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public final class AudioVideoConferenceStartAction extends AbstractToManyAction {

	/* Constructors ***********************************************************/

	public AudioVideoConferenceStartAction(Contact contact, Contact... contacts) {
		super(contact, contacts);
	}

}
