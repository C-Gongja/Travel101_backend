package com.sharavel.sharavel_be.trip.dto.response;

import com.sharavel.sharavel_be.trip.dto.TripDto;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;

public class TripResponse {
	private TripDto trip;
	private boolean isEditable;
	private UserSnippetDto userSnippet;

	// 생성자
	public TripResponse(TripDto trip, boolean isEditable, UserSnippetDto userSnippet) {
		this.trip = trip;
		this.isEditable = isEditable;
		this.userSnippet = userSnippet;
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

	public UserSnippetDto getUserSnippet() {
		return userSnippet;
	}

	public void setUserSnippet(UserSnippetDto userSnippet) {
		this.userSnippet = userSnippet;
	}
}