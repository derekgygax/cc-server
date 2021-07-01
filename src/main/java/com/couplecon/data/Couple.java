package com.couplecon.data;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import com.couplecon.data.Partner;
import com.fasterxml.jackson.annotation.JsonView;

public class Couple {
	String coupleId;
	String partnerIdLower;
	String partnerIdHigher;
	String relationshipType;
	String timeTogether;
	String story;
	String childrenAtHome;
	String youngestChild;
	String oldestChild;
	Integer numChildren;
	HashMap<String, Partner> partners;
	TreeMap<Integer, HashMap<String, String>> pictureLinks;
	Location location;
	Integer maxDistance;
	
	public Couple() {
		this.partners = new HashMap<String, Partner>();
		this.pictureLinks = new TreeMap<Integer, HashMap<String, String>>();
	}
	
	public Couple(ResultSet rs) throws Exception {
		this();
		this.loadResultSet(rs);
	}
	
	public void loadResultSet(ResultSet rs) throws Exception{
		this.coupleId = rs.getString("ID");
		this.partnerIdLower = rs.getString("partner_ID_lower");
		this.partnerIdHigher = rs.getString("partner_ID_higher");
		this.relationshipType = rs.getString("relationship_type");
		this.timeTogether = rs.getString("time_together");
		this.story = rs.getString("story");
		this.childrenAtHome = rs.getString("children_at_home");
		this.youngestChild = rs.getString("youngest_child");
		this.oldestChild = rs.getString("oldest_child");
		this.numChildren = rs.getInt("num_children");
		float lat = Float.valueOf(rs.getString("lat"));
		float lon = Float.valueOf(rs.getString("lon"));
		this.location = new Location(lat,lon);
		this.maxDistance = rs.getInt("max_distance");
	}
	
	public void addPictureLink(Integer picNum, String size, String link) {
		if (!this.pictureLinks.containsKey(picNum)) {
			this.pictureLinks.put(picNum, new HashMap<String, String>());
		}
		this.pictureLinks.get(picNum).put(size, link);
	}
	
	public void addPartner(String partnerId, Partner partner) {
		partner.setPartnerId(partnerId);
		this.addPartner(partner);
	}
	
	public void addPartner(Partner partner) {
		this.partners.put(partner.getPartnerId(), partner);
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getCoupleId() {
		return coupleId;
	}

	public void setCoupleId(String coupleId) {
		this.coupleId = coupleId;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getPartnerIdLower() {
		return partnerIdLower;
	}

	public void setPartnerIdLower(String partnerIdLower) {
		this.partnerIdLower = partnerIdLower;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getPartnerIdHigher() {
		return partnerIdHigher;
	}

	public void setPartnerIdHigher(String partnerIdHigher) {
		this.partnerIdHigher = partnerIdHigher;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getTimeTogether() {
		return timeTogether;
	}

	public void setTimeTogether(String timeTogether) {
		this.timeTogether = timeTogether;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getChildrenAtHome() {
		return childrenAtHome;
	}

	public void setChildrenAtHome(String childrenAtHome) {
		this.childrenAtHome = childrenAtHome;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getYoungestChild() {
		return youngestChild;
	}

	public void setYoungestChild(String youngestChild) {
		this.youngestChild = youngestChild;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getOldestChild() {
		return oldestChild;
	}

	public void setOldestChild(String oldestChild) {
		this.oldestChild = oldestChild;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public Integer getNumChildren() {
		return numChildren;
	}

	public void setNumChildren(Integer numChildren) {
		this.numChildren = numChildren;
	}
	
	@JsonView(JsonViews.General.CoupleMember.class)
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public HashMap<String, Partner> getPartners() {
		return partners;
	}

	@JsonView(JsonViews.General.Public.class)
	public ArrayList<HashMap<String,String>> getProfilePictures() {
		ArrayList<HashMap<String,String>> listMap = new ArrayList<HashMap<String,String>>();
		for (Integer index : this.pictureLinks.keySet()) {
			listMap.add(this.pictureLinks.get(index));
		}
		return listMap;
	}

	@JsonView(JsonViews.General.CoupleMember.class)
	public Integer getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Integer maxDistance) {
		this.maxDistance = maxDistance;
	}
	
	
}