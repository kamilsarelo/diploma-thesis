package at.jku.tk.hermes.sample.rulebase.internal;

/* Imports ********************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

import at.jku.tk.hermes.action.Action;
import at.jku.tk.hermes.action.AudioConferenceStartAction;
import at.jku.tk.hermes.action.MessageSendToOneAction;
import at.jku.tk.hermes.contact.Contact;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.protocol.SkypeProtocol;
import at.jku.tk.hermes.protocol.SkypePstnProtocol;
import at.jku.tk.hermes.rulebase.AbstractRuleBaseService;
import at.jku.tk.hermes.sensor.SensorService;
import at.jku.tk.hermes.sensor.google.calendar.EventTopic;
import at.jku.tk.hermes.tool.ToolService;

import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;

/* Class **********************************************************************/

public class RuleBaseServiceImpl extends AbstractRuleBaseService {

	/* Fields *****************************************************************/

	private LinkedBlockingQueue<CalendarEventEntry> calendarEventEntryQueue = new LinkedBlockingQueue<CalendarEventEntry>(); // http://www.exampledepot.com/egs/java.lang/FixedWorkQueue.html
	private List<String> processedCalendarEventEntryIds = new ArrayList<String>(); // only send SMS for new entries, remember the old

	/* Constructors ***********************************************************/

	public RuleBaseServiceImpl() {
		addSupportedEventTopic(EventTopic.getInstance().getEventTopic());
		// sensor
		new Timer(true).scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					((SensorService) Activator.getDefault().getSensorServiceGoogleCalendarTracker().getService()).executePublishing();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		},
		1000,
		1000);
		// tool
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						CalendarEventEntry calendarEventEntry = calendarEventEntryQueue.take();
						if (calendarEventEntry == null) {
							break;
						}
						if (! processedCalendarEventEntryIds.contains(calendarEventEntry.getId())) {
							Action action;
							// contact
							Contact contactOne = new Contact("Hermes One");
							contactOne.putIdentity(SkypeProtocol.class, "at.jku.tk.hermes.contact.1");
							Contact contactTwo = new Contact("Hermes Two");
							contactTwo.putIdentity(SkypeProtocol.class, "at.jku.tk.hermes.contact.2");
							Contact contactThree = new Contact("Kamil");
							contactThree.putIdentity(SkypeProtocol.class, "+436802076302");
							Contact contactFour = new Contact("Fabian");
							contactFour.putIdentity(SkypeProtocol.class, "+4369917711876");
							// action
							if (calendarEventEntry.getTitle().getPlainText().toLowerCase().contains("conference")) {
								// start conference call in Skype
								action = new AudioConferenceStartAction(contactOne, contactTwo, contactThree, contactFour);
							} else {
								// send instant message in Skype
								When when = calendarEventEntry.getTimes().get(0);
								String message =
									"active Google Calendar event:" +
									"title: " + calendarEventEntry.getTitle().getPlainText() + "\n" +
									"from: " + when.getStartTime().toUiString() + "\n" +
									"to: " + when.getEndTime().toUiString();
								action = new MessageSendToOneAction(contactOne, message);
							}
							// execute action
							((ToolService) Activator.getDefault().getToolServiceSkypeTracker().getService()).executeAction(SkypeProtocol.class, action);
							// remember calendar event
							processedCalendarEventEntryIds.add(calendarEventEntry.getId());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public MetaData getMetaData() {
		MetaData data = new MetaData();
		data.setName(this.getClass().getSimpleName());
		data.setDescription(
				"Listens to events from the Google Calendar sensor. Send a SMS " +
				"via the gateway whenever there is an active calendar event. " +
				"Starts a conference call via Skype whenever there is an " +
				"active calendar event whereas the label must conain the string " +
				"\"conference\"."
		);
		return data;
	}

	@SuppressWarnings("unchecked")
	public void handleEvent(Event event) {
		try {
			for (CalendarEventEntry calendarEventEntry : (List<CalendarEventEntry>) event.getProperty(EventConstants.EVENT)) {
				calendarEventEntryQueue.put(calendarEventEntry);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
