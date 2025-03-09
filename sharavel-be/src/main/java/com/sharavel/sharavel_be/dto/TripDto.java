package com.sharavel.sharavel_be.dto;

import java.sql.Date;

public class TripDto {
	private String name;
	// startDate
	private Date startDate;
	// endDate
	private Date endDate;

	private boolean isCompleted;

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

	// do a user need to fix the countries in Trip level? or should it be
	// automatically updates when user make days plans?
	// private List<String> countries;
}
