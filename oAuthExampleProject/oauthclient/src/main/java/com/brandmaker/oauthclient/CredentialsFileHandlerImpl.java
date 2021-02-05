package com.brandmaker.oauthclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Service
public class CredentialsFileHandlerImpl implements CredentialsFileHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsFileHandlerImpl.class);
	
	/** directory where to store the credentials */
	@Value("${spring.application.system.basepath}")
	private String basepath;

	private static final String filename = "credentials.json";
	
	
	@Override
	public void storeCredentials(OAuth2Credentials credentials) {
		
		File path = new File(basepath);
		path.mkdirs();
		
		FileOutputStream outputStream =  null;
		
		File credentialsFile = new File(path, filename);
		try {
			outputStream = new FileOutputStream(credentialsFile);
			
		    byte[] bytes = credentials.toString().getBytes();
		    outputStream.write(bytes);
		}
		catch ( Exception e ) {
			LOGGER.error("Error on writing credentials data", e);
		}
		finally {
			
			try {
				if ( outputStream != null )
					outputStream.close();
			}
			catch ( Exception e ) {
				LOGGER.error("Error on closing streams", e);
			}
			
		}
	}

	@Override
	public OAuth2Credentials updateCredentials(OAuth2Credentials cred) {
		
		File path = new File(basepath);
		File credentialsFile = new File(path, filename);
		
		if ( !credentialsFile.canRead() ) {
			LOGGER.error("No such file: " + credentialsFile.getAbsolutePath());
			return cred;
		}
		
		FileInputStream inputStream =  null;
		
		try {
			inputStream = new FileInputStream(credentialsFile);
			
		    byte[] bytes = inputStream.readAllBytes();
		    
		    ObjectMapper objectMapper = new ObjectMapper();
		    ObjectReader objectReader = objectMapper.readerForUpdating(cred);
		    
		    objectReader.readValue(bytes, OAuth2Credentials.class);	
		    
		}
		catch ( Exception e ) {
			LOGGER.error("Error on reading credentials data", e);
		}
		finally {
			
			try {
				if ( inputStream != null )
					inputStream.close();
			}
			catch ( Exception e ) {
				LOGGER.error("Error on closing streams", e);
			}
			
		}
		
		return cred;
	}
	

	@Override
	public OAuth2Credentials getCredentials() {
		
		OAuth2Credentials cred = null;
		
		File path = new File(basepath);
		File credentialsFile = new File(path, filename);
		
		if ( !credentialsFile.canRead() ) {
			LOGGER.error("No such file: " + credentialsFile.getAbsolutePath());
			return cred;
		}
		
		FileInputStream inputStream =  null;
		
		try {
			inputStream = new FileInputStream(credentialsFile);
			
		    byte[] bytes = inputStream.readAllBytes();
		    
		    ObjectMapper objectMapper = new ObjectMapper();
		    cred = objectMapper.readValue(bytes, OAuth2Credentials.class);	
		    
		}
		catch ( Exception e ) {
			LOGGER.error("Error on reading credentials data", e);
		}
		finally {
			
			try {
				if ( inputStream != null )
					inputStream.close();
			}
			catch ( Exception e ) {
				LOGGER.error("Error on closing streams", e);
			}
			
		}
		
		return cred;
	}
	
}
