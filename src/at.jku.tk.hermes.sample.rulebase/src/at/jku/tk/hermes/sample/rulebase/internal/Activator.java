package at.jku.tk.hermes.sample.rulebase.internal;

/* Imports ********************************************************************/

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import at.jku.tk.hermes.sensor.SensorService;
import at.jku.tk.hermes.sensor.google.calendar.EventTopic;
import at.jku.tk.hermes.tool.ToolService;

/* Class **********************************************************************/

public class Activator implements BundleActivator {

	/* Fields****************************************************************/

	private static Activator sharedInstance;
	private BundleContext bundleContext;
	private ServiceTracker sensorServiceGoogleCalendarTracker, toolServiceSkypeTracker;

	/* Methods ****************************************************************/

	/* Implementation */

	public void start(BundleContext context) throws Exception {
		// shared instance
		sharedInstance = this;
		// bundle context
		bundleContext = context;
		// open service tracker
		sensorServiceGoogleCalendarTracker = new ServiceTracker(
				context,
				SensorService.class.getName(),
				new ServiceTrackerCustomizer() {
					public Object addingService(ServiceReference reference) {
						if (reference.getBundle().getSymbolicName().equals("at.jku.tk.hermes.sensor.google.calendar")) {
							return Activator.this.bundleContext.getService(reference);
						}
						return null;
					}
					public void modifiedService(ServiceReference reference, Object service) {}
					public void removedService(ServiceReference reference, Object service) {}

				});
		sensorServiceGoogleCalendarTracker.open();
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
		// register service
		Properties properties = new Properties();
		properties.put(EventConstants.EVENT_TOPIC, new String[] { EventTopic.getInstance().getEventTopic() });
		context.registerService(
				EventHandler.class.getName(),
				new RuleBaseServiceImpl(),
				properties);
	}

	public void stop(BundleContext context) throws Exception {
		// close service tracker
		toolServiceSkypeTracker.close();
		toolServiceSkypeTracker = null;
		// close service tracker
		sensorServiceGoogleCalendarTracker.close();
		sensorServiceGoogleCalendarTracker = null;
		// bundle context
		bundleContext = null;
		// shared instance
		sharedInstance = null;
	}

	/* Public */

	public static Activator getDefault() {
		return sharedInstance;
	}

	public ServiceTracker getSensorServiceGoogleCalendarTracker() {
		return sensorServiceGoogleCalendarTracker;
	}

	public ServiceTracker getToolServiceSkypeTracker() {
		return toolServiceSkypeTracker;
	}

}
