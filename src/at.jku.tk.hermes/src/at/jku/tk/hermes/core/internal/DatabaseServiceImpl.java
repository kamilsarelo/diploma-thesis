package at.jku.tk.hermes.core.internal;

/* Imports ********************************************************************/

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import at.jku.tk.hermes.core.DatabaseService;
import at.jku.tk.hermes.core.internal.replication.smackx.packet.Replication;
import at.jku.tk.hermes.core.internal.replication.smackx.packet.Replication.Operation;

/* Class **********************************************************************/

public class DatabaseServiceImpl implements DatabaseService {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final DatabaseServiceImpl INSTANCE = new DatabaseServiceImpl();
	}

	public static DatabaseServiceImpl getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final String dbDirName = "at.jku.tk.hermes.db";
	private static final String dbName = "derby";
	private static final String derbyDriver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String derbyUrl = "jdbc:derby:";

	/* JDBC Constants & Fields ************************************************/

	private static final String strIdsCreate =
		"CREATE TABLE at_jku_tk_hermes_drs_ids (" +
		"	peer_jid VARCHAR(3072) NOT NULL PRIMARY KEY," + // @see org.xmpp.packet.JID
		"	peer_log_id BIGINT NOT NULL DEFAULT 0" +
		")";
	private PreparedStatement stmtIdsCount;
	private static final String strIdsCount =
		"SELECT COUNT(*) FROM at_jku_tk_hermes_drs_ids " +
		"WHERE peer_jid = ?";
	private PreparedStatement stmtIdsSelect;
	private static final String strIdsSelect =
		"SELECT peer_log_id FROM at_jku_tk_hermes_drs_ids " +
		"WHERE peer_jid = ?";
	private PreparedStatement stmtIdsInsert;
	private static final String strIdsInsert =
		"INSERT INTO at_jku_tk_hermes_drs_ids (" +
		"  peer_jid, peer_log_id" +
		") VALUES (" +
		"  ?, ?" +
		")";
	private PreparedStatement stmtIdsUpdate;
	private static final String strIdsUpdate =
		"UPDATE at_jku_tk_hermes_drs_ids " +
		"SET peer_log_id = ?" +
		"WHERE peer_jid = ?";
	private static final String strLogCreate =
		"CREATE TABLE at_jku_tk_hermes_drs_log (" +
		"	id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
		"	obj_timestamp BIGINT NOT NULL," +
		"	obj_operation CHAR(1) NOT NULL, " + // @see: at.jku.tk.hermes.smackx.packet.Replication.Operation
		"	obj_collection VARCHAR(1024) NOT NULL," +
		"	obj_key VARCHAR(1024)" + // may be NULL, 'cuz we log operations on collections too (and there is no key)
		")";
	private PreparedStatement stmtLogCountFreshObj;
	private static final String strLogCountFreshObj =
		"SELECT COUNT(*) FROM at_jku_tk_hermes_drs_log " +
		"WHERE " +
		"  obj_timestamp >= ? AND " +
		"  obj_collection = ? AND " +
		"  obj_key = ?";
	private PreparedStatement stmtLogContainsCol;
	private static final String strLogContainsCol =
		"SELECT COUNT(*) FROM at_jku_tk_hermes_drs_log " +
		"WHERE " +
		"  obj_collection = ? AND " +
		"  obj_key IS NULL";
	private PreparedStatement stmtLogContainsObj;
	private static final String strLogContainsObj =
		"SELECT COUNT(*) FROM at_jku_tk_hermes_drs_log " +
		"WHERE " +
		"  obj_collection = ? AND " +
		"  obj_key = ?";
	private PreparedStatement stmtLogIsDeletedCol;
	private static final String strLogIsDeletedCol =
		"SELECT COUNT(*) FROM at_jku_tk_hermes_drs_log " +
		"WHERE " +
		"  obj_collection = ? AND " +
		"  obj_key IS NULL AND " +
		"  obj_operation = '" + Operation.DELETED + "'";
	private PreparedStatement stmtLogIsDeletedObj;
	private static final String strLogIsDeletedObj =
		"SELECT COUNT(*) FROM at_jku_tk_hermes_drs_log " +
		"WHERE " +
		"  (" +
		"    obj_collection = ? AND " +
		"    obj_key IS NULL AND " +
		"    obj_operation = '" + Operation.DELETED + "'" +
		"  ) OR (" +
		"    obj_collection = ? AND " +
		"    obj_key = ? AND " +
		"    obj_operation = '" + Operation.DELETED + "'" +
		"  )";
	private PreparedStatement stmtLogSelect;
	private static final String strLogSelect =
		"SELECT * FROM at_jku_tk_hermes_drs_log " +
		"WHERE id > ? " +
		"ORDER BY id ASC"; // no LIMIT 1 possible
	private PreparedStatement stmtLogInsertCol;
	private static final String strLogInsertCol =
		"INSERT INTO at_jku_tk_hermes_drs_log (" +
		"	obj_timestamp, obj_operation, obj_collection" +
		") VALUES (" +
		"	?, ?, ?" +
		")";
	private PreparedStatement stmtLogInsertObj;
	private static final String strLogInsertObj =
		"INSERT INTO at_jku_tk_hermes_drs_log (" +
		"	obj_timestamp, obj_operation, obj_collection, obj_key" +
		") VALUES (" +
		"	?, ?, ?, ?" +
		")";
	private PreparedStatement stmtLogDeleteCol;
	private static final String strLogDeleteCol =
		"DELETE FROM at_jku_tk_hermes_drs_log " +
		"WHERE " +
		"  obj_collection = ? AND " +
		"  obj_key IS NULL";
	private PreparedStatement stmtLogDeleteObj;
	private static final String strLogDeleteObj =
		"DELETE FROM at_jku_tk_hermes_drs_log " +
		"WHERE " +
		"  obj_collection = ? AND " +
		"  obj_key = ?";
	private PreparedStatement stmtLogDeleteColObj;
	private static final String strLogDeleteColObj =
		"DELETE FROM at_jku_tk_hermes_drs_log " +
		"WHERE obj_collection = ?";

	/* Fields *****************************************************************/

	private Properties dbProperties = new Properties();
	private Connection dbConnection;
	private boolean isConnected;

	/* Constructors ***********************************************************/

	private DatabaseServiceImpl() {
		try {
			setDbSystemDirectory();
			setDbProperties();
			loadDbDriver();
			if (! dbExists()) {
				createDb();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public boolean isCollectionDeleted(String collection) {
		try {
			stmtLogIsDeletedCol.clearParameters();
			stmtLogIsDeletedCol.setString(1, collection);
			ResultSet result = stmtLogIsDeletedCol.executeQuery();
			return result.next() ? result.getInt(1) > 0 : false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isObjectDeleted(String collection, String key) {
		try {
			stmtLogIsDeletedObj.clearParameters();
			stmtLogIsDeletedObj.setString(1, collection);
			stmtLogIsDeletedObj.setString(2, collection);
			stmtLogIsDeletedObj.setString(3, key);
			ResultSet result = stmtLogIsDeletedObj.executeQuery();
			return result.next() ? result.getInt(1) > 0 : false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean containsCollection(String collection) {
		try {
			stmtLogContainsCol.clearParameters();
			stmtLogContainsCol.setString(1, collection);
			ResultSet result = stmtLogContainsCol.executeQuery();
			return result.next() ? result.getInt(1) > 0 : false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean containsObject(String collection, String key) {
		try {
			stmtLogContainsObj.clearParameters();
			stmtLogContainsObj.setString(1, collection);
			stmtLogContainsObj.setString(2, key);
			ResultSet result = stmtLogContainsObj.executeQuery();
			return result.next() ? result.getInt(1) > 0 : false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createCollection(String collection) {
		if (createCollection(TimestampServiceImpl.getInstance().getAccurateTimestamp(), collection)) {
			notifyObserversCollectionCreated(collection);
			return true;
		} else {
			return false;
		}
	}

	public boolean dropCollection(String collection) {
		if (dropCollection(TimestampServiceImpl.getInstance().getAccurateTimestamp(), collection)) {
			notifyObserversCollectionDropped(collection);
			return true;
		} else {
			return false;
		}
	}

	public String getObject(String collection, String key) {
		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			ResultSet result = stmt.executeQuery(
					"SELECT XMLSERIALIZE(obj_xmls11n AS CLOB) FROM " + collection + " " +
					"WHERE obj_key = '" + key + "'");
			return result.next() ? result.getString(1) : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean putObject(String collection, String key, String xml) {
		if (putObject(TimestampServiceImpl.getInstance().getAccurateTimestamp(), collection, key, xml)) {
			notifyObserversObjectPut(collection, key);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeObject(String collection, String key) {
		if (removeObject(TimestampServiceImpl.getInstance().getAccurateTimestamp(), collection, key)) {
			notifyObserversObjectRemoved(collection, key);
			return true;
		} else {
			return false;
		}
	}

	public Map<String, String> queryCollection(String collection, String xpath) {
		Map<String, String> map = new HashMap<String, String>();
		Statement stmt = null;
		try {
			stmt = dbConnection.createStatement();
			ResultSet result = stmt.executeQuery(
					"SELECT obj_key, XMLSERIALIZE(obj_xmls11n AS CLOB) FROM " + collection + " " +
					"WHERE XMLEXISTS('" + xpath + "' PASSING BY REF obj_xmls11n)");
			while (result.next()) {
				map.put(result.getString(1), result.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/* Public */

	public boolean connect() {
		try {
			dbConnection = DriverManager.getConnection(getDbUrl(), dbProperties);
			dbConnection.setAutoCommit(false);

			stmtLogSelect = dbConnection.prepareStatement(strLogSelect);
			stmtLogCountFreshObj = dbConnection.prepareStatement(strLogCountFreshObj);
			stmtLogContainsCol = dbConnection.prepareStatement(strLogContainsCol);
			stmtLogContainsObj = dbConnection.prepareStatement(strLogContainsObj);
			stmtLogIsDeletedCol = dbConnection.prepareStatement(strLogIsDeletedCol);
			stmtLogIsDeletedObj = dbConnection.prepareStatement(strLogIsDeletedObj);
			stmtLogInsertCol = dbConnection.prepareStatement(strLogInsertCol, Statement.RETURN_GENERATED_KEYS);
			stmtLogInsertObj = dbConnection.prepareStatement(strLogInsertObj, Statement.RETURN_GENERATED_KEYS);
			stmtLogDeleteColObj = dbConnection.prepareStatement(strLogDeleteColObj);
			stmtLogDeleteCol = dbConnection.prepareStatement(strLogDeleteCol);
			stmtLogDeleteObj = dbConnection.prepareStatement(strLogDeleteObj);

			stmtIdsSelect = dbConnection.prepareStatement(strIdsSelect);
			stmtIdsCount = dbConnection.prepareStatement(strIdsCount);
			stmtIdsUpdate = dbConnection.prepareStatement(strIdsUpdate);
			stmtIdsInsert = dbConnection.prepareStatement(strIdsInsert);

			isConnected = dbConnection != null;
		} catch (Exception e) {
			e.printStackTrace();
			isConnected = false;
		}
		return isConnected;
	}

	public void disconnect() {
		if (isConnected) {
			dbProperties.put("shutdown", "true");
			try {
				DriverManager.getConnection(getDbUrl(), dbProperties);
			} catch (Exception e) {
				e.printStackTrace();
			}
			isConnected = false;
		}
	}

	public long getReplicationLastId(String jid) {
		try {
			stmtIdsSelect.clearParameters();
			stmtIdsSelect.setString(1, jid);
			ResultSet result = stmtIdsSelect.executeQuery();
			return result.next() ? result.getLong(1) : 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public Replication getReplicationNext(long id) {
		Statement stmtQueryTable = null;
		try {
			stmtQueryTable = dbConnection.createStatement();
			stmtLogSelect.clearParameters();
			stmtLogSelect.setLong(1, id);
			ResultSet resultLog = stmtLogSelect.executeQuery();
			if (resultLog.next()) {
				Replication replication = new Replication();
				replication.setId(resultLog.getLong("id"));
				replication.setTimestamp(resultLog.getLong("obj_timestamp"));
				switch (resultLog.getString("obj_operation").charAt(0)) {
				case Operation.CREATED:
					replication.setOperationCreated();
					break;
				case Operation.UPDATED:
					replication.setOperationUpdated();
					break;
				case Operation.DELETED:
					replication.setOperationDeleted();
					break;
				}
				replication.setCollection(resultLog.getString("obj_collection"));
				replication.setKey(resultLog.getString("obj_key"));
				if (
						replication.getOperation() != Operation.DELETED && // not deleted, deleted objects don't have XML anymore
						! isNullOrEmpty(resultLog.getString("obj_key")) // object, not collection
				) {
					ResultSet resultObj = stmtQueryTable.executeQuery(
							"SELECT XMLSERIALIZE(obj_xmls11n AS CLOB) FROM " + resultLog.getString("obj_collection") + " " +
							"WHERE obj_key = '" + resultLog.getString("obj_key") + "'");
					if (resultObj.next()) {
						replication.setXmlS11N(resultObj.getString(1));
					}
				}
				return replication;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmtQueryTable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void putReplication(String jid, Replication replication) { // replication object
		try {
			stmtIdsCount.clearParameters();
			stmtIdsCount.setString(1, jid);
			ResultSet drsIdsCount = stmtIdsCount.executeQuery();
			if (drsIdsCount.next() && drsIdsCount.getInt(1) > 0) {
				stmtIdsUpdate.clearParameters();
				stmtIdsUpdate.setLong(1, replication.getId());
				stmtIdsUpdate.setString(2, jid);
				stmtIdsUpdate.executeUpdate();
			} else {
				stmtIdsInsert.clearParameters();
				stmtIdsInsert.setString(1, jid);
				stmtIdsInsert.setLong(2, replication.getId());
				stmtIdsInsert.executeUpdate();
			}
			dbConnection.commit();
			if (isNullOrEmpty(replication.getKey())) {
				if (replication.isOperationCreated()) {
					if (createCollection(replication.getTimestamp(), replication.getCollection())) {
						notifyObserversCollectionCreatedOnReplication(replication.getCollection());
					}
				}
				if (replication.isOperationDeleted()) {
					if (dropCollection(replication.getTimestamp(), replication.getCollection())) {
						notifyObserversCollectionDroppedOnReplication(replication.getCollection());
					}
				}
			} else {
				stmtLogCountFreshObj.clearParameters();
				stmtLogCountFreshObj.setLong(1, replication.getTimestamp());
				stmtLogCountFreshObj.setString(2, replication.getCollection());
				stmtLogCountFreshObj.setString(3, replication.getKey());
				ResultSet drsLogCountFreshObj = stmtLogCountFreshObj.executeQuery();
				if (! (drsLogCountFreshObj.next() && drsLogCountFreshObj.getInt(1) > 0)) {
					if (replication.isOperationCreated() || replication.isOperationUpdated()) {
						if (putObject(replication.getTimestamp(), replication.getCollection(), replication.getKey(), replication.getXmlS11N())) {
							notifyObserversObjectPutOnReplication(replication.getCollection(), replication.getKey());
						}
					}
					if (replication.isOperationDeleted()) {
						if (removeObject(replication.getTimestamp(), replication.getCollection(), replication.getKey())) {
							notifyObserversObjectRemovedOnReplication(replication.getCollection(), replication.getKey());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Private */

	private boolean createCollection(long timestamp, String collection) {
		if (isCollectionDeleted(collection)) {
			return false;
		} else {
			Statement stmt = null;
			try {
				stmt = dbConnection.createStatement();
				stmt.executeUpdate(
						"CREATE TABLE " + collection + " (" +
						"	obj_key VARCHAR(1024) NOT NULL PRIMARY KEY," +
						"	obj_xmls11n XML NOT NULL" +
				")");
				logCollection(timestamp, Operation.CREATED, collection);
				dbConnection.commit();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean dropCollection(long timestamp, String collection) {
		if (isCollectionDeleted(collection)) {
			return false;
		} else {
			Statement stmt = null;
			try {
				stmt = dbConnection.createStatement();
				stmt.execute("DROP TABLE " + collection);
				logCollection(timestamp, Operation.DELETED, collection);
				dbConnection.commit();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// http://www.devx.com/dbzone/Article/33482/1954
	// http://216.239.59.104/search?q=cache:VAkQYRGbH7UJ:db.apache.org/derby/binaries/Apache_Derby10_2_Whats_New-2006.ppt+apache+derby+xquery&hl=en&ct=clnk&cd=1
	private boolean putObject(long timestamp, String collection, String key, String xml) {
		if (isObjectDeleted(collection, key)) {
			return false;
		} else {
			if (containsObject(collection, key)) {
				Statement stmtUpdate = null;
				try {
					stmtUpdate = dbConnection.createStatement();
					stmtUpdate.executeUpdate(
							"UPDATE " + collection + " " +
							"SET obj_xmls11n = XMLPARSE (DOCUMENT CAST ('" + xml + "' AS CLOB) PRESERVE WHITESPACE) " +
							"WHERE obj_key = '" + key + "'");
					logObject(timestamp, Operation.UPDATED, collection, key);
					dbConnection.commit();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				} finally {
					try {
						stmtUpdate.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				Statement stmtInsert = null;
				try {
					stmtInsert = dbConnection.createStatement();
					stmtInsert.executeUpdate(
							"INSERT INTO " + collection + " (" +
							"	obj_key, obj_xmls11n" +
							") VALUES (" +
							"	'" + key + "', XMLPARSE (DOCUMENT CAST ('" + xml + "' AS CLOB) PRESERVE WHITESPACE)" +
					")");
					logObject(timestamp, Operation.CREATED, collection, key);
					dbConnection.commit();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				} finally {
					try {
						stmtInsert.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private boolean removeObject(long timestamp, String collection, String key) {
		if (isObjectDeleted(collection, key)) {
			return false;
		} else {
			Statement stmt = null;
			try {
				stmt = dbConnection.createStatement();
				stmt.executeUpdate(
						"DELETE FROM " + collection + " " +
						"WHERE obj_key = '" + key + "'");
				logObject(timestamp, Operation.DELETED, collection, key);
				dbConnection.commit();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void logCollection(long timestamp, char operation, String collection) {
		try {
			// delete
			if (operation == Operation.DELETED) {
				stmtLogDeleteColObj.clearParameters();
				stmtLogDeleteColObj.setString(1, collection);
				stmtLogDeleteColObj.executeUpdate();
			} else {
				stmtLogDeleteCol.clearParameters();
				stmtLogDeleteCol.setString(1, collection);
				stmtLogDeleteCol.executeUpdate();
			}
			// insert
			stmtLogInsertCol.clearParameters();
			stmtLogInsertCol.setLong(1, timestamp);
			stmtLogInsertCol.setString(2, String.valueOf(operation).toUpperCase());
			stmtLogInsertCol.setString(3, collection);
			stmtLogInsertCol.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logObject(long timestamp, char operation, String collection, String key) {
		try {
			// delete
			stmtLogDeleteObj.clearParameters();
			stmtLogDeleteObj.setString(1, collection);
			stmtLogDeleteObj.setString(2, key);
			stmtLogDeleteObj.executeUpdate();
			// insert
			stmtLogInsertObj.clearParameters();
			stmtLogInsertObj.setLong(1, timestamp);
			stmtLogInsertObj.setString(2, String.valueOf(operation).toUpperCase());
			stmtLogInsertObj.setString(3, collection);
			stmtLogInsertObj.setString(4, key);
			stmtLogInsertObj.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isNullOrEmpty(String key) {
		return key == null || key.equals("");
	}

	/* Private (Initialization) */

	private void setDbSystemDirectory() throws Exception {
		File directory = new File(ParameterServiceImpl.getInstance().getStorage(), dbDirName);
		System.setProperty("derby.system.home", directory.getAbsolutePath());
		directory.mkdirs();
	}

	private void setDbProperties() throws Exception {
		dbProperties.setProperty("user", ParameterServiceImpl.getInstance().getXmppBareUsername());
		dbProperties.setProperty("password", ParameterServiceImpl.getInstance().getXmppPassword());
		dbProperties.setProperty("derby.driver", derbyDriver);
		dbProperties.setProperty("derby.url", derbyUrl);
	}

	private void loadDbDriver() throws Exception {
		Class.forName(dbProperties.getProperty("derby.driver")).newInstance();
	}

	private boolean dbExists() {
		return new File(getDbLocation()).exists();
	}

	private boolean createDb() throws Exception {
		boolean created = false;
		dbProperties.put("create", "true");
		Connection dbConnection = DriverManager.getConnection(getDbUrl(), dbProperties);
		created = createDbTables(dbConnection);
		dbProperties.remove("create");
		return created;
	}

	private boolean createDbTables(Connection dbConnection) throws Exception {
		dbConnection.createStatement().execute(strLogCreate);
		dbConnection.createStatement().execute(strIdsCreate);
		return true;
	}

	private String getDbLocation() {
		return System.getProperty("derby.system.home") + "/" + dbName;
	}

	private String getDbUrl() {
		return dbProperties.getProperty("derby.url") + dbName;
	}

	/* Observer Notification */

	private void notifyObserversCollectionCreated(String collection) {
		notifyDatabaseServiceObservers("collectionCreated", collection, null);
	}

	private void notifyObserversCollectionDropped(String collection) {
		notifyDatabaseServiceObservers("collectionDropped", collection, null);
	}

	private void notifyObserversObjectPut(String collection, String key) {
		notifyDatabaseServiceObservers("objectPut", collection, key);
		if (collection.equalsIgnoreCase(ContactServiceImpl.COLLECTION)) {
			notifyContactServiceObservers("contactPut" ,key);
		}
	}

	private void notifyObserversObjectRemoved(String collection, String key) {
		notifyDatabaseServiceObservers("objectRemoved", collection, key);
		if (collection.equalsIgnoreCase(ContactServiceImpl.COLLECTION)) {
			notifyContactServiceObservers("contactRemoved" ,key);
		}
	}

	private void notifyObserversCollectionCreatedOnReplication(String collection) {
		notifyDatabaseServiceObservers("collectionCreatedOnReplication", collection, null);
	}

	private void notifyObserversCollectionDroppedOnReplication(String collection) {
		notifyDatabaseServiceObservers("collectionDroppedOnReplication", collection, null);
	}

	private void notifyObserversObjectPutOnReplication(String collection, String key) {
		notifyDatabaseServiceObservers("objectPutOnReplication", collection, key);
		if (collection.equalsIgnoreCase(ContactServiceImpl.COLLECTION)) {
			notifyContactServiceObservers("contactPutOnReplication" ,key);
		}
	}

	private void notifyObserversObjectRemovedOnReplication(String collection, String key) {
		notifyDatabaseServiceObservers("objectRemovedOnReplication", collection, key);
		if (collection.equalsIgnoreCase(ContactServiceImpl.COLLECTION)) {
			notifyContactServiceObservers("contactRemovedOnReplication" ,key);
		}
	}
	
	private void notifyDatabaseServiceObservers(String methodName, String collection, String key) {
		Object[] observers = Activator.getDefault().getDatabaseObserverServiceTracker().getServices();
		if (observers != null && observers.length > 0) {
			for (Object observer : observers) {
				try {
					Method method;
					if (key == null) {
						method = observer.getClass().getMethod(methodName, String.class);
						method.invoke(observer, collection);
					} else {
						method = observer.getClass().getMethod(methodName, String.class, String.class);
						method.invoke(observer, collection, key);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void notifyContactServiceObservers(String methodName, String key) {
		Object[] observers = Activator.getDefault().getContactObserverServiceTracker().getServices();
		if (observers != null && observers.length > 0) {
			for (Object observer : observers) {
				try {
					observer.getClass().getMethod(methodName, String.class).invoke(observer, key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
