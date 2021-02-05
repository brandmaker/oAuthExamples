package com.brandmaker.authentication.modules;

public class Connections {

	public static enum Modules  {
			MEDIA_POOL,
			PLANNER,
			JOBS,
	}

	public static String getRestUrl(Modules module) {

		switch ( module ) {
		
			case MEDIA_POOL:
				return "/rest/mp"; // mind the leading "/"!
				
			default:
				return "/";
			
		}
	};

}
