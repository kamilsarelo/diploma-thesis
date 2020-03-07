package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import java.io.File;
import java.io.FileNotFoundException;

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public final class FileTransferToManyAction extends AbstractToManyAction {

	/* Fields *****************************************************************/

	private File file;

	/* Constructors ***********************************************************/

	public FileTransferToManyAction(Contact contact, File file, Contact... contacts) throws FileNotFoundException {
		super(contact, contacts);
		setFile(file);
	}

	/* Methods ****************************************************************/

	/* Public */

	public File getFile() throws FileNotFoundException {
		if (file == null) {
			throw new NullPointerException();
		}
		if (! file.exists()) {
			throw new FileNotFoundException();
		}
		return file;
	}

	public void setFile(File file) throws FileNotFoundException {
		if (file == null) {
			throw new NullPointerException();
		}
		if (! file.exists()) {
			throw new FileNotFoundException();
		}
		this.file = file;
	}

}
