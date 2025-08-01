package com.sharavel.sharavel_be.comments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.comments.dto.CommentEditRequestDto;
import com.sharavel.sharavel_be.comments.dto.CommentRequestDto;
import com.sharavel.sharavel_be.comments.service.CommentService;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@PostMapping("/addComment")
	public ResponseEntity<?> addComment(@RequestBody CommentRequestDto newComment) {
		try {
			return commentService.addComment(newComment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed to post comment");
		}
	}

	@PutMapping("/updateComment")
	public ResponseEntity<?> updateComment(@RequestBody CommentEditRequestDto updateComment) {
		try {
			return commentService.updateComment(updateComment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed to update comment");
		}
	}

	@DeleteMapping("/deleteComment")
	public ResponseEntity<?> deleteComment(@RequestParam String targetUid) {
		try {
			return commentService.softDeleteComment(targetUid);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed to delete comment");
		}
	}
}
