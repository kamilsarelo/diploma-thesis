package at.jku.tk.hermes.sample.ui.swing.internal;

/* Imports ********************************************************************/

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import at.jku.tk.hermes.core.ParameterService;

/* Class **********************************************************************/

public class Frame {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final Frame INSTANCE = new Frame();
	}

	public static Frame getInstance() {
		return Holder.INSTANCE;
	}

	/* Fields *****************************************************************/

	private JFrame frame;

	/* Constructors ***********************************************************/

	private Frame() {}

	/* Methods ****************************************************************/

	/* Public */

	public void startup() {
		if (frame == null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						ParameterService service = (ParameterService) Activator.getDefault().getParameterServiceTracker().getService();
						frame = new JFrame(service.getXmppBareUsername() + "@" + service.getXmppServer());
					} catch (Exception e) {
						frame = new JFrame("at.jku.tk.hermes.ui.swing");
					}
					frame.setMinimumSize(new Dimension(400, 400)); // doesn't work on OSX
					frame.setSize(800,600);
					//frame.setIconImage(new ImageIcon(getClass().getResource(Constants.ICON_SWING_FRAME)).getImage());
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // JFrame.DISPOSE_ON_CLOSE
					frame.addWindowListener(new WindowAdapter() { // http://mindprod.com/jgloss/close.html
					});
					frame.setContentPane(Pane.getInstance());
					frame.setLocationRelativeTo(null); // centers frame on screen
					frame.setVisible(true);
				}
			});
		}
	}

	public void dipose() {
		if (frame != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					frame.setVisible(false);
					frame.dispose();
					frame = null;
				}
			});
		}
	}

}
