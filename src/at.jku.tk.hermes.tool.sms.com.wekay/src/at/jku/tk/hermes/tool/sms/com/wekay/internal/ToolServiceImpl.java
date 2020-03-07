package at.jku.tk.hermes.tool.sms.com.wekay.internal;

/* Imports ********************************************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLEncoder;

import at.jku.tk.hermes.action.Action;
import at.jku.tk.hermes.action.MessageSendToManyAction;
import at.jku.tk.hermes.action.MessageSendToOneAction;
import at.jku.tk.hermes.contact.Contact;
import at.jku.tk.hermes.contact.ProtocolNotSupportedByContactException;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.protocol.Protocol;
import at.jku.tk.hermes.protocol.SMSGatewayProtocol;
import at.jku.tk.hermes.tool.AbstractToolService;
import at.jku.tk.hermes.tool.ActionExecutionFailedException;
import at.jku.tk.hermes.tool.ActionNotSupportedException;
import at.jku.tk.hermes.tool.ProtocolNotSupportedByToolException;

/* Class **********************************************************************/

public class ToolServiceImpl extends AbstractToolService {

	/* Constructors ***********************************************************/

	public ToolServiceImpl() {
		addSupportedProtocol(SMSGatewayProtocol.class);
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public MetaData getMetaData() {
		MetaData data = new MetaData();
		data.setName(this.getClass().getSimpleName());
		data.setDescription(
				"Sends a SMS via the wekay.com gateway. Expects certain values " +
				"to be available as system properties: " +
				"at.jku.tk.hermes.tool.sms.com.wekay.applicationid - " +
				"application id for the wekay.com gateway, " +
				"at.jku.tk.hermes.tool.sms.com.wekay.username - username for " +
				"the wekay.com gateway and " +
				"at.jku.tk.hermes.tool.sms.com.wekay.password - password for " +
				"the wekay.com gateway."
		);
		return data;
	}

	public void executeAction(Class<? extends Protocol> clazz, Action action) throws ProtocolNotSupportedByToolException, ActionNotSupportedException, ActionExecutionFailedException {
		if (! supportsProtocol(clazz)) {
			throw new ProtocolNotSupportedByToolException();
		}
		if (action instanceof MessageSendToOneAction) {
			smsSendToOne(clazz, (MessageSendToOneAction) action);
		} else if (action instanceof MessageSendToManyAction) {
			smsSendToMany(clazz, (MessageSendToManyAction) action);
		} else {
			throw new ActionNotSupportedException();
		}
	}

	/* Private */

	private void smsSendToOne(Class<? extends Protocol> clazz, MessageSendToOneAction action) throws ActionExecutionFailedException {
		try {
			String to = action.getContact().getIdentity(clazz);
			to = to.startsWith("00") ? to.substring(2) : to.startsWith("+") ? to.substring(1) : to; // wekay.com limitations
			smsSend(to, action.getMessage());
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void smsSendToMany(Class<? extends Protocol> clazz, MessageSendToManyAction action) throws ActionExecutionFailedException {
		try {
			StringBuilder deliverTo = new StringBuilder();
			int c = 0;
			for (Contact contact : action.getContacts()) {
				try {
					String to = contact.getIdentity(clazz);
					deliverTo.append(to.startsWith("00") ? to.substring(2) : to.startsWith("+") ? to.substring(1) : to); // wekay.com limitations
					deliverTo.append(",");
					if (c++ >= 100) { // wekay.com limit
						break;
					}
				} catch (ProtocolNotSupportedByContactException e) {
					e.printStackTrace();
				}
			}
			smsSend(deliverTo.subSequence(0, deliverTo.length() - 1).toString(), action.getMessage());
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private void smsSend(String deliverTo, String message) throws ActionExecutionFailedException {
		try {
			if (isSmsCredit()) {
				String response = fetchWekayCom(
						"deliverto=" + deliverTo + "&" +
						"message=" + URLEncoder.encode(message, "UTF-8"));
				if (response.toLowerCase().equals("ok")) {
					return;
				} else {
					throw new ActionExecutionFailedException(response);
				}
			} else {
				throw new ActionExecutionFailedException("no credit");
			}
		} catch (Exception e) {
			throw new ActionExecutionFailedException(e.fillInStackTrace().getCause());
		}
	}

	private boolean isSmsCredit() {
		try {
			return Float.parseFloat(fetchWekayCom("showcredits=yes")) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private String fetchWekayCom(final String data) throws FetchWekayComException {
		String response = "";
		Socket socket = null;
		try {
			socket = new Socket("gw1.sms.wekay.com", 80);
			StringBuilder requestData = new StringBuilder();
			requestData.append("application_id=" + URLEncoder.encode(System.getProperty("at.jku.tk.hermes.tool.sms.com.wekay.applicationid"), "UTF-8") + "&");
			requestData.append("username=" + URLEncoder.encode(System.getProperty("at.jku.tk.hermes.tool.sms.com.wekay.username"), "UTF-8") + "&");
			requestData.append("pass=" + URLEncoder.encode(System.getProperty("at.jku.tk.hermes.tool.sms.com.wekay.password"), "UTF-8") + "&");
			requestData.append(data);
			StringBuilder requestHeader = new StringBuilder();
			requestHeader.append("POST / HTTP/1.1\r\n");
			requestHeader.append("Host: gw1.sms.wekay.com\r\n");
			requestHeader.append("Content-Type: application/x-www-form-urlencoded\r\n");
			requestHeader.append("Content-Length: " + requestData.length() + "\r\n");
			requestHeader.append("Connection: close\r\n\r\n");
			requestHeader.append(requestData);
			new PrintStream(socket.getOutputStream()).println(requestHeader);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			boolean memorize = false;
			String line = in.readLine();
			while (line != null) {
				if (line.trim().equals("<body>")) {
					memorize = true;
				}
				if (line.trim().equals("</body>")) {
					memorize = false;
				}
				if (memorize) {
					response = line.trim();
				}
				line = in.readLine();
			}
		} catch (Exception e) {
			throw new FetchWekayComException(e.fillInStackTrace().getCause());
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return response;
	}

	/* Inner Classes **********************************************************/

	private class FetchWekayComException extends Exception {

		/* Constants **********************************************************/

		private static final long serialVersionUID = 6345250872584771476L;

		/* Constructors *******************************************************/

		public FetchWekayComException() {}

		public FetchWekayComException(String message) {
			super(message);
		}

		public FetchWekayComException(Throwable cause) {
			super(cause);
		}

		public FetchWekayComException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
