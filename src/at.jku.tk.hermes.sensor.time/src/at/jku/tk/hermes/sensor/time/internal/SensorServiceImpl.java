package at.jku.tk.hermes.sensor.time.internal;

/* Imports ********************************************************************/

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import at.jku.tk.hermes.core.EventProperties;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.sensor.AbstractSensorService;
import at.jku.tk.hermes.sensor.time.EventTopic;

/* Class **********************************************************************/

public class SensorServiceImpl extends AbstractSensorService {

	/* Constructors ***********************************************************/

	public SensorServiceImpl() {
		addSupportedEventTopic(EventTopic.getInstance().getEventTopic());
		try {
			final EventAdmin eventAdmin = (EventAdmin) Activator.getDefault().getEventAdminServiceTracker().getService();
			final EventProperties properties = new EventProperties(Activator.getDefault().getBundleSymbolicName());
			properties.setMessage("current timestamp");
			new Timer(true).scheduleAtFixedRate(
					new TimerTask() {
						@Override
						public void run() {
							try {
								properties.setTimestamp(System.currentTimeMillis());
								eventAdmin.sendEvent(new Event(EventTopic.getInstance().getEventTopic(), properties.toProperties()));
							} catch (Exception e) {
							}
						}
					},
					0,
					1000);
		} catch (Exception e) {
		}
	}

	/* Implementation */

	public MetaData getMetaData() {
		MetaData data = new MetaData();
		data.setName(this.getClass().getSimpleName());
		data.setDescription(
				"Broadcasts current timestamp with the topic: " +
				EventTopic.getInstance().getEventTopic() + "."
		);
		return data;
	}

	public void executePublishing() {
		// ignore, because this service emits the current timestamp every second 
	}

}
