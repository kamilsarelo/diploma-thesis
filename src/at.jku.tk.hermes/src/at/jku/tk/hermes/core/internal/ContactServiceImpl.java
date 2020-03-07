package at.jku.tk.hermes.core.internal;

/* Imports ********************************************************************/

import java.util.HashMap;
import java.util.Map;

import at.jku.tk.hermes.contact.Contact;
import at.jku.tk.hermes.contact.ContactService;

import com.thoughtworks.xstream.XStream;

/* Class **********************************************************************/

public class ContactServiceImpl implements ContactService {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final ContactServiceImpl INSTANCE = new ContactServiceImpl();
	}

	public static ContactServiceImpl getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	protected static final String COLLECTION = "at_jku_tk_hermes_contact";

	/* Fields *****************************************************************/

	private XStream xstream = new XStream();

	/* Constructors ***********************************************************/

	private ContactServiceImpl() {
		if (! DatabaseServiceImpl.getInstance().containsCollection(COLLECTION)) {
			DatabaseServiceImpl.getInstance().createCollection(COLLECTION);
		}
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public boolean containsContact(String key) {
		return DatabaseServiceImpl.getInstance().containsObject(COLLECTION, key);
	}

	public Contact getContact(String key) {
		try {
			return (Contact) xstream.fromXML(DatabaseServiceImpl.getInstance().getObject(COLLECTION, key));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isContactDeleted(String key) {
		return DatabaseServiceImpl.getInstance().isObjectDeleted(COLLECTION, key);
	}

	public boolean putContact(String key, Contact contact) {
		if (DatabaseServiceImpl.getInstance().putObject(COLLECTION, key, xstream.toXML(contact))) {
			return true;
		}
		return false;
	}

	public boolean removeContact(String key) {
		if (DatabaseServiceImpl.getInstance().removeObject(COLLECTION, key)) {
			return true;
		}
		return false;
	}

	public Map<String, Contact> queryContacts(String xpath) {
		Map<String, Contact> contacts = new HashMap<String, Contact>();
		Map<String, String> map = DatabaseServiceImpl.getInstance().queryCollection(COLLECTION, xpath);
		for (String key : map.keySet()) {
			try {
				contacts.put(key, (Contact) xstream.fromXML(map.get(key)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return contacts;
	}

}
