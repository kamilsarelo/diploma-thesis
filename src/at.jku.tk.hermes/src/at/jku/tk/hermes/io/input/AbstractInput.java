package at.jku.tk.hermes.io.input;

/* Imports ********************************************************************/

import at.jku.tk.hermes.core.MetaData;

/* Interface ******************************************************************/

public class AbstractInput<T> implements Input<T> {

	/* Fields *****************************************************************/

	protected MetaData metaData = new MetaData();
	protected T input;

	/* Constructors ***********************************************************/

	public AbstractInput() {
		metaData.setName(this.getClass().getSimpleName());
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		if (metaData == null) {
			throw new NullPointerException();
		}
		this.metaData = metaData;
	}

	public T getInput() {
		return input;
	}

	public void setInput(T input) {
		if (input == null) {
			throw new NullPointerException();
		}
		this.input = input;
	}

}
