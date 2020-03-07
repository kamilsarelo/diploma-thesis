package at.jku.tk.hermes.io;

/* Imports ********************************************************************/

import java.util.ArrayList;
import java.util.List;

import at.jku.tk.hermes.io.input.Input;
import at.jku.tk.hermes.io.output.Output;

/* Class **********************************************************************/

public abstract class AbstractIOService implements IOService {

	/* Fields *****************************************************************/

	protected final List<Class<? extends Input<?>>> inputs = new ArrayList<Class<? extends Input<?>>>();
	protected final List<Class<? extends Output<?>>> outputs = new ArrayList<Class<? extends Output<?>>>();

	/* Methods ****************************************************************/

	/* Implementation */

	public boolean supportsInput(Class<? extends Input<?>> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (inputs) {
			return inputs.contains(clazz);
		}
	}

	public List<Class<? extends Input<?>>> getSupportedInputs() {
		synchronized (inputs) {
			List<Class<? extends Input<?>>> clone = new ArrayList<Class<? extends Input<?>>>();
			for (Class<? extends Input<?>> clazz : inputs) {
				clone.add(clazz);
			}
			return clone;
		}
	}

	public boolean supportsOutput(Class<? extends Output<?>> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (outputs) {
			return outputs.contains(clazz);
		}
	}

	public List<Class<? extends Output<?>>> getSupportedOutputs() {
		synchronized (outputs) {
			List<Class<? extends Output<?>>> clone = new ArrayList<Class<? extends Output<?>>>();
			for (Class<? extends Output<?>> clazz : outputs) {
				clone.add(clazz);
			}
			return clone;
		}
	}

	/* Protected */

	protected boolean addSupportedInput(Class<? extends Input<?>> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (inputs) {
			if (! inputs.contains(clazz)) {
				return inputs.add(clazz);
			}
		}
		return false;
	}

	protected boolean removeSupportedInput(Class<? extends Input<?>> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (inputs) {
			return inputs.remove(clazz);
		}
	}

	protected boolean addSupportedOutput(Class<? extends Output<?>> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (outputs) {
			if (! outputs.contains(clazz)) {
				return outputs.add(clazz);
			}
		}
		return false;
	}

	protected boolean removeSupportedOutput(Class<? extends Output<?>> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		synchronized (outputs) {
			return outputs.remove(clazz);
		}
	}

}
