package com.sharavel.sharavel_be.comments.service.serivceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.comments.dto.SingleCommentDto;
import com.sharavel.sharavel_be.comments.dto.CommentEditRequestDto;
import com.sharavel.sharavel_be.comments.dto.CommentRequestDto;
import com.sharavel.sharavel_be.comments.dto.CommentsResponseDto;
import com.sharavel.sharavel_be.comments.entity.Comment;
import com.sharavel.sharavel_be.comments.mapper.CommentMapper;
import com.sharavel.sharavel_be.comments.repository.CommentRepository;
import com.sharavel.sharavel_be.comments.service.CommentService;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class CommentServiceImpl implements CommentService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private CommentMapper commentMapper;

	private Users getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Current user not found"));
	}

	private Users getCurrentUserNullable() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() ||
				"anonymousUser".equals(authentication.getPrincipal())) {
			return null;
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email).orElse(null);
	}

	@Override
	public ResponseEntity<?> addComment(CommentRequestDto newComment) {
		Users currentUser = getCurrentUser();
		Comment comment = new Comment();
		comment.setTargetType(newComment.getTargetType());
		comment.setTargetUid(newComment.getTargetUid());
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

		SingleCommentDto responseDto = commentMapper.toSingleCommentDto(saved);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@Override
	public ResponseEntity<?> updateComment(CommentEditRequestDto updateComment) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		Comment comment = commentRepository.findByUid(updateComment.getUid())
				.orElseThrow(() -> new IllegalStateException("Current comment not found"));
		comment.setContent(updateComment.getContent());
		comment.setUpdatedAt(LocalDateTime.now());

		Comment saved = commentRepository.save(comment);

		SingleCommentDto responseDto = commentMapper.toSingleCommentDto(saved);

		System.out.println(responseDto.toString());

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto);
	}

	@Override
	public ResponseEntity<?> softDeleteComment(String targetUid) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		Comment comment = commentRepository.findByUid(targetUid)
				.orElseThrow(() -> new IllegalStateException("Current comment not found"));

		comment.setDeleted(true);
		commentRepository.save(comment);

		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Comment successfully deleted");
	}

	@Override
	public ResponseEntity<?> getRootComments(String targetType, String targetUid) {
		Users currentUser = getCurrentUserNullable();
		List<Comment> rootComments = commentRepository
				.findByTargetTypeAndTargetUidAndParentIsNullAndDeletedFalseOrderByCreatedAtAsc(targetType, targetUid);

		List<CommentsResponseDto> response = rootComments.stream()
				.map(comment -> commentMapper.toCommentsResponseDtoWithoutReplies(comment, currentUser))
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<?> getReplies(String parentUid) {
		Users currentUser = getCurrentUserNullable();
		Comment parent = commentRepository.findByUid(parentUid)
				.orElseThrow(() -> new RuntimeException("Parent comment not found"));

		List<Comment> replies = commentRepository
				.findByParentAndDeletedFalseOrderByCreatedAtAsc(parent);

		List<CommentsResponseDto> response = replies.stream()
				.map(comment -> commentMapper.toCommentsResponseDtoWithoutReplies(comment, currentUser))
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}
}
