/*
 * (non-Javadoc)
 * Methods of this class are user in the DatabaseServiceImpl class by using
 * reflection. When refactoring this methods, make sure their names are also
 * updated in the DatabaseServiceImpl class.
 */

package at.jku.tk.hermes.contact;

/* Interface ******************************************************************/

public interface ContactObserverService {

	/* Methods ****************************************************************/

	public void contactPut(String key);

	public void contactRemoved(String key);

	public void contactPutOnReplication(String key);
	
	public void contactRemovedOnReplication(String key);
	
}
