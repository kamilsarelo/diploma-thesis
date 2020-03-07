package at.jku.tk.hermes.core;

/* Imports ********************************************************************/

import java.io.File;

/* Interface ******************************************************************/

public interface ParameterService {

	/* Methods ****************************************************************/

	public File getStorage() throws ParameterException;

	public String getNtpTimeServer() throws ParameterException;

	public String getXmppServer() throws ParameterException;

	public String getXmppUsername() throws ParameterException;

	public String getXmppBareUsername() throws ParameterException;

	public String getXmppPassword() throws ParameterException;

	public String[] getXmppTribeServers() throws ParameterException;

	public String getXmppTribeName() throws ParameterException;

}
