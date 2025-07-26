package com.sharavel.sharavel_be.comments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.comments.service.PublicCommentService;

@RestController
@RequestMapping("/public/comment")
public class PublicCommentController {
	@Autowired
	private PublicCommentService publicCommentService;

	@GetMapping("/getTripRootComments")
	public ResponseEntity<?> getRootComments(@RequestParam String targetType, @RequestParam String targetUid) {
		return publicCommentService.getRootComments(targetType, targetUid);
	}

	@GetMapping("/getTripCommentReplies")
	public ResponseEntity<?> getRootRepliesComments(@RequestParam String parentUid) {
		return publicCommentService.getReplies(parentUid);
	}
}
