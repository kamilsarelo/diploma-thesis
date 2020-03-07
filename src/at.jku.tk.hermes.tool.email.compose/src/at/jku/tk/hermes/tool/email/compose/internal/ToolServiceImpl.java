package at.jku.tk.hermes.tool.email.compose.internal;

/* Imports ********************************************************************/

import org.eclipse.swt.program.Program;

import at.jku.tk.hermes.action.Action;
import at.jku.tk.hermes.action.EmailSendAction;
import at.jku.tk.hermes.contact.Contact;
import at.jku.tk.hermes.contact.ProtocolNotSupportedByContactException;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.protocol.EmailClientProtocol;
import at.jku.tk.hermes.protocol.Protocol;
import at.jku.tk.hermes.tool.AbstractToolService;
import at.jku.tk.hermes.tool.ActionExecutionFailedException;
import at.jku.tk.hermes.tool.ActionNotSupportedException;
import at.jku.tk.hermes.tool.ProtocolNotSupportedByToolException;

/* Class **********************************************************************/

public class ToolServiceImpl extends AbstractToolService {

	/* Constructors ***********************************************************/

	public ToolServiceImpl() {
		addSupportedProtocol(EmailClientProtocol.class);
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public MetaData getMetaData() {
		MetaData data = new MetaData();
		data.setName(this.getClass().getSimpleName());
		return data;
	}

	public void executeAction(Class<? extends Protocol> clazz, Action action) throws ProtocolNotSupportedByToolException, ActionNotSupportedException, ActionExecutionFailedException {
		if (! supportsProtocol(clazz)) {
			throw new ProtocolNotSupportedByToolException();
		}
		if (action instanceof EmailSendAction) {
			try {
				Program.launch(getMailto((EmailSendAction) action));
			} catch (Exception e) {
				throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
			}
		} else {
			throw new ActionNotSupportedException();
		}
	}

	/* Private */

	private String getMailto(EmailSendAction action) throws ActionExecutionFailedException {
		boolean noContacts = true;
		StringBuilder mailto = new StringBuilder("mailto:");
		// to
		for (Contact contact : action.getRecipients()) {
			try {
				mailto.append(contact.getName() + " <" + contact.getIdentity(EmailClientProtocol.class) + ">,");
				noContacts = false;
			} catch (ProtocolNotSupportedByContactException e) {
			}
		}
		// cc
		mailto.append("?cc=");
		for (Contact contact : action.getRecipientsCc()) {
			try {
				mailto.append(contact.getName() + " <" + contact.getIdentity(EmailClientProtocol.class) + ">,");
				noContacts = false;
			} catch (ProtocolNotSupportedByContactException e) {
			}
		}
		// bcc
		mailto.append("&bcc=");
		for (Contact contact : action.getRecipientsBcc()) {
			try {
				mailto.append(contact.getName() + " <" + contact.getIdentity(EmailClientProtocol.class) + ">,");
				noContacts = false;
			} catch (ProtocolNotSupportedByContactException e) {
			}
		}
		// eventually abort
		if (noContacts) {
			throw new ActionExecutionFailedException();
		}
		// subject
		mailto.append("&subject=" + action.getSubject());
		// body
		mailto.append("&body=" + action.getBody());
		// return
		return mailto.toString();
	}

}
