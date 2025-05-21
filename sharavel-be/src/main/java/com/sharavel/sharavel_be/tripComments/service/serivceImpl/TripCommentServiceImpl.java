package com.sharavel.sharavel_be.tripComments.service.serivceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.trip.repository.TripRepository;
import com.sharavel.sharavel_be.tripComments.dto.TripCommentEditRequestDto;
import com.sharavel.sharavel_be.tripComments.dto.TripCommentRequestDto;
import com.sharavel.sharavel_be.tripComments.dto.TripCommentsResponseDto;
import com.sharavel.sharavel_be.tripComments.dto.SingleTripCommentDto;
import com.sharavel.sharavel_be.tripComments.entity.TripComment;
import com.sharavel.sharavel_be.tripComments.mapper.TripCommentMapper;
import com.sharavel.sharavel_be.tripComments.repository.TripCommentRepository;
import com.sharavel.sharavel_be.tripComments.service.TripCommentService;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class TripCommentServiceImpl implements TripCommentService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TripRepository tripRepository;
	@Autowired
	private TripCommentRepository tripCommentRepository;

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
	public ResponseEntity<?> addComment(TripCommentRequestDto newComment) {
		Users currentUser = getCurrentUser();
		Trip trip = tripRepository.findByTripUid(newComment.getTripUid())
				.orElseThrow(() -> new IllegalStateException("Current trip not found"));
		TripComment comment = new TripComment();
		comment.setTrip(trip);
		comment.setUser(currentUser);
		comment.setContent(newComment.getContent());
		comment.setCreatedAt(LocalDateTime.now());
		// 대댓글일 경우 parent 설정
		if (newComment.getParentUid() != null) {
			TripComment parent = tripCommentRepository.findByUid(newComment.getParentUid())
					.orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
			comment.setParent(parent);
		}
		TripComment saved = tripCommentRepository.save(comment);

		SingleTripCommentDto responseDto = TripCommentMapper.toSingleCommentDto(saved);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@Override
	public ResponseEntity<?> editComment(TripCommentEditRequestDto editComment) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		TripComment comment = tripCommentRepository.findByUid(editComment.getCommentUid())
				.orElseThrow(() -> new IllegalStateException("Current comment not found"));
		comment.setContent(editComment.getContent());
		comment.setUpdatedAt(LocalDateTime.now());

		TripComment saved = tripCommentRepository.save(comment);

		SingleTripCommentDto responseDto = TripCommentMapper.toSingleCommentDto(saved);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@Override
	public ResponseEntity<?> softDeleteComment(TripCommentEditRequestDto deleteComment) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		TripComment comment = tripCommentRepository.findByUid(deleteComment.getCommentUid())
				.orElseThrow(() -> new IllegalStateException("Current comment not found"));

		comment.setDeleted(true);
		tripCommentRepository.save(comment);

		return ResponseEntity.status(HttpStatus.CREATED).body("Comment successfully deleted");
	}

	@Override
	public ResponseEntity<?> getRootComments(String tripUid) {
		Trip trip = tripRepository.findByTripUid(tripUid)
				.orElseThrow(() -> new RuntimeException("Trip not found"));

		List<TripComment> rootComments = tripCommentRepository
				.findByTripAndParentIsNullOrderByCreatedAtAsc(trip);

		List<TripCommentsResponseDto> response = rootComments.stream()
				.map(comment -> TripCommentMapper.toCommentsResponseDtoWithoutReplies(comment, tripCommentRepository))
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<?> getReplies(String parentUid) {
		TripComment parent = tripCommentRepository.findByUid(parentUid)
				.orElseThrow(() -> new RuntimeException("Parent comment not found"));

		List<TripComment> replies = tripCommentRepository
				.findByParentOrderByCreatedAtAsc(parent);

		List<TripCommentsResponseDto> response = replies.stream()
				.map(comment -> TripCommentMapper.toCommentsResponseDtoWithoutReplies(comment, tripCommentRepository))
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}
}
