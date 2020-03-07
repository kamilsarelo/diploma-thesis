package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import at.jku.tk.hermes.action.EmailSendAction;

/* Class **********************************************************************/

public final class EmailSmtpProtocol extends AbstractProtocol {

	/* Constructors ***********************************************************/

	public EmailSmtpProtocol() {
		addSupportedAction(EmailSendAction.class);
	}

}
