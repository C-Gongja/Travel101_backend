package com.sharavel.sharavel_be.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.sharavel.sharavel_be.dto.TripDto;

public interface TripService {
	public TripDto createTrip(TripDto trip);

	public TripDto getTrip(Long tripId);

	public TripDto getTripByUuid(String tripUuId);

	public List<TripDto> getUserAllTrip(Long uid);

	public ResponseEntity<?> putTrip(String tripId, TripDto trip);

	public ResponseEntity<?> patchTrip(Long tripId, TripDto trip);

	@Transactional
	public Map<String, Object> updateTripField(String tripUuid, Map<String, Object> updates);
}
