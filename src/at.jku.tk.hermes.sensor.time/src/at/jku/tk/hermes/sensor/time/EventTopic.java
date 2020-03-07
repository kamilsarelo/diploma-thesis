package at.jku.tk.hermes.sensor.time;

/* Imports ********************************************************************/

import at.jku.tk.hermes.sensor.AbstractSensorEventTopic;

/*  Class *********************************************************************/

public class EventTopic extends AbstractSensorEventTopic {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final EventTopic INSTANCE = new EventTopic();
	}

	public static EventTopic getInstance() {
		return Holder.INSTANCE;
	}

	/*  Constructors **********************************************************/

	private EventTopic() {
		setEventTopic("at/jku/tk/hermes/sensor/time/event");
	}

}
