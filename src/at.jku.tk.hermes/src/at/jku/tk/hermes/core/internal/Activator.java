package at.jku.tk.hermes.core.internal;

/* Imports ********************************************************************/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import at.jku.tk.hermes.contact.ContactObserverService;
import at.jku.tk.hermes.contact.ContactService;
import at.jku.tk.hermes.core.DatabaseObserverService;
import at.jku.tk.hermes.core.DatabaseService;
import at.jku.tk.hermes.core.ParameterService;
import at.jku.tk.hermes.core.TimestampService;
import at.jku.tk.hermes.core.internal.replication.ReplicationDaemon;

/* Class **********************************************************************/

public class Activator implements BundleActivator {

	/* Fields *****************************************************************/

	private static Activator sharedInstance;
	private ServiceTracker databaseObserverServiceTracker;
	private ServiceTracker contactObserverServiceTracker;

	/* Methods ****************************************************************/

	/* Implementation */

	public void start(BundleContext context) throws Exception {
		// shared instance
		sharedInstance = this;
		// open service tracker
		databaseObserverServiceTracker = new ServiceTracker(
				context,
				DatabaseObserverService.class.getName(),
				null);
		databaseObserverServiceTracker.open();
		// open service tracker
		contactObserverServiceTracker = new ServiceTracker(
				context,
				ContactObserverService.class.getName(),
				null);
		contactObserverServiceTracker.open();
		System.out.println("at.jku.tk.hermes: service tracker opened");
		// connect to database
		DatabaseServiceImpl.getInstance().connect();
		System.out.println("at.jku.tk.hermes: database connected");
		// start replication daemon
		ReplicationDaemon.start();
		System.out.println("at.jku.tk.hermes: replication daemon started");
		// register service
		context.registerService(
				DatabaseService.class.getName(),
				DatabaseServiceImpl.getInstance(),
				null);
		// register service
		context.registerService(
				ContactService.class.getName(),
				ContactServiceImpl.getInstance(),
				null);
		// register service
		context.registerService(
				TimestampService.class.getName(),
				TimestampServiceImpl.getInstance(),
				null);
		// register service
		context.registerService(
				ParameterService.class.getName(),
				ParameterServiceImpl.getInstance(),
				null);
		System.out.println("at.jku.tk.hermes: services registered");
	}

	public void stop(BundleContext context) throws Exception {
		// disconnect from database
		DatabaseServiceImpl.getInstance().disconnect();
		// close service tracker
		databaseObserverServiceTracker.close();
		databaseObserverServiceTracker = null;
		// close service tracker
		contactObserverServiceTracker.close();
		contactObserverServiceTracker = null;
		// shared instance
		sharedInstance = null;
	}

	/* Public */

	public static Activator getDefault() {
		return sharedInstance;
	}

	public ServiceTracker getDatabaseObserverServiceTracker() {
		return databaseObserverServiceTracker;
	}

	public ServiceTracker getContactObserverServiceTracker() {
		return contactObserverServiceTracker;
	}

}
