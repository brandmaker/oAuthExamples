package com.brandmaker.oauthclient;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * <h1>This is an oAuth2 client authentication generator.</h1>
 * 
 * <p>It collects some basic information about an external application which wants to authenticate against a BrandMaker instances APIs by oAuth2 access tokens.
 * <p>It handles the entire oAuth2 flow with BrandMaker CAS and stores the tokens into a JSON file for further usage in the client SDK.
 * <p>The client SDK as well is provided in this repository
 * 
 * @author axel.amthor
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

public class OauthclientApplication {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OauthclientApplication.class);
	
	
	public static void main(String[] args) {
		
		LOGGER.info("Client Generator started");
		
		SpringApplication.run(OauthclientApplication.class, args);
		
	}


	
}
