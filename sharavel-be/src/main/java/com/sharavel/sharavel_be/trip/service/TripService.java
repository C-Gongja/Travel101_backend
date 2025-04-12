package com.sharavel.sharavel_be.trip.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.sharavel.sharavel_be.trip.dto.TripDto;
import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.dto.response.TripResponse;

public interface TripService {
	public TripDto createTrip(TripDto trip);

	public void scriptTrip(String tripUuid);

	public TripResponse getTripByUuid(String tripUuId);

	public List<TripListDto> getUserAllTrips(String userUuid);

	public List<TripListDto> getAllTrips();

	public TripDto putUpdatedTrip(String tripuuid, TripDto updatedTrip);

	public ResponseEntity<?> deleteTrip(String tripUuid);

	@Transactional
	public TripDto updateTripField(String tripUuid, Map<String, Object> updates);
}
