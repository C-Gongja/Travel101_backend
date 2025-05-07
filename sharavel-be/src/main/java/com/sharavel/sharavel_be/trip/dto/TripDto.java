package com.sharavel.sharavel_be.trip.dto;

import java.time.LocalDate;
import java.util.List;

import com.sharavel.sharavel_be.countries.dto.CountryDto;

public class TripDto {
	private String tripUid;
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean isCompleted;
	private Long scripted;
	private List<CountryDto> countries;
	private List<DaysDto> days;

	public TripDto() {
	}

	// get trip uuid
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

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public List<CountryDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDto> countries) {
		this.countries = countries;
	}

	public List<DaysDto> getDays() {
		return days;
	}

	public void setDays(List<DaysDto> days) {
		this.days = days;
	}

	public Long getScripted() {
		return scripted;
	}

	public void setScripted(Long scripted) {
		this.scripted = scripted;
	}

	@Override
	public String toString() {
		return "TripDto{" +
				"tripName='" + name + '\'' +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", days=" + days +
				", scripted=" + scripted +
				", isCompleted=" + isCompleted +
				", countries=" + countries +
				'}';
	}
}
