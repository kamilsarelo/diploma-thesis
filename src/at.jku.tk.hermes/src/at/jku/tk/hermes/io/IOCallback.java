package at.jku.tk.hermes.io;

/* Imports ********************************************************************/

import at.jku.tk.hermes.io.input.Input;

/* Interface ******************************************************************/

public interface IOCallback {

	/* Methods ****************************************************************/

	public void onInput(Input<?> input);

	public void onInputExecutionFailed();

	public void onOutput();

	public void onOutputExecutionFailed();

}
