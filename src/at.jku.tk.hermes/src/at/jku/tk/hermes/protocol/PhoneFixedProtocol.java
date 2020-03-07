package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import at.jku.tk.hermes.action.AudioCallStartAction;

/* Class **********************************************************************/

public final class PhoneFixedProtocol extends AbstractProtocol {

	/* Constructors ***********************************************************/

	public PhoneFixedProtocol() {
		addSupportedAction(AudioCallStartAction.class);
	}

}
