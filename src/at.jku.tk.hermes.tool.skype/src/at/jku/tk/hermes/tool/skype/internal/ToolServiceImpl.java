package at.jku.tk.hermes.tool.skype.internal;

/* Imports ********************************************************************/

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.jku.tk.hermes.action.Action;
import at.jku.tk.hermes.action.AudioCallStartAction;
import at.jku.tk.hermes.action.AudioConferenceStartAction;
import at.jku.tk.hermes.action.AudioVideoCallStartAction;
import at.jku.tk.hermes.action.AudioVideoConferenceStartAction;
import at.jku.tk.hermes.action.FileTransferToManyAction;
import at.jku.tk.hermes.action.FileTransferToOneAction;
import at.jku.tk.hermes.action.MessageSendToManyAction;
import at.jku.tk.hermes.action.MessageSendToOneAction;
import at.jku.tk.hermes.action.PresenceSetAction;
import at.jku.tk.hermes.contact.Contact;
import at.jku.tk.hermes.contact.ProtocolNotSupportedByContactException;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.protocol.Protocol;
import at.jku.tk.hermes.protocol.SkypeProtocol;
import at.jku.tk.hermes.protocol.SkypePstnProtocol;
import at.jku.tk.hermes.tool.AbstractToolService;
import at.jku.tk.hermes.tool.ActionExecutionFailedException;
import at.jku.tk.hermes.tool.ActionNotSupportedException;
import at.jku.tk.hermes.tool.ProtocolNotSupportedByToolException;

import com.skype.Call;
import com.skype.Skype;
import com.skype.SkypeClient;

/* Class **********************************************************************/

public class ToolServiceImpl extends AbstractToolService {

	/* Constructors ***********************************************************/

	public ToolServiceImpl() {
		addSupportedProtocol(SkypeProtocol.class);
		addSupportedProtocol(SkypePstnProtocol.class);
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
		if (action instanceof AudioCallStartAction) {
			audioCallStart(clazz, (AudioCallStartAction) action);
		} else if (action instanceof AudioConferenceStartAction) {
			audioConferenceStart(clazz, (AudioConferenceStartAction) action);
		} else if (action instanceof AudioVideoCallStartAction) {
			audioVideoCallStart(clazz, (AudioVideoCallStartAction) action);
		} else if (action instanceof AudioVideoConferenceStartAction) {
			audioVideoConferenceStart(clazz, (AudioVideoConferenceStartAction) action);
		} else if (action instanceof FileTransferToOneAction) {
			fileTransferToOne(clazz, (FileTransferToOneAction) action);
		} else if (action instanceof FileTransferToManyAction) {
			fileTransferToMany(clazz, (FileTransferToManyAction) action);
		} else if (action instanceof MessageSendToOneAction) {
			messageSendToOne(clazz, (MessageSendToOneAction) action);
		} else if (action instanceof MessageSendToManyAction) {
			messageSendToMany(clazz, (MessageSendToManyAction) action);
		} else if (action instanceof PresenceSetAction) {
			presenceSet((PresenceSetAction) action);
		} else {
			throw new ActionNotSupportedException();
		}
	}

	/* Private */

	private void audioCallStart(Class<? extends Protocol> clazz, AudioCallStartAction action) throws ActionExecutionFailedException {
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(action.getContact());
		callOrConferenceStart(clazz, contacts, false);
	}

	private void audioVideoCallStart(Class<? extends Protocol> clazz, AudioVideoCallStartAction action) throws ActionExecutionFailedException {
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(action.getContact());
		callOrConferenceStart(clazz, contacts, true);
	}

	private void audioConferenceStart(Class<? extends Protocol> clazz, AudioConferenceStartAction action) throws ActionExecutionFailedException {
		callOrConferenceStart(clazz, action.getContacts(), false);
	}

	private void audioVideoConferenceStart(Class<? extends Protocol> clazz, AudioVideoConferenceStartAction action) throws ActionExecutionFailedException {
		callOrConferenceStart(clazz, action.getContacts(), true);
	}

	private void callOrConferenceStart(Class<? extends Protocol> clazz, List<Contact> contacts, boolean isVideoEnabled) throws ActionExecutionFailedException {
		List<String> usernames = new ArrayList<String>();
		for (Contact contact : contacts) {
			try {
				usernames.add(contact.getIdentity(clazz));
			} catch (ProtocolNotSupportedByContactException e) {
				e.printStackTrace();
			}
		}
		if (usernames.isEmpty()) {
			throw new ActionExecutionFailedException();
		}
		try {
			Call call = Skype.call(stringListToArray(usernames));
			try {
				call.setReceiveVideoEnabled(isVideoEnabled);
				call.setSendVideoEnabled(isVideoEnabled);
			} catch (Exception e) {
			}
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void fileTransferToOne(Class<? extends Protocol> clazz, FileTransferToOneAction action) throws ActionExecutionFailedException {
		try {
			List<Contact> contacts = new ArrayList<Contact>();
			contacts.add(action.getContact());
			fileTransfer(clazz, contacts, action.getFile());
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void fileTransferToMany(Class<? extends Protocol> clazz, FileTransferToManyAction action) throws ActionExecutionFailedException {
		try {
			fileTransfer(clazz, action.getContacts(), action.getFile());
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void fileTransfer(Class<? extends Protocol> clazz, List<Contact> contacts, File file) throws ActionExecutionFailedException {
		List<String> usernames = new ArrayList<String>();
		for (Contact contact : contacts) {
			try {
				usernames.add(contact.getIdentity(clazz));
			} catch (ProtocolNotSupportedByContactException e) {
				e.printStackTrace();
			}
		}
		if (usernames.isEmpty()) {
			throw new ActionExecutionFailedException();
		}
		try {
			SkypeClient.showFileTransferWindow(stringListToArray(usernames), file);
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void messageSendToOne(Class<? extends Protocol> clazz, MessageSendToOneAction action) throws ActionExecutionFailedException {
		try {
			List<Contact> contacts = new ArrayList<Contact>();
			contacts.add(action.getContact());
			messageSend(clazz, contacts, action.getMessage());
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void messageSendToMany(Class<? extends Protocol> clazz, MessageSendToManyAction action) throws ActionExecutionFailedException {
		try {
			messageSend(clazz, action.getContacts(), action.getMessage());
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void messageSend(Class<? extends Protocol> clazz, List<Contact> contacts, String message) throws ActionExecutionFailedException {
		List<String> usernames = new ArrayList<String>();
		for (Contact contact : contacts) {
			try {
				usernames.add(contact.getIdentity(clazz));
			} catch (ProtocolNotSupportedByContactException e) {
				e.printStackTrace();
			}
		}
		if (usernames.isEmpty()) {
			throw new ActionExecutionFailedException();
		}
		try {
			Skype.chat(stringListToArray(usernames)).send(message);
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void presenceSet(PresenceSetAction action) throws ActionExecutionFailedException {
		try {
			switch (action.getStatus()) {
			case ONLINE:
				Skype.getProfile().setStatus(com.skype.Profile.Status.ONLINE);
				break;
			case AWAY:
				Skype.getProfile().setStatus(com.skype.Profile.Status.AWAY);
				break;
			case NOT_AVAILABLE:
			case OUT_TO_LUNCH:
				Skype.getProfile().setStatus(com.skype.Profile.Status.NA);
				break;
			case OCCUPIED:
			case DO_NOT_DISTURB:
			case ON_THE_PHONE:
				Skype.getProfile().setStatus(com.skype.Profile.Status.DND);
				break;
			case FREE_FOR_CHAT:
				Skype.getProfile().setStatus(com.skype.Profile.Status.SKYPEME);
				break;
			case INVISIBLE:
				Skype.getProfile().setStatus(com.skype.Profile.Status.INVISIBLE);
				break;
			case OFFLINE:
			default:
				Skype.getProfile().setStatus(com.skype.Profile.Status.OFFLINE);
			}
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private static String[] stringListToArray(List<String> list) {
		if (list != null) {
			return list.toArray(new String[list.size()]); // ALTERNATIVELY: return list.toArray(new String[]{});
		}
		return new String[]{};
	}

}
