package com.couplecon.data;

import java.sql.ResultSet;
import com.couplecon.data.JsonViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

public class Partner {
	String partnerId;
	String firstName;
	String middleName;
	String lastName;
	Integer age;
	String password_hash;
	String salt;
	String emailAddress;
	String password;
	String facebookId;
	String phoneNumber;
	String address;
	String city;
	String state;
	String zipcode;
	String smoke;
	String drink;
	String politics;
	String religion;
	String gender;
	String race;
	String timeInArea;
	String incomeRange;
	public Integer verifiedEmail;
	
	public Partner() {
		
	}
	
	public Partner(ResultSet rs) throws Exception {
		this();
		this.loadResultSet(rs);
	}
	
	public void loadResultSet(ResultSet rs) throws Exception {
		this.partnerId = rs.getString("ID");
		this.firstName = rs.getString("first_name");
		this.middleName = rs.getString("middle_name");
		this.lastName = rs.getString("last_name");
		this.age = rs.getInt("age");
		this.emailAddress = rs.getString("email_address");
		this.facebookId = rs.getString("facebook_ID");
		this.phoneNumber = rs.getString("phone_number");
		this.address = rs.getString("address");
		this.city = rs.getString("city");
		this.state = rs.getString("state");
		this.zipcode = rs.getString("zipcode");
		this.smoke = rs.getString("smoke");
		this.drink = rs.getString("drink");
		this.politics = rs.getString("politics");
		this.religion = rs.getString("religion");
		this.gender = rs.getString("gender");
		this.race = rs.getString("race");
		this.timeInArea = rs.getString("time_in_area");
		this.incomeRange = rs.getString("income_range");
		this.verifiedEmail = rs.getInt("email_verified");
	}
	
	public PartnerJson generatePublicJson() {
		return new PartnerJson(this);
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@JsonView(JsonViews.General.Internal.class)
	public String getPassword_hash() {
		return password_hash;
	}

	public void setPassword_hash(String password_hash) {
		this.password_hash = password_hash;
	}
	
	@JsonView(JsonViews.General.Internal.class)
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	@JsonView(JsonViews.General.CoupleMember.class)
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@JsonView(JsonViews.General.Internal.class)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@JsonView(JsonViews.General.CoupleMember.class)
	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	
	@JsonView(JsonViews.General.CoupleMember.class)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	@JsonView(JsonViews.General.CoupleMember.class)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getSmoke() {
		return smoke;
	}

	public void setSmoke(String smoke) {
		this.smoke = smoke;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getDrink() {
		return drink;
	}

	public void setDrink(String drink) {
		this.drink = drink;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getPolitics() {
		return politics;
	}

	public void setPolitics(String politics) {
		this.politics = politics;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getTimeInArea() {
		return timeInArea;
	}

	public void setTimeInArea(String timeInArea) {
		this.timeInArea = timeInArea;
	}
	
	@JsonView(JsonViews.General.Public.class)
	public String getIncomeRange() {
		return incomeRange;
	}

	public void setIncomeRange(String incomeRange) {
		this.incomeRange = incomeRange;
	}
}
