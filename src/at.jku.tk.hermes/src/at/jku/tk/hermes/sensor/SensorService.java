package at.jku.tk.hermes.sensor;

/* Imports ********************************************************************/

import java.util.List;

import at.jku.tk.hermes.core.MetaData;

/* Interface ******************************************************************/

public interface SensorService {

	/* Methods ****************************************************************/

	public MetaData getMetaData();

	public boolean supportsEventTopic(String topic);

	public List<String> getSupportedEventTopics();

	public void executePublishing();

}
