/*
 * (non-Javadoc) Methods of this class are user in the DatabaseServiceImpl class by using
 * reflection. When refactoring this methods, make sure their names are also updated in the
 * DatabaseServiceImpl class.
 */

package at.jku.tk.hermes.core;

/* Interface ***************************************************************** */

public interface DatabaseObserverService {

	/* Methods *************************************************************** */

	public void collectionCreated(String collection);

	public void collectionDropped(String collection);

	public void objectPut(String collection, String key);

	public void objectRemoved(String collection, String key);

	public void collectionCreatedOnReplication(String collection);

	public void collectionDroppedOnReplication(String collection);

	public void objectPutOnReplication(String collection, String key);

	public void objectRemovedOnReplication(String collection, String key);

}
