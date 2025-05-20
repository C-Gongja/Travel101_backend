package com.sharavel.sharavel_be.trip_script.service;

public interface TripScriptService {
	public void scriptTrip(String tripUid);

	public Long getScriptedCount(String tripUid);
}
