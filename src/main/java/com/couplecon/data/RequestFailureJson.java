package com.couplecon.data;

public class RequestFailureJson {
	public int code;
	public String message;
	
	public static int SC_NO_REASON = 0;
	public static int SC_PROFILE_INCOMPLETE = 1;
	public static int SC_BAD_CATEGORICAL_LEVEL = 2;
	public static int SC_BAD_SURVEY_GROUP = 3;
	public static int SC_CR_TO_MATCHED_PARTNER = 4;
	public static int SC_CR_NO_PARTNER_ID = 5;
	public static int SC_NO_SUCH_PARTNER = 6;
	public static int SC_NO_SUCH_ACTION = 7;
	public static int SC_CR_NO_SUCH_CR = 8;
	
	public RequestFailureJson(int code) {
		this.code = code;
		this.message = this.getFailureMessage(code);
	}
	
	public RequestFailureJson() {
		this(0);
	}
	
	public static String getJsonString(int code) {
		RequestFailureJson x = new RequestFailureJson(code);
		return x.getJsonString();
	}
	
	public String getJsonString() {
		return "{\"code\":"+this.code+", \"message\":\""+this.message+"\"}";
	}
	
	public String getFailureMessage(int code) {
		if (code == RequestFailureJson.SC_NO_REASON) {
			return "no reason given";
		} else if (code == RequestFailureJson.SC_PROFILE_INCOMPLETE) {
			return "profile incomplete";
		} else if (code == RequestFailureJson.SC_BAD_CATEGORICAL_LEVEL) {
			return "level must be partner or couple";
		} else if (code == RequestFailureJson.SC_BAD_SURVEY_GROUP) {
			return "nonexistent survey group";
		} else if (code == RequestFailureJson.SC_CR_TO_MATCHED_PARTNER) {
			return "requested partner is already in a couple";
		} else if (code == RequestFailureJson.SC_CR_NO_PARTNER_ID) {
			return "requested partner not identified";
		} else if (code == RequestFailureJson.SC_NO_SUCH_PARTNER) {
				return "identified nonexistent partner";
		} else if (code == RequestFailureJson.SC_NO_SUCH_ACTION) {
			return "requested action is not available";
		} else if (code == RequestFailureJson.SC_CR_NO_SUCH_CR) {
			return "requested action on a couple request that does not exist";
		}
		else {
			return null;
		}
	}
	
}
