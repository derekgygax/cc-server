package com.couplecon.data;

public class PartnerJson {
	public String partnerId;
	public String firstName;
	public String middleName;
	public String lastName;
	public int age;
	public String zipcode;
	public String smoke;
	public String drink;
	public String politics;
	public String religion;
	public String gender;
	public String race;
	public String timeInArea;
	public String incomeRange;
	public Integer verifiedEmail;
	
	public PartnerJson() {
		
	}
	
	public PartnerJson(Partner partner) {
		this();
		this.partnerId = partner.partnerId;
		this.firstName = partner.firstName;
		this.middleName = partner.middleName;
		this.lastName = partner.lastName;
		this.age = partner.age;
		this.zipcode = partner.zipcode;
		this.smoke = partner.smoke;
		this.drink = partner.drink;
		this.politics = partner.politics;
		this.religion = partner.religion;
		this.gender = partner.gender;
		this.race = partner.race;
		this.timeInArea = partner.timeInArea;
		this.incomeRange = partner.incomeRange;
		this.verifiedEmail = partner.verifiedEmail;
	}
}
