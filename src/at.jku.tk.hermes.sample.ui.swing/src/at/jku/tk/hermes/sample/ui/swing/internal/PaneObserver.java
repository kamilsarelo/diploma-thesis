package at.jku.tk.hermes.sample.ui.swing.internal;

/* Imports ********************************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import at.jku.tk.hermes.contact.ContactObserverService;
import at.jku.tk.hermes.core.DatabaseObserverService;

/* Class **********************************************************************/

//http://forum.java.sun.com/thread.jspa?threadID=708960&tstart=75
//http://www.java2s.com/Code/Java/Swing-JFC/TextPaneSample.htm
public class PaneObserver extends JPanel implements ContactObserverService, DatabaseObserverService {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final PaneObserver INSTANCE = new PaneObserver();
	}

	public static PaneObserver getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final long serialVersionUID = 4360810683796720251L;
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // HH:mm:ss.SSS

	/* Fields *****************************************************************/

	private StyleContext context = new StyleContext();
	private Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
	private StyledDocument document = new DefaultStyledDocument(context);
	private JTextPane textPane = new JTextPane(document);
	private JScrollPane scrollPane = new JScrollPane(textPane);

	/* Constructors ***********************************************************/

	private PaneObserver() {
		textPane.setEditable(false);
		scrollPane.setBorder(null);

		StyleConstants.setFontFamily(style, "Courier New");
		StyleConstants.setFontSize(style, 11);
		StyleConstants.setForeground(style, Color.black);

		JLabel label = new JLabel("<html><b>ContactObserverService and DatabaseObserverService</b></html>");
		label.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

		setLayout(new BorderLayout());
		add(label, BorderLayout.PAGE_START);
		add(scrollPane, BorderLayout.CENTER);
	}

	/* Methods ****************************************************************/

	/* ContactObserverService Implementation */

	public void contactPut(String key) {
		write(dateFormat.format(new Date()) + " contact put\n\tkey: " + key);
	}

	public void contactRemoved(String key) {
		write(dateFormat.format(new Date()) + " contact removed\n\tkey: " + key);
	}

	public void contactPutOnReplication(String key) {
		write(dateFormat.format(new Date()) + " contact put on replication\n\tkey: " + key);
	}

	public void contactRemovedOnReplication(String key) {
		write(dateFormat.format(new Date()) + " contact removed on replication\n\tkey: " + key);
	}

	/* DatabaseObserverService Implementation */

	public void collectionCreated(String collection) {
		write(dateFormat.format(new Date()) + " collection created\n\tcollection: " + collection);
	}

	public void collectionDropped(String collection) {
		write(dateFormat.format(new Date()) + " collection dropped\n\tcollection: " + collection);
	}

	public void objectPut(String collection, String key) {
		write(dateFormat.format(new Date()) + " object put\n\tcollection: " + collection + "\n\tkey: " + key);
	}

	public void objectRemoved(String collection, String key) {
		write(dateFormat.format(new Date()) + " object removed\n\tcollection: " + collection + "\n\tkey: " + key);
	}

	public void collectionCreatedOnReplication(String collection) {
		write(dateFormat.format(new Date()) + " collection created on replication\n\tcollection: " + collection);
	}

	public void collectionDroppedOnReplication(String collection) {
		write(dateFormat.format(new Date()) + " collection dropped on replication\n\tcollection: " + collection);
	}

	public void objectPutOnReplication(String collection, String key) {
		write(dateFormat.format(new Date()) + " object put on replication\n\tcollection: " + collection + "\n\tkey: " + key);
	}

	public void objectRemovedOnReplication(String collection, String key) {
		write(dateFormat.format(new Date()) + " object removed on replication\n\tcollection: " + collection + "\n\tkey: " + key);
	}

	/* Private */

	private void write(String string) {
		try {
			document.insertString(
					document.getLength(),
					string + "\n",
					style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		JViewport viewport = scrollPane.getViewport();
		viewport.setViewPosition(new Point(0, viewport.getView().getHeight() - viewport.getHeight()));
	}

}
