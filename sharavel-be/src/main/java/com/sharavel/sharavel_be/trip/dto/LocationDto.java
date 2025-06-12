package com.sharavel.sharavel_be.trip.dto;

import java.math.BigDecimal;

public class LocationDto {
	private Integer number;
	private String name;
	private BigDecimal longitude;
	private BigDecimal latitude;
	private String description;
	private String countryIso2;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCountryIso2() {
		return countryIso2;
	}

	public void setCountryIso2(String countryIso2) {
		this.countryIso2 = countryIso2;
	}

	@Override
	public String toString() {
		return "Locations{" +
				"name='" + name + '\'' +
				", number=" + number +
				", address= (" + longitude + "," + latitude + ")" +
				", description=" + description +
				'}';
	}

}
