package com.sharavel.sharavel_be.trip.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.sharavel.sharavel_be.trip.dto.TripDto;
import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.dto.response.TripRequest;
import com.sharavel.sharavel_be.trip.dto.response.TripResponse;

public interface TripService {
	public TripDto createTrip(TripRequest trip);

	public void scriptTrip(String tripUid);

	public TripResponse getTripByUuid(String tripUid);

	public List<TripListDto> getUserAllTrips(String userUuid);

	public List<TripListDto> getAllTrips();

	public TripDto putUpdatedTrip(String tripUid, TripRequest updatedTrip);

	public ResponseEntity<?> deleteTrip(String tripUid);

	@Transactional
	public TripDto updateTripField(String tripUid, Map<String, Object> updates);
}
