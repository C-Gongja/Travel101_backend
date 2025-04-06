package com.sharavel.sharavel_be.trip.dto.response;

import com.sharavel.sharavel_be.trip.dto.TripDto;

public class TripResponse {
	private TripDto trip;
	private boolean isEditable;

	// 생성자
	public TripResponse(TripDto trip, boolean isEditable) {
		this.trip = trip;
		this.isEditable = isEditable;
	}

	// getter, setter
	public TripDto getTrip() {
		return trip;
	}

	public void setTrip(TripDto trip) {
		this.trip = trip;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean editable) {
		isEditable = editable;
	}
}