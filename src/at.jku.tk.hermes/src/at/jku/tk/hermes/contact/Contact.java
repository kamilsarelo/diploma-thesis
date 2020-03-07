package at.jku.tk.hermes.contact;

/* Imports ********************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.jku.tk.hermes.protocol.Protocol;

/* Class **********************************************************************/

public final class Contact {

	/* Fields *****************************************************************/

	private String name;
	private final Map<Class<? extends Protocol>, String> identities = new HashMap<Class<? extends Protocol>, String>();

	/* Constructors ***********************************************************/

	public Contact(String name) {
		setName(name);
	}

	/* Methods ****************************************************************/

	public String getName() {
		if (name == null) {
			throw new NullPointerException();
		}
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
	}

	public String getIdentity(Class<? extends Protocol> clazz) throws ProtocolNotSupportedByContactException {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (identities) {
			if (! identities.containsKey(clazz)) {
				throw new ProtocolNotSupportedByContactException();
			}
			return identities.get(clazz);
		}
	}

	public boolean putIdentity(Class<? extends Protocol> clazz, String identity) {
		if (clazz == null || identity == null) {
			throw new NullPointerException();
		}
		synchronized (identities) {
			try {
				identities.put(clazz, identity);
				return true;
			} catch (Exception e) {
			}
			return false;
		}
	}

	/**
	 * Remove an identity.
	 *
	 * @param clazz the corresponding Protocol class.
	 * @return <tt>true</tt> if the identity was removed.
	 * @throws NullPointerException if clazz is <tt>null</tt>.
	 * @throws ProtocolNotSupportedException if clazz is not supported.
	 */
	public boolean removeIdentity(Class<? extends Protocol> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (identities) {
			try {
				if (identities.remove(clazz) != null) {
					return true;
				}
			} catch (Exception e) {
			}
			return false;
		}
	}

	public Map<Class<? extends Protocol>, String> getIdentities() {
		synchronized (identities) {
			Map<Class<? extends Protocol>, String> clone = new HashMap<Class<? extends Protocol>, String>();
			for (Class<? extends Protocol> clazz : identities.keySet()) {
				clone.put(clazz, identities.get(clazz));
			}
			return clone;
		}
	}

	public boolean supportsProtocol(Class<? extends Protocol> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (identities) {
			return identities.containsKey(clazz);
		}
	}

	public List<Class<? extends Protocol>> getSupportedProtocols() {
		synchronized (identities) {
			List<Class<? extends Protocol>> clone = new ArrayList<Class<? extends Protocol>>();
			for (Class<? extends Protocol> clazz : identities.keySet()) {
				clone.add(clazz);
			}
			return clone;
		}
	}

}
