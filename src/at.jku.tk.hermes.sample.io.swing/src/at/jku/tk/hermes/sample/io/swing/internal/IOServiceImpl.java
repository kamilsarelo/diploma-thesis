package at.jku.tk.hermes.sample.io.swing.internal;

/* Imports ********************************************************************/

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.io.AbstractIOService;
import at.jku.tk.hermes.io.IOCallback;
import at.jku.tk.hermes.io.input.Input;
import at.jku.tk.hermes.io.input.InputNotSupportedException;
import at.jku.tk.hermes.io.input.StringInput;
import at.jku.tk.hermes.io.output.Output;
import at.jku.tk.hermes.io.output.OutputNotSupportedException;
import at.jku.tk.hermes.io.output.StringOutput;

/* Class **********************************************************************/

public class IOServiceImpl extends AbstractIOService {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final IOServiceImpl INSTANCE = new IOServiceImpl();
	}

	public static IOServiceImpl getInstance() {
		return Holder.INSTANCE;
	}

	/* Constructors ***********************************************************/

	private IOServiceImpl() {
		addSupportedInput(StringInput.class);
		addSupportedOutput(StringOutput.class);
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public MetaData getMetaData() {
		MetaData data = new MetaData();
		data.setName(this.getClass().getSimpleName());
		data.setDescription("Supports input and output of strings via a Java Swing GUI.");
		return data;
	}

	public void executeInput(final Input<?> input, final IOCallback callback) throws InputNotSupportedException {
		if (input instanceof StringInput) {
			// request input
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// http://java.sun.com/docs/books/tutorial/uiswing/components/dialog.html#stayup
					JFrame frame = new JFrame();
					JLabel label = new JLabel(((StringInput) input).getMetaData().getDescription() + ":");
					JTextField textField = new JTextField(10);
					Object[] array = { label, textField };
					final JOptionPane optionPane = new JOptionPane(
							array,
							JOptionPane.PLAIN_MESSAGE,
							JOptionPane.DEFAULT_OPTION);
					final JDialog dialog = new JDialog(
							frame, 
							((StringInput) input).getMetaData().getName(),
							true);
					dialog.setContentPane(optionPane);
					dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					optionPane.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent event) {
							String propertyName = event.getPropertyName();
							if (
									dialog.isVisible() 
									&& event.getSource() == optionPane
									&& propertyName.equals(JOptionPane.VALUE_PROPERTY)
							) {
								dialog.setVisible(false);
								dialog.dispose();
							}
						}
					});
					dialog.pack();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
					// method sleeps here until the dialog is closed
					if (((Integer) optionPane.getValue()).intValue() == JOptionPane.OK_OPTION) {
						((StringInput) input).setInput(textField.getText());
					} else {
						// e.g. when pressed ESC, input still == null
					}
					// return input
					if (
							((StringInput) input).getInput() != null &&
							! ((StringInput) input).getInput().equals("")
					) {
						callback.onInput(input);
					} else {
						callback.onInputExecutionFailed();
					}
				}
			});
		} else {
			throw new InputNotSupportedException();
		}
	}

	public void executeOutput(final Output<?> output, final IOCallback callback) throws OutputNotSupportedException {
		if (output instanceof StringOutput) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JFrame frame = new JFrame();
					JLabel label = new JLabel(((StringOutput) output).getOutput());
					Object[] array = { label };
					final JOptionPane optionPane = new JOptionPane(
							array,
							JOptionPane.PLAIN_MESSAGE,
							JOptionPane.DEFAULT_OPTION);
					final JDialog dialog = new JDialog(
							frame, 
							((StringOutput) output).getMetaData().getName(),
							true);
					dialog.setContentPane(optionPane);
					dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					optionPane.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent event) {
							String propertyName = event.getPropertyName();
							if (
									dialog.isVisible() 
									&& event.getSource() == optionPane
									&& propertyName.equals(JOptionPane.VALUE_PROPERTY)
							) {
								dialog.setVisible(false);
								dialog.dispose();
							}
						}
					});
					dialog.pack();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
					// method sleeps here until the dialog is closed
					callback.onOutput();
				}
			});
		} else {
			throw new OutputNotSupportedException();
		}
	}

}
