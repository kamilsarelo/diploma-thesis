package at.jku.tk.hermes.sample.ui.swing.internal;

/* Imports ********************************************************************/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

/* Class **********************************************************************/

public class Pane extends JPanel {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final Pane INSTANCE = new Pane();
	}

	public static Pane getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final long serialVersionUID = -1509974975866764770L;

	/* Constructors ***********************************************************/

	private Pane() {
		setLayout(new BorderLayout());

		Dimension minimumSize = new Dimension(200, 200);
		PaneContactNew.getInstance().setMinimumSize(minimumSize);
		PaneContacts.getInstance().setMinimumSize(minimumSize);
		PaneObserver.getInstance().setMinimumSize(minimumSize);
		PaneSystemPrintStream.getInstance().setMinimumSize(minimumSize);

		JSplitPane splitPaneChild = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				PaneObserver.getInstance(),
				PaneSystemPrintStream.getInstance());
		splitPaneChild.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.3));
		splitPaneChild.setOneTouchExpandable(true);

		JPanel paneLeft = new JPanel(new BorderLayout());
		paneLeft.add(PaneContactNew.getInstance(), BorderLayout.PAGE_START);
		paneLeft.add(PaneContacts.getInstance(), BorderLayout.CENTER);
		paneLeft.add(PaneIO.getInstance(), BorderLayout.PAGE_END);

		JSplitPane splitPaneParent = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,
				paneLeft,
				splitPaneChild);
		splitPaneParent.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.35));
		splitPaneParent.setOneTouchExpandable(true);

		add(splitPaneParent, BorderLayout.CENTER);
	}

}
