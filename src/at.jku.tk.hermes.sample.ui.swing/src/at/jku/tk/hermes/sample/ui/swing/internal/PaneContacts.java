package at.jku.tk.hermes.sample.ui.swing.internal;

/* Imports ********************************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import at.jku.tk.hermes.action.Action;
import at.jku.tk.hermes.action.AudioCallStartAction;
import at.jku.tk.hermes.action.EmailSendAction;
import at.jku.tk.hermes.action.MessageSendToOneAction;
import at.jku.tk.hermes.contact.Contact;
import at.jku.tk.hermes.contact.ContactObserverService;
import at.jku.tk.hermes.contact.ContactService;
import at.jku.tk.hermes.protocol.EmailClientProtocol;
import at.jku.tk.hermes.protocol.Protocol;
import at.jku.tk.hermes.protocol.SMSGatewayProtocol;
import at.jku.tk.hermes.protocol.SkypeProtocol;
import at.jku.tk.hermes.protocol.SkypePstnProtocol;
import at.jku.tk.hermes.tool.ToolService;

/* Class **********************************************************************/

public class PaneContacts extends JPanel implements ContactObserverService {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final PaneContacts INSTANCE = new PaneContacts();
	}

	public static PaneContacts getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final long serialVersionUID = -1249513473201789347L;

	/* Fields *****************************************************************/

	private LinkedBlockingQueue<Object> workQueue = new LinkedBlockingQueue<Object>();
	private JPanel paneContacts = new JPanel(new MigLayout(
			"fillx", // layout constraints 
			"", // column constraints
			"" // row constraints
	));
	private JScrollPane scrollPane = new JScrollPane(paneContacts);

	/* Constructors ***********************************************************/

	private PaneContacts() {
		setLayout(new BorderLayout());

		JLabel labelContacts = new JLabel("<html><b>Contacts</b></html>");
		labelContacts.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
		add(labelContacts, BorderLayout.PAGE_START);

		scrollPane.setBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		fillPaneContacts();

		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Object work = workQueue.take();
						if (work == null) {
							break;
						}
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								clearPaneContacts();
								fillPaneContacts();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public void contactPut(String key) {
		putWork();
	}

	public void contactRemoved(String key) {
		putWork();
	}

	public void contactPutOnReplication(String key) {
		putWork();
	}

	public void contactRemovedOnReplication(String key) {
		putWork();
	}

	/* Private */

	private void putWork() {
		try {
			if (workQueue.isEmpty()) {
				workQueue.put(new Object());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearPaneContacts() {
		while (paneContacts.getComponentCount() > 0) {
			try {
				paneContacts.remove(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void fillPaneContacts() {
		for (KeyContact keyContact : getKeyContacts()) {
			try {
				paneContacts.add(getPane(keyContact), "growx, wrap");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		scrollPane.validate();
		scrollPane.repaint();
	}

	// http://snipplr.com/view/2789/sorting-map-keys-by-comparing-its-values/
	private List<KeyContact> getKeyContacts() {
		List<KeyContact> keyContacts = new ArrayList<KeyContact>();
		try {
			ContactService service = (ContactService) Activator.getDefault().getContactServiceTracker().getService();
			final Map<String, Contact> contacts = service.queryContacts("/");
			List<String> keys = new ArrayList<String>(contacts.keySet());
			Collections.sort(
					keys,
					new Comparator<String>() {
						public int compare(String o1, String o2) {
							return contacts.get(o1).getName().compareToIgnoreCase(contacts.get(o2).getName());
						}
					});
			for (Iterator<String> i = keys.iterator(); i.hasNext(); ) {
				String key = i.next();
				keyContacts.add(new KeyContact(key, contacts.get(key)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyContacts;
	}

	private JPanel getPane(KeyContact keyContact) {
		JPanel pane = new JPanel(new MigLayout(
				"insets 0 14 14 7, fillx", // layout constraints 
				"[]7[]", // column constraints
				"" // row constraints
		));

		JLabel labelName = new JLabel("Name:");
		pane.add(labelName);
		JLabel labelNameValue = new JLabel(keyContact.getContact().getName());
		labelNameValue.setForeground(Color.blue);
		pane.add(labelNameValue, "growx, wrap");

		for (Class<? extends Protocol> clazz : keyContact.getContact().getSupportedProtocols()) {
			try {
				String identity = keyContact.getContact().getIdentity(clazz); // try to retrieve here first, otherwise we may already add a Swing element before the exception is thrown
				pane.add(new JLabel(clazz.getName() + ":"));
				JLabel labelIdentity = new JLabel(identity);
				labelIdentity.setForeground(Color.blue);
				pane.add(labelIdentity, "growx, wrap");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JButton buttonDelete = new JButton("DELETE");
		buttonDelete.setName(keyContact.getKey());
		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String key = ((JButton) event.getSource()).getName();
					ContactService contactService = (ContactService) Activator.getDefault().getContactServiceTracker().getService();
					contactService.removeContact(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pane.add(buttonDelete, "span, split 4");

		JButton buttonCall = new JButton("CALL Skype");
		buttonCall.setName(keyContact.getKey());
		buttonCall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String key = ((JButton) event.getSource()).getName();
					ContactService contactService = (ContactService) Activator.getDefault().getContactServiceTracker().getService();
					Action action = new AudioCallStartAction(contactService.getContact(key));
					ToolService toolService = (ToolService) Activator.getDefault().getToolServiceSkypeTracker().getService();
					try {
						toolService.executeAction(SkypeProtocol.class, action);
						System.out.println("action performed (" + SkypeProtocol.class.getName() + ")");
					} catch (Exception e) {
						System.out.println("action failed (" + SkypeProtocol.class.getName() + ")");
						toolService.executeAction(SkypePstnProtocol.class, action);
						System.out.println("action performed (" + SkypePstnProtocol.class.getName() + ")");
//						System.out.println("alternative action performed");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pane.add(buttonCall);

		JButton buttonEmail = new JButton("COMPOSE @");
		buttonEmail.setName(keyContact.getKey());
		buttonEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String key = ((JButton) event.getSource()).getName();
					ContactService contactService = (ContactService) Activator.getDefault().getContactServiceTracker().getService();
					Action action = new EmailSendAction(contactService.getContact(key), "Hello World", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nullam ac sapien. Suspendisse non orci. Duis ultricies congue purus. Curabitur sagittis suscipit dolor. Nam et arcu. Nam dui est, tristique in, fermentum a, iaculis at, est. Vestibulum fermentum elit eu ligula. Integer sodales. Fusce ullamcorper. Phasellus molestie iaculis sem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Quisque enim. Nunc malesuada, mi vel rutrum vulputate, metus ante posuere nisi, id mollis diam elit ut felis. Fusce ut leo ac mi cursus sodales. Praesent lacus.");
					ToolService toolService = (ToolService) Activator.getDefault().getToolServiceEmailComposeTracker().getService();
					toolService.executeAction(EmailClientProtocol.class, action);
					System.out.println("action performed (" + EmailClientProtocol.class.getName() + ")");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pane.add(buttonEmail);

		JButton buttonSMS = new JButton("SEND SMS");
		buttonSMS.setName(keyContact.getKey());
		buttonSMS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String key = ((JButton) event.getSource()).getName();
					ContactService contactService = (ContactService) Activator.getDefault().getContactServiceTracker().getService();
					Action action = new MessageSendToOneAction(contactService.getContact(key), "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.");
					ToolService toolService = (ToolService) Activator.getDefault().getToolServiceSmsComWekayTracker().getService();
					toolService.executeAction(SMSGatewayProtocol.class, action);
					System.out.println("action performed (" + SMSGatewayProtocol.class.getName() + ")");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pane.add(buttonSMS);

		return pane;
	}

	/* Inner Classes **********************************************************/

	private class KeyContact {

		/* Fields *************************************************************/

		private String key;
		private Contact contact;

		/* Constructors *******************************************************/

		public KeyContact(String key, Contact contact) {
			if (
					key == null ||
					key.equals("") ||
					contact == null ||
					contact.equals("")
			) {
				throw new NullPointerException();
			}
			this.key = key;
			this.contact = contact;
		}

		/* Methods ************************************************************/

		public String getKey() {
			return key;
		}

		public Contact getContact() {
			return contact;
		}

	}

}
