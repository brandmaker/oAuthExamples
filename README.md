<img align="right" src="https://raw.githubusercontent.com/brandmaker/MediaPoolWebHookConsumer/master/BrandMaker_Logo_on_light_bg.png" alt="BrandMaker" width="30%" height="30%">

# BrandMaker oAuth2 Authentication and Client Registration Examples

## Motivation

Developers who want to make calls to the BrandMaker module APIs need proper oAuth2 registration of thier application or service and need to authenticate
by the provided oAuth2 access tokens.

## Scope

This example repo contains two sub-projects, which can be used in order to speed up implementation and serve as examples on how to integrate the BrandMaker CAS oAuth2 server:

* oauthclient
* authentication

### oauthclient

This is a small Spring Boot application which provides a Web form where a user is able to put in alle the dtails about the token generation.
On submitting the form, it will retrieve access and refresh tokens from the CAS server of BrandMaker.

<img src="https://raw.githubusercontent.com/brandmaker/oAuthExamples/master/oauthclient.png" alt="Screenshot" width="50%" height="50%">

### authentication


## Prerequisits

Please make yourself familiar with the oAuth flow described in the [BrandMaker documentaion](https://developers.brandmaker.com/guides/auth/) 

### Environment

* Java >= 11
* Spring Boot
* Eclipse / IntelliJ
* Maven
* Github
* Travis-CI https://travis-ci.org/getting_started

Furthermore, you need access to a BrandMaker instance with a user, who has access rights to Administartion in order to create a new registered App.


## Project state

[![Build Status](https://travis-ci.org/brandmaker/oAuthExamples.svg?branch=master)](https://travis-ci.org/brandmaker/oAuthExamples)


