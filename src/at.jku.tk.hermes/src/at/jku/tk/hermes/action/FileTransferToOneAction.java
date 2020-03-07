package at.jku.tk.hermes.action;

/* Imports ********************************************************************/

import java.io.File;
import java.io.FileNotFoundException;

import at.jku.tk.hermes.contact.Contact;

/* Class **********************************************************************/

public final class FileTransferToOneAction extends AbstractToOneAction {

	/* Fields *****************************************************************/

	private File file;

	/* Constructors ***********************************************************/

	public FileTransferToOneAction(Contact contact, File file) throws FileNotFoundException {
		super(contact);
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
