package com.brandmaker.oauthclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FormController {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FormController.class);
	
	@Autowired
	CredentialsFileHandler credentialsFileHandler;
	
	@Autowired 
	OAuth2Credentials oauth2Credentials;
	
	@Autowired
	RestHandler restHandler;
	
	@GetMapping("/oauthflow")
    public String processAuthenticationCodes( @RequestParam(required=false) HashMap<String, String> params, Model model) {
        
		LOGGER.info("======================> " + params.toString());
		
		/*
		 * An error response:
		 * 	{
		 *	    "error":"invalid_request",
		 *	    "error_description":"The request is missing a required parameter, includes an invalid parameter value, includes a parameter more than once, or is otherwise malformed",
		 *		"error_hint": "Redirect URL is using an insecure protocol, http is only allowed for hosts with suffix `localhost`, for example: http://myapp.localhost/.",
		 *	    "state":"123456789"
		 *	}
		 */
		if ( params.containsKey("error") ) {
			model.addAttribute("errormessage", params.get("error") + ": " + params.get("error_description") + (params.get("error_hint")!=null?" - " + params.get("error_hint"):""));
			model.addAttribute("haserror", true);
			model.addAttribute("credentials", oauth2Credentials );
			
			// return to start immediately
			return "start";
		}
		else {
			model.addAttribute("errormessage", "");
			model.addAttribute("haserror", false);
			
			// check the auth codes and retrieve the access tokens
			String code = params.get("code");
			
			if ( code == null || code.trim().equals("") ) {
				model.addAttribute("errormessage", "No code has been submitted ...?");
				model.addAttribute("haserror", true);
				model.addAttribute("credentials", oauth2Credentials );
				
				// return to start immediately
				return "start";
			}
			
			oauth2Credentials.setCode(code);
			
			/*
			 * let's exchange the auth code to access and refresh tokens:
			 * 		curl -d "code=oauth2_authorization_code&\
			 * 		client_id=6274aa2f-a93f-479c-a3b3-62850f8322bd&\
			 * 		client_secret=your_client_secret&\
			 * 		grant_type=authorization_code&\
			 * 		redirect_uri=https%3A%2F%2Foauthdebugger.com%2Fdebug%0D%0A" -X POST "https://cas.brandmaker.com/api/v1.1/token"
			 * 
			 */
			boolean success = restHandler.changeCode(oauth2Credentials);
			
			model.addAttribute("credentials", oauth2Credentials );
			
			model.addAttribute("successmessage", success ? "The tokens have been generated and can be picked up from 'credentials.json'" : "" );
			model.addAttribute("success", success);
			
			return "start";
		}
        
    }
	
    /**
     * The form is submitted and we are going to start the oAuth2 flow now.
     * First, let's check the values etc.
     * 
     * @param credentials current form parameters
     * @param model current MVC model
     * 
     * @return name of thymeleaf template
     */
    @PostMapping("/")
    public String formSubmit(@ModelAttribute OAuth2Credentials credentials, Model model) {
    	
    	LOGGER.info(credentials != null ? credentials.toString() : "no credentials in Post ...?" );
    	
    	// store to file
    	credentialsFileHandler.storeCredentials(credentials);
    	
    	// update autowired pojo
    	credentialsFileHandler.updateCredentials(oauth2Credentials);
    	
    	/*
    	 * Now lets do a redirect to the CAS.
    	 * The URL looks like this:
    	 * https://cas-dev.brandmaker.com/oauth2/auth?
    	 * 		client_id=08d34285-bd11-4cb7-a257-b0f8b33e564a 
    	 * 		&redirect_uri=https://oauthdebugger.com/debug 
    	 * 		&scope=module_access 
    	 * 		&response_type=code 
    	 * 		&response_mode=query 
    	 * 		&state=asdasdasdasdasdas
    	 * 
    	 */
    	String redirectUri = oauth2Credentials.getCasUrl()
    			+ "/oauth2/auth?client_id=" + oauth2Credentials.getClientId()
    			+ "&redirect_uri=" + oauth2Credentials.getRedirectUrl()
    			+ "&scope=module_access"
    			+ "&response_type=code"
    			+ "&response_mode=query"
    			+ "&state=123456789";
    	
    	model.addAttribute("credentials", credentials);
      
    	return "redirect:"+redirectUri;
    	
    }
    
    /**
     * @param model current MVC model
     * @return ame of thymeleaf template, usually w/o ".html"
     * 
     */
    @GetMapping("/")
    public String openGeneratorForm(Model model) {
    	
    	// update autowired pojo from file
    	credentialsFileHandler.updateCredentials(oauth2Credentials);
    	
    	LOGGER.info(oauth2Credentials.toString() );
    	
    	// binding the credentials object to the model
        model.addAttribute("credentials", oauth2Credentials );
        
        return "start";
    }

}
