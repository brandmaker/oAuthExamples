package com.brandmaker.authentication.factory;

import java.io.File;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;

import com.brandmaker.authentication.modules.Connections;
import com.brandmaker.authentication.modules.Connections.Modules;
import com.brandmaker.authentication.utils.OAuth2Credentials;

import sun.awt.shell.ShellFolder.Invoker;

/**
 * <p>A wrapper class for JAX-RS
 * <p>This class wraps all necessary settings to issue API calls to a BrandMaker module such as Media Pool or MArketing Planner.
 * <p>All volatile parameters such as
 * <ul>
 * 	<li>Server address
 * 	<li>Client id
 * 	<li>Client secret
 * 	<li>refresh and access tokens and their expiration date (!)
 * </ul>
 * <p>are stored in a json file which gets automatically updated on a token refresh.
 * 
 * @author axel.amthor
 *
 */
public interface ConnectionFactory {

	/**
	 * Get an instance of the connection factory
	 * 
	 * @return a singleton instance of the connection factory, fully initialized
	 */
	static ConnectionFactory getInstance(File credentials) {
		
		return ConnectionFactoryImpl.factory(credentials);
		
	}

	/**
	 * Initialize the JAX-RS wrapper factory.
	 * 
	 * @param module the targeted module. This is mainly to retrieve the base path of the REST API
	 * @see {@link Connections}
	 * 
	 * @return
	 */
	ConnectionFactory init(Modules module);
	
	/**
	 * Build the request based on the given settings in this class.
	 * 
	 * On invoking any request based on this builder, the token authentication will be handled internally 
	 * and automatically, as well as necessary token refresh and token persistance.
	 * 
	 * @see {@link Builder}
	 * 
	 * @return fully initialized request builder
	 */
	Builder build();

	/**
	 * Load the credntials and other settings from the given JSON file. The file must correspond to OAuth2Credentials class.
	 * 
	 * @see {@link OAuth2Credentials}
	 * @param credentialsFile
	 * @return
	 */
	ConnectionFactory loadOAUthSettings(File credentialsFile);

	/** 
	 * Set the request method
	 * @param method
	 * @return
	 */
	ConnectionFactory setMethod(String method);

	/**
	 * Sets the rest end-point. this is concatenated to the modules bas URL.
	 * 
	 * @See {@link Connections}
	 * @param path
	 * @return
	 */
	ConnectionFactory setRestPath(String path);

	/**
	 * Sets the "ACCEPT" mime type for this request
	 * @param mimeType
	 * @return
	 */
	ConnectionFactory setMediaType(String mimeType);

	/**
	 * Create an invoker based on the given settings in this class.
	 * @see {@link Invocation}
	 * 
	 * @return Invoker
	 */
	Invocation getInvoker();
	

}