package com.sharavel.sharavel_be.trip.dto;

import java.util.List;

public class DaysDto {
	// private Long id;
	private Integer number;
	private List<LocationDto> locations;

	// public Long getId() {
	// return id;
	// }

	// public void setId(Long id) {
	// this.id = id;
	// }

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public List<LocationDto> getLocations() {
		return locations;
	}

	public void setLocations(List<LocationDto> locations) {
		this.locations = locations;
	}

	@Override
	public String toString() {
		return "Days{" +
				"number='" + number + '\'' +
				", locations =" + locations +
				'}';
	}
}
