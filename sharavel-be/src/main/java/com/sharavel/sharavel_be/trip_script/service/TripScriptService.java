package com.sharavel.sharavel_be.trip_script.service;

public interface TripScriptService {
	public void scriptTrip(String tripUid);

	public void scriptDay(String tripUid, Integer dayNum, String targetTripUid);

	public void scriptLocation(String tripUid, Integer dayNum, Integer locNum, String targetTripUid,
			Integer targetDayNum);

	public Long getScriptedCount(String tripUid);
}
