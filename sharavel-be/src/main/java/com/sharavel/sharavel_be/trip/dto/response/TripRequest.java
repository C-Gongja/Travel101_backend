package com.sharavel.sharavel_be.trip.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.sharavel.sharavel_be.countries.dto.CountryCodeDto;
import com.sharavel.sharavel_be.trip.dto.DaysDto;

public class TripRequest {
	private String tripUid;
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean isCompleted;
	private Long scripted;
	private List<CountryCodeDto> countries; // iso2만 보냄
	private List<DaysDto> days;

	public TripRequest() {

	}

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

	public Long getScripted() {
		return scripted;
	}

	public void setScripted(Long scripted) {
		this.scripted = scripted;
	}

	public List<CountryCodeDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryCodeDto> countries) {
		this.countries = countries;
	}

	public List<DaysDto> getDays() {
		return days;
	}

	public void setDays(List<DaysDto> days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "TripDto{" +
				"tripUid='" + tripUid + '\'' +
				", name='" + name + '\'' +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", isCompleted=" + isCompleted +
				", scripted=" + scripted +
				", countries=" + countries +
				", days=" + days +
				'}';
	}

}
