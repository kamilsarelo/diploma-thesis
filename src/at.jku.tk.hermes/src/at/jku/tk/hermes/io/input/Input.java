package at.jku.tk.hermes.io.input;

/* Imports ********************************************************************/

import at.jku.tk.hermes.core.MetaData;

/* Interface ******************************************************************/

public interface Input<T> {

	/* Methods ****************************************************************/

	public MetaData getMetaData();

	public void setMetaData(MetaData metaData);

	public T getInput(); 

	public void setInput(T input); 

}
