/*
 * (non-Javadoc)
 * Creating a user in Openfire limits the length of the user name (node part
 * of an JID, see @see org.xmpp.packet.JID) to 75 characters. If we assume
 * we use 128-bit UUID as group prefix (36 characters long) and _ as
 * separator between the UUID and the actual user name, we have to limit the
 * user name to 38 characters, e.g.:
 * 
 *   550e8400-e29b-41d4-a716-446655440000_abcdefghijklmnopqrstuvwxyz1234567890ab
 */

package at.jku.tk.hermes.core.internal;

/* Imports ********************************************************************/

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import at.jku.tk.hermes.core.ParameterException;
import at.jku.tk.hermes.core.ParameterService;

/* Class **********************************************************************/

public class ParameterServiceImpl implements ParameterService {


	/* Singleton **************************************************************/

	private static class Holder {
		private static final ParameterServiceImpl INSTANCE = new ParameterServiceImpl();
	}

	public static ParameterServiceImpl getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final int maxLength = 36; // max length for xmpp tribe name and xmpp user name

	/* Fields *****************************************************************/

	private static File storage = null;
	private static String xmppServer = null;
	private static String xmppSearchService = null;
	private static String[] xmppServerTribe = null;
	private static String xmppUsernameTribe = null;
	private static String xmppUsernameBare = null;
	private static String xmppUsername = null;
	private static String xmppPassword = null;
	private static String ntpTimeServer = null;

	/* Methods ****************************************************************/

	/* Implementation */

	public File getStorage() throws ParameterException {
		if (storage == null) {
			try {
				// fix path
				storage = new File(System.getProperty("at.jku.tk.hermes.storage")); // property can cause exception
				if (! storage.isAbsolute()) {
					storage = new File(
							System.getProperty("user.home"),
							System.getProperty("at.jku.tk.hermes.storage"));
				}
				// check if root directory may be written, e.g. optical disk drive is not
				File parent = storage;
				while (parent.getParentFile() != null) {
					parent = parent.getParentFile();
				}
				if (! parent.canWrite()) {
					throw new Exception();
				}
			} catch (Exception e) {
				storage = new File(System.getProperty("user.home"), "at.jku.tk.hermes"); // property always available
			}
			storage = new File(
					storage,
					getXmppTribeName() + File.separator + getXmppBareUsername() + "@" + getXmppServer()
			).getAbsoluteFile();
			storage.mkdirs();
		}
		return storage;
	}

	public String getNtpTimeServer() throws ParameterException {
		if (ntpTimeServer == null) {
			ntpTimeServer = System.getProperty("at.jku.tk.hermes.time");
			if (ntpTimeServer == null) {
				throw new ParameterException();
			}
		}
		return ntpTimeServer;
	}

	public String getXmppServer() throws ParameterException {
		if (xmppServer == null) {
			xmppServer = System.getProperty("at.jku.tk.hermes.xmpp.server");
			if (xmppServer == null) {
				throw new ParameterException();
			}
		}
		return xmppServer;
	}

	public String getXmppSearchService() throws ParameterException {
		if (xmppSearchService == null) {
			xmppSearchService = System.getProperty("at.jku.tk.hermes.xmpp.search");
			if (xmppSearchService == null) {
				throw new ParameterException();
			}
		}
		return xmppSearchService;
	}

	public String getXmppUsername() throws ParameterException {
		if (xmppUsername == null) {
			xmppUsername = getXmppTribeName() + "_" + getXmppBareUsername();
			if (xmppUsername == null) {
				throw new ParameterException();
			}
		}
		return xmppUsername;
	}

	public String getXmppBareUsername() throws ParameterException {
		if (xmppUsernameBare == null) {
			try {
				xmppUsernameBare = System.getProperty("at.jku.tk.hermes.xmpp.username");
				xmppUsernameBare = xmppUsernameBare.replace(" ", "");
				xmppUsernameBare = xmppUsernameBare.substring(0, Math.min(xmppUsernameBare.length(), maxLength));
			} catch (Exception e) {
				xmppUsernameBare = null;
			}
			if (xmppUsernameBare == null) {
				throw new ParameterException();
			}
		}
		return xmppUsernameBare;
	}

	public String getXmppPassword() throws ParameterException {
		if (xmppPassword == null) {
			xmppPassword = System.getProperty("at.jku.tk.hermes.xmpp.password");
			if (xmppPassword == null) {
				throw new ParameterException();
			}
		}
		return xmppPassword;
	}

	public String[] getXmppTribeServers() throws ParameterException {
		if (xmppServerTribe == null) {
			try {
				Set<String> set = new TreeSet<String>(); // TreeSet makes sure there are no null elements and no duplicates
				set.add(getXmppServer()); // including the current server
				for (String server : System.getProperty("at.jku.tk.hermes.xmpp.tribe.servers").split(",")) {
					if (server != null && ! server.equals("")) {
						set.add(server);
					}
				}
				xmppServerTribe = (String[]) set.toArray(new String[set.size()]);
			} catch (Exception e) {
				xmppServerTribe = null;
			}
			if (xmppServerTribe == null) {
				throw new ParameterException();
			}
		}
		return xmppServerTribe;
	}

	public String getXmppTribeName() throws ParameterException {
		if (xmppUsernameTribe == null) {
			try {
				xmppUsernameTribe = System.getProperty("at.jku.tk.hermes.xmpp.tribe.name");
				xmppUsernameTribe = xmppUsernameTribe.replace(" ", "").replaceAll("_", "");
				xmppUsernameTribe = xmppUsernameTribe.substring(0, Math.min(xmppUsernameTribe.length(), maxLength));
			} catch (Exception e) {
				xmppUsernameTribe = null;
			}
			if (xmppUsernameTribe == null) {
				throw new ParameterException();
			}
		}
		return xmppUsernameTribe;
	}

}
