package at.jku.tk.hermes.io.output;

/* Imports ********************************************************************/

import at.jku.tk.hermes.core.MetaData;

/* Interface ******************************************************************/

public class AbstractOutput<T> implements Output<T> {

	/* Fields *****************************************************************/

	protected MetaData metaData = new MetaData();
	protected T output;

	/* Constructors ***********************************************************/

	public AbstractOutput() {
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

	public T getOutput() {
		if (output == null) {
			throw new NullPointerException();
		}
		return output;
	}

	public void setOutput(T output) {
		if (output == null) {
			throw new NullPointerException();
		}
		this.output = output;
	}

}
