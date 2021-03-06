<img align="right" src="https://raw.githubusercontent.com/brandmaker/MediaPoolWebHookConsumer/master/BrandMaker_Logo_on_light_bg.png" alt="BrandMaker" width="30%" height="30%">

# BrandMaker oAuth2 Authentication and Client Registration Examples

## Motivation

Developers who want to make calls to the BrandMaker module APIs need proper oAuth2 registration of their application or service and need to authenticate
by the provided oAuth2 access tokens.

## Scope

This example repo contains two sub-projects, which can be used in order to speed up implementation and serve as examples on how to integrate the BrandMaker CAS oAuth2 server:

* oauthclient
* authentication

### oauthclient

This is a small Spring Boot application which provides a web form where a user is able to put in alle the details about the token generation.
On submitting the form, it will retrieve access and refresh tokens from the CAS server of BrandMaker.

<img src="https://raw.githubusercontent.com/brandmaker/oAuthExamples/master/oauthclient.png" alt="Screenshot" width="50%" height="50%">

To start the application
* go into the project directory .../oauthclient
* run `java -jar target/oauthclient-0.9.jar`
* open your web browser on `https://<your server>:<ssl port>` and fill in the form. The url depends on your particular setup, as well as the port number. Please mind the hint aboaut SSL/https below. The redirect endpoint provided by this app is always `https://<your server>:<ssl port>/`**`oauthflow`**
* hit "Generate". If everything is setup properly, you should be redirected to your BrandMaker instance in order to log in
* confirm the consent
* the created tokens are stored in the file `.../credentials/credentials.json` in the current directory.

The tokens can be picked from the above mentioned file. They will not be shown on the web pages as this would be a security risk!

> ***Make sure, that no one is able to get a copy of your client secret and client ID as with this it is pssible to gain access to your BrandMaker instance***

> Hint: the redirect must be an SSL URL (HTTPS). So either put this application behind an ssl enabled server like apache or nginx, or configure this spring boot application to use SSL. This cannot be done within this project, as it requires a verified domain name and server certificates for that. Self-signed certificates on an IP address won't work together with BarndMaker IAM. A `loclahost`address may also not work as this won't be reachable for the BrandMaker CAS service from the outside.

### authentication

This package contains a wrapper around the [`JAX-RS client API`](https://docs.oracle.com/javaee/7/tutorial/jaxrs-client.htm) in order to connect with oAuth2 tokens to the API of a BrandMaker instance.
It creates a WebTarget Builder which can be used to issue REST calls against the API. The builder has an integrated filter which handles the entire token validation and exchange if necessary. It checks on every request, whether the configured tokens are still valid and issues a token refresh if necessary. 

If the tokens are successfully exchanged, they are persisted again in the configuration JSON file.

In order to initialze the token authentication, pick the generated file from above (oauthclient) and put it into the folder `<current startdirectory>/credentials`.

<img src="https://raw.githubusercontent.com/brandmaker/oAuthExamples/master/authenticate.png"  widh="50%" height="50%">

The package `com/brandmaker/authentication/run/` contains a `Main.java` class with an example of  an integration. As seen above, this can be executed in order to demonstrate the proper token exchange and log in.

```
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
		ConnectionFactory connectionFactory = ConnectionFactory.getInstance(credentialsFile);
		connectionFactory
				
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
		response = connectionFactory.getInvoker().invoke();
		
		LOGGER.info("result " + response.getStatus());
		String data = response.readEntity(String.class);
		LOGGER.info("Response content: " + (data.length() > 200 ? data.substring(0, 200) + "... total of " + data.length() : data) );
```


## Prerequisits

Please make yourself familiar with the oAuth flow described in the [BrandMaker documentaion](https://developers.brandmaker.com/guides/auth/) 

### Environment

* Java >= 11
* [Spring Boot 2.4.2](https://spring.io/projects/spring-boot)
* [Thymeleaf](https://www.thymeleaf.org/)
* [JAX-RS](https://github.com/jax-rs)
* Eclipse / IntelliJ
* Maven
* Github
* [Travis-CI](https://travis-ci.org/getting_started)

Furthermore, you need access to a BrandMaker instance with a user, who has access rights to Administartion in order to create a new registered App.


## Project state

[![Build Status](https://travis-ci.org/brandmaker/oAuthExamples.svg?branch=master)](https://travis-ci.org/brandmaker/oAuthExamples)


