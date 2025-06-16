package com.sharavel.sharavel_be.trip.dto;

import java.math.BigDecimal;
import java.util.List;

import com.sharavel.sharavel_be.s3bucket.dto.response.S3TripMediaResponse;

public class LocationDto {
	private Integer number;
	private String name;
	private BigDecimal longitude;
	private BigDecimal latitude;
	private String description;
	private String countryIso2;
	private List<S3TripMediaResponse> media;

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

	public void setMedia(List<S3TripMediaResponse> media) {
		this.media = media;
	}

	public List<S3TripMediaResponse> getMedia() {
		return media;
	}

	@Override
	public String toString() {
		return "LocationDto [number=" + number + ", name=" + name + ", longitude=" + longitude + ", latitude=" + latitude
				+ ", description=" + description + ", countryIso2=" + countryIso2 + ", media=" + media + "]";
	}

}
