package com.sharavel.sharavel_be.trip.controller;

import java.util.HashMap;
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
	public ResponseEntity<Map<String, Object>> createTrip(@RequestBody TripRequest trip) {
		System.out.println("!!!!!!!!!!trip: " + trip);
		TripDto savedTrip = tripService.createTrip(trip);
		String redirectUrl = "/trip/" + savedTrip.getTripUid();

		Map<String, Object> response = new HashMap<>();
		response.put("trip", savedTrip);
		response.put("editable", true);
		response.put("redirectUrl", redirectUrl);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{tripUid}")
	public ResponseEntity<TripResponse> getTrip(@PathVariable String tripUid) {
		TripResponse tripResponse = tripService.getTripByUuid(tripUid);

		if (tripResponse == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.ok(tripResponse);
	}

	@PostMapping("/scriptTrip/{tripUid}")
	public ResponseEntity<?> scriptTrip(@PathVariable String tripUid) {
		tripService.scriptTrip(tripUid);
		return ResponseEntity.ok("Successfully scripted!");
	}

	@PutMapping("/{tripUid}")
	public ResponseEntity<TripDto> putTripField(
			@PathVariable String tripUid,
			@RequestBody TripRequest updates) {
		TripDto updatedTrip = tripService.putUpdatedTrip(tripUid, updates);
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
		tripService.deleteTrip(tripUid);
		return ResponseEntity.ok("Successfully Deleted");
	}
}
