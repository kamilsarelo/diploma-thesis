package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import at.jku.tk.hermes.action.AudioCallStartAction;
import at.jku.tk.hermes.action.AudioConferenceStartAction;
import at.jku.tk.hermes.action.AudioVideoCallStartAction;
import at.jku.tk.hermes.action.AudioVideoConferenceStartAction;
import at.jku.tk.hermes.action.FileTransferToManyAction;
import at.jku.tk.hermes.action.FileTransferToOneAction;
import at.jku.tk.hermes.action.MessageSendToManyAction;
import at.jku.tk.hermes.action.MessageSendToOneAction;
import at.jku.tk.hermes.action.PresenceSetAction;

/* Class **********************************************************************/

public final class SkypeProtocol extends AbstractProtocol {

	/* Constructors ***********************************************************/

	public SkypeProtocol() {
		addSupportedAction(AudioCallStartAction.class);
		addSupportedAction(AudioConferenceStartAction.class);
		addSupportedAction(AudioVideoCallStartAction.class);
		addSupportedAction(AudioVideoConferenceStartAction.class);
		addSupportedAction(FileTransferToOneAction.class);
		addSupportedAction(FileTransferToManyAction.class);
		addSupportedAction(MessageSendToOneAction.class);
		addSupportedAction(MessageSendToManyAction.class);
		addSupportedAction(PresenceSetAction.class);
	}

}
