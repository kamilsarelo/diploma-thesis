package at.jku.tk.hermes.contact;

/* Imports ********************************************************************/

import java.util.Map;

/* Interface ******************************************************************/

public interface ContactService {

	/* Methods ****************************************************************/

	public boolean isContactDeleted(String key);

	public boolean containsContact(String key);

	public boolean putContact(String key, Contact contact);

	public Contact getContact(String key);

	public boolean removeContact(String key);

	public Map<String, Contact> queryContacts(String xpath);

}
