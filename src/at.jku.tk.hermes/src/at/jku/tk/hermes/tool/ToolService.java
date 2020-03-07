package at.jku.tk.hermes.tool;

/* Imports ********************************************************************/

import java.util.List;

import at.jku.tk.hermes.action.Action;
import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.protocol.Protocol;

/* Interface ******************************************************************/

public interface ToolService {

	/* Methods ****************************************************************/

	public MetaData getMetaData();

	public boolean supportsProtocol(Class<? extends Protocol> clazz);

	public List<Class<? extends Protocol>> getSupportedProtocols();

	public boolean supportsAction(Class<? extends Action> clazz);

	public List<Class<? extends Action>> getSupportedActions();

	public void executeAction(Class<? extends Protocol> clazz, Action action) throws ProtocolNotSupportedByToolException, ActionNotSupportedException, ActionExecutionFailedException;

}
