package at.jku.tk.hermes.sample.ui.swing.internal;

/* Imports ********************************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/* Class **********************************************************************/

//http://forum.java.sun.com/thread.jspa?threadID=708960&tstart=75
//http://www.java2s.com/Code/Java/Swing-JFC/TextPaneSample.htm
public class PaneSystemPrintStream extends JPanel {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final PaneSystemPrintStream INSTANCE = new PaneSystemPrintStream();
	}

	public static PaneSystemPrintStream getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final long serialVersionUID = -5733658955051421990L;

	/* Fields *****************************************************************/

	private StyledDocument document = new DefaultStyledDocument(new StyleContext());
	private JTextPane textPane = new JTextPane(document);
	private JScrollPane scrollPane = new JScrollPane(textPane);

	/* Constructors ***********************************************************/

	private PaneSystemPrintStream() {
		textPane.setEditable(false);
		scrollPane.setBorder(null);

		final SimpleAttributeSet attributesOut = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attributesOut, "Courier New");
		StyleConstants.setFontSize(attributesOut, 11);
		StyleConstants.setForeground(attributesOut, Color.black);

		final SimpleAttributeSet attributesErr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attributesErr, "Courier New");
		StyleConstants.setFontSize(attributesErr, 11);
		StyleConstants.setForeground(attributesErr, Color.red);

		JLabel label = new JLabel("<html><b>System.out and System.err</b></html>");
		label.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

		setLayout(new BorderLayout());
		add(label, BorderLayout.PAGE_START);
		add(scrollPane, BorderLayout.CENTER);

		PrintStream out = new PrintStream(new TextPaneOutputStream(document, Color.black));
		System.setOut(out);
		PrintStream outerr = new PrintStream(new TextPaneOutputStream(document, Color.red));
		System.setErr(outerr);
	}

	/* Inner Classes **********************************************************/

	// http://www.jcreator.com/forums/index.php?showtopic=773
	public class TextPaneOutputStream extends OutputStream {

		/* Fields *************************************************************/

		private StyledDocument document;
		private SimpleAttributeSet attributes = new SimpleAttributeSet();

		/* Constants **********************************************************/

		public TextPaneOutputStream(StyledDocument document, Color color) {
			this.document = document;
			StyleConstants.setFontFamily(attributes, "Courier New");
			StyleConstants.setFontSize(attributes, 11);
			StyleConstants.setForeground(attributes, color);
		}

		/* Methods ************************************************************/

		public void write(int b) throws IOException {
			try {
				document.insertString(
						document.getLength(),
						String.valueOf((char) b),
						attributes);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			JViewport viewport = scrollPane.getViewport();
			viewport.setViewPosition(new Point(0, viewport.getView().getHeight() - viewport.getHeight()));
		}

	}

}
