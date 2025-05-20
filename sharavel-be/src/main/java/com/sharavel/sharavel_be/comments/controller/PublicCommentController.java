package com.sharavel.sharavel_be.comments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/comment")
public class PublicCommentController {

	@GetMapping("/getTripComments")
	public ResponseEntity<String> getTripComments(@RequestParam String tripUid) {
		return null;
	}

	@GetMapping("/getTripCommentsNumber")
	public String getTripCommentsNumber(@RequestParam String param) {
		return new String();
	}
}
