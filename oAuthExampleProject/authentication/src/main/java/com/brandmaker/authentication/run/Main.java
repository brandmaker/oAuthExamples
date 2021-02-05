package com.brandmaker.authentication.run;

import java.io.File;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brandmaker.authentication.factory.ConnectionFactory;
import com.brandmaker.authentication.modules.Connections.Modules;;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		LOGGER.info("Test client started");
		
		File credentialsFile = new File("./credentials/credentials.json");
		
		/*
		 * Create an "OPTIONS" call against Media Pool search API
		 */
		Builder request = ConnectionFactory.getInstance(credentialsFile)
		
							.init(Modules.MEDIA_POOL)
							.setMethod("OPTIONS")
							.setRestPath("/search")
							.setMediaType("application/json")
							
							/* 
							 * this will initialize JAX_RS entirely and returns a proper Builder to
							 * configure and invoke the request
							 */
							.build();
		
		LOGGER.info("Doing the request against Media Pool");
		Response response = request.get();
		
		LOGGER.info("result " + response.getStatus());
		String d = response.readEntity(String.class);
		LOGGER.info("Response content: " + (d.length() > 200 ? d.substring(0, 200) + "... total of " + d.length() : d) );
		
		
		/*
		 * Create a GET call against MaPl years endpoint
		 */
		
	}

	
}
