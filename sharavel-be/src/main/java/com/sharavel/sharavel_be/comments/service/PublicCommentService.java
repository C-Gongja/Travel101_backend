package com.sharavel.sharavel_be.comments.service;

import org.springframework.http.ResponseEntity;

public interface PublicCommentService {
	public ResponseEntity<?> getRootComments(String targetType, String targetUid);

	public ResponseEntity<?> getReplies(String parentUid);
}
