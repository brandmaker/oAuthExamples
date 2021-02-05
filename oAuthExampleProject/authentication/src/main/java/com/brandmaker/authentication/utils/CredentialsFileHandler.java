package com.brandmaker.authentication.utils;

import java.io.File;

public interface CredentialsFileHandler {

	public static CredentialsFileHandler getInstance () {
		return new CredentialsFileHandlerImpl();
	}
	
	/**
	 * Save the credentials to a file in JSON format
	 * 
	 * @param credentials
	 */
	void storeCredentials(OAuth2Credentials credentials, File crentialsFile);

	OAuth2Credentials getCredentials(File crentialsFile);

	OAuth2Credentials updateCredentials(OAuth2Credentials cred, File crentialsFile);

}