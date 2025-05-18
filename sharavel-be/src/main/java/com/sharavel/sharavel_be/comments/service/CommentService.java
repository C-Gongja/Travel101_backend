package com.sharavel.sharavel_be.comments.service;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.comments.dto.CommentEditRequestDto;
import com.sharavel.sharavel_be.comments.dto.CommentRequestDto;

public interface CommentService {
	public ResponseEntity<?> addComment(CommentRequestDto newComment);

	public ResponseEntity<?> editComment(CommentEditRequestDto editComment);

	public ResponseEntity<?> deleteComment(CommentEditRequestDto deleteComment);

	public ResponseEntity<?> getTripComments();
}
