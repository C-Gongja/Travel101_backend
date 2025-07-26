package com.sharavel.sharavel_be.comments.service;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.comments.dto.CommentEditRequestDto;
import com.sharavel.sharavel_be.comments.dto.CommentRequestDto;

public interface CommentService {
	public ResponseEntity<?> addComment(CommentRequestDto newComment);

	public ResponseEntity<?> updateComment(CommentEditRequestDto editComment);

	public ResponseEntity<?> softDeleteComment(String targetUid);
}
