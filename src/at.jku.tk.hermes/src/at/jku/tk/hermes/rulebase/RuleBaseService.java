package at.jku.tk.hermes.rulebase;

/* Imports ********************************************************************/

import java.util.List;

import org.osgi.service.event.EventHandler;

import at.jku.tk.hermes.core.MetaData;

/* Interface ******************************************************************/

public interface RuleBaseService extends EventHandler {

	/* Methods ****************************************************************/

	public MetaData getMetaData();

	public boolean supportsEventTopic(String topic);

	public List<String> getSupportedEventTopics();	
	
}
