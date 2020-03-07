package at.jku.tk.hermes.sample.ui.swing.internal;

/* Imports ********************************************************************/

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import at.jku.tk.hermes.contact.ContactObserverService;
import at.jku.tk.hermes.contact.ContactService;
import at.jku.tk.hermes.core.DatabaseObserverService;
import at.jku.tk.hermes.core.ParameterService;
import at.jku.tk.hermes.io.IOService;
import at.jku.tk.hermes.tool.ToolService;

/* Class **********************************************************************/

public class Activator implements BundleActivator {

	/* Fields *****************************************************************/

	private static Activator sharedInstance;
	private BundleContext bundleContext;
	private ServiceTracker contactServiceTracker, parameterServiceTracker,
	toolServiceSkypeTracker, toolServiceEmailComposeTracker,
	toolServiceSmsComWekayTracker, iterfaceServiceTracker;

	/* Methods ****************************************************************/

	/* Implementation */

	public void start(BundleContext context) throws Exception {
		// shared instance
		sharedInstance = this;
		// bundle context
		bundleContext = context;
		// open service tracker
		contactServiceTracker = new ServiceTracker(
				context,
				ContactService.class.getName(),
				null);
		contactServiceTracker.open();
		// open service tracker
		parameterServiceTracker = new ServiceTracker(
				context,
				ParameterService.class.getName(),
				null);
		parameterServiceTracker.open();
		// open service tracker
		toolServiceEmailComposeTracker = new ServiceTracker(
				context,
				ToolService.class.getName(),
				new ServiceTrackerCustomizer() {
					public Object addingService(ServiceReference reference) {
						if (reference.getBundle().getSymbolicName().equals("at.jku.tk.hermes.tool.email.compose")) {
							return Activator.this.bundleContext.getService(reference);
						}
						return null;
					}
					public void modifiedService(ServiceReference reference, Object service) {}
					public void removedService(ServiceReference reference, Object service) {}

				});
		toolServiceEmailComposeTracker.open();
		// open service tracker
		toolServiceSkypeTracker = new ServiceTracker(
				context,
				ToolService.class.getName(),
				new ServiceTrackerCustomizer() {
					public Object addingService(ServiceReference reference) {
						if (reference.getBundle().getSymbolicName().equals("at.jku.tk.hermes.tool.skype")) {
							return Activator.this.bundleContext.getService(reference);
						}
						return null;
					}
					public void modifiedService(ServiceReference reference, Object service) {}
					public void removedService(ServiceReference reference, Object service) {}

				});
		toolServiceSkypeTracker.open();
		// open service tracker
		toolServiceSmsComWekayTracker = new ServiceTracker(
				context,
				ToolService.class.getName(),
				new ServiceTrackerCustomizer() {
					public Object addingService(ServiceReference reference) {
						if (reference.getBundle().getSymbolicName().equals("at.jku.tk.hermes.tool.sms.com.wekay")) {
							return Activator.this.bundleContext.getService(reference);
						}
						return null;
					}
					public void modifiedService(ServiceReference reference, Object service) {}
					public void removedService(ServiceReference reference, Object service) {}

				});
		toolServiceSmsComWekayTracker.open();
		// open service tracker
		iterfaceServiceTracker = new ServiceTracker(
				context,
				IOService.class.getName(),
				new ServiceTrackerCustomizer() {
					public Object addingService(ServiceReference reference) {
						if (reference.getBundle().getSymbolicName().equals("at.jku.tk.hermes.sample.io.swing")) {
							return Activator.this.bundleContext.getService(reference);
						}
						return null;
					}
					public void modifiedService(ServiceReference reference, Object service) {}
					public void removedService(ServiceReference reference, Object service) {}
					
				});
		iterfaceServiceTracker.open();

		// register service
		context.registerService(
				ContactObserverService.class.getName(),
				PaneObserver.getInstance(),
				null);
		// register service
		context.registerService(
				ContactObserverService.class.getName(),
				PaneContacts.getInstance(),
				null);
		// register service
		context.registerService(
				DatabaseObserverService.class.getName(),
				PaneObserver.getInstance(),
				null);
		// Swing
		Frame.getInstance().startup();
	}

	public void stop(BundleContext context) throws Exception {
		// Swing
		Frame.getInstance().dipose();
		// close service tracker
		iterfaceServiceTracker.close();
		iterfaceServiceTracker = null;
		// close service tracker
		toolServiceSmsComWekayTracker.close();
		toolServiceSmsComWekayTracker = null;
		// close service tracker
		toolServiceSkypeTracker.close();
		toolServiceSkypeTracker = null;
		// close service tracker
		toolServiceEmailComposeTracker.close();
		toolServiceEmailComposeTracker = null;
		// close service tracker
		parameterServiceTracker.close();
		parameterServiceTracker = null;
		// close service tracker
		contactServiceTracker.close();
		contactServiceTracker = null;
		// bundle context
		bundleContext = null;
		// shared instance
		sharedInstance = null;
	}

	/* Public */

	public static Activator getDefault() {
		return sharedInstance;
	}

	public ServiceTracker getContactServiceTracker() {
		return contactServiceTracker;
	}

	public ServiceTracker getParameterServiceTracker() {
		return parameterServiceTracker;
	}

	public ServiceTracker getToolServiceEmailComposeTracker() {
		return toolServiceEmailComposeTracker;
	}

	public ServiceTracker getToolServiceSkypeTracker() {
		return toolServiceSkypeTracker;
	}

	public ServiceTracker getToolServiceSmsComWekayTracker() {
		return toolServiceSmsComWekayTracker;
	}

	public ServiceTracker getIterfaceServiceTracker() {
		return iterfaceServiceTracker;
	}
	
}
