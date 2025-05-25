package com.sharavel.sharavel_be.likes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.likes.service.LikesService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("api/likes")
public class LikesController {

	@Autowired
	private LikesService tripLikesService;

	@PostMapping("/addlike")
	public ResponseEntity<String> addLike(@RequestParam String targetType, @RequestParam String targetUid) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tripLikesService.addLike(targetType, targetUid));
	}

	@DeleteMapping("/removelike")
	public ResponseEntity<String> removeLike(@RequestParam String targetType, @RequestParam String targetUid) {
		return ResponseEntity.status(HttpStatus.OK).body(tripLikesService.removeLike(targetType, targetUid));
	}

	@GetMapping("/getlikes")
	public ResponseEntity<?> getTripLikes(@RequestParam String targetType, @RequestParam String targetUid) {
		return ResponseEntity.status(HttpStatus.OK).body(tripLikesService.getLikeCount(targetType, targetUid));
	}
}
