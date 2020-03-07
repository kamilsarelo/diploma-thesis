package at.jku.tk.hermes.core;

/* Imports ********************************************************************/

import java.util.Map;

/* Interface ******************************************************************/

public interface DatabaseService {

	/* Methods ****************************************************************/

	public boolean isCollectionDeleted(String collection);

	public boolean isObjectDeleted(String collection, String key);

	public boolean containsCollection(String collection);

	public boolean containsObject(String collection, String key);

	public boolean createCollection(String collection);

	public boolean dropCollection(String collection);

	public boolean putObject(String collection, String key, String xml);

	public String getObject(String collection, String key);

	public boolean removeObject(String collection, String key);

	public Map<String, String> queryCollection(String collection, String xpath);

}
