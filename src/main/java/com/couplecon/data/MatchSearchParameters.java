package com.couplecon.data;

import java.util.ArrayList;
import java.util.Map;

public class MatchSearchParameters {
	ArrayList<String> approve;
	ArrayList<String> partnerApprove;
	Boolean isMatch;
	Boolean orderPriority;
	Integer limit;
	
	public ArrayList<String> getApprove() {
		return approve;
	}

	public void setApprove(ArrayList<String> approve) {
		this.approve = approve;
	}

	public ArrayList<String> getPartnerApprove() {
		return partnerApprove;
	}

	public void setPartnerApprove(ArrayList<String> partnerApprove) {
		this.partnerApprove = partnerApprove;
	}

	public Boolean getIsMatch() {
		return isMatch;
	}

	public void setIsMatch(Boolean isMatch) {
		this.isMatch = isMatch;
	}

	public Boolean getOrderPriority() {
		return orderPriority;
	}

	public void setOrderPriority(Boolean orderPriority) {
		this.orderPriority = orderPriority;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	
	public MatchSearchParameters() {
		this.approve = new ArrayList<String>();
		this.partnerApprove = new ArrayList<String>();
		this.isMatch = null;
		this.orderPriority = false;
		this.limit = null;
	}
	
	public MatchSearchParameters(Map<String,String[]> paramValues) {
		this();
		if (paramValues.containsKey("limit")) {
			this.limit = Integer.valueOf(paramValues.get("limit")[0]);
		}
		if (paramValues.containsKey("approve")) {
			for (String v : paramValues.get("approve")) {
				this.approve.add(v);
			}
		}
		if (paramValues.containsKey("partnerapprove")) {
			for (String v : paramValues.get("partnerapprove")) {
				this.partnerApprove.add(v);
			}
		}
		if (paramValues.containsKey("ismatch")) {
			this.isMatch = (Boolean.valueOf(paramValues.get("ismatch")[0]));
		}
		if (paramValues.containsKey("orderpriority")) {
			this.orderPriority = Boolean.valueOf(paramValues.get("orderpriority")[0]);
		}
	}
	
	public boolean filterApprove() {
		return !this.approve.isEmpty();
	}
	
	public boolean filterPartnerApprove() {
		return !this.partnerApprove.isEmpty();
	}
	
	public boolean filterIsMatch() {
		return this.isMatch != null;
	}
	
	public boolean useLimit() {
		return this.limit != null;
	}
	
}
