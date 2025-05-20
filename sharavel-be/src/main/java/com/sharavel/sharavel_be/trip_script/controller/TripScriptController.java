package com.sharavel.sharavel_be.trip_script.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sharavel.sharavel_be.trip_script.service.TripScriptService;

@RestController
@RequestMapping("api/tripScript")
public class TripScriptController {
	@Autowired
	private TripScriptService tripScriptService;

	@PostMapping("/script")
	public ResponseEntity<?> scriptTrip(@RequestParam String tripUid) {
		tripScriptService.scriptTrip(tripUid);
		return ResponseEntity.ok("Successfully scripted!");
	}

	@GetMapping("/count")
	public ResponseEntity<?> getMethodName(@RequestParam String tripUid) {
		Long count = tripScriptService.getScriptedCount(tripUid);
		return ResponseEntity.ok(count);
	}
}
