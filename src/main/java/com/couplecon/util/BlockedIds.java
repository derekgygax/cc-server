package com.couplecon.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class BlockedIds {
	HashSet<String> blockedIds = new HashSet<String>();
	HashSet<String> ignoredIds = new HashSet<String>();
	
	public void ignoreCoupleId(String coupleId) {
		ignoredIds.add(coupleId);
		blockedIds.remove(coupleId);
		List<String> partnerIds = Arrays.asList(Utils.getPartners(coupleId));
		ignoredIds.addAll(partnerIds);
		blockedIds.removeAll(partnerIds);
	}
	
	public void ignorePartnerId(String partnerId) {
		ignoredIds.add(partnerId);
		ignoredIds.remove(partnerId);
	}
	
	public void addCoupleId(String coupleId) {
		if (!ignoredIds.contains(coupleId)) {
			blockedIds.add(coupleId);
			blockedIds.addAll(Arrays.asList(Utils.getPartners(coupleId)));
		}
	}
	
	public void addPartnerId(String partnerId) {
		if (!ignoredIds.contains(partnerId)) {
			blockedIds.add(partnerId);
			blockedIds.addAll(Arrays.asList(Utils.getPartners(partnerId)));
		}
	}
	
	public boolean contains(String Id) {
		return blockedIds.contains(Id);
	}
}
