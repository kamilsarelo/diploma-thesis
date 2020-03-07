package at.jku.tk.hermes.sample.ui.swing.internal;

/* Imports ********************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import at.jku.tk.hermes.contact.Contact;
import at.jku.tk.hermes.contact.ContactService;
import at.jku.tk.hermes.protocol.EmailClientProtocol;
import at.jku.tk.hermes.protocol.EmailSmtpProtocol;
import at.jku.tk.hermes.protocol.PhoneFixedProtocol;
import at.jku.tk.hermes.protocol.PhoneMobileProtocol;
import at.jku.tk.hermes.protocol.Protocol;
import at.jku.tk.hermes.protocol.SMSGatewayProtocol;
import at.jku.tk.hermes.protocol.SkypeProtocol;
import at.jku.tk.hermes.protocol.SkypePstnProtocol;

/* Class **********************************************************************/

public class PaneContactNew extends JPanel {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final PaneContactNew INSTANCE = new PaneContactNew();
	}

	public static PaneContactNew getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final long serialVersionUID = -8527695109912256937L;

	/* Fields *****************************************************************/

	JTextField textFieldName = new JTextField(10);
	JComboBox comboBoxClassesA = new JComboBox(new String[] {
			EmailClientProtocol.class.getName(),
			EmailSmtpProtocol.class.getName(),
			PhoneFixedProtocol.class.getName(),
			PhoneMobileProtocol.class.getName(),
			SkypeProtocol.class.getName(),
			SkypePstnProtocol.class.getName(),
			SMSGatewayProtocol.class.getName()
	});
	JComboBox comboBoxClassesB = new JComboBox(new String[] {
			EmailClientProtocol.class.getName(),
			EmailSmtpProtocol.class.getName(),
			PhoneFixedProtocol.class.getName(),
			PhoneMobileProtocol.class.getName(),
			SkypeProtocol.class.getName(),
			SkypePstnProtocol.class.getName(),
			SMSGatewayProtocol.class.getName()
	});
	JTextField textFieldIdentityA = new JTextField(10);
	JTextField textFieldIdentityB = new JTextField(10);

	/* Constructors ***********************************************************/

	private PaneContactNew() {
		setLayout(new MigLayout(
				"insets 7, fillx", // layout constraints 
				"[]7[]", // column constraints
				"" // row constraints
		));

		JLabel labelNewContact = new JLabel("<html><b>New Contact</b></html>");
		add(labelNewContact, "span, wrap 7");

		JLabel labelName = new JLabel("Name:");
		add(labelName, "gapleft 14");
		add(textFieldName, "growx, wrap");

		JLabel labelProtocolA = new JLabel("Protocol 1:");
		add(labelProtocolA, "gapleft 14");
		add(comboBoxClassesA, "growx, wrap");

		add(new JLabel("Identity 1:"), "gapleft 14");
		add(textFieldIdentityA, "growx, wrap");

		JLabel labelProtocolB = new JLabel("Protocol 2:");
		add(labelProtocolB, "gapleft 14");
		add(comboBoxClassesB, "growx, wrap");
		
		add(new JLabel("Identity 2:"), "gapleft 14");
		add(textFieldIdentityB, "growx, wrap");
		
		JButton button = new JButton("CREATE");
		button.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent event) {
				if (textFieldName.getText().equals("")) {
					throw new NullPointerException();
				}
				Contact contact = new Contact(textFieldName.getText());
				boolean firstValid = ! textFieldIdentityA.getText().equals("");
				if (firstValid) {
					try {
						Class<? extends Protocol> clazz = (Class<? extends Protocol>) Class.forName((String) comboBoxClassesA.getSelectedItem()); 
						contact.putIdentity(clazz, textFieldIdentityA.getText());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// only if first valid, check if equals first
				boolean secondValid = ! textFieldIdentityB.getText().equals("");
				if (secondValid && (! firstValid || (firstValid && ! ((String) comboBoxClassesA.getSelectedItem()).equals((String) comboBoxClassesB.getSelectedItem())))) {
					try {
						Class<? extends Protocol> clazz = (Class<? extends Protocol>) Class.forName((String) comboBoxClassesB.getSelectedItem()); 
						contact.putIdentity(clazz, textFieldIdentityB.getText());
					} catch (Exception e) {
					}
				}
				try {
					ContactService service = (ContactService) Activator.getDefault().getContactServiceTracker().getService();
					service.putContact(UUID.randomUUID().toString(), contact);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		add(button, "span, alignx trailing");
	}

}
