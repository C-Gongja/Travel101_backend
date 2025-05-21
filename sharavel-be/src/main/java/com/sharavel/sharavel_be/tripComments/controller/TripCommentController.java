package com.sharavel.sharavel_be.tripComments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.tripComments.dto.TripCommentEditRequestDto;
import com.sharavel.sharavel_be.tripComments.dto.TripCommentRequestDto;
import com.sharavel.sharavel_be.tripComments.service.TripCommentService;

@RestController
@RequestMapping("/api/comment")
public class TripCommentController {

	@Autowired
	private TripCommentService commentService;

	@PostMapping("/addComment")
	public ResponseEntity<?> addComment(@RequestBody TripCommentRequestDto newComment) {
		try {
			return commentService.addComment(newComment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/editComment")
	public ResponseEntity<?> editComment(@RequestBody TripCommentEditRequestDto editComment) {
		try {
			return commentService.editComment(editComment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@DeleteMapping("/deleteComment")
	public ResponseEntity<?> deleteComment(@RequestParam TripCommentEditRequestDto deleteComment) {
		try {
			return commentService.softDeleteComment(deleteComment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
