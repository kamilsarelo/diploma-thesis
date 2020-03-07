package at.jku.tk.hermes.core.internal.replication.smackx;

/* Imports ********************************************************************/

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import at.jku.tk.hermes.core.internal.replication.smackx.packet.Replication;

/* Class **********************************************************************/

public class ReplicationManager {

	/* Fields *****************************************************************/

	private XMPPConnection connection;
	private List<ReplicationRequestListener> replicReqListeners = new ArrayList<ReplicationRequestListener>();
	private List<ReplicationResponseListener> replicResListeners = new ArrayList<ReplicationResponseListener>();
	private PacketFilter packetFilter = new PacketExtensionFilter(
			Replication.NODENAME,
			Replication.NAMESPACE);
	private PacketListener packetListener;

	/* Constructors ***********************************************************/

	public ReplicationManager(XMPPConnection con) {
		this.connection = con;
		init();
	}

	/* Methods ****************************************************************/

	/* Public */

	private void init() {
		packetListener = new PacketListener() {
			public void processPacket(Packet packet) {
				Message message = (Message) packet;
				Replication replication = (Replication) message.getExtension(
						Replication.NODENAME,
						Replication.NAMESPACE);
				if (replication.isModeRequest()) {
					fireReplicationRequestListeners(message.getFrom(), replication);
				}
				if (replication.isModeResponse()) {
					fireReplicationResponseListeners(message.getFrom(), replication);
				}
			};

		};
		connection.addPacketListener(packetListener, packetFilter);
	}

	@Override
	public void finalize() {
		destroy();
	}

	public void destroy() {
		if (connection != null) {
			connection.removePacketListener(packetListener);
		}
	}

	public void addReplicationRequestListener(ReplicationRequestListener listener) {
		if (listener != null) {
			synchronized (replicReqListeners) {
				replicReqListeners.add(listener);
			}
		}
	}

	public void removeReplicationRequestListener(ReplicationRequestListener listener) {
		if (listener != null) {
			synchronized (replicReqListeners) {
				replicReqListeners.remove(listener);
			}
		}
	}

	public void addReplicationResponseListener(ReplicationResponseListener listener) {
		if (listener != null) {
			synchronized (replicResListeners) {
				replicResListeners.add(listener);
			}
		}
	}

	public void removeReplicationResponseListener(ReplicationResponseListener listener) {
		if (listener != null) {
			synchronized (replicResListeners) {
				replicResListeners.remove(listener);
			}
		}
	}

	public void sendRequest(
			String to,
			long drsId
	) {
		Message message = new Message(to);
		Replication replication = new Replication();
		replication.setModeRequest();
		replication.setId(drsId);
		message.addExtension(replication);
		connection.sendPacket(message);
	}

	public void sendResponse(
			String to,
			Replication replication
	) {
		Message message = new Message(to);
		replication.setModeResponse();
		message.addExtension(replication);
		connection.sendPacket(message);
	}

	/* Private */

	private void fireReplicationRequestListeners(
			String from,
			Replication replication
	) {
		ReplicationRequestListener[] listeners = null;
		synchronized (replicReqListeners) {
			listeners = new ReplicationRequestListener[replicReqListeners.size()];
			replicReqListeners.toArray(listeners);
		}
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].requested(
					this,
					from,
					replication.getId());
		}
	}

	private void fireReplicationResponseListeners(
			String from,
			Replication replication
	) {
		ReplicationResponseListener[] listeners = null;
		synchronized (replicResListeners) {
			listeners = new ReplicationResponseListener[replicResListeners.size()];
			replicResListeners.toArray(listeners);
		}
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].responded(
					this,
					from,
					replication);
		}
	}

}
