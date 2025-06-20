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
	public TripResponse createTrip(TripRequest trip);

	public TripResponse getTripByUuid(String tripUid);

	public TripResponse putUpdatedTrip(String tripUid, TripRequest updatedTrip);

	public ResponseEntity<?> deleteTrip(String tripUid);

	public List<TripListDto> getUserAllTrips(String userUuid);

	public List<TripDto> getCloneTripsList(String userUid);

	public List<TripListDto> getAllTrips();

	@Transactional
	public TripDto updateTripField(String tripUid, Map<String, Object> updates);
}
