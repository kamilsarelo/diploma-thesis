package at.jku.tk.hermes.protocol;

/* Imports ********************************************************************/

import java.util.ArrayList;
import java.util.List;

import at.jku.tk.hermes.action.Action;

/* Class **********************************************************************/

public abstract class AbstractProtocol implements Protocol {

	/* Fields *****************************************************************/

	protected final List<Class<? extends Action>> actions = new ArrayList<Class<? extends Action>>();

	/* Methods ****************************************************************/

	/* Implementation */

	public boolean supportsAction(Class<? extends Action> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (actions) {
			return actions.contains(clazz);
		}
	}

	public List<Class<? extends Action>> getSupportedActions() {
		synchronized (actions) {
			List<Class<? extends Action>> clone = new ArrayList<Class<? extends Action>>();
			for (Class<? extends Action> clazz : actions) {
				clone.add(clazz);
			}
			return clone;
		}
	}

	/* Protected */

	protected boolean addSupportedAction(Class<? extends Action> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (actions) {
			if (! actions.contains(clazz)) {
				return actions.add(clazz);
			}
		}
		return false;
	}

	protected boolean removeSupportedAction(Class<? extends Action> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (actions) {
			return actions.remove(clazz);
		}
	}

}
