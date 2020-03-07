package at.jku.tk.hermes.core;

/* Class **********************************************************************/

public final class MetaData {

	/* Fields *****************************************************************/

	private String name;
	private String description;

	/* Methods ****************************************************************/

	/* Public */

	public String getName() {
		if (name == null) {
			throw new NullPointerException();
		}
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
	}

	public String getDescription() {
		if (description == null) {
			throw new NullPointerException();
		}
		return description;
	}

	public void setDescription(String description) {
		if (description == null) {
			throw new NullPointerException();
		}
		this.description = description;
	}

}
