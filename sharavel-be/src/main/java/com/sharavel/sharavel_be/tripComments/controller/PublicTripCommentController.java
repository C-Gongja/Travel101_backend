package com.sharavel.sharavel_be.tripComments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.tripComments.service.TripCommentService;

@RestController
@RequestMapping("/public/comment")
public class PublicTripCommentController {
	@Autowired
	private TripCommentService tripCommentService;

	@GetMapping("/getTripRootComments")
	public ResponseEntity<?> getRootComments(@RequestParam String tripUid) {
		return tripCommentService.getRootComments(tripUid);
	}

	@GetMapping("/getTripCommentReplies")
	public ResponseEntity<?> getRootRepliesComments(@RequestParam String parentUid) {
		return tripCommentService.getReplies(parentUid);
	}
}
