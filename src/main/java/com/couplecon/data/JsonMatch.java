package com.couplecon.data;

import java.util.ArrayList;

public class JsonMatch {
	public String awayCoupleId;
	public String approve;
	public String partnerApprove;
	public String coupleApprove;
	public Boolean isMatch;
	public String matchId;
	public ArrayList<String> messages;
	
	public JsonMatch(String awayCoupleId) {
		this.awayCoupleId = awayCoupleId;
	}
	
}
