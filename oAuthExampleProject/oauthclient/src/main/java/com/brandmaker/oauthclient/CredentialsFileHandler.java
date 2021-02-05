package com.brandmaker.oauthclient;


public interface CredentialsFileHandler {

	/**
	 * Save the credentials to a file in JSON format
	 * 
	 * @param credentials
	 */
	void storeCredentials(OAuth2Credentials credentials);

	OAuth2Credentials getCredentials();

	OAuth2Credentials updateCredentials(OAuth2Credentials cred);

}