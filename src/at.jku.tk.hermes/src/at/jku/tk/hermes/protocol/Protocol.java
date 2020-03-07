package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import java.util.List;

import at.jku.tk.hermes.action.Action;

/* Interface ******************************************************************/

public interface Protocol {

	/* Methods ****************************************************************/

	public boolean supportsAction(Class<? extends Action> clazz);
	
	public List<Class<? extends Action>> getSupportedActions();

}
