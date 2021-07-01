package com.couplecon.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.couplecon.data.JsonMatch;
import com.couplecon.util.Utils;

public class Match {
	public String homeCoupleId;
	public String awayCoupleId;
	public HashSet<String> homePartners;
	public HashMap<String, String> partnerApprove;
	public String coupleApprove;
	public Boolean isMatch;
	public float priority;
	public String type;
	public String matchId;
	public ArrayList<String> messages = new ArrayList<String>();
	
	public Match(String homeCoupleId, String awayCoupleId) {
		this.homeCoupleId = homeCoupleId;
		this.awayCoupleId = awayCoupleId;
		this.homePartners = new HashSet<String>();
		this.partnerApprove = new HashMap<String, String>();
		for (String partnerId : Utils.getPartners(this.homeCoupleId)) {
			this.homePartners.add(partnerId);
		}
	}
	
	public void setPartnerApprove(String partnerId, String approve) {
		if (this.homePartners.contains(partnerId)) {
			this.partnerApprove.put(partnerId, approve);
		}
	}

	public String getPartnerApprove(String partnerId) {
		return partnerApprove.get(partnerId);
	}
	
	public JsonMatch getJsonMatch(String partnerId) {
		String otherPartnerId = Utils.getOtherPartnerId(homeCoupleId, partnerId);
		JsonMatch jsonMatch = new JsonMatch(awayCoupleId);
		jsonMatch.isMatch = this.isMatch;
		jsonMatch.approve = getPartnerApprove(partnerId);
		jsonMatch.partnerApprove = getPartnerApprove(otherPartnerId);
		jsonMatch.coupleApprove = this.coupleApprove;
		jsonMatch.matchId = this.matchId;
		jsonMatch.messages = this.messages;
		return jsonMatch;
	}
}