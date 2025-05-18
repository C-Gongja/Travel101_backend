package com.sharavel.sharavel_be.comments.service.serivceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.comments.dto.CommentEditRequestDto;
import com.sharavel.sharavel_be.comments.dto.CommentRequestDto;
import com.sharavel.sharavel_be.comments.dto.CommentsResponseDto;
import com.sharavel.sharavel_be.comments.dto.SingleCommentDto;
import com.sharavel.sharavel_be.comments.entity.Comment;
import com.sharavel.sharavel_be.comments.mapper.CommentMapper;
import com.sharavel.sharavel_be.comments.repository.CommentRepository;
import com.sharavel.sharavel_be.comments.service.CommentService;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip.repository.TripRepository;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class CommentServiceImpl implements CommentService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TripRepository tripRepository;
	@Autowired
	private CommentRepository commentRepository;

	private Users getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Current user not found"));
	}

	@Override
	public ResponseEntity<?> addComment(CommentRequestDto newComment) {
		Users currentUser = getCurrentUser();
		Trip trip = tripRepository.findByTripUid(newComment.getTripUid())
				.orElseThrow(() -> new IllegalStateException("Current trip not found"));
		Comment comment = new Comment();
		comment.setTrip(trip);
		comment.setUser(currentUser);
		comment.setContent(newComment.getContent());
		comment.setCreatedAt(LocalDateTime.now());
		// 대댓글일 경우 parent 설정
		if (newComment.getParentUid() != null) {
			Comment parent = commentRepository.findByUid(newComment.getParentUid())
					.orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
			comment.setParent(parent);
		}
		Comment saved = commentRepository.save(comment);

		SingleCommentDto responseDto = CommentMapper.toSingleCommentDto(saved);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@Override
	public ResponseEntity<?> editComment(CommentEditRequestDto editComment) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		Comment comment = commentRepository.findByUid(editComment.getCommentUid())
				.orElseThrow(() -> new IllegalStateException("Current comment not found"));
		comment.setContent(editComment.getContent());
		comment.setUpdatedAt(LocalDateTime.now());

		Comment saved = commentRepository.save(comment);

		SingleCommentDto responseDto = CommentMapper.toSingleCommentDto(saved);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@Override
	public ResponseEntity<?> deleteComment(CommentEditRequestDto deleteComment) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		Comment comment = commentRepository.findByUid(deleteComment.getCommentUid())
				.orElseThrow(() -> new IllegalStateException("Current comment not found"));

		commentRepository.delete(comment);

		return ResponseEntity.status(HttpStatus.CREATED).body("Comment successfully deleted");
	}

	@Override
	public ResponseEntity<?> getTripComments() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getTripComments'");
	}

}
