package at.jku.tk.hermes.core.internal.replication;

/* Imports ********************************************************************/

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import at.jku.tk.hermes.core.internal.DatabaseServiceImpl;
import at.jku.tk.hermes.core.internal.ParameterServiceImpl;
import at.jku.tk.hermes.core.internal.replication.smackx.ReplicationManager;
import at.jku.tk.hermes.core.internal.replication.smackx.ReplicationRequestListener;
import at.jku.tk.hermes.core.internal.replication.smackx.ReplicationResponseListener;
import at.jku.tk.hermes.core.internal.replication.smackx.packet.Replication;
import at.jku.tk.hermes.core.internal.replication.smackx.provider.ReplicationProvider;

/* Class **********************************************************************/

public class ReplicationDaemon {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final ReplicationDaemon INSTANCE = new ReplicationDaemon();
	}

	public static ReplicationDaemon start() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final int SLEEP = 1000;

	/* Fields *****************************************************************/

	private ReplicationManager manager;
	private final LinkedBlockingQueue<Work> workQueue  = new LinkedBlockingQueue<Work>(); // http://www.exampledepot.com/egs/java.lang/FixedWorkQueue.html
	private final Map<String, Long> responses = Collections.synchronizedMap(new HashMap<String, Long>());

	private final ReplicationRequestListener requestListener = new ReplicationRequestListener() {
		public void requested(
				ReplicationManager manager,
				String from,
				long drsId
		) {
			Replication replication = DatabaseServiceImpl.getInstance().getReplicationNext(drsId);
			if (replication != null) {
				manager.sendResponse(
						from,
						replication);
			}
		}
	};
	private final ReplicationResponseListener responseListener = new ReplicationResponseListener() {
		public void responded(
				ReplicationManager manager,
				String from,
				Replication replication
		) {
			String bareAddress = StringUtils.parseBareAddress(from);
			try {
				if (
						responses.get(bareAddress) == null ||
						responses.get(bareAddress) < replication.getId()
				) {
					responses.put(bareAddress, replication.getId());
					workQueue.put(new Work(bareAddress, replication));
				}
			} catch (Exception e) {
			}
		}
	};

	/* Constructors ***********************************************************/

	private ReplicationDaemon() {
		ProviderManager providerManager = ProviderManager.getInstance();
		providerManager.addExtensionProvider(
				Replication.NODENAME,
				Replication.NAMESPACE,
				new ReplicationProvider());

		Thread threadXmpp = new Thread(new RunnableXmpp());
		threadXmpp.setDaemon(true);
		threadXmpp.start();

		Thread threadReplication = new Thread(new RunnableReplication());
		threadReplication.setDaemon(true);
		threadReplication.start();
	}

	/* Inner Classas **********************************************************/

	private class RunnableXmpp implements Runnable {

		/* Methods ************************************************************/

		/* Public */

		public void run() {
			XMPPConnection.addConnectionCreationListener(new ConnectionCreationListener() {
				public void connectionCreated(XMPPConnection connection) {
					System.out.println("at.jku.tk.hermes: XMPP connected");
				}
			});
			Roster.setDefaultSubscriptionMode(SubscriptionMode.reject_all);
			try {
				XMPPConnection connection = new XMPPConnection(ParameterServiceImpl.getInstance().getXmppServer());
				while (true) {
					try {
						if (! connection.isConnected()) {
							// not connected
							connection.connect();
						} else {
							// connected
							if (! connection.isAuthenticated()) {
								// not authenticated
								try {
									// try to login
									connection.login(
											ParameterServiceImpl.getInstance().getXmppUsername(),
											ParameterServiceImpl.getInstance().getXmppPassword()); // ..., "resource-name");
									// ReplicationManager
									if (manager != null) {
										manager.removeReplicationRequestListener(requestListener);
										manager.removeReplicationResponseListener(responseListener);
									}
									manager = new ReplicationManager(connection);
									manager.addReplicationRequestListener(requestListener);
									manager.addReplicationResponseListener(responseListener);
								} catch (Exception e) {
									// try to create new account
									// http://www.igniterealtime.org/community/message/111056
									try {
										connection.disconnect();
										connection.connect();
										connection.getAccountManager().createAccount( // if (connection.getAccountManager().supportsAccountCreation()) {
												ParameterServiceImpl.getInstance().getXmppUsername(),
												ParameterServiceImpl.getInstance().getXmppPassword());
									} catch (Exception ea) {
										ea.printStackTrace();
									}
								}
							} else {
								// authenticated
								forAllTribeServers(connection);
								Thread.sleep(SLEEP);
							}
						}
					} catch (Exception e) { // for everything that may occur within the loop, ensures that the loop keeps going
						e.printStackTrace();
					}
				}
			} catch (Exception e) { // for the initialization
				e.printStackTrace();
			}
		}

		/* Private */

		private void forAllTribeServers(XMPPConnection connection) throws Exception {
			for (String server : ParameterServiceImpl.getInstance().getXmppTribeServers()) {
				forAllTribeUsers(connection, server);
			}
		}

		// TODO um ev zu optimieren:
		// suche nach benutzern nicht jeder mal in der schleife, sondern 1 mal pro minute oder noch seltener
		// besser wäre es das über die presence der user zu machen

		private void forAllTribeUsers(XMPPConnection connection, String server) throws Exception {
			String searchService = ParameterServiceImpl.getInstance().getXmppSearchService();
			UserSearchManager searchManager = new UserSearchManager(connection);
			Form searchForm = searchManager.getSearchForm(searchService);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", ParameterServiceImpl.getInstance().getXmppTribeName());
			ReportedData data = searchManager.getSearchResults(answerForm, searchService);
			for (Iterator<Row> iterator = data.getRows(); iterator.hasNext(); ) {
				String username = (String) iterator.next().getValues("Username").next();
				if (! username.equals(ParameterServiceImpl.getInstance().getXmppUsername())) {
					String bareAddress = username + "@" + server;
					long id;
					if (responses.get(bareAddress) == null) {
						id = DatabaseServiceImpl.getInstance().getReplicationLastId(bareAddress);
					} else {
						id = responses.get(bareAddress);
					}
					manager.sendRequest(
							bareAddress,
							id);
				}
			}
		}

	}

	private class RunnableReplication implements Runnable {

		/* Methods ************************************************************/

		/* Public */

		public void run() {
			while (true) {
				try {
					Work work = workQueue.take();
					if (work == null) {
						break;
					}
					DatabaseServiceImpl.getInstance().putReplication(
							work.getJid(),
							work.getReplication());
				} catch (Exception e) {
				}
			}
		}

	}

	private class Work {

		/* Fields *************************************************************/

		private String jid;
		private Replication replication;

		/* Constructors *******************************************************/

		public Work(String jid, Replication replication) {
			this.jid = jid;
			this.replication = replication;
		}

		/* Methods ************************************************************/

		/* Public */

		public String getJid() {
			return jid;
		}

		public Replication getReplication() {
			return replication;
		}

	}

}
