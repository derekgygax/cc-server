package com.couplecon.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {
	private float lat;
	private float lon;
	
	public Location(@JsonProperty("lat") float latitude, @JsonProperty("lon") float longitude) {
		this.lat = latitude;
		this.lon = longitude;
	}
	
	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}
	
	@JsonIgnore
	public String getWKT() {
		return "Pos("+String.valueOf(this.lat)+" "+String.valueOf(this.lon)+")";
	}
	
	@JsonIgnore
	public float[] getLocationPair() {
		return new float[]{this.lat, this.lon};
	}
	
}
