package com.sharavel.sharavel_be.trip_script.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.trip_script.dto.request.ScriptDayRequestDto;
import com.sharavel.sharavel_be.trip_script.dto.request.ScriptLocationRequestDto;
import com.sharavel.sharavel_be.trip_script.service.TripScriptService;

@RestController
@RequestMapping("api/script")
public class TripScriptController {
	@Autowired
	private TripScriptService tripScriptService;

	@PostMapping("/scriptTrip")
	public ResponseEntity<?> scriptTrip(@RequestParam String tripUid) {
		tripScriptService.scriptTrip(tripUid);
		return ResponseEntity.ok("Successfully scripted!");
	}

	@PostMapping("/scriptDay")
	public ResponseEntity<?> scriptdDay(@RequestBody ScriptDayRequestDto request) {
		tripScriptService.scriptDay(request);
		return ResponseEntity.ok("Successfully scripted!");
	}

	@PostMapping("/scriptLocation")
	public ResponseEntity<?> scriptLocation(@RequestBody ScriptLocationRequestDto request) {
		tripScriptService.scriptLocation(request);
		return ResponseEntity.ok("Successfully scripted!");
	}

	@GetMapping("/count")
	public ResponseEntity<?> getTripScriptCount(@RequestParam String tripUid) {
		Long count = tripScriptService.getScriptedCount(tripUid);
		return ResponseEntity.ok(count);
	}
}
