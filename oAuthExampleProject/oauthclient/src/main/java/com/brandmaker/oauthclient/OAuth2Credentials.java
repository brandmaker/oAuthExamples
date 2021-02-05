package com.brandmaker.oauthclient;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OAuth2Credentials {
	
	//inject via application.yaml
    @Value("${spring.application.system.cas-url}")
    private String casUrl = "https://cas.brandmaker.com";

    @Value("${spring.server.port}")
    private long portNumber = 8080L;
    
	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Credentials.class);
	
	private String systemUrl = "";
	private String clientId = "";
	private String clientSecret = "";
	private String redirectUrl = "http://<my host or ip>:" + portNumber + "/oauthflow";
	
	private String code = "";
	private String token = "";
	private String refresh = "";
	private Long  expires = 0L;
	
	
	/**
	 * returns a JSON String of this object.
	 */
	@Override
	public String toString() {
		ObjectMapper Obj = new ObjectMapper();

		try {

			String jsonStr = Obj.writeValueAsString(this);
			JSONObject jo = new JSONObject(jsonStr);
			
			return jo.toString();
		}

		catch (IOException | JSONException e) {
			LOGGER.error("an error", e);
		}
		return null;
	}
	
	/**
	 * @return the casUrl
	 */
	public String getCasUrl() {
		return casUrl;
	}
	
	/**
	 * @param casUrl the casUrl to set
	 */
	public void setCasUrl(String casUrl) {
		this.casUrl = casUrl;
	}
	
	/**
	 * @return the systemUrl
	 */
	public String getSystemUrl() {
		return systemUrl;
	}
	
	/**
	 * @param systemUrl the systemUrl to set
	 */
	public void setSystemUrl(String systemUrl) {
		this.systemUrl = systemUrl;
	}
	
	/**
	 * @return the cientId
	 */
	public String getClientId() {
		return clientId;
	}
	
	/**
	 * @param cientId the cientId to set
	 */
	public void setClientId(String cientId) {
		this.clientId = cientId;
	}
	
	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}
	
	/**
	 * @param clientSecret the clientSecret to set
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * @return the redirectUrl
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	/**
	 * @param redirectUrl the redirectUrl to set
	 */
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	/**
	 * @return the portNumber
	 */
	public long getPortNumber() {
		return portNumber;
	}

	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(long portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the refresh
	 */
	public String getRefresh() {
		return refresh;
	}

	/**
	 * @param refresh the refresh to set
	 */
	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}

	/**
	 * @return the expires
	 */
	public Long getExpires() {
		return expires;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(Long expires) {
		this.expires = expires;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

}
