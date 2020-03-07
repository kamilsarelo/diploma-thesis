/*
 * (non-Javadoc)
 * OSGi 4.1 - Event Admin Service - general property names for events [Table 113.23]
 * 
 * BUNDLE_SIGNER         String             A signer DN
 * BUNDLE_SYMBOLICNAME   String             A bundle’s symbolic name
 * EVENT                 Object             The actual event object. Used when rebroadcasting an event that was sent via some other event mechanism
 * EXCEPTION             Throwable          An exception or error
 * EXECPTION_CLASS       String             Must be equal to the name of the Exception class.
 * EXCEPTION_MESSAGE     String             Must be equal to exception.getMessage()
 * MESSAGE               String             A human-readable message that is usually not localized.
 * SERVICE               ServiceReference   A service
 * SERVICE_ID            Long               A service’s id
 * SERVICE_OBJECTCLASS   String[]           A service's objectClass
 * SERVICE_PID           String             A service’s persistent identity
 * TIMESTAMP             Long               The time when the event occurred, as reported by System.currentTimeMillis()
 * 
 * see also: http://www2.osgi.org/javadoc/r4/org/osgi/service/event/Event.html
 * see also: http://www2.osgi.org/javadoc/r4/org/osgi/service/event/EventConstants.html
 */

package at.jku.tk.hermes.core;

/* Imports ********************************************************************/

import java.util.Map;
import java.util.Properties;

import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventConstants;

/* Class **********************************************************************/

public class EventProperties {

	/* Fields *****************************************************************/

	private final Properties properties = new Properties();

	/* Constructors ***********************************************************/

	public EventProperties(String bundleSymbolicName) {
		if (bundleSymbolicName == null) {
			throw new NullPointerException();
		}
		setBundleSymbolicname(bundleSymbolicName);
		setTimestamp(System.currentTimeMillis());
	}

	/* Methods ****************************************************************/

	/* Public */

	public Map<Object, Object> toProperties() {
		return properties;
	}

	public void setBundleSigner(String bundleSigner) {
		if (bundleSigner == null) {
			properties.remove(EventConstants.BUNDLE_SIGNER);
		} else {
			properties.put(EventConstants.BUNDLE_SIGNER, bundleSigner);
		}
	}

	public void setBundleSymbolicname(String bundleSymbolicname) {
		if (bundleSymbolicname == null) {
			properties.remove(EventConstants.BUNDLE_SYMBOLICNAME);
		} else {
			properties.put(EventConstants.BUNDLE_SYMBOLICNAME, bundleSymbolicname);
		}
	}

	public void setEvent(Object event) {
		if (event == null) {
			properties.remove(EventConstants.EVENT);
		} else {
			properties.put(EventConstants.EVENT, event);
		}
	}

	public void setException(Throwable exception) {
		if (exception == null) {
			properties.remove(EventConstants.EXCEPTION);
		} else {
			properties.put(EventConstants.EXCEPTION, exception);
		}
	}

	public void setExceptionClass(String exceptionClass) {
		if (exceptionClass == null) {
			properties.remove(EventConstants.EXCEPTION_CLASS);
		} else {
			properties.put(EventConstants.EXCEPTION_CLASS, exceptionClass);
		}
	}

	public void setExceptionMessage(String exceptionMessage) {
		if (exceptionMessage == null) {
			properties.remove(EventConstants.EXCEPTION_MESSAGE);
		} else {
			properties.put(EventConstants.EXCEPTION_MESSAGE, exceptionMessage);
		}
	}

	public void setMessage(String message) {
		if (message == null) {
			properties.remove(EventConstants.MESSAGE);
		} else {
			properties.put(EventConstants.MESSAGE, message);
		}
	}

	public void setService(ServiceReference service) {
		if (service == null) {
		} else {
			properties.put(EventConstants.SERVICE, service);
		}
	}

	public void setServiceId(Long serviceId) {
		if (serviceId == null) {
			properties.remove(EventConstants.SERVICE_ID);
		} else {
			properties.put(EventConstants.SERVICE_ID, serviceId);
		}
	}

	public void setServiceObjectclass(String[] serviceObjectClass) {
		if (serviceObjectClass == null) {
			properties.remove(EventConstants.SERVICE_OBJECTCLASS);
		} else {
			properties.put(EventConstants.SERVICE_OBJECTCLASS, serviceObjectClass);
		}
	}

	public void setServicePid(String[] servicePid) {
		if (servicePid == null) {
			properties.remove(EventConstants.SERVICE_PID);
		} else {
			properties.put(EventConstants.SERVICE_PID, servicePid);
		}
	}

	public void setTimestamp(Long timestamp) {
		if (timestamp == null) {
			properties.remove(EventConstants.TIMESTAMP);
		} else {
			properties.put(EventConstants.TIMESTAMP, timestamp);
		}
	}

}
