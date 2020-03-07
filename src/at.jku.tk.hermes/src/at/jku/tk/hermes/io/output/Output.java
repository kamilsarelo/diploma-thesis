package at.jku.tk.hermes.io.output;

/* Imports ********************************************************************/

import at.jku.tk.hermes.core.MetaData;

/* Interface ******************************************************************/

public interface Output<T> {

	/* Methods ****************************************************************/

	public MetaData getMetaData();

	public void setMetaData(MetaData metaData);

	public T getOutput(); 

	public void setOutput(T output); 

}
