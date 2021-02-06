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
		
		LOGGER.info("\n=======================\n");
		/*
		 * Create an "OPTIONS" call against Media Pool search API
		 */
		Builder request = ConnectionFactory.getInstance(credentialsFile)
		
							.init(Modules.MEDIA_POOL)
							.setMethod("OPTIONS")
							.setRestPath("/v1.1/search")
							.setMediaType("application/json")
							
							/* 
							 * this will initialize JAX_RS entirely and returns a proper Builder to
							 * configure and invoke the request
							 */
							.build();
		
		LOGGER.info("Doing the request against Media Pool");
		Response response = request.options();
		
		LOGGER.info("result " + response.getStatus());
		String d = response.readEntity(String.class);
		LOGGER.info("Response content: " + (d.length() > 200 ? d.substring(0, 200) + "... total of " + d.length() : d) );
		
		
		LOGGER.info("\n=======================\n");
		/*
		 * Create a GET call against Marketing Planner years end-point
		 */
		request = ConnectionFactory.getInstance(credentialsFile)
				
				.init(Modules.PLANNER)
				.setMethod("GET")
				.setRestPath("/tree")
				.setMediaType("application/json")
				
				/* 
				 * this will initialize JAX_RS entirely and returns a proper Builder to
				 * configure and invoke the request
				 */
				.build();

		LOGGER.info("Doing the request against Marketing Planner");
		response = request.get();
		
		LOGGER.info("result " + response.getStatus());
		String data = response.readEntity(String.class);
		LOGGER.info("Response content: " + (data.length() > 200 ? data.substring(0, 200) + "... total of " + data.length() : data) );
		
	}

	
}
