package com.sharavel.sharavel_be.trip_likes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import com.sharavel.sharavel_be.trip_likes.service.TripLikesService;

@RestController
@RequestMapping("api/triplikes")
public class TripLikesController {

	@Autowired
	private TripLikesService tripLikesService;

	@PostMapping("/addlike")
	public ResponseEntity<String> addLike(@RequestParam String tripUid) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tripLikesService.addLike(tripUid));
	}

	@PutMapping("/removelike")
	public ResponseEntity<String> removeLike(@RequestParam String tripUid) {
		return ResponseEntity.status(HttpStatus.OK).body(tripLikesService.removeLike(tripUid));
	}

	@GetMapping("/getlikes")
	public ResponseEntity<?> getTripLikes(@RequestParam String tripUid) {
		return ResponseEntity.status(HttpStatus.OK).body(tripLikesService.getTripLikes(tripUid));
	}
}
