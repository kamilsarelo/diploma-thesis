package at.jku.tk.hermes.tool.skype.internal;

/* Imports ********************************************************************/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import at.jku.tk.hermes.tool.ToolService;

/* Class **********************************************************************/

public class Activator implements BundleActivator {

	/* Fields****************************************************************/

	private static Activator sharedInstance;
	private String bundleSymbolicName;
	private ServiceTracker eventAdminServiceTracker;

	/* Methods ****************************************************************/

	/* Implementation */

	public void start(BundleContext context) throws Exception {
		// shared instance
		sharedInstance = this;
		// bundle's symbolic name
		bundleSymbolicName = context.getBundle().getSymbolicName();
		// open service tracker
		eventAdminServiceTracker = new ServiceTracker(
				context,
				EventAdmin.class.getName(),
				null);
		eventAdminServiceTracker.open();
		// register service
		context.registerService(
				ToolService.class.getName(),
				new ToolServiceImpl(),
				null);
	}

	public void stop(BundleContext context) throws Exception {
		// close service tracker
		eventAdminServiceTracker.close();
		eventAdminServiceTracker = null;
		// bundle's symbolic name
		bundleSymbolicName = null;
		// shared instance
		sharedInstance = null;
	}

	/* Public */

	public static Activator getDefault() {
		return sharedInstance;
	}

	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}
	
	public ServiceTracker getEventAdminServiceTracker() {
		return eventAdminServiceTracker;
	}

}
