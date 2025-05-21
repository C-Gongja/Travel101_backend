package com.sharavel.sharavel_be.tripComments.service;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.tripComments.dto.TripCommentEditRequestDto;
import com.sharavel.sharavel_be.tripComments.dto.TripCommentRequestDto;

public interface TripCommentService {
	public ResponseEntity<?> addComment(TripCommentRequestDto newComment);

	public ResponseEntity<?> editComment(TripCommentEditRequestDto editComment);

	public ResponseEntity<?> softDeleteComment(TripCommentEditRequestDto deleteComment);

	public ResponseEntity<?> getRootComments(String tripUid);

	public ResponseEntity<?> getReplies(String parentUid);
}
