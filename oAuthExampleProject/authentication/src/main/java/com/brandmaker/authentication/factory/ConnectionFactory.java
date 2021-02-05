package com.brandmaker.authentication.factory;

import java.io.File;

import javax.ws.rs.client.Invocation.Builder;

import com.brandmaker.authentication.modules.Connections.Modules;

public interface ConnectionFactory {

	/**
	 * Get an instance of the connection factory
	 * 
	 * @return a singleton instance of the connection factory, fully initialized
	 */
	static ConnectionFactory getInstance(File credentials) {
		
		return ConnectionFactoryImpl.factory(credentials);
		
	}

	ConnectionFactory init(Modules module);
	
	Builder build();

	ConnectionFactory loadOAUthSettings(File credentialsFile);

	ConnectionFactory setMethod(String method);

	ConnectionFactory setRestPath(String path);

	ConnectionFactory setMediaType(String mimeType);
	

}