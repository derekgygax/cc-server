package com.couplecon.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.couplecon.data.Partner;

public class CoupleRequests {
	private String partnerId;
	private ArrayList<ArrayList<Partner>> pairs;
	
	public CoupleRequests (String partnerId) {
		this.partnerId = partnerId;
		this.pairs = new ArrayList<ArrayList<Partner>>();
	}
	
	public void addPair (Partner reqPartner, Partner resPartner) {
		ArrayList<Partner> pair = new ArrayList<Partner>(2);
		pair.add(0, reqPartner);
		pair.add(1, resPartner);
		this.addPair(pair);
	}
	
	public void addPair (List<Partner> pair) {
		ArrayList<Partner> alpair = new ArrayList<Partner>(pair.subList(0,2));
		this.pairs.add(alpair);
	}
	
	public ArrayList<Partner> getRecipients () {
		ArrayList<Partner> recipients = new ArrayList<Partner>();
		for (ArrayList<Partner> pair : this.pairs) {
			if (this.partnerId.equals(pair.get(0).partnerId)) {
				recipients.add(pair.get(1));
			}
		}
		return recipients;
	}
	
	public ArrayList<Partner> getSenders () {
		ArrayList<Partner> recipients = new ArrayList<Partner>();
		for (ArrayList<Partner> pair : this.pairs) {
			if (this.partnerId.equals(pair.get(1).partnerId)) {
				recipients.add(pair.get(0));
			}
		}
		return recipients;
	}
	
	public CoupleRequestsJson getJsonObject () {
		CoupleRequestsJson j = new CoupleRequestsJson();
		j.to = this.getRecipients();
		j.from = this.getSenders();
		return j;
	}
}
