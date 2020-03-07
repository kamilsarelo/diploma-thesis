package at.jku.tk.hermes.sensor;

/* Class **********************************************************************/

public abstract class AbstractSensorEventTopic {

	/* Fields *****************************************************************/

	private String eventTopic;

	/* Methods ****************************************************************/

	/* Public */

	public String getEventTopic() {
		if (eventTopic == null) {
			throw new NullPointerException();
		}
		return eventTopic;
	}
	
	/* Protected */

	public void setEventTopic(String eventTopic) {
		if (eventTopic == null) {
			throw new NullPointerException();
		}
		this.eventTopic = eventTopic;
	}

}
