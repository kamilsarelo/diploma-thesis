package at.jku.tk.hermes.io;

/* Imports ********************************************************************/

import java.util.List;

import at.jku.tk.hermes.core.MetaData;
import at.jku.tk.hermes.io.input.Input;
import at.jku.tk.hermes.io.input.InputNotSupportedException;
import at.jku.tk.hermes.io.output.Output;
import at.jku.tk.hermes.io.output.OutputNotSupportedException;

/* Interface ******************************************************************/

public interface IOService {

	/* Methods ****************************************************************/

	public MetaData getMetaData();

	public boolean supportsInput(Class<? extends Input<?>> clazz);

	public List<Class<? extends Input<?>>> getSupportedInputs();

	public void executeInput(Input<?> input, IOCallback callback) throws InputNotSupportedException;

	public boolean supportsOutput(Class<? extends Output<?>> clazz);

	public List<Class<? extends Output<?>>> getSupportedOutputs();

	public void executeOutput(Output<?> output, IOCallback callback) throws OutputNotSupportedException;

}
