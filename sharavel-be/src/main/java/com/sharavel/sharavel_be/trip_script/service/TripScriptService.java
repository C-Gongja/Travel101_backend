package com.sharavel.sharavel_be.trip_script.service;

import com.sharavel.sharavel_be.trip_script.dto.request.ScriptDayRequestDto;
import com.sharavel.sharavel_be.trip_script.dto.request.ScriptLocationRequestDto;

public interface TripScriptService {
	public void scriptTrip(String tripUid);

	public void scriptDay(ScriptDayRequestDto request);

	public void scriptLocation(ScriptLocationRequestDto request);

	public Long getScriptedCount(String tripUid);
}
