package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import at.jku.tk.hermes.action.EmailSendAction;

/* Class **********************************************************************/

public final class EmailClientProtocol extends AbstractProtocol {

	/* Constructors ***********************************************************/

	public EmailClientProtocol() {
		addSupportedAction(EmailSendAction.class);
	}

}
