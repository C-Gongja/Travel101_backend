package com.sharavel.sharavel_be.trip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.trip.dto.TripListDto;
import com.sharavel.sharavel_be.trip.dto.response.TripResponse;
import com.sharavel.sharavel_be.trip.service.TripService;

@RestController
@RequestMapping("/public/trip")
public class TripPublicController {
	@Autowired
	private TripService tripService;

	@GetMapping("/all")
	public ResponseEntity<List<TripListDto>> getAllTrips() {
		List<TripListDto> trips = tripService.getAllTrips();

		if (trips == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.ok(trips);
	}

	@GetMapping("/all/{userUuid}")
	public ResponseEntity<List<TripListDto>> getUserAllTrips(@PathVariable String userUuid) {
		List<TripListDto> trips = tripService.getUserAllTrips(userUuid);

		if (trips == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.ok(trips);
	}

	@GetMapping("/{tripUuid}")
	public ResponseEntity<TripResponse> getTrip(@PathVariable String tripUuid) {
		TripResponse tripResponse = tripService.getTripByUuid(tripUuid);

		if (tripResponse == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.ok(tripResponse);
	}
}
