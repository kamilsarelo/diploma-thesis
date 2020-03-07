package at.jku.tk.hermes.tool.email.smtp.internal;

/* Imports ********************************************************************/

import java.util.Date;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

import at.jku.tk.hermes.action.Action;
import at.jku.tk.hermes.action.EmailSendAction;
import at.jku.tk.hermes.contact.Contact;
import at.jku.tk.hermes.contact.ProtocolNotSupportedByContactException;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.protocol.EmailClientProtocol;
import at.jku.tk.hermes.protocol.EmailSmtpProtocol;
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
		data.setDescription(
				"Sends an e-mail via a SMTP server. Expects certain values to " +
				"be available as system properties: " +
				"at.jku.tk.hermes.tool.email.smtp.host - address of the SMTP " +
				"server; optionally: " +
				"at.jku.tk.hermes.tool.email.smtp.port - port number for the " +
				"SMTP server, " +
				"at.jku.tk.hermes.tool.email.smtp.username - username for the " +
				"SMTP server, " +
				"at.jku.tk.hermes.tool.email.smtp.password - username for the " +
				"SMTP server, " +
				"at.jku.tk.hermes.tool.email.smtp.starttls.enable - if starttls " +
				"should be enabled or not (for example GMail requires starttls " +
				"to be enabled)."
		);
		return data;
	}

	public void executeAction(Class<? extends Protocol> clazz, Action action) throws ProtocolNotSupportedByToolException, ActionNotSupportedException, ActionExecutionFailedException {
		if (! supportsProtocol(clazz)) {
			throw new ProtocolNotSupportedByToolException();
		}
		if (action instanceof EmailSendAction) {
			if (System.getProperties().containsKey("at.jku.tk.hermes.tool.email.smtp.host")) {
				if (
						System.getProperties().containsKey("at.jku.tk.hermes.tool.email.smtp.username") &&
						System.getProperties().containsKey("at.jku.tk.hermes.tool.email.smtp.password")
				) {
					sendWithAuthentication((EmailSendAction) action);
				} else {
					send((EmailSendAction) action);
				}
			} else {
				throw new ActionExecutionFailedException();
			}
		} else {
			throw new ActionNotSupportedException();
		}
	}

	/* Private */

	// see: http://www.javacommerce.com/displaypage.jsp?name=javamail.sql&id=18274
	// see: http://commons.apache.org/email/userguide.html
	private void send(EmailSendAction action) throws ActionExecutionFailedException {
		try {
			boolean noContacts = true;
			SimpleEmail email = new SimpleEmail();
			email.setDebug(false);
			email.setCharset("UTF-8");
			email.setSentDate(new Date());
			// host
			email.setHostName(System.getProperty("at.jku.tk.hermes.tool.email.smtp.host"));
			// port
			if (System.getProperties().containsKey("at.jku.tk.hermes.tool.email.smtp.port")) {
				email.setSmtpPort(Integer.parseInt(System.getProperty("at.jku.tk.hermes.tool.email.smtp.port")));
			}
			// to
			for (Contact contact : action.getRecipients()) {
				try {
					email.addTo(contact.getIdentity(EmailSmtpProtocol.class), contact.getName());
					noContacts = false;
				} catch (ProtocolNotSupportedByContactException e) {
				}
			}
			// cc
			for (Contact contact : action.getRecipientsCc()) {
				try {
					email.addCc(contact.getIdentity(EmailSmtpProtocol.class), contact.getName());
					noContacts = false;
				} catch (ProtocolNotSupportedByContactException e) {
				}
			}
			// bcc
			for (Contact contact : action.getRecipientsBcc()) {
				try {
					email.addBcc(contact.getIdentity(EmailSmtpProtocol.class), contact.getName());
					noContacts = false;
				} catch (ProtocolNotSupportedByContactException e) {
				}
			}
			// eventually abort
			if (noContacts) {
				throw new ActionExecutionFailedException();
			}
			// subject
			email.setSubject(action.getSubject());
			// body
			email.setMsg(action.getBody());
			//send
			email.send();
		} catch (Exception e) { 
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	// see: http://www.tutorials.de/forum/java/255387-email-mit-javamail-versenden.html
	// see: http://forum.java.sun.com/thread.jspa?threadID=668779&messageID=4455798
	// see: http://www.jsp-develop.de/forum/view/37165/
	// see: http://java.sun.com/products/javamail/
	// see: http://prasadblog.blogspot.com/2007_03_01_archive.html
	private void sendWithAuthentication(EmailSendAction action) throws ActionExecutionFailedException {
		try {
			boolean noContacts = true;
			SimpleEmail email = new SimpleEmail();
			email.setDebug(false);
			email.setCharset("UTF-8");
			email.setSentDate(new Date());
			// host
			email.setHostName(System.getProperty("at.jku.tk.hermes.tool.email.smtp.host"));
			// authentication
			email.setAuthenticator(
					new DefaultAuthenticator(
							System.getProperty("at.jku.tk.hermes.tool.email.smtp.username"),
							System.getProperty("at.jku.tk.hermes.tool.email.smtp.password")));
			// starttls
			if (System.getProperties().containsKey("at.jku.tk.hermes.tool.email.smtp.starttls.enable")) {
				email.getMailSession().getProperties().put(
						"mail.smtp.starttls.enable",
						System.getProperty("at.jku.tk.hermes.tool.email.smtp.starttls.enable"));
			}
			// port
			if (System.getProperties().containsKey("at.jku.tk.hermes.tool.email.smtp.port")) {
				email.setSmtpPort(Integer.parseInt(System.getProperty("at.jku.tk.hermes.tool.email.smtp.port")));
			}
			// to
			for (Contact contact : action.getRecipients()) {
				try {
					email.addTo(contact.getIdentity(EmailSmtpProtocol.class), contact.getName());
					noContacts = false;
				} catch (ProtocolNotSupportedByContactException e) {
				}
			}
			// cc
			for (Contact contact : action.getRecipientsCc()) {
				try {
					email.addCc(contact.getIdentity(EmailSmtpProtocol.class), contact.getName());
					noContacts = false;
				} catch (ProtocolNotSupportedByContactException e) {
				}
			}
			// bcc
			for (Contact contact : action.getRecipientsBcc()) {
				try {
					email.addBcc(contact.getIdentity(EmailSmtpProtocol.class), contact.getName());
					noContacts = false;
				} catch (ProtocolNotSupportedByContactException e) {
				}
			}
			// eventually abort
			if (noContacts) {
				throw new ActionExecutionFailedException();
			}
			// subject
			email.setSubject(action.getSubject());
			// body
			email.setMsg(action.getBody());
			//send
			email.send();
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

}
