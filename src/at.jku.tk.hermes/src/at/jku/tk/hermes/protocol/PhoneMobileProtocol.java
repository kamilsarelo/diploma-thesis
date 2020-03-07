package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import at.jku.tk.hermes.action.MessageSendToManyAction;
import at.jku.tk.hermes.action.MessageSendToOneAction;
import at.jku.tk.hermes.action.AudioCallStartAction;

/* Class **********************************************************************/

public final class PhoneMobileProtocol extends AbstractProtocol {

	/* Constructors ***********************************************************/

	public PhoneMobileProtocol() {
		addSupportedAction(AudioCallStartAction.class);
		addSupportedAction(MessageSendToOneAction.class);
		addSupportedAction(MessageSendToManyAction.class);
	}

}
