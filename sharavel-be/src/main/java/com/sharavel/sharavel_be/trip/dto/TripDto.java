package com.sharavel.sharavel_be.trip.dto;

import java.sql.Date;
import java.util.List;

public class TripDto {
	private String id;
	private String name;
	private Date startDate;
	private Date endDate;
	private boolean isCompleted;
	private Long scripted;
	private List<String> countries;
	private List<DaysDto> days;

	public TripDto() {
	}

	// get trip uuid
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public List<String> getCountries() {
		return countries;
	}

	public void setCountries(List<String> countries) {
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
