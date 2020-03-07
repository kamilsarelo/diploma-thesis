package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import java.util.ArrayList;
import java.util.List;

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public final class EmailSendAction implements Action {

	/* Fields *****************************************************************/

	protected final List<Contact> recipients = new ArrayList<Contact>();
	protected final List<Contact> recipientsCc = new ArrayList<Contact>();
	protected final List<Contact> recipientsBcc = new ArrayList<Contact>();
	protected String subject;
	protected String body;

	/* Constructors ***********************************************************/

	public EmailSendAction(Contact to, String subject, String body) {
		addRecipent(to);
		setSubject(subject);
		setBody(body);
	}

	/* Methods ****************************************************************/

	/* Public */

	public List<Contact> getRecipients() {
		return getContacts(recipients);
	}

	public boolean addRecipent(Contact contact) {
		return addContact(recipients, contact);
	}

	public boolean removeRecipent(Contact contact) {
		return removeContact(recipients, contact);
	}

	public List<Contact> getRecipientsCc() {
		return getContacts(recipientsCc);
	}

	public boolean addRecipentCc(Contact contact) {
		return addContact(recipientsCc, contact);
	}

	public boolean removeRecipentCc(Contact contact) {
		return removeContact(recipientsCc, contact);
	}

	public List<Contact> getRecipientsBcc() {
		return getContacts(recipientsBcc);
	}

	public boolean addRecipentBcc(Contact contact) {
		return addContact(recipientsBcc, contact);
	}

	public boolean removeRecipentBcc(Contact contact) {
		return removeContact(recipientsBcc, contact);
	}

	public String getSubject() {
		if (subject == null) {
			throw new NullPointerException();
		}
		return subject;
	}

	public void setSubject(String subject) {
		if (subject == null) {
			throw new NullPointerException();
		}
		this.subject = subject;
	}

	public String getBody() {
		if (body == null) {
			throw new NullPointerException();
		}
		return body;
	}

	public void setBody(String body) {
		if (body == null) {
			throw new NullPointerException();
		}
		this.body = body;
	}

	/* Protected */

	protected List<Contact> getContacts(List<Contact> list) {
		synchronized (list) {
			List<Contact> clone = new ArrayList<Contact>();
			for (Contact contact : list) {
				clone.add(contact);
			}
			return clone;
		}
	}

	protected boolean addContact(List<Contact> list, Contact contact) {
		if (contact == null) {
			throw new NullPointerException();
		}
		synchronized (list) {
			if (! list.contains(contact)) {
				return list.add(contact);
			}
		}
		return false;
	}

	protected boolean removeContact(List<Contact> list, Contact contact) {
		if (contact == null) {
			throw new NullPointerException();
		}
		synchronized (list) {
			return list.remove(contact);
		}
	}

}
