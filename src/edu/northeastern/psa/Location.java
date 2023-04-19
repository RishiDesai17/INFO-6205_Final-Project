package edu.northeastern.psa;

public class Location {
	double lat;
	double lon;
	String crimeId;
	
	public Location(double lat, double lon, String crimeId) {
		this.lat = lat;
		this.lon = lon;
		this.crimeId = crimeId;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getCrimeId() {
		return crimeId;
	}

	public void setCrimeId(String crimeId) {
		this.crimeId = crimeId;
	}
	
	
	
}
