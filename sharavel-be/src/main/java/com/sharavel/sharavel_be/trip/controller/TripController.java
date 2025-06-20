package com.sharavel.sharavel_be.trip.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.trip.dto.TripDto;
import com.sharavel.sharavel_be.trip.dto.response.TripRequest;
import com.sharavel.sharavel_be.trip.dto.response.TripResponse;
import com.sharavel.sharavel_be.trip.service.TripService;

@RestController
@RequestMapping("/api/trip")
public class TripController {
	@Autowired
	private TripService tripService;

	@PostMapping("/post")
	public ResponseEntity<TripResponse> createTrip(@RequestBody TripRequest trip) {
		TripResponse savedTrip = tripService.createTrip(trip);
		// String redirectUrl = "/trip/" + savedTrip.getTrip().getTripUid();

		return ResponseEntity.status(HttpStatus.CREATED).body(savedTrip);
	}

	@GetMapping("/{tripUid}")
	public ResponseEntity<TripResponse> getTrip(@PathVariable String tripUid) {
		TripResponse tripResponse = tripService.getTripByUuid(tripUid);

		if (tripResponse == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.ok(tripResponse);
	}

	@GetMapping("/getCloneTripList")
	public ResponseEntity<?> getCloneTripsList(@RequestParam String userUid) {
		List<TripDto> userTripsDto = tripService.getCloneTripsList(userUid);
		return ResponseEntity.ok(userTripsDto);
	}

	@PutMapping("/{tripUid}")
	public ResponseEntity<TripResponse> putTripField(
			@PathVariable String tripUid,
			@RequestBody TripRequest updates) {
		TripResponse updatedTrip = tripService.putUpdatedTrip(tripUid, updates);
		return ResponseEntity.ok(updatedTrip);
	}

	@PatchMapping("/{tripUid}")
	public ResponseEntity<TripDto> updateTripField(
			@PathVariable String tripUid,
			@RequestBody Map<String, Object> updates) {

		TripDto updatedTrip = tripService.updateTripField(tripUid, updates);
		return ResponseEntity.ok(updatedTrip);
	}

	@DeleteMapping("/delete/{tripUid}")
	public ResponseEntity<?> deleteTrip(@PathVariable String tripUid) {
		try {
			return ResponseEntity.ok(tripService.deleteTrip(tripUid));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to deleteTrip");
		}
	}
}
