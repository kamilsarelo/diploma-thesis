package at.jku.tk.hermes.sample.ui.swing.internal;

/* Imports ********************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.io.IOCallback;
import at.jku.tk.hermes.io.IOService;
import at.jku.tk.hermes.io.input.Input;
import at.jku.tk.hermes.io.input.InputExecutionFailedException;
import at.jku.tk.hermes.io.input.StringInput;
import at.jku.tk.hermes.io.output.OutputExecutionFailedException;
import at.jku.tk.hermes.io.output.StringOutput;

/* Class **********************************************************************/

public class PaneIO extends JPanel implements IOCallback {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final PaneIO INSTANCE = new PaneIO();
	}

	public static PaneIO getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final long serialVersionUID = -1944099252458243546L;

	/* Constructors ***********************************************************/

	private PaneIO() {
		setLayout(new MigLayout(
				"insets 7, fillx", // layout constraints 
				"[]7[]", // column constraints
				"" // row constraints
		));

		JLabel labelNewContact = new JLabel("<html><b>IOService and IOCallback</b></html>");
		add(labelNewContact, "span, wrap 7");

		JButton button = new JButton("TRIGGER Java Swing String Input and Output");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				StringInput input = new StringInput();
				MetaData dataInput = new MetaData();
				dataInput.setName("String Input");
				dataInput.setDescription("Input random string value");
				input.setMetaData(dataInput);
				try {
					IOService service = (IOService) Activator.getDefault().getIterfaceServiceTracker().getService();
					service.executeInput(input, PaneIO.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		add(button);
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public void onInput(Input<?> input) {
		if (input instanceof StringInput) {
			System.out.println("input performed");
			// output
			StringOutput output = new StringOutput();
			MetaData dataOutput = new MetaData();
			dataOutput.setName("String Output");
			output.setMetaData(dataOutput);
			output.setOutput(((StringInput) input).getInput());
			try {
				IOService service = (IOService) Activator.getDefault().getIterfaceServiceTracker().getService();
				service.executeOutput(output, PaneIO.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onInputExecutionFailed() {
		new InputExecutionFailedException().printStackTrace();
	}

	public void onOutput() {
		System.out.println("output performed");
	}

	public void onOutputExecutionFailed() {
		new OutputExecutionFailedException().printStackTrace();
	}

}
