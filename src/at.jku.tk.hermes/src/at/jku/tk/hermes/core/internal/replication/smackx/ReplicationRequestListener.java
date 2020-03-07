package at.jku.tk.hermes.core.internal.replication.smackx;

/* Interface ******************************************************************/

public interface ReplicationRequestListener {

	/* Methods ****************************************************************/

	public void requested(
			ReplicationManager manager,
			String from,
			long drsId);

}
