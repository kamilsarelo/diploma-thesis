package at.jku.tk.hermes.core.internal.replication.smackx.provider;

/* Imports ********************************************************************/

import org.apache.commons.codec.net.URLCodec;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import at.jku.tk.hermes.core.internal.replication.smackx.packet.Replication;
import at.jku.tk.hermes.core.internal.replication.smackx.packet.Replication.Mode;
import at.jku.tk.hermes.core.internal.replication.smackx.packet.Replication.Operation;

/* Class **********************************************************************/

public class ReplicationProvider implements PacketExtensionProvider {

	/* Constructors ***********************************************************/

	public ReplicationProvider() {}

	/* Methods ****************************************************************/

	/* PacketExtensionProvider */

	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		Replication replication = new Replication();
		boolean done = false;
		while (! done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("mode")) {
					switch (parser.nextText().charAt(0)) {
					case Mode.REQUEST:
						replication.setModeRequest();
						break;
					case Mode.RESPONSE:
						replication.setModeResponse();
						break;
					}
				}
				if (parser.getName().equals("id")) {
					replication.setId(Long.parseLong(parser.nextText()));
				}
				if (parser.getName().equals("timestamp")) {
					replication.setTimestamp(Long.parseLong(parser.nextText()));
				}
				if (parser.getName().equals("operation")) {
					switch (parser.nextText().charAt(0)) {
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
				}
				if (parser.getName().equals("collection")) {
					replication.setCollection(parser.nextText());
				}
				if (parser.getName().equals("key")) {
					replication.setKey(parser.nextText());
				}
				if (parser.getName().equals("xmlS11N")) {
					try {
						// precent-decoding of the xml-serialization
						// because of the xml-pull-parser the serialization can't contain any xml-element
						replication.setXmlS11N(new URLCodec().decode(parser.nextText()));
					} catch (Exception e) {
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("x")) {
					done = true;
				}
			}
		}
		return replication;
	}

}
