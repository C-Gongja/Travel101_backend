package com.sharavel.sharavel_be.search.dto;

public class SearchDto {
	private String tripUid;
	private String tripName;
	private String username;
	private String locationName;
	private String locationDescription;
	// private String countryName;
	// private String tagName;

	// 생성자, getter, setter
	public SearchDto(String tripUid, String tripName, String username,
			String locationName, String locationDescription) {
		this.tripUid = tripUid;
		this.tripName = tripName;
		this.username = username;
		this.locationName = locationName;
		this.locationDescription = locationDescription;
	}

	public String getTripId() {
		return tripUid;
	}

	public void setTripId(String tripUid) {
		this.tripUid = tripUid;
	}

	public String getTripName() {
		return tripName;
	}

	public void setTripName(String tripName) {
		this.tripName = tripName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}

}
