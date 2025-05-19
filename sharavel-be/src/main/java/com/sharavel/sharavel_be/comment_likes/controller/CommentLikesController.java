package com.sharavel.sharavel_be.comment_likes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sharavel.sharavel_be.comment_likes.service.CommentLikesService;

@RestController
@RequestMapping("api/commentLikes")
public class CommentLikesController {
	@Autowired
	private CommentLikesService commentLikesService;

	@PostMapping("/addlike")
	public ResponseEntity<String> addLike(@RequestBody String tripUid) {
		return ResponseEntity.status(HttpStatus.CREATED).body(commentLikesService.addLike(tripUid));
	}

	@PutMapping("/removelike")
	public ResponseEntity<String> removeLike(@RequestBody String tripUid) {
		return ResponseEntity.status(HttpStatus.OK).body(commentLikesService.removeLike(tripUid));
	}

	@GetMapping("/getlikes")
	public ResponseEntity<?> getTripLikes(@RequestBody String tripUid) {
		return ResponseEntity.status(HttpStatus.OK).body(commentLikesService.getCommentLikes(tripUid));
	}

}
