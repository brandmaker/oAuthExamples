package com.brandmaker.authentication.factory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brandmaker.authentication.modules.Connections;
import com.brandmaker.authentication.modules.Connections.*;
import com.brandmaker.authentication.utils.CredentialsFileHandler;
import com.brandmaker.authentication.utils.CredentialsFileHandlerImpl;
import com.brandmaker.authentication.utils.OAuth2Credentials;

public class ConnectionFactoryImpl implements ConnectionFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
	private static OAuth2Credentials credentials;
	private Modules module;
	private String method;
	private String restPath;
	private String mimeType;
	private Builder requestBuilder;
	
	/**
	 * hide the constructor
	 */
	private ConnectionFactoryImpl() {
		
	}

	/**
	 * @return an instance
	 */
	static ConnectionFactory factory(File credentialsFile) {
		
		ConnectionFactoryImpl cf = new ConnectionFactoryImpl();
		
		cf.loadOAUthSettings(credentialsFile);
		
		return cf;
	}

	@Override
	public ConnectionFactory init(Modules module) {
		
		this.module = module;		
		
		return this;
	}
	
	/**
	 * <p>This request filter handles the entire Bearer based authentication to the BrandMaker instance.
	 * <ul>
	 * 		<li>Check token validity
	 * 		<li>If expired
	 * 			<ul>
	 * 				<li>Request a new token set from CAS
	 * 				<li>Store the new tokens
	 * 			</ul>
	 * 		<li>create and add a authorization header to the request
	 * </ul>
	 * @author axel.amthor
	 *
	 */
	public static class BrandMakerRequestAuthenticationFilter implements ClientRequestFilter {

		private static final long GRACEPERIOD = 2*60*1000; // 2 minutes in milliseconds

		@Override
		public void filter(ClientRequestContext requestContext) throws IOException {
			
			long tokenExpiration = credentials.getExpires();
			long now = System.currentTimeMillis();
			
			if ( (tokenExpiration - GRACEPERIOD) <= now ) {
				LOGGER.info("Token expired since " + ((now - (tokenExpiration - GRACEPERIOD))/1000) + " seconds: " + (new Date(tokenExpiration)).toGMTString() );
				
				updateTokens();
			}
			else
				LOGGER.info("Token expires at "  + (new Date(tokenExpiration)).toGMTString() );
			
			requestContext.getHeaders().add("Authorization", "Bearer " + credentials.getToken() );
			LOGGER.info("--------------- here we go in the filter");
		}

		/**
		 * retrieve new token set from CAS system:
		 * <pre>
		 * curl -d "client_id=6274aa2f-a93f-479c-a3b3-62850f8322bd&\
   		 * client_secret=your_client_secret&
   		 * grant_type=refresh_token&\
   		 * refresh_token=your_refresh_token" -X POST "https://cas.brandmaker.com/api/v1.1/token"
   		 * </pre>
   		 * 
   		 * @see <a href="https://developers.brandmaker.com/guides/auth/#request-a-new-access-token-with-the-refresh-token">https://developers.brandmaker.com/guides/auth/#request-a-new-access-token-with-the-refresh-token</a>
		 */
		private void updateTokens() {
			Client client = ClientBuilder.newClient();
			
			/*
			 * construct the token refresh url
			 */
			String refreshUrl = credentials.getCasUrl().replaceAll("/*$", "") + "/api/v1.1/token";
			LOGGER.info("refresh url: " + refreshUrl);
			
			/*
			 * construct the POST data
			 */
			Form formData = new Form();
			formData.param("client_id", credentials.getClientId());
			formData.param("client_secret", credentials.getClientSecret());
			formData.param("grant_type", "refresh_token");
			formData.param("refresh_token", credentials.getRefresh() );
			
			/*
			 * create a new JAX RS web target
			 */
	        WebTarget target = client.target(refreshUrl);
	        
	        /*
	         * we need to follow redirects
	         */
	        target.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.TRUE);
	        
	        /*
	         * issue post, get response
	         */
	        Response response = target.request()
    					.accept(MediaType.APPLICATION_JSON)
	        			.post(Entity.form(formData));
	        
	        LOGGER.info("Response from CAS: " + response.getStatus());
        	String data = response.readEntity(String.class);
        	LOGGER.debug("Response from CAS: " + data);
        			
        	JSONObject tokens = new JSONObject(data);
        	if ( tokens.has("error") ) {
        		LOGGER.error("Error: " + tokens.getString("error"));
        		LOGGER.error(tokens.getString("error_description"));
        	}
        	else {
	        	/*
	        	 * update the credentials object
	        	 */
	        	credentials.setToken(tokens.getString("access_token"));
	        	credentials.setRefresh(tokens.getString("refresh_token"));
	        	credentials.setExpires( (tokens.getLong("expires_in")*1000) + System.currentTimeMillis());
	        	
	        	File credentialsFile = new File("./credentials/credentials.json");
	        	CredentialsFileHandler.getInstance().storeCredentials(credentials, credentialsFile);
        	}
		}

	}

	@Override
	public Builder build() {
		
		
		/*
         * we could easily add an authentication header here, but between this build() call and the invocation itself may some time be elapsed and 
         * the tokens expired meanwhile. Therefore we inject a request interceptor which handles the entire authentication and
         * token refresh procedure within a running request invocation.
         */
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(BrandMakerRequestAuthenticationFilter.class);
		
		/*
		 * Now that we have collected all necessary information, we can start building up the request structure
		 */
		Client client = ClientBuilder.newClient(clientConfig);
		
		/*
		 * construct the resource url
		 */
		String resourceUrl = credentials.getSystemUrl().replaceAll("/*$", "") + Connections.getRestUrl(module) + "/" + restPath.replaceAll("^/*", "");
		LOGGER.info("url: " + resourceUrl);
		
		/*
		 * create a new JAX RS web target
		 */
        WebTarget target = client.target(resourceUrl);
        
        /*
         * we need to follow redirects
         */
        target.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.TRUE);
        
        
        this.requestBuilder = target.request();
        
        
        return requestBuilder;
		
	}

	@Override
	public ConnectionFactory loadOAUthSettings(File credentialsFile) {

		if ( ConnectionFactoryImpl.credentials != null )
			CredentialsFileHandler.getInstance().updateCredentials(credentials, credentialsFile);
		else
			ConnectionFactoryImpl.credentials = CredentialsFileHandler.getInstance().getCredentials(credentialsFile);
		
		LOGGER.debug("credentials: " + ConnectionFactoryImpl.credentials.toString(4));
		
		return this;
	}

	@Override
	public ConnectionFactory setMethod(String method) {
		
		this.method = method;
		
		return this;
	}

	@Override
	public ConnectionFactory setRestPath(String path) {
		
		this.restPath = path;
		
		return this;
	}

	@Override
	public ConnectionFactory setMediaType(String mimeType) {
		
		this.mimeType = mimeType;
		
		return this;
	}
	
	
}
