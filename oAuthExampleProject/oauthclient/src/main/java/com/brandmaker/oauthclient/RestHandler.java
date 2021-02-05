package com.brandmaker.oauthclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestHandler extends HttpConnectionHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(RestHandler.class);
	
	@Autowired
	CredentialsFileHandler credentialsFileHandler;
	
	/**
	 * let's exchange the auth code to access and refresh tokens:
	 * 		curl -d "code=oauth2_authorization_code&\
	 * 		client_id=6274aa2f-a93f-479c-a3b3-62850f8322bd&\
	 * 		client_secret=your_client_secret&\
	 * 		grant_type=authorization_code&\
	 * 		redirect_uri=https%3A%2F%2Foauthdebugger.com%2Fdebug%0D%0A" -X POST "https://cas.brandmaker.com/api/v1.1/token"
	 * 
	 * @param oauth2Credentials
	 * 
	 */
	public boolean changeCode(OAuth2Credentials oauth2Credentials) {
		
		HttpURLConnection mdconn = null;
		String exchangeUrl = oauth2Credentials.getCasUrl() + "/api/v1.1/token";
		
		try {
			mdconn = connectUri(exchangeUrl, "POST");
			mdconn.connect();
			
			String rqBody = "code=" + URLEncoder.encode(oauth2Credentials.getCode(), "UTF-8")
					+ "&client_id=" + URLEncoder.encode(oauth2Credentials.getClientId(), "UTF-8")
					+ "&client_secret=" + URLEncoder.encode(oauth2Credentials.getClientSecret(), "UTF-8")
					+ "&grant_type=authorization_code"
					+ "&redirect_uri=" + URLEncoder.encode(oauth2Credentials.getRedirectUrl(), "UTF-8");
			
			LOGGER.info(rqBody);
			
			mdconn.getOutputStream().write(rqBody.getBytes(), 0, rqBody.length());
			
			int rc = mdconn.getResponseCode();

			LOGGER.info("Response code is " + rc );
			
			String data = getRequestResponseString(mdconn);
			mdconn.disconnect();
			
			LOGGER.info(data);
			JSONObject tokens = new JSONObject(data);
			
			oauth2Credentials.setToken(tokens.getString("access_token"));
			oauth2Credentials.setRefresh(tokens.getString("refresh_token"));
			oauth2Credentials.setExpires( (tokens.getLong("expires_in")*1000) + System.currentTimeMillis());
			
			credentialsFileHandler.storeCredentials(oauth2Credentials);
			
		} 
		catch ( Exception e )
		{
			LOGGER.error("An error", e);
			return false;
		}
		finally {
			if ( mdconn != null )
				mdconn.disconnect();
		}
		return true;
	}

}
