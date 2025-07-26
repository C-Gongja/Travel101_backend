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
import com.sharavel.sharavel_be.comments.entity.Comment;
import com.sharavel.sharavel_be.comments.mapper.CommentMapper;
import com.sharavel.sharavel_be.comments.repository.CommentRepository;
import com.sharavel.sharavel_be.comments.service.CommentService;
import com.sharavel.sharavel_be.likes.repository.LikesRepository;
import com.sharavel.sharavel_be.s3bucket.service.S3ProfileService;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class CommentServiceImpl implements CommentService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikesRepository likesRepository;

	@Autowired
	private S3ProfileService s3ProfileService;

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

	private CommentsResponseDto buildCommentReponseDto(Comment comment, Users user) {
		Comment parentComment = commentRepository.findByUid(comment.getUid()).orElseThrow();
		Long childCount = commentRepository.countByParentAndDeletedFalse(parentComment);
		boolean isLiked = false;
		if (user != null) {
			isLiked = likesRepository.existsByTargetTypeAndTargetUidAndUser("COMMENT", comment.getUid(), user);
		}
		Long commentLikesCount = likesRepository.countByTargetTypeAndTargetUid("COMMENT", comment.getUid());

		String profilePic = comment.getUser().getPicture();
		if (profilePic != null && profilePic.startsWith("sharavel-profile:")) {
			String s3Key = profilePic.replace("sharavel-profile:", "");
			profilePic = s3ProfileService.generatePresignedUrl(s3Key, 604800).toString();
		}

		return commentMapper.toCommentsResponseDtoWithoutReplies(
				comment,
				profilePic,
				isLiked,
				commentLikesCount,
				childCount);
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

		CommentsResponseDto responseDto = buildCommentReponseDto(saved, currentUser);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@Override
	public ResponseEntity<?> updateComment(CommentEditRequestDto updateComment) {
		Users currentUser = getCurrentUser();
		Comment comment = commentRepository.findByUid(updateComment.getUid())
				.orElseThrow(() -> new IllegalStateException("Current comment not found"));
		comment.setContent(updateComment.getContent());
		comment.setUpdatedAt(LocalDateTime.now());

		Comment saved = commentRepository.save(comment);

		CommentsResponseDto responseDto = buildCommentReponseDto(saved, currentUser);

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
}
