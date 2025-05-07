package com.sharavel.sharavel_be.trip.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.sharavel.sharavel_be.countries.dto.CountryDto;

public class TripListDto {
	private String tripUid;
	private String name;
	private String username;
	private LocalDate startDate;
	private LocalDate endDate;
	private List<CountryDto> countries;
	private boolean isCompleted;
	private Long scripted;

	public TripListDto() {
	}

	public TripListDto(String tripUid, String name, String username, LocalDate startDate, LocalDate endDate,
			List<CountryDto> countries, boolean isCompleted, Long scripted) {
		this.tripUid = tripUid;
		this.username = username;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.countries = countries;
		this.isCompleted = isCompleted;
		this.scripted = scripted;
	}

	// get trip uid
	public String getTripUid() {
		return tripUid;
	}

	public void setTripUid(String tripUid) {
		this.tripUid = tripUid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public List<CountryDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDto> countries) {
		this.countries = countries;
	}

	public Long getScripted() {
		return scripted;
	}

	public void setScripted(Long scripted) {
		this.scripted = scripted;
	}

	public boolean isIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	@Override
	public String toString() {
		return "TripDto{" +
				"username='" + username + '\'' +
				"tripName=" + name +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", scripted=" + scripted +
				", countries=" + countries +
				'}';
	}
}
