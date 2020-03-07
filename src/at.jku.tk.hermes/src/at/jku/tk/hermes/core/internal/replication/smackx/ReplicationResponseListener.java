package at.jku.tk.hermes.core.internal.replication.smackx;

/* Imports ********************************************************************/

import at.jku.tk.hermes.core.internal.replication.smackx.packet.Replication;

/* Interface ******************************************************************/

public interface ReplicationResponseListener {

	/* Methods ****************************************************************/

	public void responded(
			ReplicationManager manager,
			String from,
			Replication replication);

}
