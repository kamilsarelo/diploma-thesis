package at.jku.tk.hermes.sensor.google.calendar.internal;

/* Imports ********************************************************************/

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import at.jku.tk.hermes.core.EventProperties;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.sensor.AbstractSensorService;
import at.jku.tk.hermes.sensor.google.calendar.EventTopic;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;

/* Class **********************************************************************/

public class SensorServiceImpl extends AbstractSensorService {

	/* Fields *****************************************************************/

	private final List<CalendarEventEntry> calendarEventEntries = new ArrayList<CalendarEventEntry>();

	/* Constructors ***********************************************************/

	public SensorServiceImpl() {
		addSupportedEventTopic(EventTopic.getInstance().getEventTopic());
		try {
			final CalendarService calendarService = new CalendarService("atJkuTk-hermesSensor-1");
			calendarService.setUserCredentials(
					System.getProperty("at.jku.tk.hermes.sensor.google.calendar.username"),
					System.getProperty("at.jku.tk.hermes.sensor.google.calendar.password"));
			final CalendarQuery calendarQuery = new CalendarQuery(new URL("http://www.google.com/calendar/feeds/" + System.getProperty("at.jku.tk.hermes.sensor.google.calendar.username") + "/private/full"));
			new Timer(true).scheduleAtFixedRate(
					new TimerTask() {
						@Override
						public void run() {
							synchronized (calendarEventEntries) {
								calendarEventEntries.clear();
								try {
									calendarQuery.setMinimumStartTime(new DateTime(System.currentTimeMillis() - 24 * 60 * 60 * 1000)); // started at the earliest 24 hours ago 
									calendarQuery.setMaximumStartTime(new DateTime(System.currentTimeMillis())); // started at the latest now
									CalendarEventFeed calendarEventFeed = calendarService.query(calendarQuery, CalendarEventFeed.class);
									for (CalendarEventEntry calendarEventEntry : calendarEventFeed.getEntries()) {
										for (When when : calendarEventEntry.getTimes()) {
											if (when.getEndTime().compareTo(DateTime.now()) >= 0) { // ending at the earliest now or in future
												calendarEventEntries.add(calendarEventEntry);
												break;
											}
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					},
					0,
					Integer.parseInt(System.getProperty("at.jku.tk.hermes.sensor.google.calendar.intervall", "60000")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Implementation */

	public MetaData getMetaData() {
		MetaData data = new MetaData();
		data.setName(this.getClass().getSimpleName());
		data.setDescription(
				"Broadcasts all currently active Google Calendar events with " +
				"the topic: " + EventTopic.getInstance().getEventTopic() + ". " +
				"Expects certain values to be available as system properties: " +
				"at.jku.tk.hermes.sensor.google.calendar.username - username " +
				"for the Google Calendar service, " +
				"at.jku.tk.hermes.sensor.google.calendar.password - password " +
				"for Google Calendar service and " +
				"at.jku.tk.hermes.sensor.google.calendar.intervall - polling " +
				"interval in milliseconds."
		);
		return data;
	}

	public void executePublishing() {
		try {
			List<CalendarEventEntry> clone = new ArrayList<CalendarEventEntry>();
			synchronized (calendarEventEntries) {
				for (CalendarEventEntry entry : calendarEventEntries) {
					clone.add(entry);
				}
			}
			EventProperties eventProperties = new EventProperties(Activator.getDefault().getBundleSymbolicName());
			eventProperties.setMessage("currently active Google Calendar event(s)");
			eventProperties.setEvent(clone);
			EventAdmin eventAdmin = (EventAdmin) Activator.getDefault().getEventAdminServiceTracker().getService();
			eventAdmin.sendEvent(new Event(
					EventTopic.getInstance().getEventTopic(),
					eventProperties.toProperties()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
