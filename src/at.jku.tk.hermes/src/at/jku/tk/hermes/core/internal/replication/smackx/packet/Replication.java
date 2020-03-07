package at.jku.tk.hermes.core.internal.replication.smackx.packet;

/* Imports ********************************************************************/

import org.apache.commons.codec.net.URLCodec;
import org.jivesoftware.smack.packet.PacketExtension;

/* Class **********************************************************************/

public class Replication implements PacketExtension {

	/* Constants **************************************************************/


	public static final String NAMESPACE = "jabber:x:at:jku:tk:hermes:replication"; // packet extension's namespace
	public static final String NODENAME = "x"; // packet extension's element name

	/* Constants & Inner Classes **********************************************/

	public static final class Mode {

		/* Constants **********************************************************/

		public static final char REQUEST = 'Q';
		public static final char RESPONSE = 'A';

	}

	public static final class Operation {

		/* Constants **********************************************************/

		public static final char CREATED = 'C';
		public static final char UPDATED = 'U';
		public static final char DELETED = 'D';

	}

	/* Fields *****************************************************************/

	private char mode = 0;
	private long id = 0; // id in log table
	private long timestamp = 0;
	private char operation = 0;
	private String collection = "";
	private String key = "";
	private String xmlS11N = ""; // object's serialization in XML

	/* Methods ****************************************************************/

	/* Implementation */

	public String getElementName() {
		return NODENAME;
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public String toXML() {
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
		// ...
		if (mode != 0) {
			builder.append("<mode>").append(mode).append("</mode>");
			builder.append("<id>").append(id).append("</id>");
			if (isModeResponse()) {
				builder.append("<timestamp>").append(timestamp).append("</timestamp>");
				builder.append("<operation>").append(operation).append("</operation>");
				builder.append("<collection>").append(collection).append("</collection>");
				builder.append("<key>").append(key == null ? "" : key).append("</key>");
				try {
					// percent-encoding of the xml-serialization
					// because of the xml-pull-parser (ReplicationProvider) the serialization can't contain any xml-element
					builder.append("<xmlS11N>").append(new URLCodec().encode(xmlS11N)).append("</xmlS11N>");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// ...
		builder.append("</").append(getElementName()).append(">");
		return builder.toString();
	}

	/* Public */

	public boolean isModeRequest() {
		return mode == Mode.REQUEST;
	}

	public void setModeRequest() {
		mode = Mode.REQUEST;
	}

	public boolean isModeResponse() {
		return mode == Mode.RESPONSE;
	}

	public void setModeResponse() {
		mode = Mode.RESPONSE;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public char getOperation() {
		return operation;
	}

	public boolean isOperationCreated() {
		return operation == Operation.CREATED;
	}

	public void setOperationCreated() {
		operation = Operation.CREATED;
	}

	public boolean isOperationUpdated() {
		return operation == Operation.UPDATED;
	}

	public void setOperationUpdated() {
		operation = Operation.UPDATED;
	}

	public boolean isOperationDeleted() {
		return operation == Operation.DELETED;
	}

	public void setOperationDeleted() {
		operation = Operation.DELETED;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getXmlS11N() {
		return xmlS11N;
	}

	public void setXmlS11N(String xmlS11N) {
		this.xmlS11N = xmlS11N;
	}

}
