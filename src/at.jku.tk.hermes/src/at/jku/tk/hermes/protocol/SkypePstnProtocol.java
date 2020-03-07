package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import at.jku.tk.hermes.action.AudioCallStartAction;
import at.jku.tk.hermes.action.AudioConferenceStartAction;
import at.jku.tk.hermes.action.MessageSendToManyAction;
import at.jku.tk.hermes.action.MessageSendToOneAction;

/* Class **********************************************************************/

public final class SkypePstnProtocol extends AbstractProtocol {

	/* Constructors ***********************************************************/

	public SkypePstnProtocol() {
		addSupportedAction(AudioCallStartAction.class);
		addSupportedAction(AudioConferenceStartAction.class);
		addSupportedAction(MessageSendToOneAction.class);
		addSupportedAction(MessageSendToManyAction.class);
	}

}
