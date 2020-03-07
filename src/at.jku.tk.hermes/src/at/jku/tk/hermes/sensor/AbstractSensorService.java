package at.jku.tk.hermes.sensor;

/* Imports ********************************************************************/

import java.util.ArrayList;
import java.util.List;

/* Class **********************************************************************/

public abstract class AbstractSensorService implements SensorService {

	/* Fields *****************************************************************/

	protected final List<String> eventTopics = new ArrayList<String>();

	/* Methods ****************************************************************/

	/* Implementation */

	public boolean supportsEventTopic(String topic) {
		if (topic == null) {
			throw new NullPointerException();
		}
		synchronized (eventTopics) {
			return eventTopics.contains(topic);
		}
	}

	public List<String> getSupportedEventTopics() {
		synchronized (eventTopics) {
			List<String> clone = new ArrayList<String>();
			for (String string : eventTopics) {
				clone.add(string);
			}
			return clone;
		}
	}

	/* Protected */

	protected boolean addSupportedEventTopic(String topic) {
		if (topic == null) {
			throw new NullPointerException();
		}
		synchronized (eventTopics) {
			if (! eventTopics.contains(topic)) {
				return eventTopics.add(topic);
			}
		}
		return false;
	}

	protected boolean removeSupportedEventTopic(String topic) {
		if (topic == null) {
			throw new NullPointerException();
		}
		synchronized (eventTopics) {
			return eventTopics.remove(topic);
		}
	}

}
