package at.jku.tk.hermes.sample.io.swing.internal;

/* Imports ********************************************************************/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import at.jku.tk.hermes.io.IOService;

/* Class **********************************************************************/

public class Activator implements BundleActivator {

	/* Methods ****************************************************************/

	/* Implementation */

	public void start(BundleContext context) throws Exception {
		// register service
		context.registerService(
				IOService.class.getName(),
				IOServiceImpl.getInstance(),
				null);
	}

	public void stop(BundleContext context) throws Exception {}

}
