package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import at.jku.tk.hermes.action.MessageSendToManyAction;
import at.jku.tk.hermes.action.MessageSendToOneAction;

/* Class **********************************************************************/

public final class SMSGatewayProtocol extends AbstractProtocol {

	/* Constructors ***********************************************************/

	public SMSGatewayProtocol() {
		addSupportedAction(MessageSendToOneAction.class);
		addSupportedAction(MessageSendToManyAction.class);
	}

}
