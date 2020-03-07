package at.jku.tk.hermes.tool;

/* Imports ********************************************************************/

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import at.jku.tk.hermes.action.Action;
import at.jku.tk.hermes.protocol.Protocol;

/* Class **********************************************************************/

public abstract class AbstractToolService implements ToolService {

	/* Fields *****************************************************************/

	protected final List<Class<? extends Protocol>> protocols = new ArrayList<Class<? extends Protocol>>();

	/* Methods ****************************************************************/

	/* Implementation */

	public boolean supportsProtocol(Class<? extends Protocol> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (protocols) {
			return protocols.contains(clazz);
		}
	}
	
	public List<Class<? extends Protocol>> getSupportedProtocols() {
		synchronized (protocols) {
			List<Class<? extends Protocol>> clone = new ArrayList<Class<? extends Protocol>>();
			for (Class<? extends Protocol> clazz : protocols) {
				clone.add(clazz);
			}
			return clone;
		}
	}
	
	public boolean supportsAction(Class<? extends Action> clazz) {
		return getSupportedActions().contains(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public List<Class<? extends Action>> getSupportedActions() {
		synchronized (protocols) {
			List<Class<? extends Action>> actions = new ArrayList<Class<? extends Action>>();
			for (Class<? extends Protocol> clazz : protocols) {
				try {
					// Use Java 5 Features To Simplify Reflection Code:
					// http://www.javalobby.org/java/forums/t62171.html
					Method method = clazz.getMethod("getSupportedActions");
					for (Class<? extends Action> action : (List<Class<? extends Action>>) method.invoke(clazz)) {
						if (! actions.contains(action)) {
							actions.add(action);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return actions;
		}
	}

	/* Protected */

	protected boolean addSupportedProtocol(Class<? extends Protocol> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (protocols) {
			if (! protocols.contains(clazz)) {
				return protocols.add(clazz);
			}
		}
		return false;
	}

	protected boolean removeSupportedProtocol(Class<? extends Protocol> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (protocols) {
			return protocols.remove(clazz);
		}
	}

}
