package com.sharavel.sharavel_be.trip.dto;

import java.sql.Date;
import java.util.List;

public class TripListDto {
	private String id;
	private String name;
	private String username;
	private Date startDate;
	private Date endDate;
	private List<String> countries;
	private Long scripted;

	public TripListDto() {
	}

	public TripListDto(String id, String name, String username, Date startDate, Date endDate, List<String> countries,
			Long scripted) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.countries = countries;
		this.scripted = scripted;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public List<String> getCountries() {
		return countries;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
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
				"username='" + username + '\'' +
				"tripName=" + name +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", scripted=" + scripted +
				", countries=" + countries +
				'}';
	}
}
