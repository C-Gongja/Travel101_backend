package com.sharavel.sharavel_be.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.dto.TripDto;
import com.sharavel.sharavel_be.service.TripService;

@RestController
@RequestMapping("/public/trip")
public class TripController {
	@Autowired
	private TripService tripService;

	@GetMapping("all/{uid}")
	public String getUserAllTrips(@RequestParam String param) {
		return new String();
	}

	@GetMapping("/{tripUuid}")
	public ResponseEntity<TripDto> getTrip(@PathVariable String tripUuid) {
		System.out.println("Fetching trip with UUID: " + tripUuid);
		TripDto trip = tripService.getTripByUuid(tripUuid);

		if (trip == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.ok(trip);
	}

	@PostMapping("/post")
	public ResponseEntity<TripDto> createTrip(@RequestBody TripDto trip) {
		System.out.println("create trip: " + trip);
		TripDto savedTrip = tripService.createTrip(trip);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedTrip);
	}

	// update entire object
	// @PutMapping("/{tripId}")
	// public ResponseEntity<TripDto> updateTrip(@PathVariable String tripId,
	// @RequestBody TripDto updatedTrip) {
	// TripDto savedTrip = tripService.putTrip(tripId, updatedTrip);
	// return ResponseEntity.status(HttpStatus.OK).body(savedTrip);
	// }

	// partial update
	// 특정 필드만 업데이트하는 PATCH API

	@PatchMapping("update/{tripId}")
	public ResponseEntity<Map<String, Object>> updateTripField(
			@PathVariable String tripUuid,
			@RequestBody Map<String, Object> updates) {

		Map<String, Object> updatedFields = tripService.updateTripField(tripUuid, updates);
		return ResponseEntity.ok(updatedFields); // 변경된 필드만 반환
	}
}
